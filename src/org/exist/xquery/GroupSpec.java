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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
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
name|xquery
operator|.
name|util
operator|.
name|ExpressionDumper
import|;
end_import

begin_comment
comment|/**   * A XQuery grouping specifier as specified in an "group by" clause (based on   * {@link org.exist.xquery.OrderSpec}).   *    * Used by {@link org.exist.xquery.BindingExpression}.    *  * @author boris  * @author Wolfgang  */
end_comment

begin_class
specifier|public
class|class
name|GroupSpec
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|private
name|Expression
name|expression
decl_stmt|;
specifier|private
name|QName
name|keyVarName
init|=
literal|null
decl_stmt|;
specifier|private
name|Collator
name|collator
decl_stmt|;
specifier|public
name|GroupSpec
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|groupExpr
parameter_list|,
name|QName
name|keyVarName
parameter_list|)
block|{
if|if
condition|(
name|groupExpr
operator|==
literal|null
condition|)
block|{
comment|// Spec: "If the GroupingSpec does not contain an ExprSingle, an implicit
comment|// expression is created, consisting of a variable reference with the
comment|// same name as the grouping variable."
name|groupExpr
operator|=
operator|new
name|VariableReference
argument_list|(
name|context
argument_list|,
name|keyVarName
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|expression
operator|=
name|groupExpr
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|keyVarName
operator|=
name|keyVarName
expr_stmt|;
name|this
operator|.
name|collator
operator|=
name|context
operator|.
name|getDefaultCollator
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setCollator
parameter_list|(
name|String
name|collation
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|collator
operator|=
name|context
operator|.
name|getCollator
argument_list|(
name|collation
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Collator
name|getCollator
parameter_list|()
block|{
return|return
name|this
operator|.
name|collator
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
name|expression
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Expression
name|getGroupExpression
parameter_list|()
block|{
return|return
name|expression
return|;
block|}
specifier|public
name|QName
name|getKeyVarName
parameter_list|()
block|{
return|return
name|this
operator|.
name|keyVarName
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"$"
operator|+
name|keyVarName
operator|+
literal|" := "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expression
argument_list|)
return|;
block|}
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
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
name|replace
parameter_list|(
name|Expression
name|oldExpr
parameter_list|,
name|Expression
name|newExpr
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|==
name|oldExpr
condition|)
block|{
name|expression
operator|=
name|newExpr
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|instanceof
name|GroupSpec
operator|&&
operator|(
operator|(
name|GroupSpec
operator|)
name|obj
operator|)
operator|.
name|keyVarName
operator|.
name|equals
argument_list|(
name|keyVarName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

