begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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

begin_class
specifier|public
specifier|abstract
class|class
name|NodeImpl
implements|implements
name|Node
implements|,
name|QNameable
block|{
specifier|protected
specifier|final
specifier|static
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
comment|/** 	 * @see org.w3c.dom.Node#cloneNode(boolean) 	 */
specifier|public
name|Node
name|cloneNode
parameter_list|(
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
comment|/**      * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node)      */
specifier|public
name|Node
name|appendChild
parameter_list|(
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
comment|/**      * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node)      */
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
name|updateChild
parameter_list|(
name|Node
name|oldChild
parameter_list|,
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
comment|/**      * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node)      */
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
name|insertAfter
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
name|Txn
name|transaction
parameter_list|,
name|NodeList
name|nodes
parameter_list|,
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
name|Txn
name|transaction
parameter_list|,
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
name|Txn
name|transaction
parameter_list|,
name|Node
name|newChild
parameter_list|,
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
comment|/**      * Update a child node. This method will only update the child node      * but not its potential descendant nodes.      *       * @param oldChild      * @param newChild      * @throws DOMException      */
specifier|public
name|StoredNode
name|updateChild
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Node
name|oldChild
parameter_list|,
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
specifier|public
name|void
name|insertBefore
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|NodeList
name|nodes
parameter_list|,
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
name|insertAfter
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|NodeList
name|nodes
parameter_list|,
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
literal|"insertAfter(Txn transaction, NodeList nodes, Node refChild) not implemented on class "
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
comment|/** 	 * @see org.w3c.dom.Node#getFirstChild() 	 */
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
comment|/** 	 * @see org.w3c.dom.Node#getLastChild() 	 */
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
comment|/**      * @see org.w3c.dom.Node#hasAttributes()      */
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
name|getAttributesCount
argument_list|()
operator|>
literal|0
return|;
block|}
specifier|public
name|short
name|getAttributesCount
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
literal|"getAttributesCount() not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * @see org.w3c.dom.Node#getAttributes()      */
specifier|public
name|NamedNodeMap
name|getAttributes
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
literal|"getAttributes()  not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      *  Set the attributes that belong to this node.      *      *@param  attribNum  The new attributes value      */
specifier|public
name|void
name|setAttributes
parameter_list|(
name|short
name|attribNum
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
literal|"setAttributes(short attribNum) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * @see org.w3c.dom.Node#getNodeValue()      */
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
comment|/**      *  Set the node value.      *      *@param  value             The new nodeValue value      *@exception  DOMException  Description of the Exception      */
specifier|public
name|void
name|setNodeValue
parameter_list|(
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
comment|/**      * @see org.w3c.dom.Node#hasChildNodes()      */
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
comment|/**      *  Set the number of children.      *      *@param  count  The new childCount value      */
specifier|protected
name|void
name|setChildCount
parameter_list|(
name|int
name|count
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
literal|"setChildCount(int count) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      *  Set the node name.      *      *@param  name  The new nodeName value      */
specifier|public
name|void
name|setNodeName
parameter_list|(
name|QName
name|name
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
literal|"setNodeName(QName name) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * @see org.w3c.dom.Node#isSupported(java.lang.String, java.lang.String)      */
specifier|public
name|boolean
name|isSupported
parameter_list|(
name|String
name|key
parameter_list|,
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
comment|/**      * @see org.w3c.dom.Node#normalize()      */
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
comment|/**      * Method supports.      * @param feature      * @param version      * @return boolean      */
specifier|public
name|boolean
name|supports
parameter_list|(
name|String
name|feature
parameter_list|,
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
literal|"supports(String feature, String version) not implemented on class "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/** ? @see org.w3c.dom.Node#getBaseURI() 	 */
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
comment|/** ? @see org.w3c.dom.Node#getTextContent() 	 */
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
comment|/** ? @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node) 	 */
specifier|public
name|boolean
name|isSameNode
parameter_list|(
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
comment|/** ? @see org.w3c.dom.Node#lookupPrefix(java.lang.String) 	 */
specifier|public
name|String
name|lookupPrefix
parameter_list|(
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
comment|/** ? @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String) 	 */
specifier|public
name|boolean
name|isDefaultNamespace
parameter_list|(
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
comment|/** ? @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String) 	 */
specifier|public
name|String
name|lookupNamespaceURI
parameter_list|(
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
comment|/** ? @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node) 	 */
specifier|public
name|boolean
name|isEqualNode
parameter_list|(
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
comment|/** ? @see org.w3c.dom.Node#getUserData(java.lang.String) 	 */
specifier|public
name|Object
name|getUserData
parameter_list|(
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
comment|/** ? @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler)      */
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
comment|/**      * @see org.w3c.dom.Node#getPrefix()      */
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
name|QName
name|nodeName
init|=
name|getQName
argument_list|()
decl_stmt|;
comment|//if (nodeName != null) {
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
literal|""
else|:
name|prefix
return|;
comment|//}
comment|//return "";
block|}
comment|/**      *  Sets the prefix attribute of the NodeImpl object      *      *@param  prefix            The new prefix value      *@exception  DOMException  Description of the Exception      */
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
name|nodeName
operator|.
name|setPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.w3c.dom.Node#getNamespaceURI()      */
comment|//TODO : remove default value
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
name|QName
name|nodeName
init|=
name|getQName
argument_list|()
decl_stmt|;
comment|//if (nodeName != null)
return|return
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
return|;
comment|//return "";
block|}
comment|/**      * @see org.w3c.dom.Node#getLocalName()      */
comment|//TODO : remove default value
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
name|QName
name|nodeName
init|=
name|getQName
argument_list|()
decl_stmt|;
comment|//if (nodeName != null)
return|return
name|nodeName
operator|.
name|getLocalName
argument_list|()
return|;
comment|//return "";
block|}
comment|/**      * @see org.w3c.dom.Node#getNodeName()      */
comment|//TODO : remove default value
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
name|QName
name|nodeName
init|=
name|getQName
argument_list|()
decl_stmt|;
comment|//if(nodeName != null)
return|return
name|nodeName
operator|.
name|getStringValue
argument_list|()
return|;
comment|//return "";
block|}
block|}
end_class

end_unit

