begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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

begin_comment
comment|/**  * A dynamically constructed namespace node. Used to track namespace declarations in elements. Implements Attr, so it can be treated as a normal  * attribute.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|NamespaceNode
extends|extends
name|NodeImpl
implements|implements
name|Attr
block|{
comment|/**      * Creates a new NamespaceNode object.      *      * @param doc      * @param nodeNumber      */
specifier|public
name|NamespaceNode
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
name|short
name|getNodeType
parameter_list|()
block|{
comment|//TOUNDERSTAND : return value
comment|//XQuery doesn't support namespace nodes
comment|//so, mapping as an attribute at *serialization tile*  makes sense
comment|//however, the Query parser should not accept them in constructors !
return|return
name|NodeImpl
operator|.
name|NAMESPACE_NODE
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
name|NAMESPACE
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
name|String
name|getValue
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
name|getLastChild
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
name|Element
name|getOwnerElement
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Element
operator|)
name|document
operator|.
name|getNode
argument_list|(
name|document
operator|.
name|namespaceParent
index|[
name|nodeNumber
index|]
argument_list|)
operator|)
return|;
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
literal|false
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
name|NAMESPACE
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
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
literal|"namespace {"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
block|}
end_class

end_unit

