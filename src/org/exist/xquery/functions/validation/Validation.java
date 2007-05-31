begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|InputStream
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
name|java
operator|.
name|net
operator|.
name|URL
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
name|storage
operator|.
name|io
operator|.
name|ExistIOException
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
name|Validator
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
name|internal
operator|.
name|node
operator|.
name|NodeInputStream
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
name|Validation
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|String
name|simpleFunctionTxt
init|=
literal|"Validate document specified by $a. "
operator|+
literal|"$a is of type xs:anyURI, or a node (element or returned by fn:doc()). "
operator|+
literal|"The grammar files are resolved using the global catalog file(s)."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|extendedFunctionTxt
init|=
literal|"Validate document specified by $a using $b. "
operator|+
literal|"$a is of type xs:anyURI, or a node (element or returned by fn:doc()). "
operator|+
literal|"$b can point to an OASIS catalog file, a grammar (xml schema only) "
operator|+
literal|"or a collection (path ends with '/')"
decl_stmt|;
specifier|private
specifier|final
name|Validator
name|validator
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
literal|"validate"
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
name|simpleFunctionTxt
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"validate"
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
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"validate-report"
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
name|simpleFunctionTxt
operator|+
literal|" A simple report is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"validate-report"
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
literal|" A simple report is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Validation
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
name|validator
operator|=
operator|new
name|Validator
argument_list|(
name|brokerPool
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see BasicFunction#eval(Sequence[], Sequence)      */
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
literal|1
operator|&&
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
comment|// Get inputstream
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|ANY_URI
operator|||
name|args
index|[
literal|0
index|]
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|STRING
condition|)
block|{
comment|// anyURI provided
name|String
name|url
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|url
operator|=
literal|"xmldb:exist://"
operator|+
name|url
expr_stmt|;
block|}
name|is
operator|=
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openStream
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|ELEMENT
operator|||
name|args
index|[
literal|0
index|]
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|DOCUMENT
condition|)
block|{
comment|// Node provided
name|LOG
operator|.
name|info
argument_list|(
literal|"Node"
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|NodeInputStream
argument_list|(
name|context
argument_list|,
name|args
index|[
literal|0
index|]
operator|.
name|iterate
argument_list|()
argument_list|)
expr_stmt|;
comment|// new NodeInputStream()
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Wrong item type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"wrong item type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
comment|//ex.printStackTrace();
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Invalid resource URI"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExistIOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
comment|//ex.getCause().printStackTrace();
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"eXistIOexception"
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
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
comment|//ex.printStackTrace();
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"exception"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|ValidationReport
name|vr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|vr
operator|=
name|validator
operator|.
name|validate
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|url
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|endsWith
argument_list|(
literal|".dtd"
argument_list|)
condition|)
block|{
name|String
name|txt
init|=
literal|"Unable to validate with a specified DTD ("
operator|+
name|url
operator|+
literal|"). "
operator|+
literal|"Please register the DTD in an xml catalog document."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|txt
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|txt
argument_list|)
throw|;
block|}
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|url
operator|=
literal|"xmldb:exist://"
operator|+
name|url
expr_stmt|;
block|}
name|vr
operator|=
name|validator
operator|.
name|validate
argument_list|(
name|is
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
comment|// Create response
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"validate"
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|BooleanValue
argument_list|(
name|vr
operator|.
name|isValid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"validate-report"
argument_list|)
condition|)
block|{
name|String
name|report
index|[]
init|=
name|vr
operator|.
name|getValidationReportArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|report
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|report
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// ohoh
name|LOG
operator|.
name|error
argument_list|(
literal|"invoked with wrong function name"
argument_list|)
expr_stmt|;
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

