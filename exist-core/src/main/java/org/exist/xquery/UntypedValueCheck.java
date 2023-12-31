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
name|persistent
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
name|xquery
operator|.
name|parser
operator|.
name|XQueryAST
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
name|*
import|;
end_import

begin_comment
comment|/**  * Runtime-value check for untyped atomic values. Converts a value to the  * required type if possible.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|UntypedValueCheck
extends|extends
name|AbstractExpression
block|{
specifier|private
specifier|final
name|Expression
name|expression
decl_stmt|;
specifier|private
specifier|final
name|int
name|requiredType
decl_stmt|;
specifier|private
specifier|final
name|Error
name|error
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|atomize
decl_stmt|;
specifier|public
name|UntypedValueCheck
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|requiredType
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|requiredType
argument_list|,
name|expression
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|TYPE_MISMATCH
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UntypedValueCheck
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|requiredType
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|Error
name|error
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiredType
operator|=
name|requiredType
expr_stmt|;
if|if
condition|(
name|expression
operator|instanceof
name|Atomize
operator|&&
name|requiredType
operator|!=
name|Type
operator|.
name|ATOMIC
condition|)
block|{
name|this
operator|.
name|expression
operator|=
operator|(
operator|(
name|Atomize
operator|)
name|expression
operator|)
operator|.
name|getExpression
argument_list|()
expr_stmt|;
name|this
operator|.
name|atomize
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|atomize
operator|=
literal|false
expr_stmt|;
block|}
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.AnalyzeContextInfo)      */
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
name|expression
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|Sequence
name|seq
init|=
name|expression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|seq
operator|.
name|hasOne
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|convert
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|item
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
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
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
comment|//Type untyped values or... refine existing type
name|item
operator|=
name|convert
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
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
specifier|private
name|Item
name|convert
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|atomize
operator|||
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|UNTYPED_ATOMIC
operator|||
name|Type
operator|.
name|subTypeOfUnion
argument_list|(
name|requiredType
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
name|Type
operator|.
name|subTypeOfUnion
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|requiredType
argument_list|)
condition|)
block|{
return|return
name|item
return|;
block|}
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|INTEGER
operator|&&
name|requiredType
operator|==
name|Type
operator|.
name|POSITIVE_INTEGER
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0001
argument_list|,
literal|"cannot convert '"
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
operator|+
literal|" ("
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")' into "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
block|}
name|item
operator|=
name|item
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|error
operator|.
name|addArgs
argument_list|(
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expression
argument_list|)
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|,
name|error
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|item
return|;
block|}
comment|/* (non-Javadoc)       * @see org.exist.xquery.Expression#preselect(org.exist.dom.persistent.DocumentSet, org.exist.xquery.StaticContext)       */
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|in_docs
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
literal|"untyped-value-check["
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|expression
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
literal|"]"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|expression
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
name|requiredType
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getDependencies
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#resetState() 	 */
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
name|expression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
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
name|expression
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|expression
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setASTNode
parameter_list|(
name|XQueryAST
name|ast
parameter_list|)
block|{
name|expression
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setLocation
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|)
block|{
name|expression
operator|.
name|setLocation
argument_list|(
name|line
argument_list|,
name|column
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getLine
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getLine
argument_list|()
return|;
block|}
specifier|public
name|int
name|getColumn
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getColumn
argument_list|()
return|;
block|}
specifier|public
name|int
name|getSubExpressionCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
specifier|public
name|Expression
name|getSubExpression
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
return|return
name|expression
return|;
block|}
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Index: "
operator|+
name|index
operator|+
literal|", Size: "
operator|+
name|getSubExpressionCount
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

