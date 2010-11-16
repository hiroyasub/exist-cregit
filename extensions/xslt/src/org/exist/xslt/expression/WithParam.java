begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|AnalyzeContextInfo
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
name|xquery
operator|.
name|value
operator|.
name|Type
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
comment|/**  *<xsl:with-param  *   name = qname  *   select? = expression  *   as? = sequence-type  *   tunnel? = "yes" | "no">  *<!-- Content: sequence-constructor -->  *</xsl:with-param>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|WithParam
extends|extends
name|Declaration
block|{
specifier|private
name|String
name|attr_name
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|attr_select
init|=
literal|null
decl_stmt|;
specifier|private
name|QName
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|PathExpr
name|select
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|as
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|tunnel
init|=
literal|null
decl_stmt|;
specifier|public
name|WithParam
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
name|attr_select
operator|=
literal|null
expr_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
name|select
operator|=
literal|null
expr_stmt|;
name|as
operator|=
literal|null
expr_stmt|;
name|tunnel
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
name|_attr_name
init|=
name|attr
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|_attr_name
operator|.
name|equals
argument_list|(
name|NAME
argument_list|)
condition|)
block|{
name|attr_name
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|_attr_name
operator|.
name|equals
argument_list|(
name|SELECT
argument_list|)
condition|)
block|{
name|attr_select
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|_attr_name
operator|.
name|equals
argument_list|(
name|AS
argument_list|)
condition|)
block|{
name|as
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|_attr_name
operator|.
name|equals
argument_list|(
name|TUNNEL
argument_list|)
condition|)
block|{
name|tunnel
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
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|attr_name
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|QName
operator|.
name|parse
argument_list|(
name|contextInfo
operator|.
name|getContext
argument_list|()
argument_list|,
name|attr_name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attr_select
operator|!=
literal|null
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
name|contextInfo
operator|.
name|getContext
argument_list|()
argument_list|,
name|attr_select
argument_list|,
name|select
argument_list|)
expr_stmt|;
name|_check_
argument_list|(
name|select
argument_list|,
literal|true
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
name|Sequence
name|result
init|=
name|select
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
name|result
operator|.
name|getItemCount
argument_list|()
operator|!=
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"only one value for param posible."
argument_list|)
throw|;
comment|//TODO: error?
return|return
name|result
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|UNTYPED_ATOMIC
argument_list|)
return|;
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
literal|"<xsl:with-param"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" name = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
name|as
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" as = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|as
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tunnel
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" tunnel = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|tunnel
argument_list|)
expr_stmt|;
block|}
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
literal|"</xsl:with-param>"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<xsl:with-param"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" name = "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" select = "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|select
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|as
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" as = "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|as
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tunnel
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" tunnel = "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|tunnel
argument_list|)
expr_stmt|;
block|}
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
literal|"</xsl:with-param> "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

