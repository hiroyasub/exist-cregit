begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|dom
operator|.
name|persistent
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
name|indexing
operator|.
name|QNamedKeysIndex
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
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|IndexKeys
operator|.
name|class
argument_list|)
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
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node-set"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"start-value"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Only index keys of the same type but being greater than $start-value will be reported for non-string types. For string types, only keys starting with the given prefix are reported."
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
literal|"The function reference as created by the util:function function. "
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
literal|"The maximum number of returned keys"
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
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node-set"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"start-value"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Only index keys of the same type but being greater than $start-value will be reported for non-string types. For string types, only keys starting with the given prefix are reported."
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
literal|"The function reference as created by the util:function function. "
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
literal|"The maximum number of returned keys"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"index"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The index in which the search is made"
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
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index-keys-by-qname"
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
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"qname"
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"start-value"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Only index keys of the same type but being greater than $start-value will be reported for non-string types. For string types, only keys starting with the given prefix are reported."
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
literal|"The function reference as created by the util:function function. "
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
literal|"The maximum number of returned keys"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"index"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The index in which the search is made"
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
block|,     }
decl_stmt|;
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
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|NodeSet
name|nodes
init|=
literal|null
decl_stmt|;
name|DocumentSet
name|docs
init|=
literal|null
decl_stmt|;
name|Sequence
name|qnames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"index-keys-by-qname"
argument_list|)
condition|)
block|{
name|qnames
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
name|docs
operator|=
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
expr_stmt|;
block|}
else|else
block|{
name|nodes
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
name|docs
operator|=
name|nodes
operator|.
name|getDocumentSet
argument_list|()
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
name|int
name|max
init|=
operator|-
literal|1
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
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
comment|// if we have 5 arguments, query the user-specified index
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
specifier|final
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
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
block|}
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
name|max
operator|!=
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
operator|new
name|IntegerValue
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexWorker
operator|instanceof
name|OrderedValuesIndex
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
else|else
block|{
name|logger
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
literal|" isn't an instance of org.exist.indexing.OrderedValuesIndex. Start value '"
operator|+
name|args
index|[
literal|1
index|]
operator|+
literal|"' ignored."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|qnames
operator|!=
literal|null
condition|)
block|{
specifier|final
name|List
argument_list|<
name|QName
argument_list|>
name|qnameList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|qnames
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|qnames
operator|.
name|iterate
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
name|QNameValue
name|qv
init|=
operator|(
name|QNameValue
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|qnameList
operator|.
name|add
argument_list|(
name|qv
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|hints
operator|.
name|put
argument_list|(
name|QNamedKeysIndex
operator|.
name|QNAMES_KEY
argument_list|,
name|qnameList
argument_list|)
expr_stmt|;
block|}
specifier|final
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
specifier|final
name|int
name|len
init|=
operator|(
name|max
operator|!=
operator|-
literal|1
operator|&&
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
comment|// no index specified: use the range index
block|}
else|else
block|{
specifier|final
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
specifier|final
name|QName
index|[]
name|allQNames
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
name|allQNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
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
name|allQNames
argument_list|,
name|indexable
argument_list|)
expr_stmt|;
block|}
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
block|{
name|occur
operator|=
name|occur2
expr_stmt|;
block|}
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
specifier|final
name|int
name|len
init|=
operator|(
name|max
operator|!=
operator|-
literal|1
operator|&&
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
block|}
name|logger
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
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"index-keys-by-qname"
argument_list|)
condition|)
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
else|else
block|{
return|return
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|getDependencies
argument_list|()
return|;
block|}
block|}
comment|/**      * Check index configurations for all collection in the given DocumentSet and return      * a list of QNames, which have indexes defined on them.      *      * @param broker      * @param docs      */
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
specifier|final
name|Set
argument_list|<
name|QName
argument_list|>
name|indexes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
argument_list|>
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
argument_list|<
name|QName
argument_list|>
name|qnames
init|=
name|idxConf
operator|.
name|getIndexedQNames
argument_list|()
decl_stmt|;
for|for
control|(
name|QName
name|qname
range|:
name|qnames
control|)
block|{
specifier|final
name|QName
name|qName
init|=
operator|(
name|QName
operator|)
name|qname
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
specifier|final
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

