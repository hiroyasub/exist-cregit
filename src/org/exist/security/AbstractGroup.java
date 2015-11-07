begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id: AbstractGroup.java 12846 2010-10-01 05:23:29Z shabanovd $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|config
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|ConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Configurator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|ReferenceImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationReferenceBy
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
name|GroupImpl
import|;
end_import

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|""
argument_list|)
specifier|public
specifier|abstract
class|class
name|AbstractGroup
extends|extends
name|AbstractPrincipal
implements|implements
name|Comparable
argument_list|<
name|Object
argument_list|>
implements|,
name|Group
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|AbstractGroup
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"manager"
argument_list|)
annotation|@
name|ConfigurationReferenceBy
argument_list|(
literal|"name"
argument_list|)
specifier|private
name|List
argument_list|<
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
argument_list|>
name|managers
init|=
operator|new
name|ArrayList
argument_list|<
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"metadata"
argument_list|)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|AbstractGroup
parameter_list|(
specifier|final
name|AbstractRealm
name|realm
parameter_list|,
specifier|final
name|int
name|id
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|managers
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|realm
operator|.
name|collectionGroups
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|managers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Account
name|manager
range|:
name|managers
control|)
block|{
name|_addManager
argument_list|(
name|manager
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|AbstractGroup
parameter_list|(
specifier|final
name|AbstractRealm
name|realm
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|realm
operator|.
name|collectionGroups
argument_list|,
name|UNDEFINED_ID
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractGroup
parameter_list|(
specifier|final
name|AbstractRealm
name|realm
parameter_list|,
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
comment|//it require, because class's fields initializing after super constructor
if|if
condition|(
name|this
operator|.
name|configuration
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|GroupImpl
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"wrong type"
argument_list|)
throw|;
block|}
return|return
name|name
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|GroupImpl
operator|)
name|other
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<group name=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\" id=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|Account
name|manager
range|:
name|getManagers
argument_list|()
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"<manager name=\""
operator|+
name|manager
operator|.
name|getUsername
argument_list|()
operator|+
literal|"\"/>"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<manager error=\""
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\"/>"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"</group>"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|assertCanModifyGroup
parameter_list|(
specifier|final
name|Account
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Unspecified Account is not allowed to modify group '"
operator|+
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|else if
condition|(
operator|!
name|user
operator|.
name|hasDbaRole
argument_list|()
operator|&&
operator|!
name|isManager
argument_list|(
name|user
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Account '"
operator|+
name|user
operator|.
name|getName
argument_list|()
operator|+
literal|"' is not allowed to modify group '"
operator|+
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isManager
parameter_list|(
specifier|final
name|Account
name|account
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
name|manager
range|:
name|managers
control|)
block|{
specifier|final
name|Account
name|acc
init|=
name|manager
operator|.
name|resolve
argument_list|()
decl_stmt|;
if|if
condition|(
name|acc
operator|!=
literal|null
operator|&&
name|acc
operator|.
name|equals
argument_list|(
name|account
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|protected
name|void
name|_addManager
parameter_list|(
specifier|final
name|Account
name|account
parameter_list|)
block|{
comment|//check the manager is not already present
for|for
control|(
specifier|final
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
name|manager
range|:
name|managers
control|)
block|{
specifier|final
name|String
name|refName
init|=
name|manager
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|refName
operator|!=
literal|null
operator|&&
name|refName
operator|.
name|equals
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
comment|//add the manager
name|managers
operator|.
name|add
argument_list|(
operator|new
name|ReferenceImpl
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
argument_list|(
name|getRealm
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
argument_list|,
name|account
argument_list|,
name|account
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addManager
parameter_list|(
specifier|final
name|Account
name|manager
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
specifier|final
name|Subject
name|subject
init|=
name|getDatabase
argument_list|()
operator|.
name|getActiveBroker
argument_list|()
operator|.
name|getCurrentSubject
argument_list|()
decl_stmt|;
name|assertCanModifyGroup
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|_addManager
argument_list|(
name|manager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addManagers
parameter_list|(
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|managers
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|managers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Account
name|manager
range|:
name|managers
control|)
block|{
name|addManager
argument_list|(
name|manager
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|addManager
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
specifier|final
name|Subject
name|subject
init|=
name|getDatabase
argument_list|()
operator|.
name|getActiveBroker
argument_list|()
operator|.
name|getCurrentSubject
argument_list|()
decl_stmt|;
name|assertCanModifyGroup
argument_list|(
name|subject
argument_list|)
expr_stmt|;
comment|//check the manager is not already present`
for|for
control|(
specifier|final
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
name|ref
range|:
name|managers
control|)
block|{
specifier|final
name|String
name|refName
init|=
name|ref
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|refName
operator|!=
literal|null
operator|&&
name|refName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|managers
operator|.
name|add
argument_list|(
operator|new
name|ReferenceImpl
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
argument_list|(
name|getRealm
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
argument_list|,
literal|"getAccount"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Account
argument_list|>
name|getManagers
parameter_list|()
block|{
comment|//we use a HashSet to ensure a unique set of managers
comment|//under some cases it is possible for the same manager to
comment|//appear twice in a group config file, but we only want
comment|//to know about them once!
specifier|final
name|Set
argument_list|<
name|Account
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|Account
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|managers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
name|ref
range|:
name|managers
control|)
block|{
specifier|final
name|Account
name|acc
init|=
name|ref
operator|.
name|resolve
argument_list|()
decl_stmt|;
if|if
condition|(
name|acc
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|acc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to resolve reference to group manager '"
operator|+
name|ref
operator|.
name|getName
argument_list|()
operator|+
literal|"' for group '"
operator|+
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|Account
argument_list|>
argument_list|(
name|set
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeManager
parameter_list|(
specifier|final
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
specifier|final
name|Subject
name|subject
init|=
name|getDatabase
argument_list|()
operator|.
name|getActiveBroker
argument_list|()
operator|.
name|getCurrentSubject
argument_list|()
decl_stmt|;
name|assertCanModifyGroup
argument_list|(
name|subject
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
name|ref
range|:
name|managers
control|)
block|{
specifier|final
name|Account
name|acc
init|=
name|ref
operator|.
name|resolve
argument_list|()
decl_stmt|;
if|if
condition|(
name|acc
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|managers
operator|.
name|remove
argument_list|(
name|ref
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|//this method used only at tests, don't use it other places
specifier|public
name|void
name|setManagers
parameter_list|(
specifier|final
name|List
argument_list|<
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
argument_list|>
name|managers
parameter_list|)
block|{
name|this
operator|.
name|managers
operator|=
name|managers
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getMetadataValue
parameter_list|(
specifier|final
name|SchemaType
name|schemaType
parameter_list|)
block|{
return|return
name|metadata
operator|.
name|get
argument_list|(
name|schemaType
operator|.
name|getNamespace
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMetadataValue
parameter_list|(
specifier|final
name|SchemaType
name|schemaType
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
name|metadata
operator|.
name|put
argument_list|(
name|schemaType
operator|.
name|getNamespace
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|SchemaType
argument_list|>
name|getMetadataKeys
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|SchemaType
argument_list|>
name|metadataKeys
init|=
operator|new
name|HashSet
argument_list|<
name|SchemaType
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|key
range|:
name|metadata
operator|.
name|keySet
argument_list|()
control|)
block|{
comment|//XXX: other types?
if|if
condition|(
name|AXSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|metadataKeys
operator|.
name|add
argument_list|(
name|AXSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|EXistSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|metadataKeys
operator|.
name|add
argument_list|(
name|EXistSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|metadataKeys
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearMetadata
parameter_list|()
block|{
name|metadata
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

