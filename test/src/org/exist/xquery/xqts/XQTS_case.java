begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|xqts
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|DefaultLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|ProjectHelper
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
name|FileSource
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
name|XQueryService
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
name|AfterClass
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|DatabaseManager
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
name|ResourceSet
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
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|XQTS_case
block|{
specifier|public
specifier|static
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
name|database
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|int
name|inUse
init|=
literal|0
decl_stmt|;
specifier|protected
specifier|static
name|Collection
name|testCollection
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Thread
name|shutdowner
init|=
literal|null
decl_stmt|;
specifier|static
class|class
name|Shutdowner
implements|implements
name|Runnable
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|inUse
operator|==
literal|0
condition|)
block|{
name|database
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"database was shutdown"
argument_list|)
expr_stmt|;
name|database
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|database
operator|==
literal|null
condition|)
block|{
name|database
operator|=
operator|new
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
argument_list|(
literal|"jetty"
argument_list|)
expr_stmt|;
name|database
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"jetty"
block|}
argument_list|)
expr_stmt|;
name|testCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/XQTS"
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|testCollection
operator|==
literal|null
condition|)
block|{
name|loadXQTS
argument_list|()
expr_stmt|;
name|testCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/XQTS"
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|testCollection
operator|==
literal|null
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"There is no XQTS data at database"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shutdowner
operator|==
literal|null
condition|)
block|{
name|shutdowner
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Shutdowner
argument_list|()
argument_list|)
expr_stmt|;
name|shutdowner
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
name|inUse
operator|++
expr_stmt|;
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
block|}
comment|//		System.out.println("setUpBeforeClass PASSED");
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDownAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|inUse
operator|--
expr_stmt|;
comment|//		System.out.println("tearDownAfterClass PASSED");
block|}
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// System.out.println("setUp PASSED");
block|}
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// System.out.println("tearDown PASSED");
block|}
specifier|private
specifier|static
name|void
name|loadXQTS
parameter_list|()
block|{
name|File
name|buildFile
init|=
operator|new
name|File
argument_list|(
literal|"webapp/xqts/build.xml"
argument_list|)
decl_stmt|;
name|Project
name|p
init|=
operator|new
name|Project
argument_list|()
decl_stmt|;
name|p
operator|.
name|setUserProperty
argument_list|(
literal|"ant.file"
argument_list|,
name|buildFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|setUserProperty
argument_list|(
literal|"config.basedir"
argument_list|,
literal|"../../test/external/XQTS_1_0_2"
argument_list|)
expr_stmt|;
name|DefaultLogger
name|consoleLogger
init|=
operator|new
name|DefaultLogger
argument_list|()
decl_stmt|;
name|consoleLogger
operator|.
name|setErrorPrintStream
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|consoleLogger
operator|.
name|setOutputPrintStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|consoleLogger
operator|.
name|setMessageOutputLevel
argument_list|(
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
name|p
operator|.
name|addBuildListener
argument_list|(
name|consoleLogger
argument_list|)
expr_stmt|;
try|try
block|{
name|p
operator|.
name|fireBuildStarted
argument_list|()
expr_stmt|;
name|p
operator|.
name|init
argument_list|()
expr_stmt|;
name|ProjectHelper
name|helper
init|=
name|ProjectHelper
operator|.
name|getProjectHelper
argument_list|()
decl_stmt|;
name|p
operator|.
name|addReference
argument_list|(
literal|"ant.projectHelper"
argument_list|,
name|helper
argument_list|)
expr_stmt|;
name|helper
operator|.
name|parse
argument_list|(
name|p
argument_list|,
name|buildFile
argument_list|)
expr_stmt|;
name|p
operator|.
name|executeTarget
argument_list|(
literal|"store"
argument_list|)
expr_stmt|;
name|p
operator|.
name|fireBuildFinished
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BuildException
name|e
parameter_list|)
block|{
name|p
operator|.
name|fireBuildFinished
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
specifier|protected
name|void
name|groupCase
parameter_list|(
name|String
name|testGroup
parameter_list|,
name|String
name|testCase
parameter_list|)
block|{
comment|//		BrokerPool pool;
try|try
block|{
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|File
name|xqts
init|=
operator|new
name|File
argument_list|(
literal|"test/src/org/exist/xquery/xqts/xqts.xql"
argument_list|)
decl_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"testGroup"
argument_list|,
name|testGroup
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"testCase"
argument_list|,
name|testCase
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
operator|new
name|FileSource
argument_list|(
name|xqts
argument_list|,
literal|"UTF8"
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Element
name|root
init|=
operator|(
name|Element
operator|)
name|resource
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resource
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"pass"
argument_list|,
name|root
operator|.
name|getAttribute
argument_list|(
literal|"result"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

