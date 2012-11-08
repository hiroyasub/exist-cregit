begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|installer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|repo
operator|.
name|ExistRepository
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
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
name|DatabaseInstanceManager
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
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|FileSystemStorage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Package
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|PackageException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|UserInteractionStrategy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|tui
operator|.
name|BatchUserInteraction
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
name|XQueryService
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Initial database setup: called from the installer to set the admin password.  */
end_comment

begin_class
specifier|public
class|class
name|Setup
block|{
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist:///db"
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|//TODO: I think this will never happen with the current setup. Class needs a little more cleanup.
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"No password specified. Admin password will be empty."
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|String
name|passwd
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
literal|"pass:"
argument_list|)
condition|)
block|{
name|passwd
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|1
expr_stmt|;
block|}
name|XQueryService
name|query
init|=
name|initDb
argument_list|(
name|passwd
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|installApps
argument_list|(
name|query
argument_list|,
name|args
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"An error occurred while installing apps: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|shutdown
argument_list|(
name|passwd
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|installApps
parameter_list|(
name|XQueryService
name|query
parameter_list|,
name|String
index|[]
name|args
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|EXistException
block|{
name|File
name|home
init|=
name|getExistHome
argument_list|()
decl_stmt|;
name|ExistRepository
name|repository
init|=
name|getRepository
argument_list|(
name|home
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|uris
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|File
name|xar
init|=
name|findApp
argument_list|(
name|home
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|xar
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Installing app package "
operator|+
name|xar
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|UserInteractionStrategy
name|interact
init|=
operator|new
name|BatchUserInteraction
argument_list|()
decl_stmt|;
name|Package
name|pkg
init|=
name|repository
operator|.
name|getParentRepo
argument_list|()
operator|.
name|installPackage
argument_list|(
name|xar
argument_list|,
literal|true
argument_list|,
name|interact
argument_list|)
decl_stmt|;
name|String
name|pkgName
init|=
name|pkg
operator|.
name|getName
argument_list|()
decl_stmt|;
name|uris
operator|.
name|add
argument_list|(
name|pkgName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"App package not found: "
operator|+
name|name
operator|+
literal|". Skipping it."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PackageException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to install application package "
operator|+
name|name
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n=== Starting the installation process for each application... ==="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nPLEASE DO NOT ABORT\n"
argument_list|)
expr_stmt|;
name|String
name|prolog
init|=
literal|"import module namespace repo=\"http://exist-db.org/xquery/repo\" "
operator|+
literal|"at \"java:org.exist.xquery.modules.expathrepo.ExpathPackageModule\";\n"
decl_stmt|;
for|for
control|(
name|String
name|uri
range|:
name|uris
control|)
block|{
name|StringBuilder
name|xquery
init|=
operator|new
name|StringBuilder
argument_list|(
name|prolog
argument_list|)
decl_stmt|;
name|xquery
operator|.
name|append
argument_list|(
literal|" repo:deploy(\""
operator|+
name|uri
operator|+
literal|"\")"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Installing app package: "
operator|+
name|uri
operator|+
literal|"... "
argument_list|)
expr_stmt|;
try|try
block|{
name|query
operator|.
name|query
argument_list|(
name|xquery
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"An error occurred while deploying application: "
operator|+
name|uri
operator|+
literal|". You can install it later using the package repository."
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DONE."
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=== App installation completed. ==="
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|File
name|getExistHome
parameter_list|()
throws|throws
name|EXistException
block|{
return|return
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|File
name|findApp
parameter_list|(
name|File
name|home
parameter_list|,
name|String
name|app
parameter_list|)
block|{
name|File
name|apps
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"apps"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Apps directory: "
operator|+
name|apps
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|apps
operator|.
name|canRead
argument_list|()
operator|&&
name|apps
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|apps
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|app
argument_list|)
condition|)
return|return
name|file
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|ExistRepository
name|getRepository
parameter_list|(
name|File
name|home
parameter_list|)
throws|throws
name|EXistException
block|{
try|try
block|{
if|if
condition|(
name|home
operator|!=
literal|null
condition|)
block|{
name|File
name|repo_dir
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"webapp/WEB-INF/expathrepo"
argument_list|)
decl_stmt|;
comment|// ensure the dir exists
name|repo_dir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|FileSystemStorage
name|storage
init|=
operator|new
name|FileSystemStorage
argument_list|(
name|repo_dir
argument_list|)
decl_stmt|;
return|return
operator|new
name|ExistRepository
argument_list|(
name|storage
argument_list|)
return|;
block|}
else|else
block|{
name|File
name|repo_dir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
literal|"/expathrepo"
argument_list|)
decl_stmt|;
comment|// ensure the dir exists
name|repo_dir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|FileSystemStorage
name|storage
init|=
operator|new
name|FileSystemStorage
argument_list|(
name|repo_dir
argument_list|)
decl_stmt|;
return|return
operator|new
name|ExistRepository
argument_list|(
name|storage
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|PackageException
name|ex
parameter_list|)
block|{
comment|// problem with pkg-repo.jar throwing exception
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Problem setting expath repository"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|XQueryService
name|initDb
parameter_list|(
name|String
name|adminPass
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--- Starting embedded database instance ---"
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
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
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminPass
operator|!=
literal|null
condition|)
block|{
name|UserManagementService
name|service
init|=
operator|(
name|UserManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Account
name|admin
init|=
name|service
operator|.
name|getAccount
argument_list|(
literal|"admin"
argument_list|)
decl_stmt|;
name|admin
operator|.
name|setPassword
argument_list|(
name|adminPass
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Setting admin user password..."
argument_list|)
expr_stmt|;
name|service
operator|.
name|updateAccount
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|,
literal|"admin"
argument_list|,
name|adminPass
argument_list|)
expr_stmt|;
block|}
name|XQueryService
name|query
init|=
operator|(
name|XQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
return|return
name|query
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Caught an exception while initializing db: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|void
name|shutdown
parameter_list|(
name|String
name|adminPass
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--- Initialization complete. Shutdown embedded database instance ---"
argument_list|)
expr_stmt|;
try|try
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|,
literal|"admin"
argument_list|,
name|adminPass
argument_list|)
decl_stmt|;
name|DatabaseInstanceManager
name|manager
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Caught an exception while initializing db: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

