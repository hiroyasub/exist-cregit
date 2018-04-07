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
name|IOException
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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|java
operator|.
name|util
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
name|zip
operator|.
name|DataFormatException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Inflater
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|FunctionE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Hex
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
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClient
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|Leasable
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
name|TemporaryFileManager
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
name|Resource
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
name|ResourceIterator
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
name|ResourceSet
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

begin_class
specifier|public
class|class
name|RemoteResourceSet
implements|implements
name|ResourceSet
block|{
specifier|private
specifier|final
name|Leasable
argument_list|<
name|XmlRpcClient
argument_list|>
name|leasableXmlRpcClient
decl_stmt|;
specifier|private
specifier|final
name|XmlRpcClient
name|xmlRpcClient
decl_stmt|;
specifier|private
specifier|final
name|RemoteCollection
name|collection
decl_stmt|;
specifier|private
name|int
name|handle
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|hash
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
name|List
name|resources
decl_stmt|;
specifier|private
specifier|final
name|Properties
name|outputProperties
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|RemoteResourceSet
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|RemoteResourceSet
parameter_list|(
specifier|final
name|Leasable
argument_list|<
name|XmlRpcClient
argument_list|>
name|leasableXmlRpcClient
parameter_list|,
specifier|final
name|XmlRpcClient
name|xmlRpcClient
parameter_list|,
specifier|final
name|RemoteCollection
name|col
parameter_list|,
specifier|final
name|Properties
name|properties
parameter_list|,
specifier|final
name|Object
index|[]
name|resources
parameter_list|,
specifier|final
name|int
name|handle
parameter_list|,
specifier|final
name|int
name|hash
parameter_list|)
block|{
name|this
operator|.
name|leasableXmlRpcClient
operator|=
name|leasableXmlRpcClient
expr_stmt|;
name|this
operator|.
name|xmlRpcClient
operator|=
name|xmlRpcClient
expr_stmt|;
name|this
operator|.
name|handle
operator|=
name|handle
expr_stmt|;
name|this
operator|.
name|hash
operator|=
name|hash
expr_stmt|;
name|this
operator|.
name|resources
operator|=
operator|new
name|ArrayList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|resources
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|col
expr_stmt|;
name|this
operator|.
name|outputProperties
operator|=
name|properties
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addResource
parameter_list|(
specifier|final
name|Resource
name|resource
parameter_list|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|handle
operator|<
literal|0
condition|)
block|{
return|return;
block|}
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|handle
argument_list|)
expr_stmt|;
if|if
condition|(
name|hash
operator|>
operator|-
literal|1
condition|)
name|params
operator|.
name|add
argument_list|(
name|hash
argument_list|)
expr_stmt|;
try|try
block|{
name|xmlRpcClient
operator|.
name|execute
argument_list|(
literal|"releaseQueryResult"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to release query result on server: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|hash
operator|=
operator|-
literal|1
expr_stmt|;
name|resources
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceIterator
name|getIterator
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
operator|new
name|NewResourceIterator
argument_list|()
return|;
block|}
specifier|public
name|ResourceIterator
name|getIterator
parameter_list|(
specifier|final
name|long
name|start
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
operator|new
name|NewResourceIterator
argument_list|(
name|start
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resource
name|getMembersAsResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|handle
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Path
name|tmpfile
init|=
name|TemporaryFileManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getTemporaryFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|OutputStream
name|os
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|tmpfile
argument_list|)
init|)
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|table
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|xmlRpcClient
operator|.
name|execute
argument_list|(
literal|"retrieveAllFirstChunk"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|long
name|offset
init|=
operator|(
operator|(
name|Integer
operator|)
name|table
operator|.
name|get
argument_list|(
literal|"offset"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|table
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isCompressed
init|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|COMPRESS_OUTPUT
argument_list|,
literal|"no"
argument_list|)
argument_list|)
decl_stmt|;
comment|// One for the local cached file
name|Inflater
name|dec
init|=
literal|null
decl_stmt|;
name|byte
index|[]
name|decResult
init|=
literal|null
decl_stmt|;
name|int
name|decLength
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|isCompressed
condition|)
block|{
name|dec
operator|=
operator|new
name|Inflater
argument_list|()
expr_stmt|;
name|decResult
operator|=
operator|new
name|byte
index|[
literal|65536
index|]
expr_stmt|;
name|dec
operator|.
name|setInput
argument_list|(
name|data
argument_list|)
expr_stmt|;
do|do
block|{
name|decLength
operator|=
name|dec
operator|.
name|inflate
argument_list|(
name|decResult
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|decResult
argument_list|,
literal|0
argument_list|,
name|decLength
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|decLength
operator|==
name|decResult
operator|.
name|length
operator|||
operator|!
name|dec
operator|.
name|needsInput
argument_list|()
condition|)
do|;
block|}
else|else
block|{
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|table
operator|.
name|get
argument_list|(
literal|"handle"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|offset
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|xmlRpcClient
operator|.
name|execute
argument_list|(
literal|"getNextExtendedChunk"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|offset
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
operator|(
name|String
operator|)
name|table
operator|.
name|get
argument_list|(
literal|"offset"
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|=
operator|(
name|byte
index|[]
operator|)
name|table
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
expr_stmt|;
comment|// One for the local cached file
if|if
condition|(
name|isCompressed
condition|)
block|{
name|dec
operator|.
name|setInput
argument_list|(
name|data
argument_list|)
expr_stmt|;
do|do
block|{
name|decLength
operator|=
name|dec
operator|.
name|inflate
argument_list|(
name|decResult
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|decResult
argument_list|,
literal|0
argument_list|,
name|decLength
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|decLength
operator|==
name|decResult
operator|.
name|length
operator|||
operator|!
name|dec
operator|.
name|needsInput
argument_list|()
condition|)
do|;
block|}
else|else
block|{
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dec
operator|!=
literal|null
condition|)
block|{
name|dec
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
specifier|final
name|RemoteXMLResource
name|res
init|=
operator|new
name|RemoteXMLResource
argument_list|(
name|leasableXmlRpcClient
operator|.
name|lease
argument_list|()
argument_list|,
name|collection
argument_list|,
name|handle
argument_list|,
literal|0
argument_list|,
name|XmldbURI
operator|.
name|EMPTY_URI
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|tmpfile
argument_list|)
expr_stmt|;
name|res
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
name|xre
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|xmlRpcClient
operator|.
name|execute
argument_list|(
literal|"retrieveAll"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|String
name|content
decl_stmt|;
try|try
block|{
name|content
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|UnsupportedEncodingException
name|ue
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ue
argument_list|)
expr_stmt|;
name|content
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RemoteXMLResource
name|res
init|=
operator|new
name|RemoteXMLResource
argument_list|(
name|leasableXmlRpcClient
operator|.
name|lease
argument_list|()
argument_list|,
name|collection
argument_list|,
name|handle
argument_list|,
literal|0
argument_list|,
name|XmldbURI
operator|.
name|EMPTY_URI
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|res
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|DataFormatException
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
name|xre
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
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Resource
name|getResource
parameter_list|(
specifier|final
name|long
name|pos
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|pos
operator|>=
name|resources
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|resources
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
operator|instanceof
name|Resource
condition|)
block|{
return|return
operator|(
name|Resource
operator|)
name|resources
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|item
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|resources
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|item
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
condition|)
block|{
case|case
literal|"node()"
case|:
case|case
literal|"document-node()"
case|:
case|case
literal|"element()"
case|:
case|case
literal|"attribute()"
case|:
case|case
literal|"text()"
case|:
case|case
literal|"processing-instruction()"
case|:
case|case
literal|"comment()"
case|:
case|case
literal|"namespace()"
case|:
case|case
literal|"cdata-section()"
case|:
return|return
name|getResourceNode
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|,
name|item
argument_list|)
return|;
case|case
literal|"xs:base64Binary"
case|:
return|return
name|getResourceBinaryValue
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|,
name|item
argument_list|,
name|Base64
operator|::
name|decodeBase64
argument_list|)
return|;
case|case
literal|"xs:hexBinary"
case|:
return|return
name|getResourceBinaryValue
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|,
name|item
argument_list|,
name|Hex
operator|::
name|decodeHex
argument_list|)
return|;
default|default:
comment|// atomic value
return|return
name|getResourceValue
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|,
name|item
argument_list|)
return|;
block|}
block|}
block|}
specifier|private
name|RemoteXMLResource
name|getResourceNode
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nodeDetail
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|doc
init|=
name|nodeDetail
operator|.
name|get
argument_list|(
literal|"docUri"
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|s_id
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|nodeDetail
operator|.
name|get
argument_list|(
literal|"nodeId"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|XmldbURI
name|docUri
decl_stmt|;
try|try
block|{
name|docUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
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
specifier|final
name|RemoteCollection
name|parent
decl_stmt|;
if|if
condition|(
name|docUri
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|DB
argument_list|)
condition|)
block|{
name|parent
operator|=
name|RemoteCollection
operator|.
name|instance
argument_list|(
name|leasableXmlRpcClient
argument_list|,
name|docUri
operator|.
name|removeLastSegment
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//fake to provide a RemoteCollection for local files that have been transferred by xml-rpc
name|parent
operator|=
name|collection
expr_stmt|;
block|}
name|parent
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
specifier|final
name|RemoteXMLResource
name|res
init|=
operator|new
name|RemoteXMLResource
argument_list|(
name|leasableXmlRpcClient
operator|.
name|lease
argument_list|()
argument_list|,
name|parent
argument_list|,
name|handle
argument_list|,
name|pos
argument_list|,
name|docUri
argument_list|,
name|s_id
argument_list|)
decl_stmt|;
name|res
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
specifier|private
name|RemoteXMLResource
name|getResourceValue
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|valueDetail
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|RemoteXMLResource
name|res
init|=
operator|new
name|RemoteXMLResource
argument_list|(
name|leasableXmlRpcClient
operator|.
name|lease
argument_list|()
argument_list|,
name|collection
argument_list|,
name|handle
argument_list|,
name|pos
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|pos
argument_list|)
argument_list|)
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|valueDetail
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
specifier|private
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|RemoteBinaryResource
name|getResourceBinaryValue
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|valueDetail
parameter_list|,
specifier|final
name|FunctionE
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|,
name|E
argument_list|>
name|binaryDecoder
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|type
init|=
name|valueDetail
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|content
decl_stmt|;
try|try
block|{
name|content
operator|=
name|binaryDecoder
operator|.
name|apply
argument_list|(
name|valueDetail
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
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
argument_list|)
throw|;
block|}
specifier|final
name|RemoteBinaryResource
name|res
init|=
operator|new
name|RemoteBinaryResource
argument_list|(
name|leasableXmlRpcClient
operator|.
name|lease
argument_list|()
argument_list|,
name|collection
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|pos
argument_list|)
argument_list|)
argument_list|,
name|type
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|res
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|resources
operator|==
literal|null
condition|?
literal|0
else|:
operator|(
name|long
operator|)
name|resources
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeResource
parameter_list|(
specifier|final
name|long
name|pos
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|resources
operator|.
name|remove
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|clear
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
class|class
name|NewResourceIterator
implements|implements
name|ResourceIterator
block|{
name|long
name|pos
init|=
literal|0
decl_stmt|;
specifier|public
name|NewResourceIterator
parameter_list|()
block|{
block|}
specifier|public
name|NewResourceIterator
parameter_list|(
specifier|final
name|long
name|start
parameter_list|)
block|{
name|pos
operator|=
name|start
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasMoreResources
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|resources
operator|==
literal|null
condition|?
literal|false
else|:
name|pos
operator|<
name|resources
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Resource
name|nextResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getResource
argument_list|(
name|pos
operator|++
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

