begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|NewArrayNodeSet
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
name|AbstractMatchListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|EmbeddedXMLStreamReader
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
name|serializer
operator|.
name|AttrList
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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneMatchListener
extends|extends
name|AbstractMatchListener
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LuceneMatchListener
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Match
name|match
decl_stmt|;
specifier|private
name|Set
name|termSet
decl_stmt|;
specifier|private
name|Map
name|nodesWithMatch
decl_stmt|;
specifier|private
name|LuceneIndex
name|index
decl_stmt|;
specifier|private
name|LuceneConfig
name|config
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|public
name|LuceneMatchListener
parameter_list|(
name|LuceneIndex
name|index
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|reset
argument_list|(
name|broker
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasMatches
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|Match
name|nextMatch
init|=
name|proxy
operator|.
name|getMatches
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextMatch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nextMatch
operator|.
name|getIndexId
argument_list|()
operator|==
name|LuceneIndex
operator|.
name|ID
condition|)
block|{
return|return
literal|true
return|;
block|}
name|nextMatch
operator|=
name|nextMatch
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|protected
name|void
name|reset
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|match
operator|=
name|proxy
operator|.
name|getMatches
argument_list|()
expr_stmt|;
name|setNextInChain
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|IndexSpec
name|indexConf
init|=
name|proxy
operator|.
name|getDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
operator|.
name|getIndexConfiguration
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexConf
operator|!=
literal|null
condition|)
name|config
operator|=
operator|(
name|LuceneConfig
operator|)
name|indexConf
operator|.
name|getCustomIndexSpec
argument_list|(
name|LuceneIndex
operator|.
name|ID
argument_list|)
expr_stmt|;
name|getTerms
argument_list|()
expr_stmt|;
name|nodesWithMatch
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
comment|/* Check if an index is defined on an ancestor of the current node.         * If yes, scan the ancestor to get the offset of the first character         * in the current node. For example, if the indexed node is&lt;a>abc&lt;b>de&lt;/b></a>         * and we query for //a[text:ngram-contains(., 'de')]/b, proxy will be a&lt;b> node, but         * the offsets of the matches are relative to the start of&lt;a>.         */
name|NodeSet
name|ancestors
init|=
literal|null
decl_stmt|;
name|Match
name|nextMatch
init|=
name|this
operator|.
name|match
decl_stmt|;
while|while
condition|(
name|nextMatch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|proxy
operator|.
name|getNodeId
argument_list|()
operator|.
name|isDescendantOf
argument_list|(
name|nextMatch
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|ancestors
operator|==
literal|null
condition|)
name|ancestors
operator|=
operator|new
name|NewArrayNodeSet
argument_list|()
expr_stmt|;
name|ancestors
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|proxy
operator|.
name|getDocument
argument_list|()
argument_list|,
name|nextMatch
operator|.
name|getNodeId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|nextMatch
operator|=
name|nextMatch
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ancestors
operator|!=
literal|null
operator|&&
operator|!
name|ancestors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|ancestors
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|scanMatches
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|,
name|AttrList
name|attribs
parameter_list|)
throws|throws
name|SAXException
block|{
name|Match
name|nextMatch
init|=
name|match
decl_stmt|;
comment|// check if there are any matches in the current element
comment|// if yes, push a NodeOffset object to the stack to track
comment|// the node contents
while|while
condition|(
name|nextMatch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nextMatch
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|getCurrentNode
argument_list|()
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|scanMatches
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|getCurrentNode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|nextMatch
operator|=
name|nextMatch
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|startElement
argument_list|(
name|qname
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
name|NodeId
name|nodeId
init|=
name|getCurrentNode
argument_list|()
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
name|Offset
name|offset
init|=
operator|(
name|Offset
operator|)
name|nodesWithMatch
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|==
literal|null
condition|)
name|super
operator|.
name|characters
argument_list|(
name|seq
argument_list|)
expr_stmt|;
else|else
block|{
name|String
name|s
init|=
name|seq
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|offset
operator|.
name|startOffset
operator|>
name|pos
condition|)
block|{
if|if
condition|(
name|offset
operator|.
name|startOffset
operator|>
name|seq
operator|.
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"start offset out of bounds"
argument_list|)
throw|;
name|super
operator|.
name|characters
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|offset
operator|.
name|startOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|end
init|=
name|offset
operator|.
name|endOffset
decl_stmt|;
if|if
condition|(
name|end
operator|>
name|s
operator|.
name|length
argument_list|()
condition|)
name|end
operator|=
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|MATCH_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|offset
operator|.
name|startOffset
argument_list|,
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|endElement
argument_list|(
name|MATCH_ELEMENT
argument_list|)
expr_stmt|;
name|pos
operator|=
name|end
expr_stmt|;
name|offset
operator|=
name|offset
operator|.
name|next
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|<
name|seq
operator|.
name|length
argument_list|()
condition|)
name|super
operator|.
name|characters
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|scanMatches
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
comment|// Collect the text content of all descendants of p. Remember the start offsets
comment|// of the text nodes for later use.
name|TextExtractor
name|extractor
init|=
operator|new
name|DefaultTextExtractor
argument_list|()
decl_stmt|;
name|extractor
operator|.
name|configure
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|OffsetList
name|offsets
init|=
operator|new
name|OffsetList
argument_list|()
decl_stmt|;
name|int
name|level
init|=
literal|0
decl_stmt|;
name|int
name|textOffset
init|=
literal|0
decl_stmt|;
try|try
block|{
name|EmbeddedXMLStreamReader
name|reader
init|=
name|broker
operator|.
name|getXMLStreamReader
argument_list|(
name|p
argument_list|,
literal|false
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|ev
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|ev
condition|)
block|{
case|case
name|XMLStreamReader
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
operator|--
name|level
operator|<
literal|0
condition|)
break|break;
name|textOffset
operator|+=
name|extractor
operator|.
name|endElement
argument_list|(
name|reader
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|START_ELEMENT
case|:
operator|++
name|level
expr_stmt|;
name|textOffset
operator|+=
name|extractor
operator|.
name|startElement
argument_list|(
name|reader
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|CHARACTERS
case|:
name|NodeId
name|nodeId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|EmbeddedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
name|offsets
operator|.
name|add
argument_list|(
name|textOffset
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|textOffset
operator|+=
name|extractor
operator|.
name|characters
argument_list|(
name|reader
operator|.
name|getXMLText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem found while serializing XML: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem found while serializing XML: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Use Lucene's analyzer to tokenize the text and find matching query terms
name|TokenStream
name|stream
init|=
name|index
operator|.
name|getDefaultAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
operator|new
name|StringReader
argument_list|(
name|extractor
operator|.
name|getText
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Token
name|token
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|stream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|text
init|=
name|token
operator|.
name|termText
argument_list|()
decl_stmt|;
if|if
condition|(
name|termSet
operator|.
name|contains
argument_list|(
name|text
argument_list|)
condition|)
block|{
name|int
name|idx
init|=
name|offsets
operator|.
name|getIndex
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|offsets
operator|.
name|ids
index|[
name|idx
index|]
decl_stmt|;
name|Offset
name|offset
init|=
operator|(
name|Offset
operator|)
name|nodesWithMatch
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|!=
literal|null
condition|)
name|offset
operator|.
name|add
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
operator|-
name|offsets
operator|.
name|offsets
index|[
name|idx
index|]
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
operator|-
name|offsets
operator|.
name|offsets
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
else|else
name|nodesWithMatch
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
operator|new
name|Offset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
operator|-
name|offsets
operator|.
name|offsets
index|[
name|idx
index|]
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
operator|-
name|offsets
operator|.
name|offsets
index|[
name|idx
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem found while serializing XML: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Get all query terms from the original queries.      */
specifier|private
name|void
name|getTerms
parameter_list|()
block|{
name|Set
name|queries
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|Set
name|set
init|=
operator|new
name|TreeSet
argument_list|()
decl_stmt|;
name|Match
name|nextMatch
init|=
name|this
operator|.
name|match
decl_stmt|;
while|while
condition|(
name|nextMatch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nextMatch
operator|.
name|getIndexId
argument_list|()
operator|==
name|LuceneIndex
operator|.
name|ID
condition|)
block|{
name|Query
name|query
init|=
operator|(
operator|(
name|LuceneIndexWorker
operator|.
name|LuceneMatch
operator|)
name|nextMatch
operator|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|queries
operator|.
name|contains
argument_list|(
name|query
argument_list|)
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|index
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|query
operator|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while highlighting lucene query matches: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|index
operator|.
name|releaseReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|extractTerms
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
block|}
name|nextMatch
operator|=
name|nextMatch
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
name|termSet
operator|=
operator|new
name|TreeSet
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Term
name|term
init|=
operator|(
name|Term
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|termSet
operator|.
name|add
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|OffsetList
block|{
name|int
index|[]
name|offsets
init|=
operator|new
name|int
index|[
literal|16
index|]
decl_stmt|;
name|NodeId
index|[]
name|ids
init|=
operator|new
name|NodeId
index|[
literal|16
index|]
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
name|void
name|add
parameter_list|(
name|int
name|offset
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|len
operator|==
name|offsets
operator|.
name|length
condition|)
block|{
name|int
index|[]
name|tempOffsets
init|=
operator|new
name|int
index|[
name|len
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|offsets
argument_list|,
literal|0
argument_list|,
name|tempOffsets
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|offsets
operator|=
name|tempOffsets
expr_stmt|;
name|NodeId
index|[]
name|tempIds
init|=
operator|new
name|NodeId
index|[
name|len
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|ids
argument_list|,
literal|0
argument_list|,
name|tempIds
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|ids
operator|=
name|tempIds
expr_stmt|;
block|}
name|offsets
index|[
name|len
index|]
operator|=
name|offset
expr_stmt|;
name|ids
index|[
name|len
operator|++
index|]
operator|=
name|nodeId
expr_stmt|;
block|}
name|int
name|getIndex
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|offsets
index|[
name|i
index|]
operator|<=
name|offset
operator|&&
operator|(
name|i
operator|+
literal|1
operator|==
name|len
operator|||
name|offsets
index|[
name|i
operator|+
literal|1
index|]
operator|>
name|offset
operator|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|private
class|class
name|Offset
block|{
name|int
name|startOffset
decl_stmt|;
name|int
name|endOffset
decl_stmt|;
name|Offset
name|next
init|=
literal|null
decl_stmt|;
name|Offset
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
block|}
name|void
name|add
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|Offset
name|next
init|=
name|this
decl_stmt|;
while|while
condition|(
name|next
operator|.
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
block|}
name|next
operator|.
name|next
operator|=
operator|new
name|Offset
argument_list|(
name|offset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

