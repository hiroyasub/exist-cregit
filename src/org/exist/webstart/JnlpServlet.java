begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webstart
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

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
name|net
operator|.
name|SocketException
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Dedicated servlet for Webstart.  */
end_comment

begin_class
specifier|public
class|class
name|JnlpServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|JnlpServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|JnlpJarFiles
name|jf
init|=
literal|null
decl_stmt|;
specifier|private
name|JnlpHelper
name|jh
init|=
literal|null
decl_stmt|;
comment|/**      * Initialize servlet.cd      */
specifier|public
name|void
name|init
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing JNLP servlet"
argument_list|)
expr_stmt|;
name|String
name|realPath
init|=
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|realPath
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"getServletContext().getRealPath() did not return a "
operator|+
literal|"value. Webstart is not available."
argument_list|)
expr_stmt|;
block|}
name|File
name|contextRoot
init|=
operator|new
name|File
argument_list|(
name|realPath
argument_list|)
decl_stmt|;
name|jh
operator|=
operator|new
name|JnlpHelper
argument_list|(
name|contextRoot
argument_list|)
expr_stmt|;
name|jf
operator|=
operator|new
name|JnlpJarFiles
argument_list|(
name|jh
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|stripFilename
parameter_list|(
name|String
name|URI
parameter_list|)
block|{
name|int
name|lastPos
init|=
name|URI
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
return|return
name|URI
operator|.
name|substring
argument_list|(
name|lastPos
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/**      *  Handle webstart request for JNLP file, jar file or image.      *      * @param request   Object representing http request.      * @param response  Object representing http response.      * @throws ServletException  Standard servlet exception      * @throws IOException       Standard IO exception      */
specifier|public
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
try|try
block|{
name|JnlpWriter
name|jw
init|=
operator|new
name|JnlpWriter
argument_list|()
decl_stmt|;
name|String
name|URI
init|=
name|request
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Requested URI="
operator|+
name|URI
argument_list|)
expr_stmt|;
if|if
condition|(
name|URI
operator|.
name|endsWith
argument_list|(
literal|".jnlp"
argument_list|)
condition|)
block|{
name|jw
operator|.
name|writeJnlpXML
argument_list|(
name|jf
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|URI
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
name|String
name|filename
init|=
name|stripFilename
argument_list|(
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|)
decl_stmt|;
name|jw
operator|.
name|sendJar
argument_list|(
name|jf
argument_list|,
name|filename
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|URI
operator|.
name|endsWith
argument_list|(
literal|".gif"
argument_list|)
operator|||
name|URI
operator|.
name|endsWith
argument_list|(
literal|".jpg"
argument_list|)
condition|)
block|{
name|String
name|filename
init|=
name|stripFilename
argument_list|(
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|)
decl_stmt|;
name|jw
operator|.
name|sendImage
argument_list|(
name|jh
argument_list|,
name|jf
argument_list|,
name|filename
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Invalid filename extension."
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
literal|"Invalid filename extension."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"An error occurred: "
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
block|}
block|}
end_class

end_unit

