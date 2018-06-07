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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
operator|.
name|Txn
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTriggerProxy
parameter_list|<
name|T
extends|extends
name|Trigger
parameter_list|>
implements|implements
name|TriggerProxy
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|clazz
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
decl_stmt|;
specifier|public
name|AbstractTriggerProxy
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
block|}
specifier|public
name|AbstractTriggerProxy
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|clazz
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|getClazz
parameter_list|()
block|{
return|return
name|clazz
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setParameters
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|newInstance
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|)
throws|throws
name|TriggerException
block|{
try|try
block|{
specifier|final
name|T
name|trigger
init|=
name|getClazz
argument_list|()
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|trigger
operator|.
name|configure
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|collection
argument_list|,
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|trigger
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InstantiationException
decl||
name|IllegalAccessException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Unable to instantiate Trigger '"
operator|+
name|getClazz
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"': "
operator|+
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

