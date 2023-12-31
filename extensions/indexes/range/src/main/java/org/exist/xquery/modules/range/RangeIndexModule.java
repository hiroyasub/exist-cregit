begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|range
package|;
end_package

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
name|indexing
operator|.
name|range
operator|.
name|RangeIndex
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|RangeIndexModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/range"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"range"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-2.2"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|5
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|6
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|7
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|8
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Lookup
operator|.
name|signatures
index|[
literal|9
index|]
argument_list|,
name|Lookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|5
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|6
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|7
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|8
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|9
index|]
argument_list|,
name|FieldLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FieldLookup
operator|.
name|signatures
index|[
literal|10
index|]
argument_list|,
name|FieldLookup
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
name|IndexKeys
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|IndexKeys
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexKeys
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|IndexKeys
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|RangeIndex
operator|.
name|Operator
argument_list|>
name|OPERATOR_MAP
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RangeIndex
operator|.
name|Operator
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"eq"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|EQ
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"lt"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|LT
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"gt"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|GT
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"ge"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|GE
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"le"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|LE
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"ne"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|NE
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"starts-with"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|STARTS_WITH
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"ends-with"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|ENDS_WITH
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"contains"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|CONTAINS
argument_list|)
expr_stmt|;
name|OPERATOR_MAP
operator|.
name|put
argument_list|(
literal|"matches"
argument_list|,
name|RangeIndex
operator|.
name|Operator
operator|.
name|MATCH
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|final
specifier|static
class|class
name|RangeIndexErrorCode
extends|extends
name|ErrorCodes
operator|.
name|ErrorCode
block|{
specifier|public
name|RangeIndexErrorCode
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
specifier|public
specifier|final
specifier|static
name|ErrorCodes
operator|.
name|ErrorCode
name|EXXQDYFT0001
init|=
operator|new
name|RangeIndexErrorCode
argument_list|(
literal|"EXXQDYFT0001"
argument_list|,
literal|"Collation not "
operator|+
literal|"supported"
argument_list|)
decl_stmt|;
specifier|public
name|RangeIndexModule
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
literal|"Functions to access the range index."
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
block|}
end_class

end_unit

