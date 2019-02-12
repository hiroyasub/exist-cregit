begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|xquery
operator|.
name|optimizer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|runner
operator|.
name|XSuite
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_comment
comment|/**  * XQuery optimizer tests  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|XSuite
operator|.
name|class
argument_list|)
annotation|@
name|XSuite
operator|.
name|XSuiteFiles
argument_list|(
block|{
literal|"test/src/xquery/optimizer"
block|}
argument_list|)
specifier|public
class|class
name|OptimizerTests
block|{ }
end_class

end_unit

