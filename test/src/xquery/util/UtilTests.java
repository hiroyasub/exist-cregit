begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|xquery
operator|.
name|util
package|;
end_package

begin_import
import|import
name|xquery
operator|.
name|TestRunner
import|;
end_import

begin_class
specifier|public
class|class
name|UtilTests
extends|extends
name|TestRunner
block|{
annotation|@
name|Override
specifier|protected
name|String
name|getDirectory
parameter_list|()
block|{
return|return
literal|"test/src/xquery/util"
return|;
block|}
block|}
end_class

end_unit

