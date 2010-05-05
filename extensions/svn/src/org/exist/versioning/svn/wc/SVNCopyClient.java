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
name|Collection
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
name|SVNCopyDriver
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
name|SVNErrorManager
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
name|SVNPath
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
name|util
operator|.
name|SVNEncodingUtil
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
name|ISVNExternalsHandler
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
name|ISVNRepositoryPool
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
comment|/**  * The<b>SVNCopyClient</b> provides methods to perform any kinds of copying and moving that SVN  * supports - operating on both Working Copies (WC) and URLs.  *   *<p>  * Copy operations allow a user to copy versioned files and directories with all their   * previous history in several ways.   *   *<p>  * Supported copy operations are:  *<ul>  *<li> Working Copy to Working Copy (WC-to-WC) copying - this operation copies the source  * Working Copy item to the destination one and schedules the source copy for addition with history.  *<li> Working Copy to URL (WC-to-URL) copying - this operation commits to the repository (exactly  * to that repository location that is specified by URL) a copy of the Working Copy item.  *<li> URL to Working Copy (URL-to-WC) copying - this operation will copy the source item from  * the repository to the Working Copy item and schedule the source copy for addition with history.  *<li> URL to URL (URL-to-URL) copying - this is a fully repository-side operation, it commits   * a copy of the source item to a specified repository location (within the same repository, of  * course).   *</ul>  *   *<p>   * Besides just copying<b>SVNCopyClient</b> also is able to move a versioned item - that is  * first making a copy of the source item and then scheduling the source item for deletion   * when operating on a Working Copy, or right committing the deletion of the source item when   * operating immediately on the repository.  *   *<p>  * Supported move operations are:  *<ul>  *<li> Working Copy to Working Copy (WC-to-WC) moving - this operation copies the source  * Working Copy item to the destination one and schedules the source item for deletion.  *<li> URL to URL (URL-to-URL) moving - this is a fully repository-side operation, it commits   * a copy of the source item to a specified repository location and deletes the source item.   *</ul>  *   *<p>  * Overloaded<b>doCopy()</b> methods of<b>SVNCopyClient</b> are similar to  *<code>'svn copy'</code> and<code>'svn move'</code> commands of the SVN command line client.   *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  * @see<a target="_top" href="http://svnkit.com/kb/examples/">Examples</a>  */
end_comment

