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
name|SVNProperties
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
comment|/**  * The<b>ISVNPropertyValueProvider</b> interface should be implemented  * to be further provided to {@link SVNWCClient#doSetProperty(java.io.File, ISVNPropertyValueProvider, boolean, org.tmatesoft.svn.core.SVNDepth, ISVNPropertyHandler, java.util.Collection)}  * method for defining properties to change.  *  * @author TMate Software Ltd.  * @version 1.3  * @since   1.2  * @see SVNWCClient  */
end_comment

begin_interface
specifier|public
interface|interface
name|ISVNPropertyValueProvider
block|{
comment|/**      * Defines local item's properties to be installed.      *      * @param path          an WC item's path      * @param properties    an item's versioned properties      * @return<b>SVNProperties</b> object which stores properties to be installed on an item      * @throws SVNException      */
specifier|public
name|SVNProperties
name|providePropertyValues
parameter_list|(
name|File
name|path
parameter_list|,
name|SVNProperties
name|properties
parameter_list|)
throws|throws
name|SVNException
function_decl|;
block|}
end_interface

end_unit

