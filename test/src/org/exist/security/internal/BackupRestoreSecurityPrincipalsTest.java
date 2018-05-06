begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
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
name|TestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|Backup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|Restore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|RestoreListener
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
name|*
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
name|SecurityManager
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
name|GroupAider
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
name|UserAider
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
name|test
operator|.
name|ExistXmldbEmbeddedServer
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
name|junit
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|ResourceSet
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
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
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
name|XPathQueryService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observable
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|BackupRestoreSecurityPrincipalsTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|BACKUP_FILE_PREFIX
init|=
literal|"exist.BackupRestoreSecurityPrincipalsTest"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|BACKUP_FILE_SUFFIX
init|=
literal|".backup.zip"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|FRANK_USER
init|=
literal|"frank"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|JOE_USER
init|=
literal|"joe"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|JACK_USER
init|=
literal|"jack"
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
name|ExistXmldbEmbeddedServer
name|server
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**      * 1. With an empty database we create three      *    users: 'frank', 'joe', and 'jack'.      *      * 2. We create a backup of the database which contains      *    the three users from (1).      *      * 3. We destroy the database, restart the server,      *    and start again with a clean database.      *      * 4. With an (again) empty database we create two      *    users: 'frank', and 'jack'.      *      * 5. We then try and restore the database backup from (2), which      *    contains the original 'frank', 'joe', and 'jack' users.      *      * frank will have the same username and user id in the current      * database and the backup we are trying to restore.      *      * joe does not exist in the current database, but his user id      * in the backup will collide with that of jack in the current database.      *      * jack will have a different user id in the backup when compared to the current      * database, however he will have the same username.      *      * We want to make sure that after the restore, all three users are present      * that they have distinct and expected user ids and that any resources      * that were owned by them are still correctly owner by them (and not some other user).      */
annotation|@
name|Test
specifier|public
name|void
name|restoreConflictingUsername
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|SAXException
throws|,
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|URISyntaxException
throws|,
name|XMLDBException
throws|,
name|IllegalAccessException
throws|,
name|ClassNotFoundException
throws|,
name|InstantiationException
block|{
comment|// creates a database with new users: 'frank(id=11)', 'joe(id=12)', and 'jack(id=13)'
name|createInitialUsers
argument_list|(
name|FRANK_USER
argument_list|,
name|JOE_USER
argument_list|,
name|JACK_USER
argument_list|)
expr_stmt|;
comment|// create a backup of the database (which has the initial users)
specifier|final
name|Path
name|backupFile
init|=
name|backupDatabase
argument_list|()
decl_stmt|;
comment|//reset database to empty
name|server
operator|.
name|restart
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//create new users: 'frank(id=11)' and 'jack(id=12)'
name|createInitialUsers
argument_list|(
name|FRANK_USER
argument_list|,
name|JACK_USER
argument_list|)
expr_stmt|;
specifier|final
name|String
name|accountQuery
init|=
literal|"declare namespace c = 'http://exist-db.org/Configuration';\n"
operator|+
literal|"for $account in //c:account\n"
operator|+
literal|"return\n"
operator|+
literal|"<user id='{$account/@id}' name='{$account/c:name}'/>"
decl_stmt|;
specifier|final
name|XPathQueryService
name|xqs
init|=
operator|(
name|XPathQueryService
operator|)
name|server
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|SecurityManagerImpl
name|sm
init|=
operator|(
name|SecurityManagerImpl
operator|)
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
comment|//check the current user accounts
name|ResourceSet
name|result
init|=
name|xqs
operator|.
name|query
argument_list|(
name|accountQuery
argument_list|)
decl_stmt|;
name|assertUser
argument_list|(
name|RealmImpl
operator|.
name|ADMIN_ACCOUNT_ID
argument_list|,
name|SecurityManager
operator|.
name|DBA_USER
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
name|assertUser
argument_list|(
name|RealmImpl
operator|.
name|GUEST_ACCOUNT_ID
argument_list|,
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
name|assertUser
argument_list|(
name|SecurityManagerImpl
operator|.
name|INITIAL_LAST_ACCOUNT_ID
operator|+
literal|1
argument_list|,
literal|"frank"
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
name|assertUser
argument_list|(
name|SecurityManagerImpl
operator|.
name|INITIAL_LAST_ACCOUNT_ID
operator|+
literal|2
argument_list|,
literal|"jack"
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|3
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
comment|//check the last user id
name|assertEquals
argument_list|(
name|SecurityManagerImpl
operator|.
name|INITIAL_LAST_ACCOUNT_ID
operator|+
literal|2
argument_list|,
name|sm
operator|.
name|lastAccountId
argument_list|)
expr_stmt|;
comment|//last account id should be that of 'jack'
comment|//create a test collection and give everyone access
specifier|final
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|server
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|test
init|=
name|cms
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
specifier|final
name|UserManagementService
name|testUms
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|testUms
operator|.
name|chmod
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
expr_stmt|;
comment|//create and store a new document as 'frank'
specifier|final
name|Collection
name|frankTest
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/test"
argument_list|,
name|FRANK_USER
argument_list|,
name|FRANK_USER
argument_list|)
decl_stmt|;
specifier|final
name|String
name|FRANKS_DOCUMENT
init|=
literal|"franks-document.xml"
decl_stmt|;
specifier|final
name|Resource
name|frankDoc
init|=
name|frankTest
operator|.
name|createResource
argument_list|(
name|FRANKS_DOCUMENT
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|frankDoc
operator|.
name|setContent
argument_list|(
literal|"<hello>frank</hello>"
argument_list|)
expr_stmt|;
name|frankTest
operator|.
name|storeResource
argument_list|(
name|frankDoc
argument_list|)
expr_stmt|;
comment|//create and store a new document as 'jack'
specifier|final
name|Collection
name|jackTest
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/test"
argument_list|,
name|JACK_USER
argument_list|,
name|JACK_USER
argument_list|)
decl_stmt|;
specifier|final
name|String
name|JACKS_DOCUMENT
init|=
literal|"jacks-document.xml"
decl_stmt|;
specifier|final
name|Resource
name|jackDoc
init|=
name|jackTest
operator|.
name|createResource
argument_list|(
name|JACKS_DOCUMENT
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|jackDoc
operator|.
name|setContent
argument_list|(
literal|"<hello>jack</hello>"
argument_list|)
expr_stmt|;
name|jackTest
operator|.
name|storeResource
argument_list|(
name|jackDoc
argument_list|)
expr_stmt|;
comment|//restore the database backup
specifier|final
name|Restore
name|restore
init|=
operator|new
name|Restore
argument_list|()
decl_stmt|;
name|restore
operator|.
name|restore
argument_list|(
operator|new
name|NullRestoreListener
argument_list|()
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
name|backupFile
argument_list|,
literal|"xmldb:exist:///db"
argument_list|)
expr_stmt|;
comment|//check the current user accounts after the restore
name|result
operator|=
name|xqs
operator|.
name|query
argument_list|(
name|accountQuery
argument_list|)
expr_stmt|;
name|assertUser
argument_list|(
name|RealmImpl
operator|.
name|ADMIN_ACCOUNT_ID
argument_list|,
name|SecurityManager
operator|.
name|DBA_USER
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
name|assertUser
argument_list|(
name|RealmImpl
operator|.
name|GUEST_ACCOUNT_ID
argument_list|,
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
name|assertUser
argument_list|(
name|SecurityManagerImpl
operator|.
name|INITIAL_LAST_ACCOUNT_ID
operator|+
literal|1
argument_list|,
name|FRANK_USER
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
name|assertUser
argument_list|(
name|SecurityManagerImpl
operator|.
name|INITIAL_LAST_ACCOUNT_ID
operator|+
literal|2
argument_list|,
name|JACK_USER
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|3
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
name|assertUser
argument_list|(
name|SecurityManagerImpl
operator|.
name|INITIAL_LAST_ACCOUNT_ID
operator|+
literal|3
argument_list|,
name|JOE_USER
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|4
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
expr_stmt|;
comment|//check the last user id after the restore
name|assertEquals
argument_list|(
name|SecurityManagerImpl
operator|.
name|INITIAL_LAST_ACCOUNT_ID
operator|+
literal|3
argument_list|,
name|sm
operator|.
name|lastAccountId
argument_list|)
expr_stmt|;
comment|//last account id should be that of 'joe'
comment|//check the owner of frank's document after restore
specifier|final
name|Resource
name|fDoc
init|=
name|test
operator|.
name|getResource
argument_list|(
name|FRANKS_DOCUMENT
argument_list|)
decl_stmt|;
specifier|final
name|Permission
name|franksDocPermissions
init|=
name|testUms
operator|.
name|getPermissions
argument_list|(
name|fDoc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|FRANK_USER
argument_list|,
name|franksDocPermissions
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|//check the owner of jack's document after restore
specifier|final
name|Resource
name|jDoc
init|=
name|test
operator|.
name|getResource
argument_list|(
name|JACKS_DOCUMENT
argument_list|)
decl_stmt|;
specifier|final
name|Permission
name|jacksDocPermissions
init|=
name|testUms
operator|.
name|getPermissions
argument_list|(
name|jDoc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|JACK_USER
argument_list|,
name|jacksDocPermissions
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates initial database users.      *      * NOTE: The database must be in a clean initialised empty state.      */
specifier|private
name|void
name|createInitialUsers
parameter_list|(
specifier|final
name|String
modifier|...
name|usernames
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|XMLDBException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
throws|,
name|ClassNotFoundException
block|{
name|int
name|lastAccountId
init|=
name|SecurityManagerImpl
operator|.
name|INITIAL_LAST_ACCOUNT_ID
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|username
range|:
name|usernames
control|)
block|{
name|createUser
argument_list|(
name|username
argument_list|,
name|username
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|++
name|lastAccountId
argument_list|,
name|getUser
argument_list|(
name|username
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Backup the database.      *      * @return The path to the database backup.      */
specifier|private
name|Path
name|backupDatabase
parameter_list|()
throws|throws
name|IOException
throws|,
name|XMLDBException
throws|,
name|SAXException
block|{
specifier|final
name|Path
name|backupFile
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|BACKUP_FILE_PREFIX
argument_list|,
name|BACKUP_FILE_SUFFIX
argument_list|)
decl_stmt|;
name|backupFile
operator|.
name|toFile
argument_list|()
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
specifier|final
name|Backup
name|backup
init|=
operator|new
name|Backup
argument_list|(
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|,
name|backupFile
argument_list|)
decl_stmt|;
name|backup
operator|.
name|backup
argument_list|(
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|backupFile
return|;
block|}
specifier|private
name|void
name|assertUser
parameter_list|(
specifier|final
name|int
name|userId
parameter_list|,
specifier|final
name|String
name|userName
parameter_list|,
specifier|final
name|Node
name|account
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|userId
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|account
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|userName
argument_list|,
name|account
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createUser
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|server
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Account
name|user
init|=
operator|new
name|UserAider
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|user
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
comment|//create the personal group
specifier|final
name|Group
name|group
init|=
operator|new
name|GroupAider
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|group
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
literal|"Personal group for "
operator|+
name|username
argument_list|)
expr_stmt|;
name|group
operator|.
name|addManager
argument_list|(
name|ums
operator|.
name|getAccount
argument_list|(
literal|"admin"
argument_list|)
argument_list|)
expr_stmt|;
name|ums
operator|.
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
comment|//add the personal group as the primary group
name|user
operator|.
name|addGroup
argument_list|(
name|username
argument_list|)
expr_stmt|;
comment|//create the account
name|ums
operator|.
name|addAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|//add the new account as a manager of their personal group
name|ums
operator|.
name|addGroupManager
argument_list|(
name|username
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Account
name|getUser
parameter_list|(
specifier|final
name|String
name|username
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|server
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
return|return
name|ums
operator|.
name|getAccount
argument_list|(
name|username
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|NullRestoreListener
implements|implements
name|RestoreListener
block|{
annotation|@
name|Override
specifier|public
name|void
name|createCollection
parameter_list|(
specifier|final
name|String
name|collection
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|restored
parameter_list|(
specifier|final
name|String
name|resource
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|String
name|warningsAndErrorsAsString
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProblems
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentCollection
parameter_list|(
specifier|final
name|String
name|currentCollectionName
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentResource
parameter_list|(
specifier|final
name|String
name|currentResourceName
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|restoreStarting
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|restoreFinished
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|observe
parameter_list|(
specifier|final
name|Observable
name|observable
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentBackup
parameter_list|(
specifier|final
name|String
name|currentBackup
parameter_list|)
block|{
block|}
block|}
block|}
end_class

end_unit

