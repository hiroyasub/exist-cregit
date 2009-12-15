begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
package|;
end_package

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
name|collections
operator|.
name|Collection
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
name|DefaultDocumentSet
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
name|MutableDocumentSet
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
name|NewArrayNodeSet
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
name|dom
operator|.
name|QName
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
name|StoredNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|UpdateListener
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
name|lock
operator|.
name|Lock
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
name|LockException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|Cardinality
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
name|Dependency
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
name|Function
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
name|FunctionSignature
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
name|Profiler
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
name|functions
operator|.
name|xmldb
operator|.
name|XMLDBModule
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
name|AnyURIValue
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
name|FunctionReturnSequenceType
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
name|FunctionParameterSequenceType
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
name|Item
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceIterator
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
name|SequenceType
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
name|Type
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
name|Iterator
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ExtCollection
extends|extends
name|Function
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ExtCollection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"collection"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the documents contained in the collections "
operator|+
literal|"specified in the input sequence. "
operator|+
name|XMLDBModule
operator|.
name|COLLECTION_URI
operator|+
literal|" Documents contained in subcollections are also included."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
comment|//Different from the offical specs
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collection-uris"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The collection-uris for which to include the documents"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the document nodes contained in or under the given collections"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|includeSubCollections
init|=
literal|false
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|List
argument_list|<
name|?
argument_list|>
name|cachedArgs
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|Sequence
name|cached
init|=
literal|null
decl_stmt|;
specifier|private
name|UpdateListener
name|listener
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|ExtCollection
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|signature
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExtCollection
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|includeSubCollections
operator|=
name|inclusive
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
name|getParameterValues
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
comment|// TODO: disabled cache for now as it may cause concurrency issues
comment|// better use compile-time inspection and maybe a pragma to mark those
comment|// sections in the query that can be safely cached
comment|//		boolean cacheIsValid = false;
comment|//		if(cachedArgs != null)
comment|//		    cacheIsValid = compareArguments(cachedArgs, args);
comment|//		if(cacheIsValid) {
comment|//		    // if the expression occurs in a nested context, we might have cached the
comment|//            // document set
comment|//            if (context.getProfiler().isEnabled())
comment|//                context.getProfiler().end(this, "fn:collection: loading documents", cached);
comment|//		    return cached;
comment|//        }
comment|// build the document set
name|DocumentSet
name|docs
init|=
literal|null
decl_stmt|;
comment|//        DocumentSet docs = new DocumentSet(521);
try|try
block|{
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//TODO : add default collection to the context
comment|//If the value of the default collection is undefined an error is raised [err:FODC0002].
comment|//throw new XPathException("FODC0002: unknown collection '" + uri + "'");
name|docs
operator|=
name|context
operator|.
name|getStaticallyKnownDocuments
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|MutableDocumentSet
name|ndocs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|next
range|:
name|args
control|)
block|{
name|XmldbURI
name|uri
init|=
operator|new
name|AnyURIValue
argument_list|(
name|next
argument_list|)
operator|.
name|toXmldbURI
argument_list|()
decl_stmt|;
name|Collection
name|coll
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|coll
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|isRaiseErrorOnFailedRetrieval
argument_list|()
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"FODC0002: can not access collection '"
operator|+
name|uri
operator|+
literal|"'"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FODC0002: can not access collection '"
operator|+
name|uri
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|context
operator|.
name|inProtectedMode
argument_list|()
condition|)
name|context
operator|.
name|getProtectedDocs
argument_list|()
operator|.
name|getDocsByCollection
argument_list|(
name|coll
argument_list|,
name|includeSubCollections
argument_list|,
name|ndocs
argument_list|)
expr_stmt|;
else|else
name|coll
operator|.
name|allDocs
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|ndocs
argument_list|,
name|includeSubCollections
argument_list|,
literal|true
argument_list|,
name|context
operator|.
name|getProtectedDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|docs
operator|=
name|ndocs
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|//From AnyURIValue constructor
name|e
operator|.
name|setLocation
argument_list|(
name|line
argument_list|,
name|column
argument_list|)
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"FODC0002: can not access collection '"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FODC0002: "
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
comment|// iterate through all docs and create the node set
name|NodeSet
name|result
init|=
operator|new
name|NewArrayNodeSet
argument_list|(
name|docs
operator|.
name|getDocumentCount
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Lock
name|dlock
decl_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|docs
operator|.
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|doc
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|dlock
operator|=
name|doc
operator|.
name|getUpdateLock
argument_list|()
expr_stmt|;
name|boolean
name|lockAcquired
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|context
operator|.
name|inProtectedMode
argument_list|()
operator|&&
operator|!
name|dlock
operator|.
name|hasLock
argument_list|()
condition|)
block|{
name|dlock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|lockAcquired
operator|=
literal|true
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|// , -1, Node.DOCUMENT_NODE));
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Could not acquire lock on document "
operator|+
name|doc
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|lockAcquired
condition|)
name|dlock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
name|cached
operator|=
name|result
expr_stmt|;
name|cachedArgs
operator|=
name|args
expr_stmt|;
name|registerUpdateListener
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * @param contextSequence      * @param contextItem      * @throws XPathException      */
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getParameterValues
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|getArgumentCount
argument_list|()
operator|+
literal|10
argument_list|)
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
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Sequence
name|seq
init|=
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|j
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|next
init|=
name|j
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|args
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|boolean
name|compareArguments
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args1
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args2
parameter_list|)
block|{
if|if
condition|(
name|args1
operator|.
name|size
argument_list|()
operator|!=
name|args2
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args1
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|arg1
init|=
name|args1
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|arg2
init|=
name|args2
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|arg1
operator|.
name|equals
argument_list|(
name|arg2
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|protected
name|void
name|registerUpdateListener
parameter_list|()
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
name|listener
operator|=
operator|new
name|UpdateListener
argument_list|()
block|{
specifier|public
name|void
name|documentUpdated
parameter_list|(
name|DocumentImpl
name|document
parameter_list|,
name|int
name|event
parameter_list|)
block|{
comment|// clear all
name|cached
operator|=
literal|null
expr_stmt|;
name|cachedArgs
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|unsubscribe
parameter_list|()
block|{
name|ExtCollection
operator|.
name|this
operator|.
name|listener
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|nodeMoved
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|StoredNode
name|newNode
parameter_list|)
block|{
comment|// not relevant
block|}
specifier|public
name|void
name|debug
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"UpdateListener: Line: "
operator|+
name|getLine
argument_list|()
operator|+
literal|": "
operator|+
name|ExtCollection
operator|.
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|context
operator|.
name|registerUpdateListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#resetState()      */
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|cached
operator|=
literal|null
expr_stmt|;
name|cachedArgs
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

