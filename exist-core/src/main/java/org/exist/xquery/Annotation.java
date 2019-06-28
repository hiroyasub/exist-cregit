begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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

begin_comment
comment|/**  * Represents an XQuery 3.0 Annotation  *  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|Annotation
block|{
specifier|private
specifier|final
name|QName
name|name
decl_stmt|;
specifier|private
specifier|final
name|LiteralValue
name|value
index|[]
decl_stmt|;
specifier|private
specifier|final
name|FunctionSignature
name|signature
decl_stmt|;
specifier|public
name|Annotation
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|new
name|LiteralValue
index|[
literal|0
index|]
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Annotation
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|,
specifier|final
name|LiteralValue
index|[]
name|value
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|signature
operator|=
name|signature
expr_stmt|;
block|}
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|LiteralValue
index|[]
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * Get the signature of the function on which this      * annotation was placed      */
specifier|public
name|FunctionSignature
name|getFunctionSignature
parameter_list|()
block|{
return|return
name|signature
return|;
block|}
block|}
end_class

end_unit

