begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|Date
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
name|Observable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
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
name|BinaryDocument
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
name|storage
operator|.
name|serializers
operator|.
name|EXistOutputKeys
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
name|InputSource
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
name|Resource
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
comment|/**  *  A local implementation of the Collection interface. This  * is used when the database is running in embedded mode.  *  * Extends Observable to allow status callbacks during indexing.  * Methods storeResource notifies registered observers about the  * progress of the indexer by passing an object of type ProgressIndicator  * to the observer.  *   *@author     wolf  *@created    April 2, 2002  */
end_comment

begin_class
specifier|public
class|class
name|LocalCollection
extends|extends
name|Observable
implements|implements
name|CollectionImpl
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|LocalCollection
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|Properties
name|defaultProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerPool
name|brokerPool
init|=
literal|null
decl_stmt|;
specifier|protected
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
specifier|protected
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|(
name|defaultProperties
argument_list|)
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
init|=
literal|null
decl_stmt|;
specifier|protected
name|ArrayList
name|observers
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|protected
name|boolean
name|needsSync
init|=
literal|false
decl_stmt|;
comment|/** 	 * Create a collection with no parent (root collection). 	 *  	 * @param user 	 * @param brokerPool 	 * @param collection 	 * @throws XMLDBException 	 */
specifier|public
name|LocalCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|brokerPool
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
literal|null
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Create a collection using the supplied internal collection. 	 *  	 * @param user 	 * @param brokerPool 	 * @param parent 	 * @param collection 	 */
specifier|public
name|LocalCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|brokerPool
parameter_list|,
name|LocalCollection
name|parent
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|brokerPool
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
comment|/** 	 * Create a collection identified by its name. Load the collection from the database. 	 *  	 * @param user 	 * @param brokerPool 	 * @param parent 	 * @param name 	 * @throws XMLDBException 	 */
specifier|public
name|LocalCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|brokerPool
parameter_list|,
name|LocalCollection
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|user
operator|==
literal|null
condition|)
name|user
operator|=
operator|new
name|User
argument_list|(
literal|"guest"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
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
name|brokerPool
expr_stmt|;
name|load
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|load
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
if|if
condition|(
name|name
operator|==
literal|null
condition|)
name|name
operator|=
literal|"/db"
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|name
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
name|NO_SUCH_RESOURCE
argument_list|,
literal|"collection not found"
argument_list|)
throw|;
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
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|checkOwner
parameter_list|(
name|User
name|user
parameter_list|)
block|{
return|return
name|user
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|collection
operator|.
name|getPermissions
argument_list|()
operator|.
name|getOwner
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|checkPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
block|{
return|return
name|collection
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|perm
argument_list|)
return|;
block|}
comment|/** 	 * Close the current collection. Calling this method will flush all 	 * open buffers to disk. 	 */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|needsSync
condition|)
block|{
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
name|broker
operator|.
name|sync
argument_list|()
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
name|UNKNOWN_ERROR
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
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|createId
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|String
name|id
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|boolean
name|ok
decl_stmt|;
do|do
block|{
name|ok
operator|=
literal|true
expr_stmt|;
name|id
operator|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|+
literal|".xml"
expr_stmt|;
comment|// check if this id does already exist
if|if
condition|(
name|collection
operator|.
name|hasDocument
argument_list|(
name|id
argument_list|)
condition|)
name|ok
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|collection
operator|.
name|hasSubcollection
argument_list|(
name|id
argument_list|)
condition|)
name|ok
operator|=
literal|false
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|ok
condition|)
do|;
return|return
name|id
return|;
block|}
specifier|public
name|Resource
name|createResource
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
name|id
operator|=
name|createId
argument_list|()
expr_stmt|;
name|Resource
name|r
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"XMLResource"
argument_list|)
condition|)
name|r
operator|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|,
name|id
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|else if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"BinaryResource"
argument_list|)
condition|)
name|r
operator|=
operator|new
name|LocalBinaryResource
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|,
name|id
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"unknown resource type: "
operator|+
name|type
argument_list|)
throw|;
return|return
name|r
return|;
block|}
specifier|public
name|Collection
name|getChildCollection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
name|checkPermissions
argument_list|(
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"you are not allowed to access this collection"
argument_list|)
throw|;
name|String
name|cname
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|collection
operator|.
name|collectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|cname
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|cname
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|cname
operator|=
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|cname
expr_stmt|;
name|Collection
name|temp
init|=
operator|new
name|LocalCollection
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|,
name|cname
argument_list|)
decl_stmt|;
return|return
name|temp
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getChildCollectionCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|collection
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
return|return
name|collection
operator|.
name|getChildCollectionCount
argument_list|()
return|;
else|else
return|return
literal|0
return|;
block|}
specifier|protected
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|getCollection
parameter_list|()
block|{
return|return
name|collection
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
name|collection
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|Collection
name|getParentCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/db"
argument_list|)
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|parent
operator|==
literal|null
operator|&&
name|collection
operator|!=
literal|null
condition|)
block|{
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
name|c
init|=
name|collection
operator|.
name|getParent
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|parent
operator|=
operator|new
name|LocalCollection
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
literal|null
argument_list|,
name|c
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
name|UNKNOWN_ERROR
argument_list|,
literal|"error while retrieving parent collection: "
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
block|}
return|return
name|parent
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|//if (parent == null)
return|return
name|collection
operator|.
name|getName
argument_list|()
return|;
comment|//return (parent.getName().equals("/") ? '/' + collection.getName() :
comment|//       parent.getPath() + '/' + collection.getName());
block|}
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
name|properties
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
return|;
block|}
specifier|public
name|Resource
name|getResource
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
name|collection
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"not allowed to read collection"
argument_list|)
throw|;
name|String
name|name
init|=
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|id
decl_stmt|;
name|DocumentImpl
name|document
init|=
name|collection
operator|.
name|getDocument
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Resource
name|r
decl_stmt|;
if|if
condition|(
name|document
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
name|r
operator|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|,
name|document
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|else if
condition|(
name|document
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
name|r
operator|=
operator|new
name|LocalBinaryResource
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|document
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"unknown resource type"
argument_list|)
throw|;
return|return
name|r
return|;
block|}
specifier|public
name|int
name|getResourceCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
name|collection
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
return|return
literal|0
return|;
else|else
return|return
name|collection
operator|.
name|getDocumentCount
argument_list|()
return|;
block|}
specifier|public
name|Service
name|getService
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|version
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"XPathQueryService"
argument_list|)
condition|)
return|return
operator|new
name|LocalXPathQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
return|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"XQueryService"
argument_list|)
condition|)
return|return
operator|new
name|LocalXPathQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
return|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"CollectionManagementService"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"CollectionManager"
argument_list|)
condition|)
return|return
operator|new
name|LocalCollectionManagementService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
return|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"UserManagementService"
argument_list|)
condition|)
return|return
operator|new
name|LocalUserManagementService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
return|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|)
condition|)
return|return
operator|new
name|LocalDatabaseInstanceManager
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|)
return|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"XUpdateQueryService"
argument_list|)
condition|)
return|return
operator|new
name|LocalXUpdateQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
return|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"IndexQueryService"
argument_list|)
condition|)
return|return
operator|new
name|LocalIndexQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
return|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_SERVICE
argument_list|)
throw|;
block|}
specifier|public
name|Service
index|[]
name|getServices
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Service
index|[]
name|services
init|=
operator|new
name|Service
index|[
literal|6
index|]
decl_stmt|;
name|services
index|[
literal|0
index|]
operator|=
operator|new
name|LocalXPathQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|services
index|[
literal|1
index|]
operator|=
operator|new
name|LocalCollectionManagementService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|services
index|[
literal|2
index|]
operator|=
operator|new
name|LocalUserManagementService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|services
index|[
literal|3
index|]
operator|=
operator|new
name|LocalDatabaseInstanceManager
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|)
expr_stmt|;
name|services
index|[
literal|4
index|]
operator|=
operator|new
name|LocalXUpdateQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|services
index|[
literal|5
index|]
operator|=
operator|new
name|LocalIndexQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
name|services
return|;
comment|// jmv null;
block|}
specifier|protected
name|boolean
name|hasChildCollection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|collection
operator|.
name|hasSubcollection
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isOpen
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
return|return
name|collection
operator|!=
literal|null
return|;
block|}
specifier|public
name|String
index|[]
name|listChildCollections
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
name|checkPermissions
argument_list|(
name|Permission
operator|.
name|READ
argument_list|)
condition|)
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
name|String
index|[]
name|collections
init|=
operator|new
name|String
index|[
name|collection
operator|.
name|getChildCollectionCount
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|collection
operator|.
name|collectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
name|collections
index|[
name|j
index|]
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|collections
return|;
block|}
specifier|public
name|String
index|[]
name|listResources
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
name|collection
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
name|String
index|[]
name|resources
init|=
operator|new
name|String
index|[
name|collection
operator|.
name|getDocumentCount
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
name|int
name|p
decl_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
name|String
name|resource
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|collection
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|resource
operator|=
name|doc
operator|.
name|getFileName
argument_list|()
expr_stmt|;
name|p
operator|=
name|resource
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|resources
index|[
name|j
index|]
operator|=
operator|(
name|p
operator|<
literal|0
condition|?
name|resource
else|:
name|resource
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|resources
return|;
block|}
specifier|public
name|void
name|registerService
parameter_list|(
name|Service
name|serv
parameter_list|)
throws|throws
name|XMLDBException
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NOT_IMPLEMENTED
argument_list|)
throw|;
block|}
specifier|public
name|void
name|removeResource
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
return|return;
name|String
name|name
init|=
name|res
operator|.
name|getId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing "
operator|+
name|name
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|name
decl_stmt|;
name|DocumentImpl
name|doc
init|=
name|collection
operator|.
name|getDocument
argument_list|(
name|path
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
name|INVALID_RESOURCE
argument_list|,
literal|"resource "
operator|+
name|name
operator|+
literal|" not found"
argument_list|)
throw|;
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
if|if
condition|(
name|res
operator|.
name|getResourceType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"XMLResource"
argument_list|)
condition|)
name|collection
operator|.
name|removeDocument
argument_list|(
name|broker
argument_list|,
name|name
argument_list|)
expr_stmt|;
else|else
name|collection
operator|.
name|removeBinaryResource
argument_list|(
name|broker
argument_list|,
name|name
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
name|TriggerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
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
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|needsSync
operator|=
literal|true
expr_stmt|;
name|load
argument_list|(
name|getPath
argument_list|()
argument_list|)
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
throws|throws
name|XMLDBException
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|storeResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|resource
operator|.
name|getResourceType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"XMLResource"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing document "
operator|+
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|storeXMLResource
argument_list|(
operator|(
name|LocalXMLResource
operator|)
name|resource
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|resource
operator|.
name|getResourceType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"BinaryResource"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing binary resource "
operator|+
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|storeBinaryResource
argument_list|(
operator|(
name|LocalBinaryResource
operator|)
name|resource
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_RESOURCE_TYPE
argument_list|,
literal|"unknown resource type: "
operator|+
name|resource
operator|.
name|getResourceType
argument_list|()
argument_list|)
throw|;
name|needsSync
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
name|void
name|storeBinaryResource
parameter_list|(
name|LocalBinaryResource
name|res
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|BinaryDocument
name|blob
init|=
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|broker
argument_list|,
name|res
operator|.
name|getId
argument_list|()
argument_list|,
operator|(
name|byte
index|[]
operator|)
name|res
operator|.
name|getContent
argument_list|()
argument_list|)
decl_stmt|;
name|res
operator|.
name|blob
operator|=
name|blob
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"Exception while storing binary resource: "
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
block|}
specifier|private
name|void
name|storeXMLResource
parameter_list|(
name|LocalXMLResource
name|res
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|String
name|name
init|=
name|res
operator|.
name|getDocumentId
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
name|user
argument_list|)
expr_stmt|;
comment|//broker.flush();
name|Observer
name|observer
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|observers
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
block|{
name|observer
operator|=
operator|(
name|Observer
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|collection
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
expr_stmt|;
block|}
name|DocumentImpl
name|newDoc
decl_stmt|;
if|if
condition|(
name|res
operator|.
name|file
operator|!=
literal|null
condition|)
block|{
name|String
name|uri
init|=
name|res
operator|.
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
decl_stmt|;
name|newDoc
operator|=
name|collection
operator|.
name|addDocument
argument_list|(
name|broker
argument_list|,
name|name
argument_list|,
operator|new
name|InputSource
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|res
operator|.
name|root
operator|!=
literal|null
condition|)
name|newDoc
operator|=
name|collection
operator|.
name|addDocument
argument_list|(
name|broker
argument_list|,
name|name
argument_list|,
name|res
operator|.
name|root
argument_list|)
expr_stmt|;
else|else
name|newDoc
operator|=
name|collection
operator|.
name|addDocument
argument_list|(
name|broker
argument_list|,
name|name
argument_list|,
name|res
operator|.
name|content
argument_list|)
expr_stmt|;
name|res
operator|.
name|document
operator|=
name|newDoc
expr_stmt|;
comment|//broker.flush();
block|}
catch|catch
parameter_list|(
name|Exception
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
name|collection
operator|.
name|deleteObservers
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
name|Date
name|getCreationTime
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|collection
operator|.
name|getCreationTime
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Add a new observer to the list. Observers are just passed 	 * on to the indexer to be notified about the indexing progress. 	 */
specifier|public
name|void
name|addObserver
parameter_list|(
name|Observer
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
name|observers
operator|.
name|contains
argument_list|(
name|o
argument_list|)
condition|)
name|observers
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

