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
name|DocumentSet
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
name|Variable
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
name|xquery
operator|.
name|value
operator|.
name|SequenceType
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
comment|/**  *<!-- Category: declaration -->  *<xsl:param  *   name = qname  *   select? = expression  *   as? = sequence-type  *   required? = "yes" | "no"  *   tunnel? = "yes" | "no">  *<!-- Content: sequence-constructor -->  *</xsl:param>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Param
extends|extends
name|Declaration
implements|implements
name|Variable
block|{
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
name|required
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
name|Param
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
name|required
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
operator|new
name|QName
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
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
name|attr_name
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
name|attr_name
operator|.
name|equals
argument_list|(
name|REQUIRED
argument_list|)
condition|)
block|{
name|required
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
if|else if
condition|(
name|attr_name
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
if|if
condition|(
name|contextInfo
operator|.
name|getParent
argument_list|()
operator|instanceof
name|Parameted
condition|)
block|{
name|Parameted
name|parameted
init|=
operator|(
name|Parameted
operator|)
name|contextInfo
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|parameted
operator|.
name|addXSLParam
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"wrong parent"
argument_list|)
throw|;
comment|//XXX: error
block|}
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
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
comment|//		if (name != null) {
comment|//			Variable var = getXSLContext().resolveVariable(name);
comment|//			return var.getValue();
comment|//		} else
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
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
name|context
operator|.
name|declareVariable
argument_list|(
name|getName
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"param can't calculated"
argument_list|)
throw|;
comment|//TODO: error?
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
literal|"<xsl:param"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getName
argument_list|()
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
name|getName
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
name|dumper
operator|.
name|display
argument_list|(
literal|" select = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|select
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
name|required
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" required = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|required
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
literal|"</xsl:param>"
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
literal|"<xsl:param"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getName
argument_list|()
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" name = "
operator|+
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
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
name|as
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" as = "
operator|+
name|as
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|required
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" required = "
operator|+
name|required
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|tunnel
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" tunnel = "
operator|+
name|tunnel
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
literal|"</xsl:param> "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * @return the name 	 */
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Sequence
name|val
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getValue
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|select
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
block|}
block|}
return|return
literal|null
return|;
comment|//throw new XPathException("param can't calculated");//TODO: error?
block|}
annotation|@
name|Override
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSequenceType
parameter_list|(
name|SequenceType
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|SequenceType
name|getSequenceType
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setStaticType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|int
name|getStaticType
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isInitialized
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setIsInitialized
parameter_list|(
name|boolean
name|initialized
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setStackPosition
parameter_list|(
name|int
name|position
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|DocumentSet
name|getContextDocs
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setContextDocs
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkType
parameter_list|()
throws|throws
name|XPathException
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

