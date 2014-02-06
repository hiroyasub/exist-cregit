begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|CollectionTriggersVisitor
extends|extends
name|AbstractTriggersVisitor
argument_list|<
name|CollectionTrigger
argument_list|>
block|{
specifier|protected
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|CollectionTriggersVisitor
parameter_list|(
name|List
argument_list|<
name|CollectionTrigger
argument_list|>
name|triggers
parameter_list|)
block|{
name|super
argument_list|(
name|triggers
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

