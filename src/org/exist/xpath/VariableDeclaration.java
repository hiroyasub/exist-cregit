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
name|SequenceType
import|;
end_import

begin_comment
comment|/**  * A global variable declaration (with: declare variable). Variable bindings within  * for and let expressions are handled by {@link org.exist.xpath.ForExpr} and  * {@link org.exist.xpath.LetExpr}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|VariableDeclaration
extends|extends
name|AbstractExpression
block|{
name|String
name|qname
decl_stmt|;
name|SequenceType
name|sequenceType
init|=
literal|null
decl_stmt|;
name|Expression
name|expression
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|VariableDeclaration
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|String
name|qname
parameter_list|,
name|Expression
name|expr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expr
expr_stmt|;
block|}
comment|/** 	 * Set the sequence type of the variable. 	 *  	 * @param type 	 */
specifier|public
name|void
name|setSequenceType
parameter_list|(
name|SequenceType
name|type
parameter_list|)
block|{
name|this
operator|.
name|sequenceType
operator|=
name|type
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#eval(org.exist.dom.DocumentSet, org.exist.xpath.value.Sequence, org.exist.xpath.value.Item) 	 */
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
name|qname
argument_list|)
argument_list|)
decl_stmt|;
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
name|seq
operator|.
name|getItemType
argument_list|()
argument_list|)
expr_stmt|;
name|sequenceType
operator|.
name|checkCardinality
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
name|var
operator|.
name|setValue
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|var
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#pprint() 	 */
specifier|public
name|String
name|pprint
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#returnsType() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.AbstractExpression#getDependencies() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.AbstractExpression#getCardinality() 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getCardinality
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.AbstractExpression#resetState() 	 */
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

