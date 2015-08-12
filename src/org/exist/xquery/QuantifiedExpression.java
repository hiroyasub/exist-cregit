begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|GroupedValueSequenceTable
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
name|Type
import|;
end_import

begin_comment
comment|/**  * Represents a quantified expression: "some ... in ... satisfies",   * "every ... in ... satisfies".  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|QuantifiedExpression
extends|extends
name|BindingExpression
block|{
specifier|public
specifier|final
specifier|static
name|int
name|SOME
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|EVERY
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
name|int
name|mode
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|QuantifiedExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|SOME
case|:
case|case
name|EVERY
case|:
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"QuantifiedExpression"
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.BindingExpression#analyze(org.exist.xquery.Expression, int, org.exist.xquery.OrderSpec[])      */
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
name|context
operator|.
name|declareVariableBinding
argument_list|(
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|varName
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|inputSequence
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|returnExpr
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
name|mark
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|contextItem
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
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|LocalVariable
name|var
init|=
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|varName
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|inSeq
init|=
name|inputSequence
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
block|{
comment|//Type.EMPTY is *not* a subtype of other types ; the tests below would fail without this prior cardinality check
if|if
condition|(
operator|!
name|inSeq
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|inSeq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|sequenceType
operator|.
name|getPrimaryType
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
name|XPTY0004
argument_list|,
literal|"Invalid type for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|inSeq
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|,
name|inSeq
argument_list|)
throw|;
block|}
block|}
name|boolean
name|found
init|=
operator|(
name|mode
operator|==
name|EVERY
operator|)
condition|?
literal|true
else|:
literal|false
decl_stmt|;
name|boolean
name|canDecide
init|=
operator|(
name|mode
operator|==
name|EVERY
operator|)
condition|?
literal|true
else|:
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|inSeq
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
name|canDecide
operator|=
literal|true
expr_stmt|;
specifier|final
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
comment|// set variable value to current item
name|var
operator|.
name|setValue
argument_list|(
name|item
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|==
literal|null
condition|)
block|{
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
block|}
comment|//... because is makes some conversions
name|Sequence
name|satisfiesSeq
init|=
literal|null
decl_stmt|;
comment|//Binds the variable : now in scope
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
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
comment|//Evaluate the return clause for the current value of the variable
name|satisfiesSeq
operator|=
name|returnExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|//Unbind the variable until the next iteration : now out of scope
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|,
name|satisfiesSeq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
block|{
comment|//TODO : ignore nodes right now ; they are returned as xs:untypedAtomicType
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|toSequence
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|,
name|sequenceType
operator|.
name|getPrimaryType
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
name|XPTY0004
argument_list|,
literal|"Invalid type for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|contextItem
operator|.
name|toSequence
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|,
name|inSeq
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
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
name|XPTY0004
argument_list|,
literal|"Invalid type for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|NODE
argument_list|)
operator|+
literal|" (or more specific), got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|inSeq
argument_list|)
throw|;
block|}
comment|//trigger the old behaviour
else|else
block|{
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
block|}
block|}
name|found
operator|=
name|satisfiesSeq
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|mode
operator|==
name|SOME
operator|)
operator|&&
name|found
condition|)
block|{
break|break;
block|}
if|if
condition|(
operator|(
name|mode
operator|==
name|EVERY
operator|)
operator|&&
operator|!
name|found
condition|)
block|{
break|break;
block|}
block|}
specifier|final
name|Sequence
name|result
init|=
name|canDecide
operator|&&
name|found
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
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
name|mode
operator|==
name|SOME
condition|?
literal|"some"
else|:
literal|"every"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" $"
argument_list|)
operator|.
name|display
argument_list|(
name|varName
argument_list|)
operator|.
name|display
argument_list|(
literal|" in"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|inputSequence
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
operator|.
name|nl
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"satisfies"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|returnExpr
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
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|mode
operator|==
name|SOME
condition|?
literal|"some"
else|:
literal|"every"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" $"
argument_list|)
operator|.
name|append
argument_list|(
name|varName
argument_list|)
operator|.
name|append
argument_list|(
literal|" in"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|inputSequence
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"satisfies"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|returnExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|BOOLEAN
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_ITEM
operator||
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
block|}
end_class

end_unit

