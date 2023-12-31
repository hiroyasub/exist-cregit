begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Team  *  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|xquery
operator|.
name|XQueryContext
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
name|Locator
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
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Adapter class to build an internal, in-memory DOM from a SAX stream.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|SAXAdapter
implements|implements
name|ContentHandler
implements|,
name|LexicalHandler
block|{
specifier|private
name|MemTreeBuilder
name|builder
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|replaceAttributeFlag
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|cdataFlag
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|cdataBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|public
name|SAXAdapter
parameter_list|()
block|{
name|setBuilder
argument_list|(
operator|new
name|MemTreeBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SAXAdapter
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
name|setBuilder
argument_list|(
operator|new
name|MemTreeBuilder
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|final
name|void
name|setBuilder
parameter_list|(
specifier|final
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getDocument
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|replaceAttributeFlag
condition|)
block|{
name|builder
operator|.
name|setReplaceAttributeFlag
argument_list|(
name|replaceAttributeFlag
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|cdataFlag
condition|)
block|{
name|cdataBuf
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
name|builder
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
name|ignorableWhitespace
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
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
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|skippedEntity
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
specifier|final
name|Locator
name|locator
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|processingInstruction
parameter_list|(
specifier|final
name|String
name|target
parameter_list|,
specifier|final
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|namespaces
operator|==
literal|null
condition|)
block|{
name|namespaces
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qName
parameter_list|,
specifier|final
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaces
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
name|namespaces
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|namespaceNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|attQName
init|=
name|atts
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|attQName
operator|.
name|startsWith
argument_list|(
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
argument_list|)
condition|)
block|{
specifier|final
name|int
name|idxPrefixSep
init|=
name|attQName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
specifier|final
name|String
name|prefix
init|=
name|idxPrefixSep
operator|>
operator|-
literal|1
condition|?
name|attQName
operator|.
name|substring
argument_list|(
name|idxPrefixSep
operator|+
literal|1
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|String
name|uri
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaces
operator|==
literal|null
operator|||
operator|!
name|namespaces
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|builder
operator|.
name|namespaceNode
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|namespaces
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDTD
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|this
operator|.
name|cdataFlag
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|cdataSection
argument_list|(
name|cdataBuf
argument_list|)
expr_stmt|;
name|cdataBuf
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|cdataBuf
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|cdataFlag
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|comment
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|comment
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endEntity
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startEntity
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDTD
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|publicId
parameter_list|,
specifier|final
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
specifier|public
name|void
name|setReplaceAttributeFlag
parameter_list|(
specifier|final
name|boolean
name|replaceAttributeFlag
parameter_list|)
block|{
name|this
operator|.
name|replaceAttributeFlag
operator|=
name|replaceAttributeFlag
expr_stmt|;
block|}
block|}
end_class

end_unit

