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
comment|/**  * Abstract base class for the XQuery/XPath combining operators "union", "intersect"  * and "except".  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CombiningExpression
extends|extends
name|AbstractExpression
block|{
specifier|protected
name|PathExpr
name|left
decl_stmt|,
name|right
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|CombiningExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|PathExpr
name|left
parameter_list|,
name|PathExpr
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#eval(org.exist.dom.DocumentSet, org.exist.xpath.value.Sequence, org.exist.xpath.value.Item) 	 */
specifier|public
specifier|abstract
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
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#pprint() 	 */
specifier|public
specifier|abstract
name|String
name|pprint
parameter_list|()
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|left
operator|.
name|resetState
argument_list|()
expr_stmt|;
name|right
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.AbstractExpression#setPrimaryAxis(int) 	 */
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
name|left
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
name|right
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#setInPredicate(boolean) 	 */
specifier|public
name|void
name|setInPredicate
parameter_list|(
name|boolean
name|inPredicate
parameter_list|)
block|{
name|super
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
name|left
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
name|right
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

