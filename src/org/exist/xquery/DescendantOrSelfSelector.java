begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DescendantOrSelfSelector
extends|extends
name|DescendantSelector
block|{
specifier|public
name|DescendantOrSelfSelector
parameter_list|(
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|contextId
parameter_list|)
block|{
name|super
argument_list|(
name|contextSet
argument_list|,
name|contextId
argument_list|)
expr_stmt|;
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
name|NodeProxy
name|contextNode
init|=
name|context
operator|.
name|parentWithChild
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextNode
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|Expression
operator|.
name|NO_CONTEXT_ID
operator|!=
name|contextId
condition|)
block|{
name|p
operator|.
name|deepCopyContext
argument_list|(
name|contextNode
argument_list|,
name|contextId
argument_list|)
expr_stmt|;
block|}
else|else
name|p
operator|.
name|copyContext
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

