begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webdav
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|IndexInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|PermissionDeniedException
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
name|txn
operator|.
name|TransactionManager
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
name|txn
operator|.
name|Txn
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
name|storage
operator|.
name|lock
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
name|exist
operator|.
name|util
operator|.
name|MimeTable
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
name|MimeType
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
name|VirtualTempFile
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
name|VirtualTempFileInputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|webdav
operator|.
name|exceptions
operator|.
name|CollectionDoesNotExistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|webdav
operator|.
name|exceptions
operator|.
name|CollectionExistsException
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Class for accessing the Collection class of the exist-db native API.  *  * @author Dannes Wessels (dizzzz_at_exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ExistCollection
extends|extends
name|ExistResource
block|{
specifier|public
name|ExistCollection
parameter_list|(
name|XmldbURI
name|uri
parameter_list|,
name|BrokerPool
name|pool
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"New collection object for "
operator|+
name|uri
argument_list|)
expr_stmt|;
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|xmldbUri
operator|=
name|uri
expr_stmt|;
block|}
comment|/**      * Initialize Collection, authenticate() is required first      */
annotation|@
name|Override
specifier|public
name|void
name|initMetadata
parameter_list|()
block|{
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"User not initialized yet"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// check if initialization is required
if|if
condition|(
name|isInitialized
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Already initialized"
argument_list|)
expr_stmt|;
return|return;
block|}
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Get access to collection
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|xmldbUri
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
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Collection for "
operator|+
name|xmldbUri
operator|+
literal|" cannot be opened for  metadata"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Retrieve some meta data
name|permissions
operator|=
name|collection
operator|.
name|getPermissions
argument_list|()
expr_stmt|;
name|readAllowed
operator|=
name|permissions
operator|.
name|validate
argument_list|(
name|subject
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
expr_stmt|;
name|writeAllowed
operator|=
name|permissions
operator|.
name|validate
argument_list|(
name|subject
argument_list|,
name|Permission
operator|.
name|WRITE
argument_list|)
expr_stmt|;
name|executeAllowed
operator|=
name|permissions
operator|.
name|validate
argument_list|(
name|subject
argument_list|,
name|Permission
operator|.
name|EXECUTE
argument_list|)
expr_stmt|;
name|creationTime
operator|=
name|collection
operator|.
name|getCreationTime
argument_list|()
expr_stmt|;
name|lastModified
operator|=
name|creationTime
expr_stmt|;
comment|// Collection does not have more information.
name|ownerUser
operator|=
name|permissions
operator|.
name|getOwner
argument_list|()
operator|.
name|getUsername
argument_list|()
expr_stmt|;
name|ownerGroup
operator|=
name|permissions
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|pde
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Clean up collection
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
comment|// Return broker
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
comment|// Set flag
name|isInitialized
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**      * Retrieve full URIs of all Collections in this collection.      */
specifier|public
name|List
argument_list|<
name|XmldbURI
argument_list|>
name|getCollectionURIs
parameter_list|()
block|{
name|List
argument_list|<
name|XmldbURI
argument_list|>
name|collectionURIs
init|=
operator|new
name|ArrayList
argument_list|<
name|XmldbURI
argument_list|>
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Try to read as specified subject
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|xmldbUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
comment|// Get all collections
name|Iterator
argument_list|<
name|XmldbURI
argument_list|>
name|collections
init|=
name|collection
operator|.
name|collectionIteratorNoLock
argument_list|(
name|broker
argument_list|)
decl_stmt|;
comment|// QQ: use collectionIterator ?
while|while
condition|(
name|collections
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|collectionURIs
operator|.
name|add
argument_list|(
name|xmldbUri
operator|.
name|append
argument_list|(
name|collections
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|collectionURIs
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|pde
argument_list|)
expr_stmt|;
name|collectionURIs
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
name|collectionURIs
return|;
block|}
comment|/**      * Retrieve full URIs of all Documents in the collection.      */
specifier|public
name|List
argument_list|<
name|XmldbURI
argument_list|>
name|getDocumentURIs
parameter_list|()
block|{
name|List
argument_list|<
name|XmldbURI
argument_list|>
name|documentURIs
init|=
operator|new
name|ArrayList
argument_list|<
name|XmldbURI
argument_list|>
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Try to read as specified subject
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|xmldbUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
comment|// Get all documents
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|documents
init|=
name|collection
operator|.
name|iteratorNoLock
argument_list|(
name|broker
argument_list|)
decl_stmt|;
comment|// QQ: use 'iterator'
while|while
condition|(
name|documents
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|documentURIs
operator|.
name|add
argument_list|(
name|documents
operator|.
name|next
argument_list|()
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|documentURIs
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|documentURIs
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
comment|// Clean up resources
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
name|documentURIs
return|;
block|}
comment|/*      * Delete document or collection.      */
name|void
name|delete
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleting '"
operator|+
name|xmldbUri
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|subject
argument_list|)
expr_stmt|;
comment|// Open collection if possible, else abort
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|xmldbUri
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
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Remove collection
name|broker
operator|.
name|removeCollection
argument_list|(
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
comment|// Commit change
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Document deleted sucessfully"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TriggerException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// TODO: check if can be done earlier
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Finished delete"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|XmldbURI
name|createCollection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|CollectionExistsException
throws|,
name|EXistException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Create  '"
operator|+
name|name
operator|+
literal|"' in '"
operator|+
name|xmldbUri
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|XmldbURI
name|newCollection
init|=
name|xmldbUri
operator|.
name|append
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|subject
argument_list|)
expr_stmt|;
comment|// Check if collection exists. not likely to happen since availability is
comment|// checked by ResourceFactory
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|newCollection
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Collection already exists"
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CollectionExistsException
argument_list|(
literal|"Collection already exists"
argument_list|)
throw|;
block|}
comment|// Create collection
name|Collection
name|created
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|newCollection
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|created
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Commit change
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Collection created sucessfully"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// TODO: check if can be done earlier
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Finished creation"
argument_list|)
expr_stmt|;
block|}
return|return
name|newCollection
return|;
block|}
specifier|public
name|XmldbURI
name|createFile
parameter_list|(
name|String
name|newName
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|Long
name|length
parameter_list|,
name|String
name|contentType
parameter_list|)
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|CollectionDoesNotExistException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Create '"
operator|+
name|newName
operator|+
literal|"' in '"
operator|+
name|xmldbUri
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|XmldbURI
name|newNameUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|newName
argument_list|)
decl_stmt|;
comment|// Get mime, or NULL when not available
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|newName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|==
literal|null
condition|)
block|{
name|mime
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
comment|// References to the database
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
comment|// create temp file and store. Existdb needs to read twice from a stream.
name|BufferedInputStream
name|bis
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|VirtualTempFile
name|vtf
init|=
operator|new
name|VirtualTempFile
argument_list|()
decl_stmt|;
name|BufferedOutputStream
name|bos
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|vtf
argument_list|)
decl_stmt|;
comment|// Perform actual copy
name|IOUtils
operator|.
name|copy
argument_list|(
name|bis
argument_list|,
name|bos
argument_list|)
expr_stmt|;
name|bis
operator|.
name|close
argument_list|()
expr_stmt|;
name|bos
operator|.
name|close
argument_list|()
expr_stmt|;
name|vtf
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// To support LockNullResource, a 0-byte XML document can received. Since 0-byte
comment|// XML documents are not supported a small file will be created.
if|if
condition|(
name|mime
operator|.
name|isXMLType
argument_list|()
operator|&&
name|vtf
operator|.
name|length
argument_list|()
operator|==
literal|0L
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating dummy XML file for null resource lock '"
operator|+
name|newNameUri
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|vtf
operator|=
operator|new
name|VirtualTempFile
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|write
argument_list|(
literal|"<null_resource/>"
argument_list|,
name|vtf
argument_list|)
expr_stmt|;
name|vtf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Start transaction
name|TransactionManager
name|transact
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|subject
argument_list|)
expr_stmt|;
comment|// Check if collection exists. not likely to happen since availability is checked
comment|// by ResourceFactory
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|xmldbUri
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
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Collection "
operator|+
name|xmldbUri
operator|+
literal|" does not exist"
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CollectionDoesNotExistException
argument_list|(
name|xmldbUri
operator|+
literal|""
argument_list|)
throw|;
block|}
if|if
condition|(
name|mime
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Inserting XML document '"
operator|+
name|mime
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|// Stream into database
name|VirtualTempFileInputSource
name|vtfis
init|=
operator|new
name|VirtualTempFileInputSource
argument_list|(
name|vtf
argument_list|)
decl_stmt|;
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|newNameUri
argument_list|,
name|vtfis
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
init|=
name|info
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|mime
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|vtfis
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Inserting BINARY document '"
operator|+
name|mime
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|// Stream into database
name|InputStream
name|fis
init|=
name|vtf
operator|.
name|getByteStream
argument_list|()
decl_stmt|;
name|bis
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|newNameUri
argument_list|,
name|bis
argument_list|,
name|mime
operator|.
name|getName
argument_list|()
argument_list|,
name|length
operator|.
name|longValue
argument_list|()
argument_list|)
decl_stmt|;
name|bis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Commit change
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Document created sucessfully"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TriggerException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
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
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
name|xmldbUri
operator|+
literal|""
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|vtf
operator|!=
literal|null
condition|)
block|{
name|vtf
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|// TODO: check if can be done earlier
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Finished creation"
argument_list|)
expr_stmt|;
block|}
comment|// Send the result back to the client
name|XmldbURI
name|newResource
init|=
name|xmldbUri
operator|.
name|append
argument_list|(
name|newName
argument_list|)
decl_stmt|;
return|return
name|newResource
return|;
block|}
name|void
name|resourceCopyMove
parameter_list|(
name|XmldbURI
name|destCollectionUri
parameter_list|,
name|String
name|newName
parameter_list|,
name|Mode
name|mode
parameter_list|)
throws|throws
name|EXistException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
name|mode
operator|+
literal|" '"
operator|+
name|xmldbUri
operator|+
literal|"' to '"
operator|+
name|destCollectionUri
operator|+
literal|"' named '"
operator|+
name|newName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|XmldbURI
name|newNameUri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|newNameUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|newName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|srcCollection
init|=
literal|null
decl_stmt|;
name|Collection
name|destCollection
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|txnManager
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|txnManager
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|subject
argument_list|)
expr_stmt|;
comment|// This class contains already the URI of the resource that shall be moved/copied
name|XmldbURI
name|srcCollectionUri
init|=
name|xmldbUri
decl_stmt|;
comment|// Open collection if possible, else abort
name|srcCollection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|srcCollectionUri
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|srcCollection
operator|==
literal|null
condition|)
block|{
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
return|return;
comment|// TODO throw
block|}
comment|// Open collection if possible, else abort
name|destCollection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|destCollectionUri
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destCollection
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Destination collection "
operator|+
name|xmldbUri
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
return|return;
comment|// TODO throw?
block|}
comment|// Perform actial move/copy
if|if
condition|(
name|mode
operator|==
name|Mode
operator|.
name|COPY
condition|)
block|{
name|broker
operator|.
name|copyCollection
argument_list|(
name|txn
argument_list|,
name|srcCollection
argument_list|,
name|destCollection
argument_list|,
name|newNameUri
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|broker
operator|.
name|moveCollection
argument_list|(
name|txn
argument_list|,
name|srcCollection
argument_list|,
name|destCollection
argument_list|,
name|newNameUri
argument_list|)
expr_stmt|;
block|}
comment|// Commit change
name|txnManager
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Collection "
operator|+
name|mode
operator|+
literal|"d sucessfully"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Resource is locked."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TriggerException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|destCollection
operator|!=
literal|null
condition|)
block|{
name|destCollection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|srcCollection
operator|!=
literal|null
condition|)
block|{
name|srcCollection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Finished "
operator|+
name|mode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

