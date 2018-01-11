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
name|indexing
operator|.
name|lucene
operator|.
name|analyzers
package|;
end_package

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
name|icu
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
name|core
operator|.
name|LowerCaseFilter
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
name|core
operator|.
name|StopAnalyzer
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
name|core
operator|.
name|StopFilter
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
name|miscellaneous
operator|.
name|ASCIIFoldingFilter
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
name|standard
operator|.
name|StandardFilter
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
name|standard
operator|.
name|StandardTokenizer
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
name|util
operator|.
name|CharArraySet
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
name|util
operator|.
name|StopwordAnalyzerBase
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
name|util
operator|.
name|WordlistLoader
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
name|Version
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
name|lucene
operator|.
name|LuceneIndex
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
name|Reader
import|;
end_import

begin_comment
comment|/**  * A copy of StandardAnalyzer using an additional ASCIIFoldingFilter to  * strip diacritics.  */
end_comment

begin_class
specifier|public
class|class
name|NoDiacriticsStandardAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/** Default maximum allowed token length */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TOKEN_LENGTH
init|=
literal|255
decl_stmt|;
specifier|private
name|int
name|maxTokenLength
init|=
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/**      * Specifies whether deprecated acronyms should be replaced with HOST type.      * See {@linkplain "https://issues.apache.org/jira/browse/LUCENE-1068"}      */
specifier|private
specifier|final
name|boolean
name|replaceInvalidAcronym
decl_stmt|;
comment|/** An unmodifiable set containing some common English words that are usually not      useful for searching. */
specifier|public
specifier|static
specifier|final
name|CharArraySet
name|STOP_WORDS_SET
init|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
decl_stmt|;
comment|/** Builds an analyzer with the given stop words.      * @param stopWords stop words      */
specifier|public
name|NoDiacriticsStandardAnalyzer
parameter_list|(
specifier|final
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|super
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
name|replaceInvalidAcronym
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Builds an analyzer with the given stop words.      * @param matchVersion Lucene version to match See {@link      *<a href="#version">above</a>}      * @param stopWords stop words      *      * @deprecated Use {@link #NoDiacriticsStandardAnalyzer(CharArraySet)}      */
annotation|@
name|Deprecated
specifier|public
name|NoDiacriticsStandardAnalyzer
parameter_list|(
specifier|final
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
name|replaceInvalidAcronym
operator|=
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|LuceneIndex
operator|.
name|LUCENE_VERSION_IN_USE
argument_list|)
expr_stmt|;
block|}
comment|/**      /** Builds an analyzer with the default stop words ({@link      * #STOP_WORDS_SET}).      */
specifier|protected
name|NoDiacriticsStandardAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
operator|(
name|CharArraySet
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the default stop words ({@link      * #STOP_WORDS_SET}).      * @param matchVersion Lucene version to match See {@link      *<a href="#version">above</a>}      *      * @deprecated Use {@link #NoDiacriticsStandardAnalyzer()}      */
annotation|@
name|Deprecated
specifier|public
name|NoDiacriticsStandardAnalyzer
parameter_list|(
specifier|final
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|STOP_WORDS_SET
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given reader.      * @see WordlistLoader#getWordSet(Reader, Version)      * @param matchVersion Lucene version to match See {@link      *<a href="#version">above</a>}      * @param stopwords Reader to read stop words from */
specifier|public
name|NoDiacriticsStandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|,
name|matchVersion
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set maximum allowed token length.  If a token is seen      * that exceeds this length then it is discarded.  This      * setting only takes effect the next time tokenStream or      * reusableTokenStream is called.      */
specifier|public
name|void
name|setMaxTokenLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|maxTokenLength
operator|=
name|length
expr_stmt|;
block|}
comment|/**      * @see #setMaxTokenLength      */
specifier|public
name|int
name|getMaxTokenLength
parameter_list|()
block|{
return|return
name|maxTokenLength
return|;
block|}
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
specifier|final
name|StandardTokenizer
name|src
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|getVersion
argument_list|()
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|src
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
comment|//        src.setReplaceInvalidAcronym(replaceInvalidAcronym);
name|TokenStream
name|tok
init|=
operator|new
name|StandardFilter
argument_list|(
name|getVersion
argument_list|()
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|tok
operator|=
operator|new
name|ICUFoldingFilter
argument_list|(
name|tok
argument_list|)
expr_stmt|;
name|tok
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|getVersion
argument_list|()
argument_list|,
name|tok
argument_list|)
expr_stmt|;
name|tok
operator|=
operator|new
name|StopFilter
argument_list|(
name|getVersion
argument_list|()
argument_list|,
name|tok
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|src
argument_list|,
name|tok
argument_list|)
return|;
comment|//        {
comment|//            @Override
comment|//            protected boolean reset(final Reader reader) throws IOException {
comment|//                src.setMaxTokenLength(NoDiacriticsStandardAnalyzer.this.maxTokenLength);
comment|//                return super.reset(reader);
comment|//            }
comment|//        };
block|}
block|}
end_class

end_unit

