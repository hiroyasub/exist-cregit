begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

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
name|Iterator
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
name|AtomicValue
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
name|Item
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
name|exist
operator|.
name|xpath
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
name|xpath
operator|.
name|value
operator|.
name|StringValue
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
name|Node
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
name|ContentHandler
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

begin_comment
comment|/**  * Placeholder class for DOM nodes.   *   * NodeProxy is an internal proxy class, acting as a placeholder for all types of XML nodes  * during query processing. NodeProxy just stores the node's unique id and the document it belongs to.   * Query processing deals with these proxys most of the time. Using a NodeProxy is much cheaper   * than loading the actual node from the database. The real DOM node is only loaded,  * if further information is required for the evaluation of an XPath expression. To obtain   * the real node for a proxy, simply call {@link #getNode()}.   *   * All sets of type NodeSet operate on NodeProxys. A node set is a special type of   * sequence, so NodeProxy does also implement {@link org.exist.xpath.value.Item} and  * can thus be an item in a sequence. Since, according to XPath 2, a single node is also   * a sequence, NodeProxy does itself extend NodeSet. It thus represents a node set containing  * just one, single node.  *  *@author     Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NodeProxy
extends|extends
name|AbstractNodeSet
implements|implements
name|Item
implements|,
name|Comparable
block|{
comment|/** 	 * The owner document of this node. 	 */
specifier|public
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
comment|/** 	 * The unique internal node id. 	 */
specifier|public
name|long
name|gid
init|=
literal|0
decl_stmt|;
comment|/** 	 * The type of this node (as defined by DOM), if known, -1 if 	 * unknown. 	 */
specifier|public
name|short
name|nodeType
init|=
operator|-
literal|1
decl_stmt|;
comment|/** 	 * The first {@link Match} object associated with this node. 	 * Match objects are used to track fulltext hits throughout query processing. 	 *  	 * Matches are stored as a linked list. 	 */
specifier|public
name|Match
name|match
init|=
literal|null
decl_stmt|;
specifier|private
name|ContextItem
name|context
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|internalAddress
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|NodeProxy
parameter_list|()
block|{
block|}
comment|/** 	 *  Construct a node proxy with unique id gid and owned by document doc. 	 * 	 *@param  doc  Description of the Parameter 	 *@param  gid  Description of the Parameter 	 */
specifier|public
name|NodeProxy
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
block|}
comment|/** 	 *  as above, but a hint is given about the node type of this proxy-object. 	 * 	 *@param  doc       Description of the Parameter 	 *@param  gid       Description of the Parameter 	 *@param  nodeType  Description of the Parameter 	 */
specifier|public
name|NodeProxy
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|short
name|nodeType
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
specifier|public
name|NodeProxy
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|short
name|nodeType
parameter_list|,
name|long
name|address
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|address
expr_stmt|;
block|}
specifier|public
name|NodeProxy
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|long
name|address
parameter_list|)
block|{
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|address
expr_stmt|;
block|}
specifier|public
name|NodeProxy
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
name|doc
operator|=
name|p
operator|.
name|doc
expr_stmt|;
name|gid
operator|=
name|p
operator|.
name|gid
expr_stmt|;
name|nodeType
operator|=
name|p
operator|.
name|nodeType
expr_stmt|;
name|match
operator|=
name|p
operator|.
name|match
expr_stmt|;
name|internalAddress
operator|=
name|p
operator|.
name|internalAddress
expr_stmt|;
block|}
specifier|public
name|NodeProxy
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
name|this
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|node
operator|.
name|getGID
argument_list|()
argument_list|)
expr_stmt|;
name|internalAddress
operator|=
name|node
operator|.
name|getInternalAddress
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|NodeProxy
name|other
parameter_list|)
block|{
specifier|final
name|int
name|diff
init|=
name|doc
operator|.
name|docId
operator|-
name|other
operator|.
name|doc
operator|.
name|docId
decl_stmt|;
return|return
name|diff
operator|==
literal|0
condition|?
operator|(
name|gid
operator|<
name|other
operator|.
name|gid
condition|?
operator|-
literal|1
else|:
operator|(
name|gid
operator|>
name|other
operator|.
name|gid
condition|?
literal|1
else|:
literal|0
operator|)
operator|)
else|:
name|diff
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
specifier|final
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|docId
operator|==
name|p
operator|.
name|doc
operator|.
name|docId
condition|)
block|{
if|if
condition|(
name|gid
operator|==
name|p
operator|.
name|gid
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|gid
operator|<
name|p
operator|.
name|gid
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
if|else if
condition|(
name|doc
operator|.
name|docId
operator|<
name|p
operator|.
name|doc
operator|.
name|docId
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
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|NodeProxy
operator|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot compare nodes from different implementations"
argument_list|)
throw|;
name|NodeProxy
name|node
init|=
operator|(
name|NodeProxy
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
operator|==
name|doc
operator|.
name|getDocId
argument_list|()
operator|&&
name|node
operator|.
name|gid
operator|==
name|gid
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
specifier|public
name|DocumentImpl
name|getDoc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
specifier|public
name|long
name|getGID
parameter_list|()
block|{
return|return
name|gid
return|;
block|}
specifier|public
name|Node
name|getNode
parameter_list|()
block|{
return|return
name|doc
operator|.
name|getNode
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|short
name|getNodeType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
specifier|public
name|String
name|getNodeValue
parameter_list|()
block|{
return|return
name|doc
operator|.
name|getBroker
argument_list|()
operator|.
name|getNodeValue
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|void
name|setGID
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|doc
operator|.
name|getNode
argument_list|(
name|gid
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
class|class
name|NodeProxyComparator
implements|implements
name|Comparator
block|{
specifier|public
specifier|static
name|NodeProxyComparator
name|instance
init|=
operator|new
name|NodeProxyComparator
argument_list|()
decl_stmt|;
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|obj1
parameter_list|,
name|Object
name|obj2
parameter_list|)
block|{
if|if
condition|(
name|obj1
operator|==
literal|null
operator|||
name|obj2
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"cannot compare null values"
argument_list|)
throw|;
if|if
condition|(
operator|!
operator|(
name|obj1
operator|instanceof
name|NodeProxy
operator|&&
name|obj2
operator|instanceof
name|NodeProxy
operator|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot compare nodes "
operator|+
literal|"from different implementations"
argument_list|)
throw|;
name|NodeProxy
name|p1
init|=
operator|(
name|NodeProxy
operator|)
name|obj1
decl_stmt|;
name|NodeProxy
name|p2
init|=
operator|(
name|NodeProxy
operator|)
name|obj2
decl_stmt|;
if|if
condition|(
name|p1
operator|.
name|doc
operator|.
name|docId
operator|==
name|p2
operator|.
name|doc
operator|.
name|docId
condition|)
block|{
if|if
condition|(
name|p1
operator|.
name|gid
operator|==
name|p2
operator|.
name|gid
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|p1
operator|.
name|gid
operator|<
name|p2
operator|.
name|gid
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
if|else if
condition|(
name|p1
operator|.
name|doc
operator|.
name|docId
operator|<
name|p2
operator|.
name|doc
operator|.
name|docId
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
comment|/** 		 * Sets the doc this node belongs to. 		 * @param doc The doc to set 		 */
specifier|public
name|void
name|setDoc
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
comment|/** 		 * Sets the nodeType. 		 * @param nodeType The nodeType to set 		 */
specifier|public
name|void
name|setNodeType
parameter_list|(
name|short
name|nodeType
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
comment|/** 		 * Returns the storage address of this node in dom.dbx. 		 * @return long 		 */
specifier|public
name|long
name|getInternalAddress
parameter_list|()
block|{
return|return
name|internalAddress
return|;
block|}
comment|/** 	 * Sets the storage address of this node in dom.dbx. 	 *  	 * @param internalAddress The internalAddress to set 	 */
specifier|public
name|void
name|setInternalAddress
parameter_list|(
name|long
name|internalAddress
parameter_list|)
block|{
name|this
operator|.
name|internalAddress
operator|=
name|internalAddress
expr_stmt|;
block|}
specifier|public
name|void
name|setHasIndex
parameter_list|(
name|boolean
name|hasIndex
parameter_list|)
block|{
name|internalAddress
operator|=
operator|(
name|hasIndex
condition|?
name|internalAddress
operator||
literal|0x10000L
else|:
name|internalAddress
operator|&
operator|(
operator|~
literal|0x10000L
operator|)
operator|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasIndex
parameter_list|()
block|{
return|return
operator|(
name|internalAddress
operator|&
literal|0x10000L
operator|)
operator|>
literal|0
return|;
block|}
specifier|public
name|boolean
name|hasMatch
parameter_list|(
name|Match
name|m
parameter_list|)
block|{
if|if
condition|(
name|m
operator|==
literal|null
operator|||
name|match
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|Match
name|next
init|=
name|match
decl_stmt|;
do|do
block|{
if|if
condition|(
name|next
operator|.
name|equals
argument_list|(
name|m
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
do|while
condition|(
operator|(
name|next
operator|=
name|next
operator|.
name|getNextMatch
argument_list|()
operator|)
operator|!=
literal|null
condition|)
do|;
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|addMatch
parameter_list|(
name|Match
name|m
parameter_list|)
block|{
if|if
condition|(
name|match
operator|==
literal|null
condition|)
block|{
name|match
operator|=
name|m
expr_stmt|;
name|match
operator|.
name|prevMatch
operator|=
literal|null
expr_stmt|;
name|match
operator|.
name|nextMatch
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|Match
name|next
init|=
name|match
decl_stmt|;
name|int
name|cmp
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|cmp
operator|=
name|m
operator|.
name|compareTo
argument_list|(
name|next
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
operator|&&
name|m
operator|.
name|getNodeId
argument_list|()
operator|==
name|next
operator|.
name|getNodeId
argument_list|()
condition|)
return|return;
if|else if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|prevMatch
operator|!=
literal|null
condition|)
name|next
operator|.
name|prevMatch
operator|.
name|nextMatch
operator|=
name|m
expr_stmt|;
else|else
name|match
operator|=
name|m
expr_stmt|;
name|m
operator|.
name|prevMatch
operator|=
name|next
operator|.
name|prevMatch
expr_stmt|;
name|next
operator|.
name|prevMatch
operator|=
name|m
expr_stmt|;
name|m
operator|.
name|nextMatch
operator|=
name|next
expr_stmt|;
return|return;
block|}
if|else if
condition|(
name|next
operator|.
name|nextMatch
operator|==
literal|null
condition|)
block|{
name|next
operator|.
name|nextMatch
operator|=
name|m
expr_stmt|;
name|m
operator|.
name|prevMatch
operator|=
name|next
expr_stmt|;
name|m
operator|.
name|nextMatch
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|next
operator|=
name|next
operator|.
name|nextMatch
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addMatches
parameter_list|(
name|Match
name|m
parameter_list|)
block|{
name|Match
name|next
decl_stmt|;
while|while
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|next
operator|=
name|m
operator|.
name|nextMatch
expr_stmt|;
name|addMatch
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
name|next
expr_stmt|;
block|}
comment|//printMatches();
block|}
specifier|public
name|void
name|printMatches
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|gid
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|Match
name|next
init|=
name|match
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|next
operator|.
name|getMatchingTerm
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|nextMatch
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Add a node to the list of context nodes for this node. 	 *  	 * NodeProxy internally stores the context nodes of the XPath context, for which  	 * this node has been selected during a previous processing step. 	 *  	 * Since eXist tries to process many expressions in one, single processing step, 	 * the context information is required to resolve predicate expressions. For 	 * example, for an expression like //SCENE[SPEECH/SPEAKER='HAMLET'], 	 * we have to remember the SCENE nodes for which the equality expression 	 * in the predicate was true.  Thus, when evaluating the step SCENE[SPEECH], the 	 * SCENE nodes become context items of the SPEECH nodes and this context 	 * information is preserved through all following steps. 	 *  	 * To process the predicate expression, {@link org.exist.xpath.Predicate} will take the 	 * context nodes returned by the filter expression and compare them to its context 	 * node set. 	 */
specifier|public
name|void
name|addContextNode
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|context
operator|=
operator|new
name|ContextItem
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|//			Thread.dumpStack();
return|return;
block|}
name|ContextItem
name|next
init|=
name|context
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getNode
argument_list|()
operator|.
name|gid
operator|==
name|node
operator|.
name|gid
condition|)
break|break;
if|if
condition|(
name|next
operator|.
name|getNextItem
argument_list|()
operator|==
literal|null
condition|)
block|{
name|next
operator|.
name|setNextItem
argument_list|(
operator|new
name|ContextItem
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|next
operator|=
name|next
operator|.
name|getNextItem
argument_list|()
expr_stmt|;
block|}
comment|//		Thread.dumpStack();
block|}
specifier|public
name|void
name|clearContext
parameter_list|()
block|{
name|context
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|printContext
parameter_list|()
block|{
name|ContextItem
name|next
init|=
name|context
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|gid
operator|+
literal|": "
argument_list|)
expr_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|next
operator|.
name|getNode
argument_list|()
operator|.
name|gid
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|getNextItem
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|copyContext
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
name|context
operator|=
name|node
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ContextItem
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
comment|//	methods of interface Item
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
switch|switch
condition|(
name|nodeType
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
return|return
name|Type
operator|.
name|ELEMENT
return|;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
return|return
name|Type
operator|.
name|ATTRIBUTE
return|;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
return|return
name|Type
operator|.
name|TEXT
return|;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
return|return
name|Type
operator|.
name|PROCESSING_INSTRUCTION
return|;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
return|return
name|Type
operator|.
name|COMMENT
return|;
default|default :
return|return
name|Type
operator|.
name|NODE
return|;
comment|// unknown type
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#toSequence() 	 */
specifier|public
name|Sequence
name|toSequence
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
name|getNodeValue
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#convertTo(int) 	 */
specifier|public
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|getNodeValue
argument_list|()
argument_list|)
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#atomize() 	 */
specifier|public
name|AtomicValue
name|atomize
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|getNodeValue
argument_list|()
argument_list|)
return|;
block|}
comment|/* -----------------------------------------------* 	 * Methods of class NodeSet 	 * -----------------------------------------------*/
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#iterator() 	 */
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|SingleNodeIterator
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
return|return
operator|new
name|SingleNodeIterator
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#contains(org.exist.dom.DocumentImpl, long) 	 */
specifier|public
name|boolean
name|contains
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
operator|==
name|doc
operator|.
name|getDocId
argument_list|()
operator|&&
name|this
operator|.
name|gid
operator|==
name|nodeId
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#contains(org.exist.dom.NodeProxy) 	 */
specifier|public
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
return|return
name|doc
operator|.
name|getDocId
argument_list|()
operator|==
name|proxy
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
operator|&&
name|gid
operator|==
name|proxy
operator|.
name|gid
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#addAll(org.exist.dom.NodeSet) 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#add(org.exist.dom.NodeProxy) 	 */
specifier|public
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.NodeList#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.NodeList#item(int) 	 */
specifier|public
name|Node
name|item
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|>
literal|0
condition|?
literal|null
else|:
name|getNode
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#itemAt(int) 	 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|>
literal|0
condition|?
literal|null
else|:
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#get(int) 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|>
literal|0
condition|?
literal|null
else|:
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#get(org.exist.dom.NodeProxy) 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
return|return
name|contains
argument_list|(
name|p
argument_list|)
condition|?
name|this
else|:
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#get(org.exist.dom.DocumentImpl, long) 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
return|return
name|contains
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
condition|?
name|this
else|:
literal|null
return|;
block|}
specifier|public
name|void
name|toSAX
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
block|{
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|setProperty
argument_list|(
name|Serializer
operator|.
name|GENERATE_DOC_EVENTS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|SingleNodeIterator
implements|implements
name|Iterator
implements|,
name|SequenceIterator
block|{
specifier|private
name|boolean
name|hasNext
init|=
literal|true
decl_stmt|;
specifier|private
name|NodeProxy
name|node
decl_stmt|;
specifier|public
name|SingleNodeIterator
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|hasNext
return|;
block|}
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
name|hasNext
condition|)
block|{
name|hasNext
operator|=
literal|false
expr_stmt|;
return|return
name|node
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not supported"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.SequenceIterator#nextItem() 		 */
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
if|if
condition|(
name|hasNext
condition|)
block|{
name|hasNext
operator|=
literal|false
expr_stmt|;
return|return
name|node
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

