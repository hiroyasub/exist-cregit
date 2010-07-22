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
name|security
operator|.
name|internal
operator|.
name|SecurityManagerImpl
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
name|ExistPDP
name|pdp
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|ExistPolicyModule
parameter_list|()
block|{
block|}
comment|/** 	* Creates a new<code>ExistPolicyModule</code>.  Retains a reference 	* to the specified<code>BrokerPool</code>. 	* 	* @param pdp The<code>ExistPDP</code> for this database instance. 	*/
specifier|public
name|ExistPolicyModule
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
literal|"BrokerPool cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|pdp
operator|=
name|pdp
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
block|}
specifier|public
name|PolicyFinderResult
name|findPolicy
parameter_list|(
name|EvaluationCtx
name|context
parameter_list|)
block|{
name|BrokerPool
name|pool
init|=
name|pdp
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemAccount
argument_list|()
argument_list|)
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
name|XACMLUtil
operator|.
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
name|XACMLUtil
operator|.
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
name|XACMLUtil
name|util
init|=
name|pdp
operator|.
name|getUtil
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|it
init|=
name|mainPolicyDocs
operator|.
name|getDocumentIterator
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
name|util
operator|.
name|getPolicyDocument
argument_list|(
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
name|XACMLUtil
operator|.
name|errorResult
argument_list|(
literal|"Matched multiple policies for reqest"
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
name|XACMLUtil
operator|.
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
name|BrokerPool
name|pool
init|=
name|pdp
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemAccount
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractPolicy
name|policy
init|=
name|pdp
operator|.
name|getUtil
argument_list|()
operator|.
name|findPolicy
argument_list|(
name|broker
argument_list|,
name|idReference
argument_list|,
name|type
argument_list|)
decl_stmt|;
return|return
operator|(
name|policy
operator|==
literal|null
operator|)
condition|?
operator|new
name|PolicyFinderResult
argument_list|()
else|:
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
name|XACMLUtil
operator|.
name|errorResult
argument_list|(
literal|"Error resolving id '"
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
block|}
end_class

end_unit

