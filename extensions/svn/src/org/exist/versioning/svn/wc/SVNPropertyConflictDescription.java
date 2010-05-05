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
name|SVNNodeKind
import|;
end_import

begin_comment
comment|/**  *<b>SVNPropertyConflictDescription</b> brings a property conflict description.  *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.3  */
end_comment

begin_class
specifier|public
class|class
name|SVNPropertyConflictDescription
extends|extends
name|SVNConflictDescription
block|{
specifier|private
name|String
name|myPropertyName
decl_stmt|;
comment|/**      * Creates a new<code>SVNPropertyConflictDescription</code> object.      *       * @param mergeFiles      files involved in a property conflict      * @param nodeKind        kind of the conflicted node      * @param propertyName    versioned property name      * @param conflictAction  action lead to the conflict      * @param conflictReason  the reason why the conflict occurred      * @since 1.3      */
specifier|public
name|SVNPropertyConflictDescription
parameter_list|(
name|SVNMergeFileSet
name|mergeFiles
parameter_list|,
name|SVNNodeKind
name|nodeKind
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|SVNConflictAction
name|conflictAction
parameter_list|,
name|SVNConflictReason
name|conflictReason
parameter_list|)
block|{
name|super
argument_list|(
name|mergeFiles
argument_list|,
name|nodeKind
argument_list|,
name|conflictAction
argument_list|,
name|conflictReason
argument_list|)
expr_stmt|;
name|myPropertyName
operator|=
name|propertyName
expr_stmt|;
block|}
comment|/**      * Returns<code>false</code>.      * @return<code>false</code>      * @since 1.3       */
specifier|public
name|boolean
name|isTextConflict
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns<code>true</code>.      * @return<code>true</code>      * @since 1.3       */
specifier|public
name|boolean
name|isPropertyConflict
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Returns<code>false</code>.      * @return<code>false</code>      * @since 1.3       */
specifier|public
name|boolean
name|isTreeConflict
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns the name of the property, on which the conflict occurred.      * @return conflicted property name      * @since 1.3      */
specifier|public
name|String
name|getPropertyName
parameter_list|()
block|{
return|return
name|myPropertyName
return|;
block|}
block|}
end_class

end_unit

