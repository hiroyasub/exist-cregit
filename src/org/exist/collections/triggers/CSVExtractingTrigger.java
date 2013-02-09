begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
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
name|Collections
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
name|Stack
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
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

begin_comment
comment|/**  * Extracts CSV data from an element into a number of new child elements  *  * Mainly designed to be used at STORE event, but should also be usable at UPDATE event  *  * Example configuration -  *  *<collection xmlns="http://exist-db.org/collection-config/1.0">  *<triggers>  *<trigger event="store" class="org.exist.collections.triggers.CSVExtractingTrigger">  *  *<parameter name="separator" value="|"/>  *  *<parameter name="path">  *<xpath>/content/properties/value[@key eq "product_model"]</xpath>  *<extract index="0" element-name="product_name"/>  *<extract index="1" element-name="product_code"/>  *</parameter>  *  *</trigger>  *</triggers>  *</collection>  *  * Currently the configuration of this trigger only supports basic attribute predicates or a name eq value syntax.  *  *  * So for example, when storing a Document with content like the following -  *  *<content>  *<properties>  *<value key="product_model">SomeName|SomeCode12345</value>  *</properties>  *</content>  *  * The document will be translated at insertion time into -  *  *<content>  *<properties>  *<value key="product_model">  *<product_name>SomeName</product_name>  *<product_code>SomeCode12345</product_code>  *</value>  *</properties>  *</content>  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|CSVExtractingTrigger
extends|extends
name|FilteringTrigger
block|{
comment|//the separator characted for CSV files
specifier|private
name|String
name|separator
decl_stmt|;
comment|//key is the xpath to extract for, and value is the extractions to make from the value at that path
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Extraction
argument_list|>
name|extractions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Extraction
argument_list|>
argument_list|()
decl_stmt|;
comment|//the current node path of the SAX stream
specifier|private
name|NodePath
name|currentNodePath
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|capture
init|=
literal|false
decl_stmt|;
comment|//flag to indicate whether to buffer character data for extraction of csv values
specifier|private
name|StringBuilder
name|charactersBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|//buffer for character data, which will then be parsed to extract csv values
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
argument_list|>
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|TriggerException
block|{
name|super
operator|.
name|configure
argument_list|(
name|broker
argument_list|,
name|parent
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
comment|//get the separator
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|separators
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"separator"
argument_list|)
decl_stmt|;
if|if
condition|(
name|separators
operator|==
literal|null
operator|||
name|separators
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"A separator parameter must be provided to the CSVExtractingTrigger configuration"
argument_list|)
throw|;
block|}
else|else
block|{
name|this
operator|.
name|separator
operator|=
name|separators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|//get the extractions
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|>
argument_list|>
name|paths
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|>
argument_list|>
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|>
name|path
range|:
name|paths
control|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|xpaths
init|=
name|path
operator|.
name|get
argument_list|(
literal|"xpath"
argument_list|)
decl_stmt|;
if|if
condition|(
name|xpaths
operator|!=
literal|null
operator|&&
name|xpaths
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|String
name|xpath
init|=
name|xpaths
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//split out the path and preficate (if present) from the xpath
name|String
name|pathExpr
decl_stmt|;
name|String
name|attrPredicate
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|xpath
operator|.
name|indexOf
argument_list|(
literal|"["
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|pathExpr
operator|=
name|xpath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|xpath
operator|.
name|indexOf
argument_list|(
literal|"["
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|xpath
operator|.
name|indexOf
argument_list|(
literal|"[@"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|attrPredicate
operator|=
name|xpath
operator|.
name|substring
argument_list|(
name|xpath
operator|.
name|indexOf
argument_list|(
literal|"[@"
argument_list|)
operator|+
literal|2
argument_list|,
name|xpath
operator|.
name|indexOf
argument_list|(
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|pathExpr
operator|=
name|xpath
expr_stmt|;
block|}
name|Extraction
name|extraction
init|=
name|extractions
operator|.
name|get
argument_list|(
name|pathExpr
argument_list|)
decl_stmt|;
if|if
condition|(
name|extraction
operator|==
literal|null
condition|)
block|{
name|extraction
operator|=
operator|new
name|Extraction
argument_list|()
expr_stmt|;
if|if
condition|(
name|attrPredicate
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|attrNameValueMatch
index|[]
init|=
name|attrPredicate
operator|.
name|split
argument_list|(
literal|" eq "
argument_list|)
decl_stmt|;
name|extraction
operator|.
name|setMatchAttribute
argument_list|(
name|attrNameValueMatch
index|[
literal|0
index|]
argument_list|,
name|attrNameValueMatch
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|Properties
argument_list|>
name|extracts
init|=
name|path
operator|.
name|get
argument_list|(
literal|"extract"
argument_list|)
decl_stmt|;
if|if
condition|(
name|extracts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Properties
name|extract
range|:
name|extracts
control|)
block|{
specifier|final
name|ExtractEntry
name|extractEntry
init|=
operator|new
name|ExtractEntry
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|extract
operator|.
name|getProperty
argument_list|(
literal|"index"
argument_list|)
argument_list|)
argument_list|,
name|extract
operator|.
name|getProperty
argument_list|(
literal|"element-name"
argument_list|)
argument_list|)
decl_stmt|;
name|extraction
operator|.
name|getExtractEntries
argument_list|()
operator|.
name|add
argument_list|(
name|extractEntry
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|extraction
operator|.
name|getExtractEntries
argument_list|()
argument_list|)
expr_stmt|;
comment|//pre sort
name|extractions
operator|.
name|put
argument_list|(
name|pathExpr
argument_list|,
name|extraction
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|//skips nested elements or already extracted nodes (i.e. during update events)
comment|//TODO needs through testing during update phase
if|if
condition|(
name|capture
operator|==
literal|true
condition|)
block|{
name|capture
operator|=
literal|false
expr_stmt|;
name|charactersBuf
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|charactersBuf
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|currentNodePath
operator|.
name|add
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
expr_stmt|;
specifier|final
name|Extraction
name|extraction
init|=
name|extractions
operator|.
name|get
argument_list|(
name|currentNodePath
operator|.
name|toLocalPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|extraction
operator|!=
literal|null
condition|)
block|{
comment|//do we have to match an attribute predicate from the xpath in the trigger config?
if|if
condition|(
name|extraction
operator|.
name|mustMatchAttribute
argument_list|()
condition|)
block|{
comment|//yes - so try and match
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|extraction
operator|.
name|matchesAttribute
argument_list|(
name|attributes
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
comment|//matched the predicate, so staty capturing
name|capture
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
else|else
block|{
comment|//no, so start capturing
name|capture
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|capture
condition|)
block|{
name|charactersBuf
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|capture
condition|)
block|{
name|extractCSVValuesToElements
argument_list|()
expr_stmt|;
name|capture
operator|=
literal|false
expr_stmt|;
name|charactersBuf
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|charactersBuf
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|endElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qname
argument_list|)
expr_stmt|;
name|currentNodePath
operator|.
name|removeLast
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|extractCSVValuesToElements
parameter_list|()
throws|throws
name|SAXException
block|{
comment|//split the csv values
specifier|final
name|String
name|seperatedValues
index|[]
init|=
name|charactersBuf
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
name|getEscapedSeparatorForRegExp
argument_list|()
argument_list|)
decl_stmt|;
comment|//get the extractions for the current path
specifier|final
name|Extraction
name|extraction
init|=
name|extractions
operator|.
name|get
argument_list|(
name|currentNodePath
operator|.
name|toLocalPath
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|ExtractEntry
name|extractEntry
range|:
name|extraction
operator|.
name|getExtractEntries
argument_list|()
control|)
block|{
comment|//extract the value by index
specifier|final
name|int
name|index
init|=
name|extractEntry
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|<
name|seperatedValues
operator|.
name|length
condition|)
block|{
specifier|final
name|char
name|seperatedValue
index|[]
init|=
name|seperatedValues
index|[
name|index
index|]
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
comment|//create a new element for the extracted value
specifier|final
name|String
name|localName
init|=
name|extractEntry
operator|.
name|getElementName
argument_list|()
decl_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|,
name|localName
argument_list|,
name|localName
argument_list|,
operator|new
name|EmptyAttributes
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
name|seperatedValue
argument_list|,
literal|0
argument_list|,
name|seperatedValue
operator|.
name|length
argument_list|)
expr_stmt|;
name|super
operator|.
name|endElement
argument_list|(
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|,
name|localName
argument_list|,
name|localName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|String
name|getEscapedSeparatorForRegExp
parameter_list|()
block|{
if|if
condition|(
name|separator
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|//escape the separator character if it is a java regexp character
if|if
condition|(
literal|"|"
operator|.
name|equals
argument_list|(
name|separator
argument_list|)
operator|||
literal|","
operator|.
name|equals
argument_list|(
name|separator
argument_list|)
operator|||
literal|"$"
operator|.
name|equals
argument_list|(
name|separator
argument_list|)
operator|||
literal|"^"
operator|.
name|equals
argument_list|(
name|separator
argument_list|)
condition|)
block|{
return|return
literal|"\\"
operator|+
name|separator
return|;
block|}
block|}
return|return
name|separator
return|;
block|}
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
name|txn
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
name|txn
parameter_list|,
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
block|}
specifier|private
class|class
name|NodePath
block|{
specifier|private
name|Stack
argument_list|<
name|QName
argument_list|>
name|pathSegments
init|=
operator|new
name|Stack
argument_list|<
name|QName
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|add
parameter_list|(
name|String
name|namespaceUri
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
name|pathSegments
operator|.
name|push
argument_list|(
operator|new
name|QName
argument_list|(
name|namespaceUri
argument_list|,
name|localName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeLast
parameter_list|()
block|{
name|pathSegments
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|pathSegments
operator|.
name|size
argument_list|()
return|;
block|}
comment|//TODO replace with qname path once we understand how to pass in qnames in the xpath parameter to the trigger
specifier|public
name|String
name|toLocalPath
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|localPath
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|localPath
operator|.
name|append
argument_list|(
literal|"/"
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
name|pathSegments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|localPath
operator|.
name|append
argument_list|(
name|pathSegments
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|pathSegments
operator|.
name|size
argument_list|()
condition|)
block|{
name|localPath
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|localPath
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/*** configuration data classes ***/
specifier|private
class|class
name|Extraction
block|{
specifier|private
name|List
argument_list|<
name|ExtractEntry
argument_list|>
name|extractEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|ExtractEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|matchAttrName
decl_stmt|;
specifier|private
name|String
name|matchAttrValue
decl_stmt|;
specifier|public
name|List
argument_list|<
name|ExtractEntry
argument_list|>
name|getExtractEntries
parameter_list|()
block|{
return|return
name|extractEntries
return|;
block|}
specifier|public
name|void
name|setMatchAttribute
parameter_list|(
name|String
name|attrName
parameter_list|,
name|String
name|attrValue
parameter_list|)
block|{
name|this
operator|.
name|matchAttrName
operator|=
name|attrName
operator|.
name|trim
argument_list|()
expr_stmt|;
name|this
operator|.
name|matchAttrValue
operator|=
name|attrValue
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|""
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|mustMatchAttribute
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|matchAttrName
operator|!=
literal|null
operator|&&
name|this
operator|.
name|matchAttrValue
operator|!=
literal|null
operator|)
return|;
block|}
specifier|public
name|boolean
name|matchesAttribute
parameter_list|(
name|String
name|attrName
parameter_list|,
name|String
name|attrValue
parameter_list|)
block|{
comment|//if there is no matching then return true
if|if
condition|(
operator|!
name|mustMatchAttribute
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|matchAttrName
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
operator|&&
name|this
operator|.
name|matchAttrValue
operator|.
name|equals
argument_list|(
name|attrValue
argument_list|)
return|;
block|}
block|}
block|}
specifier|private
class|class
name|ExtractEntry
implements|implements
name|Comparable
argument_list|<
name|ExtractEntry
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
specifier|private
specifier|final
name|String
name|elementName
decl_stmt|;
specifier|public
name|ExtractEntry
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|elementName
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|elementName
operator|=
name|elementName
expr_stmt|;
block|}
specifier|public
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
specifier|public
name|String
name|getElementName
parameter_list|()
block|{
return|return
name|elementName
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|ExtractEntry
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|other
operator|.
name|getIndex
argument_list|()
operator|-
name|this
operator|.
name|getIndex
argument_list|()
return|;
block|}
block|}
block|}
specifier|private
class|class
name|EmptyAttributes
implements|implements
name|Attributes
block|{
annotation|@
name|Override
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getURI
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getQName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getType
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getValue
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getIndex
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getIndex
parameter_list|(
name|String
name|qName
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getType
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getType
parameter_list|(
name|String
name|qName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getValue
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getValue
parameter_list|(
name|String
name|qName
parameter_list|)
block|{
return|return
literal|null
return|;
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
name|txn
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
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
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
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
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
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
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
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
name|txn
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
name|txn
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
name|txn
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
name|txn
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
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
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
name|txn
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
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
throws|throws
name|TriggerException
block|{
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
throws|throws
name|TriggerException
block|{
block|}
block|}
end_class

end_unit

