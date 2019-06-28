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
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|PropertyHelper
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
name|ResourceIterator
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
name|ResourceSet
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
name|XMLResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|BinarySource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|FileSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|URLSource
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
name|serializer
operator|.
name|SAXSerializer
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
name|serializer
operator|.
name|SerializerPool
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
name|EXistXQueryService
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Ant task to execute an XQuery.  *  * The query is either passed as nested text in the element, or via an attribute "query" or via a URL or via a query file. External variables  * declared in the XQuery can be set via one or more nested&lt;variable&gt; elements.  *  * @author peter.klotz@blue-elephant-systems.com  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBXQueryTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|String
name|text
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|queryUri
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|query
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|queryFile
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|destDir
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|outputproperty
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Variable
argument_list|>
name|variables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
operator|new
name|BuildException
argument_list|(
literal|"you have to specify an XMLDB collection URI"
argument_list|)
throw|;
block|}
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
specifier|final
name|PropertyHelper
name|helper
init|=
name|PropertyHelper
operator|.
name|getPropertyHelper
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|query
operator|=
name|helper
operator|.
name|replaceProperties
argument_list|(
literal|null
argument_list|,
name|text
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryFile
operator|==
literal|null
operator|&&
name|query
operator|==
literal|null
operator|&&
name|queryUri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"you have to specify a query either as attribute, text, URI or in a file"
argument_list|)
throw|;
block|}
name|registerDatabase
argument_list|()
expr_stmt|;
try|try
block|{
name|log
argument_list|(
literal|"Get base collection: "
operator|+
name|uri
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|base
init|=
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
decl_stmt|;
if|if
condition|(
name|base
operator|==
literal|null
condition|)
block|{
specifier|final
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
specifier|final
name|EXistXQueryService
name|service
init|=
operator|(
name|EXistXQueryService
operator|)
name|base
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// set pretty-printing on
name|service
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Variable
name|var
range|:
name|variables
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Name: "
operator|+
name|var
operator|.
name|name
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Value: "
operator|+
name|var
operator|.
name|value
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
name|var
operator|.
name|name
argument_list|,
name|var
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Source
name|source
decl_stmt|;
if|if
condition|(
name|queryUri
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"XQuery url "
operator|+
name|queryUri
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryUri
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
condition|)
block|{
specifier|final
name|Resource
name|resource
init|=
name|base
operator|.
name|getResource
argument_list|(
name|queryUri
argument_list|)
decl_stmt|;
name|source
operator|=
operator|new
name|BinarySource
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|resource
operator|.
name|getContent
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|source
operator|=
operator|new
name|URLSource
argument_list|(
operator|new
name|URL
argument_list|(
name|queryUri
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|queryFile
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"XQuery file "
operator|+
name|queryFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|queryFile
operator|.
name|toPath
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
argument_list|(
literal|"XQuery string: "
operator|+
name|query
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|source
operator|=
operator|new
name|StringSource
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ResourceSet
name|results
init|=
name|service
operator|.
name|execute
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Found "
operator|+
name|results
operator|.
name|getSize
argument_list|()
operator|+
literal|" results"
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|destDir
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|results
operator|!=
literal|null
operator|)
condition|)
block|{
name|log
argument_list|(
literal|"write results to directory "
operator|+
name|destDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
specifier|final
name|ResourceIterator
name|iter
init|=
name|results
operator|.
name|getIterator
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"Writing results to directory "
operator|+
name|destDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreResources
argument_list|()
condition|)
block|{
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|iter
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"Writing resource "
operator|+
name|res
operator|.
name|getId
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|writeResource
argument_list|(
name|res
argument_list|,
name|destDir
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|outputproperty
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ResourceIterator
name|iter
init|=
name|results
operator|.
name|getIterator
argument_list|()
decl_stmt|;
name|String
name|result
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreResources
argument_list|()
condition|)
block|{
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|iter
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|result
operator|=
name|res
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|getProject
argument_list|()
operator|.
name|setNewProperty
argument_list|(
name|outputproperty
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"XMLDB exception caught while executing query: "
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
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"XMLDB exception caught while writing destination file: "
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
specifier|private
name|void
name|writeResource
parameter_list|(
specifier|final
name|XMLResource
name|resource
parameter_list|,
specifier|final
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLDBException
block|{
if|if
condition|(
name|dest
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
specifier|final
name|SAXSerializer
name|serializer
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Writer
name|writer
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
name|dest
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|String
name|fname
init|=
name|resource
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fname
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
condition|)
block|{
name|fname
operator|+=
literal|".xml"
expr_stmt|;
block|}
specifier|final
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dest
argument_list|,
name|fname
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
block|}
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|msg
init|=
literal|"Destination target does not exist."
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
block|}
specifier|public
name|void
name|addText
parameter_list|(
specifier|final
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
specifier|public
name|void
name|setQuery
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
specifier|public
name|void
name|setQueryFile
parameter_list|(
specifier|final
name|File
name|queryFile
parameter_list|)
block|{
name|this
operator|.
name|queryFile
operator|=
name|queryFile
expr_stmt|;
block|}
specifier|public
name|void
name|setQueryUri
parameter_list|(
specifier|final
name|String
name|queryUri
parameter_list|)
block|{
name|this
operator|.
name|queryUri
operator|=
name|queryUri
expr_stmt|;
block|}
specifier|public
name|void
name|setDestDir
parameter_list|(
specifier|final
name|File
name|destDir
parameter_list|)
block|{
name|this
operator|.
name|destDir
operator|=
name|destDir
expr_stmt|;
block|}
specifier|public
name|void
name|addVariable
parameter_list|(
specifier|final
name|Variable
name|variable
parameter_list|)
block|{
name|variables
operator|.
name|add
argument_list|(
name|variable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setOutputproperty
parameter_list|(
specifier|final
name|String
name|outputproperty
parameter_list|)
block|{
name|this
operator|.
name|outputproperty
operator|=
name|outputproperty
expr_stmt|;
block|}
comment|/**      * Defines a nested element to set an XQuery variable.      */
specifier|public
specifier|static
class|class
name|Variable
block|{
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|value
init|=
literal|null
decl_stmt|;
specifier|public
name|Variable
parameter_list|()
block|{
block|}
specifier|public
name|void
name|setName
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

