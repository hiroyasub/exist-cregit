begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|MimeType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|DocumentType
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|RemoteBinaryResource
extends|extends
name|AbstractRemoteResource
implements|implements
name|BinaryResource
block|{
specifier|private
name|String
name|type
init|=
literal|null
decl_stmt|;
specifier|private
name|byte
index|[]
name|content
init|=
literal|null
decl_stmt|;
comment|// only used for binary results from an XQuery execution, where we have been sent the result
specifier|public
name|RemoteBinaryResource
parameter_list|(
specifier|final
name|RemoteCollection
name|parent
parameter_list|,
specifier|final
name|XmldbURI
name|documentName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|documentName
argument_list|,
name|MimeType
operator|.
name|BINARY_TYPE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RemoteBinaryResource
parameter_list|(
specifier|final
name|RemoteCollection
name|parent
parameter_list|,
specifier|final
name|XmldbURI
name|documentName
parameter_list|,
specifier|final
name|String
name|type
parameter_list|,
specifier|final
name|byte
index|[]
name|content
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|documentName
argument_list|,
name|MimeType
operator|.
name|BINARY_TYPE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getId
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|path
operator|.
name|lastSegment
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
name|getResourceType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|BinaryResource
operator|.
name|RESOURCE_TYPE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getExtendedContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getExtendedContentInternal
argument_list|(
name|content
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getStreamLength
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getStreamLengthInternal
argument_list|(
name|content
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getStreamContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getStreamContentInternal
argument_list|(
name|content
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|getContentIntoAStream
parameter_list|(
specifier|final
name|OutputStream
name|os
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|getContentIntoAStreamInternal
argument_list|(
name|os
argument_list|,
name|content
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getStreamSymbolicPath
parameter_list|()
block|{
name|String
name|retval
init|=
literal|"<streamunknown>"
decl_stmt|;
if|if
condition|(
name|vfile
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Object
name|content
init|=
name|vfile
operator|.
name|getContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|content
operator|instanceof
name|java
operator|.
name|io
operator|.
name|File
condition|)
block|{
name|retval
operator|=
operator|(
operator|(
name|java
operator|.
name|io
operator|.
name|File
operator|)
name|content
operator|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|content
operator|instanceof
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
condition|)
block|{
name|retval
operator|=
operator|(
operator|(
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
operator|)
name|content
operator|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
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
name|getSymbolicPath
argument_list|()
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setContent
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|setContentInternal
argument_list|(
name|obj
argument_list|)
condition|)
block|{
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
specifier|final
name|LexicalHandler
name|handler
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|DocumentType
name|getDocType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocType
parameter_list|(
specifier|final
name|DocumentType
name|doctype
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|void
name|setProperties
parameter_list|(
specifier|final
name|Properties
name|properties
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

