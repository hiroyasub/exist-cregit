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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Represents a traceable history of state changes  *  * @param<S> Information about the state which was modified  * @param<C> the change which was applied to the state  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|TraceableStateChanges
parameter_list|<
name|S
parameter_list|,
name|C
parameter_list|>
block|{
specifier|private
name|List
argument_list|<
name|TraceableStateChange
argument_list|<
name|S
argument_list|,
name|C
argument_list|>
argument_list|>
name|stateChangeTrace
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Add a state change to the tail.      *      * @param stateChange the state change      */
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|TraceableStateChange
argument_list|<
name|S
argument_list|,
name|C
argument_list|>
name|stateChange
parameter_list|)
block|{
name|stateChangeTrace
operator|.
name|add
argument_list|(
name|stateChange
argument_list|)
expr_stmt|;
block|}
comment|/**      * Clear all state changes      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|stateChangeTrace
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Makes a copy of the list of state changes      * useful for archival purposes      *      * @return A copy of the state changes      */
annotation|@
name|Override
specifier|public
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
specifier|final
name|TraceableStateChanges
argument_list|<
name|S
argument_list|,
name|C
argument_list|>
name|copy
init|=
operator|new
name|TraceableStateChanges
argument_list|<>
argument_list|()
decl_stmt|;
name|copy
operator|.
name|stateChangeTrace
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|stateChangeTrace
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
comment|/**      * Writes the history fo state changes to the      * provided logger at TRACE level      *      * @param logger The logger to write the changes to      */
specifier|public
specifier|final
name|void
name|logTrace
parameter_list|(
specifier|final
name|Logger
name|logger
parameter_list|)
block|{
if|if
condition|(
operator|!
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This is only enabled at TRACE level logging"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stateChangeTrace
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|TraceableStateChange
argument_list|<
name|S
argument_list|,
name|C
argument_list|>
name|traceableStateChange
init|=
name|stateChangeTrace
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d: %s: %s(%s) from: %s(%s)"
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|traceableStateChange
operator|.
name|getId
argument_list|()
argument_list|,
name|traceableStateChange
operator|.
name|getChange
argument_list|()
argument_list|,
name|traceableStateChange
operator|.
name|describeState
argument_list|()
argument_list|,
name|traceableStateChange
operator|.
name|getThread
argument_list|()
argument_list|,
name|Stacktrace
operator|.
name|asString
argument_list|(
name|traceableStateChange
operator|.
name|getTrace
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

