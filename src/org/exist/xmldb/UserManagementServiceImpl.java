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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
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
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    26. August 2002  */
end_comment

begin_class
specifier|public
class|class
name|UserManagementServiceImpl
implements|implements
name|UserManagementService
block|{
specifier|private
name|CollectionImpl
name|parent
decl_stmt|;
specifier|public
name|UserManagementServiceImpl
parameter_list|(
name|CollectionImpl
name|collection
parameter_list|)
block|{
name|parent
operator|=
name|collection
expr_stmt|;
block|}
comment|/** 	 *  Add a new user account 	 * 	 *@param  user                The user to be added 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|void
name|addUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|user
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|Vector
name|groups
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|user
operator|.
name|getGroups
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|groups
operator|.
name|addElement
argument_list|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|user
operator|.
name|getHome
argument_list|()
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setUser"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Change access mode of a resource 	 * 	 *@param  mode                Access mode 	 *@param  res                 Description of the Parameter 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|void
name|chmod
parameter_list|(
name|Resource
name|res
parameter_list|,
name|String
name|mode
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|path
init|=
operator|(
operator|(
name|CollectionImpl
operator|)
name|res
operator|.
name|getParentCollection
argument_list|()
operator|)
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getId
argument_list|()
decl_stmt|;
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|mode
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setPermissions"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 * @see org.exist.xmldb.UserManagementService#chmod(org.xmldb.api.base.Resource, int) 	 */
specifier|public
name|void
name|chmod
parameter_list|(
name|Resource
name|res
parameter_list|,
name|int
name|mode
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|path
init|=
operator|(
operator|(
name|CollectionImpl
operator|)
name|res
operator|.
name|getParentCollection
argument_list|()
operator|)
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getId
argument_list|()
decl_stmt|;
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|mode
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setPermissions"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Change access mode of the current collection 	 * 	 *@param  mode                Access mode 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|void
name|chmod
parameter_list|(
name|String
name|mode
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|mode
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setPermissions"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 * @see org.exist.xmldb.UserManagementService#chmod(int) 	 */
specifier|public
name|void
name|chmod
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|mode
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setPermissions"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Change the owner of the current collection 	 * 	 *@param  u                   Description of the Parameter 	 *@param  group               Description of the Parameter 	 *@exception  XMLDBException  Description of the Exception 	 */
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
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|u
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setPermissions"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Change the owner of a resource 	 * 	 *@param  res                 Resource 	 *@param  u                   The new owner of the resource 	 *@param  group               The owner group 	 *@exception  XMLDBException  Description of the Exception 	 */
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
block|{
name|String
name|path
init|=
operator|(
operator|(
name|CollectionImpl
operator|)
name|res
operator|.
name|getParentCollection
argument_list|()
operator|)
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getId
argument_list|()
decl_stmt|;
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|u
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setPermissions"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|// -- Constructor
comment|/** 	 *  Gets the name attribute of the UserManagementServiceImpl object 	 * 	 *@return    The name value 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"UserManagementService"
return|;
block|}
comment|/** 	 *  Get current permissions for a collection 	 * 	 *@param  coll                Collection 	 *@return                     The permissions value 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|Permission
name|getPermissions
parameter_list|(
name|Collection
name|coll
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|coll
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"collection is null"
argument_list|)
throw|;
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|(
operator|(
name|CollectionImpl
operator|)
name|coll
operator|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Hashtable
name|result
init|=
operator|(
name|Hashtable
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getPermissions"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Permission
name|perm
init|=
operator|new
name|Permission
argument_list|(
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|,
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"group"
argument_list|)
argument_list|)
decl_stmt|;
name|perm
operator|.
name|setPermissions
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"permissions"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|perm
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Get current permissions for a resource 	 * 	 *@param  res                 Description of the Parameter 	 *@return                     The permissions value 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|Permission
name|getPermissions
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|res
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"resource is null"
argument_list|)
throw|;
name|String
name|path
init|=
operator|(
operator|(
name|CollectionImpl
operator|)
name|res
operator|.
name|getParentCollection
argument_list|()
operator|)
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getId
argument_list|()
decl_stmt|;
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Hashtable
name|result
init|=
operator|(
name|Hashtable
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getPermissions"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Permission
name|perm
init|=
operator|new
name|Permission
argument_list|(
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|,
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"group"
argument_list|)
argument_list|)
decl_stmt|;
name|perm
operator|.
name|setPermissions
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"permissions"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|perm
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|Permission
index|[]
name|listResourcePermissions
parameter_list|()
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Hashtable
name|result
init|=
operator|(
name|Hashtable
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"listDocumentPermissions"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Permission
name|perm
index|[]
init|=
operator|new
name|Permission
index|[
name|result
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|String
index|[]
name|resources
init|=
name|parent
operator|.
name|listResources
argument_list|()
decl_stmt|;
name|Vector
name|t
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|resources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
operator|=
operator|(
name|Vector
operator|)
name|result
operator|.
name|get
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|perm
index|[
name|i
index|]
operator|=
operator|new
name|Permission
argument_list|()
expr_stmt|;
name|perm
index|[
name|i
index|]
operator|.
name|setOwner
argument_list|(
operator|(
name|String
operator|)
name|t
operator|.
name|elementAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|perm
index|[
name|i
index|]
operator|.
name|setGroup
argument_list|(
operator|(
name|String
operator|)
name|t
operator|.
name|elementAt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|perm
index|[
name|i
index|]
operator|.
name|setPermissions
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|t
operator|.
name|elementAt
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|perm
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|Permission
index|[]
name|listCollectionPermissions
parameter_list|()
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Hashtable
name|result
init|=
operator|(
name|Hashtable
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"listCollectionPermissions"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Permission
name|perm
index|[]
init|=
operator|new
name|Permission
index|[
name|result
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|String
name|collections
index|[]
init|=
name|parent
operator|.
name|listChildCollections
argument_list|()
decl_stmt|;
name|Vector
name|t
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|collections
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
operator|=
operator|(
name|Vector
operator|)
name|result
operator|.
name|get
argument_list|(
name|collections
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|perm
index|[
name|i
index|]
operator|=
operator|new
name|Permission
argument_list|()
expr_stmt|;
name|perm
index|[
name|i
index|]
operator|.
name|setOwner
argument_list|(
operator|(
name|String
operator|)
name|t
operator|.
name|elementAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|perm
index|[
name|i
index|]
operator|.
name|setGroup
argument_list|(
operator|(
name|String
operator|)
name|t
operator|.
name|elementAt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|perm
index|[
name|i
index|]
operator|.
name|setPermissions
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|t
operator|.
name|elementAt
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|perm
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Gets the property attribute of the UserManagementServiceImpl object 	 * 	 *@param  property            Description of the Parameter 	 *@return                     The property value 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 *  Get user information for specified user 	 * 	 *@param  name                Description of the Parameter 	 *@return                     The user value 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|User
name|getUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Hashtable
name|tab
init|=
operator|(
name|Hashtable
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getUser"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|User
name|u
init|=
operator|new
name|User
argument_list|(
operator|(
name|String
operator|)
name|tab
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Vector
name|groups
init|=
operator|(
name|Vector
operator|)
name|tab
operator|.
name|get
argument_list|(
literal|"groups"
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|groups
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|u
operator|.
name|addGroup
argument_list|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|home
init|=
operator|(
name|String
operator|)
name|tab
operator|.
name|get
argument_list|(
literal|"home"
argument_list|)
decl_stmt|;
name|u
operator|.
name|setHome
argument_list|(
name|home
argument_list|)
expr_stmt|;
return|return
name|u
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Get a list of all users currently defined 	 * 	 *@return                     The users value 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|User
index|[]
name|getUsers
parameter_list|()
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|users
init|=
operator|(
name|Vector
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getUsers"
argument_list|,
operator|new
name|Vector
argument_list|()
argument_list|)
decl_stmt|;
name|User
index|[]
name|u
init|=
operator|new
name|User
index|[
name|users
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|u
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Hashtable
name|tab
init|=
operator|(
name|Hashtable
operator|)
name|users
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|u
index|[
name|i
index|]
operator|=
operator|new
name|User
argument_list|(
operator|(
name|String
operator|)
name|tab
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Vector
name|groups
init|=
operator|(
name|Vector
operator|)
name|tab
operator|.
name|get
argument_list|(
literal|"groups"
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|groups
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|u
index|[
name|i
index|]
operator|.
name|addGroup
argument_list|(
operator|(
name|String
operator|)
name|j
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|u
index|[
name|i
index|]
operator|.
name|setHome
argument_list|(
operator|(
name|String
operator|)
name|tab
operator|.
name|get
argument_list|(
literal|"home"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|u
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Gets the version attribute of the UserManagementServiceImpl object 	 * 	 *@return    The version value 	 */
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"1.0"
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  name                Description of the Parameter 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|void
name|removeUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"removeUser"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
comment|/** 	 *  Sets the collection attribute of the UserManagementServiceImpl object 	 * 	 *@param  collection          The new collection value 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|parent
operator|=
operator|(
name|CollectionImpl
operator|)
name|collection
expr_stmt|;
block|}
comment|/** 	 *  Sets the property attribute of the UserManagementServiceImpl object 	 * 	 *@param  property            The new property value 	 *@param  value               The new property value 	 *@exception  XMLDBException  Description of the Exception 	 */
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
block|{
block|}
comment|/** 	 *  Update the specified user 	 * 	 *@param  user                Description of the Parameter 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|void
name|updateUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|user
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|Vector
name|groups
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|user
operator|.
name|getGroups
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|groups
operator|.
name|addElement
argument_list|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|user
operator|.
name|getHome
argument_list|()
argument_list|)
expr_stmt|;
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setUser"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
block|}
end_class

begin_comment
comment|// -- end class UserManagementServiceImpl
end_comment

end_unit

