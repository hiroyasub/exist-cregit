begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

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
name|Service
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
comment|/**  * A service to manage the database instance. The service defines  * a single method shutdown() to shut down the database instance  * used by the current driver.  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|DatabaseInstanceManager
extends|extends
name|Service
block|{
comment|/** 	 * Immediately shutdown the current database instance. 	 * This current user should be a member of the "dba" group 	 * or an exception will be thrown. 	 *  	 * @throws XMLDBException 	 */
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Shutdown the current database instance after the specified 	 * delay (in milliseconds). 	 * This current user should be a member of the "dba" group 	 * or an exception will be thrown. 	 *  	 * @throws XMLDBException 	 */
specifier|public
name|void
name|shutdown
parameter_list|(
name|long
name|delay
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|boolean
name|enterServiceMode
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|exitServiceMode
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|DatabaseStatus
name|getStatus
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Returns true if the database instance is running local, i.e. in 	 * the same thread as this service. 	 *   	 * @return true if the database instance is running local 	 */
specifier|public
name|boolean
name|isLocalInstance
parameter_list|()
function_decl|;
comment|/** 	 * Returns true if XACML is enabled for the database instance.  	 * 	 * @return True if XACML is enabled 	 */
specifier|public
name|boolean
name|isXACMLEnabled
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|restorePkgRepo
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

