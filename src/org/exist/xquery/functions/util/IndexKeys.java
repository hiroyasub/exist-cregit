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
name|storage
operator|.
name|DBBroker
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
name|IndexSpec
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
name|Occurrences
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
name|*
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
literal|"The function will check for indexes defined on path as well as indexes defined by QName. "
operator|+
literal|"The node set is specified in the first argument. The second argument specifies a start "
operator|+
literal|"value. Only index keys of the same type but being greater than $b will be reported for non-string"
operator|+
literal|"types. For string types, only keys starting with the given prefix are reported. "
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
literal|"c) the current position of the key in the whole list of keys returned. "
operator|+
literal|"The fourth argument is the maximum number of returned keys"
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
block|,
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
literal|"The function will check for indexes defined on path as well as indexes defined by QName. "
operator|+
literal|"The node set is specified in the first argument. The second argument specifies a start "
operator|+
literal|"value. Only index keys of the same type but being greater than $b will be reported for non-string"
operator|+
literal|"types. For string types, only keys starting with the given prefix are reported. "
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
literal|"c) the current position of the key in the whole list of keys returned. "
operator|+
literal|"The fourth argument is the maximum number of returned keys"
operator|+
literal|"The fifth argument specifies the index in which the search is made"
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
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|IndexKeys
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
name|isEmpty
argument_list|()
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
if|if
condition|(
name|this
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|5
condition|)
block|{
name|IndexWorker
name|indexWorker
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexName
argument_list|(
name|args
index|[
literal|4
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
comment|//Alternate design
comment|//IndexWorker indexWorker = context.getBroker().getBrokerPool().getIndexManager().getIndexByName(args[4].itemAt(0).getStringValue()).getWorker();
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
name|getASTNode
argument_list|()
argument_list|,
literal|"Unknown index: "
operator|+
name|args
index|[
literal|4
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
throw|;
name|Map
name|hints
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|hints
operator|.
name|put
argument_list|(
name|IndexWorker
operator|.
name|VALUE_COUNT
argument_list|,
operator|new
name|IntegerValue
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexWorker
operator|instanceof
name|OrderedValuesIndex
condition|)
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
argument_list|)
expr_stmt|;
else|else
name|LOG
operator|.
name|warn
argument_list|(
name|indexWorker
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" isn't an instance of org.exist.indexing.OrderedIndexWorker. Start value '"
operator|+
name|args
index|[
literal|1
index|]
operator|+
literal|"' ignored."
argument_list|)
expr_stmt|;
name|Occurrences
index|[]
name|occur
init|=
name|indexWorker
operator|.
name|scanIndex
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|nodes
argument_list|,
name|hints
argument_list|)
decl_stmt|;
comment|//TODO : add an extra argument to pass the END_VALUE ?
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
block|}
else|else
block|{
name|int
name|idxType
init|=
name|nodes
operator|.
name|getIndexType
argument_list|()
decl_stmt|;
name|Indexable
name|indexable
init|=
operator|(
name|Indexable
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
name|ValueOccurrences
name|occur
index|[]
init|=
literal|null
decl_stmt|;
comment|// First check for indexes defined on qname
name|QName
index|[]
name|qnames
init|=
name|getDefinedIndexes
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|docs
argument_list|)
decl_stmt|;
if|if
condition|(
name|qnames
operator|!=
literal|null
operator|&&
name|qnames
operator|.
name|length
operator|>
literal|0
condition|)
name|occur
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getValueIndex
argument_list|()
operator|.
name|scanIndexKeys
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
name|qnames
argument_list|,
name|indexable
argument_list|)
expr_stmt|;
comment|// Also check if there's an index defined by path
name|ValueOccurrences
name|occur2
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
name|scanIndexKeys
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
name|indexable
argument_list|)
decl_stmt|;
comment|// Merge the two results
if|if
condition|(
name|occur
operator|==
literal|null
operator|||
name|occur
operator|.
name|length
operator|==
literal|0
condition|)
name|occur
operator|=
name|occur2
expr_stmt|;
else|else
block|{
name|ValueOccurrences
name|t
index|[]
init|=
operator|new
name|ValueOccurrences
index|[
name|occur
operator|.
name|length
operator|+
name|occur2
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|occur
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
name|occur
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|occur2
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
name|occur
operator|.
name|length
argument_list|,
name|occur2
operator|.
name|length
argument_list|)
expr_stmt|;
name|occur
operator|=
name|t
expr_stmt|;
block|}
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
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Returning: "
operator|+
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Check index configurations for all collection in the given DocumentSet and return      * a list of QNames, which have indexes defined on them.      *      * @param broker      * @param docs      * @return      */
specifier|private
name|QName
index|[]
name|getDefinedIndexes
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|)
block|{
name|Set
name|indexes
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|docs
operator|.
name|getCollectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
init|=
operator|(
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|IndexSpec
name|idxConf
init|=
name|collection
operator|.
name|getIndexConfiguration
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxConf
operator|!=
literal|null
condition|)
block|{
specifier|final
name|List
name|qnames
init|=
name|idxConf
operator|.
name|getIndexedQNames
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
name|qnames
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|QName
name|qName
init|=
operator|(
name|QName
operator|)
name|qnames
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|indexes
operator|.
name|add
argument_list|(
name|qName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|QName
name|qnames
index|[]
init|=
operator|new
name|QName
index|[
name|indexes
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
operator|(
name|QName
index|[]
operator|)
name|indexes
operator|.
name|toArray
argument_list|(
name|qnames
argument_list|)
return|;
block|}
block|}
end_class

end_unit

