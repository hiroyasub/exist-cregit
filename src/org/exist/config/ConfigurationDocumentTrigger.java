begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2012 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|config
package|;
end_package

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
name|triggers
operator|.
name|FilteringTrigger
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
name|dom
operator|.
name|ElementAtExist
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
name|ACLPermission
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
name|PermissionFactory
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
name|SecurityManager
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
name|internal
operator|.
name|RealmImpl
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
name|utils
operator|.
name|ConverterFrom1_0
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
name|Attributes
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ConfigurationDocumentTrigger
extends|extends
name|FilteringTrigger
block|{
specifier|protected
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|existingDocument
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
name|Configuration
name|conf
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|REMOVE_DOCUMENT_EVENT
case|:
name|conf
operator|=
name|Configurator
operator|.
name|getConfigurtion
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|documentPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|Configurator
operator|.
name|unregister
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//XXX: inform object that configuration was deleted
block|}
break|break;
default|default:
name|conf
operator|=
name|Configurator
operator|.
name|getConfigurtion
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|documentPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|conf
operator|.
name|checkForUpdates
argument_list|(
operator|(
name|ElementAtExist
operator|)
name|document
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|documentPath
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|ConverterFrom1_0
operator|.
name|LEGACY_USERS_DOCUMENT_PATH
argument_list|)
condition|)
block|{
comment|//            	Subject currectSubject = broker.getSubject();
try|try
block|{
name|SecurityManager
name|sm
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
comment|//                	broker.setSubject(sm.getSystemSubject());
name|ConverterFrom1_0
operator|.
name|convert
argument_list|(
name|sm
argument_list|,
name|document
argument_list|)
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
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
argument_list|)
expr_stmt|;
comment|//TODO : raise exception ? -pb
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ee
argument_list|)
expr_stmt|;
comment|//TODO : raise exception ? -pb
comment|//                } finally {
comment|//                	broker.setSubject(currectSubject);
block|}
block|}
break|break;
block|}
block|}
specifier|private
name|void
name|checkForUpdates
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|uri
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
name|Configuration
name|conf
init|=
name|Configurator
operator|.
name|getConfigurtion
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|conf
operator|.
name|checkForUpdates
argument_list|(
operator|(
name|ElementAtExist
operator|)
name|document
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//TODO : use XmldbURI methos ! not String.equals()
if|if
condition|(
name|uri
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|ConverterFrom1_0
operator|.
name|LEGACY_USERS_DOCUMENT_PATH
argument_list|)
condition|)
block|{
comment|//        	Subject currectSubject = broker.getSubject();
try|try
block|{
name|SecurityManager
name|sm
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
comment|//            	broker.setSubject(sm.getSystemSubject());
name|ConverterFrom1_0
operator|.
name|convert
argument_list|(
name|sm
argument_list|,
name|document
argument_list|)
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
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
argument_list|)
expr_stmt|;
comment|//TODO : raise exception ? -pb
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ee
argument_list|)
expr_stmt|;
comment|//TODO : raise exception ? -pb
comment|//            } finally {
comment|//            	broker.setSubject(currectSubject);
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCreateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCreateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//check saving list
if|if
condition|(
name|Configurator
operator|.
name|saving
operator|.
name|contains
argument_list|(
name|Configurator
operator|.
name|getFullURI
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
argument_list|)
condition|)
return|return;
name|checkForUpdates
argument_list|(
name|broker
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|XmldbURI
name|uri
init|=
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
name|SecurityManager
operator|.
name|SECURITY_COLLECTION_URI
argument_list|)
condition|)
block|{
try|try
block|{
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|processPramatter
argument_list|(
name|broker
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Configuration can't be proccessed ["
operator|+
name|document
operator|.
name|getURI
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : raise exception ? -pb
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//check saving list
if|if
condition|(
name|Configurator
operator|.
name|saving
operator|.
name|contains
argument_list|(
name|Configurator
operator|.
name|getFullURI
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
argument_list|)
condition|)
return|return;
name|XmldbURI
name|uri
init|=
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
name|SecurityManager
operator|.
name|SECURITY_COLLECTION_URI
argument_list|)
condition|)
block|{
try|try
block|{
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|processPramatterBeforeSave
argument_list|(
name|broker
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Configuration can't be proccessed ["
operator|+
name|document
operator|.
name|getURI
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : raise exception ? -pb
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//check saving list
if|if
condition|(
name|Configurator
operator|.
name|saving
operator|.
name|contains
argument_list|(
name|Configurator
operator|.
name|getFullURI
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
argument_list|)
condition|)
return|return;
name|checkForUpdates
argument_list|(
name|broker
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|XmldbURI
name|uri
init|=
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
name|SecurityManager
operator|.
name|SECURITY_COLLECTION_URI
argument_list|)
condition|)
block|{
try|try
block|{
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|processPramatter
argument_list|(
name|broker
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Configuration can't be proccessed ["
operator|+
name|document
operator|.
name|getURI
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : raise exception ? -pb
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCopyDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCopyDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|oldUri
parameter_list|)
throws|throws
name|TriggerException
block|{
name|checkForUpdates
argument_list|(
name|broker
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeMoveDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterMoveDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|oldUri
parameter_list|)
throws|throws
name|TriggerException
block|{
name|checkForUpdates
argument_list|(
name|broker
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeDeleteDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterDeleteDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
name|Configuration
name|conf
init|=
name|Configurator
operator|.
name|getConfigurtion
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|Configurator
operator|.
name|unregister
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//XXX: inform object that configuration was deleted
block|}
block|}
comment|/**      * Mappings from User ids that were used in UnixStylePermission version of eXist-db to ACLPermission version of eXist-db      */
specifier|final
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|userIdMappings
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|userIdMappings
operator|.
name|put
argument_list|(
operator|-
literal|1
argument_list|,
name|RealmImpl
operator|.
name|UNKNOWN_ACCOUNT_ID
argument_list|)
expr_stmt|;
name|userIdMappings
operator|.
name|put
argument_list|(
literal|0
argument_list|,
name|RealmImpl
operator|.
name|SYSTEM_ACCOUNT_ID
argument_list|)
expr_stmt|;
name|userIdMappings
operator|.
name|put
argument_list|(
literal|1
argument_list|,
name|RealmImpl
operator|.
name|ADMIN_ACCOUNT_ID
argument_list|)
expr_stmt|;
name|userIdMappings
operator|.
name|put
argument_list|(
literal|2
argument_list|,
name|RealmImpl
operator|.
name|GUEST_ACCOUNT_ID
argument_list|)
expr_stmt|;
block|}
comment|/**      * Mappings from group ids that were used in UnixStylePermission version of eXist-db to ACLPermission version of eXist-db      */
specifier|final
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|groupIdMappings
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|groupIdMappings
operator|.
name|put
argument_list|(
operator|-
literal|1
argument_list|,
name|RealmImpl
operator|.
name|UNKNOWN_GROUP_ID
argument_list|)
expr_stmt|;
name|groupIdMappings
operator|.
name|put
argument_list|(
literal|1
argument_list|,
name|RealmImpl
operator|.
name|DBA_GROUP_ID
argument_list|)
expr_stmt|;
name|groupIdMappings
operator|.
name|put
argument_list|(
literal|2
argument_list|,
name|RealmImpl
operator|.
name|GUEST_GROUP_ID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qname
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|boolean
name|aclPermissionInUse
init|=
name|PermissionFactory
operator|.
name|getDefaultResourcePermission
argument_list|()
operator|instanceof
name|ACLPermission
decl_stmt|;
comment|//map unix style user and group ids to acl style
if|if
condition|(
name|aclPermissionInUse
operator|&&
name|namespaceURI
operator|!=
literal|null
operator|&&
name|namespaceURI
operator|.
name|equals
argument_list|(
name|Configuration
operator|.
name|NS
argument_list|)
operator|&&
name|localName
operator|.
name|equals
argument_list|(
literal|"account"
argument_list|)
condition|)
block|{
name|Attributes
name|newAttrs
init|=
name|modifyUserGroupIdAttribute
argument_list|(
name|attributes
argument_list|,
name|userIdMappings
argument_list|)
decl_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qname
argument_list|,
name|newAttrs
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|aclPermissionInUse
operator|&&
name|namespaceURI
operator|!=
literal|null
operator|&&
name|namespaceURI
operator|.
name|equals
argument_list|(
name|Configuration
operator|.
name|NS
argument_list|)
operator|&&
name|localName
operator|.
name|equals
argument_list|(
literal|"group"
argument_list|)
condition|)
block|{
name|Attributes
name|newAttrs
init|=
name|modifyUserGroupIdAttribute
argument_list|(
name|attributes
argument_list|,
name|groupIdMappings
argument_list|)
decl_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qname
argument_list|,
name|newAttrs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qname
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Attributes
name|modifyUserGroupIdAttribute
parameter_list|(
specifier|final
name|Attributes
name|attrs
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|idMappings
parameter_list|)
block|{
name|String
name|strId
init|=
name|attrs
operator|.
name|getValue
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|strId
operator|!=
literal|null
condition|)
block|{
name|Integer
name|id
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|strId
argument_list|)
decl_stmt|;
name|Integer
name|newId
init|=
name|idMappings
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|newId
operator|==
literal|null
condition|)
block|{
name|newId
operator|=
name|id
expr_stmt|;
block|}
name|AttributesImpl
name|newAttrs
init|=
operator|new
name|AttributesImpl
argument_list|(
name|attrs
argument_list|)
decl_stmt|;
name|int
name|idIndex
init|=
name|newAttrs
operator|.
name|getIndex
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|newAttrs
operator|.
name|setAttribute
argument_list|(
name|idIndex
argument_list|,
name|newAttrs
operator|.
name|getURI
argument_list|(
name|idIndex
argument_list|)
argument_list|,
literal|"id"
argument_list|,
name|newAttrs
operator|.
name|getQName
argument_list|(
name|idIndex
argument_list|)
argument_list|,
name|newAttrs
operator|.
name|getType
argument_list|(
name|idIndex
argument_list|)
argument_list|,
name|newId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newAttrs
return|;
block|}
return|return
name|attrs
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocumentMetadata
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocumentMetadata
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|parent
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

