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
name|com
operator|.
name|thaiopensource
operator|.
name|util
operator|.
name|PropertyMapBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|validate
operator|.
name|SchemaReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|validate
operator|.
name|ValidateProperty
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|validate
operator|.
name|ValidationDriver
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|validate
operator|.
name|rng
operator|.
name|CompactSchemaReader
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
name|net
operator|.
name|MalformedURLException
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
name|persistent
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
name|storage
operator|.
name|BrokerPool
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
name|validation
operator|.
name|resolver
operator|.
name|unstable
operator|.
name|ExistResolver
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

begin_comment
comment|/**  *   xQuery function for validation of XML instance documents  * using jing for grammars like XSD, Relaxng, nvdl and schematron.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Jing
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|String
name|extendedFunctionTxt
init|=
literal|"Validate document using 'Jing'. Supported grammar documents extensions are \".xsd\" "
operator|+
literal|"\".rng\" \".rnc\" \".sch\" and \".nvdl\". Based on functionality provided by "
operator|+
literal|"'com.thaiopensource.validate.ValidationDriver'."
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
literal|"The grammar document as node (element of returned by fn:doc()), xs:anyURI, "
operator|+
literal|"returned by util:binary-doc() or as a Java file object."
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|brokerPool
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
literal|"jing"
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
literal|"grammar"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|grammarText
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
literal|"jing-report"
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
literal|"grammar"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|grammarText
argument_list|)
block|}
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
name|Jing
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
name|brokerPool
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
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
name|InputSource
name|instance
init|=
literal|null
decl_stmt|;
name|InputSource
name|grammar
init|=
literal|null
decl_stmt|;
try|try
block|{
name|report
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Get inputstream of XML instance document
name|instance
operator|=
name|Shared
operator|.
name|getInputSource
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
comment|// Validate using resource specified in second parameter
name|grammar
operator|=
name|Shared
operator|.
name|getInputSource
argument_list|(
name|args
index|[
literal|1
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
comment|// Special setup for compact notation
specifier|final
name|String
name|grammarUrl
init|=
name|grammar
operator|.
name|getSystemId
argument_list|()
decl_stmt|;
specifier|final
name|SchemaReader
name|schemaReader
init|=
operator|(
operator|(
name|grammarUrl
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".rnc"
argument_list|)
operator|)
operator|)
condition|?
name|CompactSchemaReader
operator|.
name|getInstance
argument_list|()
else|:
literal|null
decl_stmt|;
comment|// Setup validation properties. see Jing interface
specifier|final
name|PropertyMapBuilder
name|properties
init|=
operator|new
name|PropertyMapBuilder
argument_list|()
decl_stmt|;
name|ValidateProperty
operator|.
name|ERROR_HANDLER
operator|.
name|put
argument_list|(
name|properties
argument_list|,
name|report
argument_list|)
expr_stmt|;
comment|// Register resolver for xmldb:exist:/// embedded URLs
specifier|final
name|ExistResolver
name|resolver
init|=
operator|new
name|ExistResolver
argument_list|(
name|brokerPool
argument_list|)
decl_stmt|;
name|ValidateProperty
operator|.
name|URI_RESOLVER
operator|.
name|put
argument_list|(
name|properties
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
name|ValidateProperty
operator|.
name|ENTITY_RESOLVER
operator|.
name|put
argument_list|(
name|properties
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
comment|// Setup driver
specifier|final
name|ValidationDriver
name|driver
init|=
operator|new
name|ValidationDriver
argument_list|(
name|properties
operator|.
name|toPropertyMap
argument_list|()
argument_list|,
name|schemaReader
argument_list|)
decl_stmt|;
comment|// Load schema
name|driver
operator|.
name|loadSchema
argument_list|(
name|grammar
argument_list|)
expr_stmt|;
comment|// Validate XML instance
name|driver
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
name|IOException
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
name|Shared
operator|.
name|closeInputSource
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|Shared
operator|.
name|closeInputSource
argument_list|(
name|grammar
argument_list|)
expr_stmt|;
name|report
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// Create response
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"jing"
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
comment|/* isCalledAs("jing-report") */
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

