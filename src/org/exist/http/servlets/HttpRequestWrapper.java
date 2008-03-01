begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|ArrayList
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
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|fileupload
operator|.
name|DefaultFileItem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|fileupload
operator|.
name|DiskFileUpload
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|fileupload
operator|.
name|FileItem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|fileupload
operator|.
name|FileUpload
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|fileupload
operator|.
name|FileUploadException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|Constants
import|;
end_import

begin_comment
comment|/** A wrapper for requests processed by a servlet.  * @author Wolfgang Meier<wolfgang@exist-db.org>  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
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
name|servletRequest
decl_stmt|;
specifier|private
name|String
name|formEncoding
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|containerEncoding
init|=
literal|null
decl_stmt|;
specifier|private
name|Hashtable
name|params
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|HttpRequestWrapper
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|HttpRequestWrapper
parameter_list|(
name|HttpServletRequest
name|servletRequest
parameter_list|,
name|String
name|formEncoding
parameter_list|,
name|String
name|containerEncoding
parameter_list|)
block|{
name|this
argument_list|(
name|servletRequest
argument_list|,
name|formEncoding
argument_list|,
name|containerEncoding
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructs a wrapper for the given servlet request.      * @param servletRequest The request as viewed by the servlet      * @param formEncoding The encoding of the request's forms      * @param containerEncoding The encoding of the servlet      */
specifier|public
name|HttpRequestWrapper
parameter_list|(
name|HttpServletRequest
name|servletRequest
parameter_list|,
name|String
name|formEncoding
parameter_list|,
name|String
name|containerEncoding
parameter_list|,
name|boolean
name|parseMultipart
parameter_list|)
block|{
name|this
operator|.
name|servletRequest
operator|=
name|servletRequest
expr_stmt|;
name|this
operator|.
name|formEncoding
operator|=
name|formEncoding
expr_stmt|;
name|this
operator|.
name|containerEncoding
operator|=
name|containerEncoding
expr_stmt|;
if|if
condition|(
name|parseMultipart
operator|&&
name|FileUpload
operator|.
name|isMultipartContent
argument_list|(
name|servletRequest
argument_list|)
condition|)
block|{
name|parseMultipartContent
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|servletRequest
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Returns an array of Cookies      */
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
comment|/**      * Parses multi-part requests in order to set the parameters.       */
specifier|private
name|void
name|parseMultipartContent
parameter_list|()
block|{
name|DiskFileUpload
name|upload
init|=
operator|new
name|DiskFileUpload
argument_list|()
decl_stmt|;
name|upload
operator|.
name|setSizeThreshold
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|params
operator|=
operator|new
name|Hashtable
argument_list|()
expr_stmt|;
name|List
name|items
init|=
name|upload
operator|.
name|parseRequest
argument_list|(
name|this
operator|.
name|servletRequest
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|items
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|FileItem
name|next
init|=
operator|(
name|FileItem
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|old
init|=
name|params
operator|.
name|get
argument_list|(
name|next
operator|.
name|getFieldName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|old
operator|instanceof
name|List
condition|)
operator|(
operator|(
name|List
operator|)
name|old
operator|)
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
else|else
block|{
name|ArrayList
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|old
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|next
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
else|else
name|params
operator|.
name|put
argument_list|(
name|next
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileUploadException
name|e
parameter_list|)
block|{
comment|// TODO: handle this
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @param obj      * @return      */
specifier|private
name|FileItem
name|getFileItem
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|List
condition|)
return|return
operator|(
name|FileItem
operator|)
operator|(
operator|(
name|List
operator|)
name|obj
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
else|else
return|return
operator|(
name|FileItem
operator|)
name|obj
return|;
block|}
comment|/**      * @param value      * @return      */
specifier|private
name|String
name|decode
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|containerEncoding
operator|==
literal|null
condition|)
comment|//TODO : use file.encoding system property ?
name|containerEncoding
operator|=
literal|"ISO-8859-1"
expr_stmt|;
if|if
condition|(
name|containerEncoding
operator|.
name|equals
argument_list|(
name|formEncoding
argument_list|)
condition|)
return|return
name|value
return|;
try|try
block|{
name|byte
index|[]
name|bytes
init|=
name|value
operator|.
name|getBytes
argument_list|(
name|containerEncoding
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|formEncoding
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
block|}
comment|/** @see javax.servlet.http.HttpServletRequest#getInputStream()      */
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|servletRequest
operator|.
name|getInputStream
argument_list|()
return|;
block|}
comment|/** @see javax.servlet.http.HttpServletRequest#getCharacterEncoding()      */
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getCharacterEncoding
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getContentLength()      */
specifier|public
name|int
name|getContentLength
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getContentLength
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getContentType()      */
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getContentType
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getContextPath()      */
specifier|public
name|String
name|getContextPath
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getContextPath
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getHeader(String)      */
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|servletRequest
operator|.
name|getHeader
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getCharacterEncoding()      * @return An enumeration of header names      */
specifier|public
name|Enumeration
name|getHeaderNames
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getHeaderNames
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getHeaders(String)      */
specifier|public
name|Enumeration
name|getHeaders
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|servletRequest
operator|.
name|getHeaders
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getMethod()      */
specifier|public
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getMethod
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getParameter(String)      */
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|String
name|value
init|=
name|servletRequest
operator|.
name|getParameter
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|formEncoding
operator|==
literal|null
operator|||
name|value
operator|==
literal|null
condition|)
return|return
name|value
return|;
return|return
name|decode
argument_list|(
name|value
argument_list|)
return|;
block|}
else|else
block|{
name|Object
name|o
init|=
name|params
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|FileItem
name|item
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
name|item
operator|=
operator|(
name|FileItem
operator|)
operator|(
operator|(
name|List
operator|)
name|o
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
else|else
name|item
operator|=
operator|(
name|FileItem
operator|)
name|o
expr_stmt|;
if|if
condition|(
name|formEncoding
operator|==
literal|null
condition|)
return|return
name|item
operator|.
name|getString
argument_list|()
return|;
else|else
try|try
block|{
return|return
name|item
operator|.
name|getString
argument_list|(
name|formEncoding
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getParameter(String)      */
specifier|public
name|File
name|getFileUploadParam
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Object
name|o
init|=
name|params
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|FileItem
name|item
init|=
name|getFileItem
argument_list|(
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|isFormField
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
operator|(
operator|(
name|DefaultFileItem
operator|)
name|item
operator|)
operator|.
name|getStoreLocation
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getParameter(String)      */
specifier|public
name|String
name|getUploadedFileName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Object
name|o
init|=
name|params
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|FileItem
name|item
init|=
name|getFileItem
argument_list|(
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|isFormField
argument_list|()
condition|)
return|return
literal|null
return|;
comment|// Get filename from FileItem
name|String
name|itemName
init|=
name|item
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|itemName
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// Several browsers, e.g. MSIE send a full path of the LOCALLY stored
comment|// file instead of the filename alone.
comment|// Jakarta's Commons FileUpload package does not repair this
comment|// so we should remove all supplied path information.
comment|// If there are (back) slashes in the Filename, we have
comment|// a full path. Find the last (back) slash, take remaining text
name|int
name|lastFileSepPos
init|=
name|Math
operator|.
name|max
argument_list|(
name|itemName
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|itemName
operator|.
name|lastIndexOf
argument_list|(
literal|"\\"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|documentName
init|=
name|itemName
decl_stmt|;
if|if
condition|(
name|lastFileSepPos
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|documentName
operator|=
name|itemName
operator|.
name|substring
argument_list|(
name|lastFileSepPos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|documentName
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getParameterNames()      */
specifier|public
name|Enumeration
name|getParameterNames
parameter_list|()
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
return|return
name|servletRequest
operator|.
name|getParameterNames
argument_list|()
return|;
else|else
block|{
return|return
name|params
operator|.
name|keys
argument_list|()
return|;
block|}
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getParameterValues(String)      */
specifier|public
name|String
index|[]
name|getParameterValues
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|String
index|[]
name|values
init|=
name|servletRequest
operator|.
name|getParameterValues
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|formEncoding
operator|==
literal|null
operator|||
name|values
operator|==
literal|null
condition|)
return|return
name|values
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|decode
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
else|else
block|{
name|Object
name|obj
init|=
operator|(
name|Object
operator|)
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
index|[]
name|values
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|obj
decl_stmt|;
name|values
operator|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|FileItem
name|item
init|=
operator|(
name|FileItem
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|values
index|[
name|j
index|]
operator|=
name|formEncoding
operator|==
literal|null
condition|?
name|item
operator|.
name|getString
argument_list|()
else|:
name|item
operator|.
name|getString
argument_list|(
name|formEncoding
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|FileItem
name|item
init|=
operator|(
name|FileItem
operator|)
name|obj
decl_stmt|;
name|values
operator|=
operator|new
name|String
index|[
literal|1
index|]
expr_stmt|;
try|try
block|{
name|values
index|[
literal|0
index|]
operator|=
name|formEncoding
operator|==
literal|null
condition|?
name|item
operator|.
name|getString
argument_list|()
else|:
name|item
operator|.
name|getString
argument_list|(
name|formEncoding
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|values
return|;
block|}
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getPathInfo()      */
specifier|public
name|String
name|getPathInfo
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getPathInfo
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getPathTranslated()      */
specifier|public
name|String
name|getPathTranslated
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getPathTranslated
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getProtocol()      */
specifier|public
name|String
name|getProtocol
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getProtocol
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getQueryString()      */
specifier|public
name|String
name|getQueryString
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getQueryString
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getRemoteAddr()      */
specifier|public
name|String
name|getRemoteAddr
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getRemoteAddr
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getRemoteHost()      */
specifier|public
name|String
name|getRemoteHost
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getRemoteHost
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getRemoteUser()      */
specifier|public
name|String
name|getRemoteUser
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getRemoteUser
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getRequestedSessionId()      */
specifier|public
name|String
name|getRequestedSessionId
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getRequestedSessionId
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getRequestURI()      */
specifier|public
name|String
name|getRequestURI
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getRequestURI
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getRequestURL()      */
specifier|public
name|StringBuffer
name|getRequestURL
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getRequestURL
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getScheme()      */
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getScheme
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getServerName()      */
specifier|public
name|String
name|getServerName
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getServerName
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getServerPort()      */
specifier|public
name|int
name|getServerPort
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getServerPort
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getServletPath()      */
specifier|public
name|String
name|getServletPath
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getServletPath
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getSession()      */
specifier|public
name|SessionWrapper
name|getSession
parameter_list|()
block|{
name|HttpSession
name|session
init|=
name|servletRequest
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
name|HttpSessionWrapper
argument_list|(
name|session
argument_list|)
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getSession(boolean)      */
specifier|public
name|SessionWrapper
name|getSession
parameter_list|(
name|boolean
name|arg0
parameter_list|)
block|{
name|HttpSession
name|session
init|=
name|servletRequest
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
name|HttpSessionWrapper
argument_list|(
name|session
argument_list|)
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#getUserPrincipal()      */
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getUserPrincipal
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()      */
specifier|public
name|boolean
name|isRequestedSessionIdFromCookie
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|isRequestedSessionIdFromCookie
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()      */
specifier|public
name|boolean
name|isRequestedSessionIdFromURL
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|isRequestedSessionIdFromURL
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()      */
specifier|public
name|boolean
name|isRequestedSessionIdValid
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|isRequestedSessionIdValid
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#isSecure()      */
specifier|public
name|boolean
name|isSecure
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|isSecure
argument_list|()
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#isUserInRole(String)      */
specifier|public
name|boolean
name|isUserInRole
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|servletRequest
operator|.
name|isUserInRole
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#removeAttribute(String)      */
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|servletRequest
operator|.
name|removeAttribute
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#setAttribute(String, Object)      */
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
name|servletRequest
operator|.
name|setAttribute
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**@see javax.servlet.http.HttpServletRequest#setCharacterEncoding(String)      */
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
name|servletRequest
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

