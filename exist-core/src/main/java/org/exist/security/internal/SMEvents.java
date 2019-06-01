begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
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
name|Optional
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
name|config
operator|.
name|Configurable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Configurator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsElement
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
name|persistent
operator|.
name|BinaryDocument
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
name|persistent
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
name|dom
operator|.
name|persistent
operator|.
name|LockedDocument
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
name|persistent
operator|.
name|NodeSet
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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
name|DBSource
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
name|StringSource
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
name|ProcessMonitor
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
name|lock
operator|.
name|Lock
operator|.
name|LockMode
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
name|*
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"events"
argument_list|)
specifier|public
class|class
name|SMEvents
implements|implements
name|Configurable
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/security/events"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"sec-ev"
decl_stmt|;
comment|//security-events //secev //sev
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"script-uri"
argument_list|)
specifier|protected
name|String
name|scriptURI
init|=
literal|""
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"authentication"
argument_list|)
specifier|protected
name|EventAuthentication
name|authentication
init|=
literal|null
decl_stmt|;
specifier|protected
name|SecurityManager
name|sm
decl_stmt|;
specifier|private
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
specifier|public
name|SMEvents
parameter_list|(
name|SecurityManagerImpl
name|sm
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|this
operator|.
name|sm
operator|=
name|sm
expr_stmt|;
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Database
name|getDatabase
parameter_list|()
block|{
return|return
name|sm
operator|.
name|getDatabase
argument_list|()
return|;
block|}
specifier|public
name|SecurityManager
name|getSecurityManager
parameter_list|()
block|{
return|return
name|sm
return|;
block|}
specifier|protected
name|void
name|authenticated
parameter_list|(
name|Subject
name|subject
parameter_list|)
block|{
if|if
condition|(
name|authentication
operator|==
literal|null
condition|)
block|{
comment|//			List<Expression> args = new ArrayList<Expression>(2);
comment|//			args.add(new LiteralValue(context, new StringValue(subject.getRealmId()) ));
comment|//			args.add(new LiteralValue(context, new StringValue(subject.getName()) ));
name|runScript
argument_list|(
name|subject
argument_list|,
name|scriptURI
argument_list|,
literal|null
argument_list|,
name|EventAuthentication
operator|.
name|functionName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|authentication
operator|.
name|onEvent
argument_list|(
name|subject
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|runScript
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|String
name|scriptURI
parameter_list|,
name|String
name|script
parameter_list|,
name|QName
name|functionName
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|args
parameter_list|)
block|{
specifier|final
name|Database
name|db
init|=
name|getDatabase
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|db
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|ofNullable
argument_list|(
name|subject
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|Source
name|source
init|=
name|getQuerySource
argument_list|(
name|broker
argument_list|,
name|scriptURI
argument_list|,
name|script
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
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
specifier|final
name|XQueryContext
name|context
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
specifier|final
name|CompiledXQuery
name|compiled
init|=
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|source
argument_list|)
decl_stmt|;
comment|//            Sequence result = xquery.execute(compiled, subject.getName());
specifier|final
name|ProcessMonitor
name|pm
init|=
name|db
operator|.
name|getProcessMonitor
argument_list|()
decl_stmt|;
comment|//execute the XQuery
try|try
block|{
specifier|final
name|UserDefinedFunction
name|function
init|=
name|context
operator|.
name|resolveFunction
argument_list|(
name|functionName
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|function
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceQueryStart
argument_list|()
expr_stmt|;
name|pm
operator|.
name|queryStarted
argument_list|(
name|context
operator|.
name|getWatchDog
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FunctionCall
name|call
init|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|function
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|setArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Sequence
name|contextSequence
decl_stmt|;
specifier|final
name|ContextItemDeclaration
name|cid
init|=
name|context
operator|.
name|getContextItemDeclartion
argument_list|()
decl_stmt|;
if|if
condition|(
name|cid
operator|!=
literal|null
condition|)
block|{
name|contextSequence
operator|=
name|cid
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contextSequence
operator|=
name|NodeSet
operator|.
name|EMPTY_SET
expr_stmt|;
block|}
name|call
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
comment|//XXX: log
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|pm
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceQueryEnd
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|pm
operator|.
name|queryCompleted
argument_list|(
name|context
operator|.
name|getWatchDog
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|compiled
operator|.
name|reset
argument_list|()
expr_stmt|;
name|context
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
comment|//XXX: log
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Source
name|getQuerySource
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|scriptURI
parameter_list|,
name|String
name|script
parameter_list|)
block|{
if|if
condition|(
name|scriptURI
operator|!=
literal|null
condition|)
block|{
specifier|final
name|XmldbURI
name|pathUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|scriptURI
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|LockedDocument
name|lockedResource
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|pathUri
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|lockedResource
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|DBSource
argument_list|(
name|broker
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|lockedResource
operator|.
name|getDocument
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
comment|//XXX: log
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//			try {
comment|//				querySource = SourceFactory.getSource(broker, null, scriptURI, false);
comment|//			} catch(Exception e) {
comment|//				//LOG.error(e);
comment|//			}
block|}
if|else if
condition|(
name|script
operator|!=
literal|null
operator|&&
operator|!
name|script
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|StringSource
argument_list|(
name|script
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
name|configuration
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
block|}
end_class

end_unit
