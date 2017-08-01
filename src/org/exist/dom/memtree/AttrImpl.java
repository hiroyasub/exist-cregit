begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
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
name|TypeInfo
import|;
end_import

begin_class
specifier|public
class|class
name|AttrImpl
extends|extends
name|NodeImpl
implements|implements
name|Attr
block|{
specifier|public
specifier|static
specifier|final
name|int
name|ATTR_CDATA_TYPE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ATTR_ID_TYPE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ATTR_IDREF_TYPE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ATTR_IDREFS_TYPE
init|=
literal|3
decl_stmt|;
comment|/**      * Creates a new AttributeImpl object.      *      * @param doc      * @param nodeNumber      */
specifier|public
name|AttrImpl
parameter_list|(
specifier|final
name|DocumentImpl
name|doc
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|document
operator|.
name|attrNodeId
index|[
name|nodeNumber
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
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
annotation|@
name|Override
specifier|public
name|short
name|getNodeType
parameter_list|()
block|{
return|return
name|Node
operator|.
name|ATTRIBUTE_NODE
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ATTRIBUTE
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
specifier|final
name|Node
name|parent
init|=
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
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|parent
operator|.
name|getBaseURI
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getNextSibling
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getSpecified
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|String
name|getStringValue
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
annotation|@
name|Override
specifier|public
name|void
name|setNodeValue
parameter_list|(
specifier|final
name|String
name|nodeValue
parameter_list|)
throws|throws
name|DOMException
block|{
comment|//This method was added to enable the SQL XQuery Extension Module
comment|//to change the value of an attribute after the fact - Andrzej
name|document
operator|.
name|attrValue
index|[
name|nodeNumber
index|]
operator|=
name|nodeValue
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|DOMException
block|{
name|document
operator|.
name|attrValue
index|[
name|nodeNumber
index|]
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|selectDescendantAttributes
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|,
specifier|final
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Node
name|selectParentNode
parameter_list|()
block|{
return|return
name|getOwnerElement
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|selectAncestors
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
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|NodeImpl
operator|)
name|getOwnerElement
argument_list|()
operator|)
operator|.
name|selectAncestors
argument_list|(
literal|true
argument_list|,
name|test
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TypeInfo
name|getSchemaTypeInfo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isId
parameter_list|()
block|{
return|return
operator|(
name|document
operator|.
name|attrType
index|[
name|nodeNumber
index|]
operator|==
name|ATTR_ID_TYPE
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ATTRIBUTE
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"in-memory#attribute {"
operator|+
name|getQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"} {"
operator|+
name|getValue
argument_list|()
operator|+
literal|"} "
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|selectAttributes
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|,
specifier|final
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//do nothing, which will return an empty sequence
block|}
annotation|@
name|Override
specifier|public
name|void
name|selectChildren
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|,
specifier|final
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//do nothing, which will return an empty sequence
block|}
block|}
end_class

end_unit

