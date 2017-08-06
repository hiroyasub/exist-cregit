begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
operator|.
name|action
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|org
operator|.
name|exist
operator|.
name|TestUtils
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
name|FileUtils
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
name|MimeTable
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
name|CollectionManagementServiceImpl
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
name|concurrent
operator|.
name|DBUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_class
specifier|public
class|class
name|CreateCollectionAction
extends|extends
name|Action
block|{
specifier|private
name|int
name|collectionCnt
init|=
literal|0
decl_stmt|;
specifier|public
name|CreateCollectionAction
parameter_list|(
name|String
name|collectionPath
parameter_list|,
name|String
name|resourceName
parameter_list|)
block|{
name|super
argument_list|(
name|collectionPath
argument_list|,
name|resourceName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
name|col
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionPath
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Collection
name|target
init|=
name|DBUtils
operator|.
name|addCollection
argument_list|(
name|col
argument_list|,
literal|"C"
operator|+
operator|++
name|collectionCnt
argument_list|)
decl_stmt|;
name|addFiles
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|String
name|resources
index|[]
init|=
name|target
operator|.
name|listResources
argument_list|()
decl_stmt|;
name|CollectionManagementServiceImpl
name|mgt
init|=
operator|(
name|CollectionManagementServiceImpl
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|copy
init|=
name|DBUtils
operator|.
name|addCollection
argument_list|(
name|col
argument_list|,
literal|"CC"
operator|+
name|collectionCnt
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|resources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|mgt
operator|.
name|copyResource
argument_list|(
name|target
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|resources
index|[
name|i
index|]
argument_list|,
name|copy
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|resources
operator|=
name|copy
operator|.
name|listResources
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|addFiles
parameter_list|(
specifier|final
name|Collection
name|col
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
specifier|final
name|Path
name|d
init|=
name|TestUtils
operator|.
name|shakespeareSamples
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|Files
operator|.
name|isReadable
argument_list|(
name|d
argument_list|)
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|d
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot read directory: "
operator|+
name|d
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|files
init|=
name|FileUtils
operator|.
name|list
argument_list|(
name|d
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Path
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|isRegularFile
argument_list|(
name|file
argument_list|)
condition|)
block|{
if|if
condition|(
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|isXMLContent
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
argument_list|)
condition|)
block|{
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|col
argument_list|,
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

