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
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|taskdefs
operator|.
name|condition
operator|.
name|Condition
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
name|DatabaseManager
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
name|Collection
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

begin_comment
comment|/**  * an ant task to check for the existence of a collection or resource to be used as a ant condition.  *  * @author  peter.klotz@blue-elephant-systems.com  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBExistTask
extends|extends
name|AbstractXMLDBTask
implements|implements
name|Condition
block|{
specifier|private
name|String
name|resource
init|=
literal|null
decl_stmt|;
comment|/**      * DOCUMENT ME!      *      * @return  returns true if the eval is successful.      *      * @throws  BuildException      *      * @see     org.apache.tools.ant.taskdefs.condition.Condition#eval()      */
specifier|public
name|boolean
name|eval
parameter_list|()
throws|throws
name|BuildException
block|{
name|boolean
name|exist
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
literal|"You have to specify an XMLDB collection URI"
argument_list|)
operator|)
throw|;
block|}
name|registerDatabase
argument_list|()
expr_stmt|;
try|try
block|{
name|log
argument_list|(
literal|"Checking collection: "
operator|+
name|uri
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|base
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Base collection found"
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|exist
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|base
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|resource
operator|!=
literal|null
operator|)
condition|)
block|{
name|log
argument_list|(
literal|"Checking resource: "
operator|+
name|resource
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Resource not found"
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|exist
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
comment|// ignore is false already
name|log
argument_list|(
literal|"Resource or collection cannot be retrieved"
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|exist
operator|=
literal|false
expr_stmt|;
block|}
return|return
operator|(
name|exist
operator|)
return|;
block|}
comment|/**      * DOCUMENT ME!      *      * @param  resource      */
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
block|}
end_class

end_unit

