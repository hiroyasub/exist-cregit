begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
comment|/**      * Initialize servlet for log4j purposes.      */
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
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
literal|"eXist logs dir="
operator|+
name|logsdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"logger.dir"
argument_list|,
name|logsdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get log4j configuration file
name|File
name|configFile
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"eXist log4j configuration="
operator|+
name|configFile
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
name|configFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
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

