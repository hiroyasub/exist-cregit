begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2012 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|cqlparser
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
name|StringReader
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
name|org
operator|.
name|apache
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
name|xquery
operator|.
name|BasicFunction
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
name|Cardinality
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
name|ErrorCodes
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
name|FunctionSignature
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
name|XPathException
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
name|XQueryContext
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
name|StringValue
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
name|org
operator|.
name|z3950
operator|.
name|zing
operator|.
name|cql
operator|.
name|CQLNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|z3950
operator|.
name|zing
operator|.
name|cql
operator|.
name|CQLParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|z3950
operator|.
name|zing
operator|.
name|cql
operator|.
name|CQLParser
import|;
end_import

begin_comment
comment|/**  * Functions for a Contextual Query Language (CQL) parser.  *  * @author matej  * @author ljo  *  *  */
end_comment

begin_class
specifier|public
class|class
name|ParseCQL
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|String
name|OutputTypeString
init|=
literal|"string"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OutputTypeCQL
init|=
literal|"CQL"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OutputTypeXCQL
init|=
literal|"XCQL"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ParseCQL
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parse-cql"
argument_list|,
name|CQLParserModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|CQLParserModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Parses expressions in the Contextual Query Language (SRU/CQL) v1.2, "
operator|+
literal|"returning it back as XCQL or CQL, based on the second parameter, "
operator|+
literal|"default is XCQL. "
operator|+
literal|"Basic searchClauses (index relation term) can be combined with "
operator|+
literal|"boolean operators."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"expression"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The expression to parse"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"output-as"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Output as 'XCQL' or 'CQL'"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the result"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|ParseCQL
parameter_list|(
name|XQueryContext
name|context
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
name|ret
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|String
name|query
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|output
init|=
literal|"XCQL"
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
name|output
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
try|try
block|{
name|CQLParser
name|parser
init|=
operator|new
name|CQLParser
argument_list|(
name|CQLParser
operator|.
name|V1POINT2
argument_list|)
decl_stmt|;
comment|//      		String local_full_query_string = query;
comment|//      		local_full_query_string = local_full_query_string.replace("-", "%2D");
name|CQLNode
name|query_cql
init|=
name|parser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|output
operator|.
name|equals
argument_list|(
name|OutputTypeXCQL
argument_list|)
condition|)
block|{
name|String
name|xmlContent
init|=
name|query_cql
operator|.
name|toXCQL
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
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|xmlContent
argument_list|)
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|(
name|context
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
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|SAXParser
name|saxParser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|xr
init|=
name|saxParser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
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
name|ret
operator|=
operator|(
name|DocumentImpl
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|output
operator|.
name|equals
argument_list|(
name|OutputTypeString
argument_list|)
condition|)
block|{
name|ret
operator|=
operator|new
name|StringValue
argument_list|(
name|query_cql
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
operator|new
name|StringValue
argument_list|(
name|query_cql
operator|.
name|toCQL
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
catch|catch
parameter_list|(
name|CQLParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An error occurred while parsing the query expression (CQLParseException): "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Error while parsing XML: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
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
literal|"An error occurred while parsing the query expression (IOException): "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
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
literal|"Error while constructing XML parser: "
operator|+
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
end_class

end_unit

