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
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|RemoteXPathQueryService
implements|implements
name|XPathQueryServiceImpl
block|{
specifier|protected
name|RemoteCollection
name|collection
decl_stmt|;
specifier|protected
name|Hashtable
name|namespaceMappings
init|=
operator|new
name|Hashtable
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|public
name|RemoteXPathQueryService
parameter_list|(
name|RemoteCollection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
specifier|public
name|ResourceSet
name|query
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|ResourceSet
name|query
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
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
name|query
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortExpr
operator|!=
literal|null
condition|)
name|params
operator|.
name|addElement
argument_list|(
name|sortExpr
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|namespaceMappings
argument_list|)
expr_stmt|;
name|Hashtable
name|result
init|=
operator|(
name|Hashtable
operator|)
name|collection
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"queryP"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Vector
name|resources
init|=
operator|(
name|Vector
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"results"
argument_list|)
decl_stmt|;
name|int
name|handle
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
operator|&&
name|resources
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|handle
operator|=
operator|(
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
return|return
operator|new
name|ResourceSetImpl
argument_list|(
name|collection
argument_list|,
name|resources
argument_list|,
name|handle
argument_list|)
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
name|VENDOR_ERROR
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
name|ResourceSet
name|query
parameter_list|(
name|XMLResource
name|res
parameter_list|,
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
name|res
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|ResourceSet
name|query
parameter_list|(
name|XMLResource
name|res
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|RemoteXMLResource
name|resource
init|=
operator|(
name|RemoteXMLResource
operator|)
name|res
decl_stmt|;
try|try
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
name|query
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|resource
operator|.
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|resource
operator|.
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortExpr
operator|!=
literal|null
condition|)
name|params
operator|.
name|addElement
argument_list|(
name|sortExpr
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|namespaceMappings
argument_list|)
expr_stmt|;
name|Hashtable
name|result
init|=
operator|(
name|Hashtable
operator|)
name|collection
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"queryP"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Vector
name|resources
init|=
operator|(
name|Vector
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"results"
argument_list|)
decl_stmt|;
name|int
name|handle
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
operator|&&
name|resources
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|handle
operator|=
operator|(
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
return|return
operator|new
name|ResourceSetImpl
argument_list|(
name|collection
argument_list|,
name|resources
argument_list|,
name|handle
argument_list|)
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
name|VENDOR_ERROR
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
comment|/**      *  Description of the Method      *      *@param  resource            Description of the Parameter      *@param  query               Description of the Parameter      *@return                     Description of the Return Value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|ResourceSet
name|queryResource
parameter_list|(
name|String
name|resource
parameter_list|,
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
name|query
argument_list|)
return|;
block|}
comment|/**      *  Gets the version attribute of the XPathQueryServiceImpl object      *      *@return                     The version value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|String
name|getVersion
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"1.0"
return|;
block|}
comment|/**      *  Sets the collection attribute of the XPathQueryServiceImpl object      *      *@param  col                 The new collection value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|col
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
comment|/**      *  Gets the name attribute of the XPathQueryServiceImpl object      *      *@return                     The name value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"XPathQueryService"
return|;
block|}
comment|/**      *  Gets the property attribute of the XPathQueryServiceImpl object      *      *@param  property            Description of the Parameter      *@return                     The property value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|collection
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
return|;
block|}
comment|/**      *  Sets the property attribute of the XPathQueryServiceImpl object      *      *@param  property            The new property value      *@param  value               The new property value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|collection
operator|.
name|setProperty
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|clearNamespaces
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|namespaceMappings
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  ns                  Description of the Parameter      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|removeNamespace
parameter_list|(
name|String
name|ns
parameter_list|)
throws|throws
name|XMLDBException
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|namespaceMappings
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
operator|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
condition|)
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      *  Sets the namespace attribute of the XPathQueryServiceImpl object      *      *@param  prefix              The new namespace value      *@param  namespace           The new namespace value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|setNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespace
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|namespaceMappings
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Gets the namespace attribute of the XPathQueryServiceImpl object      *      *@param  prefix              Description of the Parameter      *@return                     The namespace value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|String
name|getNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
operator|(
name|String
operator|)
name|namespaceMappings
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.XPathQueryServiceImpl#declareVariable(java.lang.String, java.lang.Object) 	 */
specifier|public
name|void
name|declareVariable
parameter_list|(
name|String
name|qname
parameter_list|,
name|Object
name|initialValue
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|// TODO Not implemented
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NOT_IMPLEMENTED
argument_list|,
literal|"method not implemented"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

