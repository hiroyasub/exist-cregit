begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|*
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
name|persistent
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
name|storage
operator|.
name|DBBroker
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_comment
comment|/**  * configuration -&gt; element  * property -&gt; attribute  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ConfigurationImpl
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
argument_list|<>
argument_list|()
decl_stmt|;
specifier|protected
name|WeakReference
argument_list|<
name|Configurable
argument_list|>
name|configuredObjectReference
init|=
literal|null
decl_stmt|;
specifier|private
name|Element
name|element
decl_stmt|;
specifier|private
name|ConfigurationImpl
parameter_list|()
block|{
comment|//Nothing to do
block|}
specifier|protected
name|ConfigurationImpl
parameter_list|(
specifier|final
name|Element
name|element
parameter_list|)
block|{
name|this
operator|.
name|element
operator|=
name|element
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Element
name|getElement
parameter_list|()
block|{
return|return
name|element
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|element
operator|.
name|getLocalName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|element
operator|.
name|getTextContent
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|element
operator|.
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
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
specifier|final
name|List
argument_list|<
name|Configuration
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Node
name|child
init|=
name|element
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
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
specifier|final
name|Element
name|el
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
specifier|final
name|String
name|ns
init|=
name|el
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|el
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|&&
name|ns
operator|!=
literal|null
operator|&&
name|NS
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
condition|)
block|{
specifier|final
name|Configuration
name|config
init|=
operator|new
name|ConfigurationImpl
argument_list|(
name|el
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
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
literal|null
decl_stmt|;
specifier|private
name|void
name|cache
parameter_list|()
block|{
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
return|return;
name|props
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Node
name|child
init|=
name|element
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
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
specifier|final
name|String
name|ns
init|=
name|child
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|ns
operator|!=
literal|null
operator|&&
name|NS
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|child
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|props
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|props
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|props
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|child
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
comment|//load attributes values
name|NamedNodeMap
name|attrs
init|=
name|element
operator|.
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
name|Node
name|attr
init|=
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
operator|.
name|equals
argument_list|(
name|attr
operator|.
name|getPrefix
argument_list|()
argument_list|)
condition|)
block|{
name|props
operator|.
name|put
argument_list|(
name|attr
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|attr
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|clearCache
parameter_list|()
block|{
name|props
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|cache
argument_list|()
expr_stmt|;
return|return
name|props
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
comment|//        if (hasAttribute(name))
comment|//            {return getAttribute(name);}
comment|//        final NodeList nodes = getElementsByTagNameNS(NS, name);
comment|//        if (nodes.getLength() == 1) {
comment|//            return nodes.item(0).getTextContent();
comment|//        }
comment|//        return null;
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
specifier|final
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
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPropertyMap
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
name|Node
name|child
init|=
name|element
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
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
specifier|final
name|Element
name|el
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
specifier|final
name|String
name|ns
init|=
name|el
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|el
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|&&
name|ns
operator|!=
literal|null
operator|&&
name|NS
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|el
operator|.
name|hasAttributes
argument_list|()
condition|)
block|{
continue|continue;
block|}
specifier|final
name|NamedNodeMap
name|attrs
init|=
name|el
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|attrs
operator|.
name|getLength
argument_list|()
operator|!=
literal|1
condition|)
block|{
continue|continue;
block|}
name|Node
name|attr
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|==
literal|null
condition|)
continue|continue;
specifier|final
name|String
name|key
init|=
name|attr
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|value
init|=
name|el
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
operator|&&
operator|!
name|key
operator|.
name|isEmpty
argument_list|()
operator|&&
name|value
operator|!=
literal|null
operator|&&
operator|!
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|map
return|;
comment|//        if(hasAttribute(name)) {
comment|//            return null;
comment|//        }
comment|//        final Map<String, String> map = new HashMap<String, String>();
comment|//        final NodeList nodes = getElementsByTagNameNS(NS, name);
comment|//        for(int i = 0; i< nodes.getLength(); i++) {
comment|//            final Node item = nodes.item(i);
comment|//            if(!item.hasAttributes()){
comment|//                return null;
comment|//            }
comment|//            final NamedNodeMap attrs = item.getAttributes();
comment|//            if(attrs.getLength() != 1){
comment|//                return null;
comment|//            }
comment|//            final String key = attrs.getNamedItem("key").getNodeValue();
comment|//            final String value = item.getTextContent();
comment|//            if(value == null || value.isEmpty()){
comment|//                return null;
comment|//            }
comment|//            map.put(key, value);
comment|//        }
comment|//        return map;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|cache
argument_list|()
expr_stmt|;
return|return
name|props
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
comment|//        if (hasAttribute(name))
comment|//            {return true;}
comment|//        return (getElementsByTagName(name).getLength() == 1);
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
annotation|@
name|Override
specifier|public
name|Boolean
name|getPropertyBoolean
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|value
operator|.
name|toLowerCase
argument_list|()
condition|)
block|{
case|case
literal|"yes"
case|:
case|case
literal|"true"
case|:
return|return
literal|true
return|;
case|case
literal|"no"
case|:
case|case
literal|"false"
case|:
return|return
literal|false
return|;
default|default:
return|return
literal|null
return|;
block|}
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
name|Boolean
name|value
init|=
name|getPropertyBoolean
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
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|getPropertyInteger
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
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
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Integer
name|defaultValue
parameter_list|,
specifier|final
name|boolean
name|positive
parameter_list|)
block|{
specifier|final
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
block|{
return|return
name|defaultValue
return|;
block|}
specifier|final
name|int
name|result
init|=
name|Integer
operator|.
name|parseInt
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
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|Long
name|getPropertyLong
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
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
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Long
name|defaultValue
parameter_list|,
specifier|final
name|boolean
name|positive
parameter_list|)
block|{
specifier|final
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
block|{
return|return
name|defaultValue
return|;
block|}
specifier|final
name|long
name|result
init|=
name|Long
operator|.
name|parseLong
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
block|{
return|return
name|defaultValue
operator|.
name|longValue
argument_list|()
return|;
block|}
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
name|element
operator|.
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
block|{
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
block|}
specifier|final
name|Integer
name|result
init|=
name|Integer
operator|.
name|valueOf
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
block|{
return|return
name|defaultValue
return|;
block|}
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
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getProperties
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|properties
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|NamedNodeMap
name|attrs
init|=
name|element
operator|.
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
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
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
block|{
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
block|}
specifier|final
name|NodeList
name|children
init|=
name|element
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
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
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
name|ELEMENT_NODE
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|properties
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
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
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
if|if
condition|(
name|objects
operator|==
literal|null
condition|)
name|objects
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
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
specifier|synchronized
name|Object
name|getObject
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|objects
operator|==
literal|null
condition|)
return|return
literal|null
return|;
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
name|Element
name|element
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|saving
operator|&&
name|configuredObjectReference
operator|!=
literal|null
operator|&&
name|configuredObjectReference
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|clearCache
argument_list|()
expr_stmt|;
name|this
operator|.
name|element
operator|=
name|element
expr_stmt|;
name|Configurator
operator|.
name|configure
argument_list|(
name|configuredObjectReference
operator|.
name|get
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
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
name|ConfigurationException
block|{
comment|//ignore in-memory nodes
if|if
condition|(
name|element
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|ElementImpl
condition|)
block|{
return|return;
block|}
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
name|configuredObjectReference
operator|!=
literal|null
operator|&&
name|configuredObjectReference
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Configurator
operator|.
name|save
argument_list|(
name|configuredObjectReference
operator|.
name|get
argument_list|()
argument_list|,
operator|(
operator|(
name|DocumentImpl
operator|)
name|element
operator|.
name|getOwnerDocument
argument_list|()
operator|)
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
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
finally|finally
block|{
name|saving
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|save
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|ConfigurationException
block|{
comment|//ignore in-memory nodes
if|if
condition|(
name|element
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|ElementImpl
condition|)
block|{
return|return;
block|}
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
name|configuredObjectReference
operator|!=
literal|null
operator|&&
name|configuredObjectReference
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Configurator
operator|.
name|save
argument_list|(
name|broker
argument_list|,
name|configuredObjectReference
operator|.
name|get
argument_list|()
argument_list|,
operator|(
operator|(
name|DocumentImpl
operator|)
name|element
operator|.
name|getOwnerDocument
argument_list|()
operator|)
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
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
finally|finally
block|{
name|saving
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|)
block|{
return|return
name|equals
argument_list|(
name|obj
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|,
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|property
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|ConfigurationImpl
condition|)
block|{
specifier|final
name|ConfigurationImpl
name|conf
init|=
operator|(
name|ConfigurationImpl
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|conf
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|String
name|name
init|=
name|property
operator|.
name|orElse
argument_list|(
name|Configuration
operator|.
name|ID
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|value
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|value
operator|.
name|map
argument_list|(
name|v
lambda|->
name|v
operator|.
name|equals
argument_list|(
name|conf
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|false
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

