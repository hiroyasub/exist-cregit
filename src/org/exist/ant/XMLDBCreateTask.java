begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * an ant task to create a empty collection  *  * @author peter.klotz@blue-elephant-systems.com  */
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
comment|/* (non-Javadoc)    * @see org.apache.tools.ant.Task#execute()    */
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
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"you have to specify an XMLDB collection URI"
argument_list|)
throw|;
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
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"collection "
operator|+
name|uri
operator|+
literal|" not found"
argument_list|)
throw|;
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
literal|null
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
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"XMLDB exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * @param collection    */
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
specifier|final
name|Collection
name|mkcol
parameter_list|(
name|Collection
name|root
parameter_list|,
name|String
name|baseURI
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|relPath
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|CollectionManagementService
name|mgtService
decl_stmt|;
name|Collection
name|current
init|=
name|root
decl_stmt|,
name|c
decl_stmt|;
name|String
name|token
decl_stmt|;
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|relPath
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"BASEURI="
operator|+
name|baseURI
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
name|log
argument_list|(
literal|"PATH="
operator|+
name|path
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
comment|//TODO : use dedicated function in XmldbURI
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|token
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|path
operator|+
literal|"/"
operator|+
name|token
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
literal|"/"
operator|+
name|token
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Get collection "
operator|+
name|baseURI
operator|+
name|path
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
name|baseURI
operator|+
name|path
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
name|token
argument_list|)
expr_stmt|;
name|current
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|token
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
name|current
operator|=
name|c
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
block|}
end_class

end_unit

