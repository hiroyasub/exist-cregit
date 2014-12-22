begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|validation
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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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
name|stream
operator|.
name|StreamSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|validation
operator|.
name|Schema
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|validation
operator|.
name|SchemaFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|validation
operator|.
name|Validator
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
name|BooleanValue
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

begin_comment
comment|/**  *   xQuery function for validation of XML instance documents  * using grammars like XSDs and DTDs.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Jaxv
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|String
name|extendedFunctionTxt
init|=
literal|"Validate document specified by $instance using the schemas in $grammars. "
operator|+
literal|"Based on functionality provided by 'javax.xml.validation.Validator'. Only "
operator|+
literal|"'.xsd' grammars are supported."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|instanceText
init|=
literal|"The document referenced as xs:anyURI, a node (element or returned by fn:doc()) "
operator|+
literal|"or as a Java file object."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|grammarText
init|=
literal|"One of more XML Schema documents (.xsd), "
operator|+
literal|"referenced as xs:anyURI, a node (element or returned by fn:doc()) "
operator|+
literal|"or as Java file objects."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|languageText
init|=
literal|"The namespace URI to designate a schema language. Depending on the "
operator|+
literal|"jaxv.SchemaFactory implementation the following values are valid:"
operator|+
literal|"(XSD 1.0) http://www.w3.org/2001/XMLSchema http://www.w3.org/XML/XMLSchema/v1.0, "
operator|+
literal|"(XSD 1.1) http://www.w3.org/XML/XMLSchema/v1.1, "
operator|+
literal|"(RELAX NG 1.0) http://relaxng.org/ns/structure/1.0"
decl_stmt|;
comment|// Setup function signature
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
literal|"jaxv"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|extendedFunctionTxt
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"instance"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|instanceText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"grammars"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|languageText
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|Shared
operator|.
name|simplereportText
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"jaxv"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|extendedFunctionTxt
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"instance"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|instanceText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"grammars"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|grammarText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"language"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
name|languageText
argument_list|)
block|,                                      }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|Shared
operator|.
name|simplereportText
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"jaxv-report"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|extendedFunctionTxt
operator|+
literal|" An XML report is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"instance"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|instanceText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"grammars"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|grammarText
argument_list|)
block|,                    }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|Shared
operator|.
name|xmlreportText
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"jaxv-report"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|extendedFunctionTxt
operator|+
literal|" An XML report is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"instance"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|instanceText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"grammars"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|grammarText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"language"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
name|languageText
argument_list|)
block|,                    }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|Shared
operator|.
name|xmlreportText
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Jaxv
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
comment|/**      * @throws org.exist.xquery.XPathException       * @see BasicFunction#eval(Sequence[], Sequence)      */
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
comment|// Check input parameters
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
operator|&&
name|args
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|final
name|ValidationReport
name|report
init|=
operator|new
name|ValidationReport
argument_list|()
decl_stmt|;
name|StreamSource
name|instance
init|=
literal|null
decl_stmt|;
name|StreamSource
name|grammars
index|[]
init|=
literal|null
decl_stmt|;
name|String
name|schemaLang
init|=
name|XMLConstants
operator|.
name|W3C_XML_SCHEMA_NS_URI
decl_stmt|;
try|try
block|{
name|report
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Get inputstream for instance document
name|instance
operator|=
name|Shared
operator|.
name|getStreamSource
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// Validate using resource speciefied in second parameter
name|grammars
operator|=
name|Shared
operator|.
name|getStreamSource
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// Check input
for|for
control|(
specifier|final
name|StreamSource
name|grammar
range|:
name|grammars
control|)
block|{
specifier|final
name|String
name|grammarUrl
init|=
name|grammar
operator|.
name|getSystemId
argument_list|()
decl_stmt|;
if|if
condition|(
name|grammarUrl
operator|!=
literal|null
operator|&&
operator|!
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".xsd"
argument_list|)
operator|&&
operator|!
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".rng"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Only XML schemas (.xsd) and RELAXNG grammars (.rng) are supported"
operator|+
literal|", depending on the used XML parser."
argument_list|)
throw|;
block|}
block|}
comment|// Fetch third argument if available, and override defailt value
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|schemaLang
operator|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
comment|// Get language specific factory
name|SchemaFactory
name|factory
init|=
literal|null
decl_stmt|;
try|try
block|{
name|factory
operator|=
name|SchemaFactory
operator|.
name|newInstance
argument_list|(
name|schemaLang
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Schema language '"
operator|+
name|schemaLang
operator|+
literal|"' is not supported. "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
comment|// Create grammar
specifier|final
name|Schema
name|schema
init|=
name|factory
operator|.
name|newSchema
argument_list|(
name|grammars
argument_list|)
decl_stmt|;
comment|// Setup validator
specifier|final
name|Validator
name|validator
init|=
name|schema
operator|.
name|newValidator
argument_list|()
decl_stmt|;
name|validator
operator|.
name|setErrorHandler
argument_list|(
name|report
argument_list|)
expr_stmt|;
comment|// Perform validation
name|validator
operator|.
name|validate
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedURLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|report
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Shared
operator|.
name|closeStreamSource
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|Shared
operator|.
name|closeStreamSources
argument_list|(
name|grammars
argument_list|)
expr_stmt|;
block|}
comment|// Create response
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"jaxv"
argument_list|)
condition|)
block|{
specifier|final
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|BooleanValue
argument_list|(
name|report
operator|.
name|isValid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
else|else
comment|/* isCalledAs("jaxv-report") */
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
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
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

