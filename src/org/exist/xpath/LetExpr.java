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
name|Type
import|;
end_import

begin_comment
comment|/**  * Implements an XQuery let-expression.  *   * @author Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|LetExpr
extends|extends
name|BindingExpression
block|{
specifier|public
name|LetExpr
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
name|context
operator|.
name|pushLocalContext
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|Sequence
name|val
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
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
block|{
name|sequenceType
operator|.
name|checkType
argument_list|(
name|val
operator|.
name|getItemType
argument_list|()
argument_list|)
expr_stmt|;
name|sequenceType
operator|.
name|checkCardinality
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|var
operator|.
name|setValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|Sequence
name|filtered
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
block|{
name|filtered
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
if|if
condition|(
name|whereExpr
operator|.
name|returnsType
argument_list|()
operator|==
name|Type
operator|.
name|BOOLEAN
condition|)
block|{
if|if
condition|(
operator|!
name|filtered
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|else if
condition|(
name|filtered
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|Sequence
name|returnSeq
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|orderSpecs
operator|==
literal|null
condition|)
name|returnSeq
operator|=
name|returnExpr
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|filtered
argument_list|,
literal|null
argument_list|)
expr_stmt|;
else|else
block|{
if|if
condition|(
name|filtered
operator|!=
literal|null
condition|)
name|val
operator|=
name|filtered
expr_stmt|;
name|OrderedValueSequence
name|ordered
init|=
operator|new
name|OrderedValueSequence
argument_list|(
name|docs
argument_list|,
name|orderSpecs
argument_list|,
name|val
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|ordered
operator|.
name|addAll
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|returnSeq
operator|=
name|returnExpr
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|ordered
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|popLocalContext
argument_list|()
expr_stmt|;
return|return
name|returnSeq
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
literal|"let "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|varName
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|sequenceType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|" := "
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
block|}
end_class

end_unit

