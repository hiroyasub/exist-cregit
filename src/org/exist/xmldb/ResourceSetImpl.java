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
name|XMLResource
import|;
end_import

begin_class
specifier|public
class|class
name|ResourceSetImpl
implements|implements
name|ResourceSet
block|{
specifier|protected
name|CollectionImpl
name|collection
decl_stmt|;
specifier|protected
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|protected
name|int
name|indentXML
init|=
literal|0
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
name|XmlRpcClient
name|rpcClient
decl_stmt|;
specifier|public
name|ResourceSetImpl
parameter_list|(
name|CollectionImpl
name|col
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|col
expr_stmt|;
name|resources
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ResourceSetImpl
parameter_list|(
name|CollectionImpl
name|col
parameter_list|,
name|Vector
name|resources
parameter_list|,
name|int
name|handle
parameter_list|,
name|int
name|indentXML
parameter_list|,
name|String
name|encoding
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
name|indentXML
operator|=
name|indentXML
expr_stmt|;
name|this
operator|.
name|encoding
operator|=
name|encoding
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
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NOT_IMPLEMENTED
argument_list|)
throw|;
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
literal|'/'
argument_list|)
argument_list|)
decl_stmt|;
name|CollectionImpl
name|parent
init|=
operator|new
name|CollectionImpl
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
name|XMLResource
name|res
init|=
operator|new
name|XMLResourceImpl
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
argument_list|,
name|indentXML
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
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
name|XMLResource
name|res
init|=
operator|new
name|XMLResourceImpl
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
argument_list|,
name|indentXML
argument_list|,
name|encoding
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
comment|/* (non-Javadoc) 	 * @see java.lang.Object#finalize() 	 */
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"releasing query results"
argument_list|)
expr_stmt|;
try|try
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
operator|new
name|Integer
argument_list|(
name|handle
argument_list|)
argument_list|)
expr_stmt|;
name|rpcClient
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

