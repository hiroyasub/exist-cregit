begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_comment
comment|/**  * Runtime-check for the cardinality of a function parameter.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DynamicCardinalityCheck
extends|extends
name|AbstractExpression
block|{
specifier|private
name|Expression
name|expression
decl_stmt|;
specifier|private
name|int
name|requiredCardinality
decl_stmt|;
specifier|private
name|Error
name|error
decl_stmt|;
specifier|public
name|DynamicCardinalityCheck
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|requiredCardinality
parameter_list|,
name|Expression
name|expr
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
name|requiredCardinality
operator|=
name|requiredCardinality
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expr
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XPathException
block|{
name|expression
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|int
name|items
init|=
name|seq
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|items
operator|>
literal|0
operator|&&
name|requiredCardinality
operator|==
name|Cardinality
operator|.
name|EMPTY
condition|)
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
name|Cardinality
operator|.
name|toString
argument_list|(
name|requiredCardinality
argument_list|)
argument_list|,
operator|new
name|Integer
argument_list|(
name|items
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
name|error
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|items
operator|==
literal|0
operator|&&
operator|(
name|requiredCardinality
operator|&
name|Cardinality
operator|.
name|ZERO
operator|)
operator|==
literal|0
condition|)
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
name|Cardinality
operator|.
name|toString
argument_list|(
name|requiredCardinality
argument_list|)
argument_list|,
operator|new
name|Integer
argument_list|(
name|items
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
name|error
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|else if
condition|(
name|items
operator|>
literal|1
operator|&&
operator|(
name|requiredCardinality
operator|&
name|Cardinality
operator|.
name|MANY
operator|)
operator|==
literal|0
condition|)
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
name|Cardinality
operator|.
name|toString
argument_list|(
name|requiredCardinality
argument_list|)
argument_list|,
operator|new
name|Integer
argument_list|(
name|items
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
name|error
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|seq
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
if|if
condition|(
name|dumper
operator|.
name|verbosity
argument_list|()
operator|>
literal|1
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"#cardinality("
argument_list|)
expr_stmt|;
block|}
name|expression
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
if|if
condition|(
name|dumper
operator|.
name|verbosity
argument_list|()
operator|>
literal|1
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|expression
operator|.
name|returnsType
argument_list|()
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
parameter_list|()
block|{
name|expression
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

