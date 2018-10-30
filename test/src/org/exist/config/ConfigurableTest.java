begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelRunner
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
name|SAXAdapter
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
name|ExistSAXParserFactory
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
name|io
operator|.
name|FastByteArrayInputStream
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ConfigurableTest
block|{
name|String
name|config1
init|=
literal|"<instance xmlns='http://exist-db.org/Configuration' "
operator|+
literal|"valueString=\"a\" "
operator|+
literal|"valueInt=\"5\" "
operator|+
literal|"valueboolean=\"true\" "
operator|+
literal|"valueBoolean=\"false\" "
operator|+
literal|">"
operator|+
literal|"<valueInteger>5</valueInteger> "
operator|+
literal|"<spice name='black pepper'/>"
operator|+
literal|"<spice name='berbere'/>"
operator|+
literal|"</instance>"
decl_stmt|;
name|String
name|config2
init|=
literal|"<config xmlns='http://exist-db.org/Configuration' valueString=\"b\"><instance valueString=\"a\" valueInteger=\"5\"></instance></config>"
decl_stmt|;
name|String
name|config3
init|=
literal|"<instance xmlns='http://exist-db.org/Configuration' "
operator|+
literal|"valueString=\"a\" "
operator|+
literal|"valueInt=\"5\" "
operator|+
literal|"valueboolean=\"true\" "
operator|+
literal|"valueBoolean=\"false\" "
operator|+
literal|">"
operator|+
literal|"<valueInteger>5</valueInteger> "
operator|+
literal|"<sp name='cool'/>"
operator|+
literal|"<spice name='black pepper'/>"
operator|+
literal|"<spice name='berbere'/>"
operator|+
literal|"</instance>"
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
name|FastByteArrayInputStream
argument_list|(
name|config1
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|is
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
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|object
operator|.
name|spices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"black pepper"
argument_list|,
name|object
operator|.
name|spices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"berbere"
argument_list|,
name|object
operator|.
name|spices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|name
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
name|FastByteArrayInputStream
argument_list|(
name|config2
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
comment|// initialize xml parser
comment|// we use eXist's in-memory DOM implementation to work
comment|// around a bug in Xerces
name|SAXParserFactory
name|factory
init|=
name|ExistSAXParserFactory
operator|.
name|getSAXParserFactory
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
name|ConfigurationImpl
name|config
init|=
operator|new
name|ConfigurationImpl
argument_list|(
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
annotation|@
name|Test
specifier|public
name|void
name|notSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|config3
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|ConfigurableObject2
name|object
init|=
operator|new
name|ConfigurableObject2
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
name|assertEquals
argument_list|(
literal|"cool"
argument_list|,
name|object
operator|.
name|sp
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

