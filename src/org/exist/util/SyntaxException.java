begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist xml document repository and xpath implementation  *  Copyright (C) 2000,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *created    24. Juni 2002  */
end_comment

begin_class
specifier|public
class|class
name|SyntaxException
extends|extends
name|Exception
block|{
comment|/**  Constructor for the PermissionDeniedException object */
specifier|public
name|SyntaxException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Constructor for the PermissionDeniedException object      *      *@param  message  Description of the Parameter      */
specifier|public
name|SyntaxException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

