begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id:  */
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
name|util
operator|.
name|LongLinkedList
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
name|Comparable
implements|,
name|Cloneable
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
name|long
name|internalAddress
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|LongLinkedList
name|contextNodes
init|=
literal|null
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
index|[]
name|matches
init|=
literal|null
decl_stmt|;
comment|//public long backupId = -1;
comment|//public boolean valid = true;
specifier|public
name|NodeProxy
parameter_list|()
block|{
block|}
comment|/**      *  Constructor for the NodeProxy object      *      *@param  doc      Description of the Parameter      *@param  gid      Description of the Parameter      *@param  address  Description of the Parameter      */
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
comment|/**      *  Construct a node proxy with unique id gid and owned by document doc.      *      *@param  doc  Description of the Parameter      *@param  gid  Description of the Parameter      */
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
comment|/**      *  as above, but a hint is given about the node type of this proxy-object.      *      *@param  doc       Description of the Parameter      *@param  gid       Description of the Parameter      *@param  nodeType  Description of the Parameter      */
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
comment|/**      *  Constructor for the NodeProxy object      *      *@param  doc       Description of the Parameter      *@param  gid       Description of the Parameter      *@param  nodeType  Description of the Parameter      *@param  address   Description of the Parameter      */
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
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
comment|/**      *  Constructor for the NodeProxy object      *      *@param  p  Description of the Parameter      */
specifier|public
name|NodeProxy
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
name|this
operator|.
name|gid
operator|=
name|p
operator|.
name|gid
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|p
operator|.
name|doc
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|p
operator|.
name|internalAddress
expr_stmt|;
name|this
operator|.
name|nodeType
operator|=
name|p
operator|.
name|nodeType
expr_stmt|;
name|this
operator|.
name|matches
operator|=
name|p
operator|.
name|matches
expr_stmt|;
comment|//this.backupId = p.backupId;
block|}
comment|/**      *  Constructor for the NodeProxy object      *      *@param  node  Description of the Parameter      */
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
block|}
comment|/** 	 * Reset the object's state (for reuse). 	 * 	 */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|doc
operator|=
literal|null
expr_stmt|;
name|gid
operator|=
literal|0
expr_stmt|;
name|internalAddress
operator|=
operator|-
literal|1
expr_stmt|;
name|nodeType
operator|=
operator|-
literal|1
expr_stmt|;
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
comment|/**      *  Gets the brokerType attribute of the NodeProxy object      *      *@return    The brokerType value      */
specifier|public
name|int
name|getBrokerType
parameter_list|()
block|{
return|return
name|doc
operator|.
name|broker
operator|.
name|getDatabaseType
argument_list|()
return|;
block|}
comment|/**      *  Gets the doc attribute of the NodeProxy object      *      *@return    The doc value      */
specifier|public
name|DocumentImpl
name|getDoc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|/**      *  Gets the gID attribute of the NodeProxy object      *      *@return    The gID value      */
specifier|public
name|long
name|getGID
parameter_list|()
block|{
return|return
name|gid
return|;
block|}
comment|/**      *  Gets the node attribute of the NodeProxy object      *      *@return    The node value      */
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
comment|/**      *  Gets the nodeType attribute of the NodeProxy object      *      *@return    The nodeType value      */
specifier|public
name|short
name|getNodeType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
comment|/**      *  Gets the nodeValue attribute of the NodeProxy object      *      *@return    The nodeValue value      */
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
comment|/**      *  Sets the node-identifier of this node.      *      *@param  gid  The new gID value      */
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
comment|/**      * Returns the storage address of this node in dom.dbx.      * @return long      */
specifier|public
name|long
name|getInternalAddress
parameter_list|()
block|{
return|return
name|internalAddress
return|;
block|}
comment|/**      * Sets the doc this node belongs to.      * @param doc The doc to set      */
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
comment|/**      * Sets the storage address of this node in dom.dbx.      *       * @param internalAddress The internalAddress to set      */
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
comment|/**      * Sets the nodeType.      * @param nodeType The nodeType to set      */
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
specifier|public
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
specifier|final
name|NodeProxy
name|clone
init|=
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
name|nodeType
argument_list|,
name|internalAddress
argument_list|)
decl_stmt|;
name|clone
operator|.
name|matches
operator|=
name|matches
expr_stmt|;
return|return
name|clone
return|;
block|}
specifier|public
name|boolean
name|hasMatch
parameter_list|(
name|Match
name|match
parameter_list|)
block|{
if|if
condition|(
name|match
operator|==
literal|null
operator|||
name|matches
operator|==
literal|null
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
name|matches
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|matches
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|match
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|addMatch
parameter_list|(
name|Match
name|match
parameter_list|)
block|{
name|Match
name|m
index|[]
init|=
operator|new
name|Match
index|[
operator|(
name|matches
operator|==
literal|null
condition|?
literal|1
else|:
name|matches
operator|.
name|length
operator|+
literal|1
operator|)
index|]
decl_stmt|;
name|m
index|[
literal|0
index|]
operator|=
name|match
expr_stmt|;
if|if
condition|(
name|matches
operator|!=
literal|null
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|matches
argument_list|,
literal|0
argument_list|,
name|m
argument_list|,
literal|1
argument_list|,
name|matches
operator|.
name|length
argument_list|)
expr_stmt|;
name|matches
operator|=
name|m
expr_stmt|;
block|}
specifier|public
name|void
name|addMatches
parameter_list|(
name|Match
name|m
index|[]
parameter_list|)
block|{
if|if
condition|(
name|m
operator|==
literal|null
operator|||
name|m
operator|.
name|length
operator|==
literal|0
condition|)
return|return;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|m
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|!
name|hasMatch
argument_list|(
name|m
index|[
name|i
index|]
argument_list|)
condition|)
name|addMatch
argument_list|(
name|m
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|printMatches
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
name|gid
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
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
name|matches
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
name|matches
index|[
name|i
index|]
operator|.
name|getMatchingTerm
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
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
name|contextNodes
operator|==
literal|null
condition|)
name|contextNodes
operator|=
operator|new
name|LongLinkedList
argument_list|()
expr_stmt|;
name|contextNodes
operator|.
name|add
argument_list|(
name|node
operator|.
name|gid
argument_list|)
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
name|contextNodes
operator|=
name|node
operator|.
name|contextNodes
expr_stmt|;
block|}
specifier|public
name|LongLinkedList
name|getContext
parameter_list|()
block|{
return|return
name|contextNodes
return|;
block|}
block|}
end_class

end_unit

