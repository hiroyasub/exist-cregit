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
name|Node
import|;
end_import

begin_class
specifier|public
class|class
name|AttributeImpl
extends|extends
name|NodeImpl
implements|implements
name|Attr
block|{
comment|/** 	 * @param doc 	 * @param nodeNumber 	 */
specifier|public
name|AttributeImpl
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Attr#getName() 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|document
operator|.
name|attrName
index|[
name|nodeNumber
index|]
operator|.
name|toString
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
name|document
operator|.
name|attrName
index|[
name|nodeNumber
index|]
operator|.
name|toString
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
name|document
operator|.
name|attrName
index|[
name|nodeNumber
index|]
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
name|document
operator|.
name|attrName
index|[
name|nodeNumber
index|]
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
name|document
operator|.
name|attrName
index|[
name|nodeNumber
index|]
operator|.
name|getPrefix
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Attr#getSpecified() 	 */
specifier|public
name|boolean
name|getSpecified
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Attr#getValue() 	 */
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|document
operator|.
name|attrValue
index|[
name|nodeNumber
index|]
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
name|document
operator|.
name|attrValue
index|[
name|nodeNumber
index|]
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Attr#setValue(java.lang.String) 	 */
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Attr#getOwnerElement() 	 */
specifier|public
name|Element
name|getOwnerElement
parameter_list|()
block|{
return|return
operator|(
name|Element
operator|)
name|document
operator|.
name|getNode
argument_list|(
name|document
operator|.
name|attrParent
index|[
name|nodeNumber
index|]
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getParentNode() 	 */
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

