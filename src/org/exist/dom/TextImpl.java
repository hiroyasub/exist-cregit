begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  *   */
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
name|ByteArrayPool
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
name|ByteConversion
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
name|UTF8
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
name|w3c
operator|.
name|dom
operator|.
name|UserDataHandler
import|;
end_import

begin_comment
comment|/**  * TextImpl.java  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|TextImpl
extends|extends
name|CharacterDataImpl
implements|implements
name|Text
block|{
specifier|public
name|TextImpl
parameter_list|()
block|{
name|super
argument_list|(
name|Node
operator|.
name|TEXT_NODE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TextImpl
parameter_list|(
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|TEXT_NODE
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TextImpl
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|TEXT_NODE
argument_list|,
name|nodeId
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
specifier|final
name|int
name|nodeIdLen
init|=
name|nodeId
operator|.
name|size
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|ByteArrayPool
operator|.
name|getByteArray
argument_list|(
name|LENGTH_SIGNATURE_LENGTH
operator|+
name|nodeIdLen
operator|+
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
operator|+
name|cdata
operator|.
name|UTF8Size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|data
index|[
name|pos
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
name|Signatures
operator|.
name|Char
operator|<<
literal|0x5
operator|)
expr_stmt|;
name|pos
operator|+=
name|LENGTH_SIGNATURE_LENGTH
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
operator|(
name|short
operator|)
name|nodeId
operator|.
name|units
argument_list|()
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
expr_stmt|;
name|nodeId
operator|.
name|serialize
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|nodeIdLen
expr_stmt|;
name|cdata
operator|.
name|UTF8Encode
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
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
name|TextImpl
name|text
decl_stmt|;
if|if
condition|(
name|pooled
condition|)
name|text
operator|=
operator|(
name|TextImpl
operator|)
name|NodeObjectPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowNode
argument_list|(
name|TextImpl
operator|.
name|class
argument_list|)
expr_stmt|;
else|else
name|text
operator|=
operator|new
name|TextImpl
argument_list|()
expr_stmt|;
name|int
name|pos
init|=
name|start
decl_stmt|;
name|pos
operator|+=
name|LENGTH_SIGNATURE_LENGTH
expr_stmt|;
name|int
name|dlnLen
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
expr_stmt|;
name|NodeId
name|dln
init|=
name|doc
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromData
argument_list|(
name|dlnLen
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|text
operator|.
name|setNodeId
argument_list|(
name|dln
argument_list|)
expr_stmt|;
name|int
name|nodeIdLen
init|=
name|dln
operator|.
name|size
argument_list|()
decl_stmt|;
name|pos
operator|+=
name|nodeIdLen
expr_stmt|;
name|text
operator|.
name|cdata
operator|=
name|UTF8
operator|.
name|decode
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|len
operator|-
operator|(
name|LENGTH_SIGNATURE_LENGTH
operator|+
name|nodeIdLen
operator|+
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
operator|)
argument_list|)
expr_stmt|;
return|return
name|text
return|;
block|}
specifier|public
name|void
name|appendData
parameter_list|(
name|String
name|arg
parameter_list|)
throws|throws
name|DOMException
block|{
name|super
operator|.
name|appendData
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|appendData
parameter_list|(
name|char
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|)
throws|throws
name|DOMException
block|{
name|super
operator|.
name|appendData
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deleteData
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|DOMException
block|{
name|super
operator|.
name|deleteData
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|super
operator|.
name|getLength
argument_list|()
return|;
block|}
specifier|public
name|String
name|getNodeValue
parameter_list|()
block|{
return|return
name|super
operator|.
name|getNodeValue
argument_list|()
return|;
block|}
specifier|public
name|void
name|insertData
parameter_list|(
name|int
name|offset
parameter_list|,
name|String
name|arg
parameter_list|)
throws|throws
name|DOMException
block|{
name|super
operator|.
name|insertData
argument_list|(
name|offset
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|replaceData
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|arg
parameter_list|)
throws|throws
name|DOMException
block|{
name|super
operator|.
name|replaceData
argument_list|(
name|offset
argument_list|,
name|count
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|setNodeValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Text
name|splitText
parameter_list|(
name|int
name|offset
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|substringData
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
name|super
operator|.
name|substringData
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
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
if|if
condition|(
name|top
condition|)
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<exist:text "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"xmlns:exist=\""
operator|+
name|Namespaces
operator|.
name|EXIST_NS
operator|+
literal|"\" "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"exist:id=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\" exist:source=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
operator|(
operator|(
name|DocumentImpl
operator|)
name|getOwnerDocument
argument_list|()
operator|)
operator|.
name|getFileURI
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"</exist:text>"
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
return|return
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
comment|//bad implementations don't call hasChildNodes before
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Text#isElementContentWhitespace() 	 */
specifier|public
name|boolean
name|isElementContentWhitespace
parameter_list|()
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|false
return|;
block|}
comment|/** ? @see org.w3c.dom.Text#getWholeText() 	 */
specifier|public
name|String
name|getWholeText
parameter_list|()
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Text#replaceWholeText(java.lang.String) 	 */
specifier|public
name|Text
name|replaceWholeText
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
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
block|}
end_class

end_unit

