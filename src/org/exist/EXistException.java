begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist xml document repository and xpath implementation  *  Copyright (C) 2000,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    24. Juni 2002  */
end_comment

begin_class
specifier|public
class|class
name|EXistException
extends|extends
name|Exception
block|{
specifier|protected
name|Throwable
name|inner
init|=
literal|null
decl_stmt|;
comment|/**  Constructor for the EXistException object */
specifier|public
name|EXistException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Constructor for the EXistException object      *      *@param  inner  Description of the Parameter      */
specifier|public
name|EXistException
parameter_list|(
name|Throwable
name|inner
parameter_list|)
block|{
name|super
argument_list|(
name|inner
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|inner
operator|=
name|inner
expr_stmt|;
block|}
comment|/**      *  Constructor for the EXistException object      *      *@param  message  Description of the Parameter      */
specifier|public
name|EXistException
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
comment|/**      *  Gets the exception attribute of the EXistException object      *      *@return    The exception value      */
specifier|public
name|Throwable
name|getException
parameter_list|()
block|{
return|return
name|inner
return|;
block|}
comment|/**      *  Gets the message attribute of the EXistException object      *      *@return    The message value      */
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
name|super
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|inner
operator|!=
literal|null
condition|)
name|inner
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

