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
name|NamedNodeMapImpl
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
name|TypeInfo
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
name|ElementImpl
extends|extends
name|NodeImpl
implements|implements
name|Element
implements|,
name|QNameable
block|{
specifier|public
name|ElementImpl
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|int
name|nodeNumber
parameter_list|)
block|{
name|super
argument_list|(
name|doc
argument_list|,
name|nodeNumber
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#getTagName() 	 */
specifier|public
name|String
name|getTagName
parameter_list|()
block|{
return|return
name|getNodeName
argument_list|()
return|;
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
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
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#hasChildNodes() 	 */
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
name|nodeNumber
operator|+
literal|1
operator|<
name|document
operator|.
name|size
operator|&&
name|document
operator|.
name|treeLevel
index|[
name|nodeNumber
operator|+
literal|1
index|]
operator|>
name|document
operator|.
name|treeLevel
index|[
name|nodeNumber
index|]
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getFirstChild() 	 */
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
name|short
name|level
init|=
name|document
operator|.
name|treeLevel
index|[
name|nodeNumber
index|]
decl_stmt|;
name|int
name|nextNode
init|=
name|nodeNumber
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|nextNode
argument_list|<
name|document
operator|.
name|size
operator|&&
name|document
operator|.
name|treeLevel
index|[
name|nextNode
index|]
argument_list|>
name|level
condition|)
block|{
return|return
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getChildNodes() 	 */
specifier|public
name|NodeList
name|getChildNodes
parameter_list|()
block|{
name|NodeListImpl
name|nl
init|=
operator|new
name|NodeListImpl
argument_list|()
decl_stmt|;
name|short
name|level
init|=
operator|(
name|short
operator|)
operator|(
name|document
operator|.
name|treeLevel
index|[
name|nodeNumber
index|]
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|nextNode
init|=
name|nodeNumber
decl_stmt|;
while|while
condition|(
operator|++
name|nextNode
argument_list|<
name|document
operator|.
name|size
operator|&&
name|document
operator|.
name|next
index|[
name|nextNode
index|]
argument_list|>
name|nodeNumber
condition|)
block|{
if|if
condition|(
name|document
operator|.
name|treeLevel
index|[
name|nextNode
index|]
operator|==
name|level
condition|)
block|{
name|Node
name|n
init|=
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nl
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getPrefix() 	 */
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getPrefix
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getLocalName() 	 */
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getLocalName
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#hasAttributes() 	 */
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
name|document
operator|.
name|alpha
index|[
name|nodeNumber
index|]
operator|>
operator|-
literal|1
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#getAttribute(java.lang.String) 	 */
specifier|public
name|String
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|attr
init|=
name|document
operator|.
name|alpha
index|[
name|nodeNumber
index|]
decl_stmt|;
if|if
condition|(
name|attr
operator|<
literal|0
condition|)
return|return
literal|null
return|;
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
name|nodeNumber
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
if|if
condition|(
name|attrQName
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|document
operator|.
name|attrValue
index|[
name|attr
index|]
return|;
operator|++
name|attr
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#setAttribute(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|setAttribute
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
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#removeAttribute(java.lang.String) 	 */
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getAttributes() 	 */
specifier|public
name|NamedNodeMap
name|getAttributes
parameter_list|()
block|{
name|NamedNodeMapImpl
name|map
init|=
operator|new
name|NamedNodeMapImpl
argument_list|()
decl_stmt|;
name|int
name|attr
init|=
name|document
operator|.
name|alpha
index|[
name|nodeNumber
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
name|nodeNumber
condition|)
block|{
name|map
operator|.
name|add
argument_list|(
operator|new
name|AttributeImpl
argument_list|(
name|document
argument_list|,
name|attr
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|attr
expr_stmt|;
block|}
block|}
comment|// add namespace declarations attached to this element
name|int
name|ns
init|=
name|document
operator|.
name|alphaLen
index|[
name|nodeNumber
index|]
decl_stmt|;
if|if
condition|(
name|ns
operator|<
literal|0
condition|)
return|return
name|map
return|;
while|while
condition|(
name|ns
operator|<
name|document
operator|.
name|nextNamespace
operator|&&
name|document
operator|.
name|namespaceParent
index|[
name|ns
index|]
operator|==
name|nodeNumber
condition|)
block|{
name|NamespaceNode
name|node
init|=
operator|new
name|NamespaceNode
argument_list|(
name|document
argument_list|,
name|ns
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Adding namespace: "
operator|+
name|getNodeName
argument_list|()
operator|+
literal|": "
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
operator|++
name|ns
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|map
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#getAttributeNode(java.lang.String) 	 */
specifier|public
name|Attr
name|getAttributeNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|attr
init|=
name|document
operator|.
name|alpha
index|[
name|nodeNumber
index|]
decl_stmt|;
if|if
condition|(
name|attr
operator|<
literal|0
condition|)
return|return
literal|null
return|;
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
name|nodeNumber
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
if|if
condition|(
name|attrQName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
operator|new
name|AttributeImpl
argument_list|(
name|document
argument_list|,
name|attr
argument_list|)
return|;
operator|++
name|attr
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#setAttributeNode(org.w3c.dom.Attr) 	 */
specifier|public
name|Attr
name|setAttributeNode
parameter_list|(
name|Attr
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#removeAttributeNode(org.w3c.dom.Attr) 	 */
specifier|public
name|Attr
name|removeAttributeNode
parameter_list|(
name|Attr
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#getElementsByTagName(java.lang.String) 	 */
specifier|public
name|NodeList
name|getElementsByTagName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NodeListImpl
name|nl
init|=
operator|new
name|NodeListImpl
argument_list|()
decl_stmt|;
name|short
name|level
init|=
operator|(
name|short
operator|)
operator|(
name|document
operator|.
name|treeLevel
index|[
name|nodeNumber
index|]
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|nextNode
init|=
name|nodeNumber
decl_stmt|;
while|while
condition|(
operator|++
name|nextNode
argument_list|<
name|document
operator|.
name|size
operator|&&
name|document
operator|.
name|next
index|[
name|nextNode
index|]
argument_list|>
name|nodeNumber
condition|)
block|{
if|if
condition|(
name|document
operator|.
name|nodeKind
index|[
name|nextNode
index|]
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
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
name|nextNode
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|qn
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
name|nl
operator|.
name|add
argument_list|(
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nl
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#getAttributeNS(java.lang.String, java.lang.String) 	 */
specifier|public
name|String
name|getAttributeNS
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
name|int
name|attr
init|=
name|document
operator|.
name|alpha
index|[
name|nodeNumber
index|]
decl_stmt|;
if|if
condition|(
name|attr
operator|<
literal|0
condition|)
return|return
literal|null
return|;
name|QName
name|name
decl_stmt|;
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
name|nodeNumber
condition|)
block|{
name|name
operator|=
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
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
operator|&&
name|name
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
return|return
name|document
operator|.
name|attrValue
index|[
name|attr
index|]
return|;
operator|++
name|attr
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#setAttributeNS(java.lang.String, java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|setAttributeNS
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|,
name|String
name|arg2
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#removeAttributeNS(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|removeAttributeNS
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
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#getAttributeNodeNS(java.lang.String, java.lang.String) 	 */
specifier|public
name|Attr
name|getAttributeNodeNS
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
name|int
name|attr
init|=
name|document
operator|.
name|alpha
index|[
name|nodeNumber
index|]
decl_stmt|;
if|if
condition|(
name|attr
operator|<
literal|0
condition|)
return|return
literal|null
return|;
name|QName
name|name
decl_stmt|;
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
name|nodeNumber
condition|)
block|{
name|name
operator|=
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
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
operator|&&
name|name
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
return|return
operator|new
name|AttributeImpl
argument_list|(
name|document
argument_list|,
name|attr
argument_list|)
return|;
operator|++
name|attr
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#setAttributeNodeNS(org.w3c.dom.Attr) 	 */
specifier|public
name|Attr
name|setAttributeNodeNS
parameter_list|(
name|Attr
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#getElementsByTagNameNS(java.lang.String, java.lang.String) 	 */
specifier|public
name|NodeList
name|getElementsByTagNameNS
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|QName
name|qname
init|=
operator|new
name|QName
argument_list|(
name|name
argument_list|,
name|namespaceURI
argument_list|)
decl_stmt|;
name|NodeListImpl
name|nl
init|=
operator|new
name|NodeListImpl
argument_list|()
decl_stmt|;
name|short
name|level
init|=
operator|(
name|short
operator|)
operator|(
name|document
operator|.
name|treeLevel
index|[
name|nodeNumber
index|]
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|nextNode
init|=
name|nodeNumber
decl_stmt|;
while|while
condition|(
operator|++
name|nextNode
argument_list|<
name|document
operator|.
name|size
operator|&&
name|document
operator|.
name|next
index|[
name|nextNode
index|]
argument_list|>
name|nodeNumber
condition|)
block|{
if|if
condition|(
name|document
operator|.
name|nodeKind
index|[
name|nextNode
index|]
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
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
name|nextNode
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|qname
operator|.
name|compareTo
argument_list|(
name|qn
argument_list|)
operator|==
literal|0
condition|)
name|nl
operator|.
name|add
argument_list|(
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nl
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#hasAttribute(java.lang.String) 	 */
specifier|public
name|boolean
name|hasAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getAttribute
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Element#hasAttributeNS(java.lang.String, java.lang.String) 	 */
specifier|public
name|boolean
name|hasAttributeNS
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
return|return
name|getAttributeNS
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Element#getSchemaTypeInfo() 	 */
specifier|public
name|TypeInfo
name|getSchemaTypeInfo
parameter_list|()
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Element#setIdAttribute(java.lang.String, boolean) 	 */
specifier|public
name|void
name|setIdAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isId
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
block|}
comment|/** ? @see org.w3c.dom.Element#setIdAttributeNS(java.lang.String, java.lang.String, boolean) 	 */
specifier|public
name|void
name|setIdAttributeNS
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|boolean
name|isId
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
block|}
comment|/** ? @see org.w3c.dom.Element#setIdAttributeNode(org.w3c.dom.Attr, boolean) 	 */
specifier|public
name|void
name|setIdAttributeNode
parameter_list|(
name|Attr
name|idAttr
parameter_list|,
name|boolean
name|isId
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
block|}
block|}
end_class

end_unit

