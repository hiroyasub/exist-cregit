begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|RequestWrapper
block|{
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
function_decl|;
specifier|public
name|int
name|getContentLength
parameter_list|()
function_decl|;
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
name|String
name|getContentType
parameter_list|()
function_decl|;
specifier|public
name|String
name|getContextPath
parameter_list|()
function_decl|;
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|Enumeration
name|getHeaderNames
parameter_list|()
function_decl|;
specifier|public
name|Enumeration
name|getHeaders
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|String
name|getMethod
parameter_list|()
function_decl|;
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|Enumeration
name|getParameterNames
parameter_list|()
function_decl|;
specifier|public
name|String
index|[]
name|getParameterValues
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|File
name|getFileUploadParam
parameter_list|(
name|String
name|parameter
parameter_list|)
function_decl|;
specifier|public
name|String
name|getUploadedFileName
parameter_list|(
name|String
name|parameter
parameter_list|)
function_decl|;
specifier|public
name|String
name|getPathInfo
parameter_list|()
function_decl|;
specifier|public
name|String
name|getPathTranslated
parameter_list|()
function_decl|;
specifier|public
name|String
name|getProtocol
parameter_list|()
function_decl|;
specifier|public
name|String
name|getQueryString
parameter_list|()
function_decl|;
specifier|public
name|String
name|getRemoteAddr
parameter_list|()
function_decl|;
specifier|public
name|String
name|getRemoteHost
parameter_list|()
function_decl|;
specifier|public
name|String
name|getRemoteUser
parameter_list|()
function_decl|;
specifier|public
name|String
name|getRequestedSessionId
parameter_list|()
function_decl|;
specifier|public
name|String
name|getRequestURI
parameter_list|()
function_decl|;
specifier|public
name|StringBuffer
name|getRequestURL
parameter_list|()
function_decl|;
specifier|public
name|String
name|getScheme
parameter_list|()
function_decl|;
specifier|public
name|String
name|getServerName
parameter_list|()
function_decl|;
specifier|public
name|int
name|getServerPort
parameter_list|()
function_decl|;
specifier|public
name|String
name|getServletPath
parameter_list|()
function_decl|;
specifier|public
name|SessionWrapper
name|getSession
parameter_list|()
function_decl|;
specifier|public
name|SessionWrapper
name|getSession
parameter_list|(
name|boolean
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isRequestedSessionIdFromCookie
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isRequestedSessionIdFromURL
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isRequestedSessionIdValid
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isSecure
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isUserInRole
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
function_decl|;
specifier|public
name|void
name|setCharacterEncoding
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|UnsupportedEncodingException
function_decl|;
block|}
end_interface

end_unit

