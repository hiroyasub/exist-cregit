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
name|text
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
name|security
operator|.
name|PermissionDeniedException
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
name|*
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|IndexTerms
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
operator|new
name|FunctionSignature
index|[]
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index-terms"
argument_list|,
name|TextModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TextModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"This function can be used to collect some information on the distribution "
operator|+
literal|"of index terms within a set of nodes. The set of nodes is specified in the first "
operator|+
literal|"argument $a. The function returns term frequencies for all terms in the index found "
operator|+
literal|"in descendants of the nodes in $a. The second argument $b specifies "
operator|+
literal|"a start string. Only terms starting with the specified character sequence are returned. "
operator|+
literal|"If $a is the empty sequence, all terms in the index will be selected. "
operator|+
literal|"$c is a function reference, which points to a callback function that will be called "
operator|+
literal|"for every term occurrence. $d defines the maximum number of terms that should be "
operator|+
literal|"reported. The function reference for $c can be created with the util:function "
operator|+
literal|"function. It can be an arbitrary user-defined function, but it should take exactly 2 arguments: "
operator|+
literal|"1) the current term as found in the index as xs:string, 2) a sequence containing four int "
operator|+
literal|"values: a) the overall frequency of the term within the node set, b) the number of distinct "
operator|+
literal|"documents in the node set the term occurs in, c) the current position of the term in the whole "
operator|+
literal|"list of terms returned, d) the rank of the current term in the whole list of terms returned."
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
literal|"index-terms"
argument_list|,
name|TextModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TextModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"This version of the index-terms function is to be used with indexes that were "
operator|+
literal|"defined on a specific element or attribute QName. The second argument "
operator|+
literal|"lists the QNames or elements or attributes for which occurrences should be"
operator|+
literal|"returned. Otherwise, the function behaves like the 4-argument version."
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
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
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
name|ZERO_OR_ONE
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
block|}
decl_stmt|;
specifier|public
name|IndexTerms
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)      */
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
name|int
name|arg
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|args
index|[
name|arg
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
name|args
index|[
name|arg
operator|++
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
name|QName
index|[]
name|qnames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|5
condition|)
block|{
name|qnames
operator|=
operator|new
name|QName
index|[
name|args
index|[
name|arg
index|]
operator|.
name|getItemCount
argument_list|()
index|]
expr_stmt|;
name|int
name|q
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|args
index|[
name|arg
index|]
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|q
operator|++
control|)
block|{
name|QNameValue
name|qnv
init|=
operator|(
name|QNameValue
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|qnames
index|[
name|q
index|]
operator|=
name|qnv
operator|.
name|getQName
argument_list|()
expr_stmt|;
block|}
operator|++
name|arg
expr_stmt|;
block|}
else|else
name|qnames
operator|=
name|getDefinedIndexes
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|docs
argument_list|)
expr_stmt|;
name|String
name|start
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
name|arg
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
name|start
operator|=
name|args
index|[
name|arg
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
operator|++
name|arg
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
operator|++
name|arg
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
try|try
block|{
name|Occurrences
name|occur
index|[]
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getTextEngine
argument_list|()
operator|.
name|scanIndexTerms
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
name|qnames
argument_list|,
name|start
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|4
condition|)
block|{
name|Occurrences
name|occur2
index|[]
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getTextEngine
argument_list|()
operator|.
name|scanIndexTerms
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
name|start
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
name|Occurrences
name|t
index|[]
init|=
operator|new
name|Occurrences
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
name|Vector
name|list
init|=
operator|new
name|Vector
argument_list|(
name|len
argument_list|)
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
if|if
condition|(
operator|!
name|list
operator|.
name|contains
argument_list|(
operator|new
name|Integer
argument_list|(
name|occur
index|[
name|j
index|]
operator|.
name|getOccurrences
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|occur
index|[
name|j
index|]
operator|.
name|getOccurrences
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|HashMap
name|map
init|=
operator|new
name|HashMap
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
operator|new
name|Integer
argument_list|(
name|j
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|data
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|map
operator|.
name|get
argument_list|(
operator|new
name|Integer
argument_list|(
name|occur
index|[
name|j
index|]
operator|.
name|getOccurrences
argument_list|()
argument_list|)
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
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
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
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

