begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

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
name|dom
operator|.
name|memtree
operator|.
name|DocumentImpl
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
name|MemTreeBuilder
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
name|NodeImpl
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
name|validation
operator|.
name|ValidationReport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|validation
operator|.
name|Shared
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|FunctionParameterSequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|FunctionReturnSequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|NodeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|StringReader
import|;
end_import

begin_class
specifier|public
class|class
name|ParsingFunctions
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|RESULT_TYPE_FOR_PARSE_XML
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOCUMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the parsed document"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|RESULT_TYPE_FOR_PARSE_XML_FRAGMENT
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the parsed document fragment"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|TO_BE_PARSED_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The string to be parsed"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ParsingFunctions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parse-xml"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"This function takes as input an XML document represented as a string,"
operator|+
literal|" and returns the document node at the root of an XDM tree representing the parsed document."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|TO_BE_PARSED_PARAMETER
block|}
argument_list|,
name|RESULT_TYPE_FOR_PARSE_XML
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parse-xml-fragment"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"This function takes as input an XML external entity represented as a string,"
operator|+
literal|"and returns the document node at the root of an XDM tree representing the parsed document fragment."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|TO_BE_PARSED_PARAMETER
block|}
argument_list|,
name|RESULT_TYPE_FOR_PARSE_XML_FRAGMENT
argument_list|)
block|}
decl_stmt|;
specifier|public
name|ParsingFunctions
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|resultSequence
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|getItemCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|String
name|xmlContent
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|xmlContent
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"parse-xml-fragment"
argument_list|)
condition|)
block|{
name|xmlContent
operator|=
literal|"<root>"
operator|+
name|xmlContent
operator|+
literal|"</root>"
expr_stmt|;
block|}
specifier|final
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|xmlContent
argument_list|)
decl_stmt|;
specifier|final
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
name|parse
argument_list|(
name|src
argument_list|,
name|context
argument_list|,
name|args
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|parse
parameter_list|(
specifier|final
name|InputSource
name|src
parameter_list|,
name|XQueryContext
name|theContext
parameter_list|,
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|resultSequence
decl_stmt|;
specifier|final
name|ValidationReport
name|report
init|=
operator|new
name|ValidationReport
argument_list|()
decl_stmt|;
specifier|final
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|(
name|theContext
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
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
name|XMLReader
name|xr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|xr
operator|==
literal|null
condition|)
block|{
specifier|final
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|xr
operator|=
name|parser
operator|.
name|getXMLReader
argument_list|()
expr_stmt|;
block|}
name|xr
operator|.
name|setErrorHandler
argument_list|(
name|report
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0002
argument_list|,
literal|"Error while constructing XML parser: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Error while parsing XML: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FODC0006
argument_list|,
name|ErrorCodes
operator|.
name|FODC0006
operator|.
name|getDescription
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|report
operator|.
name|isValid
argument_list|()
condition|)
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"parse-xml-fragment"
argument_list|)
condition|)
block|{
name|resultSequence
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
name|NodeList
name|children
init|=
name|adapter
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|il
init|=
name|children
operator|.
name|getLength
argument_list|()
init|;
name|i
operator|<
name|il
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|resultSequence
operator|.
name|add
argument_list|(
operator|(
name|NodeValue
operator|)
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|resultSequence
return|;
block|}
else|else
block|{
return|return
operator|(
name|DocumentImpl
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
return|;
block|}
block|}
else|else
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|theContext
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
specifier|final
name|NodeImpl
name|result
init|=
name|Shared
operator|.
name|writeReport
argument_list|(
name|report
argument_list|,
name|builder
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FODC0006
argument_list|,
name|ErrorCodes
operator|.
name|FODC0006
operator|.
name|getDescription
argument_list|()
operator|+
literal|": "
operator|+
name|report
operator|.
name|toString
argument_list|()
argument_list|,
name|result
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

