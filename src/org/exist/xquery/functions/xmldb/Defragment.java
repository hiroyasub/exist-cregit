begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|dom
operator|.
name|NodeSet
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
name|DocumentSet
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
name|*
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
name|update
operator|.
name|Modification
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
name|FunctionParameterSequenceType
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_class
specifier|public
class|class
name|Defragment
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Defragment
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"defragment"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Start a defragmentation run on each document for which a node is passed in the first argument. "
operator|+
literal|"Fragmentation may occur if nodes are inserted into a document using XQuery update "
operator|+
literal|"extensions. "
operator|+
literal|"The second argument specifies the minimum number of fragmented pages which should "
operator|+
literal|"be in a document before it is considered for defragmentation. "
operator|+
literal|"Please note that defragmenting a document changes its internal structure, so any "
operator|+
literal|"references to this document will become invalid, in particular, variables pointing to "
operator|+
literal|"some nodes in the doc."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"nodes"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"nodes from ducuments to defragment"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"integer"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"min number of fragmented pages required before defragmenting"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"defragment"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Start a defragmentation run on each document for which a node is passed in the first argument. "
operator|+
literal|"Fragmentation may occur if nodes are inserted into a document using XQuery update "
operator|+
literal|"extensions. "
operator|+
literal|"Please note that defragmenting a document changes its internal structure, so any "
operator|+
literal|"references to this document will become invalid, in particular, variables pointing to "
operator|+
literal|"some nodes in the doc."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"nodes"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"nodes from ducuments to defragment"
argument_list|)
block|,                     }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Defragment
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
name|logger
operator|.
name|info
argument_list|(
literal|"Entering "
operator|+
name|XMLDBModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|splitCount
init|=
operator|(
operator|(
name|IntegerValue
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
name|getInt
argument_list|()
decl_stmt|;
name|NodeSet
name|nodes
init|=
name|args
index|[
literal|0
index|]
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|DocumentSet
name|docs
init|=
name|nodes
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
try|try
block|{
name|Modification
operator|.
name|checkFragmentation
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|splitCount
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"An error occurred while defragmenting documents: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|XMLDBModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

