begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2008-2013 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|Analyzer
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|PhraseQuery
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
name|*
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
name|stax
operator|.
name|ExtendedXMLStreamReader
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
name|NodePath
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
name|XMLStreamConstants
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
name|*
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|util
operator|.
name|AttributeSource
operator|.
name|State
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
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|termMap
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Offset
argument_list|>
name|nodesWithMatch
decl_stmt|;
specifier|private
specifier|final
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
argument_list|<>
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
name|NodeProxy
name|p
range|:
name|ancestors
control|)
block|{
name|scanMatches
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
comment|// Collect the text content of all descendants of p.
comment|// Remember the start offsets of the text nodes for later use.
name|NodePath
name|path
init|=
name|getPath
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|LuceneIndexConfig
name|idxConf
init|=
name|config
operator|.
name|getConfig
argument_list|(
name|path
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
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
argument_list|,
name|idxConf
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
name|XMLStreamConstants
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
block|{
break|break;
block|}
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
name|XMLStreamConstants
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
name|XMLStreamConstants
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
name|ExtendedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
name|textOffset
operator|+=
name|extractor
operator|.
name|beforeCharacters
argument_list|()
expr_stmt|;
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
decl||
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
comment|// Retrieve the Analyzer for the NodeProxy that was used for
comment|// indexing and querying.
name|Analyzer
name|analyzer
init|=
name|idxConf
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
comment|// Otherwise use system default Lucene analyzer (from conf.xml)
comment|// to tokenize the text and find matching query terms.
name|analyzer
operator|=
name|index
operator|.
name|getDefaultAnalyzer
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Analyzer: "
operator|+
name|analyzer
operator|+
literal|" for path: "
operator|+
name|path
argument_list|)
expr_stmt|;
name|String
name|str
init|=
name|extractor
operator|.
name|getText
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//Token token;
try|try
block|{
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
operator|new
name|StringReader
argument_list|(
name|str
argument_list|)
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|MarkableTokenFilter
name|stream
init|=
operator|new
name|MarkableTokenFilter
argument_list|(
name|tokenStream
argument_list|)
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|text
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
name|termMap
operator|.
name|get
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
comment|// Phrase queries need to be handled differently to filter
comment|// out wrong matches: only the phrase should be marked, not
comment|// single words which may also occur elsewhere in the document
if|if
condition|(
name|query
operator|instanceof
name|PhraseQuery
condition|)
block|{
name|PhraseQuery
name|phraseQuery
init|=
operator|(
name|PhraseQuery
operator|)
name|query
decl_stmt|;
name|Term
index|[]
name|terms
init|=
name|phraseQuery
operator|.
name|getTerms
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|equals
argument_list|(
name|terms
index|[
literal|0
index|]
operator|.
name|text
argument_list|()
argument_list|)
condition|)
block|{
comment|// Scan the following text and collect tokens to see
comment|// if they are part of the phrase.
name|stream
operator|.
name|mark
argument_list|()
expr_stmt|;
name|int
name|t
init|=
literal|1
decl_stmt|;
name|List
argument_list|<
name|State
argument_list|>
name|stateList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|terms
operator|.
name|length
argument_list|)
decl_stmt|;
name|stateList
operator|.
name|add
argument_list|(
name|stream
operator|.
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
operator|&&
name|t
operator|<
name|terms
operator|.
name|length
condition|)
block|{
name|text
operator|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|text
operator|.
name|equals
argument_list|(
name|terms
index|[
name|t
index|]
operator|.
name|text
argument_list|()
argument_list|)
condition|)
block|{
name|stateList
operator|.
name|add
argument_list|(
name|stream
operator|.
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|t
operator|==
name|terms
operator|.
name|length
condition|)
block|{
break|break;
block|}
block|}
else|else
block|{
comment|// Don't reset the token stream since we will
comment|// miss matches. /ljo
comment|//stream.reset();
break|break;
block|}
block|}
if|if
condition|(
name|stateList
operator|.
name|size
argument_list|()
operator|==
name|terms
operator|.
name|length
condition|)
block|{
comment|// we indeed have a phrase match. record the offsets of its terms.
name|int
name|lastIdx
init|=
operator|-
literal|1
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stream
operator|.
name|restoreState
argument_list|(
name|stateList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|OffsetAttribute
name|offsetAttr
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|offsets
operator|.
name|getIndex
argument_list|(
name|offsetAttr
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
if|if
condition|(
name|lastIdx
operator|==
name|idx
condition|)
name|offset
operator|.
name|setEndOffset
argument_list|(
name|offsetAttr
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
name|offset
operator|.
name|add
argument_list|(
name|offsetAttr
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
name|offsetAttr
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
name|offsetAttr
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
name|offsetAttr
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
name|lastIdx
operator|=
name|idx
expr_stmt|;
block|}
block|}
block|}
comment|// End of phrase handling
block|}
else|else
block|{
name|OffsetAttribute
name|offsetAttr
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|offsets
operator|.
name|getIndex
argument_list|(
name|offsetAttr
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
name|offsetAttr
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
name|offsetAttr
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
block|{
name|nodesWithMatch
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
operator|new
name|Offset
argument_list|(
name|offsetAttr
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
name|offsetAttr
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
specifier|private
name|NodePath
name|getPath
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
name|StoredNode
name|node
init|=
operator|(
name|StoredNode
operator|)
name|proxy
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|walkAncestor
argument_list|(
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
specifier|private
name|void
name|walkAncestor
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
return|return;
name|StoredNode
name|parent
init|=
name|node
operator|.
name|getParentStoredNode
argument_list|()
decl_stmt|;
name|walkAncestor
argument_list|(
name|parent
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|path
operator|.
name|addComponent
argument_list|(
name|node
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get all query terms from the original queries.      */
specifier|private
name|void
name|getTerms
parameter_list|()
block|{
name|Set
argument_list|<
name|Query
argument_list|>
name|queries
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|termMap
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
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
name|LuceneUtil
operator|.
name|extractTerms
argument_list|(
name|query
argument_list|,
name|termMap
argument_list|,
name|reader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|UnsupportedOperationException
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
if|if
condition|(
name|startOffset
operator|==
name|offset
condition|)
comment|// duplicate match starts at same offset. ignore.
return|return;
name|getLast
argument_list|()
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
specifier|private
name|Offset
name|getLast
parameter_list|()
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
return|return
name|next
return|;
block|}
name|void
name|setEndOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|getLast
argument_list|()
operator|.
name|endOffset
operator|=
name|offset
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

