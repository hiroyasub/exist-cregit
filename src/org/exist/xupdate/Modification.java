begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xupdate
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
name|HashMap
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
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|collections
operator|.
name|triggers
operator|.
name|DocumentTrigger
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
name|triggers
operator|.
name|DocumentTriggers
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
name|triggers
operator|.
name|TriggerException
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
name|persistent
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
name|persistent
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
name|persistent
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
name|persistent
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
name|persistent
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
name|persistent
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
name|xacml
operator|.
name|AccessContext
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
name|xacml
operator|.
name|NullAccessContextException
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
name|source
operator|.
name|StringSource
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
name|storage
operator|.
name|txn
operator|.
name|Txn
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
name|util
operator|.
name|hashtable
operator|.
name|Int2ObjectHashMap
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/**  * Base class for all XUpdate modifications.  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Modification
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Modification
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** select Statement in the current XUpdate definition; 	 * defines the set of nodes to which this XUpdate might apply. */
specifier|protected
name|String
name|selectStmt
init|=
literal|null
decl_stmt|;
comment|/**      * NodeList to keep track of created document fragments within      * the currently processed XUpdate modification.      * see {@link XUpdateProcessor#contents}      */
specifier|protected
name|NodeList
name|content
init|=
literal|null
decl_stmt|;
specifier|protected
name|DBBroker
name|broker
decl_stmt|;
comment|/** Documents concerned by this XUpdate modification, 	 * i.e. the set of documents to which this XUpdate might apply. */
specifier|protected
name|DocumentSet
name|docs
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|variables
decl_stmt|;
specifier|protected
name|DocumentSet
name|lockedDocuments
init|=
literal|null
decl_stmt|;
specifier|protected
name|MutableDocumentSet
name|modifiedDocuments
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
specifier|protected
name|Int2ObjectHashMap
argument_list|<
name|DocumentTrigger
argument_list|>
name|triggers
decl_stmt|;
specifier|private
name|AccessContext
name|accessCtx
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|Modification
parameter_list|()
block|{
block|}
comment|/** 	 * Constructor for Modification. 	 */
specifier|public
name|Modification
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|String
name|selectStmt
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|variables
parameter_list|)
block|{
name|this
operator|.
name|selectStmt
operator|=
name|selectStmt
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|namespaces
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|namespaces
argument_list|)
expr_stmt|;
name|this
operator|.
name|variables
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|variables
argument_list|)
expr_stmt|;
name|this
operator|.
name|triggers
operator|=
operator|new
name|Int2ObjectHashMap
argument_list|<
name|DocumentTrigger
argument_list|>
argument_list|(
literal|97
argument_list|)
expr_stmt|;
comment|// DESIGN_QUESTION : wouldn't that be nice to apply selectStmt right here ?
block|}
specifier|public
specifier|final
name|void
name|setAccessContext
parameter_list|(
name|AccessContext
name|accessCtx
parameter_list|)
block|{
if|if
condition|(
name|accessCtx
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullAccessContextException
argument_list|()
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|accessCtx
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Access context can only be set once."
argument_list|)
throw|;
block|}
name|this
operator|.
name|accessCtx
operator|=
name|accessCtx
expr_stmt|;
block|}
specifier|public
specifier|final
name|AccessContext
name|getAccessContext
parameter_list|()
block|{
if|if
condition|(
name|accessCtx
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Access context has not been set."
argument_list|)
throw|;
block|}
return|return
name|accessCtx
return|;
block|}
comment|/**      * Process the modification. This is the main method that has to be implemented       * by all subclasses.      *       * @param transaction       * @throws PermissionDeniedException       * @throws LockException       * @throws EXistException       * @throws XPathException       */
specifier|public
specifier|abstract
name|long
name|process
parameter_list|(
name|Txn
name|transaction
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|EXistException
throws|,
name|XPathException
throws|,
name|TriggerException
function_decl|;
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
specifier|public
name|void
name|setContent
parameter_list|(
name|NodeList
name|nodes
parameter_list|)
block|{
name|content
operator|=
name|nodes
expr_stmt|;
block|}
comment|/** 	 * Evaluate the select expression. 	 *  	 * @param docs 	 * @return The selected nodes. 	 * @throws PermissionDeniedException 	 * @throws EXistException 	 * @throws XPathException 	 */
specifier|protected
name|NodeList
name|select
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|XQueryPool
name|pool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
specifier|final
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|selectStmt
argument_list|)
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
block|{
name|context
operator|=
operator|new
name|XQueryContext
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|getAccessContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|declareNamespaces
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|declareVariables
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
try|try
block|{
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"An exception occurred while compiling the query: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|Sequence
name|resultSeq
init|=
literal|null
decl_stmt|;
try|try
block|{
name|resultSeq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
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
if|if
condition|(
operator|!
operator|(
name|resultSeq
operator|.
name|isEmpty
argument_list|()
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|resultSeq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"select expression should evaluate to a node-set; got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|resultSeq
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|resultSeq
operator|.
name|getItemCount
argument_list|()
operator|+
literal|" for select: "
operator|+
name|selectStmt
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|NodeList
operator|)
name|resultSeq
operator|.
name|toNodeSet
argument_list|()
return|;
block|}
comment|/** 	 * @param context 	 * @throws XPathException 	 */
specifier|protected
name|void
name|declareVariables
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|i
init|=
name|variables
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
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
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
block|}
comment|/** 	 * @param context 	 */
specifier|protected
name|void
name|declareNamespaces
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|i
init|=
name|namespaces
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
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
block|}
comment|/** 	 * Acquire a lock on all documents processed by this modification. We have 	 * to avoid that node positions change during the operation. 	 * feature trigger_update : 	 * At the same time we leverage on the fact that it's called before  	 * database modification to call the eventual triggers. 	 *  	 * @return The selected document nodes. 	 *  	 * @throws LockException 	 * @throws PermissionDeniedException 	 * @throws EXistException 	 * @throws XPathException  	 * @throws TriggerException  	 */
specifier|protected
specifier|final
name|StoredNode
index|[]
name|selectAndLock
parameter_list|(
name|Txn
name|transaction
parameter_list|)
throws|throws
name|LockException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|XPathException
throws|,
name|TriggerException
block|{
specifier|final
name|Lock
name|globalLock
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getGlobalUpdateLock
argument_list|()
decl_stmt|;
try|try
block|{
name|globalLock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|nl
init|=
name|select
argument_list|(
name|docs
argument_list|)
decl_stmt|;
name|lockedDocuments
operator|=
operator|(
operator|(
name|NodeSet
operator|)
name|nl
operator|)
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
comment|// acquire a lock on all documents
comment|// we have to avoid that node positions change
comment|// during the modification
name|lockedDocuments
operator|.
name|lock
argument_list|(
name|broker
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|StoredNode
name|ql
index|[]
init|=
operator|new
name|StoredNode
index|[
name|nl
operator|.
name|getLength
argument_list|()
index|]
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
name|ql
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ql
index|[
name|i
index|]
operator|=
operator|(
name|StoredNode
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|ql
index|[
name|i
index|]
operator|.
name|getOwnerDocument
argument_list|()
decl_stmt|;
comment|// call the eventual triggers
comment|// TODO -jmv separate loop on docs and not on nodes
comment|//prepare Trigger
name|prepareTrigger
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|ql
return|;
block|}
finally|finally
block|{
name|globalLock
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
comment|/** 	 * Release all acquired document locks; 	 * feature trigger_update : 	 * at the same time we leverage on the fact that it's called after  	 * database modification to call the eventual triggers 	 * @throws TriggerException  	 */
specifier|protected
specifier|final
name|void
name|unlockDocuments
parameter_list|(
name|Txn
name|transaction
parameter_list|)
throws|throws
name|TriggerException
block|{
if|if
condition|(
name|lockedDocuments
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|//finish Trigger
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|iterator
init|=
name|modifiedDocuments
operator|.
name|getDocumentIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|finishTrigger
argument_list|(
name|transaction
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|triggers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|modifiedDocuments
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//unlock documents
name|lockedDocuments
operator|.
name|unlock
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|lockedDocuments
operator|=
literal|null
expr_stmt|;
block|}
comment|/** 	 * Check if any of the modified documents needs defragmentation. 	 *  	 * Defragmentation will take place if the number of split pages in the 	 * document exceeds the limit defined in the configuration file. 	 *   	 * @param docs 	 */
specifier|protected
name|void
name|checkFragmentation
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentSet
name|docs
parameter_list|)
throws|throws
name|EXistException
block|{
name|int
name|fragmentationLimit
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|Object
name|property
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|DBBroker
operator|.
name|PROPERTY_XUPDATE_FRAGMENTATION_FACTOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|fragmentationLimit
operator|=
operator|(
operator|(
name|Integer
operator|)
name|property
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
for|for
control|(
specifier|final
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
specifier|final
name|DocumentImpl
name|next
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|getMetadata
argument_list|()
operator|.
name|getSplitCount
argument_list|()
operator|>
name|fragmentationLimit
condition|)
block|{
name|broker
operator|.
name|defragXMLResource
argument_list|(
name|transaction
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|checkXMLResourceConsistency
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Fires the prepare function for the UPDATE_DOCUMENT_EVENT trigger for the Document doc 	 *   	 * @param transaction	The transaction 	 * @param doc	The document to trigger for 	 *  	 * @throws TriggerException  	 */
specifier|private
name|void
name|prepareTrigger
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|TriggerException
block|{
specifier|final
name|Collection
name|col
init|=
name|doc
operator|.
name|getCollection
argument_list|()
decl_stmt|;
specifier|final
name|DocumentTrigger
name|trigger
init|=
operator|new
name|DocumentTriggers
argument_list|(
name|broker
argument_list|,
name|col
argument_list|)
decl_stmt|;
name|trigger
operator|.
name|beforeUpdateDocument
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|triggers
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|,
name|trigger
argument_list|)
expr_stmt|;
block|}
comment|/**  	 * Fires the finish function for UPDATE_DOCUMENT_EVENT for the documents trigger 	 *  	 * @param transaction	The transaction 	 * @param doc	The document to trigger for 	 *  	 * @throws TriggerException  	 */
specifier|private
name|void
name|finishTrigger
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|TriggerException
block|{
specifier|final
name|DocumentTrigger
name|trigger
init|=
name|triggers
operator|.
name|get
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|trigger
operator|!=
literal|null
condition|)
block|{
name|trigger
operator|.
name|afterUpdateDocument
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<xu:"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" select=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|selectStmt
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
comment|//		buf.append(XMLUtil.dump(content));
name|buf
operator|.
name|append
argument_list|(
literal|"</xu:"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

