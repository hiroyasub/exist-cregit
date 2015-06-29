begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
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
name|LogManager
import|;
end_import

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
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|JobException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|JobException
operator|.
name|JobExceptionAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|UserJavaJob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
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
name|value
operator|.
name|Sequence
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|SessionManager
block|{
specifier|public
specifier|final
specifier|static
name|long
name|TIMEOUT
init|=
literal|120000
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|long
name|TIMEOUT_CHECK_PERIOD
init|=
literal|2000
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NO_SESSION
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SessionManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
class|class
name|QueryResult
block|{
name|long
name|lastAccess
decl_stmt|;
name|String
name|queryString
decl_stmt|;
name|Sequence
name|sequence
decl_stmt|;
specifier|private
name|QueryResult
parameter_list|(
name|String
name|query
parameter_list|,
name|Sequence
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|queryString
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|sequence
operator|=
name|sequence
expr_stmt|;
name|this
operator|.
name|lastAccess
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Sequence
name|sequence
parameter_list|()
block|{
name|lastAccess
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
return|return
name|sequence
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|TimeoutCheck
extends|extends
name|UserJavaJob
block|{
specifier|public
name|TimeoutCheck
parameter_list|()
block|{
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"REST_TimeoutCheck"
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
block|}
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|BrokerPool
name|brokerpool
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|params
parameter_list|)
throws|throws
name|JobException
block|{
specifier|final
name|SessionManager
name|manager
init|=
operator|(
name|SessionManager
operator|)
name|params
operator|.
name|get
argument_list|(
literal|"session-manager"
argument_list|)
decl_stmt|;
if|if
condition|(
name|manager
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JobException
argument_list|(
name|JobExceptionAction
operator|.
name|JOB_ABORT
argument_list|,
literal|"parameter 'session-manager' is not set"
argument_list|)
throw|;
block|}
name|manager
operator|.
name|timeoutCheck
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|QueryResult
index|[]
name|slots
init|=
operator|new
name|QueryResult
index|[
literal|32
index|]
decl_stmt|;
specifier|public
name|SessionManager
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
specifier|final
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"session-manager"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|pool
operator|.
name|getScheduler
argument_list|()
operator|.
name|createPeriodicJob
argument_list|(
name|TIMEOUT_CHECK_PERIOD
argument_list|,
operator|new
name|TimeoutCheck
argument_list|()
argument_list|,
literal|2000
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|add
parameter_list|(
name|String
name|query
parameter_list|,
name|Sequence
name|sequence
parameter_list|)
block|{
specifier|final
name|int
name|len
init|=
name|slots
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|slots
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|slots
index|[
name|i
index|]
operator|=
operator|new
name|QueryResult
argument_list|(
name|query
argument_list|,
name|sequence
argument_list|)
expr_stmt|;
return|return
name|i
return|;
block|}
block|}
comment|// no free slots, resize
name|QueryResult
index|[]
name|t
init|=
operator|new
name|QueryResult
index|[
operator|(
name|len
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|slots
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|t
index|[
name|len
index|]
operator|=
operator|new
name|QueryResult
argument_list|(
name|query
argument_list|,
name|sequence
argument_list|)
expr_stmt|;
name|slots
operator|=
name|t
expr_stmt|;
return|return
name|len
return|;
block|}
specifier|public
name|Sequence
name|get
parameter_list|(
name|String
name|query
parameter_list|,
name|int
name|sessionId
parameter_list|)
block|{
if|if
condition|(
name|sessionId
operator|<
literal|0
operator|||
name|sessionId
operator|>=
name|slots
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// out of scope
specifier|final
name|QueryResult
name|cached
init|=
name|slots
index|[
name|sessionId
index|]
decl_stmt|;
if|if
condition|(
name|cached
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|cached
operator|.
name|queryString
operator|.
name|equals
argument_list|(
name|query
argument_list|)
condition|)
block|{
return|return
name|cached
operator|.
name|sequence
argument_list|()
return|;
block|}
comment|// wrong query
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|release
parameter_list|(
name|int
name|sessionId
parameter_list|)
block|{
if|if
condition|(
name|sessionId
operator|<
literal|0
operator|||
name|sessionId
operator|>=
name|slots
operator|.
name|length
condition|)
block|{
return|return;
block|}
comment|// out of scope
name|slots
index|[
name|sessionId
index|]
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|void
name|timeoutCheck
parameter_list|()
block|{
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|slots
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|slots
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|now
operator|-
name|slots
index|[
name|i
index|]
operator|.
name|lastAccess
operator|>
name|TIMEOUT
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removing cached query result for session "
operator|+
name|i
argument_list|)
expr_stmt|;
name|slots
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

