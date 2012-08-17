begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-04 Wolfgang M. Meier (wolfgang@exist-db.org)  *  and others (see http://exist-db.org) * *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. * *  You should have received a copy of the GNU Lesser General Public License *  along with this program; if not, write to the Free Software *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. *  *  $Id$ */
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
name|QName
import|;
end_import

begin_comment
comment|/**  * Represents a local variable as declared by for and let.  *   * Local variables are stored as a linked list.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|LocalVariable
extends|extends
name|VariableImpl
block|{
specifier|protected
name|LocalVariable
name|before
init|=
literal|null
decl_stmt|;
specifier|protected
name|LocalVariable
name|after
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|isClosureVar
init|=
literal|false
decl_stmt|;
specifier|public
name|LocalVariable
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|super
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LocalVariable
parameter_list|(
name|LocalVariable
name|other
parameter_list|,
name|boolean
name|isClosureVar
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|this
operator|.
name|isClosureVar
operator|=
name|isClosureVar
expr_stmt|;
block|}
specifier|public
name|LocalVariable
parameter_list|(
name|LocalVariable
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addAfter
parameter_list|(
name|LocalVariable
name|var
parameter_list|)
block|{
name|this
operator|.
name|after
operator|=
name|var
expr_stmt|;
name|var
operator|.
name|before
operator|=
name|this
expr_stmt|;
block|}
specifier|public
name|boolean
name|isClosureVar
parameter_list|()
block|{
return|return
name|isClosureVar
return|;
block|}
block|}
end_class

end_unit

