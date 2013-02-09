begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|DocumentSet
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
name|util
operator|.
name|ExpressionDumper
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Implements the XQuery typeswitch construct.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|TypeswitchExpression
extends|extends
name|AbstractExpression
block|{
comment|/**      * Internal class used to hold a single case clause.      */
specifier|private
class|class
name|Case
block|{
name|SequenceType
name|type
decl_stmt|;
name|Expression
name|returnClause
decl_stmt|;
name|QName
name|variable
decl_stmt|;
specifier|public
name|Case
parameter_list|(
name|SequenceType
name|type
parameter_list|,
name|QName
name|variable
parameter_list|,
name|Expression
name|caseClause
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|variable
operator|=
name|variable
expr_stmt|;
name|this
operator|.
name|returnClause
operator|=
name|caseClause
expr_stmt|;
block|}
block|}
specifier|private
name|Expression
name|operand
decl_stmt|;
specifier|private
name|Case
name|defaultClause
init|=
literal|null
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Case
argument_list|>
name|cases
init|=
operator|new
name|ArrayList
argument_list|<
name|Case
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|public
name|TypeswitchExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|operand
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|operand
operator|=
name|operand
expr_stmt|;
block|}
comment|/**      * Add a case clause with a sequence type and an optional variable declaration.      */
specifier|public
name|void
name|addCase
parameter_list|(
name|SequenceType
name|type
parameter_list|,
name|QName
name|var
parameter_list|,
name|Expression
name|caseClause
parameter_list|)
block|{
name|cases
operator|.
name|add
argument_list|(
operator|new
name|Case
argument_list|(
name|type
argument_list|,
name|var
argument_list|,
name|caseClause
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the default clause with an optional variable declaration.      */
specifier|public
name|void
name|setDefault
parameter_list|(
name|QName
name|var
parameter_list|,
name|Expression
name|defaultClause
parameter_list|)
block|{
name|this
operator|.
name|defaultClause
operator|=
operator|new
name|Case
argument_list|(
literal|null
argument_list|,
name|var
argument_list|,
name|defaultClause
argument_list|)
expr_stmt|;
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
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Sequence
name|opSeq
init|=
name|operand
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
specifier|final
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cases
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Case
name|next
init|=
operator|(
name|Case
operator|)
name|cases
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkType
argument_list|(
name|next
operator|.
name|type
argument_list|,
name|opSeq
argument_list|)
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|variable
operator|!=
literal|null
condition|)
block|{
specifier|final
name|LocalVariable
name|var
init|=
operator|new
name|LocalVariable
argument_list|(
name|next
operator|.
name|variable
argument_list|)
decl_stmt|;
name|var
operator|.
name|setSequenceType
argument_list|(
name|next
operator|.
name|type
argument_list|)
expr_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|opSeq
argument_list|)
expr_stmt|;
name|var
operator|.
name|setContextDocs
argument_list|(
name|operand
operator|.
name|getContextDocSet
argument_list|()
argument_list|)
expr_stmt|;
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|next
operator|.
name|returnClause
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|defaultClause
operator|.
name|variable
operator|!=
literal|null
condition|)
block|{
specifier|final
name|LocalVariable
name|var
init|=
operator|new
name|LocalVariable
argument_list|(
name|defaultClause
operator|.
name|variable
argument_list|)
decl_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|opSeq
argument_list|)
expr_stmt|;
name|var
operator|.
name|setContextDocs
argument_list|(
name|operand
operator|.
name|getContextDocSet
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|defaultClause
operator|.
name|returnClause
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|checkType
parameter_list|(
name|SequenceType
name|type
parameter_list|,
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|int
name|requiredCardinality
init|=
name|type
operator|.
name|getCardinality
argument_list|()
decl_stmt|;
name|int
name|actualCardinality
decl_stmt|;
if|if
condition|(
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|EMPTY
expr_stmt|;
block|}
if|else if
condition|(
name|seq
operator|.
name|hasMany
argument_list|()
condition|)
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|MANY
expr_stmt|;
block|}
else|else
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|ONE
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Cardinality
operator|.
name|checkCardinality
argument_list|(
name|requiredCardinality
argument_list|,
name|actualCardinality
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|seq
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
specifier|final
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|checkType
argument_list|(
name|next
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|operand
operator|.
name|returnsType
argument_list|()
return|;
block|}
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|ZERO_OR_MORE
return|;
block|}
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
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|operand
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
specifier|final
name|LocalVariable
name|mark0
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|Case
name|next
range|:
name|cases
control|)
block|{
specifier|final
name|LocalVariable
name|mark1
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|next
operator|.
name|variable
operator|!=
literal|null
condition|)
block|{
specifier|final
name|LocalVariable
name|var
init|=
operator|new
name|LocalVariable
argument_list|(
name|next
operator|.
name|variable
argument_list|)
decl_stmt|;
name|var
operator|.
name|setSequenceType
argument_list|(
name|next
operator|.
name|type
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
name|next
operator|.
name|returnClause
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|defaultClause
operator|.
name|variable
operator|!=
literal|null
condition|)
block|{
specifier|final
name|LocalVariable
name|var
init|=
operator|new
name|LocalVariable
argument_list|(
name|defaultClause
operator|.
name|variable
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
name|defaultClause
operator|.
name|returnClause
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|operand
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Case
name|next
range|:
name|cases
control|)
block|{
name|next
operator|.
name|returnClause
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setContextDocSet
parameter_list|(
name|DocumentSet
name|contextSet
parameter_list|)
block|{
name|super
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
name|operand
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"typeswitch("
argument_list|,
name|line
argument_list|)
expr_stmt|;
name|operand
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
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
name|cases
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Case
name|caseClause
init|=
operator|(
name|Case
operator|)
name|cases
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"case "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|caseClause
operator|.
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|caseClause
operator|.
name|variable
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|'$'
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|caseClause
operator|.
name|variable
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" as "
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|" return "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|caseClause
operator|.
name|returnClause
argument_list|)
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|"default "
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultClause
operator|.
name|variable
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|'$'
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|defaultClause
operator|.
name|variable
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|defaultClause
operator|.
name|returnClause
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|operand
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|defaultClause
operator|.
name|returnClause
operator|.
name|resetState
argument_list|(
name|postOptimization
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
name|cases
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Case
name|caseClause
init|=
operator|(
name|Case
operator|)
name|cases
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|caseClause
operator|.
name|returnClause
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

