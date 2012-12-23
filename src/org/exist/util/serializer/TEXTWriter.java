begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  * $Id$  */
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
comment|/**  * Write PLAIN TEXT to a writer. This class defines methods similar to SAX.  * It deals with opening and closing tags, writing attributes and so on: they  * are all ignored. Only real content is written!  *  * Note this is an initial version. Code cleanup needed. Original code is  * commented for fast repair.  *  * @author dizzz  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|TEXTWriter
extends|extends
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
specifier|public
name|TEXTWriter
parameter_list|()
block|{
comment|// empty
block|}
specifier|public
name|TEXTWriter
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|protected
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Set the output properties.      *      * @param properties outputProperties      */
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
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|outputProperties
operator|=
name|defaultProperties
expr_stmt|;
block|}
else|else
block|{
name|outputProperties
operator|=
name|properties
expr_stmt|;
block|}
specifier|final
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
comment|/**      * Set a new writer. Calling this method will reset the state of the object.      *      * @param writer      */
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
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|TransformerException
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|TransformerException
block|{
comment|// empty
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
comment|// empty
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
comment|// empty
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
comment|// empty
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
comment|// empty
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
comment|// empty
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
comment|// empty
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
comment|// empty
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
try|try
block|{
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
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
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
name|len
parameter_list|)
throws|throws
name|TransformerException
block|{
specifier|final
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
comment|// empty
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
comment|// empty
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
try|try
block|{
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
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|documentType
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
name|TransformerException
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|protected
name|void
name|closeStartTag
parameter_list|(
specifier|final
name|boolean
name|isEmpty
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeDeclaration
parameter_list|()
throws|throws
name|TransformerException
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeDoctype
parameter_list|(
specifier|final
name|String
name|rootElement
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|// empty
block|}
specifier|private
name|void
name|writeChars
parameter_list|(
specifier|final
name|CharSequence
name|s
parameter_list|,
specifier|final
name|boolean
name|inAttribute
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|writeCharSeq
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeCharSeq
parameter_list|(
specifier|final
name|CharSequence
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|protected
name|void
name|writeCharacterReference
parameter_list|(
specifier|final
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
specifier|final
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
specifier|final
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

