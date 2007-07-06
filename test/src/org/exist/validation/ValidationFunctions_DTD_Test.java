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
name|validation
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|storage
operator|.
name|DBBroker
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
name|xquery
operator|.
name|XPathException
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
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  *  Set of Tests for validation:validate($a) and validation:validate($a, $b)  * regaring validatin using DTD's.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ValidationFunctions_DTD_Test
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ValidationFunctions_DTD_Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XPathQueryService
name|service
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|root
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Database
name|database
init|=
literal|null
decl_stmt|;
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
name|XPathException
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|ValidationFunctions_XSD_Test
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ValidationFunctions_DTD_Test
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testsetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|database
operator|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
expr_stmt|;
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
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|service
operator|=
operator|(
name|XPathQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
comment|// ===========================================================
specifier|private
name|void
name|clearGrammarCache
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Clearing grammar cache"
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:clear-grammar-cache()"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
comment|// ===========================================================
specifier|public
name|void
name|testValidateUsingSystemCatalog
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// DTD for hamlet_valid.xml is registered in system catalog.
comment|// result should be "document is valid"
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP
operator|+
literal|"/hamlet_valid.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hamlet_valid.xml in systemcatalog"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testSpecifiedCatalog
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test1"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/hamlet_valid.xml') ,"
operator|+
literal|" xs:anyURI('/db/validation/dtd/catalog.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test2"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_invalid.xml') ,"
operator|+
literal|" xs:anyURI('/db/validation/dtd/catalog.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test3"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/xsd/catalog.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong catalog"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test4"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_invalid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/xsd/catalog.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong catalog, invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|bugtestSpecifiedGrammar
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test1"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/dtd/hamlet.dtd') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test2"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_invalid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/dtd/hamlet.dtd') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testSearchedGrammar
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test1"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/dtd/') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test2"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/xsd/') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document, not found"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test3"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test4"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_invalid.xml'), "
operator|+
literal|" xs:anyURI('/db/') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testtearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
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
block|}
end_class

end_unit

