begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|Subject
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
name|util
operator|.
name|function
operator|.
name|FunctionE
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
name|function
operator|.
name|LocalXmldbCollectionFunction
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
name|function
operator|.
name|LocalXmldbFunction
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
name|Optional
import|;
end_import

begin_comment
comment|/**  * Base class for Local XMLDB classes  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractLocal
block|{
specifier|protected
specifier|final
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|protected
specifier|final
name|Subject
name|user
decl_stmt|;
specifier|protected
name|LocalCollection
name|collection
decl_stmt|;
name|AbstractLocal
parameter_list|(
specifier|final
name|Subject
name|user
parameter_list|,
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|,
specifier|final
name|LocalCollection
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
name|collection
operator|=
name|collection
expr_stmt|;
block|}
specifier|protected
name|XmldbURI
name|resolve
parameter_list|(
specifier|final
name|XmldbURI
name|name
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
return|return
name|collection
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|name
return|;
block|}
block|}
comment|/**      * Higher-order-function for performing read-only operations against a database collection      *      * @param collectionUri The uri of the collection to perform read-only operations on      * @return A function to receive a read-only operation to perform against the collection      */
specifier|protected
parameter_list|<
name|R
parameter_list|>
name|FunctionE
argument_list|<
name|LocalXmldbCollectionFunction
argument_list|<
name|R
argument_list|>
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
name|read
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|readOp
lambda|->
name|withDb
argument_list|(
parameter_list|(
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
name|this
operator|.
block_content|<R>read(broker
operator|,
name|transaction
operator|,
name|collectionUri
block_content|)
block|.apply(readOp
block|)
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_comment
unit|}
comment|/**      * Higher-order-function for performing read-only operations against a database collection      *      * @param collectionUri The uri of the collection to perform read-only operations on      * @param errorCode The error code to use in the XMLDBException if the collection does not exist, see {@link ErrorCodes}      * @return A function to receive a read-only operation to perform against the collection      *      * @throws XMLDBException if the collection could not be read      */
end_comment

begin_function
unit|protected
parameter_list|<
name|R
parameter_list|>
name|FunctionE
argument_list|<
name|LocalXmldbCollectionFunction
argument_list|<
name|R
argument_list|>
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
name|read
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|,
specifier|final
name|int
name|errorCode
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|readOp
lambda|->
name|withDb
argument_list|(
parameter_list|(
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
name|this
operator|.
block_content|<R>read(broker
operator|,
name|transaction
operator|,
name|collectionUri
operator|,
name|errorCode
block_content|)
block|.apply(readOp
end_function

begin_empty_stmt
unit|))
empty_stmt|;
end_empty_stmt

begin_comment
unit|}
comment|/**      * Higher-order-function for performing read-only operations against a database collection      *      * @param broker The database broker to use when accessing the collection      * @param transaction The transaction to use when accessing the collection      * @param collectionUri The uri of the collection to perform read-only operations on      * @return A function to receive a read-only operation to perform against the collection      */
end_comment

begin_function
unit|protected
parameter_list|<
name|R
parameter_list|>
name|FunctionE
argument_list|<
name|LocalXmldbCollectionFunction
argument_list|<
name|R
argument_list|>
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
name|read
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|this
operator|.
expr|<
name|R
operator|>
name|with
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|,
name|broker
argument_list|,
name|transaction
argument_list|,
name|collectionUri
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * Higher-order-function for performing read-only operations against a database collection      *      * @param broker The database broker to use when accessing the collection      * @param transaction The transaction to use when accessing the collection      * @param collectionUri The uri of the collection to perform read-only operations on      * @param errorCode The error code to use in the XMLDBException if the collection does not exist, see {@link ErrorCodes}      * @return A function to receive a read-only operation to perform against the collection      *      * @throws XMLDBException if the collection could not be read      */
end_comment

begin_function
specifier|protected
parameter_list|<
name|R
parameter_list|>
name|FunctionE
argument_list|<
name|LocalXmldbCollectionFunction
argument_list|<
name|R
argument_list|>
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
name|read
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|,
specifier|final
name|int
name|errorCode
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|this
operator|.
expr|<
name|R
operator|>
name|with
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|,
name|broker
argument_list|,
name|transaction
argument_list|,
name|collectionUri
argument_list|,
name|errorCode
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * Higher-order-function for performing read/write operations against a database collection      *      * @param collectionUri The uri of the collection to perform read/write operations on      * @return A function to receive a read/write operation to perform against the collection      */
end_comment

begin_function
specifier|protected
parameter_list|<
name|R
parameter_list|>
name|FunctionE
argument_list|<
name|LocalXmldbCollectionFunction
argument_list|<
name|R
argument_list|>
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
name|modify
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|modifyOp
lambda|->
name|withDb
argument_list|(
parameter_list|(
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
name|this
operator|.
block_content|<R>modify(broker
operator|,
name|transaction
operator|,
name|collectionUri
block_content|)
block|.apply(modifyOp
end_function

begin_empty_stmt
unit|))
empty_stmt|;
end_empty_stmt

begin_comment
unit|}
comment|/**      * Higher-order-function for performing read/write operations against a database collection      *      * @param broker The database broker to use when accessing the collection      * @param transaction The transaction to use when accessing the collection      * @param collectionUri The uri of the collection to perform read/write operations on      * @return A function to receive a read/write operation to perform against the collection      */
end_comment

begin_function
unit|protected
parameter_list|<
name|R
parameter_list|>
name|FunctionE
argument_list|<
name|LocalXmldbCollectionFunction
argument_list|<
name|R
argument_list|>
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
name|modify
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|this
operator|.
expr|<
name|R
operator|>
name|with
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|,
name|broker
argument_list|,
name|transaction
argument_list|,
name|collectionUri
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * Higher-order function for performing lockable operations on a collection      *      * @param lockMode      * @param broker The broker to use for the operation      * @param transaction The transaction to use for the operation      * @return A function to receive an operation to perform on the locked database collection      *      * @throws XMLDBException if the collection does not exist or the caller does not have permission to open      * the collection. The error code of the XMLDBException will be either {@link ErrorCodes#INVALID_COLLECTION}      * if the collection does not exist, or {@link ErrorCodes#PERMISSION_DENIED} if the caller does not have      * permission to open the collection.      */
end_comment

begin_function
specifier|protected
parameter_list|<
name|R
parameter_list|>
name|FunctionE
argument_list|<
name|LocalXmldbCollectionFunction
argument_list|<
name|R
argument_list|>
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
name|with
parameter_list|(
specifier|final
name|int
name|lockMode
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|with
argument_list|(
name|lockMode
argument_list|,
name|broker
argument_list|,
name|transaction
argument_list|,
name|collectionUri
argument_list|,
name|ErrorCodes
operator|.
name|INVALID_COLLECTION
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * Higher-order function for performing lockable operations on a collection      *      * @param lockMode      * @param broker The broker to use for the operation      * @param transaction The transaction to use for the operation      * @param errorCode The error code to use in the XMLDBException if the collection does not exist, see {@link ErrorCodes}      * @return A function to receive an operation to perform on the locked database collection      *      * @throws XMLDBException if the collection does not exist or the caller does not have permission to open      * the collection. The error code of the XMLDBException will be either taken from the `errorCode` param      * or set to {@link ErrorCodes#PERMISSION_DENIED}      */
end_comment

begin_function
specifier|protected
parameter_list|<
name|R
parameter_list|>
name|FunctionE
argument_list|<
name|LocalXmldbCollectionFunction
argument_list|<
name|R
argument_list|>
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
name|with
parameter_list|(
specifier|final
name|int
name|lockMode
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|,
specifier|final
name|int
name|errorCode
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|collectionOp
lambda|->
block|{
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|coll
init|=
literal|null
decl_stmt|;
try|try
block|{
name|coll
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collectionUri
argument_list|,
name|lockMode
argument_list|)
expr_stmt|;
if|if
condition|(
name|coll
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|errorCode
argument_list|,
literal|"Collection "
operator|+
name|collectionUri
operator|.
name|toString
argument_list|()
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
specifier|final
name|R
name|result
init|=
name|collectionOp
operator|.
name|apply
argument_list|(
name|coll
argument_list|,
name|broker
argument_list|,
name|transaction
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
specifier|final
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
name|coll
operator|!=
literal|null
condition|)
block|{
name|coll
operator|.
name|release
argument_list|(
name|lockMode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
end_function

begin_comment
comment|/**      * Higher-order-function for performing an XMLDB operation on      * the database      *      * @param dbOperation The operation to perform on the database      * @param<R>         The return type of the operation      * @throws org.xmldb.api.base.XMLDBException      */
end_comment

begin_function
parameter_list|<
name|R
parameter_list|>
name|R
name|withDb
parameter_list|(
specifier|final
name|LocalXmldbFunction
argument_list|<
name|R
argument_list|>
name|dbOperation
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|ofNullable
argument_list|(
name|user
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|R
name|result
init|=
name|dbOperation
operator|.
name|apply
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
specifier|final
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
block|}
end_function

unit|}
end_unit
