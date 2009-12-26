begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
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
name|NodeList
import|;
end_import

begin_comment
comment|/**  * configuration -> element  * property -> attribute  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ConfigElementImpl
extends|extends
name|ProxyElement
argument_list|<
name|ElementAtExist
argument_list|>
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
name|ConfigElementImpl
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
name|ConfigElementImpl
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|ConfigElementImpl
parameter_list|()
block|{
block|}
specifier|protected
name|ConfigElementImpl
parameter_list|(
name|ElementAtExist
name|element
parameter_list|)
block|{
name|setProxyObject
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConfigElementImpl
name|getConfiguration
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|ConfigElementImpl
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
name|ConfigElementImpl
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
name|ConfigElementImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ConfigElementImpl
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
name|ConfigElementImpl
name|config
init|=
operator|new
name|ConfigElementImpl
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
return|return
name|getAttribute
argument_list|(
name|name
argument_list|)
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
name|getAttribute
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
return|return
name|hasAttribute
argument_list|(
name|name
argument_list|)
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
name|setAttribute
argument_list|(
name|name
argument_list|,
name|value
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
name|List
argument_list|<
name|String
argument_list|>
name|getProperties
parameter_list|()
block|{
name|NamedNodeMap
name|attrs
init|=
name|getAttributes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|properties
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|attrs
operator|.
name|item
argument_list|(
name|index
argument_list|)
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
block|}
end_class

end_unit

