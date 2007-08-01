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
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * This interface defines methods for restricting  * the functions or values a user is allowed to  * enter for a given attribute.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AttributeHandler
block|{
comment|/** 	 * Removes functions that should not be available for the user to 	 * apply to an attribute. 	 *  	 * @param functions The<code>Set</code> of functions to modify in place. 	 * @param attribute The relevant attribute  	 */
name|void
name|filterFunctions
parameter_list|(
name|Set
name|functions
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
function_decl|;
comment|/** 	 * Determines which values the user may select for an attribute.  If the user 	 * may enter values not in this set, this function should return true. 	 *  	 * @param values The<code>Set</code> to which allowed values should be added. 	 * @param attribute The relevant attribute 	 * @return true if the user is not restricted to the values in the set, 	 * 	false otherwise  	 */
name|boolean
name|getAllowedValues
parameter_list|(
name|Set
name|values
parameter_list|,
name|AttributeDesignator
name|attribute
parameter_list|)
function_decl|;
comment|/** 	 * Determines if the user entered value is valid. 	 *   	 * @param value The value to check 	 * @param attribute The relevant attribute 	 * @throws ParsingException if the user entered value is invalid 	 */
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
function_decl|;
block|}
end_interface

end_unit

