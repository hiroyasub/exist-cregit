begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|xacml
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|client
operator|.
name|ClientFrame
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
name|XACMLConstants
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
name|XACMLUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|ParsingException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|Policy
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|PolicySet
import|;
end_import

begin_class
specifier|public
class|class
name|DatabaseInterface
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|DatabaseInterface
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Collection
name|policyCollection
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|DatabaseInterface
parameter_list|()
block|{
block|}
specifier|public
name|DatabaseInterface
parameter_list|(
name|Collection
name|systemCollection
parameter_list|)
block|{
name|setup
argument_list|(
name|systemCollection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Collection
name|getPolicyCollection
parameter_list|()
block|{
return|return
name|policyCollection
return|;
block|}
specifier|private
name|void
name|setup
parameter_list|(
name|Collection
name|systemCollection
parameter_list|)
block|{
if|if
condition|(
name|systemCollection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"System collection cannot be null"
argument_list|)
throw|;
block|}
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|systemCollection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|policyCollection
operator|=
name|service
operator|.
name|createCollection
argument_list|(
name|XACMLConstants
operator|.
name|POLICY_COLLECTION_NAME
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|confCol
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"config"
operator|+
name|XACMLConstants
operator|.
name|POLICY_COLLECTION
argument_list|)
decl_stmt|;
specifier|final
name|String
name|confName
init|=
name|XACMLConstants
operator|.
name|POLICY_COLLECTION_NAME
operator|+
literal|".xconf"
decl_stmt|;
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|confCol
operator|.
name|createResource
argument_list|(
name|confName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|in
operator|=
name|DatabaseInterface
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|confName
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not find policy collection configuration file '"
operator|+
name|confName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|content
init|=
name|XACMLUtil
operator|.
name|toString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|confCol
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"Error setting up XACML editor"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"Error setting up XACML editor"
argument_list|,
name|xe
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
block|}
block|}
block|}
block|}
specifier|public
name|void
name|writePolicies
parameter_list|(
name|RootNode
name|root
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|removeDocs
decl_stmt|;
try|try
block|{
name|removeDocs
operator|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|policyCollection
operator|.
name|listResources
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not list policy collection resources"
argument_list|,
name|xe
argument_list|)
expr_stmt|;
name|removeDocs
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|int
name|size
init|=
name|root
operator|.
name|getChildCount
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
name|size
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|AbstractPolicyNode
name|node
init|=
operator|(
name|AbstractPolicyNode
operator|)
name|root
operator|.
name|getChild
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|documentName
init|=
name|node
operator|.
name|getDocumentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|documentName
operator|!=
literal|null
operator|&&
name|removeDocs
operator|!=
literal|null
condition|)
block|{
name|removeDocs
operator|.
name|remove
argument_list|(
name|documentName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|node
operator|.
name|isModified
argument_list|(
literal|true
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|node
operator|.
name|commit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|node
operator|.
name|create
argument_list|()
operator|.
name|encode
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
block|{
name|XMLResource
name|xres
decl_stmt|;
specifier|final
name|Resource
name|res
init|=
operator|(
name|documentName
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|policyCollection
operator|.
name|getResource
argument_list|(
name|documentName
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|xres
operator|=
literal|null
expr_stmt|;
block|}
if|else if
condition|(
name|res
operator|instanceof
name|XMLResource
condition|)
block|{
name|xres
operator|=
operator|(
name|XMLResource
operator|)
name|res
expr_stmt|;
block|}
else|else
block|{
name|xres
operator|=
literal|null
expr_stmt|;
name|policyCollection
operator|.
name|removeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xres
operator|==
literal|null
condition|)
block|{
name|xres
operator|=
operator|(
name|XMLResource
operator|)
name|policyCollection
operator|.
name|createResource
argument_list|(
name|documentName
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
if|if
condition|(
name|documentName
operator|==
literal|null
condition|)
block|{
name|documentName
operator|=
name|xres
operator|.
name|getDocumentId
argument_list|()
expr_stmt|;
name|node
operator|.
name|setDocumentName
argument_list|(
name|documentName
argument_list|)
expr_stmt|;
block|}
block|}
name|xres
operator|.
name|setContent
argument_list|(
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|policyCollection
operator|.
name|storeResource
argument_list|(
name|xres
argument_list|)
expr_stmt|;
name|node
operator|.
name|commit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"Error saving policy '"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
if|if
condition|(
name|documentName
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|" to document '"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|documentName
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
block|}
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|removeDocs
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
specifier|final
name|String
name|documentName
range|:
name|removeDocs
control|)
block|{
try|try
block|{
specifier|final
name|Resource
name|removeResource
init|=
name|policyCollection
operator|.
name|getResource
argument_list|(
name|documentName
argument_list|)
decl_stmt|;
name|policyCollection
operator|.
name|removeResource
argument_list|(
name|removeResource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not remove resource '"
operator|+
name|documentName
operator|+
literal|"'"
argument_list|,
name|xe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|RootNode
name|getPolicies
parameter_list|()
block|{
specifier|final
name|RootNode
name|root
init|=
operator|new
name|RootNode
argument_list|()
decl_stmt|;
name|findPolicies
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|root
return|;
block|}
specifier|private
name|void
name|findPolicies
parameter_list|(
name|RootNode
name|root
parameter_list|)
block|{
try|try
block|{
specifier|final
name|String
index|[]
name|resourceIds
init|=
name|policyCollection
operator|.
name|listResources
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|resourceId
range|:
name|resourceIds
control|)
block|{
specifier|final
name|Resource
name|resource
init|=
name|policyCollection
operator|.
name|getResource
argument_list|(
name|resourceId
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
operator|&&
name|resource
operator|instanceof
name|XMLResource
condition|)
block|{
name|handleResource
argument_list|(
operator|(
name|XMLResource
operator|)
name|resource
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"Error scanning for policies"
argument_list|,
name|xe
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleResource
parameter_list|(
name|XMLResource
name|xres
parameter_list|,
name|RootNode
name|root
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|documentName
init|=
name|xres
operator|.
name|getDocumentId
argument_list|()
decl_stmt|;
specifier|final
name|Node
name|content
init|=
name|xres
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|Element
name|rootElement
decl_stmt|;
if|if
condition|(
name|content
operator|instanceof
name|Document
condition|)
block|{
name|rootElement
operator|=
operator|(
operator|(
name|Document
operator|)
name|content
operator|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|content
operator|instanceof
name|Element
condition|)
block|{
name|rootElement
operator|=
operator|(
name|Element
operator|)
name|content
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The DOM representation of resource '"
operator|+
name|documentName
operator|+
literal|"' in the policy collection was not a Document or Element node."
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|String
name|namespace
init|=
name|rootElement
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
specifier|final
name|String
name|tagName
init|=
name|rootElement
operator|.
name|getTagName
argument_list|()
decl_stmt|;
comment|//sunxacml does not do namespaces, so this part is commented out for now
if|if
condition|(
comment|/*XACMLConstants.XACML_POLICY_NAMESPACE.equals(namespace)&& */
name|XACMLConstants
operator|.
name|POLICY_ELEMENT_LOCAL_NAME
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|Policy
name|policy
decl_stmt|;
try|try
block|{
name|policy
operator|=
name|Policy
operator|.
name|getInstance
argument_list|(
name|rootElement
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParsingException
name|pe
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"Error parsing policy document '"
operator|+
name|documentName
operator|+
literal|"'"
argument_list|,
name|pe
argument_list|)
expr_stmt|;
return|return;
block|}
name|root
operator|.
name|add
argument_list|(
operator|new
name|PolicyNode
argument_list|(
name|root
argument_list|,
name|documentName
argument_list|,
name|policy
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
comment|/*XACMLConstants.XACML_POLICY_NAMESPACE.equals(namespace)&& */
name|XACMLConstants
operator|.
name|POLICY_SET_ELEMENT_LOCAL_NAME
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
condition|)
block|{
name|PolicySet
name|policySet
decl_stmt|;
try|try
block|{
name|policySet
operator|=
name|PolicySet
operator|.
name|getInstance
argument_list|(
name|rootElement
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParsingException
name|pe
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"Error parsing policy set document '"
operator|+
name|documentName
operator|+
literal|"'"
argument_list|,
name|pe
argument_list|)
expr_stmt|;
return|return;
block|}
name|root
operator|.
name|add
argument_list|(
operator|new
name|PolicySetNode
argument_list|(
name|root
argument_list|,
name|documentName
argument_list|,
name|policySet
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Document '"
operator|+
name|documentName
operator|+
literal|"' in policy collection is not a policy: root tag has namespace '"
operator|+
name|namespace
operator|+
literal|"' and name '"
operator|+
name|tagName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

