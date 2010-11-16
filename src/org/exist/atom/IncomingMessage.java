begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * IncomingMimeMessage.java  *  * Created on June 14, 2006, 11:55 PM  *  * (C) R. Alexander Milowski alex@milowski.com  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
package|;
end_package

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
comment|/**  *  * @author R. Alexander Milowski  */
end_comment

begin_interface
specifier|public
interface|interface
name|IncomingMessage
block|{
name|String
name|getMethod
parameter_list|()
function_decl|;
name|String
name|getPath
parameter_list|()
function_decl|;
name|String
name|getHeader
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|long
name|getContentLength
parameter_list|()
function_decl|;
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|String
name|getModuleBase
parameter_list|()
function_decl|;
name|HttpServletRequest
name|getRequest
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

