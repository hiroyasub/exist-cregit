begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|ngram
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
name|persistent
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
name|MatchListener
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
name|ngram
operator|.
name|NGramIndex
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
name|ngram
operator|.
name|NGramIndexWorker
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
name|ngram
operator|.
name|NGramMatchCallback
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
name|dom
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
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
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
name|serializers
operator|.
name|Serializer
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
name|serializer
operator|.
name|Receiver
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|HighlightMatches
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
literal|"filter-matches"
argument_list|,
name|NGramModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|NGramModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Highlight matching strings within text nodes that resulted from a ngram search. "
operator|+
literal|"The function takes a sequence of nodes as first argument $nodes and a callback function (defined with "
operator|+
literal|"util:function) as second parameter $function-reference. Each node in $nodes will be copied into a new document fragment. "
operator|+
literal|"For each ngram match found while copying a node, the callback function in $function-reference will be called once. The "
operator|+
literal|"callback function should take 2 arguments:\n\n1) the matching text string as xs:string,\n2) the node to which this "
operator|+
literal|"text string belongs.\n\nThe callback function should return zero or more nodes, which will be inserted into the "
operator|+
literal|"resulting node set at the place where the matching text sequence occurred.\n\n"
operator|+
literal|"Note: a ngram match on mixed content may span multiple nodes. In this case, the callback function is called "
operator|+
literal|"once for every text node which is part of the matching text sequence."
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
name|ZERO_OR_MORE
argument_list|,
literal|"The sequence of nodes"
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
literal|"The callback function"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"a resulting node set"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|HighlightMatches
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
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
init|(
name|FunctionReference
name|func
init|=
operator|(
name|FunctionReference
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
init|)
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|NGramIndexWorker
name|index
init|=
operator|(
name|NGramIndexWorker
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
name|NGramIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
name|DocumentBuilderReceiver
name|docBuilder
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|MatchCallback
name|matchCb
init|=
operator|new
name|MatchCallback
argument_list|(
name|func
argument_list|,
name|docBuilder
argument_list|)
decl_stmt|;
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|0
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
control|)
block|{
name|NodeValue
name|v
init|=
operator|(
name|NodeValue
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|nodeNr
init|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getLastNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
block|{
operator|(
operator|(
name|NodeImpl
operator|)
name|v
operator|)
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|docBuilder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|v
decl_stmt|;
name|MatchListener
name|ml
init|=
name|index
operator|.
name|getMatchListener
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|p
argument_list|,
name|matchCb
argument_list|)
decl_stmt|;
name|Receiver
name|receiver
decl_stmt|;
if|if
condition|(
name|ml
operator|==
literal|null
condition|)
name|receiver
operator|=
name|docBuilder
expr_stmt|;
else|else
block|{
name|ml
operator|.
name|setNextInChain
argument_list|(
name|docBuilder
argument_list|)
expr_stmt|;
name|receiver
operator|=
name|ml
expr_stmt|;
block|}
name|serializer
operator|.
name|setReceiver
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toReceiver
argument_list|(
operator|(
name|NodeProxy
operator|)
name|v
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
operator|++
name|nodeNr
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
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
specifier|private
class|class
name|MatchCallback
implements|implements
name|NGramMatchCallback
block|{
specifier|private
name|FunctionReference
name|callback
decl_stmt|;
specifier|private
name|DocumentBuilderReceiver
name|docBuilder
decl_stmt|;
specifier|private
name|MatchCallback
parameter_list|(
name|FunctionReference
name|callback
parameter_list|,
name|DocumentBuilderReceiver
name|docBuilder
parameter_list|)
block|{
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
name|this
operator|.
name|docBuilder
operator|=
name|docBuilder
expr_stmt|;
block|}
specifier|public
name|void
name|match
parameter_list|(
name|Receiver
name|receiver
parameter_list|,
name|String
name|matchingText
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
name|Sequence
name|params
index|[]
init|=
block|{
operator|new
name|StringValue
argument_list|(
name|matchingText
argument_list|)
block|,
name|node
block|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
block|}
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|Sequence
name|seq
init|=
name|callback
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
decl_stmt|;
for|for
control|(
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
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|next
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|docBuilder
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

