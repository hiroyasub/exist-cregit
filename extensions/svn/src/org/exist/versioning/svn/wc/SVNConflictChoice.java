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

begin_comment
comment|/**  * The<b>SVNConflictChoice</b> is an enumeration of constants representing the way in which the conflict   * {@link ISVNConflictHandler callback} chooses a course of action.  *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  */
end_comment

begin_class
specifier|public
class|class
name|SVNConflictChoice
block|{
comment|/**      * Constant saying: don't resolve the conflict now. The path will be marked as in a state of conflict.      */
specifier|public
specifier|static
name|SVNConflictChoice
name|POSTPONE
init|=
operator|new
name|SVNConflictChoice
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**      * Constant saying to choose the base version of the file to resolve the conflict here and now.      */
specifier|public
specifier|static
name|SVNConflictChoice
name|BASE
init|=
operator|new
name|SVNConflictChoice
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**      * Constant saying to choose the incoming version of the file to resolve the conflict here and now.      */
specifier|public
specifier|static
name|SVNConflictChoice
name|THEIRS_FULL
init|=
operator|new
name|SVNConflictChoice
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|/**      * Constant saying to choose the own version of the file to resolve the conflict here and now.      */
specifier|public
specifier|static
name|SVNConflictChoice
name|MINE_FULL
init|=
operator|new
name|SVNConflictChoice
argument_list|(
literal|3
argument_list|)
decl_stmt|;
comment|/**      * Constant saying to choose the incoming (for conflicted hunks) version of the file to resolve the conflict here and now.      */
specifier|public
specifier|static
name|SVNConflictChoice
name|THEIRS_CONFLICT
init|=
operator|new
name|SVNConflictChoice
argument_list|(
literal|4
argument_list|)
decl_stmt|;
comment|/**      * Constant saying to choose the own (for conflicted hunks) version of the file to resolve the conflict here and now.      */
specifier|public
specifier|static
name|SVNConflictChoice
name|MINE_CONFLICT
init|=
operator|new
name|SVNConflictChoice
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|/**      * Constant saying to choose the merged version of the file to resolve the conflict here and now.      */
specifier|public
specifier|static
name|SVNConflictChoice
name|MERGED
init|=
operator|new
name|SVNConflictChoice
argument_list|(
literal|6
argument_list|)
decl_stmt|;
specifier|private
name|int
name|myID
decl_stmt|;
specifier|private
name|SVNConflictChoice
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|myID
operator|=
name|id
expr_stmt|;
block|}
comment|/**      * Returns a unique ID number for this object.      * @return id number      */
specifier|public
name|int
name|getID
parameter_list|()
block|{
return|return
name|myID
return|;
block|}
block|}
end_class

end_unit

