begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|system
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
name|memtree
operator|.
name|MemTreeBuilder
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
name|XQueryWatchDog
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

begin_comment
comment|/**  * Return a list of the currently running XQueries (must be dba)  *   * @author Andrzej Taramina (andrzej@chaeron.com)  */
end_comment

begin_class
specifier|public
class|class
name|GetRunningXQueries
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|GetRunningXQueries
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
name|SystemModule
operator|.
name|NAMESPACE_URI
decl_stmt|;
specifier|final
specifier|static
name|String
name|PREFIX
init|=
name|SystemModule
operator|.
name|PREFIX
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
literal|"get-running-xqueries"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Get a list of running XQueries (dba role only)."
argument_list|,
literal|null
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetRunningXQueries
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
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
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
name|logger
operator|.
name|info
argument_list|(
literal|"Entering "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":get-scheduled-jobs"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|context
operator|.
name|getUser
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
name|XPathException
name|xPathException
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied, calling user '"
operator|+
name|context
operator|.
name|getUser
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' must be a DBA to get the list of running xqueries"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Invalid user "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":get-scheduled-xqueries"
argument_list|,
name|xPathException
argument_list|)
expr_stmt|;
throw|throw
name|xPathException
throw|;
block|}
return|return
operator|(
name|getRunningXQueries
argument_list|()
operator|)
return|;
block|}
specifier|private
name|Sequence
name|getRunningXQueries
parameter_list|()
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Entering getRunningXQueries"
argument_list|)
expr_stmt|;
name|Sequence
name|xmlResponse
init|=
literal|null
decl_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"xqueries"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//Add all the running xqueries
name|XQueryWatchDog
name|watchdogs
index|[]
init|=
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getProcessMonitor
argument_list|()
operator|.
name|getRunningXQueries
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|watchdogs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|XQueryContext
name|context
init|=
name|watchdogs
index|[
name|i
index|]
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|getRunningXQuery
argument_list|(
name|builder
argument_list|,
name|context
argument_list|,
name|watchdogs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|xmlResponse
operator|=
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"Exiting getRunningXQueries"
argument_list|)
expr_stmt|;
return|return
operator|(
name|xmlResponse
operator|)
return|;
block|}
specifier|private
name|void
name|getRunningXQuery
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|,
name|XQueryContext
name|context
parameter_list|,
name|XQueryWatchDog
name|watchdog
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Entering getRunningXQuery"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"xquery"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"id"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|""
operator|+
name|context
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sourceType"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|context
operator|.
name|getSourceType
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"terminating"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|(
name|watchdog
operator|.
name|isTerminating
argument_list|()
condition|?
literal|"true"
else|:
literal|"false"
operator|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sourceKey"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|context
operator|.
name|getSourceKey
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"xqueryExpression"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|context
operator|.
name|getRootExpression
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"Exiting getRunningXQuery"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

