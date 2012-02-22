begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
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
name|Group
import|;
end_import

begin_comment
comment|/**  * Created by IntelliJ IDEA. User: lcahlander Date: Aug 25, 2010 Time: 3:09:13 PM To change this template use File | Settings | File Templates.  */
end_comment

begin_class
specifier|public
class|class
name|RemoveGroupTask
extends|extends
name|UserTask
block|{
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
comment|/* (non-Javadoc)      * @see org.apache.tools.ant.Task#execute()      */
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
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
literal|"You have to specify a name"
argument_list|)
operator|)
throw|;
block|}
name|log
argument_list|(
literal|"Removing group "
operator|+
name|name
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
try|try
block|{
name|Group
name|group
init|=
name|service
operator|.
name|getGroup
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|removeGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
argument_list|(
literal|"Group "
operator|+
name|name
operator|+
literal|" does not exist."
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"XMLDB exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|failonerror
condition|)
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
operator|)
throw|;
block|}
else|else
block|{
name|log
argument_list|(
name|msg
argument_list|,
name|e
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

