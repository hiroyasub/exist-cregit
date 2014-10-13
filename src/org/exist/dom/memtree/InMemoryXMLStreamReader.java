begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-14 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
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
name|QName
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
name|stax
operator|.
name|ExtendedXMLStreamReader
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

begin_comment
comment|/**  * Implementation of a StAX {@link javax.xml.stream.XMLStreamReader}, which wraps around eXist's in-memory DOM. This class complements {@link  * org.exist.stax.EmbeddedXMLStreamReader} which reads persistent documents.  */
end_comment

begin_class
specifier|public
class|class
name|InMemoryXMLStreamReader
implements|implements
name|ExtendedXMLStreamReader
block|{
specifier|private
specifier|final
name|DocumentImpl
name|doc
decl_stmt|;
specifier|private
specifier|final
name|NodeImpl
name|rootNode
decl_stmt|;
specifier|private
name|int
name|currentNode
decl_stmt|;
specifier|private
name|int
name|previous
decl_stmt|;
specifier|private
name|int
name|state
init|=
name|XMLStreamReader
operator|.
name|START_DOCUMENT
decl_stmt|;
specifier|public
name|InMemoryXMLStreamReader
parameter_list|(
specifier|final
name|DocumentImpl
name|doc
parameter_list|,
specifier|final
name|NodeImpl
name|node
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|rootNode
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|currentNode
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getProperty
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|PROPERTY_NODE_ID
argument_list|)
condition|)
block|{
if|if
condition|(
name|currentNode
operator|<
literal|0
operator|||
name|currentNode
operator|>=
name|doc
operator|.
name|size
condition|)
block|{
return|return
literal|null
return|;
block|}
name|doc
operator|.
name|expand
argument_list|()
expr_stmt|;
return|return
name|doc
operator|.
name|nodeId
index|[
name|currentNode
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
name|XMLStreamReader
operator|.
name|END_ELEMENT
condition|)
block|{
name|previous
operator|=
name|currentNode
expr_stmt|;
block|}
if|if
condition|(
name|currentNode
operator|>
operator|-
literal|1
condition|)
block|{
name|int
name|next
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|state
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
operator|||
name|state
operator|==
name|XMLStreamReader
operator|.
name|START_DOCUMENT
condition|)
block|{
name|next
operator|=
name|doc
operator|.
name|getFirstChildFor
argument_list|(
name|currentNode
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|<
literal|0
condition|)
block|{
comment|// no child nodes
name|state
operator|=
name|XMLStreamReader
operator|.
name|END_ELEMENT
expr_stmt|;
return|return
name|state
return|;
block|}
block|}
if|if
condition|(
name|next
operator|<
literal|0
condition|)
block|{
name|next
operator|=
name|doc
operator|.
name|next
index|[
name|currentNode
index|]
expr_stmt|;
if|if
condition|(
name|next
operator|<
name|currentNode
condition|)
block|{
if|if
condition|(
name|next
operator|==
literal|0
condition|)
block|{
name|state
operator|=
name|XMLStreamReader
operator|.
name|END_DOCUMENT
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
name|XMLStreamReader
operator|.
name|END_ELEMENT
expr_stmt|;
block|}
name|currentNode
operator|=
name|next
expr_stmt|;
return|return
name|state
return|;
block|}
block|}
name|currentNode
operator|=
name|next
expr_stmt|;
block|}
else|else
block|{
name|currentNode
operator|=
name|rootNode
operator|.
name|getNodeNumber
argument_list|()
expr_stmt|;
block|}
switch|switch
condition|(
name|doc
operator|.
name|nodeKind
index|[
name|currentNode
index|]
condition|)
block|{
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
block|{
name|state
operator|=
name|XMLStreamReader
operator|.
name|CHARACTERS
expr_stmt|;
break|break;
block|}
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
block|{
name|state
operator|=
name|XMLStreamReader
operator|.
name|CDATA
expr_stmt|;
break|break;
block|}
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
block|{
name|state
operator|=
name|XMLStreamReader
operator|.
name|COMMENT
expr_stmt|;
break|break;
block|}
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
block|{
name|state
operator|=
name|XMLStreamReader
operator|.
name|PROCESSING_INSTRUCTION
expr_stmt|;
break|break;
block|}
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
block|{
name|state
operator|=
name|XMLStreamReader
operator|.
name|START_ELEMENT
expr_stmt|;
break|break;
block|}
block|}
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|require
parameter_list|(
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|)
throws|throws
name|XMLStreamException
block|{
block|}
annotation|@
name|Override
specifier|public
name|String
name|getElementText
parameter_list|()
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|getEventType
argument_list|()
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"parser must be on START_ELEMENT to read next text"
argument_list|)
throw|;
block|}
name|int
name|eventType
init|=
name|next
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|content
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|eventType
operator|!=
name|END_ELEMENT
condition|)
block|{
if|if
condition|(
name|eventType
operator|==
name|CHARACTERS
operator|||
name|eventType
operator|==
name|CDATA
operator|||
name|eventType
operator|==
name|SPACE
operator|||
name|eventType
operator|==
name|ENTITY_REFERENCE
condition|)
block|{
name|content
operator|.
name|append
argument_list|(
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|eventType
operator|==
name|PROCESSING_INSTRUCTION
operator|||
name|eventType
operator|==
name|COMMENT
condition|)
block|{
comment|// skipping
block|}
if|else if
condition|(
name|eventType
operator|==
name|END_DOCUMENT
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"unexpected end of document when reading element text content"
argument_list|)
throw|;
block|}
if|else if
condition|(
name|eventType
operator|==
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"element text content may not contain START_ELEMENT"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|XMLStreamException
argument_list|(
literal|"Unexpected event type "
operator|+
name|eventType
argument_list|)
throw|;
block|}
name|eventType
operator|=
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|content
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|XMLStreamException
block|{
return|return
name|currentNode
operator|!=
name|rootNode
operator|.
name|getNodeNumber
argument_list|()
operator|||
name|state
operator|==
name|XMLStreamReader
operator|.
name|START_DOCUMENT
operator|||
name|state
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|XMLStreamException
block|{
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStartElement
parameter_list|()
block|{
return|return
name|state
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEndElement
parameter_list|()
block|{
return|return
name|state
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCharacters
parameter_list|()
block|{
return|return
name|state
operator|==
name|XMLStreamReader
operator|.
name|CHARACTERS
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isWhiteSpace
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAttributeValue
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|)
block|{
specifier|final
name|int
name|attrCount
init|=
name|doc
operator|.
name|getAttributesCountFor
argument_list|(
name|currentNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|attrCount
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|attrStart
init|=
name|doc
operator|.
name|alpha
index|[
name|currentNode
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
name|attrCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|QName
name|qname
init|=
name|doc
operator|.
name|attrName
index|[
name|attrStart
operator|+
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|namespaceURI
operator|==
literal|null
operator|||
name|namespaceURI
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|)
operator|&&
name|localName
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|doc
operator|.
name|attrValue
index|[
name|attrStart
operator|+
name|i
index|]
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getAttributeCount
parameter_list|()
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
return|return
name|doc
operator|.
name|getAttributesCountFor
argument_list|(
name|currentNode
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|QName
name|getAttributeQName
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|>
name|getAttributeCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"bad attribute index"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|attr
init|=
name|doc
operator|.
name|alpha
index|[
name|currentNode
index|]
decl_stmt|;
return|return
name|doc
operator|.
name|attrName
index|[
name|attr
operator|+
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|getAttributeName
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
name|getAttributeQName
argument_list|(
name|index
argument_list|)
operator|.
name|toJavaQName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAttributeNamespace
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
return|return
name|getAttributeQName
argument_list|(
name|index
argument_list|)
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAttributeLocalName
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
return|return
name|getAttributeQName
argument_list|(
name|index
argument_list|)
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAttributePrefix
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
return|return
name|getAttributeQName
argument_list|(
name|index
argument_list|)
operator|.
name|getPrefix
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeId
name|getAttributeId
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|>
name|getAttributeCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"bad attribute index"
argument_list|)
throw|;
block|}
name|doc
operator|.
name|expand
argument_list|()
expr_stmt|;
specifier|final
name|int
name|attr
init|=
name|doc
operator|.
name|alpha
index|[
name|currentNode
index|]
decl_stmt|;
return|return
name|doc
operator|.
name|attrNodeId
index|[
name|attr
operator|+
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAttributeType
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|>
name|getAttributeCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"bad attribute index"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|attr
init|=
name|doc
operator|.
name|alpha
index|[
name|currentNode
index|]
decl_stmt|;
specifier|final
name|int
name|type
init|=
name|doc
operator|.
name|attrType
index|[
name|attr
operator|+
name|index
index|]
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|AttributeImpl
operator|.
name|ATTR_ID_TYPE
case|:
block|{
return|return
operator|(
literal|"ID"
operator|)
return|;
block|}
case|case
name|AttributeImpl
operator|.
name|ATTR_IDREF_TYPE
case|:
block|{
return|return
operator|(
literal|"IDREF"
operator|)
return|;
block|}
case|case
name|AttributeImpl
operator|.
name|ATTR_IDREFS_TYPE
case|:
block|{
return|return
operator|(
literal|"IDREFS"
operator|)
return|;
block|}
default|default:
block|{
return|return
operator|(
literal|"CDATA"
operator|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAttributeValue
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|>
name|getAttributeCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"bad attribute index"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|attr
init|=
name|doc
operator|.
name|alpha
index|[
name|currentNode
index|]
decl_stmt|;
return|return
name|doc
operator|.
name|attrValue
index|[
name|attr
operator|+
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAttributeSpecified
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getNamespaceCount
parameter_list|()
block|{
if|if
condition|(
name|state
operator|!=
name|START_ELEMENT
operator|&&
name|state
operator|!=
name|END_ELEMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
return|return
operator|(
name|doc
operator|.
name|getNamespacesCountFor
argument_list|(
name|currentNode
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespacePrefix
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|>
name|getNamespaceCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"bad namespace index"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|ns
init|=
name|doc
operator|.
name|alphaLen
index|[
name|currentNode
index|]
decl_stmt|;
specifier|final
name|QName
name|nsQName
init|=
name|doc
operator|.
name|namespaceCode
index|[
name|ns
operator|+
name|index
index|]
decl_stmt|;
return|return
name|nsQName
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|>
name|getNamespaceCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"bad namespace index"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|ns
init|=
name|doc
operator|.
name|alphaLen
index|[
name|currentNode
index|]
decl_stmt|;
specifier|final
name|QName
name|nsQName
init|=
name|doc
operator|.
name|namespaceCode
index|[
name|ns
operator|+
name|index
index|]
decl_stmt|;
return|return
name|nsQName
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|int
name|getEventType
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
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
operator|||
name|state
operator|==
name|CDATA
condition|)
block|{
return|return
operator|new
name|String
argument_list|(
name|doc
operator|.
name|characters
argument_list|,
name|doc
operator|.
name|alpha
index|[
name|currentNode
index|]
argument_list|,
name|doc
operator|.
name|alphaLen
index|[
name|currentNode
index|]
argument_list|)
return|;
block|}
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|char
index|[]
name|getTextCharacters
parameter_list|()
block|{
specifier|final
name|char
index|[]
name|ch
init|=
operator|new
name|char
index|[
name|doc
operator|.
name|alphaLen
index|[
name|currentNode
index|]
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|doc
operator|.
name|characters
argument_list|,
name|doc
operator|.
name|alpha
index|[
name|currentNode
index|]
argument_list|,
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|ch
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getTextCharacters
parameter_list|(
specifier|final
name|int
name|sourceStart
parameter_list|,
specifier|final
name|char
index|[]
name|target
parameter_list|,
specifier|final
name|int
name|targetStart
parameter_list|,
specifier|final
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
operator|||
name|state
operator|==
name|CDATA
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
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
return|return
name|doc
operator|.
name|nodeName
index|[
name|currentNode
index|]
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cursor is not at an element"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|getName
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|toJavaQName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasName
parameter_list|()
block|{
return|return
name|state
operator|==
name|START_ELEMENT
operator|||
name|state
operator|==
name|END_ELEMENT
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|getQName
argument_list|()
operator|.
name|getPrefix
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"1.0"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStandalone
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|standaloneSet
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCharacterEncodingScheme
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPITarget
parameter_list|()
block|{
specifier|final
name|QName
name|qn
init|=
name|doc
operator|.
name|nodeName
index|[
name|currentNode
index|]
decl_stmt|;
return|return
name|qn
operator|!=
literal|null
condition|?
name|qn
operator|.
name|getLocalPart
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPIData
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|doc
operator|.
name|characters
argument_list|,
name|doc
operator|.
name|alpha
index|[
name|currentNode
index|]
argument_list|,
name|doc
operator|.
name|alphaLen
index|[
name|currentNode
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

