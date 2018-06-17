begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|txn
operator|.
name|bridge
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
name|xmldb
operator|.
name|*
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
name|Optional
import|;
end_import

begin_comment
comment|/**  * Avoids overlapping transactions on Collections  * when the XML:DB Local API executes XQuery that then  * calls the XMLDB XQuery Module which then tries  * to use the XML:DB Local API  *  * @author Adam Retter  */
end_comment

begin_class
specifier|public
class|class
name|InTxnLocalCollection
extends|extends
name|LocalCollection
block|{
specifier|public
name|InTxnLocalCollection
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
name|parent
parameter_list|,
specifier|final
name|XmldbURI
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|super
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|parent
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
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
return|return
name|withDb
argument_list|(
name|brokerPool
argument_list|,
name|user
argument_list|,
name|dbOperation
argument_list|)
return|;
block|}
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withDb
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|,
specifier|final
name|Subject
name|user
parameter_list|,
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
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|broker
operator|.
name|continueOrBeginTransaction
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
annotation|@
name|Override
specifier|public
name|Service
name|getService
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|version
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|Service
name|service
decl_stmt|;
switch|switch
condition|(
name|name
condition|)
block|{
case|case
literal|"XPathQueryService"
case|:
case|case
literal|"XQueryService"
case|:
name|service
operator|=
operator|new
name|InTxnLocalXPathQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"CollectionManagementService"
case|:
case|case
literal|"CollectionManager"
case|:
name|service
operator|=
operator|new
name|InTxnLocalCollectionManagementService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"UserManagementService"
case|:
name|service
operator|=
operator|new
name|InTxnLocalUserManagementService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"DatabaseInstanceManager"
case|:
name|service
operator|=
operator|new
name|LocalDatabaseInstanceManager
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"XUpdateQueryService"
case|:
name|service
operator|=
operator|new
name|InTxnLocalXUpdateQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"IndexQueryService"
case|:
name|service
operator|=
operator|new
name|InTxnLocalIndexQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
expr_stmt|;
break|break;
default|default:
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
return|return
name|service
return|;
block|}
annotation|@
name|Override
specifier|public
name|Service
index|[]
name|getServices
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Service
index|[]
name|services
init|=
block|{
operator|new
name|InTxnLocalXPathQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|InTxnLocalCollectionManagementService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|InTxnLocalUserManagementService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|LocalDatabaseInstanceManager
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|)
block|,
operator|new
name|InTxnLocalXUpdateQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|InTxnLocalIndexQueryService
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
return|return
name|services
return|;
block|}
annotation|@
name|Override
specifier|public
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
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
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
specifier|final
name|XmldbURI
name|parentUri
init|=
name|this
operator|.
expr|<
name|XmldbURI
operator|>
name|read
argument_list|()
operator|.
name|apply
argument_list|(
parameter_list|(
name|collection
parameter_list|,
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
name|collection
operator|.
name|getParentURI
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|collection
operator|=
operator|new
name|InTxnLocalCollection
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
literal|null
argument_list|,
name|parentUri
argument_list|)
expr_stmt|;
block|}
return|return
name|collection
return|;
block|}
annotation|@
name|Override
specifier|public
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|getChildCollection
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XmldbURI
name|childURI
decl_stmt|;
try|try
block|{
name|childURI
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|XmldbURI
name|nameUri
init|=
name|this
operator|.
expr|<
name|XmldbURI
operator|>
name|read
argument_list|()
operator|.
name|apply
argument_list|(
parameter_list|(
name|collection
parameter_list|,
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
block|{
name|XmldbURI
name|childName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|collection
operator|.
name|hasChildCollection
argument_list|(
name|broker
argument_list|,
name|childURI
argument_list|)
condition|)
block|{
name|childName
operator|=
name|getPathURI
argument_list|()
operator|.
name|append
argument_list|(
name|childURI
argument_list|)
expr_stmt|;
block|}
return|return
name|childName
return|;
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|nameUri
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|InTxnLocalCollection
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|this
argument_list|,
name|nameUri
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit
