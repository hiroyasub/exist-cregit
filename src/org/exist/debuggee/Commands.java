begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
package|;
end_package

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Commands
block|{
specifier|public
name|int
name|WAIT
init|=
literal|0
decl_stmt|;
specifier|public
name|int
name|STOP_ON_FIRST_LINE
init|=
literal|1
decl_stmt|;
specifier|public
name|int
name|STEP_INTO
init|=
literal|1
decl_stmt|;
specifier|public
name|int
name|STEP_OUT
init|=
literal|2
decl_stmt|;
specifier|public
name|int
name|STEP_OVER
init|=
literal|3
decl_stmt|;
block|}
end_interface

end_unit

