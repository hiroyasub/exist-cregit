begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2003-2016 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|EXistException
import|;
end_import

begin_comment
comment|/**  * Traces the lease of a Broker  *  * Note that tracing the stack is expensive  * this should only be done in debug mode by  * enabling the System Property {@link #TRACE_BROKERS_PROPERTY_NAME}  */
end_comment

begin_class
specifier|public
class|class
name|BrokerWatchdog
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TRACE_BROKERS_PROPERTY_NAME
init|=
literal|"trace.brokers"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|df
init|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EOL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
class|class
name|WatchedBroker
block|{
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|long
name|timeAdded
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|trace
decl_stmt|;
name|WatchedBroker
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|timeAdded
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|trace
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|trace
argument_list|()
expr_stmt|;
block|}
name|void
name|trace
parameter_list|()
block|{
name|trace
operator|.
name|append
argument_list|(
literal|"Reference count: "
argument_list|)
operator|.
name|append
argument_list|(
name|broker
operator|.
name|getReferenceCount
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|EOL
argument_list|)
expr_stmt|;
specifier|final
name|StackTraceElement
index|[]
name|stack
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
specifier|final
name|int
name|showElementCount
init|=
name|stack
operator|.
name|length
operator|>
literal|20
condition|?
literal|20
else|:
name|stack
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|4
init|;
name|i
operator|<
name|showElementCount
condition|;
name|i
operator|++
control|)
block|{
name|trace
operator|.
name|append
argument_list|(
name|stack
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|EOL
argument_list|)
expr_stmt|;
block|}
name|trace
operator|.
name|append
argument_list|(
name|EOL
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
name|Map
argument_list|<
name|DBBroker
argument_list|,
name|WatchedBroker
argument_list|>
name|watched
init|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
specifier|final
name|WatchedBroker
name|old
init|=
name|watched
operator|.
name|get
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|checkForTimeout
argument_list|()
expr_stmt|;
name|watched
operator|.
name|put
argument_list|(
name|broker
argument_list|,
operator|new
name|WatchedBroker
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|old
operator|.
name|timeAdded
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|old
operator|.
name|trace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
name|watched
operator|.
name|remove
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|get
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
specifier|final
name|WatchedBroker
name|w
init|=
name|watched
operator|.
name|get
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
return|return
name|w
operator|.
name|trace
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|""
return|;
block|}
specifier|public
name|void
name|checkForTimeout
parameter_list|()
throws|throws
name|EXistException
block|{
for|for
control|(
specifier|final
name|WatchedBroker
name|broker
range|:
name|watched
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|broker
operator|.
name|timeAdded
operator|>
literal|30000
condition|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Broker: "
operator|+
name|broker
operator|.
name|broker
operator|.
name|getId
argument_list|()
operator|+
literal|" did not return for 30sec."
operator|+
name|EOL
operator|+
name|EOL
operator|+
name|broker
operator|.
name|trace
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|dump
parameter_list|(
specifier|final
name|PrintWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"Active brokers:"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|WatchedBroker
name|broker
range|:
name|watched
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|format
argument_list|(
literal|"%20s: %s%s"
argument_list|,
literal|"Broker"
argument_list|,
name|broker
operator|.
name|broker
operator|.
name|getId
argument_list|()
argument_list|,
name|EOL
argument_list|)
expr_stmt|;
name|writer
operator|.
name|format
argument_list|(
literal|"%20s: %s%s"
argument_list|,
literal|"Active since"
argument_list|,
name|df
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|broker
operator|.
name|timeAdded
argument_list|)
argument_list|)
argument_list|,
name|EOL
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Stack:"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
name|broker
operator|.
name|trace
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"----------------------------------------------------------------"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
