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
name|util
operator|.
name|serializer
operator|.
name|encodings
package|;
end_package

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Latin1CharSet
extends|extends
name|CharacterSet
block|{
specifier|protected
specifier|final
specifier|static
name|CharacterSet
name|instance
init|=
operator|new
name|Latin1CharSet
argument_list|()
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.encodings.CharacterSet#inCharacterSet(char) 	 */
specifier|public
name|boolean
name|inCharacterSet
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
name|ch
operator|<=
literal|0xff
return|;
block|}
specifier|public
specifier|static
name|CharacterSet
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
block|}
end_class

end_unit

