begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|xquery
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
name|persistent
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
name|dom
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
name|DateTimeValue
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
name|IntegerValue
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
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|auth
operator|.
name|ISVNAuthenticationManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|io
operator|.
name|dav
operator|.
name|DAVRepositoryFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|io
operator|.
name|svn
operator|.
name|SVNRepositoryFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|io
operator|.
name|SVNRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|io
operator|.
name|SVNRepositoryFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|wc
operator|.
name|SVNWCUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

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
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|SVNLog
extends|extends
name|AbstractSVNFunction
block|{
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
literal|"log"
argument_list|,
name|SVNModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SVNModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Retrieves the log entries from a subversion repository."
operator|+
literal|"\n\nThe return is formatted as follows:\n"
operator|+
literal|"<log uri=\"\" start=\"\">\n"
operator|+
literal|"<entry rev=\"\" author=\"\" date=\"\">\n"
operator|+
literal|"<message></message>\n"
operator|+
literal|"<paths>\n"
operator|+
literal|"<path revtype=\"M\"></path>\n"
operator|+
literal|"</paths>\n"
operator|+
literal|"</entry>\n"
operator|+
literal|"</log>\n\n"
operator|+
literal|"Revtype values are 'A' (item added), 'D' (item deleted), 'M' (item modified), or 'R' (item replaced)."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|SVN_URI
block|,
name|LOGIN
block|,
name|PASSWORD
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"start-revision"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The subversion revision to start from.  If empty, then start from the beginning."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"end-revision"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The subversion revision to end with.  If empty, then end with the HEAD revision"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"log"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"a sequence containing the log entries"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|LOG_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"log"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|ENTRY_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"entry"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|MESSAGE_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"message"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|PATHS_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"paths"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|PATH_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"path"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|AttributesImpl
name|EMPTY_ATTRIBS
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
specifier|public
name|SVNLog
parameter_list|(
name|XQueryContext
name|context
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
name|DAVRepositoryFactory
operator|.
name|setup
argument_list|()
expr_stmt|;
name|SVNRepositoryFactoryImpl
operator|.
name|setup
argument_list|()
expr_stmt|;
name|String
name|uri
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
name|SVNRepository
name|repo
init|=
name|SVNRepositoryFactory
operator|.
name|create
argument_list|(
name|SVNURL
operator|.
name|parseURIDecoded
argument_list|(
name|uri
argument_list|)
argument_list|)
decl_stmt|;
name|ISVNAuthenticationManager
name|authManager
init|=
name|SVNWCUtil
operator|.
name|createDefaultAuthenticationManager
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|repo
operator|.
name|setAuthenticationManager
argument_list|(
name|authManager
argument_list|)
expr_stmt|;
name|long
name|startRevision
init|=
literal|0
decl_stmt|;
name|long
name|endRevision
init|=
operator|-
literal|1
decl_stmt|;
comment|// = HEAD
if|if
condition|(
operator|!
name|args
index|[
literal|3
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
name|startRevision
operator|=
operator|(
operator|(
name|IntegerValue
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
name|getLong
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|4
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
name|endRevision
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|4
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"uri"
argument_list|,
literal|"uri"
argument_list|,
literal|"CDATA"
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"start"
argument_list|,
literal|"start"
argument_list|,
literal|"CDATA"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|startRevision
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|LOG_ELEMENT
argument_list|,
name|attribs
argument_list|)
decl_stmt|;
name|LogHandler
name|handler
init|=
operator|new
name|LogHandler
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|repo
operator|.
name|log
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|startRevision
argument_list|,
name|endRevision
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
class|class
name|LogHandler
implements|implements
name|ISVNLogEntryHandler
block|{
name|MemTreeBuilder
name|builder
decl_stmt|;
specifier|private
name|LogHandler
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
specifier|public
name|void
name|handleLogEntry
parameter_list|(
name|SVNLogEntry
name|entry
parameter_list|)
throws|throws
name|SVNException
block|{
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"rev"
argument_list|,
literal|"rev"
argument_list|,
literal|"CDATA"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|entry
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"author"
argument_list|,
literal|"author"
argument_list|,
literal|"CDATA"
argument_list|,
name|entry
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|date
init|=
literal|null
decl_stmt|;
try|try
block|{
name|date
operator|=
operator|new
name|DateTimeValue
argument_list|(
name|entry
operator|.
name|getDate
argument_list|()
argument_list|)
operator|.
name|getStringValue
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
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"date"
argument_list|,
literal|"date"
argument_list|,
literal|"CDATA"
argument_list|,
name|date
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|ENTRY_ELEMENT
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|MESSAGE_ELEMENT
argument_list|,
name|EMPTY_ATTRIBS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|entry
operator|.
name|getMessage
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
name|PATHS_ELEMENT
argument_list|,
name|EMPTY_ATTRIBS
argument_list|)
expr_stmt|;
name|Map
name|paths
init|=
name|entry
operator|.
name|getChangedPaths
argument_list|()
decl_stmt|;
name|Iterator
name|iterator
init|=
name|paths
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|SVNLogEntryPath
name|svnLogEntryPath
init|=
operator|(
name|SVNLogEntryPath
operator|)
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|AttributesImpl
name|pathAttribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|pathAttribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"revtype"
argument_list|,
literal|"revtype"
argument_list|,
literal|"CDATA"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|svnLogEntryPath
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//                pathAttribs.addAttribute("", "copypath", "copypath", "CDATA", svnLogEntryPath.getCopyPath());
comment|//                pathAttribs.addAttribute("", "copyrev", "copyrev", "CDATA", String.valueOf(svnLogEntryPath.getCopyRevision()));
name|builder
operator|.
name|startElement
argument_list|(
name|PATH_ELEMENT
argument_list|,
name|pathAttribs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|e
operator|.
name|getKey
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
block|}
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
block|}
block|}
block|}
end_class

end_unit

