begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2007-09 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|MemTreeBuilder
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
name|Module
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
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_class
specifier|public
class|class
name|ExtractDocs
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ExtractDocs
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
literal|"extract-docs"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns an XML document which describes the functions available in a given module. "
operator|+
literal|"The module is identified through its module namespace URI, which is passed as an argument. "
operator|+
literal|"The function returns a module documentation in XQDoc format."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The namespace URI of the function module"
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
name|ZERO_OR_ONE
argument_list|,
literal|"the xqdocs for the function module"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|XQDOC_NS
init|=
literal|"http://www.xqdoc.org/1.0"
decl_stmt|;
specifier|public
name|ExtractDocs
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
name|String
name|moduleURI
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|moduleURI
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|XQDOC_NS
argument_list|,
literal|"xqdoc"
argument_list|,
literal|"xqdoc"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|module
argument_list|(
name|module
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|functions
argument_list|(
name|module
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
operator|(
operator|(
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
specifier|private
name|void
name|functions
parameter_list|(
name|Module
name|module
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|XQDOC_NS
argument_list|,
literal|"functions"
argument_list|,
literal|"functions"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|FunctionSignature
index|[]
name|functions
init|=
name|module
operator|.
name|listFunctions
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|functions
argument_list|,
operator|new
name|FunctionSignatureComparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|functions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FunctionSignature
name|function
init|=
name|functions
index|[
name|i
index|]
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|XQDOC_NS
argument_list|,
literal|"function"
argument_list|,
literal|"function"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"name"
argument_list|,
name|function
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"signature"
argument_list|,
name|function
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|XQDOC_NS
argument_list|,
literal|"comment"
argument_list|,
literal|"comment"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|functionDescription
init|=
name|function
operator|.
name|getDescription
argument_list|()
decl_stmt|;
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"description"
argument_list|,
name|functionDescription
argument_list|)
expr_stmt|;
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|function
operator|.
name|getArgumentTypes
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SequenceType
name|parameter
range|:
name|function
operator|.
name|getArgumentTypes
argument_list|()
control|)
block|{
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"param"
argument_list|,
name|parameterText
argument_list|(
name|parameter
argument_list|,
operator|++
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
init|;
name|index
operator|<
name|function
operator|.
name|getArgumentCount
argument_list|()
condition|;
control|)
block|{
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"param"
argument_list|,
name|parameterText
argument_list|(
literal|null
argument_list|,
operator|++
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|function
operator|.
name|isOverloaded
argument_list|()
condition|)
block|{
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"param"
argument_list|,
literal|"overloaded"
argument_list|)
expr_stmt|;
block|}
name|SequenceType
name|returnValue
init|=
name|function
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
if|if
condition|(
name|returnValue
operator|instanceof
name|FunctionReturnSequenceType
condition|)
block|{
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"return"
argument_list|,
operator|(
operator|(
name|FunctionReturnSequenceType
operator|)
name|returnValue
operator|)
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|deprecated
init|=
name|function
operator|.
name|getDeprecated
argument_list|()
decl_stmt|;
if|if
condition|(
name|deprecated
operator|!=
literal|null
operator|&&
name|deprecated
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"deprecated"
argument_list|,
name|deprecated
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|parameterText
parameter_list|(
name|SequenceType
name|parameter
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|char
name|var
init|=
literal|'a'
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"$"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parameter
operator|!=
literal|null
operator|&&
name|parameter
operator|instanceof
name|FunctionParameterSequenceType
condition|)
block|{
name|FunctionParameterSequenceType
name|funcType
init|=
operator|(
name|FunctionParameterSequenceType
operator|)
name|parameter
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|funcType
operator|.
name|getAttributeName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|funcType
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
name|var
operator|+
name|index
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|module
parameter_list|(
name|Module
name|module
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"library"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|XQDOC_NS
argument_list|,
literal|"module"
argument_list|,
literal|"module"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"uri"
argument_list|,
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"name"
argument_list|,
name|module
operator|.
name|getDefaultPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|XQDOC_NS
argument_list|,
literal|"comment"
argument_list|,
literal|"comment"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"description"
argument_list|,
name|module
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|simpleElement
argument_list|(
name|builder
argument_list|,
literal|"release-version"
argument_list|,
name|module
operator|.
name|getReleaseVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AbstractMethodError
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Problem with function module for ["
operator|+
name|module
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Problem with function module for ["
operator|+
name|module
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|simpleElement
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|,
name|String
name|tag
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|XQDOC_NS
argument_list|,
name|tag
argument_list|,
name|tag
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|value
operator|==
literal|null
condition|?
literal|""
else|:
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|//////////////////////////////////////////////////FunctionSignatureComparator
end_comment

begin_comment
comment|//To sort directories before funcSigs, then alphabetically.
end_comment

begin_class
class|class
name|FunctionSignatureComparator
implements|implements
name|Comparator
argument_list|<
name|FunctionSignature
argument_list|>
block|{
comment|// Comparator interface requires defining compare method.
specifier|public
name|int
name|compare
parameter_list|(
name|FunctionSignature
name|funcSiga
parameter_list|,
name|FunctionSignature
name|funcSigb
parameter_list|)
block|{
comment|//... Sort directories before funcSigs,
comment|//    otherwise alphabetical ignoring case.
return|return
name|funcSiga
operator|.
name|toString
argument_list|()
operator|.
name|compareToIgnoreCase
argument_list|(
name|funcSigb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

