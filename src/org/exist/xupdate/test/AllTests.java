begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xupdate
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
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
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

begin_comment
comment|/**  * @author berlinge-to  *  * To change this generated comment edit the template variable "typecomment":  * Window>Preferences>Java>Templates.  * To enable and disable the creation of type comments go to  * Window>Preferences>Java>Code Generation.  */
end_comment

begin_class
specifier|public
class|class
name|AllTests
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
name|XUpdateTest
name|test
init|=
operator|new
name|XUpdateTest
argument_list|()
decl_stmt|;
name|TestSuite
name|suite
init|=
operator|new
name|TestSuite
argument_list|(
literal|"Test suite for org.exist.xupdate"
argument_list|)
decl_stmt|;
comment|//$JUnit-BEGIN$
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"append"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"insertafter"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"insertbefore"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"remove"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"update"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"appendAttribute"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"appendChild"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"insertafter_big"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"conditional"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"variables"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"replace"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"whitespace"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|XUpdateTestCases
argument_list|(
literal|"namespaces"
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
comment|/*          * create new TestCase          * -------------------          * add the following line:          *          * suite.addTest(new XUpdateTests(<TestName>, exist));          *           * Param: TestName is the filename of the XUpdateStatement xml file (without '.xml').          *           */
comment|//$JUnit-END$
return|return
name|suite
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

