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

begin_comment
comment|/**  * The<b>ISVNCommitHandler</b> should be implemented to   * provide an ability to manage commit log messages for items to be committed in  * a common transaction.  *   *<p>  * The interface defines the only one method which takes the initial log message  * and an array of items that are intended for a commit. For example, an implementor's   * code can process those items and add some generated additional comment to that one   * passed into the method. There could be plenty of scenarios.    *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  * @see     DefaultSVNCommitHandler        */
end_comment

begin_interface
specifier|public
interface|interface
name|ISVNCommitHandler
block|{
comment|/**      * Handles the incoming initial log message and items intended for a commit and       * returns a new commit log message.      *        * @param  message			an initial log message      * @param  commitables		an array of items to be committed      * @return					a new log message string or NULL to cancel commit operation.      * @throws SVNException      */
specifier|public
name|String
name|getCommitMessage
parameter_list|(
name|String
name|message
parameter_list|,
name|SVNCommitItem
index|[]
name|commitables
parameter_list|)
throws|throws
name|SVNException
function_decl|;
comment|/**      * Handles the incoming revision properties and returns filtered revision properties given the paths       * (represented by<code>commitables</code>) collected for committing and the commit log message.      *       *<p>      * Only the returned filtered revision properties will be set on a new committed revision.      *       * @param  message             log message for commit          * @param  commitables         paths to commit      * @param  revisionProperties  initial revision properties      * @return                     filtered revision properties      * @throws SVNException       */
specifier|public
name|SVNProperties
name|getRevisionProperties
parameter_list|(
name|String
name|message
parameter_list|,
name|SVNCommitItem
index|[]
name|commitables
parameter_list|,
name|SVNProperties
name|revisionProperties
parameter_list|)
throws|throws
name|SVNException
function_decl|;
block|}
end_interface

end_unit

