begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-06 Wolfgang M.  * Meier meier@ifs.tu-darmstadt.de http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *  * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  * $Id$  */
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
name|BufferedReader
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
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|ServletInputStream
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

begin_comment
comment|/** A wrapper for HttpServletRequest  *   * A method of differentiating between POST parameters in the URL or Content Body of the request was needed.  * The standard javax.servlet.http.HTTPServletRequest does not differentiate between URL or content body parameters,  * this class does, the type is indicated in RequestParameter.type.  *   * To differentiate manually we need to read the URL (getQueryString()) and the Content body (getInputStream()),  * this is problematic with the standard javax.servlet.http.HTTPServletRequest as parameter functions (getParameterMap(), getParameterNames(), getParameter(String), getParameterValues(String))   * affect the  input stream functions (getInputStream(), getReader()) and vice versa.  *   * This class solves this by reading the Request Parameters initially from both the URL and the Content Body of the Request  * and storing them in the private variable params for later use.  *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-02-28  * @version 1.1  */
end_comment

begin_comment
comment|//TODO: check loops to make sure they only iterate as few times as needed
end_comment

begin_comment
comment|//TODO: do we need to do anything with encoding strings manually?
end_comment

begin_class
specifier|public
class|class
name|HttpServletRequestWrapper
implements|implements
name|HttpServletRequest
block|{
comment|//Simple Enumeration implementation for String's, needed for getParameterNames()
specifier|private
class|class
name|StringEnumeration
implements|implements
name|Enumeration
block|{
specifier|private
name|String
index|[]
name|strings
init|=
literal|null
decl_stmt|;
comment|//Strings in the Enumeration
name|int
name|aryPos
init|=
operator|-
literal|1
decl_stmt|;
comment|//Current Position in Enumeration
comment|/** 		 * StringEnumeration Constructor 		 * @param strings[] 	an array of strings for the Enumeration 		 */
name|StringEnumeration
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
block|{
if|if
condition|(
name|strings
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|strings
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|//Create a new array
name|this
operator|.
name|strings
operator|=
operator|new
name|String
index|[
name|strings
operator|.
name|length
index|]
expr_stmt|;
comment|//Copy the data over
name|System
operator|.
name|arraycopy
argument_list|(
name|strings
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|strings
argument_list|,
literal|0
argument_list|,
name|strings
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//Set the position to the start of the array
name|aryPos
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
comment|/** 		 * @see java.util.Enumeration#hasMoreElements 		 */
specifier|public
name|boolean
name|hasMoreElements
parameter_list|()
block|{
if|if
condition|(
name|aryPos
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|aryPos
operator|<
name|strings
operator|.
name|length
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** 		 * @see java.util.Enumeration#nextElement 		 */
specifier|public
name|Object
name|nextElement
parameter_list|()
block|{
if|if
condition|(
name|aryPos
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|aryPos
operator|<
name|strings
operator|.
name|length
condition|)
block|{
name|Object
name|s
init|=
operator|(
name|Object
operator|)
name|strings
index|[
name|aryPos
index|]
decl_stmt|;
name|aryPos
operator|++
expr_stmt|;
return|return
name|s
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"No more String's in the Enumeration, End Reached"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Enumeration is empty"
argument_list|)
throw|;
block|}
block|}
block|}
comment|//Simple class to hold the value and type of a request parameter
specifier|private
class|class
name|RequestParamater
block|{
specifier|public
specifier|final
specifier|static
name|int
name|PARAM_TYPE_URL
init|=
literal|1
decl_stmt|;
comment|//parameter from the URL of the request
specifier|public
specifier|final
specifier|static
name|int
name|PARAM_TYPE_CONTENT
init|=
literal|2
decl_stmt|;
comment|//parameter from the Content of the request
specifier|private
name|String
name|value
init|=
literal|null
decl_stmt|;
comment|//parameter value
specifier|private
name|int
name|type
init|=
literal|0
decl_stmt|;
comment|//parameter type, either PARAM_TYPE_URL or PARAM_TYPE_CONTENT
comment|/** 		 * RequestParameter Constructor 		 * @param value 	Value of the Request Parameter 		 * @param type		Type of the Request Parameter, URL (1) or Content (2) 		 */
name|RequestParamater
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/** 		 * Request parameter value accessor 		 * @return		Value of Request parameter  		 */
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
operator|(
name|value
operator|)
return|;
block|}
comment|/** 		 * Request parameter type accessor 		 * @return		Type of Request parameter 		 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
operator|(
name|type
operator|)
return|;
block|}
block|}
comment|//Members
specifier|private
name|HttpServletRequest
name|request
init|=
literal|null
decl_stmt|;
comment|//The Request
specifier|private
name|String
name|formEncoding
init|=
literal|null
decl_stmt|;
comment|//The encoding for the Request
specifier|private
name|LinkedHashMap
name|params
init|=
literal|null
decl_stmt|;
comment|//The Request Parameters
comment|/* params LinkedHashMap 	 * ==================== 	 * params keys are String 	 * params values are Vector of RequestParameter's 	 */
comment|/** 	 * HttpServletRequestWrapper Constructor 	 * @param request		The HttpServletRequest to wrap 	 * @param formEncoding		The encoding to use 	 */
comment|//Constructor
specifier|public
name|HttpServletRequestWrapper
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|formEncoding
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|formEncoding
operator|=
name|formEncoding
expr_stmt|;
name|params
operator|=
operator|new
name|LinkedHashMap
argument_list|()
expr_stmt|;
name|initialiseWrapper
argument_list|()
expr_stmt|;
block|}
comment|//Initalises the wrapper, setup encoding and parameter hashtable
specifier|private
name|void
name|initialiseWrapper
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
comment|//encoding
if|if
condition|(
name|request
operator|.
name|getCharacterEncoding
argument_list|()
operator|==
literal|null
condition|)
block|{
name|request
operator|.
name|setCharacterEncoding
argument_list|(
name|formEncoding
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|formEncoding
operator|=
name|getCharacterEncoding
argument_list|()
expr_stmt|;
block|}
comment|//Parse out parameters from the URL
name|parseURLParameters
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|)
expr_stmt|;
comment|//If POST request, Parse out parameters from the Content Body
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|toUpperCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"POST"
argument_list|)
condition|)
block|{
comment|//If there is some Content
if|if
condition|(
name|request
operator|.
name|getContentLength
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//If a form POST and not a document POST
if|if
condition|(
name|request
operator|.
name|getContentType
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"application/x-www-form-urlencoded"
argument_list|)
operator|&&
name|request
operator|.
name|getHeader
argument_list|(
literal|"ContentType"
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|//Parse out parameters from the Content Body
name|parseContentBodyParameters
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//Stores parameters from the QueryString of the request
specifier|private
name|void
name|parseURLParameters
parameter_list|(
name|String
name|querystring
parameter_list|)
block|{
if|if
condition|(
name|querystring
operator|!=
literal|null
condition|)
block|{
comment|//Parse any parameters from the URL
name|parseParameters
argument_list|(
name|querystring
argument_list|,
name|RequestParamater
operator|.
name|PARAM_TYPE_URL
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Stores parameters from the Content Body of the Request
specifier|private
name|void
name|parseContentBodyParameters
parameter_list|()
block|{
comment|//Create a buffer big enough to hold the Content Body
name|char
index|[]
name|content
init|=
operator|new
name|char
index|[
name|request
operator|.
name|getContentLength
argument_list|()
index|]
decl_stmt|;
try|try
block|{
comment|//Read the Content Body into the buffer
name|BufferedReader
name|bufRequestBody
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|InputStreamReader
argument_list|(
name|request
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|bufRequestBody
operator|.
name|read
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|bufRequestBody
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//TODO: handle this properly
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//Parse any parameters from the Content Body
name|parseParameters
argument_list|(
operator|new
name|String
argument_list|(
name|content
argument_list|)
argument_list|,
name|RequestParamater
operator|.
name|PARAM_TYPE_CONTENT
argument_list|)
expr_stmt|;
block|}
comment|//Parses Parameters into param objects and stores them in a vector in params
specifier|private
name|void
name|parseParameters
parameter_list|(
name|String
name|parameters
parameter_list|,
name|int
name|type
parameter_list|)
block|{
comment|//Split parameters into an array
name|String
index|[]
name|nameValuePairs
init|=
name|parameters
operator|.
name|split
argument_list|(
literal|"&"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|nameValuePairs
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
comment|//Split parameter into name and value
name|String
index|[]
name|thePair
init|=
name|nameValuePairs
index|[
name|k
index|]
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
try|try
block|{
comment|//URL Decode the parameter name and value
name|thePair
index|[
literal|0
index|]
operator|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|thePair
index|[
literal|0
index|]
argument_list|,
name|formEncoding
argument_list|)
expr_stmt|;
if|if
condition|(
name|thePair
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|thePair
index|[
literal|1
index|]
operator|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|thePair
index|[
literal|1
index|]
argument_list|,
name|formEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
comment|//TODO: handle this properly
name|uee
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//Have we encountered a parameter with this name?
if|if
condition|(
name|params
operator|.
name|containsKey
argument_list|(
name|thePair
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
comment|//key exists in hash map, add value and type to vector
name|Vector
name|vecValues
init|=
operator|(
name|Vector
operator|)
name|params
operator|.
name|get
argument_list|(
name|thePair
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|vecValues
operator|.
name|add
argument_list|(
operator|new
name|RequestParamater
argument_list|(
operator|(
name|thePair
operator|.
name|length
operator|==
literal|2
condition|?
name|thePair
index|[
literal|1
index|]
else|:
operator|new
name|String
argument_list|()
operator|)
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|thePair
index|[
literal|0
index|]
argument_list|,
name|vecValues
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//not in hash map so add a vector with the initial value
name|Vector
name|vecValues
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|vecValues
operator|.
name|add
argument_list|(
operator|new
name|RequestParamater
argument_list|(
operator|(
name|thePair
operator|.
name|length
operator|==
literal|2
condition|?
name|thePair
index|[
literal|1
index|]
else|:
operator|new
name|String
argument_list|()
operator|)
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|thePair
index|[
literal|0
index|]
argument_list|,
name|vecValues
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getAuthType 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getCookies 	 */
specifier|public
name|Cookie
index|[]
name|getCookies
parameter_list|()
block|{
return|return
name|request
operator|.
name|getCookies
argument_list|()
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getDateHeader 	 */
specifier|public
name|long
name|getDateHeader
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|getDateHeader
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getHeader 	 */
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|getHeader
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getHeaders 	 */
specifier|public
name|Enumeration
name|getHeaders
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|getHeaders
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getHeaderNames 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getIntHeader 	 */
specifier|public
name|int
name|getIntHeader
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|getIntHeader
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getMethod 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getPathInfo 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getPathTranslated 	 */
specifier|public
name|String
name|getPathTranslated
parameter_list|()
block|{
return|return
name|request
operator|.
name|getPathInfo
argument_list|()
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getContextPath 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getQueryString 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRemoteUser 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#isUserInRole 	 */
specifier|public
name|boolean
name|isUserInRole
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|isUserInRole
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRequestURI 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRequestedURL 	 */
specifier|public
name|StringBuffer
name|getRequestURL
parameter_list|()
block|{
return|return
name|request
operator|.
name|getRequestURL
argument_list|()
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getServletPath 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getSession 	 */
specifier|public
name|HttpSession
name|getSession
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
return|return
name|request
operator|.
name|getSession
argument_list|(
name|create
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getSession 	 */
specifier|public
name|HttpSession
name|getSession
parameter_list|()
block|{
return|return
name|request
operator|.
name|getSession
argument_list|()
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValie 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl 	 */
specifier|public
name|boolean
name|isRequestedSessionIdFromUrl
parameter_list|()
block|{
return|return
name|request
operator|.
name|isRequestedSessionIdFromUrl
argument_list|()
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getAttribute 	 */
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getAttributeNames 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getCharacterEncoding 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#setCharacterEncoding 	 */
specifier|public
name|void
name|setCharacterEncoding
parameter_list|(
name|String
name|env
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"here"
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCharacterEncoding
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getContentLength 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getContentType 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getInputStream 	 */
specifier|public
name|ServletInputStream
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
comment|/** 	 * get the value of a Request parameter by its name from the local parameter store 	 * @param name		The name of the Request parameter to get the value for 	 * @return		The value of the Request parameter with the specified name 	 */
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|//Does the parameter exist?
if|if
condition|(
name|params
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|//Get the parameters vector of values
name|Vector
name|vecParameterValues
init|=
operator|(
name|Vector
operator|)
name|params
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|//return the first value in the vector
return|return
operator|(
operator|(
name|RequestParamater
operator|)
name|vecParameterValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** 	 * get the names of the Request parameters from the local parameter store 	 * @return		An enumeration of string values representing the Request parameters names 	 */
specifier|public
name|Enumeration
name|getParameterNames
parameter_list|()
block|{
comment|//get the key set as an array
name|Object
index|[]
name|keySet
init|=
name|params
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
comment|//create a new string array, the same size as the ket set array
name|String
index|[]
name|strKeySet
init|=
operator|new
name|String
index|[
name|keySet
operator|.
name|length
index|]
decl_stmt|;
comment|//copy the data from the key set to the string key set
name|System
operator|.
name|arraycopy
argument_list|(
name|keySet
argument_list|,
literal|0
argument_list|,
name|strKeySet
argument_list|,
literal|0
argument_list|,
name|keySet
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//return an enumeration of strings of the keys
return|return
operator|new
name|StringEnumeration
argument_list|(
name|strKeySet
argument_list|)
return|;
block|}
comment|/** 	 * get the values of the Request parameter indicated by name from the local parameter store 	 * @param name		The name of the Request parameter to get the values for 	 * @return		The String array of the Request parameter's values 	 */
specifier|public
name|String
index|[]
name|getParameterValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|//Does the parameter exist?
if|if
condition|(
name|params
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|//Get the parameters vector of values
name|Vector
name|vecParameterValues
init|=
operator|(
name|Vector
operator|)
name|params
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|//Create a string array to hold the values
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[
name|vecParameterValues
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|//Copy each value into the string array
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vecParameterValues
operator|.
name|size
argument_list|()
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
operator|(
operator|(
name|RequestParamater
operator|)
name|vecParameterValues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
comment|//return the string array of values
return|return
operator|(
name|values
operator|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** 	 * get a Map of Request parameters (keys and values) from the local parameter store 	 * @return		Map of Request Parameters. Key is of type String and Value is of type String[]. 	 */
specifier|public
name|Map
name|getParameterMap
parameter_list|()
block|{
comment|//Map to hold the parameters
name|LinkedHashMap
name|mapParameters
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|Set
name|setParams
init|=
name|params
operator|.
name|entrySet
argument_list|()
decl_stmt|;
comment|//iterate through the Request Parameters
for|for
control|(
name|Iterator
name|itParams
init|=
name|setParams
operator|.
name|iterator
argument_list|()
init|;
name|itParams
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
comment|//Get the parameter
name|Map
operator|.
name|Entry
name|me
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|itParams
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//Get the parameters values
name|Vector
name|vecParamValues
init|=
operator|(
name|Vector
operator|)
name|me
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|//Create a string array to hold the parameter values
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[
name|vecParamValues
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|//Copy the parameter values into a string array
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|itParamValues
init|=
name|vecParamValues
operator|.
name|iterator
argument_list|()
init|;
name|itParamValues
operator|.
name|hasNext
argument_list|()
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
operator|(
operator|(
name|RequestParamater
operator|)
name|itParamValues
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|mapParameters
operator|.
name|put
argument_list|(
name|me
operator|.
name|getKey
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
comment|//Store the parameter in a map
block|}
return|return
name|mapParameters
return|;
comment|//return the Map of parameters
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getProtocol 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getScheme 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getServerName 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getServerPort 	 */
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
specifier|public
name|BufferedReader
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRemoteAddr 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRemoteHost 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#setAttribute 	 */
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
name|request
operator|.
name|setAttribute
argument_list|(
name|name
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#removeAttribute 	 */
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|request
operator|.
name|removeAttribute
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getLocale 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getLocales 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#isSecure 	 */
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
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRequestDispatcher 	 */
specifier|public
name|RequestDispatcher
name|getRequestDispatcher
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|getRequestDispatcher
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRealPath 	 */
specifier|public
name|String
name|getRealPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|request
operator|.
name|getRealPath
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getRemotePort 	 */
specifier|public
name|int
name|getRemotePort
parameter_list|()
block|{
return|return
name|request
operator|.
name|getRemotePort
argument_list|()
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getLocalName 	 */
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|request
operator|.
name|getLocalName
argument_list|()
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getLocalAddr 	 */
specifier|public
name|String
name|getLocalAddr
parameter_list|()
block|{
return|return
name|request
operator|.
name|getLocalAddr
argument_list|()
return|;
block|}
comment|/** 	 * @see javax.servlet.http.HttpServletRequest#getLocalPort 	 */
specifier|public
name|int
name|getLocalPort
parameter_list|()
block|{
return|return
name|request
operator|.
name|getLocalPort
argument_list|()
return|;
block|}
comment|/** 	 * Similar to javax.servlet.http.HttpServletRequest.toString() except it includes output of the Request parameters from the Request's Content Body 	 * @return		String representation of HttpServletRequestWrapper 	 */
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|//If POST request AND there is some content AND its not a file upload
if|if
condition|(
operator|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|toUpperCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"POST"
argument_list|)
operator|)
operator|&&
operator|(
name|request
operator|.
name|getContentLength
argument_list|()
operator|>
literal|0
operator|)
operator|&&
operator|(
operator|!
name|request
operator|.
name|getContentType
argument_list|()
operator|.
name|toUpperCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"MULTIPART/"
argument_list|)
operator|)
condition|)
block|{
comment|//Also return the content parameters, these are not part of the standard HttpServletRequest.toString() output
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
name|request
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Set
name|setParams
init|=
name|params
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|itParams
init|=
name|setParams
operator|.
name|iterator
argument_list|()
init|;
name|itParams
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|me
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|itParams
operator|.
name|next
argument_list|()
decl_stmt|;
name|Vector
name|vecParamValues
init|=
operator|(
name|Vector
operator|)
name|me
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|itParamValues
init|=
name|vecParamValues
operator|.
name|iterator
argument_list|()
init|;
name|itParamValues
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|RequestParamater
name|p
init|=
operator|(
name|RequestParamater
operator|)
name|itParamValues
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|type
operator|==
name|RequestParamater
operator|.
name|PARAM_TYPE_CONTENT
condition|)
block|{
if|if
condition|(
name|buf
operator|.
name|charAt
argument_list|(
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|'\n'
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|"&"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|me
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|buf
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
comment|//Return standard HttpServletRequest.toString() output
return|return
name|request
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

