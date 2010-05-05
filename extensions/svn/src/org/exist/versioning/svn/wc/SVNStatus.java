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
name|wc
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
name|Date
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
name|internal
operator|.
name|wc
operator|.
name|SVNFileUtil
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
name|admin
operator|.
name|SVNEntry
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
name|SVNLock
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
name|wc
operator|.
name|SVNRevision
import|;
end_import

begin_comment
comment|/**  * The<b>SVNStatus</b> class is used to provide detailed status information for  * a Working Copy item as a result of a status operation invoked by a   * doStatus() method of<b>SVNStatusClient</b>.<b>SVNStatus</b> objects are  * generated for each 'interesting' local item and depending on the doStatus() method   * in use either passed for notification to an<b>ISVNStatusHandler</b>   * implementation or such an object is just returned by the method as a   * status info for a single item.   *   *<p>  * Within the status handler implementation a developer decides how to interpret status   * information. For some purposes this way may be more flexible in comparison   * with calling doStatus() that returns an<b>SVNStatus</b> per one local item.  * However the latter one may be useful when needing to find out the status of   * the concrete item.    *   *<p>  *   * There are two approaches how to process<b>SVNStatus</b> objects:<br />  * 1. Implementing an<b>ISVNStatusHandler</b>:  *<pre class="javacode">  *<span class="javakeyword">import</span> org.tmatesoft.svn.core.wc.ISVNStatusHandler;  *<span class="javakeyword">import</span> org.tmatesoft.svn.core.wc.SVNStatus;  *<span class="javakeyword">import</span> org.tmatesoft.svn.core.wc.SVNStatusType;  * ...  *   *<span class="javakeyword">public class</span> MyCustomStatusHandler<span class="javakeyword">implements</span> ISVNStatusHandler {  *<span class="javakeyword">public void</span> handleStatus(SVNStatus status) {  *<span class="javacomment">//parse the item's contents status</span>  *<span class="javakeyword">if</span>(status.getContentsStatus() == SVNStatusType.STATUS_MODIFIED) {  *             ...  *         }<span class="javakeyword">else if</span>(status.getContentsStatus() == SVNStatusType.STATUS_CONFLICTED) {  *             ...          *         }  *         ...  *<span class="javacomment">//parse properties status</span>  *<span class="javakeyword">if</span>(status.getPropertiesStatus() == SVNStatusType.STATUS_MODIFIED) {  *             ...  *         }  *         ...  *     }  * }</pre><br />  * ...and providing a status handler implementation to an<b>SVNStatusClient</b>'s   * doStatus() method:  *<pre class="javacode">  * ...  *<span class="javakeyword">import</span> org.tmatesoft.svn.core.wc.SVNStatusClient;  * ...  *   * SVNStatusClient statusClient;  * ...  *   * statusClient.doStatus(....,<span class="javakeyword">new</span> MyCustomStatusHandler());  * ...</pre><br />  * 2. Or process an<b>SVNStatus</b> like this:  *<pre class="javacode">  * ...  * SVNStatus status = statusClient.doStatus(<span class="javakeyword">new</span> File(myPath),<span class="javakeyword">false</span>);  *<span class="javacomment">//parsing status info here</span>  * ...</pre>  *</p>   *<p>  *<b>SVNStatus</b>'s methods which names start with<code>getRemote</code> are relevant  * for remote status invocations - that is when a doStatus() method of<b>SVNStatusClient</b>  * is called with the flag<code>remote</code> set to<span class="javakeyword">true</span>.  *    * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  * @see     ISVNStatusHandler  * @see     SVNStatusType  * @see<a target="_top" href="http://svnkit.com/kb/examples/">Examples</a>  */
end_comment

