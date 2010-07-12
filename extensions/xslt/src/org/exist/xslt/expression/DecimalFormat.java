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
name|xslt
operator|.
name|XSLContext
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
comment|/**  *<!-- Category: declaration -->  *<xsl:decimal-format  *   name? = qname  *   decimal-separator? = char  *   grouping-separator? = char  *   infinity? = string  *   minus-sign? = char  *   NaN? = string  *   percent? = char  *   per-mille? = char  *   zero-digit? = char  *   digit? = char  *   pattern-separator? = char />  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DecimalFormat
extends|extends
name|Declaration
block|{
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|decimal_separator
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|grouping_separator
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|infinity
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|minus_sign
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|NaN
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|percent
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|per_mille
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|zero_digit
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|digit
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|pattern_separator
init|=
literal|null
decl_stmt|;
specifier|public
name|DecimalFormat
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
name|name
operator|=
literal|null
expr_stmt|;
name|decimal_separator
operator|=
literal|null
expr_stmt|;
name|grouping_separator
operator|=
literal|null
expr_stmt|;
name|infinity
operator|=
literal|null
expr_stmt|;
name|minus_sign
operator|=
literal|null
expr_stmt|;
name|NaN
operator|=
literal|null
expr_stmt|;
name|percent
operator|=
literal|null
expr_stmt|;
name|per_mille
operator|=
literal|null
expr_stmt|;
name|zero_digit
operator|=
literal|null
expr_stmt|;
name|digit
operator|=
literal|null
expr_stmt|;
name|pattern_separator
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
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|NAME
argument_list|)
condition|)
block|{
name|name
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|DECIMAL_SEPARATOR
argument_list|)
condition|)
block|{
name|decimal_separator
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|GROUPING_SEPARATOR
argument_list|)
condition|)
block|{
name|grouping_separator
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|INFINITY
argument_list|)
condition|)
block|{
name|infinity
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|MINUS_SIGN
argument_list|)
condition|)
block|{
name|minus_sign
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|NAN
argument_list|)
condition|)
block|{
name|NaN
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|PERCENT
argument_list|)
condition|)
block|{
name|percent
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|PER_MILLE
argument_list|)
condition|)
block|{
name|per_mille
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|ZERO_DIGIT
argument_list|)
condition|)
block|{
name|zero_digit
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|DIGIT
argument_list|)
condition|)
block|{
name|digit
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|PATTERN_SEPARATOR
argument_list|)
condition|)
block|{
name|pattern_separator
operator|=
name|attr
operator|.
name|getValue
argument_list|()
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
literal|"<xsl:decimal-format"
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
name|decimal_separator
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" decimal_separator = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|decimal_separator
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|grouping_separator
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" grouping_separator = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|grouping_separator
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|infinity
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" infinity = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|infinity
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minus_sign
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" minus_sign = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|minus_sign
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|NaN
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" NaN = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|NaN
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|percent
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" percent = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|percent
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|per_mille
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" per_mille = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|per_mille
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|zero_digit
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" zero_digit = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|zero_digit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|digit
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" digit = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|digit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pattern_separator
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" pattern_separator = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|pattern_separator
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
literal|"</xsl:decimal-format>"
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
literal|"<xsl:decimal-format"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" name = "
operator|+
name|name
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|decimal_separator
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" decimal-separator = "
operator|+
name|decimal_separator
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|grouping_separator
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" grouping-separator = "
operator|+
name|grouping_separator
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|infinity
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" infinity = "
operator|+
name|infinity
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|minus_sign
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" minus-sign = "
operator|+
name|minus_sign
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|NaN
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" NaN = "
operator|+
name|NaN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|percent
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" percent = "
operator|+
name|percent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|per_mille
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" per-mille = "
operator|+
name|per_mille
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|zero_digit
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" zero-digit = "
operator|+
name|zero_digit
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|digit
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" digit = "
operator|+
name|digit
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|pattern_separator
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" pattern-separator = "
operator|+
name|pattern_separator
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
literal|"</xsl:decimal-format> "
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

