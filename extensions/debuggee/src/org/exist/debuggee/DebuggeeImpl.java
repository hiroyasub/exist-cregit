begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|session
operator|.
name|IoSession
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
name|Init
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|SourceFactory
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
name|XQuery
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DebuggeeImpl
implements|implements
name|Debuggee
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Debuggee
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|GET_FEATURES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|SET_GET_FEATURES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|GET_FEATURES
operator|.
name|put
argument_list|(
literal|"language_supports_threads"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|GET_FEATURES
operator|.
name|put
argument_list|(
literal|"language_name"
argument_list|,
literal|"XQuery"
argument_list|)
expr_stmt|;
name|GET_FEATURES
operator|.
name|put
argument_list|(
literal|"language_version"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|GET_FEATURES
operator|.
name|put
argument_list|(
literal|"protocol_version"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|GET_FEATURES
operator|.
name|put
argument_list|(
literal|"supports_async"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|GET_FEATURES
operator|.
name|put
argument_list|(
literal|"breakpoint_types"
argument_list|,
literal|"line"
argument_list|)
expr_stmt|;
name|SET_GET_FEATURES
operator|.
name|put
argument_list|(
literal|"multiple_sessions"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|SET_GET_FEATURES
operator|.
name|put
argument_list|(
literal|"encoding"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|SET_GET_FEATURES
operator|.
name|put
argument_list|(
literal|"max_children"
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
name|SET_GET_FEATURES
operator|.
name|put
argument_list|(
literal|"max_data"
argument_list|,
literal|"1024"
argument_list|)
expr_stmt|;
name|SET_GET_FEATURES
operator|.
name|put
argument_list|(
literal|"max_depth"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DebuggeeConnectionTCP
name|connection
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Session
argument_list|>
name|sessions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Session
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|DebuggeeImpl
parameter_list|()
block|{
name|connection
operator|=
operator|new
name|DebuggeeConnectionTCP
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|joint
parameter_list|(
name|CompiledXQuery
name|compiledXQuery
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|IoSession
name|session
init|=
name|connection
operator|.
name|connect
argument_list|()
decl_stmt|;
if|if
condition|(
name|session
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|//link debugger session& script
name|DebuggeeJointImpl
name|joint
init|=
operator|new
name|DebuggeeJointImpl
argument_list|()
decl_stmt|;
name|joint
operator|.
name|setCompiledScript
argument_list|(
name|compiledXQuery
argument_list|)
expr_stmt|;
name|XQueryContext
name|context
init|=
name|compiledXQuery
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setDebuggeeJoint
argument_list|(
name|joint
argument_list|)
expr_stmt|;
name|String
name|idesession
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|isVarDeclared
argument_list|(
name|Debuggee
operator|.
name|SESSION
argument_list|)
condition|)
block|{
try|try
block|{
name|Variable
name|var
init|=
name|context
operator|.
name|resolveVariable
argument_list|(
name|Debuggee
operator|.
name|SESSION
argument_list|)
decl_stmt|;
name|idesession
operator|=
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
block|}
block|}
name|String
name|idekey
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|isVarDeclared
argument_list|(
name|Debuggee
operator|.
name|IDEKEY
argument_list|)
condition|)
block|{
try|try
block|{
name|Variable
name|var
init|=
name|context
operator|.
name|resolveVariable
argument_list|(
name|Debuggee
operator|.
name|IDEKEY
argument_list|)
decl_stmt|;
name|idekey
operator|=
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
block|}
block|}
name|joint
operator|.
name|continuation
argument_list|(
operator|new
name|Init
argument_list|(
name|session
argument_list|,
name|idesession
argument_list|,
name|idekey
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|start
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|Database
name|db
init|=
literal|null
decl_stmt|;
name|ScriptRunner
name|runner
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
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
comment|// Try to find the XQuery
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|broker
argument_list|,
literal|""
argument_list|,
name|uri
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
return|return
literal|null
return|;
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
name|XQueryContext
name|queryContext
init|=
operator|new
name|XQueryContext
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
comment|// Find correct script load path
name|queryContext
operator|.
name|setModuleLoadPath
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
operator|.
name|removeLastSegment
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|CompiledXQuery
name|compiled
decl_stmt|;
try|try
block|{
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|queryContext
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|String
name|sessionId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|queryContext
operator|.
name|hashCode
argument_list|()
argument_list|)
decl_stmt|;
comment|//link debugger session& script
name|DebuggeeJointImpl
name|joint
init|=
operator|new
name|DebuggeeJointImpl
argument_list|()
decl_stmt|;
name|SessionImpl
name|session
init|=
operator|new
name|SessionImpl
argument_list|()
decl_stmt|;
name|joint
operator|.
name|setCompiledScript
argument_list|(
name|compiled
argument_list|)
expr_stmt|;
name|queryContext
operator|.
name|setDebuggeeJoint
argument_list|(
name|joint
argument_list|)
expr_stmt|;
name|joint
operator|.
name|continuation
argument_list|(
operator|new
name|Init
argument_list|(
name|session
argument_list|,
name|sessionId
argument_list|,
literal|"eXist"
argument_list|)
argument_list|)
expr_stmt|;
name|runner
operator|=
operator|new
name|ScriptRunner
argument_list|(
name|session
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
name|runner
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|joint
operator|.
name|firstExpression
operator|==
literal|null
operator|&&
name|runner
operator|.
name|exception
operator|==
literal|null
operator|&&
name|count
operator|<
literal|10
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|runner
operator|.
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|runner
operator|.
name|exception
throw|;
block|}
if|if
condition|(
name|joint
operator|.
name|firstExpression
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Can't run debug session."
argument_list|)
throw|;
block|}
comment|//queryContext.declareVariable(Debuggee.SESSION, sessionId);
comment|//XXX: make sure that it started up
name|sessions
operator|.
name|put
argument_list|(
name|sessionId
argument_list|,
name|session
argument_list|)
expr_stmt|;
return|return
name|sessionId
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|runner
operator|!=
literal|null
condition|)
name|runner
operator|.
name|stop
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Session
name|getSession
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|sessions
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
end_class

end_unit

