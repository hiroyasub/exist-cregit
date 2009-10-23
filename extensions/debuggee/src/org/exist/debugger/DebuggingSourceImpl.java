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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|BreakpointImpl
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DebuggingSourceImpl
implements|implements
name|DebuggingSource
block|{
specifier|private
name|Debugger
name|debugger
decl_stmt|;
specifier|private
name|String
name|fileURI
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
name|breakpoints
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|DebuggingSourceImpl
parameter_list|(
name|Debugger
name|debugger
parameter_list|,
name|String
name|fileURI
parameter_list|)
block|{
name|this
operator|.
name|debugger
operator|=
name|debugger
expr_stmt|;
name|this
operator|.
name|fileURI
operator|=
name|fileURI
expr_stmt|;
block|}
specifier|public
name|Breakpoint
name|getBreakpoint
parameter_list|()
block|{
name|BreakpointImpl
name|breakpoint
init|=
operator|new
name|BreakpointImpl
argument_list|()
decl_stmt|;
name|breakpoint
operator|.
name|setFilename
argument_list|(
name|fileURI
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|setDebuggingSource
argument_list|(
name|debugger
argument_list|)
expr_stmt|;
return|return
name|breakpoint
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#detach() 	 */
specifier|public
name|void
name|detach
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#getStackFrames() 	 */
specifier|public
name|Location
index|[]
name|getStackFrames
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#getVariables() 	 */
specifier|public
name|Variable
index|[]
name|getVariables
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#isSuspended() 	 */
specifier|public
name|boolean
name|isSuspended
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#isTerminated() 	 */
specifier|public
name|boolean
name|isTerminated
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#removeBreakpoint(org.exist.debugger.model.Breakpoint) 	 */
specifier|public
name|void
name|removeBreakpoint
parameter_list|(
name|Breakpoint
name|breakpoint
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#removeBreakpoints() 	 */
specifier|public
name|void
name|removeBreakpoints
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#run() 	 */
specifier|public
name|void
name|run
parameter_list|()
block|{
name|debugger
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#stepInto() 	 */
specifier|public
name|void
name|stepInto
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#stepOut() 	 */
specifier|public
name|void
name|stepOut
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#stepOver() 	 */
specifier|public
name|void
name|stepOver
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.DebuggingSource#stop() 	 */
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
specifier|public
name|Breakpoint
name|newBreakpoint
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
specifier|private
name|String
name|code
init|=
literal|null
decl_stmt|;
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|code
return|;
block|}
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|code
operator|=
name|text
expr_stmt|;
block|}
block|}
end_class

end_unit

