begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelParameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistXmldbEmbeddedServer
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
name|xmlunit
operator|.
name|builder
operator|.
name|DiffBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|Input
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|diff
operator|.
name|Diff
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
name|Source
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
name|net
operator|.
name|URI
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
name|util
operator|.
name|Arrays
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelParameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ExtDocTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"external-doc-ns-1"
block|,
literal|"<elem1 xmlns:xyz=\"http://xyz\"/>"
block|,
literal|null
block|}
block|,
block|{
literal|"external-doc-ns-2"
block|,
literal|"<elem1 xmlns=\"hello\" xmlns:xyz=\"http://xyz\"/>"
block|,
literal|null
block|}
block|,
block|{
literal|"external-doc-ns-3"
block|,
literal|"<abc:elem1 xmlns:abc=\"hello\" xmlns:xyz=\"http://xyz\"/>"
block|,
literal|null
block|}
block|,
block|{
literal|"external-doc-ns-4"
block|,
literal|"<abc:elem1 xmlns:abc=\"hello\" xmlns:xyz=\"http://xyz\" xmlns=\"123\"/>"
block|,
literal|null
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Parameter
specifier|public
name|String
name|docName
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|value
operator|=
literal|1
argument_list|)
specifier|public
name|String
name|docContent
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|value
operator|=
literal|2
argument_list|)
specifier|public
name|Path
name|externalDoc
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|storeExtDoc
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|externalDocFile
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|docName
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|externalDocFile
argument_list|,
name|docContent
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|externalDoc
operator|=
name|externalDocFile
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|removeExtDoc
parameter_list|()
block|{
if|if
condition|(
name|externalDoc
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|externalDoc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|parse
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|URI
name|docUri
init|=
name|externalDoc
operator|.
name|toUri
argument_list|()
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
literal|"xquery version \"3.1\";\n"
operator|+
literal|"\n"
operator|+
literal|"declare namespace output = \"http://www.w3.org/2010/xslt-xquery-serialization\";"
operator|+
literal|"declare option output:omit-xml-declaration \"yes\";\n"
operator|+
literal|"\n"
operator|+
literal|"fn:doc('"
operator|+
name|docUri
operator|+
literal|"')"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Resource
name|resource
init|=
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|,
name|resource
operator|.
name|getResourceType
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Source
name|expectedSource
init|=
name|Input
operator|.
name|fromString
argument_list|(
name|docContent
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Source
name|actualSource
init|=
name|Input
operator|.
name|fromNode
argument_list|(
operator|(
operator|(
name|XMLResource
operator|)
name|resource
operator|)
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Diff
name|diff
init|=
name|DiffBuilder
operator|.
name|compare
argument_list|(
name|expectedSource
argument_list|)
operator|.
name|withTest
argument_list|(
name|actualSource
argument_list|)
operator|.
name|checkForSimilar
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|diff
operator|.
name|hasDifferences
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

