begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-03 Wolfgang M. Meier  * wolfgang@exist-db.org http://exist.sourceforge.net  *   * This program is free software; you can redistribute it and/or modify it under  * the terms of the GNU Lesser General Public License as published by the Free  * Software Foundation; either version 2 of the License, or (at your option) any  * later version.  *   * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more  * details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation, Inc.,  * 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
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
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|dom
operator|.
name|AttrNSImpl
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
name|QName
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
name|NamePool
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
name|CDATASection
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
name|Comment
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
name|DOMImplementation
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
name|DocumentFragment
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
name|DocumentType
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
name|Element
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
name|EntityReference
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|ProcessingInstruction
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
name|Text
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
comment|/**  * An in-memory implementation of Document.  *   * This implementation stores all node data in the document object. Nodes from  * another document, i.e. a persistent document in the database, can be stored  * as reference nodes, i.e. the nodes are not copied into this document object.  * Instead a reference is inserted which will only be expanded during  * serialization.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DocumentImpl
extends|extends
name|NodeImpl
implements|implements
name|Document
block|{
specifier|protected
name|NamePool
name|namePool
init|=
operator|new
name|NamePool
argument_list|()
decl_stmt|;
specifier|protected
name|short
index|[]
name|nodeKind
init|=
literal|null
decl_stmt|;
specifier|protected
name|short
index|[]
name|treeLevel
decl_stmt|;
specifier|protected
name|int
index|[]
name|next
decl_stmt|;
specifier|protected
name|int
index|[]
name|nodeName
decl_stmt|;
specifier|protected
name|int
index|[]
name|alpha
decl_stmt|;
specifier|protected
name|int
index|[]
name|alphaLen
decl_stmt|;
specifier|protected
name|char
index|[]
name|characters
decl_stmt|;
specifier|protected
name|int
name|nextChar
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
index|[]
name|attrName
decl_stmt|;
specifier|protected
name|int
index|[]
name|attrParent
decl_stmt|;
specifier|protected
name|String
index|[]
name|attrValue
decl_stmt|;
specifier|protected
name|int
name|nextAttr
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|size
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|documentRootNode
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|NodeProxy
name|references
index|[]
decl_stmt|;
specifier|protected
name|int
name|nextRef
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|NODE_SIZE
init|=
literal|128
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|ATTR_SIZE
init|=
literal|64
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|CHAR_BUF_SIZE
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|REF_SIZE
init|=
literal|128
decl_stmt|;
specifier|public
name|DocumentImpl
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|init
parameter_list|()
block|{
name|nodeKind
operator|=
operator|new
name|short
index|[
name|NODE_SIZE
index|]
expr_stmt|;
name|treeLevel
operator|=
operator|new
name|short
index|[
name|NODE_SIZE
index|]
expr_stmt|;
name|next
operator|=
operator|new
name|int
index|[
name|NODE_SIZE
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|next
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|nodeName
operator|=
operator|new
name|int
index|[
name|NODE_SIZE
index|]
expr_stmt|;
name|alpha
operator|=
operator|new
name|int
index|[
name|NODE_SIZE
index|]
expr_stmt|;
name|alphaLen
operator|=
operator|new
name|int
index|[
name|NODE_SIZE
index|]
expr_stmt|;
name|characters
operator|=
operator|new
name|char
index|[
name|CHAR_BUF_SIZE
index|]
expr_stmt|;
name|attrName
operator|=
operator|new
name|int
index|[
name|ATTR_SIZE
index|]
expr_stmt|;
name|attrParent
operator|=
operator|new
name|int
index|[
name|ATTR_SIZE
index|]
expr_stmt|;
name|attrValue
operator|=
operator|new
name|String
index|[
name|ATTR_SIZE
index|]
expr_stmt|;
name|references
operator|=
operator|new
name|NodeProxy
index|[
name|REF_SIZE
index|]
expr_stmt|;
name|treeLevel
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
name|nodeKind
index|[
literal|0
index|]
operator|=
name|Node
operator|.
name|DOCUMENT_NODE
expr_stmt|;
name|document
operator|=
name|this
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|size
operator|=
literal|0
expr_stmt|;
name|nextChar
operator|=
literal|0
expr_stmt|;
name|nextAttr
operator|=
literal|0
expr_stmt|;
name|nextRef
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|int
name|addNode
parameter_list|(
name|short
name|kind
parameter_list|,
name|short
name|level
parameter_list|,
name|QName
name|qname
parameter_list|)
block|{
if|if
condition|(
name|nodeKind
operator|==
literal|null
condition|)
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
operator|==
name|nodeKind
operator|.
name|length
condition|)
name|grow
argument_list|()
expr_stmt|;
name|nodeKind
index|[
name|size
index|]
operator|=
name|kind
expr_stmt|;
name|treeLevel
index|[
name|size
index|]
operator|=
name|level
expr_stmt|;
name|nodeName
index|[
name|size
index|]
operator|=
operator|(
name|qname
operator|!=
literal|null
condition|?
name|namePool
operator|.
name|add
argument_list|(
name|qname
argument_list|)
else|:
operator|-
literal|1
operator|)
expr_stmt|;
name|alpha
index|[
name|size
index|]
operator|=
operator|-
literal|1
expr_stmt|;
comment|// undefined
name|next
index|[
name|size
index|]
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|size
operator|++
return|;
block|}
specifier|public
name|void
name|addChars
parameter_list|(
name|int
name|nodeNr
parameter_list|,
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|nodeKind
operator|==
literal|null
condition|)
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextChar
operator|+
name|len
operator|>=
name|characters
operator|.
name|length
condition|)
block|{
name|int
name|newLen
init|=
operator|(
name|characters
operator|.
name|length
operator|*
literal|3
operator|)
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|newLen
operator|<
name|nextChar
operator|+
name|len
condition|)
name|newLen
operator|=
name|nextChar
operator|+
name|len
expr_stmt|;
name|char
index|[]
name|nc
init|=
operator|new
name|char
index|[
name|newLen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|characters
argument_list|,
literal|0
argument_list|,
name|nc
argument_list|,
literal|0
argument_list|,
name|characters
operator|.
name|length
argument_list|)
expr_stmt|;
name|characters
operator|=
name|nc
expr_stmt|;
block|}
name|alpha
index|[
name|nodeNr
index|]
operator|=
name|nextChar
expr_stmt|;
name|alphaLen
index|[
name|nodeNr
index|]
operator|=
name|len
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|characters
argument_list|,
name|nextChar
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|nextChar
operator|+=
name|len
expr_stmt|;
block|}
specifier|public
name|void
name|addChars
parameter_list|(
name|int
name|nodeNr
parameter_list|,
name|CharSequence
name|s
parameter_list|)
block|{
if|if
condition|(
name|nodeKind
operator|==
literal|null
condition|)
name|init
argument_list|()
expr_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextChar
operator|+
name|len
operator|>=
name|characters
operator|.
name|length
condition|)
block|{
name|int
name|newLen
init|=
operator|(
name|characters
operator|.
name|length
operator|*
literal|3
operator|)
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|newLen
operator|<
name|nextChar
operator|+
name|len
condition|)
name|newLen
operator|=
name|nextChar
operator|+
name|len
expr_stmt|;
name|char
index|[]
name|nc
init|=
operator|new
name|char
index|[
name|newLen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|characters
argument_list|,
literal|0
argument_list|,
name|nc
argument_list|,
literal|0
argument_list|,
name|characters
operator|.
name|length
argument_list|)
expr_stmt|;
name|characters
operator|=
name|nc
expr_stmt|;
block|}
name|alpha
index|[
name|nodeNr
index|]
operator|=
name|nextChar
expr_stmt|;
name|alphaLen
index|[
name|nodeNr
index|]
operator|=
name|len
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|characters
index|[
name|nextChar
operator|++
index|]
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addReferenceNode
parameter_list|(
name|int
name|nodeNr
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
block|{
if|if
condition|(
name|nodeKind
operator|==
literal|null
condition|)
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextRef
operator|==
name|references
operator|.
name|length
condition|)
name|growReferences
argument_list|()
expr_stmt|;
name|references
index|[
name|nextRef
index|]
operator|=
name|proxy
expr_stmt|;
name|alpha
index|[
name|nodeNr
index|]
operator|=
name|nextRef
operator|++
expr_stmt|;
block|}
specifier|public
name|int
name|addAttribute
parameter_list|(
name|int
name|nodeNr
parameter_list|,
name|QName
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|nodeKind
operator|==
literal|null
condition|)
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextAttr
operator|==
name|attrName
operator|.
name|length
condition|)
name|growAttributes
argument_list|()
expr_stmt|;
name|attrParent
index|[
name|nextAttr
index|]
operator|=
name|nodeNr
expr_stmt|;
name|attrName
index|[
name|nextAttr
index|]
operator|=
name|namePool
operator|.
name|add
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|attrValue
index|[
name|nextAttr
index|]
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|alpha
index|[
name|nodeNr
index|]
operator|<
literal|0
condition|)
name|alpha
index|[
name|nodeNr
index|]
operator|=
name|nextAttr
expr_stmt|;
return|return
name|nextAttr
operator|++
return|;
block|}
specifier|public
name|int
name|getLastNode
parameter_list|()
block|{
return|return
name|size
operator|-
literal|1
return|;
block|}
specifier|private
name|void
name|grow
parameter_list|()
block|{
name|int
name|newSize
init|=
operator|(
name|size
operator|*
literal|3
operator|)
operator|/
literal|2
decl_stmt|;
name|short
index|[]
name|newNodeKind
init|=
operator|new
name|short
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|nodeKind
argument_list|,
literal|0
argument_list|,
name|newNodeKind
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|nodeKind
operator|=
name|newNodeKind
expr_stmt|;
name|short
index|[]
name|newTreeLevel
init|=
operator|new
name|short
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|treeLevel
argument_list|,
literal|0
argument_list|,
name|newTreeLevel
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|treeLevel
operator|=
name|newTreeLevel
expr_stmt|;
name|int
index|[]
name|newNext
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|newNext
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|next
argument_list|,
literal|0
argument_list|,
name|newNext
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|next
operator|=
name|newNext
expr_stmt|;
name|int
index|[]
name|newNodeName
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|nodeName
argument_list|,
literal|0
argument_list|,
name|newNodeName
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|nodeName
operator|=
name|newNodeName
expr_stmt|;
name|int
index|[]
name|newAlpha
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|alpha
argument_list|,
literal|0
argument_list|,
name|newAlpha
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|alpha
operator|=
name|newAlpha
expr_stmt|;
name|int
index|[]
name|newAlphaLen
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|alphaLen
argument_list|,
literal|0
argument_list|,
name|newAlphaLen
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|alphaLen
operator|=
name|newAlphaLen
expr_stmt|;
block|}
specifier|private
name|void
name|growAttributes
parameter_list|()
block|{
name|int
name|size
init|=
name|attrName
operator|.
name|length
decl_stmt|;
name|int
name|newSize
init|=
operator|(
name|size
operator|*
literal|3
operator|)
operator|/
literal|2
decl_stmt|;
name|int
index|[]
name|newAttrName
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|attrName
argument_list|,
literal|0
argument_list|,
name|newAttrName
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|attrName
operator|=
name|newAttrName
expr_stmt|;
name|int
index|[]
name|newAttrParent
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|attrParent
argument_list|,
literal|0
argument_list|,
name|newAttrParent
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|attrParent
operator|=
name|newAttrParent
expr_stmt|;
name|String
index|[]
name|newAttrValue
init|=
operator|new
name|String
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|attrValue
argument_list|,
literal|0
argument_list|,
name|newAttrValue
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|attrValue
operator|=
name|newAttrValue
expr_stmt|;
block|}
specifier|private
name|void
name|growReferences
parameter_list|()
block|{
name|int
name|size
init|=
name|references
operator|.
name|length
decl_stmt|;
name|int
name|newSize
init|=
operator|(
name|size
operator|*
literal|3
operator|)
operator|/
literal|2
decl_stmt|;
name|NodeProxy
name|newReferences
index|[]
init|=
operator|new
name|NodeProxy
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|references
argument_list|,
literal|0
argument_list|,
name|newReferences
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|references
operator|=
name|newReferences
expr_stmt|;
block|}
specifier|public
name|NodeImpl
name|getAttribute
parameter_list|(
name|int
name|nodeNr
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
operator|new
name|AttributeImpl
argument_list|(
name|this
argument_list|,
name|nodeNr
argument_list|)
return|;
block|}
specifier|public
name|NodeImpl
name|getNode
parameter_list|(
name|int
name|nodeNr
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|nodeNr
operator|==
literal|0
condition|)
return|return
name|this
return|;
if|if
condition|(
name|nodeNr
operator|>=
name|size
condition|)
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|HIERARCHY_REQUEST_ERR
argument_list|,
literal|"node not found"
argument_list|)
throw|;
name|NodeImpl
name|node
decl_stmt|;
switch|switch
condition|(
name|nodeKind
index|[
name|nodeNr
index|]
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|node
operator|=
operator|new
name|ElementImpl
argument_list|(
name|this
argument_list|,
name|nodeNr
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
name|node
operator|=
operator|new
name|TextImpl
argument_list|(
name|this
argument_list|,
name|nodeNr
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
name|node
operator|=
operator|new
name|CommentImpl
argument_list|(
name|this
argument_list|,
name|nodeNr
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
name|node
operator|=
operator|new
name|ProcessingInstructionImpl
argument_list|(
name|this
argument_list|,
name|nodeNr
argument_list|)
expr_stmt|;
break|break;
case|case
name|NodeImpl
operator|.
name|REFERENCE_NODE
case|:
name|node
operator|=
operator|new
name|ReferenceNode
argument_list|(
name|this
argument_list|,
name|nodeNr
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_FOUND_ERR
argument_list|,
literal|"node not found"
argument_list|)
throw|;
block|}
return|return
name|node
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Node#getParentNode()      */
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#getDoctype()      */
specifier|public
name|DocumentType
name|getDoctype
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#getImplementation()      */
specifier|public
name|DOMImplementation
name|getImplementation
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#getDocumentElement()      */
specifier|public
name|Element
name|getDocumentElement
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
literal|1
condition|)
return|return
literal|null
return|;
name|int
name|nodeNr
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|nodeKind
index|[
name|nodeNr
index|]
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
if|if
condition|(
name|next
index|[
name|nodeNr
index|]
operator|<
name|nodeNr
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
name|nodeNr
operator|=
name|next
index|[
name|nodeNr
index|]
expr_stmt|;
block|}
return|return
operator|(
name|Element
operator|)
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Node#getFirstChild()      */
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
if|if
condition|(
name|size
operator|>
literal|1
condition|)
return|return
name|getNode
argument_list|(
literal|1
argument_list|)
return|;
else|else
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createElement(java.lang.String)      */
specifier|public
name|Element
name|createElement
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createDocumentFragment()      */
specifier|public
name|DocumentFragment
name|createDocumentFragment
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createTextNode(java.lang.String)      */
specifier|public
name|Text
name|createTextNode
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createComment(java.lang.String)      */
specifier|public
name|Comment
name|createComment
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createCDATASection(java.lang.String)      */
specifier|public
name|CDATASection
name|createCDATASection
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createProcessingInstruction(java.lang.String,      *           java.lang.String)      */
specifier|public
name|ProcessingInstruction
name|createProcessingInstruction
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createAttribute(java.lang.String)      */
specifier|public
name|Attr
name|createAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createEntityReference(java.lang.String)      */
specifier|public
name|EntityReference
name|createEntityReference
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#getElementsByTagName(java.lang.String)      */
specifier|public
name|NodeList
name|getElementsByTagName
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#importNode(org.w3c.dom.Node, boolean)      */
specifier|public
name|Node
name|importNode
parameter_list|(
name|Node
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createElementNS(java.lang.String,      *           java.lang.String)      */
specifier|public
name|Element
name|createElementNS
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#createAttributeNS(java.lang.String,      *           java.lang.String)      */
specifier|public
name|Attr
name|createAttributeNS
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#getElementsByTagNameNS(java.lang.String,      *           java.lang.String)      */
specifier|public
name|NodeList
name|getElementsByTagNameNS
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Document#getElementById(java.lang.String)      */
specifier|public
name|Element
name|getElementById
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.w3c.dom.Node#getOwnerDocument()      */
specifier|public
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|getOwnerDocument
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/**      * Copy the document fragment starting at the specified node to the given      * receiver.      *       * @param node      * @param receiver      */
specifier|public
name|void
name|copyTo
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|SAXException
block|{
name|NodeImpl
name|top
init|=
name|node
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|startNode
argument_list|(
name|node
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
name|NodeImpl
name|nextNode
init|=
operator|(
name|NodeImpl
operator|)
name|node
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextNode
operator|==
literal|null
condition|)
block|{
name|endNode
argument_list|(
name|node
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
if|if
condition|(
name|top
operator|!=
literal|null
operator|&&
name|top
operator|.
name|nodeNumber
operator|==
name|node
operator|.
name|nodeNumber
condition|)
break|break;
name|nextNode
operator|=
operator|(
name|NodeImpl
operator|)
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextNode
operator|==
literal|null
condition|)
block|{
name|node
operator|=
operator|(
name|NodeImpl
operator|)
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
operator|||
operator|(
name|top
operator|!=
literal|null
operator|&&
name|top
operator|.
name|nodeNumber
operator|==
name|node
operator|.
name|nodeNumber
operator|)
condition|)
block|{
name|endNode
argument_list|(
name|node
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
name|nextNode
operator|=
literal|null
expr_stmt|;
break|break;
block|}
block|}
block|}
name|node
operator|=
name|nextNode
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|startNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|SAXException
block|{
name|int
name|nr
init|=
name|node
operator|.
name|nodeNumber
decl_stmt|;
switch|switch
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|QName
name|nodeName
init|=
operator|(
name|QName
operator|)
name|document
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|document
operator|.
name|nodeName
index|[
name|nr
index|]
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|startElement
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|int
name|attr
init|=
name|document
operator|.
name|alpha
index|[
name|nr
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
name|document
operator|.
name|nextAttr
operator|&&
name|document
operator|.
name|attrParent
index|[
name|attr
index|]
operator|==
name|nr
condition|)
block|{
name|QName
name|attrQName
init|=
operator|(
name|QName
operator|)
name|document
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|document
operator|.
name|attrName
index|[
name|attr
index|]
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|attribute
argument_list|(
name|attrQName
argument_list|,
name|attrValue
index|[
name|attr
index|]
argument_list|)
expr_stmt|;
operator|++
name|attr
expr_stmt|;
block|}
block|}
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
name|receiver
operator|.
name|characters
argument_list|(
name|document
operator|.
name|characters
argument_list|,
name|document
operator|.
name|alpha
index|[
name|nr
index|]
argument_list|,
name|document
operator|.
name|alphaLen
index|[
name|nr
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
name|QName
name|attrQName
init|=
operator|(
name|QName
operator|)
name|document
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|document
operator|.
name|attrName
index|[
name|nr
index|]
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|attribute
argument_list|(
name|attrQName
argument_list|,
name|attrValue
index|[
name|nr
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
name|receiver
operator|.
name|comment
argument_list|(
name|document
operator|.
name|characters
argument_list|,
name|document
operator|.
name|alpha
index|[
name|nr
index|]
argument_list|,
name|document
operator|.
name|alphaLen
index|[
name|nr
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
name|QName
name|qn
init|=
operator|(
name|QName
operator|)
name|document
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|document
operator|.
name|nodeName
index|[
name|nr
index|]
argument_list|)
decl_stmt|;
name|String
name|data
init|=
operator|new
name|String
argument_list|(
name|document
operator|.
name|characters
argument_list|,
name|document
operator|.
name|alpha
index|[
name|nr
index|]
argument_list|,
name|document
operator|.
name|alphaLen
index|[
name|nr
index|]
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|processingInstruction
argument_list|(
name|qn
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
name|NodeImpl
operator|.
name|REFERENCE_NODE
case|:
name|receiver
operator|.
name|addReferenceNode
argument_list|(
name|document
operator|.
name|references
index|[
name|document
operator|.
name|alpha
index|[
name|nr
index|]
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
specifier|private
name|void
name|endNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
name|receiver
operator|.
name|endElement
argument_list|(
name|node
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

