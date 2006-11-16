begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Scheduler Module Extension ScheduleFunctions  *  Copyright (C) 2006 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|scheduler
package|;
end_package

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
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|Scheduler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|UserJavaJob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|UserJob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|UserXQueryJob
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|BooleanValue
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
name|SequenceType
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
name|Type
import|;
end_import

begin_comment
comment|/**  * eXist Mail Module Extension SendEmailFunction  *   * Schedules job's with eXist's Scheduler    *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-11-15  * @version 1.0  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|ScheduleFunctions
extends|extends
name|BasicFunction
block|{
specifier|private
name|Scheduler
name|scheduler
init|=
literal|null
decl_stmt|;
specifier|private
name|User
name|user
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"schedule-java-cron-job"
argument_list|,
name|SchedulerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SchedulerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Schedules the Java Class named in $a (the class must extend org.exist.scheduler.UserJob) according to the Cron expression in $b"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"schedule-xquery-cron-job"
argument_list|,
name|SchedulerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SchedulerModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Schedules the XQuery resource named in $a (e.g. /db/foo.xql) according to the Cron expression in $b"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * ScheduleFunctions Constructor 	 *  	 * @param context	The Context of the calling XQuery 	 */
specifier|public
name|ScheduleFunctions
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|scheduler
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getScheduler
argument_list|()
expr_stmt|;
name|user
operator|=
name|context
operator|.
name|getUser
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * evaluate the call to the xquery send-email function, 	 * it is really the main entry point of this class 	 *  	 * @param args		arguments from the send-email() function call 	 * @param contextSequence	the Context Sequence to operate on (not used here internally!) 	 * @return		A sequence representing the result of the send-email() function call 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|resource
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|cronExpression
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|//Check if the user is a DBA
if|if
condition|(
operator|!
name|user
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
return|return
operator|(
name|BooleanValue
operator|.
name|FALSE
operator|)
return|;
block|}
name|Object
name|job
init|=
literal|null
decl_stmt|;
comment|//scheule-xquery-cron-job
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"schedule-xquery-cron-job"
argument_list|)
condition|)
block|{
name|job
operator|=
operator|new
name|UserXQueryJob
argument_list|(
name|resource
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
comment|//schedule-java-cron-job
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"schedule-java-cron-job"
argument_list|)
condition|)
block|{
try|try
block|{
comment|//Check if the Class is a UserJob
name|Class
name|jobClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|job
operator|=
name|jobClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|job
operator|instanceof
name|UserJavaJob
operator|)
condition|)
block|{
return|return
operator|(
name|BooleanValue
operator|.
name|FALSE
operator|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
comment|//TODO: log?
return|return
operator|(
name|BooleanValue
operator|.
name|FALSE
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
comment|//TODO: log?
return|return
operator|(
name|BooleanValue
operator|.
name|FALSE
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
comment|//TODO: log?
return|return
operator|(
name|BooleanValue
operator|.
name|FALSE
operator|)
return|;
block|}
block|}
if|if
condition|(
name|job
operator|!=
literal|null
condition|)
block|{
comment|//schedule the job
if|if
condition|(
name|scheduler
operator|.
name|createCronJob
argument_list|(
name|cronExpression
argument_list|,
operator|(
name|UserJob
operator|)
name|job
argument_list|,
literal|null
argument_list|)
condition|)
block|{
return|return
operator|(
name|BooleanValue
operator|.
name|TRUE
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|BooleanValue
operator|.
name|FALSE
operator|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|(
name|BooleanValue
operator|.
name|FALSE
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

