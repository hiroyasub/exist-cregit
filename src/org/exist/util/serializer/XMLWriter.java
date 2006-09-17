begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|Arrays
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
name|encodings
operator|.
name|CharacterSet
import|;
end_import

begin_comment
comment|/**  * Write XML to a writer. This class defines methods similar to SAX. It deals  * with opening and closing tags, writing attributes and so on.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLWriter
block|{
specifier|protected
specifier|final
specifier|static
name|Properties
name|defaultProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Writer
name|writer
init|=
literal|null
decl_stmt|;
specifier|protected
name|CharacterSet
name|charSet
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|tagIsOpen
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|tagIsEmpty
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|declarationWritten
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|doctypeWritten
init|=
literal|false
decl_stmt|;
specifier|protected
name|Properties
name|outputProperties
decl_stmt|;
specifier|private
name|char
index|[]
name|charref
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
specifier|private
specifier|static
name|boolean
index|[]
name|textSpecialChars
decl_stmt|;
specifier|private
specifier|static
name|boolean
index|[]
name|attrSpecialChars
decl_stmt|;
specifier|private
name|String
name|defaultNamespace
init|=
literal|""
decl_stmt|;
static|static
block|{
name|textSpecialChars
operator|=
operator|new
name|boolean
index|[
literal|128
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|textSpecialChars
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|textSpecialChars
index|[
literal|'<'
index|]
operator|=
literal|true
expr_stmt|;
name|textSpecialChars
index|[
literal|'>'
index|]
operator|=
literal|true
expr_stmt|;
comment|// textSpecialChars['\r'] = true;
name|textSpecialChars
index|[
literal|'&'
index|]
operator|=
literal|true
expr_stmt|;
name|attrSpecialChars
operator|=
operator|new
name|boolean
index|[
literal|128
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|attrSpecialChars
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|attrSpecialChars
index|[
literal|'<'
index|]
operator|=
literal|true
expr_stmt|;
name|attrSpecialChars
index|[
literal|'>'
index|]
operator|=
literal|true
expr_stmt|;
name|attrSpecialChars
index|[
literal|'\r'
index|]
operator|=
literal|true
expr_stmt|;
name|attrSpecialChars
index|[
literal|'\n'
index|]
operator|=
literal|true
expr_stmt|;
name|attrSpecialChars
index|[
literal|'\t'
index|]
operator|=
literal|true
expr_stmt|;
name|attrSpecialChars
index|[
literal|'&'
index|]
operator|=
literal|true
expr_stmt|;
name|attrSpecialChars
index|[
literal|'"'
index|]
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|XMLWriter
parameter_list|()
block|{
block|}
specifier|public
name|XMLWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
comment|/** 	 * Set the output properties. 	 *  	 * @param properties outputProperties 	 */
specifier|public
name|void
name|setOutputProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
name|outputProperties
operator|=
name|defaultProperties
expr_stmt|;
else|else
name|outputProperties
operator|=
name|properties
expr_stmt|;
name|String
name|encoding
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|charSet
operator|=
name|CharacterSet
operator|.
name|getCharacterSet
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|reset
parameter_list|()
block|{
name|writer
operator|=
literal|null
expr_stmt|;
name|defaultNamespace
operator|=
literal|""
expr_stmt|;
block|}
comment|/** 	 * Set a new writer. Calling this method will reset the state of the object. 	 *  	 * @param writer 	 */
specifier|public
name|void
name|setWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|tagIsOpen
operator|=
literal|false
expr_stmt|;
name|tagIsEmpty
operator|=
literal|true
expr_stmt|;
name|declarationWritten
operator|=
literal|false
expr_stmt|;
name|defaultNamespace
operator|=
literal|""
expr_stmt|;
block|}
specifier|public
name|void
name|setDefaultNamespace
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
name|defaultNamespace
operator|=
name|namespace
operator|==
literal|null
condition|?
literal|""
else|:
name|namespace
expr_stmt|;
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|TransformerException
block|{
name|tagIsOpen
operator|=
literal|false
expr_stmt|;
name|tagIsEmpty
operator|=
literal|true
expr_stmt|;
name|declarationWritten
operator|=
literal|false
expr_stmt|;
name|doctypeWritten
operator|=
literal|false
expr_stmt|;
name|defaultNamespace
operator|=
literal|""
expr_stmt|;
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|TransformerException
block|{
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
operator|!
name|declarationWritten
condition|)
name|writeDeclaration
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|doctypeWritten
condition|)
name|writeDoctype
argument_list|(
name|qname
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
name|closeStartTag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|tagIsOpen
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
operator|!
name|declarationWritten
condition|)
name|writeDeclaration
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|doctypeWritten
condition|)
name|writeDoctype
argument_list|(
name|qname
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
name|closeStartTag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
if|if
condition|(
name|qname
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
name|qname
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|tagIsOpen
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
name|closeStartTag
argument_list|(
literal|true
argument_list|)
expr_stmt|;
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"</"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|public
name|void
name|endElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
name|closeStartTag
argument_list|(
literal|true
argument_list|)
expr_stmt|;
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"</"
argument_list|)
expr_stmt|;
if|if
condition|(
name|qname
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
name|qname
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|public
name|void
name|namespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|nsURI
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
operator|(
name|nsURI
operator|==
literal|null
operator|)
operator|&&
operator|(
name|prefix
operator|==
literal|null
operator|||
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
return|return;
try|try
block|{
if|if
condition|(
operator|!
name|tagIsOpen
condition|)
throw|throw
operator|new
name|TransformerException
argument_list|(
literal|"Found a namespace declaration outside an element"
argument_list|)
throw|;
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"xmlns"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|writeChars
argument_list|(
name|nsURI
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|defaultNamespace
operator|.
name|equals
argument_list|(
name|nsURI
argument_list|)
condition|)
return|return;
name|writer
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"xmlns"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|writeChars
argument_list|(
name|nsURI
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|defaultNamespace
operator|=
name|nsURI
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|public
name|void
name|attribute
parameter_list|(
name|String
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|TransformerException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|tagIsOpen
condition|)
block|{
name|characters
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return;
comment|// throw new TransformerException("Found an attribute outside an
comment|// element");
block|}
name|writer
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|writeChars
argument_list|(
name|value
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|public
name|void
name|attribute
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|TransformerException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|tagIsOpen
condition|)
block|{
name|characters
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return;
comment|// throw new TransformerException("Found an attribute outside an
comment|// element");
block|}
name|writer
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
if|if
condition|(
name|qname
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
name|qname
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|writeChars
argument_list|(
name|value
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
if|if
condition|(
operator|!
name|declarationWritten
condition|)
name|writeDeclaration
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
name|closeStartTag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writeChars
argument_list|(
name|chars
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
name|len
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
operator|!
name|declarationWritten
condition|)
name|writeDeclaration
argument_list|()
expr_stmt|;
name|XMLString
name|s
init|=
operator|new
name|XMLString
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|characters
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|s
operator|.
name|release
argument_list|()
expr_stmt|;
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
name|TransformerException
block|{
if|if
condition|(
operator|!
name|declarationWritten
condition|)
name|writeDeclaration
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
name|closeStartTag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"<?"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
operator|&&
name|data
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|writeChars
argument_list|(
name|data
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"?>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|public
name|void
name|comment
parameter_list|(
name|CharSequence
name|data
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
operator|!
name|declarationWritten
condition|)
name|writeDeclaration
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
name|closeStartTag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"<!--"
argument_list|)
expr_stmt|;
name|writeChars
argument_list|(
name|data
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"-->"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|public
name|void
name|cdataSection
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|tagIsOpen
condition|)
name|closeStartTag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<![CDATA["
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"]]>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
if|if
condition|(
operator|!
name|declarationWritten
condition|)
name|writeDeclaration
argument_list|()
expr_stmt|;
if|if
condition|(
name|publicId
operator|==
literal|null
operator|&&
name|systemId
operator|==
literal|null
condition|)
return|return;
try|try
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<!DOCTYPE "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|publicId
operator|!=
literal|null
condition|)
block|{
comment|//writer.write(" PUBLIC \"" + publicId + "\"");
name|writer
operator|.
name|write
argument_list|(
literal|" PUBLIC \""
operator|+
name|publicId
operator|.
name|replaceAll
argument_list|(
literal|"&#160;"
argument_list|,
literal|" "
argument_list|)
operator|+
literal|"\""
argument_list|)
expr_stmt|;
comment|//workaround for XHTML doctype, declare does not allow spaces so use&#160; instead and then replace each&#160; with a space here - delirium
block|}
if|if
condition|(
name|systemId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|publicId
operator|==
literal|null
condition|)
name|writer
operator|.
name|write
argument_list|(
literal|" SYSTEM"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|" \""
operator|+
name|systemId
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|doctypeWritten
operator|=
literal|true
expr_stmt|;
block|}
specifier|protected
name|void
name|closeStartTag
parameter_list|(
name|boolean
name|isEmpty
parameter_list|)
throws|throws
name|TransformerException
block|{
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
block|{
if|if
condition|(
name|isEmpty
condition|)
name|writer
operator|.
name|write
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
else|else
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|tagIsOpen
operator|=
literal|false
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
specifier|protected
name|void
name|writeDeclaration
parameter_list|()
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|declarationWritten
condition|)
return|return;
if|if
condition|(
name|outputProperties
operator|==
literal|null
condition|)
name|outputProperties
operator|=
name|defaultProperties
expr_stmt|;
name|declarationWritten
operator|=
literal|true
expr_stmt|;
name|String
name|omitXmlDecl
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"yes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|omitXmlDecl
operator|.
name|equals
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
name|String
name|version
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|VERSION
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|standalone
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|STANDALONE
argument_list|)
decl_stmt|;
name|String
name|encoding
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<?xml version=\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\" encoding=\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
if|if
condition|(
name|standalone
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|" standalone=\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|standalone
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"?>\n"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
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
block|}
specifier|protected
name|void
name|writeDoctype
parameter_list|(
name|String
name|rootElement
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|doctypeWritten
condition|)
return|return;
name|String
name|publicId
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|DOCTYPE_PUBLIC
argument_list|)
decl_stmt|;
name|String
name|systemId
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|DOCTYPE_SYSTEM
argument_list|)
decl_stmt|;
if|if
condition|(
name|publicId
operator|!=
literal|null
operator|||
name|systemId
operator|!=
literal|null
condition|)
name|documentType
argument_list|(
name|rootElement
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
name|doctypeWritten
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
specifier|final
name|void
name|writeChars
parameter_list|(
name|CharSequence
name|s
parameter_list|,
name|boolean
name|inAttribute
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
index|[]
name|specialChars
init|=
name|inAttribute
condition|?
name|attrSpecialChars
else|:
name|textSpecialChars
decl_stmt|;
name|char
name|ch
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|,
name|i
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|len
condition|)
block|{
name|i
operator|=
name|pos
expr_stmt|;
while|while
condition|(
name|i
operator|<
name|len
condition|)
block|{
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|<
literal|128
condition|)
block|{
if|if
condition|(
name|specialChars
index|[
name|ch
index|]
condition|)
break|break;
else|else
name|i
operator|++
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|charSet
operator|.
name|inCharacterSet
argument_list|(
name|ch
argument_list|)
operator|||
name|ch
operator|==
literal|160
condition|)
break|break;
else|else
name|i
operator|++
expr_stmt|;
block|}
name|writeCharSeq
argument_list|(
name|s
argument_list|,
name|pos
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// writer.write(s.subSequence(pos, i).toString());
if|if
condition|(
name|i
operator|>=
name|len
condition|)
return|return;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'<'
case|:
name|writer
operator|.
name|write
argument_list|(
literal|"&lt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'>'
case|:
name|writer
operator|.
name|write
argument_list|(
literal|"&gt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'&'
case|:
name|writer
operator|.
name|write
argument_list|(
literal|"&amp;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\r'
case|:
name|writer
operator|.
name|write
argument_list|(
literal|"&#xD;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\n'
case|:
name|writer
operator|.
name|write
argument_list|(
literal|"&#xA;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\t'
case|:
name|writer
operator|.
name|write
argument_list|(
literal|"&#x9;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
name|writer
operator|.
name|write
argument_list|(
literal|"&#34;"
argument_list|)
expr_stmt|;
break|break;
comment|// non-breaking space:
case|case
literal|160
case|:
name|writer
operator|.
name|write
argument_list|(
literal|"&#160;"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|writeCharacterReference
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
name|pos
operator|=
operator|++
name|i
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|writeCharSeq
parameter_list|(
name|CharSequence
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|ch
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeCharacterReference
parameter_list|(
name|char
name|charval
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|o
init|=
literal|0
decl_stmt|;
name|charref
index|[
name|o
operator|++
index|]
operator|=
literal|'&'
expr_stmt|;
name|charref
index|[
name|o
operator|++
index|]
operator|=
literal|'#'
expr_stmt|;
name|charref
index|[
name|o
operator|++
index|]
operator|=
literal|'x'
expr_stmt|;
name|String
name|code
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|charval
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|code
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|len
condition|;
name|k
operator|++
control|)
block|{
name|charref
index|[
name|o
operator|++
index|]
operator|=
name|code
operator|.
name|charAt
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|charref
index|[
name|o
operator|++
index|]
operator|=
literal|';'
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|charref
argument_list|,
literal|0
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

