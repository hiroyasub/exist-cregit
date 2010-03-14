begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|gate
operator|.
name|desktop
package|;
end_package

begin_class
specifier|public
class|class
name|WindowsDesktop
extends|extends
name|Desktop
block|{
specifier|protected
name|String
name|openFileCmd
parameter_list|()
block|{
return|return
literal|"cmd /c start"
return|;
block|}
block|}
end_class

end_unit

