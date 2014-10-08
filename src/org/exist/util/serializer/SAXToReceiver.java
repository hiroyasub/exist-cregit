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
name|util
operator|.
name|serializer
package|;
end_package

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
name|Attributes
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
name|persistent
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
name|util
operator|.
name|XMLString
import|;
end_import

begin_class
specifier|public
class|class
name|SAXToReceiver
implements|implements
name|ContentHandler
implements|,
name|LexicalHandler
block|{
specifier|private
name|Receiver
name|receiver
decl_stmt|;
specifier|private
name|boolean
name|inCDATASection
init|=
literal|false
decl_stmt|;
specifier|public
name|SAXToReceiver
parameter_list|(
name|Receiver
name|receiver
parameter_list|)
block|{
name|this
operator|.
name|receiver
operator|=
name|receiver
expr_stmt|;
block|}
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
name|String
name|prefix
init|=
literal|null
decl_stmt|;
name|int
name|p
init|=
name|qName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>
operator|-
literal|1
condition|)
block|{
name|prefix
operator|=
name|qName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
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
name|atts
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
name|atts
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|attrPrefix
init|=
literal|null
decl_stmt|;
name|p
operator|=
name|atts
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|>
operator|-
literal|1
condition|)
block|{
name|attrPrefix
operator|=
name|atts
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|type
init|=
name|AttrImpl
operator|.
name|CDATA
decl_stmt|;
specifier|final
name|String
name|atype
init|=
name|atts
operator|.
name|getType
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"ID"
operator|.
name|equals
argument_list|(
name|atype
argument_list|)
condition|)
block|{
name|type
operator|=
name|AttrImpl
operator|.
name|ID
expr_stmt|;
block|}
if|else if
condition|(
literal|"IDREF"
operator|.
name|equals
argument_list|(
name|atype
argument_list|)
condition|)
block|{
name|type
operator|=
name|AttrImpl
operator|.
name|IDREF
expr_stmt|;
block|}
if|else if
condition|(
literal|"IDREFS"
operator|.
name|equals
argument_list|(
name|atype
argument_list|)
condition|)
block|{
name|type
operator|=
name|AttrImpl
operator|.
name|IDREFS
expr_stmt|;
block|}
name|attrs
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|,
name|atts
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
argument_list|,
name|attrPrefix
argument_list|)
argument_list|,
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|uri
argument_list|,
name|prefix
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
name|String
name|prefix
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|p
init|=
name|qName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>
operator|-
literal|1
condition|)
block|{
name|prefix
operator|=
name|qName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|endElement
argument_list|(
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|uri
argument_list|,
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|inCDATASection
condition|)
block|{
name|receiver
operator|.
name|cdataSection
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
name|receiver
operator|.
name|characters
argument_list|(
operator|new
name|XMLString
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|ignorableWhitespace
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
block|}
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
name|receiver
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|skippedEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
specifier|public
name|void
name|startDTD
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
specifier|public
name|void
name|endDTD
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
specifier|public
name|void
name|startEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
specifier|public
name|void
name|endEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
specifier|public
name|void
name|startCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|inCDATASection
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|endCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|inCDATASection
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|void
name|comment
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
name|receiver
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
block|}
end_class

end_unit

