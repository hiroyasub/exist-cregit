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
name|attr
operator|.
name|AttributeDesignator
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
name|attr
operator|.
name|AttributeValue
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
name|attr
operator|.
name|BagAttribute
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
name|attr
operator|.
name|StringAttribute
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
name|EvaluationResult
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
name|AttributeFinderModule
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
name|HashSet
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
name|User
import|;
end_import

begin_comment
comment|/** * This class looks up attributes for a Subject with a subject-category * of access-subject.  The currently supported attributes are * {@link XACMLConstants#USER_NAME_ATTRIBUTE user name} and * {@link XACMLConstants#GROUP_ATTRIBUTE groups}.  This is a possible * implementation point for LDAP lookup if this is desired * in the future. */
end_comment

begin_class
specifier|public
class|class
name|UserAttributeModule
extends|extends
name|AttributeFinderModule
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
name|UserAttributeModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ExistPDP
name|pdp
decl_stmt|;
specifier|private
name|UserAttributeModule
parameter_list|()
block|{
block|}
comment|/** 	* Creates an<code>AttributeFinderModule</code> capable of retrieving attributes 	* for a<code>User</code>. 	* 	* @param pdp The<code>ExistPDP</code> that is used to obtain information 	* about a given<code>User</code>. 	*/
specifier|public
name|UserAttributeModule
parameter_list|(
name|ExistPDP
name|pdp
parameter_list|)
block|{
name|this
operator|.
name|pdp
operator|=
name|pdp
expr_stmt|;
block|}
specifier|public
name|EvaluationResult
name|findAttribute
parameter_list|(
name|URI
name|attributeType
parameter_list|,
name|URI
name|attributeId
parameter_list|,
name|URI
name|issuer
parameter_list|,
name|URI
name|subjectCategory
parameter_list|,
name|EvaluationCtx
name|context
parameter_list|,
name|int
name|designatorType
parameter_list|)
block|{
if|if
condition|(
name|designatorType
operator|!=
name|AttributeDesignator
operator|.
name|SUBJECT_TARGET
condition|)
return|return
name|errorResult
argument_list|(
literal|"Invalid designator type: UserAttributeModule only handles subjects"
argument_list|)
return|;
if|if
condition|(
name|issuer
operator|!=
literal|null
condition|)
return|return
name|errorResult
argument_list|(
literal|"UserAttributeModule cannot handle requests with an issuer specified."
argument_list|)
return|;
if|if
condition|(
operator|!
name|XACMLConstants
operator|.
name|ACCESS_SUBJECT
operator|.
name|equals
argument_list|(
name|subjectCategory
argument_list|)
condition|)
return|return
name|errorResult
argument_list|(
literal|"UserAttributeModule can only handle subject category '"
operator|+
name|XACMLConstants
operator|.
name|ACCESS_SUBJECT
operator|+
literal|"'"
argument_list|)
return|;
if|if
condition|(
operator|!
name|XACMLConstants
operator|.
name|STRING_TYPE
operator|.
name|equals
argument_list|(
name|attributeType
argument_list|)
condition|)
return|return
name|errorResult
argument_list|(
literal|"UserAttributeModule can only handle data type '"
operator|+
name|XACMLConstants
operator|.
name|STRING_TYPE
operator|+
literal|"'"
argument_list|)
return|;
name|EvaluationResult
name|subjectID
init|=
name|context
operator|.
name|getSubjectAttribute
argument_list|(
name|attributeType
argument_list|,
name|XACMLConstants
operator|.
name|SUBJECT_ID_ATTRIBUTE
argument_list|,
name|issuer
argument_list|,
name|subjectCategory
argument_list|)
decl_stmt|;
if|if
condition|(
name|subjectID
operator|.
name|indeterminate
argument_list|()
condition|)
return|return
name|subjectID
return|;
name|AttributeValue
name|value
init|=
name|subjectID
operator|.
name|getAttributeValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
name|errorResult
argument_list|(
literal|"Could not find user for context: null subject-id"
argument_list|)
return|;
if|if
condition|(
name|value
operator|.
name|isBag
argument_list|()
condition|)
block|{
name|BagAttribute
name|bag
init|=
operator|(
name|BagAttribute
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|bag
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|errorResult
argument_list|(
literal|"Could not find user for context: no subject-id found"
argument_list|)
return|;
if|if
condition|(
name|bag
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
return|return
name|errorResult
argument_list|(
literal|"Error finding attribute: Subject-id attribute is not unique."
argument_list|)
return|;
name|value
operator|=
operator|(
name|AttributeValue
operator|)
name|bag
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|value
operator|instanceof
name|StringAttribute
operator|)
condition|)
return|return
name|errorResult
argument_list|(
literal|"Error finding attribute: Subject-id attribute must be a string."
argument_list|)
return|;
name|String
name|uid
init|=
operator|(
operator|(
name|StringAttribute
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|pdp
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|uid
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
return|return
name|errorResult
argument_list|(
literal|"No user exists for UID '"
operator|+
name|uid
operator|+
literal|"'"
argument_list|)
return|;
if|if
condition|(
name|XACMLConstants
operator|.
name|GROUP_ATTRIBUTE
operator|.
name|equals
argument_list|(
name|attributeId
argument_list|)
condition|)
return|return
name|getGroups
argument_list|(
name|user
argument_list|)
return|;
if|else if
condition|(
name|XACMLConstants
operator|.
name|USER_NAME_ATTRIBUTE
operator|.
name|equals
argument_list|(
name|attributeId
argument_list|)
condition|)
return|return
operator|new
name|EvaluationResult
argument_list|(
operator|new
name|StringAttribute
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
else|else
return|return
name|errorResult
argument_list|(
literal|"UserAttributeModule cannot handle attribute '"
operator|+
name|attributeId
operator|+
literal|"'"
argument_list|)
return|;
block|}
comment|//gets a bag consisting of the groups of the user
specifier|private
name|EvaluationResult
name|getGroups
parameter_list|(
name|User
name|user
parameter_list|)
block|{
name|String
index|[]
name|groupArray
init|=
name|user
operator|.
name|getGroups
argument_list|()
decl_stmt|;
name|int
name|size
init|=
operator|(
name|groupArray
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|groupArray
operator|.
name|length
decl_stmt|;
name|Set
name|groupAttributes
init|=
operator|new
name|HashSet
argument_list|(
name|size
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
name|size
condition|;
operator|++
name|i
control|)
name|groupAttributes
operator|.
name|add
argument_list|(
operator|new
name|StringAttribute
argument_list|(
name|groupArray
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|AttributeValue
name|value
init|=
operator|new
name|BagAttribute
argument_list|(
name|XACMLConstants
operator|.
name|STRING_TYPE
argument_list|,
name|groupAttributes
argument_list|)
decl_stmt|;
return|return
operator|new
name|EvaluationResult
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|//logs the specified message and exception
comment|//then, returns a result with status Indeterminate and the given message
specifier|private
specifier|static
name|EvaluationResult
name|errorResult
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|EvaluationResult
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
comment|/** 	* Indicates support of looking up attributes by 	* data supplied by an AttributeDesignator element, 	* specifically, a SubjectAttributeDesignator element. 	* 	* @return true to indicate that this module supports 	* this method of looking up attributes  	*/
specifier|public
name|boolean
name|isDesignatorSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** 	* Returns a<code>Set</code> containing 	*<code>AttributeDesignator.SUBJECT_TARGET</code> 	* to indicate that this module only supports 	*<code>Subject</code>s. 	* 	* @return A<code>Set</code> indicating the supported 	* designator type. 	*/
specifier|public
name|Set
name|getSupportedDesignatorTypes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|Integer
argument_list|(
name|AttributeDesignator
operator|.
name|SUBJECT_TARGET
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	* A<code>Set</code> containing the<code>URI</code>s 	* {@link XACMLConstants#USER_NAME_ATTRIBUTE user name} and 	* {@link XACMLConstants#GROUP_ATTRIBUTE groups} to indicate that 	* these are the only attributes supported by this module. 	* 	* @return A<code>Set</code> indicating the supported 	* attribute ids. 	*/
specifier|public
name|Set
name|getSupportedIds
parameter_list|()
block|{
name|Set
name|set
init|=
operator|new
name|HashSet
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|GROUP_ATTRIBUTE
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|USER_NAME_ATTRIBUTE
argument_list|)
expr_stmt|;
return|return
name|set
return|;
block|}
block|}
end_class

end_unit

