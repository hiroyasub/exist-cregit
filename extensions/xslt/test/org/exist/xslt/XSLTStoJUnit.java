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
name|xslt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|util
operator|.
name|Vector
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
name|XMLReader
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|XSLTStoJUnit
implements|implements
name|ContentHandler
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TESTCASES
init|=
literal|"test-group"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TESTCASE
init|=
literal|"testcase"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INPUT
init|=
literal|"input"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STYLESHEET
init|=
literal|"stylesheet"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE_DOCUMENT
init|=
literal|"source-document"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT
init|=
literal|"output"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RESULT_DOCUMENT
init|=
literal|"result-document"
decl_stmt|;
specifier|private
specifier|static
name|File
name|folder
decl_stmt|;
specifier|private
name|String
name|sep
init|=
name|File
operator|.
name|separator
decl_stmt|;
specifier|private
name|BufferedWriter
name|out
decl_stmt|;
specifier|private
name|Vector
argument_list|<
name|String
argument_list|>
name|currentPath
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|stylesheet
decl_stmt|;
specifier|private
name|String
name|sourceDocument
decl_stmt|;
specifier|private
name|String
name|resultDocument
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|folder
operator|=
operator|new
name|File
argument_list|(
literal|"extensions/xslt/test/org/exist/xslt/xslts"
argument_list|)
expr_stmt|;
name|File
name|xslts
init|=
operator|new
name|File
argument_list|(
literal|"test/external/XSLTS_1_1_0/catalog.xml"
argument_list|)
decl_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|xslts
argument_list|)
decl_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
decl_stmt|;
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
name|reader
operator|.
name|setEntityResolver
argument_list|(
operator|new
name|SpecialEntityResolver
argument_list|(
literal|"test/external/XSLTS_1_1_0/"
argument_list|)
argument_list|)
expr_stmt|;
name|XSLTStoJUnit
name|adapter
init|=
operator|new
name|XSLTStoJUnit
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
name|currentPath
operator|.
name|lastElement
argument_list|()
operator|.
name|equals
argument_list|(
name|NAME
argument_list|)
condition|)
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|s
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
name|name
operator|=
operator|new
name|String
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
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
name|currentPath
operator|.
name|remove
argument_list|(
name|currentPath
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|TESTCASES
argument_list|)
condition|)
block|{
try|try
block|{
name|endTestFile
argument_list|()
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
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|TESTCASE
argument_list|)
condition|)
block|{
try|try
block|{
name|writeTestCase
argument_list|()
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
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|writeTestCase
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|"	/* "
operator|+
name|name
operator|+
literal|" */\n"
operator|+
literal|"	@Test\n"
operator|+
literal|"	public void test_"
operator|+
name|adoptString
argument_list|(
name|name
argument_list|)
operator|+
literal|"() throws Exception {\n"
operator|+
literal|"		testCase(\""
operator|+
name|sourceDocument
operator|+
literal|"\", \""
operator|+
name|stylesheet
operator|+
literal|"\", \""
operator|+
name|resultDocument
operator|+
literal|"\");\n"
operator|+
literal|"	}\n\n"
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
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
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
name|currentPath
operator|.
name|add
argument_list|(
name|localName
argument_list|)
expr_stmt|;
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|TESTCASES
argument_list|)
condition|)
block|{
try|try
block|{
name|newTestFile
argument_list|(
name|adoptString
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
argument_list|)
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
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|TESTCASE
argument_list|)
condition|)
block|{
name|name
operator|=
literal|""
expr_stmt|;
name|stylesheet
operator|=
literal|""
expr_stmt|;
name|sourceDocument
operator|=
literal|""
expr_stmt|;
name|resultDocument
operator|=
literal|""
expr_stmt|;
block|}
if|else if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|STYLESHEET
argument_list|)
condition|)
block|{
name|stylesheet
operator|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|SOURCE_DOCUMENT
argument_list|)
condition|)
block|{
name|sourceDocument
operator|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|RESULT_DOCUMENT
argument_list|)
condition|)
block|{
name|resultDocument
operator|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|newTestFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|jTest
init|=
operator|new
name|File
argument_list|(
name|folder
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|sep
operator|+
name|name
operator|+
literal|".java"
argument_list|)
decl_stmt|;
name|FileWriter
name|fstream
init|=
operator|new
name|FileWriter
argument_list|(
name|jTest
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|=
operator|new
name|BufferedWriter
argument_list|(
name|fstream
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"package org.exist.xslt.xslts;\n\n"
operator|+
comment|//   	    		"import org.exist.xquery.xqts.XQTS_case;\n" +
comment|//   	    		"import static org.junit.Assert.*;\n" +
literal|"import org.exist.xslt.XSLTS_case;\n"
operator|+
literal|"import org.junit.Test;\n\n"
operator|+
literal|"public class "
operator|+
name|name
operator|+
literal|" extends XSLTS_case {\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|endTestFile
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
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
block|}
specifier|private
name|String
name|adoptString
parameter_list|(
name|String
name|caseName
parameter_list|)
block|{
if|if
condition|(
name|caseName
operator|.
name|equals
argument_list|(
literal|"for"
argument_list|)
condition|)
return|return
literal|"_for_"
return|;
if|else if
condition|(
name|caseName
operator|.
name|equals
argument_list|(
literal|"if"
argument_list|)
condition|)
return|return
literal|"_if_"
return|;
name|String
name|result
init|=
name|caseName
operator|.
name|replace
argument_list|(
literal|"-"
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

