begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|StringReader
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
name|XQueryPool
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
name|CompiledXQuery
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
name|Pragma
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
name|exist
operator|.
name|xquery
operator|.
name|XQuery
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
name|XQueryContext
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
name|base
operator|.
name|CompiledExpression
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
implements|,
name|XQueryService
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
specifier|protected
name|boolean
name|xpathCompatible
init|=
literal|true
decl_stmt|;
specifier|protected
name|String
name|moduleLoadPath
init|=
literal|null
decl_stmt|;
specifier|protected
name|Properties
name|properties
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|lockDocuments
init|=
literal|false
decl_stmt|;
specifier|protected
name|DocumentSet
name|lockedDocuments
init|=
literal|null
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
name|this
operator|.
name|properties
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
return|return
operator|(
name|String
operator|)
name|namespaceDecls
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
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
name|String
index|[]
name|docs
init|=
operator|new
name|String
index|[]
block|{
name|collection
operator|.
name|getName
argument_list|()
block|}
decl_stmt|;
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
name|String
index|[]
name|docs
init|=
operator|new
name|String
index|[]
block|{
name|res
operator|.
name|getParentCollection
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getDocumentId
argument_list|()
block|}
decl_stmt|;
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
else|else
block|{
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
name|String
index|[]
name|docs
init|=
operator|new
name|String
index|[]
block|{
name|node
operator|.
name|getDocument
argument_list|()
operator|.
name|getName
argument_list|()
block|}
decl_stmt|;
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
block|}
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
name|execute
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|expression
argument_list|,
literal|null
argument_list|)
return|;
block|}
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
name|String
index|[]
name|docs
init|=
operator|new
name|String
index|[]
block|{
name|res
operator|.
name|getParentCollection
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getDocumentId
argument_list|()
block|}
decl_stmt|;
return|return
name|execute
argument_list|(
name|docs
argument_list|,
literal|null
argument_list|,
name|expression
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
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
name|String
index|[]
name|docs
init|=
operator|new
name|String
index|[]
block|{
name|node
operator|.
name|getDocument
argument_list|()
operator|.
name|getName
argument_list|()
block|}
decl_stmt|;
return|return
name|execute
argument_list|(
name|docs
argument_list|,
name|set
argument_list|,
name|expression
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
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
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Sequence
name|result
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
name|DocumentSet
name|docs
init|=
name|collection
operator|.
name|getCollection
argument_list|()
operator|.
name|allDocs
argument_list|(
name|broker
argument_list|,
operator|new
name|DocumentSet
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryPool
name|pool
init|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|()
expr_stmt|;
else|else
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|setBackwardsCompatibility
argument_list|(
name|xpathCompatible
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|setupContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|checkPragmas
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
name|result
operator|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|source
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"query took "
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
literal|" ms."
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
operator|new
name|LocalResourceSet
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|collection
argument_list|,
name|properties
argument_list|,
name|result
argument_list|,
literal|null
argument_list|)
return|;
else|else
return|return
literal|null
return|;
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
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
init|=
name|xquery
operator|.
name|newContext
argument_list|()
decl_stmt|;
name|setupContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|CompiledXQuery
name|expr
init|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
operator|new
name|StringReader
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|checkPragmas
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"compilation took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
return|return
name|expr
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
name|String
index|[]
name|docs
init|=
operator|new
name|String
index|[]
block|{
name|res
operator|.
name|getParentCollection
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getDocumentId
argument_list|()
block|}
decl_stmt|;
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
specifier|protected
name|void
name|setupContext
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|XPathException
block|{
name|context
operator|.
name|setBaseURI
argument_list|(
name|properties
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
if|if
condition|(
name|moduleLoadPath
operator|!=
literal|null
condition|)
name|context
operator|.
name|setModuleLoadPath
argument_list|(
name|moduleLoadPath
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
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|setBackwardsCompatibility
argument_list|(
name|xpathCompatible
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Check if the XQuery contains pragmas that define serialization settings. 	 * If yes, copy the corresponding settings to the current set of output properties. 	 *  	 * @param context 	 */
specifier|private
name|void
name|checkPragmas
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
name|Pragma
name|pragma
init|=
name|context
operator|.
name|getPragma
argument_list|(
name|Pragma
operator|.
name|SERIALIZE_QNAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|pragma
operator|==
literal|null
condition|)
return|return;
name|String
index|[]
name|contents
init|=
name|pragma
operator|.
name|tokenizeContents
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|contents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|pair
init|=
name|Pragma
operator|.
name|parseKeyValuePair
argument_list|(
name|contents
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|pair
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown parameter found in "
operator|+
name|pragma
operator|.
name|getQName
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|": '"
operator|+
name|contents
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|)
throw|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting serialization property from pragma: "
operator|+
name|pair
index|[
literal|0
index|]
operator|+
literal|" = "
operator|+
name|pair
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|pair
index|[
literal|0
index|]
argument_list|,
name|pair
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|ResourceSet
name|doQuery
parameter_list|(
name|String
name|query
parameter_list|,
name|String
index|[]
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
name|CompiledExpression
name|expr
init|=
name|compile
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
name|execute
argument_list|(
name|docs
argument_list|,
name|contextSet
argument_list|,
name|expr
argument_list|,
name|sortExpr
argument_list|)
return|;
block|}
comment|/** 	 * Execute all following queries in a protected environment. 	 * Protected means: it is guaranteed that documents referenced by the 	 * query or the result set are not modified by other threads 	 * until {@link #endProtected} is called. 	 */
specifier|public
name|void
name|beginProtected
parameter_list|()
block|{
name|lockDocuments
operator|=
literal|true
expr_stmt|;
name|lockedDocuments
operator|=
operator|new
name|DocumentSet
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Close the protected environment. All locks held 	 * by the current thread are released. The result set 	 * is no longer guaranteed to be stable. 	 */
specifier|public
name|void
name|endProtected
parameter_list|()
block|{
name|lockDocuments
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|lockedDocuments
operator|!=
literal|null
condition|)
block|{
name|lockedDocuments
operator|.
name|unlock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|lockedDocuments
operator|=
literal|null
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
specifier|private
name|ResourceSet
name|execute
parameter_list|(
name|String
index|[]
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|CompiledExpression
name|expression
parameter_list|,
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|CompiledXQuery
name|expr
init|=
operator|(
name|CompiledXQuery
operator|)
name|expression
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Sequence
name|result
decl_stmt|;
name|XQueryContext
name|context
init|=
name|expr
operator|.
name|getContext
argument_list|()
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
name|context
operator|.
name|setBackwardsCompatibility
argument_list|(
name|xpathCompatible
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|setupContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|checkPragmas
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
if|if
condition|(
name|lockDocuments
condition|)
name|context
operator|.
name|setLockDocumentsOnLoad
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|result
operator|=
name|xquery
operator|.
name|execute
argument_list|(
name|expr
argument_list|,
name|contextSet
argument_list|)
expr_stmt|;
if|if
condition|(
name|lockDocuments
condition|)
block|{
name|DocumentSet
name|locked
init|=
name|context
operator|.
name|releaseUnusedDocuments
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|locked
operator|!=
literal|null
condition|)
block|{
name|lockedDocuments
operator|.
name|addAll
argument_list|(
name|locked
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|context
operator|.
name|releaseLockedDocuments
argument_list|()
expr_stmt|;
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
name|context
operator|.
name|releaseLockedDocuments
argument_list|()
expr_stmt|;
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
name|Exception
name|e
parameter_list|)
block|{
comment|// need to catch all runtime exceptions here to be able to release locked documents
name|context
operator|.
name|releaseLockedDocuments
argument_list|()
expr_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"query took "
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
literal|" ms."
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
operator|new
name|LocalResourceSet
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|collection
argument_list|,
name|properties
argument_list|,
name|result
argument_list|,
name|sortExpr
argument_list|)
return|;
else|else
return|return
literal|null
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
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.XQueryService#setXPathCompatibility(boolean) 	 */
specifier|public
name|void
name|setXPathCompatibility
parameter_list|(
name|boolean
name|backwardsCompatible
parameter_list|)
block|{
name|this
operator|.
name|xpathCompatible
operator|=
name|backwardsCompatible
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.XQueryService#setModuleLoadPath(java.lang.String) 	 */
specifier|public
name|void
name|setModuleLoadPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|moduleLoadPath
operator|=
name|path
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.XQueryService#dump(org.exist.xmldb.CompiledExpression, java.io.Writer) 	 */
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
name|CompiledXQuery
name|expr
init|=
operator|(
name|CompiledXQuery
operator|)
name|expression
decl_stmt|;
name|expr
operator|.
name|dump
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

