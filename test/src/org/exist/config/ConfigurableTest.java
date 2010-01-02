begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|config
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|InputStream
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
name|SAXParser
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
name|SAXParserFactory
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
name|ElementAtExist
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|SAXAdapter
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
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
name|XMLReader
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ConfigurableTest
block|{
name|String
name|config1
init|=
literal|"<instance "
operator|+
literal|"valueString=\"a\" "
operator|+
literal|"valueInteger=\"5\" "
operator|+
literal|"valueInt=\"5\" "
operator|+
literal|"valueboolean=\"true\" "
operator|+
literal|"valueBoolean=\"false\" "
operator|+
literal|"></instance>"
decl_stmt|;
name|String
name|config2
init|=
literal|"<config valueString=\"b\"><instance valueString=\"a\" valueInteger=\"5\"></instance></config>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|config1
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
comment|// initialize xml parser
comment|// we use eXist's in-memory DOM implementation to work
comment|// around a bug in Xerces
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|ConfigElementImpl
name|config
init|=
operator|new
name|ConfigElementImpl
argument_list|(
operator|(
name|ElementAtExist
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|ConfigurableObject
name|object
init|=
operator|new
name|ConfigurableObject
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|object
operator|.
name|some
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|,
name|object
operator|.
name|someInteger
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|object
operator|.
name|simpleInteger
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|object
operator|.
name|defaultInteger
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|object
operator|.
name|someboolean
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|object
operator|.
name|someBoolean
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|subelement
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|config2
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
comment|// initialize xml parser
comment|// we use eXist's in-memory DOM implementation to work
comment|// around a bug in Xerces
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|ConfigElementImpl
name|config
init|=
operator|new
name|ConfigElementImpl
argument_list|(
operator|(
name|ElementAtExist
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|ConfigurableObject
name|object
init|=
operator|new
name|ConfigurableObject
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|object
operator|.
name|some
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|,
name|object
operator|.
name|someInteger
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

