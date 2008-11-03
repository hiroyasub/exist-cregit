begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_class
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
specifier|final
specifier|static
name|String
name|ROOT_ELEMENT
init|=
literal|"collection"
decl_stmt|;
comment|/** First level element in a collection configuration document */
specifier|private
specifier|final
specifier|static
name|String
name|TRIGGERS_ELEMENT
init|=
literal|"triggers"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|EVENT_ATTRIBUTE
init|=
literal|"event"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CLASS_ATTRIBUTE
init|=
literal|"class"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PARAMETER_ELEMENT
init|=
literal|"parameter"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PARAM_NAME_ATTRIBUTE
init|=
literal|"name"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PARAM_VALUE_ATTRIBUTE
init|=
literal|"value"
decl_stmt|;
comment|/** First level element in a collection configuration document */
specifier|private
specifier|final
specifier|static
name|String
name|INDEX_ELEMENT
init|=
literal|"index"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PERMISSIONS_ELEMENT
init|=
literal|"default-permissions"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|RESOURCE_PERMISSIONS_ATTR
init|=
literal|"resource"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COLLECTION_PERMISSIONS_ATTR
init|=
literal|"collection"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALIDATION_ELEMENT
init|=
literal|"validation"
decl_stmt|;
specifier|private
specifier|final
specifier|static
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|CollectionConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TriggerConfig
index|[]
name|triggers
init|=
operator|new
name|TriggerConfig
index|[
literal|6
index|]
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
decl_stmt|;
specifier|private
name|int
name|defResPermissions
decl_stmt|;
specifier|private
name|int
name|validationMode
init|=
name|XMLReaderObjectFactory
operator|.
name|VALIDATION_UNKNOWN
decl_stmt|;
specifier|public
name|CollectionConfiguration
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|defResPermissions
operator|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getResourceDefaultPerms
argument_list|()
expr_stmt|;
name|this
operator|.
name|defCollPermissions
operator|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getCollectionDefaultPerms
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|boolean
name|isCollectionConfigDocument
parameter_list|(
name|XmldbURI
name|docName
parameter_list|)
block|{
return|return
name|docName
operator|.
name|endsWith
argument_list|(
name|CollectionConfiguration
operator|.
name|COLLECTION_CONFIG_SUFFIX_URI
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isCollectionConfigDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|XmldbURI
name|docName
init|=
name|doc
operator|.
name|getURI
argument_list|()
decl_stmt|;
return|return
name|isCollectionConfigDocument
argument_list|(
name|docName
argument_list|)
return|;
block|}
comment|/**      * @param broker      * @param srcCollectionURI The collection from which the document is being read.  This      * is not necessarily the same as this.collection.getURI() because the      * source document may have come from a parent collection.      * @param docName The name of the document being read      * @param doc collection configuration document      * @throws CollectionConfigurationException      */
specifier|protected
name|void
name|read
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Document
name|doc
parameter_list|,
name|boolean
name|checkOnly
parameter_list|,
name|XmldbURI
name|srcCollectionURI
parameter_list|,
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
name|NodeList
name|childNodes
init|=
name|root
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|node
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
name|node
operator|=
name|childNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
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
condition|)
name|createTrigger
argument_list|(
name|broker
argument_list|,
operator|(
name|Element
operator|)
name|node
argument_list|,
name|checkOnly
argument_list|)
expr_stmt|;
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
else|else
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
catch|catch
parameter_list|(
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
if|if
condition|(
name|checkOnly
condition|)
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
else|else
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
name|RESOURCE_PERMISSIONS_ATTR
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
name|NumberFormatException
name|e
parameter_list|)
block|{
if|if
condition|(
name|checkOnly
condition|)
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Ilegal value for permissions in configuration document : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
else|else
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ilegal value for permissions in configuration document : "
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
name|permsOpt
operator|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|COLLECTION_PERMISSIONS_ATTR
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
name|NumberFormatException
name|e
parameter_list|)
block|{
if|if
condition|(
name|checkOnly
condition|)
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Ilegal value for permissions in configuration document : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
else|else
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ilegal value for permissions in configuration document : "
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
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
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
name|VALIDATION_UNKNOWN
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
name|String
name|message
parameter_list|,
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
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
name|message
argument_list|)
throw|;
else|else
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
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
name|int
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
specifier|public
name|Trigger
name|newTrigger
parameter_list|(
name|int
name|eventType
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|CollectionConfigurationException
block|{
name|TriggerConfig
name|config
init|=
name|getTriggerConfiguration
argument_list|(
name|eventType
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
return|return
name|config
operator|.
name|newInstance
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|)
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|TriggerConfig
name|getTriggerConfiguration
parameter_list|(
name|int
name|eventType
parameter_list|)
block|{
return|return
name|triggers
index|[
name|eventType
index|]
return|;
block|}
specifier|private
name|void
name|createTrigger
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Element
name|node
parameter_list|,
name|boolean
name|testConfig
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
name|String
name|eventAttr
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|EVENT_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|eventAttr
operator|==
literal|null
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"'"
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
operator|+
literal|"' requires an attribute '"
operator|+
name|EVENT_ATTRIBUTE
operator|+
literal|"'"
argument_list|,
name|testConfig
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|classAttr
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|CLASS_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|classAttr
operator|==
literal|null
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"'"
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
operator|+
literal|"' requires an attribute '"
operator|+
name|CLASS_ATTRIBUTE
operator|+
literal|"'"
argument_list|,
name|testConfig
argument_list|)
expr_stmt|;
return|return;
block|}
name|TriggerConfig
name|trigger
init|=
name|instantiate
argument_list|(
name|broker
argument_list|,
name|node
argument_list|,
name|classAttr
argument_list|,
name|testConfig
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|testConfig
condition|)
block|{
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|eventAttr
argument_list|,
literal|", "
argument_list|)
decl_stmt|;
name|String
name|event
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|event
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Registering trigger '"
operator|+
name|classAttr
operator|+
literal|"' for event '"
operator|+
name|event
operator|+
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"store"
argument_list|)
condition|)
block|{
if|if
condition|(
name|triggers
index|[
name|Trigger
operator|.
name|STORE_DOCUMENT_EVENT
index|]
operator|!=
literal|null
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trigger '"
operator|+
name|classAttr
operator|+
literal|"' already registered"
argument_list|)
expr_stmt|;
name|triggers
index|[
name|Trigger
operator|.
name|STORE_DOCUMENT_EVENT
index|]
operator|=
name|trigger
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"update"
argument_list|)
condition|)
block|{
if|if
condition|(
name|triggers
index|[
name|Trigger
operator|.
name|UPDATE_DOCUMENT_EVENT
index|]
operator|!=
literal|null
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trigger '"
operator|+
name|classAttr
operator|+
literal|"' already registered"
argument_list|)
expr_stmt|;
name|triggers
index|[
name|Trigger
operator|.
name|UPDATE_DOCUMENT_EVENT
index|]
operator|=
name|trigger
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"remove"
argument_list|)
condition|)
block|{
if|if
condition|(
name|triggers
index|[
name|Trigger
operator|.
name|REMOVE_DOCUMENT_EVENT
index|]
operator|!=
literal|null
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trigger '"
operator|+
name|classAttr
operator|+
literal|"' already registered"
argument_list|)
expr_stmt|;
name|triggers
index|[
name|Trigger
operator|.
name|REMOVE_DOCUMENT_EVENT
index|]
operator|=
name|trigger
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"create-collection"
argument_list|)
condition|)
block|{
if|if
condition|(
name|triggers
index|[
name|Trigger
operator|.
name|CREATE_COLLECTION_EVENT
index|]
operator|!=
literal|null
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trigger '"
operator|+
name|classAttr
operator|+
literal|"' already registered"
argument_list|)
expr_stmt|;
name|triggers
index|[
name|Trigger
operator|.
name|CREATE_COLLECTION_EVENT
index|]
operator|=
name|trigger
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"rename-collection"
argument_list|)
condition|)
block|{
if|if
condition|(
name|triggers
index|[
name|Trigger
operator|.
name|RENAME_COLLECTION_EVENT
index|]
operator|!=
literal|null
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trigger '"
operator|+
name|classAttr
operator|+
literal|"' already registered"
argument_list|)
expr_stmt|;
name|triggers
index|[
name|Trigger
operator|.
name|RENAME_COLLECTION_EVENT
index|]
operator|=
name|trigger
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"delete-collection"
argument_list|)
condition|)
block|{
if|if
condition|(
name|triggers
index|[
name|Trigger
operator|.
name|DELETE_COLLECTION_EVENT
index|]
operator|!=
literal|null
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"Trigger '"
operator|+
name|classAttr
operator|+
literal|"' already registered"
argument_list|)
expr_stmt|;
name|triggers
index|[
name|Trigger
operator|.
name|DELETE_COLLECTION_EVENT
index|]
operator|=
name|trigger
expr_stmt|;
block|}
else|else
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown event type '"
operator|+
name|event
operator|+
literal|"' in trigger '"
operator|+
name|classAttr
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|TriggerConfig
name|instantiate
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Element
name|node
parameter_list|,
name|String
name|classname
parameter_list|,
name|boolean
name|testOnly
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|classname
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
return|return
literal|null
return|;
block|}
name|TriggerConfig
name|triggerConf
init|=
operator|new
name|TriggerConfig
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|NodeList
name|nodes
init|=
name|node
operator|.
name|getElementsByTagNameNS
argument_list|(
name|NAMESPACE
argument_list|,
name|PARAMETER_ELEMENT
argument_list|)
decl_stmt|;
comment|//TODO : rely on schema-driven validation -pb
if|if
condition|(
name|nodes
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
name|parameters
init|=
operator|new
name|HashMap
argument_list|(
name|nodes
operator|.
name|getLength
argument_list|()
argument_list|)
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
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|param
init|=
operator|(
name|Element
operator|)
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//TODO : rely on schema-driven validation -pb
name|String
name|name
init|=
name|param
operator|.
name|getAttribute
argument_list|(
name|PARAM_NAME_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"Expected attribute '"
operator|+
name|PARAM_NAME_ATTRIBUTE
operator|+
literal|"' for element '"
operator|+
name|PARAMETER_ELEMENT
operator|+
literal|"' in trigger's configuration."
argument_list|,
name|testOnly
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|value
init|=
name|param
operator|.
name|getAttribute
argument_list|(
name|PARAM_VALUE_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|throwOrLog
argument_list|(
literal|"Expected attribute '"
operator|+
name|PARAM_VALUE_ATTRIBUTE
operator|+
literal|"' for element '"
operator|+
name|PARAMETER_ELEMENT
operator|+
literal|"' in trigger's configuration."
argument_list|,
name|testOnly
argument_list|)
expr_stmt|;
block|}
else|else
name|parameters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|triggerConf
operator|.
name|setParameters
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
block|}
return|return
name|triggerConf
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|testOnly
condition|)
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
else|else
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
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexSpec
operator|!=
literal|null
condition|)
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
literal|'\n'
argument_list|)
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
name|triggers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TriggerConfig
name|trigger
init|=
name|triggers
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|trigger
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|i
condition|)
block|{
case|case
name|Trigger
operator|.
name|STORE_DOCUMENT_EVENT
case|:
name|result
operator|.
name|append
argument_list|(
literal|"store document trigger"
argument_list|)
expr_stmt|;
case|case
name|Trigger
operator|.
name|UPDATE_DOCUMENT_EVENT
case|:
name|result
operator|.
name|append
argument_list|(
literal|"update document trigger"
argument_list|)
expr_stmt|;
case|case
name|Trigger
operator|.
name|REMOVE_DOCUMENT_EVENT
case|:
name|result
operator|.
name|append
argument_list|(
literal|"remove document trigger"
argument_list|)
expr_stmt|;
case|case
name|Trigger
operator|.
name|CREATE_COLLECTION_EVENT
case|:
name|result
operator|.
name|append
argument_list|(
literal|"create collection trigger"
argument_list|)
expr_stmt|;
case|case
name|Trigger
operator|.
name|RENAME_COLLECTION_EVENT
case|:
name|result
operator|.
name|append
argument_list|(
literal|"rename collection trigger"
argument_list|)
expr_stmt|;
case|case
name|Trigger
operator|.
name|DELETE_COLLECTION_EVENT
case|:
name|result
operator|.
name|append
argument_list|(
literal|"delete collection trigger"
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
operator|.
name|append
argument_list|(
name|trigger
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
class|class
name|TriggerConfig
block|{
specifier|private
name|Class
name|clazz
decl_stmt|;
specifier|private
name|Map
name|parameters
decl_stmt|;
specifier|public
name|TriggerConfig
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
block|}
specifier|public
name|Trigger
name|newInstance
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
try|try
block|{
name|Trigger
name|trigger
init|=
operator|(
name|Trigger
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|trigger
operator|.
name|configure
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
return|return
name|trigger
return|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
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
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
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
block|}
specifier|public
name|Map
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
specifier|public
name|void
name|setParameters
parameter_list|(
name|Map
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
specifier|public
name|Class
name|getTriggerClass
parameter_list|()
block|{
return|return
name|clazz
return|;
block|}
block|}
block|}
end_class

end_unit

