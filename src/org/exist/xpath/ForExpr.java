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
name|xpath
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|IntegerValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
operator|.
name|value
operator|.
name|OrderedValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * Represents an XQuery "for" expression.  *   * @author Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|ForExpr
extends|extends
name|BindingExpression
block|{
specifier|private
name|String
name|positionalVariable
init|=
literal|null
decl_stmt|;
specifier|public
name|ForExpr
parameter_list|(
name|StaticContext
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
name|setPositionalVariable
parameter_list|(
name|String
name|var
parameter_list|)
block|{
name|positionalVariable
operator|=
name|var
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#eval(org.exist.xpath.StaticContext, org.exist.dom.DocumentSet, org.exist.xpath.value.Sequence, org.exist.xpath.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
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
name|result
init|=
literal|null
decl_stmt|;
name|context
operator|.
name|pushLocalContext
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// declare the variable
name|Variable
name|var
init|=
operator|new
name|Variable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|varName
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|var
argument_list|)
expr_stmt|;
comment|// declare positional variable
name|Variable
name|at
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
block|{
name|at
operator|=
operator|new
name|Variable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|positionalVariable
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|at
argument_list|)
expr_stmt|;
block|}
comment|// evaluate the "in" expression
name|Sequence
name|in
init|=
name|inputSequence
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
name|whereExpr
operator|.
name|setInPredicate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
operator|&&
operator|(
name|whereExpr
operator|.
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
operator|&&
name|at
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"using single walk-through"
argument_list|)
expr_stmt|;
name|setContext
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|in
operator|=
name|applyWhereExpression
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|whereExpr
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|orderSpecs
operator|!=
literal|null
condition|)
name|result
operator|=
operator|new
name|OrderedValueSequence
argument_list|(
name|docs
argument_list|,
name|orderSpecs
argument_list|,
name|in
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
name|Sequence
name|val
init|=
literal|null
decl_stmt|;
name|int
name|p
init|=
literal|1
decl_stmt|;
name|IntegerValue
name|atVal
init|=
operator|new
name|IntegerValue
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
name|at
operator|.
name|setValue
argument_list|(
name|atVal
argument_list|)
expr_stmt|;
comment|// loop through each variable binding
for|for
control|(
name|SequenceIterator
name|i
init|=
name|in
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|p
operator|++
control|)
block|{
name|contextItem
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|context
operator|.
name|setContextPosition
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|atVal
operator|.
name|setValue
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextItem
operator|instanceof
name|NodeProxy
condition|)
operator|(
operator|(
name|NodeProxy
operator|)
name|contextItem
operator|)
operator|.
name|addContextNode
argument_list|(
operator|(
name|NodeProxy
operator|)
name|contextItem
argument_list|)
expr_stmt|;
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
comment|// set variable value to current item
name|var
operator|.
name|setValue
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
comment|// check optional where clause
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
block|{
name|val
operator|=
name|applyWhereExpression
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|val
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
continue|continue;
block|}
else|else
name|val
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
comment|// if there is no "order by" clause, immediately call
comment|// the return clause
if|if
condition|(
name|orderSpecs
operator|==
literal|null
condition|)
name|val
operator|=
name|returnExpr
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|orderSpecs
operator|!=
literal|null
condition|)
block|{
comment|// sort the result and call return for every item
operator|(
operator|(
name|OrderedValueSequence
operator|)
name|result
operator|)
operator|.
name|sort
argument_list|()
expr_stmt|;
name|Sequence
name|orderedResult
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|p
operator|=
literal|1
expr_stmt|;
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
name|p
operator|++
control|)
block|{
name|contextItem
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
comment|// set variable value to current item
name|var
operator|.
name|setValue
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
name|context
operator|.
name|setContextPosition
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|val
operator|=
name|returnExpr
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
name|orderedResult
operator|.
name|addAll
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|orderedResult
expr_stmt|;
block|}
name|context
operator|.
name|popLocalContext
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#pprint() 	 */
specifier|public
name|String
name|pprint
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"for "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|varName
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" in "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|inputSequence
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|" where "
argument_list|)
operator|.
name|append
argument_list|(
name|whereExpr
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" return "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|returnExpr
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ITEM
return|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|setContext
parameter_list|(
name|Sequence
name|seq
parameter_list|)
block|{
name|Item
name|next
decl_stmt|;
for|for
control|(
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
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|instanceof
name|NodeProxy
condition|)
operator|(
operator|(
name|NodeProxy
operator|)
name|next
operator|)
operator|.
name|addContextNode
argument_list|(
operator|(
name|NodeProxy
operator|)
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

