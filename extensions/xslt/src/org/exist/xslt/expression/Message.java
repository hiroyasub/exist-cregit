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
name|interpreter
operator|.
name|ContextAtExist
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
name|util
operator|.
name|ExpressionDumper
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
name|pattern
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_comment
comment|/**  *<!-- Category: instruction -->  *<xsl:message  *   select? = expression  *   terminate? = { "yes" | "no" }>  *<!-- Content: sequence-constructor -->  *</xsl:message>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Message
extends|extends
name|SimpleConstructor
block|{
specifier|private
name|PathExpr
name|select
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|terminate
init|=
literal|null
decl_stmt|;
specifier|public
name|Message
parameter_list|(
name|XSLContext
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
name|setToDefaults
parameter_list|()
block|{
name|select
operator|=
literal|null
expr_stmt|;
name|terminate
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|prepareAttribute
parameter_list|(
name|ContextAtExist
name|context
parameter_list|,
name|Attr
name|attr
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|attr_name
init|=
name|attr
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|SELECT
argument_list|)
condition|)
block|{
name|select
operator|=
operator|new
name|PathExpr
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|Pattern
operator|.
name|parse
argument_list|(
operator|(
name|XQueryContext
operator|)
name|context
argument_list|,
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
name|select
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|TERMINATE
argument_list|)
condition|)
block|{
name|terminate
operator|=
name|getBoolean
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"eval(Sequence contextSequence, Item contextItem) at "
operator|+
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"<xsl:message"
argument_list|)
expr_stmt|;
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" select = "
argument_list|)
expr_stmt|;
name|select
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|terminate
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" terminate = "
operator|+
name|terminate
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
name|super
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"</xsl:message>"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<xsl:message"
argument_list|)
expr_stmt|;
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" select = "
operator|+
name|select
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|terminate
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" terminate = "
operator|+
name|terminate
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"</xsl:message>"
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

