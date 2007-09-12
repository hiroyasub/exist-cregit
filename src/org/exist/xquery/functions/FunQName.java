begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
package|;
end_package

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
literal|"Returns an xs:QName with the namespace URI given in $a. If $a is the empty "
operator|+
literal|"string or the empty sequence, it represents 'no namespace'. The prefix in $b "
operator|+
literal|"is retained in the returned xs:QName value. The local name in the result is "
operator|+
literal|"taken from the local part of $b"
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
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
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: namespace URI is of type '"
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
argument_list|)
throw|;
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
name|namespace
operator|=
literal|""
expr_stmt|;
else|else
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
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:FOCA0002: invalid lexical form of either prefix or local name."
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:FOCA0002: non-empty namespace prefix with empty namespace URI"
argument_list|)
throw|;
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
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

