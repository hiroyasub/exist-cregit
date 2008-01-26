begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * OutgoingMIMEMessage.java  *  * Created on June 14, 2006, 11:55 PM  *  * (C) R. Alexander Milowski alex@milowski.com  */
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
name|HttpServletResponse
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_comment
comment|/**  *  * @author R. Alexander Milowski  */
end_comment

begin_interface
specifier|public
interface|interface
name|OutgoingMessage
block|{
name|void
name|setStatusCode
parameter_list|(
name|int
name|code
parameter_list|)
function_decl|;
name|void
name|setContentType
parameter_list|(
name|String
name|mimeType
parameter_list|)
function_decl|;
name|void
name|setHeader
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|Writer
name|getWriter
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|HttpServletResponse
name|getResponse
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

