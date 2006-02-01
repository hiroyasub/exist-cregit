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
name|PDP
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
name|PDPConfig
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
name|RequestCtx
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
name|ResponseCtx
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
name|Result
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
name|AttributeFinder
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
name|ResourceFinder
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
name|impl
operator|.
name|CurrentEnvModule
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
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/** * This class is responsible for creating the XACML Policy Decision Point (PDP) * for a database instance.  The PDP is the entity that accepts access requests * and makes a decision whether the access is allowed.  The PDP returns a decision * to the requesting entity (called a Policy Enforcement Point, or PEP).  This * decision is either Permit, Deny, Indeterminate, or Not Applicable.  Not * Applicable occurs if no policy could be found that applied to the request. * Indeterminate occurs if there was an error processing the request or the * request was invalid. *<p> * This class also provides convenience methods for most uses.  The main method * is<code>evaluate</code>, which will throw a *<code>PermissionDeniedException</code> unless the decision was Permit and no * Obligations were required.  An Obligation is a conditional access decision. * If the PEP cannot perform the Obligation, then it cannot accept the decision. *<p> *<code>RequestHelper</code> provides methods for creating a *<code>RequestCtx</code>, which is then passed to the<code>PDP</code> either * indirectly by calling<code>evaluate</code> or directly by calling *<code>getPDP().evaluate()</code>.  The first method can probably be used in * most cases, while the second one allows more flexibility in handling the * response. * * @see XACMLConstants * @see ExistPolicyModule * @see RequestHelper */
end_comment