begin_class
specifier|public
class|class
name|SVNStatus
block|{
specifier|private
name|SVNURL
name|myURL
decl_stmt|;
specifier|private
name|File
name|myFile
decl_stmt|;
specifier|private
name|SVNNodeKind
name|myKind
decl_stmt|;
specifier|private
name|SVNRevision
name|myRevision
decl_stmt|;
specifier|private
name|SVNRevision
name|myCommittedRevision
decl_stmt|;
specifier|private
name|Date
name|myCommittedDate
decl_stmt|;
specifier|private
name|String
name|myAuthor
decl_stmt|;
specifier|private
name|SVNStatusType
name|myContentsStatus
decl_stmt|;
specifier|private
name|SVNStatusType
name|myPropertiesStatus
decl_stmt|;
specifier|private
name|SVNStatusType
name|myRemoteContentsStatus
decl_stmt|;
specifier|private
name|SVNStatusType
name|myRemotePropertiesStatus
decl_stmt|;
specifier|private
name|boolean
name|myIsLocked
decl_stmt|;
specifier|private
name|boolean
name|myIsCopied
decl_stmt|;
specifier|private
name|boolean
name|myIsSwitched
decl_stmt|;
specifier|private
name|boolean
name|myIsFileExternal
decl_stmt|;
specifier|private
name|File
name|myConflictNewFile
decl_stmt|;
specifier|private
name|File
name|myConflictOldFile
decl_stmt|;
specifier|private
name|File
name|myConflictWrkFile
decl_stmt|;
specifier|private
name|File
name|myPropRejectFile
decl_stmt|;
specifier|private
name|String
name|myCopyFromURL
decl_stmt|;
specifier|private
name|SVNRevision
name|myCopyFromRevision
decl_stmt|;
specifier|private
name|SVNLock
name|myRemoteLock
decl_stmt|;
specifier|private
name|SVNLock
name|myLocalLock
decl_stmt|;
specifier|private
name|Map
name|myEntryProperties
decl_stmt|;
specifier|private
name|SVNRevision
name|myRemoteRevision
decl_stmt|;
specifier|private
name|SVNURL
name|myRemoteURL
decl_stmt|;
specifier|private
name|SVNNodeKind
name|myRemoteKind
decl_stmt|;
specifier|private
name|String
name|myRemoteAuthor
decl_stmt|;
specifier|private
name|Date
name|myRemoteDate
decl_stmt|;
specifier|private
name|Date
name|myLocalContentsDate
decl_stmt|;
specifier|private
name|Date
name|myLocalPropertiesDate
decl_stmt|;
specifier|private
name|SVNEntry
name|myEntry
decl_stmt|;
specifier|private
name|String
name|myChangelistName
decl_stmt|;
specifier|private
name|int
name|myWorkingCopyFormat
decl_stmt|;
specifier|private
name|SVNTreeConflictDescription
name|myTreeConflict
decl_stmt|;
comment|/**      * Constructs an<b>SVNStatus</b> object filling it with status information      * details.        *       *<p>      * Used by SVNKit internals to construct and initialize an       *<b>SVNStatus</b> object. It's not intended for users (from an API       * point of view).      *       * @param url                      item's repository location       * @param file                     item's path in a File representation      * @param kind                     item's node kind      * @param revision                 item's working revision      * @param committedRevision        item's last changed revision      * @param committedDate            item's last changed date      * @param author                   item's last commit author       * @param contentsStatus           local status of item's contents      * @param propertiesStatus         local status of item's properties      * @param remoteContentsStatus     status of item's contents against a repository      * @param remotePropertiesStatus   status of item's properties against a repository      * @param isLocked                 if the item is locked by the driver (not a user lock)      * @param isCopied                 if the item is added with history       * @param isSwitched               if the item is switched to a different URL      * @param isFileExternal           tells if the item is an external file      * @param conflictNewFile          temp file with latest changes from the repository      * @param conflictOldFile          temp file just as the conflicting one was at the BASE revision      * @param conflictWrkFile          temp file with all user's current local modifications       * @param projRejectFile           temp file describing properties conflicts      * @param copyFromURL              url of the item's ancestor from which the item was copied       * @param copyFromRevision         item's ancestor revision from which the item was copied      * @param remoteLock               item's lock in the repository      * @param localLock                item's local lock      * @param entryProperties          item's SVN specific '&lt;entry' properties      * @param changelistName           changelist name which the item belongs to      * @param wcFormatVersion          working copy format number               * @param treeConflict             tree conflict description      * @since 1.3      */
specifier|public
name|SVNStatus
parameter_list|(
name|SVNURL
name|url
parameter_list|,
name|File
name|file
parameter_list|,
name|SVNNodeKind
name|kind
parameter_list|,
name|SVNRevision
name|revision
parameter_list|,
name|SVNRevision
name|committedRevision
parameter_list|,
name|Date
name|committedDate
parameter_list|,
name|String
name|author
parameter_list|,
name|SVNStatusType
name|contentsStatus
parameter_list|,
name|SVNStatusType
name|propertiesStatus
parameter_list|,
name|SVNStatusType
name|remoteContentsStatus
parameter_list|,
name|SVNStatusType
name|remotePropertiesStatus
parameter_list|,
name|boolean
name|isLocked
parameter_list|,
name|boolean
name|isCopied
parameter_list|,
name|boolean
name|isSwitched
parameter_list|,
name|boolean
name|isFileExternal
parameter_list|,
name|File
name|conflictNewFile
parameter_list|,
name|File
name|conflictOldFile
parameter_list|,
name|File
name|conflictWrkFile
parameter_list|,
name|File
name|projRejectFile
parameter_list|,
name|String
name|copyFromURL
parameter_list|,
name|SVNRevision
name|copyFromRevision
parameter_list|,
name|SVNLock
name|remoteLock
parameter_list|,
name|SVNLock
name|localLock
parameter_list|,
name|Map
name|entryProperties
parameter_list|,
name|String
name|changelistName
parameter_list|,
name|int
name|wcFormatVersion
parameter_list|,
name|SVNTreeConflictDescription
name|treeConflict
parameter_list|)
block|{
name|myURL
operator|=
name|url
expr_stmt|;
name|myFile
operator|=
name|file
expr_stmt|;
name|myKind
operator|=
name|kind
operator|==
literal|null
condition|?
name|SVNNodeKind
operator|.
name|NONE
else|:
name|kind
expr_stmt|;
name|myRevision
operator|=
name|revision
operator|==
literal|null
condition|?
name|SVNRevision
operator|.
name|UNDEFINED
else|:
name|revision
expr_stmt|;
name|myCommittedRevision
operator|=
name|committedRevision
operator|==
literal|null
condition|?
name|SVNRevision
operator|.
name|UNDEFINED
else|:
name|committedRevision
expr_stmt|;
name|myCommittedDate
operator|=
name|committedDate
expr_stmt|;
name|myAuthor
operator|=
name|author
expr_stmt|;
name|myContentsStatus
operator|=
name|contentsStatus
operator|==
literal|null
condition|?
name|SVNStatusType
operator|.
name|STATUS_NONE
else|:
name|contentsStatus
expr_stmt|;
name|myPropertiesStatus
operator|=
name|propertiesStatus
operator|==
literal|null
condition|?
name|SVNStatusType
operator|.
name|STATUS_NONE
else|:
name|propertiesStatus
expr_stmt|;
name|myRemoteContentsStatus
operator|=
name|remoteContentsStatus
operator|==
literal|null
condition|?
name|SVNStatusType
operator|.
name|STATUS_NONE
else|:
name|remoteContentsStatus
expr_stmt|;
name|myRemotePropertiesStatus
operator|=
name|remotePropertiesStatus
operator|==
literal|null
condition|?
name|SVNStatusType
operator|.
name|STATUS_NONE
else|:
name|remotePropertiesStatus
expr_stmt|;
name|myIsLocked
operator|=
name|isLocked
expr_stmt|;
name|myIsCopied
operator|=
name|isCopied
expr_stmt|;
name|myIsSwitched
operator|=
name|isSwitched
expr_stmt|;
name|myIsFileExternal
operator|=
name|isFileExternal
expr_stmt|;
name|myConflictNewFile
operator|=
name|conflictNewFile
expr_stmt|;
name|myConflictOldFile
operator|=
name|conflictOldFile
expr_stmt|;
name|myConflictWrkFile
operator|=
name|conflictWrkFile
expr_stmt|;
name|myCopyFromURL
operator|=
name|copyFromURL
expr_stmt|;
name|myCopyFromRevision
operator|=
name|copyFromRevision
operator|==
literal|null
condition|?
name|SVNRevision
operator|.
name|UNDEFINED
else|:
name|copyFromRevision
expr_stmt|;
name|myRemoteLock
operator|=
name|remoteLock
expr_stmt|;
name|myLocalLock
operator|=
name|localLock
expr_stmt|;
name|myPropRejectFile
operator|=
name|projRejectFile
expr_stmt|;
name|myEntryProperties
operator|=
name|entryProperties
expr_stmt|;
name|myChangelistName
operator|=
name|changelistName
expr_stmt|;
name|myWorkingCopyFormat
operator|=
name|wcFormatVersion
expr_stmt|;
name|myTreeConflict
operator|=
name|treeConflict
expr_stmt|;
block|}
comment|/**      * Gets the item's repository location. URL is taken from the        * {@link org.tmatesoft.svn.core.SVNProperty#URL} property.      *       * @return  the item's URL represented as an<b>SVNURL</b> object      */
specifier|public
name|SVNURL
name|getURL
parameter_list|()
block|{
return|return
name|myURL
return|;
block|}
comment|/**      * Gets the item's latest repository location.       * For example, the item could have been moved in the repository,      * but {@link SVNStatus#getURL() getURL()} returns the item's       * URL as it's defined in a URL entry property. Applicable      * for a remote status invocation.      *       * @return  the item's URL as it's real repository location       */
specifier|public
name|SVNURL
name|getRemoteURL
parameter_list|()
block|{
return|return
name|myRemoteURL
return|;
block|}
comment|/**      * Gets the item's path in the filesystem.      *       * @return a File representation of the item's path      */
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|myFile
return|;
block|}
comment|/**      * Gets the item's node kind characterizing it as an entry.       *       * @return the item's node kind (whether it's a file, directory, etc.)      */
specifier|public
name|SVNNodeKind
name|getKind
parameter_list|()
block|{
return|return
name|myKind
return|;
block|}
comment|/**      * Gets the item's current working revision.      *        * @return the item's working revision      */
specifier|public
name|SVNRevision
name|getRevision
parameter_list|()
block|{
return|return
name|myRevision
return|;
block|}
comment|/**      * Gets the revision when the item was last changed (committed).      *       * @return the last committed revision      */
specifier|public
name|SVNRevision
name|getCommittedRevision
parameter_list|()
block|{
return|return
name|myCommittedRevision
return|;
block|}
comment|/**      * Gets the timestamp when the item was last changed (committed).      *       * @return the last committed date       */
specifier|public
name|Date
name|getCommittedDate
parameter_list|()
block|{
return|return
name|myCommittedDate
return|;
block|}
comment|/**      * Gets the author who last changed the item.      *       * @return the item's last commit author      */
specifier|public
name|String
name|getAuthor
parameter_list|()
block|{
return|return
name|myAuthor
return|;
block|}
comment|/**      * Gets the Working Copy local item's contents status type.      *       * @return the local contents status type      */
specifier|public
name|SVNStatusType
name|getContentsStatus
parameter_list|()
block|{
return|return
name|myContentsStatus
return|;
block|}
comment|/**      * Gets the Working Copy local item's properties status type.      *       * @return the local properties status type      */
specifier|public
name|SVNStatusType
name|getPropertiesStatus
parameter_list|()
block|{
return|return
name|myPropertiesStatus
return|;
block|}
comment|/**      * Gets the Working Copy item's contents status type against the      * repository - that is comparing the item's BASE revision and the       * latest one in the repository when the item was changed.       * Applicable for a remote status invocation.      *      *<p>      * If the remote contents status type != {@link SVNStatusType#STATUS_NONE}       * the local file may be out of date.        *       * @return the remote contents status type      */
specifier|public
name|SVNStatusType
name|getRemoteContentsStatus
parameter_list|()
block|{
return|return
name|myRemoteContentsStatus
return|;
block|}
comment|/**      * Gets the Working Copy item's properties status type against the       * repository - that is comparing the item's BASE revision and the       * latest one in the repository when the item was changed. Applicable       * for a remote status invocation.      *       *<p>      * If the remote properties status type != {@link SVNStatusType#STATUS_NONE}       * the local file may be out of date.        *       * @return the remote properties status type      */
specifier|public
name|SVNStatusType
name|getRemotePropertiesStatus
parameter_list|()
block|{
return|return
name|myRemotePropertiesStatus
return|;
block|}
comment|/**      * Finds out if the item is locked (not a user lock but a driver's       * one when during an operation a Working Copy is locked in<i>.svn</i>       * administrative areas to prevent from other operations interrupting       * until the running one finishes).        *<p>      * To clean up a Working Copy use {@link SVNWCClient#doCleanup(File) doCleanup()}.      *        * @return<span class="javakeyword">true</span> if locked, otherwise      *<span class="javakeyword">false</span>       */
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|myIsLocked
return|;
block|}
comment|/**      * Finds out if the item is added with history.      *       * @return<span class="javakeyword">true</span> if the item      *         is added with history, otherwise<span class="javakeyword">false</span>      */
specifier|public
name|boolean
name|isCopied
parameter_list|()
block|{
return|return
name|myIsCopied
return|;
block|}
comment|/**      * Finds out whether the item is switched to a different      * repository location.      *        * @return<span class="javakeyword">true</span> if switched, otherwise      *<span class="javakeyword">false</span>      */
specifier|public
name|boolean
name|isSwitched
parameter_list|()
block|{
return|return
name|myIsSwitched
return|;
block|}
comment|/**      * Tells if this is an externals file or not.      *       * @return<span class="javakeyword">true</span> if is a file external,       *         otherwise<span class="javakeyword">false</span>      * @since  1.3      */
specifier|public
name|boolean
name|isFileExternal
parameter_list|()
block|{
return|return
name|myIsFileExternal
return|;
block|}
comment|/**      * Gets the temporary file that contains all latest changes from the       * repository which led to a conflict with local changes. This file is      * at the HEAD revision.      *       * @return  an autogenerated temporary file just as it is in the latest       *          revision in the repository       */
specifier|public
name|File
name|getConflictNewFile
parameter_list|()
block|{
return|return
name|myConflictNewFile
return|;
block|}
comment|/**      * Gets the temporary BASE revision file of that working file that is      * currently in conflict with changes received from the repository. This      * file does not contain the latest user's modifications, only 'pristine'      * contents.        *       * @return an autogenerated temporary file just as the conflicting file was      *         before any modifications to it      */
specifier|public
name|File
name|getConflictOldFile
parameter_list|()
block|{
return|return
name|myConflictOldFile
return|;
block|}
comment|/**      * Gets the temporary<i>'.mine'</i> file with all current local changes to the       * original file. That is if the file item is in conflict with changes that       * came during an update this temporary file is created to get the snapshot      * of the user's file with only the user's local modifications and nothing       * more.        *       * @return an autogenerated temporary file with only the user's modifications       */
specifier|public
name|File
name|getConflictWrkFile
parameter_list|()
block|{
return|return
name|myConflictWrkFile
return|;
block|}
comment|/**      * Gets the<i>'.prej'</i> file containing details on properties conflicts.      * If the item's properties are in conflict with those that came      * during an update this file will contain a conflict description.       *       * @return  the properties conflicts file      */
specifier|public
name|File
name|getPropRejectFile
parameter_list|()
block|{
return|return
name|myPropRejectFile
return|;
block|}
comment|/**      * Gets the URL (repository location) of the ancestor from which the      * item was copied. That is when the item is added with history.      *       * @return the item ancestor's URL      */
specifier|public
name|String
name|getCopyFromURL
parameter_list|()
block|{
return|return
name|myCopyFromURL
return|;
block|}
comment|/**      * Gets the revision of the item's ancestor      * from which the item was copied (the item is added      * with history).       *       * @return the ancestor's revision       */
specifier|public
name|SVNRevision
name|getCopyFromRevision
parameter_list|()
block|{
return|return
name|myCopyFromRevision
return|;
block|}
comment|/**      * Gets the file item's repository lock -       * applicable for a remote status invocation.      *       * @return file item's repository lock      */
specifier|public
name|SVNLock
name|getRemoteLock
parameter_list|()
block|{
return|return
name|myRemoteLock
return|;
block|}
comment|/**      * Gets the file item's local lock.      *       * @return file item's local lock      */
specifier|public
name|SVNLock
name|getLocalLock
parameter_list|()
block|{
return|return
name|myLocalLock
return|;
block|}
comment|/**      * Gets the item's SVN specific<i>'&lt;entry'</i> properties.      * These properties' names start with       * {@link org.tmatesoft.svn.core.SVNProperty#SVN_ENTRY_PREFIX}.      *       * @return a Map which keys are names of SVN entry properties mapped      *         against their values (both strings)      */
specifier|public
name|Map
name|getEntryProperties
parameter_list|()
block|{
return|return
name|myEntryProperties
return|;
block|}
comment|/**      * Gets the item's last committed repository revision. Relevant for a       * remote status invocation.       *       * @return the latest repository revision when the item was changed;       *<span class="javakeyword">null</span> if there are no incoming      *         changes for this file or directory.       */
specifier|public
name|SVNRevision
name|getRemoteRevision
parameter_list|()
block|{
return|return
name|myRemoteRevision
return|;
block|}
comment|/**      * Returns the kind of the item got from the repository. Relevant for a       * remote status invocation.       *        * @return a remote item kind      */
specifier|public
name|SVNNodeKind
name|getRemoteKind
parameter_list|()
block|{
return|return
name|myRemoteKind
return|;
block|}
comment|/**      * Gets the item's last changed date. Relevant for a       * remote status invocation.       *       * @return a repository last changed date      */
specifier|public
name|Date
name|getRemoteDate
parameter_list|()
block|{
return|return
name|myRemoteDate
return|;
block|}
comment|/**      * Gets the item's last changed author. Relevant for a       * remote status invocation.       *       * @return a last commit author       */
specifier|public
name|String
name|getRemoteAuthor
parameter_list|()
block|{
return|return
name|myRemoteAuthor
return|;
block|}
comment|/**      * Returns the last modified local time of the file item.       * Irrelevant for directories (for directories returns<code>Date(0)</code>).      *       * @return last modified time of the file      */
specifier|public
name|Date
name|getWorkingContentsDate
parameter_list|()
block|{
if|if
condition|(
name|myLocalContentsDate
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|getFile
argument_list|()
operator|!=
literal|null
operator|&&
name|getKind
argument_list|()
operator|==
name|SVNNodeKind
operator|.
name|FILE
condition|)
block|{
name|myLocalContentsDate
operator|=
operator|new
name|Date
argument_list|(
name|getFile
argument_list|()
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|myLocalContentsDate
operator|=
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|myLocalContentsDate
return|;
block|}
comment|/**      * Returns the last modified local time of file or directory       * properties.       *       * @return last modified time of the item properties      */
specifier|public
name|Date
name|getWorkingPropertiesDate
parameter_list|()
block|{
if|if
condition|(
name|myLocalPropertiesDate
operator|==
literal|null
condition|)
block|{
name|File
name|propFile
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getFile
argument_list|()
operator|!=
literal|null
operator|&&
name|getKind
argument_list|()
operator|==
name|SVNNodeKind
operator|.
name|DIR
condition|)
block|{
name|propFile
operator|=
operator|new
name|Resource
argument_list|(
name|getFile
argument_list|()
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|SVNFileUtil
operator|.
name|getAdminDirectoryName
argument_list|()
argument_list|)
expr_stmt|;
name|propFile
operator|=
operator|new
name|Resource
argument_list|(
name|propFile
argument_list|,
literal|"dir-props"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|getFile
argument_list|()
operator|!=
literal|null
operator|&&
name|getKind
argument_list|()
operator|==
name|SVNNodeKind
operator|.
name|FILE
condition|)
block|{
name|propFile
operator|=
operator|new
name|Resource
argument_list|(
name|getFile
argument_list|()
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|SVNFileUtil
operator|.
name|getAdminDirectoryName
argument_list|()
argument_list|)
expr_stmt|;
name|propFile
operator|=
operator|new
name|Resource
argument_list|(
name|propFile
argument_list|,
literal|"props/"
operator|+
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".svn-work"
argument_list|)
expr_stmt|;
block|}
name|myLocalPropertiesDate
operator|=
name|propFile
operator|!=
literal|null
condition|?
operator|new
name|Date
argument_list|(
name|propFile
operator|.
name|lastModified
argument_list|()
argument_list|)
else|:
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|myLocalPropertiesDate
return|;
block|}
comment|/**      * Marks the item as an external. This method is used by SVNKit internals      * and not intended for users (from an API point of view).      *      */
specifier|public
name|void
name|markExternal
parameter_list|()
block|{
name|myContentsStatus
operator|=
name|SVNStatusType
operator|.
name|STATUS_EXTERNAL
expr_stmt|;
block|}
comment|/**      * Sets the item's remote status. Used by SVNKit internals and not      * intended for users (from an API point of view).      *       * @param contents item's contents status type against the repository       * @param props    item's properties status type against the repository      * @param lock     item's lock in the repository      * @param kind     item's node kind      */
specifier|public
name|void
name|setRemoteStatus
parameter_list|(
name|SVNStatusType
name|contents
parameter_list|,
name|SVNStatusType
name|props
parameter_list|,
name|SVNLock
name|lock
parameter_list|,
name|SVNNodeKind
name|kind
parameter_list|)
block|{
if|if
condition|(
name|contents
operator|==
name|SVNStatusType
operator|.
name|STATUS_ADDED
operator|&&
name|myRemoteContentsStatus
operator|==
name|SVNStatusType
operator|.
name|STATUS_DELETED
condition|)
block|{
name|contents
operator|=
name|SVNStatusType
operator|.
name|STATUS_REPLACED
expr_stmt|;
block|}
name|myRemoteContentsStatus
operator|=
name|contents
operator|!=
literal|null
condition|?
name|contents
else|:
name|myRemoteContentsStatus
expr_stmt|;
name|myRemotePropertiesStatus
operator|=
name|props
operator|!=
literal|null
condition|?
name|props
else|:
name|myRemotePropertiesStatus
expr_stmt|;
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
name|myRemoteLock
operator|=
name|lock
expr_stmt|;
block|}
if|if
condition|(
name|kind
operator|!=
literal|null
condition|)
block|{
name|myRemoteKind
operator|=
name|kind
expr_stmt|;
block|}
block|}
comment|/**      * Sets the item's remote status. Used by SVNKit internals and not      * intended for users (from an API point of view).      *       * @param url      item's repository URL      * @param contents item's contents status type against the repository       * @param props    item's properties status type against the repository      * @param lock     item's lock in the repository      * @param kind     item's node kind      * @param revision item's latest revision when it was last committed      * @param date     last item's committed date       * @param author   last item's committed author      */
specifier|public
name|void
name|setRemoteStatus
parameter_list|(
name|SVNURL
name|url
parameter_list|,
name|SVNStatusType
name|contents
parameter_list|,
name|SVNStatusType
name|props
parameter_list|,
name|SVNLock
name|lock
parameter_list|,
name|SVNNodeKind
name|kind
parameter_list|,
name|SVNRevision
name|revision
parameter_list|,
name|Date
name|date
parameter_list|,
name|String
name|author
parameter_list|)
block|{
name|setRemoteStatus
argument_list|(
name|contents
argument_list|,
name|props
argument_list|,
name|lock
argument_list|,
name|kind
argument_list|)
expr_stmt|;
name|myRemoteURL
operator|=
name|url
expr_stmt|;
name|myRemoteRevision
operator|=
name|revision
operator|==
literal|null
condition|?
name|SVNRevision
operator|.
name|UNDEFINED
else|:
name|revision
expr_stmt|;
name|myRemoteDate
operator|=
name|date
expr_stmt|;
name|myRemoteAuthor
operator|=
name|author
expr_stmt|;
name|myRemoteKind
operator|=
name|kind
expr_stmt|;
block|}
comment|/**      * Sets the item's contents status type. Used by SVNKit internals and not      * intended for users (from an API point of view).      *       * @param statusType status type of the item's contents      */
specifier|public
name|void
name|setContentsStatus
parameter_list|(
name|SVNStatusType
name|statusType
parameter_list|)
block|{
name|myContentsStatus
operator|=
name|statusType
expr_stmt|;
block|}
comment|/**      * Sets a WC entry for which this object is generated.      * Used in internals.      *       * @param entry  a WC entry      */
specifier|public
name|void
name|setEntry
parameter_list|(
name|SVNEntry
name|entry
parameter_list|)
block|{
name|myEntry
operator|=
name|entry
expr_stmt|;
block|}
comment|/**      * Returns a WC entry for which this object is generated.      *       * @return a WC entry (if set)      */
specifier|public
name|SVNEntry
name|getEntry
parameter_list|()
block|{
return|return
name|myEntry
return|;
block|}
comment|/**      * Returns the name of the changelist which the working copy item, denoted by this object,      * belongs to.         *       * @return  changelist name        * @since   1.2      */
specifier|public
name|String
name|getChangelistName
parameter_list|()
block|{
return|return
name|myChangelistName
return|;
block|}
comment|/**      * Returns a tree conflict description.      *       * @return tree conflict description;<code>null</code> if       *         no conflict description exists on this item      * @since  1.3      */
specifier|public
name|SVNTreeConflictDescription
name|getTreeConflict
parameter_list|()
block|{
return|return
name|myTreeConflict
return|;
block|}
comment|/**      * Returns the working copy format number for the admin directory       * which the statused item is versioned under.      *       *<p/>      * If this status object is a result of a remote status operation, the method will return       *<code>-1</code>.       *       * @return working copy format number;<code>-1</code> for remote status      * @since  1.2      */
specifier|public
name|int
name|getWorkingCopyFormat
parameter_list|()
block|{
return|return
name|myWorkingCopyFormat
return|;
block|}
block|}
end_class

end_unit

