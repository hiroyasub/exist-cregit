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
name|xquery
operator|.
name|Constants
operator|.
name|NodeComparisonOperator
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
name|Error
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
name|NodeValue
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
name|Type
import|;
end_import

begin_comment
comment|/**  * Implements node comparisons: is, isnot,&lt;&lt;,&gt;&gt;.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|NodeComparison
extends|extends
name|BinaryOp
block|{
specifier|private
specifier|final
name|NodeComparisonOperator
name|relation
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|NodeComparison
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|NodeComparisonOperator
name|relation
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|relation
operator|=
name|relation
expr_stmt|;
name|add
argument_list|(
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|left
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|NODE_COMP_TYPE_MISMATCH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
comment|//add(new DynamicCardinalityCheck(context, Cardinality.ZERO_OR_ONE, right,
comment|//        new Error(Error.NODE_COMP_TYPE_MISMATCH)));
comment|//add(left);
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#getDependencies()      */
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
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#getCardinality()      */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|ZERO_OR_ONE
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.BinaryOp#returnsType()      */
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
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
name|Sequence
name|result
decl_stmt|;
specifier|final
name|Sequence
name|ls
init|=
name|getLeft
argument_list|()
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|rs
init|=
name|getRight
argument_list|()
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
operator|!
name|ls
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|rs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ls
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
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
literal|"left item is not a node; got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|ls
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rs
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
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
literal|"right item is not a node; got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|rs
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
specifier|final
name|NodeValue
name|lv
init|=
operator|(
name|NodeValue
operator|)
name|ls
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|NodeValue
name|rv
init|=
operator|(
name|NodeValue
operator|)
name|rs
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|lv
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|rv
operator|.
name|getImplementationType
argument_list|()
condition|)
block|{
comment|// different implementations : can't be the same nodes
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|IS
case|:
name|result
operator|=
name|lv
operator|.
name|equals
argument_list|(
name|rv
argument_list|)
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
break|break;
case|case
name|BEFORE
case|:
name|result
operator|=
name|lv
operator|.
name|before
argument_list|(
name|rv
argument_list|,
literal|false
argument_list|)
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
break|break;
case|case
name|AFTER
case|:
name|result
operator|=
name|lv
operator|.
name|after
argument_list|(
name|rv
argument_list|,
literal|false
argument_list|)
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Illegal argument: unknown relation"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|ls
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|rs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rs
operator|.
name|getItemType
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
literal|"The empty sequence cant be an atomic value"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|ls
operator|.
name|isEmpty
argument_list|()
operator|&&
name|rs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ls
operator|.
name|getItemType
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
literal|"The empty sequence cant be an atomic value"
argument_list|)
throw|;
block|}
block|}
name|result
operator|=
name|BooleanValue
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
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
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|getLeft
argument_list|()
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
literal|' '
argument_list|)
operator|.
name|display
argument_list|(
name|relation
operator|.
name|symbol
argument_list|)
operator|.
name|display
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|getRight
argument_list|()
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getLeft
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|' '
operator|+
name|relation
operator|.
name|symbol
operator|+
literal|' '
operator|+
name|getRight
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
