begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  *     * Copyright (C) 2001-04 Wolfgang M. Meier wolfgang@exist-db.org  *   * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *   * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
end_comment

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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
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
name|PermissionDeniedException
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
name|storage
operator|.
name|BrokerPool
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
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|LockException
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

begin_class
specifier|public
class|class
name|LocalCollectionManagementService
implements|implements
name|CollectionManagementServiceImpl
block|{
specifier|protected
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|protected
name|LocalCollection
name|parent
init|=
literal|null
decl_stmt|;
specifier|protected
name|User
name|user
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LocalCollectionManagementService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|LocalCollectionManagementService
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|parent
parameter_list|)
block|{
if|if
condition|(
name|user
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
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
name|collName
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
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|coll
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|collName
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|coll
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
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
literal|"failed to create collection "
operator|+
name|collName
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"not allowed to create collection"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LocalCollection
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|parent
argument_list|,
name|collName
argument_list|)
return|;
block|}
comment|/**      *  Creates a new collection in the database identified by name and using      *  the provided configuration.      *      *@param  path                the path of the new collection      *@param  configuration       the XML collection configuration to use for      *      creating this collection.      *@return                     The newly created collection      *@exception  XMLDBException      */
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
name|path
init|=
operator|(
name|collName
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|?
name|collName
else|:
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|collName
operator|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|path
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|path
operator|+
literal|" not found"
argument_list|)
throw|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing collection "
operator|+
name|path
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"failed to remove collection "
operator|+
name|collName
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
finally|finally
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
name|collection
operator|.
name|release
argument_list|()
expr_stmt|;
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.CollectionManagementServiceImpl#move(org.xmldb.api.base.Collection, org.xmldb.api.base.Collection, java.lang.String)      */
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
if|if
condition|(
operator|!
name|collectionPath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
name|collectionPath
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|collectionPath
expr_stmt|;
if|if
condition|(
operator|!
name|destinationPath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
name|destinationPath
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|destinationPath
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|destination
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collectionPath
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|collectionPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|destination
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|destinationPath
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|destinationPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|broker
operator|.
name|moveCollection
argument_list|(
name|collection
argument_list|,
name|destination
argument_list|,
name|newName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"failed to move collection "
operator|+
name|collectionPath
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
finally|finally
block|{
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
name|destination
operator|.
name|release
argument_list|()
expr_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
name|collection
operator|.
name|release
argument_list|()
expr_stmt|;
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
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
if|if
condition|(
operator|!
name|resourcePath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
name|resourcePath
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|resourcePath
expr_stmt|;
if|if
condition|(
operator|!
name|destinationPath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
name|destinationPath
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|destinationPath
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|destination
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|source
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
name|resourcePath
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|collName
init|=
name|resourcePath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|String
name|docName
init|=
name|resourcePath
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
decl_stmt|;
name|source
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collName
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|collName
operator|+
literal|" not found"
argument_list|)
throw|;
name|DocumentImpl
name|doc
init|=
name|source
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|docName
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_RESOURCE
argument_list|,
literal|"Resource "
operator|+
name|resourcePath
operator|+
literal|" not found"
argument_list|)
throw|;
name|destination
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|destinationPath
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|destinationPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|broker
operator|.
name|moveResource
argument_list|(
name|doc
argument_list|,
name|destination
argument_list|,
name|newName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"failed to move resource "
operator|+
name|resourcePath
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
finally|finally
block|{
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
name|source
operator|.
name|release
argument_list|()
expr_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
name|destination
operator|.
name|release
argument_list|()
expr_stmt|;
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.CollectionManagementServiceImpl#copyResource(java.lang.String, java.lang.String, java.lang.String) 	 */
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
if|if
condition|(
operator|!
name|resourcePath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
name|resourcePath
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|resourcePath
expr_stmt|;
if|if
condition|(
operator|!
name|destinationPath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
name|destinationPath
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|destinationPath
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|destination
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|source
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
name|resourcePath
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|collName
init|=
name|resourcePath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|String
name|docName
init|=
name|resourcePath
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
decl_stmt|;
name|source
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collName
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|collName
operator|+
literal|" not found"
argument_list|)
throw|;
name|DocumentImpl
name|doc
init|=
name|source
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|docName
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_RESOURCE
argument_list|,
literal|"Resource "
operator|+
name|resourcePath
operator|+
literal|" not found"
argument_list|)
throw|;
name|destination
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|destinationPath
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|destinationPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|broker
operator|.
name|copyResource
argument_list|(
name|doc
argument_list|,
name|destination
argument_list|,
name|newName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"failed to move resource "
operator|+
name|resourcePath
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
finally|finally
block|{
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
name|source
operator|.
name|release
argument_list|()
expr_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
name|destination
operator|.
name|release
argument_list|()
expr_stmt|;
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|!
name|collectionPath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
name|collectionPath
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|collectionPath
expr_stmt|;
if|if
condition|(
operator|!
name|destinationPath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
name|destinationPath
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|destinationPath
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|destination
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collectionPath
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|collectionPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|destination
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|destinationPath
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|destinationPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|broker
operator|.
name|copyCollection
argument_list|(
name|collection
argument_list|,
name|destination
argument_list|,
name|newName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"failed to move collection "
operator|+
name|collectionPath
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
finally|finally
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
name|collection
operator|.
name|release
argument_list|()
expr_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
name|collection
operator|.
name|release
argument_list|()
expr_stmt|;
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
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
name|LocalCollection
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
block|}
end_class

end_unit

