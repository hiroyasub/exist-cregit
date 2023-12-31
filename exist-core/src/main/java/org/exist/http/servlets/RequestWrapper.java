begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|javax
operator|.
name|servlet
operator|.
name|RequestDispatcher
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
name|Cookie
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|RequestWrapper
block|{
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getAttributeNames
parameter_list|()
function_decl|;
name|String
name|getCharacterEncoding
parameter_list|()
function_decl|;
name|long
name|getContentLength
parameter_list|()
function_decl|;
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|Cookie
index|[]
name|getCookies
parameter_list|()
function_decl|;
name|String
name|getContentType
parameter_list|()
function_decl|;
name|String
name|getContextPath
parameter_list|()
function_decl|;
name|String
name|getHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
name|Enumeration
name|getHeaderNames
parameter_list|()
function_decl|;
name|Enumeration
name|getHeaders
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
name|String
name|getMethod
parameter_list|()
function_decl|;
name|String
name|getParameter
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getParameterNames
parameter_list|()
function_decl|;
name|String
index|[]
name|getParameterValues
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
name|List
argument_list|<
name|Path
argument_list|>
name|getFileUploadParam
parameter_list|(
name|String
name|parameter
parameter_list|)
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|getUploadedFileName
parameter_list|(
name|String
name|parameter
parameter_list|)
function_decl|;
name|String
name|getPathInfo
parameter_list|()
function_decl|;
name|String
name|getPathTranslated
parameter_list|()
function_decl|;
name|String
name|getProtocol
parameter_list|()
function_decl|;
name|String
name|getQueryString
parameter_list|()
function_decl|;
name|String
name|getRemoteAddr
parameter_list|()
function_decl|;
name|String
name|getRemoteHost
parameter_list|()
function_decl|;
name|int
name|getRemotePort
parameter_list|()
function_decl|;
name|String
name|getRemoteUser
parameter_list|()
function_decl|;
name|String
name|getRequestedSessionId
parameter_list|()
function_decl|;
name|String
name|getRequestURI
parameter_list|()
function_decl|;
name|StringBuffer
name|getRequestURL
parameter_list|()
function_decl|;
name|String
name|getScheme
parameter_list|()
function_decl|;
name|String
name|getServerName
parameter_list|()
function_decl|;
name|int
name|getServerPort
parameter_list|()
function_decl|;
name|String
name|getServletPath
parameter_list|()
function_decl|;
name|SessionWrapper
name|getSession
parameter_list|()
function_decl|;
name|SessionWrapper
name|getSession
parameter_list|(
name|boolean
name|arg0
parameter_list|)
function_decl|;
name|Principal
name|getUserPrincipal
parameter_list|()
function_decl|;
name|boolean
name|isRequestedSessionIdFromCookie
parameter_list|()
function_decl|;
name|boolean
name|isRequestedSessionIdFromURL
parameter_list|()
function_decl|;
name|boolean
name|isRequestedSessionIdValid
parameter_list|()
function_decl|;
name|boolean
name|isSecure
parameter_list|()
function_decl|;
name|boolean
name|isUserInRole
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
name|void
name|removeAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
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
name|void
name|setCharacterEncoding
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|UnsupportedEncodingException
function_decl|;
name|boolean
name|isMultipartContent
parameter_list|()
function_decl|;
name|RequestDispatcher
name|getRequestDispatcher
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

