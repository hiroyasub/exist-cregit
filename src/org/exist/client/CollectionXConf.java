begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
comment|/**  * @author adam  *   * */
end_comment

begin_comment
comment|//class to represent a collection.xconf
end_comment

begin_class
specifier|public
class|class
name|CollectionXConf
block|{
specifier|private
name|String
name|path
init|=
literal|null
decl_stmt|;
specifier|private
name|FullTextIndex
name|fulltextIndex
init|=
literal|null
decl_stmt|;
specifier|private
name|RangeIndex
index|[]
name|rangeIndexes
init|=
literal|null
decl_stmt|;
specifier|private
name|QNameIndex
index|[]
name|qnameIndexes
decl_stmt|;
specifier|private
name|Trigger
index|[]
name|triggers
init|=
literal|null
decl_stmt|;
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
name|path
operator|=
name|DBBroker
operator|.
name|CONFIG_COLLECTION
operator|+
name|CollectionName
expr_stmt|;
name|Collection
name|collection
init|=
name|client
operator|.
name|getCollection
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
comment|//if no config collection for this collection exists, just return
return|return;
comment|//get the resource from the db
name|Resource
name|resConfig
init|=
name|collection
operator|.
name|getResource
argument_list|(
name|DBBroker
operator|.
name|COLLECTION_CONFIG_FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|resConfig
operator|==
literal|null
condition|)
comment|//if, no config file exists for that collection, just return
return|return;
comment|//Parse the configuration file into a DOM
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|ParserConfigurationException
name|pce
parameter_list|)
block|{
comment|//TODO: do something here, throw xmldbexception?
block|}
catch|catch
parameter_list|(
name|SAXException
name|se
parameter_list|)
block|{
comment|//TODO: do something here, throw xmldbexception?
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//TODO: do something here, throw xmldbexception?
block|}
comment|//Get the root of the collection.xconf
name|Element
name|xconf
init|=
name|docConfig
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
comment|//Read FullText Index from xconf
name|fulltextIndex
operator|=
name|getFullTextIndex
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
comment|//Read QName Indexes from xconf
name|qnameIndexes
operator|=
name|getQNameIndexes
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
specifier|public
name|boolean
name|getFullTextIndexDefaultAll
parameter_list|()
block|{
return|return
name|fulltextIndex
operator|!=
literal|null
condition|?
name|fulltextIndex
operator|.
name|getDefaultAll
argument_list|()
else|:
literal|false
return|;
block|}
specifier|public
name|boolean
name|getFullTextIndexAttributes
parameter_list|()
block|{
return|return
name|fulltextIndex
operator|!=
literal|null
condition|?
name|fulltextIndex
operator|.
name|getAttributes
argument_list|()
else|:
literal|false
return|;
block|}
specifier|public
name|boolean
name|getFullTextIndexAlphanum
parameter_list|()
block|{
return|return
name|fulltextIndex
operator|!=
literal|null
condition|?
name|fulltextIndex
operator|.
name|getAlphanum
argument_list|()
else|:
literal|false
return|;
block|}
specifier|public
name|String
name|getFullTextIndexPath
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|fulltextIndex
operator|.
name|getXPath
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|String
name|getFullTextIndexPathAction
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|fulltextIndex
operator|.
name|getAction
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|int
name|getFullTextPathCount
parameter_list|()
block|{
if|if
condition|(
name|fulltextIndex
operator|!=
literal|null
condition|)
block|{
return|return
name|fulltextIndex
operator|.
name|getLength
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
specifier|public
name|void
name|addFullTextIndex
parameter_list|(
name|String
name|XPath
parameter_list|,
name|String
name|action
parameter_list|)
block|{
name|fulltextIndex
operator|.
name|addIndex
argument_list|(
name|XPath
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|updateFullTextIndex
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|XPath
parameter_list|,
name|String
name|action
parameter_list|)
block|{
if|if
condition|(
name|XPath
operator|!=
literal|null
condition|)
name|fulltextIndex
operator|.
name|setXPath
argument_list|(
name|index
argument_list|,
name|XPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
name|fulltextIndex
operator|.
name|setAction
argument_list|(
name|index
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deleteFullTextIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|fulltextIndex
operator|.
name|deleteIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|void
name|updateRangeIndex
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|XPath
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
if|if
condition|(
name|XPath
operator|!=
literal|null
condition|)
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
if|if
condition|(
name|xsType
operator|!=
literal|null
condition|)
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
specifier|public
name|void
name|addRangeIndex
parameter_list|(
name|String
name|XPath
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
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
specifier|public
name|QNameIndex
index|[]
name|getQNameIndexes
parameter_list|()
block|{
return|return
name|qnameIndexes
return|;
block|}
specifier|public
name|QNameIndex
name|getQNameIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|qnameIndexes
index|[
name|index
index|]
return|;
block|}
specifier|public
name|int
name|getQNameIndexCount
parameter_list|()
block|{
if|if
condition|(
name|qnameIndexes
operator|!=
literal|null
condition|)
block|{
return|return
name|qnameIndexes
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
specifier|public
name|void
name|deleteQNameIndex
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
name|qnameIndexes
operator|.
name|length
condition|)
block|{
comment|//if its the last item in the array just null the array
if|if
condition|(
name|qnameIndexes
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|qnameIndexes
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|QNameIndex
name|newQNameIndexes
index|[]
init|=
operator|new
name|QNameIndex
index|[
name|qnameIndexes
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
name|qnameIndexes
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
name|newQNameIndexes
index|[
name|x
index|]
operator|=
name|qnameIndexes
index|[
name|i
index|]
expr_stmt|;
name|x
operator|++
expr_stmt|;
block|}
block|}
name|qnameIndexes
operator|=
name|newQNameIndexes
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|updateQNameIndex
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|QName
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
if|if
condition|(
name|QName
operator|!=
literal|null
condition|)
name|qnameIndexes
index|[
name|index
index|]
operator|.
name|setQName
argument_list|(
name|QName
argument_list|)
expr_stmt|;
if|if
condition|(
name|xsType
operator|!=
literal|null
condition|)
name|qnameIndexes
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
specifier|public
name|void
name|addQNameIndex
parameter_list|(
name|String
name|QName
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
if|if
condition|(
name|qnameIndexes
operator|==
literal|null
condition|)
block|{
name|qnameIndexes
operator|=
operator|new
name|QNameIndex
index|[
literal|1
index|]
expr_stmt|;
name|qnameIndexes
index|[
literal|0
index|]
operator|=
operator|new
name|QNameIndex
argument_list|(
name|QName
argument_list|,
name|xsType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|QNameIndex
name|newQNameIndexes
index|[]
init|=
operator|new
name|QNameIndex
index|[
name|qnameIndexes
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
name|qnameIndexes
argument_list|,
literal|0
argument_list|,
name|newQNameIndexes
argument_list|,
literal|0
argument_list|,
name|qnameIndexes
operator|.
name|length
argument_list|)
expr_stmt|;
name|newQNameIndexes
index|[
name|qnameIndexes
operator|.
name|length
index|]
operator|=
operator|new
name|QNameIndex
argument_list|(
name|QName
argument_list|,
name|xsType
argument_list|)
expr_stmt|;
name|qnameIndexes
operator|=
name|newQNameIndexes
expr_stmt|;
block|}
block|}
comment|//given the root element of collection.xconf it will return the fulltext index
specifier|private
name|FullTextIndex
name|getFullTextIndex
parameter_list|(
name|Element
name|xconf
parameter_list|)
block|{
name|NodeList
name|nlFullTextIndex
init|=
name|xconf
operator|.
name|getElementsByTagName
argument_list|(
literal|"fulltext"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nlFullTextIndex
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|boolean
name|defaultAll
init|=
literal|true
decl_stmt|;
name|boolean
name|attributes
init|=
literal|false
decl_stmt|;
name|boolean
name|alphanum
init|=
literal|false
decl_stmt|;
name|FullTextIndexPath
index|[]
name|paths
init|=
literal|null
decl_stmt|;
name|Element
name|elemFullTextIndex
init|=
operator|(
name|Element
operator|)
name|nlFullTextIndex
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|defaultAll
operator|=
name|elemFullTextIndex
operator|.
name|getAttribute
argument_list|(
literal|"default"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"all"
argument_list|)
expr_stmt|;
name|attributes
operator|=
name|elemFullTextIndex
operator|.
name|getAttribute
argument_list|(
literal|"attributes"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
name|alphanum
operator|=
name|elemFullTextIndex
operator|.
name|getAttribute
argument_list|(
literal|"alphanum"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
name|NodeList
name|nlInclude
init|=
name|elemFullTextIndex
operator|.
name|getElementsByTagName
argument_list|(
literal|"include"
argument_list|)
decl_stmt|;
name|NodeList
name|nlExclude
init|=
name|elemFullTextIndex
operator|.
name|getElementsByTagName
argument_list|(
literal|"exclude"
argument_list|)
decl_stmt|;
name|int
name|iPaths
init|=
name|nlInclude
operator|.
name|getLength
argument_list|()
operator|+
name|nlExclude
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|iPaths
operator|>
literal|0
condition|)
block|{
name|paths
operator|=
operator|new
name|FullTextIndexPath
index|[
name|iPaths
index|]
expr_stmt|;
if|if
condition|(
name|nlInclude
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
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nlInclude
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
operator|new
name|FullTextIndexPath
argument_list|(
operator|(
operator|(
name|Element
operator|)
name|nlInclude
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
argument_list|,
name|FullTextIndexPath
operator|.
name|ACTION_INCLUDE
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nlExclude
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
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nlExclude
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
operator|new
name|FullTextIndexPath
argument_list|(
operator|(
operator|(
name|Element
operator|)
name|nlExclude
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
argument_list|,
name|FullTextIndexPath
operator|.
name|ACTION_EXCLUDE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|FullTextIndex
argument_list|(
name|defaultAll
argument_list|,
name|attributes
argument_list|,
name|alphanum
argument_list|,
name|paths
argument_list|)
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
name|Vector
name|vecRangeIndexes
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
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
comment|//is it a range index or a qname index
if|if
condition|(
name|rangeIndex
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|vecRangeIndexes
operator|.
name|add
argument_list|(
operator|new
name|RangeIndex
argument_list|(
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
name|RangeIndex
index|[]
name|rangeIndexes
init|=
operator|new
name|RangeIndex
index|[
name|vecRangeIndexes
operator|.
name|size
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
name|vecRangeIndexes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|rangeIndexes
index|[
name|i
index|]
operator|=
operator|(
name|RangeIndex
operator|)
name|vecRangeIndexes
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|rangeIndexes
return|;
block|}
return|return
literal|null
return|;
block|}
comment|//given the root element of collection.xconf it will return an array of qname indexes
specifier|private
name|QNameIndex
index|[]
name|getQNameIndexes
parameter_list|(
name|Element
name|xconf
parameter_list|)
block|{
name|Vector
name|vecQNameIndexes
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|NodeList
name|nlQNameIndexes
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
name|nlQNameIndexes
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
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nlQNameIndexes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|qnameIndex
init|=
operator|(
name|Element
operator|)
name|nlQNameIndexes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//is it a range index or a qname index
if|if
condition|(
name|qnameIndex
operator|.
name|getAttribute
argument_list|(
literal|"qname"
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|vecQNameIndexes
operator|.
name|add
argument_list|(
operator|new
name|QNameIndex
argument_list|(
name|qnameIndex
operator|.
name|getAttribute
argument_list|(
literal|"qname"
argument_list|)
argument_list|,
name|qnameIndex
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
name|QNameIndex
index|[]
name|qnameIndexes
init|=
operator|new
name|QNameIndex
index|[
name|vecQNameIndexes
operator|.
name|size
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
name|vecQNameIndexes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|qnameIndexes
index|[
name|i
index|]
operator|=
operator|(
name|QNameIndex
operator|)
name|vecQNameIndexes
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|qnameIndexes
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
name|Properties
name|parameters
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
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
literal|"event"
argument_list|)
argument_list|,
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
comment|//represents a path in the fulltext index in the collection.xconf
specifier|protected
class|class
name|FullTextIndexPath
block|{
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_INCLUDE
init|=
literal|"include"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ACTION_EXCLUDE
init|=
literal|"exclude"
decl_stmt|;
specifier|private
name|String
name|xpath
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|action
init|=
literal|null
decl_stmt|;
name|FullTextIndexPath
parameter_list|(
name|String
name|xpath
parameter_list|,
name|String
name|action
parameter_list|)
block|{
name|this
operator|.
name|xpath
operator|=
name|xpath
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
specifier|public
name|String
name|getXPath
parameter_list|()
block|{
return|return
name|xpath
return|;
block|}
specifier|public
name|String
name|getAction
parameter_list|()
block|{
return|return
name|action
return|;
block|}
specifier|public
name|void
name|setXPath
parameter_list|(
name|String
name|xpath
parameter_list|)
block|{
name|this
operator|.
name|xpath
operator|=
name|xpath
expr_stmt|;
block|}
specifier|public
name|void
name|setAction
parameter_list|(
name|String
name|action
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
block|}
comment|//represents the fulltext index in the collection.xconf
specifier|protected
class|class
name|FullTextIndex
block|{
name|boolean
name|defaultAll
init|=
literal|true
decl_stmt|;
name|boolean
name|attributes
init|=
literal|false
decl_stmt|;
name|boolean
name|alphanum
init|=
literal|false
decl_stmt|;
name|FullTextIndexPath
index|[]
name|xpaths
init|=
literal|null
decl_stmt|;
name|FullTextIndex
parameter_list|(
name|boolean
name|defaultAll
parameter_list|,
name|boolean
name|attributes
parameter_list|,
name|boolean
name|alphanum
parameter_list|,
name|FullTextIndexPath
index|[]
name|xpaths
parameter_list|)
block|{
name|this
operator|.
name|defaultAll
operator|=
name|defaultAll
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
name|this
operator|.
name|alphanum
operator|=
name|alphanum
expr_stmt|;
name|this
operator|.
name|xpaths
operator|=
name|xpaths
expr_stmt|;
block|}
specifier|public
name|boolean
name|getDefaultAll
parameter_list|()
block|{
return|return
name|defaultAll
return|;
block|}
specifier|public
name|boolean
name|getAttributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
specifier|public
name|boolean
name|getAlphanum
parameter_list|()
block|{
return|return
name|alphanum
return|;
block|}
specifier|public
name|String
name|getXPath
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|xpaths
index|[
name|index
index|]
operator|.
name|getXPath
argument_list|()
return|;
block|}
specifier|public
name|String
name|getAction
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|xpaths
index|[
name|index
index|]
operator|.
name|getAction
argument_list|()
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|xpaths
operator|!=
literal|null
condition|?
name|xpaths
operator|.
name|length
else|:
literal|0
return|;
block|}
specifier|public
name|void
name|setXPath
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|XPath
parameter_list|)
block|{
name|xpaths
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
specifier|public
name|void
name|setAction
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|action
parameter_list|)
block|{
name|xpaths
index|[
name|index
index|]
operator|.
name|setAction
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addIndex
parameter_list|(
name|String
name|XPath
parameter_list|,
name|String
name|action
parameter_list|)
block|{
if|if
condition|(
name|xpaths
operator|==
literal|null
condition|)
block|{
name|xpaths
operator|=
operator|new
name|FullTextIndexPath
index|[
literal|1
index|]
expr_stmt|;
name|xpaths
index|[
literal|0
index|]
operator|=
operator|new
name|FullTextIndexPath
argument_list|(
name|XPath
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FullTextIndexPath
name|newxpaths
index|[]
init|=
operator|new
name|FullTextIndexPath
index|[
name|xpaths
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
name|xpaths
argument_list|,
literal|0
argument_list|,
name|newxpaths
argument_list|,
literal|0
argument_list|,
name|xpaths
operator|.
name|length
argument_list|)
expr_stmt|;
name|newxpaths
index|[
name|xpaths
operator|.
name|length
index|]
operator|=
operator|new
name|FullTextIndexPath
argument_list|(
name|XPath
argument_list|,
name|action
argument_list|)
expr_stmt|;
name|xpaths
operator|=
name|newxpaths
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|deleteIndex
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
name|xpaths
operator|.
name|length
condition|)
block|{
comment|//if its the last item in the array just null the array
if|if
condition|(
name|xpaths
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|xpaths
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|//else remove the item at index from the array
name|FullTextIndexPath
name|newxpaths
index|[]
init|=
operator|new
name|FullTextIndexPath
index|[
name|xpaths
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
name|xpaths
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
name|newxpaths
index|[
name|x
index|]
operator|=
name|xpaths
index|[
name|i
index|]
expr_stmt|;
name|x
operator|++
expr_stmt|;
block|}
block|}
name|xpaths
operator|=
name|newxpaths
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//represents a range index in the collection.xconf
specifier|protected
class|class
name|RangeIndex
block|{
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
name|RangeIndex
parameter_list|(
name|String
name|XPath
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
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
block|}
comment|//represents a qname index in the collection.xconf
specifier|protected
class|class
name|QNameIndex
block|{
specifier|private
name|String
name|QName
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|xsType
init|=
literal|null
decl_stmt|;
name|QNameIndex
parameter_list|(
name|String
name|QName
parameter_list|,
name|String
name|xsType
parameter_list|)
block|{
name|this
operator|.
name|QName
operator|=
name|QName
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
name|getQName
parameter_list|()
block|{
return|return
operator|(
name|QName
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
name|void
name|setQName
parameter_list|(
name|String
name|QName
parameter_list|)
block|{
name|this
operator|.
name|QName
operator|=
name|QName
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
block|}
comment|//represents a Trigger in the collection.xconf
specifier|protected
class|class
name|Trigger
block|{
comment|/*public final static int EVENT_STORE_DOCUMENT = 1; 		public final static int EVENT_UPDATE_DOCUMENT = 2; 		public final static int EVENT_REMOVE_DOCUMENT = 3; 		public final static int EVENT_RENAME_COLLECTION = 4; 		public final static int EVENT_CREATE_COLLECTION = 5; 		 		private int triggerEvent = -1;*/
specifier|private
name|String
name|triggerEvent
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|triggerClass
init|=
literal|null
decl_stmt|;
name|Properties
name|parameters
init|=
literal|null
decl_stmt|;
name|Trigger
parameter_list|(
name|String
name|triggerEvent
parameter_list|,
name|String
name|triggerClass
parameter_list|,
name|Properties
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|triggerEvent
operator|=
name|triggerEvent
expr_stmt|;
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
block|}
block|}
end_class

end_unit

