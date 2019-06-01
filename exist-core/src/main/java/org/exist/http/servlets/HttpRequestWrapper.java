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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|FileUploadException
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
name|disk
operator|.
name|DiskFileItem
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
name|disk
operator|.
name|DiskFileItemFactory
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
name|servlet
operator|.
name|ServletFileUpload
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
name|io
operator|.
name|FilenameUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * A wrapper for requests processed by a servlet. All parameters, submitted as part of  * the URL and via the http POST body (application/x-www-form-urlencoded and  * multipart/form-data encoded) are made available transparently.  *   * @author Wolfgang Meier<wolfgang@exist-db.org>  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  * @author Dannes Wessels<dannes@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|HttpRequestWrapper
implements|implements
name|RequestWrapper
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
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
name|String
name|pathInfo
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|servletPath
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isMultipartContent
init|=
literal|false
decl_stmt|;
comment|// Use linkedhashmap to preserver order
comment|// Object can be a single object, or a List of objects
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|// flag to administer wether multi-part formdata was processed
specifier|private
name|boolean
name|isFormDataParsed
init|=
literal|false
decl_stmt|;
comment|/**      * Constructs a wrapper for the given servlet request. multipart/form-data       * will be parsed when available upon indication.      *      * Defaults to UTF-8 encoding      *       * @param servletRequest The request as viewed by the servlet      */
specifier|public
name|HttpRequestWrapper
parameter_list|(
name|HttpServletRequest
name|servletRequest
parameter_list|)
block|{
name|this
argument_list|(
name|servletRequest
argument_list|,
literal|"UTF-8"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a wrapper for the given servlet request. multipart/form-data       * will be parsed when available upon indication.      *      * @param servletRequest The request as viewed by the servlet      * @param formEncoding The encoding of the request's forms      * @param containerEncoding The encoding of the servlet      */
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
comment|/**      * Constructs a wrapper for the given servlet request.      *      * @param servletRequest The request as viewed by the servlet      * @param formEncoding The encoding of the request's forms      * @param containerEncoding The encoding of the servlet      * @param parseMultipart Set to TRUE to enable parse multipart/form-data when available.      */
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
name|this
operator|.
name|pathInfo
operator|=
name|servletRequest
operator|.
name|getPathInfo
argument_list|()
expr_stmt|;
name|this
operator|.
name|servletPath
operator|=
name|servletRequest
operator|.
name|getServletPath
argument_list|()
expr_stmt|;
comment|// Get url-encoded parameters (url-ecoded from http GET and POST)
name|parseParameters
argument_list|()
expr_stmt|;
comment|// Determine if request is a multipart
name|isMultipartContent
operator|=
name|ServletFileUpload
operator|.
name|isMultipartContent
argument_list|(
name|servletRequest
argument_list|)
expr_stmt|;
comment|// Get multi-part formdata parameters when it is a mpfd request
comment|// and when instructed to do so
if|if
condition|(
name|parseMultipart
operator|&&
name|isMultipartContent
condition|)
block|{
comment|// Formdata is actually parsed
name|isFormDataParsed
operator|=
literal|true
expr_stmt|;
comment|// Get multi-part formdata
name|parseMultipartContent
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Retrieved "
operator|+
name|params
operator|.
name|size
argument_list|()
operator|+
literal|" parameters."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|Enumeration
name|getAttributeNames
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getAttributeNames
argument_list|()
return|;
block|}
comment|/**      * Returns an array of Cookies      */
annotation|@
name|Override
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
specifier|private
specifier|static
name|void
name|addParameter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|,
name|String
name|paramName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
specifier|final
name|Object
name|original
init|=
name|map
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
decl_stmt|;
if|if
condition|(
name|original
operator|!=
literal|null
condition|)
block|{
comment|// Check if original value was already a List
if|if
condition|(
name|original
operator|instanceof
name|List
condition|)
block|{
comment|// Add value to existing List
operator|(
operator|(
name|List
operator|)
name|original
operator|)
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Single value already detected, convert to List and add both items
specifier|final
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|paramName
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Parameter did not exist yet, add single value
name|map
operator|.
name|put
argument_list|(
name|paramName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Parses multi-part requests in order to set the parameters.       */
specifier|private
name|void
name|parseMultipartContent
parameter_list|()
block|{
comment|// Create a factory for disk-based file items
specifier|final
name|DiskFileItemFactory
name|factory
init|=
operator|new
name|DiskFileItemFactory
argument_list|()
decl_stmt|;
comment|// Dizzzz: Wonder why this should be zero
name|factory
operator|.
name|setSizeThreshold
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Create a new file upload handler
specifier|final
name|ServletFileUpload
name|upload
init|=
operator|new
name|ServletFileUpload
argument_list|(
name|factory
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|FileItem
argument_list|>
name|items
init|=
name|upload
operator|.
name|parseRequest
argument_list|(
name|servletRequest
argument_list|)
decl_stmt|;
comment|// Iterate over all mult-part formdata items and
comment|// add all data (field and files) to parmeters
for|for
control|(
specifier|final
name|FileItem
name|item
range|:
name|items
control|)
block|{
name|addParameter
argument_list|(
name|params
argument_list|,
name|item
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|FileUploadException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Parses the url-encoded parameters      */
specifier|private
name|void
name|parseParameters
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
init|=
name|servletRequest
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|param
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// Write keys and values
for|for
control|(
specifier|final
name|String
name|value
range|:
name|param
operator|.
name|getValue
argument_list|()
control|)
block|{
name|addParameter
argument_list|(
name|params
argument_list|,
name|param
operator|.
name|getKey
argument_list|()
argument_list|,
name|decode
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      *  Convert object to FileItem, get FirstItem from list, or null      * if object or object in list is not a FileItem      *       * @param obj List or Fileitem      * @return First Fileitem in list or Fileitem.      */
specifier|private
name|List
argument_list|<
name|FileItem
argument_list|>
name|getFileItem
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|FileItem
argument_list|>
name|fileList
init|=
operator|new
name|LinkedList
argument_list|<
name|FileItem
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|List
condition|)
block|{
comment|// Cast
specifier|final
name|List
name|list
init|=
operator|(
name|List
operator|)
name|obj
decl_stmt|;
comment|// Return first FileItem object if present
for|for
control|(
specifier|final
name|Object
name|listObject
range|:
name|list
control|)
block|{
if|if
condition|(
name|listObject
operator|instanceof
name|FileItem
operator|&&
operator|!
operator|(
operator|(
name|FileItem
operator|)
name|listObject
operator|)
operator|.
name|isFormField
argument_list|()
condition|)
block|{
name|fileList
operator|.
name|add
argument_list|(
operator|(
name|FileItem
operator|)
name|listObject
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|FileItem
operator|&&
operator|!
operator|(
operator|(
name|FileItem
operator|)
name|obj
operator|)
operator|.
name|isFormField
argument_list|()
condition|)
block|{
comment|// Cast and return
name|fileList
operator|.
name|add
argument_list|(
operator|(
name|FileItem
operator|)
name|obj
argument_list|)
expr_stmt|;
block|}
comment|// object did not represent a List of FileItem's or FileItem.
return|return
name|fileList
return|;
block|}
comment|/**      * @param value      */
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
name|formEncoding
operator|==
literal|null
operator|||
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
if|if
condition|(
name|containerEncoding
operator|==
literal|null
condition|)
block|{
comment|//TODO : use file.encoding system property ?
name|containerEncoding
operator|=
literal|"ISO-8859-1"
expr_stmt|;
block|}
if|if
condition|(
name|containerEncoding
operator|.
name|equals
argument_list|(
name|formEncoding
argument_list|)
condition|)
block|{
return|return
name|value
return|;
block|}
try|try
block|{
specifier|final
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
specifier|final
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getInputStream()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getCharacterEncoding()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getContentLength()      */
annotation|@
name|Override
specifier|public
name|long
name|getContentLength
parameter_list|()
block|{
name|long
name|retval
init|=
name|servletRequest
operator|.
name|getContentLength
argument_list|()
decl_stmt|;
specifier|final
name|String
name|lenstr
init|=
name|servletRequest
operator|.
name|getHeader
argument_list|(
literal|"Content-Length"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lenstr
operator|!=
literal|null
condition|)
block|{
name|retval
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|lenstr
argument_list|)
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getContentType()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getContextPath()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getHeader(String)      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getCharacterEncoding()      * @return An enumeration of header names      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getHeaders(String)      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getMethod()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getParameter(String)      */
annotation|@
name|Override
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// Parameters
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
block|{
return|return
literal|null
return|;
block|}
comment|// If Parameter is a List, get first entry. The data is used later on
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
specifier|final
name|List
name|lst
init|=
operator|(
operator|(
name|List
operator|)
name|o
operator|)
decl_stmt|;
name|o
operator|=
name|lst
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// If parameter is file item, convert to string
if|if
condition|(
name|o
operator|instanceof
name|FileItem
condition|)
block|{
specifier|final
name|FileItem
name|fi
init|=
operator|(
name|FileItem
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|formEncoding
operator|==
literal|null
condition|)
block|{
return|return
name|fi
operator|.
name|getString
argument_list|()
return|;
block|}
else|else
block|{
try|try
block|{
return|return
name|fi
operator|.
name|getString
argument_list|(
name|formEncoding
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
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
comment|// Return just a simple value
block|}
if|else if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|o
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getParameter(String)      */
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getFileUploadParam
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isFormDataParsed
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|FileItem
argument_list|>
name|items
init|=
name|getFileItem
argument_list|(
name|o
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|items
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|FileItem
name|item
range|:
name|items
control|)
block|{
name|files
operator|.
name|add
argument_list|(
operator|(
operator|(
name|DiskFileItem
operator|)
name|item
operator|)
operator|.
name|getStoreLocation
argument_list|()
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|files
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getParameter(String)      */
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getUploadedFileName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isFormDataParsed
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|FileItem
argument_list|>
name|items
init|=
name|getFileItem
argument_list|(
name|o
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|items
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|FileItem
name|item
range|:
name|items
control|)
block|{
name|files
operator|.
name|add
argument_list|(
name|FilenameUtils
operator|.
name|normalize
argument_list|(
name|item
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|files
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getParameterNames()      */
annotation|@
name|Override
specifier|public
name|Enumeration
name|getParameterNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|enumeration
argument_list|(
name|params
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getParameterValues(String)      */
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getParameterValues
parameter_list|(
name|String
name|key
parameter_list|)
block|{
comment|// params already retrieved
specifier|final
name|Object
name|obj
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// Fast return
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Allocate return values
name|String
index|[]
name|values
decl_stmt|;
comment|// If object is a List, retrieve data from list
if|if
condition|(
name|obj
operator|instanceof
name|List
condition|)
block|{
comment|// Cast to List
specifier|final
name|List
name|list
init|=
operator|(
name|List
operator|)
name|obj
decl_stmt|;
comment|// Reserve the right aboumt of elements
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
comment|// position in array
name|int
name|position
init|=
literal|0
decl_stmt|;
comment|// Iterate over list
for|for
control|(
specifier|final
name|Object
name|object
range|:
name|list
control|)
block|{
comment|// Item is a FileItem
if|if
condition|(
name|object
operator|instanceof
name|FileItem
condition|)
block|{
comment|// Cast
specifier|final
name|FileItem
name|item
init|=
operator|(
name|FileItem
operator|)
name|object
decl_stmt|;
comment|// Get string representation of FileItem
try|try
block|{
name|values
index|[
name|position
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
specifier|final
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
else|else
block|{
comment|// Normal formfield
name|values
index|[
name|position
index|]
operator|=
operator|(
name|String
operator|)
name|object
expr_stmt|;
block|}
name|position
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// No list retrieve one element only
comment|// Allocate space
name|values
operator|=
operator|new
name|String
index|[
literal|1
index|]
expr_stmt|;
comment|// Item is a FileItem
if|if
condition|(
name|obj
operator|instanceof
name|FileItem
condition|)
block|{
specifier|final
name|FileItem
name|item
init|=
operator|(
name|FileItem
operator|)
name|obj
decl_stmt|;
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
specifier|final
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
else|else
block|{
comment|// Normal formfield
name|values
index|[
literal|0
index|]
operator|=
operator|(
name|String
operator|)
name|obj
expr_stmt|;
block|}
block|}
return|return
name|values
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getPathInfo()      */
annotation|@
name|Override
specifier|public
name|String
name|getPathInfo
parameter_list|()
block|{
return|return
name|pathInfo
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getPathTranslated()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getProtocol()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getQueryString()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getRemoteAddr()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getRemoteHost()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getRemotePort()      */
annotation|@
name|Override
specifier|public
name|int
name|getRemotePort
parameter_list|()
block|{
return|return
name|servletRequest
operator|.
name|getRemotePort
argument_list|()
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getRemoteUser()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getRequestURI()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getRequestURL()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getScheme()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getServerName()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getServerPort()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#getServletPath()      */
annotation|@
name|Override
specifier|public
name|String
name|getServletPath
parameter_list|()
block|{
return|return
name|servletPath
return|;
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getSession()      */
annotation|@
name|Override
specifier|public
name|SessionWrapper
name|getSession
parameter_list|()
block|{
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|HttpSessionWrapper
argument_list|(
name|session
argument_list|)
return|;
block|}
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getSession(boolean)      */
annotation|@
name|Override
specifier|public
name|SessionWrapper
name|getSession
parameter_list|(
name|boolean
name|arg0
parameter_list|)
block|{
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|HttpSessionWrapper
argument_list|(
name|session
argument_list|)
return|;
block|}
block|}
comment|/**      * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#isSecure()      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#isUserInRole(String)      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#removeAttribute(String)      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#setAttribute(String, Object)      */
annotation|@
name|Override
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
comment|/**      * @see javax.servlet.http.HttpServletRequest#setCharacterEncoding(String)      */
annotation|@
name|Override
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
specifier|public
name|void
name|setPathInfo
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|pathInfo
operator|=
name|arg0
expr_stmt|;
block|}
specifier|public
name|void
name|setServletPath
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|servletPath
operator|=
name|arg0
expr_stmt|;
block|}
comment|/**      *  Indicate if a form is processed.      *      * @return TRUE if a form is processed else FALSE.      */
specifier|public
name|boolean
name|isFormDataParsed
parameter_list|()
block|{
return|return
name|isFormDataParsed
return|;
block|}
comment|/**      *  Indicate if the request is a multi-part formdata request      *      * @return TRUE if request is multi-part/formdata request, else FALSE.      */
annotation|@
name|Override
specifier|public
name|boolean
name|isMultipartContent
parameter_list|()
block|{
return|return
name|isMultipartContent
return|;
block|}
annotation|@
name|Override
specifier|public
name|RequestDispatcher
name|getRequestDispatcher
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
return|return
name|servletRequest
operator|.
name|getRequestDispatcher
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit
