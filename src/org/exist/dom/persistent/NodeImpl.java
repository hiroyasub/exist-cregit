begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|INode
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
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|NodeImpl
parameter_list|<
name|T
extends|extends
name|NodeImpl
parameter_list|>
implements|implements
name|INode
argument_list|<
name|DocumentImpl
argument_list|,
name|T
argument_list|>
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|NodeImpl
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Node
name|cloneNode
parameter_list|(
specifier|final
name|boolean
name|deep
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|appendChild
parameter_list|(
specifier|final
name|Node
name|child
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|removeChild
parameter_list|(
specifier|final
name|Node
name|oldChild
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|replaceChild
parameter_list|(
specifier|final
name|Node
name|newChild
parameter_list|,
specifier|final
name|Node
name|oldChild
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|insertBefore
parameter_list|(
specifier|final
name|Node
name|newChild
parameter_list|,
specifier|final
name|Node
name|refChild
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|void
name|appendChildren
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|NodeList
name|nodes
parameter_list|,
specifier|final
name|int
name|child
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|Node
name|removeChild
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|Node
name|oldChild
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|Node
name|replaceChild
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|Node
name|newChild
parameter_list|,
specifier|final
name|Node
name|oldChild
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * Update a child node. This method will only update the child node      * but not its potential descendant nodes.      *      * @param oldChild      * @param newChild      * @throws DOMException      */
specifier|public
name|IStoredNode
name|updateChild
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|Node
name|oldChild
parameter_list|,
specifier|final
name|Node
name|newChild
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * Insert a list of nodes at the position before the reference      * child.      *<p/>      * NOTE: You must call insertBefore on the parent node of the node that you      * want to insert nodes before.      */
specifier|public
name|void
name|insertBefore
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|NodeList
name|nodes
parameter_list|,
specifier|final
name|Node
name|refChild
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * Insert a list of nodes at the position following the reference      * child.      *<p/>      * NOTE: You must call insertAfter on the parent node of the node that you want      * to insert nodes after.      */
specifier|public
name|void
name|insertAfter
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|NodeList
name|nodes
parameter_list|,
specifier|final
name|Node
name|refChild
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"insertAfter(Txn transaction, NodeList nodes, Node refChild) "
operator|+
literal|"not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getChildCount() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|NodeList
name|getChildNodes
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getChildNodes() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * Note: Typically you should call {@link org.w3c.dom.Node#hasChildNodes()}      * first.      *      * @see org.w3c.dom.Node#getFirstChild()      */
annotation|@
name|Override
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getFirstChild() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
empty_stmt|;
annotation|@
name|Override
specifier|public
name|Node
name|getLastChild
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getLastChild() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|NamedNodeMap
name|getAttributes
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNodeValue
parameter_list|()
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getNodeValue() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNodeValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"setNodeValue(String value) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
name|getChildCount
argument_list|()
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupported
parameter_list|(
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"isSupported(String key, String value) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalize
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"normalize() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getBaseURI() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|compareDocumentPosition
parameter_list|(
specifier|final
name|Node
name|other
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"compareDocumentPosition(Node other) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTextContent
parameter_list|()
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getTextContent() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTextContent
parameter_list|(
specifier|final
name|String
name|textContent
parameter_list|)
throws|throws
name|DOMException
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"setTextContent(String textContent) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
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
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"isSameNode(Node other) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|lookupPrefix
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"lookupPrefix(String namespaceURI) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDefaultNamespace
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"isDefaultNamespace(String namespaceURI) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|lookupNamespaceURI
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"lookupNamespaceURI(String prefix) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEqualNode
parameter_list|(
specifier|final
name|Node
name|arg
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"isEqualNode(Node arg) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getFeature
parameter_list|(
specifier|final
name|String
name|feature
parameter_list|,
specifier|final
name|String
name|version
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getFeature(String feature, String version) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getUserData
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"getUserData(String key) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|setUserData
parameter_list|(
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|Object
name|data
parameter_list|,
specifier|final
name|UserDataHandler
name|handler
parameter_list|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|NOT_SUPPORTED_ERR
argument_list|,
literal|"setUserData(String key, Object data, UserDataHandler handler) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
specifier|final
name|QName
name|nodeName
init|=
name|getQName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|prefix
init|=
name|nodeName
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
return|return
name|prefix
operator|==
literal|null
condition|?
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
else|:
name|prefix
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrefix
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
throws|throws
name|DOMException
block|{
specifier|final
name|QName
name|nodeName
init|=
name|getQName
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeName
operator|!=
literal|null
condition|)
block|{
name|setQName
argument_list|(
operator|new
name|QName
argument_list|(
name|nodeName
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit
