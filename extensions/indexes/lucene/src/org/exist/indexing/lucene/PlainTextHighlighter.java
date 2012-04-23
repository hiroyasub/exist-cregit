begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|PhraseQuery
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
name|util
operator|.
name|AttributeSource
operator|.
name|State
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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

begin_class
specifier|public
class|class
name|PlainTextHighlighter
block|{
specifier|private
name|TreeMap
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|termMap
decl_stmt|;
specifier|public
name|PlainTextHighlighter
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termMap
operator|=
operator|new
name|TreeMap
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
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
specifier|public
name|void
name|highlight
parameter_list|(
name|String
name|content
parameter_list|,
name|List
argument_list|<
name|Offset
argument_list|>
name|offsets
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|offsets
operator|==
literal|null
operator|||
name|offsets
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Offset
name|offset
range|:
name|offsets
control|)
block|{
if|if
condition|(
name|offset
operator|.
name|startOffset
argument_list|()
operator|>
name|lastOffset
condition|)
name|builder
operator|.
name|characters
argument_list|(
name|content
operator|.
name|substring
argument_list|(
name|lastOffset
argument_list|,
name|offset
operator|.
name|startOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"match"
argument_list|,
literal|"exist:match"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|content
operator|.
name|substring
argument_list|(
name|offset
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offset
operator|.
name|endOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|lastOffset
operator|=
name|offset
operator|.
name|endOffset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|lastOffset
operator|<
name|content
operator|.
name|length
argument_list|()
condition|)
name|builder
operator|.
name|characters
argument_list|(
name|content
operator|.
name|substring
argument_list|(
name|lastOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|Offset
argument_list|>
name|getOffsets
parameter_list|(
name|String
name|content
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
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
name|content
argument_list|)
argument_list|)
decl_stmt|;
name|MarkableTokenFilter
name|stream
init|=
operator|new
name|MarkableTokenFilter
argument_list|(
name|tokenStream
argument_list|)
decl_stmt|;
comment|//Token token;
name|List
argument_list|<
name|Offset
argument_list|>
name|offsets
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|lastOffset
init|=
literal|0
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
name|termQuery
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
name|termQuery
operator|!=
literal|null
condition|)
block|{
comment|// phrase queries need to be handled differently to filter
comment|// out wrong matches: only the phrase should be marked, not single
comment|// words which may also occur elsewhere in the document
if|if
condition|(
name|termQuery
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
name|termQuery
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
comment|// scan the following text and collect tokens to see if
comment|// they are part of the phrase
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
argument_list|<
name|State
argument_list|>
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
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
name|offsets
operator|=
operator|new
name|ArrayList
argument_list|<
name|Offset
argument_list|>
argument_list|()
expr_stmt|;
name|stream
operator|.
name|restoreState
argument_list|(
name|stateList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|start
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|stream
operator|.
name|restoreState
argument_list|(
name|stateList
operator|.
name|get
argument_list|(
name|terms
operator|.
name|length
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|end
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|offsets
operator|.
name|add
argument_list|(
operator|new
name|Offset
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|)
expr_stmt|;
comment|//restore state as before
name|stream
operator|.
name|restoreState
argument_list|(
name|stateList
operator|.
name|get
argument_list|(
name|stateList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
name|offsets
operator|=
operator|new
name|ArrayList
argument_list|<
name|Offset
argument_list|>
argument_list|()
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
name|offsets
operator|.
name|add
argument_list|(
operator|new
name|Offset
argument_list|(
name|offsetAttr
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offsetAttr
operator|.
name|endOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
name|offsets
return|;
block|}
specifier|public
specifier|static
class|class
name|Offset
block|{
specifier|protected
name|int
name|startOffset
decl_stmt|,
name|endOffset
decl_stmt|;
name|Offset
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|end
expr_stmt|;
block|}
specifier|public
name|int
name|startOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
specifier|public
name|int
name|endOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
block|}
block|}
end_class

end_unit

