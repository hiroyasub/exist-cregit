begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|Backup
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_comment
comment|/**  * DOCUMENT ME!  *  * @author  wolf  */
end_comment

begin_class
specifier|public
class|class
name|BackupTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|String
name|dir
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|deduplicateBlobs
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
literal|"you have to specify an XMLDB collection URI"
argument_list|)
operator|)
throw|;
block|}
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
literal|"missing required parameter: dir"
argument_list|)
operator|)
throw|;
block|}
name|registerDatabase
argument_list|()
expr_stmt|;
name|log
argument_list|(
literal|"Creating backup of collection: "
operator|+
name|uri
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Backup directory: "
operator|+
name|dir
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Backup
name|backup
init|=
operator|new
name|Backup
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|dir
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
argument_list|,
literal|null
argument_list|,
name|deduplicateBlobs
argument_list|)
decl_stmt|;
name|backup
operator|.
name|backup
argument_list|(
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
specifier|final
name|String
name|msg
init|=
literal|"Exception during backup: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|failonerror
condition|)
block|{
throw|throw
operator|(
operator|new
name|BuildException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
operator|)
throw|;
block|}
else|else
block|{
name|log
argument_list|(
name|msg
argument_list|,
name|e
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Set the directory.      *      * @param dir the directory.      */
specifier|public
name|void
name|setDir
parameter_list|(
name|String
name|dir
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
specifier|public
name|void
name|setDeduplicateBlobs
parameter_list|(
specifier|final
name|boolean
name|deduplicateBlobs
parameter_list|)
block|{
name|this
operator|.
name|deduplicateBlobs
operator|=
name|deduplicateBlobs
expr_stmt|;
block|}
block|}
end_class

end_unit

