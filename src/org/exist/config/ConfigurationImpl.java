begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|Map
operator|.
name|Entry
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
name|dom
operator|.
name|DocumentAtExist
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
name|PermissionDeniedException
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
name|NamedNodeMap
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

begin_comment
comment|/**  * configuration -> element  * property -> attribute  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ConfigurationImpl
extends|extends
name|ProxyElement
argument_list|<
name|ElementAtExist
argument_list|>
implements|implements
name|Configuration
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Configuration
argument_list|>
argument_list|>
name|configs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Configuration
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|WeakReference
argument_list|<
name|Configurable
argument_list|>
name|configuredObjectReferene
init|=
literal|null
decl_stmt|;
specifier|private
name|ConfigurationImpl
parameter_list|()
block|{
block|}
specifier|protected
name|ConfigurationImpl
parameter_list|(
name|ElementAtExist
name|element
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|setProxyObject
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|getLocalName
argument_list|()
return|;
block|}
specifier|public
name|Configuration
name|getConfiguration
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
name|List
argument_list|<
name|Configuration
argument_list|>
name|list
init|=
name|getConfigurations
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
return|return
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|Configuration
argument_list|>
name|getConfigurations
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|configs
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|configs
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
name|NodeList
name|nodes
init|=
name|getElementsByTagName
argument_list|(
name|name
argument_list|)
decl_stmt|;
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
name|List
argument_list|<
name|Configuration
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Configuration
argument_list|>
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
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Configuration
name|config
init|=
operator|new
name|ConfigurationImpl
argument_list|(
operator|(
name|ElementAtExist
operator|)
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
name|configs
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|list
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|hasAttribute
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|getAttribute
argument_list|(
name|name
argument_list|)
return|;
name|NodeList
name|nodes
init|=
name|getElementsByTagNameNS
argument_list|(
name|NS
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|nodes
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeValue
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|default_property
parameter_list|)
block|{
name|String
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
condition|)
return|return
name|default_property
return|;
return|return
name|property
return|;
block|}
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|hasAttribute
argument_list|(
name|name
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
operator|(
name|getElementsByTagName
argument_list|(
name|name
argument_list|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
operator|)
return|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
comment|//detect save place: attribute or element's text
name|setAttribute
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|Integer
name|value
parameter_list|)
block|{
name|setProperty
argument_list|(
name|property
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|getRuntimeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|runtimeProperties
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasRuntimeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|runtimeProperties
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|void
name|setRuntimeProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|runtimeProperties
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Boolean
name|getPropertyBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|value
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
literal|"yes"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
return|return
literal|true
return|;
if|else if
condition|(
literal|"no"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
return|return
literal|false
return|;
if|else if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
return|return
literal|true
return|;
if|else if
condition|(
literal|"false"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
return|return
literal|false
return|;
comment|//???
return|return
literal|null
return|;
block|}
specifier|public
name|Boolean
name|getPropertyBoolean
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|defaultValue
argument_list|)
return|;
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
literal|"yes"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
operator|||
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Integer
name|getPropertyInteger
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|value
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
name|Integer
name|getPropertyInteger
parameter_list|(
name|String
name|name
parameter_list|,
name|Integer
name|defaultValue
parameter_list|,
name|boolean
name|positive
parameter_list|)
block|{
name|String
name|value
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
name|defaultValue
return|;
name|Integer
name|result
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|positive
operator|)
operator|&&
operator|(
name|result
operator|<
literal|0
operator|)
condition|)
return|return
name|defaultValue
operator|.
name|intValue
argument_list|()
return|;
return|return
name|result
return|;
block|}
specifier|public
name|Long
name|getPropertyLong
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|value
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
name|Long
name|getPropertyLong
parameter_list|(
name|String
name|name
parameter_list|,
name|Long
name|defaultValue
parameter_list|,
name|boolean
name|positive
parameter_list|)
block|{
name|String
name|value
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
name|defaultValue
return|;
name|long
name|result
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|positive
operator|)
operator|&&
operator|(
name|result
operator|<
literal|0
operator|)
condition|)
return|return
name|defaultValue
operator|.
name|longValue
argument_list|()
return|;
return|return
name|result
return|;
block|}
specifier|public
name|Integer
name|getPropertyMegabytes
parameter_list|(
name|String
name|name
parameter_list|,
name|Integer
name|defaultValue
parameter_list|)
block|{
name|String
name|cacheMem
init|=
name|getAttribute
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheMem
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|cacheMem
operator|.
name|endsWith
argument_list|(
literal|"M"
argument_list|)
operator|||
name|cacheMem
operator|.
name|endsWith
argument_list|(
literal|"m"
argument_list|)
condition|)
name|cacheMem
operator|=
name|cacheMem
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|cacheMem
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Integer
name|result
init|=
operator|new
name|Integer
argument_list|(
name|cacheMem
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|<
literal|0
condition|)
return|return
name|defaultValue
return|;
return|return
name|result
return|;
block|}
return|return
name|defaultValue
return|;
block|}
specifier|public
name|String
name|getConfigFilePath
parameter_list|()
block|{
return|return
literal|""
return|;
comment|//XXX: put config url
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getProperties
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|properties
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NamedNodeMap
name|attrs
init|=
name|getAttributes
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
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|//ignore namespace declarations
if|if
condition|(
operator|!
literal|"xmlns"
operator|.
name|equals
argument_list|(
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getPrefix
argument_list|()
argument_list|)
condition|)
name|properties
operator|.
name|add
argument_list|(
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
comment|//XXX: detect single element as field value
name|NodeList
name|children
init|=
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
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
condition|)
block|{
comment|//properties.add(child.getNodeName());
block|}
if|else if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|map
operator|.
name|containsKey
argument_list|(
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|entry
operator|.
name|getValue
argument_list|()
condition|)
name|properties
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getPropertyClass
parameter_list|(
name|String
name|propertySecurityClass
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|//related objects
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|objects
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
name|putObject
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|object
parameter_list|)
block|{
return|return
name|objects
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|object
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getObject
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|objects
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|saving
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|checkForUpdates
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|)
block|{
if|if
condition|(
operator|!
name|saving
operator|&&
name|configuredObjectReferene
operator|!=
literal|null
operator|&&
name|configuredObjectReferene
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setProxyObject
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
name|Configurator
operator|.
name|configure
argument_list|(
name|configuredObjectReferene
operator|.
name|get
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|//ignore in-memory nodes
if|if
condition|(
name|getProxyObject
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"org.exist.memtree"
argument_list|)
condition|)
return|return;
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|saving
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|configuredObjectReferene
operator|!=
literal|null
operator|&&
name|configuredObjectReferene
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
name|Configurator
operator|.
name|save
argument_list|(
name|configuredObjectReferene
operator|.
name|get
argument_list|()
argument_list|,
name|getProxyObject
argument_list|()
operator|.
name|getDocumentAtExist
argument_list|()
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
comment|//Configurator.save(getProxyObject().getDocumentAtExist());
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//throw new EXistException(e);
block|}
finally|finally
block|{
name|saving
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

