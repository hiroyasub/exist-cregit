begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|DocumentSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
operator|.
name|ResponseWrapper
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
name|functions
operator|.
name|response
operator|.
name|ResponseModule
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
name|JavaObjectValue
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
comment|/** A place holder for static utility functions related to HTTP.   * @author jmv */
end_comment

begin_class
specifier|public
class|class
name|HTTPUtils
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|XQuery
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Feature "Guess last modification time for an XQuery result";       *  the HTTP header Last-Modified is filled with most recent time stamp among all       *  XQuery documents appearing in the actual response.      *  Note however, that the actual response can be influenced, through tests in the query,      *  by documents more recent. 	 * 	 * @param result the XQuery result to inspect 	 * @param context current context 	 */
specifier|public
specifier|static
name|void
name|addLastModifiedHeader
parameter_list|(
name|Sequence
name|result
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
block|{
try|try
block|{
specifier|final
name|DocumentSet
name|documentSet
init|=
name|result
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|long
name|mostRecentDocumentTime
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|documentSet
operator|.
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|mostRecentDocumentTime
operator|=
name|Math
operator|.
name|max
argument_list|(
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
argument_list|,
name|mostRecentDocumentTime
argument_list|)
expr_stmt|;
comment|//					LOG.debug("getFileName: " + doc.getFileName() + ", "
comment|//							+ doc.getLastModified());
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"mostRecentDocumentTime: "
operator|+
name|mostRecentDocumentTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|mostRecentDocumentTime
operator|>
literal|0
condition|)
block|{
specifier|final
name|Optional
argument_list|<
name|ResponseWrapper
argument_list|>
name|maybeResponse
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|context
operator|.
name|getHttpContext
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|XQueryContext
operator|.
name|HttpContext
operator|::
name|getResponse
argument_list|)
decl_stmt|;
if|if
condition|(
name|maybeResponse
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// have to take in account that if the header has allready been explicitely set
comment|// by the XQuery script, we should not modify it .
specifier|final
name|ResponseWrapper
name|responseWrapper
init|=
name|maybeResponse
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseWrapper
operator|.
name|getDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|)
operator|==
literal|0
condition|)
block|{
name|responseWrapper
operator|.
name|setDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|mostRecentDocumentTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|String
name|printStackTraceHTML
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|StackTraceElement
index|[]
name|trace
init|=
name|e
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<table id=\"javatrace\">"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<caption>Java Stack Trace:</caption>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<tr><th>Class Name</th><th>Method Name</th><th>File Name</th><th>Line</th></tr>"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|trace
operator|.
name|length
operator|&&
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"<tr>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<td class=\"class\">"
argument_list|)
operator|.
name|append
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"</td>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<td class=\"method\">"
argument_list|)
operator|.
name|append
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"</td>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<td class=\"file\">"
argument_list|)
operator|.
name|append
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getFileName
argument_list|()
operator|==
literal|null
condition|?
literal|"Unknown"
else|:
name|trace
index|[
name|i
index|]
operator|.
name|getFileName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"</td>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<td class=\"line\">"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getLineNumber
argument_list|()
operator|<
literal|0
condition|?
literal|"Unavailable"
else|:
name|Integer
operator|.
name|toString
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getLineNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</td>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</tr>"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"</table>"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

