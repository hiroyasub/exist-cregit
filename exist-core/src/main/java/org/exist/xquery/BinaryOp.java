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

begin_class
specifier|public
specifier|abstract
class|class
name|BinaryOp
extends|extends
name|PathExpr
block|{
specifier|protected
name|boolean
name|inWhereClause
init|=
literal|false
decl_stmt|;
specifier|public
name|BinaryOp
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
specifier|public
name|Expression
name|getLeft
parameter_list|()
block|{
return|return
name|getExpression
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|getRight
parameter_list|()
block|{
return|return
name|getExpression
argument_list|(
literal|1
argument_list|)
return|;
block|}
specifier|public
name|void
name|setLeft
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|steps
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setRight
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|steps
operator|.
name|add
argument_list|(
literal|1
argument_list|,
name|expr
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
name|getLeft
argument_list|()
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
name|getRight
argument_list|()
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.PathExpr#analyze(org.exist.xquery.Expression)      */
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
name|inPredicate
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_PREDICATE
operator|)
operator|!=
literal|0
expr_stmt|;
name|contextId
operator|=
name|contextInfo
operator|.
name|getContextId
argument_list|()
expr_stmt|;
name|inWhereClause
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_WHERE_CLAUSE
operator|)
operator|!=
literal|0
expr_stmt|;
name|getLeft
argument_list|()
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
argument_list|)
expr_stmt|;
name|getRight
argument_list|()
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.Expression#eval(org.exist.xquery.value.Sequence,      *          org.exist.xquery.value.Item)      */
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
specifier|public
name|Expression
name|simplify
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

