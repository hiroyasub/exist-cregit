begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
package|;
end_package

begin_comment
comment|/**  * A function definition, consisting of a signature and the implementing class.  *   * Used by modules to define the available functions.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FunctionDef
block|{
specifier|protected
name|FunctionSignature
name|signature
decl_stmt|;
specifier|protected
name|Class
name|implementingClass
decl_stmt|;
specifier|public
name|FunctionDef
parameter_list|(
name|FunctionSignature
name|signature
parameter_list|,
name|Class
name|implementingClass
parameter_list|)
block|{
name|this
operator|.
name|signature
operator|=
name|signature
expr_stmt|;
name|this
operator|.
name|implementingClass
operator|=
name|implementingClass
expr_stmt|;
block|}
specifier|public
name|FunctionSignature
name|getSignature
parameter_list|()
block|{
return|return
name|signature
return|;
block|}
specifier|public
name|Class
name|getImplementingClass
parameter_list|()
block|{
return|return
name|implementingClass
return|;
block|}
block|}
end_class

end_unit

