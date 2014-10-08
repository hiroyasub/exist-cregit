begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2008-2013 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|config
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
name|persistent
operator|.
name|NodeAtExist
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
name|persistent
operator|.
name|DocumentAtExist
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
name|w3c
operator|.
name|dom
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Node proxy object. Help to provide single interface for in-memory& store nodes.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ProxyNode
parameter_list|<
name|E
extends|extends
name|NodeAtExist
parameter_list|>
implements|implements
name|NodeAtExist
implements|,
name|Proxy
argument_list|<
name|E
argument_list|>
block|{
specifier|private
name|E
name|node
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|appendChild
parameter_list|(
name|Node
name|newChild
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|appendChild
argument_list|(
name|newChild
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#cloneNode(boolean) 	 */
specifier|public
name|Node
name|cloneNode
parameter_list|(
name|boolean
name|deep
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|cloneNode
argument_list|(
name|deep
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node) 	 */
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
return|return
name|getProxyObject
argument_list|()
operator|.
name|compareDocumentPosition
argument_list|(
name|other
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
name|getProxyObject
argument_list|()
operator|.
name|getAttributes
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getBaseURI() 	 */
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getBaseURI
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getChildNodes() 	 */
specifier|public
name|NodeList
name|getChildNodes
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getChildNodes
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getFeature(java.lang.String, java.lang.String) 	 */
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
return|return
name|getProxyObject
argument_list|()
operator|.
name|getFeature
argument_list|(
name|feature
argument_list|,
name|version
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getFirstChild() 	 */
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getFirstChild
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getLastChild() 	 */
specifier|public
name|Node
name|getLastChild
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getLastChild
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getLocalPart() 	 */
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getLocalName
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNextSibling() 	 */
specifier|public
name|Node
name|getNextSibling
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getNextSibling
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNodeName() 	 */
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getNodeName
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNodeType() 	 */
specifier|public
name|short
name|getNodeType
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getNodeType
argument_list|()
return|;
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
name|getProxyObject
argument_list|()
operator|.
name|getNodeValue
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getOwnerDocument() 	 */
specifier|public
name|Document
name|getOwnerDocument
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getOwnerDocument
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getParentNode() 	 */
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getParentNode
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
name|getProxyObject
argument_list|()
operator|.
name|getPrefix
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getPreviousSibling() 	 */
specifier|public
name|Node
name|getPreviousSibling
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getPreviousSibling
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getTextContent() 	 */
specifier|public
name|String
name|getTextContent
parameter_list|()
throws|throws
name|DOMException
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getTextContent
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getUserData(java.lang.String) 	 */
specifier|public
name|Object
name|getUserData
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getUserData
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#hasAttributes() 	 */
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|hasAttributes
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#hasChildNodes() 	 */
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|hasChildNodes
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|insertBefore
parameter_list|(
name|Node
name|newChild
parameter_list|,
name|Node
name|refChild
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|insertBefore
argument_list|(
name|newChild
argument_list|,
name|refChild
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String) 	 */
specifier|public
name|boolean
name|isDefaultNamespace
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|isDefaultNamespace
argument_list|(
name|namespaceURI
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node) 	 */
specifier|public
name|boolean
name|isEqualNode
parameter_list|(
name|Node
name|arg
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|isEqualNode
argument_list|(
name|arg
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node) 	 */
specifier|public
name|boolean
name|isSameNode
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|isSameNode
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#isSupported(java.lang.String, java.lang.String) 	 */
specifier|public
name|boolean
name|isSupported
parameter_list|(
name|String
name|feature
parameter_list|,
name|String
name|version
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|isSupported
argument_list|(
name|feature
argument_list|,
name|version
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String) 	 */
specifier|public
name|String
name|lookupNamespaceURI
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|lookupNamespaceURI
argument_list|(
name|prefix
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#lookupPrefix(java.lang.String) 	 */
specifier|public
name|String
name|lookupPrefix
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|lookupPrefix
argument_list|(
name|namespaceURI
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#normalize() 	 */
specifier|public
name|void
name|normalize
parameter_list|()
block|{
name|getProxyObject
argument_list|()
operator|.
name|normalize
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#removeChild(org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|removeChild
parameter_list|(
name|Node
name|oldChild
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|removeChild
argument_list|(
name|oldChild
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|replaceChild
parameter_list|(
name|Node
name|newChild
parameter_list|,
name|Node
name|oldChild
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|replaceChild
argument_list|(
name|newChild
argument_list|,
name|oldChild
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#setNodeValue(java.lang.String) 	 */
specifier|public
name|void
name|setNodeValue
parameter_list|(
name|String
name|nodeValue
parameter_list|)
throws|throws
name|DOMException
block|{
name|getProxyObject
argument_list|()
operator|.
name|setNodeValue
argument_list|(
name|nodeValue
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#setPrefix(java.lang.String) 	 */
specifier|public
name|void
name|setPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|DOMException
block|{
name|getProxyObject
argument_list|()
operator|.
name|setPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#setTextContent(java.lang.String) 	 */
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
name|getProxyObject
argument_list|()
operator|.
name|setTextContent
argument_list|(
name|textContent
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler) 	 */
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
return|return
name|getProxyObject
argument_list|()
operator|.
name|setUserData
argument_list|(
name|key
argument_list|,
name|data
argument_list|,
name|handler
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.i.dom.NodeAteXist#getNodeId() 	 */
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getNodeId
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.i.dom.NodeAteXist#getNodeNumber() 	 */
specifier|public
name|int
name|getNodeNumber
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getNodeNumber
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.persistent.QNameable#getQName() 	 */
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getQName
argument_list|()
return|;
block|}
specifier|public
name|DocumentAtExist
name|getDocumentAtExist
parameter_list|()
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|getDocumentAtExist
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Comparable#compareTo(java.lang.Object) 	 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|getProxyObject
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
argument_list|)
return|;
block|}
specifier|public
name|E
name|getProxyObject
parameter_list|()
block|{
return|return
name|node
return|;
block|}
specifier|public
name|void
name|setProxyObject
parameter_list|(
name|E
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|instanceof
name|NodeAtExist
condition|)
block|{
name|this
operator|.
name|node
operator|=
operator|(
name|E
operator|)
name|object
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Only NodeAtExist allowed"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

