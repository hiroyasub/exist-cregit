begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|security
operator|.
name|Permission
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
name|internal
operator|.
name|aider
operator|.
name|UnixStylePermissionAider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|SyntaxException
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
name|UserManagementService
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
comment|/**  * DOCUMENT ME!  *  * @author  wolf  * @author  andrzej@chaeron.com  */
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
name|ssl
init|=
literal|false
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
specifier|protected
name|String
name|permissions
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|String
name|UNIX_PERMS_REGEX
init|=
literal|"([r-][w-][x-]){3}"
decl_stmt|;
comment|/**      * DOCUMENT ME!      *      * @param  driver      */
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
comment|/**      * DOCUMENT ME!      *      * @param  password      */
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
comment|/**      * DOCUMENT ME!      *      * @param  user      */
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
comment|/**      * DOCUMENT ME!      *      * @param  uri      */
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
comment|/**      * DOCUMENT ME!      *      * @param  ssl      */
specifier|public
name|void
name|setSsl
parameter_list|(
name|boolean
name|ssl
parameter_list|)
block|{
name|this
operator|.
name|ssl
operator|=
name|ssl
expr_stmt|;
block|}
comment|/**      * DOCUMENT ME!      *      * @param  create      */
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
specifier|public
name|void
name|setPermissions
parameter_list|(
name|String
name|permissions
parameter_list|)
block|{
name|this
operator|.
name|permissions
operator|=
name|permissions
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
specifier|final
name|Database
index|[]
name|allDataBases
init|=
name|DatabaseManager
operator|.
name|getDatabases
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Database
name|database
range|:
name|allDataBases
control|)
block|{
if|if
condition|(
name|database
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
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
decl_stmt|;
specifier|final
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
name|database
operator|.
name|setProperty
argument_list|(
literal|"ssl-enable"
argument_list|,
name|ssl
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
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"failed to initialize XMLDB database driver"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|final
name|Collection
name|mkcol
parameter_list|(
name|Collection
name|rootCollection
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
name|rootCollection
decl_stmt|;
name|Collection
name|collection
decl_stmt|;
name|String
name|token
decl_stmt|;
comment|///TODO : use dedicated function in XmldbURI
specifier|final
name|StringTokenizer
name|tokenizer
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
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|token
operator|=
name|tokenizer
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
name|collection
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
name|collection
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
block|{
name|current
operator|=
name|collection
expr_stmt|;
block|}
block|}
return|return
operator|(
name|current
operator|)
return|;
block|}
specifier|protected
specifier|final
name|void
name|setPermissions
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|BuildException
block|{
name|Collection
name|base
init|=
literal|null
decl_stmt|;
name|UserManagementService
name|service
init|=
literal|null
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
literal|"you have to specify an XMLDB collection URI"
argument_list|)
operator|)
throw|;
block|}
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
name|base
operator|=
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
expr_stmt|;
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
else|else
block|{
name|service
operator|=
operator|(
name|UserManagementService
operator|)
name|base
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|setPermissions
argument_list|(
name|res
argument_list|,
name|service
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
specifier|protected
specifier|final
name|void
name|setPermissions
parameter_list|(
name|Collection
name|col
parameter_list|)
throws|throws
name|BuildException
block|{
try|try
block|{
if|if
condition|(
name|permissions
operator|!=
literal|null
condition|)
block|{
name|setPermissions
argument_list|(
literal|null
argument_list|,
operator|(
name|UserManagementService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
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
specifier|protected
specifier|final
name|void
name|setPermissions
parameter_list|(
name|Resource
name|res
parameter_list|,
name|UserManagementService
name|service
parameter_list|)
throws|throws
name|BuildException
block|{
try|try
block|{
if|if
condition|(
name|permissions
operator|!=
literal|null
condition|)
block|{
comment|// if the permissions string matches the Unix Perms Regex, we use a unix style
comment|// permission string approach, otherwise we assume permissions are specified
comment|// in eXist's own syntax (user=+write,...).
if|if
condition|(
name|permissions
operator|.
name|matches
argument_list|(
name|UNIX_PERMS_REGEX
argument_list|)
condition|)
block|{
comment|// Unix-style permissions string provided
specifier|final
name|Permission
name|perm
init|=
name|UnixStylePermissionAider
operator|.
name|fromString
argument_list|(
name|permissions
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|chmod
argument_list|(
name|res
argument_list|,
name|perm
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|service
operator|.
name|chmod
argument_list|(
name|perm
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// eXist-style syntax for permission string (eg. user=+write,...)
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|chmod
argument_list|(
name|res
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|service
operator|.
name|chmod
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
specifier|final
name|SyntaxException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Syntax error in permissions: "
operator|+
name|permissions
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
block|}
end_class

end_unit

