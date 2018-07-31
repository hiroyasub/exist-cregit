begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|TestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistXmldbEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
operator|.
name|action
operator|.
name|Action
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|IndexQueryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Abstract base class for concurrent tests.  *   * @author wolf  * @author aretter  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ConcurrentTestBase
block|{
specifier|private
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<lucene>"
operator|+
literal|"<text match=\"/*\"/>"
operator|+
literal|"</lucene>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|protected
name|Collection
name|testCol
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existXmldbEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
specifier|final
name|void
name|startupDb
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Collection
name|rootCol
init|=
name|existXmldbEmbeddedServer
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|rootCol
argument_list|)
expr_stmt|;
specifier|final
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|rootCol
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|testCol
operator|=
name|rootCol
operator|.
name|getChildCollection
argument_list|(
name|getTestCollectionName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|testCol
operator|!=
literal|null
condition|)
block|{
name|CollectionManagementService
name|mgr
init|=
name|DBUtils
operator|.
name|getCollectionManagementService
argument_list|(
name|rootCol
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|removeCollection
argument_list|(
name|getTestCollectionName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|testCol
operator|=
name|DBUtils
operator|.
name|addCollection
argument_list|(
name|rootCol
argument_list|,
name|getTestCollectionName
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCol
argument_list|)
expr_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|rootCol
argument_list|,
literal|"biblio.rdf"
argument_list|,
name|TestUtils
operator|.
name|resolveSample
argument_list|(
literal|"biblio.rdf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
specifier|final
name|void
name|tearDownDb
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|rootCol
init|=
name|existXmldbEmbeddedServer
operator|.
name|getRoot
argument_list|()
decl_stmt|;
specifier|final
name|Resource
name|res
init|=
name|rootCol
operator|.
name|getResource
argument_list|(
literal|"biblio.rdf"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|rootCol
operator|.
name|removeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|DBUtils
operator|.
name|removeCollection
argument_list|(
name|rootCol
argument_list|,
name|getTestCollectionName
argument_list|()
argument_list|)
expr_stmt|;
name|testCol
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Get the name of the test collection.      *      * @return the name of the test collection.      */
specifier|public
specifier|abstract
name|String
name|getTestCollectionName
parameter_list|()
function_decl|;
comment|/**      * Get the runners for the test      *      * @return the runners for the test.      */
specifier|public
specifier|abstract
name|List
argument_list|<
name|Runner
argument_list|>
name|getRunners
parameter_list|()
function_decl|;
specifier|public
name|Collection
name|getTestCollection
parameter_list|()
block|{
return|return
name|testCol
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrent
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make a copy of the actions
specifier|final
name|List
argument_list|<
name|Runner
argument_list|>
name|runners
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|getRunners
argument_list|()
argument_list|)
decl_stmt|;
comment|// start all threads
specifier|final
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|runners
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Future
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Runner
name|runner
range|:
name|runners
control|)
block|{
name|futures
operator|.
name|add
argument_list|(
name|executorService
operator|.
name|submit
argument_list|(
name|runner
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// await first error, or all results
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
name|Exception
name|failedException
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|futures
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
name|Future
argument_list|<
name|Boolean
argument_list|>
name|completedFuture
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|Future
argument_list|<
name|Boolean
argument_list|>
name|future
range|:
name|futures
control|)
block|{
if|if
condition|(
name|future
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|completedFuture
operator|=
name|future
expr_stmt|;
break|break;
comment|// exit for-loop
block|}
block|}
if|if
condition|(
name|completedFuture
operator|!=
literal|null
condition|)
block|{
comment|// remove the completed future from the list of futures
name|futures
operator|.
name|remove
argument_list|(
name|completedFuture
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|boolean
name|success
init|=
name|completedFuture
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
break|break;
comment|// exit while-loop
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
decl||
name|ExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|failed
operator|=
literal|true
expr_stmt|;
name|failedException
operator|=
name|e
expr_stmt|;
break|break;
comment|// exit while-loop
block|}
block|}
else|else
block|{
comment|// sleep, repeat...
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|failedException
operator|=
name|e
expr_stmt|;
break|break;
comment|// exit while-loop
block|}
block|}
block|}
comment|// repeat while-loop
if|if
condition|(
name|failed
condition|)
block|{
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
if|if
condition|(
name|failedException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|failedException
throw|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
block|}
name|assertAdditional
argument_list|()
expr_stmt|;
block|}
comment|/**      * Override this if you need to make      * additional assertions after the {@link #concurrent()}      * test has completed.      */
specifier|protected
name|void
name|assertAdditional
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|// no-op
block|}
comment|/**      * Runs the specified Action a number of times.      *       * @author wolf      */
class|class
name|Runner
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
block|{
specifier|private
specifier|final
name|Action
name|action
decl_stmt|;
specifier|private
specifier|final
name|int
name|repeat
decl_stmt|;
specifier|private
specifier|final
name|long
name|delayBeforeStart
decl_stmt|;
specifier|private
specifier|final
name|long
name|delay
decl_stmt|;
specifier|public
name|Runner
parameter_list|(
specifier|final
name|Action
name|action
parameter_list|,
specifier|final
name|int
name|repeat
parameter_list|,
specifier|final
name|long
name|delayBeforeStart
parameter_list|,
specifier|final
name|long
name|delay
parameter_list|)
block|{
name|super
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
name|repeat
operator|=
name|repeat
expr_stmt|;
name|this
operator|.
name|delayBeforeStart
operator|=
name|delayBeforeStart
expr_stmt|;
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
block|}
comment|/**          * Returns true if execution completes.          *          * @return true if execution completes, false otherwise          */
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
if|if
condition|(
name|delayBeforeStart
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|sleep
argument_list|(
name|delayBeforeStart
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
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
name|repeat
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|action
operator|.
name|execute
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|delay
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|sleep
argument_list|(
name|delay
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**          * Sleeps the current thread for a period of time.          *          * @param period the period to sleep for.          *          * @return true if the thread slept for the period and was not interrupted          */
specifier|private
name|boolean
name|sleep
parameter_list|(
specifier|final
name|long
name|period
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|period
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

