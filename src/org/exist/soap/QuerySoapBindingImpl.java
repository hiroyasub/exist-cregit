begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  QueryServiceSoapBindingImpl.java This file was auto-generated from WSDL by  *  the Apache Axis Wsdl2java emitter.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
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
name|rmi
operator|.
name|RemoteException
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
name|DocumentImpl
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
name|Permission
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
name|storage
operator|.
name|serializers
operator|.
name|Serializer
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
name|Configuration
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
name|Value
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
name|ValueSet
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
name|NodeList
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
name|SAXException
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

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    30. April 2002  */
end_comment

begin_class
specifier|public
class|class
name|QuerySoapBindingImpl
implements|implements
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Query
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
literal|"QueryService"
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
comment|/**  Constructor for the QuerySoapBindingImpl object */
specifier|public
name|QuerySoapBindingImpl
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|()
condition|)
name|configure
argument_list|()
expr_stmt|;
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failed to initialize broker pool"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|QueryResponseCollection
index|[]
name|collectQueryInfo
parameter_list|(
name|TreeMap
name|collections
parameter_list|)
block|{
name|QueryResponseCollection
name|c
index|[]
init|=
operator|new
name|QueryResponseCollection
index|[
name|collections
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|QueryResponseDocument
name|doc
decl_stmt|;
name|QueryResponseDocument
name|docs
index|[]
decl_stmt|;
name|String
name|docId
decl_stmt|;
name|int
name|k
init|=
literal|0
decl_stmt|;
name|int
name|l
decl_stmt|;
name|TreeMap
name|documents
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|collections
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
name|k
operator|++
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|c
index|[
name|k
index|]
operator|=
operator|new
name|QueryResponseCollection
argument_list|()
expr_stmt|;
name|c
index|[
name|k
index|]
operator|.
name|setCollectionName
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|documents
operator|=
operator|(
name|TreeMap
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|docs
operator|=
operator|new
name|QueryResponseDocument
index|[
name|documents
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|c
index|[
name|k
index|]
operator|.
name|setDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|l
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|documents
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
name|l
operator|++
control|)
block|{
name|Map
operator|.
name|Entry
name|docEntry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|j
operator|.
name|next
argument_list|()
decl_stmt|;
name|doc
operator|=
operator|new
name|QueryResponseDocument
argument_list|()
expr_stmt|;
name|docId
operator|=
operator|(
name|String
operator|)
name|docEntry
operator|.
name|getKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|docId
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>
operator|-
literal|1
condition|)
name|docId
operator|=
name|docId
operator|.
name|substring
argument_list|(
name|docId
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setDocumentName
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setHitCount
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|docEntry
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|docs
index|[
name|l
index|]
operator|=
name|doc
expr_stmt|;
block|}
block|}
return|return
name|c
return|;
block|}
specifier|private
name|void
name|configure
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|pathSep
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|home
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|home
operator|+
name|pathSep
operator|+
literal|"conf.xml"
argument_list|)
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Session
name|getSession
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|Session
name|session
init|=
name|SessionManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getSession
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|session
operator|==
literal|null
condition|)
throw|throw
operator|new
name|java
operator|.
name|rmi
operator|.
name|RemoteException
argument_list|(
literal|"Session is invalid or timed out"
argument_list|)
throw|;
return|return
name|session
return|;
block|}
specifier|public
name|String
name|connect
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|User
name|u
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|u
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"user "
operator|+
name|user
operator|+
literal|" does not exist"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|u
operator|.
name|validate
argument_list|(
name|password
argument_list|)
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"the supplied password is invalid"
argument_list|)
throw|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"user "
operator|+
name|user
operator|+
literal|" connected"
argument_list|)
expr_stmt|;
return|return
name|SessionManager
operator|.
name|getInstance
argument_list|()
operator|.
name|createSession
argument_list|(
name|u
argument_list|)
return|;
block|}
specifier|public
name|void
name|disconnect
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RemoteException
block|{
name|SessionManager
name|manager
init|=
name|SessionManager
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|manager
operator|.
name|getSession
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"disconnecting session "
operator|+
name|id
argument_list|)
expr_stmt|;
name|manager
operator|.
name|disconnect
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 *  Gets the resource attribute of the QuerySoapBindingImpl object 	 * 	 *@param  name                          Description of the Parameter 	 *@param  prettyPrint                   Description of the Parameter 	 *@param  encoding                      Description of the Parameter 	 *@return                               The resource value 	 *@exception  java.rmi.RemoteException  Description of the Exception 	 */
specifier|public
name|String
name|getResource
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|indent
parameter_list|,
name|boolean
name|xinclude
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|Session
name|session
init|=
name|getSession
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|DocumentImpl
name|document
init|=
operator|(
name|DocumentImpl
operator|)
name|broker
operator|.
name|getDocument
argument_list|(
name|session
operator|.
name|getUser
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"resource "
operator|+
name|name
operator|+
literal|" not found"
argument_list|)
throw|;
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|Map
name|props
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|PRETTY_PRINT
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|indent
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|EXPAND_XINCLUDES
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|xinclude
argument_list|)
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
return|return
name|serializer
operator|.
name|serialize
argument_list|(
name|document
argument_list|)
return|;
comment|//			if (xml != null)
comment|//				try {
comment|//					return xml.getBytes("UTF-8");
comment|//				} catch (java.io.UnsupportedEncodingException e) {
comment|//					return xml.getBytes();
comment|//				}
comment|//
comment|//			return null;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
name|saxe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RemoteException
argument_list|(
name|saxe
operator|.
name|getMessage
argument_list|()
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
name|RemoteException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
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
name|RemoteException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  path                 Description of the Parameter 	 *@return                      Description of the Return Value 	 *@exception  RemoteException  Description of the Exception 	 */
specifier|public
name|Collection
name|listCollection
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|RemoteException
block|{
name|Session
name|session
init|=
name|getSession
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
name|path
operator|=
literal|"/db"
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"collection "
operator|+
name|path
operator|+
literal|" not found"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|collection
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|session
operator|.
name|getUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"permission denied"
argument_list|)
throw|;
name|Collection
name|c
init|=
operator|new
name|Collection
argument_list|()
decl_stmt|;
comment|// Sub-collections
name|String
name|childCollections
index|[]
init|=
operator|new
name|String
index|[
name|collection
operator|.
name|getChildCollectionCount
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|collection
operator|.
name|collectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
name|childCollections
index|[
name|j
index|]
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Resources
name|String
index|[]
name|resources
init|=
operator|new
name|String
index|[
name|collection
operator|.
name|getDocumentCount
argument_list|()
index|]
decl_stmt|;
name|j
operator|=
literal|0
expr_stmt|;
name|int
name|p
decl_stmt|;
name|String
name|resource
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|collection
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|resource
operator|=
operator|(
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getFileName
argument_list|()
expr_stmt|;
name|p
operator|=
name|resource
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|resources
index|[
name|j
index|]
operator|=
name|p
operator|<
literal|0
condition|?
name|resource
else|:
name|resource
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|setResources
argument_list|(
name|resources
argument_list|)
expr_stmt|;
name|c
operator|.
name|setCollections
argument_list|(
name|childCollections
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RemoteException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  query                         Description of the Parameter 	 *@return                               Description of the Return Value 	 *@exception  java.rmi.RemoteException  Description of the Exception 	 */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QueryResponse
name|query
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|query
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|Session
name|session
init|=
name|SessionManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getSession
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
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
literal|"document(*)"
operator|+
name|query
expr_stmt|;
name|QueryResponse
name|resp
init|=
operator|new
name|QueryResponse
argument_list|()
decl_stmt|;
name|resp
operator|.
name|setHits
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|StaticContext
name|context
init|=
operator|new
name|StaticContext
argument_list|(
name|session
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
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
argument_list|)
decl_stmt|;
name|XPathTreeParser2
name|treeParser
init|=
operator|new
name|XPathTreeParser2
argument_list|(
name|pool
argument_list|,
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
throw|throw
operator|new
name|RemoteException
argument_list|(
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
name|pool
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
name|EXistException
argument_list|(
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
name|DocumentSet
name|ndocs
init|=
name|expr
operator|.
name|preselect
argument_list|()
decl_stmt|;
if|if
condition|(
name|ndocs
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|resp
return|;
name|Value
name|value
init|=
name|expr
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|ndocs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|QueryResponseCollection
index|[]
name|collections
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Value
operator|.
name|isNodeList
condition|)
name|collections
operator|=
name|collectQueryInfo
argument_list|(
name|scanResults
argument_list|(
name|value
operator|.
name|getNodeList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|addQueryResult
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setCollections
argument_list|(
name|collections
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setHits
argument_list|(
name|value
operator|.
name|getNodeList
argument_list|()
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setQueryTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"query execution failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|resp
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  resultId                      Description of the Parameter 	 *@param  num                           Description of the Parameter 	 *@param  encoding                      Description of the Parameter 	 *@param  prettyPrint                   Description of the Parameter 	 *@return                               Description of the Return Value 	 *@exception  java.rmi.RemoteException  Description of the Exception 	 */
specifier|public
name|String
index|[]
name|retrieve
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|,
name|boolean
name|indent
parameter_list|,
name|boolean
name|xinclude
parameter_list|,
name|String
name|highlight
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|Session
name|session
init|=
name|SessionManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getSession
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|Value
name|qr
init|=
operator|(
name|Value
operator|)
name|session
operator|.
name|getQueryResult
argument_list|()
operator|.
name|result
decl_stmt|;
if|if
condition|(
name|qr
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"result set unknown or timed out"
argument_list|)
throw|;
name|String
name|xml
index|[]
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|qr
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Value
operator|.
name|isNodeList
case|:
name|NodeList
name|resultSet
init|=
name|qr
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
operator|--
name|start
expr_stmt|;
if|if
condition|(
name|start
operator|<
literal|0
operator|||
name|start
operator|>=
name|resultSet
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"index "
operator|+
name|start
operator|+
literal|" out of bounds ("
operator|+
name|resultSet
operator|.
name|getLength
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
if|if
condition|(
name|start
operator|+
name|howmany
operator|>=
name|resultSet
operator|.
name|getLength
argument_list|()
condition|)
name|howmany
operator|=
name|resultSet
operator|.
name|getLength
argument_list|()
operator|-
name|start
expr_stmt|;
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|Map
name|properties
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|PRETTY_PRINT
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|indent
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|EXPAND_XINCLUDES
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|xinclude
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|HIGHLIGHT_MATCHES
argument_list|,
name|highlight
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|xml
operator|=
operator|new
name|String
index|[
name|howmany
index|]
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
name|howmany
condition|;
name|i
operator|++
control|)
block|{
name|NodeProxy
name|proxy
init|=
operator|(
operator|(
name|NodeSet
operator|)
name|resultSet
operator|)
operator|.
name|get
argument_list|(
name|start
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|proxy
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not found: "
operator|+
operator|(
name|start
operator|+
name|i
operator|)
argument_list|)
throw|;
name|xml
index|[
name|i
index|]
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
break|break;
default|default :
name|ValueSet
name|valueSet
init|=
name|qr
operator|.
name|getValueSet
argument_list|()
decl_stmt|;
operator|--
name|start
expr_stmt|;
if|if
condition|(
name|start
operator|<
literal|0
operator|||
name|start
operator|>=
name|valueSet
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"index "
operator|+
name|start
operator|+
literal|" out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|start
operator|+
name|howmany
operator|>=
name|valueSet
operator|.
name|getLength
argument_list|()
condition|)
name|howmany
operator|=
name|valueSet
operator|.
name|getLength
argument_list|()
operator|-
name|start
expr_stmt|;
name|xml
operator|=
operator|new
name|String
index|[
name|howmany
index|]
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
name|howmany
condition|;
name|i
operator|++
control|)
block|{
name|Value
name|val
init|=
name|valueSet
operator|.
name|get
argument_list|(
name|start
argument_list|)
decl_stmt|;
name|xml
index|[
name|i
index|]
operator|=
name|val
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|xml
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RemoteException
argument_list|(
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  resultId             Description of the Parameter 	 *@param  pos                  Description of the Parameter 	 *@param  docPath              Description of the Parameter 	 *@param  encoding             Description of the Parameter 	 *@param  prettyPrint          Description of the Parameter 	 *@return                      Description of the Return Value 	 *@exception  RemoteException  Description of the Exception 	 */
specifier|public
name|String
index|[]
name|retrieveByDocument
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|,
name|String
name|docPath
parameter_list|,
name|boolean
name|indent
parameter_list|,
name|boolean
name|xinclude
parameter_list|,
name|String
name|highlight
parameter_list|)
throws|throws
name|RemoteException
block|{
name|Session
name|session
init|=
name|SessionManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getSession
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|Value
name|qr
init|=
operator|(
name|Value
operator|)
name|session
operator|.
name|getQueryResult
argument_list|()
operator|.
name|result
decl_stmt|;
if|if
condition|(
name|qr
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"result set unknown or timed out"
argument_list|)
throw|;
name|String
name|xml
index|[]
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|qr
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Value
operator|.
name|isNodeList
case|:
name|NodeList
name|resultSet
init|=
name|qr
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
name|ArraySet
name|hitsByDoc
init|=
operator|new
name|ArraySet
argument_list|(
literal|50
argument_list|)
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
operator|(
operator|(
name|NodeSet
operator|)
name|resultSet
operator|)
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|doc
operator|.
name|getFileName
argument_list|()
operator|.
name|equals
argument_list|(
name|docPath
argument_list|)
condition|)
name|hitsByDoc
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
operator|--
name|start
expr_stmt|;
if|if
condition|(
name|start
operator|<
literal|0
operator|||
name|start
operator|>
name|hitsByDoc
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"index "
operator|+
name|start
operator|+
literal|"out of bounds ("
operator|+
name|hitsByDoc
operator|.
name|getLength
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
if|if
condition|(
name|start
operator|+
name|howmany
operator|>=
name|hitsByDoc
operator|.
name|getLength
argument_list|()
condition|)
name|howmany
operator|=
name|hitsByDoc
operator|.
name|getLength
argument_list|()
operator|-
name|start
expr_stmt|;
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|Map
name|properties
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|PRETTY_PRINT
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|indent
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|EXPAND_XINCLUDES
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|xinclude
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Serializer
operator|.
name|HIGHLIGHT_MATCHES
argument_list|,
name|highlight
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|xml
operator|=
operator|new
name|String
index|[
name|howmany
index|]
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
name|howmany
condition|;
name|i
operator|++
control|)
block|{
name|NodeProxy
name|proxy
init|=
operator|(
operator|(
name|NodeSet
operator|)
name|hitsByDoc
operator|)
operator|.
name|get
argument_list|(
name|start
argument_list|)
decl_stmt|;
if|if
condition|(
name|proxy
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not found: "
operator|+
name|start
argument_list|)
throw|;
name|xml
index|[
name|i
index|]
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
break|break;
default|default :
throw|throw
operator|new
name|RemoteException
argument_list|(
literal|"result set is not a node list"
argument_list|)
throw|;
block|}
return|return
name|xml
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RemoteException
argument_list|(
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|TreeMap
name|scanResults
parameter_list|(
name|NodeList
name|results
parameter_list|)
block|{
name|TreeMap
name|collections
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|TreeMap
name|documents
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|Integer
name|hits
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
operator|(
operator|(
name|NodeSet
operator|)
name|results
operator|)
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|documents
operator|=
operator|(
name|TreeMap
operator|)
name|collections
operator|.
name|get
argument_list|(
name|p
operator|.
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|documents
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
name|collections
operator|.
name|put
argument_list|(
name|p
operator|.
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|documents
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"added "
operator|+
name|p
operator|.
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|hits
operator|=
operator|(
name|Integer
operator|)
name|documents
operator|.
name|get
argument_list|(
name|p
operator|.
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|)
operator|)
operator|==
literal|null
condition|)
name|documents
operator|.
name|put
argument_list|(
name|p
operator|.
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|documents
operator|.
name|put
argument_list|(
name|p
operator|.
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|,
operator|new
name|Integer
argument_list|(
name|hits
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|collections
return|;
block|}
block|}
end_class

end_unit

