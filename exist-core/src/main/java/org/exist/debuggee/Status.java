begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|Status
block|{
specifier|public
name|String
name|FIRST_RUN
init|=
literal|"FIRST_RUN"
decl_stmt|;
comment|/** 	 * State prior to execution of any code 	 */
specifier|public
name|String
name|STARTING
init|=
literal|"starting"
decl_stmt|;
comment|/** 	 * Code is currently executing. Note that this state would only be seen with async support turned on, otherwise the typical state during IDE/debugger interaction would be 'break' 	 */
specifier|public
name|String
name|RUNNING
init|=
literal|"running"
decl_stmt|;
comment|/** 	 * Code execution is paused, for whatever reason (see below), and the IDE/debugger can pass information back and forth. 	 */
specifier|public
name|String
name|BREAK
init|=
literal|"break"
decl_stmt|;
comment|/** 	 * State after completion of code execution. This typically happens at the end of code execution, allowing the IDE to further interact with the debugger engine (for example, to collect performance data, or use other extended commands). 	 */
specifier|public
name|String
name|STOPPING
init|=
literal|"stopping"
decl_stmt|;
comment|/** 	 * IDE is detached from process, no further interaction is possible. 	 */
specifier|public
name|String
name|STOPPED
init|=
literal|"stopped"
decl_stmt|;
block|}
end_interface

end_unit

