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
name|exist
operator|.
name|security
operator|.
name|Permission
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  An eXist-specific service which provides methods to manage users and  *  permissions.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    20. August 2002  */
end_comment

begin_interface
specifier|public
interface|interface
name|UserManagementService
extends|extends
name|Service
block|{
comment|/**      *  Get the name of this service      *      *@return    The name      */
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      *  Get the version of this service      *      *@return    The version value      */
specifier|public
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**      *  Change owner and group of the current collection.      *      *@param  u                   Description of the Parameter      *@param  group               Description of the Parameter      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|chown
parameter_list|(
name|User
name|u
parameter_list|,
name|String
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Change owner and group of the specified resource.      *      *@param  res                 Description of the Parameter      *@param  u                   Description of the Parameter      *@param  group               Description of the Parameter      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|chown
parameter_list|(
name|Resource
name|res
parameter_list|,
name|User
name|u
parameter_list|,
name|String
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Change permissions for the specified resource.      *      * Permissions are specified in a string according to the      * following format:      *       *<pre>[user|group|other]=[+|-][read|write|update]</pre>      *       * For example, to grant all permissions to the group and      * deny everything to others:      *       * group=+write,+read,+update,other=-read      *       * The changes are applied to the permissions currently      * active for this resource.      *       *@param  resource            Description of the Parameter      *@param  modeStr             Description of the Parameter      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|chmod
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|String
name|modeStr
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Change permissions for the current collection      *      *@param  modeStr             String describing the permissions to      * grant or deny.      *@exception  XMLDBException      *       */
specifier|public
name|void
name|chmod
parameter_list|(
name|String
name|modeStr
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Add a new user to the database      *      *@param  user                The feature to be added to the User attribute      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|addUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Update existing user information      *      *@param  user                Description of the Parameter      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|updateUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Get a user record from the database      *      *@param  name                Description of the Parameter      *@return                     The user value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|User
name|getUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Retrieve a list of all existing users.      *      *@return                     The users value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|User
index|[]
name|getUsers
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Get a property defined by this service.      *      *@param  property            Description of the Parameter      *@return                     The property value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Set a property for this service.      *      *@param  property            The new property value      *@param  value               The new property value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Set the current collection for this service      *      *@param  collection          The new collection value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Get permissions for the specified collections      *      *@param  coll                Description of the Parameter      *@return                     The permissions value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Permission
name|getPermissions
parameter_list|(
name|Collection
name|coll
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Get permissions for the specified resource      *      *@param  res                 Description of the Parameter      *@return                     The permissions value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Permission
name|getPermissions
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get permissions for all resources contained in the current      * collection. Returns a list of permissions in the same order      * as Collection.listResources().      *       * @return Permission[]      * @throws XMLDBException      */
specifier|public
name|Permission
index|[]
name|listResourcePermissions
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get permissions for all child collections contained in the current      * collection. Returns a list of permissions in the same order      * as Collection.listChildCollections().      *       * @return Permission[]      * @throws XMLDBException      */
specifier|public
name|Permission
index|[]
name|listCollectionPermissions
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Delete a user from the database      *      *@param  name                Description of the Parameter      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|removeUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

