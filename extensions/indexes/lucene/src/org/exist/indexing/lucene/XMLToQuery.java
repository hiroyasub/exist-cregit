begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * \$Id\$  */
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
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|Node
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
name|NodeList
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
name|search
operator|.
name|regex
operator|.
name|RegexQuery
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanTermQuery
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanFirstQuery
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
name|Token
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
name|io
operator|.
name|IOException
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
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Parses the XML representation of a Lucene query and transforms  * it into a tree of {@link org.apache.lucene.search.Query} objects.  */
end_comment

begin_class
specifier|public
class|class
name|XMLToQuery
block|{
specifier|private
name|LuceneIndex
name|index
decl_stmt|;
specifier|public
name|XMLToQuery
parameter_list|(
name|LuceneIndex
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
specifier|public
name|Query
name|parse
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|root
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|XPathException
block|{
name|Query
name|query
decl_stmt|;
name|String
name|localName
init|=
name|root
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|parseChildren
argument_list|(
name|field
argument_list|,
name|root
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"term"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|termQuery
argument_list|(
name|field
argument_list|,
name|root
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"wildcard"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|wildcardQuery
argument_list|(
name|field
argument_list|,
name|root
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"prefix"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|prefixQuery
argument_list|(
name|field
argument_list|,
name|root
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"fuzzy"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|fuzzyQuery
argument_list|(
name|field
argument_list|,
name|root
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"bool"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|booleanQuery
argument_list|(
name|field
argument_list|,
name|root
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"phrase"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|phraseQuery
argument_list|(
name|field
argument_list|,
name|root
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"near"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|nearQuery
argument_list|(
name|field
argument_list|,
name|root
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"first"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|getSpanFirst
argument_list|(
name|field
argument_list|,
name|root
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"regex"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
name|query
operator|=
name|regexQuery
argument_list|(
name|field
argument_list|,
name|root
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown element in lucene query expression: "
operator|+
name|localName
argument_list|)
throw|;
name|setBoost
argument_list|(
name|root
argument_list|,
name|query
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
specifier|private
name|Query
name|phraseQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|XPathException
block|{
name|NodeList
name|termList
init|=
name|node
operator|.
name|getElementsByTagName
argument_list|(
literal|"term"
argument_list|)
decl_stmt|;
if|if
condition|(
name|termList
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|String
name|qstr
init|=
name|getText
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|qstr
argument_list|)
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|()
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
argument_list|(
name|token
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|str
init|=
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Error while parsing phrase query: "
operator|+
name|qstr
argument_list|)
throw|;
block|}
name|int
name|slop
init|=
name|getSlop
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|slop
operator|>
operator|-
literal|1
condition|)
name|query
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
else|else
block|{
name|MultiPhraseQuery
name|query
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
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
name|termList
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|termList
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|getText
argument_list|(
name|elem
argument_list|)
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|>
operator|-
literal|1
operator|||
name|text
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
operator|>
literal|0
condition|)
block|{
name|Term
index|[]
name|expanded
init|=
name|expandTerms
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|expanded
operator|.
name|length
operator|>
literal|0
condition|)
name|query
operator|.
name|add
argument_list|(
name|expanded
argument_list|)
expr_stmt|;
block|}
else|else
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|slop
init|=
name|getSlop
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|slop
operator|>
operator|-
literal|1
condition|)
name|query
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
block|}
specifier|private
name|SpanQuery
name|nearQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|slop
init|=
name|getSlop
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|slop
operator|<
literal|0
condition|)
name|slop
operator|=
literal|0
expr_stmt|;
name|boolean
name|inOrder
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasAttribute
argument_list|(
literal|"ordered"
argument_list|)
condition|)
name|inOrder
operator|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"ordered"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hasElementContent
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|String
name|qstr
init|=
name|getText
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|qstr
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SpanTermQuery
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SpanTermQuery
argument_list|>
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|()
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
argument_list|(
name|token
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|str
init|=
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|str
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Error while parsing phrase query: "
operator|+
name|qstr
argument_list|)
throw|;
block|}
return|return
operator|new
name|SpanNearQuery
argument_list|(
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|SpanTermQuery
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
else|else
block|{
name|SpanQuery
index|[]
name|children
init|=
name|parseSpanChildren
argument_list|(
name|field
argument_list|,
name|node
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpanNearQuery
argument_list|(
name|children
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
block|}
specifier|private
name|SpanQuery
index|[]
name|parseSpanChildren
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|XPathException
block|{
name|List
argument_list|<
name|SpanQuery
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SpanQuery
argument_list|>
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|Node
name|child
init|=
name|node
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
if|if
condition|(
literal|"term"
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
name|list
operator|.
name|add
argument_list|(
name|getSpanTerm
argument_list|(
name|field
argument_list|,
operator|(
name|Element
operator|)
name|child
argument_list|)
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"near"
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
name|list
operator|.
name|add
argument_list|(
name|nearQuery
argument_list|(
name|field
argument_list|,
operator|(
name|Element
operator|)
name|child
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"first"
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
name|list
operator|.
name|add
argument_list|(
name|getSpanFirst
argument_list|(
name|field
argument_list|,
operator|(
name|Element
operator|)
name|child
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown query element: "
operator|+
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|)
throw|;
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|private
name|SpanQuery
name|getSpanTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|getText
argument_list|(
name|node
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|SpanQuery
name|getSpanFirst
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|XPathException
block|{
name|SpanQuery
name|query
decl_stmt|;
if|if
condition|(
name|hasElementContent
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|SpanQuery
index|[]
name|children
init|=
name|parseSpanChildren
argument_list|(
name|field
argument_list|,
name|node
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|length
operator|!=
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Query element 'first' expects exactly one child element"
argument_list|)
throw|;
name|query
operator|=
name|children
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|getText
argument_list|(
name|node
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|end
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasAttribute
argument_list|(
literal|"end"
argument_list|)
condition|)
block|{
try|try
block|{
name|end
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|node
operator|.
name|getAttribute
argument_list|(
literal|"end"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Attribute 'end' to query element 'first' should be a "
operator|+
literal|"valid integer. Got: "
operator|+
name|node
operator|.
name|getAttribute
argument_list|(
literal|"end"
argument_list|)
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|SpanFirstQuery
argument_list|(
name|query
argument_list|,
name|end
argument_list|)
return|;
block|}
specifier|private
name|int
name|getSlop
parameter_list|(
name|Element
name|node
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|slop
init|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"slop"
argument_list|)
decl_stmt|;
if|if
condition|(
name|slop
operator|!=
literal|null
operator|&&
name|slop
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|slop
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Query parameter 'slop' should be an integer value. Got: "
operator|+
name|slop
argument_list|)
throw|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|private
name|Term
index|[]
name|expandTerms
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryStr
parameter_list|)
throws|throws
name|XPathException
block|{
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
name|List
argument_list|<
name|Term
argument_list|>
name|termList
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|WildcardTermEnum
name|terms
init|=
operator|new
name|WildcardTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|queryStr
argument_list|)
argument_list|)
decl_stmt|;
name|Term
name|term
decl_stmt|;
do|do
block|{
name|term
operator|=
name|terms
operator|.
name|term
argument_list|()
expr_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|termList
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
do|;
name|terms
operator|.
name|close
argument_list|()
expr_stmt|;
name|Term
index|[]
name|matchingTerms
init|=
operator|new
name|Term
index|[
name|termList
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|termList
operator|.
name|toArray
argument_list|(
name|matchingTerms
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Lucene index error while creating query: "
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
specifier|private
name|Query
name|termQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|getText
argument_list|(
name|node
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Query
name|wildcardQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|)
block|{
return|return
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|getText
argument_list|(
name|node
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Query
name|prefixQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|)
block|{
return|return
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|getText
argument_list|(
name|node
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Query
name|fuzzyQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|)
throws|throws
name|XPathException
block|{
name|float
name|minSimilarity
init|=
name|FuzzyQuery
operator|.
name|defaultMinSimilarity
decl_stmt|;
name|String
name|attr
init|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"min-similarity"
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|!=
literal|null
operator|&&
name|attr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|minSimilarity
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Query parameter 'min-similarity' should be a float value. Got: "
operator|+
name|attr
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|getText
argument_list|(
name|node
argument_list|)
argument_list|)
argument_list|,
name|minSimilarity
argument_list|)
return|;
block|}
specifier|private
name|Query
name|regexQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|RegexQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|getText
argument_list|(
name|node
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Query
name|booleanQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|node
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|XPathException
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|Node
name|child
init|=
name|node
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
name|Query
name|childQuery
init|=
name|parse
argument_list|(
name|field
argument_list|,
name|elem
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
name|occur
init|=
name|getOccur
argument_list|(
name|elem
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|childQuery
argument_list|,
name|occur
argument_list|)
expr_stmt|;
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
specifier|private
name|BooleanClause
operator|.
name|Occur
name|getOccur
parameter_list|(
name|Element
name|elem
parameter_list|)
block|{
name|BooleanClause
operator|.
name|Occur
name|occur
init|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
decl_stmt|;
name|String
name|occurOpt
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"occur"
argument_list|)
decl_stmt|;
if|if
condition|(
name|occurOpt
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|occurOpt
operator|.
name|equals
argument_list|(
literal|"must"
argument_list|)
condition|)
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
expr_stmt|;
if|else if
condition|(
name|occurOpt
operator|.
name|equals
argument_list|(
literal|"not"
argument_list|)
condition|)
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
expr_stmt|;
if|else if
condition|(
name|occurOpt
operator|.
name|equals
argument_list|(
literal|"should"
argument_list|)
condition|)
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
expr_stmt|;
block|}
return|return
name|occur
return|;
block|}
specifier|private
name|Query
name|parseChildren
parameter_list|(
name|String
name|field
parameter_list|,
name|Element
name|root
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|XPathException
block|{
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|Node
name|child
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|Query
name|childQuery
init|=
name|parse
argument_list|(
name|field
argument_list|,
operator|(
name|Element
operator|)
name|child
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
operator|(
operator|(
name|BooleanQuery
operator|)
name|query
operator|)
operator|.
name|add
argument_list|(
name|childQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
else|else
block|{
name|BooleanQuery
name|boolQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|boolQuery
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|boolQuery
operator|.
name|add
argument_list|(
name|childQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|=
name|boolQuery
expr_stmt|;
block|}
block|}
else|else
name|query
operator|=
name|childQuery
expr_stmt|;
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
specifier|private
name|void
name|setBoost
parameter_list|(
name|Element
name|node
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|boost
init|=
name|node
operator|.
name|getAttribute
argument_list|(
literal|"boost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|null
operator|&&
name|boost
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|query
operator|.
name|setBoost
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|boost
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Bad value for boost in query parameter. Got: "
operator|+
name|boost
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|String
name|getText
parameter_list|(
name|Element
name|root
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Node
name|child
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
name|buf
operator|.
name|append
argument_list|(
name|child
operator|.
name|getNodeValue
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|boolean
name|hasElementContent
parameter_list|(
name|Element
name|root
parameter_list|)
block|{
name|Node
name|child
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
return|return
literal|true
return|;
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

