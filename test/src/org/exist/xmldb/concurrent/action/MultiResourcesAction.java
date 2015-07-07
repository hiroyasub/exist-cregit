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
name|File
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|MultiResourcesAction
extends|extends
name|Action
block|{
specifier|private
name|String
name|dirPath
decl_stmt|;
comment|/**      *       *       * @param dirPath       * @param collectionPath       */
specifier|public
name|MultiResourcesAction
parameter_list|(
name|String
name|dirPath
parameter_list|,
name|String
name|collectionPath
parameter_list|)
block|{
name|super
argument_list|(
name|collectionPath
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|dirPath
operator|=
name|dirPath
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.test.concurrent.Action#execute()      */
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
name|addFiles
argument_list|(
name|col
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|/** 	 * @param files 	 * @param col 	 * @throws XMLDBException 	 */
specifier|private
name|void
name|addFiles
parameter_list|(
name|Collection
name|col
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|File
name|d
init|=
operator|new
name|File
argument_list|(
name|dirPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|d
operator|.
name|canRead
argument_list|()
operator|&&
name|d
operator|.
name|isDirectory
argument_list|()
operator|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot read directory: "
operator|+
name|dirPath
argument_list|)
throw|;
name|File
index|[]
name|files
init|=
name|d
operator|.
name|listFiles
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|files
index|[
name|i
index|]
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|col
argument_list|,
name|files
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

