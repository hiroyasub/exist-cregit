begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2013 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|exist
operator|.
name|dom
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
name|transform
operator|.
name|OutputKeys
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
name|TransformerException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|CharBuffer
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

begin_comment
comment|/**  * Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|MicroXmlWriter
extends|extends
name|IndentingXMLWriter
block|{
specifier|private
name|String
name|removePrefix
parameter_list|(
specifier|final
name|String
name|qname
parameter_list|)
block|{
specifier|final
name|int
name|prefixDelimIdx
init|=
name|qname
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
specifier|final
name|String
name|result
decl_stmt|;
if|if
condition|(
name|prefixDelimIdx
operator|>
operator|-
literal|1
condition|)
block|{
name|result
operator|=
name|qname
operator|.
name|substring
argument_list|(
name|prefixDelimIdx
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|qname
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|QName
name|removePrefix
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
block|{
name|qname
operator|.
name|setNamespaceURI
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|qname
operator|.
name|setPrefix
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return
name|qname
return|;
block|}
specifier|private
name|CharSequence
name|removeRestrictedChars
parameter_list|(
specifier|final
name|CharSequence
name|charSeq
parameter_list|)
block|{
specifier|final
name|CharBuffer
name|buf
init|=
name|CharBuffer
operator|.
name|allocate
argument_list|(
name|charSeq
operator|.
name|length
argument_list|()
argument_list|)
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
name|charSeq
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|charSeq
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
literal|'>'
case|:
comment|//ignore char
break|break;
default|default:
name|buf
operator|.
name|append
argument_list|(
name|charSeq
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|compact
argument_list|()
expr_stmt|;
return|return
name|buf
return|;
comment|//TODO prohibit
comment|/*         Unicode noncharacters (XML only prohibits #xFFFE and #xFFFF);         Unicode C1 control characters;         numeric character references to #xD;         decimal character references;         */
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|String
name|namespaceUri
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
name|localName
argument_list|,
name|removePrefix
argument_list|(
name|qname
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|startElement
argument_list|(
name|qname
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
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
name|localName
argument_list|,
name|removePrefix
argument_list|(
name|qname
argument_list|)
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
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|endElement
argument_list|(
name|removePrefix
argument_list|(
name|qname
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|namespace
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|nsURI
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
specifier|final
name|String
name|qname
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|qname
operator|!=
literal|null
operator|&&
operator|!
name|qname
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
block|{
name|super
operator|.
name|attribute
argument_list|(
name|removePrefix
argument_list|(
name|qname
argument_list|)
argument_list|,
name|removeRestrictedChars
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|qname
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|qname
operator|.
name|getLocalPart
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
operator|||
operator|(
name|qname
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|qname
operator|.
name|getPrefix
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
operator|)
operator|)
condition|)
block|{
name|super
operator|.
name|attribute
argument_list|(
name|removePrefix
argument_list|(
name|qname
argument_list|)
argument_list|,
name|removeRestrictedChars
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDefaultNamespace
parameter_list|(
specifier|final
name|String
name|namespace
parameter_list|)
block|{
comment|//no-op
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
name|TransformerException
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|cdataSection
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
name|len
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|documentType
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
name|TransformerException
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
specifier|final
name|CharSequence
name|chars
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|characters
argument_list|(
name|removeRestrictedChars
argument_list|(
name|chars
argument_list|)
argument_list|)
expr_stmt|;
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
name|len
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|characters
argument_list|(
name|removeRestrictedChars
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOutputProperties
parameter_list|(
specifier|final
name|Properties
name|properties
parameter_list|)
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|super
operator|.
name|setOutputProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
comment|//To change body of overridden methods use File | Settings | File Templates.
block|}
block|}
end_class

end_unit

