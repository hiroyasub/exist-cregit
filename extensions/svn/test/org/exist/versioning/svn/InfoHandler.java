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
package|;
end_package

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
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|SVNInfo
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

begin_comment
comment|/*  * An implementation of ISVNInfoHandler that is  used  in  WorkingCopy.java  to   * display  info  on  a  working  copy path.  This implementation is passed  to  *   * SVNWCClient.doInfo(File path, SVNRevision revision, boolean recursive,   * ISVNInfoHandler handler)   *   * For each item to be processed doInfo(..) collects information and creates an   * SVNInfo which keeps that information. Then  doInfo(..)  calls  implementor's   * handler.handleInfo(SVNInfo) where it passes the gathered info.  */
end_comment

begin_class
specifier|public
class|class
name|InfoHandler
implements|implements
name|ISVNInfoHandler
block|{
comment|/*      * This is an implementation  of  ISVNInfoHandler.handleInfo(SVNInfo info).      * Just prints out information on a Working Copy path in the manner of  the      * native SVN command line client.      */
specifier|public
name|void
name|handleInfo
parameter_list|(
name|SVNInfo
name|info
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-----------------INFO-----------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Local Path: "
operator|+
name|info
operator|.
name|getFile
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"URL: "
operator|+
name|info
operator|.
name|getURL
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Repository Root URL: "
operator|+
name|info
operator|.
name|getRepositoryRootURL
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Repository UUID: "
operator|+
name|info
operator|.
name|getRepositoryUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Revision: "
operator|+
name|info
operator|.
name|getRevision
argument_list|()
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Node Kind: "
operator|+
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Schedule: "
operator|+
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Last Changed Author: "
operator|+
name|info
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Last Changed Revision: "
operator|+
name|info
operator|.
name|getCommittedRevision
argument_list|()
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Last Changed Date: "
operator|+
name|info
operator|.
name|getCommittedDate
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Properties Last Updated: "
operator|+
name|info
operator|.
name|getPropTime
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Text Last Updated: "
operator|+
name|info
operator|.
name|getTextTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Checksum: "
operator|+
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Lock Token: "
operator|+
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Lock Owner: "
operator|+
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Lock Created: "
operator|+
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getCreationDate
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Lock Expires: "
operator|+
name|info
operator|.
name|getLock
argument_list|()
operator|.
name|getExpirationDate
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Lock Comment: "
operator|+
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
block|}
block|}
end_class

end_unit

