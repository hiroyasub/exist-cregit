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
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|DefaultSVNOptions
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
name|ISVNEventHandler
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
name|SVNClientManager
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
name|SVNCopySource
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
name|SVNUpdateClient
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
name|SVNWCUtil
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
name|wc
operator|.
name|SVNRevision
import|;
end_import

begin_class
specifier|public
class|class
name|WorkingCopy
block|{
specifier|private
specifier|static
name|SVNClientManager
name|ourClientManager
decl_stmt|;
specifier|private
specifier|static
name|ISVNEventHandler
name|myCommitEventHandler
decl_stmt|;
specifier|private
specifier|static
name|ISVNEventHandler
name|myUpdateEventHandler
decl_stmt|;
specifier|private
specifier|static
name|ISVNEventHandler
name|myWCEventHandler
decl_stmt|;
specifier|public
name|WorkingCopy
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|setupLibrary
argument_list|()
expr_stmt|;
comment|/*          * Creating custom handlers that will process events          */
name|myCommitEventHandler
operator|=
operator|new
name|CommitEventHandler
argument_list|()
expr_stmt|;
name|myUpdateEventHandler
operator|=
operator|new
name|UpdateEventHandler
argument_list|()
expr_stmt|;
name|myWCEventHandler
operator|=
operator|new
name|WCEventHandler
argument_list|()
expr_stmt|;
comment|/*          * Creates a default run-time configuration options driver. Default options           * created in this way use the Subversion run-time configuration area (for           * instance, on a Windows platform it can be found in the '%APPDATA%\Subversion'           * directory).           *           * readonly = true - not to save  any configuration changes that can be done           * during the program run to a config file (config settings will only           * be read to initialize; to enable changes the readonly flag should be set          * to false).          *           * SVNWCUtil is a utility class that creates a default options driver.          */
name|DefaultSVNOptions
name|options
init|=
name|SVNWCUtil
operator|.
name|createDefaultOptions
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/*          * Creates an instance of SVNClientManager providing authentication          * information (name, password) and an options driver          */
name|ourClientManager
operator|=
name|SVNClientManager
operator|.
name|newInstance
argument_list|(
name|options
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
comment|/*          * Sets a custom event handler for operations of an SVNCommitClient           * instance          */
name|ourClientManager
operator|.
name|getCommitClient
argument_list|()
operator|.
name|setEventHandler
argument_list|(
name|myCommitEventHandler
argument_list|)
expr_stmt|;
comment|/*          * Sets a custom event handler for operations of an SVNUpdateClient           * instance          */
name|ourClientManager
operator|.
name|getUpdateClient
argument_list|()
operator|.
name|setEventHandler
argument_list|(
name|myUpdateEventHandler
argument_list|)
expr_stmt|;
comment|/*          * Sets a custom event handler for operations of an SVNWCClient           * instance          */
name|ourClientManager
operator|.
name|getWCClient
argument_list|()
operator|.
name|setEventHandler
argument_list|(
name|myWCEventHandler
argument_list|)
expr_stmt|;
block|}
comment|/*      * Initializes the library to work with a repository via       * different protocols.      */
specifier|private
specifier|static
name|void
name|setupLibrary
parameter_list|()
block|{
comment|/*          * For using over http:// and https://          */
name|DAVRepositoryFactory
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|/*          * For using over svn:// and svn+xxx://          */
name|SVNRepositoryFactoryImpl
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|/*          * For using over file:///          */
name|FSRepositoryFactory
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
comment|/*      * Creates a new version controlled directory (doesn't create any intermediate      * directories) right in a repository. Like 'svn mkdir URL -m "some comment"'       * command. It's done by invoking       *       * SVNCommitClient.doMkDir(SVNURL[] urls, String commitMessage)       *       * which takes the following parameters:      *       * urls - an array of URLs that are to be created;      *       * commitMessage - a commit log message since a URL-based directory creation is       * immediately committed to a repository.      */
specifier|public
name|SVNCommitInfo
name|makeDirectory
parameter_list|(
name|SVNURL
name|url
parameter_list|,
name|String
name|commitMessage
parameter_list|)
throws|throws
name|SVNException
block|{
comment|/*          * Returns SVNCommitInfo containing information on the new revision committed           * (revision number, etc.)           */
return|return
name|ourClientManager
operator|.
name|getCommitClient
argument_list|()
operator|.
name|doMkDir
argument_list|(
operator|new
name|SVNURL
index|[]
block|{
name|url
block|}
argument_list|,
name|commitMessage
argument_list|)
return|;
block|}
comment|/*      * Imports an unversioned directory into a repository location denoted by a      * destination URL (all necessary parent non-existent paths will be created       * automatically). This operation commits the repository to a new revision.       * Like 'svn import PATH URL (-N) -m "some comment"' command. It's done by       * invoking       *       * SVNCommitClient.doImport(File path, SVNURL dstURL, String commitMessage, boolean recursive)       *       * which takes the following parameters:      *       * path - a local unversioned directory or singal file that will be imported into a       * repository;      *       * dstURL - a repository location where the local unversioned directory/file will be       * imported into; this URL path may contain non-existent parent paths that will be       * created by the repository server;      *       * commitMessage - a commit log message since the new directory/file are immediately      * created in the repository;      *       * recursive - if true and path parameter corresponds to a directory then the directory      * will be added with all its child subdirictories, otherwise the operation will cover      * only the directory itself (only those files which are located in the directory).        */
specifier|public
name|SVNCommitInfo
name|importDirectory
parameter_list|(
name|File
name|localPath
parameter_list|,
name|SVNURL
name|dstURL
parameter_list|,
name|String
name|commitMessage
parameter_list|,
name|boolean
name|isRecursive
parameter_list|)
throws|throws
name|SVNException
block|{
comment|/*          * Returns SVNCommitInfo containing information on the new revision committed           * (revision number, etc.)           */
return|return
name|ourClientManager
operator|.
name|getCommitClient
argument_list|()
operator|.
name|doImport
argument_list|(
name|localPath
argument_list|,
name|dstURL
argument_list|,
name|commitMessage
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|SVNDepth
operator|.
name|fromRecurse
argument_list|(
name|isRecursive
argument_list|)
argument_list|)
return|;
block|}
comment|/*      * Committs changes in a working copy to a repository. Like       * 'svn commit PATH -m "some comment"' command. It's done by invoking       *       * SVNCommitClient.doCommit(File[] paths, boolean keepLocks, String commitMessage,       * boolean force, boolean recursive)       *       * which takes the following parameters:      *       * paths - working copy paths which changes are to be committed;      *       * keepLocks - if true then doCommit(..) won't unlock locked paths; otherwise they will      * be unlocked after a successful commit;       *       * commitMessage - a commit log message;      *       * force - if true then a non-recursive commit will be forced anyway;        *       * recursive - if true and a path corresponds to a directory then doCommit(..) recursively       * commits changes for the entire directory, otherwise - only for child entries of the       * directory;      */
specifier|public
name|SVNCommitInfo
name|commit
parameter_list|(
name|File
name|wcPath
parameter_list|,
name|boolean
name|keepLocks
parameter_list|,
name|String
name|commitMessage
parameter_list|)
throws|throws
name|SVNException
block|{
comment|/*          * Returns SVNCommitInfo containing information on the new revision committed           * (revision number, etc.)           */
return|return
name|ourClientManager
operator|.
name|getCommitClient
argument_list|()
operator|.
name|doCommit
argument_list|(
operator|new
name|File
index|[]
block|{
name|wcPath
block|}
argument_list|,
name|keepLocks
argument_list|,
name|commitMessage
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|SVNDepth
operator|.
name|INFINITY
argument_list|)
return|;
block|}
comment|/*      * Checks out a working copy from a repository. Like 'svn checkout URL[@REV] PATH (-r..)'      * command; It's done by invoking       *       * SVNUpdateClient.doCheckout(SVNURL url, File dstPath, SVNRevision pegRevision,       * SVNRevision revision, boolean recursive)      *       * which takes the following parameters:      *       * url - a repository location from where a working copy is to be checked out;      *       * dstPath - a local path where the working copy will be fetched into;      *       * pegRevision - an SVNRevision representing a revision to concretize      * url (what exactly URL a user means and is sure of being the URL he needs); in other      * words that is the revision in which the URL is first looked up;      *       * revision - a revision at which a working copy being checked out is to be;       *       * recursive - if true and url corresponds to a directory then doCheckout(..) recursively       * fetches out the entire directory, otherwise - only child entries of the directory;         */
specifier|public
name|long
name|checkout
parameter_list|(
name|SVNURL
name|url
parameter_list|,
name|SVNRevision
name|revision
parameter_list|,
name|File
name|destPath
parameter_list|,
name|boolean
name|isRecursive
parameter_list|)
throws|throws
name|SVNException
block|{
name|SVNUpdateClient
name|updateClient
init|=
name|ourClientManager
operator|.
name|getUpdateClient
argument_list|()
decl_stmt|;
comment|/*          * sets externals not to be ignored during the checkout          */
name|updateClient
operator|.
name|setIgnoreExternals
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|/*          * returns the number of the revision at which the working copy is           */
return|return
name|updateClient
operator|.
name|doCheckout
argument_list|(
name|url
argument_list|,
name|destPath
argument_list|,
name|revision
argument_list|,
name|revision
argument_list|,
name|SVNDepth
operator|.
name|fromRecurse
argument_list|(
name|isRecursive
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/*      * Updates a working copy (brings changes from the repository into the working copy).       * Like 'svn update PATH' command; It's done by invoking       *       * SVNUpdateClient.doUpdate(File file, SVNRevision revision, boolean recursive)       *       * which takes the following parameters:      *       * file - a working copy entry that is to be updated;      *       * revision - a revision to which a working copy is to be updated;      *       * recursive - if true and an entry is a directory then doUpdate(..) recursively       * updates the entire directory, otherwise - only child entries of the directory;         */
specifier|public
name|long
name|update
parameter_list|(
name|File
name|wcPath
parameter_list|,
name|SVNRevision
name|updateToRevision
parameter_list|,
name|boolean
name|isRecursive
parameter_list|)
throws|throws
name|SVNException
block|{
name|SVNUpdateClient
name|updateClient
init|=
name|ourClientManager
operator|.
name|getUpdateClient
argument_list|()
decl_stmt|;
comment|/*          * sets externals not to be ignored during the update          */
name|updateClient
operator|.
name|setIgnoreExternals
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|/*          * returns the number of the revision wcPath was updated to          */
return|return
name|updateClient
operator|.
name|doUpdate
argument_list|(
name|wcPath
argument_list|,
name|updateToRevision
argument_list|,
name|SVNDepth
operator|.
name|fromRecurse
argument_list|(
name|isRecursive
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/*      * Updates a working copy to a different URL. Like 'svn switch URL' command.      * It's done by invoking       *       * SVNUpdateClient.doSwitch(File file, SVNURL url, SVNRevision revision, boolean recursive)       *       * which takes the following parameters:      *       * file - a working copy entry that is to be switched to a new url;      *       * url - a target URL a working copy is to be updated against;      *       * revision - a revision to which a working copy is to be updated;      *       * recursive - if true and an entry (file) is a directory then doSwitch(..) recursively       * switches the entire directory, otherwise - only child entries of the directory;         */
specifier|public
name|long
name|switchToURL
parameter_list|(
name|File
name|wcPath
parameter_list|,
name|SVNURL
name|url
parameter_list|,
name|SVNRevision
name|updateToRevision
parameter_list|,
name|boolean
name|isRecursive
parameter_list|)
throws|throws
name|SVNException
block|{
name|SVNUpdateClient
name|updateClient
init|=
name|ourClientManager
operator|.
name|getUpdateClient
argument_list|()
decl_stmt|;
comment|/*          * sets externals not to be ignored during the switch          */
name|updateClient
operator|.
name|setIgnoreExternals
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|/*          * returns the number of the revision wcPath was updated to          */
return|return
name|updateClient
operator|.
name|doSwitch
argument_list|(
name|wcPath
argument_list|,
name|url
argument_list|,
name|SVNRevision
operator|.
name|UNDEFINED
argument_list|,
name|updateToRevision
argument_list|,
name|SVNDepth
operator|.
name|getInfinityOrFilesDepth
argument_list|(
name|isRecursive
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/*      * Collects status information on local path(s). Like 'svn status (-u) (-N)'       * command. It's done by invoking       *       * SVNStatusClient.doStatus(File path, boolean recursive,       * boolean remote, boolean reportAll, boolean includeIgnored,       * boolean collectParentExternals, ISVNStatusHandler handler)       *       * which takes the following parameters:      *       * path - an entry which status info to be gathered;      *       * recursive - if true and an entry is a directory then doStatus(..) collects status       * info not only for that directory but for each item inside stepping down recursively;      *       * remote - if true then doStatus(..) will cover the repository (not only the working copy)      * as well to find out what entries are out of date;      *       * reportAll - if true then doStatus(..) will also include unmodified entries;      *       * includeIgnored - if true then doStatus(..) will also include entries being ignored;       *       * collectParentExternals - if true then externals definitions won't be ignored;      *       * handler - an implementation of ISVNStatusHandler to process status info per each entry      * doStatus(..) traverses; such info is collected in an SVNStatus object and      * is passed to a handler's handleStatus(SVNStatus status) method where an implementor      * decides what to do with it.        */
specifier|public
name|void
name|showStatus
parameter_list|(
name|File
name|wcPath
parameter_list|,
name|boolean
name|isRecursive
parameter_list|,
name|boolean
name|isRemote
parameter_list|,
name|boolean
name|isReportAll
parameter_list|,
name|boolean
name|isIncludeIgnored
parameter_list|,
name|boolean
name|isCollectParentExternals
parameter_list|)
throws|throws
name|SVNException
block|{
comment|/*          * StatusHandler displays status information for each entry in the console (in the           * manner of the native Subversion command line client)          */
name|ourClientManager
operator|.
name|getStatusClient
argument_list|()
operator|.
name|doStatus
argument_list|(
name|wcPath
argument_list|,
name|SVNRevision
operator|.
name|HEAD
argument_list|,
name|SVNDepth
operator|.
name|fromRecurse
argument_list|(
name|isRecursive
argument_list|)
argument_list|,
name|isRemote
argument_list|,
name|isReportAll
argument_list|,
name|isIncludeIgnored
argument_list|,
name|isCollectParentExternals
argument_list|,
operator|new
name|StatusHandler
argument_list|(
name|isRemote
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/*      * Collects information on local path(s). Like 'svn info (-R)' command.      * It's done by invoking       *       * SVNWCClient.doInfo(File path, SVNRevision revision,      * boolean recursive, ISVNInfoHandler handler)       *       * which takes the following parameters:      *       * path - a local entry for which info will be collected;      *       * revision - a revision of an entry which info is interested in; if it's not      * WORKING then info is got from a repository;      *       * recursive - if true and an entry is a directory then doInfo(..) collects info       * not only for that directory but for each item inside stepping down recursively;      *       * handler - an implementation of ISVNInfoHandler to process info per each entry      * doInfo(..) traverses; such info is collected in an SVNInfo object and      * is passed to a handler's handleInfo(SVNInfo info) method where an implementor      * decides what to do with it.           */
specifier|public
name|void
name|showInfo
parameter_list|(
name|File
name|wcPath
parameter_list|,
name|SVNRevision
name|revision
parameter_list|,
name|boolean
name|isRecursive
parameter_list|)
throws|throws
name|SVNException
block|{
comment|/*          * InfoHandler displays information for each entry in the console (in the manner of          * the native Subversion command line client)          */
name|ourClientManager
operator|.
name|getWCClient
argument_list|()
operator|.
name|doInfo
argument_list|(
name|wcPath
argument_list|,
name|SVNRevision
operator|.
name|UNDEFINED
argument_list|,
name|revision
argument_list|,
name|SVNDepth
operator|.
name|getInfinityOrEmptyDepth
argument_list|(
name|isRecursive
argument_list|)
argument_list|,
literal|null
argument_list|,
operator|new
name|InfoHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Puts directories and files under version control scheduling them for addition      * to a repository. They will be added in a next commit. Like 'svn add PATH'       * command. It's done by invoking       *       * SVNWCClient.doAdd(File path, boolean force,       * boolean mkdir, boolean climbUnversionedParents, boolean recursive)       *       * which takes the following parameters:      *       * path - an entry to be scheduled for addition;      *       * force - set to true to force an addition of an entry anyway;      *       * mkdir - if true doAdd(..) creates an empty directory at path and schedules      * it for addition, like 'svn mkdir PATH' command;      *       * climbUnversionedParents - if true and the parent of the entry to be scheduled      * for addition is not under version control, then doAdd(..) automatically schedules      * the parent for addition, too;      *       * recursive - if true and an entry is a directory then doAdd(..) recursively       * schedules all its inner dir entries for addition as well.       */
specifier|public
name|void
name|addEntry
parameter_list|(
name|File
name|wcPath
parameter_list|)
throws|throws
name|SVNException
block|{
name|ourClientManager
operator|.
name|getWCClient
argument_list|()
operator|.
name|doAdd
argument_list|(
name|wcPath
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
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
block|}
comment|/*      * Locks working copy paths, so that no other user can commit changes to them.      * Like 'svn lock PATH' command. It's done by invoking       *       * SVNWCClient.doLock(File[] paths, boolean stealLock, String lockMessage)       *       * which takes the following parameters:      *       * paths - an array of local entries to be locked;      *       * stealLock - set to true to steal the lock from another user or working copy;      *       * lockMessage - an optional lock comment string.      */
specifier|public
name|void
name|lock
parameter_list|(
name|File
name|wcPath
parameter_list|,
name|boolean
name|isStealLock
parameter_list|,
name|String
name|lockComment
parameter_list|)
throws|throws
name|SVNException
block|{
name|ourClientManager
operator|.
name|getWCClient
argument_list|()
operator|.
name|doLock
argument_list|(
operator|new
name|File
index|[]
block|{
name|wcPath
block|}
argument_list|,
name|isStealLock
argument_list|,
name|lockComment
argument_list|)
expr_stmt|;
block|}
comment|/*      * Schedules directories and files for deletion from version control upon the next      * commit (locally). Like 'svn delete PATH' command. It's done by invoking       *       * SVNWCClient.doDelete(File path, boolean force, boolean dryRun)       *       * which takes the following parameters:      *       * path - an entry to be scheduled for deletion;      *       * force - a boolean flag which is set to true to force a deletion even if an entry      * has local modifications;      *       * dryRun - set to true not to delete an entry but to check if it can be deleted;      * if false - then it's a deletion itself.        */
specifier|public
name|void
name|delete
parameter_list|(
name|File
name|wcPath
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|SVNException
block|{
name|ourClientManager
operator|.
name|getWCClient
argument_list|()
operator|.
name|doDelete
argument_list|(
name|wcPath
argument_list|,
name|force
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/*      * Duplicates srcURL to dstURL (URL->URL)in a repository remembering history.      * Like 'svn copy srcURL dstURL -m "some comment"' command. It's done by      * invoking       *       * doCopy(SVNURL srcURL, SVNRevision srcRevision, SVNURL dstURL,       * boolean isMove, String commitMessage)       *       * which takes the following parameters:      *       * srcURL - a source URL that is to be copied;      *       * srcRevision - a definite revision of srcURL       *       * dstURL - a URL where srcURL will be copied; if srcURL& dstURL are both       * directories then there are two cases:       * a) dstURL already exists - then doCopy(..) will duplicate the entire source       * directory and put it inside dstURL (for example,       * consider srcURL = svn://localhost/rep/MyRepos,       * dstURL = svn://localhost/rep/MyReposCopy, in this case if doCopy(..) succeeds       * MyRepos will be in MyReposCopy - svn://localhost/rep/MyReposCopy/MyRepos);       * b) dstURL doesn't exist yet - then doCopy(..) will create a directory and      * recursively copy entries from srcURL into dstURL (for example, consider the same      * srcURL = svn://localhost/rep/MyRepos, dstURL = svn://localhost/rep/MyReposCopy,       * in this case if doCopy(..) succeeds MyRepos entries will be in MyReposCopy, like:      * svn://localhost/rep/MyRepos/Dir1 -> svn://localhost/rep/MyReposCopy/Dir1...);        *       * isMove - if false then srcURL is only copied to dstURL what      * corresponds to 'svn copy srcURL dstURL -m "some comment"'; but if it's true then      * srcURL will be copied and deleted - 'svn move srcURL dstURL -m "some comment"';       *       * commitMessage - a commit log message since URL->URL copying is immediately       * committed to a repository.      */
specifier|public
name|SVNCommitInfo
name|copy
parameter_list|(
name|SVNURL
name|srcURL
parameter_list|,
name|SVNURL
name|dstURL
parameter_list|,
name|boolean
name|isMove
parameter_list|,
name|String
name|commitMessage
parameter_list|)
throws|throws
name|SVNException
block|{
comment|/*          * SVNRevision.HEAD means the latest revision.          * Returns SVNCommitInfo containing information on the new revision committed           * (revision number, etc.)           */
return|return
name|ourClientManager
operator|.
name|getCopyClient
argument_list|()
operator|.
name|doCopy
argument_list|(
operator|new
name|SVNCopySource
index|[]
block|{
operator|new
name|SVNCopySource
argument_list|(
name|SVNRevision
operator|.
name|HEAD
argument_list|,
name|SVNRevision
operator|.
name|HEAD
argument_list|,
name|srcURL
argument_list|)
block|}
argument_list|,
name|dstURL
argument_list|,
name|isMove
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|commitMessage
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/*      * Displays error information and exits.       */
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|message
operator|+
operator|(
name|e
operator|!=
literal|null
condition|?
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
comment|//        System.exit(1);
block|}
comment|/*      * This method does not relate to SVNKit API. Just a method which creates      * local directories and files :)      */
specifier|public
specifier|final
name|void
name|createLocalDir
parameter_list|(
name|Resource
name|aNewDir
parameter_list|,
name|Resource
index|[]
name|localFiles
parameter_list|,
name|String
index|[]
name|fileContents
parameter_list|)
block|{
if|if
condition|(
operator|!
name|aNewDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|error
argument_list|(
literal|"failed to create a new directory '"
operator|+
name|aNewDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'."
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|localFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Resource
name|aNewFile
init|=
name|localFiles
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|aNewFile
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|error
argument_list|(
literal|"failed to create a new file '"
operator|+
name|aNewFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'."
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|aNewFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|error
argument_list|(
literal|"error while creating a new file '"
operator|+
name|aNewFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
name|String
name|contents
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|i
operator|>
name|fileContents
operator|.
name|length
operator|-
literal|1
condition|)
block|{
continue|continue;
block|}
name|contents
operator|=
name|fileContents
index|[
name|i
index|]
expr_stmt|;
comment|/* 	         * writing a text into the file 	         */
name|OutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fos
operator|=
name|aNewFile
operator|.
name|getOutputStream
argument_list|()
expr_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|contents
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|error
argument_list|(
literal|"the file '"
operator|+
name|aNewFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"' is not found"
argument_list|,
name|fnfe
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|error
argument_list|(
literal|"error while writing into the file '"
operator|+
name|aNewFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

