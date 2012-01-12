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
comment|/**  * Represents an XQuery 3.0 Annotation  *  * @author Adam Retter<adam@exist-db.org>  */
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
specifier|public
name|Annotation
parameter_list|(
name|QName
name|name
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
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Annotation
parameter_list|(
name|QName
name|name
parameter_list|,
name|LiteralValue
index|[]
name|value
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
block|}
block|}
end_class

end_unit

