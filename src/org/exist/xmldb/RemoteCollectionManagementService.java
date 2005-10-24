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
name|XmlRpcClient
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
name|storage
operator|.
name|NativeBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|Collection
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
name|ErrorCodes
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
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_class
specifier|public
class|class
name|RemoteCollectionManagementService
implements|implements
name|CollectionManagementServiceImpl
block|{
specifier|protected
name|XmlRpcClient
name|client
decl_stmt|;
specifier|protected
name|RemoteCollection
name|parent
init|=
literal|null
decl_stmt|;
specifier|public
name|RemoteCollectionManagementService
parameter_list|(
name|RemoteCollection
name|parent
parameter_list|,
name|XmlRpcClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|String
name|collName
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|createCollection
argument_list|(
name|collName
argument_list|,
operator|(
name|Date
operator|)
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|String
name|collName
parameter_list|,
name|Date
name|created
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|name
init|=
name|collName
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|collName
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
operator|)
operator|&&
name|parent
operator|!=
literal|null
condition|)
name|name
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|"/"
operator|+
name|collName
expr_stmt|;
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
if|if
condition|(
name|created
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|addElement
argument_list|(
name|created
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
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
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|RemoteCollection
name|collection
init|=
operator|new
name|RemoteCollection
argument_list|(
name|client
argument_list|,
operator|(
name|RemoteCollection
operator|)
name|parent
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|parent
operator|.
name|addChildCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
return|return
name|collection
return|;
block|}
comment|/**      *  Implements createCollection from interface CollectionManager. Gets      *  called by some applications based on Xindice.      *      *@param  path                Description of the Parameter      *@param  configuration       Description of the Parameter      *@return                     Description of the Return Value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|String
name|path
parameter_list|,
name|Document
name|configuration
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|createCollection
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"CollectionManagementService"
return|;
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getVersion
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"1.0"
return|;
block|}
specifier|public
name|void
name|removeCollection
parameter_list|(
name|String
name|collName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|name
init|=
name|collName
decl_stmt|;
if|if
condition|(
operator|!
name|collName
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|name
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|collName
expr_stmt|;
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
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"removeCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
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
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|parent
operator|.
name|removeChildCollection
argument_list|(
name|collName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|parent
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|parent
operator|=
operator|(
name|RemoteCollection
operator|)
name|parent
expr_stmt|;
block|}
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
block|{
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.CollectionManagementServiceImpl#move(java.lang.String, java.lang.String, java.lang.String)      */
specifier|public
name|void
name|move
parameter_list|(
name|String
name|collectionPath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|/*  if(!collectionPath.startsWith("/db/"))             collectionPath = parent.getPath() + '/' + collectionPath;*/
name|collectionPath
operator|=
name|NativeBroker
operator|.
name|checkPath
argument_list|(
name|collectionPath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|/*if(destinationPath != null)         {         	if(!destinationPath.startsWith("/db/"))         		destinationPath = parent.getPath() + '/' + destinationPath;         }         else         {         	destinationPath = parent.getPath();         }*/
name|destinationPath
operator|=
name|NativeBroker
operator|.
name|checkPath
argument_list|(
name|destinationPath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
condition|)
block|{
name|int
name|p
init|=
name|collectionPath
operator|.
name|lastIndexOf
argument_list|(
operator|(
literal|'/'
operator|)
argument_list|)
decl_stmt|;
name|newName
operator|=
name|collectionPath
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|collectionPath
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|destinationPath
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|newName
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"moveCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
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
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.CollectionManagementServiceImpl#moveResource(java.lang.String, java.lang.String, java.lang.String)      */
specifier|public
name|void
name|moveResource
parameter_list|(
name|String
name|resourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|/*if(!resourcePath.startsWith("/db/"))             resourcePath = parent.getPath() + '/' + resourcePath;*/
name|resourcePath
operator|=
name|NativeBroker
operator|.
name|checkPath
argument_list|(
name|resourcePath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|/*if(destinationPath != null)         {         	if(!destinationPath.startsWith("/db/"))         		destinationPath = parent.getPath() + '/' + destinationPath;         }         else         {         	destinationPath = parent.getPath();         }*/
name|destinationPath
operator|=
name|NativeBroker
operator|.
name|checkPath
argument_list|(
name|destinationPath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
condition|)
block|{
name|int
name|p
init|=
name|resourcePath
operator|.
name|lastIndexOf
argument_list|(
operator|(
literal|'/'
operator|)
argument_list|)
decl_stmt|;
name|newName
operator|=
name|resourcePath
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|resourcePath
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|destinationPath
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|newName
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"moveResource"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
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
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.CollectionManagementServiceImpl#copy(java.lang.String, java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|copy
parameter_list|(
name|String
name|collectionPath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|/*if(!collectionPath.startsWith("/db/"))             collectionPath = parent.getPath() + '/' + collectionPath;*/
name|collectionPath
operator|=
name|NativeBroker
operator|.
name|checkPath
argument_list|(
name|collectionPath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|/*if(!destinationPath.startsWith("/db/"))             destinationPath = parent.getPath() + '/' + destinationPath;*/
name|destinationPath
operator|=
name|NativeBroker
operator|.
name|checkPath
argument_list|(
name|destinationPath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
condition|)
block|{
name|int
name|p
init|=
name|collectionPath
operator|.
name|lastIndexOf
argument_list|(
operator|(
literal|'/'
operator|)
argument_list|)
decl_stmt|;
name|newName
operator|=
name|collectionPath
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|collectionPath
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|destinationPath
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|newName
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"copyCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
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
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.CollectionManagementServiceImpl#copyResource(java.lang.String, java.lang.String, java.lang.String)      */
specifier|public
name|void
name|copyResource
parameter_list|(
name|String
name|resourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|/*if(!resourcePath.startsWith("/db/"))             resourcePath = parent.getPath() + '/' + resourcePath;*/
name|resourcePath
operator|=
name|NativeBroker
operator|.
name|checkPath
argument_list|(
name|resourcePath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|/*if(!destinationPath.startsWith("/db/"))             destinationPath = parent.getPath() + '/' + destinationPath;*/
name|destinationPath
operator|=
name|NativeBroker
operator|.
name|checkPath
argument_list|(
name|destinationPath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
condition|)
block|{
name|int
name|p
init|=
name|resourcePath
operator|.
name|lastIndexOf
argument_list|(
operator|(
literal|'/'
operator|)
argument_list|)
decl_stmt|;
name|newName
operator|=
name|resourcePath
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|resourcePath
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|destinationPath
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|newName
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"copyResource"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
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
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

