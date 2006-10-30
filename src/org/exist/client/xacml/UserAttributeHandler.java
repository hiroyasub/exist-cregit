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
name|ArrayList
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
name|User
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
name|xmldb
operator|.
name|UserManagementService
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
name|XMLDBException
import|;
end_import

begin_class
specifier|public
class|class
name|UserAttributeHandler
implements|implements
name|AttributeHandler
block|{
specifier|private
name|Collection
name|collection
decl_stmt|;
specifier|private
name|UserAttributeHandler
parameter_list|()
block|{
block|}
specifier|public
name|UserAttributeHandler
parameter_list|(
name|DatabaseInterface
name|dbInterface
parameter_list|)
block|{
if|if
condition|(
name|dbInterface
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Database interface cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|collection
operator|=
name|dbInterface
operator|.
name|getPolicyCollection
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|filterFunctions
parameter_list|(
name|Set
name|functions
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
block|{
name|URI
name|id
init|=
name|attribute
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|SUBJECT_ID_ATTRIBUTE
argument_list|)
operator|||
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|USER_NAME_ATTRIBUTE
argument_list|)
operator|||
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|GROUP_ATTRIBUTE
argument_list|)
operator|||
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|SUBJECT_NS_ATTRIBUTE
argument_list|)
condition|)
block|{
name|List
name|retain
init|=
operator|new
name|ArrayList
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|retain
operator|.
name|add
argument_list|(
literal|"equals"
argument_list|)
expr_stmt|;
name|retain
operator|.
name|add
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|functions
operator|.
name|retainAll
argument_list|(
name|retain
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|getAllowedValues
parameter_list|(
name|Set
name|values
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
block|{
name|URI
name|id
init|=
name|attribute
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|SUBJECT_ID_ATTRIBUTE
argument_list|)
condition|)
block|{
name|User
index|[]
name|users
init|=
name|getUsers
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
name|users
operator|.
name|length
condition|;
operator|++
name|i
control|)
name|values
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|users
index|[
name|i
index|]
operator|.
name|getUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|USER_NAME_ATTRIBUTE
argument_list|)
condition|)
block|{
name|User
index|[]
name|users
init|=
name|getUsers
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
name|users
operator|.
name|length
condition|;
operator|++
name|i
control|)
name|values
operator|.
name|add
argument_list|(
name|users
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|GROUP_ATTRIBUTE
argument_list|)
condition|)
block|{
name|String
index|[]
name|groupNames
init|=
name|getGroups
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
name|groupNames
operator|.
name|length
condition|;
operator|++
name|i
control|)
name|values
operator|.
name|add
argument_list|(
name|groupNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|XACMLConstants
operator|.
name|SUBJECT_NS_ATTRIBUTE
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|SUBJECT_NS
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|User
index|[]
name|getUsers
parameter_list|()
block|{
name|UserManagementService
name|service
init|=
name|getUserService
argument_list|()
decl_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
return|return
operator|new
name|User
index|[
literal|0
index|]
return|;
try|try
block|{
return|return
name|service
operator|.
name|getUsers
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"Could not get list of users: user attributes will be invalid"
argument_list|,
name|xe
argument_list|)
expr_stmt|;
return|return
operator|new
name|User
index|[
literal|0
index|]
return|;
block|}
block|}
specifier|private
name|String
index|[]
name|getGroups
parameter_list|()
block|{
name|UserManagementService
name|service
init|=
name|getUserService
argument_list|()
decl_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
try|try
block|{
return|return
name|service
operator|.
name|getGroups
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"Could not get list of groups: group attributes will be invalid"
argument_list|,
name|xe
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
block|}
specifier|private
name|UserManagementService
name|getUserService
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|UserManagementService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"Could not get user management service: user and group attributes will be invalid."
argument_list|,
name|xe
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|void
name|checkUserValue
parameter_list|(
name|AttributeValue
name|value
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
throws|throws
name|ParsingException
block|{
comment|//user is not allowed to edit any of the handled attributes, so this
comment|//method will not be called for those attributes
block|}
block|}
end_class

end_unit

