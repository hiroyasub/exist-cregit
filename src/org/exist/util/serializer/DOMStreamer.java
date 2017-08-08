begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
package|;
end_package

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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
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
name|memtree
operator|.
name|ReferenceNode
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
name|w3c
operator|.
name|dom
operator|.
name|CharacterData
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
name|Comment
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
name|ProcessingInstruction
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
name|ext
operator|.
name|LexicalHandler
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|NamespaceSupport
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
comment|/**  * General purpose class to stream a DOM node to SAX.  *  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DOMStreamer
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|DOMStreamer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ContentHandler
name|contentHandler
init|=
literal|null
decl_stmt|;
specifier|private
name|LexicalHandler
name|lexicalHandler
init|=
literal|null
decl_stmt|;
specifier|private
name|NamespaceSupport
name|nsSupport
init|=
operator|new
name|NamespaceSupport
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaceDecls
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Deque
argument_list|<
name|ElementInfo
argument_list|>
name|stack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|DOMStreamer
parameter_list|()
block|{
comment|//TODUNDERSTAND : what is this class ? java.lang.Object ?
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|DOMStreamer
parameter_list|(
specifier|final
name|ContentHandler
name|contentHandler
parameter_list|,
specifier|final
name|LexicalHandler
name|lexicalHandler
parameter_list|)
block|{
name|this
operator|.
name|contentHandler
operator|=
name|contentHandler
expr_stmt|;
name|this
operator|.
name|lexicalHandler
operator|=
name|lexicalHandler
expr_stmt|;
block|}
specifier|public
name|void
name|setContentHandler
parameter_list|(
specifier|final
name|ContentHandler
name|handler
parameter_list|)
block|{
name|contentHandler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
specifier|final
name|LexicalHandler
name|handler
parameter_list|)
block|{
name|lexicalHandler
operator|=
name|handler
expr_stmt|;
block|}
comment|/**      * Reset internal state for reuse. Registered handlers will be set      * to null.      */
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|nsSupport
operator|.
name|reset
argument_list|()
expr_stmt|;
name|namespaceDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
name|stack
operator|.
name|clear
argument_list|()
expr_stmt|;
name|contentHandler
operator|=
literal|null
expr_stmt|;
name|lexicalHandler
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Serialize the given node and all its descendants to SAX.      *      * @param node      * @throws SAXException      */
specifier|public
name|void
name|serialize
parameter_list|(
specifier|final
name|Node
name|node
parameter_list|)
throws|throws
name|SAXException
block|{
name|serialize
argument_list|(
name|node
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Serialize the given node and all its descendants to SAX. If      * callDocumentEvents is set to false, startDocument/endDocument      * events will not be fired.      *      * @param node      * @param callDocumentEvents      * @throws SAXException      */
specifier|public
name|void
name|serialize
parameter_list|(
name|Node
name|node
parameter_list|,
specifier|final
name|boolean
name|callDocumentEvents
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|callDocumentEvents
condition|)
block|{
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Node
name|top
init|=
name|node
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|startNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|Node
name|nextNode
init|=
name|node
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
comment|//TODO : make it happy
if|if
condition|(
name|node
operator|instanceof
name|ReferenceNode
condition|)
block|{
name|nextNode
operator|=
literal|null
expr_stmt|;
block|}
while|while
condition|(
name|nextNode
operator|==
literal|null
condition|)
block|{
name|endNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|top
operator|!=
literal|null
operator|&&
name|top
operator|.
name|equals
argument_list|(
name|node
argument_list|)
condition|)
block|{
break|break;
block|}
name|nextNode
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextNode
operator|==
literal|null
condition|)
block|{
name|node
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
operator|||
operator|(
name|top
operator|!=
literal|null
operator|&&
name|top
operator|.
name|equals
argument_list|(
name|node
argument_list|)
operator|)
condition|)
block|{
name|endNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|//nextNode = null;
break|break;
block|}
block|}
block|}
name|node
operator|=
name|nextNode
expr_stmt|;
block|}
if|if
condition|(
name|callDocumentEvents
condition|)
block|{
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|startNode
parameter_list|(
specifier|final
name|Node
name|node
parameter_list|)
throws|throws
name|SAXException
block|{
name|String
name|cdata
decl_stmt|;
switch|switch
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
case|case
name|Node
operator|.
name|DOCUMENT_FRAGMENT_NODE
case|:
break|break;
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|namespaceDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nsSupport
operator|.
name|pushContext
argument_list|()
expr_stmt|;
name|String
name|uri
init|=
name|node
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
name|node
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
name|XMLConstants
operator|.
name|XML_NS_URI
expr_stmt|;
block|}
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
expr_stmt|;
block|}
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
condition|)
block|{
name|namespaceDecls
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|// check attributes for required namespace declarations
specifier|final
name|NamedNodeMap
name|attrs
init|=
name|node
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Attr
name|nextAttr
decl_stmt|;
name|String
name|attrName
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
name|nextAttr
operator|=
operator|(
name|Attr
operator|)
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrName
operator|=
name|nextAttr
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
name|nextAttr
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|namespaceDecls
operator|.
name|put
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|attrName
operator|.
name|startsWith
argument_list|(
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
operator|+
literal|":"
argument_list|)
condition|)
block|{
name|prefix
operator|=
name|attrName
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
expr_stmt|;
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
name|nextAttr
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|namespaceDecls
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|attrName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|>
literal|0
condition|)
block|{
name|prefix
operator|=
name|nextAttr
operator|.
name|getPrefix
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
expr_stmt|;
block|}
name|uri
operator|=
name|nextAttr
operator|.
name|getNamespaceURI
argument_list|()
expr_stmt|;
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
condition|)
block|{
name|namespaceDecls
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|ElementInfo
name|info
init|=
operator|new
name|ElementInfo
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|String
index|[]
name|declaredPrefixes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|namespaceDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|declaredPrefixes
operator|=
operator|new
name|String
index|[
name|namespaceDecls
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
block|}
comment|// output all namespace declarations
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nsEntry
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|i
init|=
name|namespaceDecls
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|nsEntry
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|declaredPrefixes
index|[
name|j
index|]
operator|=
name|nsEntry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|contentHandler
operator|.
name|startPrefixMapping
argument_list|(
name|declaredPrefixes
index|[
name|j
index|]
argument_list|,
name|nsEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|prefixes
operator|=
name|declaredPrefixes
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|info
argument_list|)
expr_stmt|;
comment|// output attributes
specifier|final
name|AttributesImpl
name|saxAttrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|String
name|attrNS
decl_stmt|,
name|attrLocalName
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
name|nextAttr
operator|=
operator|(
name|Attr
operator|)
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrNS
operator|=
name|nextAttr
operator|.
name|getNamespaceURI
argument_list|()
expr_stmt|;
if|if
condition|(
name|attrNS
operator|==
literal|null
condition|)
block|{
name|attrNS
operator|=
name|XMLConstants
operator|.
name|NULL_NS_URI
expr_stmt|;
block|}
name|attrLocalName
operator|=
name|nextAttr
operator|.
name|getLocalName
argument_list|()
expr_stmt|;
if|if
condition|(
name|attrLocalName
operator|==
literal|null
condition|)
block|{
name|attrLocalName
operator|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|nextAttr
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|saxAttrs
operator|.
name|addAttribute
argument_list|(
name|attrNS
argument_list|,
name|attrLocalName
argument_list|,
name|nextAttr
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"CDATA"
argument_list|,
name|nextAttr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|localName
init|=
name|node
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|localName
operator|==
literal|null
condition|)
block|{
name|localName
operator|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|namespaceURI
init|=
name|node
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|XMLConstants
operator|.
name|NULL_NS_URI
expr_stmt|;
block|}
name|contentHandler
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|saxAttrs
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
name|cdata
operator|=
operator|(
operator|(
name|CharacterData
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
expr_stmt|;
name|contentHandler
operator|.
name|characters
argument_list|(
name|cdata
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cdata
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
name|cdata
operator|=
operator|(
operator|(
name|CharacterData
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
expr_stmt|;
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|lexicalHandler
operator|.
name|startCDATA
argument_list|()
expr_stmt|;
block|}
name|contentHandler
operator|.
name|characters
argument_list|(
name|cdata
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cdata
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|lexicalHandler
operator|.
name|endCDATA
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
break|break;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
name|contentHandler
operator|.
name|processingInstruction
argument_list|(
operator|(
operator|(
name|ProcessingInstruction
operator|)
name|node
operator|)
operator|.
name|getTarget
argument_list|()
argument_list|,
operator|(
operator|(
name|ProcessingInstruction
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|cdata
operator|=
operator|(
operator|(
name|Comment
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
expr_stmt|;
name|lexicalHandler
operator|.
name|comment
argument_list|(
name|cdata
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cdata
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
comment|//TODO : what kind of default here ? -pb
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown node type: "
operator|+
name|node
operator|.
name|getNodeType
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
specifier|protected
name|void
name|endNode
parameter_list|(
specifier|final
name|Node
name|node
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|node
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
name|ElementInfo
name|info
init|=
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|nsSupport
operator|.
name|popContext
argument_list|()
expr_stmt|;
name|String
name|localName
init|=
name|node
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|localName
operator|==
literal|null
condition|)
block|{
name|localName
operator|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|namespaceURI
init|=
name|node
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|XMLConstants
operator|.
name|NULL_NS_URI
expr_stmt|;
block|}
name|contentHandler
operator|.
name|endElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|prefixes
operator|!=
literal|null
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
name|info
operator|.
name|prefixes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
name|info
operator|.
name|prefixes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|ElementInfo
block|{
specifier|final
name|Node
name|element
decl_stmt|;
name|String
index|[]
name|prefixes
init|=
literal|null
decl_stmt|;
specifier|public
name|ElementInfo
parameter_list|(
specifier|final
name|Node
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
block|}
block|}
end_class

end_unit

