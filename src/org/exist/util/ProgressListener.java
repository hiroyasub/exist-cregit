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
comment|/* eXist xml document repository and xpath implementation  * Copyright (C) 2001,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventListener
import|;
end_import

begin_interface
specifier|public
interface|interface
name|ProgressListener
extends|extends
name|EventListener
block|{
specifier|public
name|void
name|update
parameter_list|(
name|int
name|percent
parameter_list|,
name|int
name|max
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

