begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|DirectoryScanner
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
name|types
operator|.
name|FileSet
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
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_comment
comment|/**  * An Ant task to store a set of files into eXist.  *   * The task expects a nested fileset element. The files  * selected by the fileset will be stored into the database.  *   * New collections can be created as needed. It is also possible   * to specify that files relative to the base  * directory should be stored into subcollections of the root  * collection, where the relative path of the directory corresponds  * to the relative path of the subcollections.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBStoreTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|FileSet
name|fileSet
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|createCollection
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|createSubcollections
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|type
init|=
literal|"xml"
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.apache.tools.ant.Task#execute() 	 */
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
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"you have to specify an XMLDB collection URI"
argument_list|)
throw|;
if|if
condition|(
name|fileSet
operator|==
literal|null
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"no file set specified"
argument_list|)
throw|;
name|registerDatabase
argument_list|()
expr_stmt|;
name|int
name|p
init|=
name|uri
operator|.
name|indexOf
argument_list|(
literal|"/db"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|<
literal|0
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"invalid uri: "
operator|+
name|uri
argument_list|)
throw|;
try|try
block|{
name|String
name|baseURI
init|=
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|uri
operator|.
name|length
argument_list|()
operator|-
literal|3
condition|)
name|path
operator|=
literal|""
expr_stmt|;
else|else
name|path
operator|=
name|uri
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|3
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|createCollection
condition|)
block|{
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
operator|+
literal|"/db"
argument_list|)
expr_stmt|;
name|root
operator|=
name|mkcol
argument_list|(
name|root
argument_list|,
name|baseURI
argument_list|,
literal|"/db"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
else|else
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"collection "
operator|+
name|uri
operator|+
literal|" not found"
argument_list|)
throw|;
name|DirectoryScanner
name|scanner
init|=
name|fileSet
operator|.
name|getDirectoryScanner
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|scanner
operator|.
name|scan
argument_list|()
expr_stmt|;
name|String
index|[]
name|files
init|=
name|scanner
operator|.
name|getIncludedFiles
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"Found "
operator|+
name|files
operator|.
name|length
operator|+
literal|" files.\n"
argument_list|)
expr_stmt|;
name|Collection
name|collection
init|=
name|root
decl_stmt|;
name|Resource
name|res
decl_stmt|;
name|File
name|file
decl_stmt|;
name|String
name|relDir
decl_stmt|,
name|prevDir
init|=
literal|null
decl_stmt|,
name|resourceType
init|=
literal|"XMLResource"
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
name|file
operator|=
operator|new
name|File
argument_list|(
name|scanner
operator|.
name|getBasedir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Storing "
operator|+
name|files
index|[
name|i
index|]
operator|+
literal|" ...\n"
argument_list|)
expr_stmt|;
name|p
operator|=
name|files
index|[
name|i
index|]
operator|.
name|lastIndexOf
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|>
operator|-
literal|1
condition|)
block|{
name|relDir
operator|=
name|files
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|createSubcollections
operator|&&
operator|(
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
operator|)
condition|)
block|{
name|collection
operator|=
name|mkcol
argument_list|(
name|root
argument_list|,
name|baseURI
argument_list|,
literal|"/db"
operator|+
name|path
argument_list|,
name|relDir
argument_list|)
expr_stmt|;
name|prevDir
operator|=
name|relDir
expr_stmt|;
block|}
block|}
name|resourceType
operator|=
name|type
operator|.
name|equals
argument_list|(
literal|"binary"
argument_list|)
condition|?
literal|"BinaryResource"
else|:
literal|"XMLResource"
expr_stmt|;
name|res
operator|=
name|collection
operator|.
name|createResource
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
name|resourceType
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"XMLDB exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|FileSet
name|createFileSet
parameter_list|()
block|{
name|this
operator|.
name|fileSet
operator|=
operator|new
name|FileSet
argument_list|()
expr_stmt|;
return|return
name|fileSet
return|;
block|}
specifier|public
name|void
name|setCreatecollection
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
name|this
operator|.
name|createCollection
operator|=
name|create
expr_stmt|;
block|}
specifier|public
name|void
name|setSubcollections
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
name|this
operator|.
name|createSubcollections
operator|=
name|create
expr_stmt|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|private
specifier|final
name|Collection
name|mkcol
parameter_list|(
name|Collection
name|root
parameter_list|,
name|String
name|baseURI
parameter_list|,
name|String
name|path
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
name|root
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
name|path
operator|=
name|path
operator|+
literal|'/'
operator|+
name|token
expr_stmt|;
name|c
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
operator|+
name|path
argument_list|,
name|user
argument_list|,
name|password
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
name|log
argument_list|(
literal|"Created collection "
operator|+
name|current
operator|.
name|getName
argument_list|()
operator|+
literal|'.'
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

