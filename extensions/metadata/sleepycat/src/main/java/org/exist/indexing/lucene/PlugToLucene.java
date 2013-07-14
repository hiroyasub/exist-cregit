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
comment|//        // Set DocId
comment|//        NumericField fDocId = new NumericField(LuceneIndexWorker.FIELD_DOC_ID, Field.Store.YES, true);
comment|//        fDocId.setIntValue(doc.getDocId());
comment|//        pendingDoc.add(fDocId);
comment|//
comment|//        // For binary documents the doc path needs to be stored
comment|//        String uri = metas.getURI();
comment|//        Field fDocUri = new Field(FIELD_META_DOC_URI, uri, Field.Store.YES, Field.Index.NOT_ANALYZED);
comment|//        pendingDoc.add(fDocUri);
comment|//
comment|//        StringBuilder sb = new StringBuilder();
comment|//
comment|//        // Iterate over all found fields and write the data.
comment|//        for (Meta meta : metas.metas()) {
comment|//            Object value = meta.getValue();
comment|//            if (! (value instanceof String)) {
comment|//                //ignore non string values
comment|//                continue;
comment|//            }
comment|//
comment|//            // Get field type configuration
comment|////            FieldType fieldType = config == null ? null : config.getFieldType(field.getName());
comment|////
comment|//            Field.Store store = null;
comment|////            if (fieldType != null)
comment|////                store = fieldType.getStore();
comment|////            if (store == null)
comment|//                store = Field.Store.YES;//field.getStore();
comment|//
comment|//            // Get name from SOLR field
comment|//            String contentFieldName = meta.getKey();
comment|//
comment|//            //Analyzer fieldAnalyzer = (fieldType == null) ? null : fieldType.getAnalyzer();
comment|//
comment|//            // Extract (document) Boost factor
comment|////            if (field.getBoost()> 0) {
comment|////                pendingDoc.setBoost(field.getBoost());
comment|////            }
comment|//
comment|//            // Actual field content ; Store flag can be set in solrField
comment|//            Field contentField = new Field(contentFieldName, value.toString(), store, Field.Index.ANALYZED, Field.TermVector.YES);
comment|//
comment|//            // Set boost value from SOLR config
comment|//            //contentField.setBoost(field.getBoost());
comment|//
comment|//            pendingDoc.add(contentField);
comment|//
comment|//            sb.append(value.toString()).append(" ");
comment|//        }
comment|//
comment|//        Field contentField = new Field("ALL_METAS", sb.toString(), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES);
comment|//
comment|//        // Set boost value from SOLR config
comment|//        //contentField.setBoost(field.getBoost());
comment|//
comment|//        pendingDoc.add(contentField);
comment|//
comment|//        IndexWriter writer = null;
comment|//        try {
comment|//            writer = index.getWriter();
comment|//
comment|//            // by default, Lucene only indexes the first 10,000 terms in a field
comment|//            writer.setMaxFieldLength(Integer.MAX_VALUE);
comment|//
comment|//            writer.addDocument(pendingDoc);
comment|//        } catch (IOException e) {
comment|//            //LOG.warn("An exception was caught while indexing document: " + e.getMessage(), e);
comment|//
comment|//        } finally {
comment|//            index.releaseWriter(writer);
comment|//        }
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
comment|//        IndexSearcher searcher = null;
comment|//        try {
comment|//            // Get index searcher
comment|//            searcher = index.getSearcher();
comment|//
comment|//            // Get analyzer : to be retrieved from configuration
comment|//            Analyzer searchAnalyzer = new StandardAnalyzer(Version.LUCENE_29);
comment|//
comment|//            // Setup query Version, default field, analyzer
comment|//            QueryParser parser = new QueryParser(Version.LUCENE_29, "", searchAnalyzer);
comment|//            Query query = parser.parse(queryText);
comment|//
comment|//            // extract all used fields from query
comment|//            String[] fields = LuceneUtil.extractFields(query, searcher.getIndexReader());
comment|//
comment|//            // Setup collector for results
comment|//            LuceneHitCollector collector = new LuceneHitCollector();
comment|//
comment|//            // Perform actual search
comment|//            searcher.search(query, collector);
comment|//
comment|//            // Retrieve all documents that match the query
comment|//            List<ScoreDoc> results = collector.getDocsByScore();
comment|//
comment|//            // reusable attributes
comment|//            AttributesImpl attribs = null;
comment|//
comment|//            PlainTextHighlighter highlighter = new PlainTextHighlighter(query, searcher.getIndexReader());
comment|//
comment|//            MemTreeBuilder builder = new MemTreeBuilder();
comment|//            builder.startDocument();
comment|//
comment|//            // start root element
comment|//            int nodeNr = builder.startElement("", "results", "results", null);
comment|//
comment|//            BitVector processed = new BitVector(searcher.maxDoc());
comment|//            // Process result documents
comment|//            for (ScoreDoc scoreDoc : results) {
comment|//                if (processed.get(scoreDoc.doc))
comment|//                    continue;
comment|//                processed.set(scoreDoc.doc);
comment|//
comment|//                Document doc = searcher.doc(scoreDoc.doc);
comment|//
comment|//                // Get URI field of document
comment|//                String fDocUri = doc.get(FIELD_META_DOC_URI);
comment|//
comment|//                // Get score
comment|//                float score = scoreDoc.score;
comment|//
comment|//                // Check if document URI has a full match or if a
comment|//                // document is in a collection
comment|//                if(isDocumentMatch(fDocUri, toBeMatchedURIs)){
comment|//
comment|//                    // setup attributes
comment|//                    attribs = new AttributesImpl();
comment|//                    attribs.addAttribute("", "uri", "uri", "CDATA", fDocUri);
comment|//                    attribs.addAttribute("", "score", "score", "CDATA", ""+score);
comment|//
comment|//                    // write element and attributes
comment|//                    builder.startElement("", "search", "search", attribs);
comment|//                    for (String field : fields) {
comment|//                        String[] fieldContent = doc.getValues(field);
comment|//                        attribs.clear();
comment|//                        attribs.addAttribute("", "name", "name", "CDATA", field);
comment|//                        for (String content : fieldContent) {
comment|//                            List<Offset> offsets = highlighter.getOffsets(content, searchAnalyzer);
comment|//                            if (offsets != null) {
comment|//                                builder.startElement("", "field", "field", attribs);
comment|//                                highlighter.highlight(content, offsets, builder);
comment|//                                builder.endElement();
comment|//                            }
comment|//                        }
comment|//                    }
comment|//                    builder.endElement();
comment|//
comment|//                    // clean attributes
comment|//                    attribs.clear();
comment|//                }
comment|//            }
comment|//
comment|//            // finish root element
comment|//            builder.endElement();
comment|//
comment|//            //System.out.println(builder.getDocument().toString());
comment|//
comment|//            // TODO check
comment|//            report = ((org.exist.memtree.DocumentImpl) builder.getDocument()).getNode(nodeNr);
comment|//
comment|//
comment|//        } catch (Exception ex){
comment|//            ex.printStackTrace();
comment|//            //LOG.error(ex);
comment|//            throw new XPathException(ex);
comment|//
comment|//        } finally {
comment|//            index.releaseSearcher(searcher);
comment|//        }
return|return
name|report
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|searchDocuments
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
name|List
argument_list|<
name|String
argument_list|>
name|uris
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//        IndexSearcher searcher = null;
comment|//        try {
comment|//            // Get index searcher
comment|//            searcher = index.getSearcher();
comment|//
comment|//            // Get analyzer : to be retrieved from configuration
comment|//            Analyzer searchAnalyzer = new StandardAnalyzer(Version.LUCENE_29);
comment|//
comment|//            // Setup query Version, default field, analyzer
comment|//            QueryParser parser = new QueryParser(Version.LUCENE_29, "", searchAnalyzer);
comment|//            Query query = parser.parse(queryText);
comment|//
comment|//            // Setup collector for results
comment|//            LuceneHitCollector collector = new LuceneHitCollector();
comment|//
comment|//            // Perform actual search
comment|//            searcher.search(query, collector);
comment|//
comment|//            // Retrieve all documents that match the query
comment|//            List<ScoreDoc> results = collector.getDocsByScore();
comment|//
comment|//            BitVector processed = new BitVector(searcher.maxDoc());
comment|//            // Process result documents
comment|//            for (ScoreDoc scoreDoc : results) {
comment|//                if (processed.get(scoreDoc.doc))
comment|//                    continue;
comment|//                processed.set(scoreDoc.doc);
comment|//
comment|//                Document doc = searcher.doc(scoreDoc.doc);
comment|//
comment|//                // Get URI field of document
comment|//                String fDocUri = doc.get(FIELD_META_DOC_URI);
comment|//
comment|//                // Get score
comment|//                float score = scoreDoc.score;
comment|//
comment|//                // Check if document URI has a full match or if a
comment|//                // document is in a collection
comment|//                if(isDocumentMatch(fDocUri, toBeMatchedURIs)){
comment|//                    uris.add(fDocUri);
comment|//                }
comment|//            }
comment|//
comment|//        } catch (Exception ex){
comment|//            ex.printStackTrace();
comment|//            //LOG.error(ex);
comment|//            throw new XPathException(ex);
comment|//
comment|//        } finally {
comment|//            index.releaseSearcher(searcher);
comment|//        }
return|return
name|uris
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
comment|//        @Override
comment|//        public void setNextReader(IndexReader indexReader, int docBase) throws IOException {
comment|//            this.docBase = docBase;
comment|//        }
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
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
block|}
end_class

end_unit

