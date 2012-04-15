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
name|dom
operator|.
name|NewArrayNodeSet
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
name|NodeProxy
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
name|NodeSet
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
name|xquery
operator|.
name|AnalyzeContextInfo
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
name|Expression
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
name|Item
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
name|SequenceIterator
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

begin_class
specifier|public
class|class
name|FunNot
extends|extends
name|Function
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
literal|"not"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|" Returns true if the effective boolean "
operator|+
literal|"value is false, and false if the effective boolean value is true. \n\n $arg is reduced to an effective boolean value by applying "
operator|+
literal|"the fn:boolean() function."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The input items"
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
literal|"the negated effective boolean value (ebv) of $arg"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|boolean
name|inWhereClause
init|=
literal|false
decl_stmt|;
specifier|public
name|FunNot
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#analyze(org.exist.xquery.Expression, int)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|inWhereClause
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_WHERE_CLAUSE
operator|)
operator|!=
literal|0
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
comment|//TODO: test for possible performance lost
comment|//return Type.BOOLEAN;
return|return
name|Type
operator|.
name|subTypeOf
argument_list|(
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|?
name|Type
operator|.
name|NODE
else|:
name|Type
operator|.
name|BOOLEAN
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator||
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|getDependencies
argument_list|()
return|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
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
if|if
condition|(
name|contextItem
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
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|Sequence
name|result
decl_stmt|;
name|Expression
name|arg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// case 1: if the argument expression returns a node set,
comment|// subtract the set from the context node set and return
comment|// the remaining set
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|arg
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
operator|(
name|contextSequence
operator|==
literal|null
operator|||
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
operator|)
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|arg
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
block|{
if|if
condition|(
name|contextSequence
operator|==
literal|null
operator|||
name|contextSequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// TODO: special treatment if the context sequence is empty:
comment|// within a predicate, we just return the empty sequence
comment|// otherwise evaluate the argument and return a boolean result
comment|//				if (inPredicate&& !inWhereClause)
comment|//                    result = Sequence.EMPTY_SEQUENCE;
comment|//				else
name|result
operator|=
name|evalBoolean
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
operator|.
name|copy
argument_list|()
expr_stmt|;
if|if
condition|(
name|inPredicate
condition|)
block|{
for|for
control|(
name|SequenceIterator
name|i
init|=
name|result
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|item
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
comment|//    					item.addContextNode(getExpressionId(), item);
if|if
condition|(
name|contextId
operator|!=
name|Expression
operator|.
name|NO_CONTEXT_ID
condition|)
name|item
operator|.
name|addContextNode
argument_list|(
name|contextId
argument_list|,
name|item
argument_list|)
expr_stmt|;
else|else
name|item
operator|.
name|addContextNode
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
block|}
comment|// evaluate argument expression
name|Sequence
name|argSeq
init|=
name|arg
operator|.
name|eval
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|NodeSet
name|argSet
decl_stmt|;
if|if
condition|(
name|contextId
operator|!=
name|Expression
operator|.
name|NO_CONTEXT_ID
condition|)
block|{
name|argSet
operator|=
name|argSeq
operator|.
name|toNodeSet
argument_list|()
operator|.
name|getContextNodes
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|argSet
operator|=
name|argSeq
operator|.
name|toNodeSet
argument_list|()
operator|.
name|getContextNodes
argument_list|(
name|getExpressionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
operator|(
operator|(
name|NodeSet
operator|)
name|result
operator|)
operator|.
name|except
argument_list|(
name|argSet
argument_list|)
expr_stmt|;
block|}
comment|// case 2: simply invert the boolean value
block|}
else|else
block|{
return|return
name|evalBoolean
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|arg
argument_list|)
return|;
block|}
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
comment|/** 	 * @param contextSequence 	 * @param contextItem 	 * @param arg 	 * @return 	 * @throws XPathException 	 */
specifier|private
name|Sequence
name|evalBoolean
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|Expression
name|arg
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|seq
init|=
name|arg
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
return|return
name|seq
operator|.
name|effectiveBooleanValue
argument_list|()
condition|?
name|BooleanValue
operator|.
name|FALSE
else|:
name|BooleanValue
operator|.
name|TRUE
return|;
block|}
block|}
end_class

end_unit

