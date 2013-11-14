begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTriggersVisitor
parameter_list|<
name|T
extends|extends
name|Trigger
parameter_list|>
implements|implements
name|TriggersVisitor
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|AbstractTriggerProxies
argument_list|<
name|T
argument_list|>
name|proxies
decl_stmt|;
specifier|private
name|List
argument_list|<
name|T
argument_list|>
name|triggers
decl_stmt|;
specifier|public
name|AbstractTriggersVisitor
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|AbstractTriggerProxies
argument_list|<
name|T
argument_list|>
name|proxies
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|proxies
operator|=
name|proxies
expr_stmt|;
block|}
comment|/**      * lazy instantiated      */
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|getTriggers
parameter_list|()
throws|throws
name|TriggerException
block|{
if|if
condition|(
name|triggers
operator|==
literal|null
condition|)
block|{
name|triggers
operator|=
name|proxies
operator|.
name|instantiateTriggers
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
name|triggers
return|;
block|}
block|}
end_class

end_unit

