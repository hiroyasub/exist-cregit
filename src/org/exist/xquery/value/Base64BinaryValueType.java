begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|Base64OutputStream
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
name|XPathException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|Base64BinaryValueType
extends|extends
name|BinaryValueType
argument_list|<
name|Base64OutputStream
argument_list|>
block|{
comment|//private final static Pattern base64Pattern = Pattern.compile("^(?:[A-Za-z0-9+/\\s]{4})*(?:[A-Za-z0-9+/\\s]{2}==|[A-Za-z0-9+/\\s]{3}=)?$");
comment|//private final static Pattern base64Pattern = Pattern.compile("^((?:(?:\\s*[A-Za-z0-9+/]){4})*(?:(?:\\s*[A-Za-z0-9+/]){2}\\s*=\\s*=|(?:\\s*[A-Za-z0-9+/]){3}\\s*=)?)$");
comment|//private final static Pattern base64Pattern = Pattern.compile("^((?:(?:\\s*[A-Za-z0-9+/]){4})*(?:(?:\\s*[A-Za-z0-9+/]){1}(?:\\s*[AQgw]){1}\\s*=\\s*=|(?:\\s*[A-Za-z0-9+/]){3}\\s*=)?)$");
specifier|private
specifier|final
specifier|static
name|Pattern
name|base64Pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^((?>(?>\\s*[A-Za-z0-9+/]){4})*(?>(?>\\s*[A-Za-z0-9+/]){1}(?>\\s*[AQgw]){1}\\s*=\\s*=|(?>\\s*[A-Za-z0-9+/]){3}\\s*=)?)$"
argument_list|)
decl_stmt|;
specifier|public
name|Base64BinaryValueType
parameter_list|()
block|{
name|super
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Base64OutputStream
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Matcher
name|getMatcher
parameter_list|(
specifier|final
name|String
name|toMatch
parameter_list|)
block|{
return|return
name|base64Pattern
operator|.
name|matcher
argument_list|(
name|toMatch
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|verifyString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|getMatcher
argument_list|(
name|str
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: Invalid base64 data"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|String
name|formatString
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
return|;
block|}
block|}
end_class

end_unit

