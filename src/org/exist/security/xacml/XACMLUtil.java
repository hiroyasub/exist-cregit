begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
package|;
end_package

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|AbstractPolicy
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
name|Indenter
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
name|PolicyReference
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

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|PolicyTreeElement
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
name|ProcessingException
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
name|Target
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
name|cond
operator|.
name|Apply
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
name|ctx
operator|.
name|Status
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
name|finder
operator|.
name|PolicyFinderResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|CharArrayWriter
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|IndexInfo
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
name|DocumentSet
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
name|NodeSet
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
name|QName
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
name|NativeValueIndexByQName
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
name|UpdateListener
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
name|Configuration
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
name|AtomicValue
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
name|Sequence
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

begin_comment
comment|/**  * This class contains utility methods for working with XACML  * in eXist.  */
end_comment

begin_class
specifier|public
class|class
name|XACMLUtil
implements|implements
name|UpdateListener
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ExistPolicyModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
name|POLICY_CACHE
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|(
literal|8
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|samplePolicyDocs
init|=
block|{
literal|"/xacml/builtin_policy.xml"
block|,
literal|"/xacml/external_modules_policy.xml"
block|,
literal|"/xacml/reflection_policy.xml"
block|}
decl_stmt|;
specifier|private
name|ExistPDP
name|pdp
decl_stmt|;
specifier|private
name|XACMLUtil
parameter_list|()
block|{
block|}
name|XACMLUtil
parameter_list|(
name|ExistPDP
name|pdp
parameter_list|)
block|{
if|if
condition|(
name|pdp
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"ExistPDP cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|pdp
operator|=
name|pdp
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|pdp
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|pool
operator|.
name|getNotificationService
argument_list|()
operator|.
name|subscribe
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|initializePolicyCollection
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initializePolicyCollection
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
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
argument_list|()
expr_stmt|;
name|initializePolicyCollection
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not get broker pool to initialize policy collection"
argument_list|,
name|ee
argument_list|)
expr_stmt|;
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
specifier|private
name|void
name|initializePolicyCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|Collection
name|policyCollection
init|=
name|getPolicyCollection
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|policyCollection
operator|==
literal|null
condition|)
return|return;
comment|//warning generated by getPolicyCollection, no need to duplicate here
if|if
condition|(
name|policyCollection
operator|.
name|getDocumentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Configuration
name|conf
init|=
name|broker
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|conf
operator|==
literal|null
operator|||
name|conf
operator|.
name|getProperty
argument_list|(
name|XACMLConstants
operator|.
name|LOAD_DEFAULT_POLICIES_PROPERTY
argument_list|)
operator|==
name|Boolean
operator|.
name|TRUE
condition|)
name|storeDefaultPolicies
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|//UpdateListener method
comment|/** 	 * This method is called by the<code>NotificationService</code> 	 * when documents are updated in the databases.  If a document 	 * is removed or updated from the policy collection, it is removed 	 * from the policy cache. 	 */
specifier|public
name|void
name|documentUpdated
parameter_list|(
name|DocumentImpl
name|document
parameter_list|,
name|int
name|event
parameter_list|)
block|{
if|if
condition|(
name|inPolicyCollection
argument_list|(
name|document
argument_list|)
operator|&&
operator|(
name|event
operator|==
name|UpdateListener
operator|.
name|REMOVE
operator|||
name|event
operator|==
name|UpdateListener
operator|.
name|UPDATE
operator|)
condition|)
name|POLICY_CACHE
operator|.
name|remove
argument_list|(
name|document
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Returns true if the specified document is in the policy collection. 	 * This does not check subcollections. 	 *  	 * @param document The document in question 	 * @return if the document is in the policy collection 	 */
specifier|public
specifier|static
name|boolean
name|inPolicyCollection
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
block|{
return|return
name|XACMLConstants
operator|.
name|POLICY_COLLECTION
operator|.
name|equals
argument_list|(
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	* Performs any necessary cleanup operations.  Generally only 	* called if XACML has been disabled. 	*/
specifier|public
name|void
name|close
parameter_list|()
block|{
name|pdp
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNotificationService
argument_list|()
operator|.
name|unsubscribe
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/** 	* Gets the policy (or policy set) specified by the given id. 	*  	* @param type The type of id reference: 	*	PolicyReference.POLICY_REFERENCE for a policy reference 	*	or PolicyReference.POLICYSET_REFERENCE for a policy set 	*	reference. 	* @param idReference The id of the policy (or policy set) to 	*	retrieve 	* @param broker the broker to use to access the database 	* @return The referenced policy. 	* @throws ProcessingException if there is an error finding 	*	the policy (or policy set). 	* @throws XPathException 	*/
specifier|public
name|AbstractPolicy
name|findPolicy
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|URI
name|idReference
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|ParsingException
throws|,
name|ProcessingException
throws|,
name|XPathException
block|{
name|QName
name|idAttributeQName
init|=
name|getIdAttributeQName
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|idAttributeQName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Invalid reference type: "
operator|+
name|type
argument_list|)
throw|;
name|DocumentImpl
name|policyDoc
init|=
name|getPolicyDocument
argument_list|(
name|broker
argument_list|,
name|idAttributeQName
argument_list|,
name|idReference
argument_list|)
decl_stmt|;
if|if
condition|(
name|policyDoc
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|getPolicyDocument
argument_list|(
name|policyDoc
argument_list|)
return|;
block|}
comment|/** 	* This method returns all policy documents in the policies collection. 	* If recursive is true, policies in subcollections are returned as well. 	* 	* @param broker the broker to use to access the database 	* @param recursive true if policies in subcollections should be 	*	returned as well 	* @return All policy documents in the policies collection 	*/
specifier|public
specifier|static
name|DocumentSet
name|getPolicyDocuments
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|boolean
name|recursive
parameter_list|)
block|{
name|Collection
name|policyCollection
init|=
name|getPolicyCollection
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|policyCollection
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|int
name|documentCount
init|=
name|policyCollection
operator|.
name|getDocumentCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|documentCount
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|DocumentSet
name|documentSet
init|=
operator|new
name|DocumentSet
argument_list|(
name|documentCount
argument_list|)
decl_stmt|;
return|return
name|policyCollection
operator|.
name|allDocs
argument_list|(
name|broker
argument_list|,
name|documentSet
argument_list|,
name|recursive
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** 	 * Gets the policy collection or creates it if it does not exist. 	 *  	 * @param broker The broker to use to access the database. 	 * @return A<code>Collection</code> object for the policy collection. 	 */
specifier|public
specifier|static
name|Collection
name|getPolicyCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|Collection
name|policyCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XACMLConstants
operator|.
name|POLICY_COLLECTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|policyCollection
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating policy collection '"
operator|+
name|XACMLConstants
operator|.
name|POLICY_COLLECTION
operator|+
literal|"'..."
argument_list|)
expr_stmt|;
name|TransactionManager
name|transact
init|=
name|broker
operator|.
name|getBrokerPool
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
name|policyCollection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XACMLConstants
operator|.
name|POLICY_COLLECTION
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|policyCollection
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error creating policy collection"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error creating policy collection"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|return
name|policyCollection
return|;
block|}
comment|/** 	* Returns the single policy (or policy set) document that has the 	* attribute specified by attributeQName with the value 	* attributeValue, null if none match, or throws a 	*<code>ProcessingException</code> if more than one match.  This is 	* performed by a QName range index lookup and so it requires a range 	* index to be given on the attribute. 	*  	* @param attributeQName The name of the attribute 	* @param attributeValue The value of the attribute 	* @param broker the broker to use to access the database 	* @return The referenced policy. 	* @throws ProcessingException if there is an error finding 	*	the policy (or policy set) documents. 	* @throws XPathException if there is an error performing 	*	the index lookup 	*/
specifier|public
name|DocumentImpl
name|getPolicyDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|QName
name|attributeQName
parameter_list|,
name|URI
name|attributeValue
parameter_list|)
throws|throws
name|ProcessingException
throws|,
name|XPathException
block|{
name|DocumentSet
name|documentSet
init|=
name|getPolicyDocuments
argument_list|(
name|broker
argument_list|,
name|attributeQName
argument_list|,
name|attributeValue
argument_list|)
decl_stmt|;
name|int
name|documentCount
init|=
operator|(
name|documentSet
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|documentSet
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|documentCount
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not find "
operator|+
name|attributeQName
operator|.
name|getLocalName
argument_list|()
operator|+
literal|" '"
operator|+
name|attributeValue
operator|+
literal|"'"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|documentCount
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|ProcessingException
argument_list|(
literal|"Too many applicable policies for "
operator|+
name|attributeQName
operator|.
name|getLocalName
argument_list|()
operator|+
literal|" '"
operator|+
name|attributeValue
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
operator|(
name|DocumentImpl
operator|)
name|documentSet
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
comment|/** 	* Gets all policy (or policy set) documents that have the 	* attribute specified by attributeQName with the value 	* attributeValue.  This is performed by a QName range index 	* lookup and so it requires a range index to be given 	* on the attribute. 	*  	* @param attributeQName The name of the attribute 	* @param attributeValue The value of the attribute 	* @param broker the broker to use to access the database 	* @return The referenced policy. 	* @throws ProcessingException if there is an error finding 	*	the policy (or policy set) documents. 	* @throws XPathException if there is an error performing the 	*	index lookup 	*/
specifier|public
name|DocumentSet
name|getPolicyDocuments
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|QName
name|attributeQName
parameter_list|,
name|URI
name|attributeValue
parameter_list|)
throws|throws
name|ProcessingException
throws|,
name|XPathException
block|{
if|if
condition|(
name|attributeQName
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|attributeValue
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|AtomicValue
name|comparison
init|=
operator|new
name|AnyURIValue
argument_list|(
name|attributeValue
argument_list|)
decl_stmt|;
name|DocumentSet
name|documentSet
init|=
name|getPolicyDocuments
argument_list|(
name|broker
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|NodeSet
name|nodeSet
init|=
name|documentSet
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|NativeValueIndexByQName
name|index
init|=
name|broker
operator|.
name|getQNameValueIndex
argument_list|()
decl_stmt|;
name|Sequence
name|results
init|=
name|index
operator|.
name|findByQName
argument_list|(
name|attributeQName
argument_list|,
name|comparison
argument_list|,
name|nodeSet
argument_list|)
decl_stmt|;
return|return
operator|(
name|results
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|results
operator|.
name|getDocumentSet
argument_list|()
return|;
block|}
comment|/** 	* Gets the name of the attribute that specifies the policy 	* (if type == PolicyReference.POLICY_REFERENCE) or 	* the policy set (if type == PolicyReference.POLICYSET_REFERENCE). 	* 	* @param type The type of id reference: 	*	PolicyReference.POLICY_REFERENCE for a policy reference 	*	or PolicyReference.POLICYSET_REFERENCE for a policy set 	*	reference. 	* @return The attribute name for the reference type 	*/
specifier|public
specifier|static
name|QName
name|getIdAttributeQName
parameter_list|(
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|PolicyReference
operator|.
name|POLICY_REFERENCE
condition|)
return|return
operator|new
name|QName
argument_list|(
name|XACMLConstants
operator|.
name|POLICY_ID_LOCAL_NAME
argument_list|,
name|XACMLConstants
operator|.
name|XACML_POLICY_NAMESPACE
argument_list|)
return|;
if|else if
condition|(
name|type
operator|==
name|PolicyReference
operator|.
name|POLICYSET_REFERENCE
condition|)
return|return
operator|new
name|QName
argument_list|(
name|XACMLConstants
operator|.
name|POLICY_SET_ID_LOCAL_NAME
argument_list|,
name|XACMLConstants
operator|.
name|XACML_POLICY_NAMESPACE
argument_list|)
return|;
else|else
return|return
literal|null
return|;
block|}
comment|//logs the specified message and exception
comment|//then, returns a result with status Indeterminate and the given message
comment|/** 	* Convenience method for errors occurring while processing.  The message 	* and exception are logged and a<code>PolicyFinderResult</code> is 	* generated with Status.STATUS_PROCESSING_ERROR as the error condition 	* and the message as the message. 	* 	* @param message The message describing the error. 	* @param t The cause of the error, may be null 	* @return A<code>PolicyFinderResult</code> representing the error. 	*/
specifier|public
specifier|static
name|PolicyFinderResult
name|errorResult
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|,
name|t
argument_list|)
expr_stmt|;
return|return
operator|new
name|PolicyFinderResult
argument_list|(
operator|new
name|Status
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|Status
operator|.
name|STATUS_PROCESSING_ERROR
argument_list|)
argument_list|,
name|message
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	* Obtains a parsed representation of the specified XACML Policy or PolicySet 	* document.  If the document has already been parsed, this method returns the 	* cached<code>AbstractPolicy</code>.  Otherwise, it unmarshals the document into 	* an<code>AbstractPolicy</code> and caches it. 	* 	* @param policyDoc the policy (or policy set) document 	*	for which a parsed representation should be obtained 	* @return a parsed policy (or policy set) 	* @throws ParsingException if an error occurs while parsing the specified document 	*/
specifier|public
name|AbstractPolicy
name|getPolicyDocument
parameter_list|(
name|DocumentImpl
name|policyDoc
parameter_list|)
throws|throws
name|ParsingException
block|{
name|String
name|name
init|=
name|policyDoc
operator|.
name|getName
argument_list|()
decl_stmt|;
name|AbstractPolicy
name|policy
init|=
operator|(
name|AbstractPolicy
operator|)
name|POLICY_CACHE
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|policy
operator|==
literal|null
condition|)
block|{
name|policy
operator|=
name|parsePolicyDocument
argument_list|(
name|policyDoc
argument_list|)
expr_stmt|;
name|POLICY_CACHE
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
return|return
name|policy
return|;
block|}
comment|/** 	* Parses a DOM representation of a policy document into an 	*<code>AbstractPolicy</code>. 	* 	* @param policyDoc The DOM<code>Document</code> representing 	*	the XACML policy or policy set. 	* @return The parsed policy 	* @throws ParsingException if there is an error parsing the document 	*/
specifier|public
name|AbstractPolicy
name|parsePolicyDocument
parameter_list|(
name|Document
name|policyDoc
parameter_list|)
throws|throws
name|ParsingException
block|{
name|Element
name|root
init|=
name|policyDoc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|root
operator|.
name|getTagName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|POLICY_SET_ELEMENT_LOCAL_NAME
argument_list|)
condition|)
return|return
name|PolicySet
operator|.
name|getInstance
argument_list|(
name|root
argument_list|,
name|pdp
operator|.
name|getPDPConfig
argument_list|()
operator|.
name|getPolicyFinder
argument_list|()
argument_list|)
return|;
if|else if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|POLICY_ELEMENT_LOCAL_NAME
argument_list|)
condition|)
return|return
name|Policy
operator|.
name|getInstance
argument_list|(
name|root
argument_list|)
return|;
else|else
throw|throw
operator|new
name|ParsingException
argument_list|(
literal|"The root element of the policy document must be '"
operator|+
name|XACMLConstants
operator|.
name|POLICY_SET_ID_LOCAL_NAME
operator|+
literal|"' or '"
operator|+
name|XACMLConstants
operator|.
name|POLICY_SET_ID_LOCAL_NAME
operator|+
literal|"', was: '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|/** 	 * Escapes characters that are not allowed in various places 	 * in XML by replacing all invalid characters with 	 *<code>getEscape(c)</code>. 	 *  	 * @param buffer The<code>StringBuffer</code> containing 	 * the text to escape in place. 	 */
specifier|public
specifier|static
name|void
name|XMLEscape
parameter_list|(
name|StringBuffer
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
return|return;
name|char
name|c
decl_stmt|;
name|String
name|escape
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
name|buffer
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
name|c
operator|=
name|buffer
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|escape
operator|=
name|getEscape
argument_list|(
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
name|escape
operator|==
literal|null
condition|)
name|i
operator|++
expr_stmt|;
else|else
block|{
name|buffer
operator|.
name|replace
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|escape
argument_list|)
expr_stmt|;
name|i
operator|+=
name|escape
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Escapes characters that are not allowed in various 	 * places in XML.  Characters are replaced by the 	 * corresponding entity.  The characters&amp;,&lt;, 	 *&gt;,&quot;, and&apos; are escaped. 	 *  	 * @param c The character to escape. 	 * @return A<code>String</code> representing the 	 * 	escaped character or null if the character does 	 *  not need to be escaped. 	 */
specifier|public
specifier|static
name|String
name|getEscape
parameter_list|(
name|char
name|c
parameter_list|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'&'
case|:
return|return
literal|"&amp;"
return|;
case|case
literal|'<'
case|:
return|return
literal|"&lt;"
return|;
case|case
literal|'>'
case|:
return|return
literal|"&gt;"
return|;
case|case
literal|'\"'
case|:
return|return
literal|"&quot;"
return|;
case|case
literal|'\''
case|:
return|return
literal|"&apos;"
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
comment|/** 	 * Escapes characters that are not allowed in various places 	 * in XML by replacing all invalid characters with 	 *<code>getEscape(c)</code>. 	 *  	 * @param in The<code>String</code> containing 	 * the text to escape in place. 	 */
specifier|public
specifier|static
name|String
name|XMLEscape
parameter_list|(
name|String
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|StringBuffer
name|temp
init|=
operator|new
name|StringBuffer
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|XMLEscape
argument_list|(
name|temp
argument_list|)
expr_stmt|;
return|return
name|temp
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Serializes the specified<code>PolicyTreeElement</code> to a 	 *<code>String</code> as XML.  The XML is indented if indent 	 * is true. 	 *  	 * @param element The<code>PolicyTreeElement</code> to serialize 	 * @param indent If the XML should be indented 	 * @return The XML representation of the element 	 */
specifier|public
specifier|static
name|String
name|serialize
parameter_list|(
name|PolicyTreeElement
name|element
parameter_list|,
name|boolean
name|indent
parameter_list|)
block|{
if|if
condition|(
name|element
operator|==
literal|null
condition|)
return|return
literal|""
return|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|indent
condition|)
name|element
operator|.
name|encode
argument_list|(
name|out
argument_list|,
operator|new
name|Indenter
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|element
operator|.
name|encode
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Serializes the specified<code>Target</code> to a 	 *<code>String</code> as XML.  The XML is indented if indent 	 * is true. 	 *  	 * @param target The<code>Target</code> to serialize 	 * @param indent If the XML should be indented 	 * @return The XML representation of the target 	 */
specifier|public
specifier|static
name|String
name|serialize
parameter_list|(
name|Target
name|target
parameter_list|,
name|boolean
name|indent
parameter_list|)
block|{
if|if
condition|(
name|target
operator|==
literal|null
condition|)
return|return
literal|""
return|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|indent
condition|)
name|target
operator|.
name|encode
argument_list|(
name|out
argument_list|,
operator|new
name|Indenter
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|target
operator|.
name|encode
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Serializes the specified<code>Apply</code> to a 	 *<code>String</code> as XML.  The XML is indented if indent 	 * is true. 	 *  	 * @param apply The<code>Apply</code> to serialize 	 * @param indent If the XML should be indented 	 * @return The XML representation of the apply 	 */
specifier|public
specifier|static
name|String
name|serialize
parameter_list|(
name|Apply
name|apply
parameter_list|,
name|boolean
name|indent
parameter_list|)
block|{
if|if
condition|(
name|apply
operator|==
literal|null
condition|)
return|return
literal|""
return|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|indent
condition|)
name|apply
operator|.
name|encode
argument_list|(
name|out
argument_list|,
operator|new
name|Indenter
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|apply
operator|.
name|encode
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Stores the default policies 	 *  	 * @param broker The broker with which to access the database 	 */
specifier|public
specifier|static
name|void
name|storeDefaultPolicies
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Storing default XACML policies"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|samplePolicyDocs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|String
name|docPath
init|=
name|samplePolicyDocs
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|storePolicy
argument_list|(
name|broker
argument_list|,
name|docPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"IO Error storing default policy '"
operator|+
name|docPath
operator|+
literal|"'"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"IO Error storing default policy '"
operator|+
name|docPath
operator|+
literal|"'"
argument_list|,
name|ee
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Stores the resource at docPath into the policies collection. 	 *  	 * @param broker The broker with which to access the database 	 * @param docPath The location of the resource 	 * @throws EXistException 	 */
specifier|public
specifier|static
name|void
name|storePolicy
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|docPath
parameter_list|)
throws|throws
name|EXistException
throws|,
name|IOException
block|{
name|String
name|docName
init|=
name|docPath
decl_stmt|;
name|int
name|lastSlash
init|=
name|docName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastSlash
operator|>=
literal|0
condition|)
name|docName
operator|=
name|docName
operator|.
name|substring
argument_list|(
name|lastSlash
operator|+
literal|1
argument_list|)
expr_stmt|;
name|URL
name|url
init|=
name|XACMLUtil
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|docPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
return|return;
name|String
name|content
init|=
name|toString
argument_list|(
name|url
operator|.
name|openStream
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|content
operator|==
literal|null
condition|)
return|return;
name|Collection
name|collection
init|=
name|getPolicyCollection
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
return|return;
name|TransactionManager
name|transact
init|=
name|broker
operator|.
name|getBrokerPool
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
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|docName
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|content
argument_list|,
literal|false
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
catch|catch
parameter_list|(
name|Exception
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
name|e
operator|instanceof
name|EXistException
condition|)
throw|throw
operator|(
name|EXistException
operator|)
name|e
throw|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Reads an<code>InputStream</code> into a string. 	 * @param in The stream to read into a string. 	 * @return The stream as a string 	 * @throws IOException 	 */
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|100
index|]
decl_stmt|;
name|CharArrayWriter
name|writer
init|=
operator|new
name|CharArrayWriter
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|int
name|read
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
name|writer
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

