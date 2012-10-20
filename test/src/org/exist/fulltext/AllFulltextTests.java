begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fulltext
package|;
end_package

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
comment|/**  */
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
name|FTIndexTest
operator|.
name|class
block|,
name|FtQueryTest
operator|.
name|class
block|,
name|FTMatchListenerTest
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|AllFulltextTests
block|{
comment|//TODO: rewrite to use others indexes (FT will be removed)
block|}
end_class

end_unit

