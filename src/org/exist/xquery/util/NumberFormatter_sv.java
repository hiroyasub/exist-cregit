begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Swedish language formatting of numbers and dates.  *  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|NumberFormatter_sv
extends|extends
name|NumberFormatter
block|{
specifier|public
name|NumberFormatter_sv
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
name|super
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOrdinalSuffix
parameter_list|(
name|long
name|number
parameter_list|)
block|{
comment|// Swedish date ordinals do not usually use suffices,
comment|// so this method is a bit coarse for both numbers *and* dates.
comment|// For dates it should preferrably be a switch with:
comment|//  return "";
if|if
condition|(
name|number
operator|>
literal|10
operator|&&
name|number
operator|<
literal|20
condition|)
block|{
return|return
literal|":e"
return|;
block|}
specifier|final
name|long
name|mod
init|=
name|number
operator|%
literal|10
decl_stmt|;
if|if
condition|(
name|mod
operator|==
literal|1
operator|||
name|mod
operator|==
literal|2
condition|)
block|{
return|return
literal|":a"
return|;
block|}
else|else
block|{
return|return
literal|":e"
return|;
block|}
block|}
block|}
end_class

end_unit

