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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|DocumentImpl
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
name|storage
operator|.
name|serializers
operator|.
name|NativeSerializer
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
name|w3c
operator|.
name|dom
operator|.
name|DOMException
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
name|Attributes
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

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
literal|"highlight-matches"
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
literal|"Highlight matching strings within text nodes that resulted from a fulltext search. "
operator|+
literal|"When searching with one of the fulltext operators or functions, eXist keeps track of "
operator|+
literal|"the fulltext matches within the text. Usually, the serializer will mark those matches by enclosing them "
operator|+
literal|"into an 'exist:match' element. One can then use an XSLT stylesheet to replace those match elements "
operator|+
literal|"and highlight matches to the user. However, this is not always possible, so Instead of using an XSLT "
operator|+
literal|"to post-process the serialized output, the "
operator|+
literal|"highlight-matches function provides direct access to the matching portions of the text within XQuery. "
operator|+
literal|"The function takes a sequence of text nodes as first argument and a callback function (defined with "
operator|+
literal|"util:function) as second parameter. Text nodes without matches will be returned as they are. However, if the text "
operator|+
literal|"contains a match marker, the matching character sequence is reported to the callback function, and the "
operator|+
literal|"result of the function call is inserted into the resulting node set where the matching sequence occurred. For example, "
operator|+
literal|"you can use this to mark all matching terms with a<span class=\"highlight\">abc</span>."
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
name|TEXT
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
name|FUNCTION_REFERENCE
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
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
name|FunctionReference
name|ref
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
decl_stmt|;
name|FunctionCall
name|call
init|=
name|ref
operator|.
name|getFunctionCall
argument_list|()
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
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
name|result
operator|.
name|add
argument_list|(
name|v
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
name|String
name|s
init|=
name|processText
argument_list|(
operator|(
name|TextImpl
operator|)
name|p
operator|.
name|getNode
argument_list|()
argument_list|,
name|p
operator|.
name|getMatches
argument_list|()
argument_list|)
decl_stmt|;
name|display
argument_list|(
name|s
argument_list|,
name|builder
argument_list|,
name|call
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
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
name|String
name|processText
parameter_list|(
name|TextImpl
name|text
parameter_list|,
name|Match
name|match
parameter_list|)
block|{
if|if
condition|(
name|match
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// prepare a regular expression to mark match-terms
name|StringBuffer
name|expr
init|=
literal|null
decl_stmt|;
name|Match
name|next
init|=
name|match
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
operator|==
name|text
operator|.
name|getGID
argument_list|()
condition|)
block|{
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
block|{
name|expr
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|expr
operator|.
name|append
argument_list|(
literal|"\\b("
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expr
operator|.
name|length
argument_list|()
operator|>
literal|5
condition|)
name|expr
operator|.
name|append
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
name|expr
operator|.
name|append
argument_list|(
name|next
operator|.
name|getMatchingTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|next
operator|=
name|next
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|expr
operator|!=
literal|null
condition|)
block|{
name|expr
operator|.
name|append
argument_list|(
literal|")\\b"
argument_list|)
expr_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|UNICODE_CASE
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|text
operator|.
name|getData
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|matcher
operator|.
name|replaceAll
argument_list|(
literal|"||$1||"
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|final
name|void
name|display
parameter_list|(
name|String
name|data
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|,
name|FunctionCall
name|call
parameter_list|,
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|p0
init|=
literal|0
decl_stmt|,
name|p1
decl_stmt|;
name|boolean
name|inTerm
init|=
literal|false
decl_stmt|;
name|int
name|nodeNr
decl_stmt|;
name|Sequence
name|params
index|[]
init|=
operator|new
name|Sequence
index|[
literal|1
index|]
decl_stmt|;
while|while
condition|(
name|p0
operator|<
name|data
operator|.
name|length
argument_list|()
condition|)
block|{
name|p1
operator|=
name|data
operator|.
name|indexOf
argument_list|(
literal|"||"
argument_list|,
name|p0
argument_list|)
expr_stmt|;
if|if
condition|(
name|p1
operator|<
literal|0
condition|)
block|{
name|nodeNr
operator|=
name|builder
operator|.
name|characters
argument_list|(
name|data
operator|.
name|substring
argument_list|(
name|p0
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
break|break;
block|}
if|if
condition|(
name|inTerm
condition|)
block|{
name|params
index|[
literal|0
index|]
operator|=
operator|new
name|StringValue
argument_list|(
name|data
operator|.
name|substring
argument_list|(
name|p0
argument_list|,
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|call
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|inTerm
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|inTerm
operator|=
literal|true
expr_stmt|;
name|nodeNr
operator|=
name|builder
operator|.
name|characters
argument_list|(
name|data
operator|.
name|substring
argument_list|(
name|p0
argument_list|,
name|p1
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
block|}
name|p0
operator|=
name|p1
operator|+
literal|2
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

