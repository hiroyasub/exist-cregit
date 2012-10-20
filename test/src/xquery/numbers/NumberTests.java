begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|xquery
operator|.
name|numbers
package|;
end_package

begin_import
import|import
name|xquery
operator|.
name|TestRunner
import|;
end_import

begin_class
specifier|public
class|class
name|NumberTests
extends|extends
name|TestRunner
block|{
annotation|@
name|Override
specifier|protected
name|String
name|getDirectory
parameter_list|()
block|{
return|return
literal|"test/src/xquery/numbers"
return|;
block|}
block|}
end_class

end_unit

