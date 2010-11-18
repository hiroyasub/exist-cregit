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
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
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
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_comment
comment|/**  *<!-- Category: instruction -->  *<xsl:text  *   [disable-output-escaping]? = "yes" | "no">  *<!-- Content: #PCDATA -->  *</xsl:text>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Text
extends|extends
name|SimpleConstructor
block|{
specifier|private
name|String
name|text
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|disable_output_escaping
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isWhitespaceOnly
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|sequenceItSelf
init|=
literal|false
decl_stmt|;
specifier|public
name|Text
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
name|Text
parameter_list|(
name|XSLContext
name|context
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
specifier|public
name|void
name|setToDefaults
parameter_list|()
block|{
name|disable_output_escaping
operator|=
literal|null
expr_stmt|;
name|sequenceItSelf
operator|=
literal|false
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
name|DISABLE_OUTPUT_ESCAPING
argument_list|)
condition|)
block|{
name|disable_output_escaping
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
name|isWhitespaceOnly
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|StringValue
operator|.
name|expand
argument_list|(
name|text
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|!
name|isWhiteSpace
argument_list|(
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|isWhitespaceOnly
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
comment|//TODO: The text node does not have an ancestor element that has an xml:space attribute with a value of preserve, unless there is a closer ancestor element having an xml:space attribute with a value of default.
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
if|if
condition|(
name|isWhitespaceOnly
operator|&&
name|context
operator|.
name|stripWhitespace
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
if|if
condition|(
name|sequenceItSelf
condition|)
block|{
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
return|return
operator|new
name|StringValue
argument_list|(
name|text
argument_list|)
return|;
name|getContext
argument_list|()
operator|.
name|setStripWhitespace
argument_list|(
literal|false
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
name|Text
condition|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
operator|(
operator|(
name|Text
operator|)
name|expr
operator|)
operator|.
name|text
argument_list|)
return|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"unsupported subelement"
argument_list|)
throw|;
block|}
name|getContext
argument_list|()
operator|.
name|setStripWhitespace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newDocumentContext
condition|)
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|int
name|nodeNr
init|=
name|builder
operator|.
name|characters
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|NodeImpl
name|node
init|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
decl_stmt|;
return|return
name|node
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|newDocumentContext
condition|)
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
comment|//		return super.eval(contextSequence, contextItem);
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
literal|"<xsl:text"
argument_list|)
expr_stmt|;
if|if
condition|(
name|disable_output_escaping
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" disable_output_escaping = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|disable_output_escaping
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|">"
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
literal|"</xsl:text>"
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
literal|"<xsl:text"
argument_list|)
expr_stmt|;
if|if
condition|(
name|disable_output_escaping
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" disable_output_escaping = "
operator|+
name|disable_output_escaping
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
literal|"</xsl:text> "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowMixNodesInReturn
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|protected
specifier|final
specifier|static
name|boolean
name|isWhiteSpace
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
operator|(
name|ch
operator|==
literal|0x20
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0x09
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0xD
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0xA
operator|)
return|;
block|}
block|}
end_class

end_unit

