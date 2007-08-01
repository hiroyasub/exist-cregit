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
name|cocoon
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
name|FileOutputStream
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
name|OutputStream
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|servlet
operator|.
name|multipart
operator|.
name|Part
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
name|servlets
operator|.
name|RequestWrapper
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
name|servlets
operator|.
name|SessionWrapper
import|;
end_import

begin_comment
comment|/** A wrapper for requests processed by Cocoon.  * @author Wolfgang Meier<wolfgang@exist-db.org>  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|CocoonRequestWrapper
implements|implements
name|RequestWrapper
block|{
specifier|private
name|Request
name|cocoonRequest
decl_stmt|;
specifier|private
name|HttpServletRequest
name|servletRequest
init|=
literal|null
decl_stmt|;
comment|/** 	 * Constructs a wrapper for the given Cocoon request. 	 * @param cocoonRequest The request as viewed by Cocoon. 	 */
specifier|public
name|CocoonRequestWrapper
parameter_list|(
name|Request
name|cocoonRequest
parameter_list|)
block|{
name|this
operator|.
name|cocoonRequest
operator|=
name|cocoonRequest
expr_stmt|;
block|}
comment|/** Constructs a wrapper for the given Cocoon request. 	 * @param cocoonRequest The request as viewed by Cocoon. 	 * @param servletRequest The request as viewed by Cocoon's servlet 	 */
specifier|public
name|CocoonRequestWrapper
parameter_list|(
name|Request
name|cocoonRequest
parameter_list|,
name|HttpServletRequest
name|servletRequest
parameter_list|)
block|{
name|this
operator|.
name|cocoonRequest
operator|=
name|cocoonRequest
expr_stmt|;
name|this
operator|.
name|servletRequest
operator|=
name|servletRequest
expr_stmt|;
block|}
specifier|public
name|Cookie
index|[]
name|getCookies
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getCookies
argument_list|()
return|;
block|}
comment|/**  	 * @see javax.servlet.http.HttpServletRequest#getInputStream() 	 */
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|servletRequest
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Request input stream is only available "
operator|+
literal|"within a servlet environment"
argument_list|)
throw|;
return|return
name|servletRequest
operator|.
name|getInputStream
argument_list|()
return|;
block|}
comment|/** 	 * @see org.apache.cocoon.environment.Request#get(String) 	 */
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|cocoonRequest
operator|.
name|get
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @see org.apache.cocoon.environment.Request#getCharacterEncoding() 	 */
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getCharacterEncoding
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getContentLength() 	 */
specifier|public
name|int
name|getContentLength
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getContentLength
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getContentType() 	 */
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getContentType
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getContextPath() 	 */
specifier|public
name|String
name|getContextPath
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getContextPath
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getCookieMap() 	 */
specifier|public
name|Map
name|getCookieMap
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getCookieMap
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getHeader(String) 	 */
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|cocoonRequest
operator|.
name|getHeader
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getHeaderNames() 	 */
specifier|public
name|Enumeration
name|getHeaderNames
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getHeaderNames
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getHeaders(String) 	 */
specifier|public
name|Enumeration
name|getHeaders
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|cocoonRequest
operator|.
name|getHeaders
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getMethod() 	 */
specifier|public
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getMethod
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getParameter(String) 	 */
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|cocoonRequest
operator|.
name|getParameter
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getParameterNames() 	 */
specifier|public
name|Enumeration
name|getParameterNames
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getParameterNames
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getParameterValues(String) 	 */
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
name|cocoonRequest
operator|.
name|getParameterValues
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getPathInfo() 	 */
specifier|public
name|String
name|getPathInfo
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getPathInfo
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getPathTranslated() 	 */
specifier|public
name|String
name|getPathTranslated
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getPathTranslated
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getProtocol() 	 */
specifier|public
name|String
name|getProtocol
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getProtocol
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getQueryString() 	 */
specifier|public
name|String
name|getQueryString
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getQueryString
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getRemoteAddr() 	 */
specifier|public
name|String
name|getRemoteAddr
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getRemoteAddr
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getRemoteHost() 	 */
specifier|public
name|String
name|getRemoteHost
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getRemoteHost
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getRemoteUser() 	 */
specifier|public
name|String
name|getRemoteUser
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getRemoteUser
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getRequestedSessionId() 	 */
specifier|public
name|String
name|getRequestedSessionId
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getRequestedSessionId
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getRequestURI() 	 */
specifier|public
name|String
name|getRequestURI
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getRequestURI
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getRequestURL() 	 */
specifier|public
name|StringBuffer
name|getRequestURL
parameter_list|()
block|{
comment|//TODO : check accuracy
if|if
condition|(
name|this
operator|.
name|servletRequest
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|this
operator|.
name|servletRequest
operator|.
name|getRequestURL
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getScheme() 	 */
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getScheme
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getServerName() 	 */
specifier|public
name|String
name|getServerName
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getServerName
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getServerPort() 	 */
specifier|public
name|int
name|getServerPort
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getServerPort
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getServletPath()() 	 */
specifier|public
name|String
name|getServletPath
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getServletPath
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getSession() 	 */
specifier|public
name|SessionWrapper
name|getSession
parameter_list|()
block|{
name|Session
name|session
init|=
name|cocoonRequest
operator|.
name|getSession
argument_list|()
decl_stmt|;
if|if
condition|(
name|session
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
operator|new
name|CocoonSessionWrapper
argument_list|(
name|session
argument_list|)
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getSession(boolean) 	 */
specifier|public
name|SessionWrapper
name|getSession
parameter_list|(
name|boolean
name|arg0
parameter_list|)
block|{
name|Session
name|session
init|=
name|cocoonRequest
operator|.
name|getSession
argument_list|(
name|arg0
argument_list|)
decl_stmt|;
if|if
condition|(
name|session
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
operator|new
name|CocoonSessionWrapper
argument_list|(
name|session
argument_list|)
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getSitemapURI() 	 */
specifier|public
name|String
name|getSitemapURI
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getSitemapURI
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#getUserPrincipal() 	 */
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|getUserPrincipal
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#isRequestedSessionIdFromCookie() 	 */
specifier|public
name|boolean
name|isRequestedSessionIdFromCookie
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|isRequestedSessionIdFromCookie
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#isRequestedSessionIdFromURL() 	 */
specifier|public
name|boolean
name|isRequestedSessionIdFromURL
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|isRequestedSessionIdFromURL
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#isRequestedSessionIdValid() 	 */
specifier|public
name|boolean
name|isRequestedSessionIdValid
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|isRequestedSessionIdValid
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#isSecure() 	 */
specifier|public
name|boolean
name|isSecure
parameter_list|()
block|{
return|return
name|cocoonRequest
operator|.
name|isSecure
argument_list|()
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#isUserInRole(String) 	 */
specifier|public
name|boolean
name|isUserInRole
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|cocoonRequest
operator|.
name|isUserInRole
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#removeAttribute(String) 	 */
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|cocoonRequest
operator|.
name|removeAttribute
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#setAttribute(String, Object) 	 */
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
name|cocoonRequest
operator|.
name|setAttribute
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**@see org.apache.cocoon.environment.Request#setCharacterEncoding(String) 	 */
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
name|cocoonRequest
operator|.
name|setCharacterEncoding
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/*  	 * @see org.apache.cocoon.environment.Request#getFileUploadParam(String) 	 */
specifier|public
name|File
name|getFileUploadParam
parameter_list|(
name|String
name|parameter
parameter_list|)
block|{
name|Object
name|param
init|=
name|cocoonRequest
operator|.
name|get
argument_list|(
name|parameter
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|param
operator|instanceof
name|Part
condition|)
block|{
name|Part
name|part
init|=
operator|(
name|Part
operator|)
name|param
decl_stmt|;
try|try
block|{
name|File
name|temp
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"existCRW"
argument_list|,
literal|".xml"
argument_list|)
decl_stmt|;
name|temp
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|temp
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|part
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|part
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
name|temp
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/*  	 * @see org.apache.cocoon.environment.Request#getUploadedFileName(String) 	 */
specifier|public
name|String
name|getUploadedFileName
parameter_list|(
name|String
name|parameter
parameter_list|)
block|{
name|Object
name|param
init|=
name|cocoonRequest
operator|.
name|get
argument_list|(
name|parameter
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|param
operator|instanceof
name|Part
condition|)
block|{
name|Part
name|part
init|=
operator|(
name|Part
operator|)
name|param
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|part
operator|.
name|getUploadName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

