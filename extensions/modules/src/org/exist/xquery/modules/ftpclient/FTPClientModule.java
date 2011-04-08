begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|ftpclient
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  *  * @author WStarcev  */
end_comment

begin_class
specifier|public
class|class
name|FTPClientModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/ftpclient"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"ftpclient"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2011-03-24"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.2"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|HTTP_MODULE_PERSISTENT_STATE
init|=
literal|"_eXist_ftpclient_module_persistent_state"
decl_stmt|;
specifier|private
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
name|GetDirListFunction
operator|.
name|signature
argument_list|,
name|GetDirListFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SendFileFunction
operator|.
name|signature
argument_list|,
name|SendFileFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetFileFunction
operator|.
name|signature
argument_list|,
name|GetFileFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FTPClientModule
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
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
operator|(
name|NAMESPACE_URI
operator|)
return|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
operator|(
name|PREFIX
operator|)
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
operator|(
literal|"A module for performing FTP requests as a client"
operator|)
return|;
block|}
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
operator|(
name|RELEASED_IN_VERSION
operator|)
return|;
block|}
block|}
end_class

end_unit

