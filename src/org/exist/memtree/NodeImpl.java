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
name|memtree
package|;
end_package

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
name|NodeListImpl
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
name|QNameable
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
name|util
operator|.
name|serializer
operator|.
name|DOMStreamer
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
name|serializer
operator|.
name|DOMStreamerPool
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
name|serializer
operator|.
name|Receiver
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
name|NodeValue
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
name|StringValue
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
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
name|NamedNodeMap
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
name|UserDataHandler
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

begin_class
specifier|public
class|class
name|NodeImpl
implements|implements
name|Node
implements|,
name|NodeValue
implements|,
name|QNameable
implements|,
name|Comparable
block|{
specifier|public
specifier|final
specifier|static
name|short
name|REFERENCE_NODE
init|=
literal|100
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|short
name|NAMESPACE_NODE
init|=
literal|101
decl_stmt|;
specifier|protected
name|int
name|nodeNumber
decl_stmt|;
specifier|protected
name|DocumentImpl
name|document
decl_stmt|;
specifier|public
name|NodeImpl
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|int
name|nodeNumber
parameter_list|)
block|{
name|this
operator|.
name|document
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|nodeNumber
operator|=
name|nodeNumber
expr_stmt|;
block|}
specifier|public
name|int
name|getNodeNumber
parameter_list|()
block|{
return|return
name|nodeNumber
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NodeValue#getImplementation() 	 */
specifier|public
name|int
name|getImplementationType
parameter_list|()
block|{
return|return
name|NodeValue
operator|.
name|IN_MEMORY_NODE
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#getDocumentSet()      */
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
return|return
name|DocumentSet
operator|.
name|EMPTY_DOCUMENT_SET
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NodeValue#getNode() 	 */
specifier|public
name|Node
name|getNode
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNodeName() 	 */
specifier|public
name|String
name|getNodeName
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
literal|"#document"
return|;
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
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
name|nodeNumber
index|]
argument_list|)
decl_stmt|;
return|return
name|qn
operator|.
name|toString
argument_list|()
return|;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
return|return
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
name|nodeNumber
index|]
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|NodeImpl
operator|.
name|NAMESPACE_NODE
case|:
return|return
name|document
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|document
operator|.
name|namespaceCode
index|[
name|nodeNumber
index|]
argument_list|)
operator|.
name|toString
argument_list|()
return|;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
return|return
literal|"#text"
return|;
default|default :
return|return
literal|"#unknown"
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
name|ATTRIBUTE_NODE
case|:
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
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
name|nodeNumber
index|]
argument_list|)
decl_stmt|;
return|return
name|qn
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
name|TEXT_NODE
case|:
return|return
name|QName
operator|.
name|TEXT_QNAME
return|;
default|default :
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|void
name|expand
parameter_list|()
throws|throws
name|DOMException
block|{
name|document
operator|.
name|expand
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNodeValue() 	 */
specifier|public
name|String
name|getNodeValue
parameter_list|()
throws|throws
name|DOMException
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#setNodeValue(java.lang.String) 	 */
specifier|public
name|void
name|setNodeValue
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNodeType() 	 */
specifier|public
name|short
name|getNodeType
parameter_list|()
block|{
return|return
name|document
operator|.
name|nodeKind
index|[
name|nodeNumber
index|]
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getParentNode() 	 */
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
name|int
name|next
init|=
name|document
operator|.
name|next
index|[
name|nodeNumber
index|]
decl_stmt|;
while|while
condition|(
name|next
operator|>
name|nodeNumber
condition|)
block|{
name|next
operator|=
name|document
operator|.
name|next
index|[
name|next
index|]
expr_stmt|;
block|}
if|if
condition|(
name|next
operator|<
literal|0
condition|)
return|return
name|document
return|;
return|return
name|document
operator|.
name|getNode
argument_list|(
name|next
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#equals(java.lang.Object) 	 */
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
name|NodeImpl
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|nodeNumber
operator|==
operator|(
operator|(
name|NodeImpl
operator|)
name|obj
operator|)
operator|.
name|nodeNumber
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NodeValue#equals(org.exist.xquery.value.NodeValue) 	 */
specifier|public
name|boolean
name|equals
parameter_list|(
name|NodeValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"annot compare persistent node with in-memory node"
argument_list|)
throw|;
return|return
name|nodeNumber
operator|==
operator|(
operator|(
name|NodeImpl
operator|)
name|other
operator|)
operator|.
name|nodeNumber
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NodeValue#after(org.exist.xquery.value.NodeValue) 	 */
specifier|public
name|boolean
name|after
parameter_list|(
name|NodeValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"annot compare persistent node with in-memory node"
argument_list|)
throw|;
return|return
name|nodeNumber
operator|<
operator|(
operator|(
name|NodeImpl
operator|)
name|other
operator|)
operator|.
name|nodeNumber
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NodeValue#before(org.exist.xquery.value.NodeValue) 	 */
specifier|public
name|boolean
name|before
parameter_list|(
name|NodeValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"annot compare persistent node with in-memory node"
argument_list|)
throw|;
return|return
name|nodeNumber
operator|>
operator|(
operator|(
name|NodeImpl
operator|)
name|other
operator|)
operator|.
name|nodeNumber
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
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|NodeImpl
operator|)
condition|)
return|return
operator|-
literal|1
return|;
name|NodeImpl
name|n
init|=
operator|(
name|NodeImpl
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|document
operator|==
name|document
condition|)
block|{
if|if
condition|(
name|nodeNumber
operator|==
name|n
operator|.
name|nodeNumber
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|nodeNumber
operator|<
name|n
operator|.
name|nodeNumber
condition|)
return|return
literal|1
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getChildNodes() 	 */
specifier|public
name|NodeList
name|getChildNodes
parameter_list|()
block|{
return|return
operator|new
name|NodeListImpl
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getFirstChild() 	 */
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getLastChild() 	 */
specifier|public
name|Node
name|getLastChild
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getPreviousSibling() 	 */
specifier|public
name|Node
name|getPreviousSibling
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNextSibling() 	 */
specifier|public
name|Node
name|getNextSibling
parameter_list|()
block|{
name|int
name|nextNr
init|=
name|document
operator|.
name|next
index|[
name|nodeNumber
index|]
decl_stmt|;
return|return
name|nextNr
operator|<
name|nodeNumber
condition|?
literal|null
else|:
name|document
operator|.
name|getNode
argument_list|(
name|nextNr
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getAttributes() 	 */
specifier|public
name|NamedNodeMap
name|getAttributes
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getOwnerDocument() 	 */
specifier|public
name|Document
name|getOwnerDocument
parameter_list|()
block|{
return|return
name|document
return|;
block|}
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|document
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|insertBefore
parameter_list|(
name|Node
name|arg0
parameter_list|,
name|Node
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|replaceChild
parameter_list|(
name|Node
name|arg0
parameter_list|,
name|Node
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#removeChild(org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|removeChild
parameter_list|(
name|Node
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|appendChild
parameter_list|(
name|Node
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#hasChildNodes() 	 */
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#cloneNode(boolean) 	 */
specifier|public
name|Node
name|cloneNode
parameter_list|(
name|boolean
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#normalize() 	 */
specifier|public
name|void
name|normalize
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#isSupported(java.lang.String, java.lang.String) 	 */
specifier|public
name|boolean
name|isSupported
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
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getPrefix() 	 */
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#setPrefix(java.lang.String) 	 */
specifier|public
name|void
name|setPrefix
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getLocalName() 	 */
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#hasAttributes() 	 */
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/* 	 * Methods of interface Item 	 */
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
name|int
name|type
init|=
name|getNodeType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
return|return
name|Type
operator|.
name|DOCUMENT
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
default|default :
return|return
name|Type
operator|.
name|NODE
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
name|int
name|level
init|=
name|document
operator|.
name|treeLevel
index|[
name|nodeNumber
index|]
decl_stmt|;
name|StringBuffer
name|buf
init|=
literal|null
decl_stmt|;
name|int
name|next
init|=
name|nodeNumber
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|next
argument_list|<
name|document
operator|.
name|size
operator|&&
name|document
operator|.
name|treeLevel
index|[
name|next
index|]
argument_list|>
name|level
condition|)
block|{
if|if
condition|(
name|document
operator|.
name|nodeKind
index|[
name|next
index|]
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
name|buf
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|document
operator|.
name|characters
argument_list|,
name|document
operator|.
name|alpha
index|[
name|next
index|]
argument_list|,
name|document
operator|.
name|alphaLen
index|[
name|next
index|]
argument_list|)
expr_stmt|;
block|}
operator|++
name|next
expr_stmt|;
block|}
return|return
name|buf
operator|==
literal|null
condition|?
literal|""
else|:
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#toSequence() 	 */
specifier|public
name|Sequence
name|toSequence
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#convertTo(int) 	 */
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
name|getStringValue
argument_list|()
argument_list|)
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#atomize() 	 */
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
name|getStringValue
argument_list|()
argument_list|)
return|;
block|}
comment|/* 	 * Methods of interface Sequence 	 */
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#add(org.exist.xquery.value.Item) 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#addAll(org.exist.xquery.value.Sequence) 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getItemType() 	 */
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#iterate() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#unorderedIterator() 	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getCardinality() 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|EXACTLY_ONE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#itemAt(int) 	 */
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
operator|==
literal|0
condition|?
name|this
else|:
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#toNodeSet() 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
comment|//		throw new XPathException("Querying constructed nodes is not yet implemented");
name|ValueSequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|seq
operator|.
name|toNodeSet
argument_list|()
return|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|SingleNodeIterator
implements|implements
name|SequenceIterator
block|{
name|NodeImpl
name|node
decl_stmt|;
specifier|public
name|SingleNodeIterator
parameter_list|(
name|NodeImpl
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
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#hasNext() 		 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|node
operator|!=
literal|null
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#nextItem() 		 */
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
name|NodeImpl
name|next
init|=
name|node
decl_stmt|;
name|node
operator|=
literal|null
expr_stmt|;
return|return
name|next
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#toSAX(org.exist.storage.DBBroker, org.xml.sax.ContentHandler) 	 */
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
name|DOMStreamer
name|streamer
init|=
literal|null
decl_stmt|;
try|try
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
name|reset
argument_list|()
expr_stmt|;
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
name|setSAXHandlers
argument_list|(
name|handler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|streamer
operator|=
name|DOMStreamerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowDOMStreamer
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|serialize
argument_list|(
name|this
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|DOMStreamerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnDOMStreamer
argument_list|(
name|streamer
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentBuilderReceiver
name|receiver
parameter_list|)
throws|throws
name|SAXException
block|{
name|document
operator|.
name|copyTo
argument_list|(
name|this
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|streamTo
parameter_list|(
name|Serializer
name|serializer
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|SAXException
block|{
name|document
operator|.
name|streamTo
argument_list|(
name|serializer
argument_list|,
name|this
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#conversionPreference(java.lang.Class) 	 */
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
name|javaClass
parameter_list|)
block|{
if|if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|NodeImpl
operator|.
name|class
argument_list|)
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|Node
operator|.
name|class
argument_list|)
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|javaClass
operator|==
name|String
operator|.
name|class
operator|||
name|javaClass
operator|==
name|CharSequence
operator|.
name|class
condition|)
return|return
literal|2
return|;
if|if
condition|(
name|javaClass
operator|==
name|Character
operator|.
name|class
operator|||
name|javaClass
operator|==
name|char
operator|.
name|class
condition|)
return|return
literal|2
return|;
if|if
condition|(
name|javaClass
operator|==
name|Double
operator|.
name|class
operator|||
name|javaClass
operator|==
name|double
operator|.
name|class
condition|)
return|return
literal|10
return|;
if|if
condition|(
name|javaClass
operator|==
name|Float
operator|.
name|class
operator|||
name|javaClass
operator|==
name|float
operator|.
name|class
condition|)
return|return
literal|11
return|;
if|if
condition|(
name|javaClass
operator|==
name|Long
operator|.
name|class
operator|||
name|javaClass
operator|==
name|long
operator|.
name|class
condition|)
return|return
literal|12
return|;
if|if
condition|(
name|javaClass
operator|==
name|Integer
operator|.
name|class
operator|||
name|javaClass
operator|==
name|int
operator|.
name|class
condition|)
return|return
literal|13
return|;
if|if
condition|(
name|javaClass
operator|==
name|Short
operator|.
name|class
operator|||
name|javaClass
operator|==
name|short
operator|.
name|class
condition|)
return|return
literal|14
return|;
if|if
condition|(
name|javaClass
operator|==
name|Byte
operator|.
name|class
operator|||
name|javaClass
operator|==
name|byte
operator|.
name|class
condition|)
return|return
literal|15
return|;
if|if
condition|(
name|javaClass
operator|==
name|Boolean
operator|.
name|class
operator|||
name|javaClass
operator|==
name|boolean
operator|.
name|class
condition|)
return|return
literal|16
return|;
if|if
condition|(
name|javaClass
operator|==
name|Object
operator|.
name|class
condition|)
return|return
literal|20
return|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#toJavaObject(java.lang.Class) 	 */
specifier|public
name|Object
name|toJavaObject
parameter_list|(
name|Class
name|target
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|NodeImpl
operator|.
name|class
argument_list|)
condition|)
return|return
name|this
return|;
if|else if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|Node
operator|.
name|class
argument_list|)
condition|)
return|return
name|this
return|;
if|else if
condition|(
name|target
operator|==
name|Object
operator|.
name|class
condition|)
return|return
name|this
return|;
else|else
block|{
name|StringValue
name|v
init|=
operator|new
name|StringValue
argument_list|(
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|v
operator|.
name|toJavaObject
argument_list|(
name|target
argument_list|)
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#setSelfAsContext() 	 */
specifier|public
name|void
name|setSelfAsContext
parameter_list|()
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#isCached() 	 */
specifier|public
name|boolean
name|isCached
parameter_list|()
block|{
comment|// always return false
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#setIsCached(boolean) 	 */
specifier|public
name|void
name|setIsCached
parameter_list|(
name|boolean
name|cached
parameter_list|)
block|{
comment|// ignore
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#removeDuplicates()      */
specifier|public
name|void
name|removeDuplicates
parameter_list|()
block|{
comment|// do nothing: this is a single node
block|}
comment|/** ? @see org.w3c.dom.Node#getBaseURI() 	 */
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node) 	 */
specifier|public
name|short
name|compareDocumentPosition
parameter_list|(
name|Node
name|other
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|0
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#getTextContent() 	 */
specifier|public
name|String
name|getTextContent
parameter_list|()
throws|throws
name|DOMException
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#setTextContent(java.lang.String) 	 */
specifier|public
name|void
name|setTextContent
parameter_list|(
name|String
name|textContent
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
block|}
comment|/** ? @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node) 	 */
specifier|public
name|boolean
name|isSameNode
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|false
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#lookupPrefix(java.lang.String) 	 */
specifier|public
name|String
name|lookupPrefix
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String) 	 */
specifier|public
name|boolean
name|isDefaultNamespace
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|false
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String) 	 */
specifier|public
name|String
name|lookupNamespaceURI
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node) 	 */
specifier|public
name|boolean
name|isEqualNode
parameter_list|(
name|Node
name|arg
parameter_list|)
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|false
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#getFeature(java.lang.String, java.lang.String) 	 */
specifier|public
name|Object
name|getFeature
parameter_list|(
name|String
name|feature
parameter_list|,
name|String
name|version
parameter_list|)
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler) 	 */
specifier|public
name|Object
name|setUserData
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|data
parameter_list|,
name|UserDataHandler
name|handler
parameter_list|)
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#getUserData(java.lang.String) 	 */
specifier|public
name|Object
name|getUserData
parameter_list|(
name|String
name|key
parameter_list|)
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#isPersistentSet()      */
specifier|public
name|boolean
name|isPersistentSet
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

