begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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

begin_interface
specifier|public
interface|interface
name|RewritableExpression
block|{
specifier|public
name|void
name|replace
parameter_list|(
name|Expression
name|oldExpr
parameter_list|,
name|Expression
name|newExpr
parameter_list|)
function_decl|;
specifier|public
name|void
name|remove
parameter_list|(
name|Expression
name|oldExpr
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|insertAfter
parameter_list|(
name|Expression
name|exprBefore
parameter_list|,
name|Expression
name|newExpr
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|Expression
name|getPrevious
parameter_list|(
name|Expression
name|current
parameter_list|)
function_decl|;
specifier|public
name|Expression
name|getFirst
parameter_list|()
function_decl|;
name|boolean
name|allowMixedNodesInReturn
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

