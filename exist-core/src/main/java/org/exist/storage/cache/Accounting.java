begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|cache
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
name|util
operator|.
name|hashtable
operator|.
name|SequencedLongHashMap
import|;
end_import

begin_comment
comment|/**  * Keeps track of various cache parameters. Most important,  * this class is used to determine if a cache should be grown by  * computing the cache efficiency, which is expressed as the amount   * of trashing that occurs during a certain  * period of time. Trashing occurs if a page is replaced from the cache  * and is reloaded shortly after. For the B+-tree pages, we normally  * don't want any trashing at all.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|Accounting
block|{
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
name|Accounting
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Object
name|DUMMY
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/** the period (in milliseconds) for which trashing is recorded. */
specifier|private
name|int
name|checkPeriod
init|=
literal|30000
decl_stmt|;
comment|/** start of the last check period */
specifier|private
name|long
name|checkPeriodStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|/** max. entries to keep in the table of replaced pages */
specifier|private
name|int
name|maxEntries
init|=
literal|5000
decl_stmt|;
comment|/** total cache hits during the lifetime of the cache*/
specifier|private
name|int
name|hits
init|=
literal|0
decl_stmt|;
comment|/** total cache misses during the lifetime of the cache */
specifier|private
name|int
name|misses
init|=
literal|0
decl_stmt|;
comment|/** the current size of the cache */
specifier|private
name|int
name|totalSize
init|=
literal|0
decl_stmt|;
comment|/** the number of pages replaced and reloaded during the check period */
specifier|private
name|int
name|thrashing
init|=
literal|0
decl_stmt|;
comment|/** determines the amount of allowed trashing before a cache resize will      * be requested. This is expressed as a fraction of the total cache size.      */
specifier|private
name|double
name|thrashingFactor
decl_stmt|;
comment|/** the map used to track replaced page numbers */
specifier|private
name|SequencedLongHashMap
argument_list|<
name|Object
argument_list|>
name|map
decl_stmt|;
specifier|public
name|Accounting
parameter_list|(
name|double
name|thrashingFactor
parameter_list|)
block|{
name|map
operator|=
operator|new
name|SequencedLongHashMap
argument_list|<
name|Object
argument_list|>
argument_list|(
operator|(
name|maxEntries
operator|*
literal|3
operator|)
operator|/
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|thrashingFactor
operator|=
name|thrashingFactor
expr_stmt|;
block|}
comment|/**      * Set the current size of the cache. Should be called by the      * cache whenever it changes its size.      *       * @param totalSize of the cache      */
specifier|public
name|void
name|setTotalSize
parameter_list|(
name|int
name|totalSize
parameter_list|)
block|{
name|this
operator|.
name|totalSize
operator|=
name|totalSize
expr_stmt|;
block|}
comment|/**      * Increment the number of total cache hits by one.      */
specifier|public
name|void
name|hitIncrement
parameter_list|()
block|{
operator|++
name|hits
expr_stmt|;
block|}
comment|/**      * Returns the number of total cache hits during the      * lifetime of the cache.      *       * @return number of total cache hits      */
specifier|public
name|int
name|getHits
parameter_list|()
block|{
return|return
name|hits
return|;
block|}
comment|/**      * Increment the number of total cache faults by one.      */
specifier|public
name|void
name|missesIncrement
parameter_list|()
block|{
operator|++
name|misses
expr_stmt|;
block|}
comment|/**      * Returns the number of total cache faults.      * @return number of total cache faults      */
specifier|public
name|int
name|getMisses
parameter_list|()
block|{
return|return
name|misses
return|;
block|}
comment|/**      * Called by the cache to signal that a page was replaced      * in order to store the Cacheable object passed.      *       * @param cacheable object      */
specifier|public
name|void
name|replacedPage
parameter_list|(
name|Cacheable
name|cacheable
parameter_list|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|checkPeriodStart
operator|>
name|checkPeriod
condition|)
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|thrashing
operator|=
literal|0
expr_stmt|;
name|checkPeriodStart
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|==
name|maxEntries
condition|)
block|{
name|map
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|map
operator|.
name|get
argument_list|(
name|cacheable
operator|.
name|getKey
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
operator|++
name|thrashing
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|cacheable
operator|.
name|getKey
argument_list|()
argument_list|,
name|DUMMY
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Return the current amount of trashing.      * @return current amount of trashing      */
specifier|public
name|int
name|getThrashing
parameter_list|()
block|{
return|return
name|thrashing
return|;
block|}
comment|/**      * Returns true if a cache resize would increase the      * cache efficiency.      *       * @return True if a cache resize would increase the      * cache efficiency      */
specifier|public
name|boolean
name|resizeNeeded
parameter_list|()
block|{
if|if
condition|(
name|thrashingFactor
operator|==
literal|0
condition|)
block|{
return|return
name|thrashing
operator|>
literal|0
return|;
block|}
return|return
name|thrashing
operator|>
name|totalSize
operator|*
name|thrashingFactor
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|thrashing
operator|=
literal|0
expr_stmt|;
name|checkPeriodStart
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stats
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"hits: "
operator|+
name|hits
operator|+
literal|"; misses: "
operator|+
name|misses
operator|+
literal|"; thrashing: "
operator|+
name|getThrashing
argument_list|()
operator|+
literal|"; thrashing period: "
operator|+
name|checkPeriod
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

