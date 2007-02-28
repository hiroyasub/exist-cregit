begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|BindException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|StandaloneServer
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
name|PermissionFactory
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
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|MultiException
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

begin_comment
comment|/** A test case for accessing user management service remotely ?   * @author Sebastian Bossung, Technische Universitaet Hamburg-Harburg  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|RemoteDatabaseImplTest
extends|extends
name|RemoteDBTest
block|{
specifier|private
specifier|static
name|StandaloneServer
name|server
init|=
literal|null
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|ADMIN_PASSWORD
init|=
literal|"somepwd"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|ADMIN_COLLECTION_NAME
init|=
literal|"admin-collection"
decl_stmt|;
specifier|public
name|RemoteDatabaseImplTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
comment|//Don't worry about closing the server : the shutdown hook will do the job
name|initServer
argument_list|()
expr_stmt|;
name|setUpRemoteDatabase
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|initServer
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
operator|new
name|StandaloneServer
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|server
operator|.
name|isStarted
argument_list|()
condition|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting standalone server..."
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
block|{}
decl_stmt|;
name|server
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|server
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MultiException
name|e
parameter_list|)
block|{
name|boolean
name|rethrow
init|=
literal|true
decl_stmt|;
name|Iterator
name|i
init|=
name|e
operator|.
name|getExceptions
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Exception
name|e0
init|=
operator|(
name|Exception
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|e0
operator|instanceof
name|BindException
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"A server is running already !"
argument_list|)
expr_stmt|;
name|rethrow
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|rethrow
condition|)
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testGetCollection
parameter_list|()
block|{
try|try
block|{
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DB_DRIVER
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
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Collection
name|rootCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|adminCollection
init|=
name|cms
operator|.
name|createCollection
argument_list|(
name|ADMIN_COLLECTION_NAME
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ums
operator|!=
literal|null
condition|)
block|{
name|Permission
name|p
init|=
name|PermissionFactory
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|p
operator|.
name|setPermissions
argument_list|(
name|Permission
operator|.
name|USER_STRING
operator|+
literal|"=+read,+write,"
operator|+
name|Permission
operator|.
name|GROUP_STRING
operator|+
literal|"=-read,-write,"
operator|+
name|Permission
operator|.
name|OTHER_STRING
operator|+
literal|"=-read,-write"
argument_list|)
expr_stmt|;
name|ums
operator|.
name|setPermissions
argument_list|(
name|adminCollection
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Collection
name|guestCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|ADMIN_COLLECTION_NAME
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|guestCollection
operator|.
name|createResource
argument_list|(
literal|"testguest"
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
literal|"123"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|guestCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
block|}
name|cms
operator|.
name|removeCollection
argument_list|(
name|ADMIN_COLLECTION_NAME
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|TestRunner
operator|.
name|run
argument_list|(
name|RemoteDatabaseImplTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//Explicit shutdown for the shutdown hook
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

