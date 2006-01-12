begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Admin.java  *  * This file was auto-generated from WSDL  * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
package|;
end_package

begin_interface
specifier|public
interface|interface
name|Admin
extends|extends
name|java
operator|.
name|rmi
operator|.
name|Remote
block|{
comment|/** 	 * Store a new document into the database. The document will be stored using 	 * the name and location as specified by the path argument. To avoid any conflicts 	 * with the SOAP transport layer, document contents are passed as base64 encoded 	 * binary data. Internally, all documents are stored in UTF-8 encoding. 	 *  	 * The method will automatically replace an already existing document with the same 	 * path if the replace argument is set to true (and the user has sufficient privileges). 	 *   	 * @param sessionId a unique id for the created session. 	 * @param data the document contents as base64 encoded binary data. 	 * @param encoding the character encoding used for the document data. 	 * @param path the target path for the new document. 	 * @param replace should an existing document be replaced?  	 * @throws RemoteException 	 */
specifier|public
name|void
name|store
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|encoding
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|,
name|boolean
name|replace
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** 	 * Create a new user session. Authenticates the user against the database. 	 * The user has to be a valid database user. If the provided user information 	 * is valid, a new session will be registered on the server and a session id 	 * will be returned. 	 *  	 * The session will be valid for at least 60 minutes. Please call disconnect() to 	 * release the session. 	 *  	 * Sessions are shared between the Query and Admin services. A session created 	 * through the Query service can be used with the Admin service and vice versa. 	 *  	 * @param user 	 * @param password 	 * @return session-id a unique id for the created session  	 * @throws RemoteException if the user cannot log in 	 */
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|connect
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|userId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|password
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** 	 * Release a user session. This will free all resources (including result sets). 	 *  	 * @param sessionId a valid session id as returned by connect(). 	 * @throws java.rmi.RemoteException 	 */
specifier|public
name|void
name|disconnect
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** 	 * Remove the specified collection. 	 *  	 * @param sessionId sessionId a unique id for the created session. 	 * @param path the full path to the collection. 	 * @return true on success. 	 *  	 * @throws RemoteException 	 */
specifier|public
name|boolean
name|removeCollection
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** 	 * Remove the specified document. 	 *  	 * @param sessionId a unique id for the created session. 	 * @param path the full path to the document. 	 * @return true on success. 	 *  	 * @throws RemoteException 	 */
specifier|public
name|boolean
name|removeDocument
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** 	 * Create a new collection using the specified path. 	 *  	 * @param sessionId a unique id for the created session. 	 * @param path the full path to the collection. 	 * @return 	 * @throws RemoteException 	 */
specifier|public
name|boolean
name|createCollection
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** 	 * Apply a set of XUpdate modifications to a collection. 	 *  	 * @param sessionId a unique id for the created session. 	 * @param collectionName the full path to the collection. 	 * @param xupdate the XUpdate document to be applied. 	 * @return 	 * @throws RemoteException 	 */
specifier|public
name|int
name|xupdate
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|collectionName
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|xupdate
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** 	 * Apply a set of XUpdate modifications to the specified document. 	 *  	 * @param sessionId a unique id for the created session. 	 * @param documentName the full path to the document. 	 * @param xupdate the XUpdate document to be applied. 	 * @return 	 * @throws RemoteException 	 */
specifier|public
name|int
name|xupdateResource
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|documentName
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|xupdate
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**      * Retrieve a binary resource from the database      * @param sessionId the session identifier      * @param name the name of the binary resource      * @return the binary resource data      * @throws java.rmi.RemoteException      */
specifier|public
name|byte
index|[]
name|getBinaryResource
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|name
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**      * Obtain a description of the specified collection.      *       * The description contains       *   - the collection permissions      *   - list of sub-collections      *   - list of documents and their permissions      *       * @param sessionId the session identifier      * @param collectionName the collection      * @return the collection descriptor      * @throws java.rmi.RemoteException      */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|CollectionDesc
name|getCollectionDesc
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|collectionName
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**      * Set the owner, group and access permissions for a document or collection      * @param sessionId the session id      * @param resource the document/collection that will get new permissions      * @param owner the new owner      * @param ownerGroup the new group      * @param permissions the new access permissions      * @return      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|resource
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|owner
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|ownerGroup
parameter_list|,
name|int
name|permissions
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**      * Copy a resource to the destination collection and rename it.      * @param sessionId the session identifier      * @param docPath the resource to cop      * @param destinationPath the destination collection      * @param newName the new name for the resource      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|copyResource
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|docPath
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|destinationPath
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|newName
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**      * Copy a collection to the destination collection and rename it.       * @param sessionId the session identifier      * @param collectionPath the collection to rename      * @param destinationPath the destination collection      * @param newName the new name of the collection.      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|copyCollection
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|collectionPath
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|destinationPath
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|newName
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** Create a new user.      * Requires Admin privilege.      * @param sessionId the session identifier      * @param name the name of the new user      * @param password the password for the new user      * @param groups the new user should belong to these groups       * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|setUser
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|name
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|password
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Strings
name|groups
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|home
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**       * Obtain information about an eXist user.      *       * @param sessionId the session identifier      * @param user the user      * @return the user information - name, groups and home collection      * @throws java.rmi.RemoteException if user doesn't exist      */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|UserDesc
name|getUser
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|user
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**       * Remove an eXist user account.      *       * Requires Admin privilege      * @param sessionId the session identifier      * @param name the name of the user      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|removeUser
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|name
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**       * Get an list of users      *       * @param sessionId the session identifier      * @return an array of user infomation (name, groups, home collection)      * @throws java.rmi.RemoteException      */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|UserDescs
name|getUsers
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**      *  Obtain a list of the defined database groups      *        * @param sessionId the session identifier      * @return the list of groups      * @throws java.rmi.RemoteException      */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Strings
name|getGroups
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**       * Move a collection and its contents.      *       * @param sessionId the session isentifier      * @param collectionPath the collection to move      * @param destinationPath the new parent collection       * @param newName the new collection name      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|moveCollection
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|collectionPath
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|destinationPath
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|newName
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**       * Move a resource.      *        * @param sessionId the session identifier      * @param docPath the resource to move      * @param destinationPath the collection to receive the moved resource      * @param newName the new name for the resource      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|moveResource
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|docPath
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|destinationPath
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|newName
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** Place a write lock on the specified resource       * @param sessionId the session identifier      * @param path the path of the resource to lock      * @param userName the user name of the lock owner      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|lockResource
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|userName
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** Release the lock on the specified resource      * @param sessionId the session identifier      * @param path path of the resource to unlock      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|unlockResource
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** Return the name of the user owning the lock on the specified resource      * @param sessionId the session identifier      * @param path the resource      * @return the name of the lock owner or "" if there is no lock      * @throws java.rmi.RemoteException      */
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|hasUserLock
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** Return the permissions of the specified collection/document      * @param sessionId the session identifier      * @param resource the collection or document      * @return the permissions (owner, group, access permissions)      * @throws java.rmi.RemoteException      */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Permissions
name|getPermissions
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|resource
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** Return a list of the permissions of the child collections of the specified parent collection      * @param sessionId the session identifier      * @param name the name of the parent collection      * @return array containing child collections with their permissions      * @throws java.rmi.RemoteException      */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|EntityPermissionsList
name|listCollectionPermissions
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|name
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/** Return a list of the permissions of the child documents of the specified parent collection      * @param sessionId the session identifier      * @param name name of the parent collection      * @return array containing documents with their permissions      * @throws java.rmi.RemoteException      */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|EntityPermissionsList
name|listDocumentPermissions
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|name
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**      *  Return a list of Indexed Elements for a collection      * @param sessionId the session identifier      * @param collectionName the collection name      * @param inclusive include sub-collections ?      * @return the list of Indexed Elements      * @throws java.rmi.RemoteException      */
specifier|public
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|IndexedElements
name|getIndexedElements
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|collectionName
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
comment|/**      * Store a binary resource in the database      *       * @param sessionId the session identifier      * @param data the binary data      * @param path the path for the new resource      * @param mimeType the mime type for the resource      * @param replace replace resource if it already exists      * @throws java.rmi.RemoteException      */
specifier|public
name|void
name|storeBinary
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|sessionId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|path
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|mimeType
parameter_list|,
name|boolean
name|replace
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
function_decl|;
block|}
end_interface

end_unit

