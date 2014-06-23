begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-10 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|client
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
name|FileInputStream
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
name|OutputStreamWriter
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServlet
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
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
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
name|lang3
operator|.
name|ArrayUtils
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
name|lang3
operator|.
name|StringUtils
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
name|util
operator|.
name|serializer
operator|.
name|DOMSerializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  * A servlet to monitor the database. It returns status information for the database based on the JMX interface. For  * simplicity, the JMX beans provided by eXist are organized into categories. One calls the servlet with one or more  * categories in parameter "c", e.g.:  *  * /exist/jmx?c=instances&c=memory  *  * If no parameter is specified, all categories will be returned. Valid categories are "memory", "instances", "disk",  * "system", "caches", "locking", "processes", "sanity", "all".  *  * The servlet can also be used to test if the database is responsive by using parameter "operation=ping" and a timeout  * (t=timeout-in-milliseconds). For example, the following call  *  * /exist/jmx?operation=ping&t=1000  *  * will wait for a response within 1000ms. If the ping returns within the specified timeout, the servlet returns the  * attributes of the SanityReport JMX bean, which will include an element&lt;jmx:Status&gt;PING_OK&lt;/jmx:Status&gt;.  * If the ping takes longer than the timeout, you'll instead find an element&lt;jmx:error&gt; in the returned XML. In  * this case, additional information on running queries, memory consumption and database locks will be provided.  *  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|JMXServlet
extends|extends
name|HttpServlet
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|JMXServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TOKEN_KEY
init|=
literal|"token"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TOKEN_FILE
init|=
literal|"jmxservlet.token"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|WEBINF_DATA_DIR
init|=
literal|"WEB-INF/data"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Properties
name|defaultProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|JMXtoXML
name|client
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|localhostAddresses
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|File
name|dataDir
decl_stmt|;
specifier|private
name|File
name|tokenFile
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Verify if request is from localhost or if user has specific servlet/container managed role.
if|if
condition|(
name|isFromLocalHost
argument_list|(
name|request
argument_list|)
condition|)
block|{
comment|// Localhost is always authorized to access
name|LOG
operator|.
name|debug
argument_list|(
literal|"Local access granted"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|hasSecretToken
argument_list|(
name|request
argument_list|,
name|getToken
argument_list|()
argument_list|)
condition|)
block|{
comment|// Correct token is provided
name|LOG
operator|.
name|debug
argument_list|(
literal|"Correct token provided by "
operator|+
name|request
operator|.
name|getRemoteHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if user is already authorized, e.g. via MONEX allow user too
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
literal|"Access allowed for localhost, or when correct token has been provided."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Perform actual writing of data
name|writeXmlData
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeXmlData
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|Element
name|root
init|=
literal|null
decl_stmt|;
specifier|final
name|String
name|operation
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"operation"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"ping"
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
name|long
name|timeout
init|=
literal|5000
decl_stmt|;
specifier|final
name|String
name|timeoutParam
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|timeoutParam
argument_list|)
condition|)
block|{
try|try
block|{
name|timeout
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|timeoutParam
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"timeout parameter needs to be a number. Got: "
operator|+
name|timeoutParam
argument_list|)
throw|;
block|}
block|}
specifier|final
name|long
name|responseTime
init|=
name|client
operator|.
name|ping
argument_list|(
literal|"exist"
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|responseTime
operator|==
name|JMXtoXML
operator|.
name|PING_TIMEOUT
condition|)
block|{
name|root
operator|=
name|client
operator|.
name|generateXMLReport
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"no response on ping after %sms"
argument_list|,
name|timeout
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"sanity"
block|,
literal|"locking"
block|,
literal|"processes"
block|,
literal|"instances"
block|,
literal|"memory"
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|root
operator|=
name|client
operator|.
name|generateXMLReport
argument_list|(
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"sanity"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|operation
operator|!=
literal|null
operator|&&
name|operation
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|String
name|mbean
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"mbean"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbean
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"to call an operation, you also need to specify parameter 'mbean'"
argument_list|)
throw|;
block|}
name|String
index|[]
name|args
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
literal|"args"
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|=
name|client
operator|.
name|invoke
argument_list|(
name|mbean
argument_list|,
name|operation
argument_list|,
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"operation "
operator|+
name|operation
operator|+
literal|" not found on "
operator|+
name|mbean
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"mbean "
operator|+
name|mbean
operator|+
literal|" not found: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MBeanException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ReflectionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IntrospectionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|String
index|[]
name|categories
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
if|if
condition|(
name|categories
operator|==
literal|null
condition|)
block|{
name|categories
operator|=
operator|new
name|String
index|[]
block|{
literal|"all"
block|}
expr_stmt|;
block|}
name|root
operator|=
name|client
operator|.
name|generateXMLReport
argument_list|(
literal|null
argument_list|,
name|categories
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/xml"
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|useAttribute
init|=
name|request
operator|.
name|getAttribute
argument_list|(
literal|"jmx.attribute"
argument_list|)
decl_stmt|;
if|if
condition|(
name|useAttribute
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setAttribute
argument_list|(
name|useAttribute
operator|.
name|toString
argument_list|()
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
specifier|final
name|DOMSerializer
name|streamer
init|=
operator|new
name|DOMSerializer
argument_list|(
name|writer
argument_list|,
name|defaultProperties
argument_list|)
decl_stmt|;
try|try
block|{
name|streamer
operator|.
name|serialize
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TransformerException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessageAndLocation
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Error while serializing result: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// Setup JMS client
name|client
operator|=
operator|new
name|JMXtoXML
argument_list|()
expr_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|// Register all known localhost addresses
name|registerLocalHostAddresses
argument_list|()
expr_stmt|;
comment|// Get directory for token file
specifier|final
name|String
name|jmxDataDir
init|=
name|client
operator|.
name|getDataDir
argument_list|()
decl_stmt|;
if|if
condition|(
name|jmxDataDir
operator|==
literal|null
condition|)
block|{
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|WEBINF_DATA_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|jmxDataDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|dataDir
operator|.
name|isDirectory
argument_list|()
operator|||
operator|!
name|dataDir
operator|.
name|canWrite
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot access directory "
operator|+
name|WEBINF_DATA_DIR
argument_list|)
expr_stmt|;
block|}
comment|// Setup token and tokenfile
name|obtainTokenFileReference
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"JMXservlet token: %s"
argument_list|,
name|getToken
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Register all known IP-addresses for localhost.      */
name|void
name|registerLocalHostAddresses
parameter_list|()
block|{
comment|// The external IP address of the server
try|try
block|{
name|localhostAddresses
operator|.
name|add
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to get HostAddress for localhost: %s"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// The configured Localhost addresses
try|try
block|{
for|for
control|(
name|InetAddress
name|address
range|:
name|InetAddress
operator|.
name|getAllByName
argument_list|(
literal|"localhost"
argument_list|)
control|)
block|{
name|localhostAddresses
operator|.
name|add
argument_list|(
name|address
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to retrieve ipaddresses for localhost: %s"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|localhostAddresses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to determine addresses for localhost, jmx servlet might be disfunctional."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Determine if HTTP request is originated from localhost.      *      * @param request The HTTP request      * @return TRUE if request is from LOCALHOST otherwise FALSE      */
name|boolean
name|isFromLocalHost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
return|return
name|localhostAddresses
operator|.
name|contains
argument_list|(
name|request
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Check if URL contains magic Token      *      * @param request The HTTP request      * @return TRUE if request contains correct value for token, else FALSE      */
name|boolean
name|hasSecretToken
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|token
parameter_list|)
block|{
name|String
index|[]
name|tokenValue
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
name|TOKEN_KEY
argument_list|)
decl_stmt|;
return|return
name|ArrayUtils
operator|.
name|contains
argument_list|(
name|tokenValue
argument_list|,
name|token
argument_list|)
return|;
block|}
comment|/**      * Obtain reference to token file      */
specifier|private
name|void
name|obtainTokenFileReference
parameter_list|()
block|{
if|if
condition|(
name|tokenFile
operator|==
literal|null
condition|)
block|{
name|tokenFile
operator|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
name|TOKEN_FILE
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Token file:  %s"
argument_list|,
name|tokenFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Get token from file, create if not existent. Data is read for each call so the file can be updated run-time.      *      * @return Toke for servlet      */
specifier|private
name|String
name|getToken
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|String
name|token
init|=
literal|null
decl_stmt|;
comment|// Read if possible
if|if
condition|(
name|tokenFile
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
init|(
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|tokenFile
argument_list|)
init|)
block|{
name|props
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|token
operator|=
name|props
operator|.
name|getProperty
argument_list|(
name|TOKEN_KEY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Create and write when needed
if|if
condition|(
operator|!
name|tokenFile
operator|.
name|exists
argument_list|()
operator|||
name|token
operator|==
literal|null
condition|)
block|{
comment|// Create random token
name|token
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// Set value to properties
name|props
operator|.
name|setProperty
argument_list|(
name|TOKEN_KEY
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// Write data to file
try|try
init|(
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tokenFile
argument_list|)
init|)
block|{
name|props
operator|.
name|store
argument_list|(
name|os
argument_list|,
literal|"JMXservlet token: http://localhost:8080/exist/status?token=......"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Token written to file %s"
argument_list|,
name|tokenFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

