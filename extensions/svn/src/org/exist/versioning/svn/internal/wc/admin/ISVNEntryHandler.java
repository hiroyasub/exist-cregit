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
name|internal
operator|.
name|wc
operator|.
name|admin
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
name|SVNErrorMessage
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
comment|/**  * @version 1.3  * @author  TMate Software Ltd.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ISVNEntryHandler
block|{
specifier|public
name|void
name|handleEntry
parameter_list|(
name|File
name|path
parameter_list|,
name|SVNEntry
name|entry
parameter_list|)
throws|throws
name|SVNException
function_decl|;
specifier|public
name|void
name|handleError
parameter_list|(
name|File
name|path
parameter_list|,
name|SVNErrorMessage
name|error
parameter_list|)
throws|throws
name|SVNException
function_decl|;
block|}
end_interface

end_unit

