begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|methods
package|;
end_package

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
name|http
operator|.
name|webdav
operator|.
name|WebDAVMethod
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
comment|/**  * Abstract base class for all WebDAV methods.  *   * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractWebDAVMethod
implements|implements
name|WebDAVMethod
block|{
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AbstractWebDAVMethod
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// common error messages
specifier|final
specifier|static
name|String
name|READ_PERMISSION_DENIED
init|=
literal|"Not allowed to read resource"
decl_stmt|;
specifier|final
specifier|static
name|String
name|NOT_FOUND_ERR
init|=
literal|"No resource or collection found"
decl_stmt|;
specifier|protected
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|AbstractWebDAVMethod
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
block|}
end_class

end_unit

