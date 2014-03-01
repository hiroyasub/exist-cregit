begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
comment|/**  * Implements an XQuery extension expression. An extension expression starts with  * a list of pragmas, followed by an expression enclosed in curly braces. For evaluation  * details check {{@link #eval(Sequence, Item)}.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|ExtensionExpression
extends|extends
name|AbstractExpression
block|{
specifier|private
name|Expression
name|innerExpression
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Pragma
argument_list|>
name|pragmas
init|=
operator|new
name|ArrayList
argument_list|<
name|Pragma
argument_list|>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|public
name|ExtensionExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setExpression
parameter_list|(
name|Expression
name|inner
parameter_list|)
block|{
name|this
operator|.
name|innerExpression
operator|=
name|inner
expr_stmt|;
block|}
specifier|public
name|void
name|addPragma
parameter_list|(
name|Pragma
name|pragma
parameter_list|)
block|{
name|pragmas
operator|.
name|add
argument_list|(
name|pragma
argument_list|)
expr_stmt|;
block|}
comment|/**      * For every pragma in the list, calls {@link Pragma#before(XQueryContext, Expression)} before evaluation.      * The method then tries to call {@link Pragma#eval(Sequence, Item)} on every pragma.      * If a pragma does not return null for this call, the returned Sequence will become the result      * of the extension expression. If more than one pragma returns something for eval, an exception      * will be thrown. If all pragmas return null, we call eval on the original expression and return      * that.      */
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
name|callBefore
argument_list|()
expr_stmt|;
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|Pragma
name|pragma
range|:
name|pragmas
control|)
block|{
name|Sequence
name|temp
init|=
name|pragma
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
name|temp
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|temp
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
name|result
operator|=
name|innerExpression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
block|}
name|callAfter
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|void
name|callAfter
parameter_list|()
throws|throws
name|XPathException
block|{
for|for
control|(
specifier|final
name|Pragma
name|pragma
range|:
name|pragmas
control|)
block|{
name|pragma
operator|.
name|after
argument_list|(
name|context
argument_list|,
name|innerExpression
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|callBefore
parameter_list|()
throws|throws
name|XPathException
block|{
for|for
control|(
specifier|final
name|Pragma
name|pragma
range|:
name|pragmas
control|)
block|{
name|pragma
operator|.
name|before
argument_list|(
name|context
argument_list|,
name|innerExpression
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|innerExpression
operator|.
name|returnsType
argument_list|()
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
specifier|final
name|AnalyzeContextInfo
name|newContext
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Pragma
name|pragma
range|:
name|pragmas
control|)
block|{
name|pragma
operator|.
name|analyze
argument_list|(
name|newContext
argument_list|)
expr_stmt|;
block|}
name|innerExpression
operator|.
name|analyze
argument_list|(
name|newContext
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
for|for
control|(
specifier|final
name|Pragma
name|pragma
range|:
name|pragmas
control|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"(# "
operator|+
name|pragma
operator|.
name|getQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|pragma
operator|.
name|getContents
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|' '
argument_list|)
operator|.
name|display
argument_list|(
name|pragma
operator|.
name|getContents
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|"#)"
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
literal|'{'
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|innerExpression
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
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|'}'
argument_list|)
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#getDependencies()      */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|innerExpression
operator|.
name|getDependencies
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#getCardinality()      */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|innerExpression
operator|.
name|getCardinality
argument_list|()
return|;
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
name|innerExpression
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
name|innerExpression
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getPrimaryAxis
parameter_list|()
block|{
return|return
name|innerExpression
operator|.
name|getPrimaryAxis
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)     * @see org.exist.xquery.AbstractExpression#resetState()     */
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
name|innerExpression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Pragma
name|pragma
range|:
name|pragmas
control|)
block|{
name|pragma
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|innerExpression
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

