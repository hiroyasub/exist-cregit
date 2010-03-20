begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * HttpRequestMIMEMessage.java  *  * Created on June 16, 2006, 12:09 PM  *  * (C) R. Alexander Milowski alex@milowski.com  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|IncomingMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  *   * @author R. Alexander Milowski  */
end_comment

begin_class
specifier|public
class|class
name|HttpRequestMessage
implements|implements
name|IncomingMessage
block|{
name|String
name|path
decl_stmt|;
name|String
name|base
decl_stmt|;
name|HttpServletRequest
name|request
decl_stmt|;
comment|/** Creates a new instance of HttpRequestMIMEMessage */
specifier|public
name|HttpRequestMessage
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|base
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
specifier|public
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|request
operator|.
name|getMethod
argument_list|()
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|getParameter
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|request
operator|.
name|getHeader
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|int
name|getContentLength
parameter_list|()
block|{
return|return
name|request
operator|.
name|getContentLength
argument_list|()
return|;
block|}
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|request
operator|.
name|getInputStream
argument_list|()
return|;
block|}
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|request
operator|.
name|getReader
argument_list|()
return|;
block|}
specifier|public
name|String
name|getModuleBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
specifier|public
name|HttpServletRequest
name|getRequest
parameter_list|()
block|{
return|return
name|request
return|;
block|}
block|}
end_class

end_unit

