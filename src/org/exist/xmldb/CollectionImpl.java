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
name|xmlrpc
operator|.
name|*
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
comment|/**  * A remote implementation of the Collection interface. This   * implementation communicates with the server through the XMLRPC  * protocol.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CollectionImpl
implements|implements
name|Collection
block|{
specifier|protected
name|HashMap
name|childCollections
init|=
operator|new
name|HashMap
argument_list|()
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
literal|1
decl_stmt|;
specifier|protected
name|String
name|name
decl_stmt|;
specifier|protected
name|CollectionImpl
name|parent
init|=
literal|null
decl_stmt|;
specifier|protected
name|ArrayList
name|resources
init|=
operator|new
name|ArrayList
argument_list|()
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
name|CollectionImpl
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
name|CollectionImpl
parameter_list|(
name|XmlRpcClient
name|client
parameter_list|,
name|CollectionImpl
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
name|readCollection
argument_list|()
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
block|{
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
name|String
operator|)
name|resources
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
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
name|XMLResourceImpl
name|r
init|=
operator|new
name|XMLResourceImpl
argument_list|(
name|this
argument_list|,
name|id
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
name|String
operator|)
name|resources
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|XMLResourceImpl
name|r
init|=
operator|new
name|XMLResourceImpl
argument_list|(
name|this
argument_list|,
operator|(
name|String
operator|)
name|resources
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
literal|4
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
block|{
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
comment|/**      *  Returns a list of collection names naming all child collections of the      *  current collection. Only the name of the collection is returned - not      *  the entire path to the collection.      *      *@return                     Description of the Return Value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|String
index|[]
name|listChildCollections
parameter_list|()
throws|throws
name|XMLDBException
block|{
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
return|return
operator|(
name|String
index|[]
operator|)
name|resources
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|resources
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|private
name|void
name|readCollection
parameter_list|()
throws|throws
name|XMLDBException
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
name|String
name|docName
decl_stmt|;
name|String
name|childName
decl_stmt|;
name|int
name|p
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
name|documents
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|docName
operator|=
operator|(
name|String
operator|)
name|documents
operator|.
name|elementAt
argument_list|(
name|i
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
name|addResource
argument_list|(
name|docName
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
name|CollectionImpl
name|child
init|=
operator|new
name|CollectionImpl
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
operator|!
name|resources
operator|.
name|contains
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|)
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
name|res
operator|.
name|getId
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
name|resources
operator|.
name|remove
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
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
name|addResource
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

