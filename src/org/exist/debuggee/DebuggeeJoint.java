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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dbgp
operator|.
name|packets
operator|.
name|Command
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
name|Breakpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|TerminatedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_comment
comment|//TODO: rename DebuggeeRuntime ?
end_comment

begin_interface
specifier|public
interface|interface
name|DebuggeeJoint
block|{
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
function_decl|;
specifier|public
name|void
name|expressionStart
parameter_list|(
name|Expression
name|expr
parameter_list|)
throws|throws
name|TerminatedException
function_decl|;
specifier|public
name|void
name|expressionEnd
parameter_list|(
name|Expression
name|expr
parameter_list|)
function_decl|;
specifier|public
name|void
name|stackEnter
parameter_list|(
name|Expression
name|expr
parameter_list|)
throws|throws
name|TerminatedException
function_decl|;
specifier|public
name|void
name|stackLeave
parameter_list|(
name|Expression
name|expr
parameter_list|)
function_decl|;
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|featureSet
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
specifier|public
name|String
name|featureGet
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|public
name|void
name|continuation
parameter_list|(
name|CommandContinuation
name|command
parameter_list|)
function_decl|;
specifier|public
name|CommandContinuation
name|getCurrentCommand
parameter_list|()
function_decl|;
comment|//	public String run();
comment|//
comment|//	public String stepInto();
comment|//	public String stepOut();
comment|//	public String stepOver();
comment|//
comment|//	public String stop();
specifier|public
name|List
argument_list|<
name|Expression
argument_list|>
name|stackGet
parameter_list|()
function_decl|;
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|getVariables
parameter_list|()
function_decl|;
specifier|public
name|Variable
name|getVariable
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|//breakpoints methods
specifier|public
name|int
name|setBreakpoint
parameter_list|(
name|Breakpoint
name|breakpoint
parameter_list|)
function_decl|;
specifier|public
name|Breakpoint
name|getBreakpoint
parameter_list|(
name|int
name|breakpointID
parameter_list|)
function_decl|;
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
name|getBreakpoints
parameter_list|()
function_decl|;
specifier|public
name|Breakpoint
name|removeBreakpoint
parameter_list|(
name|int
name|breakpointID
parameter_list|)
function_decl|;
specifier|public
name|void
name|sessionClosed
parameter_list|(
name|boolean
name|disconnect
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

