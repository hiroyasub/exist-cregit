begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
specifier|public
class|class
name|CollectionConfiguration
block|{
specifier|public
specifier|final
specifier|static
name|String
name|COLLECTION_CONFIG_FILE
init|=
literal|"collection.xconf"
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
specifier|private
specifier|final
specifier|static
name|String
name|INDEX_ELEMENT
init|=
literal|"index"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCROOT_ATTRIBUTE
init|=
literal|"root"
decl_stmt|;
specifier|private
name|Trigger
index|[]
name|triggers
init|=
operator|new
name|Trigger
index|[
literal|3
index|]
decl_stmt|;
specifier|private
name|IndexSpec
name|indexSpec
init|=
literal|null
decl_stmt|;
specifier|private
name|Collection
name|collection
decl_stmt|;
specifier|public
name|CollectionConfiguration
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
specifier|public
name|CollectionConfiguration
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|read
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param broker      * @param collection      * @param doc      * @throws CollectionConfigurationException      */
specifier|protected
name|void
name|read
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
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
operator|!
operator|(
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
name|ROOT_ELEMENT
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Wrong document root for collection configuration. "
operator|+
literal|"The root element should be "
operator|+
name|ROOT_ELEMENT
operator|+
literal|" in namespace "
operator|+
name|NAMESPACE
argument_list|)
throw|;
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
name|elem
argument_list|)
expr_stmt|;
else|else
name|indexSpec
operator|.
name|read
argument_list|(
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
block|}
block|}
block|}
specifier|public
name|Collection
name|getCollection
parameter_list|()
block|{
return|return
name|collection
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
name|getTrigger
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
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"trigger requires an attribute 'event'"
argument_list|)
throw|;
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
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"trigger requires an attribute 'class'"
argument_list|)
throw|;
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
name|Trigger
name|trigger
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Registering trigger "
operator|+
name|classAttr
operator|+
literal|" for event "
operator|+
name|event
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
name|triggers
index|[
name|Trigger
operator|.
name|STORE_DOCUMENT_EVENT
index|]
operator|=
name|instantiate
argument_list|(
name|broker
argument_list|,
name|node
argument_list|,
name|classAttr
argument_list|)
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
name|triggers
index|[
name|Trigger
operator|.
name|UPDATE_DOCUMENT_EVENT
index|]
operator|=
name|instantiate
argument_list|(
name|broker
argument_list|,
name|node
argument_list|,
name|classAttr
argument_list|)
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
name|triggers
index|[
name|Trigger
operator|.
name|REMOVE_DOCUMENT_EVENT
index|]
operator|=
name|instantiate
argument_list|(
name|broker
argument_list|,
name|node
argument_list|,
name|classAttr
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"unknown event type '"
operator|+
name|event
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Trigger
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
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"supplied class is not a subclass of org.exist.collections.Trigger"
argument_list|)
throw|;
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
name|NodeList
name|nodes
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|next
decl_stmt|;
name|Element
name|param
decl_stmt|;
name|String
name|name
decl_stmt|,
name|value
decl_stmt|;
name|Map
name|parameters
init|=
operator|new
name|HashMap
argument_list|(
literal|5
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
name|next
operator|=
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|next
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|NAMESPACE
argument_list|)
operator|&&
name|next
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|PARAMETER_ELEMENT
argument_list|)
condition|)
block|{
name|param
operator|=
operator|(
name|Element
operator|)
name|next
expr_stmt|;
name|name
operator|=
name|param
operator|.
name|getAttribute
argument_list|(
name|PARAM_NAME_ATTRIBUTE
argument_list|)
expr_stmt|;
name|value
operator|=
name|param
operator|.
name|getAttribute
argument_list|(
name|PARAM_VALUE_ATTRIBUTE
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|value
operator|==
literal|null
condition|)
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"element parameter requires attributes "
operator|+
literal|"'name' and 'value'"
argument_list|)
throw|;
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
name|ClassNotFoundException
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
block|}
end_class

end_unit

