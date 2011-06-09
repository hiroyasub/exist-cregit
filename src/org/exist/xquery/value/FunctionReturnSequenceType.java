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
name|FunctionReturnSequenceType
extends|extends
name|SequenceType
block|{
specifier|private
name|String
name|description
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param primaryType	The<strong>Type</strong> of the parameter. 	 * @param cardinality	The<strong>Cardinality</strong> of the parameter. 	 * @param description	A description of the parameter in the<strong>FunctionSignature</strong>. 	 * @see org.exist.xquery.FunctionSignature @see Type @see org.exist.xquery.Cardinality 	 */
specifier|public
name|FunctionReturnSequenceType
parameter_list|(
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
specifier|public
name|FunctionReturnSequenceType
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @return the description 	 */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/** 	 * @param description the description to set 	 */
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
block|}
end_class

end_unit

