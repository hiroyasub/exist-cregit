begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2014 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_class
specifier|public
class|class
name|Union
extends|extends
name|CombiningExpression
block|{
specifier|public
name|Union
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|PathExpr
name|left
parameter_list|,
specifier|final
name|PathExpr
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|combine
parameter_list|(
specifier|final
name|Sequence
name|ls
parameter_list|,
specifier|final
name|Sequence
name|rs
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|ls
operator|.
name|isEmpty
argument_list|()
operator|&&
name|rs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
if|else if
condition|(
name|rs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ls
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"union operand is not a node sequence"
argument_list|)
throw|;
block|}
name|result
operator|=
name|ls
expr_stmt|;
block|}
if|else if
condition|(
name|ls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rs
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"union operand is not a node sequence"
argument_list|)
throw|;
block|}
name|result
operator|=
name|rs
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ls
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rs
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"union operand is not a node sequence"
argument_list|)
throw|;
block|}
if|if
condition|(
name|ls
operator|.
name|isPersistentSet
argument_list|()
operator|&&
name|rs
operator|.
name|isPersistentSet
argument_list|()
condition|)
block|{
name|result
operator|=
name|ls
operator|.
name|toNodeSet
argument_list|()
operator|.
name|union
argument_list|(
name|rs
operator|.
name|toNodeSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|ValueSequence
name|values
init|=
operator|new
name|ValueSequence
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|values
operator|.
name|addAll
argument_list|(
name|ls
argument_list|)
expr_stmt|;
name|values
operator|.
name|addAll
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|values
operator|.
name|sortInDocumentOrder
argument_list|()
expr_stmt|;
name|values
operator|.
name|removeDuplicates
argument_list|()
expr_stmt|;
name|result
operator|=
name|values
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getOperatorName
parameter_list|()
block|{
return|return
literal|"union"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visitUnionExpr
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

