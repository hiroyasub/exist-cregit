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
name|security
operator|.
name|xacml
operator|.
name|XACMLConstants
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

begin_class
specifier|public
class|class
name|ResourceCategoryAttributeHandler
implements|implements
name|AttributeHandler
block|{
specifier|public
name|void
name|filterFunctions
parameter_list|(
name|Set
argument_list|<
name|Object
argument_list|>
name|functions
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
block|{
specifier|final
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
name|RESOURCE_CATEGORY_ATTRIBUTE
argument_list|)
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|retain
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|retain
operator|.
name|add
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|retain
operator|.
name|add
argument_list|(
literal|"equals"
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
argument_list|<
name|Object
argument_list|>
name|values
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
block|{
specifier|final
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
name|RESOURCE_CATEGORY_ATTRIBUTE
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|MAIN_MODULE_RESOURCE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|FUNCTION_RESOURCE
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|XACMLConstants
operator|.
name|METHOD_RESOURCE
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

