begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
package|;
end_package

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|Remote
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

begin_comment
comment|/**  * This interface defines eXist's SOAP service for (read-only)   * queries on the database.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Query
extends|extends
name|Remote
block|{
comment|/** 	 * Create a new user session. Authenticates the user against the database. 	 * The user has to be a valid database user. If the provided user information 	 * is valid, a new session will be registered on the server and a session id 	 * will be returned. 	 *  	 * The session will be valid for at least 60 minutes. Please call disconnect() to 	 * release the session. 	 *  	 * Sessions are shared between the Query and Admin services. A session created 	 * through the Query service can be used with the Admin service and vice versa. 	 *  	 * @param user 	 * @param password 	 * @return session-id a unique id for the created session  	 * @throws RemoteException if the user cannot log in 	 */
specifier|public
name|String
name|connect
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|RemoteException
function_decl|;
comment|/** 	 * Release a user session. This will free all resources (including result sets). 	 *  	 * @param sessionId a valid session id as returned by connect(). 	 * @throws java.rmi.RemoteException 	 */
specifier|public
name|void
name|disconnect
parameter_list|(
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
comment|/** 	 * Retrieve a document from the database. 	 *  	 * @param sessionId a valid session id as returned by connect(). 	 * @param path the full path to the document. 	 * @param indent should the document be pretty-printed (indented)? 	 * @param xinclude should xinclude tags be expanded? 	 * @return the resource as string 	 * @throws RemoteException 	 */
specifier|public
name|String
name|getResource
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|indent
parameter_list|,
name|boolean
name|xinclude
parameter_list|)
throws|throws
name|RemoteException
function_decl|;
comment|/**      *       * @param sessionId a valid session id as returned by connect().      * @param xpath XPath query string.      * @return QueryResponse describing the query results.      * @throws RemoteException      */
specifier|public
name|QueryResponse
name|query
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|String
name|xpath
parameter_list|)
throws|throws
name|RemoteException
function_decl|;
comment|/**      * Retrieve a set of query results from the last query executed within      * the current session.      *       * The first result to be retrieved from the result set is defined by the      * start-parameter. Results are counted from 1.      *        * @param sessionId a valid session id as returned by connect().      * @param start the first result to retrieve.      * @param howmany number of results to be returned.      * @param indent should the XML be pretty-printed?      * @param xinclude should xinclude tags be expanded?      * @param highlight highlight matching search terms within elements      * or attributes. Possible values are: "elements" for elements only,      * "attributes" for attributes only, "both" for elements and attributes,      * "none" to disable highlighting. For elements, matching terms are      * surrounded by&lt;exist:match&gt; tags. For attributes, terms are      * marked with the char sequence "||".      *       * @return      * @throws RemoteException      */
specifier|public
name|String
index|[]
name|retrieve
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|,
name|boolean
name|indent
parameter_list|,
name|boolean
name|xinclude
parameter_list|,
name|String
name|highlight
parameter_list|)
throws|throws
name|RemoteException
function_decl|;
comment|/** 	 * For the specified document, retrieve a set of query results from  	 * the last query executed within the current session. Only hits in 	 * the given document (identified by its path) are returned.  	 *  	 * The first result to be retrieved from the result set is defined by the 	 * start-parameter. Results are counted from 1. 	 *   	 * @param sessionId a valid session id as returned by connect(). 	 * @param start the first result to retrieve. 	 * @param howmany number of results to be returned. 	 * @param path the full path to the document. 	 * @param indent should the XML be pretty-printed? 	 * @param xinclude should xinclude tags be expanded? 	 * @param highlight highlight matching search terms within elements 	 * or attributes. Possible values are: "elements" for elements only, 	 * "attributes" for attributes only, "both" for elements and attributes, 	 * "none" to disable highlighting. For elements, matching terms are 	 * surrounded by&lt;exist:match&gt; tags. For attributes, terms are 	 * marked with the char sequence "||". 	 *  	 * @return 	 * @throws RemoteException 	 */
specifier|public
name|String
index|[]
name|retrieveByDocument
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|indent
parameter_list|,
name|boolean
name|xinclude
parameter_list|,
name|String
name|highlight
parameter_list|)
throws|throws
name|RemoteException
function_decl|;
comment|/**      * Get information on the specified collection.      *       * @param sessionId a valid session id as returned by connect().      * @param path the full path to the collection.      * @return      * @throws java.rmi.RemoteException      */
specifier|public
name|Collection
name|listCollection
parameter_list|(
name|String
name|sessionId
parameter_list|,
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
block|}
end_interface

end_unit

