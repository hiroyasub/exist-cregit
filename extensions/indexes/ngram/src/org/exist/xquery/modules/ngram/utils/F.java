begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|ngram
operator|.
name|utils
package|;
end_package

begin_comment
comment|/**  * A transformation or function from<code>A</code> to<code>B</code>.  */
end_comment

begin_interface
specifier|public
interface|interface
name|F
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
block|{
comment|/**      * Transform<code>A</code> to<code>B</code>.      *       * @param a      *            The<code>A</code> to transform.      * @return The result of the transformation.      */
name|B
name|f
parameter_list|(
name|A
name|a
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

