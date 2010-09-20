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
name|TreeSet
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
name|BooleanClause
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
name|BooleanQuery
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
name|FuzzyQuery
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
name|MultiTermQuery
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
name|PrefixQuery
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
name|TermQuery
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
name|WildcardQuery
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

begin_class
specifier|public
class|class
name|LuceneUtil
block|{
comment|/**      * Extract all terms which would be matched by a given query.      * The terms are put into a map with the term as key and the      * corresponding query object as value.      *      * This method is used by {@link LuceneMatchListener}      * to highlight matches in the search results.      *      * @param query      * @param terms      * @throws IOException in case of an error      */
specifier|public
specifier|static
name|void
name|extractTerms
parameter_list|(
name|Query
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
name|extractTermsFromBoolean
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|query
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|else if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
name|extractTermsFromTerm
argument_list|(
operator|(
name|TermQuery
operator|)
name|query
argument_list|,
name|terms
argument_list|)
expr_stmt|;
if|else if
condition|(
name|query
operator|instanceof
name|WildcardQuery
condition|)
name|extractTermsFromWildcard
argument_list|(
operator|(
name|WildcardQuery
operator|)
name|query
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|else if
condition|(
name|query
operator|instanceof
name|RegexQuery
condition|)
name|extractTermsFromRegex
argument_list|(
operator|(
name|RegexQuery
operator|)
name|query
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|else if
condition|(
name|query
operator|instanceof
name|FuzzyQuery
condition|)
name|extractTermsFromFuzzy
argument_list|(
operator|(
name|FuzzyQuery
operator|)
name|query
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|else if
condition|(
name|query
operator|instanceof
name|PrefixQuery
condition|)
name|extractTermsFromPrefix
argument_list|(
operator|(
name|PrefixQuery
operator|)
name|query
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|else if
condition|(
name|query
operator|instanceof
name|PhraseQuery
condition|)
name|extractTermsFromPhrase
argument_list|(
operator|(
name|PhraseQuery
operator|)
name|query
argument_list|,
name|terms
argument_list|)
expr_stmt|;
else|else
block|{
comment|// fallback to Lucene's Query.extractTerms if none of the
comment|// above matches
name|Set
argument_list|<
name|Term
argument_list|>
name|tempSet
init|=
operator|new
name|TreeSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|query
operator|.
name|extractTerms
argument_list|(
name|tempSet
argument_list|)
expr_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|tempSet
control|)
block|{
name|terms
operator|.
name|put
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromBoolean
parameter_list|(
name|BooleanQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanClause
name|clauses
index|[]
init|=
name|query
operator|.
name|getClauses
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|extractTerms
argument_list|(
name|clauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromTerm
parameter_list|(
name|TermQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|)
block|{
name|terms
operator|.
name|put
argument_list|(
name|query
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromWildcard
parameter_list|(
name|WildcardQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|extractTerms
argument_list|(
name|rewrite
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromRegex
parameter_list|(
name|RegexQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|extractTerms
argument_list|(
name|rewrite
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromFuzzy
parameter_list|(
name|FuzzyQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|extractTerms
argument_list|(
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromPrefix
parameter_list|(
name|PrefixQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|extractTerms
argument_list|(
name|rewrite
argument_list|(
name|query
argument_list|,
name|reader
argument_list|)
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromPhrase
parameter_list|(
name|PhraseQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|)
block|{
name|Term
index|[]
name|t
init|=
name|query
operator|.
name|getTerms
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
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|terms
operator|.
name|put
argument_list|(
name|t
index|[
name|i
index|]
operator|.
name|text
argument_list|()
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Query
name|rewrite
parameter_list|(
name|MultiTermQuery
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|PrefixQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
return|return
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
end_class

end_unit

