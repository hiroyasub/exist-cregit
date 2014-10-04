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
name|util
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|WorkingCopy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|ISVNInfoHandler
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
name|FunctionReturnSequenceType
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
name|SVNException
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
name|SVNNodeKind
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
name|SVNRevision
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

begin_comment
comment|/**  * Collects information on local path(s). Like 'svn info (-R)' command.  *   * @author<a href="mailto:amir.akhmedov@gmail.com">Amir Akhmedov</a>  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_class
specifier|public
class|class
name|SVNInfo
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
literal|"info"
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
literal|"Collects information on local path(s). Like 'svn info (-R)' command."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DB_PATH
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|SVNInfo
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
annotation|@
name|Override
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
name|WorkingCopy
name|wc
init|=
operator|new
name|WorkingCopy
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
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
name|Resource
name|wcDir
init|=
operator|new
name|Resource
argument_list|(
name|uri
argument_list|)
decl_stmt|;
try|try
block|{
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
comment|//attribs.addAttribute("", "start", "start", "CDATA", Long.toString(startRevision));
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|INFO_ELEMENT
argument_list|,
name|attribs
argument_list|)
decl_stmt|;
name|wc
operator|.
name|showInfo
argument_list|(
name|wcDir
argument_list|,
name|SVNRevision
operator|.
name|WORKING
argument_list|,
literal|true
argument_list|,
operator|new
name|InfoHandler
argument_list|(
name|builder
argument_list|)
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
name|svne
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"error while collecting info for the location '"
operator|+
name|uri
operator|+
literal|"'"
argument_list|,
name|svne
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
class|class
name|InfoHandler
implements|implements
name|ISVNInfoHandler
block|{
name|MemTreeBuilder
name|builder
decl_stmt|;
specifier|public
name|InfoHandler
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
annotation|@
name|Override
specifier|public
name|void
name|handleInfo
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|SVNInfo
name|info
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
literal|"local-path"
argument_list|,
literal|"local-path"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getFile
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"URL"
argument_list|,
literal|"URL"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|isRemote
argument_list|()
operator|&&
name|info
operator|.
name|getRepositoryRootURL
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Root-URL"
argument_list|,
literal|"Root-URL"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getRepositoryRootURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getRepositoryUUID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Repository-UUID"
argument_list|,
literal|"Repository-UUID"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getRepositoryUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Revision"
argument_list|,
literal|"Revision"
argument_list|,
literal|"CDATA"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|info
operator|.
name|getRevision
argument_list|()
operator|.
name|getNumber
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
literal|"Node-Kind"
argument_list|,
literal|"Node-Kind"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getKind
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|info
operator|.
name|isRemote
argument_list|()
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Schedule"
argument_list|,
literal|"Schedule"
argument_list|,
literal|"CDATA"
argument_list|,
operator|(
name|info
operator|.
name|getSchedule
argument_list|()
operator|!=
literal|null
condition|?
name|info
operator|.
name|getSchedule
argument_list|()
else|:
literal|"normal"
operator|)
argument_list|)
expr_stmt|;
block|}
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Last-Changed-Author"
argument_list|,
literal|"Last-Changed-Author"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Last-Changed-Revision"
argument_list|,
literal|"Last-Changed-Revision"
argument_list|,
literal|"CDATA"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|info
operator|.
name|getCommittedRevision
argument_list|()
operator|.
name|getNumber
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
literal|"Last-Changed-Date"
argument_list|,
literal|"Last-Changed-Date"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getCommittedDate
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getPropTime
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Properties-Last-Updated"
argument_list|,
literal|"Properties-Last-Updated"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getPropTime
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getKind
argument_list|()
operator|==
name|SVNNodeKind
operator|.
name|FILE
operator|&&
name|info
operator|.
name|getChecksum
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|getTextTime
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Text-Last-Updated"
argument_list|,
literal|"Text-Last-Updated"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getTextTime
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Checksum"
argument_list|,
literal|"Checksum"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getChecksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getLock
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Lock-Token"
argument_list|,
literal|"Lock-Token"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Lock-Owner"
argument_list|,
literal|"Lock-Owner"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Lock-Created"
argument_list|,
literal|"Lock-Created"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getCreationDate
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getExpirationDate
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Lock-Expires"
argument_list|,
literal|"Lock-Expires"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getExpirationDate
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getComment
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"Lock-Comment"
argument_list|,
literal|"Lock-Comment"
argument_list|,
literal|"CDATA"
argument_list|,
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getComment
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|INFO_ELEMENT
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
specifier|static
name|QName
name|INFO_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"info"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

