begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:  */
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|UnsupportedEncodingException
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|log4j
operator|.
name|spi
operator|.
name|ErrorCode
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
name|security
operator|.
name|Permission
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
comment|/**  * A remote implementation of the Collection interface. This   * implementation communicates with the server through the XMLRPC  * protocol.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|RemoteCollection
implements|implements
name|CollectionImpl
block|{
comment|// max size of a resource to be send to the server
comment|// if the resource exceeds this limit, the data is split into
comment|// junks and uploaded to the server via the update() call
specifier|private
specifier|static
specifier|final
name|int
name|MAX_CHUNK_LENGTH
init|=
literal|512000
decl_stmt|;
specifier|protected
name|Map
name|childCollections
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|protected
name|int
name|indentXML
init|=
literal|0
decl_stmt|;
specifier|protected
name|String
name|name
decl_stmt|;
specifier|protected
name|Permission
name|permissions
init|=
literal|null
decl_stmt|;
specifier|protected
name|RemoteCollection
name|parent
init|=
literal|null
decl_stmt|;
specifier|protected
name|List
name|resources
init|=
literal|null
decl_stmt|;
specifier|protected
name|XmlRpcClient
name|rpcClient
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|saxDocumentEvents
init|=
literal|true
decl_stmt|;
specifier|public
name|RemoteCollection
parameter_list|(
name|XmlRpcClient
name|client
parameter_list|,
name|String
name|host
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
name|host
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RemoteCollection
parameter_list|(
name|XmlRpcClient
name|client
parameter_list|,
name|RemoteCollection
name|parent
parameter_list|,
name|String
name|host
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|name
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|rpcClient
operator|=
name|client
expr_stmt|;
block|}
specifier|protected
name|void
name|addChildCollection
parameter_list|(
name|Collection
name|child
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
name|childCollections
operator|.
name|put
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addResource
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"sync"
argument_list|,
operator|new
name|Vector
argument_list|()
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
name|UNKNOWN_ERROR
argument_list|,
literal|"failed to close collection"
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
name|UNKNOWN_ERROR
argument_list|,
literal|"failed to close collection"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
name|createId
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|(
operator|(
name|DocumentProxy
operator|)
name|resources
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
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
name|childCollections
operator|.
name|containsKey
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
name|String
name|newId
init|=
name|id
operator|==
literal|null
condition|?
name|createId
argument_list|()
else|:
name|id
decl_stmt|;
name|RemoteXMLResource
name|r
init|=
operator|new
name|RemoteXMLResource
argument_list|(
name|this
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|newId
argument_list|,
literal|null
argument_list|,
name|indentXML
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|r
operator|.
name|setSAXDocEvents
argument_list|(
name|this
operator|.
name|saxDocumentEvents
argument_list|)
expr_stmt|;
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
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>
operator|-
literal|1
condition|)
return|return
operator|(
name|Collection
operator|)
name|childCollections
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
else|else
return|return
operator|(
name|Collection
operator|)
name|childCollections
operator|.
name|get
argument_list|(
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|name
argument_list|)
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
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
return|return
name|childCollections
operator|.
name|size
argument_list|()
return|;
block|}
specifier|protected
name|XmlRpcClient
name|getClient
parameter_list|()
block|{
return|return
name|rpcClient
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
name|name
return|;
block|}
specifier|public
name|Collection
name|getParentCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
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
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
return|return
literal|"/db"
return|;
return|return
name|name
return|;
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
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"pretty"
argument_list|)
condition|)
return|return
operator|(
name|indentXML
operator|==
literal|1
operator|)
condition|?
literal|"true"
else|:
literal|"false"
return|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"encoding"
argument_list|)
condition|)
return|return
name|encoding
return|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"sax-document-events"
argument_list|)
condition|)
return|return
name|saxDocumentEvents
condition|?
literal|"true"
else|:
literal|"false"
return|;
return|return
literal|null
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
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
name|int
name|rlen
init|=
name|resources
operator|.
name|size
argument_list|()
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
name|rlen
condition|;
name|i
operator|++
control|)
block|{
name|DocumentProxy
name|dp
init|=
operator|(
name|DocumentProxy
operator|)
name|resources
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RemoteXMLResource
name|r
decl_stmt|;
if|if
condition|(
name|dp
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|r
operator|=
operator|new
name|RemoteXMLResource
argument_list|(
name|this
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|dp
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
name|indentXML
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
name|r
operator|.
name|setSAXDocEvents
argument_list|(
name|this
operator|.
name|saxDocumentEvents
argument_list|)
expr_stmt|;
name|r
operator|.
name|setPermissions
argument_list|(
name|dp
operator|.
name|getPermissions
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
return|return
literal|null
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
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
return|return
name|resources
operator|.
name|size
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
name|RemoteXPathQueryService
argument_list|(
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
name|CollectionManagementServiceImpl
argument_list|(
name|this
argument_list|,
name|rpcClient
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
name|UserManagementServiceImpl
argument_list|(
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
name|DatabaseInstanceManagerImpl
argument_list|(
name|rpcClient
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
name|RemoteIndexQueryService
argument_list|(
name|rpcClient
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
literal|"XUpdateQueryService"
argument_list|)
condition|)
return|return
operator|new
name|RemoteXUpdateQueryService
argument_list|(
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
name|RemoteXPathQueryService
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|services
index|[
literal|1
index|]
operator|=
operator|new
name|CollectionManagementServiceImpl
argument_list|(
name|this
argument_list|,
name|rpcClient
argument_list|)
expr_stmt|;
name|services
index|[
literal|2
index|]
operator|=
operator|new
name|UserManagementServiceImpl
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|services
index|[
literal|3
index|]
operator|=
operator|new
name|DatabaseInstanceManagerImpl
argument_list|(
name|rpcClient
argument_list|)
expr_stmt|;
name|services
index|[
literal|4
index|]
operator|=
operator|new
name|RemoteIndexQueryService
argument_list|(
name|rpcClient
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
name|RemoteXUpdateQueryService
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|services
return|;
block|}
specifier|protected
name|boolean
name|hasChildCollection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
return|return
name|childCollections
operator|.
name|containsKey
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
comment|/** 	 *  Returns a list of collection names naming all child collections of the 	 *  current collection. Only the name of the collection is returned - not 	 *  the entire path to the collection. 	 * 	 *@return                     Description of the Return Value 	 *@exception  XMLDBException  Description of the Exception 	 */
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
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
name|String
name|coll
index|[]
init|=
operator|new
name|String
index|[
name|childCollections
operator|.
name|size
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
for|for
control|(
name|Iterator
name|i
init|=
name|childCollections
operator|.
name|keySet
argument_list|()
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
name|coll
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
if|if
condition|(
operator|(
name|p
operator|=
name|coll
index|[
name|j
index|]
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
name|coll
index|[
name|j
index|]
operator|=
name|coll
index|[
name|j
index|]
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|coll
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
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
name|int
name|lsize
init|=
name|resources
operator|.
name|size
argument_list|()
decl_stmt|;
name|String
index|[]
name|list
init|=
operator|new
name|String
index|[
name|lsize
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
name|lsize
condition|;
name|i
operator|++
control|)
name|list
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|DocumentProxy
operator|)
name|resources
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getName
argument_list|()
expr_stmt|;
return|return
name|list
return|;
block|}
specifier|private
name|void
name|readCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|resources
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|childCollections
operator|=
operator|new
name|HashMap
argument_list|()
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
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Hashtable
name|collection
decl_stmt|;
try|try
block|{
name|collection
operator|=
operator|(
name|Hashtable
operator|)
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"getCollectionDesc"
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
name|INVALID_COLLECTION
argument_list|,
literal|"an io error occurred"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|Vector
name|documents
init|=
operator|(
name|Vector
operator|)
name|collection
operator|.
name|get
argument_list|(
literal|"documents"
argument_list|)
decl_stmt|;
name|Vector
name|collections
init|=
operator|(
name|Vector
operator|)
name|collection
operator|.
name|get
argument_list|(
literal|"collections"
argument_list|)
decl_stmt|;
name|permissions
operator|=
operator|new
name|Permission
argument_list|(
operator|(
name|String
operator|)
name|collection
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|,
operator|(
name|String
operator|)
name|collection
operator|.
name|get
argument_list|(
literal|"group"
argument_list|)
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|collection
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
name|String
name|docName
decl_stmt|;
name|String
name|childName
decl_stmt|;
name|Hashtable
name|hash
decl_stmt|;
name|DocumentProxy
name|proxy
decl_stmt|;
name|Permission
name|perm
decl_stmt|;
name|int
name|p
decl_stmt|,
name|dsize
init|=
name|documents
operator|.
name|size
argument_list|()
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
name|dsize
condition|;
name|i
operator|++
control|)
block|{
name|hash
operator|=
operator|(
name|Hashtable
operator|)
name|documents
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|docName
operator|=
operator|(
name|String
operator|)
name|hash
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|p
operator|=
name|docName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
name|docName
operator|=
name|docName
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
name|proxy
operator|=
operator|new
name|DocumentProxy
argument_list|(
name|docName
argument_list|)
expr_stmt|;
name|perm
operator|=
operator|new
name|Permission
argument_list|(
operator|(
name|String
operator|)
name|hash
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|,
operator|(
name|String
operator|)
name|hash
operator|.
name|get
argument_list|(
literal|"group"
argument_list|)
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|hash
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
name|proxy
operator|.
name|setPermissions
argument_list|(
name|perm
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|childName
operator|=
operator|(
name|String
operator|)
name|collections
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
try|try
block|{
name|RemoteCollection
name|child
init|=
operator|new
name|RemoteCollection
argument_list|(
name|rpcClient
argument_list|,
name|this
argument_list|,
literal|null
argument_list|,
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|childName
argument_list|)
decl_stmt|;
name|addChildCollection
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
block|}
block|}
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
name|removeChildCollection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|childCollections
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
name|childCollections
operator|.
name|remove
argument_list|(
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|name
argument_list|)
expr_stmt|;
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
name|resources
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
name|int
name|pos
init|=
operator|-
literal|1
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|(
operator|(
name|DocumentProxy
operator|)
name|resources
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|pos
operator|=
name|i
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|pos
operator|<
literal|0
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
name|res
operator|.
name|getId
argument_list|()
operator|+
literal|" not found"
argument_list|)
throw|;
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
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"remove"
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
name|INVALID_RESOURCE
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
name|resources
operator|.
name|remove
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Date
name|getCreationTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Date
operator|)
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"getCreationDate"
argument_list|,
name|params
argument_list|)
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
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"pretty"
argument_list|)
condition|)
name|indentXML
operator|=
operator|(
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"encoding"
argument_list|)
condition|)
name|encoding
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"sax-document-events"
argument_list|)
condition|)
name|saxDocumentEvents
operator|=
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|storeResource
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|resources
operator|==
literal|null
condition|)
name|readCollection
argument_list|()
expr_stmt|;
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|(
operator|(
name|DocumentProxy
operator|)
name|resources
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
name|resources
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|Object
name|content
init|=
name|res
operator|.
name|getContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|content
operator|instanceof
name|File
condition|)
block|{
name|File
name|file
init|=
operator|(
name|File
operator|)
name|content
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|canRead
argument_list|()
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"failed to read resource from file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|file
operator|.
name|length
argument_list|()
operator|<
name|MAX_CHUNK_LENGTH
condition|)
operator|(
operator|(
name|RemoteXMLResource
operator|)
name|res
operator|)
operator|.
name|readContent
argument_list|()
expr_stmt|;
else|else
block|{
name|uploadAndStore
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
operator|new
name|DocumentProxy
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|store
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
operator|new
name|DocumentProxy
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|store
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|data
init|=
operator|(
name|String
operator|)
name|res
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bdata
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bdata
operator|=
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|bdata
operator|=
name|data
operator|.
name|getBytes
argument_list|()
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
name|bdata
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getId
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
literal|1
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"parse"
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
name|INVALID_RESOURCE
argument_list|,
name|xre
operator|==
literal|null
condition|?
literal|"unknown error"
else|:
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
specifier|private
name|void
name|uploadAndStore
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|File
name|file
init|=
operator|(
name|File
operator|)
name|res
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|byte
index|[]
name|chunk
init|=
operator|new
name|byte
index|[
name|MAX_CHUNK_LENGTH
index|]
decl_stmt|;
try|try
block|{
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|int
name|len
decl_stmt|;
name|String
name|fileName
init|=
literal|null
decl_stmt|;
name|Vector
name|params
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|is
operator|.
name|read
argument_list|(
name|chunk
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|params
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
name|params
operator|.
name|addElement
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|len
argument_list|)
argument_list|)
expr_stmt|;
name|fileName
operator|=
operator|(
name|String
operator|)
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"upload"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|params
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|res
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Boolean
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"parseLocal"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
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
literal|"could not read resource from file "
operator|+
name|file
operator|.
name|getAbsolutePath
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
name|INVALID_RESOURCE
argument_list|,
literal|"failed to read resource from file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
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
literal|"networking error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Permission
name|getPermissions
parameter_list|()
block|{
return|return
name|permissions
return|;
block|}
block|}
end_class

end_unit

