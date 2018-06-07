begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|*
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
name|persistent
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
name|persistent
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
name|dom
operator|.
name|persistent
operator|.
name|LockedDocument
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
name|Permission
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
name|security
operator|.
name|internal
operator|.
name|aider
operator|.
name|UnixStylePermissionAider
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
operator|.
name|LockMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|FastByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
specifier|final
name|BinaryDocument
name|doc
decl_stmt|;
specifier|private
specifier|final
name|XmldbURI
name|key
decl_stmt|;
specifier|private
specifier|final
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
specifier|final
name|boolean
name|checkEncoding
decl_stmt|;
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|public
name|DBSource
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|BinaryDocument
name|doc
parameter_list|,
specifier|final
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
name|getURI
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|doc
operator|.
name|getMetadata
argument_list|()
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
annotation|@
name|Override
specifier|public
name|String
name|path
parameter_list|()
block|{
return|return
name|getDocumentPath
argument_list|()
operator|.
name|toString
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
literal|"DB"
return|;
block|}
comment|/* (non-Javadoc)              * @see org.exist.source.Source#getKey()              */
annotation|@
name|Override
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
name|XmldbURI
name|getDocumentPath
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
name|Validity
name|result
decl_stmt|;
try|try
init|(
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|key
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|;
init|)
block|{
if|if
condition|(
name|lockedDoc
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|Validity
operator|.
name|INVALID
expr_stmt|;
block|}
if|else if
condition|(
name|lockedDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
operator|>
name|lastModified
condition|)
block|{
name|result
operator|=
name|Validity
operator|.
name|INVALID
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|Validity
operator|.
name|VALID
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|result
operator|=
name|Validity
operator|.
name|INVALID
expr_stmt|;
block|}
return|return
name|result
return|;
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
specifier|final
name|Validity
name|result
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|DBSource
operator|)
condition|)
block|{
name|result
operator|=
name|Validity
operator|.
name|INVALID
expr_stmt|;
block|}
if|else if
condition|(
operator|(
operator|(
name|DBSource
operator|)
name|other
operator|)
operator|.
name|getLastModified
argument_list|()
operator|>
name|lastModified
condition|)
block|{
name|result
operator|=
name|Validity
operator|.
name|INVALID
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|Validity
operator|.
name|VALID
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getReader()      */
annotation|@
name|Override
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|InputStream
name|is
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|doc
argument_list|)
decl_stmt|;
specifier|final
name|BufferedInputStream
name|bis
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|bis
operator|.
name|mark
argument_list|(
literal|64
argument_list|)
expr_stmt|;
name|checkEncoding
argument_list|(
name|bis
argument_list|)
expr_stmt|;
name|bis
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
operator|new
name|InputStreamReader
argument_list|(
name|bis
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
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|doc
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getContent()      */
annotation|@
name|Override
specifier|public
name|String
name|getContent
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|binaryLength
init|=
name|broker
operator|.
name|getBinaryResourceSize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|binaryLength
operator|>
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource too big to be read using this method."
argument_list|)
throw|;
block|}
comment|//final byte [] data = new byte[(int)binaryLength];
try|try
init|(
specifier|final
name|InputStream
name|raw
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|doc
argument_list|)
init|;
specifier|final
name|FastByteArrayOutputStream
name|buf
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|(
operator|(
name|int
operator|)
name|binaryLength
argument_list|)
init|)
block|{
name|buf
operator|.
name|write
argument_list|(
name|raw
argument_list|)
expr_stmt|;
comment|//raw.close();
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|buf
operator|.
name|toFastByteInputStream
argument_list|()
init|)
block|{
name|checkEncoding
argument_list|(
name|is
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|(
name|encoding
argument_list|)
return|;
block|}
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
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|doc
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
specifier|private
name|void
name|checkEncoding
parameter_list|(
specifier|final
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
name|checkedEnc
expr_stmt|;
block|}
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
name|doc
operator|.
name|getDocumentURI
argument_list|()
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
name|mode
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|//TODO This check should not even be here! Its up to the database to refuse access not requesting source
if|if
condition|(
operator|!
name|doc
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|subject
argument_list|,
name|mode
argument_list|)
condition|)
block|{
specifier|final
name|String
name|modeStr
init|=
operator|new
name|UnixStylePermissionAider
argument_list|(
name|mode
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Subject '"
operator|+
name|subject
operator|.
name|getName
argument_list|()
operator|+
literal|"' does not have '"
operator|+
name|modeStr
operator|+
literal|"' access to resource '"
operator|+
name|doc
operator|.
name|getURI
argument_list|()
operator|+
literal|"'."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Permission
name|getPermissions
parameter_list|()
block|{
return|return
name|doc
operator|.
name|getPermissions
argument_list|()
return|;
block|}
block|}
end_class

end_unit

