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
name|text
package|;
end_package

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
name|List
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
name|Match
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
name|dom
operator|.
name|TextImpl
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
name|util
operator|.
name|FastQSort
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

begin_class
specifier|public
class|class
name|KWICDisplay
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|TEXT_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"text"
argument_list|,
name|Type
operator|.
name|TEXT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The text nodes"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|WIDTH_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"width"
argument_list|,
name|Type
operator|.
name|POSITIVE_INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The width"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|CALLBACK_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"callback-function"
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
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|RESULT_CALLBACK_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result-callback"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The result callback function"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|PARAMETERS_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"parameters"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The parameters passed into the last argument of the callback function"
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
literal|"kwic-display"
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
literal|"Deprecated: kwic functionality is now provided by an XQuery module, see "
operator|+
literal|"http://exist-org/kwic.html."
operator|+
literal|"This function takes a sequence of text nodes in $a, containing matches from a fulltext search. "
operator|+
literal|"It highlights matching strings within those text nodes in the same way as the text:highlight-matches "
operator|+
literal|"function. However, only a defined portion of the text surrounding the first match (and maybe following matches) "
operator|+
literal|"is returned. If the text preceding the first match is larger than the width specified in the second argument $b, "
operator|+
literal|"it will be truncated to fill no more than (width - keyword-length) / 2 characters. Likewise, the text following "
operator|+
literal|"the match will be truncated in such a way that the whole string sequence fits into width characters. "
operator|+
literal|"The third parameter $c is a callback function (defined with util:function). $d may contain an additional sequence of "
operator|+
literal|"values that will be passed to the last parameter of the callback function. Any matching character sequence is reported "
operator|+
literal|"to the callback function, and the "
operator|+
literal|"result of the function call is inserted into the resulting node set where the matching sequence occurred. "
operator|+
literal|"For example, you can use this to mark all matching terms with a<span class=\"highlight\">abc</span>. "
operator|+
literal|"The callback function should take 3 or 4 arguments: 1) the text sequence corresponding to the match as xs:string, "
operator|+
literal|"2) the text node to which this match belongs, 3) the sequence passed as last argument to kwic-display. "
operator|+
literal|"If the callback function accepts 4 arguments, the last argument will contain additional "
operator|+
literal|"information on the match as a sequence of 4 integers: a) the number of the match if there's more than "
operator|+
literal|"one match in a text node - the first match will be numbered 1; b) the offset of the match into the original text node "
operator|+
literal|"string; c) the length of the match as reported by the index."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|TEXT_ARG
block|,
name|WIDTH_ARG
block|,
name|CALLBACK_ARG
block|,
name|PARAMETERS_ARG
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
literal|"the results"
argument_list|)
argument_list|,
literal|"Improved kwic functionality is now provided by a separate XQuery module, see "
operator|+
literal|"http://www.exist-db.org/exist/apps/doc/kwic.xml."
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"kwic-display"
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
literal|"This function takes a sequence of text nodes in $a, containing matches from a fulltext search. "
operator|+
literal|"It highlights matching strings within those text nodes in the same way as the text:highlight-matches "
operator|+
literal|"function. However, only a defined portion of the text surrounding the first match (and maybe following matches) "
operator|+
literal|"is returned. If the text preceding the first match is larger than the width specified in the second argument $b, "
operator|+
literal|"it will be truncated to fill no more than (width - keyword-length) / 2 characters. Likewise, the text following "
operator|+
literal|"the match will be truncated in such a way that the whole string sequence fits into width characters. "
operator|+
literal|"The third parameter $c is a callback function (defined with util:function). $d may contain an additional sequence of "
operator|+
literal|"values that will be passed to the last parameter of the callback function. Any matching character sequence is reported "
operator|+
literal|"to the callback function, and the "
operator|+
literal|"result of the function call is inserted into the resulting node set where the matching sequence occurred. "
operator|+
literal|"For example, you can use this to mark all matching terms with a<span class=\"highlight\">abc</span>. "
operator|+
literal|"The callback function should take 3 or 4 arguments: 1) the text sequence corresponding to the match as xs:string, "
operator|+
literal|"2) the text node to which this match belongs, 3) the sequence passed as last argument to kwic-display. "
operator|+
literal|"If the callback function accepts 4 arguments, the last argument will contain additional "
operator|+
literal|"information on the match as a sequence of 4 integers: a) the number of the match if there's more than "
operator|+
literal|"one match in a text node - the first match will be numbered 1; b) the offset of the match into the original text node "
operator|+
literal|"string; c) the length of the match as reported by the index."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|TEXT_ARG
block|,
name|WIDTH_ARG
block|,
name|CALLBACK_ARG
block|,
name|RESULT_CALLBACK_ARG
block|,
name|PARAMETERS_ARG
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
literal|"the results"
argument_list|)
argument_list|,
literal|"Improved kwic functionality is now provided by a separate XQuery module, see "
operator|+
literal|"http://www.exist-db.org/exist/apps/doc/kwic.xml."
argument_list|)
block|}
decl_stmt|;
specifier|public
name|KWICDisplay
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
specifier|final
name|FunctionReference
name|call
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
name|FunctionReference
name|resultCallback
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|5
condition|)
block|{
name|resultCallback
operator|=
operator|(
name|FunctionReference
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
expr_stmt|;
block|}
specifier|final
name|int
name|width
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
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|processText
argument_list|(
name|builder
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|,
name|width
argument_list|,
name|call
argument_list|,
name|resultCallback
argument_list|,
name|args
index|[
name|getArgumentCount
argument_list|()
operator|-
literal|1
index|]
argument_list|)
decl_stmt|;
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
specifier|final
name|Sequence
name|processText
parameter_list|(
name|MemTreeBuilder
name|builder
parameter_list|,
name|Sequence
name|nodes
parameter_list|,
name|int
name|width
parameter_list|,
name|FunctionReference
name|callback
parameter_list|,
name|FunctionReference
name|resultCallback
parameter_list|,
name|Sequence
name|extraArgs
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|NodeValue
name|node
decl_stmt|;
name|List
argument_list|<
name|Match
operator|.
name|Offset
argument_list|>
name|offsets
init|=
literal|null
decl_stmt|;
name|NodeProxy
name|firstProxy
init|=
literal|null
decl_stmt|;
comment|// First step: scan the passed node sequence and collect the string values of all nodes.
comment|// Translate the relative offsets into absolute offsets.
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|nodes
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
name|node
operator|=
operator|(
name|NodeValue
operator|)
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Function kwic-display"
operator|+
literal|" can not be invoked on constructed nodes"
argument_list|)
throw|;
block|}
name|NodeProxy
name|proxy
init|=
operator|(
name|NodeProxy
operator|)
name|node
decl_stmt|;
comment|// remember the first node, we need it later
if|if
condition|(
name|firstProxy
operator|==
literal|null
condition|)
block|{
name|firstProxy
operator|=
name|proxy
expr_stmt|;
block|}
specifier|final
name|TextImpl
name|text
init|=
operator|(
name|TextImpl
operator|)
name|proxy
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|Match
name|next
init|=
name|proxy
operator|.
name|getMatches
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|text
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
block|{
name|offsets
operator|=
operator|new
name|ArrayList
argument_list|<
name|Match
operator|.
name|Offset
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|freq
init|=
name|next
operator|.
name|getFrequency
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
name|freq
condition|;
name|j
operator|++
control|)
block|{
comment|// translate the relative offset into an absolute offset and add it to the list
specifier|final
name|Match
operator|.
name|Offset
name|offset
init|=
name|next
operator|.
name|getOffset
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|offset
operator|.
name|setOffset
argument_list|(
name|str
operator|.
name|length
argument_list|()
operator|+
name|offset
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|offsets
operator|.
name|add
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
name|next
operator|=
name|next
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
comment|// append the string value of the node to the buffer
name|str
operator|.
name|append
argument_list|(
name|text
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Second step: output the text
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|final
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|int
name|nodeNr
decl_stmt|;
name|int
name|currentWidth
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
block|{
comment|// no matches: just output the entire text
if|if
condition|(
name|width
operator|>
name|str
operator|.
name|length
argument_list|()
condition|)
block|{
name|width
operator|=
name|str
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|nodeNr
operator|=
name|builder
operator|.
name|characters
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|width
argument_list|)
argument_list|)
expr_stmt|;
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
name|nodeNr
argument_list|)
argument_list|)
expr_stmt|;
name|currentWidth
operator|+=
name|width
expr_stmt|;
block|}
else|else
block|{
comment|// sort the offsets
name|FastQSort
operator|.
name|sort
argument_list|(
name|offsets
argument_list|,
literal|0
argument_list|,
name|offsets
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|int
name|nextOffset
init|=
literal|0
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|int
name|lastNodeNr
init|=
operator|-
literal|1
decl_stmt|;
comment|// prepare array for callback function arguments
specifier|final
name|Sequence
name|params
index|[]
init|=
operator|new
name|Sequence
index|[
name|callback
operator|.
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
index|]
decl_stmt|;
name|params
index|[
literal|1
index|]
operator|=
name|firstProxy
expr_stmt|;
name|params
index|[
literal|2
index|]
operator|=
name|extraArgs
expr_stmt|;
comment|// handle the first match: if the text to the left of the match
comment|// is larger than half of the width, truncate it.
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|>
name|width
condition|)
block|{
specifier|final
name|Match
operator|.
name|Offset
name|firstMatch
init|=
name|offsets
operator|.
name|get
argument_list|(
name|nextOffset
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstMatch
operator|.
name|getOffset
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|leftWidth
init|=
operator|(
name|width
operator|-
name|firstMatch
operator|.
name|getLength
argument_list|()
operator|)
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|firstMatch
operator|.
name|getOffset
argument_list|()
operator|>
name|leftWidth
condition|)
block|{
name|pos
operator|=
name|truncateStart
argument_list|(
name|str
argument_list|,
name|firstMatch
operator|.
name|getOffset
argument_list|()
operator|-
name|leftWidth
argument_list|,
name|firstMatch
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|leftWidth
operator|=
name|firstMatch
operator|.
name|getOffset
argument_list|()
operator|-
name|pos
expr_stmt|;
block|}
else|else
block|{
name|leftWidth
operator|=
name|firstMatch
operator|.
name|getOffset
argument_list|()
expr_stmt|;
block|}
name|nodeNr
operator|=
name|builder
operator|.
name|characters
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|pos
operator|+
name|leftWidth
argument_list|)
argument_list|)
expr_stmt|;
comment|// adjacent chunks of text will be merged into one text node. we may
comment|// thus get duplicate nodes here. check the nodeNr to avoid adding
comment|// the same node twice.
if|if
condition|(
name|lastNodeNr
operator|!=
name|nodeNr
condition|)
block|{
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
name|nodeNr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastNodeNr
operator|=
name|nodeNr
expr_stmt|;
name|currentWidth
operator|+=
name|leftWidth
expr_stmt|;
name|pos
operator|+=
name|leftWidth
expr_stmt|;
block|}
comment|// put the matching term into argument 0 of the callback function
name|params
index|[
literal|0
index|]
operator|=
operator|new
name|StringValue
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|firstMatch
operator|.
name|getOffset
argument_list|()
argument_list|,
name|firstMatch
operator|.
name|getOffset
argument_list|()
operator|+
name|firstMatch
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// if the callback function accepts 4 arguments, the last argument should contain additional
comment|// information on the match:
if|if
condition|(
name|callback
operator|.
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|4
condition|)
block|{
name|params
index|[
literal|3
index|]
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
name|params
index|[
literal|3
index|]
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|nextOffset
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|params
index|[
literal|3
index|]
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|firstMatch
operator|.
name|getOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|params
index|[
literal|3
index|]
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|firstMatch
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now execute the callback func.
specifier|final
name|Sequence
name|callbackResult
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
comment|// iterate through the result of the callback
for|for
control|(
specifier|final
name|SequenceIterator
name|iter
init|=
name|callbackResult
operator|.
name|iterate
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Item
name|next
init|=
name|iter
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|nodeNr
operator|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getLastNode
argument_list|()
expr_stmt|;
try|try
block|{
name|next
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
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
name|lastNodeNr
operator|=
name|nodeNr
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Internal error while copying nodes: "
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
block|}
block|}
name|currentWidth
operator|+=
name|firstMatch
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|pos
operator|+=
name|firstMatch
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|width
operator|=
name|str
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|// output the rest of the text and matches
name|Match
operator|.
name|Offset
name|offset
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|nextOffset
init|;
name|i
operator|<
name|offsets
operator|.
name|size
argument_list|()
operator|&&
name|currentWidth
operator|<
name|width
condition|;
name|i
operator|++
control|)
block|{
name|offset
operator|=
name|offsets
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|offset
operator|.
name|getOffset
argument_list|()
operator|>
name|pos
condition|)
block|{
name|int
name|len
init|=
name|offset
operator|.
name|getOffset
argument_list|()
operator|-
name|pos
decl_stmt|;
if|if
condition|(
name|currentWidth
operator|+
name|len
operator|>
name|width
condition|)
block|{
name|len
operator|=
name|width
operator|-
name|currentWidth
expr_stmt|;
block|}
name|nodeNr
operator|=
name|builder
operator|.
name|characters
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|pos
operator|+
name|len
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastNodeNr
operator|!=
name|nodeNr
condition|)
block|{
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
name|nodeNr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|currentWidth
operator|+=
name|len
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
block|}
if|if
condition|(
name|currentWidth
operator|+
name|offset
operator|.
name|getLength
argument_list|()
operator|<
name|width
condition|)
block|{
comment|// put the matching term into argument 0 of the callback function
name|params
index|[
literal|0
index|]
operator|=
operator|new
name|StringValue
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|offset
operator|.
name|getOffset
argument_list|()
argument_list|,
name|offset
operator|.
name|getOffset
argument_list|()
operator|+
name|offset
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// if the callback function accepts 4 arguments, the last argument should contain additional
comment|// information on the match:
if|if
condition|(
name|callback
operator|.
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|4
condition|)
block|{
name|params
index|[
literal|3
index|]
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
name|params
index|[
literal|3
index|]
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|params
index|[
literal|3
index|]
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|offset
operator|.
name|getOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|params
index|[
literal|3
index|]
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|offset
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// execute the callback function
specifier|final
name|Sequence
name|callbackResult
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
specifier|final
name|SequenceIterator
name|iter
init|=
name|callbackResult
operator|.
name|iterate
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Item
name|next
init|=
name|iter
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|nodeNr
operator|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getLastNode
argument_list|()
expr_stmt|;
try|try
block|{
name|next
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
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
name|lastNodeNr
operator|=
name|nodeNr
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Internal error while copying nodes: "
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
block|}
block|}
name|currentWidth
operator|+=
name|offset
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|pos
operator|+=
name|offset
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
comment|// print the final text chunk if more space is available
if|if
condition|(
name|currentWidth
operator|<
name|width
operator|&&
name|pos
operator|<
name|str
operator|.
name|length
argument_list|()
condition|)
block|{
name|boolean
name|truncated
init|=
literal|false
decl_stmt|;
name|int
name|len
init|=
name|str
operator|.
name|length
argument_list|()
operator|-
name|pos
decl_stmt|;
if|if
condition|(
name|len
operator|>
name|width
operator|-
name|currentWidth
condition|)
block|{
name|truncated
operator|=
literal|true
expr_stmt|;
name|len
operator|=
name|width
operator|-
name|currentWidth
expr_stmt|;
block|}
name|nodeNr
operator|=
name|builder
operator|.
name|characters
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|pos
operator|+
name|len
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastNodeNr
operator|!=
name|nodeNr
condition|)
block|{
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
name|nodeNr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastNodeNr
operator|=
name|nodeNr
expr_stmt|;
name|currentWidth
operator|+=
name|len
expr_stmt|;
if|if
condition|(
name|truncated
condition|)
block|{
name|nodeNr
operator|=
name|builder
operator|.
name|characters
argument_list|(
literal|" ..."
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastNodeNr
operator|!=
name|nodeNr
condition|)
block|{
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
name|nodeNr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastNodeNr
operator|=
name|nodeNr
expr_stmt|;
block|}
block|}
block|}
comment|// if the user specified a result callback function, call it now
if|if
condition|(
name|resultCallback
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Sequence
name|params
index|[]
init|=
operator|new
name|Sequence
index|[
literal|3
index|]
decl_stmt|;
name|params
index|[
literal|0
index|]
operator|=
name|result
expr_stmt|;
name|params
index|[
literal|1
index|]
operator|=
operator|new
name|IntegerValue
argument_list|(
name|currentWidth
argument_list|)
expr_stmt|;
name|params
index|[
literal|2
index|]
operator|=
name|extraArgs
expr_stmt|;
return|return
name|resultCallback
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|result
return|;
block|}
block|}
specifier|private
specifier|final
specifier|static
name|int
name|truncateStart
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
if|if
condition|(
name|start
operator|>
literal|0
operator|&&
operator|!
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|buf
operator|.
name|charAt
argument_list|(
name|start
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|start
return|;
block|}
while|while
condition|(
name|start
operator|<
name|end
operator|&&
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|buf
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
argument_list|)
condition|)
block|{
name|start
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|start
operator|<
name|end
operator|&&
operator|!
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|buf
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
argument_list|)
condition|)
block|{
name|start
operator|++
expr_stmt|;
block|}
return|return
name|start
return|;
block|}
block|}
end_class

end_unit

