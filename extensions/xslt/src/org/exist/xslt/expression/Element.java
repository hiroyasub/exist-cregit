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
name|ElementConstructor
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
name|LiteralValue
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
comment|/**  *<!-- Category: instruction -->  *<xsl:element  *   name = { qname }  *   namespace? = { uri-reference }  *   inherit-namespaces? = "yes" | "no"  *   use-attribute-sets? = qnames  *   type? = qname  *   validation? = "strict" | "lax" | "preserve" | "strip">  *<!-- Content: sequence-constructor -->  *</xsl:element>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Element
extends|extends
name|SimpleConstructor
block|{
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|namespace
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|inherit_namespaces
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|use_attribute_sets
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|type
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|validation
init|=
literal|null
decl_stmt|;
specifier|private
name|ElementConstructor
name|constructor
decl_stmt|;
specifier|public
name|Element
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
name|constructor
operator|=
operator|new
name|ElementConstructor
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|constructor
operator|.
name|setContent
argument_list|(
name|this
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
name|namespace
operator|=
literal|null
expr_stmt|;
name|inherit_namespaces
operator|=
literal|null
expr_stmt|;
name|use_attribute_sets
operator|=
literal|null
expr_stmt|;
name|type
operator|=
literal|null
expr_stmt|;
name|validation
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
name|constructor
operator|.
name|setNameExpr
argument_list|(
operator|new
name|LiteralValue
argument_list|(
operator|(
name|XQueryContext
operator|)
name|context
argument_list|,
operator|new
name|StringValue
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|NAMESPACE
argument_list|)
condition|)
block|{
name|namespace
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
name|INHERIT_NAMESPACES
argument_list|)
condition|)
block|{
name|inherit_namespaces
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
name|USE_ATTRIBUTE_SETS
argument_list|)
condition|)
block|{
name|use_attribute_sets
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
name|TYPE
argument_list|)
condition|)
block|{
name|type
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
name|VALIDATION
argument_list|)
condition|)
block|{
name|validation
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|internalCall
init|=
literal|false
decl_stmt|;
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
operator|!
name|internalCall
condition|)
block|{
name|internalCall
operator|=
literal|true
expr_stmt|;
return|return
name|constructor
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
name|internalCall
operator|=
literal|false
expr_stmt|;
return|return
name|super
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
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
literal|"<xsl:element"
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
name|namespace
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" namespace = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|inherit_namespaces
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" inherit_namespaces = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|inherit_namespaces
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|use_attribute_sets
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" use_attribute_sets = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|use_attribute_sets
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" type = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|validation
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" validation = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|validation
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
literal|"</xsl:element>"
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
literal|"<xsl:element"
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
name|namespace
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" namespace = "
operator|+
name|namespace
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|inherit_namespaces
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" inherit_namespaces = "
operator|+
name|inherit_namespaces
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|use_attribute_sets
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" use_attribute_sets = "
operator|+
name|use_attribute_sets
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" type = "
operator|+
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|validation
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" validation = "
operator|+
name|validation
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
literal|"</xsl:element> "
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

