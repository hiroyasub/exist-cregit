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
name|xmldb
operator|.
name|EXistCollectionManagementService
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
comment|/**  * an ant task to move a collection or resource to a new name.  *  * @author<a href="mailto:peter.klotz@blue-elephant-systems.com">Peter Klotz</a>  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBMoveTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|String
name|resource
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|collection
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|destination
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
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
literal|"You have to specify an XMLDB collection URI"
argument_list|)
operator|)
throw|;
block|}
if|if
condition|(
operator|(
name|resource
operator|==
literal|null
operator|)
operator|&&
operator|(
name|collection
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
literal|"Missing parameter: either resource or collection should be specified"
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
operator|==
literal|null
condition|)
block|{
specifier|final
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
name|log
argument_list|(
literal|"Create collection management service for collection "
operator|+
name|base
operator|.
name|getName
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
specifier|final
name|EXistCollectionManagementService
name|service
init|=
operator|(
name|EXistCollectionManagementService
operator|)
name|base
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Moving resource: "
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
specifier|final
name|String
name|msg
init|=
literal|"Resource "
operator|+
name|resource
operator|+
literal|" not found."
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
name|service
operator|.
name|moveResource
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|resource
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|destination
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
argument_list|(
literal|"Moving collection: "
operator|+
name|collection
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
name|service
operator|.
name|move
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|collection
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|destination
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|name
argument_list|)
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
literal|"XMLDB exception during move: "
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
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"URI syntax exception: "
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
comment|/**      * Set the collection.      *      * @param collection the collection      */
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
comment|/**      * Set the resource.      *      * @param resource the resource.      */
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
name|setDestination
parameter_list|(
name|String
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
end_class

end_unit

