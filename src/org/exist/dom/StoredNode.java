begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2000-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|Iterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
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
name|stax
operator|.
name|EmbeddedXMLStreamReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|ExtendedXMLStreamReader
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
name|NodePath
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
name|Signatures
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
name|pool
operator|.
name|NodePool
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
name|Constants
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
name|DOMException
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
name|Document
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
comment|/**  *  The base class for all persistent DOM nodes in the database.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  */
end_comment

begin_class
specifier|public
class|class
name|StoredNode
extends|extends
name|NodeImpl
implements|implements
name|Visitable
implements|,
name|NodeHandle
block|{
specifier|public
specifier|final
specifier|static
name|int
name|LENGTH_SIGNATURE_LENGTH
init|=
literal|1
decl_stmt|;
comment|//sizeof byte
specifier|public
specifier|final
specifier|static
name|long
name|UNKNOWN_NODE_IMPL_ADDRESS
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|NodeId
name|nodeId
init|=
literal|null
decl_stmt|;
specifier|protected
name|DocumentImpl
name|ownerDocument
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|internalAddress
init|=
name|UNKNOWN_NODE_IMPL_ADDRESS
decl_stmt|;
specifier|private
name|short
name|nodeType
init|=
name|NodeProxy
operator|.
name|UNKNOWN_NODE_TYPE
decl_stmt|;
comment|/**      * Creates a new<code>StoredNode</code> instance.      *      * @param nodeType a<code>short</code> value      */
specifier|public
name|StoredNode
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
comment|/**      * Creates a new<code>StoredNode</code> instance.      *      * @param nodeType a<code>short</code> value      * @param nodeId a<code>NodeId</code> value      */
specifier|public
name|StoredNode
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
block|}
comment|/**      * Copy constructor: creates a copy of the other node.      *      * @param other a<code>StoredNode</code> value      */
specifier|public
name|StoredNode
parameter_list|(
name|StoredNode
name|other
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|other
operator|.
name|nodeType
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|other
operator|.
name|nodeId
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|other
operator|.
name|internalAddress
expr_stmt|;
name|this
operator|.
name|ownerDocument
operator|=
name|other
operator|.
name|ownerDocument
expr_stmt|;
block|}
comment|/**      * Creates a new<code>StoredNode</code> instance.      *      * @param other a<code>NodeProxy</code> value      */
specifier|public
name|StoredNode
parameter_list|(
name|NodeProxy
name|other
parameter_list|)
block|{
name|this
operator|.
name|ownerDocument
operator|=
name|other
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeType
operator|=
name|other
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|other
operator|.
name|getNodeId
argument_list|()
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|other
operator|.
name|getInternalAddress
argument_list|()
expr_stmt|;
block|}
comment|/**      * Reset this object to its initial state. Required by the      * parser to be able to reuse node objects.      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|this
operator|.
name|nodeId
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|UNKNOWN_NODE_IMPL_ADDRESS
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|INVALID_ACCESS_ERR
argument_list|,
literal|"Can't serialize "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * Read a node from the specified byte array.      *       * This checks the node type and calls the {@link #deserialize(byte[], int, int,DocumentImpl,boolean)}      * method of the corresponding node class.      *       * @param data      * @param start      * @param len      * @param doc      */
specifier|public
specifier|static
name|StoredNode
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
block|{
return|return
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Read a node from the specified byte array.      *       * This checks the node type and calls the {@link #deserialize(byte[], int, int, DocumentImpl, boolean)}      * method of the corresponding node class. The node will be allocated in the pool      * and should be released once it is no longer needed.      *       * @param data      * @param start      * @param len      * @param doc      */
specifier|public
specifier|static
name|StoredNode
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|pooled
parameter_list|)
block|{
name|short
name|type
init|=
name|Signatures
operator|.
name|getType
argument_list|(
name|data
index|[
name|start
index|]
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
return|return
name|TextImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
return|return
name|ElementImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
return|return
name|AttrImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
return|return
name|ProcessingInstructionImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
return|return
name|CommentImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
return|return
name|CDATASectionImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
name|pooled
argument_list|)
return|;
default|default :
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown node type: "
operator|+
name|type
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|dumpStack
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
switch|switch
condition|(
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
return|return
name|QName
operator|.
name|DOCUMENT_QNAME
return|;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
return|return
name|QName
operator|.
name|TEXT_QNAME
return|;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
return|return
name|QName
operator|.
name|COMMENT_QNAME
return|;
case|case
name|Node
operator|.
name|DOCUMENT_TYPE_NODE
case|:
return|return
name|QName
operator|.
name|DOCTYPE_QNAME
return|;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown node type: "
operator|+
name|getNodeType
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**      * @see java.lang.Object#equals(java.lang.Object)      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|StoredNode
operator|)
condition|)
return|return
literal|false
return|;
return|return
operator|(
operator|(
name|StoredNode
operator|)
name|obj
operator|)
operator|.
name|nodeId
operator|.
name|equals
argument_list|(
name|nodeId
argument_list|)
return|;
block|}
comment|/**      * The method<code>setNodeId</code>      *      * @param dln a<code>NodeId</code> value      */
specifier|public
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|dln
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|dln
expr_stmt|;
block|}
comment|/**      * The method<code>getNodeId</code>      *      * @return a<code>NodeId</code> value      */
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
comment|/**      *  Get the internal storage address of this node      *      *@return    The internalAddress value      */
specifier|public
name|long
name|getInternalAddress
parameter_list|()
block|{
return|return
name|internalAddress
return|;
block|}
comment|/**      *  Set the internal storage address of this node.      *      *@param  internalAddress  The new internalAddress value      */
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
comment|/**      * Returns true if the node was modified recently and nodes      * were inserted at the start or in the middle of its children.      *      * @return TRUE when node is 'dirty'      */
specifier|public
name|boolean
name|isDirty
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|setDirty
parameter_list|(
name|boolean
name|dirty
parameter_list|)
block|{
comment|//Nothing to do
block|}
comment|/**      * @see org.w3c.dom.Node#getNodeType()      */
specifier|public
name|short
name|getNodeType
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeType
return|;
block|}
comment|/**      * @see org.w3c.dom.Node#getOwnerDocument()      */
specifier|public
name|Document
name|getOwnerDocument
parameter_list|()
block|{
return|return
name|ownerDocument
return|;
block|}
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|ownerDocument
return|;
block|}
specifier|public
name|DocumentAtExist
name|getDocumentAtExist
parameter_list|()
block|{
return|return
name|ownerDocument
return|;
block|}
comment|/**      *  Set the owner document.      *      *@param  ownerDocument  The new ownerDocument value      */
specifier|public
name|void
name|setOwnerDocument
parameter_list|(
name|DocumentImpl
name|ownerDocument
parameter_list|)
block|{
name|this
operator|.
name|ownerDocument
operator|=
name|ownerDocument
expr_stmt|;
block|}
specifier|public
name|int
name|getDocId
parameter_list|()
block|{
return|return
name|ownerDocument
operator|.
name|getDocId
argument_list|()
return|;
block|}
comment|/**      * @see org.w3c.dom.Node#getParentNode()      */
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
name|NodeId
name|parentId
init|=
name|nodeId
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentId
operator|==
name|NodeId
operator|.
name|DOCUMENT_NODE
condition|)
return|return
name|ownerDocument
return|;
comment|// Filter out the temporary nodes wrapper element
if|if
condition|(
name|parentId
operator|.
name|getTreeLevel
argument_list|()
operator|==
literal|1
operator|&&
operator|(
operator|(
name|DocumentImpl
operator|)
name|getOwnerDocument
argument_list|()
operator|)
operator|.
name|getCollection
argument_list|()
operator|.
name|isTempCollection
argument_list|()
condition|)
return|return
name|ownerDocument
return|;
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|parentId
argument_list|)
return|;
block|}
specifier|public
name|StoredNode
name|getParentStoredNode
parameter_list|()
block|{
name|Node
name|parent
init|=
name|getParentNode
argument_list|()
decl_stmt|;
return|return
name|parent
operator|instanceof
name|StoredNode
condition|?
operator|(
name|StoredNode
operator|)
name|parent
else|:
literal|null
return|;
block|}
comment|/**      * @see org.w3c.dom.Node#getPreviousSibling()      */
specifier|public
name|Node
name|getPreviousSibling
parameter_list|()
block|{
name|StoredNode
name|parent
init|=
name|getParentStoredNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|parent
operator|.
name|isDirty
argument_list|()
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
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|EmbeddedXMLStreamReader
name|reader
init|=
name|broker
operator|.
name|getXMLStreamReader
argument_list|(
name|parent
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|level
init|=
name|nodeId
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
name|StoredNode
name|last
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
name|NodeId
name|currentId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|ExtendedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|&&
name|currentId
operator|.
name|getTreeLevel
argument_list|()
operator|==
name|level
condition|)
block|{
if|if
condition|(
name|currentId
operator|.
name|equals
argument_list|(
name|nodeId
argument_list|)
condition|)
return|return
name|last
return|;
name|last
operator|=
name|reader
operator|.
name|getNode
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|NodeId
name|firstChild
init|=
name|parent
operator|.
name|getNodeId
argument_list|()
operator|.
name|newChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeId
operator|.
name|equals
argument_list|(
name|firstChild
argument_list|)
condition|)
return|return
literal|null
return|;
name|NodeId
name|siblingId
init|=
name|nodeId
operator|.
name|precedingSibling
argument_list|()
decl_stmt|;
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|siblingId
argument_list|)
return|;
block|}
comment|/**      * @see org.w3c.dom.Node#getNextSibling()      */
specifier|public
name|Node
name|getNextSibling
parameter_list|()
block|{
if|if
condition|(
name|nodeId
operator|.
name|getTreeLevel
argument_list|()
operator|==
literal|2
operator|&&
operator|(
operator|(
name|DocumentImpl
operator|)
name|getOwnerDocument
argument_list|()
operator|)
operator|.
name|getCollection
argument_list|()
operator|.
name|isTempCollection
argument_list|()
condition|)
return|return
literal|null
return|;
specifier|final
name|StoredNode
name|parent
init|=
name|getParentStoredNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|parent
operator|.
name|isDirty
argument_list|()
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
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|EmbeddedXMLStreamReader
name|reader
init|=
name|broker
operator|.
name|getXMLStreamReader
argument_list|(
name|parent
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|int
name|level
init|=
name|nodeId
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
name|NodeId
name|currentId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|ExtendedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|&&
name|currentId
operator|.
name|getTreeLevel
argument_list|()
operator|==
name|level
condition|)
block|{
if|if
condition|(
name|currentId
operator|.
name|compareTo
argument_list|(
name|nodeId
argument_list|)
operator|>
literal|0
condition|)
return|return
name|reader
operator|.
name|getNode
argument_list|()
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|NodeId
name|siblingId
init|=
name|nodeId
operator|.
name|nextSibling
argument_list|()
decl_stmt|;
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|siblingId
argument_list|)
return|;
block|}
specifier|protected
name|StoredNode
name|getLastNode
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|hasChildNodes
argument_list|()
condition|)
return|return
name|node
return|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|EmbeddedXMLStreamReader
name|reader
init|=
name|broker
operator|.
name|getXMLStreamReader
argument_list|(
name|node
argument_list|,
literal|true
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|reader
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|reader
operator|.
name|getPreviousNode
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading child nodes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|StoredNode
name|getLastNode
parameter_list|(
name|Iterator
argument_list|<
name|StoredNode
argument_list|>
name|iterator
parameter_list|,
name|StoredNode
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|hasChildNodes
argument_list|()
condition|)
return|return
name|node
return|;
specifier|final
name|int
name|children
init|=
name|node
operator|.
name|getChildCount
argument_list|()
decl_stmt|;
name|StoredNode
name|next
init|=
literal|null
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
name|children
condition|;
name|i
operator|++
control|)
block|{
name|next
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
comment|//Recursivity helps taversing...
name|next
operator|=
name|getLastNode
argument_list|(
name|iterator
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
specifier|public
name|NodePath
name|getPath
parameter_list|()
block|{
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
if|if
condition|(
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
name|path
operator|.
name|addComponent
argument_list|(
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
name|NodeImpl
name|parent
init|=
operator|(
name|NodeImpl
operator|)
name|getParentNode
argument_list|()
decl_stmt|;
while|while
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
block|{
name|path
operator|.
name|addComponentAtStart
argument_list|(
name|parent
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
name|parent
operator|=
operator|(
name|NodeImpl
operator|)
name|parent
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
specifier|public
name|NodePath
name|getPath
parameter_list|(
name|NodePath
name|parentPath
parameter_list|)
block|{
if|if
condition|(
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
name|parentPath
operator|.
name|addComponent
argument_list|(
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parentPath
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
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
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getQName
argument_list|()
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
name|String
name|toString
parameter_list|(
name|boolean
name|top
parameter_list|)
block|{
return|return
name|toString
argument_list|()
return|;
block|}
comment|/**      * Release all memory resources hold by this node.       */
specifier|public
name|void
name|release
parameter_list|()
block|{
name|ownerDocument
operator|=
literal|null
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
name|NodePool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnNode
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|accept
parameter_list|(
name|NodeVisitor
name|visitor
parameter_list|)
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
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|Iterator
argument_list|<
name|StoredNode
argument_list|>
name|iterator
init|=
name|broker
operator|.
name|getNodeIterator
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|accept
argument_list|(
name|iterator
argument_list|,
name|visitor
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while reading node: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|accept
parameter_list|(
name|Iterator
argument_list|<
name|StoredNode
argument_list|>
name|iterator
parameter_list|,
name|NodeVisitor
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|int
name|getNodeNumber
parameter_list|()
block|{
return|return
literal|0
return|;
comment|//TODO: find a value for node number
block|}
annotation|@
name|Deprecated
specifier|private
specifier|final
specifier|static
class|class
name|PreviousSiblingVisitor
implements|implements
name|NodeVisitor
block|{
specifier|private
name|StoredNode
name|current
decl_stmt|;
specifier|private
name|StoredNode
name|last
init|=
literal|null
decl_stmt|;
specifier|public
name|PreviousSiblingVisitor
parameter_list|(
name|StoredNode
name|current
parameter_list|)
block|{
name|this
operator|.
name|current
operator|=
name|current
expr_stmt|;
block|}
specifier|public
name|boolean
name|visit
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|nodeId
operator|.
name|equals
argument_list|(
name|current
operator|.
name|nodeId
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|node
operator|.
name|nodeId
operator|.
name|getTreeLevel
argument_list|()
operator|==
name|current
operator|.
name|nodeId
operator|.
name|getTreeLevel
argument_list|()
condition|)
name|last
operator|=
name|node
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
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
name|StoredNode
operator|)
condition|)
block|{
return|return
operator|(
name|Constants
operator|.
name|INFERIOR
operator|)
return|;
block|}
name|StoredNode
name|n
init|=
operator|(
name|StoredNode
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|ownerDocument
operator|==
name|ownerDocument
condition|)
block|{
return|return
name|nodeId
operator|.
name|compareTo
argument_list|(
name|n
operator|.
name|nodeId
argument_list|)
return|;
block|}
if|else if
condition|(
name|ownerDocument
operator|.
name|getDocId
argument_list|()
operator|<
name|n
operator|.
name|ownerDocument
operator|.
name|getDocId
argument_list|()
condition|)
block|{
return|return
operator|(
name|Constants
operator|.
name|INFERIOR
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|Constants
operator|.
name|SUPERIOR
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

