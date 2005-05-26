begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|util
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
name|DocumentSet
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|Indexable
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
name|ValueOccurrences
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
name|FunctionCall
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
name|FunctionReference
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * @author wolf  *   */
end_comment

begin_class
specifier|public
class|class
name|IndexKeys
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index-keys"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Can be used to query existing range indexes defined on a set of nodes. "
operator|+
literal|"All index keys defined for the given node set are reported to a callback function. "
operator|+
literal|"The node set is specified in the first argument. The second argument specifies a start "
operator|+
literal|"value. Only index keys being greater than $b will be reported. "
operator|+
literal|"The third arguments is a function reference as created by the util:function function. "
operator|+
literal|"It can be an arbitrary user-defined function, but it should take exactly 2 arguments: "
operator|+
literal|"1) the current index key as found in the range index as an atomic value, 2) a sequence "
operator|+
literal|"containing three int values: a) the overall frequency of the key within the node set, "
operator|+
literal|"b) the number of distinct documents in the node set the key occurs in, "
operator|+
literal|"c) the current position of the key in the whole list of keys returned."
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
name|ATOMIC
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
name|FUNCTION_REFERENCE
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
name|INT
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
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * @param context      * @param signature      */
specifier|public
name|IndexKeys
parameter_list|(
name|XQueryContext
name|context
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
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[],      *      org.exist.xquery.value.Sequence)      */
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
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
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
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
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
decl_stmt|;
name|int
name|max
init|=
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
decl_stmt|;
name|FunctionCall
name|call
init|=
name|ref
operator|.
name|getFunctionCall
argument_list|()
decl_stmt|;
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|ValueOccurrences
name|occur
index|[]
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getValueIndex
argument_list|()
operator|.
name|scanIndexTerms
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
operator|(
name|Indexable
operator|)
name|args
index|[
literal|1
index|]
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|len
init|=
operator|(
name|occur
operator|.
name|length
operator|>
name|max
condition|?
name|max
else|:
name|occur
operator|.
name|length
operator|)
decl_stmt|;
name|Sequence
name|params
index|[]
init|=
operator|new
name|Sequence
index|[
literal|2
index|]
decl_stmt|;
name|ValueSequence
name|data
init|=
operator|new
name|ValueSequence
argument_list|()
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
name|len
condition|;
name|j
operator|++
control|)
block|{
name|params
index|[
literal|0
index|]
operator|=
name|occur
index|[
name|j
index|]
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|occur
index|[
name|j
index|]
operator|.
name|getOccurrences
argument_list|()
argument_list|,
name|Type
operator|.
name|UNSIGNED_INT
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|occur
index|[
name|j
index|]
operator|.
name|getDocuments
argument_list|()
argument_list|,
name|Type
operator|.
name|UNSIGNED_INT
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|j
operator|+
literal|1
argument_list|,
name|Type
operator|.
name|UNSIGNED_INT
argument_list|)
argument_list|)
expr_stmt|;
name|params
index|[
literal|1
index|]
operator|=
name|data
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|call
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Returning: "
operator|+
name|result
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

