begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|external
operator|.
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|BrokerPool
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|EXistInputSource
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
name|LockException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ErrorCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|BinaryResource
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Date
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|LocalBinaryResource
extends|extends
name|AbstractEXistResource
implements|implements
name|ExtendedResource
implements|,
name|BinaryResource
implements|,
name|EXistResource
block|{
specifier|protected
name|InputSource
name|inputSource
init|=
literal|null
decl_stmt|;
specifier|protected
name|File
name|file
init|=
literal|null
decl_stmt|;
specifier|protected
name|byte
index|[]
name|rawData
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isExternal
init|=
literal|false
decl_stmt|;
specifier|protected
name|Date
name|datecreated
init|=
literal|null
decl_stmt|;
specifier|protected
name|Date
name|datemodified
init|=
literal|null
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|LocalBinaryResource
parameter_list|(
name|Subject
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|collection
parameter_list|,
name|XmldbURI
name|docId
parameter_list|)
block|{
name|super
argument_list|(
name|user
argument_list|,
name|pool
argument_list|,
name|collection
argument_list|,
name|docId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Resource#getParentCollection() 	 */
specifier|public
name|Collection
name|getParentCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|parent
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Resource#getId() 	 */
specifier|public
name|String
name|getId
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|docId
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Resource#getResourceType() 	 */
specifier|public
name|String
name|getResourceType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"BinaryResource"
return|;
block|}
specifier|public
name|Object
name|getExtendedContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
return|return
name|file
return|;
if|if
condition|(
name|inputSource
operator|!=
literal|null
condition|)
return|return
name|inputSource
return|;
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BinaryDocument
name|blob
init|=
literal|null
decl_stmt|;
name|InputStream
name|rawDataStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|blob
operator|=
operator|(
name|BinaryDocument
operator|)
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|blob
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Permission denied to read resource"
argument_list|)
throw|;
name|rawDataStream
operator|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|blob
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|releaseDocument
argument_list|(
name|blob
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
block|}
return|return
name|rawDataStream
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Resource#getContent() 	 */
specifier|public
name|Object
name|getContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Object
name|res
init|=
name|getExtendedContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|res
operator|instanceof
name|File
condition|)
block|{
return|return
name|readFile
argument_list|(
operator|(
name|File
operator|)
name|res
argument_list|)
return|;
block|}
if|else if
condition|(
name|res
operator|instanceof
name|InputSource
condition|)
block|{
return|return
name|readFile
argument_list|(
operator|(
name|InputSource
operator|)
name|res
argument_list|)
return|;
block|}
if|else if
condition|(
name|res
operator|instanceof
name|InputStream
condition|)
block|{
return|return
name|readFile
argument_list|(
operator|(
name|InputStream
operator|)
name|res
argument_list|)
return|;
block|}
block|}
return|return
name|res
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Resource#setContent(java.lang.Object) 	 */
specifier|public
name|void
name|setContent
parameter_list|(
name|Object
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|value
operator|instanceof
name|File
condition|)
block|{
name|file
operator|=
operator|(
name|File
operator|)
name|value
expr_stmt|;
name|isExternal
operator|=
literal|true
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|InputSource
condition|)
block|{
name|inputSource
operator|=
operator|(
name|InputSource
operator|)
name|value
expr_stmt|;
name|isExternal
operator|=
literal|true
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|rawData
operator|=
operator|(
name|byte
index|[]
operator|)
name|value
expr_stmt|;
name|isExternal
operator|=
literal|true
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|rawData
operator|=
operator|(
operator|(
name|String
operator|)
name|value
operator|)
operator|.
name|getBytes
argument_list|()
expr_stmt|;
name|isExternal
operator|=
literal|true
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"don't know how to handle value of type "
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|InputStream
name|getStreamContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|InputStream
name|retval
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|retval
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
comment|// Cannot fire it :-(
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|fnfe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|fnfe
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|inputSource
operator|!=
literal|null
condition|)
block|{
name|retval
operator|=
name|inputSource
operator|.
name|getByteStream
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|rawData
operator|!=
literal|null
condition|)
block|{
name|retval
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|rawData
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BinaryDocument
name|blob
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|blob
operator|=
operator|(
name|BinaryDocument
operator|)
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|blob
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Permission denied to read resource"
argument_list|)
throw|;
name|retval
operator|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|blob
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|releaseDocument
argument_list|(
name|blob
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|retval
return|;
block|}
specifier|public
name|void
name|getContentIntoAFile
parameter_list|(
name|File
name|tmpfile
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpfile
argument_list|)
decl_stmt|;
name|BufferedOutputStream
name|bos
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|fos
argument_list|)
decl_stmt|;
name|getContentIntoAStream
argument_list|(
name|bos
argument_list|)
expr_stmt|;
name|bos
operator|.
name|close
argument_list|()
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|getContentIntoAStream
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BinaryDocument
name|blob
init|=
literal|null
decl_stmt|;
name|boolean
name|doClose
init|=
literal|false
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|blob
operator|=
operator|(
name|BinaryDocument
operator|)
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|blob
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Permission denied to read resource"
argument_list|)
throw|;
comment|// Improving the performance a bit for files!
if|if
condition|(
name|os
operator|instanceof
name|FileOutputStream
condition|)
block|{
name|os
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|os
argument_list|,
literal|655360
argument_list|)
expr_stmt|;
name|doClose
operator|=
literal|true
expr_stmt|;
block|}
name|broker
operator|.
name|readBinaryResource
argument_list|(
name|blob
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|releaseDocument
argument_list|(
name|blob
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
if|if
condition|(
name|doClose
condition|)
block|{
try|try
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// IgnoreIT(R)
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|freeResources
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isExternal
operator|&&
name|file
operator|!=
literal|null
condition|)
block|{
name|file
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|getStreamLength
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|long
name|retval
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|retval
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|inputSource
operator|!=
literal|null
operator|&&
name|inputSource
operator|instanceof
name|EXistInputSource
condition|)
block|{
name|retval
operator|=
operator|(
operator|(
name|EXistInputSource
operator|)
name|inputSource
operator|)
operator|.
name|getByteStreamLength
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|rawData
operator|!=
literal|null
condition|)
block|{
name|retval
operator|=
name|rawData
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BinaryDocument
name|blob
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|blob
operator|=
operator|(
name|BinaryDocument
operator|)
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|retval
operator|=
name|blob
operator|.
name|getContentLength
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|releaseDocument
argument_list|(
name|blob
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|retval
return|;
block|}
specifier|private
name|byte
index|[]
name|readFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
return|return
name|readFile
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" could not be found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|byte
index|[]
name|readFile
parameter_list|(
name|InputSource
name|is
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|readFile
argument_list|(
name|is
operator|.
name|getByteStream
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|byte
index|[]
name|readFile
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|2048
argument_list|)
decl_stmt|;
name|byte
index|[]
name|temp
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|is
operator|.
name|read
argument_list|(
name|temp
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" could not be found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"IO exception while reading file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getCreationTime() 	 */
specifier|public
name|Date
name|getCreationTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|isNewResource
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"The resource has not yet been stored"
argument_list|)
throw|;
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|BinaryDocument
name|blob
init|=
operator|(
name|BinaryDocument
operator|)
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|NO_LOCK
argument_list|)
decl_stmt|;
return|return
operator|new
name|Date
argument_list|(
name|blob
operator|.
name|getMetadata
argument_list|()
operator|.
name|getCreated
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getLastModificationTime() 	 */
specifier|public
name|Date
name|getLastModificationTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|isNewResource
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"The resource has not yet been stored"
argument_list|)
throw|;
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|BinaryDocument
name|blob
init|=
operator|(
name|BinaryDocument
operator|)
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|NO_LOCK
argument_list|)
decl_stmt|;
return|return
operator|new
name|Date
argument_list|(
name|blob
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.AbstractEXistResource#getMimeType()      */
specifier|public
name|String
name|getMimeType
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|isNewResource
condition|)
return|return
name|mimeType
return|;
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|BinaryDocument
name|blob
init|=
operator|(
name|BinaryDocument
operator|)
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|NO_LOCK
argument_list|)
decl_stmt|;
name|mimeType
operator|=
name|blob
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
expr_stmt|;
return|return
name|mimeType
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getMode() 	 */
specifier|public
name|Permission
name|getPermissions
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|isNewResource
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"The resource has not yet been stored"
argument_list|)
throw|;
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|DocumentImpl
name|document
init|=
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|NO_LOCK
argument_list|)
decl_stmt|;
return|return
name|document
operator|!=
literal|null
condition|?
name|document
operator|.
name|getPermissions
argument_list|()
else|:
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getContentLength() 	 */
specifier|public
name|long
name|getContentLength
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|isNewResource
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"The resource has not yet been stored"
argument_list|)
throw|;
name|Subject
name|preserveSubject
init|=
name|pool
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|DocumentImpl
name|document
init|=
name|getDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|NO_LOCK
argument_list|)
decl_stmt|;
return|return
name|document
operator|.
name|getContentLength
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setSubject
argument_list|(
name|preserveSubject
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|DocumentImpl
name|getDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|int
name|lock
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|lock
operator|!=
name|Lock
operator|.
name|NO_LOCK
condition|)
block|{
try|try
block|{
name|document
operator|=
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|docId
argument_list|,
name|lock
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Permission denied for document "
operator|+
name|docId
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Failed to acquire lock on document "
operator|+
name|docId
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
name|document
operator|=
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Permission denied for document "
operator|+
name|docId
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|document
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|)
throw|;
block|}
if|if
condition|(
name|document
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|WRONG_CONTENT_TYPE
argument_list|,
literal|"Document "
operator|+
name|docId
operator|+
literal|" is not a binary resource"
argument_list|)
throw|;
block|}
return|return
name|document
return|;
block|}
specifier|protected
name|DocumentImpl
name|openDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|int
name|lockMode
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DocumentImpl
name|document
init|=
name|super
operator|.
name|openDocument
argument_list|(
name|broker
argument_list|,
name|lockMode
argument_list|)
decl_stmt|;
if|if
condition|(
name|document
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
name|closeDocument
argument_list|(
name|document
argument_list|,
name|lockMode
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|WRONG_CONTENT_TYPE
argument_list|,
literal|"Document "
operator|+
name|docId
operator|+
literal|" is not a binary resource"
argument_list|)
throw|;
block|}
return|return
name|document
return|;
block|}
block|}
end_class

end_unit

