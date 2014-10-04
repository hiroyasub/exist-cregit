begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2001/02,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|persistent
operator|.
name|CharacterDataImpl
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
name|persistent
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
name|dom
operator|.
name|persistent
operator|.
name|DocumentSet
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
name|persistent
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
name|persistent
operator|.
name|StoredNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|StreamTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observable
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
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|fulltext
operator|.
name|ElementContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|analysis
operator|.
name|SimpleTokenizer
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
name|analysis
operator|.
name|Tokenizer
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
name|btree
operator|.
name|DBException
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
name|Configuration
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
name|Occurrences
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
name|PorterStemmer
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
name|TerminatedException
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

begin_comment
comment|/**  * This is the base class for all classes providing access to the fulltext index.  *   * The class has methods to add text and attribute nodes to the fulltext index,  * or to search for nodes matching selected search terms.  *    * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TextSearchEngine
extends|extends
name|Observable
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TextSearchEngine
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|TreeSet
argument_list|<
name|String
argument_list|>
name|stoplist
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
specifier|protected
name|Tokenizer
name|tokenizer
decl_stmt|;
specifier|protected
name|Configuration
name|config
decl_stmt|;
specifier|protected
name|boolean
name|indexNumbers
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|stem
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|termFreq
init|=
literal|true
decl_stmt|;
specifier|protected
name|PorterStemmer
name|stemmer
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|trackMatches
init|=
name|Serializer
operator|.
name|TAG_ELEMENT_MATCHES
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INDEX_NUMBERS_ATTRIBUTE
init|=
literal|"parseNumbers"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|STEM_ATTRIBUTE
init|=
literal|"stemming"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|STORE_TERM_FREQUENCY_ATTRIBUTE
init|=
literal|"track-term-freq"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TOKENIZER_ATTRIBUTE
init|=
literal|"tokenizer"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_STOPWORDS_ELEMENT_NAME
init|=
literal|"stopwords"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|STOPWORD_FILE_ATTRIBUTE
init|=
literal|"file"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_INDEX_NUMBERS
init|=
literal|"indexer.indexNumbers"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_STEM
init|=
literal|"indexer.stem"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_STORE_TERM_FREQUENCY
init|=
literal|"indexer.store-term-freq"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_TOKENIZER
init|=
literal|"indexer.tokenizer"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_STOPWORD_FILE
init|=
literal|"stopwords"
decl_stmt|;
comment|/** 	 * Construct a new instance and configure it. 	 *  	 * @param broker 	 * @param conf 	 */
specifier|public
name|TextSearchEngine
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Configuration
name|conf
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
name|config
operator|=
name|conf
expr_stmt|;
name|String
name|stopword
decl_stmt|,
name|tokenizerClass
decl_stmt|;
name|Boolean
name|num
decl_stmt|,
name|stemming
decl_stmt|,
name|termFrequencies
decl_stmt|;
if|if
condition|(
operator|(
name|num
operator|=
operator|(
name|Boolean
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_INDEX_NUMBERS
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|indexNumbers
operator|=
name|num
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|stemming
operator|=
operator|(
name|Boolean
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_STEM
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|stem
operator|=
name|stemming
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|termFrequencies
operator|=
operator|(
name|Boolean
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_STORE_TERM_FREQUENCY
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|termFreq
operator|=
name|termFrequencies
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
name|String
name|track
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|Serializer
operator|.
name|PROPERTY_TAG_MATCHING_ELEMENTS
argument_list|)
decl_stmt|;
if|if
condition|(
name|track
operator|!=
literal|null
condition|)
block|{
name|trackMatches
operator|=
name|track
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
condition|?
name|Serializer
operator|.
name|TAG_ELEMENT_MATCHES
else|:
name|Serializer
operator|.
name|TAG_NONE
expr_stmt|;
block|}
name|track
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|Serializer
operator|.
name|PROPERTY_TAG_MATCHING_ATTRIBUTES
argument_list|)
expr_stmt|;
if|if
condition|(
name|track
operator|!=
literal|null
operator|&&
name|track
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
name|trackMatches
operator|=
name|trackMatches
operator||
name|Serializer
operator|.
name|TAG_ATTRIBUTE_MATCHES
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|tokenizerClass
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TOKENIZER
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|tokClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|tokenizerClass
argument_list|)
decl_stmt|;
name|tokenizer
operator|=
operator|(
name|Tokenizer
operator|)
name|tokClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"using tokenizer: "
operator|+
name|tokenizerClass
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InstantiationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tokenizer
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"using simple tokenizer"
argument_list|)
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|SimpleTokenizer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|stem
condition|)
block|{
name|stemmer
operator|=
operator|new
name|PorterStemmer
argument_list|()
expr_stmt|;
block|}
name|tokenizer
operator|.
name|setStemming
argument_list|(
name|stem
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|stopword
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_STOPWORD_FILE
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|FileReader
name|in
init|=
operator|new
name|FileReader
argument_list|(
name|stopword
argument_list|)
decl_stmt|;
specifier|final
name|StreamTokenizer
name|tok
init|=
operator|new
name|StreamTokenizer
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|int
name|next
init|=
name|tok
operator|.
name|nextToken
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
name|StreamTokenizer
operator|.
name|TT_EOF
condition|)
block|{
if|if
condition|(
name|next
operator|!=
name|StreamTokenizer
operator|.
name|TT_WORD
condition|)
block|{
continue|continue;
block|}
name|stoplist
operator|.
name|add
argument_list|(
name|tok
operator|.
name|sval
argument_list|)
expr_stmt|;
name|next
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Returns the Tokenizer used for tokenizing strings into 	 * words. 	 *  	 * @return tokenizer 	 */
specifier|public
name|Tokenizer
name|getTokenizer
parameter_list|()
block|{
return|return
name|tokenizer
return|;
block|}
comment|/** 	 * Tokenize and index the given text node. 	 *  	 * @param indexSpec 	 * @param node 	 */
specifier|public
specifier|abstract
name|void
name|storeText
parameter_list|(
name|CharacterDataImpl
name|node
parameter_list|,
name|int
name|indexingHint
parameter_list|,
name|FulltextIndexSpec
name|indexSpec
parameter_list|,
name|boolean
name|remove
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|storeText
parameter_list|(
name|StoredNode
name|parent
parameter_list|,
name|ElementContent
name|text
parameter_list|,
name|int
name|indexingHint
parameter_list|,
name|FulltextIndexSpec
name|indexSpec
parameter_list|,
name|boolean
name|remove
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|flush
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|close
parameter_list|()
throws|throws
name|DBException
function_decl|;
specifier|public
name|int
name|getTrackMatches
parameter_list|()
block|{
return|return
name|trackMatches
return|;
block|}
specifier|public
name|void
name|setTrackMatches
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|trackMatches
operator|=
name|flags
expr_stmt|;
block|}
specifier|public
name|NodeSet
name|getNodesContaining
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|axis
parameter_list|,
name|QName
name|qname
parameter_list|,
name|String
name|expr
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|TerminatedException
block|{
return|return
name|getNodesContaining
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|,
name|axis
argument_list|,
name|qname
argument_list|,
name|expr
argument_list|,
name|type
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** 	 * For each of the given search terms and each of the documents in the 	 * document set, return a node-set of matching nodes. 	 * 	 * The type-argument indicates if search terms should be compared using 	 * a regular expression. Valid values are DBBroker.MATCH_EXACT or 	 * DBBroker.MATCH_REGEXP. 	 */
specifier|public
specifier|abstract
name|NodeSet
name|getNodesContaining
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|axis
parameter_list|,
name|QName
name|qname
parameter_list|,
name|String
name|expr
parameter_list|,
name|int
name|type
parameter_list|,
name|boolean
name|matchAll
parameter_list|)
throws|throws
name|TerminatedException
function_decl|;
specifier|public
specifier|abstract
name|NodeSet
name|getNodes
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|axis
parameter_list|,
name|QName
name|qname
parameter_list|,
name|TermMatcher
name|matcher
parameter_list|,
name|CharSequence
name|startTerm
parameter_list|)
throws|throws
name|TerminatedException
function_decl|;
comment|/** 	 * Queries the fulltext index to retrieve information on indexed words contained 	 * in the index for the current collection. Returns a list of {@link Occurrences} for all  	 * words contained in the index. If param end is null, all words starting with  	 * the string sequence param start are returned. Otherwise, the method  	 * returns all words that come after start and before end in lexical order. 	 */
specifier|public
specifier|abstract
name|Occurrences
index|[]
name|scanIndexTerms
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
specifier|abstract
name|Occurrences
index|[]
name|scanIndexTerms
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|QName
index|[]
name|qnames
parameter_list|,
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
specifier|abstract
name|String
index|[]
name|getIndexTerms
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|TermMatcher
name|matcher
parameter_list|)
function_decl|;
comment|/** 	 * Remove index entries for an entire collection. 	 *  	 * @param collection 	 */
specifier|public
specifier|abstract
name|void
name|dropIndex
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
comment|/** 	 * Remove all index entries for the given document. 	 *  	 * @param doc 	 */
specifier|public
specifier|abstract
name|void
name|dropIndex
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
block|}
end_class

end_unit

