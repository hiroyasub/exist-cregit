begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|ByteArrayInputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|BinaryDocument
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
name|DocumentImpl
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
name|storage
operator|.
name|DBBroker
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
name|lock
operator|.
name|Lock
import|;
end_import

begin_comment
comment|/**  * Source implementation that reads from a binary resource  * stored in the database.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DBSource
extends|extends
name|AbstractSource
block|{
specifier|private
name|BinaryDocument
name|doc
decl_stmt|;
specifier|private
name|String
name|key
decl_stmt|;
specifier|private
name|long
name|lastModified
decl_stmt|;
specifier|private
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|private
name|boolean
name|checkEncoding
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|public
name|DBSource
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|BinaryDocument
name|doc
parameter_list|,
name|boolean
name|checkXQEncoding
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|doc
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|doc
operator|.
name|getLastModified
argument_list|()
expr_stmt|;
name|this
operator|.
name|checkEncoding
operator|=
name|checkXQEncoding
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getKey()      */
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModified
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#isValid()      */
specifier|public
name|int
name|isValid
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|broker
operator|.
name|openDocument
argument_list|(
name|key
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
return|return
name|INVALID
return|;
if|if
condition|(
name|doc
operator|.
name|getLastModified
argument_list|()
operator|>
name|lastModified
condition|)
return|return
name|INVALID
return|;
return|return
name|VALID
return|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
return|return
name|INVALID
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#isValid(org.exist.source.Source)      */
specifier|public
name|int
name|isValid
parameter_list|(
name|Source
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|DBSource
operator|)
condition|)
return|return
name|INVALID
return|;
name|DBSource
name|source
init|=
operator|(
name|DBSource
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|source
operator|.
name|getLastModified
argument_list|()
operator|>
name|lastModified
condition|)
return|return
name|INVALID
return|;
return|return
name|VALID
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getReader()      */
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|broker
operator|.
name|getBinaryResourceData
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|checkEncoding
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|is
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|encoding
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getContent()      */
specifier|public
name|String
name|getContent
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|broker
operator|.
name|getBinaryResourceData
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|checkEncoding
argument_list|(
name|is
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|encoding
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkEncoding
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|checkEncoding
condition|)
block|{
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
name|encoding
operator|=
name|checkedEnc
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

