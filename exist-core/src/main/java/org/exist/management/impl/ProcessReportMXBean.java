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
name|management
operator|.
name|impl
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

begin_interface
specifier|public
interface|interface
name|ProcessReportMXBean
extends|extends
name|PerInstanceMBean
block|{
name|List
argument_list|<
name|Job
argument_list|>
name|getScheduledJobs
parameter_list|()
function_decl|;
name|List
argument_list|<
name|Job
argument_list|>
name|getRunningJobs
parameter_list|()
function_decl|;
name|List
argument_list|<
name|RunningQuery
argument_list|>
name|getRunningQueries
parameter_list|()
function_decl|;
name|List
argument_list|<
name|RecentQueryHistory
argument_list|>
name|getRecentQueryHistory
parameter_list|()
function_decl|;
name|void
name|killQuery
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
comment|/**      * Configures the recent query history.      *      * @param minTimeRecorded minimum execution time of queries recorded in the recent query history      * @param historyTimespan time span (in milliseconds) for which the stats for an executed query should      *                        be kept in the recent query history      * @param trackURI        Enable request tracking: for every executed query, try to figure out which HTTP      *                        URL triggered it (if applicable)      */
name|void
name|configure
parameter_list|(
name|long
name|minTimeRecorded
parameter_list|,
name|long
name|historyTimespan
parameter_list|,
name|boolean
name|trackURI
parameter_list|)
function_decl|;
comment|/**      * Sets the time span (in milliseconds) for which the stats for an executed query should      * be kept in the recent query history.      *      * @param time      */
name|void
name|setHistoryTimespan
parameter_list|(
name|long
name|time
parameter_list|)
function_decl|;
name|long
name|getHistoryTimespan
parameter_list|()
function_decl|;
comment|/**      * Sets the minimum execution time of queries recorded in the recent query history.      * Queries faster than this are not recorded.      *      * @param time      */
name|void
name|setMinTime
parameter_list|(
name|long
name|time
parameter_list|)
function_decl|;
name|long
name|getMinTime
parameter_list|()
function_decl|;
comment|/**      * Enable request tracking: for every executed query, try to figure out which HTTP      * URL triggered it (if applicable). For performance reasons this is disabled by default,      * though the overhead should be small.      *      * @param track      */
name|void
name|setTrackRequestURI
parameter_list|(
name|boolean
name|track
parameter_list|)
function_decl|;
name|boolean
name|getTrackRequestURI
parameter_list|()
function_decl|;
block|}
end_interface

end_unit
