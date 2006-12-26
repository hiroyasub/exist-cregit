begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|actions
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|EXistResource
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
name|DirectoryScanner
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
name|util
operator|.
name|MimeType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|Runner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|AbstractAction
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
name|Resource
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
name|modules
operator|.
name|CollectionManagementService
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
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_class
specifier|public
class|class
name|StoreFromFile
extends|extends
name|AbstractAction
block|{
specifier|private
name|String
name|collectionPath
decl_stmt|;
specifier|private
name|String
name|dir
decl_stmt|;
specifier|private
name|String
name|includes
decl_stmt|;
specifier|private
name|String
name|mimeType
init|=
literal|"text/xml"
decl_stmt|;
specifier|public
name|void
name|configure
parameter_list|(
name|Runner
name|runner
parameter_list|,
name|Action
name|parent
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
name|super
operator|.
name|configure
argument_list|(
name|runner
argument_list|,
name|parent
argument_list|,
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"collection"
argument_list|)
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
name|StoreFromFile
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" requires an attribute 'collection'"
argument_list|)
throw|;
name|collectionPath
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"dir"
argument_list|)
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
name|StoreFromFile
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" requires an attribute 'dir'"
argument_list|)
throw|;
name|dir
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"dir"
argument_list|)
expr_stmt|;
name|includes
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"includes"
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"mime-type"
argument_list|)
condition|)
name|mimeType
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"mime-type"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|EXistException
block|{
name|Collection
name|collection
init|=
name|connection
operator|.
name|getCollection
argument_list|(
name|collectionPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"collection "
operator|+
name|collectionPath
operator|+
literal|" not found"
argument_list|)
throw|;
name|String
name|resourceType
init|=
name|getResourceType
argument_list|()
decl_stmt|;
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|DirectoryScanner
operator|.
name|scanDir
argument_list|(
name|baseDir
argument_list|,
name|includes
argument_list|)
decl_stmt|;
name|Collection
name|col
init|=
name|collection
decl_stmt|;
name|String
name|relDir
decl_stmt|,
name|prevDir
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|relPath
init|=
name|files
index|[
name|j
index|]
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
name|baseDir
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|p
init|=
name|relPath
operator|.
name|lastIndexOf
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|)
decl_stmt|;
name|relDir
operator|=
name|relPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|relDir
operator|=
name|relDir
operator|.
name|replace
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
if|if
condition|(
name|prevDir
operator|==
literal|null
operator|||
operator|(
operator|!
name|relDir
operator|.
name|equals
argument_list|(
name|prevDir
argument_list|)
operator|)
condition|)
block|{
name|col
operator|=
name|makeColl
argument_list|(
name|collection
argument_list|,
name|relDir
argument_list|)
expr_stmt|;
name|prevDir
operator|=
name|relDir
expr_stmt|;
block|}
comment|//TODO  : these probably need to be encoded
name|Resource
name|resource
init|=
name|col
operator|.
name|createResource
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|resourceType
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|files
index|[
name|j
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"BinaryResource"
operator|.
name|equals
argument_list|(
name|resourceType
argument_list|)
condition|)
operator|(
operator|(
name|EXistResource
operator|)
name|resource
operator|)
operator|.
name|setMimeType
argument_list|(
name|mimeType
argument_list|)
expr_stmt|;
name|col
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Storing "
operator|+
name|col
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getResourceType
parameter_list|()
block|{
name|String
name|resourceType
init|=
literal|"XMLResource"
decl_stmt|;
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentType
argument_list|(
name|mimeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
name|resourceType
operator|=
name|mime
operator|.
name|isXMLType
argument_list|()
condition|?
literal|"XMLResource"
else|:
literal|"BinaryResource"
expr_stmt|;
return|return
name|resourceType
return|;
block|}
specifier|private
name|Collection
name|makeColl
parameter_list|(
name|Collection
name|parentColl
parameter_list|,
name|String
name|relPath
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|CollectionManagementService
name|mgtService
decl_stmt|;
name|Collection
name|current
init|=
name|parentColl
decl_stmt|,
name|c
decl_stmt|;
name|String
name|token
decl_stmt|;
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|relPath
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|token
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|c
operator|=
name|current
operator|.
name|getChildCollection
argument_list|(
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|mgtService
operator|=
operator|(
name|CollectionManagementService
operator|)
name|current
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|current
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
else|else
name|current
operator|=
name|c
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
block|}
end_class

end_unit

