begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|XSLContext
import|;
end_import

begin_comment
comment|/**  * Top-level elements fall into two categories: declarations, and user-defined data elements.   * Top-level elements whose names are in the XSLT namespace are declarations.   * Top-level elements in any other namespace are user-defined data elements.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Declaration
extends|extends
name|XSLPathExpr
block|{
specifier|public
name|Declaration
parameter_list|(
name|XSLContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|SimpleConstructor
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|instanceof
name|Text
condition|)
block|{
return|return;
comment|//ignore text nodes
block|}
name|steps
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

