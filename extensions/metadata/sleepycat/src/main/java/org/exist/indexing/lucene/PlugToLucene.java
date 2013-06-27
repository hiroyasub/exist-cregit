begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|standard
operator|.
name|StandardAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|NumericField
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
name|IndexWriter
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
name|queryParser
operator|.
name|QueryParser
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
name|Collector
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
name|IndexSearcher
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
name|ScoreDoc
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
name|Scorer
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
name|BitVector
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
name|EXistException
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
name|DocumentImpl
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
name|StreamListener
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
name|PlainTextHighlighter
operator|.
name|Offset
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
name|memtree
operator|.
name|NodeImpl
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
name|Subject
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
name|md
operator|.
name|Meta
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
name|md
operator|.
name|MetaDataImpl
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
name|md
operator|.
name|Metas
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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|PlugToLucene
block|{
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_META_DOC_URI
init|=
literal|"metaDocUri"
decl_stmt|;
name|MetaDataImpl
name|metadata
decl_stmt|;
name|LuceneIndex
name|index
decl_stmt|;
name|LuceneIndexWorker
name|worker
decl_stmt|;
specifier|public
name|PlugToLucene
parameter_list|(
name|MetaDataImpl
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|DBBroker
name|broker
init|=
name|getBroker
argument_list|()
decl_stmt|;
name|worker
operator|=
operator|(
name|LuceneIndexWorker
operator|)
name|broker
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexId
argument_list|(
name|LuceneIndex
operator|.
name|ID
argument_list|)
expr_stmt|;
try|try
block|{
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|field
init|=
name|worker
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|index
operator|=
operator|(
name|LuceneIndex
operator|)
name|field
operator|.
name|get
argument_list|(
name|worker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't get LuceneIndex"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|DBBroker
name|getBroker
parameter_list|()
block|{
name|BrokerPool
name|db
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't get BrokerPool"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|db
operator|.
name|getActiveBroker
argument_list|()
return|;
block|}
specifier|private
name|DocumentImpl
name|getDocument
parameter_list|(
name|Metas
name|metas
parameter_list|)
block|{
comment|//object
name|String
name|uuid
init|=
name|metas
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
name|getBroker
argument_list|()
decl_stmt|;
name|Subject
name|currentSubject
init|=
name|broker
operator|.
name|getSubject
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|setSubject
argument_list|(
name|broker
operator|.
name|getDatabase
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|metadata
operator|.
name|getDocument
argument_list|(
name|uuid
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Document '"
operator|+
name|uuid
operator|+
literal|"' not found."
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|broker
operator|.
name|setSubject
argument_list|(
name|currentSubject
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addMetas
parameter_list|(
name|Metas
name|metas
parameter_list|)
block|{
comment|//update lucene record
name|DocumentImpl
name|doc
init|=
name|getDocument
argument_list|(
name|metas
argument_list|)
decl_stmt|;
comment|//make sure that index worker do not process different document
name|DocumentImpl
name|indexDoc
init|=
name|worker
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexDoc
operator|!=
literal|null
operator|&&
operator|!
name|checkPendingDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Index processing different document '"
operator|+
name|indexDoc
operator|.
name|getFileURI
argument_list|()
operator|+
literal|"' ['"
operator|+
name|doc
operator|.
name|getFileURI
argument_list|()
operator|+
literal|"]."
argument_list|)
throw|;
block|}
comment|// Note: code order is important here,
comment|//worker.setDocument(doc, StreamListener.STORE);
comment|//worker.setMode(StreamListener.STORE);
name|indexMetas
argument_list|(
name|doc
argument_list|,
name|metas
argument_list|)
expr_stmt|;
comment|//write
comment|//worker.writeNonXML();
block|}
specifier|private
name|void
name|indexMetas
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|Metas
name|metas
parameter_list|)
block|{
comment|// create Lucene document
name|Document
name|pendingDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Set DocId
name|NumericField
name|fDocId
init|=
operator|new
name|NumericField
argument_list|(
name|LuceneIndexWorker
operator|.
name|FIELD_DOC_ID
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|fDocId
operator|.
name|setIntValue
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|pendingDoc
operator|.
name|add
argument_list|(
name|fDocId
argument_list|)
expr_stmt|;
comment|// For binary documents the doc path needs to be stored
name|String
name|uri
init|=
name|metas
operator|.
name|getURI
argument_list|()
decl_stmt|;
name|Field
name|fDocUri
init|=
operator|new
name|Field
argument_list|(
name|FIELD_META_DOC_URI
argument_list|,
name|uri
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|pendingDoc
operator|.
name|add
argument_list|(
name|fDocUri
argument_list|)
expr_stmt|;
comment|// Iterate over all found fields and write the data.
for|for
control|(
name|Meta
name|meta
range|:
name|metas
operator|.
name|metas
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|meta
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|value
operator|instanceof
name|String
operator|)
condition|)
block|{
comment|//ignore non string values
continue|continue;
block|}
comment|// Get field type configuration
comment|//            FieldType fieldType = config == null ? null : config.getFieldType(field.getName());
comment|//
name|Field
operator|.
name|Store
name|store
init|=
literal|null
decl_stmt|;
comment|//            if (fieldType != null)
comment|//                store = fieldType.getStore();
comment|//            if (store == null)
name|store
operator|=
name|Field
operator|.
name|Store
operator|.
name|NO
expr_stmt|;
comment|//field.getStore();
comment|// Get name from SOLR field
name|String
name|contentFieldName
init|=
name|meta
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|//Analyzer fieldAnalyzer = (fieldType == null) ? null : fieldType.getAnalyzer();
comment|// Extract (document) Boost factor
comment|//            if (field.getBoost()> 0) {
comment|//                pendingDoc.setBoost(field.getBoost());
comment|//            }
comment|// Actual field content ; Store flag can be set in solrField
name|Field
name|contentField
init|=
operator|new
name|Field
argument_list|(
name|contentFieldName
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|store
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|YES
argument_list|)
decl_stmt|;
comment|// Set boost value from SOLR config
comment|//contentField.setBoost(field.getBoost());
name|pendingDoc
operator|.
name|add
argument_list|(
name|contentField
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
name|index
operator|.
name|getWriter
argument_list|()
expr_stmt|;
comment|// by default, Lucene only indexes the first 10,000 terms in a field
name|writer
operator|.
name|setMaxFieldLength
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|pendingDoc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//LOG.warn("An exception was caught while indexing document: " + e.getMessage(), e);
block|}
finally|finally
block|{
name|index
operator|.
name|releaseWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeMetas
parameter_list|(
name|Metas
name|metas
parameter_list|)
block|{
name|DocumentImpl
name|doc
init|=
name|getDocument
argument_list|(
name|metas
argument_list|)
decl_stmt|;
comment|//make sure that index worker do not process different document
name|DocumentImpl
name|indexDoc
init|=
name|worker
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexDoc
operator|!=
literal|null
operator|&&
operator|!
name|checkPendingDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Index processing different document '"
operator|+
name|indexDoc
operator|.
name|getFileURI
argument_list|()
operator|+
literal|"' ['"
operator|+
name|doc
operator|.
name|getFileURI
argument_list|()
operator|+
literal|"]."
argument_list|)
throw|;
block|}
comment|// Note: code order is important here,
comment|//worker.setDocument(doc, StreamListener.STORE);
comment|//worker.setMode(StreamListener.STORE);
name|removeMetas
argument_list|(
name|doc
argument_list|,
name|metas
argument_list|)
expr_stmt|;
comment|//write
comment|//worker.writeNonXML();
block|}
specifier|private
name|void
name|removeMetas
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|Metas
name|metas
parameter_list|)
block|{
comment|//update lucene record
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
name|index
operator|.
name|getWriter
argument_list|()
expr_stmt|;
name|String
name|uri
init|=
name|metas
operator|.
name|getURI
argument_list|()
decl_stmt|;
name|Term
name|dt
init|=
operator|new
name|Term
argument_list|(
name|FIELD_META_DOC_URI
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|dt
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//LOG.warn("Error while removing lucene index: " + e.getMessage(), e);
block|}
finally|finally
block|{
name|index
operator|.
name|releaseWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|checkPendingDoc
parameter_list|()
block|{
try|try
block|{
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|field
init|=
name|worker
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"pendingDoc"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|field
operator|.
name|get
argument_list|(
name|worker
argument_list|)
operator|==
literal|null
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|NodeImpl
name|search
parameter_list|(
name|String
name|queryText
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|toBeMatchedURIs
parameter_list|)
throws|throws
name|XPathException
block|{
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Subject
name|currentSubject
init|=
name|broker
operator|.
name|getSubject
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|setSubject
argument_list|(
name|db
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
comment|//                LuceneIndexWorker index = (LuceneIndexWorker) broker
comment|//                        .getIndexController().getWorkerByIndexId(LuceneIndex.ID);
return|return
name|search
argument_list|(
name|toBeMatchedURIs
argument_list|,
name|queryText
argument_list|)
return|;
block|}
finally|finally
block|{
name|broker
operator|.
name|setSubject
argument_list|(
name|currentSubject
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|NodeImpl
name|search
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|toBeMatchedURIs
parameter_list|,
name|String
name|queryText
parameter_list|)
throws|throws
name|XPathException
block|{
name|NodeImpl
name|report
init|=
literal|null
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Get index searcher
name|searcher
operator|=
name|index
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
comment|// Get analyzer : to be retrieved from configuration
name|Analyzer
name|searchAnalyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_29
argument_list|)
decl_stmt|;
comment|// Setup query Version, default field, analyzer
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_29
argument_list|,
literal|""
argument_list|,
name|searchAnalyzer
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|queryText
argument_list|)
decl_stmt|;
comment|// extract all used fields from query
name|String
index|[]
name|fields
init|=
name|LuceneUtil
operator|.
name|extractFields
argument_list|(
name|query
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
comment|// Setup collector for results
name|LuceneHitCollector
name|collector
init|=
operator|new
name|LuceneHitCollector
argument_list|()
decl_stmt|;
comment|// Perform actual search
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
comment|// Retrieve all documents that match the query
name|List
argument_list|<
name|ScoreDoc
argument_list|>
name|results
init|=
name|collector
operator|.
name|getDocsByScore
argument_list|()
decl_stmt|;
comment|// reusable attributes
name|AttributesImpl
name|attribs
init|=
literal|null
decl_stmt|;
name|PlainTextHighlighter
name|highlighter
init|=
operator|new
name|PlainTextHighlighter
argument_list|(
name|query
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
comment|// start root element
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"results"
argument_list|,
literal|"results"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|BitVector
name|processed
init|=
operator|new
name|BitVector
argument_list|(
name|searcher
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|// Process result documents
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|results
control|)
block|{
if|if
condition|(
name|processed
operator|.
name|get
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
condition|)
continue|continue;
name|processed
operator|.
name|set
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
decl_stmt|;
comment|// Get URI field of document
name|String
name|fDocUri
init|=
name|doc
operator|.
name|get
argument_list|(
name|FIELD_META_DOC_URI
argument_list|)
decl_stmt|;
comment|// Get score
name|float
name|score
init|=
name|scoreDoc
operator|.
name|score
decl_stmt|;
comment|// Check if document URI has a full match or if a
comment|// document is in a collection
if|if
condition|(
name|isDocumentMatch
argument_list|(
name|fDocUri
argument_list|,
name|toBeMatchedURIs
argument_list|)
condition|)
block|{
comment|// setup attributes
name|attribs
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"uri"
argument_list|,
literal|"uri"
argument_list|,
literal|"CDATA"
argument_list|,
name|fDocUri
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"score"
argument_list|,
literal|"score"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|score
argument_list|)
expr_stmt|;
comment|// write element and attributes
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"search"
argument_list|,
literal|"search"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|String
index|[]
name|fieldContent
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|attribs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|field
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|content
range|:
name|fieldContent
control|)
block|{
name|List
argument_list|<
name|Offset
argument_list|>
name|offsets
init|=
name|highlighter
operator|.
name|getOffsets
argument_list|(
name|content
argument_list|,
name|searchAnalyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|offsets
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"field"
argument_list|,
literal|"field"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|highlighter
operator|.
name|highlight
argument_list|(
name|content
argument_list|,
name|offsets
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|// clean attributes
name|attribs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|// finish root element
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|//System.out.println(builder.getDocument().toString());
comment|// TODO check
name|report
operator|=
operator|(
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|//LOG.error(ex);
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|index
operator|.
name|releaseSearcher
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
return|return
name|report
return|;
block|}
specifier|private
name|boolean
name|isDocumentMatch
parameter_list|(
name|String
name|docUri
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|toBeMatchedUris
parameter_list|)
block|{
if|if
condition|(
name|docUri
operator|==
literal|null
condition|)
block|{
comment|//LOG.error("docUri is null.");
return|return
literal|false
return|;
block|}
if|if
condition|(
name|toBeMatchedUris
operator|==
literal|null
condition|)
block|{
comment|//LOG.error("match is null.");
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|doc
range|:
name|toBeMatchedUris
control|)
block|{
if|if
condition|(
name|docUri
operator|.
name|startsWith
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
class|class
name|LuceneHitCollector
extends|extends
name|Collector
block|{
specifier|private
name|List
argument_list|<
name|ScoreDoc
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|ScoreDoc
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|docBase
decl_stmt|;
specifier|private
name|Scorer
name|scorer
decl_stmt|;
specifier|private
name|LuceneHitCollector
parameter_list|()
block|{
comment|//Nothing special to do
block|}
specifier|public
name|List
argument_list|<
name|ScoreDoc
argument_list|>
name|getDocs
parameter_list|()
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|docs
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ScoreDoc
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|scoreDoc
parameter_list|,
name|ScoreDoc
name|scoreDoc1
parameter_list|)
block|{
if|if
condition|(
name|scoreDoc
operator|.
name|doc
operator|==
name|scoreDoc1
operator|.
name|doc
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|scoreDoc
operator|.
name|doc
operator|<
name|scoreDoc1
operator|.
name|doc
condition|)
return|return
operator|-
literal|1
return|;
return|return
literal|1
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|docs
return|;
block|}
comment|/**          * Get matching lucene documents by descending score          * @return          */
specifier|public
name|List
argument_list|<
name|ScoreDoc
argument_list|>
name|getDocsByScore
parameter_list|()
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|docs
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ScoreDoc
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreDoc
name|scoreDoc
parameter_list|,
name|ScoreDoc
name|scoreDoc1
parameter_list|)
block|{
if|if
condition|(
name|scoreDoc
operator|.
name|score
operator|==
name|scoreDoc1
operator|.
name|score
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|scoreDoc
operator|.
name|score
operator|<
name|scoreDoc1
operator|.
name|score
condition|)
return|return
literal|1
return|;
return|return
operator|-
literal|1
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|docs
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
operator|new
name|ScoreDoc
argument_list|(
name|doc
operator|+
name|docBase
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
block|}
block|}
end_class

end_unit
