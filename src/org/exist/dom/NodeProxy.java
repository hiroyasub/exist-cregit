begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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

begin_comment
comment|/**  *  Placeholder class for any DOM-node. NodeProxy stores a node's unique id and  *  the document a node belongs to. eXist will always try to use a NodeProxy  *  instead of the actual node. Using a NodeProxy is much cheaper than loading  *  the actual node from the database. All sets of type NodeSet operate on  *  NodeProxys. NodeProxy implements Comparable, which is needed by all  *  node-sets. To convert a NodeProxy to a real node, simply call getNode().  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    22. Juli 2002  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NodeProxy
implements|implements
name|Item
implements|,
name|Comparable
block|{
specifier|public
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
specifier|public
name|long
name|gid
init|=
literal|0
decl_stmt|;
specifier|public
name|short
name|nodeType
init|=
operator|-
literal|1
decl_stmt|;
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
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
name|nodeType
argument_list|)
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
comment|/** 		 * Sets the storage address of this node in dom.dbx. 		 *  		 * @param internalAddress The internalAddress to set 		 */
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
literal|true
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
break|break;
block|}
if|else if
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
break|break;
if|if
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
break|break;
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
return|return;
block|}
name|next
operator|=
name|next
operator|.
name|getNextItem
argument_list|()
expr_stmt|;
block|}
comment|//		System.out.print(gid + " context: ");
comment|//		for(Iterator i = contextNodes.iterator(); i.hasNext(); ) {
comment|//			System.out.print(((NodeProxy)i.next()).gid + " ");
comment|//		}
comment|//		System.out.println();
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
operator|new
name|SingleNodeSet
argument_list|(
name|this
argument_list|)
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
block|}
end_class

end_unit

