begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|securitymanager
package|;
end_package

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
name|Account
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
name|PermissionDeniedException
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XQueryContext
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceType
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
name|StringValue
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
name|Type
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
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|GroupMembershipFunctions
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|qnGetGroupManagers
init|=
operator|new
name|QName
argument_list|(
literal|"get-group-managers"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnGetGroupMembers
init|=
operator|new
name|QName
argument_list|(
literal|"get-group-members"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
name|qnGetGroupManagers
argument_list|,
literal|"Gets a list of the group managers. Can only be called by a group manager."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"group"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The group name to retrieve the list of managers for."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"The list of group managers for the group $group"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
name|qnGetGroupMembers
argument_list|,
literal|"Gets a list of the group members."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"group"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The group name to retrieve the list of members for."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"The list of group members for the group $group"
argument_list|)
argument_list|)
block|,     }
decl_stmt|;
specifier|public
name|GroupMembershipFunctions
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|String
name|groupName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|SecurityManager
name|manager
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnGetGroupManagers
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|Group
name|group
init|=
name|manager
operator|.
name|getGroup
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
name|ValueSequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
name|groupManager
range|:
name|group
operator|.
name|getManagers
argument_list|()
control|)
block|{
name|seq
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|groupManager
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|seq
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnGetGroupMembers
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groupMembers
init|=
name|manager
operator|.
name|findAllGroupMembers
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
name|ValueSequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|groupMember
range|:
name|groupMembers
control|)
block|{
name|seq
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|groupMember
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|seq
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|pde
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

