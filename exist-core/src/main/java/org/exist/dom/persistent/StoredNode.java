begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2000-2014 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
package|;
end_package

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
name|QName
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
name|ExtendedXMLStreamReader
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
name|IEmbeddedXMLStreamReader
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
name|NodePath2
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
name|storage
operator|.
name|dom
operator|.
name|INodeIterator
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
name|Attr
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
name|Node
import|;
end_import

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

begin_comment
comment|/**  * The base class for all persistent DOM nodes in the database.  *  * @author<a href="mailto:meier@ifs.tu-darmstadt.de">Wolfgang Meier</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|StoredNode
parameter_list|<
name|T
extends|extends
name|StoredNode
parameter_list|>
extends|extends
name|NodeImpl
argument_list|<
name|T
argument_list|>
implements|implements
name|Visitable
implements|,
name|NodeHandle
implements|,
name|IStoredNode
argument_list|<
name|T
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH_SIGNATURE_LENGTH
init|=
literal|1
decl_stmt|;
comment|//sizeof byte
specifier|public
specifier|static
specifier|final
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
specifier|protected
specifier|final
name|short
name|nodeType
decl_stmt|;
comment|/**      * Creates a new<code>StoredNode</code> instance.      *      * @param nodeType a<code>short</code> value      * return new StoredNode      */
specifier|protected
name|StoredNode
parameter_list|(
specifier|final
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
comment|/**      * Creates a new<code>StoredNode</code> instance.      *      * @param nodeType a<code>short</code> value      * @param nodeId   a<code>NodeId</code> value      */
specifier|protected
name|StoredNode
parameter_list|(
specifier|final
name|short
name|nodeType
parameter_list|,
specifier|final
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
specifier|protected
name|StoredNode
parameter_list|(
specifier|final
name|short
name|nodeType
parameter_list|,
specifier|final
name|NodeId
name|nodeId
parameter_list|,
specifier|final
name|DocumentImpl
name|ownerDocument
parameter_list|,
name|long
name|internalAddress
parameter_list|)
block|{
name|this
argument_list|(
name|nodeType
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|this
operator|.
name|ownerDocument
operator|=
name|ownerDocument
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|internalAddress
expr_stmt|;
block|}
specifier|protected
name|StoredNode
parameter_list|(
specifier|final
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
comment|/**      * Extracts just the details of the StoredNode      * @return details of the stored node      *      */
specifier|public
name|StoredNode
name|extract
parameter_list|()
block|{
return|return
operator|new
name|StoredNode
argument_list|(
name|this
argument_list|)
block|{         }
return|;
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
annotation|@
name|Override
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
comment|/**      * Read a node from the specified byte array.      *      * This checks the node type and calls the {@link #deserialize(byte[], int, int, DocumentImpl, boolean)}      * method of the corresponding node class.      *      * @param data the byte array to read a node from      * @param start where to start      * @param len how much to read      * @param doc the doc to store the result in      * @return StoredNode of given byte array      */
specifier|public
specifier|static
name|StoredNode
name|deserialize
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|,
specifier|final
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
comment|/**      * Read a node from the specified byte array.      *      * This checks the node type and calls the {@link #deserialize(byte[], int, int, DocumentImpl, boolean)}      * method of the corresponding node class. The node will be allocated in the pool      * and should be released once it is no longer needed.      *      * @param data the byte array to read a node from       * @param start where to start      * @param len how much to read      * @param doc the doc to store the result in      * @param pooled if true the node will be allocated in the pool      * @return StoredNode of given byte array      */
specifier|public
specifier|static
name|StoredNode
name|deserialize
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|,
specifier|final
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|pooled
parameter_list|)
block|{
specifier|final
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
default|default:
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|setQName
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
block|{
comment|//do nothing
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
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
block|{
return|return
literal|false
return|;
block|}
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
annotation|@
name|Override
specifier|public
name|void
name|setNodeId
parameter_list|(
specifier|final
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
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getInternalAddress
parameter_list|()
block|{
return|return
name|internalAddress
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setInternalAddress
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|setDirty
parameter_list|(
specifier|final
name|boolean
name|dirty
parameter_list|)
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|DocumentImpl
name|getOwnerDocument
parameter_list|()
block|{
return|return
name|ownerDocument
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOwnerDocument
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
specifier|final
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
block|{
return|return
name|ownerDocument
return|;
block|}
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
name|getOwnerDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
operator|.
name|isTempCollection
argument_list|()
condition|)
block|{
return|return
name|ownerDocument
return|;
block|}
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|parentId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoredNode
name|getParentStoredNode
parameter_list|()
block|{
specifier|final
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
annotation|@
name|Override
specifier|public
name|Node
name|getPreviousSibling
parameter_list|()
block|{
comment|// if we are the root node, there is no sibling
if|if
condition|(
name|nodeId
operator|.
name|equals
argument_list|(
name|NodeId
operator|.
name|ROOT_NODE
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// handle siblings of level 1, e.g. a comment before/after a document element
if|if
condition|(
name|nodeId
operator|.
name|getTreeLevel
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|NodeId
name|siblingId
init|=
name|nodeId
operator|.
name|precedingSibling
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
return|return
name|broker
operator|.
name|objectWith
argument_list|(
name|ownerDocument
argument_list|,
name|siblingId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading previous sibling node: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : throw exception -pb
block|}
block|}
comment|// handle siblings of level 1+n
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
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|isDirty
argument_list|()
condition|)
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
specifier|final
name|int
name|parentLevel
init|=
name|parent
operator|.
name|getNodeId
argument_list|()
operator|.
name|getTreeLevel
argument_list|()
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
specifier|final
name|IEmbeddedXMLStreamReader
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
name|IStoredNode
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
specifier|final
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
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
condition|)
block|{
if|if
condition|(
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
block|{
return|return
name|last
return|;
block|}
name|last
operator|=
name|reader
operator|.
name|getNode
argument_list|()
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|status
operator|==
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|&&
name|currentId
operator|.
name|getTreeLevel
argument_list|()
operator|==
name|parentLevel
condition|)
block|{
comment|// reached the end of the parent element
break|break;
comment|// exit while loop
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|XMLStreamException
decl||
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
comment|//TODO : throw exception -pb
block|}
return|return
literal|null
return|;
block|}
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
specifier|final
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
annotation|@
name|Override
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
name|getOwnerDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
operator|.
name|isTempCollection
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// handle siblings of level 1, e.g. a comment before/after a document element
if|if
condition|(
name|nodeId
operator|.
name|getTreeLevel
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|NodeId
name|siblingId
init|=
name|nodeId
operator|.
name|nextSibling
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
return|return
name|broker
operator|.
name|objectWith
argument_list|(
name|ownerDocument
argument_list|,
name|siblingId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Internal error while reading next sibling node: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : throw exception -pb
block|}
block|}
comment|// handle siblings of level 1+n
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
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|isDirty
argument_list|()
condition|)
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
specifier|final
name|int
name|parentLevel
init|=
name|parent
operator|.
name|getNodeId
argument_list|()
operator|.
name|getTreeLevel
argument_list|()
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
specifier|final
name|IEmbeddedXMLStreamReader
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
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
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
operator|&&
name|currentId
operator|.
name|compareTo
argument_list|(
name|nodeId
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|reader
operator|.
name|getNode
argument_list|()
return|;
block|}
if|else if
condition|(
name|status
operator|==
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|&&
name|currentId
operator|.
name|getTreeLevel
argument_list|()
operator|==
name|parentLevel
condition|)
block|{
comment|// reached the end of the parent element
break|break;
comment|// exit while loop
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|XMLStreamException
decl||
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
comment|//TODO : throw exception -pb
block|}
return|return
literal|null
return|;
block|}
specifier|final
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
name|IStoredNode
name|getLastNode
parameter_list|(
specifier|final
name|IStoredNode
name|node
parameter_list|)
block|{
comment|// only applicable to elements with children or attributes
if|if
condition|(
operator|!
operator|(
name|node
operator|.
name|hasChildNodes
argument_list|()
operator|||
name|node
operator|.
name|hasAttributes
argument_list|()
operator|)
condition|)
block|{
return|return
name|node
return|;
block|}
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
specifier|final
name|int
name|thisLevel
init|=
name|node
operator|.
name|getNodeId
argument_list|()
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
specifier|final
name|int
name|childLevel
init|=
name|thisLevel
operator|+
literal|1
decl_stmt|;
specifier|final
name|IEmbeddedXMLStreamReader
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
specifier|final
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|NodeId
name|otherId
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
specifier|final
name|int
name|otherLevel
init|=
name|otherId
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
comment|//NOTE(AR): The order of the checks below has been carefully chosen to optimize non-empty children, which is likely the most common case!
comment|// skip descendants
if|if
condition|(
name|otherLevel
operator|>
name|childLevel
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|status
operator|==
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|&&
name|otherLevel
operator|==
name|thisLevel
condition|)
block|{
comment|// we have finished scanning the children of the element...
break|break;
comment|// exit-while
block|}
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
specifier|final
name|IOException
decl||
name|XMLStreamException
decl||
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
comment|//TODO : throw exception -pb
block|}
return|return
literal|null
return|;
block|}
comment|//    protected StoredNode getLastNode(final Iterator<StoredNode> iterator, final StoredNode node) {
comment|//        if(!node.hasChildNodes()) {
comment|//            return node;
comment|//        }
comment|//        final int children = node.getChildCount();
comment|//        StoredNode next = null;
comment|//        for(int i = 0; i< children; i++) {
comment|//            next = iterator.next();
comment|//            //Recursivity helps taversing...
comment|//            next = getLastNode(iterator, next);
comment|//        }
comment|//        return next;
comment|//    }
annotation|@
name|Override
specifier|public
name|NodePath
name|getPath
parameter_list|()
block|{
specifier|final
name|NodePath2
name|path
init|=
operator|new
name|NodePath2
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
block|{
name|path
operator|.
name|addNode
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|Node
name|parent
decl_stmt|;
if|if
condition|(
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
condition|)
block|{
name|parent
operator|=
operator|(
operator|(
name|Attr
operator|)
name|this
operator|)
operator|.
name|getOwnerElement
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|=
name|getParentNode
argument_list|()
expr_stmt|;
block|}
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
name|addNode
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|parent
operator|=
name|parent
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
name|path
operator|.
name|reverseNodes
argument_list|()
expr_stmt|;
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodePath
name|getPath
parameter_list|(
specifier|final
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
block|{
name|parentPath
operator|.
name|addComponent
argument_list|(
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
return|return
name|nodeId
operator|.
name|toString
argument_list|()
operator|+
literal|'\t'
operator|+
name|getQName
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
specifier|final
name|boolean
name|top
parameter_list|)
block|{
return|return
name|toString
argument_list|()
return|;
block|}
comment|/**      * Release all memory resources hold by this node.      */
annotation|@
name|Override
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
specifier|final
name|NodeVisitor
name|visitor
parameter_list|)
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|;
specifier|final
name|INodeIterator
name|iterator
init|=
name|broker
operator|.
name|getNodeIterator
argument_list|(
name|this
argument_list|)
init|)
block|{
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
specifier|final
name|EXistException
decl||
name|IOException
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
comment|//TODO : throw exception -pb
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
specifier|final
name|INodeIterator
name|iterator
parameter_list|,
specifier|final
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
comment|//TODO iterator is not used here?
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|StoredNode
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
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
name|other
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
name|other
operator|.
name|ownerDocument
operator|.
name|getDocId
argument_list|()
condition|)
block|{
return|return
name|Constants
operator|.
name|INFERIOR
return|;
block|}
else|else
block|{
return|return
name|Constants
operator|.
name|SUPERIOR
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSameNode
parameter_list|(
specifier|final
name|Node
name|other
parameter_list|)
block|{
comment|// This function is used by Saxon in some circumstances, and is required for proper Saxon operation.
if|if
condition|(
name|other
operator|instanceof
name|IStoredNode
condition|)
block|{
return|return
operator|(
name|this
operator|.
name|nodeId
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|IStoredNode
argument_list|<
name|?
argument_list|>
operator|)
name|other
operator|)
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|&&
name|this
operator|.
name|ownerDocument
operator|.
name|getDocId
argument_list|()
operator|==
operator|(
operator|(
name|IStoredNode
argument_list|<
name|?
extends|extends
name|IStoredNode
argument_list|>
operator|)
name|other
operator|)
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|getDocId
argument_list|()
operator|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

