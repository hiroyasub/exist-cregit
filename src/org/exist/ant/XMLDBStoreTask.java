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
name|ArrayList
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
name|Project
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
name|exist
operator|.
name|storage
operator|.
name|DBBroker
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
name|xquery
operator|.
name|Constants
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

begin_comment
comment|/**  * An Ant task to store a set of files into eXist.  *<p/>  * The task expects a nested fileset element. The files  * selected by the fileset will be stored into the database.  *<p/>  * New collections can be created as needed. It is also possible  * to specify that files relative to the base  * directory should be stored into subcollections of the root  * collection, where the relative path of the directory corresponds  * to the relative path of the subcollections.  *  * @author wolf  *<p/>  *         slightly modified by:  * @author peter.klotz@blue-elephant-systems.com  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBStoreTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|File
name|mimeTypesFile
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|srcFile
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|targetFile
init|=
literal|null
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|FileSet
argument_list|>
name|fileSetList
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
literal|null
decl_stmt|;
specifier|private
name|String
name|defaultMimeType
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|forceMimeType
init|=
literal|null
decl_stmt|;
specifier|private
name|MimeTable
name|mtable
init|=
literal|null
decl_stmt|;
comment|/* (non-Javadoc)      * @see org.apache.tools.ant.Task#execute()      */
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
operator|new
name|BuildException
argument_list|(
literal|"you have to specify an XMLDB collection URI"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fileSetList
operator|==
literal|null
operator|&&
name|srcFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"no file set specified"
argument_list|)
throw|;
block|}
name|registerDatabase
argument_list|()
expr_stmt|;
try|try
block|{
name|int
name|p
init|=
name|uri
operator|.
name|indexOf
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"invalid uri: '"
operator|+
name|uri
operator|+
literal|"'"
argument_list|)
throw|;
block|}
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
block|{
name|path
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
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
block|}
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
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
name|user
argument_list|,
name|password
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
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Collection "
operator|+
name|uri
operator|+
literal|" could not be found."
decl_stmt|;
if|if
condition|(
name|failonerror
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
else|else
block|{
name|log
argument_list|(
name|msg
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Resource
name|res
decl_stmt|;
name|File
name|file
decl_stmt|;
name|Collection
name|col
init|=
name|root
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
if|if
condition|(
name|srcFile
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Storing "
operator|+
name|srcFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MimeType
name|mime
init|=
name|getMimeTable
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|srcFile
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|baseMimeType
decl_stmt|;
if|if
condition|(
name|forceMimeType
operator|!=
literal|null
condition|)
block|{
name|baseMimeType
operator|=
name|forceMimeType
expr_stmt|;
block|}
if|else if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
name|baseMimeType
operator|=
name|mime
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|baseMimeType
operator|=
name|defaultMimeType
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
name|mime
operator|=
operator|(
name|baseMimeType
operator|!=
literal|null
operator|)
condition|?
operator|(
operator|new
name|MimeType
argument_list|(
name|baseMimeType
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|)
operator|)
else|:
name|MimeType
operator|.
name|XML_TYPE
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"binary"
argument_list|)
condition|)
block|{
name|mime
operator|=
operator|(
name|baseMimeType
operator|!=
literal|null
operator|)
condition|?
operator|(
operator|new
name|MimeType
argument_list|(
name|baseMimeType
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|)
operator|)
else|:
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
block|}
comment|// single file
if|if
condition|(
name|mime
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Cannot guess mime-type kind for "
operator|+
name|srcFile
operator|.
name|getName
argument_list|()
operator|+
literal|". Treating it as a binary."
decl_stmt|;
name|log
argument_list|(
name|msg
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|mime
operator|=
operator|(
name|baseMimeType
operator|!=
literal|null
operator|)
condition|?
operator|(
operator|new
name|MimeType
argument_list|(
name|baseMimeType
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|)
operator|)
else|:
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
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
if|if
condition|(
name|targetFile
operator|==
literal|null
condition|)
block|{
name|targetFile
operator|=
name|srcFile
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Creating resource "
operator|+
name|targetFile
operator|+
literal|" in collection "
operator|+
name|col
operator|.
name|getName
argument_list|()
operator|+
literal|" of type "
operator|+
name|resourceType
operator|+
literal|" with mime-type: "
operator|+
name|mime
operator|.
name|getName
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|res
operator|=
name|col
operator|.
name|createResource
argument_list|(
name|targetFile
argument_list|,
name|resourceType
argument_list|)
expr_stmt|;
if|if
condition|(
name|srcFile
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// note: solves bug id 2429889 when this task hits empty files
block|}
else|else
block|{
name|res
operator|.
name|setContent
argument_list|(
name|srcFile
argument_list|)
expr_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|res
operator|)
operator|.
name|setMimeType
argument_list|(
name|mime
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|col
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|FileSet
name|fileSet
range|:
name|fileSetList
control|)
block|{
name|log
argument_list|(
literal|"Storing fileset"
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
comment|// using fileset
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
name|includedFiles
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
name|includedFiles
operator|.
name|length
operator|+
literal|" files.\n"
argument_list|)
expr_stmt|;
name|File
name|baseDir
init|=
name|scanner
operator|.
name|getBasedir
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|included
range|:
name|includedFiles
control|)
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|included
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Storing "
operator|+
name|included
operator|+
literal|" ...\n"
argument_list|)
expr_stmt|;
comment|//TODO : use dedicated function in XmldbURI
comment|// check whether the relative file path contains file seps
name|p
operator|=
name|included
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
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|relDir
operator|=
name|included
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
comment|// It's necessary to do this translation on Windows, and possibly MacOS:
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
comment|//TODO : use dedicated function in XmldbURI
name|col
operator|=
name|mkcol
argument_list|(
name|root
argument_list|,
name|baseURI
argument_list|,
name|DBBroker
operator|.
name|ROOT_COLLECTION
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
else|else
block|{
comment|// No file separator found in resource name, reset col to the root collection
name|col
operator|=
name|root
expr_stmt|;
block|}
name|MimeType
name|currentMime
init|=
name|getMimeTable
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|currentBaseMimeType
decl_stmt|;
if|if
condition|(
name|forceMimeType
operator|!=
literal|null
condition|)
block|{
name|currentBaseMimeType
operator|=
name|forceMimeType
expr_stmt|;
block|}
if|else if
condition|(
name|currentMime
operator|!=
literal|null
condition|)
block|{
name|currentBaseMimeType
operator|=
name|currentMime
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentBaseMimeType
operator|=
name|defaultMimeType
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
name|currentMime
operator|=
operator|(
name|currentBaseMimeType
operator|!=
literal|null
operator|)
condition|?
operator|(
operator|new
name|MimeType
argument_list|(
name|currentBaseMimeType
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|)
operator|)
else|:
name|MimeType
operator|.
name|XML_TYPE
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"binary"
argument_list|)
condition|)
block|{
name|currentMime
operator|=
operator|(
name|currentBaseMimeType
operator|!=
literal|null
operator|)
condition|?
operator|(
operator|new
name|MimeType
argument_list|(
name|currentBaseMimeType
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|)
operator|)
else|:
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
block|}
if|if
condition|(
name|currentMime
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Cannot find mime-type kind for "
operator|+
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|". Treating it as a binary."
decl_stmt|;
name|log
argument_list|(
name|msg
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|currentMime
operator|=
operator|(
name|currentBaseMimeType
operator|!=
literal|null
operator|)
condition|?
operator|(
operator|new
name|MimeType
argument_list|(
name|currentBaseMimeType
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|)
operator|)
else|:
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
name|resourceType
operator|=
name|currentMime
operator|.
name|isXMLType
argument_list|()
condition|?
literal|"XMLResource"
else|:
literal|"BinaryResource"
expr_stmt|;
name|log
argument_list|(
literal|"Creating resource "
operator|+
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|" in collection "
operator|+
name|col
operator|.
name|getName
argument_list|()
operator|+
literal|" of type "
operator|+
name|resourceType
operator|+
literal|" with mime-type: "
operator|+
name|currentMime
operator|.
name|getName
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|res
operator|=
name|col
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
operator|(
operator|(
name|EXistResource
operator|)
name|res
operator|)
operator|.
name|setMimeType
argument_list|(
name|currentMime
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|col
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"XMLDB exception caught: "
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
operator|new
name|BuildException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
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
comment|/**     This method allows more than one Fileset per store task!      */
specifier|public
name|void
name|addFileset
parameter_list|(
name|FileSet
name|set
parameter_list|)
block|{
if|if
condition|(
name|fileSetList
operator|==
literal|null
condition|)
block|{
name|fileSetList
operator|=
operator|new
name|ArrayList
argument_list|<
name|FileSet
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|fileSetList
operator|.
name|add
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSrcFile
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|this
operator|.
name|srcFile
operator|=
name|file
expr_stmt|;
block|}
specifier|public
name|void
name|setTargetFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|targetFile
operator|=
name|name
expr_stmt|;
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
name|setCreatesubcollections
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
name|setMimeTypesFile
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|this
operator|.
name|mimeTypesFile
operator|=
name|file
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
specifier|public
name|void
name|setDefaultMimeType
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
name|this
operator|.
name|defaultMimeType
operator|=
name|mimeType
expr_stmt|;
block|}
specifier|public
name|void
name|setForceMimeType
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
name|this
operator|.
name|forceMimeType
operator|=
name|mimeType
expr_stmt|;
block|}
specifier|private
specifier|final
name|MimeTable
name|getMimeTable
parameter_list|()
throws|throws
name|BuildException
block|{
if|if
condition|(
name|mtable
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|mimeTypesFile
operator|!=
literal|null
operator|&&
name|mimeTypesFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
argument_list|(
literal|"Trying to use MIME Types file "
operator|+
name|mimeTypesFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|mtable
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|(
name|mimeTypesFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
argument_list|(
literal|"Using default MIME Types resources"
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|mtable
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|mtable
return|;
block|}
block|}
end_class

end_unit

