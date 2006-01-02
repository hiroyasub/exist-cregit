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
name|Comparator
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
name|NodeIndexListener
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
name|StorageAddress
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|Modification
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|selectStmt
init|=
literal|null
decl_stmt|;
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
specifier|protected
name|DocumentSet
name|docs
decl_stmt|;
specifier|protected
name|Map
name|namespaces
decl_stmt|;
specifier|protected
name|Map
name|variables
decl_stmt|;
specifier|protected
name|DocumentSet
name|lockedDocuments
init|=
literal|null
decl_stmt|;
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
name|namespaces
parameter_list|,
name|Map
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
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Process the modification. This is the main method that has to be implemented  	 * by all subclasses. 	 *  	 * @return 	 * @throws PermissionDeniedException 	 * @throws LockException 	 * @throws EXistException 	 * @throws XPathException 	 */
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
comment|/** 	 * Evaluate the select expression. 	 *  	 * @param docs 	 * @return 	 * @throws PermissionDeniedException 	 * @throws EXistException 	 * @throws XPathException 	 */
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
name|setExclusiveMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|context
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
name|getLength
argument_list|()
operator|==
literal|0
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|resultSeq
operator|.
name|getLength
argument_list|()
operator|+
literal|" for select: "
operator|+
name|selectStmt
argument_list|)
expr_stmt|;
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
name|Iterator
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
name|context
operator|.
name|declareVariable
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
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
name|entry
decl_stmt|;
for|for
control|(
name|Iterator
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
block|}
comment|/** 	 * Acquire a lock on all documents processed by this modification. 	 * We have to avoid that node positions change during the 	 * operation. 	 *  	 * @param nl 	 * @return 	 * @throws LockException 	 */
specifier|protected
name|StoredNode
index|[]
name|selectAndLock
parameter_list|()
throws|throws
name|LockException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
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
literal|true
argument_list|)
expr_stmt|;
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
name|DocumentImpl
name|doc
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
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|ql
index|[
name|i
index|]
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setBroker
argument_list|(
name|broker
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
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * Release all acquired document locks. 	 */
specifier|protected
name|void
name|unlockDocuments
parameter_list|()
block|{
if|if
condition|(
name|lockedDocuments
operator|==
literal|null
condition|)
return|return;
name|lockedDocuments
operator|.
name|unlock
argument_list|(
literal|true
argument_list|)
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
for|for
control|(
name|Iterator
name|i
init|=
name|docs
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
name|DocumentImpl
name|next
init|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|getSplitCount
argument_list|()
operator|>
name|broker
operator|.
name|getFragmentationLimit
argument_list|()
condition|)
name|broker
operator|.
name|defrag
argument_list|(
name|transaction
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|broker
operator|.
name|consistencyCheck
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
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
specifier|final
specifier|static
class|class
name|IndexListener
implements|implements
name|NodeIndexListener
block|{
name|StoredNode
index|[]
name|nodes
decl_stmt|;
specifier|public
name|IndexListener
parameter_list|(
name|StoredNode
index|[]
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.dom.NodeIndexListener#nodeChanged(org.exist.dom.NodeImpl) 		 */
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
specifier|final
name|long
name|address
init|=
name|node
operator|.
name|getInternalAddress
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
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|StorageAddress
operator|.
name|equals
argument_list|(
name|nodes
index|[
name|i
index|]
operator|.
name|getInternalAddress
argument_list|()
argument_list|,
name|address
argument_list|)
condition|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
block|}
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.dom.NodeIndexListener#nodeChanged(long, long) 		 */
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|long
name|oldAddress
parameter_list|,
name|long
name|newAddress
parameter_list|)
block|{
comment|// Ignore the address change
comment|// TODO: is this really save?
comment|//			for (int i = 0; i< nodes.length; i++) {
comment|//				if (StorageAddress.equals(nodes[i].getInternalAddress(), oldAddress)) {
comment|//					nodes[i].setInternalAddress(newAddress);
comment|//				}
comment|//			}
block|}
block|}
specifier|final
specifier|static
class|class
name|NodeComparator
implements|implements
name|Comparator
block|{
comment|/* (non-Javadoc) 		* @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) 		*/
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|StoredNode
name|n1
init|=
operator|(
name|StoredNode
operator|)
name|o1
decl_stmt|;
name|StoredNode
name|n2
init|=
operator|(
name|StoredNode
operator|)
name|o2
decl_stmt|;
if|if
condition|(
name|n1
operator|.
name|getInternalAddress
argument_list|()
operator|==
name|n2
operator|.
name|getInternalAddress
argument_list|()
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|n1
operator|.
name|getInternalAddress
argument_list|()
operator|<
name|n2
operator|.
name|getInternalAddress
argument_list|()
condition|)
return|return
operator|-
literal|1
return|;
else|else
return|return
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

