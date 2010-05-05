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

begin_comment
comment|/**  * The<b>ISVNStatusHandler</b> interface should be implemented in order to  * be further provided to some of<b>SVNStatusClient</b>'s doStatus() methods  * to handle status information of Working Copy items.   *   *<p>  * When running a status operation using a status handler an   *<b>SVNStatusClient</b>'s doStatus() method generates an<b>SVNStatus</b>  * object per each interesting WC entry and dispatches that object to the  * status handler where it's up to a developer to retrieve status detailes     * from the<b>SVNStatus</b> object and interprete them in a desired way.  *   *<p>  * All calls to a<b>handleStatus()</b> method are synchronous - that is the  * caller is blocked till the method finishes.  *    * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  * @see     SVNStatusClient  * @see     SVNStatus  * @see<a target="_top" href="http://svnkit.com/kb/examples/">Examples</a>  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|ISVNStatusHandler
block|{
comment|/**      * Handles WC item's status information using an<b>SVNStatus</b> object.      *       * @param status  an object that contains per item status information      * @throws SVNException      */
specifier|public
name|void
name|handleStatus
parameter_list|(
name|SVNStatus
name|status
parameter_list|)
throws|throws
name|SVNException
function_decl|;
block|}
end_interface

end_unit

