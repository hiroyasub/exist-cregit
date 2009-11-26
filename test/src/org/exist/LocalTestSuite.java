begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|AllTriggerTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|fulltext
operator|.
name|AllFulltextTests
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
name|XmldbLocalTests
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
name|AllXqueryTests
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
name|OptimizerTest
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Suite
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Suite
operator|.
name|class
argument_list|)
annotation|@
name|Suite
operator|.
name|SuiteClasses
argument_list|(
block|{
name|XmldbLocalTests
operator|.
name|class
block|,
comment|//        AllXupdateTests.class,
name|AllXqueryTests
operator|.
name|class
block|,
name|OptimizerTest
operator|.
name|class
block|,
name|AllTriggerTests
operator|.
name|class
block|,
name|AllFulltextTests
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|LocalTestSuite
block|{ }
end_class

end_unit

