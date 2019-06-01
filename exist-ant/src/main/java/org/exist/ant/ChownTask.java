begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Account
import|;
end_import

begin_comment
comment|/**  * an ant task to change permissions on a resource.  *  * @author  peter.klotz@blue-elephant-systems.com  */
end_comment

begin_class
specifier|public
class|class
name|ChownTask
extends|extends
name|UserTask
block|{
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|group
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|resource
init|=
literal|null
decl_stmt|;
comment|/* (non-Javadoc)      * @see org.apache.tools.ant.Task#execute()      */
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
name|super
operator|.
name|execute
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|name
operator|==
literal|null
operator|)
operator|||
operator|(
name|group
operator|==
literal|null
operator|)
condition|)
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
literal|"Must specify user and group"
argument_list|)
operator|)
throw|;
block|}
try|try
block|{
specifier|final
name|Account
name|usr
init|=
name|service
operator|.
name|getAccount
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Resource
name|res
init|=
name|base
operator|.
name|getResource
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|service
operator|.
name|chown
argument_list|(
name|res
argument_list|,
name|usr
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|service
operator|.
name|chown
argument_list|(
name|usr
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"XMLDB exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|failonerror
condition|)
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
operator|)
throw|;
block|}
else|else
block|{
name|log
argument_list|(
name|msg
argument_list|,
name|e
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|user
expr_stmt|;
block|}
specifier|public
name|void
name|setResource
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
specifier|public
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
block|}
block|}
end_class

end_unit
