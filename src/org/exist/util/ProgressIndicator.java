begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
comment|/*  *  eXist xml document repository and xpath implementation  *  Copyright (C) 2001,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    27. Juni 2002  */
end_comment

begin_class
specifier|public
class|class
name|ProgressIndicator
block|{
specifier|protected
name|double
name|mMax
init|=
literal|1
decl_stmt|;
specifier|protected
name|double
name|mValue
init|=
literal|0
decl_stmt|;
comment|/**      *  Constructor for the ProgressIndicator object      *      *@param  max  Description of the Parameter      */
specifier|public
name|ProgressIndicator
parameter_list|(
name|double
name|max
parameter_list|)
block|{
name|mMax
operator|=
name|max
expr_stmt|;
block|}
comment|/**      *  Sets the value attribute of the ProgressIndicator object      *      *@param  value  The new value value      */
specifier|public
name|void
name|setValue
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|mValue
operator|=
name|value
expr_stmt|;
block|}
comment|/**      *  Gets the percentage attribute of the ProgressIndicator object      *      *@return    The percentage value      */
specifier|public
name|int
name|getPercentage
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
operator|(
operator|(
name|mValue
operator|/
name|mMax
operator|)
operator|*
literal|100
operator|)
return|;
block|}
comment|/**      *  Gets the max attribute of the ProgressIndicator object      *      *@return    The max value      */
specifier|public
name|double
name|getMax
parameter_list|()
block|{
return|return
name|mMax
return|;
block|}
comment|/**      *  Gets the value attribute of the ProgressIndicator object      *      *@return    The value value      */
specifier|public
name|double
name|getValue
parameter_list|()
block|{
return|return
name|mValue
return|;
block|}
block|}
end_class

end_unit

