begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2012, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqServiceRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
operator|.
name|RestXqServiceRegistryImpl
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|RestXqServiceRegistryManager
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|RestXqServiceRegistryManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|RestXqServiceRegistryImpl
name|registry
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|RestXqServiceRegistryPersistence
name|persistence
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
specifier|synchronized
name|RestXqServiceRegistry
name|getRegistry
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
block|{
if|if
condition|(
name|registry
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialising RESTXQ..."
argument_list|)
expr_stmt|;
name|registry
operator|=
operator|new
name|RestXqServiceRegistryImpl
argument_list|()
expr_stmt|;
comment|//add logging listener
name|registry
operator|.
name|addListener
argument_list|(
operator|new
name|RestXqServiceRegistryLogger
argument_list|()
argument_list|)
expr_stmt|;
comment|//add compiled cache cleanup listener
name|registry
operator|.
name|addListener
argument_list|(
operator|new
name|RestXqServiceCompiledXQueryCacheCleanupListener
argument_list|()
argument_list|)
expr_stmt|;
comment|//add persistence listener
name|persistence
operator|=
operator|new
name|RestXqServiceRegistryPersistence
argument_list|(
name|pool
argument_list|,
name|registry
argument_list|)
expr_stmt|;
comment|//load registry
name|persistence
operator|.
name|loadRegistry
argument_list|()
expr_stmt|;
comment|//NOTE: must load registry before listening for registered events
name|registry
operator|.
name|addListener
argument_list|(
name|persistence
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RESTXQ is ready."
argument_list|)
expr_stmt|;
block|}
return|return
name|registry
return|;
block|}
block|}
end_class

end_unit

