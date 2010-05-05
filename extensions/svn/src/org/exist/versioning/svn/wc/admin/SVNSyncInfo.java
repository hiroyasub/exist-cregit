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
operator|.
name|admin
package|;
end_package

begin_comment
comment|/**  *<b>SVNSyncInfo</b> represents information on repository synchronization  * @version 1.3  * @author  TMate Software Ltd.  * @since   1.3  */
end_comment

begin_class
specifier|public
class|class
name|SVNSyncInfo
block|{
specifier|private
name|String
name|mySrcURL
decl_stmt|;
specifier|private
name|String
name|mySourceRepositoryUUID
decl_stmt|;
specifier|private
name|long
name|myLastMergedRevision
decl_stmt|;
comment|/**      * Creates a new<code>SVNSyncInfo</code> object.      *       * @param srcURL                 url of the source repository to synchronize with      * @param sourceRepositoryUUID   uuid of the source repository      * @param lastMergedRevision     last source repository revision synchronized with       * @since 1.3      */
specifier|public
name|SVNSyncInfo
parameter_list|(
name|String
name|srcURL
parameter_list|,
name|String
name|sourceRepositoryUUID
parameter_list|,
name|long
name|lastMergedRevision
parameter_list|)
block|{
name|mySrcURL
operator|=
name|srcURL
expr_stmt|;
name|mySourceRepositoryUUID
operator|=
name|sourceRepositoryUUID
expr_stmt|;
name|myLastMergedRevision
operator|=
name|lastMergedRevision
expr_stmt|;
block|}
comment|/**      * Returns the url of the source repository.      *       * @return url of the source repository synchronized with       * @since  1.3      */
specifier|public
name|String
name|getSrcURL
parameter_list|()
block|{
return|return
name|mySrcURL
return|;
block|}
comment|/**      * Returns the source repository UUID.      * @return  source repository UUID      * @since  1.3      */
specifier|public
name|String
name|getSourceRepositoryUUID
parameter_list|()
block|{
return|return
name|mySourceRepositoryUUID
return|;
block|}
comment|/**      * Returns the last revision of the source repository       * synchronized with.      * @return last merged revision      * @since  1.3      */
specifier|public
name|long
name|getLastMergedRevision
parameter_list|()
block|{
return|return
name|myLastMergedRevision
return|;
block|}
block|}
end_class

end_unit

