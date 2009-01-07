begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: ProcessMonitor.java 8235 2008-10-17 16:03:27Z chaeron $  */
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
name|xquery
operator|.
name|XQueryWatchDog
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
name|util
operator|.
name|ExpressionDumper
import|;
end_import

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
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Class to keep track of all running queries in a database instance. The main  * purpose of this class is to signal running queries that the database is going to  * shut down. This is done through the {@link org.exist.xquery.XQueryWatchDog}  * registered by each query. It is up to the query to check the watchdog's state.  * If it simply ignores the terminate signal, it will be killed after the shutdown  * timeout is reached.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ProcessMonitor
block|{
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_UNSPECIFIED
init|=
literal|"unspecified"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_VALIDATE_DOC
init|=
literal|"validating document"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_STORE_DOC
init|=
literal|"storing document"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_STORE_BINARY
init|=
literal|"storing binary resource"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_REMOVE_XML
init|=
literal|"remove XML resource"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_REMOVE_BINARY
init|=
literal|"remove binary resource"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_REMOVE_COLLECTION
init|=
literal|"remove collection"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_REINDEX_COLLECTION
init|=
literal|"reindex collection"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_COPY_COLLECTION
init|=
literal|"copy collection"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_MOVE_COLLECTION
init|=
literal|"move collection"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_COPY_XML_RESOURCE
init|=
literal|"copy xml resource"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_MOVE_XML_RESOURCE
init|=
literal|"move xml resource"
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
name|ProcessMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Set
name|runningQueries
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|processes
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|public
name|ProcessMonitor
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startJob
parameter_list|(
name|String
name|action
parameter_list|)
block|{
name|startJob
argument_list|(
name|action
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startJob
parameter_list|(
name|String
name|action
parameter_list|,
name|Object
name|addInfo
parameter_list|)
block|{
name|JobInfo
name|info
init|=
operator|new
name|JobInfo
argument_list|(
name|action
argument_list|)
decl_stmt|;
name|info
operator|.
name|setAddInfo
argument_list|(
name|addInfo
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|processes
init|)
block|{
name|processes
operator|.
name|put
argument_list|(
name|info
operator|.
name|getThread
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|endJob
parameter_list|()
block|{
synchronized|synchronized
init|(
name|processes
init|)
block|{
name|processes
operator|.
name|remove
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|JobInfo
index|[]
name|runningJobs
parameter_list|()
block|{
synchronized|synchronized
init|(
name|processes
init|)
block|{
name|JobInfo
name|jobs
index|[]
init|=
operator|new
name|JobInfo
index|[
name|processes
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|processes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|jobs
index|[
name|j
index|]
operator|=
operator|(
name|JobInfo
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|jobs
return|;
block|}
block|}
specifier|public
name|void
name|queryStarted
parameter_list|(
name|XQueryWatchDog
name|watchdog
parameter_list|)
block|{
synchronized|synchronized
init|(
name|runningQueries
init|)
block|{
name|runningQueries
operator|.
name|add
argument_list|(
name|watchdog
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|queryCompleted
parameter_list|(
name|XQueryWatchDog
name|watchdog
parameter_list|)
block|{
synchronized|synchronized
init|(
name|runningQueries
init|)
block|{
name|runningQueries
operator|.
name|remove
argument_list|(
name|watchdog
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|killAll
parameter_list|(
name|long
name|waitTime
parameter_list|)
block|{
comment|// directly called from BrokerPool itself. no need to synchronize.
name|XQueryWatchDog
name|watchdog
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|runningQueries
operator|.
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
name|watchdog
operator|=
operator|(
name|XQueryWatchDog
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Killing query: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|watchdog
operator|.
name|getContext
argument_list|()
operator|.
name|getRootExpression
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|watchdog
operator|.
name|kill
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|XQueryWatchDog
index|[]
name|getRunningXQueries
parameter_list|()
block|{
synchronized|synchronized
init|(
name|runningQueries
init|)
block|{
name|XQueryWatchDog
name|watchdogs
index|[]
init|=
operator|new
name|XQueryWatchDog
index|[
name|runningQueries
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|runningQueries
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|watchdogs
index|[
name|j
index|]
operator|=
operator|(
name|XQueryWatchDog
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|watchdogs
return|;
block|}
block|}
specifier|public
specifier|final
specifier|static
class|class
name|JobInfo
block|{
specifier|private
name|Thread
name|thread
decl_stmt|;
specifier|private
name|String
name|action
decl_stmt|;
specifier|private
name|long
name|startTime
decl_stmt|;
specifier|private
name|Object
name|addInfo
init|=
literal|null
decl_stmt|;
specifier|public
name|JobInfo
parameter_list|(
name|String
name|action
parameter_list|)
block|{
name|this
operator|.
name|thread
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getAction
parameter_list|()
block|{
return|return
name|action
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
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
specifier|public
name|void
name|setAddInfo
parameter_list|(
name|Object
name|info
parameter_list|)
block|{
name|this
operator|.
name|addInfo
operator|=
name|info
expr_stmt|;
block|}
specifier|public
name|Object
name|getAddInfo
parameter_list|()
block|{
return|return
name|addInfo
return|;
block|}
block|}
block|}
end_class

end_unit

