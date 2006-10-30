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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|util
operator|.
name|Date
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
name|xml
operator|.
name|DOMConfigurator
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
name|Configuration
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
name|DatabaseConfigurationException
import|;
end_import

begin_comment
comment|/**  * Helper servlet for initializing the log4j framework in a webcontainer.  */
end_comment

begin_class
specifier|public
class|class
name|Log4jInit
extends|extends
name|HttpServlet
block|{
specifier|private
name|String
name|getTimestamp
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|convertLogFile
parameter_list|(
name|File
name|srcConfig
parameter_list|,
name|File
name|destConfig
parameter_list|,
name|File
name|logDir
parameter_list|)
block|{
comment|// Step 1 read config file into memory
name|String
name|srcDoc
init|=
literal|"not initialized"
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|srcConfig
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
name|srcDoc
operator|=
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Step 2 ; substitute Patterns
name|String
name|destDoc
init|=
name|srcDoc
operator|.
name|replaceAll
argument_list|(
literal|"loggerdir"
argument_list|,
name|logDir
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Step 3 ; write back to file
try|try
block|{
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|destDoc
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|destConfig
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|bais
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|bais
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Initialize servlet for log4j purposes in servlet container (war file).      */
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
comment|// We need to check how eXist is running. If eXist is started in a
comment|// servlet container like Tomcat, then initialization *is* needed.
comment|//
comment|// If eXist is started in its own jetty server, the logging is
comment|// already initialized. All can, must and shall be skipped then.
if|if
condition|(
operator|!
name|isInWarFile
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Logging already initialized. Skipping..."
argument_list|)
expr_stmt|;
return|return;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"============= eXist Initialization ============="
argument_list|)
expr_stmt|;
comment|// Get data from web.xml
name|String
name|file
init|=
name|getInitParameter
argument_list|(
literal|"log4j-init-file"
argument_list|)
decl_stmt|;
comment|// Get path where eXist is running
name|String
name|existDir
init|=
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
comment|// Define location of logfiles
name|File
name|logsdir
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"WEB-INF/logs"
argument_list|)
decl_stmt|;
name|logsdir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getTimestamp
argument_list|()
operator|+
literal|" - eXist logs dir="
operator|+
name|logsdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get log4j configuration file
name|File
name|srcConfigFile
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
name|file
argument_list|)
decl_stmt|;
comment|// Convert
name|File
name|log4jConfigFile
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"WEB-INF/TMPfile.xml"
argument_list|)
decl_stmt|;
name|convertLogFile
argument_list|(
name|srcConfigFile
argument_list|,
name|log4jConfigFile
argument_list|,
name|logsdir
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getTimestamp
argument_list|()
operator|+
literal|" - eXist log4j configuration="
operator|+
name|log4jConfigFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Configure log4j
name|DOMConfigurator
operator|.
name|configure
argument_list|(
name|log4jConfigFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Setup exist
name|File
name|eXistConfigFile
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"WEB-INF/conf.xml"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getTimestamp
argument_list|()
operator|+
literal|" - eXist-DB configuration="
operator|+
name|eXistConfigFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|eXistConfigFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|DatabaseConfigurationException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"================================================"
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Check wether exist runs in Servlet container (as war file).      * @return TRUE if exist runs in servlet container.      */
specifier|public
name|boolean
name|isInWarFile
parameter_list|()
block|{
name|boolean
name|retVal
init|=
literal|true
decl_stmt|;
if|if
condition|(
operator|new
name|File
argument_list|(
name|Configuration
operator|.
name|getExistHome
argument_list|()
argument_list|,
literal|"lib/core"
argument_list|)
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|retVal
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|retVal
return|;
block|}
comment|/**      *  Empty method.      *      * @param req HTTP Request object      * @param res HTTP Response object      */
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
block|{
comment|//
block|}
block|}
end_class

end_unit

