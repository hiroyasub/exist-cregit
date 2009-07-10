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
name|FunctionParameterSequenceType
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
name|NodeValue
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
comment|/**  * eXist Scheduler Module Extension ScheduleFunctions  *   * Schedules job's with eXist's Scheduler    *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @author Loren Cahlander<loren.cahlander@gmail.com>  * @serial 2009-05-15  * @version 1.3  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|ScheduleFunctions
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SCHEDULE_XQUERY_CRON_JOB
init|=
literal|"schedule-xquery-cron-job"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SCHEDULE_XQUERY_PERIODIC_JOB
init|=
literal|"schedule-xquery-periodic-job"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SCHEDULE_JAVA_CRON_JOB
init|=
literal|"schedule-java-cron-job"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SCHEDULE_JAVA_PERIODIC_JOB
init|=
literal|"schedule-java-periodic-job"
decl_stmt|;
specifier|private
name|Scheduler
name|scheduler
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionSignature
name|scheduleJavaCronJobNoParam
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|SCHEDULE_JAVA_CRON_JOB
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
literal|"Schedules the Java Class named (the class must extend org.exist.scheduler.UserJavaJob) according "
operator|+
literal|"to the Cron expression. The job will be registered using the job name."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"java-classname"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full name of the class to be executed.  It must extend the org.exist.scheduler.UserJavaJob class."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"cron-expression"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A cron expression.  Please see the scheduler documentation."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the job."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"success"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Flag indicating successful execution"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionSignature
name|scheduleJavaCronJobParam
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|SCHEDULE_JAVA_CRON_JOB
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
literal|"Schedules the Java Class named (the class must extend org.exist.scheduler.UserJavaJob) according "
operator|+
literal|"to the Cron expression. The job will be registered using the name passed in $c. The final "
operator|+
literal|"argument can be used to specify "
operator|+
literal|"parameters for the job, which will be passed to the query as external variables. Parameters are specified "
operator|+
literal|"in an XML fragment with the following structure:<parameters><param name=\"param-name1\" value=\"param-value1\"/></parameters>."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"java-classname"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full name of the class to be executed.  It must extend the org.exist.scheduler.UserJavaJob class."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"cron-expression"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A cron expression.  Please see the scheduler documentation."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the job."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-parameters"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"XML fragment with the following structure:<parameters><param name=\"param-name1\" value=\"param-value1\"/></parameters>"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"success"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Flag indicating successful execution"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionSignature
name|scheduleJavaPeriodicParam
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|SCHEDULE_JAVA_PERIODIC_JOB
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
literal|"Schedules the Java Class named (the class must extend org.exist.scheduler.UserJavaJob) according "
operator|+
literal|"to the periodic value. The job will be registered using the job name. The $job-parameters "
operator|+
literal|"argument can be used to specify "
operator|+
literal|"parameters for the job, which will be passed to the query as external variables. Parameters are specified "
operator|+
literal|"in an XML fragment with the following structure: "
operator|+
literal|"<parameters><param name=\"param-name1\" value=\"param-value1\"/></parameters>,  Given the delay and the repeat."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"java-classname"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full name of the class to be executed.  It must extend the org.exist.scheduler.UserJavaJob class."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"period"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Time in milliseconds between execution of the job"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the job."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-parameters"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"XML fragment with the following structure:<parameters><param name=\"param-name1\" value=\"param-value1\"/></parameters>"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"delay"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Can be used with a period in milliseconds to delay the start of a job."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"repeat"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Number of times to repeat the job after the initial execution"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"success"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Flag indicating successful execution"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionSignature
name|scheduleXQueryCronJobNoParam
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|SCHEDULE_XQUERY_CRON_JOB
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
literal|"Schedules the named XQuery resource (e.g. /db/foo.xql) according to the Cron expression. "
operator|+
literal|"XQuery job's will be launched under the guest account initially, although the running XQuery may switch permissions through calls to xmldb:login(). "
operator|+
literal|"The job will be registered using the job name."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"xquery-resource"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The path to the XQuery resource"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"cron-expression"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A cron expression.  Please see the scheduler documentation."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the job."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"success"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Flag indicating successful execution"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionSignature
name|scheduleXQueryCronJobParam
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|SCHEDULE_XQUERY_CRON_JOB
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
literal|"Schedules the named XQuery resource (e.g. /db/foo.xql) according to the Cron expression. "
operator|+
literal|"XQuery job's will be launched under the guest account initially, although the running XQuery may switch permissions through calls to xmldb:login(). "
operator|+
literal|"The job will be registered using the job name. The final argument can be used to specify "
operator|+
literal|"parameters for the job, which will be passed to the query as external variables. Parameters are specified "
operator|+
literal|"in an XML fragment with the following structure: "
operator|+
literal|"<parameters><param name=\"param-name1\" value=\"param-value1\"/></parameters>"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"xquery-resource"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The path to the XQuery resource"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"cron-expression"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A cron expression.  Please see the scheduler documentation."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the job."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-parameters"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"XML fragment with the following structure:<parameters><param name=\"param-name1\" value=\"param-value1\"/></parameters>"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"success"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Flag indicating successful execution"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionSignature
name|scheduleXQueryPeriodicParam
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|SCHEDULE_XQUERY_PERIODIC_JOB
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
literal|"Schedules the named XQuery resource (e.g. /db/foo.xql) according to the period. "
operator|+
literal|"XQuery job's will be launched under the guest account initially, although the running XQuery may switch permissions through calls to xmldb:login(). "
operator|+
literal|"The job will be registered using the job name. The job parameters argument can be used to specify "
operator|+
literal|"parameters for the job, which will be passed to the query as external variables. Parameters are specified "
operator|+
literal|"in an XML fragment with the following structure: "
operator|+
literal|"<parameters><param name=\"param-name1\" value=\"param-value1\"/></parameters>"
operator|+
literal|",  Given the delay passed and the repeat value."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"xquery-resource"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The path to the XQuery resource"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"period"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Time in milliseconds between execution of the job"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the job."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"job-parameters"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"XML fragment with the following structure:<parameters><param name=\"param-name1\" value=\"param-value1\"/></parameters>"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"delay"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Can be used with a period in milliseconds to delay the start of a job."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"repeat"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Number of times to repeat the job after the initial execution"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"success"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Flag indicating successful execution"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
name|scheduleJavaCronJobNoParam
block|,
name|scheduleJavaCronJobParam
block|,
name|scheduleJavaPeriodicParam
block|,
name|scheduleXQueryCronJobNoParam
block|,
name|scheduleXQueryCronJobParam
block|,
name|scheduleXQueryPeriodicParam
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
block|}
comment|/** 	 * evaluate thed call to the xquery function, 	 * it is really the main entry point of this class 	 *  	 * @param args		arguments from the  function call 	 * @param contextSequence	the Context Sequence to operate on (not used here internally!) 	 * @return		A sequence representing the result of the function call 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
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
name|long
name|periodicValue
init|=
literal|0
decl_stmt|;
name|long
name|delayValue
init|=
literal|0
decl_stmt|;
name|int
name|repeatValue
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|jobName
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|delayString
init|=
literal|"0"
decl_stmt|;
name|String
name|repeatString
init|=
literal|"0"
decl_stmt|;
name|Properties
name|properties
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>=
literal|4
condition|)
block|{
name|Node
name|options
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|properties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|parseParameters
argument_list|(
name|options
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>=
literal|5
condition|)
block|{
name|delayString
operator|=
name|args
index|[
literal|4
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
try|try
block|{
name|delayValue
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|delayString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
block|}
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>=
literal|6
condition|)
block|{
name|repeatString
operator|=
name|args
index|[
literal|5
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
try|try
block|{
name|repeatValue
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|repeatString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
block|}
name|User
name|user
init|=
name|context
operator|.
name|getUser
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
name|boolean
name|isCron
init|=
literal|true
decl_stmt|;
comment|//scheule-xquery-cron-job
if|if
condition|(
name|isCalledAs
argument_list|(
name|SCHEDULE_XQUERY_CRON_JOB
argument_list|)
condition|)
block|{
name|job
operator|=
operator|new
name|UserXQueryJob
argument_list|(
name|jobName
argument_list|,
name|resource
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|SCHEDULE_XQUERY_PERIODIC_JOB
argument_list|)
condition|)
block|{
name|periodicValue
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|cronExpression
argument_list|)
expr_stmt|;
name|job
operator|=
operator|new
name|UserXQueryJob
argument_list|(
name|jobName
argument_list|,
name|resource
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|isCron
operator|=
literal|true
expr_stmt|;
block|}
comment|//schedule-java-cron-job
if|else if
condition|(
name|isCalledAs
argument_list|(
name|SCHEDULE_JAVA_CRON_JOB
argument_list|)
operator|||
name|isCalledAs
argument_list|(
name|SCHEDULE_JAVA_PERIODIC_JOB
argument_list|)
condition|)
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
name|SCHEDULE_JAVA_PERIODIC_JOB
argument_list|)
condition|)
block|{
name|periodicValue
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|cronExpression
argument_list|)
expr_stmt|;
block|}
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot Schedule job. Class "
operator|+
name|resource
operator|+
literal|" is not an instance of org.exist.scheduler.UserJavaJob"
argument_list|)
expr_stmt|;
return|return
operator|(
name|BooleanValue
operator|.
name|FALSE
operator|)
return|;
block|}
operator|(
operator|(
name|UserJavaJob
operator|)
name|job
operator|)
operator|.
name|setName
argument_list|(
name|jobName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|cnfe
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
name|iae
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
name|ie
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|isCron
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
name|properties
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
comment|//schedule the job
if|if
condition|(
name|scheduler
operator|.
name|createPeriodicJob
argument_list|(
name|periodicValue
argument_list|,
operator|(
name|UserJob
operator|)
name|job
argument_list|,
name|delayValue
argument_list|,
name|properties
argument_list|,
name|repeatValue
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
specifier|private
name|void
name|parseParameters
parameter_list|(
name|Node
name|options
parameter_list|,
name|Properties
name|properties
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|options
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|options
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"parameters"
argument_list|)
condition|)
block|{
name|Node
name|child
init|=
name|options
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"param"
argument_list|)
condition|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
name|String
name|name
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|value
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Name or value attribute missing for stylesheet parameter"
argument_list|)
throw|;
name|properties
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

