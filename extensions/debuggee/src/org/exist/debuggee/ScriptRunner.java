begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dbgp
operator|.
name|packets
operator|.
name|Stop
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
name|xquery
operator|.
name|CompiledXQuery
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
name|XQuery
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ScriptRunner
implements|implements
name|Runnable
implements|,
name|Observer
block|{
specifier|private
name|SessionImpl
name|session
decl_stmt|;
specifier|private
name|CompiledXQuery
name|expression
decl_stmt|;
specifier|private
name|Thread
name|thread
decl_stmt|;
specifier|protected
name|Exception
name|exception
init|=
literal|null
decl_stmt|;
specifier|public
name|ScriptRunner
parameter_list|(
name|SessionImpl
name|session
parameter_list|,
name|CompiledXQuery
name|compiled
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|expression
operator|=
name|compiled
expr_stmt|;
name|thread
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"Debug session "
operator|+
name|compiled
operator|.
name|getContext
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Runnable#run() 	 */
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
specifier|final
name|Database
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|db
operator|.
name|addStatusObserver
argument_list|(
name|this
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|db
operator|.
name|getBroker
argument_list|()
init|)
block|{
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|expression
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//	        XQueryContext context = expression.getContext();
comment|//
comment|//	        expression.reset();
comment|//
comment|//	        context.setBroker(broker);
comment|//	        context.getWatchDog().reset();
comment|//
comment|//	        //do any preparation before execution
comment|//	        context.prepare();
comment|//
comment|//	        context.getProfiler().traceQueryStart();
comment|//	        broker.getBrokerPool().getProcessMonitor().queryStarted(context.getWatchDog());
comment|//	        try {
comment|//	        	Sequence result = expression.eval(null);
comment|//
comment|//	        	if(outputProperties != null)
comment|//	        		context.checkOptions(outputProperties); //must be done before context.reset!
comment|//
comment|//	        	//return result;
comment|//	        } finally {
comment|//	            context.getProfiler().traceQueryEnd(context);
comment|//	            expression.reset();
comment|//                context.reset();
comment|//	        	broker.getBrokerPool().getProcessMonitor().queryCompleted(context.getWatchDog());
comment|//	        }
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|Observable
name|o
parameter_list|,
name|Object
name|arg
parameter_list|)
block|{
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
name|BrokerPool
operator|.
name|SIGNAL_SHUTDOWN
argument_list|)
condition|)
block|{
name|Stop
name|command
init|=
operator|new
name|Stop
argument_list|(
name|session
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|command
operator|.
name|exec
argument_list|()
expr_stmt|;
comment|//TODO: make sure that session is closed? what can be done if not?
block|}
block|}
block|}
end_class

end_unit

