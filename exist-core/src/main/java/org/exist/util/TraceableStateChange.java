begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
comment|/**  * Represents a traceable change of state  *  * @param<S> Information about the state which was modified  * @param<C> the change which was applied to the state  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TraceableStateChange
parameter_list|<
name|S
parameter_list|,
name|C
parameter_list|>
block|{
specifier|private
specifier|final
name|C
name|change
decl_stmt|;
specifier|private
specifier|final
name|StackTraceElement
name|trace
index|[]
decl_stmt|;
specifier|private
specifier|final
name|S
name|state
decl_stmt|;
specifier|private
specifier|final
name|Thread
name|thread
decl_stmt|;
specifier|public
name|TraceableStateChange
parameter_list|(
specifier|final
name|C
name|change
parameter_list|,
specifier|final
name|S
name|subject
parameter_list|)
block|{
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|trace
operator|=
name|Stacktrace
operator|.
name|substack
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getStackTrace
argument_list|()
argument_list|,
literal|2
argument_list|,
name|Stacktrace
operator|.
name|DEFAULT_STACK_TOP
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|subject
expr_stmt|;
name|this
operator|.
name|thread
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|String
name|getId
parameter_list|()
function_decl|;
specifier|public
name|C
name|getChange
parameter_list|()
block|{
return|return
name|change
return|;
block|}
specifier|public
name|S
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|public
name|String
name|describeState
parameter_list|()
block|{
return|return
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|StackTraceElement
index|[]
name|getTrace
parameter_list|()
block|{
return|return
name|trace
return|;
block|}
specifier|public
name|Thread
name|getThread
parameter_list|()
block|{
return|return
name|thread
return|;
block|}
block|}
end_class

end_unit

