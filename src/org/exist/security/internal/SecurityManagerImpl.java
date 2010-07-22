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
name|collections
operator|.
name|CollectionConfigurationManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DefaultDocumentSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|MutableDocumentSet
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
name|UnixStylePermission
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
name|User
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
name|Indexable
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
name|ValueOccurrences
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|IntegerValue
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
name|Attr
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
name|Document
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
name|Element
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/**  * SecurityManager is responsible for managing users and groups.  *   * There's only one SecurityManager for each database instance, which  * may be obtained by {@link BrokerPool#getSecurityManager()}.  *   * Users and groups are stored in the system collection, in document  * users.xml. While it is possible to edit this file by hand, it  * may lead to unexpected results, since SecurityManager reads   * users.xml only during database startup and shutdown.  */
end_comment

begin_class
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
comment|//	private Int2ObjectHashMap<Group> groups = new Int2ObjectHashMap<Group>(65);
comment|//	private Int2ObjectHashMap<User> users = new Int2ObjectHashMap<User>(65);
specifier|protected
name|int
name|nextUserId
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|nextGroupId
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|defCollectionPermissions
init|=
name|Permission
operator|.
name|DEFAULT_PERM
decl_stmt|;
specifier|private
name|int
name|defResourcePermissions
init|=
name|Permission
operator|.
name|DEFAULT_PERM
decl_stmt|;
specifier|private
name|ExistPDP
name|pdp
decl_stmt|;
comment|//default UnixStylePermission
specifier|public
specifier|final
name|Permission
name|DEFAULT_PERMISSION
decl_stmt|;
specifier|private
name|RealmImpl
name|defaultRealm
decl_stmt|;
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
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
specifier|public
name|SecurityManagerImpl
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
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
argument_list|)
expr_stmt|;
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
name|DEFAULT_PERMISSION
operator|=
operator|new
name|UnixStylePermission
argument_list|(
name|this
argument_list|)
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
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<fulltext default=\"none\">"
operator|+
literal|"</fulltext>"
operator|+
literal|"<create path=\"//user/@uid\" type=\"xs:integer\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
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
try|try
block|{
name|collection
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
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
return|return;
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
name|CollectionConfigurationManager
name|mgr
init|=
name|pool
operator|.
name|getConfigurationManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addConfiguration
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|COLLECTION_CONFIG
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
name|Document
name|config
init|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|ACL_FILE_URI
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|Element
name|element
init|=
name|config
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|Attr
name|version
init|=
name|element
operator|.
name|getAttributeNode
argument_list|(
literal|"version"
argument_list|)
decl_stmt|;
name|int
name|major
init|=
literal|0
decl_stmt|;
name|int
name|minor
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|numbers
init|=
name|version
operator|.
name|getValue
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|major
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numbers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|minor
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numbers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
comment|//check version
comment|//load last account& role id
name|NodeList
name|nl
init|=
name|element
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Element
name|next
decl_stmt|;
name|String
name|lastId
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
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
continue|continue;
name|next
operator|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"users"
argument_list|)
condition|)
block|{
name|lastId
operator|=
name|next
operator|.
name|getAttribute
argument_list|(
literal|"last-id"
argument_list|)
expr_stmt|;
try|try
block|{
name|nextUserId
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lastId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
block|}
if|else if
condition|(
name|next
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"groups"
argument_list|)
condition|)
block|{
name|lastId
operator|=
name|next
operator|.
name|getAttribute
argument_list|(
literal|"last-id"
argument_list|)
expr_stmt|;
try|try
block|{
name|nextGroupId
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lastId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
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
comment|//       TransactionManager transact = pool.getTransactionManager();
comment|//       Txn txn = null;
comment|//       DBBroker broker = sysBroker;
comment|//       try {
comment|//          Collection sysCollection = broker.getCollection(XmldbURI.SYSTEM_COLLECTION_URI);
comment|//          if (sysCollection == null) {
comment|//             txn = transact.beginTransaction();
comment|//             sysCollection = broker.getOrCreateCollection(txn, XmldbURI.SYSTEM_COLLECTION_URI);
comment|//              if (sysCollection == null)
comment|//                return;
comment|//             sysCollection.setPermissions(0770);
comment|//             broker.saveCollection(txn, sysCollection);
comment|//             transact.commit(txn);
comment|//          }
comment|//          Document acl = sysCollection.getDocument(broker, ACL_FILE_URI);
comment|//          Element docElement = null;
comment|//          if (acl != null)
comment|//             docElement = acl.getDocumentElement();
comment|//          if (docElement == null) {
comment|//             LOG.debug("creating system users");
comment|//             UserImpl user = new UserImpl(DBA_USER, "");
comment|//             user.addGroup(DBA_GROUP);
comment|//             user.setUID(++nextUserId);
comment|//             users.put(user.getUID(), user);
comment|//             user = new UserImpl(GUEST_USER, GUEST_USER, GUEST_GROUP);
comment|//             user.setUID(++nextUserId);
comment|//             users.put(user.getUID(), user);
comment|//             newGroup(DBA_GROUP);
comment|//             newGroup(GUEST_GROUP);
comment|//             txn = transact.beginTransaction();
comment|//             save(broker, txn);
comment|//             transact.commit(txn);
comment|//          } else {
comment|//             LOG.debug("loading acl");
comment|//             Element root = acl.getDocumentElement();
comment|//             Attr version = root.getAttributeNode("version");
comment|//             int major = 0;
comment|//             int minor = 0;
comment|//             if (version!=null) {
comment|//                String [] numbers = version.getValue().split("\\.");
comment|//                major = Integer.parseInt(numbers[0]);
comment|//                minor = Integer.parseInt(numbers[1]);
comment|//             }
comment|//             NodeList nl = root.getChildNodes();
comment|//             Node node;
comment|//             Element next;
comment|//             User user;
comment|//             NodeList ul;
comment|//             String lastId;
comment|//             Group group;
comment|//             for (int i = 0; i< nl.getLength(); i++) {
comment|//                if(nl.item(i).getNodeType() != Node.ELEMENT_NODE)
comment|//                   continue;
comment|//                next = (Element) nl.item(i);
comment|//                if (next.getTagName().equals("users")) {
comment|//                   lastId = next.getAttribute("last-id");
comment|//                   try {
comment|//                      nextUserId = Integer.parseInt(lastId);
comment|//                   } catch (NumberFormatException e) {
comment|//                   }
comment|//                   ul = next.getChildNodes();
comment|//                   for (int j = 0; j< ul.getLength(); j++) {
comment|//                      node = ul.item(j);
comment|//                      if(node.getNodeType() == Node.ELEMENT_NODE&&
comment|//                              node.getLocalName().equals("user")) {
comment|//                         user = new UserImpl(major,minor,(Element)node);
comment|//                         users.put(user.getUID(), user);
comment|//                      }
comment|//                   }
comment|//                } else if (next.getTagName().equals("groups")) {
comment|//                   lastId = next.getAttribute("last-id");
comment|//                   try {
comment|//                      nextGroupId = Integer.parseInt(lastId);
comment|//                   } catch (NumberFormatException e) {
comment|//                   }
comment|//                   ul = next.getChildNodes();
comment|//                   for (int j = 0; j< ul.getLength(); j++) {
comment|//                      node = ul.item(j);
comment|//                      if(node.getNodeType() == Node.ELEMENT_NODE&&
comment|//                              node.getLocalName().equals("group")) {
comment|//                         group = new GroupImpl((Element)node);
comment|//                         groups.put(group.getId(), group);
comment|//                      }
comment|//                   }
comment|//                }
comment|//             }
comment|//          }
comment|//       } catch (Exception e) {
comment|//          transact.abort(txn);
comment|//          e.printStackTrace();
comment|//          LOG.debug("loading acl failed: " + e.getMessage());
comment|//       }
comment|// read default collection and resource permissions
name|Integer
name|defOpt
init|=
operator|(
name|Integer
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_PERMISSIONS_COLLECTIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|defOpt
operator|!=
literal|null
condition|)
name|defCollectionPermissions
operator|=
name|defOpt
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|defOpt
operator|=
operator|(
name|Integer
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_PERMISSIONS_RESOURCES
argument_list|)
expr_stmt|;
if|if
condition|(
name|defOpt
operator|!=
literal|null
condition|)
name|defResourcePermissions
operator|=
name|defOpt
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|Boolean
name|enableXACML
init|=
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
decl_stmt|;
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
name|void
name|deleteRole
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
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
name|deleteUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|deleteUser
argument_list|(
name|getUser
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|deleteUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
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
comment|//		user = (User)users.remove(user.getUID());
comment|//		if(user != null)
comment|//			LOG.debug("user " + user.getName() + " removed");
comment|//		else
comment|//			LOG.debug("user not found");
comment|//		DBBroker broker = null;
comment|//        TransactionManager transact = pool.getTransactionManager();
comment|//        Txn txn = transact.beginTransaction();
comment|//		try {
comment|//			broker = pool.get(SYSTEM_USER);
comment|//			save(broker, txn);
comment|//            transact.commit(txn);
comment|//		} catch (EXistException e) {
comment|//            transact.abort(txn);
comment|//			e.printStackTrace();
comment|//		} finally {
comment|//			pool.release(broker);
comment|//		}
block|}
specifier|public
specifier|synchronized
name|User
name|getUser
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
name|User
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
specifier|synchronized
name|User
name|getUser
parameter_list|(
name|int
name|uid
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|getSystemAccount
argument_list|()
argument_list|)
expr_stmt|;
name|MutableDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|DocumentImpl
name|acl
init|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|ACL_FILE_URI
argument_list|)
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|Indexable
name|value
init|=
operator|new
name|IntegerValue
argument_list|(
name|uid
argument_list|)
decl_stmt|;
name|ValueOccurrences
index|[]
name|occurrences
init|=
name|broker
operator|.
name|getValueIndex
argument_list|()
operator|.
name|scanIndexKeys
argument_list|(
name|docs
argument_list|,
literal|null
argument_list|,
name|value
argument_list|)
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
name|occurrences
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ValueOccurrences
name|occurrence
init|=
name|occurrences
index|[
name|i
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found: "
operator|+
name|occurrence
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|occurrence
operator|.
name|getValue
argument_list|()
operator|.
name|compareTo
argument_list|(
name|value
argument_list|)
operator|==
literal|0
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"found"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Realm
name|realm
range|:
name|realms
control|)
block|{
name|User
name|account
init|=
name|realm
operator|.
name|getAccount
argument_list|(
name|uid
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
comment|//		LOG.debug("user with uid " + uid + " not found");
return|return
literal|null
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
name|hasRole
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
name|getRole
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
specifier|synchronized
name|Group
name|getGroup
parameter_list|(
name|int
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
name|Group
name|role
init|=
name|realm
operator|.
name|getRole
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|role
operator|!=
literal|null
condition|)
return|return
name|role
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasAdminPrivileges
parameter_list|(
name|User
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
name|hasUser
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
specifier|private
specifier|synchronized
name|void
name|save
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|)
throws|throws
name|EXistException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing acl file"
argument_list|)
expr_stmt|;
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
block|}
specifier|public
specifier|synchronized
name|void
name|setUser
parameter_list|(
name|User
name|user
parameter_list|)
block|{
name|defaultRealm
operator|.
name|addAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
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
block|}
specifier|public
name|int
name|getResourceDefaultPerms
parameter_list|()
block|{
return|return
name|defResourcePermissions
return|;
block|}
specifier|public
name|int
name|getCollectionDefaultPerms
parameter_list|()
block|{
return|return
name|defCollectionPermissions
return|;
block|}
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
name|User
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
name|User
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
name|getUser
argument_list|(
name|DBA_USER
argument_list|)
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
name|User
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
name|User
name|getSystemAccount
parameter_list|()
block|{
return|return
name|defaultRealm
operator|.
name|ACCOUNT_SYSTEM
return|;
block|}
annotation|@
name|Override
specifier|public
name|User
name|getGuestAccount
parameter_list|()
block|{
return|return
name|defaultRealm
operator|.
name|ACCOUNT_GUEST
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
name|nextGroupId
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
name|nextUserId
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
name|User
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
block|}
end_class

end_unit

