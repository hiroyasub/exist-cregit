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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|HttpRequestWrapper
implements|implements
name|RequestWrapper
block|{
specifier|private
name|HttpServletRequest
name|request
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|HttpRequestWrapper
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|request
operator|.
name|getAttribute
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|Enumeration
name|getAttributeNames
parameter_list|()
block|{
return|return
name|request
operator|.
name|getAttributeNames
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getAuthType
parameter_list|()
block|{
return|return
name|request
operator|.
name|getAuthType
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|request
operator|.
name|getCharacterEncoding
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
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
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|request
operator|.
name|getContentType
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getContextPath
parameter_list|()
block|{
return|return
name|request
operator|.
name|getContextPath
argument_list|()
return|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|long
name|getDateHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|request
operator|.
name|getDateHeader
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|request
operator|.
name|getHeader
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|Enumeration
name|getHeaderNames
parameter_list|()
block|{
return|return
name|request
operator|.
name|getHeaderNames
argument_list|()
return|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|Enumeration
name|getHeaders
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|request
operator|.
name|getHeaders
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|Locale
name|getLocale
parameter_list|()
block|{
return|return
name|request
operator|.
name|getLocale
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|Enumeration
name|getLocales
parameter_list|()
block|{
return|return
name|request
operator|.
name|getLocales
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
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
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|request
operator|.
name|getParameter
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|Enumeration
name|getParameterNames
parameter_list|()
block|{
return|return
name|request
operator|.
name|getParameterNames
argument_list|()
return|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|String
index|[]
name|getParameterValues
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|request
operator|.
name|getParameterValues
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getPathInfo
parameter_list|()
block|{
return|return
name|request
operator|.
name|getPathInfo
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getPathTranslated
parameter_list|()
block|{
return|return
name|request
operator|.
name|getPathTranslated
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getProtocol
parameter_list|()
block|{
return|return
name|request
operator|.
name|getProtocol
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getQueryString
parameter_list|()
block|{
return|return
name|request
operator|.
name|getQueryString
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getRemoteAddr
parameter_list|()
block|{
return|return
name|request
operator|.
name|getRemoteAddr
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getRemoteHost
parameter_list|()
block|{
return|return
name|request
operator|.
name|getRemoteHost
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getRemoteUser
parameter_list|()
block|{
return|return
name|request
operator|.
name|getRemoteUser
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getRequestedSessionId
parameter_list|()
block|{
return|return
name|request
operator|.
name|getRequestedSessionId
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getRequestURI
parameter_list|()
block|{
return|return
name|request
operator|.
name|getRequestURI
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|request
operator|.
name|getScheme
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getServerName
parameter_list|()
block|{
return|return
name|request
operator|.
name|getServerName
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|int
name|getServerPort
parameter_list|()
block|{
return|return
name|request
operator|.
name|getServerPort
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getServletPath
parameter_list|()
block|{
return|return
name|request
operator|.
name|getServletPath
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|SessionWrapper
name|getSession
parameter_list|()
block|{
return|return
operator|new
name|HttpSessionWrapper
argument_list|(
name|request
operator|.
name|getSession
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|SessionWrapper
name|getSession
parameter_list|(
name|boolean
name|arg0
parameter_list|)
block|{
return|return
operator|new
name|HttpSessionWrapper
argument_list|(
name|request
operator|.
name|getSession
argument_list|(
name|arg0
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
name|request
operator|.
name|getUserPrincipal
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|boolean
name|isRequestedSessionIdFromCookie
parameter_list|()
block|{
return|return
name|request
operator|.
name|isRequestedSessionIdFromCookie
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|boolean
name|isRequestedSessionIdFromURL
parameter_list|()
block|{
return|return
name|request
operator|.
name|isRequestedSessionIdFromURL
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|boolean
name|isRequestedSessionIdValid
parameter_list|()
block|{
return|return
name|request
operator|.
name|isRequestedSessionIdValid
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|boolean
name|isSecure
parameter_list|()
block|{
return|return
name|request
operator|.
name|isSecure
argument_list|()
return|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|boolean
name|isUserInRole
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|request
operator|.
name|isUserInRole
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @param arg0 	 */
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|request
operator|.
name|removeAttribute
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 */
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
block|{
name|request
operator|.
name|setAttribute
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @throws java.io.UnsupportedEncodingException 	 */
specifier|public
name|void
name|setCharacterEncoding
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|request
operator|.
name|setCharacterEncoding
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

