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
name|queryParser
operator|.
name|ParseException
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
name|Hits
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
name|dom
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
name|ElementImpl
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
name|NewArrayNodeSet
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
name|NodeProxy
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
name|StoredNode
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
name|TextImpl
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
name|AbstractStreamListener
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
name|IndexController
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
name|IndexWorker
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
name|MatchListener
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
name|storage
operator|.
name|txn
operator|.
name|Txn
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
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|DatabaseConfigurationException
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
name|XMLString
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
name|Expression
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
name|Stack
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

begin_class
specifier|public
class|class
name|LuceneIndexWorker
implements|implements
name|IndexWorker
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
name|LuceneIndexWorker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INDEX_ELEMENT
init|=
literal|"text"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QNAME_ATTR
init|=
literal|"qname"
decl_stmt|;
specifier|private
name|LuceneIndex
name|index
decl_stmt|;
specifier|private
name|IndexController
name|controller
decl_stmt|;
specifier|private
name|DocumentImpl
name|currentDoc
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|mode
init|=
literal|0
decl_stmt|;
specifier|private
name|Map
name|config
decl_stmt|;
specifier|private
name|Stack
name|contentStack
init|=
literal|null
decl_stmt|;
specifier|private
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
specifier|public
name|LuceneIndexWorker
parameter_list|(
name|LuceneIndex
name|parent
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|parent
expr_stmt|;
block|}
specifier|public
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|LuceneIndex
operator|.
name|ID
return|;
block|}
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|index
operator|.
name|getIndexName
argument_list|()
return|;
block|}
specifier|public
name|Object
name|configure
parameter_list|(
name|IndexController
name|controller
parameter_list|,
name|NodeList
name|configNodes
parameter_list|,
name|Map
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|this
operator|.
name|controller
operator|=
name|controller
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Configuring lucene index"
argument_list|)
expr_stmt|;
comment|// We use a map to store the QNames to be indexed
name|Map
name|map
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|Node
name|node
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
name|configNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|=
name|configNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|INDEX_ELEMENT
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|qname
init|=
operator|(
operator|(
name|Element
operator|)
name|node
operator|)
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|qname
operator|==
literal|null
operator|||
name|qname
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Configuration error: element "
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
operator|+
literal|" must have an attribute "
operator|+
name|QNAME_ATTR
argument_list|)
throw|;
name|LuceneIndexConfig
name|config
init|=
operator|new
name|LuceneIndexConfig
argument_list|(
name|namespaces
argument_list|,
name|qname
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|config
operator|.
name|getQName
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
try|try
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|StreamListener
operator|.
name|STORE
case|:
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|index
operator|.
name|releaseWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|=
literal|null
expr_stmt|;
break|break;
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
literal|"Caught an exception while flushing lucene index: "
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
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
block|{
name|setDocument
argument_list|(
name|document
argument_list|,
name|StreamListener
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|document
parameter_list|,
name|int
name|newMode
parameter_list|)
block|{
name|currentDoc
operator|=
name|document
expr_stmt|;
comment|//config = null;
name|contentStack
operator|=
literal|null
expr_stmt|;
name|IndexSpec
name|indexConf
init|=
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getIndexConfiguration
argument_list|(
name|document
operator|.
name|getBroker
argument_list|()
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
name|Map
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
name|mode
operator|=
name|newMode
expr_stmt|;
block|}
specifier|public
name|void
name|setMode
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
try|try
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|StreamListener
operator|.
name|STORE
case|:
name|writer
operator|=
name|index
operator|.
name|getWriter
argument_list|()
expr_stmt|;
break|break;
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
literal|"Caught exception while preparing lucene index: "
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
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|currentDoc
return|;
block|}
specifier|public
name|int
name|getMode
parameter_list|()
block|{
return|return
name|this
operator|.
name|mode
return|;
block|}
specifier|public
name|StoredNode
name|getReindexRoot
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
return|return
literal|null
return|;
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
specifier|private
name|StreamListener
name|listener
init|=
operator|new
name|LuceneStreamListener
argument_list|()
decl_stmt|;
specifier|public
name|StreamListener
name|getListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
specifier|public
name|MatchListener
name|getMatchListener
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
block|{
return|return
literal|null
return|;
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
specifier|public
name|void
name|removeCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|DBBroker
name|broker
parameter_list|)
block|{
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
specifier|public
name|NodeSet
name|query
parameter_list|(
name|int
name|contextId
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|String
name|queryStr
parameter_list|,
name|int
name|axis
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|NodeSet
name|resultSet
init|=
operator|new
name|NewArrayNodeSet
argument_list|()
decl_stmt|;
name|boolean
name|returnAncestor
init|=
name|axis
operator|==
name|NodeSet
operator|.
name|ANCESTOR
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
try|try
block|{
name|searcher
operator|=
name|index
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
literal|"contents"
argument_list|,
name|index
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|queryStr
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found "
operator|+
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" hits."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Field
name|fDocId
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"docId"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|temp
init|=
name|fDocId
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|int
name|docId
init|=
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DocumentImpl
name|storedDocument
init|=
name|docs
operator|.
name|getDoc
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|storedDocument
operator|==
literal|null
condition|)
continue|continue;
name|Field
name|fNodeId
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"nodeId"
argument_list|)
decl_stmt|;
name|temp
operator|=
name|fNodeId
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
name|int
name|units
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|index
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromData
argument_list|(
name|units
argument_list|,
name|temp
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|NodeProxy
name|storedNode
init|=
operator|new
name|NodeProxy
argument_list|(
name|storedDocument
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
comment|// if a context set is specified, we can directly check if the
comment|// matching node is a descendant of one of the nodes
comment|// in the context set.
if|if
condition|(
name|contextSet
operator|!=
literal|null
condition|)
block|{
name|int
name|sizeHint
init|=
name|contextSet
operator|.
name|getSizeHint
argument_list|(
name|storedDocument
argument_list|)
decl_stmt|;
if|if
condition|(
name|returnAncestor
condition|)
block|{
name|NodeProxy
name|parentNode
init|=
name|contextSet
operator|.
name|parentWithChild
argument_list|(
name|storedNode
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|NodeProxy
operator|.
name|UNKNOWN_NODE_LEVEL
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentNode
operator|!=
literal|null
condition|)
block|{
name|resultSet
operator|.
name|add
argument_list|(
name|parentNode
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
if|if
condition|(
name|Expression
operator|.
name|NO_CONTEXT_ID
operator|!=
name|contextId
condition|)
block|{
name|parentNode
operator|.
name|deepCopyContext
argument_list|(
name|storedNode
argument_list|,
name|contextId
argument_list|)
expr_stmt|;
block|}
else|else
name|parentNode
operator|.
name|copyContext
argument_list|(
name|storedNode
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|resultSet
operator|.
name|add
argument_list|(
name|storedNode
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|resultSet
return|;
block|}
specifier|public
name|boolean
name|checkIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
literal|false
return|;
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
specifier|public
name|Occurrences
index|[]
name|scanIndex
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
name|Map
name|hints
parameter_list|)
block|{
return|return
operator|new
name|Occurrences
index|[
literal|0
index|]
return|;
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
specifier|protected
name|void
name|indexText
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|XMLString
name|content
parameter_list|)
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
return|return;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|byte
index|[]
name|docId
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|currentDoc
operator|.
name|getDocId
argument_list|()
argument_list|,
name|docId
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|nodeIdLen
init|=
name|nodeId
operator|.
name|size
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|nodeIdLen
operator|+
literal|2
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
operator|(
name|short
operator|)
name|nodeId
operator|.
name|units
argument_list|()
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
literal|2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"docId"
argument_list|,
name|docId
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"nodeId"
argument_list|,
name|data
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|content
operator|.
name|toString
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
literal|"An exception was caught while indexing document: "
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
class|class
name|LuceneStreamListener
extends|extends
name|AbstractStreamListener
block|{
specifier|public
name|void
name|startElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|element
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|get
argument_list|(
name|element
operator|.
name|getQName
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|contentStack
operator|==
literal|null
condition|)
name|contentStack
operator|=
operator|new
name|Stack
argument_list|()
expr_stmt|;
name|XMLString
name|contentBuf
init|=
operator|new
name|XMLString
argument_list|()
decl_stmt|;
name|contentStack
operator|.
name|push
argument_list|(
name|contentBuf
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|startElement
argument_list|(
name|transaction
argument_list|,
name|element
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|element
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|get
argument_list|(
name|element
operator|.
name|getQName
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|XMLString
name|content
init|=
operator|(
name|XMLString
operator|)
name|contentStack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|indexText
argument_list|(
name|element
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|endElement
argument_list|(
name|transaction
argument_list|,
name|element
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|characters
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|TextImpl
name|text
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|contentStack
operator|!=
literal|null
operator|&&
operator|!
name|contentStack
operator|.
name|isEmpty
argument_list|()
condition|)
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
name|contentStack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|XMLString
name|next
init|=
operator|(
name|XMLString
operator|)
name|contentStack
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|next
operator|.
name|append
argument_list|(
name|text
operator|.
name|getXMLString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|characters
argument_list|(
name|transaction
argument_list|,
name|text
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexWorker
name|getWorker
parameter_list|()
block|{
return|return
name|LuceneIndexWorker
operator|.
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

