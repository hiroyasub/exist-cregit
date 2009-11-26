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
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Task
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
name|Database
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
comment|/**  * @author wolf  * @author andrzej@chaeron.com  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractXMLDBTask
extends|extends
name|Task
block|{
specifier|protected
name|String
name|driver
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|protected
name|String
name|user
init|=
literal|"guest"
decl_stmt|;
specifier|protected
name|String
name|password
init|=
literal|"guest"
decl_stmt|;
specifier|protected
name|String
name|uri
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|createDatabase
init|=
literal|false
decl_stmt|;
specifier|protected
name|String
name|configuration
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|failonerror
init|=
literal|true
decl_stmt|;
comment|/**    * @param driver    */
specifier|public
name|void
name|setDriver
parameter_list|(
name|String
name|driver
parameter_list|)
block|{
name|this
operator|.
name|driver
operator|=
name|driver
expr_stmt|;
block|}
comment|/**    * @param password    */
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/**    * @param user    */
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
comment|/**    * @param uri    */
specifier|public
name|void
name|setUri
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
comment|/**    * @param create    */
specifier|public
name|void
name|setInitdb
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
name|this
operator|.
name|createDatabase
operator|=
name|create
expr_stmt|;
block|}
specifier|public
name|void
name|setConfiguration
parameter_list|(
name|String
name|config
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|void
name|setFailonerror
parameter_list|(
name|boolean
name|failonerror
parameter_list|)
block|{
name|this
operator|.
name|failonerror
operator|=
name|failonerror
expr_stmt|;
block|}
specifier|protected
name|void
name|registerDatabase
parameter_list|()
throws|throws
name|BuildException
block|{
try|try
block|{
name|log
argument_list|(
literal|"Registering database"
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|Database
name|dbs
index|[]
init|=
name|DatabaseManager
operator|.
name|getDatabases
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dbs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|dbs
index|[
name|i
index|]
operator|.
name|acceptsURI
argument_list|(
name|uri
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
name|createDatabase
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
expr_stmt|;
if|if
condition|(
name|configuration
operator|!=
literal|null
condition|)
block|{
name|database
operator|.
name|setProperty
argument_list|(
literal|"configuration"
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Database driver registered."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
literal|"failed to initialize XMLDB database driver"
argument_list|)
operator|)
throw|;
block|}
block|}
specifier|protected
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
comment|///TODO : use dedicated function in XmldbURI
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
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
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
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
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

