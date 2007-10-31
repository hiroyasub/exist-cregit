begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Scheduler Module Extension DeleteSheduledJob  *  Copyright (C) 2006 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|UserJob
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
comment|/**  * eXist Scheduler Module Extension DeleteScheduledJob  *   * Removes a Job from the Scheduler  *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-11-15  * @version 1.0  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|DeleteScheduledJob
extends|extends
name|BasicFunction
block|{
specifier|private
name|Scheduler
name|scheduler
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"delete-scheduled-job"
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
literal|"Delete the job named in $a from the Scheduler. Will only delete User Scheduled Jobs! Returns true() if the Job was deleted."
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
decl_stmt|;
comment|/** 	 * DeleteScheduledJob Constructor 	 *  	 * @param context	The Context of the calling XQuery 	 */
specifier|public
name|DeleteScheduledJob
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
comment|/** 	 * evaluate the call to the xquery function, 	 * it is really the main entry point of this class 	 *  	 * @param args		arguments from the  function call 	 * @param contextSequence	the Context Sequence to operate on (not used here internally!) 	 * @return		A sequence representing the result of the function call 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
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
name|jobName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
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
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|scheduler
operator|.
name|deleteJob
argument_list|(
name|jobName
argument_list|,
name|UserJob
operator|.
name|JOB_GROUP
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

