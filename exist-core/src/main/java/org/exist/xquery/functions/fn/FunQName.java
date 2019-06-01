begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|util
operator|.
name|XMLNames
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
name|Dependency
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
name|Function
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
name|Profiler
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
name|QNameValue
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
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|FunQName
extends|extends
name|BasicFunction
block|{
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
literal|"QName"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:QName with the namespace URI given in $uri. If $uri is "
operator|+
literal|"the zero-length string or the empty sequence, it represents \"no namespace\"; in "
operator|+
literal|"this case, if the value of $qname contains a colon (:), an error is "
operator|+
literal|"raised [err:FOCA0002]. The prefix (or absence of a prefix) in $qname is "
operator|+
literal|"retained in the returned xs:QName value. The local name in the result is "
operator|+
literal|"taken from the local part of $qname.\n\nIf $qname does not have "
operator|+
literal|"the correct lexical form for xs:QName an error is raised [err:FOCA0002].\n\n"
operator|+
literal|"Note that unlike xs:QName this function does not require a xs:string literal as the argument."
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
name|ZERO_OR_ONE
argument_list|,
literal|"The namespace URI"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"qname"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The prefix"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the xs:QName with the namespace URI given in $uri"
argument_list|)
argument_list|)
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|FunQName
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
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
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
block|}
comment|//TODO : currently useless (but for empty sequences) since the type is forced :-(
if|if
condition|(
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|&&
name|args
index|[
literal|0
index|]
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|STRING
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Namespace URI is of type '"
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
operator|+
literal|"', 'xs:string' expected"
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|)
throw|;
block|}
name|String
name|namespace
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
block|{
name|namespace
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|namespace
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|param
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
name|String
name|localName
init|=
literal|null
decl_stmt|;
try|try
block|{
name|prefix
operator|=
name|QName
operator|.
name|extractPrefix
argument_list|(
name|param
argument_list|)
expr_stmt|;
name|localName
operator|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|param
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
specifier|final
name|ValueSequence
name|argsSeq
init|=
operator|new
name|ValueSequence
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|argsSeq
operator|.
name|addAll
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOCA0002
argument_list|,
literal|"Invalid lexical form of either prefix or local name."
argument_list|,
name|argsSeq
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
operator|&&
operator|(
name|namespace
operator|==
literal|null
operator|||
name|namespace
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
specifier|final
name|ValueSequence
name|argsSeq
init|=
operator|new
name|ValueSequence
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|argsSeq
operator|.
name|addAll
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOCA0002
argument_list|,
literal|"Non-empty namespace prefix with empty namespace URI"
argument_list|,
name|argsSeq
argument_list|)
throw|;
block|}
if|if
condition|(
name|namespace
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|namespace
operator|.
name|equalsIgnoreCase
argument_list|(
name|Namespaces
operator|.
name|XMLNS_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0044
argument_list|,
literal|"'"
operator|+
name|Namespaces
operator|.
name|XMLNS_NS
operator|+
literal|"' can't be use with no prefix"
argument_list|)
throw|;
if|else if
condition|(
operator|!
name|prefix
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0044
argument_list|,
literal|"'"
operator|+
name|Namespaces
operator|.
name|XMLNS_NS
operator|+
literal|"' can't be use with prefix '"
operator|+
name|prefix
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|namespace
operator|.
name|equalsIgnoreCase
argument_list|(
name|Namespaces
operator|.
name|XML_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0044
argument_list|,
literal|"'"
operator|+
name|Namespaces
operator|.
name|XML_NS
operator|+
literal|"' can't be use with no prefix"
argument_list|)
throw|;
if|else if
condition|(
operator|!
name|prefix
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"xml"
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0044
argument_list|,
literal|"'"
operator|+
name|Namespaces
operator|.
name|XML_NS
operator|+
literal|"' can't be use with prefix '"
operator|+
name|prefix
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|prefix
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"xml"
argument_list|)
operator|&&
operator|!
name|namespace
operator|.
name|equalsIgnoreCase
argument_list|(
name|Namespaces
operator|.
name|XML_NS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0044
argument_list|,
literal|"prefix 'xml' can be used only with '"
operator|+
name|Namespaces
operator|.
name|XML_NS
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|QName
name|qname
init|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespace
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
name|namespace
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|//TOCHECK : context.declareInScopeNamespace(prefix, uri) ?
name|context
operator|.
name|declareNamespace
argument_list|(
name|prefix
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
block|}
comment|//context.declareInScopeNamespace(prefix, namespace);
block|}
if|if
condition|(
operator|!
name|XMLNames
operator|.
name|isName
argument_list|(
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOCA0002
argument_list|,
literal|"'"
operator|+
name|qname
operator|.
name|getLocalPart
argument_list|()
operator|+
literal|"' is not a valid local name."
argument_list|)
throw|;
block|}
specifier|final
name|Sequence
name|result
init|=
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit
