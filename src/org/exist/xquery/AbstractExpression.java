begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|util
operator|.
name|sanity
operator|.
name|SanityCheck
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractExpression
implements|implements
name|Expression
block|{
specifier|private
name|int
name|expressionId
init|=
name|EXPRESSION_ID_INVALID
decl_stmt|;
specifier|protected
name|XQueryContext
name|context
decl_stmt|;
specifier|protected
name|XQueryAST
name|astNode
init|=
literal|null
decl_stmt|;
specifier|protected
name|DocumentSet
name|contextDocSet
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|contextId
init|=
name|Expression
operator|.
name|NO_CONTEXT_ID
decl_stmt|;
specifier|public
name|AbstractExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|expressionId
operator|=
name|context
operator|.
name|nextExpressionId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getExpressionId
parameter_list|()
block|{
name|SanityCheck
operator|.
name|THROW_ASSERT
argument_list|(
name|expressionId
operator|!=
name|EXPRESSION_ID_INVALID
argument_list|,
literal|"The expression "
operator|+
name|toString
argument_list|()
operator|+
literal|" should have a unique id!"
argument_list|)
expr_stmt|;
return|return
name|expressionId
return|;
block|}
specifier|public
name|int
name|getContextId
parameter_list|()
block|{
return|return
name|contextId
return|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
specifier|abstract
name|int
name|returnsType
parameter_list|()
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|contextDocSet
operator|=
literal|null
expr_stmt|;
block|}
comment|/** 	 * The default cardinality is {@link Cardinality#EXACTLY_ONE}. 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|EXACTLY_ONE
return|;
comment|// default cardinality
block|}
comment|/** 	 * Returns {@link Dependency#DEFAULT_DEPENDENCIES}. 	 *  	 * @see org.exist.xquery.Expression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|DEFAULT_DEPENDENCIES
return|;
block|}
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#setContextDocSet(org.exist.dom.DocumentSet)      */
specifier|public
name|void
name|setContextDocSet
parameter_list|(
name|DocumentSet
name|contextSet
parameter_list|)
block|{
name|this
operator|.
name|contextDocSet
operator|=
name|contextSet
expr_stmt|;
block|}
specifier|public
name|DocumentSet
name|getContextDocSet
parameter_list|()
block|{
return|return
name|contextDocSet
return|;
block|}
specifier|public
name|void
name|setASTNode
parameter_list|(
name|XQueryAST
name|ast
parameter_list|)
block|{
name|this
operator|.
name|astNode
operator|=
name|ast
expr_stmt|;
block|}
specifier|public
name|XQueryAST
name|getASTNode
parameter_list|()
block|{
return|return
name|astNode
return|;
block|}
block|}
end_class

end_unit

