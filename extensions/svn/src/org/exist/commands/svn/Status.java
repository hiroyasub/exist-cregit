begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|commands
operator|.
name|svn
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|plugin
operator|.
name|command
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|WorkingCopy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|ISVNStatusHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|SVNClientManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|SVNStatusClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|SVNWCUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNDepth
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|io
operator|.
name|svn
operator|.
name|SVNRepositoryFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|wc
operator|.
name|SVNRevision
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Status
extends|extends
name|AbstractCommand
block|{
specifier|public
name|Status
parameter_list|()
block|{
name|names
operator|=
operator|new
name|String
index|[]
block|{
literal|"status"
block|,
literal|"st"
block|}
expr_stmt|;
block|}
specifier|public
name|void
name|process
parameter_list|(
name|XmldbURI
name|collection
parameter_list|,
name|String
index|[]
name|params
parameter_list|)
throws|throws
name|CommandException
block|{
name|String
name|userName
init|=
literal|""
decl_stmt|;
name|String
name|password
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|userName
operator|=
name|params
index|[
literal|0
index|]
expr_stmt|;
name|password
operator|=
name|params
index|[
literal|1
index|]
expr_stmt|;
block|}
name|WorkingCopy
name|wc
init|=
operator|new
name|WorkingCopy
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|SVNRepositoryFactoryImpl
operator|.
name|setup
argument_list|()
expr_stmt|;
name|SVNClientManager
name|manager
init|=
name|SVNClientManager
operator|.
name|newInstance
argument_list|(
name|SVNWCUtil
operator|.
name|createDefaultOptions
argument_list|(
literal|false
argument_list|)
argument_list|,
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|SVNStatusClient
name|statusClient
init|=
name|manager
operator|.
name|getStatusClient
argument_list|()
decl_stmt|;
comment|//		SVNWCClient wcClient = manager.getWCClient();
try|try
block|{
name|statusClient
operator|.
name|doStatus
argument_list|(
operator|new
name|Resource
argument_list|(
name|collection
argument_list|)
argument_list|,
name|SVNRevision
operator|.
name|HEAD
argument_list|,
name|SVNDepth
operator|.
name|getInfinityOrFilesDepth
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
operator|new
name|AddStatusHandler
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|CommandException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|AddStatusHandler
implements|implements
name|ISVNStatusHandler
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleStatus
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|SVNStatus
name|status
parameter_list|)
throws|throws
name|SVNException
block|{
name|out
argument_list|()
operator|.
name|println
argument_list|(
name|status
operator|.
name|getContentsStatus
argument_list|()
operator|.
name|getCode
argument_list|()
operator|+
literal|" "
operator|+
name|status
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

