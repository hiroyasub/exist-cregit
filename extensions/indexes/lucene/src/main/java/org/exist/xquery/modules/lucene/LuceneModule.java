begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|lucene
package|;
end_package

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
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetsCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
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
name|AbstractInternalModule
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
name|ErrorCodes
operator|.
name|ErrorCode
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
name|FunctionDef
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
name|XQueryContext
import|;
end_import

begin_comment
comment|/**  * Module function definitions for Lucene-based full text indexed searching.  *  * @author wolf  * @author ljo  *  */
end_comment

begin_class
specifier|public
class|class
name|LuceneModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/lucene"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"ft"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2008-09-03"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.4"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|EXXQDYFT0001
init|=
operator|new
name|LuceneErrorCode
argument_list|(
literal|"EXXQDYFT0001"
argument_list|,
literal|"Permission denied."
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|EXXQDYFT0002
init|=
operator|new
name|LuceneErrorCode
argument_list|(
literal|"EXXQDYFT0002"
argument_list|,
literal|"IO Exception in lucene index."
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|EXXQDYFT0003
init|=
operator|new
name|LuceneErrorCode
argument_list|(
literal|"EXXQDYFT0003"
argument_list|,
literal|"Document not found."
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|EXXQDYFT0004
init|=
operator|new
name|LuceneErrorCode
argument_list|(
literal|"EXXQDYFT0004"
argument_list|,
literal|"Wrong configuration passed to ft:query"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|Query
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Query
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Query
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Query
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|QueryField
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|QueryField
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|QueryField
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|QueryField
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Score
operator|.
name|signature
argument_list|,
name|Score
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Optimize
operator|.
name|signature
argument_list|,
name|Optimize
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Index
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Index
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Index
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Index
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Index
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|Index
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|InspectIndex
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|InspectIndex
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RemoveIndex
operator|.
name|signature
argument_list|,
name|RemoveIndex
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Search
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Search
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Search
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Search
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Search
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|Search
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetField
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|GetField
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Facets
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Facets
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Facets
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Facets
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|LuceneModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for full text indexed searching based on Lucene."
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
specifier|protected
specifier|final
specifier|static
class|class
name|LuceneErrorCode
extends|extends
name|ErrorCode
block|{
specifier|public
name|LuceneErrorCode
parameter_list|(
name|String
name|code
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|QName
argument_list|(
name|code
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

