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
name|indexing
operator|.
name|spatial
operator|.
name|AbstractGMLJDBCIndex
operator|.
name|SpatialOperator
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

begin_class
specifier|public
class|class
name|FunSpatialSearch
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
literal|"equals"
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
literal|"Returns the nodes in $a that contain a geometry which is equal to geometry $b"
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
name|ZERO_OR_MORE
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"disjoint"
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
literal|"Returns the nodes in $a that contain a geometry which is disjoint with geometry $b"
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
name|ZERO_OR_MORE
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"intersects"
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
literal|"Returns the nodes in $a that contain a geometry which instersects with geometry $b"
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
name|ZERO_OR_MORE
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"touches"
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
literal|"Returns the nodes in $a that contain a geometry which touches geometry $b"
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
name|ZERO_OR_MORE
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"crosses"
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
literal|"Returns the nodes in $a that contain a geometry which crosses geometry $b"
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
name|ZERO_OR_MORE
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"within"
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
literal|"Returns the nodes in $a that contain a geometry which is within geometry $b"
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
name|ZERO_OR_MORE
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"contains"
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
literal|"Returns the nodes in $a that contain a geometry which contains geometry $b"
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
name|ZERO_OR_MORE
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"overlaps"
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
literal|"Returns the nodes in $a that contain a geometry which overlaps geometry $b"
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
name|ZERO_OR_MORE
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunSpatialSearch
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
if|else if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
comment|//TODO : to be discussed. We could also return an empty sequence here
name|result
operator|=
name|nodes
expr_stmt|;
else|else
block|{
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
name|Geometry
name|EPSG4326_geometry
init|=
literal|null
decl_stmt|;
name|NodeValue
name|geometryNode
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
comment|//The node should be indexed
name|EPSG4326_geometry
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
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|EPSG4326_geometry
operator|==
literal|null
condition|)
block|{
name|Geometry
name|geometry
init|=
name|indexWorker
operator|.
name|streamGeometryForNode
argument_list|(
name|context
argument_list|,
name|geometryNode
argument_list|)
decl_stmt|;
comment|//Argl ! No SRS !
comment|//sourceCRS = ((Element)geometryNode).getAttribute("srsName").trim();
comment|//Erroneous workaround
name|String
name|sourceCRS
init|=
literal|"osgb:BNG"
decl_stmt|;
comment|//Transform the geometry to EPSG:4326
name|EPSG4326_geometry
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
block|}
name|int
name|spatialOp
init|=
name|SpatialOperator
operator|.
name|UNKNOWN
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"equals"
argument_list|)
condition|)
name|spatialOp
operator|=
name|SpatialOperator
operator|.
name|EQUALS
expr_stmt|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"disjoint"
argument_list|)
condition|)
name|spatialOp
operator|=
name|SpatialOperator
operator|.
name|DISJOINT
expr_stmt|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"intersects"
argument_list|)
condition|)
name|spatialOp
operator|=
name|SpatialOperator
operator|.
name|INTERSECTS
expr_stmt|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"touches"
argument_list|)
condition|)
name|spatialOp
operator|=
name|SpatialOperator
operator|.
name|TOUCHES
expr_stmt|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"crosses"
argument_list|)
condition|)
name|spatialOp
operator|=
name|SpatialOperator
operator|.
name|CROSSES
expr_stmt|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"within"
argument_list|)
condition|)
name|spatialOp
operator|=
name|SpatialOperator
operator|.
name|WITHIN
expr_stmt|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"contains"
argument_list|)
condition|)
name|spatialOp
operator|=
name|SpatialOperator
operator|.
name|CONTAINS
expr_stmt|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"overlaps"
argument_list|)
condition|)
name|spatialOp
operator|=
name|SpatialOperator
operator|.
name|OVERLAPS
expr_stmt|;
comment|//Search the EPSG:4326 in the index
name|result
operator|=
name|indexWorker
operator|.
name|search
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|nodes
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|EPSG4326_geometry
argument_list|,
name|spatialOp
argument_list|)
expr_stmt|;
name|hasUsedIndex
operator|=
literal|true
expr_stmt|;
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

