begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: pak  * Date: Apr 17, 2005  * Time: 7:41:35 PM  * To change this template use File | Settings | File Templates.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
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
name|XmldbURI
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

begin_comment
comment|/**  * an ant task to add a user  *  * @author peter.klotz@blue-elephant-systems.com  */
end_comment

begin_class
specifier|public
class|class
name|AddUserTask
extends|extends
name|UserTask
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|primaryGroup
decl_stmt|;
specifier|private
name|String
name|home
decl_stmt|;
specifier|private
name|String
name|secret
decl_stmt|;
comment|/* (non-Javadoc)    * @see org.apache.tools.ant.Task#execute()    */
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
name|super
operator|.
name|execute
argument_list|()
expr_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Must specify at leat a user name"
argument_list|)
throw|;
try|try
block|{
name|User
name|usr
init|=
operator|new
name|User
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|secret
operator|!=
literal|null
condition|)
name|usr
operator|.
name|setPassword
argument_list|(
name|secret
argument_list|)
expr_stmt|;
if|if
condition|(
name|home
operator|!=
literal|null
condition|)
name|usr
operator|.
name|setHome
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|home
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|primaryGroup
operator|!=
literal|null
condition|)
name|usr
operator|.
name|addGroup
argument_list|(
name|primaryGroup
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Adding user "
operator|+
name|name
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
name|service
operator|.
name|addUser
argument_list|(
name|usr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"XMLDB exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"XMLDB exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|setPrimaryGroup
parameter_list|(
name|String
name|primaryGroup
parameter_list|)
block|{
name|this
operator|.
name|primaryGroup
operator|=
name|primaryGroup
expr_stmt|;
block|}
specifier|public
name|void
name|setHome
parameter_list|(
name|String
name|home
parameter_list|)
block|{
name|this
operator|.
name|home
operator|=
name|home
expr_stmt|;
block|}
specifier|public
name|void
name|setSecret
parameter_list|(
name|String
name|secret
parameter_list|)
block|{
name|this
operator|.
name|secret
operator|=
name|secret
expr_stmt|;
block|}
block|}
end_class

end_unit

