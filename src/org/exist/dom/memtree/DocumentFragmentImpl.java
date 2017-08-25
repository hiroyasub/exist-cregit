begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|w3c
operator|.
name|dom
operator|.
name|DocumentFragment
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
name|NodeList
import|;
end_import

begin_class
specifier|public
class|class
name|DocumentFragmentImpl
extends|extends
name|NodeImpl
argument_list|<
name|DocumentFragmentImpl
argument_list|>
implements|implements
name|DocumentFragment
block|{
specifier|public
name|DocumentFragmentImpl
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|DocumentImpl
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|0
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
return|return
name|DOCUMENT_FRAGMENT_NODE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
name|document
operator|.
name|hasChildNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeList
name|getChildNodes
parameter_list|()
block|{
return|return
name|document
operator|.
name|getChildNodes
argument_list|()
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
name|document
operator|.
name|getAttributes
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
name|document
operator|.
name|selectAttributes
argument_list|(
name|test
argument_list|,
name|result
argument_list|)
expr_stmt|;
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
name|document
operator|.
name|selectDescendantAttributes
argument_list|(
name|test
argument_list|,
name|result
argument_list|)
expr_stmt|;
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
name|document
operator|.
name|selectChildren
argument_list|(
name|test
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|DocumentFragmentImpl
name|other
parameter_list|)
block|{
return|return
name|document
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|document
argument_list|)
return|;
block|}
block|}
end_class

end_unit

