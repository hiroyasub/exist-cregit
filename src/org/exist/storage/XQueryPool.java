begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|hashtable
operator|.
name|Object2ObjectHashMap
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
name|CompiledXQuery
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
comment|/**  * Global pool for pre-compiled XQuery expressions. Expressions are  * stored and retrieved from the pool by comparing the {@link org.exist.source.Source}  * objects from which they were created. For each XQuery, a maximum of   * {@link #MAX_STACK_SIZE} compiled expressions are kept in the pool.  * An XQuery expression will be removed from the pool if it has not been  * used for a pre-defined timeout. These settings can be configured in conf.xml.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XQueryPool
extends|extends
name|Object2ObjectHashMap
block|{
specifier|public
specifier|final
specifier|static
name|int
name|MAX_POOL_SIZE
init|=
literal|128
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MAX_STACK_SIZE
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|long
name|TIMEOUT
init|=
literal|120000L
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|long
name|TIMEOUT_CHECK_INTERVAL
init|=
literal|30000L
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XQueryPool
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|lastTimeOutCheck
decl_stmt|;
specifier|private
name|int
name|maxPoolSize
decl_stmt|;
specifier|private
name|int
name|maxStackSize
decl_stmt|;
specifier|private
name|long
name|timeout
decl_stmt|;
specifier|private
name|long
name|timeoutCheckInterval
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"query-pool"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MAX_STACK_SIZE_ATTRIBUTE
init|=
literal|"max-stack-size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|POOL_SIZE_ATTTRIBUTE
init|=
literal|"size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TIMEOUT_ATTRIBUTE
init|=
literal|"timeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TIMEOUT_CHECK_INTERVAL_ATTRIBUTE
init|=
literal|"timeout-check-interval"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_MAX_STACK_SIZE
init|=
literal|"db-connection.query-pool.max-stack-size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_POOL_SIZE
init|=
literal|"db-connection.query-pool.size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_TIMEOUT
init|=
literal|"db-connection.query-pool.timeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_TIMEOUT_CHECK_INTERVAL
init|=
literal|"db-connection.query-pool.timeout-check-interval"
decl_stmt|;
comment|/**      * @param conf      */
specifier|public
name|XQueryPool
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
literal|27
argument_list|)
expr_stmt|;
name|lastTimeOutCheck
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|Integer
name|maxStSz
init|=
operator|(
name|Integer
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_MAX_STACK_SIZE
argument_list|)
decl_stmt|;
name|Integer
name|maxPoolSz
init|=
operator|(
name|Integer
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_POOL_SIZE
argument_list|)
decl_stmt|;
name|Long
name|t
init|=
operator|(
name|Long
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TIMEOUT
argument_list|)
decl_stmt|;
name|Long
name|tci
init|=
operator|(
name|Long
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TIMEOUT_CHECK_INTERVAL
argument_list|)
decl_stmt|;
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxPoolSz
operator|!=
literal|null
condition|)
name|maxPoolSize
operator|=
name|maxPoolSz
operator|.
name|intValue
argument_list|()
expr_stmt|;
else|else
name|maxPoolSize
operator|=
name|MAX_POOL_SIZE
expr_stmt|;
if|if
condition|(
name|maxStSz
operator|!=
literal|null
condition|)
name|maxStackSize
operator|=
name|maxStSz
operator|.
name|intValue
argument_list|()
expr_stmt|;
else|else
name|maxStackSize
operator|=
name|MAX_STACK_SIZE
expr_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
name|timeout
operator|=
name|t
operator|.
name|longValue
argument_list|()
expr_stmt|;
else|else
name|timeout
operator|=
name|TIMEOUT
expr_stmt|;
comment|//TODO : check that it is inferior to t
if|if
condition|(
name|tci
operator|!=
literal|null
condition|)
name|timeoutCheckInterval
operator|=
name|tci
operator|.
name|longValue
argument_list|()
expr_stmt|;
else|else
name|timeoutCheckInterval
operator|=
name|TIMEOUT_CHECK_INTERVAL
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"QueryPool: size = "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|maxPoolSize
argument_list|)
operator|+
literal|"; maxStackSize = "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|maxStackSize
argument_list|)
operator|+
literal|"; timeout = "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|timeout
argument_list|)
operator|+
literal|"; timeoutCheckInterval = "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|timeoutCheckInterval
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|returnCompiledXQuery
parameter_list|(
name|Source
name|source
parameter_list|,
name|CompiledXQuery
name|xquery
parameter_list|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|<
name|maxPoolSize
condition|)
block|{
name|Stack
name|stack
init|=
operator|(
name|Stack
operator|)
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|stack
operator|==
literal|null
condition|)
block|{
name|stack
operator|=
operator|new
name|Stack
argument_list|()
expr_stmt|;
name|source
operator|.
name|setCacheTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|source
argument_list|,
name|stack
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|<
name|maxStackSize
condition|)
block|{
name|stack
operator|.
name|push
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
block|}
block|}
name|timeoutCheck
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|CompiledXQuery
name|borrowCompiledXQuery
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Source
name|source
parameter_list|)
block|{
name|int
name|idx
init|=
name|getIndex
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Source
name|key
init|=
operator|(
name|Source
operator|)
name|keys
index|[
name|idx
index|]
decl_stmt|;
name|int
name|validity
init|=
name|key
operator|.
name|isValid
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|validity
operator|==
name|Source
operator|.
name|UNKNOWN
condition|)
name|validity
operator|=
name|key
operator|.
name|isValid
argument_list|(
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|validity
operator|==
name|Source
operator|.
name|INVALID
operator|||
name|validity
operator|==
name|Source
operator|.
name|UNKNOWN
condition|)
block|{
name|keys
index|[
name|idx
index|]
operator|=
name|REMOVED
expr_stmt|;
name|values
index|[
name|idx
index|]
operator|=
literal|null
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|source
operator|.
name|getKey
argument_list|()
operator|+
literal|" is invalid"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Stack
name|stack
init|=
operator|(
name|Stack
operator|)
name|values
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
name|stack
operator|!=
literal|null
operator|&&
operator|!
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// now check if the compiled expression is valid
comment|// it might become invalid if an imported module has changed.
name|CompiledXQuery
name|query
init|=
operator|(
name|CompiledXQuery
operator|)
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
init|=
name|query
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|query
operator|.
name|isValid
argument_list|()
condition|)
block|{
comment|// the compiled query is no longer valid: one of the imported
comment|// modules may have changed
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
return|return
name|query
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|timeoutCheck
parameter_list|()
block|{
specifier|final
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeoutCheckInterval
operator|<
literal|0L
condition|)
return|return;
if|if
condition|(
name|currentTime
operator|-
name|lastTimeOutCheck
operator|<
name|timeoutCheckInterval
condition|)
return|return;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Source
name|next
init|=
operator|(
name|Source
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentTime
operator|-
name|next
operator|.
name|getCacheTimestamp
argument_list|()
operator|>
name|timeout
condition|)
block|{
name|remove
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

