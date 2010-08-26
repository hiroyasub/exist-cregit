begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

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
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|CollectionConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Configurator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|ConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsElement
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
name|AuthenticationException
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
name|Group
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
name|PermissionDeniedException
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
name|Subject
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
name|realm
operator|.
name|Realm
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
name|xacml
operator|.
name|ExistPDP
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
name|storage
operator|.
name|DBBroker
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
name|txn
operator|.
name|TransactionManager
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
name|txn
operator|.
name|Txn
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
name|hashtable
operator|.
name|Int2ObjectHashMap
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

begin_comment
comment|/**  * SecurityManager is responsible for managing users and groups.  *   * There's only one SecurityManager for each database instance, which  * may be obtained by {@link BrokerPool#getSecurityManager()}.  *   * Users and groups are stored in the system collection, in document  * users.xml. While it is possible to edit this file by hand, it  * may lead to unexpected results, since SecurityManager reads   * users.xml only during database startup and shutdown.  */
end_comment

begin_comment
comment|//<!-- Central user configuration. Editing this document will cause the security to reload and update its internal database. Please handle with care! -->
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"security-manager"
argument_list|)
specifier|public
class|class
name|SecurityManagerImpl
implements|implements
name|SecurityManager
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"default-permissions"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_ATTRIBUTE
init|=
literal|"collection"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_ATTRIBUTE
init|=
literal|"resource"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_PERMISSIONS_COLLECTIONS
init|=
literal|"indexer.permissions.collection"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_PERMISSIONS_RESOURCES
init|=
literal|"indexer.permissions.resource"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|protected
name|Int2ObjectHashMap
argument_list|<
name|Group
argument_list|>
name|groupsById
init|=
operator|new
name|Int2ObjectHashMap
argument_list|<
name|Group
argument_list|>
argument_list|(
literal|65
argument_list|)
decl_stmt|;
specifier|protected
name|Int2ObjectHashMap
argument_list|<
name|Account
argument_list|>
name|usersById
init|=
operator|new
name|Int2ObjectHashMap
argument_list|<
name|Account
argument_list|>
argument_list|(
literal|65
argument_list|)
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"last-account-id"
argument_list|)
specifier|protected
name|int
name|lastUserId
init|=
literal|0
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"last-group-id"
argument_list|)
specifier|protected
name|int
name|lastGroupId
init|=
literal|0
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"version"
argument_list|)
specifier|private
name|String
name|version
init|=
literal|"2.0"
decl_stmt|;
comment|//	@ConfigurationField("enableXACML")
specifier|private
name|Boolean
name|enableXACML
init|=
literal|false
decl_stmt|;
specifier|private
name|ExistPDP
name|pdp
decl_stmt|;
specifier|private
name|RealmImpl
name|defaultRealm
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"realm"
argument_list|)
specifier|private
name|List
argument_list|<
name|Realm
argument_list|>
name|realms
init|=
operator|new
name|ArrayList
argument_list|<
name|Realm
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
specifier|private
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
specifier|public
name|SecurityManagerImpl
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|defaultRealm
operator|=
operator|new
name|RealmImpl
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//TODO: in-memory configuration???
name|realms
operator|.
name|add
argument_list|(
name|defaultRealm
argument_list|)
expr_stmt|;
name|PermissionFactory
operator|.
name|sm
operator|=
name|this
expr_stmt|;
block|}
comment|/** 	 * Initialize the security manager. 	 *  	 * Checks if the file users.xml exists in the system collection of the database. 	 * If not, it is created with two default users: admin and guest. 	 *   	 * @param pool 	 * @param sysBroker 	 */
specifier|public
name|void
name|attach
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
comment|//    	groups = new Int2ObjectHashMap<Group>(65);
comment|//    	users = new Int2ObjectHashMap<User>(65);
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|TransactionManager
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
name|Collection
name|systemCollection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|systemCollection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
argument_list|)
expr_stmt|;
if|if
condition|(
name|systemCollection
operator|==
literal|null
condition|)
block|{
name|txn
operator|=
name|transaction
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|systemCollection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
argument_list|)
expr_stmt|;
if|if
condition|(
name|systemCollection
operator|==
literal|null
condition|)
return|return;
name|systemCollection
operator|.
name|setPermissions
argument_list|(
literal|0770
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|systemCollection
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|(
name|txn
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
name|transaction
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading acl failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|SECURITY_COLLETION_URI
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|txn
operator|=
name|transaction
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|SECURITY_COLLETION_URI
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
return|return;
comment|//throw error???
name|collection
operator|.
name|setPermissions
argument_list|(
literal|0770
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|_config_
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|CONFIG_FILE_URI
argument_list|)
decl_stmt|;
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|_config_
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|transaction
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading configuration failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
name|realm
operator|.
name|startUp
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|enableXACML
operator|=
operator|(
name|Boolean
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"xacml.enable"
argument_list|)
expr_stmt|;
if|if
condition|(
name|enableXACML
operator|!=
literal|null
operator|&&
name|enableXACML
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|pdp
operator|=
operator|new
name|ExistPDP
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"XACML enabled"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isXACMLEnabled
parameter_list|()
block|{
return|return
name|pdp
operator|!=
literal|null
return|;
block|}
specifier|public
name|ExistPDP
name|getPDP
parameter_list|()
block|{
return|return
name|pdp
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|updateAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
return|return
name|defaultRealm
operator|.
name|updateAccount
argument_list|(
name|account
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|deleteGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|defaultRealm
operator|.
name|deleteRole
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|deleteAccount
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|deleteAccount
argument_list|(
name|getAccount
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|deleteAccount
parameter_list|(
name|Account
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
if|if
condition|(
name|user
operator|==
literal|null
condition|)
return|return;
name|defaultRealm
operator|.
name|deleteAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|Account
name|getAccount
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
name|Account
name|account
init|=
name|realm
operator|.
name|getAccount
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
return|return
name|account
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"user "
operator|+
name|name
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|Account
name|getAccount
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
name|usersById
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|addGroup
parameter_list|(
name|Group
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|defaultRealm
operator|.
name|addGroup
argument_list|(
name|name
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
if|if
condition|(
name|realm
operator|.
name|hasGroup
argument_list|(
name|name
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|hasGroup
parameter_list|(
name|Group
name|group
parameter_list|)
block|{
return|return
name|hasGroup
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|Group
name|getGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
name|Group
name|group
init|=
name|realm
operator|.
name|getGroup
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
return|return
name|group
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|Group
name|getGroup
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
name|groupsById
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasAdminPrivileges
parameter_list|(
name|Account
name|user
parameter_list|)
block|{
return|return
name|user
operator|.
name|hasDbaRole
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasAccount
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
if|if
condition|(
name|realm
operator|.
name|hasAccount
argument_list|(
name|name
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|//	private synchronized void save(DBBroker broker, Txn transaction) throws EXistException {
comment|//		LOG.debug("storing acl file");
comment|//		StringBuffer buf = new StringBuffer();
comment|//        buf.append("<!-- Central user configuration. Editing this document will cause the security " +
comment|//                "to reload and update its internal database. Please handle with care! -->");
comment|//		buf.append("<auth version='1.0'>");
comment|//		// save groups
comment|//        buf.append("<!-- Please do not remove the guest and admin groups -->");
comment|//		buf.append("<groups last-id=\"");
comment|//		buf.append(Integer.toString(nextGroupId));
comment|//		buf.append("\">");
comment|//		for (Iterator i = groups.valueIterator(); i.hasNext();)
comment|//			buf.append(((Group) i.next()).toString());
comment|//		buf.append("</groups>");
comment|//		//save users
comment|//        buf.append("<!-- Please do not remove the admin user. -->");
comment|//		buf.append("<users last-id=\"");
comment|//		buf.append(Integer.toString(nextUserId));
comment|//		buf.append("\">");
comment|//		for (Iterator i = users.valueIterator(); i.hasNext();)
comment|//			buf.append(((User) i.next()).toString());
comment|//		buf.append("</users>");
comment|//		buf.append("</auth>");
comment|//
comment|//		// store users.xml
comment|//		broker.flush();
comment|//		broker.sync(Sync.MAJOR_SYNC);
comment|//
comment|//		User currentUser = broker.getUser();
comment|//		try {
comment|//			broker.setUser(getUser(DBA_USER));
comment|//			Collection sysCollection = broker.getCollection(XmldbURI.SYSTEM_COLLECTION_URI);
comment|//            String data = buf.toString();
comment|//            IndexInfo info = sysCollection.validateXMLResource(transaction, broker, ACL_FILE_URI, data);
comment|//            //TODO : unlock the collection here ?
comment|//            DocumentImpl doc = info.getDocument();
comment|//            doc.getMetadata().setMimeType(MimeType.XML_TYPE.getName());
comment|//            sysCollection.store(transaction, broker, info, data, false);
comment|//			doc.setPermissions(0770);
comment|//			broker.saveCollection(transaction, doc.getCollection());
comment|//		} catch (IOException e) {
comment|//			throw new EXistException(e.getMessage());
comment|//        } catch (TriggerException e) {
comment|//            throw new EXistException(e.getMessage());
comment|//		} catch (SAXException e) {
comment|//			throw new EXistException(e.getMessage());
comment|//		} catch (PermissionDeniedException e) {
comment|//			throw new EXistException(e.getMessage());
comment|//		} catch (LockException e) {
comment|//			throw new EXistException(e.getMessage());
comment|//		} finally {
comment|//			broker.setUser(currentUser);
comment|//		}
comment|//
comment|//		broker.flush();
comment|//		broker.sync(Sync.MAJOR_SYNC);
comment|//	}
comment|//	public synchronized void addAccount(Account account) throws PermissionDeniedException, EXistException, ConfigurationException {
comment|//		 defaultRealm.addAccount(account);
comment|//		if (user.getUID()< 0)
comment|//			user.setUID(++nextUserId);
comment|//		users.put(user.getUID(), user);
comment|//		String[] groups = user.getGroups();
comment|//        // if no group is specified, we automatically fall back to the guest group
comment|//        if (groups.length == 0)
comment|//            user.addGroup(GUEST_GROUP);
comment|//		for (int i = 0; i< groups.length; i++) {
comment|//			if (!hasGroup(groups[i]))
comment|//				newGroup(groups[i]);
comment|//		}
comment|//        TransactionManager transact = pool.getTransactionManager();
comment|//        Txn txn = transact.beginTransaction();
comment|//		DBBroker broker = null;
comment|//		try {
comment|//			broker = pool.get(SYSTEM_USER);
comment|//			save(broker, txn);
comment|//			createUserHome(broker, txn, user);
comment|//            transact.commit(txn);
comment|//		} catch (EXistException e) {
comment|//            transact.abort(txn);
comment|//			LOG.debug("error while creating user", e);
comment|//		} catch (IOException e) {
comment|//            transact.abort(txn);
comment|//			LOG.debug("error while creating home collection", e);
comment|//		} catch (PermissionDeniedException e) {
comment|//            transact.abort(txn);
comment|//			LOG.debug("error while creating home collection", e);
comment|//		} finally {
comment|//			pool.release(broker);
comment|//		}
comment|//	}
specifier|private
name|void
name|createUserHome
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|Account
name|user
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
block|{
if|if
condition|(
name|user
operator|.
name|getHome
argument_list|()
operator|==
literal|null
condition|)
return|return;
name|Subject
name|currentUser
init|=
name|broker
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
name|home
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|user
operator|.
name|getHome
argument_list|()
argument_list|)
decl_stmt|;
name|home
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionConfiguration
name|config
init|=
name|home
operator|.
name|getConfiguration
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|String
name|group
init|=
operator|(
name|config
operator|!=
literal|null
operator|)
condition|?
name|config
operator|.
name|getDefCollGroup
argument_list|(
name|user
argument_list|)
else|:
name|user
operator|.
name|getPrimaryGroup
argument_list|()
decl_stmt|;
name|home
operator|.
name|getPermissions
argument_list|()
operator|.
name|setGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|home
operator|.
name|getPermissions
argument_list|()
operator|.
name|setGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|home
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|currentUser
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|Subject
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|Object
name|credentials
parameter_list|)
throws|throws
name|AuthenticationException
block|{
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
try|try
block|{
return|return
name|realm
operator|.
name|authenticate
argument_list|(
name|username
argument_list|,
name|credentials
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getType
argument_list|()
operator|!=
name|AuthenticationException
operator|.
name|ACCOUNT_NOT_FOUND
condition|)
throw|throw
name|e
throw|;
block|}
block|}
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|ACCOUNT_NOT_FOUND
argument_list|,
literal|"User ["
operator|+
name|username
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Subject
name|getSystemSubject
parameter_list|()
block|{
return|return
operator|new
name|SubjectImpl
argument_list|(
operator|(
name|AccountImpl
operator|)
name|defaultRealm
operator|.
name|ACCOUNT_SYSTEM
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Subject
name|getGuestSubject
parameter_list|()
block|{
return|return
operator|new
name|SubjectImpl
argument_list|(
operator|(
name|AccountImpl
operator|)
name|defaultRealm
operator|.
name|ACCOUNT_GUEST
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|getDBAGroup
parameter_list|()
block|{
return|return
name|defaultRealm
operator|.
name|GROUP_DBA
return|;
block|}
annotation|@
name|Override
specifier|public
name|BrokerPool
name|getDatabase
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
specifier|protected
specifier|synchronized
name|int
name|getNextGroupId
parameter_list|()
block|{
return|return
operator|++
name|lastGroupId
return|;
block|}
specifier|protected
specifier|synchronized
name|int
name|getNextAccoutId
parameter_list|()
block|{
return|return
operator|++
name|lastUserId
return|;
block|}
annotation|@
name|Override
specifier|public
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Account
argument_list|>
name|getUsers
parameter_list|()
block|{
return|return
name|defaultRealm
operator|.
name|getAccounts
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Group
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|defaultRealm
operator|.
name|getRoles
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|addGroup
argument_list|(
operator|new
name|GroupAider
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|Account
name|addAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
if|if
condition|(
name|account
operator|.
name|getRealmId
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"Account must have realm id."
argument_list|)
throw|;
if|if
condition|(
name|account
operator|.
name|getName
argument_list|()
operator|==
literal|null
operator|||
name|account
operator|.
name|getName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"Account must have name."
argument_list|)
throw|;
name|AbstractRealm
name|registeredRealm
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
if|if
condition|(
name|realm
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|account
operator|.
name|getRealmId
argument_list|()
argument_list|)
condition|)
block|{
name|registeredRealm
operator|=
operator|(
name|AbstractRealm
operator|)
name|realm
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|registeredRealm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"The realm id = '"
operator|+
name|account
operator|.
name|getRealmId
argument_list|()
operator|+
literal|"' not found."
argument_list|)
throw|;
name|int
name|id
init|=
name|getNextAccoutId
argument_list|()
decl_stmt|;
name|AccountImpl
name|new_account
init|=
operator|new
name|AccountImpl
argument_list|(
name|registeredRealm
argument_list|,
name|id
argument_list|,
name|account
argument_list|)
decl_stmt|;
name|usersById
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|new_account
argument_list|)
expr_stmt|;
name|registeredRealm
operator|.
name|registerAccount
argument_list|(
name|new_account
argument_list|)
expr_stmt|;
comment|//XXX: one transaction?
name|save
argument_list|()
expr_stmt|;
name|new_account
operator|.
name|save
argument_list|()
expr_stmt|;
name|createUserHome
argument_list|(
name|new_account
argument_list|)
expr_stmt|;
return|return
name|account
return|;
block|}
specifier|protected
name|void
name|save
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
if|if
condition|(
name|configuration
operator|!=
literal|null
condition|)
name|configuration
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
name|configuration
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
specifier|private
name|void
name|createUserHome
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
if|if
condition|(
name|account
operator|.
name|getHome
argument_list|()
operator|==
literal|null
condition|)
return|return;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
name|getDatabase
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|getDatabase
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Subject
name|currentUser
init|=
name|broker
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
name|home
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|account
operator|.
name|getHome
argument_list|()
argument_list|)
decl_stmt|;
name|home
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|account
argument_list|)
expr_stmt|;
name|CollectionConfiguration
name|config
init|=
name|home
operator|.
name|getConfiguration
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|String
name|role
init|=
operator|(
name|config
operator|!=
literal|null
operator|)
condition|?
name|config
operator|.
name|getDefCollGroup
argument_list|(
name|account
argument_list|)
else|:
name|account
operator|.
name|getPrimaryGroup
argument_list|()
decl_stmt|;
name|home
operator|.
name|getPermissions
argument_list|()
operator|.
name|setGroup
argument_list|(
name|role
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|home
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|currentUser
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|getDatabase
argument_list|()
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Realm
name|getRealm
parameter_list|(
name|String
name|id
parameter_list|)
block|{
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|realm
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
return|return
name|realm
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

