begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|xquery
operator|.
name|contentextraction
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
literal|"src/test/xquery/contentextraction"
block|}
argument_list|)
specifier|public
class|class
name|ContentExtractionTests
block|{ }
end_class

end_unit

