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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * The<b>SVNConflictResult</b> represents the decision of the user's {@link ISVNConflictHandler conflict handler}  * regarding a conflict situation.     *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  */
end_comment

begin_class
specifier|public
class|class
name|SVNConflictResult
block|{
specifier|private
name|SVNConflictChoice
name|myConflictChoice
decl_stmt|;
specifier|private
name|File
name|myMergedFile
decl_stmt|;
comment|/**      * Creates a new<code>SVNConflictChoice</code> object.      *       * @param conflictChoice way that the conflict should be resolved in       * @param mergedFile     file containing the merge result            */
specifier|public
name|SVNConflictResult
parameter_list|(
name|SVNConflictChoice
name|conflictChoice
parameter_list|,
name|File
name|mergedFile
parameter_list|)
block|{
name|myConflictChoice
operator|=
name|conflictChoice
expr_stmt|;
name|myMergedFile
operator|=
name|mergedFile
expr_stmt|;
block|}
comment|/**      * Returns the conflict handler's choice. This way implementor can manage conflicts providing a choice       * object defining what to do with the conflict.      *       * @return  conflict choice      */
specifier|public
name|SVNConflictChoice
name|getConflictChoice
parameter_list|()
block|{
return|return
name|myConflictChoice
return|;
block|}
comment|/**      * Returns the file with the merge result.      *       *<p/>      * Usually this will be the {@link SVNMergeFileSet#getResultFile() result file} obtained by the       * user's {@link ISVNConflictHandler conflict handler} from the {@link SVNConflictDescription description}'s       * {@link SVNMergeFileSet merge file set} object.      *       * @return merged file      */
specifier|public
name|File
name|getMergedFile
parameter_list|()
block|{
return|return
name|myMergedFile
return|;
block|}
block|}
end_class

end_unit

