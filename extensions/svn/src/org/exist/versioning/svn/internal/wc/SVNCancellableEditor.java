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
name|ISVNCanceller
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
name|util
operator|.
name|ISVNDebugLog
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
name|util
operator|.
name|SVNDebugLog
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
name|util
operator|.
name|SVNLogType
import|;
end_import

begin_comment
comment|/**  * @version 1.3  * @author  TMate Software Ltd.  */
end_comment

begin_class
specifier|public
class|class
name|SVNCancellableEditor
implements|implements
name|ISVNEditor
block|{
specifier|private
name|ISVNEditor
name|myDelegate
decl_stmt|;
specifier|private
name|ISVNCanceller
name|myCancel
decl_stmt|;
specifier|private
name|ISVNDebugLog
name|myLog
decl_stmt|;
specifier|public
specifier|static
name|ISVNEditor
name|newInstance
parameter_list|(
name|ISVNEditor
name|editor
parameter_list|,
name|ISVNCanceller
name|cancel
parameter_list|,
name|ISVNDebugLog
name|log
parameter_list|)
block|{
if|if
condition|(
name|cancel
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|SVNCancellableEditor
argument_list|(
name|editor
argument_list|,
name|cancel
argument_list|,
name|log
argument_list|)
return|;
block|}
return|return
name|editor
return|;
block|}
specifier|private
name|SVNCancellableEditor
parameter_list|(
name|ISVNEditor
name|delegate
parameter_list|,
name|ISVNCanceller
name|cancel
parameter_list|,
name|ISVNDebugLog
name|log
parameter_list|)
block|{
name|myDelegate
operator|=
name|delegate
expr_stmt|;
name|myCancel
operator|=
name|cancel
expr_stmt|;
name|myLog
operator|=
name|log
operator|==
literal|null
condition|?
name|SVNDebugLog
operator|.
name|getDefaultLog
argument_list|()
else|:
name|log
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myDelegate
operator|.
name|targetRevision
argument_list|(
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"root"
argument_list|)
expr_stmt|;
name|myDelegate
operator|.
name|openRoot
argument_list|(
name|revision
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"del "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|absentDir
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|SVNException
block|{
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"absent dir "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"absent file "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"add dir "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"open dir "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"change dir prop "
operator|+
name|name
operator|+
literal|" = "
operator|+
name|SVNPropertyValue
operator|.
name|getPropertyAsString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|myDelegate
operator|.
name|changeDirProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|closeDir
parameter_list|()
throws|throws
name|SVNException
block|{
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"close dir"
argument_list|)
expr_stmt|;
name|myDelegate
operator|.
name|closeDir
argument_list|()
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"add file "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"open file "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"apply delta "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"delta chunk "
operator|+
name|path
argument_list|)
expr_stmt|;
return|return
name|myDelegate
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
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"delta end "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
operator|.
name|textDeltaEnd
argument_list|(
name|path
argument_list|)
expr_stmt|;
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"change file prop "
operator|+
name|name
operator|+
literal|" = "
operator|+
name|SVNPropertyValue
operator|.
name|getPropertyAsString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|myDelegate
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
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"close file "
operator|+
name|path
argument_list|)
expr_stmt|;
name|myDelegate
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
name|SVNCommitInfo
name|closeEdit
parameter_list|()
throws|throws
name|SVNException
block|{
name|myCancel
operator|.
name|checkCancelled
argument_list|()
expr_stmt|;
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"close edit"
argument_list|)
expr_stmt|;
return|return
name|myDelegate
operator|.
name|closeEdit
argument_list|()
return|;
block|}
specifier|public
name|void
name|abortEdit
parameter_list|()
throws|throws
name|SVNException
block|{
name|myLog
operator|.
name|logFine
argument_list|(
name|SVNLogType
operator|.
name|WC
argument_list|,
literal|"abort edit"
argument_list|)
expr_stmt|;
name|myDelegate
operator|.
name|abortEdit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

