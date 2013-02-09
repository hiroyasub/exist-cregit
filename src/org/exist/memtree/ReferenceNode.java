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
name|exist
operator|.
name|dom
operator|.
name|NodeProxy
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

begin_comment
comment|/**  * DOCUMENT ME!  *  * @author  wolf  */
end_comment

begin_class
specifier|public
class|class
name|ReferenceNode
extends|extends
name|NodeImpl
block|{
comment|/**      * Creates a new ReferenceNode object.      *      * @param  doc      * @param  nodeNumber      */
specifier|public
name|ReferenceNode
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
specifier|public
name|NodeProxy
name|getReference
parameter_list|()
block|{
specifier|final
name|int
name|p
init|=
name|document
operator|.
name|alpha
index|[
name|nodeNumber
index|]
decl_stmt|;
return|return
operator|(
name|document
operator|.
name|references
index|[
name|p
index|]
operator|)
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
literal|"reference[ "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" ]"
argument_list|)
expr_stmt|;
return|return
operator|(
name|result
operator|.
name|toString
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
comment|//TODO : improve performance ?
return|return
operator|(
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
comment|//TODO : improve performance ?
return|return
operator|(
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
operator|.
name|getLocalName
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NamedNodeMap
name|getAttributes
parameter_list|()
block|{
comment|//TODO : improve performance ?
return|return
operator|(
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
operator|.
name|getAttributes
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
comment|//TODO : improve performance ?
comment|//TODO : how to make this node a reference as well ?
return|return
operator|(
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
operator|.
name|getFirstChild
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
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
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
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
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
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
comment|// TODO Auto-generated method stub
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
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
operator|.
name|getNodeValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

