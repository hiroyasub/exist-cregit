begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|inspect
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AbstractInternalModule
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
name|FunctionDef
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_class
specifier|public
class|class
name|InspectionModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/inspection"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"inspect"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASE
init|=
literal|"2.0"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|InspectFunction
operator|.
name|SIGNATURE
argument_list|,
name|InspectFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|InspectModule
operator|.
name|FNS_INSPECT_MODULE
argument_list|,
name|InspectModule
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|InspectModule
operator|.
name|FNS_INSPECT_MODULE_URI
argument_list|,
name|InspectModule
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleFunctions
operator|.
name|FNS_MODULE_FUNCTIONS_CURRENT
argument_list|,
name|ModuleFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleFunctions
operator|.
name|FNS_MODULE_FUNCTIONS_OTHER
argument_list|,
name|ModuleFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleFunctions
operator|.
name|FNS_MODULE_FUNCTIONS_OTHER_URI
argument_list|,
name|ModuleFunctions
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|InspectionModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Functions for inspecting XQuery modules and functions"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASE
return|;
block|}
block|}
end_class

end_unit

