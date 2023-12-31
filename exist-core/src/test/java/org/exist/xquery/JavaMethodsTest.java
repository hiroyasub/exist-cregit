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

begin_comment
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_class
specifier|public
class|class
name|JavaMethodsTest
block|{
specifier|public
specifier|static
name|String
name|echo
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{
return|return
name|arg0
operator|+
literal|" "
operator|+
name|arg1
return|;
block|}
specifier|public
specifier|static
name|String
name|echo
parameter_list|(
name|String
name|arg0
parameter_list|,
name|boolean
name|bool
parameter_list|)
block|{
return|return
name|arg0
operator|+
literal|" is "
operator|+
name|bool
return|;
block|}
specifier|public
specifier|static
name|int
name|add
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
return|return
name|a
operator|+
name|b
return|;
block|}
specifier|public
specifier|static
name|double
name|add
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|)
block|{
return|return
name|a
operator|+
name|b
return|;
block|}
specifier|private
name|String
name|message
decl_stmt|;
specifier|public
name|JavaMethodsTest
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|message
operator|=
name|msg
expr_stmt|;
block|}
specifier|public
name|String
name|display
parameter_list|(
name|String
name|welcome
parameter_list|)
block|{
return|return
name|welcome
operator|+
literal|": "
operator|+
name|message
return|;
block|}
block|}
end_class

end_unit

