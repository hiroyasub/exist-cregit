begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2007-09 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  *    *  @author Pierrick Brihaye<pierrick.brihaye@free.fr>  *  @author ljo  */
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
name|spatial
package|;
end_package

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
name|FunctionDef
import|;
end_import

begin_class
specifier|public
class|class
name|SpatialModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/spatial"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"spatial"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2007-05-28"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.2"
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
name|FunSpatialSearch
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunSpatialSearch
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSpatialSearch
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunSpatialSearch
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSpatialSearch
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|FunSpatialSearch
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSpatialSearch
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|FunSpatialSearch
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSpatialSearch
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|FunSpatialSearch
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSpatialSearch
operator|.
name|signatures
index|[
literal|5
index|]
argument_list|,
name|FunSpatialSearch
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSpatialSearch
operator|.
name|signatures
index|[
literal|6
index|]
argument_list|,
name|FunSpatialSearch
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunSpatialSearch
operator|.
name|signatures
index|[
literal|7
index|]
argument_list|,
name|FunSpatialSearch
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|5
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|6
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|7
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|8
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|9
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|10
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|11
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|12
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|13
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|14
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|15
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|16
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|17
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|18
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|18
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|19
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|20
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|21
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGeometricProperties
operator|.
name|signatures
index|[
literal|22
index|]
argument_list|,
name|FunGeometricProperties
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|5
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|6
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|7
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|8
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|9
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|10
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunGMLProducers
operator|.
name|signatures
index|[
literal|11
index|]
argument_list|,
name|FunGMLProducers
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|SpatialModule
parameter_list|()
block|{
name|super
argument_list|(
name|functions
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
literal|"A module for spatial operations on GML 2D geometries."
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

