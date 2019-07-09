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
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|CollectionConfiguration
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
name|CollectionConfigurationManager
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
name|io
operator|.
name|FastByteArrayInputStream
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
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
name|Resource
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

begin_comment
comment|/**  * Class to represent a collection.xconf which holds the configuration data for a collection  *   * @author<a href="mailto:adam.retter@devon.gov.uk">Adam Retter</a>  * @serial 2006-08-25  * @version 1.2  */
end_comment

begin_class
specifier|public
class|class
name|CollectionXConf
block|{
specifier|public
specifier|final
specifier|static
name|String
name|TYPE_QNAME
init|=
literal|"qname"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TYPE_PATH
init|=
literal|"path"
decl_stmt|;
specifier|private
name|InteractiveClient
name|client
init|=
literal|null
decl_stmt|;
comment|//the client
specifier|private
name|String
name|path
init|=
literal|null
decl_stmt|;
comment|//path of the collection.xconf file
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
comment|//the configuration collection
name|Resource
name|resConfig
init|=
literal|null
decl_stmt|;
comment|//the collection.xconf resource
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|customNamespaces
init|=
literal|null
decl_stmt|;
comment|//custom namespaces
specifier|private
name|RangeIndex
index|[]
name|rangeIndexes
init|=
literal|null
decl_stmt|;
comment|//range indexes model
specifier|private
name|Trigger
index|[]
name|triggers
init|=
literal|null
decl_stmt|;
comment|//triggers model
specifier|private
name|boolean
name|hasChanged
init|=
literal|false
decl_stmt|;
comment|//indicates if changes have been made to the current collection configuration
comment|/** 	 * Constructor 	 *  	 * @param CollectionName	The path of the collection to retreive the collection.xconf for 	 * @param client	The interactive client 	 */
name|CollectionXConf
parameter_list|(
name|String
name|CollectionName
parameter_list|,
name|InteractiveClient
name|client
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
comment|//get configuration collection for the named collection
comment|//TODO : use XmldbURIs
name|path
operator|=
name|CollectionConfigurationManager
operator|.
name|CONFIG_COLLECTION
operator|+
name|CollectionName
expr_stmt|;
name|collection
operator|=
name|client
operator|.
name|getCollection
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
comment|//if no config collection for this collection exists, just return
block|{
return|return;
block|}
comment|//get the resource from the db
specifier|final
name|String
index|[]
name|resources
init|=
name|collection
operator|.
name|listResources
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|resource
range|:
name|resources
control|)
block|{
if|if
condition|(
name|resource
operator|.
name|endsWith
argument_list|(
name|CollectionConfiguration
operator|.
name|COLLECTION_CONFIG_SUFFIX
argument_list|)
condition|)
block|{
name|resConfig
operator|=
name|collection
operator|.
name|getResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|resConfig
operator|==
literal|null
condition|)
comment|//if, no config file exists for that collection
block|{
return|return;
block|}
comment|//Parse the configuration file into a DOM
specifier|final
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Document
name|docConfig
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|DocumentBuilder
name|builder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|docConfig
operator|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|FastByteArrayInputStream
argument_list|(
name|resConfig
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
decl||
name|SAXException
decl||
name|IOException
name|pce
parameter_list|)
block|{
comment|//TODO: do something here, throw exception?
block|}
comment|//Get the root of the collection.xconf
specifier|final
name|Element
name|xconf
init|=
name|docConfig
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
comment|//Read any custom namespaces from xconf
name|customNamespaces
operator|=
name|getCustomNamespaces
argument_list|(
name|xconf
argument_list|)
expr_stmt|;
comment|//Read Range Indexes from xconf
name|rangeIndexes
operator|=
name|getRangeIndexes
argument_list|(
name|xconf
argument_list|)
expr_stmt|;
comment|//read Triggers from xconf
name|triggers
operator|=
name|getTriggers
argument_list|(
name|xconf
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Returns an array of the Range Indexes 	 *  	 * @return Array of Range Indexes 	 */
specifier|public
name|RangeIndex
index|[]
name|getRangeIndexes
parameter_list|()
block|{
return|return
name|rangeIndexes
return|;
block|}
comment|/** 	 * Returns n specific Range Index 	 *  	 * @param index	The numeric index of the Range Index to return 	 *  	 * @return The Range Index 	 */
specifier|public
name|RangeIndex
name|getRangeIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|rangeIndexes
index|[
name|index
index|]
return|;
block|}
comment|/** 	 * Returns the number of Range Indexes defined 	 *   	 * @return The number of Range indexes 	 */
specifier|public
name|int
name|getRangeIndexCount
parameter_list|()
block|{
if|if
condition|(
name|rangeIndexes
operator|!=
literal|null
condition|)
block|{
return|return
name|rangeIndexes
operator|.
name|length
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** 	 * Delete a Range Index 	 *  	 * @param index	The numeric index of the Range Index to delete 	 */
specifier|public
name|void
name|deleteRangeIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
comment|//can only remove an index which is in the array
if|if
condition|(
name|index
operator|<
name|rangeIndexes
operator|.
name|length
condition|)
block|{
name|hasChanged
operator|=
literal|true
expr_stmt|;
comment|//if its the last item in the array just null the array
if|if
condition|(
name|rangeIndexes
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|rangeIndexes
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|//else remove the item at index from the array
name|RangeIndex
name|newRangeIndexes
index|[]
init|=
operator|new
name|RangeIndex
index|[
name|rangeIndexes
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|int
name|x
init|=
literal|0
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
name|rangeIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
name|index
condition|)
block|{
name|newRangeIndexes
index|[
name|x
index|]
operator|=
name|rangeIndexes
index|[
name|i
index|]
expr_stmt|;
name|x
operator|++
expr_stmt|;
block|}
block|}
name|rangeIndexes
operator|=
name|newRangeIndexes
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Update the details of a Range Index 	 * 	 * @param index		The numeric index of the range index to update 	 * @param type		The type of the index, either {@link #TYPE_PATH} or {@link #TYPE_QNAME} 	 * @param XPath		The new XPath, or null to just set the type 	 * @param xsType	The new type of the path, a valid xs:type, or just null to set the path 	 */
specifier|public
name|void
name|updateRangeIndex
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|XPath
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
name|hasChanged
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|rangeIndexes
index|[
name|index
index|]
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|XPath
operator|!=
literal|null
condition|)
block|{
name|rangeIndexes
index|[
name|index
index|]
operator|.
name|setXPath
argument_list|(
name|XPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xsType
operator|!=
literal|null
condition|)
block|{
name|rangeIndexes
index|[
name|index
index|]
operator|.
name|setxsType
argument_list|(
name|xsType
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Add a Range Index 	 * 	 * @param type		The type of the index, either {@link #TYPE_PATH} or {@link #TYPE_QNAME} 	 * @param XPath		The XPath to index 	 * @param xsType	The type of the path, a valid xs:type 	 */
specifier|public
name|void
name|addRangeIndex
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|XPath
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
name|hasChanged
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|rangeIndexes
operator|==
literal|null
condition|)
block|{
name|rangeIndexes
operator|=
operator|new
name|RangeIndex
index|[
literal|1
index|]
expr_stmt|;
name|rangeIndexes
index|[
literal|0
index|]
operator|=
operator|new
name|RangeIndex
argument_list|(
name|type
argument_list|,
name|XPath
argument_list|,
name|xsType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RangeIndex
name|newRangeIndexes
index|[]
init|=
operator|new
name|RangeIndex
index|[
name|rangeIndexes
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|rangeIndexes
argument_list|,
literal|0
argument_list|,
name|newRangeIndexes
argument_list|,
literal|0
argument_list|,
name|rangeIndexes
operator|.
name|length
argument_list|)
expr_stmt|;
name|newRangeIndexes
index|[
name|rangeIndexes
operator|.
name|length
index|]
operator|=
operator|new
name|RangeIndex
argument_list|(
name|type
argument_list|,
name|XPath
argument_list|,
name|xsType
argument_list|)
expr_stmt|;
name|rangeIndexes
operator|=
name|newRangeIndexes
expr_stmt|;
block|}
block|}
comment|/** 	 * Returns an array of Triggers 	 *  	 * @return Array of Range Indexes 	 */
specifier|public
name|Trigger
index|[]
name|getTriggers
parameter_list|()
block|{
return|return
name|triggers
return|;
block|}
comment|/** 	 * Returns n specific Trigger 	 *  	 * @param index	The numeric index of the Trigger to return 	 *  	 * @return The Trigger 	 */
specifier|public
name|Trigger
name|getTrigger
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|triggers
index|[
name|index
index|]
return|;
block|}
comment|/** 	 * Returns the number of Triggers defined 	 *   	 * @return The number of Triggers 	 */
specifier|public
name|int
name|getTriggerCount
parameter_list|()
block|{
if|if
condition|(
name|triggers
operator|!=
literal|null
condition|)
block|{
return|return
name|triggers
operator|.
name|length
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** 	 * Delete a Trigger 	 *  	 * @param index	The numeric index of the Trigger to delete 	 */
specifier|public
name|void
name|deleteTrigger
parameter_list|(
name|int
name|index
parameter_list|)
block|{
comment|//can only remove an index which is in the array
if|if
condition|(
name|index
operator|<
name|triggers
operator|.
name|length
condition|)
block|{
name|hasChanged
operator|=
literal|true
expr_stmt|;
comment|//if its the last item in the array just null the array
if|if
condition|(
name|triggers
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|triggers
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|//else remove the item at index from the array
name|Trigger
name|newTriggers
index|[]
init|=
operator|new
name|Trigger
index|[
name|triggers
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|int
name|x
init|=
literal|0
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
name|triggers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
name|index
condition|)
block|{
name|newTriggers
index|[
name|x
index|]
operator|=
name|triggers
index|[
name|i
index|]
expr_stmt|;
name|x
operator|++
expr_stmt|;
block|}
block|}
name|triggers
operator|=
name|newTriggers
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Update the details of a Trigger 	 * 	 * @param index		The numeric index of the range index to update 	 * @param triggerClass	The name of the new class for the trigger 	 * @param parameters The parameters to the trigger 	 *  	 */
specifier|public
name|void
name|updateTrigger
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|triggerClass
parameter_list|,
name|Properties
name|parameters
parameter_list|)
block|{
comment|//TODO: finish this!!! - need to add code for parameters
name|hasChanged
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|triggerClass
operator|!=
literal|null
condition|)
block|{
name|triggers
index|[
name|index
index|]
operator|.
name|setTriggerClass
argument_list|(
name|triggerClass
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Add a Trigger 	 * 	 * @param triggerClass The class for the Trigger 	 * @param parameters Parameters to pass to trigger 	 *  	 */
specifier|public
name|void
name|addTrigger
parameter_list|(
name|String
name|triggerClass
parameter_list|,
name|Properties
name|parameters
parameter_list|)
block|{
comment|//TODO: finish this!!! seee updateTrigger
name|hasChanged
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|triggers
operator|==
literal|null
condition|)
block|{
name|triggers
operator|=
operator|new
name|Trigger
index|[
literal|1
index|]
expr_stmt|;
name|triggers
index|[
literal|0
index|]
operator|=
operator|new
name|Trigger
argument_list|(
name|triggerClass
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Trigger
name|newTriggers
index|[]
init|=
operator|new
name|Trigger
index|[
name|triggers
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|triggers
argument_list|,
literal|0
argument_list|,
name|newTriggers
argument_list|,
literal|0
argument_list|,
name|triggers
operator|.
name|length
argument_list|)
expr_stmt|;
name|newTriggers
index|[
name|triggers
operator|.
name|length
index|]
operator|=
operator|new
name|Trigger
argument_list|(
name|triggerClass
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
name|triggers
operator|=
name|newTriggers
expr_stmt|;
block|}
block|}
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getCustomNamespaces
parameter_list|(
name|Element
name|xconf
parameter_list|)
block|{
specifier|final
name|NamedNodeMap
name|attrs
init|=
name|xconf
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
comment|//there will always be one attribute - the default namespace
if|if
condition|(
name|attrs
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
block|{
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
init|=
operator|new
name|LinkedHashMap
argument_list|<>
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
specifier|final
name|Node
name|a
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
name|a
operator|.
name|getNodeName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"xmlns:"
argument_list|)
condition|)
block|{
specifier|final
name|String
name|namespaceLocalName
init|=
name|a
operator|.
name|getNodeName
argument_list|()
operator|.
name|substring
argument_list|(
name|a
operator|.
name|getNodeName
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|namespaces
operator|.
name|put
argument_list|(
name|namespaceLocalName
argument_list|,
name|a
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|namespaces
return|;
block|}
return|return
literal|null
return|;
block|}
comment|//given the root element of collection.xconf it will return an array of range indexes
specifier|private
name|RangeIndex
index|[]
name|getRangeIndexes
parameter_list|(
name|Element
name|xconf
parameter_list|)
block|{
specifier|final
name|NodeList
name|nlRangeIndexes
init|=
name|xconf
operator|.
name|getElementsByTagName
argument_list|(
literal|"create"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nlRangeIndexes
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|List
argument_list|<
name|RangeIndex
argument_list|>
name|rl
init|=
operator|new
name|ArrayList
argument_list|<
name|RangeIndex
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
name|nlRangeIndexes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Element
name|rangeIndex
init|=
operator|(
name|Element
operator|)
name|nlRangeIndexes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|rangeIndex
operator|.
name|hasAttribute
argument_list|(
literal|"type"
argument_list|)
condition|)
block|{
if|if
condition|(
name|rangeIndex
operator|.
name|hasAttribute
argument_list|(
literal|"qname"
argument_list|)
condition|)
block|{
name|rl
operator|.
name|add
argument_list|(
operator|new
name|RangeIndex
argument_list|(
name|TYPE_QNAME
argument_list|,
name|rangeIndex
operator|.
name|getAttribute
argument_list|(
literal|"qname"
argument_list|)
argument_list|,
name|rangeIndex
operator|.
name|getAttribute
argument_list|(
literal|"type"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rl
operator|.
name|add
argument_list|(
operator|new
name|RangeIndex
argument_list|(
name|TYPE_PATH
argument_list|,
name|rangeIndex
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
argument_list|,
name|rangeIndex
operator|.
name|getAttribute
argument_list|(
literal|"type"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|RangeIndex
index|[]
name|rangeIndexes
init|=
operator|new
name|RangeIndex
index|[
name|rl
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|rangeIndexes
operator|=
name|rl
operator|.
name|toArray
argument_list|(
name|rangeIndexes
argument_list|)
expr_stmt|;
return|return
name|rangeIndexes
return|;
block|}
return|return
literal|null
return|;
block|}
comment|//given the root element of collection.xconf it will return an array of triggers
specifier|private
name|Trigger
index|[]
name|getTriggers
parameter_list|(
name|Element
name|xconf
parameter_list|)
block|{
specifier|final
name|NodeList
name|nlTriggers
init|=
name|xconf
operator|.
name|getElementsByTagName
argument_list|(
literal|"trigger"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nlTriggers
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|Trigger
index|[]
name|triggers
init|=
operator|new
name|Trigger
index|[
name|nlTriggers
operator|.
name|getLength
argument_list|()
index|]
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
name|nlTriggers
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Element
name|trigger
init|=
operator|(
name|Element
operator|)
name|nlTriggers
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|parameters
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|NodeList
name|nlTriggerParameters
init|=
name|trigger
operator|.
name|getElementsByTagName
argument_list|(
literal|"parameter"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nlTriggerParameters
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|nlTriggerParameters
operator|.
name|getLength
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
specifier|final
name|Element
name|parameter
init|=
operator|(
name|Element
operator|)
name|nlTriggerParameters
operator|.
name|item
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|parameters
operator|.
name|setProperty
argument_list|(
name|parameter
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|parameter
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//create the trigger
name|triggers
index|[
name|i
index|]
operator|=
operator|new
name|Trigger
argument_list|(
name|trigger
operator|.
name|getAttribute
argument_list|(
literal|"class"
argument_list|)
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
return|return
name|triggers
return|;
block|}
return|return
literal|null
return|;
block|}
comment|//has the collection.xconf been modified?
comment|/** 	 * Indicates whether the collection configuration has changed 	 *  	 * @return true if the configuration has changed, false otherwise 	 */
specifier|public
name|boolean
name|hasChanged
parameter_list|()
block|{
return|return
name|hasChanged
return|;
block|}
comment|//produces a string of XML describing the collection.xconf
specifier|private
name|String
name|toXMLString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|xconf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|xconf
operator|.
name|append
argument_list|(
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|customNamespaces
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|customNamespaces
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|xconf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
specifier|final
name|String
name|namespaceLocalName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|namespaceURL
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|xconf
operator|.
name|append
argument_list|(
literal|"xmlns:"
argument_list|)
operator|.
name|append
argument_list|(
name|namespaceLocalName
argument_list|)
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
operator|.
name|append
argument_list|(
name|namespaceURL
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
block|}
name|xconf
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|xconf
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
comment|//index
if|if
condition|(
name|rangeIndexes
operator|!=
literal|null
condition|)
block|{
name|xconf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|xconf
operator|.
name|append
argument_list|(
literal|"<index>"
argument_list|)
expr_stmt|;
name|xconf
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
comment|//range indexes
if|if
condition|(
name|rangeIndexes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|RangeIndex
name|rangeIndex
range|:
name|rangeIndexes
control|)
block|{
name|xconf
operator|.
name|append
argument_list|(
literal|"\t\t\t"
argument_list|)
expr_stmt|;
name|xconf
operator|.
name|append
argument_list|(
name|rangeIndex
operator|.
name|toXMLString
argument_list|()
argument_list|)
expr_stmt|;
name|xconf
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
block|}
name|xconf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|xconf
operator|.
name|append
argument_list|(
literal|"</index>"
argument_list|)
expr_stmt|;
name|xconf
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
comment|//triggers
if|if
condition|(
name|triggers
operator|!=
literal|null
condition|)
block|{
name|xconf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|xconf
operator|.
name|append
argument_list|(
literal|"<triggers>"
argument_list|)
expr_stmt|;
for|for
control|(
name|Trigger
name|trigger
range|:
name|triggers
control|)
block|{
name|xconf
operator|.
name|append
argument_list|(
literal|"\t\t\t"
argument_list|)
expr_stmt|;
name|xconf
operator|.
name|append
argument_list|(
name|trigger
operator|.
name|toXMLString
argument_list|()
argument_list|)
expr_stmt|;
name|xconf
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
name|xconf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|xconf
operator|.
name|append
argument_list|(
literal|"</triggers>"
argument_list|)
expr_stmt|;
name|xconf
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
name|xconf
operator|.
name|append
argument_list|(
literal|"</collection>"
argument_list|)
expr_stmt|;
return|return
name|xconf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Saves the collection configuation back to the collection.xconf 	 *  	 * @return true if the save succeeds, false otherwise 	 */
specifier|public
name|boolean
name|Save
parameter_list|()
block|{
try|try
block|{
comment|//is there an existing config file?
if|if
condition|(
name|resConfig
operator|==
literal|null
condition|)
block|{
comment|//no
comment|//is there an existing configuration collection?
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
comment|//no
name|client
operator|.
name|process
argument_list|(
literal|"mkcol "
operator|+
name|path
argument_list|)
expr_stmt|;
name|collection
operator|=
name|client
operator|.
name|getCollection
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|resConfig
operator|=
name|collection
operator|.
name|createResource
argument_list|(
name|CollectionConfigurationManager
operator|.
name|COLLECTION_CONFIG_FILENAME
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
block|}
comment|//set the content of the collection.xconf
name|resConfig
operator|.
name|setContent
argument_list|(
name|toXMLString
argument_list|()
argument_list|)
expr_stmt|;
comment|//store the collection.xconf
name|collection
operator|.
name|storeResource
argument_list|(
name|resConfig
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xmldbe
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/** 	 * Represents a Range Index in the collection.xconf 	 */
specifier|protected
class|class
name|RangeIndex
block|{
specifier|private
name|String
name|type
init|=
name|TYPE_QNAME
decl_stmt|;
specifier|private
name|String
name|XPath
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|xsType
init|=
literal|null
decl_stmt|;
comment|/** 		 * Constructor 		 * 		 * @param type type of the index, either "qname" or "path" 		 * @param XPath		The XPath to create a range index on 		 * @param xsType	The data type pointed to by the XPath as an xs:type  		 */
name|RangeIndex
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|XPath
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|XPath
operator|=
name|XPath
expr_stmt|;
name|this
operator|.
name|xsType
operator|=
name|xsType
expr_stmt|;
block|}
specifier|public
name|String
name|getXPath
parameter_list|()
block|{
return|return
operator|(
name|XPath
operator|)
return|;
block|}
specifier|public
name|String
name|getxsType
parameter_list|()
block|{
return|return
operator|(
name|xsType
operator|)
return|;
block|}
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setXPath
parameter_list|(
name|String
name|XPath
parameter_list|)
block|{
name|this
operator|.
name|XPath
operator|=
name|XPath
expr_stmt|;
block|}
specifier|public
name|void
name|setxsType
parameter_list|(
name|String
name|xsType
parameter_list|)
block|{
name|this
operator|.
name|xsType
operator|=
name|xsType
expr_stmt|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|//produces a collection.xconf suitable string of XML describing the range index
specifier|protected
name|String
name|toXMLString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|range
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|TYPE_PATH
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|range
operator|.
name|append
argument_list|(
literal|"<create path=\""
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|range
operator|.
name|append
argument_list|(
literal|"<create qname=\""
argument_list|)
expr_stmt|;
block|}
name|range
operator|.
name|append
argument_list|(
name|XPath
argument_list|)
expr_stmt|;
name|range
operator|.
name|append
argument_list|(
literal|"\" type=\""
argument_list|)
expr_stmt|;
name|range
operator|.
name|append
argument_list|(
name|xsType
argument_list|)
expr_stmt|;
name|range
operator|.
name|append
argument_list|(
literal|"\"/>"
argument_list|)
expr_stmt|;
return|return
name|range
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** 	 * Represents a Trigger in the collection.xconf 	 */
specifier|protected
specifier|static
class|class
name|Trigger
block|{
specifier|private
name|String
name|triggerClass
init|=
literal|null
decl_stmt|;
specifier|private
name|Properties
name|parameters
init|=
literal|null
decl_stmt|;
comment|/** 		 * Constructor 		 *  		 * @param triggerClass				The fully qualified java class name of the trigger 		 * @param parameters				Properties describing any name=value parameters for the trigger 		 */
name|Trigger
parameter_list|(
specifier|final
name|String
name|triggerClass
parameter_list|,
specifier|final
name|Properties
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|triggerClass
operator|=
name|triggerClass
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
specifier|public
name|String
name|getTriggerClass
parameter_list|()
block|{
return|return
name|triggerClass
return|;
block|}
specifier|public
name|void
name|setTriggerClass
parameter_list|(
name|String
name|triggerClass
parameter_list|)
block|{
name|this
operator|.
name|triggerClass
operator|=
name|triggerClass
expr_stmt|;
block|}
comment|//produces a collection.xconf suitable string of XML describing the trigger
specifier|protected
name|String
name|toXMLString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|trigger
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|triggerClass
argument_list|)
condition|)
block|{
name|trigger
operator|.
name|append
argument_list|(
literal|"<trigger class=\""
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|append
argument_list|(
name|triggerClass
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
comment|//parameters if any
if|if
condition|(
name|parameters
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|parameters
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|Enumeration
name|pKeys
init|=
name|parameters
operator|.
name|keys
argument_list|()
decl_stmt|;
while|while
condition|(
name|pKeys
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|String
name|name
init|=
operator|(
name|String
operator|)
name|pKeys
operator|.
name|nextElement
argument_list|()
decl_stmt|;
specifier|final
name|String
name|value
init|=
name|parameters
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|trigger
operator|.
name|append
argument_list|(
literal|"<parameter name=\""
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|append
argument_list|(
literal|"\" value=\""
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|append
argument_list|(
literal|"\"/>"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|trigger
operator|.
name|append
argument_list|(
literal|"</trigger>"
argument_list|)
expr_stmt|;
block|}
return|return
name|trigger
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

