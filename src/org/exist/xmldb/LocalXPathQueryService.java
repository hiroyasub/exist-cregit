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
name|StringReader
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
name|Category
import|;
end_import

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
name|ArraySet
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
name|DocumentSet
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
name|NodeProxy
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
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|parser
operator|.
name|XPathLexer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|parser
operator|.
name|XPathParser
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
name|User
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
name|xpath
operator|.
name|PathExpr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|Value
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
name|LocalXPathQueryService
implements|implements
name|XPathQueryServiceImpl
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|LocalXPathQueryService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|protected
name|LocalCollection
name|collection
decl_stmt|;
specifier|protected
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|protected
name|boolean
name|indentXML
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|saxDocumentEvents
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|createContainerElements
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|matchTagging
init|=
literal|true
decl_stmt|;
specifier|protected
name|User
name|user
decl_stmt|;
specifier|public
name|LocalXPathQueryService
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
block|}
specifier|public
name|void
name|clearNamespaces
parameter_list|()
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
name|getNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
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
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"pretty"
argument_list|)
condition|)
return|return
name|indentXML
condition|?
literal|"true"
else|:
literal|"false"
return|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"encoding"
argument_list|)
condition|)
return|return
name|encoding
return|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"create-container-elements"
argument_list|)
condition|)
return|return
name|createContainerElements
condition|?
literal|"true"
else|:
literal|"false"
return|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"match-tagging"
argument_list|)
condition|)
return|return
name|matchTagging
condition|?
literal|"true"
else|:
literal|"false"
return|;
return|return
literal|null
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
name|String
name|query
parameter_list|,
name|String
name|sortBy
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
operator|(
name|query
operator|.
name|startsWith
argument_list|(
literal|"document("
argument_list|)
operator|||
name|query
operator|.
name|startsWith
argument_list|(
literal|"collection("
argument_list|)
operator|||
name|query
operator|.
name|startsWith
argument_list|(
literal|"xcollection("
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
name|collection
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/db"
argument_list|)
operator|||
name|collection
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
name|query
operator|=
literal|"document(*)"
operator|+
name|query
expr_stmt|;
else|else
name|query
operator|=
literal|"collection('"
operator|+
name|collection
operator|.
name|getPath
argument_list|()
operator|+
literal|"')"
operator|+
name|query
expr_stmt|;
block|}
return|return
name|doQuery
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|sortBy
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
name|sortBy
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|NodeProxy
name|node
init|=
operator|(
operator|(
name|LocalXMLResource
operator|)
name|res
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
comment|// resource is a document
if|if
condition|(
operator|!
operator|(
name|query
operator|.
name|startsWith
argument_list|(
literal|"document("
argument_list|)
operator|||
name|query
operator|.
name|startsWith
argument_list|(
literal|"collection("
argument_list|)
operator|)
condition|)
name|query
operator|=
literal|"document('"
operator|+
name|res
operator|.
name|getDocumentId
argument_list|()
operator|+
literal|"')"
operator|+
name|query
expr_stmt|;
block|}
name|NodeSet
name|set
init|=
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|node
operator|.
name|getDoc
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|doQuery
argument_list|(
name|query
argument_list|,
name|docs
argument_list|,
name|set
argument_list|,
name|sortBy
argument_list|)
return|;
block|}
specifier|protected
name|ResourceSet
name|doQuery
parameter_list|(
name|String
name|query
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|XPathLexer
name|lexer
init|=
operator|new
name|XPathLexer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|XPathParser
name|parser
init|=
operator|new
name|XPathParser
argument_list|(
name|brokerPool
argument_list|,
name|user
argument_list|,
name|lexer
argument_list|)
decl_stmt|;
name|PathExpr
name|expr
init|=
operator|new
name|PathExpr
argument_list|(
name|brokerPool
argument_list|)
decl_stmt|;
name|parser
operator|.
name|expr
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"query: "
operator|+
name|expr
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|foundErrors
argument_list|()
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|parser
operator|.
name|getErrorMsg
argument_list|()
argument_list|)
throw|;
name|docs
operator|=
operator|(
name|docs
operator|==
literal|null
condition|?
name|expr
operator|.
name|preselect
argument_list|()
else|:
name|expr
operator|.
name|preselect
argument_list|(
name|docs
argument_list|)
operator|)
expr_stmt|;
if|if
condition|(
name|docs
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|Value
name|resultValue
init|=
name|expr
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|expr
operator|.
name|pprint
argument_list|()
operator|+
literal|" found: "
operator|+
name|resultValue
operator|.
name|getLength
argument_list|()
operator|+
literal|" in "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"ms."
argument_list|)
expr_stmt|;
name|LocalResourceSet
name|result
init|=
operator|new
name|LocalResourceSet
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|collection
argument_list|,
name|resultValue
argument_list|,
name|indentXML
argument_list|,
name|encoding
argument_list|,
name|saxDocumentEvents
argument_list|,
name|createContainerElements
argument_list|,
name|matchTagging
argument_list|,
name|sortExpr
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|antlr
operator|.
name|RecognitionException
name|re
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
name|re
operator|.
name|getMessage
argument_list|()
argument_list|,
name|re
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|antlr
operator|.
name|TokenStreamException
name|te
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
name|te
operator|.
name|getMessage
argument_list|()
argument_list|,
name|te
argument_list|)
throw|;
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
comment|/** 	 *  Description of the Method 	 * 	 *@param  resource            Description of the Parameter 	 *@param  query               Description of the Parameter 	 *@return                     Description of the Return Value 	 *@exception  XMLDBException  Description of the Exception 	 */
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
name|query
operator|=
literal|"document('"
operator|+
name|collection
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|resource
operator|+
literal|"')"
operator|+
name|query
expr_stmt|;
return|return
name|query
argument_list|(
name|query
argument_list|)
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  ns                  Description of the Parameter 	 *@exception  XMLDBException  Description of the Exception 	 */
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
comment|/** 	 *  Sets the collection attribute of the LocalXPathQueryService object 	 * 	 *@param  col                 The new collection value 	 *@exception  XMLDBException  Description of the Exception 	 */
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
comment|/** 	 *  Sets the namespace attribute of the LocalXPathQueryService object 	 * 	 *@param  prefix              The new namespace value 	 *@param  namespace           The new namespace value 	 *@exception  XMLDBException  Description of the Exception 	 */
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
comment|/** 	 *  Sets the property attribute of the LocalXPathQueryService object 	 * 	 *@param  property            The new property value 	 *@param  value               The new property value 	 *@exception  XMLDBException  Description of the Exception 	 */
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
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"pretty"
argument_list|)
condition|)
name|indentXML
operator|=
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"encoding"
argument_list|)
condition|)
block|{
name|encoding
operator|=
name|value
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"encoding = "
operator|+
name|encoding
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"sax-document-events"
argument_list|)
condition|)
name|saxDocumentEvents
operator|=
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"create-container-elements"
argument_list|)
condition|)
name|createContainerElements
operator|=
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"match-tagging"
argument_list|)
condition|)
name|matchTagging
operator|=
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