begin_class
specifier|public
class|class
name|SVNCopyClient
extends|extends
name|SVNCopyDriver
block|{
specifier|protected
name|ISVNCommitHandler
name|myCommitHandler
decl_stmt|;
specifier|protected
name|ISVNCommitParameters
name|myCommitParameters
decl_stmt|;
specifier|protected
name|ISVNExternalsHandler
name|myExternalsHandler
decl_stmt|;
comment|/**      * Constructs and initializes an<b>SVNCopyClient</b> object      * with the specified run-time configuration and authentication       * drivers.      *       *<p>      * If<code>options</code> is<span class="javakeyword">null</span>,      * then this<b>SVNCopyClient</b> will be using a default run-time      * configuration driver  which takes client-side settings from the       * default SVN's run-time configuration area but is not able to      * change those settings (read more on {@link ISVNOptions} and {@link SVNWCUtil}).        *       *<p>      * If<code>authManager</code> is<span class="javakeyword">null</span>,      * then this<b>SVNCopyClient</b> will be using a default authentication      * and network layers driver (see {@link SVNWCUtil#createDefaultAuthenticationManager()})      * which uses server-side settings and auth storage from the       * default SVN's run-time configuration area (or system properties      * if that area is not found).      *       * @param authManager an authentication and network layers driver      * @param options     a run-time configuration options driver           */
specifier|public
name|SVNCopyClient
parameter_list|(
name|ISVNAuthenticationManager
name|authManager
parameter_list|,
name|ISVNOptions
name|options
parameter_list|)
block|{
name|super
argument_list|(
name|authManager
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs and initializes an<b>SVNCopyClient</b> object      * with the specified run-time configuration and repository pool object.      *       *<p/>      * If<code>options</code> is<span class="javakeyword">null</span>,      * then this<b>SVNCopyClient</b> will be using a default run-time      * configuration driver  which takes client-side settings from the      * default SVN's run-time configuration area but is not able to      * change those settings (read more on {@link ISVNOptions} and {@link SVNWCUtil}).      *       *<p/>      * If<code>repositoryPool</code> is<span class="javakeyword">null</span>,      * then {@link org.tmatesoft.svn.core.io.SVNRepositoryFactory} will be used to create {@link SVNRepository repository access objects}.      *      * @param repositoryPool   a repository pool object      * @param options          a run-time configuration options driver      */
specifier|public
name|SVNCopyClient
parameter_list|(
name|ISVNRepositoryPool
name|repositoryPool
parameter_list|,
name|ISVNOptions
name|options
parameter_list|)
block|{
name|super
argument_list|(
name|repositoryPool
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets an implementation of<b>ISVNCommitHandler</b> to      * the commit handler that will be used during commit operations to handle      * commit log messages. The handler will receive a clien's log message and items      * (represented as<b>SVNCommitItem</b> objects) that will be      * committed. Depending on implementor's aims the initial log message can      * be modified (or something else) and returned back.      *      *<p>      * If using<b>SVNCopyClient</b> without specifying any      * commit handler then a default one will be used - {@link DefaultSVNCommitHandler}.      *      * @param handler               an implementor's handler that will be used to handle      *                              commit log messages      * @see   #getCommitHandler()      * @see   SVNCommitItem      */
specifier|public
name|void
name|setCommitHandler
parameter_list|(
name|ISVNCommitHandler
name|handler
parameter_list|)
block|{
name|myCommitHandler
operator|=
name|handler
expr_stmt|;
block|}
comment|/**      * Returns the specified commit handler (if set) being in use or a default one      * (<b>DefaultSVNCommitHandler</b>) if no special      * implementations of<b>ISVNCommitHandler</b> were      * previousely provided.      *      * @return  the commit handler being in use or a default one      * @see     #setCommitHandler(ISVNCommitHandler)      * @see     DefaultSVNCommitHandler      */
specifier|public
name|ISVNCommitHandler
name|getCommitHandler
parameter_list|()
block|{
if|if
condition|(
name|myCommitHandler
operator|==
literal|null
condition|)
block|{
name|myCommitHandler
operator|=
operator|new
name|DefaultSVNCommitHandler
argument_list|()
expr_stmt|;
block|}
return|return
name|myCommitHandler
return|;
block|}
comment|/**      * Sets commit parameters to use.      *      *<p>      * When no parameters are set {@link DefaultSVNCommitParameters default}      * ones are used.      *      * @param parameters commit parameters      * @see              #getCommitParameters()      */
specifier|public
name|void
name|setCommitParameters
parameter_list|(
name|ISVNCommitParameters
name|parameters
parameter_list|)
block|{
name|myCommitParameters
operator|=
name|parameters
expr_stmt|;
block|}
comment|/**      * Returns commit parameters.      *      *<p>      * If no user parameters were previously specified, once creates and      * returns {@link DefaultSVNCommitParameters default} ones.      *      * @return commit parameters      * @see    #setCommitParameters(ISVNCommitParameters)      */
specifier|public
name|ISVNCommitParameters
name|getCommitParameters
parameter_list|()
block|{
if|if
condition|(
name|myCommitParameters
operator|==
literal|null
condition|)
block|{
name|myCommitParameters
operator|=
operator|new
name|DefaultSVNCommitParameters
argument_list|()
expr_stmt|;
block|}
return|return
name|myCommitParameters
return|;
block|}
comment|/**      * Sets an externals handler to be used by this client object.      *      * @param externalsHandler user's implementation of {@link ISVNExternalsHandler}      * @see   #getExternalsHandler()      * @since 1.2      */
specifier|public
name|void
name|setExternalsHandler
parameter_list|(
name|ISVNExternalsHandler
name|externalsHandler
parameter_list|)
block|{
name|myExternalsHandler
operator|=
name|externalsHandler
expr_stmt|;
block|}
comment|/**      * Returns an externals handler used by this update client.      *      *<p/>      * If no user's handler is provided then {@link ISVNExternalsHandler#DEFAULT} is returned and      * used by this client object by default.      *      *<p/>      * For more information what externals handlers are for, please, refer to {@link ISVNExternalsHandler} and      * {@link #doCopy(SVNCopySource[], SVNURL, boolean, boolean, boolean, String, SVNProperties)}.      *      * @return           externals handler being in use      * @see              #setExternalsHandler(ISVNExternalsHandler)      * @since            1.2      */
specifier|public
name|ISVNExternalsHandler
name|getExternalsHandler
parameter_list|()
block|{
if|if
condition|(
name|myExternalsHandler
operator|==
literal|null
condition|)
block|{
name|myExternalsHandler
operator|=
name|ISVNExternalsHandler
operator|.
name|DEFAULT
expr_stmt|;
block|}
return|return
name|myExternalsHandler
return|;
block|}
comment|/**       * Copies each source in<code>sources</code> to<code>dst</code>.      *      *<p/>      * If multiple<code>sources</code> are given,<code>dst</code> must be a directory, and<code>sources</code>       * will be copied as children of<code>dst</code>.      *      *<p/>      * Each<code>src</code> in<code>sources</code> must be files or directories under version control,      * or URLs of a versioned item in the repository. If<code>sources</code> has multiple items, they        * must be all repository URLs or all working copy paths.      *       *<p/>      * The parent of<code>dst</code> must already exist.      *       *<p/>      * If<code>sources</code> has only one item, attempts to copy it to<code>dst</code>.       * If<code>failWhenDstExists</code> is<span class="javakeyword">false</span> and<code>dst</code> already       * exists, attempts to copy the item as a child of<code>dst</code>. If<code>failWhenDstExists</code> is       *<span class="javakeyword">true</span> and<code>dst</code> already exists, throws an {@link SVNException}       * with the {@link SVNErrorCode#ENTRY_EXISTS} error code.      *       *<p/>      * If<code>sources</code> has multiple items, and<code>failWhenDstExists</code> is       *<span class="javakeyword">false</span>, all<code>sources</code> are copied as children of<code>dst</code>.       * If any child of<code>dst</code> already exists with the same name any item in<code>sources</code>,      * throws an {@link SVNException} with the {@link SVNErrorCode#ENTRY_EXISTS} error code.      *       *<p/>      * If<code>sources</code> has multiple items, and<code>failWhenDstExists</code> is       *<span class="javakeyword">true</span>, throws an {@link SVNException} with the       * {@link SVNErrorCode#CLIENT_MULTIPLE_SOURCES_DISALLOWED}.      *      *<p/>      * This method is just a variant of a local add operation, where<code>sources</code> are scheduled for       * addition as copies. No changes will happen to the repository until a commit occurs. This scheduling can       * be removed with {@link SVNWCClient#doRevert(File[], SVNDepth, Collection)}.      *       *<p/>      * If<code>makeParents is<span class="javakeyword">true</span>, creates any non-existent parent directories      * also.      *       *<p/>      * If the caller's {@link ISVNEventHandler} is non-<span class="javakeyword">null</span>, invokes it        * for each item added at the new location.      *       *<p/>      * Note: this routine requires repository access only when sources are urls.      *       * @param  sources               array of copy sources       * @param  dst                   destination working copy path      * @param  isMove                if<span class="javakeyword">true</span>, then it will be a move operation       *                               (delete, then add with history)                       * @param  makeParents           if<span class="javakeyword">true</span>, creates non-existent parent       *                               directories as well      * @param  failWhenDstExists     controls whether to fail or not if<code>dst</code> already exists      * @throws SVNException                * @since                        1.2, SVN 1.5      */
specifier|public
name|void
name|doCopy
parameter_list|(
name|SVNCopySource
index|[]
name|sources
parameter_list|,
name|File
name|dst
parameter_list|,
name|boolean
name|isMove
parameter_list|,
name|boolean
name|makeParents
parameter_list|,
name|boolean
name|failWhenDstExists
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|sources
operator|.
name|length
operator|>
literal|1
operator|&&
name|failWhenDstExists
condition|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|CLIENT_MULTIPLE_SOURCES_DISALLOWED
argument_list|)
decl_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|SVNLogType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
name|sources
operator|=
name|expandCopySources
argument_list|(
name|sources
argument_list|)
expr_stmt|;
if|if
condition|(
name|sources
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
try|try
block|{
name|setupCopy
argument_list|(
name|sources
argument_list|,
operator|new
name|SVNPath
argument_list|(
name|dst
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|isMove
argument_list|,
name|makeParents
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|getCommitHandler
argument_list|()
argument_list|,
name|getCommitParameters
argument_list|()
argument_list|,
name|getExternalsHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
name|SVNErrorCode
name|err
init|=
name|e
operator|.
name|getErrorMessage
argument_list|()
operator|.
name|getErrorCode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|failWhenDstExists
operator|&&
name|sources
operator|.
name|length
operator|==
literal|1
operator|&&
operator|(
name|err
operator|==
name|SVNErrorCode
operator|.
name|ENTRY_EXISTS
operator|||
name|err
operator|==
name|SVNErrorCode
operator|.
name|FS_ALREADY_EXISTS
operator|)
condition|)
block|{
name|SVNCopySource
name|source
init|=
name|sources
index|[
literal|0
index|]
decl_stmt|;
name|String
name|baseName
init|=
name|source
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|.
name|isURL
argument_list|()
condition|)
block|{
name|baseName
operator|=
name|SVNEncodingUtil
operator|.
name|uriDecode
argument_list|(
name|baseName
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|setupCopy
argument_list|(
name|sources
argument_list|,
operator|new
name|SVNPath
argument_list|(
operator|new
name|File
argument_list|(
name|dst
argument_list|,
name|baseName
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|isMove
argument_list|,
name|makeParents
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|getCommitHandler
argument_list|()
argument_list|,
name|getCommitParameters
argument_list|()
argument_list|,
name|getExternalsHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|second
parameter_list|)
block|{
throw|throw
name|second
throw|;
block|}
return|return;
block|}
throw|throw
name|e
throw|;
block|}
block|}
comment|/**      * Copies each source in<code>sources</code> to<code>dst</code>.     *     *<p/>     * If multiple<code>sources</code> are given,<code>dst</code> must be a directory, and<code>sources</code>      * will be copied as children of<code>dst</code>.     *     *<p/>     * Each<code>src</code> in<code>sources</code> must be files or directories under version control,     * or URLs of a versioned item in the repository. If<code>sources</code> has multiple items, they       * must be all repository URLs or all working copy paths.     *      *<p/>     * The parent of<code>dst</code> must already exist.     *      *<p/>     * If<code>sources</code> has only one item, attempts to copy it to<code>dst</code>.      * If<code>failWhenDstExists</code> is<span class="javakeyword">false</span> and<code>dst</code> already      * exists, attempts to copy the item as a child of<code>dst</code>. If<code>failWhenDstExists</code> is      *<span class="javakeyword">true</span> and<code>dst</code> already exists, throws an {@link SVNException}      * with the {@link SVNErrorCode#FS_ALREADY_EXISTS} error code.     *      *<p/>     * If<code>sources</code> has multiple items, and<code>failWhenDstExists</code> is      *<span class="javakeyword">false</span>, all<code>sources</code> are copied as children of<code>dst</code>.      * If any child of<code>dst</code> already exists with the same name any item in<code>sources</code>,     * throws an {@link SVNException} with the {@link SVNErrorCode#FS_ALREADY_EXISTS} error code.     *      *<p/>     * If<code>sources</code> has multiple items, and<code>failWhenDstExists</code> is      *<span class="javakeyword">true</span>, throws an {@link SVNException} with the      * {@link SVNErrorCode#CLIENT_MULTIPLE_SOURCES_DISALLOWED}.     *     *<p/>     * {@link ISVNAuthenticationManager Authentication manager} (whether provided directly through the      * appropriate constructor or in an {@link ISVNRepositoryPool} instance) and {@link #getCommitHandler() commit handler}      * are used to immediately attempt to commit the copy action in the repository.      *     *<p/>     * If<code>makeParents is<span class="javakeyword">true</span>, creates any non-existent parent directories     * also.     *      *<p/>     * If non-<span class="javakeyword">null</span>,<code>revisionProperties</code> is an object holding      * additional, custom revision properties (<code>String</code> to {@link SVNPropertyValue} mappings) to be      * set on the new revision. This table cannot contain any standard Subversion properties.     *      *<p/>     * If the caller's {@link ISVNEventHandler} is non-<span class="javakeyword">null</span>, invokes it       * for each item added at the new location.     *      *<p/>     * When performing a wc-to-url copy (tagging|branching from a working copy) it's possible to fix      * revisions of external working copies (if any) which are located within the working copy being copied.     * For example, imagine you have a working copy and on one of its subdirecotries you set an      *<span class="javastring">"svn:externals"</span> property which does not contain a revision number.      * Suppose you have made a tag from your working copy and in some period of time a user checks out      * that tag. It could have happened that the external project has evolved since the tag creation moment      * and the tag version is nomore compatible with it. So, the user has a broken project since it will not      * compile because of the API incompatibility between the two versions of the external project: the HEAD      * one and the one existed in the moment of the tag creation. That is why it appears useful to fix externals      * revisions during a wc-to-url copy. To enable externals revision fixing a user should implement      * {@link ISVNExternalsHandler}. The user's implementation      * {@link ISVNExternalsHandler#handleExternal(File, SVNURL, SVNRevision, SVNRevision, String, SVNRevision)}      * method will be called on every external that will be met in the working copy. If the user's implementation      * returns non-<span class="javakeyword">null</span> external revision, it's compared with the revisions      * fetched from the external definition. If they are different, the user's revision will be written in      * the external definition of the tag. Otherwise if the returned revision is equal to the revision from      * the external definition or if the user's implementation returns<span class="javakeyword">null</span> for      * that external, it will be skipped (i.e. left as is, unprocessed).             *      *<p/>     * Note: this routine requires repository access.     *      * @param  sources               array of copy sources      * @param  dst                   destination url     * @param  isMove                if<span class="javakeyword">true</span>, then it will be a move operation      *                               (delete, then add with history)                      * @param  makeParents           if<span class="javakeyword">true</span>, creates non-existent parent      *                               directories as well     * @param  failWhenDstExists     controls whether to fail or not if<code>dst</code> already exists     * @param  commitMessage         commit log message     * @param  revisionProperties    custom revision properties     * @return                       information about the new committed revision      * @throws SVNException               * @since                        1.2, SVN 1.5     */
specifier|public
name|SVNCommitInfo
name|doCopy
parameter_list|(
name|SVNCopySource
index|[]
name|sources
parameter_list|,
name|SVNURL
name|dst
parameter_list|,
name|boolean
name|isMove
parameter_list|,
name|boolean
name|makeParents
parameter_list|,
name|boolean
name|failWhenDstExists
parameter_list|,
name|String
name|commitMessage
parameter_list|,
name|SVNProperties
name|revisionProperties
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|sources
operator|.
name|length
operator|>
literal|1
operator|&&
name|failWhenDstExists
condition|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|CLIENT_MULTIPLE_SOURCES_DISALLOWED
argument_list|)
decl_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|SVNLogType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
name|sources
operator|=
name|expandCopySources
argument_list|(
name|sources
argument_list|)
expr_stmt|;
if|if
condition|(
name|sources
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|SVNCommitInfo
operator|.
name|NULL
return|;
block|}
try|try
block|{
return|return
name|setupCopy
argument_list|(
name|sources
argument_list|,
operator|new
name|SVNPath
argument_list|(
name|dst
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|isMove
argument_list|,
name|makeParents
argument_list|,
name|commitMessage
argument_list|,
name|revisionProperties
argument_list|,
name|getCommitHandler
argument_list|()
argument_list|,
name|getCommitParameters
argument_list|()
argument_list|,
name|getExternalsHandler
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
name|SVNErrorCode
name|err
init|=
name|e
operator|.
name|getErrorMessage
argument_list|()
operator|.
name|getErrorCode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|failWhenDstExists
operator|&&
name|sources
operator|.
name|length
operator|==
literal|1
operator|&&
operator|(
name|err
operator|==
name|SVNErrorCode
operator|.
name|ENTRY_EXISTS
operator|||
name|err
operator|==
name|SVNErrorCode
operator|.
name|FS_ALREADY_EXISTS
operator|)
condition|)
block|{
name|SVNCopySource
name|source
init|=
name|sources
index|[
literal|0
index|]
decl_stmt|;
name|String
name|baseName
init|=
name|source
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|source
operator|.
name|isURL
argument_list|()
condition|)
block|{
name|baseName
operator|=
name|SVNEncodingUtil
operator|.
name|uriEncode
argument_list|(
name|baseName
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|setupCopy
argument_list|(
name|sources
argument_list|,
operator|new
name|SVNPath
argument_list|(
name|dst
operator|.
name|appendPath
argument_list|(
name|baseName
argument_list|,
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|isMove
argument_list|,
name|makeParents
argument_list|,
name|commitMessage
argument_list|,
name|revisionProperties
argument_list|,
name|getCommitHandler
argument_list|()
argument_list|,
name|getCommitParameters
argument_list|()
argument_list|,
name|getExternalsHandler
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|second
parameter_list|)
block|{
throw|throw
name|second
throw|;
block|}
block|}
throw|throw
name|e
throw|;
block|}
block|}
comment|/**      * Converts a disjoint working copy to a copied one.      *       *<p/>      * Note: this routine does not require repository access. However if it's performed on an old format       * working copy where repository root urls were not written, the routine will connect to the repository       * to fetch the repository root url.       *       * @param  nestedWC      the root of the working copy located in another working copy (disjoint wc)      * @throws SVNException  in the following cases:      *<ul>      *<li/>exception with {@link SVNErrorCode#UNSUPPORTED_FEATURE} error code -       *                       if<code>nestedWC</code> is either not a directory, or has no parent at all;      *                       if the current local filesystem parent of<code>nestedWC</code> is actually a       *                       child of it in the repository      *<li/>exception with {@link SVNErrorCode#ENTRY_EXISTS} error code -        *                       if<code>nestedWC</code> is not a disjoint working copy, i.e. there is already      *                       a versioned item under the parent path of<code>nestedWC</code>;      *                       if<code>nestedWC</code> is not in the repository yet (has got a schedule for       *                       addition flag)      *<li/>exception with {@link SVNErrorCode#WC_INVALID_SCHEDULE} error code -       *                       if<code>nestedWC</code> is not from the same repository as the parent directory;      *                       if the parent of<code>nestedWC</code> is scheduled for deletion;      *                       if<code>nestedWC</code> is scheduled for deletion      *<li/>      *</ul>      * @since                1.2.0       */
specifier|public
name|void
name|doCopy
parameter_list|(
name|File
name|nestedWC
parameter_list|)
throws|throws
name|SVNException
block|{
name|copyDisjointWCToWC
argument_list|(
name|nestedWC
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

