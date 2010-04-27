begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

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
name|EXistException
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
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNCancelException
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
name|SVNCommitInfo
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
name|SVNDepth
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
name|SVNErrorCode
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
name|SVNErrorMessage
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
name|SVNLogEntry
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
name|SVNProperty
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
name|SVNURL
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
name|fs
operator|.
name|FSRepositoryFactory
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
name|internal
operator|.
name|wc
operator|.
name|DefaultSVNAuthenticationManager
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
name|ISVNEditor
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
name|ISVNReporterBaton
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
name|ISVNCommitParameters
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
name|ISVNEventHandler
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
name|SVNClientManager
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
name|SVNCommitClient
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
name|SVNCommitPacket
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
name|SVNEvent
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
name|SVNEventAction
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
name|SVNStatusType
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
name|SVNUpdateClient
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
name|SVNWCClient
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|Subversion
implements|implements
name|ISVNEventHandler
block|{
specifier|public
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Subversion
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SVNURL
name|svnurl
init|=
literal|null
decl_stmt|;
specifier|private
name|XmldbURI
name|collection
init|=
literal|null
decl_stmt|;
specifier|private
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
name|repository
init|=
literal|null
decl_stmt|;
specifier|private
name|ISVNAuthenticationManager
name|authManager
decl_stmt|;
specifier|private
name|SVNClientManager
name|clientManager
decl_stmt|;
specifier|private
name|SVNUpdateClient
name|updateClient
decl_stmt|;
specifier|private
name|SVNCommitClient
name|commitClient
decl_stmt|;
specifier|private
name|SVNWCClient
name|wcClient
decl_stmt|;
specifier|public
name|Subversion
parameter_list|(
name|XmldbURI
name|collection
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|SVNException
block|{
name|this
argument_list|(
name|collection
argument_list|,
name|url
argument_list|,
literal|"anonymous"
argument_list|,
literal|"anonymous"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Subversion
parameter_list|(
name|XmldbURI
name|collection
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|SVNException
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|setupType
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|authManager
operator|=
name|SVNWCUtil
operator|.
name|createDefaultAuthenticationManager
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
operator|(
operator|(
name|DefaultSVNAuthenticationManager
operator|)
name|authManager
operator|)
operator|.
name|setAuthenticationForced
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|repository
operator|.
name|setAuthenticationManager
argument_list|(
name|authManager
argument_list|)
expr_stmt|;
name|checkRoot
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connected to "
operator|+
name|svnurl
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Repository latest revision: "
operator|+
name|latestRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setupType
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|SVNException
block|{
name|svnurl
operator|=
name|SVNURL
operator|.
name|parseURIDecoded
argument_list|(
name|url
argument_list|)
expr_stmt|;
comment|// over http:// and https://
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"http"
argument_list|)
condition|)
block|{
name|DAVRepositoryFactory
operator|.
name|setup
argument_list|()
expr_stmt|;
name|repository
operator|=
name|DAVRepositoryFactory
operator|.
name|create
argument_list|(
name|svnurl
argument_list|)
expr_stmt|;
comment|// over svn:// and svn+xxx://
block|}
if|else if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"svn"
argument_list|)
condition|)
block|{
name|SVNRepositoryFactoryImpl
operator|.
name|setup
argument_list|()
expr_stmt|;
name|repository
operator|=
name|SVNRepositoryFactoryImpl
operator|.
name|create
argument_list|(
name|svnurl
argument_list|)
expr_stmt|;
comment|// over file:///
block|}
else|else
block|{
name|FSRepositoryFactory
operator|.
name|setup
argument_list|()
expr_stmt|;
name|repository
operator|=
name|FSRepositoryFactory
operator|.
name|create
argument_list|(
name|svnurl
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkRoot
parameter_list|()
throws|throws
name|SVNException
block|{
name|SVNNodeKind
name|nodeKind
init|=
name|repository
operator|.
name|checkPath
argument_list|(
literal|""
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeKind
operator|==
name|SVNNodeKind
operator|.
name|NONE
condition|)
block|{
name|SVNErrorMessage
name|error
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|UNKNOWN
argument_list|,
literal|"No entry at URL ''{0}''"
argument_list|,
name|svnurl
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SVNException
argument_list|(
name|error
argument_list|)
throw|;
block|}
if|else if
condition|(
name|nodeKind
operator|==
name|SVNNodeKind
operator|.
name|FILE
condition|)
block|{
name|SVNErrorMessage
name|error
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|UNKNOWN
argument_list|,
literal|"Entry at URL ''{0}'' is a file while directory was expected"
argument_list|,
name|svnurl
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SVNException
argument_list|(
name|error
argument_list|)
throw|;
block|}
block|}
specifier|public
name|long
name|latestRevision
parameter_list|()
throws|throws
name|SVNException
block|{
return|return
name|repository
operator|.
name|getLatestRevision
argument_list|()
return|;
block|}
specifier|public
name|SVNCommitInfo
name|commit
parameter_list|(
name|File
name|dstPath
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWCClient
argument_list|()
operator|.
name|doAdd
argument_list|(
name|dstPath
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|SVNDepth
operator|.
name|INFINITY
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SVNCommitPacket
name|packet
init|=
name|getCommitClient
argument_list|()
operator|.
name|doCollectCommitItems
argument_list|(
operator|new
name|File
index|[]
block|{
name|dstPath
block|}
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|SVNDepth
operator|.
name|INFINITY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|getCommitClient
argument_list|()
operator|.
name|doCommit
argument_list|(
name|packet
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|protected
name|boolean
name|update
parameter_list|()
throws|throws
name|SVNException
block|{
return|return
name|update
argument_list|(
name|latestRevision
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|update
parameter_list|(
name|long
name|toRevision
parameter_list|)
throws|throws
name|SVNException
block|{
name|ISVNReporterBaton
name|reporterBaton
init|=
operator|new
name|ExportReporterBaton
argument_list|(
name|toRevision
argument_list|)
decl_stmt|;
name|ISVNEditor
name|exportEditor
decl_stmt|;
try|try
block|{
name|exportEditor
operator|=
operator|new
name|ExportEditor
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|repository
operator|.
name|update
argument_list|(
name|toRevision
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|reporterBaton
argument_list|,
name|exportEditor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Exported revision: "
operator|+
name|toRevision
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|SVNLogEntry
argument_list|>
name|log
parameter_list|(
name|String
index|[]
name|targetPaths
parameter_list|,
name|Collection
argument_list|<
name|SVNLogEntry
argument_list|>
name|entries
parameter_list|,
name|long
name|startRevision
parameter_list|,
name|long
name|endRevision
parameter_list|,
name|boolean
name|changedPath
parameter_list|,
name|boolean
name|strictNode
parameter_list|)
throws|throws
name|SVNException
block|{
return|return
name|repository
operator|.
name|log
argument_list|(
operator|new
name|String
index|[]
block|{
literal|""
block|}
argument_list|,
name|entries
argument_list|,
name|startRevision
argument_list|,
name|endRevision
argument_list|,
name|changedPath
argument_list|,
name|strictNode
argument_list|)
return|;
block|}
specifier|private
specifier|synchronized
name|SVNClientManager
name|getClientManager
parameter_list|()
block|{
if|if
condition|(
name|clientManager
operator|==
literal|null
condition|)
block|{
name|clientManager
operator|=
name|SVNClientManager
operator|.
name|newInstance
argument_list|(
name|SVNWCUtil
operator|.
name|createDefaultOptions
argument_list|(
literal|true
argument_list|)
argument_list|,
name|authManager
argument_list|)
expr_stmt|;
block|}
return|return
name|clientManager
return|;
block|}
specifier|public
name|SVNWCClient
name|getWCClient
parameter_list|()
block|{
if|if
condition|(
name|wcClient
operator|==
literal|null
condition|)
block|{
name|wcClient
operator|=
name|getClientManager
argument_list|()
operator|.
name|getWCClient
argument_list|()
expr_stmt|;
name|wcClient
operator|.
name|setEventHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|wcClient
return|;
block|}
specifier|private
name|SVNUpdateClient
name|getUpdateClient
parameter_list|()
block|{
if|if
condition|(
name|updateClient
operator|==
literal|null
condition|)
block|{
name|updateClient
operator|=
name|getClientManager
argument_list|()
operator|.
name|getUpdateClient
argument_list|()
expr_stmt|;
name|updateClient
operator|.
name|setEventHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|updateClient
return|;
block|}
specifier|private
name|SVNCommitClient
name|getCommitClient
parameter_list|()
block|{
if|if
condition|(
name|commitClient
operator|==
literal|null
condition|)
block|{
name|commitClient
operator|=
name|getClientManager
argument_list|()
operator|.
name|getCommitClient
argument_list|()
expr_stmt|;
name|commitClient
operator|.
name|setEventHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|commitClient
operator|.
name|setCommitParameters
argument_list|(
operator|new
name|ISVNCommitParameters
argument_list|()
block|{
specifier|public
name|boolean
name|onDirectoryDeletion
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|onFileDeletion
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Action
name|onMissingDirectory
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|ISVNCommitParameters
operator|.
name|DELETE
return|;
block|}
specifier|public
name|Action
name|onMissingFile
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|ISVNCommitParameters
operator|.
name|DELETE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|commitClient
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkCancelled
parameter_list|()
throws|throws
name|SVNCancelException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleEvent
parameter_list|(
name|SVNEvent
name|event
parameter_list|,
name|double
name|progress
parameter_list|)
throws|throws
name|SVNException
block|{
name|String
name|nullString
init|=
literal|" "
decl_stmt|;
name|SVNEventAction
name|action
init|=
name|event
operator|.
name|getAction
argument_list|()
decl_stmt|;
name|String
name|pathChangeType
init|=
name|nullString
decl_stmt|;
if|if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|ADD
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"A     "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|COPY
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"A  +  "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|DELETE
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"D     "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|LOCKED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"L     "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|LOCK_FAILED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"failed to lock    "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|UPDATE_ADD
condition|)
block|{
name|pathChangeType
operator|=
literal|"A"
expr_stmt|;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|UPDATE_DELETE
condition|)
block|{
name|pathChangeType
operator|=
literal|"D"
expr_stmt|;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|UPDATE_UPDATE
condition|)
block|{
name|SVNStatusType
name|contentsStatus
init|=
name|event
operator|.
name|getContentsStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|contentsStatus
operator|==
name|SVNStatusType
operator|.
name|CHANGED
condition|)
block|{
name|pathChangeType
operator|=
literal|"U"
expr_stmt|;
block|}
if|else if
condition|(
name|contentsStatus
operator|==
name|SVNStatusType
operator|.
name|CONFLICTED
condition|)
block|{
name|pathChangeType
operator|=
literal|"C"
expr_stmt|;
block|}
if|else if
condition|(
name|contentsStatus
operator|==
name|SVNStatusType
operator|.
name|MERGED
condition|)
block|{
name|pathChangeType
operator|=
literal|"G"
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|UPDATE_EXTERNAL
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Fetching external item into '"
operator|+
name|event
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"External at revision "
operator|+
name|event
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|UPDATE_COMPLETED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"At revision "
operator|+
name|event
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|SVNStatusType
name|propertiesStatus
init|=
name|event
operator|.
name|getPropertiesStatus
argument_list|()
decl_stmt|;
name|String
name|propertiesChangeType
init|=
name|nullString
decl_stmt|;
if|if
condition|(
name|propertiesStatus
operator|==
name|SVNStatusType
operator|.
name|CHANGED
condition|)
block|{
name|propertiesChangeType
operator|=
literal|"U"
expr_stmt|;
block|}
if|else if
condition|(
name|propertiesStatus
operator|==
name|SVNStatusType
operator|.
name|CONFLICTED
condition|)
block|{
name|propertiesChangeType
operator|=
literal|"C"
expr_stmt|;
block|}
if|else if
condition|(
name|propertiesStatus
operator|==
name|SVNStatusType
operator|.
name|MERGED
condition|)
block|{
name|propertiesChangeType
operator|=
literal|"G"
expr_stmt|;
block|}
name|String
name|lockLabel
init|=
name|nullString
decl_stmt|;
name|SVNStatusType
name|lockType
init|=
name|event
operator|.
name|getLockStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|lockType
operator|==
name|SVNStatusType
operator|.
name|LOCK_UNLOCKED
condition|)
block|{
name|lockLabel
operator|=
literal|"B"
expr_stmt|;
block|}
if|if
condition|(
name|pathChangeType
operator|!=
name|nullString
operator|||
name|propertiesChangeType
operator|!=
name|nullString
operator|||
name|lockLabel
operator|!=
name|nullString
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|pathChangeType
operator|+
name|propertiesChangeType
operator|+
name|lockLabel
operator|+
literal|"       "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|COMMIT_MODIFIED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending   "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|COMMIT_DELETED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting   "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|COMMIT_REPLACED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Replacing   "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|COMMIT_DELTA_SENT
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Transmitting file data...."
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|action
operator|==
name|SVNEventAction
operator|.
name|COMMIT_ADDED
condition|)
block|{
name|String
name|mimeType
init|=
name|event
operator|.
name|getMimeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|SVNProperty
operator|.
name|isBinaryMimeType
argument_list|(
name|mimeType
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding  (bin)  "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding         "
operator|+
name|event
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

