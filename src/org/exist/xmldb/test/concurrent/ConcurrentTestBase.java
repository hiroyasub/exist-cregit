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
name|test
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
name|File
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|test
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
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * Abstract base class for concurrent tests.  *   * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ConcurrentTestBase
extends|extends
name|TestCase
block|{
specifier|protected
name|String
name|rootColURI
decl_stmt|;
specifier|protected
name|Collection
name|rootCol
decl_stmt|;
specifier|protected
name|String
name|testColName
decl_stmt|;
specifier|protected
name|Collection
name|testCol
decl_stmt|;
specifier|protected
name|List
name|actions
init|=
operator|new
name|ArrayList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|protected
specifier|volatile
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
comment|/**      * @param name the name of the test.      * @param uri the XMLDB URI of the root collection.      * @param testCollection the name of the collection that will be created for the test.      */
specifier|public
name|ConcurrentTestBase
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|uri
parameter_list|,
name|String
name|testCollection
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|rootColURI
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|testColName
operator|=
name|testCollection
expr_stmt|;
block|}
comment|/**      * Add an {@link Action} to the list of actions that will be processed      * concurrently. Should be called after {@link #setUp()}.      *       * @param action the action.      * @param repeat number of times the actions should be repeated.      */
specifier|public
name|void
name|addAction
parameter_list|(
name|Action
name|action
parameter_list|,
name|int
name|repeat
parameter_list|,
name|long
name|delayBeforeStart
parameter_list|,
name|long
name|delay
parameter_list|)
block|{
name|actions
operator|.
name|add
argument_list|(
operator|new
name|Runner
argument_list|(
name|action
argument_list|,
name|repeat
argument_list|,
name|delayBeforeStart
argument_list|,
name|delay
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Collection
name|getTestCollection
parameter_list|()
block|{
return|return
name|testCol
return|;
block|}
specifier|public
name|void
name|testConcurrent
parameter_list|()
block|{
comment|// start all threads
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|actions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|t
init|=
operator|(
name|Thread
operator|)
name|actions
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// wait for threads to finish
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|actions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|t
init|=
operator|(
name|Thread
operator|)
name|actions
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
comment|/*      * @see TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|rootCol
operator|=
name|DBUtils
operator|.
name|setupDB
argument_list|(
name|rootColURI
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|rootCol
argument_list|)
expr_stmt|;
name|testCol
operator|=
name|rootCol
operator|.
name|getChildCollection
argument_list|(
name|testColName
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
name|testColName
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
name|testColName
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCol
argument_list|)
expr_stmt|;
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|File
name|existDir
init|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
decl_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|rootCol
argument_list|,
literal|"biblio.rdf"
argument_list|,
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/biblio.rdf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * @see TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
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
name|testColName
argument_list|)
expr_stmt|;
name|DBUtils
operator|.
name|shutdownDB
argument_list|(
name|rootColURI
argument_list|)
expr_stmt|;
name|rootCol
operator|=
literal|null
expr_stmt|;
name|testCol
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Runs the specified Action a number of times.      *       * @author wolf      */
class|class
name|Runner
extends|extends
name|Thread
block|{
specifier|private
name|Action
name|action
decl_stmt|;
specifier|private
name|int
name|repeat
decl_stmt|;
specifier|private
name|long
name|delay
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|delayBeforeStart
init|=
literal|0
decl_stmt|;
specifier|public
name|Runner
parameter_list|(
name|Action
name|action
parameter_list|,
name|int
name|repeat
parameter_list|,
name|long
name|delayBeforeStart
parameter_list|,
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
name|delay
operator|=
name|delay
expr_stmt|;
name|this
operator|.
name|delayBeforeStart
operator|=
name|delayBeforeStart
expr_stmt|;
block|}
comment|/* (non-Javadoc)          * @see java.lang.Thread#run()          */
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|delayBeforeStart
operator|>
literal|0
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|(
name|delayBeforeStart
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Action failed in Thread "
operator|+
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
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
name|failed
condition|)
block|{
break|break;
block|}
name|failed
operator|=
name|action
operator|.
name|execute
argument_list|()
expr_stmt|;
if|if
condition|(
name|delay
operator|>
literal|0
condition|)
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|(
name|delay
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Action failed in Thread "
operator|+
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

