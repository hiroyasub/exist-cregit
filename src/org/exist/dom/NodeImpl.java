begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id:  */
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
name|DOMException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|LexicalHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|*
import|;
end_import

begin_comment
comment|/**  * NodeImpl.java  *   * @author Wolfgang Meier  */
end_comment

begin_comment
comment|/**  *  The base class for all DOM objects.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    8. Juli 2002  */
end_comment

begin_class
specifier|public
class|class
name|NodeImpl
implements|implements
name|Node
block|{
specifier|private
specifier|final
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|NodeImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|short
name|attributes
init|=
literal|0
decl_stmt|;
specifier|protected
name|long
name|gid
decl_stmt|;
specifier|protected
name|long
name|internalAddress
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|String
name|nodeName
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|nodeNameRef
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|short
name|nodeType
init|=
literal|0
decl_stmt|;
specifier|protected
name|DocumentImpl
name|ownerDocument
init|=
literal|null
decl_stmt|;
comment|/**  Constructor for the NodeImpl object */
specifier|public
name|NodeImpl
parameter_list|()
block|{
block|}
comment|/** 	 *  Constructor for the NodeImpl object 	 * 	 *@param  nodeType  Description of the Parameter 	 */
specifier|public
name|NodeImpl
parameter_list|(
name|short
name|nodeType
parameter_list|)
block|{
name|this
argument_list|(
name|nodeType
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Constructor for the NodeImpl object 	 * 	 *@param  n  Description of the Parameter 	 */
specifier|public
name|NodeImpl
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
name|this
argument_list|(
name|n
operator|.
name|getNodeType
argument_list|()
argument_list|,
name|n
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ownerDocument
operator|=
operator|(
name|DocumentImpl
operator|)
name|n
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
block|}
comment|/** 	 *  Constructor for the NodeImpl object 	 * 	 *@param  gid  Description of the Parameter 	 */
specifier|public
name|NodeImpl
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
name|this
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|,
literal|""
argument_list|,
name|gid
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Constructor for the NodeImpl object 	 * 	 *@param  nodeType  Description of the Parameter 	 *@param  gid       Description of the Parameter 	 */
specifier|public
name|NodeImpl
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|long
name|gid
parameter_list|)
block|{
name|this
argument_list|(
name|nodeType
argument_list|,
literal|""
argument_list|,
name|gid
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Constructor for the NodeImpl object 	 * 	 *@param  nodeType  Description of the Parameter 	 *@param  nodeName  Description of the Parameter 	 */
specifier|public
name|NodeImpl
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
name|this
argument_list|(
name|nodeType
argument_list|,
name|nodeName
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Constructor for the NodeImpl object 	 * 	 *@param  nodeType  Description of the Parameter 	 *@param  nodeName  Description of the Parameter 	 *@param  gid       Description of the Parameter 	 */
specifier|public
name|NodeImpl
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|long
name|gid
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
name|nodeName
operator|=
name|nodeName
expr_stmt|;
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
block|}
comment|/** 	 *  Deserialize a node from a byte array. 	 * 	 *@param  data  Description of the Parameter 	 *@param  doc   Description of the Parameter 	 *@return       Description of the Return Value 	 */
specifier|public
specifier|static
name|NodeImpl
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
parameter_list|)
block|{
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
argument_list|)
return|;
default|default :
name|LOG
operator|.
name|debug
argument_list|(
literal|"not implemented"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/** 	 * Reset this object to its initial state. Required by the 	 * parser to be able to reuse node objects. 	 */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|attributes
operator|=
literal|0
expr_stmt|;
name|gid
operator|=
literal|0
expr_stmt|;
name|internalAddress
operator|=
operator|-
literal|1
expr_stmt|;
name|nodeName
operator|=
literal|null
expr_stmt|;
name|ownerDocument
operator|=
literal|null
expr_stmt|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node) 	 */
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
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
name|Node
name|appendChildren
parameter_list|(
name|NodeList
name|nodes
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
literal|"not implemented"
argument_list|)
throw|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#cloneNode(boolean) 	 */
specifier|public
name|Node
name|cloneNode
parameter_list|(
name|boolean
name|deep
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/** 	 * @see java.lang.Object#equals(java.lang.Object) 	 */
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
if|if
condition|(
operator|(
operator|(
name|NodeImpl
operator|)
name|obj
operator|)
operator|.
name|gid
operator|==
name|gid
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|long
name|firstChildID
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getAttributes() 	 */
specifier|public
name|NamedNodeMap
name|getAttributes
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 * Method getAttributesCount. 	 * @return short 	 */
specifier|public
name|short
name|getAttributesCount
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
comment|/** 	 *  Gets the broker attribute of the NodeImpl object 	 * 	 *@return    The broker value 	 */
specifier|public
name|DBBroker
name|getBroker
parameter_list|()
block|{
return|return
operator|(
name|DBBroker
operator|)
name|ownerDocument
operator|.
name|broker
return|;
block|}
comment|/** 	 * Method getChildCount. 	 * @return int 	 */
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** 	 *  Gets the childNodes attribute of the NodeImpl object 	 * 	 *@return    The childNodes value 	 */
specifier|public
name|NodeList
name|getChildNodes
parameter_list|()
block|{
return|return
operator|(
name|NodeList
operator|)
operator|new
name|NodeListImpl
argument_list|()
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getFirstChild() 	 */
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 *  Get the unique identifier assigned to this node 	 * 	 *@return    The gID value 	 */
specifier|public
name|long
name|getGID
parameter_list|()
block|{
return|return
name|gid
return|;
block|}
comment|/** 	 *  Get the internal storage address of this node 	 * 	 *@return    The internalAddress value 	 */
specifier|public
name|long
name|getInternalAddress
parameter_list|()
block|{
return|return
name|internalAddress
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getLastChild() 	 */
specifier|public
name|Node
name|getLastChild
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getLocalName() 	 */
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
if|if
condition|(
name|nodeName
operator|!=
literal|null
operator|&&
name|nodeName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|>
operator|-
literal|1
condition|)
return|return
name|nodeName
operator|.
name|substring
argument_list|(
name|nodeName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|+
literal|1
argument_list|)
return|;
return|return
name|nodeName
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
if|if
condition|(
name|nodeName
operator|!=
literal|null
operator|&&
name|nodeName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|String
name|prefix
init|=
name|nodeName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|nodeName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|prefix
operator|.
name|equals
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
return|return
name|ownerDocument
operator|.
name|broker
operator|.
name|getNamespaceURI
argument_list|(
name|prefix
argument_list|)
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getNextSibling() 	 */
specifier|public
name|Node
name|getNextSibling
parameter_list|()
block|{
name|NodeImpl
name|parent
init|=
operator|(
name|NodeImpl
operator|)
name|getParentNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|gid
operator|<
name|parent
operator|.
name|lastChildID
argument_list|()
condition|)
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|gid
operator|+
literal|1
argument_list|)
return|;
return|return
literal|null
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getNodeName() 	 */
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getNodeType() 	 */
specifier|public
name|short
name|getNodeType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getNodeValue() 	 */
specifier|public
name|String
name|getNodeValue
parameter_list|()
throws|throws
name|DOMException
block|{
return|return
literal|""
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getOwnerDocument() 	 */
specifier|public
name|Document
name|getOwnerDocument
parameter_list|()
block|{
return|return
name|ownerDocument
return|;
block|}
comment|/** 	 *  Get the unique node identifier of this node's parent node. 	 * 	 *@return    The parentGID value 	 */
specifier|public
name|long
name|getParentGID
parameter_list|()
block|{
name|int
name|level
init|=
name|ownerDocument
operator|.
name|getTreeLevel
argument_list|(
name|gid
argument_list|)
decl_stmt|;
return|return
operator|(
name|gid
operator|-
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
operator|)
operator|/
name|ownerDocument
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
return|;
comment|//return (gid - 2) / ownerDocument.getOrder() + 1;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getParentNode() 	 */
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
name|long
name|pid
init|=
name|getParentGID
argument_list|()
decl_stmt|;
return|return
name|pid
operator|<
literal|0
condition|?
name|ownerDocument
else|:
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|pid
argument_list|)
return|;
block|}
specifier|public
name|StringBuffer
name|getPath
parameter_list|()
block|{
name|StringBuffer
name|path
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Node
name|parent
init|=
name|getParentNode
argument_list|()
decl_stmt|;
while|while
condition|(
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
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|parent
operator|.
name|getNodeName
argument_list|()
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
return|return
name|path
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getPrefix() 	 */
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
if|if
condition|(
name|nodeName
operator|!=
literal|null
operator|&&
name|nodeName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|>
operator|-
literal|1
condition|)
return|return
name|nodeName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|nodeName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
argument_list|)
return|;
return|return
literal|""
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getPreviousSibling() 	 */
specifier|public
name|Node
name|getPreviousSibling
parameter_list|()
block|{
name|int
name|level
init|=
name|ownerDocument
operator|.
name|getTreeLevel
argument_list|(
name|gid
argument_list|)
decl_stmt|;
name|long
name|pid
init|=
operator|(
name|gid
operator|-
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
operator|)
operator|/
name|ownerDocument
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|firstChildId
init|=
operator|(
name|pid
operator|-
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
operator|)
operator|*
name|ownerDocument
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
name|gid
operator|>
name|firstChildId
condition|)
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|gid
operator|-
literal|1
argument_list|)
return|;
return|return
literal|null
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#hasAttributes() 	 */
specifier|public
name|boolean
name|hasAttributes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#hasChildNodes() 	 */
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node) 	 */
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
literal|"not implemented"
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
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
name|Node
name|insertAfter
parameter_list|(
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
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
name|Node
name|insertBefore
parameter_list|(
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
literal|"not implemented"
argument_list|)
throw|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#isSupported(java.lang.String, java.lang.String) 	 */
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
return|return
literal|false
return|;
block|}
comment|/** 	 *  Get the unique node identifier of the last child of this node. 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|long
name|lastChildID
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#normalize() 	 */
specifier|public
name|void
name|normalize
parameter_list|()
block|{
return|return;
block|}
comment|/** 	 * @see org.w3c.dom.Node#removeChild(org.w3c.dom.Node) 	 */
specifier|public
name|Node
name|removeChild
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node) 	 */
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
literal|null
return|;
block|}
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 *  Set the attributes that belong to this node. 	 * 	 *@param  attribNum  The new attributes value 	 */
specifier|public
name|void
name|setAttributes
parameter_list|(
name|short
name|attribNum
parameter_list|)
block|{
name|attributes
operator|=
name|attribNum
expr_stmt|;
block|}
comment|/** 	 *  Set the number of children. 	 * 	 *@param  count  The new childCount value 	 */
specifier|protected
name|void
name|setChildCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return;
block|}
comment|/** 	 *  Set the unique node identifier of this node. 	 * 	 *@param  gid  The new gID value 	 */
specifier|public
name|void
name|setGID
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
block|}
comment|/** 	 *  Set the internal storage address of this node. 	 * 	 *@param  address  The new internalAddress value 	 */
specifier|public
name|void
name|setInternalAddress
parameter_list|(
name|long
name|address
parameter_list|)
block|{
name|internalAddress
operator|=
name|address
expr_stmt|;
block|}
comment|/** 	 *  Set the node name. 	 * 	 *@param  name  The new nodeName value 	 */
specifier|public
name|void
name|setNodeName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|nodeName
operator|=
name|name
expr_stmt|;
block|}
comment|/** 	 *  Set the node value. 	 * 	 *@param  value             The new nodeValue value 	 *@exception  DOMException  Description of the Exception 	 */
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
block|}
comment|/** 	 *  Set the owner document. 	 * 	 *@param  doc  The new ownerDocument value 	 */
specifier|public
name|void
name|setOwnerDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|ownerDocument
operator|=
operator|(
name|DocumentImpl
operator|)
name|doc
expr_stmt|;
block|}
comment|/** 	 *  Sets the prefix attribute of the NodeImpl object 	 * 	 *@param  prefix            The new prefix value 	 *@exception  DOMException  Description of the Exception 	 */
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
block|}
comment|/** 	 * Method supports. 	 * @param feature 	 * @param version 	 * @return boolean 	 */
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
return|return
literal|false
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  contentHandler    Description of the Parameter 	 *@param  lexicalHandler    Description of the Parameter 	 *@param  first             Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|public
name|void
name|toSAX
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|LexicalHandler
name|lexicalHandler
parameter_list|,
name|boolean
name|first
parameter_list|)
throws|throws
name|SAXException
block|{
name|toSAX
argument_list|(
name|contentHandler
argument_list|,
name|lexicalHandler
argument_list|,
name|first
argument_list|,
operator|new
name|ArrayList
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  contentHandler    Description of the Parameter 	 *@param  lexicalHandler    Description of the Parameter 	 *@param  first             Description of the Parameter 	 *@param  prefixes          Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|public
name|void
name|toSAX
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|LexicalHandler
name|lexicalHandler
parameter_list|,
name|boolean
name|first
parameter_list|,
name|ArrayList
name|prefixes
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|gid
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  top  Description of the Parameter 	 *@return      Description of the Return Value 	 */
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|top
parameter_list|)
block|{
return|return
name|toString
argument_list|()
return|;
block|}
comment|/** 	* Returns the nodeNameRef. 	* @return int 	*/
specifier|public
name|int
name|getNodeNameRef
parameter_list|()
block|{
return|return
name|nodeNameRef
return|;
block|}
comment|/** 	 * Sets the nodeNameRef. 	 * @param nodeNameRef The nodeNameRef to set 	 */
specifier|public
name|void
name|setNodeNameRef
parameter_list|(
name|int
name|nodeNameRef
parameter_list|)
block|{
name|this
operator|.
name|nodeNameRef
operator|=
name|nodeNameRef
expr_stmt|;
block|}
specifier|protected
name|NodeImpl
name|getLastNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
specifier|final
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
name|ownerDocument
argument_list|,
name|node
operator|.
name|gid
argument_list|,
name|node
operator|.
name|internalAddress
argument_list|)
decl_stmt|;
name|Iterator
name|iterator
init|=
name|ownerDocument
operator|.
name|getBroker
argument_list|()
operator|.
name|getNodeIterator
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|getLastNode
argument_list|(
name|iterator
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|NodeImpl
name|getLastNode
parameter_list|(
name|Iterator
name|iterator
parameter_list|,
name|NodeImpl
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|hasChildNodes
argument_list|()
condition|)
block|{
specifier|final
name|long
name|firstChild
init|=
name|node
operator|.
name|firstChildID
argument_list|()
decl_stmt|;
specifier|final
name|long
name|lastChild
init|=
name|firstChild
operator|+
name|node
operator|.
name|getChildCount
argument_list|()
decl_stmt|;
name|NodeImpl
name|next
init|=
literal|null
decl_stmt|;
for|for
control|(
name|long
name|gid
init|=
name|firstChild
init|;
name|gid
operator|<
name|lastChild
condition|;
name|gid
operator|++
control|)
block|{
name|next
operator|=
operator|(
name|NodeImpl
operator|)
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|next
operator|.
name|setGID
argument_list|(
name|gid
argument_list|)
expr_stmt|;
name|next
operator|=
name|getLastNode
argument_list|(
name|iterator
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
else|else
return|return
name|node
return|;
block|}
comment|//	protected NodeImpl getLastNode(NodeImpl node) {
comment|//		if (node.getNodeType() == Node.ELEMENT_NODE)
comment|//			return node.getChildCount() == 0 ? node : getLastNode((NodeImpl) node.getLastChild());
comment|//		else
comment|//			return node;
comment|//	}
comment|/** 		 * Update a child node. This method will only update the child node 		 * but not its potential descendant nodes. 		 *  		 * @param oldChild 		 * @param newChild 		 * @throws DOMException 		 */
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
name|NO_MODIFICATION_ALLOWED_ERR
argument_list|,
literal|"method not allowed on this node type"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

