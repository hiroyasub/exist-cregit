begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000,  Wolfgang Meier (wolfgang@exist-db.org)  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|util
operator|.
name|ByteArrayPool
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
name|DOMException
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

begin_class
specifier|public
class|class
name|AttrImpl
extends|extends
name|NodeImpl
implements|implements
name|Attr
block|{
specifier|public
specifier|final
specifier|static
name|int
name|CDATA
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ID
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|attributeType
init|=
name|CDATA
decl_stmt|;
specifier|protected
name|ElementImpl
name|ownerElement
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|value
init|=
literal|null
decl_stmt|;
specifier|public
name|AttrImpl
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|ATTRIBUTE_NODE
argument_list|,
name|gid
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AttrImpl
parameter_list|(
name|QName
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|ATTRIBUTE_NODE
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
specifier|static
name|NodeImpl
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|int
name|next
init|=
name|start
decl_stmt|;
name|byte
name|idSizeType
init|=
operator|(
name|byte
operator|)
operator|(
name|data
index|[
name|next
index|]
operator|&
literal|0x3
operator|)
decl_stmt|;
name|boolean
name|hasNamespace
init|=
operator|(
name|data
index|[
name|next
index|]
operator|&
literal|0x10
operator|)
operator|==
literal|0x10
decl_stmt|;
name|int
name|attrType
init|=
operator|(
name|int
operator|)
operator|(
operator|(
name|data
index|[
name|next
index|]
operator|&
literal|0x4
operator|)
operator|>>
literal|0x2
operator|)
decl_stmt|;
name|short
name|id
init|=
operator|(
name|short
operator|)
name|Signatures
operator|.
name|read
argument_list|(
name|idSizeType
argument_list|,
name|data
argument_list|,
operator|++
name|next
argument_list|)
decl_stmt|;
name|next
operator|+=
name|Signatures
operator|.
name|getLength
argument_list|(
name|idSizeType
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|doc
operator|.
name|getSymbols
argument_list|()
operator|.
name|getName
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|short
name|nsId
init|=
literal|0
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasNamespace
condition|)
block|{
name|nsId
operator|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|next
operator|+=
literal|2
expr_stmt|;
name|int
name|prefixLen
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|next
argument_list|)
decl_stmt|;
name|next
operator|+=
literal|2
expr_stmt|;
if|if
condition|(
name|prefixLen
operator|>
literal|0
condition|)
name|prefix
operator|=
name|UTF8
operator|.
name|decode
argument_list|(
name|data
argument_list|,
name|next
argument_list|,
name|prefixLen
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|next
operator|+=
name|prefixLen
expr_stmt|;
block|}
name|String
name|namespace
init|=
name|nsId
operator|==
literal|0
condition|?
literal|""
else|:
name|doc
operator|.
name|getSymbols
argument_list|()
operator|.
name|getNamespace
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
name|String
name|value
decl_stmt|;
try|try
block|{
name|value
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|next
argument_list|,
name|len
operator|-
operator|(
name|next
operator|-
name|start
operator|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
name|value
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|next
argument_list|,
name|len
operator|-
operator|(
name|next
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
block|}
name|AttrImpl
name|attr
init|=
operator|new
name|AttrImpl
argument_list|(
operator|new
name|QName
argument_list|(
name|name
argument_list|,
name|namespace
argument_list|,
name|prefix
argument_list|)
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|attr
operator|.
name|setType
argument_list|(
name|attrType
argument_list|)
expr_stmt|;
return|return
name|attr
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|nodeName
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|attributeType
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|attributeType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|getNodeValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|Element
name|getOwnerElement
parameter_list|()
block|{
return|return
operator|(
name|Element
operator|)
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|getParentGID
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|getSpecified
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
specifier|final
name|short
name|id
init|=
name|ownerDocument
operator|.
name|getSymbols
argument_list|()
operator|.
name|getSymbol
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|final
name|byte
name|idSizeType
init|=
name|Signatures
operator|.
name|getSizeType
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|int
name|prefixLen
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|needsNamespaceDecl
argument_list|()
condition|)
block|{
name|prefixLen
operator|=
name|nodeName
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|UTF8
operator|.
name|encoded
argument_list|(
name|nodeName
operator|.
name|getPrefix
argument_list|()
argument_list|)
else|:
literal|0
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|data
init|=
name|ByteArrayPool
operator|.
name|getByteArray
argument_list|(
name|UTF8
operator|.
name|encoded
argument_list|(
name|value
argument_list|)
operator|+
name|Signatures
operator|.
name|getLength
argument_list|(
name|idSizeType
argument_list|)
operator|+
operator|(
name|nodeName
operator|.
name|needsNamespaceDecl
argument_list|()
condition|?
name|prefixLen
operator|+
literal|4
else|:
literal|0
operator|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|data
index|[
name|pos
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
name|Signatures
operator|.
name|Attr
operator|<<
literal|0x5
operator|)
expr_stmt|;
name|data
index|[
name|pos
index|]
operator||=
name|idSizeType
expr_stmt|;
name|data
index|[
name|pos
index|]
operator||=
operator|(
name|byte
operator|)
operator|(
name|attributeType
operator|<<
literal|0x2
operator|)
expr_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|needsNamespaceDecl
argument_list|()
condition|)
name|data
index|[
name|pos
index|]
operator||=
literal|0x10
expr_stmt|;
name|Signatures
operator|.
name|write
argument_list|(
name|idSizeType
argument_list|,
name|id
argument_list|,
name|data
argument_list|,
operator|++
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|Signatures
operator|.
name|getLength
argument_list|(
name|idSizeType
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|needsNamespaceDecl
argument_list|()
condition|)
block|{
specifier|final
name|short
name|nsId
init|=
name|ownerDocument
operator|.
name|getSymbols
argument_list|()
operator|.
name|getNSSymbol
argument_list|(
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|nsId
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|2
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
operator|(
name|short
operator|)
name|prefixLen
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|2
expr_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|UTF8
operator|.
name|encode
argument_list|(
name|nodeName
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|prefixLen
expr_stmt|;
block|}
name|UTF8
operator|.
name|encode
argument_list|(
name|value
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|DOMException
block|{
name|this
operator|.
name|value
operator|=
name|XMLUtil
operator|.
name|encodeAttrMarkup
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|toSAX
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|LexicalHandler
name|lexicalHandler
parameter_list|,
name|boolean
name|first
parameter_list|,
name|Set
name|namespaces
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|first
condition|)
block|{
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|,
literal|"id"
argument_list|,
literal|"exist:id"
argument_list|,
literal|"CDATA"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|gid
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|,
literal|"source"
argument_list|,
literal|"exist:source"
argument_list|,
literal|"CDATA"
argument_list|,
name|ownerDocument
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|getNamespaceURI
argument_list|()
argument_list|,
name|getLocalName
argument_list|()
argument_list|,
name|getNodeName
argument_list|()
argument_list|,
literal|"CDATA"
argument_list|,
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|startElement
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|,
literal|"attribute"
argument_list|,
literal|"exist:attribute"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|endElement
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|,
literal|"attribute"
argument_list|,
literal|"exist:attribute"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|top
parameter_list|)
block|{
if|if
condition|(
name|top
condition|)
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<exist:attribute "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"xmlns:exist=\"http://exist.sourceforge.net/NS/exist\" "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"exist:id=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|gid
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\" exist:source=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|ownerDocument
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\" "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\"/>"
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
return|return
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

