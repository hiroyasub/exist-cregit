begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    */
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
name|BufferedWriter
import|;
end_import

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
name|io
operator|.
name|FileWriter
import|;
end_import

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
name|util
operator|.
name|ConfigurationHelper
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
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  * JUnit tests generator from XQTS Catalog.  *   * @author @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XQTS_To_junit
block|{
specifier|private
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
name|database
decl_stmt|;
specifier|private
name|String
name|sep
init|=
name|File
operator|.
name|separator
decl_stmt|;
comment|/**      * @param args      * @throws Exception       */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|XQTS_To_junit
name|convertor
init|=
operator|new
name|XQTS_To_junit
argument_list|()
decl_stmt|;
try|try
block|{
name|convertor
operator|.
name|startup
argument_list|()
expr_stmt|;
name|convertor
operator|.
name|create
argument_list|()
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
finally|finally
block|{
name|convertor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|startup
parameter_list|()
throws|throws
name|Exception
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
block|}
comment|/**      * @throws java.lang.Exception      */
annotation|@
name|After
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
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
block|}
specifier|private
name|Collection
name|collection
decl_stmt|;
specifier|public
name|void
name|create
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|File
name|file
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|File
name|folder
init|=
operator|new
name|File
argument_list|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|sep
operator|+
literal|"test"
operator|+
name|sep
operator|+
literal|"src"
operator|+
name|sep
operator|+
literal|"org"
operator|+
name|sep
operator|+
literal|"exist"
operator|+
name|sep
operator|+
literal|"xquery"
operator|+
name|sep
operator|+
literal|"xqts"
operator|+
name|sep
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|folder
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"XQTS junit tests folder unreadable."
argument_list|)
throw|;
block|}
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/XQTS"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|loadXQTS
argument_list|()
expr_stmt|;
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/XQTS"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"There is no XQTS data at database"
argument_list|)
throw|;
block|}
block|}
name|String
name|query
init|=
literal|"declare namespace catalog=\"http://www.w3.org/2005/02/query-test-XQTSCatalog\";"
operator|+
literal|"let $XQTSCatalog := xmldb:document('/db/XQTS/XQTSCatalog.xml') "
operator|+
literal|"return xs:string($XQTSCatalog/catalog:test-suite/@version)"
decl_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|results
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|getSize
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|String
name|catalog
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|catalog
operator|=
literal|"XQTS_"
operator|+
name|adoptString
argument_list|(
name|catalog
argument_list|)
expr_stmt|;
name|File
name|subfolder
init|=
operator|new
name|File
argument_list|(
name|folder
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|sep
operator|+
name|catalog
argument_list|)
decl_stmt|;
name|processGroups
argument_list|(
literal|null
argument_list|,
name|subfolder
argument_list|,
literal|"."
operator|+
name|catalog
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
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
comment|//File xqtsFile = new File("webapp/xqts/build.xml");
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
literal|"../../"
operator|+
name|XQTS_case
operator|.
name|XQTS_folder
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
comment|//Nothing to do
block|}
block|}
specifier|private
name|boolean
name|processGroups
parameter_list|(
name|String
name|parentName
parameter_list|,
name|File
name|folder
parameter_list|,
name|String
name|_package_
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"declare namespace catalog=\"http://www.w3.org/2005/02/query-test-XQTSCatalog\";"
operator|+
literal|"let $XQTSCatalog := xmldb:document('/db/XQTS/XQTSCatalog.xml')"
decl_stmt|;
if|if
condition|(
name|parentName
operator|==
literal|null
condition|)
name|query
operator|+=
literal|"for $testGroup in $XQTSCatalog/catalog:test-suite/catalog:test-group"
expr_stmt|;
else|else
name|query
operator|+=
literal|"for $testGroup in $XQTSCatalog//catalog:test-group[@name = '"
operator|+
name|parentName
operator|+
literal|"']/catalog:test-group"
expr_stmt|;
name|query
operator|+=
literal|"	return xs:string($testGroup/@name)"
expr_stmt|;
name|ResourceSet
name|results
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|getSize
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|File
name|subfolder
decl_stmt|;
name|String
name|subPackage
decl_stmt|;
comment|//if (parentName == null) {
comment|//subfolder = folder;
comment|//subPackage = _package_;
comment|//} else {
comment|//subfolder = new File(folder.getAbsolutePath()+sep+parentName);
comment|//subPackage = _package_+"."+adoptString(parentName);
comment|//}
name|BufferedWriter
name|allTests
init|=
name|startAllTests
argument_list|(
name|folder
argument_list|,
name|_package_
argument_list|)
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|testCases
argument_list|(
name|parentName
argument_list|,
name|folder
argument_list|,
name|_package_
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
name|allTests
operator|.
name|write
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
else|else
name|first
operator|=
literal|false
expr_stmt|;
name|allTests
operator|.
name|write
argument_list|(
literal|"\t\tC_"
operator|+
name|adoptString
argument_list|(
name|parentName
argument_list|)
operator|+
literal|".class"
argument_list|)
expr_stmt|;
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
name|results
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|groupName
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|subfolder
operator|=
operator|new
name|File
argument_list|(
name|folder
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|sep
operator|+
name|groupName
argument_list|)
expr_stmt|;
name|subPackage
operator|=
name|_package_
operator|+
literal|"."
operator|+
name|adoptString
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
if|if
condition|(
name|processGroups
argument_list|(
name|groupName
argument_list|,
name|subfolder
argument_list|,
name|subPackage
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
name|allTests
operator|.
name|write
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
else|else
name|first
operator|=
literal|false
expr_stmt|;
name|allTests
operator|.
name|write
argument_list|(
literal|"\t\ttorg.exist.xquery.xqts"
operator|+
name|subPackage
operator|+
literal|".AllTests.class"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|testCases
argument_list|(
name|groupName
argument_list|,
name|folder
argument_list|,
name|_package_
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
name|allTests
operator|.
name|write
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
else|else
name|first
operator|=
literal|false
expr_stmt|;
name|allTests
operator|.
name|write
argument_list|(
literal|"\t\tC_"
operator|+
name|adoptString
argument_list|(
name|groupName
argument_list|)
operator|+
literal|".class"
argument_list|)
expr_stmt|;
block|}
block|}
name|endAllTests
argument_list|(
name|allTests
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|BufferedWriter
name|startAllTests
parameter_list|(
name|File
name|folder
parameter_list|,
name|String
name|_package_
parameter_list|)
throws|throws
name|IOException
block|{
name|folder
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|jTest
init|=
operator|new
name|File
argument_list|(
name|folder
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|sep
operator|+
literal|"AllTests.java"
argument_list|)
decl_stmt|;
name|FileWriter
name|fstream
init|=
operator|new
name|FileWriter
argument_list|(
name|jTest
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
decl_stmt|;
name|BufferedWriter
name|out
init|=
operator|new
name|BufferedWriter
argument_list|(
name|fstream
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"package org.exist.xquery.xqts"
operator|+
name|_package_
operator|+
literal|";\n\n"
operator|+
literal|"import org.junit.runner.RunWith;\n"
operator|+
literal|"import org.junit.runners.Suite;\n\n"
operator|+
literal|"@RunWith(Suite.class)\n"
operator|+
literal|"@Suite.SuiteClasses({\n"
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
specifier|private
name|void
name|endAllTests
parameter_list|(
name|BufferedWriter
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|"\n})\n\n"
operator|+
literal|"public class AllTests {\n\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|testCases
parameter_list|(
name|String
name|testGroup
parameter_list|,
name|File
name|folder
parameter_list|,
name|String
name|_package_
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"declare namespace catalog=\"http://www.w3.org/2005/02/query-test-XQTSCatalog\";"
operator|+
literal|"let $XQTSCatalog := xmldb:document('/db/XQTS/XQTSCatalog.xml')"
operator|+
literal|"for $testGroup in $XQTSCatalog//catalog:test-group[@name = '"
operator|+
name|testGroup
operator|+
literal|"']/catalog:test-case"
operator|+
literal|"\treturn xs:string($testGroup/@name)"
decl_stmt|;
name|ResourceSet
name|results
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|getSize
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|folder
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|jTest
init|=
operator|new
name|File
argument_list|(
name|folder
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|sep
operator|+
literal|"C_"
operator|+
name|adoptString
argument_list|(
name|testGroup
argument_list|)
operator|+
literal|".java"
argument_list|)
decl_stmt|;
name|FileWriter
name|fstream
init|=
operator|new
name|FileWriter
argument_list|(
name|jTest
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
decl_stmt|;
name|BufferedWriter
name|out
init|=
operator|new
name|BufferedWriter
argument_list|(
name|fstream
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"package org.exist.xquery.xqts"
operator|+
name|_package_
operator|+
literal|";\n\n"
operator|+
literal|"import org.exist.xquery.xqts.XQTS_case;\n"
operator|+
comment|//"import static org.junit.Assert.*;\n" +
literal|"import org.junit.Test;\n\n"
operator|+
literal|"public class C_"
operator|+
name|adoptString
argument_list|(
name|testGroup
argument_list|)
operator|+
literal|" extends XQTS_case {\n"
operator|+
literal|"\tprivate String testGroup = \""
operator|+
name|testGroup
operator|+
literal|"\";\n\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|caseName
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"	/* "
operator|+
name|caseName
operator|+
literal|" */"
operator|+
literal|"\t@Test\n"
operator|+
literal|"\tpublic void test_"
operator|+
name|adoptString
argument_list|(
name|caseName
argument_list|)
operator|+
literal|"() {\n"
operator|+
literal|"\tgroupCase(testGroup, \""
operator|+
name|caseName
operator|+
literal|"\");"
operator|+
literal|"\t}\n\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|String
name|adoptString
parameter_list|(
name|String
name|caseName
parameter_list|)
block|{
name|String
name|result
init|=
name|caseName
operator|.
name|replace
argument_list|(
literal|"-"
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

