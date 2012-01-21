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
name|http
operator|.
name|sleepy
operator|.
name|annotations
operator|.
name|RESTAnnotation
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
name|XmldbURI
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
name|UserDefinedFunction
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|RESTfulXQueryServiceRegistry
block|{
specifier|private
specifier|static
name|RESTfulXQueryServiceRegistry
name|instance
init|=
operator|new
name|RESTfulXQueryServiceRegistry
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|RESTfulXQueryServiceRegistry
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
specifier|public
name|void
name|register
parameter_list|(
name|XmldbURI
name|moduleUri
parameter_list|,
name|UserDefinedFunction
name|function
parameter_list|,
name|List
argument_list|<
name|RESTAnnotation
argument_list|>
name|functionRestAnnotations
parameter_list|)
block|{
comment|//TODO synchronize accces to the underlying registry
comment|//replace any entries for this module and function with the new annotations
block|}
block|}
end_class

end_unit

