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
name|io
operator|.
name|ISVNWorkspaceMediator
import|;
end_import

begin_comment
comment|/**  * @version 1.3  * @author  TMate Software Ltd.  */
end_comment

begin_class
specifier|public
class|class
name|SVNImportMediator
implements|implements
name|ISVNWorkspaceMediator
block|{
specifier|public
name|SVNImportMediator
parameter_list|()
block|{
block|}
specifier|public
name|SVNPropertyValue
name|getWorkspaceProperty
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SVNException
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setWorkspaceProperty
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|SVNPropertyValue
name|value
parameter_list|)
throws|throws
name|SVNException
block|{
block|}
block|}
end_class

end_unit

