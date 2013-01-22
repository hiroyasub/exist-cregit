begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|scheduler
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
name|quartz
operator|.
name|JobDataMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|JobExecutionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|JobExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|DocumentImpl
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
name|Subject
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
name|xacml
operator|.
name|AccessContext
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
name|storage
operator|.
name|XQueryPool
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
name|StringValue
import|;
end_import

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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Class to represent a User's XQuery Job Extends UserJob.  *  * @author  Adam Retter<adam@exist-db.org>  * @author  Andrzej Taramina<andrzej@chaeron.com>  */
end_comment

begin_class
specifier|public
class|class
name|UserXQueryJob
extends|extends
name|UserJob
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|UserXQueryJob
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|DEFAULT_JOB_NAME_PREFIX
init|=
literal|"XQuery"
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|String
name|xqueryResource
decl_stmt|;
specifier|private
specifier|final
name|Subject
name|user
decl_stmt|;
comment|/**      * Default Constructor for Quartz.      */
specifier|public
name|UserXQueryJob
parameter_list|()
block|{
name|xqueryResource
operator|=
literal|null
expr_stmt|;
name|user
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Constructor for Creating a new XQuery User Job.      *      * @param  jobName         The name of the job      * @param  xqueryResource  The XQuery itself      * @param  user            The user under which the xquery should be executed      */
specifier|public
name|UserXQueryJob
parameter_list|(
specifier|final
name|String
name|jobName
parameter_list|,
specifier|final
name|String
name|xqueryResource
parameter_list|,
specifier|final
name|Subject
name|user
parameter_list|)
block|{
name|this
operator|.
name|xqueryResource
operator|=
name|xqueryResource
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
if|if
condition|(
name|jobName
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|name
operator|=
name|DEFAULT_JOB_NAME_PREFIX
operator|+
literal|": "
operator|+
name|xqueryResource
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|name
operator|=
name|jobName
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setName
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Returns the XQuery Resource for this Job.      *      * @return  The XQuery Resource for this Job      */
specifier|protected
name|String
name|getXQueryResource
parameter_list|()
block|{
return|return
name|xqueryResource
return|;
block|}
comment|/**      * Returns the User for this Job.      *      * @return  The User for this Job      */
specifier|protected
name|Subject
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|execute
parameter_list|(
specifier|final
name|JobExecutionContext
name|jec
parameter_list|)
throws|throws
name|JobExecutionException
block|{
specifier|final
name|JobDataMap
name|jobDataMap
init|=
name|jec
operator|.
name|getJobDetail
argument_list|()
operator|.
name|getJobDataMap
argument_list|()
decl_stmt|;
comment|//TODO why are these values not used from the class members?
specifier|final
name|String
name|xqueryresource
init|=
operator|(
name|String
operator|)
name|jobDataMap
operator|.
name|get
argument_list|(
literal|"xqueryresource"
argument_list|)
decl_stmt|;
specifier|final
name|Subject
name|user
init|=
operator|(
name|Subject
operator|)
name|jobDataMap
operator|.
name|get
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
operator|(
name|BrokerPool
operator|)
name|jobDataMap
operator|.
name|get
argument_list|(
literal|"brokerpool"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|params
init|=
operator|(
name|Properties
operator|)
name|jobDataMap
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|unschedule
init|=
operator|(
operator|(
name|Boolean
operator|)
name|jobDataMap
operator|.
name|get
argument_list|(
literal|"unschedule"
argument_list|)
operator|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
comment|//if invalid arguments then abort
if|if
condition|(
operator|(
name|pool
operator|==
literal|null
operator|)
operator|||
operator|(
name|xqueryresource
operator|==
literal|null
operator|)
operator|||
operator|(
name|user
operator|==
literal|null
operator|)
condition|)
block|{
name|abort
argument_list|(
literal|"BrokerPool or XQueryResource or User was null!"
argument_list|)
expr_stmt|;
block|}
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|resource
init|=
literal|null
decl_stmt|;
name|Source
name|source
init|=
literal|null
decl_stmt|;
name|XQueryPool
name|xqPool
init|=
literal|null
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
literal|null
decl_stmt|;
name|XQueryContext
name|context
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//get the xquery
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|xqueryresource
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|>
literal|0
condition|)
block|{
name|source
operator|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|broker
argument_list|,
literal|""
argument_list|,
name|xqueryresource
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmldbURI
name|pathUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|xqueryresource
argument_list|)
decl_stmt|;
name|resource
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|pathUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|source
operator|=
operator|new
name|DBSource
argument_list|(
name|broker
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|resource
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
comment|//execute the xquery
specifier|final
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|xqPool
operator|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
expr_stmt|;
comment|//try and get a pre-compiled query from the pool
name|compiled
operator|=
name|xqPool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
block|{
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|REST
argument_list|)
expr_stmt|;
comment|//TODO should probably have its own AccessContext.SCHEDULER
block|}
else|else
block|{
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
comment|//TODO: don't hardcode this?
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|setModuleLoadPath
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI
operator|.
name|append
argument_list|(
name|resource
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
operator|new
name|XmldbURI
index|[]
block|{
name|resource
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|abort
argument_list|(
literal|"Failed to read query from "
operator|+
name|xqueryresource
argument_list|)
expr_stmt|;
block|}
block|}
comment|//declare any parameters as external variables
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|String
name|bindingPrefix
init|=
name|params
operator|.
name|getProperty
argument_list|(
literal|"bindingPrefix"
argument_list|)
decl_stmt|;
if|if
condition|(
name|bindingPrefix
operator|==
literal|null
condition|)
block|{
name|bindingPrefix
operator|=
literal|"local"
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|Entry
name|param
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|param
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
operator|(
name|String
operator|)
name|param
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|":"
operator|+
name|key
argument_list|,
operator|new
name|StringValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"XQuery User Job not found: "
operator|+
name|xqueryresource
operator|+
literal|", job not scheduled"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|ee
parameter_list|)
block|{
name|abort
argument_list|(
literal|"Could not get DBBroker!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|abort
argument_list|(
literal|"Permission denied for the scheduling user: "
operator|+
name|user
operator|.
name|getName
argument_list|()
operator|+
literal|"!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|xpe
parameter_list|)
block|{
name|abort
argument_list|(
literal|"XPathException in the Job: "
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
operator|+
literal|"!"
argument_list|,
name|unschedule
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedURLException
name|e
parameter_list|)
block|{
name|abort
argument_list|(
literal|"Could not load XQuery: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|abort
argument_list|(
literal|"Could not load XQuery: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|cleanupBinaryValueInstances
argument_list|()
expr_stmt|;
block|}
comment|//return the compiled query to the pool
if|if
condition|(
name|xqPool
operator|!=
literal|null
operator|&&
name|source
operator|!=
literal|null
operator|&&
name|compiled
operator|!=
literal|null
condition|)
block|{
name|xqPool
operator|.
name|returnCompiledXQuery
argument_list|(
name|source
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
comment|//release the lock on the xquery resource
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|resource
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
comment|// Release the DBBroker
if|if
condition|(
name|pool
operator|!=
literal|null
operator|&&
name|broker
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|abort
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
throws|throws
name|JobExecutionException
block|{
name|abort
argument_list|(
name|message
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|abort
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|boolean
name|unschedule
parameter_list|)
throws|throws
name|JobExecutionException
block|{
specifier|final
name|JobExecutionException
name|jaa
init|=
operator|new
name|JobExecutionException
argument_list|(
literal|"UserXQueryJob Failed: "
operator|+
name|message
operator|+
operator|(
name|unschedule
condition|?
literal|" Unscheduling UserXQueryJob."
else|:
literal|""
operator|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//abort all triggers for this job if specified that we should unschedule the job
name|jaa
operator|.
name|setUnscheduleAllTriggers
argument_list|(
name|unschedule
argument_list|)
expr_stmt|;
throw|throw
name|jaa
throw|;
block|}
block|}
end_class

end_unit

