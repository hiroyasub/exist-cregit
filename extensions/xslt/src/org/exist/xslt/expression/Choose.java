begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|SequenceIterator
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
name|ValueSequence
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
name|expression
operator|.
name|i
operator|.
name|Parameted
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
comment|/**  *<!-- Category: instruction -->  *<xsl:choose>  *<!-- Content: (xsl:when+, xsl:otherwise?) -->  *</xsl:choose>  *   * @author shabanovd  *  */
end_comment

begin_class
specifier|public
class|class
name|Choose
extends|extends
name|SimpleConstructor
block|{
specifier|private
name|List
argument_list|<
name|When
argument_list|>
name|whens
init|=
operator|new
name|ArrayList
argument_list|<
name|When
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Otherwise
name|otherwise
init|=
literal|null
decl_stmt|;
specifier|public
name|Choose
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
name|whens
operator|=
operator|new
name|ArrayList
argument_list|<
name|When
argument_list|>
argument_list|()
expr_stmt|;
name|otherwise
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|prepareAttribute
parameter_list|(
name|Attr
name|attr
parameter_list|)
throws|throws
name|XPathException
block|{
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
for|for
control|(
name|Expression
name|expr
range|:
name|steps
control|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|When
condition|)
block|{
name|whens
operator|.
name|add
argument_list|(
operator|(
name|When
operator|)
name|expr
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|expr
operator|instanceof
name|Otherwise
condition|)
block|{
name|otherwise
operator|=
operator|(
name|Otherwise
operator|)
name|expr
expr_stmt|;
comment|//TODO: check for double
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"not permited element."
argument_list|)
throw|;
comment|//TODO: error?
block|}
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
for|for
control|(
name|When
name|when
range|:
name|whens
control|)
block|{
if|if
condition|(
name|when
operator|.
name|test
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
condition|)
block|{
return|return
name|when
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|otherwise
operator|!=
literal|null
condition|)
return|return
name|otherwise
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
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
literal|"<xsl:choose"
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
literal|"</xsl:choose>"
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
literal|"<xsl:choose"
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
literal|"</xsl:choose>"
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

