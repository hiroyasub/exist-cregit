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
name|Writer
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
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmlrpc
operator|.
name|RpcAPI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
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
name|CompiledExpression
import|;
end_import

begin_class
specifier|public
class|class
name|RemoteXPathQueryService
implements|implements
name|XPathQueryServiceImpl
implements|,
name|XQueryService
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
specifier|protected
name|Hashtable
name|variableDecls
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
specifier|protected
name|Properties
name|outputProperties
init|=
literal|null
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
name|this
operator|.
name|outputProperties
operator|=
operator|new
name|Properties
argument_list|(
name|collection
operator|.
name|properties
argument_list|)
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
name|Hashtable
name|optParams
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
if|if
condition|(
name|sortExpr
operator|!=
literal|null
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|SORT_EXPR
argument_list|,
name|sortExpr
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaceMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|NAMESPACES
argument_list|,
name|namespaceMappings
argument_list|)
expr_stmt|;
if|if
condition|(
name|variableDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|VARIABLES
argument_list|,
name|variableDecls
argument_list|)
expr_stmt|;
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
literal|"base-uri"
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|optParams
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
if|if
condition|(
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
operator|!=
literal|null
condition|)
name|throwException
argument_list|(
name|result
argument_list|)
expr_stmt|;
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
name|RemoteResourceSet
argument_list|(
name|collection
argument_list|,
name|outputProperties
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
name|CompiledExpression
name|compile
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
return|return
name|compileAndCheck
argument_list|(
name|query
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
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
block|}
specifier|public
name|CompiledExpression
name|compileAndCheck
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|XPathException
block|{
try|try
block|{
name|Hashtable
name|optParams
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|NAMESPACES
argument_list|,
name|namespaceMappings
argument_list|)
expr_stmt|;
if|if
condition|(
name|variableDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|VARIABLES
argument_list|,
name|variableDecls
argument_list|)
expr_stmt|;
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
literal|"base-uri"
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|optParams
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
literal|"compile"
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
operator|!=
literal|null
condition|)
name|throwXPathException
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
operator|new
name|RemoteCompiledExpression
argument_list|(
name|query
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
comment|/** 	 * @param result      * @throws XPathException  	 */
specifier|private
name|void
name|throwException
parameter_list|(
name|Hashtable
name|result
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|message
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
decl_stmt|;
name|Integer
name|lineInt
init|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|LINE
argument_list|)
decl_stmt|;
name|Integer
name|columnInt
init|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
name|int
name|line
init|=
name|lineInt
operator|==
literal|null
condition|?
literal|0
else|:
name|lineInt
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|int
name|column
init|=
name|columnInt
operator|==
literal|null
condition|?
literal|0
else|:
name|columnInt
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|XPathException
name|cause
init|=
operator|new
name|XPathException
argument_list|(
name|message
argument_list|,
name|line
argument_list|,
name|column
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|message
argument_list|,
name|cause
argument_list|)
throw|;
block|}
specifier|private
name|void
name|throwXPathException
parameter_list|(
name|Hashtable
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|message
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
decl_stmt|;
name|Integer
name|lineInt
init|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|LINE
argument_list|)
decl_stmt|;
name|Integer
name|columnInt
init|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
name|int
name|line
init|=
name|lineInt
operator|==
literal|null
condition|?
literal|0
else|:
name|lineInt
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|int
name|column
init|=
name|columnInt
operator|==
literal|null
condition|?
literal|0
else|:
name|columnInt
operator|.
name|intValue
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|message
argument_list|,
name|line
argument_list|,
name|column
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.XQueryService#execute(org.exist.source.Source)      */
specifier|public
name|ResourceSet
name|execute
parameter_list|(
name|Source
name|source
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|String
name|xq
init|=
name|source
operator|.
name|getContent
argument_list|()
decl_stmt|;
return|return
name|query
argument_list|(
name|xq
argument_list|,
literal|null
argument_list|)
return|;
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
name|Hashtable
name|optParams
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|NAMESPACES
argument_list|,
name|namespaceMappings
argument_list|)
expr_stmt|;
if|if
condition|(
name|variableDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|VARIABLES
argument_list|,
name|variableDecls
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortExpr
operator|!=
literal|null
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|SORT_EXPR
argument_list|,
name|sortExpr
argument_list|)
expr_stmt|;
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
literal|"base-uri"
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|.
name|id
operator|==
literal|null
condition|)
name|params
operator|.
name|addElement
argument_list|(
literal|""
argument_list|)
expr_stmt|;
else|else
name|params
operator|.
name|addElement
argument_list|(
name|resource
operator|.
name|id
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|optParams
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
if|if
condition|(
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
operator|!=
literal|null
condition|)
name|throwException
argument_list|(
name|result
argument_list|)
expr_stmt|;
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
name|RemoteResourceSet
argument_list|(
name|collection
argument_list|,
name|outputProperties
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
name|Resource
name|res
init|=
name|collection
operator|.
name|getResource
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"Resource "
operator|+
name|resource
operator|+
literal|" not found"
argument_list|)
throw|;
if|if
condition|(
operator|!
literal|"XMLResource"
operator|.
name|equals
argument_list|(
name|res
operator|.
name|getResourceType
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"Resource "
operator|+
name|resource
operator|+
literal|" is not an XML resource"
argument_list|)
throw|;
return|return
name|query
argument_list|(
operator|(
name|XMLResource
operator|)
name|res
argument_list|,
name|query
argument_list|)
return|;
block|}
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
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
return|;
block|}
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
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
name|prefix
operator|=
literal|""
expr_stmt|;
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
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
name|prefix
operator|=
literal|""
expr_stmt|;
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
name|variableDecls
operator|.
name|put
argument_list|(
name|qname
argument_list|,
name|initialValue
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.XQueryService#execute(org.exist.xmldb.CompiledExpression) 	 */
specifier|public
name|ResourceSet
name|execute
parameter_list|(
name|CompiledExpression
name|expression
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
operator|(
operator|(
name|RemoteCompiledExpression
operator|)
name|expression
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.XQueryService#execute(org.xmldb.api.modules.XMLResource, org.exist.xmldb.CompiledExpression) 	 */
specifier|public
name|ResourceSet
name|execute
parameter_list|(
name|XMLResource
name|res
parameter_list|,
name|CompiledExpression
name|expression
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
name|res
argument_list|,
operator|(
operator|(
name|RemoteCompiledExpression
operator|)
name|expression
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.XQueryService#setXPathCompatibility(boolean) 	 */
specifier|public
name|void
name|setXPathCompatibility
parameter_list|(
name|boolean
name|backwardsCompatible
parameter_list|)
block|{
comment|// TODO: not passed
block|}
comment|/**  	 * Calling this method has no effect. The server loads modules 	 * relative to its own context. 	 *  	 * @see org.exist.xmldb.XQueryService#setModuleLoadPath(java.lang.String) 	 */
specifier|public
name|void
name|setModuleLoadPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.XQueryService#dump(org.exist.xmldb.CompiledExpression, java.io.Writer)      */
specifier|public
name|void
name|dump
parameter_list|(
name|CompiledExpression
name|expression
parameter_list|,
name|Writer
name|writer
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|query
init|=
operator|(
operator|(
name|RemoteCompiledExpression
operator|)
name|expression
operator|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Hashtable
name|optParams
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|NAMESPACES
argument_list|,
name|namespaceMappings
argument_list|)
expr_stmt|;
if|if
condition|(
name|variableDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|VARIABLES
argument_list|,
name|variableDecls
argument_list|)
expr_stmt|;
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
literal|"base-uri"
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|optParams
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|dump
init|=
operator|(
name|String
operator|)
name|collection
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"printDiagnostics"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|dump
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
comment|/* (non-Javadoc)      * @see org.exist.xmldb.XPathQueryServiceImpl#beginProtected()      */
specifier|public
name|void
name|beginProtected
parameter_list|()
block|{
comment|// not yet supported
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.XPathQueryServiceImpl#endProtected()      */
specifier|public
name|void
name|endProtected
parameter_list|()
block|{
comment|// not yet supported
block|}
block|}
end_class

end_unit

