begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|fn
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|util
operator|.
name|Collations
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
name|AnalyzeContextInfo
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
name|ErrorCodes
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
name|Function
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
name|FunctionReturnSequenceType
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
name|Item
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
name|SequenceIterator
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

begin_class
specifier|public
class|class
name|FunSort
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
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sort"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Sorts a supplied sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"input"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|""
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
literal|"the first item or the empty sequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sort"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Sorts a supplied sequence, based on the value of a sort key supplied as a function."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"input"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|""
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collation"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|""
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
literal|"the resulting sequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sort"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Sorts a supplied sequence, based on the value of a sort key supplied as a function."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"input"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|""
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collation"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|""
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"key"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|""
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
literal|"the resulting sequence"
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|AnalyzeContextInfo
name|cachedContextInfo
decl_stmt|;
specifier|public
name|FunSort
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
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|getContext
argument_list|()
operator|.
name|getXQueryVersion
argument_list|()
operator|<
literal|30
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0003
argument_list|,
literal|"Function "
operator|+
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" is only supported for xquery version \"3.0\" and later."
argument_list|)
throw|;
block|}
name|cachedContextInfo
operator|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|super
operator|.
name|analyze
argument_list|(
name|cachedContextInfo
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
name|Sequence
name|seq
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|Collator
name|collator
init|=
name|collator
argument_list|(
name|args
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|FunctionReference
name|ref
init|=
name|function
argument_list|(
name|args
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Sequence
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|refArgs
index|[]
init|=
operator|new
name|Sequence
index|[
literal|1
index|]
decl_stmt|;
name|Item
name|item
decl_stmt|;
name|Sequence
name|value
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|seq
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
name|item
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|refArgs
index|[
literal|0
index|]
operator|=
name|item
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|value
operator|=
name|ref
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
name|refArgs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|item
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
name|keys
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|sort
argument_list|(
name|seq
argument_list|,
name|keys
argument_list|,
name|collator
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|sort
parameter_list|(
name|Sequence
name|seq
parameter_list|,
name|ArrayList
argument_list|<
name|Sequence
argument_list|>
name|keys
parameter_list|,
name|Collator
name|collator
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//preparing
specifier|final
name|int
name|size
init|=
name|seq
operator|.
name|getItemCount
argument_list|()
decl_stmt|;
specifier|final
name|Integer
index|[]
name|order
init|=
operator|new
name|Integer
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
name|order
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
comment|//sorting
try|try
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|order
argument_list|,
parameter_list|(
name|i1
parameter_list|,
name|i2
parameter_list|)
lambda|->
block|{
name|Sequence
name|seq1
init|=
name|keys
operator|.
name|get
argument_list|(
name|i1
argument_list|)
decl_stmt|;
name|Sequence
name|seq2
init|=
name|keys
operator|.
name|get
argument_list|(
name|i2
argument_list|)
decl_stmt|;
name|int
name|size1
init|=
name|seq1
operator|.
name|getItemCount
argument_list|()
decl_stmt|;
name|int
name|size2
init|=
name|seq2
operator|.
name|getItemCount
argument_list|()
decl_stmt|;
name|int
name|minSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|size1
argument_list|,
name|size2
argument_list|)
decl_stmt|;
if|if
condition|(
name|size1
operator|==
literal|0
condition|)
block|{
return|return
operator|-
name|size2
return|;
block|}
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|minSize
condition|;
name|pos
operator|++
control|)
block|{
name|Item
name|item1
init|=
name|seq1
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|Item
name|item2
init|=
name|seq2
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
comment|//          int res;
comment|//          if (item1 instanceof org.exist.dom.memtree.NodeImpl&& (!(item2 instanceof org.exist.dom.memtree.NodeImpl))) {
comment|//            res = Constants.INFERIOR;
comment|//          } else if (item1 instanceof Comparable&& item2 instanceof Comparable) {
comment|//            res = ((Comparable) item1).compareTo(item2);
comment|//          } else {
comment|//            res = Constants.INFERIOR;
comment|//          }
name|int
name|res
init|=
name|Constants
operator|.
name|EQUAL
decl_stmt|;
if|if
condition|(
name|FunDeepEqual
operator|.
name|deepEquals
argument_list|(
name|item1
argument_list|,
name|item2
argument_list|,
name|collator
argument_list|)
condition|)
block|{
continue|continue;
comment|//TODO: } else if (isNAN)
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item1
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item2
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
try|try
block|{
name|res
operator|=
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|item1
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|item2
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|item1
operator|instanceof
name|Comparable
operator|&&
name|item2
operator|instanceof
name|Comparable
condition|)
block|{
name|res
operator|=
operator|(
operator|(
name|Comparable
operator|)
name|item1
operator|)
operator|.
name|compareTo
argument_list|(
name|item2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|=
name|Constants
operator|.
name|INFERIOR
expr_stmt|;
block|}
if|if
condition|(
name|res
operator|!=
name|Constants
operator|.
name|EQUAL
condition|)
return|return
name|res
return|;
block|}
return|return
operator|(
name|size1
operator|-
name|size2
operator|)
return|;
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|FunSort
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
comment|//form final sequence
specifier|final
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|(
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|keepUnOrdered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
name|order
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Collator
name|collator
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|int
name|pos
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|>
name|pos
condition|)
block|{
if|if
condition|(
name|args
index|[
name|pos
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|context
operator|.
name|getDefaultCollator
argument_list|()
return|;
block|}
name|String
name|collationURI
init|=
name|args
index|[
name|pos
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
return|return
name|context
operator|.
name|getCollator
argument_list|(
name|collationURI
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|context
operator|.
name|getDefaultCollator
argument_list|()
return|;
block|}
block|}
specifier|private
name|FunctionReference
name|function
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|int
name|pos
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|>
name|pos
condition|)
block|{
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
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
comment|// need to create a new AnalyzeContextInfo to avoid memory leak
comment|// cachedContextInfo will stay in memory
name|ref
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|(
name|cachedContextInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ref
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

