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
name|facet
operator|.
name|DrillDownQuery
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
name|AtomicReaderContext
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
name|Fields
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
name|IndexReaderContext
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
name|Terms
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
name|TermsEnum
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
name|AttributeSource
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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
specifier|final
name|int
name|docId
parameter_list|,
specifier|final
name|NodeId
name|nodeId
parameter_list|)
block|{
comment|// build id from nodeId and docId
specifier|final
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
specifier|final
name|NodeId
name|nodeId
parameter_list|)
block|{
specifier|final
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
specifier|final
name|int
name|doc
parameter_list|,
specifier|final
name|BinaryDocValues
name|nodeIdValues
parameter_list|,
specifier|final
name|BrokerPool
name|pool
parameter_list|)
block|{
specifier|final
name|BytesRef
name|ref
init|=
name|nodeIdValues
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
specifier|final
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
comment|/**      * Encode an element or attribute qname into a lucene field name using the      * internal ids for namespace and local name.      *      * @param qname the name      * @param symbols the symbol table      *      * @return the encoded qname      */
specifier|public
specifier|static
name|String
name|encodeQName
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|SymbolTable
name|symbols
parameter_list|)
block|{
specifier|final
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
specifier|final
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
specifier|final
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
comment|/**      * Decode the lucene field name into an element or attribute qname.      *      * @param s the encoded qname      * @param symbols the symbol table      *      * @return the qname      */
specifier|public
specifier|static
name|QName
name|decodeQName
parameter_list|(
specifier|final
name|String
name|s
parameter_list|,
specifier|final
name|SymbolTable
name|symbols
parameter_list|)
block|{
try|try
block|{
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
return|return
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|,
name|type
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
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
specifier|final
name|Query
name|query
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
comment|/**      * Extract all terms which would be matched by a given query.      * The terms are put into a map with the term as key and the      * corresponding query object as value.      *      * This method is used by {@link LuceneMatchListener}      * to highlight matches in the search results.      *      * @param query the query      * @param terms the terms      * @param reader the index reader      * @param includeFields true to include fields, false to exclude      *      * @throws IOException if an I/O error occurs      * @throws UnsupportedOperationException if the query type is not supported      */
specifier|public
specifier|static
name|void
name|extractTerms
parameter_list|(
specifier|final
name|Query
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
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
block|{
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
block|}
if|else if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
block|{
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
block|}
if|else if
condition|(
name|query
operator|instanceof
name|WildcardQuery
condition|)
block|{
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
block|}
if|else if
condition|(
name|query
operator|instanceof
name|RegexpQuery
condition|)
block|{
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
block|}
if|else if
condition|(
name|query
operator|instanceof
name|FuzzyQuery
condition|)
block|{
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
block|}
if|else if
condition|(
name|query
operator|instanceof
name|PrefixQuery
condition|)
block|{
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
block|}
if|else if
condition|(
name|query
operator|instanceof
name|PhraseQuery
condition|)
block|{
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
block|}
if|else if
condition|(
name|query
operator|instanceof
name|TermRangeQuery
condition|)
block|{
name|extractTermsFromTermRange
argument_list|(
operator|(
name|TermRangeQuery
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
block|}
if|else if
condition|(
name|query
operator|instanceof
name|DrillDownQuery
condition|)
block|{
name|extractTermsFromDrillDown
argument_list|(
operator|(
name|DrillDownQuery
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
block|}
else|else
block|{
comment|// fallback to Lucene's Query.extractTerms if none of the
comment|// above matches
specifier|final
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
specifier|final
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
block|{
name|terms
operator|.
name|put
argument_list|(
name|t
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
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromDrillDown
parameter_list|(
name|DrillDownQuery
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
specifier|final
name|Query
name|rewritten
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|extractTerms
argument_list|(
name|rewritten
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
name|extractTermsFromBoolean
parameter_list|(
specifier|final
name|BooleanQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
specifier|final
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
specifier|final
name|TermQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
specifier|final
name|boolean
name|includeFields
parameter_list|)
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
name|query
operator|.
name|getTerm
argument_list|()
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
block|}
specifier|private
specifier|static
name|void
name|extractTermsFromWildcard
parameter_list|(
specifier|final
name|WildcardQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
block|{
name|extractTermsFromMultiTerm
argument_list|(
name|query
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
specifier|final
name|RegexpQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
block|{
name|extractTermsFromMultiTerm
argument_list|(
name|query
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
specifier|final
name|FuzzyQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
block|{
name|extractTermsFromMultiTerm
argument_list|(
name|query
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
specifier|final
name|PrefixQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
block|{
name|extractTermsFromMultiTerm
argument_list|(
name|query
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
specifier|final
name|PhraseQuery
name|query
parameter_list|,
specifier|final
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
specifier|final
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
specifier|final
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
name|void
name|extractTermsFromTermRange
parameter_list|(
specifier|final
name|TermRangeQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|terms
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
block|{
name|TERM_EXTRACTOR
operator|.
name|extractTerms
argument_list|(
name|query
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
name|Query
name|rewrite
parameter_list|(
specifier|final
name|MultiTermQuery
name|query
parameter_list|,
specifier|final
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
specifier|private
specifier|static
name|void
name|extractTermsFromMultiTerm
parameter_list|(
specifier|final
name|MultiTermQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|termsMap
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
block|{
name|TERM_EXTRACTOR
operator|.
name|extractTerms
argument_list|(
name|query
argument_list|,
name|termsMap
argument_list|,
name|reader
argument_list|,
name|includeFields
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|MultiTermExtractor
name|TERM_EXTRACTOR
init|=
operator|new
name|MultiTermExtractor
argument_list|()
decl_stmt|;
comment|/**      * A class for extracting MultiTerms (all of them).      * Subclassing MultiTermQuery.RewriteMethod      * to gain access to its protected method getTermsEnum      */
specifier|private
specifier|static
class|class
name|MultiTermExtractor
extends|extends
name|MultiTermQuery
operator|.
name|RewriteMethod
block|{
specifier|public
name|void
name|extractTerms
parameter_list|(
specifier|final
name|MultiTermQuery
name|query
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Query
argument_list|>
name|termsMap
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|boolean
name|includeFields
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getContext
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|AtomicReaderContext
name|context
range|:
name|topReaderContext
operator|.
name|leaves
argument_list|()
control|)
block|{
specifier|final
name|Fields
name|fields
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|// reader has no fields
continue|continue;
block|}
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// field does not exist
continue|continue;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getTermsEnum
argument_list|(
name|query
argument_list|,
name|terms
argument_list|,
operator|new
name|AttributeSource
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
if|if
condition|(
name|termsEnum
operator|==
name|TermsEnum
operator|.
name|EMPTY
condition|)
block|{
continue|continue;
block|}
name|BytesRef
name|bytes
decl_stmt|;
while|while
condition|(
operator|(
name|bytes
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeFields
condition|)
block|{
name|termsMap
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termsMap
operator|.
name|put
argument_list|(
name|term
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
block|}
annotation|@
name|Override
specifier|public
name|Query
name|rewrite
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

