begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ====================================================================  * Copyright (c) 2004-2010 TMate Software Ltd.  All rights reserved.  *  * This software is licensed as described in the file COPYING, which  * you should have received as part of this distribution.  The terms  * are also available at http://svnkit.com/license.html.  * If newer versions of this license are posted there, you may use a  * newer version instead, at your option.  * ====================================================================  */
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
name|SVNException
import|;
end_import

begin_comment
comment|/**  *<b>SVNEventAdapter</b> is an adapter class for {@link ISVNEventHandler}.  * Users's event handler implementations should extend this adapter class rather than implementing   * {@link ISVNEventHandler} directly. This way, if the {@link ISVNEventHandler} interface is changed    * in future, users' event handler implementations won't get broken since the changes will be reflected in   * this adapter class.   *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  */
end_comment

begin_class
specifier|public
class|class
name|SVNEventAdapter
implements|implements
name|ISVNEventHandler
block|{
comment|/**      * Does nothing. To be overridden by a user's implementation.      *       * @throws SVNCancelException       */
specifier|public
name|void
name|checkCancelled
parameter_list|()
throws|throws
name|SVNCancelException
block|{
block|}
comment|/**      * Does nothing. To be overridden by a user's implementation.      *       * @param event       * @param progress       * @throws SVNException       */
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
block|}
block|}
end_class

end_unit

