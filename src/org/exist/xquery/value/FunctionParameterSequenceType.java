begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_comment
comment|/**  * This class is used to specify the name and description of an XQuery function parameter.  * @author lcahlander  * @version 1.3  *  */
end_comment

begin_class
specifier|public
class|class
name|FunctionParameterSequenceType
extends|extends
name|FunctionReturnSequenceType
block|{
specifier|private
name|String
name|attributeName
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param attributeName	The name of the parameter in the<strong>FunctionSignature</strong>. 	 * @param primaryType	The<strong>Type</strong> of the parameter. 	 * @param cardinality	The<strong>Cardinality</strong> of the parameter. 	 * @param description	A description of the parameter in the<strong>FunctionSignature</strong>. 	 * @see org.exist.xquery.FunctionSignature @see Type @see org.exist.xquery.Cardinality 	 */
specifier|public
name|FunctionParameterSequenceType
parameter_list|(
name|String
name|attributeName
parameter_list|,
name|int
name|primaryType
parameter_list|,
name|int
name|cardinality
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|primaryType
argument_list|,
name|cardinality
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|attributeName
operator|=
name|attributeName
expr_stmt|;
block|}
comment|/** 	 * @return the attributeName 	 */
specifier|public
name|String
name|getAttributeName
parameter_list|()
block|{
return|return
name|attributeName
return|;
block|}
comment|/** 	 * @param attributeName the attributeName to set 	 */
specifier|public
name|void
name|setAttributeName
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
name|this
operator|.
name|attributeName
operator|=
name|attributeName
expr_stmt|;
block|}
block|}
end_class

end_unit

