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
name|memtree
operator|.
name|DocumentBuilderReceiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|MemTreeBuilder
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
name|IntegerValue
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
name|Type
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
name|ParseException
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
name|WKTReader
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
name|operation
operator|.
name|buffer
operator|.
name|BufferOp
import|;
end_import

begin_class
specifier|public
class|class
name|FunGMLProducers
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
literal|"transform"
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
literal|"Returns the GML representation of geometry $a with the SRS $b"
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
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
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
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"WKTtoGML"
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
literal|"Returns the GML representation of WKT $a with the SRS $b"
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
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
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"buffer"
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
literal|"Returns the GML representation of a buffer around geometry $a having width $b in its CRS. "
operator|+
literal|"Curves will be represented by 8 segments per circle quadrant."
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
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
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
literal|"buffer"
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
literal|"Returns the GML representation of a buffer around geometry $a having width $b in its CRS. "
operator|+
literal|"Curves will be represented by $c segments per circle quadrant."
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
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
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
literal|"buffer"
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
literal|"Returns the GML representation of a buffer around geometry $a having width $b in its CRS. "
operator|+
literal|"Curves will be represented by $c segments per circle quadrant."
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
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
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
literal|"getBbox"
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
literal|"Returns the GML representation of the bounding box of geometry $a."
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
name|NODE
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
literal|"convexHull"
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
literal|"Returns the GML representation of the convex hull of geometry $a."
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
name|NODE
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
literal|"boundary"
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
literal|"Returns the GML representation of the boundary of geometry $a."
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
name|NODE
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
literal|"intersection"
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
literal|"Returns the GML representation of the intersection of geometry $a and geometry $b."
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
block|,
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
name|NODE
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
literal|"union"
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
literal|"Returns the GML representation of the union of geometry $a and geometry $b."
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
block|,
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
name|NODE
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
literal|"difference"
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
literal|"Returns the GML representation of the difference of geometry $a and geometry $b."
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
block|,
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
name|NODE
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
literal|"symetricDifference"
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
literal|"Returns the GML representation of the symetric difference of geometry $a and geometry $b."
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
block|,
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunGMLProducers
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
try|try
block|{
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
name|getIndexWorkerById
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
name|Geometry
name|geometry
init|=
literal|null
decl_stmt|;
name|String
name|srsName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"transform"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
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
name|NodeValue
name|geometryNode
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|sourceSRS
init|=
literal|null
decl_stmt|;
comment|//Try to get the geometry from the index
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
argument_list|)
expr_stmt|;
name|sourceSRS
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
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
comment|//Otherwise, build it
block|}
else|else
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
comment|//sourceSRS = ((Element)geometryNode).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|sourceSRS
operator|=
literal|"osgb:BNG"
expr_stmt|;
block|}
name|srsName
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
name|geometry
operator|=
name|indexWorker
operator|.
name|transformGeometry
argument_list|(
name|geometry
argument_list|,
name|sourceSRS
argument_list|,
name|srsName
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"WKTtoGML"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
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
name|String
name|wkt
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|WKTReader
name|wktReader
init|=
operator|new
name|WKTReader
argument_list|()
decl_stmt|;
try|try
block|{
name|geometry
operator|=
name|wktReader
operator|.
name|read
argument_list|(
name|wkt
argument_list|)
expr_stmt|;
name|srsName
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
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
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"buffer"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
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
name|NodeValue
name|geometryNode
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//Try to get the geometry from the index
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
argument_list|)
expr_stmt|;
name|srsName
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
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
comment|//Otherwise, build it
block|}
else|else
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
comment|//srsName = ((Element)geometryNode).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|srsName
operator|=
literal|"osgb:BNG"
expr_stmt|;
block|}
name|double
name|distance
init|=
operator|(
operator|(
name|DoubleValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDouble
argument_list|()
decl_stmt|;
name|int
name|quadrantSegments
init|=
literal|8
decl_stmt|;
name|int
name|endCapStyle
init|=
name|BufferOp
operator|.
name|CAP_ROUND
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|2
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
condition|)
name|quadrantSegments
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|3
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
condition|)
name|endCapStyle
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|endCapStyle
condition|)
block|{
case|case
name|BufferOp
operator|.
name|CAP_ROUND
case|:
case|case
name|BufferOp
operator|.
name|CAP_BUTT
case|:
case|case
name|BufferOp
operator|.
name|CAP_SQUARE
case|:
comment|//OK
break|break;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid line end style"
argument_list|)
throw|;
block|}
name|geometry
operator|=
name|geometry
operator|.
name|buffer
argument_list|(
name|distance
argument_list|,
name|quadrantSegments
argument_list|,
name|endCapStyle
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"getBbox"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
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
name|NodeValue
name|geometryNode
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//Try to get the geometry from the index
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
argument_list|)
expr_stmt|;
name|srsName
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
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
comment|//Otherwise, build it
block|}
else|else
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
comment|//srsName = ((Element)geometryNode).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|srsName
operator|=
literal|"osgb:BNG"
expr_stmt|;
block|}
name|geometry
operator|=
name|geometry
operator|.
name|getEnvelope
argument_list|()
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"convexHull"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
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
name|NodeValue
name|geometryNode
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//Try to get the geometry from the index
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
argument_list|)
expr_stmt|;
name|srsName
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
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
comment|//Otherwise, build it
block|}
else|else
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
comment|//srsName = ((Element)geometryNode).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|srsName
operator|=
literal|"osgb:BNG"
expr_stmt|;
block|}
name|geometry
operator|=
name|geometry
operator|.
name|convexHull
argument_list|()
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"boundary"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
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
name|NodeValue
name|geometryNode
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//Try to get the geometry from the index
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
argument_list|)
expr_stmt|;
name|srsName
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
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
comment|//Otherwise, build it
block|}
else|else
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
comment|//srsName = ((Element)geometryNode).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|srsName
operator|=
literal|"osgb:BNG"
expr_stmt|;
block|}
name|geometry
operator|=
name|geometry
operator|.
name|getBoundary
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|Geometry
name|geometry1
init|=
literal|null
decl_stmt|;
name|Geometry
name|geometry2
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|&&
name|args
index|[
literal|1
index|]
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
if|else if
condition|(
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|&&
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toSequence
argument_list|()
expr_stmt|;
if|else if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toSequence
argument_list|()
expr_stmt|;
else|else
block|{
name|NodeValue
name|geometryNode1
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NodeValue
name|geometryNode2
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|srsName1
init|=
literal|null
decl_stmt|;
name|String
name|srsName2
init|=
literal|null
decl_stmt|;
comment|//Try to get the geometries from the index
if|if
condition|(
name|geometryNode1
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
name|geometry1
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
name|geometryNode1
argument_list|)
expr_stmt|;
name|srsName1
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
name|geometryNode1
argument_list|,
literal|"SRS_NAME"
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|geometryNode2
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
name|geometry2
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
name|geometryNode2
argument_list|)
expr_stmt|;
name|srsName2
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
name|geometryNode2
argument_list|,
literal|"SRS_NAME"
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
block|}
comment|//Otherwise build them
if|if
condition|(
name|geometry1
operator|==
literal|null
condition|)
block|{
name|geometry1
operator|=
name|indexWorker
operator|.
name|streamGeometryForNode
argument_list|(
name|context
argument_list|,
name|geometryNode1
argument_list|)
expr_stmt|;
comment|//Argl ! No SRS !
comment|//srsName1 = ((Element)geometryNode1).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|srsName1
operator|=
literal|"osgb:BNG"
expr_stmt|;
block|}
if|if
condition|(
name|geometry2
operator|==
literal|null
condition|)
block|{
name|geometry2
operator|=
name|indexWorker
operator|.
name|streamGeometryForNode
argument_list|(
name|context
argument_list|,
name|geometryNode2
argument_list|)
expr_stmt|;
comment|//Argl ! No SRS !
comment|//srsName2 = ((Element)geometryNode2).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|srsName2
operator|=
literal|"osgb:BNG"
expr_stmt|;
block|}
comment|//Transform the second geometry if necessary
if|if
condition|(
operator|!
name|srsName1
operator|.
name|equalsIgnoreCase
argument_list|(
name|srsName2
argument_list|)
condition|)
block|{
name|geometry2
operator|=
name|indexWorker
operator|.
name|transformGeometry
argument_list|(
name|geometry2
argument_list|,
name|srsName1
argument_list|,
name|srsName2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"intersection"
argument_list|)
condition|)
block|{
name|geometry
operator|=
name|geometry1
operator|.
name|intersection
argument_list|(
name|geometry2
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"union"
argument_list|)
condition|)
block|{
name|geometry
operator|=
name|geometry1
operator|.
name|union
argument_list|(
name|geometry2
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"difference"
argument_list|)
condition|)
block|{
name|geometry
operator|=
name|geometry1
operator|.
name|difference
argument_list|(
name|geometry2
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"symetricDifference"
argument_list|)
condition|)
block|{
name|geometry
operator|=
name|geometry1
operator|.
name|symDifference
argument_list|(
name|geometry2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|String
name|gmlPrefix
init|=
name|context
operator|.
name|getPrefixForURI
argument_list|(
name|AbstractGMLJDBCIndexWorker
operator|.
name|GML_NS
argument_list|)
decl_stmt|;
if|if
condition|(
name|gmlPrefix
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"'"
operator|+
name|AbstractGMLJDBCIndexWorker
operator|.
name|GML_NS
operator|+
literal|"' namespace is not defined"
argument_list|)
throw|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|result
operator|=
operator|(
name|NodeValue
operator|)
name|indexWorker
operator|.
name|getGML
argument_list|(
name|geometry
argument_list|,
name|srsName
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
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

