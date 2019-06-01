begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
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
name|persistent
operator|.
name|ExtNodeSet
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
name|persistent
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
name|dom
operator|.
name|persistent
operator|.
name|NodeSet
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

begin_class
specifier|public
class|class
name|AncestorSelector
implements|implements
name|NodeSelector
block|{
specifier|private
name|NodeSet
name|ancestors
decl_stmt|;
specifier|private
name|NodeSet
name|descendants
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|contextId
decl_stmt|;
specifier|private
name|boolean
name|includeSelf
decl_stmt|;
specifier|private
name|boolean
name|copyMatches
decl_stmt|;
specifier|public
name|AncestorSelector
parameter_list|(
name|NodeSet
name|descendants
parameter_list|,
name|int
name|contextId
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|boolean
name|copyMatches
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|contextId
operator|=
name|contextId
expr_stmt|;
name|this
operator|.
name|includeSelf
operator|=
name|includeSelf
expr_stmt|;
name|this
operator|.
name|copyMatches
operator|=
name|copyMatches
expr_stmt|;
if|if
condition|(
name|descendants
operator|instanceof
name|ExtNodeSet
condition|)
block|{
name|this
operator|.
name|descendants
operator|=
name|descendants
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|ancestors
operator|=
name|descendants
operator|.
name|getAncestors
argument_list|(
name|contextId
argument_list|,
name|includeSelf
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|NodeProxy
name|match
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|descendants
operator|==
literal|null
condition|)
block|{
return|return
name|ancestors
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
name|ExtNodeSet
operator|)
name|descendants
operator|)
operator|.
name|hasDescendantsInSet
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|,
name|includeSelf
argument_list|,
name|contextId
argument_list|,
name|copyMatches
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
