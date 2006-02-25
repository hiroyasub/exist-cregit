begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|util
operator|.
name|Enumeration
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
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|WebDAV
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_comment
comment|/**  * Provides a WebDAV interface to the database. All WebDAV requests  * are delegated to the {@link org.exist.http.webdav.WebDAV} class.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|WebDAVServlet
extends|extends
name|HttpServlet
block|{
specifier|private
name|WebDAV
name|webdav
decl_stmt|;
comment|/** id of the database registred against the BrokerPool */
specifier|protected
name|String
name|databaseid
init|=
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
decl_stmt|;
comment|/* (non-Javadoc)          * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)          */
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
comment|//<frederic.glorieux@ajlsm.com> to allow multi-instance webdav server,
comment|// use a databaseid everywhere
name|String
name|id
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"database-id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
name|this
operator|.
name|databaseid
operator|=
name|id
expr_stmt|;
name|int
name|authMethod
init|=
name|WebDAV
operator|.
name|DIGEST_AUTH
decl_stmt|;
name|String
name|param
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"authentication"
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|!=
literal|null
operator|&&
literal|"basic"
operator|.
name|equalsIgnoreCase
argument_list|(
name|param
argument_list|)
condition|)
name|authMethod
operator|=
name|WebDAV
operator|.
name|BASIC_AUTH
expr_stmt|;
name|webdav
operator|=
operator|new
name|WebDAV
argument_list|(
name|authMethod
argument_list|,
name|this
operator|.
name|databaseid
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)          * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)          */
specifier|protected
name|void
name|service
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
comment|//		dumpHeaders(request);
name|webdav
operator|.
name|process
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|dumpHeaders
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-------------------------------------------------------"
argument_list|)
expr_stmt|;
for|for
control|(
name|Enumeration
name|e
init|=
name|request
operator|.
name|getHeaderNames
argument_list|()
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|String
name|header
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|header
operator|+
literal|" = "
operator|+
name|request
operator|.
name|getHeader
argument_list|(
name|header
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

