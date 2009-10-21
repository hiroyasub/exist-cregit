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
name|debugger
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|model
operator|.
name|Breakpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|model
operator|.
name|Location
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|model
operator|.
name|Variable
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|DebuggingSource
block|{
comment|/** 	 * Starts or resumes the script until a new breakpoint is reached, or the end of the script is reached. 	 */
specifier|public
name|void
name|run
parameter_list|()
function_decl|;
comment|/** 	 * Steps to the next statement, if there is a function call involved it will break on the first statement in that function 	 */
specifier|public
name|void
name|stepInto
parameter_list|()
function_decl|;
comment|/** 	 * Steps to the next statement, if there is a function call on the line from which the step_over is issued then the debugger engine will stop at the statement after the function call in the same scope as from where the command was issued 	 */
specifier|public
name|void
name|stepOver
parameter_list|()
function_decl|;
comment|/** 	 * Steps out of the current scope and breaks on the statement after returning from the current function. (Also called 'finish' in GDB) 	 */
specifier|public
name|void
name|stepOut
parameter_list|()
function_decl|;
comment|/** 	 * Ends execution of the script immediately, the debugger engine may not respond, though if possible should be designed to do so. The script will be terminated right away and be followed by a disconnection of the network connection from the IDE (and debugger engine if required in multi request apache processes). 	 */
specifier|public
name|void
name|stop
parameter_list|()
function_decl|;
comment|/** 	 * Stops interaction with the debugger engine. Once this command is executed, the IDE will no longer be able to communicate with the debugger engine. This does not end execution of the script as does the stop command above, but rather detaches from debugging. Support of this continuation command is optional, and the IDE should verify support for it via the feature_get command. If the IDE has created stdin/stdout/stderr pipes for execution of the script (eg. an interactive shell or other console to catch script output), it should keep those open and usable by the process until the process has terminated normally. 	 */
specifier|public
name|void
name|detach
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isSuspended
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isTerminated
parameter_list|()
function_decl|;
specifier|public
name|Variable
index|[]
name|getVariables
parameter_list|()
function_decl|;
specifier|public
name|Location
index|[]
name|getStackFrames
parameter_list|()
function_decl|;
specifier|public
name|Breakpoint
name|newBreakpoint
parameter_list|()
function_decl|;
specifier|public
name|String
name|getText
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

