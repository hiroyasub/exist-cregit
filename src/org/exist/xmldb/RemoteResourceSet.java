begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|BufferedOutputStream
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
name|FileOutputStream
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
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
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
specifier|protected
name|RemoteCollection
name|collection
decl_stmt|;
specifier|protected
name|int
name|handle
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|List
name|resources
decl_stmt|;
specifier|protected
name|Properties
name|outputProperties
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|Logger
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
name|RemoteCollection
name|col
parameter_list|,
name|Properties
name|properties
parameter_list|,
name|Object
index|[]
name|resources
parameter_list|,
name|int
name|handle
parameter_list|)
block|{
name|this
operator|.
name|handle
operator|=
name|handle
expr_stmt|;
name|this
operator|.
name|resources
operator|=
operator|new
name|ArrayList
argument_list|(
name|resources
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|resources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|resources
operator|.
name|add
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|void
name|addResource
parameter_list|(
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
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|handle
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|collection
operator|.
name|getClient
argument_list|()
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
name|XmlRpcException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to release query result on server: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|resources
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
specifier|public
name|Resource
name|getMembersAsResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Integer
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
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
name|BufferedOutputStream
name|bos
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|File
name|tmpfile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"eXistARR"
argument_list|,
literal|".xml"
argument_list|)
decl_stmt|;
name|tmpfile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpfile
argument_list|)
expr_stmt|;
name|bos
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|Map
name|table
init|=
operator|(
name|Map
operator|)
name|collection
operator|.
name|getClient
argument_list|()
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
name|boolean
name|isCompressed
init|=
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
operator|.
name|equals
argument_list|(
literal|"yes"
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
name|bos
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
name|bos
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
operator|)
name|collection
operator|.
name|getClient
argument_list|()
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
operator|new
name|Long
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
operator|.
name|longValue
argument_list|()
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
name|bos
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
name|bos
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
name|dec
operator|.
name|end
argument_list|()
expr_stmt|;
name|RemoteXMLResource
name|res
init|=
operator|new
name|RemoteXMLResource
argument_list|(
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
literal|null
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
name|XmlRpcException
name|xre
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|collection
operator|.
name|getClient
argument_list|()
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
name|RemoteXMLResource
name|res
init|=
operator|new
name|RemoteXMLResource
argument_list|(
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
literal|null
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
name|DataFormatException
name|dfe
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
name|dfe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|dfe
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|bos
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|bos
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
comment|//IgnoreIT(R)
block|}
block|}
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
block|{
try|try
block|{
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
comment|//IgnoreIT(R)
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
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
specifier|public
name|Resource
name|getResource
parameter_list|(
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
return|return
literal|null
return|;
comment|// node or value?
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
name|Object
index|[]
condition|)
block|{
comment|// node
name|Object
index|[]
name|v
init|=
operator|(
name|Object
index|[]
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
name|String
name|doc
init|=
operator|(
name|String
operator|)
name|v
index|[
literal|0
index|]
decl_stmt|;
name|String
name|s_id
init|=
operator|(
name|String
operator|)
name|v
index|[
literal|1
index|]
decl_stmt|;
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
name|RemoteCollection
name|parent
init|=
operator|new
name|RemoteCollection
argument_list|(
name|collection
operator|.
name|getClient
argument_list|()
argument_list|,
literal|null
argument_list|,
name|docUri
operator|.
name|removeLastSegment
argument_list|()
argument_list|)
decl_stmt|;
name|parent
operator|.
name|properties
operator|=
name|outputProperties
expr_stmt|;
name|RemoteXMLResource
name|res
init|=
operator|new
name|RemoteXMLResource
argument_list|(
name|parent
argument_list|,
name|handle
argument_list|,
operator|(
name|int
operator|)
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
if|else if
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
else|else
block|{
comment|// value
name|RemoteXMLResource
name|res
init|=
operator|new
name|RemoteXMLResource
argument_list|(
name|collection
argument_list|,
name|handle
argument_list|,
operator|(
name|int
operator|)
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
literal|null
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|resources
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|pos
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
block|}
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
specifier|public
name|void
name|removeResource
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|resources
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
expr_stmt|;
block|}
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
name|long
name|start
parameter_list|)
block|{
name|pos
operator|=
name|start
expr_stmt|;
block|}
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

