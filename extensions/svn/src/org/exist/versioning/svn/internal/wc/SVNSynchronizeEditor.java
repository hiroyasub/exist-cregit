begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ====================================================================  * Copyright (c) 2004-2010 TMate Software Ltd.  All rights reserved.  *  * This software is licensed as described in the file COPYING, which  * you should have received as part of this distribution.  The terms  * are also available at http://svnkit.com/license.html  * If newer versions of this license are posted there, you may use a  * newer version instead, at your option.  * ====================================================================  */
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
name|internal
operator|.
name|wc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|ISVNLogEntryHandler
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
name|SVNProperties
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
name|SVNPropertyValue
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
name|diff
operator|.
name|SVNDiffWindow
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
name|admin
operator|.
name|SVNAdminClient
import|;
end_import

begin_comment
comment|/**  * @version 1.3  * @author  TMate Software Ltd.  */
end_comment

begin_class
specifier|public
class|class
name|SVNSynchronizeEditor
implements|implements
name|ISVNEditor
block|{
specifier|private
name|ISVNEditor
name|myWrappedEditor
decl_stmt|;
specifier|private
name|boolean
name|myIsRootOpened
decl_stmt|;
specifier|private
name|long
name|myBaseRevision
decl_stmt|;
specifier|private
name|SVNCommitInfo
name|myCommitInfo
decl_stmt|;
specifier|private
name|ISVNLogEntryHandler
name|myHandler
decl_stmt|;
specifier|private
name|SVNRepository
name|myTargetRepository
decl_stmt|;
specifier|private
name|int
name|myNormalizedNodePropsCounter
decl_stmt|;
specifier|private
name|SVNProperties
name|myRevisionProperties
decl_stmt|;
specifier|public
name|SVNSynchronizeEditor
parameter_list|(
name|SVNRepository
name|toRepository
parameter_list|,
name|ISVNLogEntryHandler
name|handler
parameter_list|,
name|long
name|baseRevision
parameter_list|,
name|SVNProperties
name|revProps
parameter_list|)
block|{
name|myTargetRepository
operator|=
name|toRepository
expr_stmt|;
name|myIsRootOpened
operator|=
literal|false
expr_stmt|;
name|myBaseRevision
operator|=
name|baseRevision
expr_stmt|;
name|myHandler
operator|=
name|handler
expr_stmt|;
name|myNormalizedNodePropsCounter
operator|=
literal|0
expr_stmt|;
name|myRevisionProperties
operator|=
name|revProps
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|(
name|long
name|baseRevision
parameter_list|,
name|SVNProperties
name|revProps
parameter_list|)
block|{
name|myWrappedEditor
operator|=
literal|null
expr_stmt|;
name|myCommitInfo
operator|=
literal|null
expr_stmt|;
name|myIsRootOpened
operator|=
literal|false
expr_stmt|;
name|myBaseRevision
operator|=
name|baseRevision
expr_stmt|;
name|myNormalizedNodePropsCounter
operator|=
literal|0
expr_stmt|;
name|myRevisionProperties
operator|=
name|revProps
expr_stmt|;
block|}
specifier|public
name|void
name|abortEdit
parameter_list|()
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|abortEdit
argument_list|()
expr_stmt|;
block|}
specifier|private
name|ISVNEditor
name|getWrappedEditor
parameter_list|()
throws|throws
name|SVNException
block|{
if|if
condition|(
name|myWrappedEditor
operator|==
literal|null
condition|)
block|{
name|myWrappedEditor
operator|=
name|myTargetRepository
operator|.
name|getCommitEditor
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|myRevisionProperties
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|myWrappedEditor
return|;
block|}
specifier|public
name|void
name|absentDir
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|absentDir
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|absentFile
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|absentFile
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addDir
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|copyFromPath
parameter_list|,
name|long
name|copyFromRevision
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|addDir
argument_list|(
name|path
argument_list|,
name|copyFromPath
argument_list|,
name|copyFromRevision
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addFile
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|copyFromPath
parameter_list|,
name|long
name|copyFromRevision
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|addFile
argument_list|(
name|path
argument_list|,
name|copyFromPath
argument_list|,
name|copyFromRevision
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|changeDirProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|SVNPropertyValue
name|value
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|SVNProperty
operator|.
name|isRegularProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|SVNPropertiesManager
operator|.
name|propNeedsTranslation
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|normalizedValue
init|=
name|SVNAdminClient
operator|.
name|normalizeString
argument_list|(
name|SVNPropertyValue
operator|.
name|getPropertyAsString
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalizedValue
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|normalizedValue
argument_list|)
expr_stmt|;
name|myNormalizedNodePropsCounter
operator|++
expr_stmt|;
block|}
block|}
name|getWrappedEditor
argument_list|()
operator|.
name|changeDirProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|changeFileProperty
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|SVNPropertyValue
name|value
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|SVNProperty
operator|.
name|isRegularProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|SVNPropertiesManager
operator|.
name|propNeedsTranslation
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|normalizedVal
init|=
name|SVNAdminClient
operator|.
name|normalizeString
argument_list|(
name|SVNPropertyValue
operator|.
name|getPropertyAsString
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalizedVal
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|normalizedVal
argument_list|)
expr_stmt|;
name|myNormalizedNodePropsCounter
operator|++
expr_stmt|;
block|}
block|}
name|getWrappedEditor
argument_list|()
operator|.
name|changeFileProperty
argument_list|(
name|path
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|closeDir
parameter_list|()
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|closeDir
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SVNCommitInfo
name|closeEdit
parameter_list|()
throws|throws
name|SVNException
block|{
name|ISVNEditor
name|wrappedEditor
init|=
name|getWrappedEditor
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|myIsRootOpened
condition|)
block|{
name|wrappedEditor
operator|.
name|openRoot
argument_list|(
name|myBaseRevision
argument_list|)
expr_stmt|;
block|}
name|myCommitInfo
operator|=
name|wrappedEditor
operator|.
name|closeEdit
argument_list|()
expr_stmt|;
if|if
condition|(
name|myHandler
operator|!=
literal|null
condition|)
block|{
name|SVNLogEntry
name|logEntry
init|=
operator|new
name|SVNLogEntry
argument_list|(
literal|null
argument_list|,
name|myCommitInfo
operator|.
name|getNewRevision
argument_list|()
argument_list|,
name|myCommitInfo
operator|.
name|getAuthor
argument_list|()
argument_list|,
name|myCommitInfo
operator|.
name|getDate
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|myHandler
operator|.
name|handleLogEntry
argument_list|(
name|logEntry
argument_list|)
expr_stmt|;
block|}
return|return
name|myCommitInfo
return|;
block|}
specifier|public
name|void
name|closeFile
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|textChecksum
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|closeFile
argument_list|(
name|path
argument_list|,
name|textChecksum
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deleteEntry
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|deleteEntry
argument_list|(
name|path
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|openDir
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|openDir
argument_list|(
name|path
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|openFile
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|openFile
argument_list|(
name|path
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|openRoot
parameter_list|(
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|openRoot
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|myIsRootOpened
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|targetRevision
parameter_list|(
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|targetRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|applyTextDelta
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|baseChecksum
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|applyTextDelta
argument_list|(
name|path
argument_list|,
name|baseChecksum
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OutputStream
name|textDeltaChunk
parameter_list|(
name|String
name|path
parameter_list|,
name|SVNDiffWindow
name|diffWindow
parameter_list|)
throws|throws
name|SVNException
block|{
return|return
name|getWrappedEditor
argument_list|()
operator|.
name|textDeltaChunk
argument_list|(
name|path
argument_list|,
name|diffWindow
argument_list|)
return|;
block|}
specifier|public
name|void
name|textDeltaEnd
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|SVNException
block|{
name|getWrappedEditor
argument_list|()
operator|.
name|textDeltaEnd
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SVNCommitInfo
name|getCommitInfo
parameter_list|()
block|{
return|return
name|myCommitInfo
return|;
block|}
specifier|public
name|int
name|getNormalizedNodePropsCounter
parameter_list|()
block|{
return|return
name|myNormalizedNodePropsCounter
return|;
block|}
block|}
end_class

end_unit

