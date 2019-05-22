begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|launcher
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Interface for managing platform native Services.  *  * @author Adam Retter  */
end_comment

begin_interface
specifier|public
interface|interface
name|ServiceManager
block|{
comment|//TODO(AR) expand to support multiple services by adding a Service interface and pass that as a parameter to each function below
comment|/**      * Installs the Service.      *      * If the service is already installed an exception is raised.      *      * @throws ServiceManagerException if an error occurs whilst installing the service.      */
name|void
name|install
parameter_list|()
throws|throws
name|ServiceManagerException
function_decl|;
comment|/**      * Returns true if the Service is installed.      *      * @return true if the service is installed, false otherwise.      */
name|boolean
name|isInstalled
parameter_list|()
function_decl|;
comment|/**      * Uninstalls the Service.      *      * If the service is already uninstalled an exception is raised.      *      * @throws ServiceManagerException if an error occurs whilst uninstalling the service.      */
name|void
name|uninstall
parameter_list|()
throws|throws
name|ServiceManagerException
function_decl|;
comment|/**      * Starts the Service.      *      * If the service is already started, this is a noop      *      * @throws ServiceManagerException if an error occurs whilst starting the service.      */
name|void
name|start
parameter_list|()
throws|throws
name|ServiceManagerException
function_decl|;
comment|/**      * Returns true if the Service is running      *      * @return true if the service is running, false otherwise.      */
name|boolean
name|isRunning
parameter_list|()
function_decl|;
comment|/**      * Stops the Service.      *      * If the service is already stopped, this is a noop      *      * @throws ServiceManagerException if an error occurs whilst stopping the service.      */
name|void
name|stop
parameter_list|()
throws|throws
name|ServiceManagerException
function_decl|;
comment|/**      * Show the platforms native Service Management console.      *      * @throws UnsupportedOperationException if the service manager      *      does not support showing the platforms native Service      *      ManagementConsole.      *      * @throws ServiceManagerException if an error occurs opening the console.      */
name|void
name|showNativeServiceManagementConsole
parameter_list|()
throws|throws
name|UnsupportedOperationException
throws|,
name|ServiceManagerException
function_decl|;
block|}
end_interface

end_unit

