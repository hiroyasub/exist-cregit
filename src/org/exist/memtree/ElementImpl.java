begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|Namespaces
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
name|xquery
operator|.
name|NodeTest
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
name|HashSet
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
name|Set
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
name|document
operator|.
name|nodeName
index|[
name|nodeNumber
index|]
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
name|int
name|nextNode
init|=
name|document
operator|.
name|getFirstChildFor
argument_list|(
name|nodeNumber
argument_list|)
decl_stmt|;
while|while
condition|(
name|nextNode
operator|>
name|nodeNumber
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
name|nextNode
operator|=
name|document
operator|.
name|next
index|[
name|nextNode
index|]
expr_stmt|;
block|}
return|return
name|nl
return|;
block|}
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
return|return
name|document
operator|.
name|getChildCountFor
argument_list|(
name|nodeNumber
argument_list|)
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
operator|||
name|document
operator|.
name|alphaLen
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
name|QName
name|attrQName
init|=
name|document
operator|.
name|attrName
index|[
name|attr
index|]
decl_stmt|;
if|if
condition|(
name|attrQName
operator|.
name|getStringValue
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
block|}
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"xmlns:"
argument_list|)
condition|)
block|{
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
operator|-
literal|1
operator|<
name|ns
condition|)
block|{
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
name|QName
name|nsQName
init|=
name|document
operator|.
name|namespaceCode
index|[
name|ns
index|]
decl_stmt|;
if|if
condition|(
name|nsQName
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|nsQName
operator|.
name|getNamespaceURI
argument_list|()
return|;
operator|++
name|ns
expr_stmt|;
block|}
block|}
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
specifier|public
name|int
name|getAttributesCount
parameter_list|()
block|{
return|return
name|document
operator|.
name|getAttributesCountFor
argument_list|(
name|nodeNumber
argument_list|)
operator|+
name|document
operator|.
name|getNamespacesCountFor
argument_list|(
name|nodeNumber
argument_list|)
return|;
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
name|QName
name|attrQName
init|=
name|document
operator|.
name|attrName
index|[
name|attr
index|]
decl_stmt|;
if|if
condition|(
name|attrQName
operator|.
name|getStringValue
argument_list|()
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
block|}
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"xmlns:"
argument_list|)
condition|)
block|{
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
operator|-
literal|1
operator|<
name|ns
condition|)
block|{
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
name|QName
name|nsQName
init|=
name|document
operator|.
name|namespaceCode
index|[
name|ns
index|]
decl_stmt|;
if|if
condition|(
name|nsQName
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
operator|new
name|NamespaceNode
argument_list|(
name|document
argument_list|,
name|ns
argument_list|)
return|;
operator|++
name|ns
expr_stmt|;
block|}
block|}
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
specifier|public
name|void
name|selectAttributes
parameter_list|(
name|NodeTest
name|test
parameter_list|,
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
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
name|AttributeImpl
name|attrib
init|=
operator|new
name|AttributeImpl
argument_list|(
name|document
argument_list|,
name|attr
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|attrib
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|attrib
argument_list|)
expr_stmt|;
operator|++
name|attr
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|selectDescendantAttributes
parameter_list|(
name|NodeTest
name|test
parameter_list|,
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|treeLevel
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
decl_stmt|;
name|NodeImpl
name|n
init|=
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
decl_stmt|;
name|n
operator|.
name|selectAttributes
argument_list|(
name|test
argument_list|,
name|result
argument_list|)
expr_stmt|;
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
name|treeLevel
index|[
name|nextNode
index|]
argument_list|>
name|treeLevel
condition|)
block|{
name|n
operator|=
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
name|n
operator|.
name|selectAttributes
argument_list|(
name|test
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|selectChildren
parameter_list|(
name|NodeTest
name|test
parameter_list|,
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|nextNode
init|=
name|document
operator|.
name|getFirstChildFor
argument_list|(
name|nodeNumber
argument_list|)
decl_stmt|;
while|while
condition|(
name|nextNode
operator|>
name|nodeNumber
condition|)
block|{
name|NodeImpl
name|n
init|=
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|n
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|nextNode
operator|=
name|document
operator|.
name|next
index|[
name|nextNode
index|]
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|selectDescendants
parameter_list|(
name|boolean
name|includeSelf
parameter_list|,
name|NodeTest
name|test
parameter_list|,
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|treeLevel
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
decl_stmt|;
if|if
condition|(
name|includeSelf
condition|)
block|{
name|NodeImpl
name|n
init|=
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|n
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
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
name|treeLevel
index|[
name|nextNode
index|]
argument_list|>
name|treeLevel
condition|)
block|{
name|NodeImpl
name|n
init|=
name|document
operator|.
name|getNode
argument_list|(
name|nextNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|n
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
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
name|int
name|nextNode
init|=
name|nodeNumber
decl_stmt|;
while|while
condition|(
operator|++
name|nextNode
operator|<
name|document
operator|.
name|size
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
name|document
operator|.
name|nodeName
index|[
name|nextNode
index|]
decl_stmt|;
if|if
condition|(
name|qn
operator|.
name|getStringValue
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
if|if
condition|(
name|document
operator|.
name|next
index|[
name|nextNode
index|]
operator|<=
name|nodeNumber
condition|)
break|break;
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
operator|-
literal|1
operator|<
name|attr
condition|)
block|{
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
name|document
operator|.
name|attrName
index|[
name|attr
index|]
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
block|}
if|if
condition|(
name|Namespaces
operator|.
name|XMLNS_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
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
operator|-
literal|1
operator|<
name|ns
condition|)
block|{
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
name|QName
name|nsQName
init|=
name|document
operator|.
name|namespaceCode
index|[
name|ns
index|]
decl_stmt|;
if|if
condition|(
name|nsQName
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
return|return
name|nsQName
operator|.
name|getNamespaceURI
argument_list|()
return|;
operator|++
name|ns
expr_stmt|;
block|}
block|}
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
operator|-
literal|1
operator|<
name|attr
condition|)
block|{
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
name|document
operator|.
name|attrName
index|[
name|attr
index|]
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
block|}
if|if
condition|(
name|Namespaces
operator|.
name|XMLNS_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
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
operator|-
literal|1
operator|<
name|ns
condition|)
block|{
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
name|QName
name|nsQName
init|=
name|document
operator|.
name|namespaceCode
index|[
name|ns
index|]
decl_stmt|;
if|if
condition|(
name|nsQName
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
return|return
operator|new
name|NamespaceNode
argument_list|(
name|document
argument_list|,
name|ns
argument_list|)
return|;
operator|++
name|ns
expr_stmt|;
block|}
block|}
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
name|int
name|nextNode
init|=
name|nodeNumber
decl_stmt|;
while|while
condition|(
operator|++
name|nextNode
operator|<
name|document
operator|.
name|size
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
name|document
operator|.
name|nodeName
index|[
name|nextNode
index|]
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
if|if
condition|(
name|document
operator|.
name|next
index|[
name|nextNode
index|]
operator|<=
name|nodeNumber
condition|)
break|break;
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
comment|/**      * The method<code>getNamespaceForPrefix</code>      *      * @param name a<code>String</code> value      * @return a<code>String</code> value      */
specifier|public
name|String
name|getNamespaceForPrefix
parameter_list|(
name|String
name|name
parameter_list|)
block|{
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
operator|-
literal|1
operator|<
name|ns
condition|)
block|{
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
name|QName
name|nsQName
init|=
name|document
operator|.
name|namespaceCode
index|[
name|ns
index|]
decl_stmt|;
if|if
condition|(
name|nsQName
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"xmlns:"
operator|+
name|name
argument_list|)
condition|)
return|return
name|nsQName
operator|.
name|getNamespaceURI
argument_list|()
return|;
operator|++
name|ns
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * The method<code>getPrefixes</code>      *      * @return a<code>Set</code> value      */
specifier|public
name|Set
name|getPrefixes
parameter_list|()
block|{
name|HashSet
name|set
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
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
operator|-
literal|1
operator|<
name|ns
condition|)
block|{
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
name|QName
name|nsQName
init|=
name|document
operator|.
name|namespaceCode
index|[
name|ns
index|]
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|nsQName
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|ns
expr_stmt|;
block|}
block|}
return|return
name|set
return|;
block|}
comment|/**      * The method<code>declaresNamespacePrefixes</code>      *      * @return a<code>boolean</code> value      */
specifier|public
name|boolean
name|declaresNamespacePrefixes
parameter_list|()
block|{
return|return
operator|(
name|document
operator|.
name|getNamespacesCountFor
argument_list|(
name|nodeNumber
argument_list|)
operator|>
literal|0
operator|)
return|;
block|}
comment|/**      * The method<code>getNamespaceMap</code>      *      * @return a<code>Map</code> value      */
specifier|public
name|Map
name|getNamespaceMap
parameter_list|()
block|{
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
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
operator|-
literal|1
operator|<
name|ns
condition|)
block|{
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
name|QName
name|nsQName
init|=
name|document
operator|.
name|namespaceCode
index|[
name|ns
index|]
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|nsQName
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|nsQName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|ns
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ELEMENT
return|;
block|}
comment|/** ? @see org.w3c.dom.Node#getBaseURI() 	 */
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
name|String
name|baseURI
init|=
name|getAttributeNS
argument_list|(
name|Namespaces
operator|.
name|XML_NS
argument_list|,
literal|"base"
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseURI
operator|==
literal|null
condition|)
block|{
name|baseURI
operator|=
literal|""
expr_stmt|;
block|}
name|int
name|parent
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|test
init|=
operator|-
literal|1
decl_stmt|;
name|test
operator|=
name|document
operator|.
name|getParentNodeFor
argument_list|(
name|nodeNumber
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|.
name|nodeKind
index|[
name|test
index|]
operator|!=
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
block|{
name|parent
operator|=
name|test
expr_stmt|;
block|}
comment|// fixme! UNDEFINED instead of all the -1s in this file./ljo
while|while
condition|(
name|parent
operator|!=
operator|-
literal|1
operator|&&
name|document
operator|.
name|getNode
argument_list|(
name|parent
argument_list|)
operator|.
name|getBaseURI
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|baseURI
argument_list|)
condition|)
block|{
name|baseURI
operator|=
name|document
operator|.
name|getNode
argument_list|(
name|parent
argument_list|)
operator|.
name|getBaseURI
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|baseURI
operator|=
name|document
operator|.
name|getNode
argument_list|(
name|parent
argument_list|)
operator|.
name|getBaseURI
argument_list|()
operator|+
literal|"/"
operator|+
name|baseURI
expr_stmt|;
block|}
name|test
operator|=
name|document
operator|.
name|getParentNodeFor
argument_list|(
name|parent
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|.
name|nodeKind
index|[
name|test
index|]
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
block|{
return|return
name|baseURI
return|;
block|}
else|else
block|{
name|parent
operator|=
name|test
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|baseURI
argument_list|)
condition|)
block|{
name|baseURI
operator|=
name|getDocument
argument_list|()
operator|.
name|getBaseURI
argument_list|()
expr_stmt|;
block|}
return|return
name|baseURI
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"in-memory#"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"element {"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"} {"
argument_list|)
expr_stmt|;
name|NamedNodeMap
name|theAttrs
decl_stmt|;
if|if
condition|(
operator|(
name|theAttrs
operator|=
name|getAttributes
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|theAttrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|Node
name|natt
init|=
name|theAttrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"org.exist.memtree.AttributeImpl"
operator|.
name|equals
argument_list|(
name|natt
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
operator|(
operator|(
name|AttributeImpl
operator|)
name|natt
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
operator|(
operator|(
name|NamespaceNode
operator|)
name|natt
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|getChildCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|Node
name|child
init|=
name|getChildNodes
argument_list|()
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|child
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

