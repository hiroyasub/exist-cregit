begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 17.03.2005 - $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|test
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
comment|/** Tests for various standart XQuery functions  * @author jens  */
end_comment

begin_class
specifier|public
class|class
name|XQueryFunctionsTest
extends|extends
name|TestCase
block|{
specifier|private
name|String
index|[]
name|testvalues
decl_stmt|;
specifier|private
name|String
index|[]
name|resultvalues
decl_stmt|;
specifier|private
name|XPathQueryService
name|service
decl_stmt|;
specifier|private
name|Collection
name|root
init|=
literal|null
decl_stmt|;
specifier|private
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
name|XQueryFunctionsTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructor for XQueryFunctionsTest. 	 * @param arg0 	 */
specifier|public
name|XQueryFunctionsTest
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
comment|/** Tests the XQuery-/XPath-function fn:round-half-to-even 	 * with the rounding value typed xs:integer 	 */
specifier|public
name|void
name|testRoundHtE_INTEGER
parameter_list|()
throws|throws
name|XPathException
block|{
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|query
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|""
decl_stmt|;
try|try
block|{
name|query
operator|=
literal|"fn:round-half-to-even( xs:integer('1'), 0 )"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
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
literal|"1"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"fn:round-half-to-even( xs:integer('6'), -1 )"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
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
literal|"10"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"fn:round-half-to-even( xs:integer('5'), -1 )"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
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
literal|"0"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testRoundHtE_INTEGER(): "
operator|+
name|e
argument_list|)
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
comment|/** Tests the XQuery-/XPath-function fn:round-half-to-even 	 * with the rounding value typed xs:double 	 */
specifier|public
name|void
name|testRoundHtE_DOUBLE
parameter_list|()
throws|throws
name|XPathException
block|{
comment|/* List of Values to test with Rounding */
name|String
index|[]
name|testvalues
init|=
block|{
literal|"0.5"
block|,
literal|"1.5"
block|,
literal|"2.5"
block|,
literal|"3.567812E+3"
block|,
literal|"4.7564E-3"
block|,
literal|"35612.25"
block|}
decl_stmt|;
name|String
index|[]
name|resultvalues
init|=
block|{
literal|"0.0"
block|,
literal|"2.0"
block|,
literal|"2.0"
block|,
literal|"3567.81"
block|,
literal|"0.0"
block|,
literal|"35600.0"
block|}
decl_stmt|;
name|int
index|[]
name|precision
init|=
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|2
block|,
literal|2
block|,
operator|-
literal|2
block|}
decl_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|query
init|=
literal|null
decl_stmt|;
try|try
block|{
name|XPathQueryService
name|service
init|=
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
name|testvalues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|=
literal|"fn:round-half-to-even( xs:double('"
operator|+
name|testvalues
index|[
name|i
index|]
operator|+
literal|"'), "
operator|+
name|precision
index|[
name|i
index|]
operator|+
literal|" )"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|String
name|r
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
name|resultvalues
index|[
name|i
index|]
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testRoundHtE_DOUBLE(): "
operator|+
name|e
argument_list|)
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
comment|/** Tests the XQuery-XPath function fn:tokenize() */
specifier|public
name|void
name|testTokenize
parameter_list|()
throws|throws
name|XPathException
block|{
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|""
decl_stmt|;
try|try
block|{
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"count ( tokenize('a/b' , '/') )"
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
literal|"2"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"count ( tokenize('a/b/' , '/') )"
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
literal|"3"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testTokenize(): "
operator|+
name|e
argument_list|)
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
name|testDistinctValues
parameter_list|()
throws|throws
name|XPathException
block|{
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|""
decl_stmt|;
try|try
block|{
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"declare variable $c { distinct-values(('a', 'a')) }; $c"
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
literal|"a"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"declare variable $c { distinct-values((<a>a</a>,<b>a</b>)) }; $c"
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
literal|"a"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testTokenize(): "
operator|+
name|e
argument_list|)
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
comment|/* 	 * @see TestCase#setUp() 	 */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// initialize driver
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
literal|"xmldb:exist:///db"
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
comment|/* 	 * @see TestCase#tearDown() 	 */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
comment|//System.out.println("tearDown PASSED");
block|}
block|}
end_class

end_unit

