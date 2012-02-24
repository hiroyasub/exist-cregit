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
name|XMLDBException
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
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * an ant task to create a empty collection.  *  * @author  peter.klotz@blue-elephant-systems.com  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBCreateTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|String
name|collection
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
literal|"you have to specify an XMLDB collection URI"
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
literal|"Get base collection: "
operator|+
name|uri
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
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
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Collection "
operator|+
name|uri
operator|+
literal|" could not be found."
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
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Collection
name|root
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Creating collection "
operator|+
name|collection
operator|+
literal|" in base collection "
operator|+
name|uri
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|root
operator|=
name|mkcol
argument_list|(
name|base
argument_list|,
name|uri
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|root
operator|=
name|base
expr_stmt|;
block|}
if|if
condition|(
name|permissions
operator|!=
literal|null
condition|)
block|{
name|setPermissions
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Created collection "
operator|+
name|root
operator|.
name|getName
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
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
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"URISyntaxException: "
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
comment|/**      * DOCUMENT ME!      *      * @param  collection      */
specifier|public
name|void
name|setCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
specifier|private
name|Collection
name|mkcol
parameter_list|(
name|Collection
name|root
parameter_list|,
name|String
name|base
parameter_list|,
comment|/*String path,*/
name|String
name|relPath
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|URISyntaxException
block|{
name|CollectionManagementService
name|mgtService
decl_stmt|;
name|Collection
name|current
init|=
name|root
decl_stmt|;
name|Collection
name|c
decl_stmt|;
name|XmldbURI
name|baseUri
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|XmldbURI
name|collPath
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"BASEURI="
operator|+
name|baseUri
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"RELPATH="
operator|+
name|relPath
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
comment|//log("PATH=" + path, Project.MSG_DEBUG);
name|XmldbURI
index|[]
name|segments
init|=
name|collPath
operator|.
name|getPathSegments
argument_list|()
decl_stmt|;
for|for
control|(
name|XmldbURI
name|segment
range|:
name|segments
control|)
block|{
name|baseUri
operator|=
name|baseUri
operator|.
name|append
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Get collection "
operator|+
name|baseUri
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|c
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseUri
operator|.
name|toString
argument_list|()
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Create collection management service for collection "
operator|+
name|current
operator|.
name|getName
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|mgtService
operator|=
operator|(
name|CollectionManagementService
operator|)
name|current
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Create child collection "
operator|+
name|segment
argument_list|)
expr_stmt|;
name|current
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|segment
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Created collection "
operator|+
name|current
operator|.
name|getName
argument_list|()
operator|+
literal|'.'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
name|c
expr_stmt|;
block|}
block|}
return|return
operator|(
name|current
operator|)
return|;
block|}
block|}
end_class

end_unit

