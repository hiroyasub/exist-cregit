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
name|EvaluationCtx
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
name|MatchResult
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
name|PolicyFinder
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
name|PolicyFinderModule
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
name|net
operator|.
name|URI
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
name|Iterator
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
comment|/* *Added new constructor to AnyURIValue to accept a URI */
end_comment

begin_comment
comment|/** * This class finds Policy and PolicySet documents located in * the /db/system/policies collection.  It implements both of the *<code>findPolicy</code> methods of<code>PolicyFinderModule</code>. * Finding policies by reference uses a range index on PolicySetId * and PolicyId, so that must be set up for references to work. * Finding policies for a given request is not yet optimized, but * just loads all policies in the policies collection, parses them, * and determines if they match the request. * * @see XACMLConstants */
end_comment

begin_class
specifier|public
class|class
name|ExistPolicyModule
extends|extends
name|PolicyFinderModule
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
name|PolicyFinder
name|finder
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|ExistPolicyModule
parameter_list|()
block|{
block|}
comment|/** 	* Creates a new<code>ExistPolicyModule</code>.  Retains a reference 	* to the specified<code>BrokerPool</code>. 	* 	* @param pool The<code>BrokerPool</code> that will be used to 	*	access the database to find policies. 	*/
specifier|public
name|ExistPolicyModule
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
block|}
specifier|public
name|boolean
name|isRequestSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isIdReferenceSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|PolicyFinder
name|finder
parameter_list|)
block|{
name|this
operator|.
name|finder
operator|=
name|finder
expr_stmt|;
block|}
specifier|public
name|PolicyFinderResult
name|findPolicy
parameter_list|(
name|EvaluationCtx
name|context
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
return|return
name|findPolicy
argument_list|(
name|broker
argument_list|,
name|context
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
return|return
name|errorResult
argument_list|(
literal|"Error while finding policy: "
operator|+
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ee
argument_list|)
return|;
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
name|PolicyFinderResult
name|findPolicy
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|EvaluationCtx
name|context
parameter_list|)
block|{
name|DocumentSet
name|mainPolicyDocs
init|=
name|getPolicyDocuments
argument_list|(
name|broker
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|mainPolicyDocs
operator|==
literal|null
condition|)
return|return
operator|new
name|PolicyFinderResult
argument_list|()
return|;
name|AbstractPolicy
name|matchedPolicy
init|=
literal|null
decl_stmt|;
name|AbstractPolicy
name|policy
decl_stmt|;
name|MatchResult
name|match
decl_stmt|;
name|int
name|result
decl_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
name|it
init|=
name|mainPolicyDocs
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|policy
operator|=
name|parsePolicyDocument
argument_list|(
operator|(
name|Document
operator|)
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|match
operator|=
name|policy
operator|.
name|match
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|result
operator|=
name|match
operator|.
name|getResult
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|==
name|MatchResult
operator|.
name|INDETERMINATE
condition|)
block|{
return|return
operator|new
name|PolicyFinderResult
argument_list|(
name|match
operator|.
name|getStatus
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|result
operator|==
name|MatchResult
operator|.
name|MATCH
condition|)
block|{
if|if
condition|(
name|matchedPolicy
operator|==
literal|null
condition|)
name|matchedPolicy
operator|=
name|policy
expr_stmt|;
else|else
return|return
name|errorResult
argument_list|(
literal|"Matched multiple profiles for reqest"
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ParsingException
name|pe
parameter_list|)
block|{
return|return
name|errorResult
argument_list|(
literal|"Error retrieving policies: "
operator|+
name|pe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pe
argument_list|)
return|;
block|}
if|if
condition|(
name|matchedPolicy
operator|==
literal|null
condition|)
return|return
operator|new
name|PolicyFinderResult
argument_list|()
return|;
else|else
return|return
operator|new
name|PolicyFinderResult
argument_list|(
name|matchedPolicy
argument_list|)
return|;
block|}
specifier|public
name|PolicyFinderResult
name|findPolicy
parameter_list|(
name|URI
name|idReference
parameter_list|,
name|int
name|type
parameter_list|)
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
return|return
name|errorResult
argument_list|(
literal|"Invalid reference type: "
operator|+
name|type
argument_list|,
literal|null
argument_list|)
return|;
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
name|Document
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
operator|new
name|PolicyFinderResult
argument_list|()
return|;
name|AbstractPolicy
name|policy
init|=
name|parsePolicyDocument
argument_list|(
name|policyDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|policy
operator|==
literal|null
condition|)
return|return
operator|new
name|PolicyFinderResult
argument_list|()
return|;
return|return
operator|new
name|PolicyFinderResult
argument_list|(
name|policy
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|errorResult
argument_list|(
literal|"Error resolving "
operator|+
name|idAttributeQName
operator|.
name|getLocalName
argument_list|()
operator|+
literal|" '"
operator|+
name|idReference
operator|.
name|toString
argument_list|()
operator|+
literal|"': "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
return|;
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
comment|//gets documents in the policies collection
specifier|private
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
name|warn
argument_list|(
literal|"Policy collection '"
operator|+
name|XACMLConstants
operator|.
name|POLICY_COLLECTION
operator|+
literal|"' does not exist"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
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
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Policy collection contains no documents."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
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
comment|//resolves a reference to a policy document
specifier|private
name|Document
name|getPolicyDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|QName
name|idAttributeQName
parameter_list|,
name|URI
name|idReference
parameter_list|)
throws|throws
name|ProcessingException
throws|,
name|XPathException
block|{
if|if
condition|(
name|idReference
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
name|idReference
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
name|idAttributeQName
argument_list|,
name|comparison
argument_list|,
name|nodeSet
argument_list|)
decl_stmt|;
name|documentSet
operator|=
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
expr_stmt|;
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
name|idAttributeQName
operator|.
name|getLocalName
argument_list|()
operator|+
literal|" '"
operator|+
name|idReference
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
name|idAttributeQName
operator|.
name|getLocalName
argument_list|()
operator|+
literal|" '"
operator|+
name|idReference
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
specifier|private
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
specifier|private
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
comment|//parses a DOM representation of a policy document into an AbstractPolicy
specifier|private
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
name|finder
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
block|}
end_class

end_unit

