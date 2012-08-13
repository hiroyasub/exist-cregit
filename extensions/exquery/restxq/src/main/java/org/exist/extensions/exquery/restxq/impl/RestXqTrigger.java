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
name|java
operator|.
name|util
operator|.
name|*
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
name|triggers
operator|.
name|FilteringTrigger
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
name|triggers
operator|.
name|TriggerException
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
name|DocumentImpl
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|CompiledXQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|ExQueryException
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
name|RestXqService
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

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|RestXqTrigger
extends|extends
name|FilteringTrigger
block|{
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
specifier|final
name|int
name|event
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|XmldbURI
name|documentPath
parameter_list|,
specifier|final
name|DocumentImpl
name|existingDocument
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|(
specifier|final
name|int
name|event
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|XmldbURI
name|documentPath
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCreateDocument
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
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCreateDocument
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
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//TOOD ideally the compilation step would be in beforeCreateDocument - but we cant access the new module source at that point!
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|services
init|=
name|findServices
argument_list|(
name|broker
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|registerServices
argument_list|(
name|broker
argument_list|,
name|services
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocument
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
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
name|deregisterServices
argument_list|(
name|broker
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocument
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
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//TOOD ideally the compilation step would be in beforeUpdateDocument - but we cant access the new module source at that point!
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|services
init|=
name|findServices
argument_list|(
name|broker
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|registerServices
argument_list|(
name|broker
argument_list|,
name|services
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCopyDocument
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
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCopyDocument
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
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|XmldbURI
name|oldUri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeMoveDocument
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
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterMoveDocument
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
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|XmldbURI
name|oldUri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeDeleteDocument
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
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
name|deregisterServices
argument_list|(
name|broker
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterDeleteDocument
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
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
specifier|private
name|void
name|registerServices
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|services
parameter_list|)
throws|throws
name|TriggerException
block|{
name|getRegistry
argument_list|(
name|broker
argument_list|)
operator|.
name|register
argument_list|(
name|services
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|deregisterServices
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|XmldbURI
name|xqueryLocation
parameter_list|)
throws|throws
name|TriggerException
block|{
name|getRegistry
argument_list|(
name|broker
argument_list|)
operator|.
name|deregister
argument_list|(
name|xqueryLocation
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|RestXqService
argument_list|>
name|findServices
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
try|try
block|{
specifier|final
name|CompiledXQuery
name|compiled
init|=
name|XQueryCompiler
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|document
argument_list|)
decl_stmt|;
return|return
name|XQueryInspector
operator|.
name|findServices
argument_list|(
name|compiled
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|RestXqServiceCompilationException
name|rxsce
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|rxsce
operator|.
name|getMessage
argument_list|()
argument_list|,
name|rxsce
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ExQueryException
name|eqe
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|eqe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|eqe
argument_list|)
throw|;
block|}
block|}
specifier|private
name|RestXqServiceRegistry
name|getRegistry
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|RestXqServiceRegistryManager
operator|.
name|getRegistry
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
