begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2007 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|stax
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
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
name|numbering
operator|.
name|NodeId
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
name|Signatures
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
name|btree
operator|.
name|Value
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
name|dom
operator|.
name|RawNodeIterator
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
name|ByteConversion
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
name|UTF8
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
name|XMLString
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
name|serializer
operator|.
name|AttrList
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
name|ProcessingInstruction
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
name|NamespaceContext
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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|Location
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
name|XMLStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Lazy implementation of a StAX {@link javax.xml.stream.XMLStreamReader}, which directly reads  * information from the persistent DOM. The class is optimized to support fast scanning of the DOM, where only  * a few selected node properties are requested. Node properties are extracted on demand. For example, the QName of  * an element will not be read unless {@link #getText()} is called.  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedXMLStreamReader
implements|implements
name|XMLStreamReader
block|{
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_NODE_ID
init|=
literal|"node-id"
decl_stmt|;
specifier|private
name|RawNodeIterator
name|iterator
decl_stmt|;
specifier|private
name|Value
name|current
init|=
literal|null
decl_stmt|;
specifier|private
name|Value
name|previous
init|=
literal|null
decl_stmt|;
specifier|private
name|Stack
name|elementStack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
name|int
name|state
init|=
name|START_DOCUMENT
decl_stmt|;
specifier|private
name|DocumentImpl
name|document
decl_stmt|;
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
specifier|private
name|QName
name|qname
init|=
literal|null
decl_stmt|;
specifier|private
name|XMLString
name|text
init|=
operator|new
name|XMLString
argument_list|(
literal|256
argument_list|)
decl_stmt|;
specifier|private
name|AttrList
name|attributes
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|reportAttribs
init|=
literal|false
decl_stmt|;
comment|/**      * Construct an EmbeddedXMLStreamReader.      *      * @param doc the document to which the start node belongs.      * @param iterator a RawNodeIterator positioned on the start node.      * @param reportAttributes if set to true, attributes will be reported as top-level events.      * @throws XMLStreamException      */
specifier|public
name|EmbeddedXMLStreamReader
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|RawNodeIterator
name|iterator
parameter_list|,
name|boolean
name|reportAttributes
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|this
operator|.
name|document
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
name|this
operator|.
name|reportAttribs
operator|=
name|reportAttributes
expr_stmt|;
block|}
comment|/**      * Reposition the stream reader to another start node, maybe in a different document.      *      * @param node the new start node.      * @param reportAttributes if set to true, attributes will be reported as top-level events.      * @throws IOException      */
specifier|public
name|void
name|reposition
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|boolean
name|reportAttributes
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|current
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|previous
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|elementStack
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|START_DOCUMENT
expr_stmt|;
name|this
operator|.
name|reportAttribs
operator|=
name|reportAttributes
expr_stmt|;
name|this
operator|.
name|document
operator|=
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|iterator
operator|.
name|seek
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reposition the stream reader to another start node, maybe in a different document.      *      * @param proxy the new start node.      * @param reportAttributes if set to true, attributes will be reported as top-level events.      * @throws IOException      */
specifier|public
name|void
name|reposition
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|,
name|boolean
name|reportAttributes
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|current
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|previous
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|elementStack
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|START_DOCUMENT
expr_stmt|;
name|this
operator|.
name|reportAttribs
operator|=
name|reportAttributes
expr_stmt|;
name|this
operator|.
name|document
operator|=
operator|(
name|DocumentImpl
operator|)
name|proxy
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|iterator
operator|.
name|seek
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initNode
parameter_list|()
block|{
specifier|final
name|short
name|type
init|=
name|Signatures
operator|.
name|getType
argument_list|(
name|current
operator|.
name|data
argument_list|()
index|[
name|current
operator|.
name|start
argument_list|()
index|]
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|state
operator|=
name|START_ELEMENT
expr_stmt|;
name|elementStack
operator|.
name|push
argument_list|(
operator|new
name|ElementEvent
argument_list|(
name|current
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
name|state
operator|=
name|ATTRIBUTE
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
name|state
operator|=
name|CHARACTERS
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
name|state
operator|=
name|COMMENT
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
name|state
operator|=
name|CDATA
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
name|state
operator|=
name|PROCESSING_INSTRUCTION
expr_stmt|;
break|break;
block|}
name|reset
argument_list|()
expr_stmt|;
name|readNodeId
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|skipAttributes
parameter_list|()
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
comment|// attributes were not yet read. skip them...
specifier|final
name|ElementEvent
name|parent
init|=
operator|(
name|ElementEvent
operator|)
name|elementStack
operator|.
name|peek
argument_list|()
decl_stmt|;
specifier|final
name|int
name|attrs
init|=
name|getAttributeCount
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
condition|;
name|i
operator|++
control|)
block|{
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|parent
operator|.
name|incrementChild
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|readAttributes
parameter_list|()
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
specifier|final
name|ElementEvent
name|parent
init|=
operator|(
name|ElementEvent
operator|)
name|elementStack
operator|.
name|peek
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|getAttributeCount
argument_list|()
decl_stmt|;
name|attributes
operator|=
operator|new
name|AttrList
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Value
name|v
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|AttrImpl
operator|.
name|addToList
argument_list|(
name|document
operator|.
name|getBroker
argument_list|()
argument_list|,
name|v
operator|.
name|data
argument_list|()
argument_list|,
name|v
operator|.
name|start
argument_list|()
argument_list|,
name|v
operator|.
name|getLength
argument_list|()
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
name|parent
operator|.
name|incrementChild
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|readNodeId
parameter_list|()
block|{
name|int
name|offset
init|=
name|current
operator|.
name|start
argument_list|()
operator|+
operator|(
name|state
operator|==
name|START_ELEMENT
operator|||
name|state
operator|==
name|END_ELEMENT
condition|?
literal|5
else|:
literal|1
operator|)
decl_stmt|;
name|int
name|dlnLen
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|current
operator|.
name|data
argument_list|()
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|+=
literal|2
expr_stmt|;
name|nodeId
operator|=
name|document
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromData
argument_list|(
name|dlnLen
argument_list|,
name|current
operator|.
name|data
argument_list|()
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|next
parameter_list|()
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|state
operator|!=
name|END_ELEMENT
condition|)
name|previous
operator|=
name|current
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|START_ELEMENT
operator|&&
operator|!
name|reportAttribs
condition|)
name|skipAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|elementStack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ElementEvent
name|parent
init|=
operator|(
name|ElementEvent
operator|)
name|elementStack
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|getChildCount
argument_list|()
operator|==
name|parent
operator|.
name|getCurrentChild
argument_list|()
condition|)
block|{
name|elementStack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|state
operator|=
name|END_ELEMENT
expr_stmt|;
name|current
operator|=
name|parent
operator|.
name|data
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
return|return
name|state
return|;
block|}
else|else
block|{
name|parent
operator|.
name|incrementChild
argument_list|()
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|state
operator|!=
name|START_DOCUMENT
condition|)
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
name|current
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|initNode
argument_list|()
expr_stmt|;
return|return
name|state
return|;
block|}
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|nodeId
operator|=
literal|null
expr_stmt|;
name|qname
operator|=
literal|null
expr_stmt|;
name|attributes
operator|=
literal|null
expr_stmt|;
name|text
operator|.
name|reuse
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|require
parameter_list|(
name|int
name|i
parameter_list|,
name|String
name|string
parameter_list|,
name|String
name|string1
parameter_list|)
throws|throws
name|XMLStreamException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|String
name|getElementText
parameter_list|()
throws|throws
name|XMLStreamException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|Object
name|getProperty
parameter_list|(
name|String
name|string
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|string
operator|.
name|equals
argument_list|(
name|PROPERTY_NODE_ID
argument_list|)
condition|)
block|{
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
name|readNodeId
argument_list|()
expr_stmt|;
return|return
name|nodeId
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|nextTag
parameter_list|()
throws|throws
name|XMLStreamException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|XMLStreamException
block|{
return|return
name|state
operator|==
name|START_DOCUMENT
operator|||
operator|!
name|elementStack
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|XMLStreamException
block|{
name|iterator
operator|.
name|closeDocument
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStartElement
parameter_list|()
block|{
return|return
name|state
operator|==
name|START_ELEMENT
return|;
block|}
specifier|public
name|boolean
name|isEndElement
parameter_list|()
block|{
return|return
name|state
operator|==
name|END_ELEMENT
return|;
block|}
specifier|public
name|boolean
name|isCharacters
parameter_list|()
block|{
return|return
name|state
operator|==
name|CHARACTERS
return|;
block|}
specifier|public
name|boolean
name|isWhiteSpace
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|String
name|getAttributeValue
parameter_list|(
name|String
name|string
parameter_list|,
name|String
name|string1
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|getAttributeCount
parameter_list|()
block|{
name|int
name|offset
init|=
name|current
operator|.
name|start
argument_list|()
operator|+
literal|7
operator|+
name|nodeId
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|current
operator|.
name|data
argument_list|()
argument_list|,
name|offset
argument_list|)
return|;
block|}
specifier|public
name|QName
name|getAttributeName
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
name|readAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|>
name|attributes
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"index should be< "
operator|+
name|attributes
operator|.
name|getLength
argument_list|()
argument_list|)
throw|;
return|return
name|attributes
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|toJavaQName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getAttributeNamespace
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
name|readAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|>
name|attributes
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"index should be< "
operator|+
name|attributes
operator|.
name|getLength
argument_list|()
argument_list|)
throw|;
return|return
name|attributes
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
specifier|public
name|String
name|getAttributeLocalName
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
name|readAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|>
name|attributes
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"index should be< "
operator|+
name|attributes
operator|.
name|getLength
argument_list|()
argument_list|)
throw|;
return|return
name|attributes
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|getLocalName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getAttributePrefix
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
name|readAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|>
name|attributes
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"index should be< "
operator|+
name|attributes
operator|.
name|getLength
argument_list|()
argument_list|)
throw|;
return|return
name|attributes
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|getPrefix
argument_list|()
return|;
block|}
specifier|public
name|String
name|getAttributeType
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
name|readAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|>
name|attributes
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"index should be< "
operator|+
name|attributes
operator|.
name|getLength
argument_list|()
argument_list|)
throw|;
specifier|final
name|int
name|type
init|=
name|attributes
operator|.
name|getType
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|AttrImpl
operator|.
name|ID
condition|)
return|return
literal|"ID"
return|;
return|return
literal|"CDATA"
return|;
block|}
specifier|public
name|String
name|getAttributeValue
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
name|readAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|>
name|attributes
operator|.
name|getLength
argument_list|()
condition|)
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"index should be< "
operator|+
name|attributes
operator|.
name|getLength
argument_list|()
argument_list|)
throw|;
return|return
name|attributes
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isAttributeSpecified
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|getNamespaceCount
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|String
name|getNamespacePrefix
parameter_list|(
name|int
name|i
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|(
name|int
name|i
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|NamespaceContext
name|getNamespaceContext
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|getEventType
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
name|CHARACTERS
operator|||
name|state
operator|==
name|COMMENT
condition|)
block|{
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|CharacterDataImpl
operator|.
name|readData
argument_list|(
name|nodeId
argument_list|,
name|current
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
return|return
name|text
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|""
return|;
block|}
specifier|public
name|char
index|[]
name|getTextCharacters
parameter_list|()
block|{
name|String
name|s
init|=
name|getText
argument_list|()
decl_stmt|;
name|char
index|[]
name|dst
init|=
operator|new
name|char
index|[
name|s
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|s
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|dst
operator|.
name|length
argument_list|,
name|dst
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|dst
return|;
block|}
specifier|public
name|int
name|getTextCharacters
parameter_list|(
name|int
name|sourceStart
parameter_list|,
name|char
index|[]
name|chars
parameter_list|,
name|int
name|targetStart
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|XMLStreamException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|getTextStart
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|getTextLength
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|String
name|getEncoding
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|hasText
parameter_list|()
block|{
return|return
name|state
operator|==
name|CHARACTERS
operator|||
name|state
operator|==
name|COMMENT
return|;
block|}
specifier|public
name|Location
name|getLocation
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|QName
name|getName
parameter_list|()
block|{
if|if
condition|(
name|qname
operator|!=
literal|null
condition|)
return|return
name|qname
return|;
if|if
condition|(
name|state
operator|==
name|START_ELEMENT
operator|||
name|state
operator|==
name|END_ELEMENT
condition|)
block|{
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
name|readNodeId
argument_list|()
expr_stmt|;
name|qname
operator|=
name|ElementImpl
operator|.
name|readQName
argument_list|(
name|current
argument_list|,
name|document
argument_list|,
name|nodeId
argument_list|)
operator|.
name|toJavaQName
argument_list|()
expr_stmt|;
block|}
return|return
name|qname
return|;
block|}
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|.
name|getPrefix
argument_list|()
return|;
block|}
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|hasName
parameter_list|()
block|{
return|return
operator|(
name|state
operator|==
name|START_ELEMENT
operator|||
name|state
operator|==
name|END_ELEMENT
operator|)
return|;
block|}
comment|/**      * Deserialize the node at the current position of the cursor and return      * it as a {@link org.exist.dom.StoredNode}.      *      * @return the node at the current position.      */
specifier|public
name|StoredNode
name|getNode
parameter_list|()
block|{
name|StoredNode
name|node
init|=
name|StoredNode
operator|.
name|deserialize
argument_list|(
name|current
operator|.
name|data
argument_list|()
argument_list|,
name|current
operator|.
name|start
argument_list|()
argument_list|,
name|current
operator|.
name|getLength
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|node
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|node
operator|.
name|setInternalAddress
argument_list|(
name|current
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
comment|/**      * Returns the last node in document sequence that occurs before the      * current node. Usually used to find the last child before an END_ELEMENT      * event.      *      * @return the last node in document sequence before the current node      */
specifier|public
name|StoredNode
name|getPreviousNode
parameter_list|()
block|{
name|StoredNode
name|node
init|=
name|StoredNode
operator|.
name|deserialize
argument_list|(
name|previous
operator|.
name|data
argument_list|()
argument_list|,
name|previous
operator|.
name|start
argument_list|()
argument_list|,
name|previous
operator|.
name|getLength
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|node
operator|.
name|setOwnerDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|node
operator|.
name|setInternalAddress
argument_list|(
name|previous
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
comment|/**      * Returns the (internal) address of the node at the cursor's current      * position.      *       * @return      */
specifier|public
name|long
name|getCurrentPosition
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|currentAddress
argument_list|()
return|;
block|}
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"1.0"
return|;
block|}
specifier|public
name|boolean
name|isStandalone
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|standaloneSet
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|String
name|getCharacterEncodingScheme
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getPITarget
parameter_list|()
block|{
name|readPI
argument_list|()
expr_stmt|;
return|return
name|qname
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
specifier|public
name|String
name|getPIData
parameter_list|()
block|{
name|readPI
argument_list|()
expr_stmt|;
return|return
name|text
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|readPI
parameter_list|()
block|{
if|if
condition|(
name|qname
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|state
operator|!=
name|PROCESSING_INSTRUCTION
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at a processing instruction"
argument_list|)
throw|;
name|ProcessingInstruction
name|pi
init|=
operator|(
name|ProcessingInstruction
operator|)
name|StoredNode
operator|.
name|deserialize
argument_list|(
name|current
operator|.
name|data
argument_list|()
argument_list|,
name|current
operator|.
name|start
argument_list|()
argument_list|,
name|current
operator|.
name|getLength
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|qname
operator|=
operator|new
name|QName
argument_list|(
literal|""
argument_list|,
name|pi
operator|.
name|getTarget
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|text
operator|.
name|append
argument_list|(
name|pi
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ElementEvent
block|{
specifier|private
name|Value
name|data
decl_stmt|;
specifier|private
name|int
name|childCount
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|currentChild
init|=
literal|0
decl_stmt|;
specifier|public
name|ElementEvent
parameter_list|(
name|Value
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|childCount
operator|=
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|data
operator|.
name|data
argument_list|()
argument_list|,
name|data
operator|.
name|start
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Value
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
return|return
name|childCount
return|;
block|}
specifier|public
name|int
name|getCurrentChild
parameter_list|()
block|{
return|return
name|currentChild
return|;
block|}
specifier|public
name|void
name|setCurrentChild
parameter_list|(
name|int
name|currentChild
parameter_list|)
block|{
name|this
operator|.
name|currentChild
operator|=
name|currentChild
expr_stmt|;
block|}
specifier|public
name|void
name|incrementChild
parameter_list|()
block|{
name|currentChild
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

