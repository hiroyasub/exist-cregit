begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * A source implementation reading from the path system.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FileSource
extends|extends
name|AbstractSource
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|FileSource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
specifier|private
name|Charset
name|encoding
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|checkEncoding
decl_stmt|;
specifier|private
name|String
name|filePath
decl_stmt|;
specifier|private
name|long
name|lastModified
decl_stmt|;
comment|/**      * Defaults to UTF-8 encoding for the path path      * @param path to file source      * @param checkXQEncoding enable / disable XQEncoding      */
specifier|public
name|FileSource
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|boolean
name|checkXQEncoding
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileSource
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|Charset
name|encoding
parameter_list|,
specifier|final
name|boolean
name|checkXQEncoding
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|encoding
operator|=
name|encoding
expr_stmt|;
name|this
operator|.
name|checkEncoding
operator|=
name|checkXQEncoding
expr_stmt|;
name|this
operator|.
name|filePath
operator|=
name|path
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|lastModifiedSafe
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|path
parameter_list|()
block|{
return|return
name|getFilePath
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
literal|"File"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|filePath
return|;
block|}
specifier|public
name|String
name|getFilePath
parameter_list|()
block|{
return|return
name|filePath
return|;
block|}
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validity
name|isValid
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
specifier|final
name|long
name|currentLastModified
init|=
name|lastModifiedSafe
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentLastModified
operator|==
operator|-
literal|1
operator|||
name|currentLastModified
operator|>
name|lastModified
condition|)
block|{
return|return
name|Validity
operator|.
name|INVALID
return|;
block|}
else|else
block|{
return|return
name|Validity
operator|.
name|VALID
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Validity
name|isValid
parameter_list|(
specifier|final
name|Source
name|other
parameter_list|)
block|{
return|return
name|Validity
operator|.
name|INVALID
return|;
block|}
annotation|@
name|Override
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|checkEncoding
argument_list|()
expr_stmt|;
return|return
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|path
argument_list|,
name|encoding
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContent
parameter_list|()
throws|throws
name|IOException
block|{
name|checkEncoding
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|path
argument_list|)
argument_list|,
name|encoding
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Charset
name|getEncoding
parameter_list|()
throws|throws
name|IOException
block|{
name|checkEncoding
argument_list|()
expr_stmt|;
return|return
name|encoding
return|;
block|}
specifier|private
name|void
name|checkEncoding
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|checkEncoding
condition|)
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|)
init|)
block|{
specifier|final
name|String
name|checkedEnc
init|=
name|guessXQueryEncoding
argument_list|(
name|is
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkedEnc
operator|!=
literal|null
condition|)
block|{
name|encoding
operator|=
name|Charset
operator|.
name|forName
argument_list|(
name|checkedEnc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|long
name|lastModifiedSafe
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
try|try
block|{
return|return
name|Files
operator|.
name|getLastModifiedTime
argument_list|(
name|path
argument_list|)
operator|.
name|toMillis
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|QName
name|isModule
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|)
init|)
block|{
return|return
name|getModuleDecl
argument_list|(
name|is
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|filePath
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|validate
parameter_list|(
specifier|final
name|Subject
name|subject
parameter_list|,
specifier|final
name|int
name|perm
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|// TODO protected?
block|}
block|}
end_class

end_unit

