begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2013 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|json
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_comment
comment|/**  * Used to serialize attribute nodes, which are written as a simple  * "property": "value" pair.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|JSONSimpleProperty
extends|extends
name|JSONNode
block|{
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|public
name|JSONSimpleProperty
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JSONSimpleProperty
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|,
specifier|final
name|boolean
name|isLiteral
parameter_list|)
block|{
name|super
argument_list|(
name|Type
operator|.
name|SIMPLE_PROPERTY_TYPE
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|JSONValue
operator|.
name|escape
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|isLiteral
condition|)
block|{
name|setSerializationType
argument_list|(
name|SerializationType
operator|.
name|AS_LITERAL
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|serialize
parameter_list|(
specifier|final
name|Writer
name|writer
parameter_list|,
specifier|final
name|boolean
name|isRoot
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\" : "
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSerializationType
argument_list|()
operator|!=
name|SerializationType
operator|.
name|AS_LITERAL
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSerializationType
argument_list|()
operator|!=
name|SerializationType
operator|.
name|AS_LITERAL
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|serializeContent
parameter_list|(
specifier|final
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
block|}
block|}
end_class

end_unit

