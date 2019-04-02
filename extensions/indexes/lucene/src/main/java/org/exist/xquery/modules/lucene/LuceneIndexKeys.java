begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|persistent
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
name|indexing
operator|.
name|IndexWorker
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
name|OrderedValuesIndex
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
name|lucene
operator|.
name|LuceneIndex
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
name|lucene
operator|.
name|LuceneIndexWorker
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
name|value
operator|.
name|*
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
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexKeys
extends|extends
name|BasicFunction
block|{
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
literal|"index-keys-for-field"
argument_list|,
name|LuceneModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LuceneModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Similar to the util:index-keys functions, but returns index entries for a field "
operator|+
literal|"associated with a lucene index."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"field"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the field"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"start-value"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Only keys starting with the given prefix are reported."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function-reference"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A function reference. "
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
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"max-number-returned"
argument_list|,
name|Type
operator|.
name|INT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The maximum number of keys to return"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the results of the eval of the $function-reference"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|LuceneIndexKeys
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
annotation|@
name|Override
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
specifier|final
name|String
name|fieldName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|DocumentSet
name|docs
init|=
name|contextSequence
operator|==
literal|null
condition|?
name|context
operator|.
name|getStaticallyKnownDocuments
argument_list|()
else|:
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|hints
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|hints
operator|.
name|put
argument_list|(
name|OrderedValuesIndex
operator|.
name|START_VALUE
argument_list|,
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|IntegerValue
name|max
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|3
index|]
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|max
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
expr_stmt|;
block|}
if|if
condition|(
name|max
operator|!=
literal|null
operator|&&
name|max
operator|.
name|getInt
argument_list|()
operator|>
operator|-
literal|1
condition|)
block|{
name|hints
operator|.
name|put
argument_list|(
name|IndexWorker
operator|.
name|VALUE_COUNT
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
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
init|)
block|{
specifier|final
name|LuceneIndexWorker
name|index
init|=
operator|(
name|LuceneIndexWorker
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
name|LuceneIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
specifier|final
name|Occurrences
index|[]
name|occur
init|=
name|index
operator|.
name|scanIndexByField
argument_list|(
name|fieldName
argument_list|,
name|docs
argument_list|,
name|hints
argument_list|)
decl_stmt|;
specifier|final
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
specifier|final
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
name|occur
operator|.
name|length
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
operator|new
name|StringValue
argument_list|(
name|occur
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
name|ref
operator|.
name|evalFunction
argument_list|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
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
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
block|}
end_class

end_unit

