begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id$  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|xmldb
operator|.
name|EXistXQueryService
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
name|*
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
comment|/**  * Static utility methods used by the tests.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DBUtils
block|{
comment|/**      * @param elementCnt      * @param attrCnt      * @param wordList      * @return File      */
specifier|public
specifier|static
name|Path
name|generateXMLFile
parameter_list|(
specifier|final
name|int
name|elementCnt
parameter_list|,
specifier|final
name|int
name|attrCnt
parameter_list|,
specifier|final
name|String
index|[]
name|wordList
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|generateXMLFile
argument_list|(
name|elementCnt
argument_list|,
name|attrCnt
argument_list|,
name|wordList
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * @param elementCnt      * @param attrCnt      * @param wordList      * @param namespaces      * @return File      */
specifier|public
specifier|static
name|Path
name|generateXMLFile
parameter_list|(
specifier|final
name|int
name|elementCnt
parameter_list|,
specifier|final
name|int
name|attrCnt
parameter_list|,
specifier|final
name|String
index|[]
name|wordList
parameter_list|,
specifier|final
name|boolean
name|namespaces
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|generateXMLFile
argument_list|(
literal|3
argument_list|,
name|elementCnt
argument_list|,
name|attrCnt
argument_list|,
name|wordList
argument_list|,
name|namespaces
argument_list|)
return|;
block|}
comment|/**      * @param depth      * @param elementCnt      * @param attrCnt      * @param wordList      * @param namespaces      * @return File      */
specifier|public
specifier|static
name|Path
name|generateXMLFile
parameter_list|(
specifier|final
name|int
name|depth
parameter_list|,
specifier|final
name|int
name|elementCnt
parameter_list|,
specifier|final
name|int
name|attrCnt
parameter_list|,
specifier|final
name|String
index|[]
name|wordList
parameter_list|,
specifier|final
name|boolean
name|namespaces
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|".xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|file
argument_list|)
operator|&&
operator|!
name|Files
operator|.
name|isWritable
argument_list|(
name|file
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot write to output file "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
try|try
init|(
specifier|final
name|Writer
name|writer
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|file
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
specifier|final
name|XMLGenerator
name|gen
init|=
operator|new
name|XMLGenerator
argument_list|(
name|elementCnt
argument_list|,
name|attrCnt
argument_list|,
name|depth
argument_list|,
name|wordList
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
name|gen
operator|.
name|generateXML
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
return|return
name|file
return|;
block|}
specifier|public
specifier|static
name|Collection
name|addCollection
parameter_list|(
specifier|final
name|Collection
name|parent
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|CollectionManagementService
name|service
init|=
name|getCollectionManagementService
argument_list|(
name|parent
argument_list|)
decl_stmt|;
return|return
name|service
operator|.
name|createCollection
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|removeCollection
parameter_list|(
specifier|final
name|Collection
name|parent
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|CollectionManagementService
name|service
init|=
name|getCollectionManagementService
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|service
operator|.
name|removeCollection
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|CollectionManagementService
name|getCollectionManagementService
parameter_list|(
specifier|final
name|Collection
name|col
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
operator|(
name|CollectionManagementService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|addXMLResource
parameter_list|(
specifier|final
name|Collection
name|col
parameter_list|,
specifier|final
name|String
name|resourceId
parameter_list|,
specifier|final
name|Path
name|file
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|file
operator|==
literal|null
operator|||
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|file
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"File does not exist: "
operator|+
name|file
argument_list|)
throw|;
block|}
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|col
operator|.
name|createResource
argument_list|(
name|resourceId
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|file
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
specifier|public
specifier|static
name|void
name|addXMLResource
parameter_list|(
specifier|final
name|Collection
name|col
parameter_list|,
specifier|final
name|String
name|resourceId
parameter_list|,
specifier|final
name|String
name|contents
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|col
operator|.
name|createResource
argument_list|(
name|resourceId
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|contents
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
specifier|public
specifier|static
name|ResourceSet
name|query
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|xpath
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|(
name|collection
argument_list|)
decl_stmt|;
return|return
name|service
operator|.
name|query
argument_list|(
name|xpath
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ResourceSet
name|queryResource
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|resource
parameter_list|,
specifier|final
name|String
name|xpath
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|(
name|collection
argument_list|)
decl_stmt|;
return|return
name|service
operator|.
name|queryResource
argument_list|(
name|resource
argument_list|,
name|xpath
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ResourceSet
name|xquery
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|xquery
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|EXistXQueryService
name|service
init|=
name|getXQueryService
argument_list|(
name|collection
argument_list|)
decl_stmt|;
specifier|final
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
return|return
name|service
operator|.
name|execute
argument_list|(
name|source
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|XPathQueryService
name|getQueryService
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
operator|(
name|XPathQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|EXistXQueryService
name|getXQueryService
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
operator|(
name|EXistXQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
index|[]
name|wordList
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|URL
name|url
init|=
name|DBUtils
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"uk-towns.txt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|()
throw|;
block|}
specifier|final
name|String
index|[]
name|words
init|=
operator|new
name|String
index|[
literal|100
index|]
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|url
operator|.
name|openStream
argument_list|()
init|;
specifier|final
name|LineNumberReader
name|reader
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|words
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|words
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|words
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
