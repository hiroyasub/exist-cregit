begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
operator|.
name|json
package|;
end_package

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
name|SAXSerializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Document
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
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|*
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
name|dom
operator|.
name|DOMSource
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
name|sax
operator|.
name|SAXResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Created by aretter on 16/05/2017.  */
end_comment

begin_class
specifier|public
class|class
name|JSONWriterTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|EOL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DocumentBuilderFactory
name|documentBuilderFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
static|static
block|{
name|documentBuilderFactory
operator|.
name|setIgnoringElementContentWhitespace
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|TransformerFactory
name|transformerFactory
init|=
operator|new
name|net
operator|.
name|sf
operator|.
name|saxon
operator|.
name|TransformerFactoryImpl
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|whitespaceTextNodes
parameter_list|()
throws|throws
name|IOException
throws|,
name|TransformerException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
block|{
specifier|final
name|Node
name|xmlDoc
init|=
name|parseXml
argument_list|(
literal|"<a z='99'>"
operator|+
name|EOL
operator|+
literal|"<b x='1'/>"
operator|+
name|EOL
operator|+
literal|"<b x='2'></b>"
operator|+
name|EOL
operator|+
literal|"<b x='3'>stuff</b>"
operator|+
name|EOL
operator|+
literal|"<b x='4'>\t\r\n   \r\n</b>"
operator|+
name|EOL
operator|+
literal|"</a>"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
specifier|final
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|properties
argument_list|)
expr_stmt|;
specifier|final
name|Transformer
name|transformer
init|=
name|transformerFactory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
specifier|final
name|SAXResult
name|saxResult
init|=
operator|new
name|SAXResult
argument_list|(
name|serializer
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|xmlDoc
argument_list|)
argument_list|,
name|saxResult
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"z\":\"99\",\"#text\":[\"\\n    \",\"\\n    \",\"\\n    \",\"\\n    \",\"\\n\"],\"b\":[{\"x\":\"1\"},{\"x\":\"2\"},{\"x\":\"3\",\"#text\":\"stuff\"},{\"x\":\"4\",\"#text\":\"\\t\\n   \\n\"}]}"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|ignoreWhitespaceTextNodes
parameter_list|()
throws|throws
name|IOException
throws|,
name|TransformerException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
block|{
specifier|final
name|Node
name|xmlDoc
init|=
name|parseXml
argument_list|(
literal|"<a z='99'>"
operator|+
name|EOL
operator|+
literal|"<b x='1'/>"
operator|+
name|EOL
operator|+
literal|"<b x='2'></b>"
operator|+
name|EOL
operator|+
literal|"<b x='3'>stuff</b>"
operator|+
name|EOL
operator|+
literal|"<b x='4'>\t\r\n   \r\n</b>"
operator|+
name|EOL
operator|+
literal|"</a>"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|JSON_IGNORE_WHITESPACE_TEXT_NODES
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
specifier|final
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|properties
argument_list|)
expr_stmt|;
specifier|final
name|Transformer
name|transformer
init|=
name|transformerFactory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
specifier|final
name|SAXResult
name|saxResult
init|=
operator|new
name|SAXResult
argument_list|(
name|serializer
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|xmlDoc
argument_list|)
argument_list|,
name|saxResult
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"z\":\"99\",\"b\":[{\"x\":\"1\"},{\"x\":\"2\"},{\"x\":\"3\",\"#text\":\"stuff\"},{\"x\":\"4\"}]}"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|serializesMixedContent_whenAttrsPresent
parameter_list|()
throws|throws
name|IOException
throws|,
name|TransformerException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
block|{
specifier|final
name|Node
name|xmlDoc
init|=
name|parseXml
argument_list|(
literal|"<a x='y' xx='yy'>"
operator|+
name|EOL
operator|+
literal|"\tbefore-b"
operator|+
name|EOL
operator|+
literal|"\t<b y='z'>before-c<c>c-value</c> after-c</b>"
operator|+
name|EOL
operator|+
literal|"\tafter-b"
operator|+
name|EOL
operator|+
literal|"</a>"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
specifier|final
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|properties
argument_list|)
expr_stmt|;
specifier|final
name|Transformer
name|transformer
init|=
name|transformerFactory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
specifier|final
name|SAXResult
name|saxResult
init|=
operator|new
name|SAXResult
argument_list|(
name|serializer
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|xmlDoc
argument_list|)
argument_list|,
name|saxResult
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"x\":\"y\",\"xx\":\"yy\",\"#text\":[\"\\n\\tbefore-b\\n\\t\",\"\\n\\tafter-b\\n\"],\"b\":{\"y\":\"z\",\"#text\":[\"before-c \",\" after-c\"],\"c\":\"c-value\"}}"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|serializesMixedContent
parameter_list|()
throws|throws
name|IOException
throws|,
name|TransformerException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
block|{
specifier|final
name|Node
name|xmlDoc
init|=
name|parseXml
argument_list|(
literal|"<a>"
operator|+
name|EOL
operator|+
literal|"\tbefore-b"
operator|+
name|EOL
operator|+
literal|"\t<b>before-c<c>c-value</c> after-c</b>"
operator|+
name|EOL
operator|+
literal|"\tafter-b"
operator|+
name|EOL
operator|+
literal|"</a>"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
specifier|final
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|properties
argument_list|)
expr_stmt|;
specifier|final
name|Transformer
name|transformer
init|=
name|transformerFactory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
specifier|final
name|SAXResult
name|saxResult
init|=
operator|new
name|SAXResult
argument_list|(
name|serializer
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|xmlDoc
argument_list|)
argument_list|,
name|saxResult
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"#text\":[\"\\n\\tbefore-b\\n\\t\",\"\\n\\tafter-b\\n\"],\"b\":{\"#text\":[\"before-c \",\" after-c\"],\"c\":\"c-value\"}}"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Document
name|parseXml
parameter_list|(
specifier|final
name|String
name|xmlStr
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|DocumentBuilder
name|documentBuilder
init|=
name|documentBuilderFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|xmlStr
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
init|)
block|{
return|return
name|documentBuilder
operator|.
name|parse
argument_list|(
name|is
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

