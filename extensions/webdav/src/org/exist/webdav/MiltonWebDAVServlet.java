begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webdav
package|;
end_package

begin_import
import|import
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|MiltonServlet
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
comment|/**  *  Wrapper around the MiltonServlet for post-configuring the framework.  *   * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|MiltonWebDAVServlet
extends|extends
name|MiltonServlet
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
name|MiltonWebDAVServlet
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing wrapper servlet"
argument_list|)
expr_stmt|;
comment|// Initialize Milton
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// Retrieve parameters, set to FALSE if not existent
name|String
name|enableInitParameter
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"enable.expect.continue"
argument_list|)
decl_stmt|;
if|if
condition|(
name|enableInitParameter
operator|==
literal|null
condition|)
block|{
name|enableInitParameter
operator|=
literal|"FALSE"
expr_stmt|;
block|}
comment|// Calculate effective value
name|boolean
name|enableExpectContinue
init|=
literal|"TRUE"
operator|.
name|equalsIgnoreCase
argument_list|(
name|enableInitParameter
argument_list|)
decl_stmt|;
comment|// Pass value to Milton
name|httpManager
operator|.
name|setEnableExpectContinue
argument_list|(
name|enableExpectContinue
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set 'Enable Expect Continue' to "
operator|+
name|enableExpectContinue
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

