begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
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
name|collections
operator|.
name|triggers
operator|.
name|CollectionTrigger
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
name|CollectionTriggerProxy
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
name|DocumentTrigger
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
name|DocumentTriggerProxy
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
name|Trigger
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
name|collections
operator|.
name|triggers
operator|.
name|TriggerProxy
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
name|security
operator|.
name|Account
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
name|IndexSpec
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
name|DatabaseConfigurationException
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
name|ParametersExtractor
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
name|XMLReaderObjectFactory
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
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|Node
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
name|NodeList
import|;
end_import

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"collection"
argument_list|)
specifier|public
class|class
name|CollectionConfiguration
block|{
specifier|public
specifier|final
specifier|static
name|String
name|COLLECTION_CONFIG_SUFFIX
init|=
literal|".xconf"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|COLLECTION_CONFIG_SUFFIX_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|COLLECTION_CONFIG_SUFFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_COLLECTION_CONFIG_FILE
init|=
literal|"collection"
operator|+
name|COLLECTION_CONFIG_SUFFIX
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|DEFAULT_COLLECTION_CONFIG_FILE_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|DEFAULT_COLLECTION_CONFIG_FILE
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE
init|=
literal|"http://exist-db.org/collection-config/1.0"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ROOT_ELEMENT
init|=
literal|"collection"
decl_stmt|;
comment|/**      * First level element in a collection configuration document      */
specifier|private
specifier|static
specifier|final
name|String
name|TRIGGERS_ELEMENT
init|=
literal|"triggers"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRIGGER_ELEMENT
init|=
literal|"trigger"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CLASS_ATTRIBUTE
init|=
literal|"class"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PARAMETER_ELEMENT
init|=
literal|"parameter"
decl_stmt|;
comment|/**      * First level element in a collection configuration document      */
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_ELEMENT
init|=
literal|"index"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PERMISSIONS_ELEMENT
init|=
literal|"default-permissions"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GROUP_ELEMENT
init|=
literal|"default-group"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RESOURCE_ATTR
init|=
literal|"resource"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_ATTR
init|=
literal|"collection"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VALIDATION_ELEMENT
init|=
literal|"validation"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VALIDATION_MODE_ATTR
init|=
literal|"mode"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|CollectionConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
argument_list|>
name|colTriggers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|DocumentTrigger
argument_list|>
argument_list|>
name|docTriggers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|IndexSpec
name|indexSpec
init|=
literal|null
decl_stmt|;
specifier|private
name|XmldbURI
name|docName
init|=
literal|null
decl_stmt|;
specifier|private
name|XmldbURI
name|srcCollectionURI
decl_stmt|;
specifier|private
name|int
name|defCollPermissions
init|=
name|Permission
operator|.
name|DEFAULT_COLLECTION_PERM
decl_stmt|;
specifier|private
name|int
name|defResPermissions
init|=
name|Permission
operator|.
name|DEFAULT_RESOURCE_PERM
decl_stmt|;
specifier|private
name|String
name|defCollGroup
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|defResGroup
init|=
literal|null
decl_stmt|;
specifier|private
name|XMLReaderObjectFactory
operator|.
name|VALIDATION_SETTING
name|validationMode
init|=
name|XMLReaderObjectFactory
operator|.
name|VALIDATION_SETTING
operator|.
name|UNKNOWN
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|CollectionConfiguration
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
comment|/**      * @param broker the database broker      * @param doc collection configuration document      * @param checkOnly true to only check      * @param srcCollectionURI The collection from which the document is being read.  This      *                         is not necessarily the same as this.collection.getURI() because the      *                         source document may have come from a parent collection.      * @param docName The name of the document being read       * @throws CollectionConfigurationException if an error occurs whilst reading the collection configuration      */
specifier|protected
name|void
name|read
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Document
name|doc
parameter_list|,
specifier|final
name|boolean
name|checkOnly
parameter_list|,
specifier|final
name|XmldbURI
name|srcCollectionURI
parameter_list|,
specifier|final
name|XmldbURI
name|docName
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
if|if
condition|(
operator|!
name|checkOnly
condition|)
block|{
name|this
operator|.
name|docName
operator|=
name|docName
expr_stmt|;
name|this
operator|.
name|srcCollectionURI
operator|=
name|srcCollectionURI
expr_stmt|;
block|}
specifier|final
name|Element
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"Configuration document can not be parsed"
argument_list|,
name|checkOnly
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|ROOT_ELEMENT
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"Expected element '"
operator|+
name|ROOT_ELEMENT
operator|+
literal|"' in configuration document. Got element '"
operator|+
name|root
operator|.
name|getLocalName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|checkOnly
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|root
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"Expected namespace '"
operator|+
name|NAMESPACE
operator|+
literal|"' for element '"
operator|+
name|PARAMETER_ELEMENT
operator|+
literal|"' in configuration document. Got '"
operator|+
name|root
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|"'"
argument_list|,
name|checkOnly
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|NodeList
name|childNodes
init|=
name|root
operator|.
name|getChildNodes
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
name|childNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|childNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|TRIGGERS_ELEMENT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|NodeList
name|triggers
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|triggers
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|node
operator|=
name|triggers
operator|.
name|item
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|node
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|TRIGGER_ELEMENT
argument_list|)
condition|)
block|{
name|configureTrigger
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|(
name|Element
operator|)
name|node
argument_list|,
name|srcCollectionURI
argument_list|,
name|checkOnly
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|INDEX_ELEMENT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
try|try
block|{
if|if
condition|(
name|indexSpec
operator|==
literal|null
condition|)
block|{
name|indexSpec
operator|=
operator|new
name|IndexSpec
argument_list|(
name|broker
argument_list|,
name|elem
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexSpec
operator|.
name|read
argument_list|(
name|broker
argument_list|,
name|elem
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
if|if
condition|(
name|checkOnly
condition|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|PERMISSIONS_ELEMENT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
name|String
name|permsOpt
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|RESOURCE_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|permsOpt
operator|!=
literal|null
operator|&&
name|permsOpt
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"RESOURCE: "
operator|+
name|permsOpt
argument_list|)
expr_stmt|;
try|try
block|{
name|defResPermissions
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|permsOpt
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NumberFormatException
name|e
parameter_list|)
block|{
if|if
condition|(
name|checkOnly
condition|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Illegal value for permissions in "
operator|+
literal|"configuration document : "
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
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ilegal value for permissions in "
operator|+
literal|"configuration document : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|permsOpt
operator|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|COLLECTION_ATTR
argument_list|)
expr_stmt|;
if|if
condition|(
name|permsOpt
operator|!=
literal|null
operator|&&
name|permsOpt
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"COLLECTION: "
operator|+
name|permsOpt
argument_list|)
expr_stmt|;
try|try
block|{
name|defCollPermissions
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|permsOpt
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NumberFormatException
name|e
parameter_list|)
block|{
if|if
condition|(
name|checkOnly
condition|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Illegal value for permissions in configuration "
operator|+
literal|"document : "
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
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ilegal value for permissions in configuration "
operator|+
literal|"document : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|else if
condition|(
name|GROUP_ELEMENT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
name|String
name|groupOpt
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|RESOURCE_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupOpt
operator|!=
literal|null
operator|&&
name|groupOpt
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"RESOURCE: "
operator|+
name|groupOpt
argument_list|)
expr_stmt|;
if|if
condition|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getGroup
argument_list|(
name|groupOpt
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|defResGroup
operator|=
name|groupOpt
expr_stmt|;
block|}
else|else
block|{
comment|//? Seems inconsistent : what does "checkOnly" means then ?
if|if
condition|(
name|checkOnly
condition|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Ilegal value "
operator|+
literal|"for group in configuration document : "
operator|+
name|groupOpt
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ilegal value for group in configuration document : "
operator|+
name|groupOpt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|groupOpt
operator|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|COLLECTION_ATTR
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupOpt
operator|!=
literal|null
operator|&&
name|groupOpt
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"COLLECTION: "
operator|+
name|groupOpt
argument_list|)
expr_stmt|;
if|if
condition|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getGroup
argument_list|(
name|groupOpt
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|defCollGroup
operator|=
name|groupOpt
expr_stmt|;
block|}
else|else
block|{
comment|//? Seems inconsistent : what does "checkOnly" means then ?
if|if
condition|(
name|checkOnly
condition|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Ilegal value "
operator|+
literal|"for group in configuration document : "
operator|+
name|groupOpt
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ilegal value for group in configuration document : "
operator|+
name|groupOpt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|else if
condition|(
name|VALIDATION_ELEMENT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
specifier|final
name|String
name|mode
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|VALIDATION_MODE_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|mode
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unable to determine validation mode in "
operator|+
name|srcCollectionURI
argument_list|)
expr_stmt|;
name|validationMode
operator|=
name|XMLReaderObjectFactory
operator|.
name|VALIDATION_SETTING
operator|.
name|UNKNOWN
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|srcCollectionURI
operator|+
literal|" : Validation mode="
operator|+
name|mode
argument_list|)
expr_stmt|;
name|validationMode
operator|=
name|XMLReaderObjectFactory
operator|.
name|convertValidationMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|throwOrLog
argument_list|(
literal|"Ignored node '"
operator|+
name|node
operator|.
name|getLocalName
argument_list|()
operator|+
literal|"' in configuration document"
argument_list|,
name|checkOnly
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception like above ? -pb
block|}
block|}
if|else if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"Ignored node '"
operator|+
name|node
operator|.
name|getLocalName
argument_list|()
operator|+
literal|"' in namespace '"
operator|+
name|node
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|"' in configuration document"
argument_list|,
name|checkOnly
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|throwOrLog
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|boolean
name|throwExceptions
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
if|if
condition|(
name|throwExceptions
condition|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|XmldbURI
name|getDocName
parameter_list|()
block|{
return|return
name|docName
return|;
block|}
specifier|protected
name|void
name|setIndexConfiguration
parameter_list|(
specifier|final
name|IndexSpec
name|spec
parameter_list|)
block|{
name|this
operator|.
name|indexSpec
operator|=
name|spec
expr_stmt|;
block|}
specifier|public
name|XmldbURI
name|getSourceCollectionURI
parameter_list|()
block|{
return|return
name|srcCollectionURI
return|;
block|}
specifier|public
name|int
name|getDefCollPermissions
parameter_list|()
block|{
return|return
name|defCollPermissions
return|;
block|}
specifier|public
name|int
name|getDefResPermissions
parameter_list|()
block|{
return|return
name|defResPermissions
return|;
block|}
specifier|public
name|String
name|getDefCollGroup
parameter_list|(
specifier|final
name|Account
name|user
parameter_list|)
block|{
return|return
name|defCollGroup
operator|!=
literal|null
condition|?
name|defCollGroup
else|:
name|user
operator|.
name|getPrimaryGroup
argument_list|()
return|;
block|}
specifier|public
name|String
name|getDefResGroup
parameter_list|(
specifier|final
name|Account
name|user
parameter_list|)
block|{
return|return
operator|(
name|defResGroup
operator|!=
literal|null
operator|)
condition|?
name|defResGroup
else|:
name|user
operator|.
name|getPrimaryGroup
argument_list|()
return|;
block|}
specifier|public
name|XMLReaderObjectFactory
operator|.
name|VALIDATION_SETTING
name|getValidationMode
parameter_list|()
block|{
return|return
name|validationMode
return|;
block|}
specifier|public
name|IndexSpec
name|getIndexConfiguration
parameter_list|()
block|{
return|return
name|indexSpec
return|;
block|}
specifier|private
name|void
name|configureTrigger
parameter_list|(
specifier|final
name|ClassLoader
name|cl
parameter_list|,
specifier|final
name|Element
name|triggerElement
parameter_list|,
specifier|final
name|XmldbURI
name|collectionConfigurationURI
parameter_list|,
specifier|final
name|boolean
name|testOnly
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
comment|//TODO : rely on schema-driven validation -pb
specifier|final
name|String
name|classname
init|=
name|triggerElement
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
name|CLASS_ATTRIBUTE
argument_list|)
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|classname
argument_list|,
literal|true
argument_list|,
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Trigger
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"Trigger's class '"
operator|+
name|classname
operator|+
literal|"' is not assignable from '"
operator|+
name|Trigger
operator|.
name|class
operator|+
literal|"'"
argument_list|,
name|testOnly
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|NodeList
name|nlParameter
init|=
name|triggerElement
operator|.
name|getElementsByTagNameNS
argument_list|(
name|NAMESPACE
argument_list|,
name|PARAMETER_ELEMENT
argument_list|)
decl_stmt|;
specifier|final
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
init|=
name|ParametersExtractor
operator|.
name|extract
argument_list|(
name|nlParameter
argument_list|)
decl_stmt|;
name|boolean
name|added
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|DocumentTrigger
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|docTriggers
operator|.
name|add
argument_list|(
operator|new
name|DocumentTriggerProxy
argument_list|(
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|DocumentTrigger
argument_list|>
operator|)
name|clazz
argument_list|,
name|parameters
argument_list|)
argument_list|)
expr_stmt|;
name|added
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|CollectionTrigger
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|colTriggers
operator|.
name|add
argument_list|(
operator|new
name|CollectionTriggerProxy
argument_list|(
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
operator|)
name|clazz
argument_list|,
name|parameters
argument_list|)
argument_list|)
expr_stmt|;
name|added
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|added
condition|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Unknown Trigger class type: "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassNotFoundException
decl||
name|TriggerException
name|e
parameter_list|)
block|{
if|if
condition|(
name|testOnly
condition|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trigger class not found: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|List
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
argument_list|>
name|collectionTriggers
parameter_list|()
block|{
return|return
name|colTriggers
return|;
block|}
specifier|public
name|List
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|DocumentTrigger
argument_list|>
argument_list|>
name|documentTriggers
parameter_list|()
block|{
return|return
name|docTriggers
return|;
block|}
specifier|public
name|boolean
name|triggerRegistered
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|triggerClass
parameter_list|)
block|{
if|if
condition|(
name|DocumentTrigger
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|triggerClass
argument_list|)
condition|)
block|{
if|if
condition|(
name|hasTriggerProxy
argument_list|(
name|docTriggers
argument_list|,
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|DocumentTrigger
argument_list|>
operator|)
name|triggerClass
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
name|CollectionTrigger
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|triggerClass
argument_list|)
condition|)
block|{
if|if
condition|(
name|hasTriggerProxy
argument_list|(
name|colTriggers
argument_list|,
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
operator|)
name|triggerClass
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
specifier|private
parameter_list|<
name|T
parameter_list|>
name|boolean
name|hasTriggerProxy
parameter_list|(
specifier|final
name|List
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|triggerProxies
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|triggerProxyClazz
parameter_list|)
block|{
for|for
control|(
specifier|final
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|triggerProxy
range|:
name|triggerProxies
control|)
block|{
if|if
condition|(
name|triggerProxy
operator|.
name|getClazz
argument_list|()
operator|==
name|triggerProxyClazz
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
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexSpec
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|indexSpec
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

