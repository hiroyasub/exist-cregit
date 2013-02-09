begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*   *  eXist Open Source Native XML Database   *  Copyright (C) 2001-06 The eXist Project   *  http://exist-db.org   *  http://exist.sourceforge.net   *     *  This program is free software; you can redistribute it and/or   *  modify it under the terms of the GNU Lesser General Public License   *  as published by the Free Software Foundation; either version 2   *  of the License, or (at your option) any later version.   *     *  This program is distributed in the hope that it will be useful,   *  but WITHOUT ANY WARRANTY; without even the implied warranty of   *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the   *  GNU Lesser General Public License for more details.   *     *  You should have received a copy of the GNU Lesser General Public License   *  along with this program; if not, write to the Free Software   *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.   *     *  $Id$   */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|GroupSpec
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

begin_comment
comment|/**  * An Hashtable that containts a GroupedValueSequence for each group. Groups are  * specified by the group specs of a "group by" clause. Used by  * {@link org.exist.xquery.ForExpr} et al.  *   * WARNING : don't use except for experimental "group by" clause  *   * @author Boris Verhaegen (boris.verhaegen@gmail.com)  */
end_comment

begin_class
specifier|public
class|class
name|GroupedValueSequenceTable
extends|extends
name|Hashtable
argument_list|<
name|String
argument_list|,
name|GroupedValueSequence
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1324942298919800292L
decl_stmt|;
specifier|private
name|GroupSpec
name|groupSpecs
index|[]
decl_stmt|;
specifier|private
name|String
name|toGroupVarName
decl_stmt|;
specifier|private
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|GroupedValueSequenceTable
parameter_list|(
name|GroupSpec
name|groupSpecs
index|[]
parameter_list|,
name|String
name|varName
parameter_list|,
name|XQueryContext
name|aContext
parameter_list|)
block|{
name|super
argument_list|(
literal|11
argument_list|,
operator|(
name|float
operator|)
literal|0.75
argument_list|)
expr_stmt|;
comment|// Hashtable parameters
name|this
operator|.
name|groupSpecs
operator|=
name|groupSpecs
expr_stmt|;
name|this
operator|.
name|toGroupVarName
operator|=
name|varName
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|aContext
expr_stmt|;
comment|//UNDERSTAND: do we need context here??? -shabanovd
block|}
specifier|public
name|void
name|setToGroupVarName
parameter_list|(
name|String
name|varName
parameter_list|)
block|{
name|toGroupVarName
operator|=
name|varName
expr_stmt|;
block|}
specifier|public
name|String
name|getToGroupVarName
parameter_list|()
block|{
return|return
name|toGroupVarName
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterate
parameter_list|()
block|{
return|return
name|this
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** 	 * Add<code>item</code> in the correct<code>GroupedValueSequence</code>. 	 * Create correct GroupedValueSequence if needed. Insertion based on the 	 * group specs of a "group by" clause. 	 *  	 * @throws XPathException 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|specEvaluation
index|[]
init|=
operator|new
name|Sequence
index|[
name|groupSpecs
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|ValueSequence
name|keySequence
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groupSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// evaluates the values of the grouping keys
name|specEvaluation
index|[
name|i
index|]
operator|=
name|groupSpecs
index|[
name|i
index|]
operator|.
name|getGroupExpression
argument_list|()
operator|.
name|eval
argument_list|(
name|item
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO : too early evaluation !
if|if
condition|(
name|specEvaluation
index|[
name|i
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|keySequence
operator|.
name|add
argument_list|(
name|AtomicValue
operator|.
name|EMPTY_VALUE
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|specEvaluation
index|[
name|i
index|]
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|keySequence
operator|.
name|add
argument_list|(
name|specEvaluation
index|[
name|i
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|groupSpecs
index|[
name|i
index|]
operator|.
name|getGroupExpression
argument_list|()
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"More that one key values"
argument_list|,
name|specEvaluation
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
specifier|final
name|String
name|hashKey
init|=
name|keySequence
operator|.
name|getHashKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|containsKey
argument_list|(
name|hashKey
argument_list|)
condition|)
block|{
specifier|final
name|GroupedValueSequence
name|currentGroup
init|=
name|super
operator|.
name|get
argument_list|(
name|hashKey
argument_list|)
decl_stmt|;
name|currentGroup
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// this group doesn't exists, then creates this group
specifier|final
name|GroupedValueSequence
name|newGroup
init|=
operator|new
name|GroupedValueSequence
argument_list|(
name|groupSpecs
argument_list|,
literal|1
argument_list|,
name|keySequence
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|newGroup
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|super
operator|.
name|put
argument_list|(
name|hashKey
argument_list|,
name|newGroup
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Add all items of a sequence 	 *  	 * @param sequence 	 * @throws XPathException 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|sequence
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|sequence
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
name|this
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

