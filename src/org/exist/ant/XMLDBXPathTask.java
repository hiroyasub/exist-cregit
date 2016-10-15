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
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XPathQueryService
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
comment|/**  * an ant task to execute an query using XPath.  *<p>  *<p/>The query is either passed as nested text in the element, or via an attribute "query".  *</p>  *  * @author wolf<p>modified by:</p>  * @author peter.klotz@blue-elephant-systems.com  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBXPathTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|String
name|resource
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|namespace
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
name|String
name|text
init|=
literal|null
decl_stmt|;
comment|// count mode
specifier|private
name|boolean
name|count
init|=
literal|false
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
name|query
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"you have to specify a query"
argument_list|)
throw|;
block|}
name|log
argument_list|(
literal|"XPath is: "
operator|+
name|query
argument_list|,
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
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
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|base
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
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
if|if
condition|(
name|namespace
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Using namespace: "
operator|+
name|namespace
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|service
operator|.
name|setNamespace
argument_list|(
literal|"ns"
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ResourceSet
name|results
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Query resource: "
operator|+
name|resource
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|results
operator|=
name|service
operator|.
name|queryResource
argument_list|(
name|resource
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
argument_list|(
literal|"Query collection"
argument_list|,
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|results
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|count
condition|)
block|{
name|getProject
argument_list|()
operator|.
name|setNewProperty
argument_list|(
name|outputproperty
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
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
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
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
operator|.
name|append
argument_list|(
name|res
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
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
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * DOCUMENT ME!      *      * @param query      */
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
name|setResource
parameter_list|(
specifier|final
name|String
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
specifier|public
name|void
name|setNamespace
parameter_list|(
specifier|final
name|String
name|namespace
parameter_list|)
block|{
name|this
operator|.
name|namespace
operator|=
name|namespace
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
specifier|public
name|void
name|setCount
parameter_list|(
specifier|final
name|boolean
name|count
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
block|}
end_class

end_unit

