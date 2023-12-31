begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|interpreter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|NamePool
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
name|XPathException
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
name|AnyURIValue
import|;
end_import

begin_interface
specifier|public
interface|interface
name|ContextNS
block|{
comment|/** 	 * Declare a user-defined static prefix/namespace mapping. 	 * 	 * eXist internally keeps a table containing all prefix/namespace mappings it found in documents, which have been previously stored into the 	 * database. These default mappings need not to be declared explicitely. 	 * 	 * @param prefix the namespace prefix. 	 * @param uri the namespace URI. 	 * 	 * @throws XPathException if an error occurs whilst declaring the namespace. 	 */
specifier|public
name|void
name|declareNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|declareNamespaces
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaceMap
parameter_list|)
function_decl|;
comment|/** 	 * Removes the namespace URI from the prefix/namespace mappings table. 	 * 	 * @param uri the namespace URI. 	 */
specifier|public
name|void
name|removeNamespace
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** 	 * Declare an in-scope namespace. This is called during query execution. 	 * 	 * @param prefix the namespace prefix. 	 * @param uri the namespace URI. 	 */
specifier|public
name|void
name|declareInScopeNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
function_decl|;
specifier|public
name|String
name|getInScopeNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
specifier|public
name|String
name|getInScopePrefix
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
specifier|public
name|String
name|getInheritedNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
specifier|public
name|String
name|getInheritedPrefix
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** 	 * Return the namespace URI mapped to the registered prefix or null if the prefix is not registered. 	 * 	 * @param prefix the namespace prefix. 	 * 	 * @return namespace 	 */
specifier|public
name|String
name|getURIForPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
comment|/** 	 * Get URI Prefix 	 * 	 * @param uri the namespace URI. 	 * 	 * @return the prefix mapped to the registered URI or null if the URI is not registered. 	 */
specifier|public
name|String
name|getPrefixForURI
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** 	 * Returns the current default function namespace. 	 * 	 * @return current default function namespace 	 */
specifier|public
name|String
name|getDefaultFunctionNamespace
parameter_list|()
function_decl|;
comment|/** 	 * Set the default function namespace. By default, this points to the namespace for XPath built-in functions. 	 * 	 * @param uri the namespace URI. 	 * 	 * @throws XPathException if an error occurs whilst setting the default function namespace. 	 */
specifier|public
name|void
name|setDefaultFunctionNamespace
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns the current default element namespace. 	 * 	 * @return current default element namespace schema 	 * 	 * @throws XPathException if an error occurs whilst getting the default element namespace schema. 	 */
specifier|public
name|String
name|getDefaultElementNamespaceSchema
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Set the default element namespace. By default, this points to the empty uri. 	 * 	 * @param uri the namespace URI. 	 * 	 * @throws XPathException if an error occurs whilst setting the default element namespace schema. 	 */
specifier|public
name|void
name|setDefaultElementNamespaceSchema
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns the current default element namespace. 	 * 	 * @return current default element namespace 	 * 	 * @throws XPathException if an error occurs whilst getting the default element namespace. 	 */
specifier|public
name|String
name|getDefaultElementNamespace
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Set the default element namespace. By default, this points to the empty uri. 	 * 	 * @param uri the namespace URI. 	 * @param schema the schema 	 * 	 * @throws XPathException if an error occurs whilst getting the default element namespace. 	 */
specifier|public
name|void
name|setDefaultElementNamespace
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|schema
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns true if namespaces for constructed element and document nodes should be preserved on copy by default. 	 * 	 * @return true if namespaces are preserved, false otherwise. 	 */
specifier|public
name|boolean
name|preserveNamespaces
parameter_list|()
function_decl|;
comment|/** 	 * Set whether namespaces should be preserved. 	 * 	 * @param preserve true if namespaces should be preserved, false otherwise. 	 */
specifier|public
name|void
name|setPreserveNamespaces
parameter_list|(
specifier|final
name|boolean
name|preserve
parameter_list|)
function_decl|;
comment|/** 	 * Returns true if namespaces for constructed element and document nodes should be inherited on copy by default. 	 * 	 * @return true if namespaces are inheirted, false otherwise. 	 */
specifier|public
name|boolean
name|inheritNamespaces
parameter_list|()
function_decl|;
comment|/** 	 * Set to true if namespaces for constructed element and document nodes should be inherited on copy by default. 	 * 	 * @param inherit true if namespaces are inheirted, false otherwise. 	 */
specifier|public
name|void
name|setInheritNamespaces
parameter_list|(
specifier|final
name|boolean
name|inherit
parameter_list|)
function_decl|;
comment|/** 	 * Returns the shared name pool used by all in-memory documents which are created within this query context. Create a name pool for every document 	 * would be a waste of memory, especially since it is likely that the documents contain elements or attributes with similar names. 	 * 	 * @return the shared name pool 	 */
specifier|public
name|NamePool
name|getSharedNamePool
parameter_list|()
function_decl|;
comment|/** 	 * Set the base URI for the evaluation context. 	 * 	 * This is the URI returned by the fn:base-uri() function. 	 * 	 * @param uri the namespace URI. 	 */
specifier|public
name|void
name|setBaseURI
parameter_list|(
name|AnyURIValue
name|uri
parameter_list|)
function_decl|;
comment|/** 	 * Set the base URI for the evaluation context. 	 * 	 * A base URI specified via the base-uri directive in the XQuery prolog overwrites any other setting. 	 * 	 * @param uri the namespace URI. 	 * @param setInProlog true if the base-uri was defined in the prolog. 	 */
specifier|public
name|void
name|setBaseURI
parameter_list|(
name|AnyURIValue
name|uri
parameter_list|,
name|boolean
name|setInProlog
parameter_list|)
function_decl|;
comment|/** 	 * Determine if the base-uri is declared. 	 * 	 * @return true if the base-uri is declared, false otherwise. 	 */
specifier|public
name|boolean
name|isBaseURIDeclared
parameter_list|()
function_decl|;
comment|/** 	 * Get the base URI of the evaluation context. 	 * 	 * This is the URI returned by the fn:base-uri() function. 	 * 	 * @return base URI of the evaluation context 	 * 	 * @throws XPathException if an error occurs whilst setting the base-uri 	 */
specifier|public
name|AnyURIValue
name|getBaseURI
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|pushInScopeNamespaces
parameter_list|()
function_decl|;
comment|/** 	 * Push all in-scope namespace declarations onto the stack. 	 * 	 * @param  inherit true if namespaces should be inheirted when pushing 	 */
specifier|public
name|void
name|pushInScopeNamespaces
parameter_list|(
name|boolean
name|inherit
parameter_list|)
function_decl|;
specifier|public
name|void
name|popInScopeNamespaces
parameter_list|()
function_decl|;
specifier|public
name|void
name|pushNamespaceContext
parameter_list|()
function_decl|;
specifier|public
name|void
name|popNamespaceContext
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

