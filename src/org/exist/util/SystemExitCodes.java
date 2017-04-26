begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
comment|/**  * Definitions of codes to use with {@link System#exit(int)}  */
end_comment

begin_class
specifier|public
class|class
name|SystemExitCodes
block|{
specifier|public
specifier|final
specifier|static
name|int
name|OK_EXIT_CODE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|CATCH_ALL_GENERAL_ERROR_EXIT_CODE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|INVALID_ARGUMENT_EXIT_CODE
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NO_BROKER_EXIT_CODE
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TERMINATED_EARLY_EXIT_CODE
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PERMISSION_DENIED_EXIT_CODE
init|=
literal|6
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IO_ERROR_EXIT_CODE
init|=
literal|7
decl_stmt|;
block|}
end_class

end_unit
