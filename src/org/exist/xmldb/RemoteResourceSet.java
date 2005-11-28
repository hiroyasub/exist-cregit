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
name|Vector
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
name|xmlrpc
operator|.
name|XmlRpcClient
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
name|Vector
name|resources
decl_stmt|;
specifier|protected
name|Properties
name|outputProperties
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
name|Vector
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
name|resources
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
name|addElement
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
operator|new
name|Integer
argument_list|(
name|handle
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
try|try
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
literal|""
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
name|elementAt
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
operator|instanceof
name|Vector
condition|)
block|{
comment|// node
name|Vector
name|v
init|=
operator|(
name|Vector
operator|)
name|resources
operator|.
name|elementAt
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
operator|.
name|elementAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|s_id
init|=
operator|(
name|String
operator|)
name|v
operator|.
name|elementAt
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|//TODO : use dedicated function in XmldbURI
name|String
name|path
init|=
name|doc
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|doc
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
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
name|path
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
name|doc
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
name|elementAt
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
name|elementAt
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
name|Long
operator|.
name|toString
argument_list|(
name|pos
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
name|elementAt
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
name|removeElementAt
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
expr_stmt|;
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

