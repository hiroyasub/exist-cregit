begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|Namespaces
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
name|functions
operator|.
name|FunMatches
import|;
end_import

begin_class
specifier|public
class|class
name|ForceIndexUse
extends|extends
name|Pragma
block|{
name|Expression
name|expression
decl_stmt|;
name|boolean
name|bailout
init|=
literal|true
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|EXCEPTION_IF_INDEX_NOT_USED_PRAGMA
init|=
operator|new
name|QName
argument_list|(
literal|"force-index-use"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|public
name|ForceIndexUse
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|qname
argument_list|,
name|contents
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|before
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
specifier|public
name|void
name|after
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
name|expression
operator|.
name|accept
argument_list|(
operator|new
name|DefaultExpressionVisitor
argument_list|()
block|{
specifier|public
name|void
name|visitGeneralComparison
parameter_list|(
name|GeneralComparison
name|expression
parameter_list|)
block|{
name|bailout
operator|=
operator|!
name|expression
operator|.
name|hasUsedIndex
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|visitBuiltinFunction
parameter_list|(
name|Function
name|expression
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|instanceof
name|FunMatches
condition|)
name|bailout
operator|=
operator|!
operator|(
operator|(
name|FunMatches
operator|)
name|expression
operator|)
operator|.
name|hasUsedIndex
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|bailout
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
operator|.
name|getASTNode
argument_list|()
argument_list|,
literal|"XQDYxxxx: Can not use index on expression '"
operator|+
name|expression
operator|+
literal|"'"
argument_list|)
throw|;
comment|/*     	if (expression instanceof PathExpr) {     		PathExpr pe = (PathExpr)expression;     		for (Iterator i = pe.steps.iterator(); i.hasNext();) {                 Expression expr = (Expression) i.next();                 if (expr instanceof GeneralComparison) {                 	if (!((GeneralComparison)expr).hasUsedIndex())                 		throw new XPathException(expression.getASTNode(), "XQDYxxxx: Can not use index");                	                 }                 if (expr instanceof FunMatches) {                 	if (!((FunMatches)expr).hasUsedIndex())                 		throw new XPathException(expression.getASTNode(), "XQDYxxxx: Can not use index");                	                 }              }     	}     	*/
block|}
block|}
end_class

end_unit

