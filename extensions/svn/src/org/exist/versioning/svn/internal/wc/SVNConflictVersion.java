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
name|internal
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

begin_comment
comment|/**  *<b>SVNConflictVersion</b> represents Info about one of the conflicting versions of a node.  *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.3  */
end_comment

begin_class
specifier|public
class|class
name|SVNConflictVersion
block|{
specifier|private
specifier|final
name|SVNURL
name|myRepositoryRoot
decl_stmt|;
specifier|private
specifier|final
name|String
name|myPath
decl_stmt|;
specifier|private
specifier|final
name|long
name|myPegRevision
decl_stmt|;
specifier|private
specifier|final
name|SVNNodeKind
name|myKind
decl_stmt|;
comment|/**      * Creates a new<code>SVNConflictVersion</code>.      *       * @param repositoryRoot  repository root url      * @param path            absolute repository path                      * @param pegRevision     peg revision at which to look up<code>path</code>      * @param kind            node kind of the<code>path</code>      * @since 1.3      */
specifier|public
name|SVNConflictVersion
parameter_list|(
name|SVNURL
name|repositoryRoot
parameter_list|,
name|String
name|path
parameter_list|,
name|long
name|pegRevision
parameter_list|,
name|SVNNodeKind
name|kind
parameter_list|)
block|{
name|myRepositoryRoot
operator|=
name|repositoryRoot
expr_stmt|;
name|myPath
operator|=
name|path
expr_stmt|;
name|myPegRevision
operator|=
name|pegRevision
expr_stmt|;
name|myKind
operator|=
name|kind
expr_stmt|;
block|}
comment|/**      * Returns the repository root url.      *       * @return repository root url      * @since  1.3      */
specifier|public
name|SVNURL
name|getRepositoryRoot
parameter_list|()
block|{
return|return
name|myRepositoryRoot
return|;
block|}
comment|/**      * Returns the repository path.      * @return  absolute repository path      * @since   1.3      */
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|myPath
return|;
block|}
comment|/**      * Returns the peg revision      * @return  peg revision      * @since   1.3      */
specifier|public
name|long
name|getPegRevision
parameter_list|()
block|{
return|return
name|myPegRevision
return|;
block|}
comment|/**      * Returns the node kind.      * @return  node kind of the path      * @since   1.3      */
specifier|public
name|SVNNodeKind
name|getKind
parameter_list|()
block|{
return|return
name|myKind
return|;
block|}
comment|/**      * Returns a string representation of this object.      * @return  string representation      * @sinec   1.3      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"[SVNConflictVersion root = "
operator|+
name|getRepositoryRoot
argument_list|()
operator|+
literal|"; path = "
operator|+
name|getPath
argument_list|()
operator|+
literal|"@"
operator|+
name|getPegRevision
argument_list|()
operator|+
literal|" "
operator|+
name|getKind
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

