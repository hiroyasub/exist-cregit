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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|AbstractIndex
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
name|util
operator|.
name|DatabaseConfigurationException
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
name|NodeList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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

begin_class
specifier|public
class|class
name|LuceneIndex
extends|extends
name|AbstractIndex
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
specifier|public
specifier|final
specifier|static
name|String
name|ID
init|=
name|LuceneIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|protected
name|Directory
name|directory
decl_stmt|;
specifier|protected
name|Analyzer
name|defaultAnalyzer
decl_stmt|;
specifier|protected
name|double
name|bufferSize
init|=
name|IndexWriter
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
decl_stmt|;
specifier|protected
name|IndexWriter
name|cachedWriter
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|writerUseCount
init|=
literal|0
decl_stmt|;
specifier|protected
name|IndexReader
name|cachedReader
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|readerUseCount
init|=
literal|0
decl_stmt|;
specifier|protected
name|IndexReader
name|cachedWritingReader
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|writingReaderUseCount
init|=
literal|0
decl_stmt|;
specifier|protected
name|IndexSearcher
name|cachedSearcher
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|searcherUseCount
init|=
literal|0
decl_stmt|;
specifier|public
name|LuceneIndex
parameter_list|()
block|{
block|}
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|super
operator|.
name|configure
argument_list|(
name|pool
argument_list|,
name|dataDir
argument_list|,
name|config
argument_list|)
expr_stmt|;
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
literal|"Configuring Lucene index"
argument_list|)
expr_stmt|;
name|String
name|bufferSizeParam
init|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"buffer"
argument_list|)
decl_stmt|;
if|if
condition|(
name|bufferSizeParam
operator|!=
literal|null
condition|)
try|try
block|{
name|bufferSize
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|bufferSizeParam
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid buffer size setting for lucene index: "
operator|+
name|bufferSizeParam
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
literal|"Using buffer size: "
operator|+
name|bufferSize
argument_list|)
expr_stmt|;
name|NodeList
name|nl
init|=
name|config
operator|.
name|getElementsByTagName
argument_list|(
literal|"analyzer"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Element
name|node
init|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|defaultAnalyzer
operator|=
name|AnalyzerConfig
operator|.
name|configureAnalyzer
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|defaultAnalyzer
operator|==
literal|null
condition|)
name|defaultAnalyzer
operator|=
operator|new
name|StandardAnalyzer
argument_list|()
expr_stmt|;
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
literal|"Using default analyzer: "
operator|+
name|defaultAnalyzer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getDataDir
argument_list|()
argument_list|,
literal|"lucene"
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
literal|"Opening Lucene index directory: "
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene index location is not a directory: "
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
else|else
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|directory
operator|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|writer
operator|=
name|getWriter
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Exception while reading lucene index directory: "
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
name|releaseWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DBException
block|{
try|try
block|{
if|if
condition|(
name|cachedWriter
operator|!=
literal|null
condition|)
name|cachedWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DBException
argument_list|(
literal|"Caught exception while closing lucene indexes: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
block|}
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
block|{
try|try
block|{
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|list
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
name|directory
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
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
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
operator|new
name|LuceneIndexWorker
argument_list|(
name|this
argument_list|,
name|broker
argument_list|)
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
specifier|protected
name|Analyzer
name|getDefaultAnalyzer
parameter_list|()
block|{
return|return
name|defaultAnalyzer
return|;
block|}
specifier|protected
specifier|synchronized
name|IndexWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|writingReaderUseCount
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
if|if
condition|(
name|cachedWriter
operator|!=
literal|null
condition|)
block|{
name|writerUseCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|cachedWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|,
name|defaultAnalyzer
argument_list|)
expr_stmt|;
name|cachedWriter
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|writerUseCount
operator|=
literal|1
expr_stmt|;
block|}
name|notifyAll
argument_list|()
expr_stmt|;
return|return
name|cachedWriter
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|releaseWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|writer
operator|!=
name|cachedWriter
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexWriter was not obtained from getWriter()."
argument_list|)
throw|;
name|writerUseCount
operator|--
expr_stmt|;
if|if
condition|(
name|writerUseCount
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|cachedWriter
operator|.
name|close
argument_list|()
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
literal|"Exception while closing lucene index: "
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
finally|finally
block|{
name|cachedWriter
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|notifyAll
argument_list|()
expr_stmt|;
name|waitForReadersAndReopen
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|IndexReader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cachedReader
operator|!=
literal|null
condition|)
block|{
name|readerUseCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|cachedReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|readerUseCount
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|cachedReader
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|releaseReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|reader
operator|!=
name|cachedReader
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexReader was not obtained from getReader()."
argument_list|)
throw|;
name|readerUseCount
operator|--
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
comment|//        try {
comment|//            cachedReader.close();
comment|//        } catch (IOException e) {
comment|//            LOG.warn("Exception while closing lucene index: " + e.getMessage(), e);
comment|//        } finally {
comment|//            cachedReader = null;
comment|//        }
block|}
specifier|protected
specifier|synchronized
name|IndexReader
name|getWritingReader
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|writerUseCount
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
if|if
condition|(
name|cachedWritingReader
operator|!=
literal|null
condition|)
block|{
name|writingReaderUseCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|cachedWritingReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|writingReaderUseCount
operator|=
literal|1
expr_stmt|;
block|}
name|notifyAll
argument_list|()
expr_stmt|;
return|return
name|cachedWritingReader
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|releaseWritingReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|reader
operator|!=
name|cachedWritingReader
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexReader was not obtained from getWritingReader()."
argument_list|)
throw|;
name|writingReaderUseCount
operator|--
expr_stmt|;
if|if
condition|(
name|writingReaderUseCount
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|cachedWritingReader
operator|.
name|close
argument_list|()
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
literal|"Exception while closing lucene index: "
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
finally|finally
block|{
name|cachedWritingReader
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|notifyAll
argument_list|()
expr_stmt|;
name|waitForReadersAndReopen
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|waitForReadersAndReopen
parameter_list|()
block|{
while|while
condition|(
name|readerUseCount
operator|>
literal|0
operator|||
name|searcherUseCount
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
name|reopenReaders
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|reopenReaders
parameter_list|()
block|{
if|if
condition|(
name|cachedReader
operator|==
literal|null
condition|)
return|return;
name|IndexReader
name|oldReader
init|=
name|cachedReader
decl_stmt|;
try|try
block|{
name|cachedReader
operator|=
name|cachedReader
operator|.
name|reopen
argument_list|()
expr_stmt|;
if|if
condition|(
name|oldReader
operator|!=
name|cachedReader
condition|)
block|{
name|oldReader
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Exception while refreshing lucene index: "
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
if|if
condition|(
name|cachedSearcher
operator|!=
literal|null
condition|)
name|cachedSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|cachedReader
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|IndexSearcher
name|getSearcher
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cachedSearcher
operator|!=
literal|null
condition|)
block|{
name|searcherUseCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|cachedSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|getReader
argument_list|()
argument_list|)
expr_stmt|;
name|readerUseCount
operator|--
expr_stmt|;
name|searcherUseCount
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|cachedSearcher
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|releaseSearcher
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|searcher
operator|!=
name|cachedSearcher
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexSearcher was not obtained from getWritingReader()."
argument_list|)
throw|;
name|searcherUseCount
operator|--
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
comment|//        try {
comment|//            cachedSearcher.close();
comment|//        } catch (IOException e) {
comment|//            LOG.warn("Exception while closing lucene index: " + e.getMessage(), e);
comment|//        } finally {
comment|//            cachedSearcher = null;
comment|//        }
block|}
block|}
end_class

end_unit

