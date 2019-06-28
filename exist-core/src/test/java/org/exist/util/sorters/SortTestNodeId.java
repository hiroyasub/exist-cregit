begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|sorters
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|io
operator|.
name|VariableByteOutputStream
import|;
end_import

begin_comment
comment|/**  * Mock NodeId.  *  * This work was undertaken as part of the development of the taxonomic  * repository at http://biodiversity.org.au . See<A  * href="ghw-at-anbg.gov.au">Greg&nbsp;Whitbread</A> for further details.  *   * @author pmurray@bigpond.com  * @author pmurray@anbg.gov.au  * @author https://sourceforge.net/users/paulmurray  * @author http://www.users.bigpond.com/pmurray  * @see NodeId  *   */
end_comment

begin_class
class|class
name|SortTestNodeId
implements|implements
name|NodeId
block|{
specifier|final
name|int
name|i
decl_stmt|;
name|SortTestNodeId
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
block|}
specifier|public
name|boolean
name|after
parameter_list|(
name|NodeId
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|before
parameter_list|(
name|NodeId
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|SortTestNodeId
name|arg0
parameter_list|)
block|{
if|if
condition|(
name|i
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Sort ought not be looking at the nodeid"
argument_list|)
throw|;
return|return
name|i
operator|-
name|arg0
operator|.
name|i
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|NodeId
name|arg0
parameter_list|)
block|{
return|return
name|compareTo
argument_list|(
operator|(
name|SortTestNodeId
operator|)
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|int
name|computeRelation
parameter_list|(
name|NodeId
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|NodeId
name|arg0
parameter_list|)
block|{
return|return
name|i
operator|==
operator|(
operator|(
name|SortTestNodeId
operator|)
name|arg0
operator|)
operator|.
name|i
return|;
block|}
specifier|public
name|NodeId
name|getChild
parameter_list|(
name|int
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NodeId
name|getParentId
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|getTreeLevel
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NodeId
name|insertBefore
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NodeId
name|insertNode
parameter_list|(
name|NodeId
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|isChildOf
parameter_list|(
name|NodeId
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|isDescendantOf
parameter_list|(
name|NodeId
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|isDescendantOrSelfOf
parameter_list|(
name|NodeId
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|isSiblingOf
parameter_list|(
name|NodeId
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NodeId
name|newChild
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NodeId
name|nextSibling
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NodeId
name|precedingSibling
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
name|byte
index|[]
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|units
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|VariableByteOutputStream
name|arg0
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NodeId
name|write
parameter_list|(
name|NodeId
name|arg0
parameter_list|,
name|VariableByteOutputStream
name|arg1
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NodeId
name|append
parameter_list|(
name|NodeId
name|other
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

