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

begin_comment
comment|/**  *<b>DefaultSVNCommitParameters</b> is the default commit parameters   * implementation.   *   * @version 1.3  * @since   1.2  * @author  TMate Software Ltd.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultSVNCommitParameters
implements|implements
name|ISVNCommitParameters
block|{
comment|/**      * Says a committer to skip a missing file.      *       * @param  file a missing file      * @return      {@link ISVNCommitParameters#SKIP SKIP}      */
specifier|public
name|Action
name|onMissingFile
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|SKIP
return|;
block|}
comment|/**      * Says a committer to abort the operation.      *       * @param  file a missing directory      * @return      {@link ISVNCommitParameters#ERROR ERROR}      */
specifier|public
name|Action
name|onMissingDirectory
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|ERROR
return|;
block|}
comment|/**      * Returns<span class="javakeyword">true</span>.      *       * @param directory working copy directory      * @return<span class="javakeyword">true</span>      */
specifier|public
name|boolean
name|onDirectoryDeletion
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Returns<span class="javakeyword">true</span>.      * @param file   working copy file      * @return<span class="javakeyword">true</span>      *       */
specifier|public
name|boolean
name|onFileDeletion
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

