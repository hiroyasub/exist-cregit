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
operator|.
name|admin
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
name|SVNPropertyValue
import|;
end_import

begin_comment
comment|/**  * @version 1.3  * @author  TMate Software Ltd.  */
end_comment

begin_class
specifier|public
class|class
name|SVNProperties13
extends|extends
name|SVNVersionedProperties
block|{
specifier|public
name|SVNProperties13
parameter_list|(
name|SVNProperties
name|properties
parameter_list|)
block|{
name|super
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|containsProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
operator|!
name|isEmpty
argument_list|()
condition|)
block|{
name|SVNProperties
name|props
init|=
name|loadProperties
argument_list|()
decl_stmt|;
return|return
name|props
operator|.
name|containsName
argument_list|(
name|name
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|SVNPropertyValue
name|getPropertyValue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|getProperties
argument_list|()
operator|!=
literal|null
operator|&&
name|getProperties
argument_list|()
operator|.
name|containsName
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|getProperties
argument_list|()
operator|.
name|getSVNPropertyValue
argument_list|(
name|name
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|isEmpty
argument_list|()
condition|)
block|{
name|SVNProperties
name|props
init|=
name|loadProperties
argument_list|()
decl_stmt|;
return|return
name|props
operator|.
name|getSVNPropertyValue
argument_list|(
name|name
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|SVNProperties
name|loadProperties
parameter_list|()
throws|throws
name|SVNException
block|{
name|SVNProperties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
name|props
operator|=
operator|new
name|SVNProperties
argument_list|()
expr_stmt|;
name|setPropertiesMap
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
specifier|protected
name|SVNVersionedProperties
name|wrap
parameter_list|(
name|SVNProperties
name|properties
parameter_list|)
block|{
return|return
operator|new
name|SVNProperties13
argument_list|(
name|properties
argument_list|)
return|;
block|}
block|}
end_class

end_unit

