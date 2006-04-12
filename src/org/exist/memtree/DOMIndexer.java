begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|memtree
package|;
end_package

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
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
name|*
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
name|CommentImpl
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
name|ElementImpl
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
name|ProcessingInstructionImpl
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
name|TextImpl
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

begin_comment
comment|/**  * Helper class to make a in-memory document fragment persistent.  * The class directly accesses the in-memory document structure and writes  * it into a temporary doc on the database. This is much faster than first serializing the  * document tree to SAX and passing it to   * {@link org.exist.collections.Collection#store(org.exist.storage.txn.Txn, org.exist.storage.DBBroker, org.exist.collections.IndexInfo, org.xml.sax.InputSource, boolean)}.  *   * As the in-memory document fragment may not be a well-formed XML doc (having more  * than one root element), a wrapper element is put around the content nodes.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DOMIndexer
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|ROOT_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"temp"
argument_list|,
name|Serializer
operator|.
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|Txn
name|transaction
decl_stmt|;
specifier|private
name|DocumentImpl
name|doc
decl_stmt|;
specifier|private
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
name|targetDoc
decl_stmt|;
specifier|private
name|Stack
name|stack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
name|TextImpl
name|text
init|=
operator|new
name|TextImpl
argument_list|()
decl_stmt|;
specifier|private
name|StoredNode
name|prevNode
init|=
literal|null
decl_stmt|;
specifier|public
name|DOMIndexer
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
name|targetDoc
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|transaction
operator|=
name|transaction
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|targetDoc
operator|=
name|targetDoc
expr_stmt|;
block|}
comment|/**      * Scan the DOM tree once to determine its structure.      *       * @throws EXistException      */
specifier|public
name|void
name|scan
parameter_list|()
throws|throws
name|EXistException
block|{
comment|//Creates a dummy DOCTYPE
specifier|final
name|DocumentTypeImpl
name|dt
init|=
operator|new
name|DocumentTypeImpl
argument_list|(
literal|"temp"
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|targetDoc
operator|.
name|setDocumentType
argument_list|(
name|dt
argument_list|)
expr_stmt|;
name|targetDoc
operator|.
name|setTreeLevelOrder
argument_list|(
literal|1
argument_list|,
name|doc
operator|.
name|getChildCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|doc
operator|.
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|treeLevel
index|[
name|i
index|]
operator|+
literal|1
operator|>
name|targetDoc
operator|.
name|getMaxDepth
argument_list|()
condition|)
name|targetDoc
operator|.
name|setMaxDepth
argument_list|(
name|doc
operator|.
name|treeLevel
index|[
name|i
index|]
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|nodeKind
index|[
name|i
index|]
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|int
name|length
init|=
name|doc
operator|.
name|getChildCountFor
argument_list|(
name|i
argument_list|)
operator|+
name|doc
operator|.
name|getAttributesCountFor
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|>
name|targetDoc
operator|.
name|getTreeLevelOrder
argument_list|(
name|doc
operator|.
name|treeLevel
index|[
name|i
index|]
operator|+
literal|1
argument_list|)
condition|)
name|targetDoc
operator|.
name|setTreeLevelOrder
argument_list|(
name|doc
operator|.
name|treeLevel
index|[
name|i
index|]
operator|+
literal|1
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|// increase computed max depth by one
name|targetDoc
operator|.
name|setMaxDepth
argument_list|(
name|targetDoc
operator|.
name|getMaxDepth
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|targetDoc
operator|.
name|calculateTreeLevelStartPoints
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Store the nodes.      *      */
specifier|public
name|void
name|store
parameter_list|()
block|{
comment|// create a wrapper element as root node
name|ElementImpl
name|elem
init|=
operator|new
name|ElementImpl
argument_list|(
literal|1
argument_list|,
name|ROOT_QNAME
argument_list|)
decl_stmt|;
name|elem
operator|.
name|setNodeId
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createInstance
argument_list|()
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setOwnerDocument
argument_list|(
name|targetDoc
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setChildCount
argument_list|(
name|doc
operator|.
name|getChildCount
argument_list|()
argument_list|)
expr_stmt|;
name|elem
operator|.
name|addNamespaceMapping
argument_list|(
literal|"exist"
argument_list|,
name|Serializer
operator|.
name|EXIST_NS
argument_list|)
expr_stmt|;
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
name|path
operator|.
name|addComponent
argument_list|(
name|ROOT_QNAME
argument_list|)
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|elem
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|elem
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|targetDoc
operator|.
name|appendChild
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setChildCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// store the document nodes
name|int
name|top
init|=
name|doc
operator|.
name|size
operator|>
literal|1
condition|?
literal|1
else|:
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|top
operator|>
literal|0
condition|)
block|{
name|store
argument_list|(
name|top
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|top
operator|=
name|doc
operator|.
name|getNextSiblingFor
argument_list|(
name|top
argument_list|)
expr_stmt|;
block|}
comment|// close the wrapper element
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|endElement
argument_list|(
name|elem
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|path
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|store
parameter_list|(
name|int
name|top
parameter_list|,
name|NodePath
name|currentPath
parameter_list|)
block|{
name|int
name|nodeNr
init|=
name|top
decl_stmt|;
while|while
condition|(
name|nodeNr
operator|>
literal|0
condition|)
block|{
name|startNode
argument_list|(
name|nodeNr
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
name|int
name|nextNode
init|=
name|doc
operator|.
name|getFirstChildFor
argument_list|(
name|nodeNr
argument_list|)
decl_stmt|;
while|while
condition|(
name|nextNode
operator|==
operator|-
literal|1
condition|)
block|{
name|endNode
argument_list|(
name|nodeNr
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|top
operator|==
name|nodeNr
condition|)
break|break;
name|nextNode
operator|=
name|doc
operator|.
name|getNextSiblingFor
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextNode
operator|==
operator|-
literal|1
condition|)
block|{
name|nodeNr
operator|=
name|doc
operator|.
name|getParentNodeFor
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeNr
operator|==
operator|-
literal|1
operator|||
name|top
operator|==
name|nodeNr
condition|)
block|{
name|endNode
argument_list|(
name|nodeNr
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
name|nextNode
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
block|}
block|}
block|}
name|nodeNr
operator|=
name|nextNode
expr_stmt|;
block|}
block|}
comment|/**      * @param nodeNr      */
specifier|private
name|void
name|startNode
parameter_list|(
name|int
name|nodeNr
parameter_list|,
name|NodePath
name|currentPath
parameter_list|)
block|{
if|if
condition|(
name|doc
operator|.
name|nodeKind
index|[
name|nodeNr
index|]
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|ElementImpl
name|elem
init|=
operator|new
name|ElementImpl
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|stack
operator|.
name|empty
argument_list|()
condition|)
block|{
name|elem
operator|.
name|setNodeId
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createInstance
argument_list|()
argument_list|)
expr_stmt|;
name|initElement
argument_list|(
name|nodeNr
argument_list|,
name|elem
argument_list|)
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|elem
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
name|targetDoc
operator|.
name|appendChild
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setChildCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ElementImpl
name|last
init|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
name|initElement
argument_list|(
name|nodeNr
argument_list|,
name|elem
argument_list|)
expr_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|prevNode
argument_list|,
name|elem
argument_list|)
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|elem
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setChildCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|prevNode
operator|=
literal|null
expr_stmt|;
name|currentPath
operator|.
name|addComponent
argument_list|(
name|elem
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
name|storeAttributes
argument_list|(
name|nodeNr
argument_list|,
name|elem
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|doc
operator|.
name|nodeKind
index|[
name|nodeNr
index|]
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
name|ElementImpl
name|last
init|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
name|text
operator|.
name|setData
argument_list|(
operator|new
name|String
argument_list|(
name|doc
operator|.
name|characters
argument_list|,
name|doc
operator|.
name|alpha
index|[
name|nodeNr
index|]
argument_list|,
name|doc
operator|.
name|alphaLen
index|[
name|nodeNr
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|targetDoc
argument_list|)
expr_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|prevNode
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|prevNode
operator|=
name|text
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|text
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|text
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|doc
operator|.
name|nodeKind
index|[
name|nodeNr
index|]
operator|==
name|Node
operator|.
name|COMMENT_NODE
condition|)
block|{
name|CommentImpl
name|comment
init|=
operator|new
name|CommentImpl
argument_list|(
operator|new
name|String
argument_list|(
name|doc
operator|.
name|characters
argument_list|,
name|doc
operator|.
name|alpha
index|[
name|nodeNr
index|]
argument_list|,
name|doc
operator|.
name|alphaLen
index|[
name|nodeNr
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|comment
operator|.
name|setOwnerDocument
argument_list|(
name|targetDoc
argument_list|)
expr_stmt|;
if|if
condition|(
name|stack
operator|.
name|empty
argument_list|()
condition|)
block|{
name|comment
operator|.
name|setGID
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|comment
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|targetDoc
operator|.
name|appendChild
argument_list|(
name|comment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ElementImpl
name|last
init|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|prevNode
argument_list|,
name|comment
argument_list|)
expr_stmt|;
name|prevNode
operator|=
name|comment
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|comment
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|doc
operator|.
name|nodeKind
index|[
name|nodeNr
index|]
operator|==
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
condition|)
block|{
name|ProcessingInstructionImpl
name|pi
init|=
operator|new
name|ProcessingInstructionImpl
argument_list|()
decl_stmt|;
name|pi
operator|.
name|setOwnerDocument
argument_list|(
name|targetDoc
argument_list|)
expr_stmt|;
name|QName
name|qn
init|=
operator|(
name|QName
operator|)
name|doc
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|doc
operator|.
name|nodeName
index|[
name|nodeNr
index|]
argument_list|)
decl_stmt|;
name|pi
operator|.
name|setTarget
argument_list|(
name|qn
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|pi
operator|.
name|setData
argument_list|(
operator|new
name|String
argument_list|(
name|doc
operator|.
name|characters
argument_list|,
name|doc
operator|.
name|alpha
index|[
name|nodeNr
index|]
argument_list|,
name|doc
operator|.
name|alphaLen
index|[
name|nodeNr
index|]
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|stack
operator|.
name|empty
argument_list|()
condition|)
block|{
name|pi
operator|.
name|setGID
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|pi
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|targetDoc
operator|.
name|appendChild
argument_list|(
name|pi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ElementImpl
name|last
init|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
name|last
operator|.
name|appendChildInternal
argument_list|(
name|prevNode
argument_list|,
name|pi
argument_list|)
expr_stmt|;
name|prevNode
operator|=
name|pi
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|pi
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @param nodeNr      * @param elem      */
specifier|private
name|void
name|initElement
parameter_list|(
name|int
name|nodeNr
parameter_list|,
name|ElementImpl
name|elem
parameter_list|)
block|{
name|short
name|attribs
init|=
operator|(
name|short
operator|)
name|doc
operator|.
name|getAttributesCountFor
argument_list|(
name|nodeNr
argument_list|)
decl_stmt|;
name|elem
operator|.
name|setOwnerDocument
argument_list|(
name|targetDoc
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setAttributes
argument_list|(
name|attribs
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setChildCount
argument_list|(
name|doc
operator|.
name|getChildCountFor
argument_list|(
name|nodeNr
argument_list|)
operator|+
name|attribs
argument_list|)
expr_stmt|;
name|elem
operator|.
name|setNodeName
argument_list|(
operator|(
name|QName
operator|)
name|doc
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|doc
operator|.
name|nodeName
index|[
name|nodeNr
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|ns
init|=
name|getNamespaces
argument_list|(
name|nodeNr
argument_list|)
decl_stmt|;
if|if
condition|(
name|ns
operator|!=
literal|null
condition|)
name|elem
operator|.
name|setNamespaceMappings
argument_list|(
name|ns
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Map
name|getNamespaces
parameter_list|(
name|int
name|nodeNr
parameter_list|)
block|{
name|int
name|ns
init|=
name|doc
operator|.
name|alphaLen
index|[
name|nodeNr
index|]
decl_stmt|;
if|if
condition|(
name|ns
operator|<
literal|0
condition|)
return|return
literal|null
return|;
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
while|while
condition|(
name|ns
operator|<
name|doc
operator|.
name|nextNamespace
operator|&&
name|doc
operator|.
name|namespaceParent
index|[
name|ns
index|]
operator|==
name|nodeNr
condition|)
block|{
name|QName
name|qn
init|=
operator|(
name|QName
operator|)
name|doc
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|doc
operator|.
name|namespaceCode
index|[
name|ns
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"xmlns"
operator|.
name|equals
argument_list|(
name|qn
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
name|map
operator|.
name|put
argument_list|(
literal|""
argument_list|,
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|map
operator|.
name|put
argument_list|(
name|qn
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|ns
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/**      * @param nodeNr      * @param elem      * @throws DOMException      */
specifier|private
name|void
name|storeAttributes
parameter_list|(
name|int
name|nodeNr
parameter_list|,
name|ElementImpl
name|elem
parameter_list|,
name|NodePath
name|path
parameter_list|)
throws|throws
name|DOMException
block|{
name|int
name|attr
init|=
name|doc
operator|.
name|alpha
index|[
name|nodeNr
index|]
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|<
name|attr
condition|)
block|{
while|while
condition|(
name|attr
operator|<
name|doc
operator|.
name|nextAttr
operator|&&
name|doc
operator|.
name|attrParent
index|[
name|attr
index|]
operator|==
name|nodeNr
condition|)
block|{
name|QName
name|qn
init|=
operator|(
name|QName
operator|)
name|doc
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|doc
operator|.
name|attrName
index|[
name|attr
index|]
argument_list|)
decl_stmt|;
name|AttrImpl
name|attrib
init|=
operator|new
name|AttrImpl
argument_list|(
name|qn
argument_list|,
name|doc
operator|.
name|attrValue
index|[
name|attr
index|]
argument_list|)
decl_stmt|;
name|attrib
operator|.
name|setOwnerDocument
argument_list|(
name|targetDoc
argument_list|)
expr_stmt|;
name|elem
operator|.
name|appendChildInternal
argument_list|(
name|prevNode
argument_list|,
name|attrib
argument_list|)
expr_stmt|;
name|prevNode
operator|=
name|attrib
expr_stmt|;
name|broker
operator|.
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|attrib
argument_list|,
name|path
argument_list|)
expr_stmt|;
operator|++
name|attr
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @param nodeNr      */
specifier|private
name|void
name|endNode
parameter_list|(
name|int
name|nodeNr
parameter_list|,
name|NodePath
name|currentPath
parameter_list|)
block|{
if|if
condition|(
name|doc
operator|.
name|nodeKind
index|[
name|nodeNr
index|]
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|ElementImpl
name|last
init|=
operator|(
name|ElementImpl
operator|)
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|broker
operator|.
name|endElement
argument_list|(
name|last
argument_list|,
name|currentPath
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|currentPath
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
name|prevNode
operator|=
name|last
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

