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
name|Map
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
name|TreeMap
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
name|XPathLexer2
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
name|XPathParser2
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
name|XPathTreeParser2
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
name|StaticContext
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
name|XPathException
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
name|value
operator|.
name|Sequence
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

begin_import
import|import
name|antlr
operator|.
name|collections
operator|.
name|AST
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
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LocalXPathQueryService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
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
name|User
name|user
decl_stmt|;
specifier|protected
name|TreeMap
name|namespaceDecls
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|protected
name|TreeMap
name|variableDecls
init|=
operator|new
name|TreeMap
argument_list|()
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
name|namespaceDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
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
return|return
name|properties
operator|.
name|getProperty
argument_list|(
name|property
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
name|DocumentSet
name|docs
init|=
literal|null
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"query: "
operator|+
name|query
argument_list|)
expr_stmt|;
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
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|docs
operator|=
name|collection
operator|.
name|collection
operator|.
name|allDocs
argument_list|(
name|broker
argument_list|,
literal|true
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
name|UNKNOWN_ERROR
argument_list|,
literal|"error while loading documents: "
operator|+
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
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doQuery
argument_list|(
name|query
argument_list|,
name|docs
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
name|contextSet
parameter_list|,
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|StaticContext
name|context
init|=
operator|new
name|StaticContext
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|context
operator|.
name|setBaseURI
argument_list|(
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
name|entry
decl_stmt|;
comment|// declare namespace/prefix mappings
for|for
control|(
name|Iterator
name|i
init|=
name|namespaceDecls
operator|.
name|entrySet
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
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"prefix "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" = "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareNamespace
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// declare static variables
for|for
control|(
name|Iterator
name|i
init|=
name|variableDecls
operator|.
name|entrySet
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
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|XPathLexer2
name|lexer
init|=
operator|new
name|XPathLexer2
argument_list|(
operator|new
name|StringReader
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|XPathParser2
name|parser
init|=
operator|new
name|XPathParser2
argument_list|(
name|lexer
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|XPathTreeParser2
name|treeParser
init|=
operator|new
name|XPathTreeParser2
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|parser
operator|.
name|xpath
argument_list|()
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|parser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
name|parser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
block|}
name|AST
name|ast
init|=
name|parser
operator|.
name|getAST
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"generated AST: "
operator|+
name|ast
operator|.
name|toStringTree
argument_list|()
argument_list|)
expr_stmt|;
name|PathExpr
name|expr
init|=
operator|new
name|PathExpr
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|treeParser
operator|.
name|xpath
argument_list|(
name|ast
argument_list|,
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
name|treeParser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
name|treeParser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
block|}
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
comment|//if (parser.foundErrors())
comment|//	throw new XMLDBException(ErrorCodes.VENDOR_ERROR, parser.getErrorMsg());
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
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
argument_list|(
name|context
argument_list|)
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
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"no documents!"
argument_list|)
expr_stmt|;
block|}
else|else
name|result
operator|=
name|expr
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSet
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|result
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
name|resultSet
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
name|result
argument_list|,
name|properties
argument_list|,
name|sortExpr
argument_list|)
decl_stmt|;
return|return
name|resultSet
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
name|IllegalArgumentException
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
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
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
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|LocalXMLResource
name|res
init|=
operator|(
name|LocalXMLResource
operator|)
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
literal|"resource "
operator|+
name|resource
operator|+
literal|" not found"
argument_list|)
throw|;
name|docs
operator|.
name|add
argument_list|(
name|res
operator|.
name|getDocument
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
literal|null
argument_list|,
literal|null
argument_list|)
return|;
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
name|namespaceDecls
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
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
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
name|namespaceDecls
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
name|properties
operator|.
name|setProperty
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

