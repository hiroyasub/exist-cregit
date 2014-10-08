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
name|BinaryDocValues
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
name|util
operator|.
name|BytesRef
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
name|persistent
operator|.
name|SymbolTable
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
name|storage
operator|.
name|BrokerPool
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
name|ByteConversion
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneUtil
block|{
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_NODE_ID
init|=
literal|"nodeId"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_DOC_ID
init|=
literal|"docId"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_DOC_URI
init|=
literal|"docUri"
decl_stmt|;
specifier|public
specifier|static
name|byte
index|[]
name|createId
parameter_list|(
name|int
name|docId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
block|{
comment|// build id from nodeId and docId
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|nodeId
operator|.
name|size
argument_list|()
operator|+
literal|4
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|intToByteH
argument_list|(
name|docId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|nodeId
operator|.
name|serialize
argument_list|(
name|data
argument_list|,
literal|4
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|public
specifier|static
name|byte
index|[]
name|createId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|nodeId
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|nodeId
operator|.
name|serialize
argument_list|(
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|public
specifier|static
name|NodeId
name|readNodeId
parameter_list|(
name|int
name|doc
parameter_list|,
name|BinaryDocValues
name|nodeIdValues
parameter_list|,
name|BrokerPool
name|pool
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|nodeIdValues
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|int
name|units
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|offset
argument_list|)
decl_stmt|;
return|return
name|pool
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromData
argument_list|(
name|units
argument_list|,
name|ref
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|offset
operator|+
literal|2
argument_list|)
return|;
block|}
comment|/**      * Encode an element or attribute qname into a lucene field name using the      * internal ids for namespace and local name.      *      * @param qname      * @return encoded qname      */
specifier|public
specifier|static
name|String
name|encodeQName
parameter_list|(
name|QName
name|qname
parameter_list|,
name|SymbolTable
name|symbols
parameter_list|)
block|{
name|short
name|namespaceId
init|=
name|symbols
operator|.
name|getNSSymbol
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|short
name|localNameId
init|=
name|symbols
operator|.
name|getSymbol
argument_list|(
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|nameId
init|=
name|qname
operator|.
name|getNameType
argument_list|()
operator||
operator|(
name|namespaceId
operator|&
literal|0xFFFF
operator|)
operator|<<
literal|16
operator||
operator|(
name|localNameId
operator|&
literal|0xFFFFFFFFL
operator|)
operator|<<
literal|32
decl_stmt|;
return|return
name|Long
operator|.
name|toHexString
argument_list|(
name|nameId
argument_list|)
return|;
block|}
comment|/**      * Decode the lucene field name into an element or attribute qname.      *      * @param s      * @return the qname      */
specifier|public
specifier|static
name|QName
name|decodeQName
parameter_list|(
name|String
name|s
parameter_list|,
name|SymbolTable
name|symbols
parameter_list|)
block|{
try|try
block|{
name|long
name|l
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|s
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|short
name|namespaceId
init|=
operator|(
name|short
operator|)
operator|(
operator|(
name|l
operator|>>>
literal|16
operator|)
operator|&
literal|0xFFFFL
operator|)
decl_stmt|;
name|short
name|localNameId
init|=
operator|(
name|short
operator|)
operator|(
operator|(
name|l
operator|>>>
literal|32
operator|)
operator|&
literal|0xFFFFL
operator|)
decl_stmt|;
name|byte
name|type
init|=
operator|(
name|byte
operator|)
operator|(
name|l
operator|&
literal|0xFFL
operator|)
decl_stmt|;
name|String
name|namespaceURI
init|=
name|symbols
operator|.
name|getNamespace
argument_list|(
name|namespaceId
argument_list|)
decl_stmt|;
name|String
name|localName
init|=
name|symbols
operator|.
name|getName
argument_list|(
name|localNameId
argument_list|)
decl_stmt|;
name|QName
name|qname
init|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|qname
operator|.
name|setNameType
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|qname
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
specifier|static
name|String
index|[]
name|extractFields
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
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|extractTerms
argument_list|(
name|query
argument_list|,
name|map
argument_list|,
name|reader
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|term
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
operator|(
operator|(
name|Term
operator|)
name|term
operator|)
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|fieldArray
init|=
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|fields
operator|.
name|toArray
argument_list|(
name|fieldArray
argument_list|)
return|;
block|}
comment|/**      * Extract all terms which would be matched by a given query.      * The terms are put into a map with the term as key and the      * corresponding query object as value.      *      * This method is used by {@link LuceneMatchListener}      * to highlight matches in the search results.      *      * @param query      * @param terms      * @throws IOException in case of an error      * @throws UnsupportedOperationException in case of an error      */
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
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
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
argument_list|,
name|includeFields
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
argument_list|,
name|includeFields
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
argument_list|,
name|includeFields
argument_list|)
expr_stmt|;
if|else if
condition|(
name|query
operator|instanceof
name|RegexpQuery
condition|)
name|extractTermsFromRegex
argument_list|(
operator|(
name|RegexpQuery
operator|)
name|query
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|,
name|includeFields
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
argument_list|,
name|includeFields
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
argument_list|,
name|includeFields
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
argument_list|,
name|includeFields
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
argument_list|<>
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
if|if
condition|(
name|includeFields
condition|)
name|terms
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|query
argument_list|)
expr_stmt|;
else|else
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
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|includeFields
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
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|extractTerms
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|,
name|includeFields
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
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|boolean
name|includeFields
parameter_list|)
block|{
if|if
condition|(
name|includeFields
condition|)
name|terms
operator|.
name|put
argument_list|(
name|query
operator|.
name|getTerm
argument_list|()
argument_list|,
name|query
argument_list|)
expr_stmt|;
else|else
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
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|includeFields
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
argument_list|,
name|includeFields
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromRegex
parameter_list|(
name|RegexpQuery
name|query
parameter_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|includeFields
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
argument_list|,
name|includeFields
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
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|includeFields
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
argument_list|,
name|includeFields
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
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|includeFields
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
argument_list|,
name|includeFields
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
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
name|boolean
name|includeFields
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
name|Term
name|t1
range|:
name|t
control|)
block|{
if|if
condition|(
name|includeFields
condition|)
block|{
name|terms
operator|.
name|put
argument_list|(
name|t1
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|terms
operator|.
name|put
argument_list|(
name|t1
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
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
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

