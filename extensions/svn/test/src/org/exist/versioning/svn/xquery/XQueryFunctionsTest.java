begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|xquery
package|;
end_package

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
name|IOException
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|SAXAdapter
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
name|source
operator|.
name|StringSource
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
name|xmldb
operator|.
name|DatabaseInstanceManager
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
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|Ignore
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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
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
name|Database
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
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XQueryFunctionsTest
block|{
name|String
name|common
init|=
literal|"xquery version \"3.0\"; "
operator|+
literal|"let $collection := '/db/test/commit' "
operator|+
literal|"let $target-file := 'test.xml'"
operator|+
literal|"let $file-path := concat($collection, '/', $target-file) "
operator|+
literal|"let $url := "
operator|+
name|repositoryBaseURI
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $user := "
operator|+
name|testAccount
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $password := "
operator|+
name|testPassword
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $checkout-rel-path := concat($url, '/commit') "
operator|+
literal|"let $login := xmldb:login($collection, 'guest', 'guest') "
operator|+
literal|"return<result pass='true'>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|test_001
parameter_list|()
block|{
name|test
argument_list|(
literal|"xquery version \"1.0\"; "
operator|+
literal|"let $destination-path := '/db/test/svn/checkout' "
operator|+
literal|"let $fake := if (xmldb:collection-available($destination-path)) "
operator|+
literal|"then xmldb:remove($destination-path )"
operator|+
literal|"else() "
operator|+
literal|"return<result pass=\"true\">subversion:checkout("
operator|+
name|repositoryBaseURI
argument_list|()
operator|+
literal|", '/db/test/svn/checkout', "
operator|+
name|testAccount
argument_list|()
operator|+
literal|", "
operator|+
name|testPassword
argument_list|()
operator|+
literal|")</result>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|test_002
parameter_list|()
block|{
name|test
argument_list|(
literal|"xquery version \"1.0\"; "
operator|+
literal|"let $url := "
operator|+
name|repositoryBaseURI
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $user := "
operator|+
name|testAccount
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $password := "
operator|+
name|testPassword
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $target-file := 'test.xml' "
operator|+
literal|"let $collection := '/db/test/svn/checkout' "
operator|+
literal|"let $file-path := concat($collection, '/', $target-file) "
operator|+
literal|"let $file := "
operator|+
literal|"if (doc-available($file-path)) "
operator|+
literal|"then () "
operator|+
literal|"else xmldb:store($collection, $target-file,<test>{current-dateTime()}</test>) "
operator|+
literal|"let $add := subversion:add($file-path) "
operator|+
literal|"let $commit-1 := subversion:commit($collection, 'Test of Commit after Add', $user, $password) "
operator|+
literal|"let $list1 := subversion:info($collection) "
operator|+
literal|"let $delete := subversion:delete($file-path) "
operator|+
literal|"let $commit-2 := subversion:commit($collection, 'Test of Commit after Delete', $user, $password) "
operator|+
literal|"let $list2 := subversion:info($collection) "
operator|+
literal|"return "
operator|+
literal|"<result pass=\"true\">"
operator|+
literal|"<list1>{$list1}</list1>"
operator|+
literal|"<list2>{$list2}</list2>"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test_003
parameter_list|()
block|{
name|test
argument_list|(
literal|"xquery version \"1.0\"; "
operator|+
literal|"<result pass=\"true\">"
operator|+
literal|"subversion:get-latest-revision-number("
operator|+
literal|"'/db/test/svn/checkout', "
operator|+
name|testAccount
argument_list|()
operator|+
literal|", "
operator|+
name|testPassword
argument_list|()
operator|+
literal|")"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test_004
parameter_list|()
block|{
name|test
argument_list|(
literal|"xquery version \"1.0\"; "
operator|+
literal|"<result pass=\"true\">"
operator|+
literal|"subversion:clean-up("
operator|+
literal|"'/db/test/svn/checkout')"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|test_010
parameter_list|()
block|{
name|test
argument_list|(
literal|"xquery version \"3.0\"; "
operator|+
literal|"let $collection := '/db/test/svn/checkout' "
operator|+
literal|"let $target-file := 'test.xml'"
operator|+
literal|"let $file-path := concat($collection, '/', $target-file) "
operator|+
literal|"let $url := "
operator|+
name|repositoryBaseURI
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $user := "
operator|+
name|testAccount
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $password := "
operator|+
name|testPassword
argument_list|()
operator|+
literal|" "
operator|+
literal|"let $tmp := subversion:checkout("
operator|+
name|repositoryBaseURI
argument_list|()
operator|+
literal|", $collection, "
operator|+
name|testAccount
argument_list|()
operator|+
literal|", "
operator|+
name|testPassword
argument_list|()
operator|+
literal|") "
operator|+
literal|"let $file := "
operator|+
literal|"if (doc-available($file-path)) "
operator|+
literal|"then () "
operator|+
literal|"else xmldb:store($collection, $target-file,<test>{current-dateTime()}</test>) "
operator|+
literal|"let $initial-owner := xmldb:get-owner($collection, $target-file) "
operator|+
literal|"let $initial-group := xmldb:get-group($collection, $target-file) "
operator|+
literal|"let $initial-permissions := xmldb:get-permissions($collection, $target-file) "
operator|+
literal|"let $commit := system:as-user('guest', 'guest', subversion:commit($file-path, 'Test Commit', $user, $password)) "
operator|+
literal|"let $final-owner := xmldb:get-owner($collection, $target-file) "
operator|+
literal|"let $final-group := xmldb:get-group($collection, $target-file) "
operator|+
literal|"let $final-permissions := xmldb:get-permissions($collection, $target-file) "
operator|+
literal|"return<result pass=\"true\"> "
operator|+
literal|"<BeforeCommit owner='{$initial-owner}' group='{$initial-group}' perms='{xmldb:permissions-to-string($initial-permissions)}'/>"
operator|+
literal|"<AfterCommit owner='{$final-owner}' group='{$final-group}' perms='{xmldb:permissions-to-string($final-permissions)}'/>"
operator|+
literal|"<revision>{$commit}</revision>"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|test_011
parameter_list|()
block|{
name|test
argument_list|(
name|common
operator|+
literal|"{if (xmldb:collection-available($collection)) "
operator|+
literal|"then ('Removing Old Files', xmldb:remove($collection)) "
operator|+
literal|"else()}"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|common
operator|+
literal|"{subversion:checkout($checkout-rel-path, $collection, $test-user, $test-password)}"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|common
operator|+
literal|"{let $result := xmldb:get-child-collections($collection) "
operator|+
literal|"return "
operator|+
literal|"if ( $result = '.svn') "
operator|+
literal|"then "
operator|+
literal|"<test>"
operator|+
literal|"result = {$result} (expecting '.svn') "
operator|+
literal|"passed"
operator|+
literal|"</test> "
operator|+
literal|"else<test>fail</test>"
operator|+
literal|"}"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|common
operator|+
literal|"{"
operator|+
literal|"let $timestamp := current-dateTime() "
operator|+
literal|"let $new-message := concat('Updated Message ', $timestamp) "
operator|+
literal|"let $update := update value doc($new-file-path) "
operator|+
literal|"return "
operator|+
literal|"doc($new-file-path) "
operator|+
literal|"}"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|common
operator|+
literal|"{"
operator|+
literal|"subversion:commit($collection, 'Test Modify Commit From Unit Test', $user, $password)"
operator|+
literal|"}"
operator|+
literal|"</result>"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|repositoryBaseURI
parameter_list|()
block|{
return|return
literal|"'http://localhost:9080/svn/testRepo'"
return|;
block|}
specifier|private
name|String
name|testAccount
parameter_list|()
block|{
name|String
name|str
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"svn_username"
argument_list|)
decl_stmt|;
if|if
condition|(
name|str
operator|==
literal|null
condition|)
return|return
literal|"'svnTest'"
return|;
return|return
name|str
return|;
block|}
specifier|private
name|String
name|testPassword
parameter_list|()
block|{
name|String
name|str
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"svn_password"
argument_list|)
decl_stmt|;
if|if
condition|(
name|str
operator|==
literal|null
condition|)
return|return
literal|"'testing'"
return|;
return|return
name|str
return|;
block|}
specifier|public
name|void
name|test
parameter_list|(
name|String
name|script
parameter_list|)
block|{
try|try
block|{
name|StringBuilder
name|fails
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|results
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|XQueryService
name|xqs
init|=
operator|(
name|XQueryService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Source
name|query
init|=
operator|new
name|StringSource
argument_list|(
name|script
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xqs
operator|.
name|execute
argument_list|(
name|query
argument_list|)
decl_stmt|;
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
name|results
operator|.
name|append
argument_list|(
name|resource
operator|.
name|getContent
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
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
name|NodeList
name|tests
init|=
name|root
operator|.
name|getElementsByTagName
argument_list|(
literal|"result"
argument_list|)
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
name|tests
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|test
init|=
operator|(
name|Element
operator|)
name|tests
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|passed
init|=
name|test
operator|.
name|getAttribute
argument_list|(
literal|"pass"
argument_list|)
decl_stmt|;
if|if
condition|(
name|passed
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
name|fails
operator|.
name|append
argument_list|(
literal|"Test '"
operator|+
name|test
operator|.
name|getAttribute
argument_list|(
literal|"n"
argument_list|)
operator|+
literal|"' failed.\n"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fails
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|results
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|fails
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|results
argument_list|)
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
specifier|private
name|Collection
name|rootCollection
decl_stmt|;
annotation|@
name|Before
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|public
name|void
name|setUpBefore
parameter_list|()
throws|throws
name|Exception
block|{
comment|// initialize driver
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|rootCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Configuration
name|config
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|XQueryContext
operator|.
name|PROPERTY_BUILT_IN_MODULES
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"http://exist-db.org/xquery/versioning/svn"
argument_list|,
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|xquery
operator|.
name|SVNModule
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDownAfter
parameter_list|()
block|{
if|if
condition|(
name|rootCollection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
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
name|rootCollection
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
specifier|static
name|Document
name|parse
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|ParserConfigurationException
block|{
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|xr
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|xr
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
return|return
name|adapter
operator|.
name|getDocument
argument_list|()
return|;
block|}
block|}
end_class

end_unit

