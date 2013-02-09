begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-10 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xqj
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
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
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|NameTest
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
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|InMemoryNodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ElementImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentBuilderReceiver
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
name|QName
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQItemType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQException
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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_comment
comment|/**  * A utility class that provides marshalling services for external variables and methods   * to create DOM Nodes from streamed representation.  *   * @author Wolfgang Meier  *  */
end_comment

begin_class
specifier|public
class|class
name|Marshaller
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE
init|=
literal|"http://exist-db.org/xquery/types/serialized"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"sx"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Properties
name|OUTPUT_PROPERTIES
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALUE_ELEMENT
init|=
literal|"value"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALUE_ELEMENT_QNAME
init|=
name|PREFIX
operator|+
literal|":value"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|VALUE_QNAME
init|=
operator|new
name|QName
argument_list|(
name|VALUE_ELEMENT
argument_list|,
name|NAMESPACE
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|SEQ_ELEMENT
init|=
literal|"sequence"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|SEQ_ELEMENT_QNAME
init|=
name|PREFIX
operator|+
literal|":sequence"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ATTR_TYPE
init|=
literal|"type"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ATTR_ITEM_TYPE
init|=
literal|"item-type"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|ROOT_ELEMENT_QNAME
init|=
operator|new
name|QName
argument_list|(
name|SEQ_ELEMENT
argument_list|,
name|NAMESPACE
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
comment|/**      * Marshall a sequence in an xml based string representation      * @param broker      * @param seq Sequence to be marshalled      * @param handler Content handler for building the resulting string      * @throws XPathException      * @throws SAXException      */
specifier|public
specifier|static
name|void
name|marshall
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Sequence
name|seq
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
specifier|final
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
name|ATTR_ITEM_TYPE
argument_list|,
name|ATTR_ITEM_TYPE
argument_list|,
literal|"CDATA"
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|seq
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
name|SEQ_ELEMENT
argument_list|,
name|SEQ_ELEMENT_QNAME
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|marshallItem
argument_list|(
name|broker
argument_list|,
name|i
operator|.
name|nextItem
argument_list|()
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
name|SEQ_ELEMENT
argument_list|,
name|SEQ_ELEMENT_QNAME
argument_list|)
expr_stmt|;
block|}
comment|/**      * Marshall the items of a sequence in  an xml based string representation      * @param broker      * @param seq Sequence which items are to be marshalled      * @param start index of first item to be marshalled      * @param howmany number of items following and including the first to be marshalled      * @param handler      * @throws XPathException      * @throws SAXException      */
specifier|public
specifier|static
name|void
name|marshall
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Sequence
name|seq
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
specifier|final
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
name|ATTR_ITEM_TYPE
argument_list|,
name|ATTR_ITEM_TYPE
argument_list|,
literal|"CDATA"
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|seq
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
name|SEQ_ELEMENT
argument_list|,
name|SEQ_ELEMENT_QNAME
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|howmany
operator|&&
name|i
operator|<
name|seq
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|marshallItem
argument_list|(
name|broker
argument_list|,
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
name|SEQ_ELEMENT
argument_list|,
name|SEQ_ELEMENT_QNAME
argument_list|)
expr_stmt|;
block|}
comment|/**      * Marshall an item in an xml based string representation      * @param broker      * @param item Sequence(or Item) to me marshalled      * @param handler      * @throws SAXException      * @throws XPathException      */
specifier|public
specifier|static
name|void
name|marshallItem
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Item
name|item
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
specifier|final
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|int
name|type
init|=
name|item
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|NODE
condition|)
block|{
name|type
operator|=
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getNode
argument_list|()
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
block|}
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
name|ATTR_TYPE
argument_list|,
name|ATTR_TYPE
argument_list|,
literal|"CDATA"
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
name|VALUE_ELEMENT
argument_list|,
name|VALUE_ELEMENT_QNAME
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
specifier|final
name|NodeValue
name|nv
init|=
operator|(
name|NodeValue
operator|)
name|item
decl_stmt|;
name|nv
operator|.
name|toSAX
argument_list|(
name|broker
argument_list|,
name|handler
argument_list|,
name|OUTPUT_PROPERTIES
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
name|VALUE_ELEMENT
argument_list|,
name|VALUE_ELEMENT_QNAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
name|VALUE_ELEMENT
argument_list|,
name|VALUE_ELEMENT_QNAME
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
specifier|final
name|String
name|value
init|=
name|item
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
name|VALUE_ELEMENT
argument_list|,
name|VALUE_ELEMENT_QNAME
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|Sequence
name|demarshall
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|XPathException
block|{
specifier|final
name|XMLInputFactory
name|factory
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_NAMESPACE_AWARE
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_VALIDATING
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
specifier|final
name|XMLStreamReader
name|parser
init|=
name|factory
operator|.
name|createXMLStreamReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
name|demarshall
argument_list|(
name|broker
argument_list|,
name|parser
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Sequence
name|demarshall
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Node
name|n
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|XPathException
block|{
specifier|final
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|n
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|XMLInputFactory
name|factory
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_NAMESPACE_AWARE
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_VALIDATING
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
specifier|final
name|XMLStreamReader
name|parser
init|=
name|factory
operator|.
name|createXMLStreamReader
argument_list|(
name|source
argument_list|)
decl_stmt|;
return|return
name|demarshall
argument_list|(
name|broker
argument_list|,
name|parser
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Sequence
name|demarshall
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|XPathException
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
name|event
operator|!=
name|XMLStreamConstants
operator|.
name|START_ELEMENT
condition|)
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"Root element is not in the correct namespace. Expected: "
operator|+
name|NAMESPACE
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|SEQ_ELEMENT
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"Root element should be a "
operator|+
name|SEQ_ELEMENT_QNAME
argument_list|)
throw|;
block|}
specifier|final
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
operator|)
operator|!=
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
condition|)
block|{
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
if|if
condition|(
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
name|VALUE_ELEMENT
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|typeName
init|=
literal|null
decl_stmt|;
comment|// scan through attributes instead of direct lookup to work around issue in xerces
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ATTR_TYPE
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|typeName
operator|=
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|typeName
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|type
init|=
name|Type
operator|.
name|getType
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
name|Item
name|item
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|item
operator|=
name|streamToDOM
argument_list|(
name|type
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|item
operator|=
operator|new
name|StringValue
argument_list|(
name|parser
operator|.
name|getElementText
argument_list|()
argument_list|)
operator|.
name|convertTo
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
name|SEQ_ELEMENT
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|result
return|;
block|}
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|Sequence
name|demarshall
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|XPathException
block|{
if|if
condition|(
operator|!
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
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"Root element is not in the correct namespace. Expected: "
operator|+
name|NAMESPACE
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|SEQ_ELEMENT
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
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"Root element should be a "
operator|+
name|SEQ_ELEMENT_QNAME
argument_list|)
throw|;
block|}
specifier|final
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|final
name|InMemoryNodeSet
name|values
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
decl_stmt|;
name|node
operator|.
name|selectChildren
argument_list|(
operator|new
name|NameTest
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|VALUE_QNAME
argument_list|)
argument_list|,
name|values
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|values
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|ElementImpl
name|child
init|=
operator|(
name|ElementImpl
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
specifier|final
name|String
name|typeName
init|=
name|child
operator|.
name|getAttribute
argument_list|(
name|ATTR_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeName
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|type
init|=
name|Type
operator|.
name|getType
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
name|Item
name|item
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|item
operator|=
operator|(
name|Item
operator|)
name|child
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DOCUMENT
condition|)
block|{
specifier|final
name|NodeImpl
name|n
init|=
operator|(
name|NodeImpl
operator|)
name|item
decl_stmt|;
specifier|final
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|()
decl_stmt|;
try|try
block|{
name|receiver
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|n
operator|.
name|getDocument
argument_list|()
operator|.
name|copyTo
argument_list|(
name|n
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Error while demarshalling node: "
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
name|item
operator|=
operator|(
name|Item
operator|)
name|receiver
operator|.
name|getDocument
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|StringBuilder
name|data
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Node
name|txt
init|=
name|child
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|txt
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|txt
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|||
name|txt
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
operator|)
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"sx:value should only contain text if type is "
operator|+
name|typeName
argument_list|)
throw|;
block|}
name|data
operator|.
name|append
argument_list|(
name|txt
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|txt
operator|=
name|txt
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
name|item
operator|=
operator|new
name|StringValue
argument_list|(
name|data
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|convertTo
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|Item
name|streamToDOM
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|XQException
block|{
if|if
condition|(
name|type
operator|.
name|getBaseType
argument_list|()
operator|==
name|XQItemType
operator|.
name|XQITEMKIND_DOCUMENT_ELEMENT
operator|||
name|type
operator|.
name|getBaseType
argument_list|()
operator|==
name|XQItemType
operator|.
name|XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT
condition|)
block|{
return|return
name|streamToDOM
argument_list|(
name|Type
operator|.
name|DOCUMENT
argument_list|,
name|parser
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|streamToDOM
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|parser
argument_list|)
return|;
block|}
block|}
comment|/**      * Creates an Item from a streamed representation      * @param parser Parser to read xml elements from      * @return item      * @throws XMLStreamException      */
specifier|public
specifier|static
name|Item
name|streamToDOM
parameter_list|(
name|int
name|rootType
parameter_list|,
name|XMLStreamReader
name|parser
parameter_list|)
throws|throws
name|XMLStreamException
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|int
name|event
decl_stmt|;
name|boolean
name|finish
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|(
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
operator|)
operator|!=
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
condition|)
block|{
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
specifier|final
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|qn
init|=
name|parser
operator|.
name|getAttributeName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|qn
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|qn
operator|.
name|getPrefix
argument_list|()
operator|+
literal|':'
operator|+
name|qn
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|parser
operator|.
name|getAttributeType
argument_list|(
name|i
argument_list|)
argument_list|,
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|QName
operator|.
name|fromJavaQName
argument_list|(
name|parser
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
comment|//                    for (int i = 0; i< parser.getNamespaceCount(); i++) {
comment|//                        builder.namespaceNode(parser.getNamespacePrefix(i), parser.getNamespaceURI(i));
comment|//                    }
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
name|VALUE_ELEMENT
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|finish
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|builder
operator|.
name|characters
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|finish
condition|)
block|{
break|break;
block|}
block|}
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|rootType
operator|==
name|Type
operator|.
name|DOCUMENT
condition|)
block|{
return|return
name|builder
operator|.
name|getDocument
argument_list|()
return|;
block|}
if|else if
condition|(
name|rootType
operator|==
name|Type
operator|.
name|ELEMENT
condition|)
block|{
return|return
operator|(
name|NodeImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|(
name|NodeImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getFirstChild
argument_list|()
return|;
block|}
block|}
comment|/**      * Creates an Item from a streamed representation      * @param reader      * @return item      * @throws XMLStreamException      */
specifier|public
specifier|static
name|Item
name|streamToDOM
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|XQException
block|{
specifier|final
name|XMLInputFactory
name|factory
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_NAMESPACE_AWARE
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_VALIDATING
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
specifier|final
name|XMLStreamReader
name|parser
init|=
name|factory
operator|.
name|createXMLStreamReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
name|streamToDOM
argument_list|(
name|parser
argument_list|,
name|type
argument_list|)
return|;
block|}
comment|/**      * Creates a node from a string representation      * @param content      * @return node      * @throws XMLStreamException      */
specifier|public
specifier|static
name|Node
name|streamToNode
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|XMLStreamException
block|{
specifier|final
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
decl_stmt|;
return|return
name|streamToNode
argument_list|(
name|reader
argument_list|)
return|;
block|}
comment|/**      * Creates a node from a streamed representation      * @param reader      * @return item      * @throws XMLStreamException      */
specifier|public
specifier|static
name|Node
name|streamToNode
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|XMLStreamException
block|{
specifier|final
name|XMLInputFactory
name|factory
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_NAMESPACE_AWARE
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setProperty
argument_list|(
name|XMLInputFactory
operator|.
name|IS_VALIDATING
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
specifier|final
name|XMLStreamReader
name|parser
init|=
name|factory
operator|.
name|createXMLStreamReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|int
name|event
decl_stmt|;
name|boolean
name|finish
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|(
name|event
operator|=
name|parser
operator|.
name|next
argument_list|()
operator|)
operator|!=
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
condition|)
block|{
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
specifier|final
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
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
name|parser
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|qn
init|=
name|parser
operator|.
name|getAttributeName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|qn
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|qn
operator|.
name|getPrefix
argument_list|()
operator|+
literal|':'
operator|+
name|qn
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|parser
operator|.
name|getAttributeType
argument_list|(
name|i
argument_list|)
argument_list|,
name|parser
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|QName
operator|.
name|fromJavaQName
argument_list|(
name|parser
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
comment|//                    for (int i = 0; i< parser.getNamespaceCount(); i++) {
comment|//                        builder.namespaceNode(parser.getNamespacePrefix(i), parser.getNamespaceURI(i));
comment|//                    }
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
name|VALUE_ELEMENT
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|finish
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|builder
operator|.
name|characters
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|finish
condition|)
block|{
break|break;
block|}
block|}
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
return|;
block|}
block|}
end_class

end_unit

