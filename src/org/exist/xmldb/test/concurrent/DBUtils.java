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
name|test
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
name|BufferedWriter
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
name|FileWriter
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
name|ArrayList
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
name|util
operator|.
name|Occurrences
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
name|DatabaseInstanceManager
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
name|IndexQueryService
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
name|XQueryService
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
name|Database
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

begin_comment
comment|/**  * Static utility methods used by the tests.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DBUtils
block|{
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|public
specifier|static
name|Collection
name|setupDB
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
return|return
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|shutdownDB
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|Collection
name|collection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|DatabaseInstanceManager
name|manager
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|File
name|generateXMLFile
parameter_list|(
name|int
name|elementCnt
parameter_list|,
name|int
name|attrCnt
parameter_list|,
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
specifier|public
specifier|static
name|File
name|generateXMLFile
parameter_list|(
name|int
name|elementCnt
parameter_list|,
name|int
name|attrCnt
parameter_list|,
name|String
index|[]
name|wordList
parameter_list|,
name|boolean
name|namespaces
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|File
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
name|file
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|canWrite
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot write to output file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Generating XML file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
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
literal|3
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|file
return|;
block|}
specifier|public
specifier|static
name|Collection
name|addCollection
parameter_list|(
name|Collection
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|Collection
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|Collection
name|col
parameter_list|,
name|String
name|resourceId
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|Collection
name|col
parameter_list|,
name|String
name|resourceId
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|Collection
name|collection
parameter_list|,
name|String
name|xpath
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|Collection
name|collection
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|xpath
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|Collection
name|collection
parameter_list|,
name|String
name|xquery
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|XQueryService
name|service
init|=
name|getXQueryService
argument_list|(
name|collection
argument_list|)
decl_stmt|;
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
name|XQueryService
name|getXQueryService
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
operator|(
name|XQueryService
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
parameter_list|(
name|Collection
name|root
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|IndexQueryService
name|service
init|=
operator|(
name|IndexQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ArrayList
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|String
name|alphas
init|=
literal|"abcdefghijklmnopqrstuvwxyz"
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
name|alphas
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|alphas
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Occurrences
index|[]
name|terms
init|=
name|service
operator|.
name|scanIndexTerms
argument_list|(
name|s
argument_list|,
name|s
operator|+
literal|'z'
argument_list|,
literal|true
argument_list|)
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
name|terms
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|terms
index|[
name|j
index|]
operator|.
name|getTerm
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|words
init|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|words
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Size of the word list: "
operator|+
name|words
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|words
return|;
block|}
block|}
end_class

end_unit

