begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2007 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  *    *  @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
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
name|dom
operator|.
name|NodeProxy
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
name|indexing
operator|.
name|spatial
operator|.
name|AbstractGMLJDBCIndex
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
name|spatial
operator|.
name|AbstractGMLJDBCIndexWorker
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
name|spatial
operator|.
name|SpatialIndexException
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
name|BasicFunction
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
name|Cardinality
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
name|exist
operator|.
name|xquery
operator|.
name|FunctionSignature
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
name|IndexUseReporter
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
name|XPathException
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Base64Binary
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
name|value
operator|.
name|BooleanValue
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
name|value
operator|.
name|DoubleValue
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
name|value
operator|.
name|NodeValue
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
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|SequenceType
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
name|value
operator|.
name|StringValue
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
name|value
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|Geometry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|io
operator|.
name|WKBWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|io
operator|.
name|WKTWriter
import|;
end_import

begin_class
specifier|public
class|class
name|FunGeometricProperties
extends|extends
name|BasicFunction
implements|implements
name|IndexUseReporter
block|{
name|boolean
name|hasUsedIndex
init|=
literal|false
decl_stmt|;
specifier|protected
name|WKTWriter
name|wktWriter
init|=
operator|new
name|WKTWriter
argument_list|()
decl_stmt|;
specifier|protected
name|WKBWriter
name|wkbWriter
init|=
operator|new
name|WKBWriter
argument_list|()
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"GMLtoWKT"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the WKT representation of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getWKB"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the WKB representation of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getMinX"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the minimal X of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getMaxX"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the maximal X of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getMinY"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the minimal Y of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getMaxY"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the maximal Y of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getCentroidX"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the X of centroid of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getCentroidY"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the Y of centroid of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getArea"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the area of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getEPSG4326WKB"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the WKB representation of geometry $a in the EPSG:4326 SRS"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getEPSG4326MinX"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the minimal X of geometry $a in the EPSG:4326 SRS"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getEPSG4326MaxX"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the maximal X of geometry $a in the EPSG:4326 SRS"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getEPSG4326MinY"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the minimal Y of geometry $a in the EPSG:4326 SRS"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getEPSG4326MaxY"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the maximal Y of geometry $a in the EPSG:4326 SRS"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getEPSG4326CentroidX"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the X of centroid of geometry $a in the EPSG:4326 SRS"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getEPSG4326CentroidY"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the Y of centroid of geometry $a in the EPSG:4326 SRS"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getEPSG4326Area"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the area of geometry $a in the EPSG:4326 SRS"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getSRS"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the spatial reference system of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"getGeometryType"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the type of geometry $a"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"isClosed"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns if geometry $a is closed"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"isSimple"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns if geometry $a is simple"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"isValid"
argument_list|,
name|SpatialModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SpatialModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns if geometry $a is valid"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunGeometricProperties
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
name|Sequence
name|nodes
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
try|try
block|{
name|Geometry
name|geometry
init|=
literal|null
decl_stmt|;
name|String
name|sourceCRS
init|=
literal|null
decl_stmt|;
name|AbstractGMLJDBCIndexWorker
name|indexWorker
init|=
operator|(
name|AbstractGMLJDBCIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexId
argument_list|(
name|AbstractGMLJDBCIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexWorker
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unable to find a spatial index worker"
argument_list|)
throw|;
name|NodeValue
name|geometryNode
init|=
operator|(
name|NodeValue
operator|)
name|nodes
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|geometryNode
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
name|String
name|propertyName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"GMLtoWKT"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"WKT"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getWKB"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"WKB"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getMinX"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"MINX"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getMaxX"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"MAXX"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getMinY"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"MINY"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getMaxY"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"MAXY"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getCentroidX"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"CENTROID_X"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getCentroidY"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"CENTROID_Y"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getArea"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"AREA"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326WKB"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"EPSG4326_WKB"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326MinX"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"EPSG4326_MINX"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326MaxX"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"EPSG4326_MAXX"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326MinY"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"EPSG4326_MINY"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326MaxY"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"EPSG4326_MAXY"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326CentroidX"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"EPSG4326_CENTROID_X"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326CentroidY"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"EPSG4326_CENTROID_Y"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326Area"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"EPSG4326_AREA"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getSRS"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"SRS_NAME"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getGeometryType"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"GEOMETRY_TYPE"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"isClosed"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"IS_CLOSED"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"isSimple"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"IS_SIMPLE"
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"isValid"
argument_list|)
condition|)
block|{
name|propertyName
operator|=
literal|"IS_VALID"
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown spatial property: "
operator|+
name|mySignature
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
comment|//The node should be indexed : get its properties
name|result
operator|=
name|indexWorker
operator|.
name|getGeometricPropertyForNode
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
operator|(
name|NodeProxy
operator|)
name|geometryNode
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|//Or, at least, its geometry for further processing
comment|//TODO : think ; the signature may require getEPSG4326
name|geometry
operator|=
name|indexWorker
operator|.
name|getGeometryForNode
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
operator|(
name|NodeProxy
operator|)
name|geometryNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sourceCRS
operator|=
name|indexWorker
operator|.
name|getGeometricPropertyForNode
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
operator|(
name|NodeProxy
operator|)
name|geometryNode
argument_list|,
literal|"SRS_NAME"
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|//builds the geometry
if|if
condition|(
name|geometry
operator|==
literal|null
condition|)
block|{
name|geometry
operator|=
name|indexWorker
operator|.
name|streamGeometryForNode
argument_list|(
name|context
argument_list|,
name|geometryNode
argument_list|)
expr_stmt|;
comment|//Argl ! No SRS !
comment|//sourceCRS = ((Element)geometryNode).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|sourceCRS
operator|=
literal|"osgb:BNG"
expr_stmt|;
block|}
comment|//Provisional workaround : Geotools sometimes returns null geometries
comment|//due to a too strict check.
comment|//I can't see a way to return something useful in such a case
if|if
condition|(
name|geometry
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
name|hasUsedIndex
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|//Transform the geometry to EPSG:4326 if relevant
if|if
condition|(
name|mySignature
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"EPSG4326"
argument_list|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|geometry
operator|=
name|indexWorker
operator|.
name|transformGeometry
argument_list|(
name|geometry
argument_list|,
name|sourceCRS
argument_list|,
literal|"EPSG:4326"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326WKB"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|Base64Binary
argument_list|(
name|wkbWriter
operator|.
name|write
argument_list|(
name|geometry
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326MinX"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getEnvelopeInternal
argument_list|()
operator|.
name|getMinX
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326MaxX"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getEnvelopeInternal
argument_list|()
operator|.
name|getMaxX
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326MinY"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getEnvelopeInternal
argument_list|()
operator|.
name|getMinY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326MaxY"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getEnvelopeInternal
argument_list|()
operator|.
name|getMaxY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326CentroidX"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getCentroid
argument_list|()
operator|.
name|getX
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326CentroidY"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getCentroid
argument_list|()
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getEPSG4326Area"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getArea
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"GMLtoWKT"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|wktWriter
operator|.
name|write
argument_list|(
name|geometry
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getWKB"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|Base64Binary
argument_list|(
name|wkbWriter
operator|.
name|write
argument_list|(
name|geometry
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getMinX"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getEnvelopeInternal
argument_list|()
operator|.
name|getMinX
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getMaxX"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getEnvelopeInternal
argument_list|()
operator|.
name|getMaxX
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getMinY"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getEnvelopeInternal
argument_list|()
operator|.
name|getMinY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getMaxY"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getEnvelopeInternal
argument_list|()
operator|.
name|getMaxY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getCentroidX"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getCentroid
argument_list|()
operator|.
name|getX
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getCentroidY"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getCentroid
argument_list|()
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getArea"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|geometry
operator|.
name|getArea
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getSRS"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|StringValue
argument_list|(
operator|(
operator|(
name|Element
operator|)
name|geometryNode
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"srsName"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getGeometryType"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|geometry
operator|.
name|getGeometryType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"isClosed"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|BooleanValue
argument_list|(
operator|!
name|geometry
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"isSimple"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|BooleanValue
argument_list|(
name|geometry
operator|.
name|isSimple
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"isValid"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|BooleanValue
argument_list|(
name|geometry
operator|.
name|isValid
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown spatial property: "
operator|+
name|mySignature
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SpatialIndexException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|hasUsedIndex
parameter_list|()
block|{
return|return
name|hasUsedIndex
return|;
block|}
block|}
end_class

end_unit

