begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
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
name|AttrImpl
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
name|dom
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
name|dom
operator|.
name|StoredNode
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
name|NodeProxy
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
name|EmbeddedXMLStreamReader
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
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|Receiver
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
name|XQuery
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
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|SequenceIterator
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
name|xacml
operator|.
name|AccessContext
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
name|Attr
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Patch a given source document by applying a diff in eXist's diff format.  */
end_comment

begin_class
specifier|public
class|class
name|Patch
block|{
specifier|private
specifier|final
specifier|static
name|String
name|D_START
init|=
literal|"start"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|D_END
init|=
literal|"end"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|D_BOTH
init|=
literal|"both"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|D_SUBTREE
init|=
literal|"subtree"
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|Map
name|deletedNodes
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
name|insertedNodes
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
name|appendedNodes
init|=
literal|null
decl_stmt|;
comment|/**      * Create a new Patch instance using the specified broker and diff document.      *      * @param broker the DBBroker to use      * @param diff the diff document to apply      *      * @throws XPathException      */
specifier|public
name|Patch
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|diff
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|parseDiff
argument_list|(
name|broker
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
comment|/**      * Apply the diff to the given source data stream passed as an XMLStreamReader. Write      * output to the specified receiver.      *      * @throws DiffException      */
specifier|public
name|void
name|patch
parameter_list|(
name|ExtendedXMLStreamReader
name|reader
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|DiffException
block|{
try|try
block|{
name|NodeId
name|skipSubtree
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
name|NodeId
name|nodeId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|EmbeddedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|XMLStreamReader
operator|.
name|END_ELEMENT
condition|)
block|{
name|ElementImpl
name|insertedNode
init|=
operator|(
name|ElementImpl
operator|)
name|insertedNodes
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|insertedNode
operator|!=
literal|null
condition|)
block|{
name|insertNode
argument_list|(
name|insertedNode
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ElementImpl
name|appendedNode
init|=
operator|(
name|ElementImpl
operator|)
name|appendedNodes
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appendedNode
operator|!=
literal|null
condition|)
block|{
name|insertNode
argument_list|(
name|appendedNode
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|opt
init|=
operator|(
name|String
operator|)
name|deletedNodes
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|==
name|D_SUBTREE
condition|)
block|{
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
condition|)
name|skipSubtree
operator|=
name|nodeId
expr_stmt|;
block|}
if|else if
condition|(
name|opt
operator|==
name|D_BOTH
condition|)
block|{
comment|//skip
block|}
if|else if
condition|(
name|opt
operator|==
name|D_END
operator|&&
name|status
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
condition|)
block|{
comment|// skip
block|}
if|else if
condition|(
name|opt
operator|==
name|D_START
operator|&&
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
condition|)
block|{
comment|// skip
block|}
if|else if
condition|(
name|skipSubtree
operator|==
literal|null
condition|)
name|copyNode
argument_list|(
name|reader
argument_list|,
name|receiver
argument_list|,
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
operator|&&
name|skipSubtree
operator|!=
literal|null
operator|&&
name|skipSubtree
operator|.
name|equals
argument_list|(
name|nodeId
argument_list|)
condition|)
name|skipSubtree
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiffException
argument_list|(
literal|"Caught exception while reading source document for patch: "
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiffException
argument_list|(
literal|"Caught exception while patching document: "
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
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiffException
argument_list|(
literal|"Caught exception while serializing patch output: "
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
block|}
specifier|private
name|void
name|insertNode
parameter_list|(
name|StoredNode
name|insertedNode
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|ExtendedXMLStreamReader
name|reader
init|=
name|broker
operator|.
name|newXMLStreamReader
argument_list|(
name|insertedNode
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|reader
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
operator|||
name|status
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
operator|)
operator|&&
name|XMLDiff
operator|.
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
condition|)
block|{
if|if
condition|(
literal|"attribute"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|int
name|attrCount
init|=
name|reader
operator|.
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
name|attrCount
condition|;
name|i
operator|++
control|)
block|{
name|QName
name|qname
init|=
name|reader
operator|.
name|getAttributeQName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|attribute
argument_list|(
name|qname
argument_list|,
name|reader
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
literal|"start"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|namespace
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"namespace"
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
name|QName
operator|.
name|extractLocalName
argument_list|(
name|name
argument_list|)
argument_list|,
name|namespace
argument_list|,
name|QName
operator|.
name|extractPrefix
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"end"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|namespace
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"namespace"
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|endElement
argument_list|(
operator|new
name|QName
argument_list|(
name|QName
operator|.
name|extractLocalName
argument_list|(
name|name
argument_list|)
argument_list|,
name|namespace
argument_list|,
name|QName
operator|.
name|extractPrefix
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|copyNode
argument_list|(
name|reader
argument_list|,
name|receiver
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|copyNode
parameter_list|(
name|ExtendedXMLStreamReader
name|reader
parameter_list|,
name|Receiver
name|receiver
parameter_list|,
name|int
name|status
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XMLStreamException
throws|,
name|IOException
block|{
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|XMLStreamReader
operator|.
name|START_ELEMENT
case|:
name|AttrList
name|attrs
init|=
operator|new
name|AttrList
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
name|reader
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// check if an attribute has to be inserted before the current attribute
name|NodeId
name|nodeId
init|=
name|reader
operator|.
name|getAttributeId
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// check if an attribute has to be inserted before the current attribute
name|ElementImpl
name|insertedNode
init|=
operator|(
name|ElementImpl
operator|)
name|insertedNodes
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|insertedNode
operator|!=
literal|null
condition|)
block|{
name|StoredNode
name|child
init|=
operator|(
name|StoredNode
operator|)
name|insertedNode
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
name|XMLDiff
operator|.
name|NAMESPACE
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
literal|"attribute"
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|NamedNodeMap
name|map
init|=
name|child
operator|.
name|getAttributes
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
name|map
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|AttrImpl
name|attr
init|=
operator|(
name|AttrImpl
operator|)
name|map
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|attr
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
name|attrs
operator|.
name|addAttribute
argument_list|(
name|attr
operator|.
name|getQName
argument_list|()
argument_list|,
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
name|attr
operator|.
name|getType
argument_list|()
argument_list|,
name|attr
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|child
operator|=
operator|(
name|StoredNode
operator|)
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|deletedNodes
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|==
literal|null
condition|)
block|{
name|QName
name|attrQn
init|=
operator|new
name|QName
argument_list|(
name|reader
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
argument_list|,
name|reader
operator|.
name|getAttributeNamespace
argument_list|(
name|i
argument_list|)
argument_list|,
name|reader
operator|.
name|getAttributePrefix
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
name|attrQn
argument_list|,
name|reader
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
argument_list|,
name|getAttributeType
argument_list|(
name|reader
operator|.
name|getAttributeType
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|receiver
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|reader
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|reader
operator|.
name|getPrefix
argument_list|()
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|END_ELEMENT
case|:
name|receiver
operator|.
name|endElement
argument_list|(
operator|new
name|QName
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|reader
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|reader
operator|.
name|getPrefix
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|CHARACTERS
case|:
name|receiver
operator|.
name|characters
argument_list|(
name|reader
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|CDATA
case|:
name|char
index|[]
name|cdata
init|=
name|reader
operator|.
name|getTextCharacters
argument_list|()
decl_stmt|;
name|receiver
operator|.
name|cdataSection
argument_list|(
name|cdata
argument_list|,
literal|0
argument_list|,
name|cdata
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|PROCESSING_INSTRUCTION
case|:
name|receiver
operator|.
name|processingInstruction
argument_list|(
name|reader
operator|.
name|getPITarget
argument_list|()
argument_list|,
name|reader
operator|.
name|getPIData
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|COMMENT
case|:
name|char
index|[]
name|ch
init|=
name|reader
operator|.
name|getTextCharacters
argument_list|()
decl_stmt|;
name|receiver
operator|.
name|comment
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
specifier|private
name|void
name|parseDiff
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|XPathException
block|{
name|deletedNodes
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
name|insertedNodes
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
name|appendedNodes
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
name|XQuery
name|service
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|Sequence
name|changes
init|=
name|service
operator|.
name|execute
argument_list|(
literal|"declare namespace v=\"http://exist-db.org/versioning\";"
operator|+
literal|"doc('"
operator|+
name|doc
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"')/v:version/*"
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|changes
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
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Element
name|child
init|=
operator|(
name|Element
operator|)
name|p
operator|.
name|getNode
argument_list|()
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
operator|&&
name|child
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|XMLDiff
operator|.
name|NAMESPACE
argument_list|)
condition|)
block|{
name|NodeId
name|id
init|=
name|parseRef
argument_list|(
name|broker
argument_list|,
name|child
argument_list|,
literal|"ref"
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"delete"
argument_list|)
condition|)
block|{
name|String
name|event
init|=
operator|(
operator|(
name|Element
operator|)
name|child
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"event"
argument_list|)
decl_stmt|;
if|if
condition|(
name|event
operator|==
literal|null
operator|||
name|event
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|deletedNodes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|D_SUBTREE
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"both"
operator|.
name|equals
argument_list|(
name|event
argument_list|)
condition|)
name|deletedNodes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|D_BOTH
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"start"
operator|.
name|equals
argument_list|(
name|event
argument_list|)
condition|)
block|{
name|String
name|opt
init|=
operator|(
name|String
operator|)
name|deletedNodes
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|==
name|D_END
condition|)
name|deletedNodes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|D_BOTH
argument_list|)
expr_stmt|;
else|else
name|deletedNodes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|D_START
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|opt
init|=
operator|(
name|String
operator|)
name|deletedNodes
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|==
name|D_START
condition|)
name|deletedNodes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|D_BOTH
argument_list|)
expr_stmt|;
else|else
name|deletedNodes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|D_END
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"insert"
argument_list|)
condition|)
block|{
name|insertedNodes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"append"
argument_list|)
condition|)
block|{
name|appendedNodes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|NodeId
name|parseRef
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Node
name|child
parameter_list|,
name|String
name|attr
parameter_list|)
block|{
name|String
name|idval
init|=
operator|(
operator|(
name|Element
operator|)
name|child
operator|)
operator|.
name|getAttribute
argument_list|(
name|attr
argument_list|)
decl_stmt|;
return|return
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromString
argument_list|(
name|idval
argument_list|)
return|;
block|}
specifier|private
name|int
name|getAttributeType
parameter_list|(
name|String
name|attributeType
parameter_list|)
block|{
if|if
condition|(
literal|"ID"
operator|.
name|equals
argument_list|(
name|attributeType
argument_list|)
condition|)
return|return
name|AttrImpl
operator|.
name|ID
return|;
if|else if
condition|(
literal|"IDREF"
operator|.
name|equals
argument_list|(
name|attributeType
argument_list|)
condition|)
return|return
name|AttrImpl
operator|.
name|IDREF
return|;
if|else if
condition|(
literal|"IDREFS"
operator|.
name|equals
argument_list|(
name|attributeType
argument_list|)
condition|)
return|return
name|AttrImpl
operator|.
name|IDREFS
return|;
else|else
return|return
name|AttrImpl
operator|.
name|CDATA
return|;
block|}
block|}
end_class

end_unit

