begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|sleepy
operator|.
name|annotations
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Annotation
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractRESTAnnotation
implements|implements
name|RESTAnnotation
block|{
specifier|private
specifier|final
name|Annotation
name|annotation
decl_stmt|;
specifier|protected
name|AbstractRESTAnnotation
parameter_list|(
name|Annotation
name|annotation
parameter_list|)
block|{
name|this
operator|.
name|annotation
operator|=
name|annotation
expr_stmt|;
block|}
block|}
end_class

end_unit