begin_class
specifier|public
class|class
name|ExistPDP
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
name|ExistPDP
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|PDPConfig
name|pdpConfig
decl_stmt|;
comment|//the per database instance util object
specifier|private
name|XACMLUtil
name|util
decl_stmt|;
comment|//the PDP object that actually evaluates requests
specifier|private
name|PDP
name|pdp
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
comment|/** 	 * Assists client in creating<code>RequestCtx</code>s. 	 */
specifier|private
name|RequestHelper
name|helper
init|=
operator|new
name|RequestHelper
argument_list|()
decl_stmt|;
static|static
block|{
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"com.sun.xacml"
argument_list|)
operator|.
name|setLevel
argument_list|(
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
operator|.
name|WARNING
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ExistPDP
parameter_list|()
block|{
block|}
comment|/** 	* @param pool A<code>BrokerPool</code> used to obtain an instance 	* of a DBBroker in order to read policies from the database. 	*/
specifier|public
name|ExistPDP
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
if|if
condition|(
name|pool
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
name|pool
operator|=
name|pool
expr_stmt|;
name|util
operator|=
operator|new
name|XACMLUtil
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|pdpConfig
operator|=
operator|new
name|PDPConfig
argument_list|(
name|createAttributeFinder
argument_list|()
argument_list|,
name|createPolicyFinder
argument_list|()
argument_list|,
name|createResourceFinder
argument_list|()
argument_list|)
expr_stmt|;
name|pdp
operator|=
operator|new
name|PDP
argument_list|(
name|pdpConfig
argument_list|)
expr_stmt|;
block|}
comment|/** 	* Returns the<code>PDPConfig</code> used to initialize the 	* underlying<code>PDP</code>. 	* 	* @return the<code>PDPConfig</code> 	*/
specifier|public
name|PDPConfig
name|getPDPConfig
parameter_list|()
block|{
return|return
name|pdpConfig
return|;
block|}
comment|/** 	 * Obtains the<code>BrokerPool</code> with which this instance 	 * is associated. 	 *  	 * @return This instance's associated<code>BrokerPool</code> 	 */
specifier|public
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
comment|/** 	 * Obtains the XACML utility instance for this database instance. 	 *   	 * @return the associated XACML utility object 	 */
specifier|public
name|XACMLUtil
name|getUtil
parameter_list|()
block|{
return|return
name|util
return|;
block|}
comment|/** 	* Performs any necessary cleanup operations.  Generally only 	* called if XACML has been disabled. 	*/
specifier|public
name|void
name|close
parameter_list|()
block|{
name|util
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** 	* The method that will be used most of the time.  It provides the 	* simplest interface to the underlying<code>PDP</code> by 	* permitting the request only if the<code>ResponseCtx</code> 	* includes<code>Result</code>s that have no<code>Obligation</code>s 	* and only have the decision<code>Permit</code>.  Other cases 	* result in a<code>PermissionDeniedException</code>.  The other cases 	* include when an applicable policy cannot be found and when an error 	* occurs. 	* 	* @param request the access request 	* @throws PermissionDeniedException if the request is not allowed 	*/
specifier|public
name|void
name|evaluate
parameter_list|(
name|RequestCtx
name|request
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|ResponseCtx
name|response
init|=
name|pdp
operator|.
name|evaluate
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/** 	* This method handles a<code>ResponseCtx</code> generated by a 	*<code>PDP</code> request by doing nothing if the<code>ResponseCtx</code> 	* includes<code>Result</code>s that have no<code>Obligation</code>s 	* and only have the decision<code>Permit</code>.  Other cases 	* result in a<code>PermissionDeniedException</code>.  The other cases 	* include the Deny, Indeterminate, and Not Applicable decisions. 	* 	* @param response the<code>PDP</code> response to an access request 	* @throws PermissionDeniedException if the response does not have a decsion 	*		of Permit or it has any<code>Obligation</code>s. 	*/
specifier|public
name|void
name|handleResponse
parameter_list|(
name|ResponseCtx
name|response
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|response
operator|==
literal|null
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"The response was null"
argument_list|)
throw|;
name|Set
name|results
init|=
name|response
operator|.
name|getResults
argument_list|()
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
operator|||
name|results
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"The response was empty"
argument_list|)
throw|;
for|for
control|(
name|Iterator
name|it
init|=
name|results
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
name|handleResult
argument_list|(
operator|(
name|Result
operator|)
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	* This method handles a single<code>Result</code> generated by a 	*<code>PDP</code> request by doing nothing if the<code>Result</code> 	* has no<code>Obligation</code>s and only has the decision 	*<code>Permit</code>.  Other cases result in a 	*<code>PermissionDeniedException</code>. The other cases include a 	* decision of Deny, Indeterminate, or Not Applicable. 	* 	* @param result a<code>Result</code> in a<code>ResponseCtx</code> 	*		generated by a<code>PDP</code> in response to an access request 	* @throws PermissionDeniedException if the result does not have a decsion 	*		of Permit or it has any<code>Obligation</code>s. 	*/
specifier|public
name|void
name|handleResult
parameter_list|(
name|Result
name|result
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"A result of a request's response was null"
argument_list|)
throw|;
name|Set
name|obligations
init|=
name|result
operator|.
name|getObligations
argument_list|()
decl_stmt|;
if|if
condition|(
name|obligations
operator|!=
literal|null
operator|&&
name|obligations
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"The XACML response had obligations that could not be fulfilled."
argument_list|)
throw|;
block|}
name|int
name|decision
init|=
name|result
operator|.
name|getDecision
argument_list|()
decl_stmt|;
if|if
condition|(
name|decision
operator|==
name|Result
operator|.
name|DECISION_PERMIT
condition|)
return|return;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"The response did not permit the request.  The decision was: "
operator|+
name|getDecisionString
argument_list|(
name|decision
argument_list|,
name|result
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|//only really intended to be used by handleResult
specifier|private
specifier|static
name|String
name|getDecisionString
parameter_list|(
specifier|final
name|int
name|decision
parameter_list|,
specifier|final
name|Status
name|status
parameter_list|)
block|{
switch|switch
condition|(
name|decision
condition|)
block|{
case|case
name|Result
operator|.
name|DECISION_PERMIT
case|:
return|return
literal|"permit the request"
return|;
case|case
name|Result
operator|.
name|DECISION_DENY
case|:
return|return
literal|"deny the request"
return|;
case|case
name|Result
operator|.
name|DECISION_INDETERMINATE
case|:
name|String
name|error
init|=
operator|(
name|status
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|status
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|error
operator|==
literal|null
condition|)
name|error
operator|=
literal|""
expr_stmt|;
if|else if
condition|(
name|error
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|error
operator|=
literal|": "
operator|+
name|error
expr_stmt|;
return|return
literal|"indeterminate (there was an error)"
operator|+
name|error
return|;
case|case
name|Result
operator|.
name|DECISION_NOT_APPLICABLE
case|:
return|return
literal|"the request was not applicable to the policy"
return|;
default|default:
return|return
literal|": of an unknown type"
return|;
block|}
block|}
comment|/** For use when<code>evaluate</code> is not flexible enough.  That is, 	* use this method when you want direct access to the<code>PDP</code>. 	* This allows you to use an<code>EvaluationCtx</code> instead of a 	*<code>RequestCtx</code> and direct access to the ResponseCtx to allow 	* for handling of<code>Obligation</code>s or decisions other than Permit. 	*<p> 	* The basic usage is then: 	*<p> 	*<code>ResponseCtx response = getPDP().evaluate(RequestCtx ctx)</code> 	*<p> 	* or 	*<p> 	*<code>ResponseCtx response = getPDP().evaluate(EvaluationCtx ctx)</code> 	*<p> 	* The response should then be checked for<code>Obligation</code>s and 	* the<code>PDP</code>'s decision. 	* 	* @return the actual<code>PDP</code> wrapped by this class 	*/
specifier|public
name|PDP
name|getPDP
parameter_list|()
block|{
return|return
name|pdp
return|;
block|}
comment|/** 	 * Gets a<code>RequestHelper</code> 	 *  	 * @return The<code>RequestHelper</code> for this database instance 	 */
specifier|public
name|RequestHelper
name|getRequestHelper
parameter_list|()
block|{
return|return
name|helper
return|;
block|}
comment|/** 	* Creates a<code>ResourceFinder</code> that is used by the 	*<code>PDP</code> to locate hierarchical resources.  Hierarchical resources 	* are not currently supported (org.exist.security.xacml, not sunxacml) so 	* this method returns null. 	* 	* @return A<code>ResourceFinder</code> for hierarchical resources. 	*/
specifier|private
name|ResourceFinder
name|createResourceFinder
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	* Creates an<code>AttributeFinder</code> that is used by the 	*<code>PDP</code> to locate attributes required by a policy but unspecified 	* by the request context.  The XACML specification requires that certain 	* attributes of the environment always be available, so the CurrentEnvModule 	* is a provided<code>AttributeFinderModule</code> in the returned 	*<code>AttributeFinder</code>.  The other module looks up attributes 	* for a<code>User</code>.  This module,<code>UserAttributeModule</code>, 	* finds the user's name and the user's groups from the subject-id (which is 	* the uid of the user). 	*  	* @return An<code>AttributeFinder</code> for unspecified attributes. 	*/
specifier|private
name|AttributeFinder
name|createAttributeFinder
parameter_list|()
block|{
name|List
name|modules
init|=
operator|new
name|ArrayList
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|UserAttributeModule
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|CurrentEnvModule
argument_list|()
argument_list|)
expr_stmt|;
name|AttributeFinder
name|attributeFinder
init|=
operator|new
name|AttributeFinder
argument_list|()
decl_stmt|;
name|attributeFinder
operator|.
name|setModules
argument_list|(
name|modules
argument_list|)
expr_stmt|;
return|return
name|attributeFinder
return|;
block|}
comment|/** 	* Creates a<code>PolicyFinder</code> that is used by the<code>PDP</code> 	* to locate<code>Policy</code>s for a given request or to resolve policy 	* references.  The returned<code>PolicyFinder</code> uses the 	*<code>ExistPolicyModule</code> for both resolving policy references and 	* finding the applicable policy for a given request. 	*  	* @return A<code>PolicyFinder</code> for unspecified attributes. 	*/
specifier|private
name|PolicyFinder
name|createPolicyFinder
parameter_list|()
block|{
name|ExistPolicyModule
name|policyModule
init|=
operator|new
name|ExistPolicyModule
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|PolicyFinder
name|policyFinder
init|=
operator|new
name|PolicyFinder
argument_list|()
decl_stmt|;
name|policyFinder
operator|.
name|setModules
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|policyModule
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|policyFinder
return|;
block|}
block|}
end_class

end_unit

