begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AnyNodeTest
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
name|Constants
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
name|Expression
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
name|LocationStep
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
name|PathExpr
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
name|TextConstructor
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
name|StringValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|XSLContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|XSLExceptions
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|XSLPathExpr
extends|extends
name|PathExpr
implements|implements
name|XSLExpression
block|{
specifier|public
name|XSLPathExpr
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
name|setToDefaults
argument_list|()
expr_stmt|;
block|}
specifier|public
name|XSLContext
name|getXSLContext
parameter_list|()
block|{
return|return
operator|(
name|XSLContext
operator|)
name|getContext
argument_list|()
return|;
block|}
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|XPathException
block|{
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|this
operator|.
name|getLength
argument_list|()
condition|;
name|pos
operator|++
control|)
block|{
name|Expression
name|expr
init|=
name|this
operator|.
name|getExpression
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|instanceof
name|XSLPathExpr
condition|)
block|{
name|XSLPathExpr
name|xsl
init|=
operator|(
name|XSLPathExpr
operator|)
name|expr
decl_stmt|;
name|xsl
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xslt.instruct.Expression#compileError(java.lang.String) 	 */
specifier|public
name|void
name|compileError
parameter_list|(
name|String
name|error
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|error
argument_list|)
throw|;
block|}
specifier|public
name|Boolean
name|getBoolean
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|YES
argument_list|)
condition|)
return|return
literal|true
return|;
if|else if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|NO
argument_list|)
condition|)
return|return
literal|false
return|;
name|compileError
argument_list|(
name|XSLExceptions
operator|.
name|ERR_XTSE0020
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|_check_
parameter_list|(
name|Expression
name|path
parameter_list|)
block|{
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|path
operator|.
name|getLength
argument_list|()
condition|;
name|pos
operator|++
control|)
block|{
comment|// getLength
name|Expression
name|expr
init|=
name|path
operator|.
name|getExpression
argument_list|(
name|pos
argument_list|)
decl_stmt|;
comment|// getExpression
if|if
condition|(
operator|(
name|pos
operator|==
literal|0
operator|)
operator|&&
operator|(
name|expr
operator|instanceof
name|LocationStep
operator|)
condition|)
block|{
name|LocationStep
name|location
init|=
operator|(
name|LocationStep
operator|)
name|expr
decl_stmt|;
if|if
condition|(
name|location
operator|.
name|getTest
argument_list|()
operator|.
name|isWildcardTest
argument_list|()
condition|)
empty_stmt|;
if|else if
condition|(
name|location
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|CHILD_AXIS
condition|)
block|{
name|location
operator|.
name|setAxis
argument_list|(
name|Constants
operator|.
name|SELF_AXIS
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|_check_
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|_check_childNodes_
parameter_list|(
name|Expression
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|getLength
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|// getLength
name|Expression
name|expr
init|=
name|path
operator|.
name|getExpression
argument_list|(
name|path
operator|.
name|getLength
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// 2x
if|if
condition|(
name|expr
operator|instanceof
name|LocationStep
condition|)
block|{
name|LocationStep
name|location
init|=
operator|(
name|LocationStep
operator|)
name|expr
decl_stmt|;
comment|//TODO: rewrite
if|if
condition|(
name|location
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
condition|)
empty_stmt|;
if|else if
condition|(
operator|!
literal|"node()"
operator|.
name|equals
argument_list|(
name|location
operator|.
name|getTest
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
operator|(
operator|(
name|PathExpr
operator|)
name|path
operator|)
operator|.
name|add
argument_list|(
operator|new
name|LocationStep
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|Constants
operator|.
name|CHILD_AXIS
argument_list|,
operator|new
name|AnyNodeTest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|location
operator|.
name|setAxis
argument_list|(
name|Constants
operator|.
name|CHILD_AXIS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|protected
name|void
name|_check_
parameter_list|(
name|Expression
name|path
parameter_list|,
name|boolean
name|childNodes
parameter_list|)
block|{
name|_check_
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|childNodes
condition|)
name|_check_childNodes_
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addText
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|XPathException
block|{
name|text
operator|=
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|TextConstructor
name|constructer
init|=
operator|new
name|TextConstructor
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|text
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|constructer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

