begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
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
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|LexicalHandler
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

begin_comment
comment|/**  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|RemoteBinaryResource
implements|implements
name|BinaryResource
implements|,
name|EXistResource
block|{
specifier|private
name|String
name|documentName
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
specifier|private
name|String
name|mimeType
init|=
literal|"application/octet-stream"
decl_stmt|;
specifier|private
name|RemoteCollection
name|parent
decl_stmt|;
specifier|private
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
specifier|private
name|Permission
name|permissions
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|contentLen
init|=
literal|0
decl_stmt|;
specifier|public
name|RemoteBinaryResource
parameter_list|(
name|RemoteCollection
name|parent
parameter_list|,
name|String
name|documentName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|int
name|p
decl_stmt|;
if|if
condition|(
name|documentName
operator|!=
literal|null
operator|&&
operator|(
name|p
operator|=
name|documentName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|path
operator|=
name|documentName
expr_stmt|;
name|this
operator|.
name|documentName
operator|=
name|documentName
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|path
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|documentName
expr_stmt|;
name|this
operator|.
name|documentName
operator|=
name|documentName
expr_stmt|;
block|}
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
name|documentName
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
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Resource#getContent() 	 */
specifier|public
name|Object
name|getContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
return|return
name|data
return|;
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
try|try
block|{
name|data
operator|=
operator|(
name|byte
index|[]
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getBinaryResource"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|data
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Resource#setContent(java.lang.Object) 	 */
specifier|public
name|void
name|setContent
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|obj
operator|instanceof
name|File
condition|)
name|readFile
argument_list|(
operator|(
name|File
operator|)
name|obj
argument_list|)
expr_stmt|;
if|else if
condition|(
name|obj
operator|instanceof
name|byte
index|[]
condition|)
name|data
operator|=
operator|(
name|byte
index|[]
operator|)
name|obj
expr_stmt|;
if|else if
condition|(
name|obj
operator|instanceof
name|String
condition|)
name|data
operator|=
operator|(
operator|(
name|String
operator|)
name|obj
operator|)
operator|.
name|getBytes
argument_list|()
expr_stmt|;
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
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|private
name|void
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
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
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
name|data
operator|=
name|bos
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
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
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Date
operator|)
operator|(
operator|(
name|Vector
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getTimestamps"
argument_list|,
name|params
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
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
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getLastModificationTime() 	 */
specifier|public
name|Date
name|getLastModificationTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Date
operator|)
operator|(
operator|(
name|Vector
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getTimestamps"
argument_list|,
name|params
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
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
block|}
specifier|public
name|void
name|setPermissions
parameter_list|(
name|Permission
name|perms
parameter_list|)
block|{
name|this
operator|.
name|permissions
operator|=
name|perms
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getPermissions() 	 */
specifier|public
name|Permission
name|getPermissions
parameter_list|()
block|{
return|return
name|permissions
return|;
block|}
specifier|public
name|void
name|setContentLength
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|contentLen
operator|=
name|len
expr_stmt|;
block|}
specifier|public
name|int
name|getContentLength
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|contentLen
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#setLexicalHandler(org.xml.sax.ext.LexicalHandler) 	 */
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
name|LexicalHandler
name|handler
parameter_list|)
block|{
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.EXistResource#setMimeType(java.lang.String)      */
specifier|public
name|void
name|setMimeType
parameter_list|(
name|String
name|mime
parameter_list|)
block|{
name|this
operator|.
name|mimeType
operator|=
name|mime
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.EXistResource#getMimeType()      */
specifier|public
name|String
name|getMimeType
parameter_list|()
block|{
return|return
name|mimeType
return|;
block|}
block|}
end_class

end_unit

