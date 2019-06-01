begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2000-2015 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  */
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|exist
operator|.
name|Namespaces
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
name|storage
operator|.
name|serializers
operator|.
name|EXistOutputKeys
import|;
end_import

begin_class
specifier|public
class|class
name|IndentingXMLWriter
extends|extends
name|XMLWriter
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
name|IndentingXMLWriter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|indent
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|indentAmount
init|=
literal|4
decl_stmt|;
specifier|private
name|String
name|indentChars
init|=
literal|"                                                                                           "
decl_stmt|;
specifier|private
name|int
name|level
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|afterTag
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|sameline
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|whitespacePreserve
init|=
literal|false
decl_stmt|;
specifier|private
name|Deque
argument_list|<
name|Integer
argument_list|>
name|whitespacePreserveStack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|IndentingXMLWriter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param writer A writer to send the serialized XML output to      */
specifier|public
name|IndentingXMLWriter
parameter_list|(
specifier|final
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setWriter
parameter_list|(
specifier|final
name|Writer
name|writer
parameter_list|)
block|{
name|super
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|level
operator|=
literal|0
expr_stmt|;
name|afterTag
operator|=
literal|false
expr_stmt|;
name|sameline
operator|=
literal|false
expr_stmt|;
name|whitespacePreserveStack
operator|.
name|clear
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
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|afterTag
operator|&&
operator|!
name|isInlineTag
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
condition|)
block|{
name|indent
argument_list|()
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
argument_list|)
expr_stmt|;
name|addIndent
argument_list|()
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
name|sameline
operator|=
literal|true
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
if|if
condition|(
name|afterTag
operator|&&
operator|!
name|isInlineTag
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|indent
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|startElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|addIndent
argument_list|()
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
name|sameline
operator|=
literal|true
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
name|endIndent
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
expr_stmt|;
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
name|popWhitespacePreserve
argument_list|()
expr_stmt|;
comment|// apply ancestor's xml:space value _after_ end element
name|sameline
operator|=
name|isInlineTag
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
expr_stmt|;
name|afterTag
operator|=
literal|true
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
name|endIndent
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|popWhitespacePreserve
argument_list|()
expr_stmt|;
comment|// apply ancestor's xml:space value _after_ end element
name|sameline
operator|=
name|isInlineTag
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|chars
parameter_list|)
throws|throws
name|TransformerException
block|{
specifier|final
name|int
name|start
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|chars
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return;
comment|// whitespace only: skip
block|}
if|if
condition|(
name|length
operator|<
name|chars
operator|.
name|length
argument_list|()
condition|)
block|{
name|chars
operator|=
name|chars
operator|.
name|subSequence
argument_list|(
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// drop whitespace
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
name|chars
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|chars
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'\n'
condition|)
block|{
name|sameline
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|afterTag
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
name|chars
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|comment
parameter_list|(
specifier|final
name|CharSequence
name|data
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|comment
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
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
name|super
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocumentType
parameter_list|()
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|endDocumentType
argument_list|()
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sameline
operator|=
literal|false
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
name|super
operator|.
name|setOutputProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
specifier|final
name|String
name|option
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|INDENT_SPACES
argument_list|,
literal|"4"
argument_list|)
decl_stmt|;
try|try
block|{
name|indentAmount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid indentation value: '"
operator|+
name|option
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|indent
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"xml:space"
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
condition|)
block|{
name|pushWhitespacePreserve
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|attribute
argument_list|(
name|qname
argument_list|,
name|value
argument_list|)
expr_stmt|;
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
literal|"xml"
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
argument_list|)
operator|&&
literal|"space"
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
name|pushWhitespacePreserve
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|attribute
argument_list|(
name|qname
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|pushWhitespacePreserve
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"preserve"
argument_list|)
condition|)
block|{
name|whitespacePreserve
operator|=
literal|true
expr_stmt|;
name|whitespacePreserveStack
operator|.
name|push
argument_list|(
operator|-
name|level
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
condition|)
block|{
name|whitespacePreserve
operator|=
literal|false
expr_stmt|;
name|whitespacePreserveStack
operator|.
name|push
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|popWhitespacePreserve
parameter_list|()
block|{
if|if
condition|(
operator|!
name|whitespacePreserveStack
operator|.
name|isEmpty
argument_list|()
operator|&&
name|Math
operator|.
name|abs
argument_list|(
name|whitespacePreserveStack
operator|.
name|peek
argument_list|()
argument_list|)
operator|>
name|level
condition|)
block|{
name|whitespacePreserveStack
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|whitespacePreserveStack
operator|.
name|isEmpty
argument_list|()
operator|||
name|whitespacePreserveStack
operator|.
name|peek
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|whitespacePreserve
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|whitespacePreserve
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|boolean
name|isInlineTag
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
return|return
name|isMatchTag
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isMatchTag
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
return|return
name|namespaceURI
operator|!=
literal|null
operator|&&
name|namespaceURI
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|)
operator|&&
name|localName
operator|.
name|equals
argument_list|(
literal|"match"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|addSpaceIfIndent
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|indent
operator|||
name|whitespacePreserve
condition|)
block|{
return|return;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|indent
parameter_list|()
throws|throws
name|TransformerException
block|{
if|if
condition|(
operator|!
name|indent
operator|||
name|whitespacePreserve
condition|)
block|{
return|return;
block|}
specifier|final
name|int
name|spaces
init|=
name|indentAmount
operator|*
name|level
decl_stmt|;
while|while
condition|(
name|spaces
operator|>=
name|indentChars
operator|.
name|length
argument_list|()
condition|)
block|{
name|indentChars
operator|+=
name|indentChars
expr_stmt|;
block|}
name|super
operator|.
name|characters
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
name|indentChars
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|spaces
argument_list|)
argument_list|)
expr_stmt|;
name|sameline
operator|=
literal|false
expr_stmt|;
block|}
specifier|protected
name|void
name|addIndent
parameter_list|()
block|{
name|level
operator|++
expr_stmt|;
block|}
specifier|protected
name|void
name|endIndent
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|)
throws|throws
name|TransformerException
block|{
name|level
operator|--
expr_stmt|;
if|if
condition|(
name|afterTag
operator|&&
operator|!
name|sameline
operator|&&
operator|!
name|isInlineTag
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
condition|)
block|{
name|indent
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
