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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|Function2E
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|FunctionE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|facet
operator|.
name|taxonomy
operator|.
name|SearcherTaxonomyManager
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
name|taxonomy
operator|.
name|TaxonomyWriter
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
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|backup
operator|.
name|RawDataBackup
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
name|indexing
operator|.
name|RawBackupSupport
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
name|exist
operator|.
name|util
operator|.
name|FileUtils
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndex
extends|extends
name|AbstractIndex
implements|implements
name|RawBackupSupport
block|{
specifier|public
specifier|final
specifier|static
name|Version
name|LUCENE_VERSION_IN_USE
init|=
name|Version
operator|.
name|LUCENE_4_10_4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
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
specifier|private
specifier|static
specifier|final
name|String
name|DIR_NAME
init|=
literal|"lucene"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TAXONOMY_DIR_NAME
init|=
literal|"taxonomy"
decl_stmt|;
specifier|protected
name|Directory
name|directory
decl_stmt|;
specifier|protected
name|Directory
name|taxoDirectory
decl_stmt|;
specifier|protected
name|Analyzer
name|defaultAnalyzer
decl_stmt|;
specifier|protected
name|double
name|bufferSize
init|=
name|IndexWriterConfig
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
name|DirectoryTaxonomyWriter
name|cachedTaxonomyWriter
init|=
literal|null
decl_stmt|;
specifier|protected
name|SearcherTaxonomyManager
name|searcherManager
init|=
literal|null
decl_stmt|;
specifier|protected
name|ReaderManager
name|readerManager
init|=
literal|null
decl_stmt|;
specifier|public
name|String
name|getDirName
parameter_list|()
block|{
return|return
name|DIR_NAME
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Path
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
argument_list|(
name|LUCENE_VERSION_IN_USE
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
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
name|Path
name|dir
init|=
name|getDataDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|getDirName
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|taxoDir
init|=
name|dir
operator|.
name|resolve
argument_list|(
name|TAXONOMY_DIR_NAME
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
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|dir
argument_list|)
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene index location is not a directory: "
operator|+
name|dir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|taxoDir
argument_list|)
expr_stmt|;
block|}
name|directory
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|dir
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|taxoDirectory
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|taxoDir
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexWriterConfig
name|idxWriterConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|LUCENE_VERSION_IN_USE
argument_list|,
name|defaultAnalyzer
argument_list|)
decl_stmt|;
name|idxWriterConfig
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|cachedWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|idxWriterConfig
argument_list|)
expr_stmt|;
name|cachedTaxonomyWriter
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDirectory
argument_list|)
expr_stmt|;
name|searcherManager
operator|=
operator|new
name|SearcherTaxonomyManager
argument_list|(
name|cachedWriter
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|cachedTaxonomyWriter
argument_list|)
expr_stmt|;
name|readerManager
operator|=
operator|new
name|ReaderManager
argument_list|(
name|cachedWriter
argument_list|,
literal|true
argument_list|)
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
annotation|@
name|Override
specifier|public
specifier|synchronized
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
name|searcherManager
operator|!=
literal|null
condition|)
block|{
name|searcherManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcherManager
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|readerManager
operator|!=
literal|null
condition|)
block|{
name|readerManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|readerManager
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|cachedWriter
operator|!=
literal|null
condition|)
block|{
name|commit
argument_list|()
expr_stmt|;
name|cachedTaxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|cachedWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|cachedTaxonomyWriter
operator|=
literal|null
expr_stmt|;
name|cachedWriter
operator|=
literal|null
expr_stmt|;
block|}
name|taxoDirectory
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
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
comment|//Nothing special to do
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
block|{
name|close
argument_list|()
expr_stmt|;
name|Path
name|dir
init|=
name|getDataDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|getDirName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|list
argument_list|(
name|dir
argument_list|)
operator|.
name|forEach
argument_list|(
name|path
lambda|->
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// never abort at this point, so recovery can continue
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
annotation|@
name|Override
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
annotation|@
name|Override
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
name|boolean
name|needsCommit
init|=
literal|false
decl_stmt|;
specifier|public
name|IndexWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getWriter
argument_list|(
literal|false
argument_list|)
return|;
block|}
specifier|public
name|IndexWriter
name|getWriter
parameter_list|(
name|boolean
name|exclusive
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|cachedWriter
return|;
block|}
specifier|public
name|TaxonomyWriter
name|getTaxonomyWriter
parameter_list|()
block|{
return|return
name|cachedTaxonomyWriter
return|;
block|}
specifier|public
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
name|needsCommit
operator|=
literal|true
expr_stmt|;
block|}
specifier|protected
name|void
name|commit
parameter_list|()
block|{
if|if
condition|(
operator|!
name|needsCommit
condition|)
block|{
return|return;
block|}
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Committing lucene index"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cachedWriter
operator|!=
literal|null
condition|)
block|{
name|cachedTaxonomyWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|cachedWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|needsCommit
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CorruptIndexException
name|cie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Detected corrupt Lucence index on writer release and commit: "
operator|+
name|cie
operator|.
name|getMessage
argument_list|()
argument_list|,
name|cie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Detected Lucence index issue on writer release and commit: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|withReader
parameter_list|(
name|FunctionE
argument_list|<
name|IndexReader
argument_list|,
name|R
argument_list|,
name|IOException
argument_list|>
name|fn
parameter_list|)
throws|throws
name|IOException
block|{
name|readerManager
operator|.
name|maybeRefreshBlocking
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|reader
init|=
name|readerManager
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|fn
operator|.
name|apply
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
name|readerManager
operator|.
name|release
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|withSearcher
parameter_list|(
name|Function2E
argument_list|<
name|SearcherTaxonomyManager
operator|.
name|SearcherAndTaxonomy
argument_list|,
name|R
argument_list|,
name|IOException
argument_list|,
name|XPathException
argument_list|>
name|consumer
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
block|{
name|searcherManager
operator|.
name|maybeRefreshBlocking
argument_list|()
expr_stmt|;
specifier|final
name|SearcherTaxonomyManager
operator|.
name|SearcherAndTaxonomy
name|searcher
init|=
name|searcherManager
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|consumer
operator|.
name|apply
argument_list|(
name|searcher
argument_list|)
return|;
block|}
finally|finally
block|{
name|searcherManager
operator|.
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|backupToArchive
parameter_list|(
specifier|final
name|RawDataBackup
name|backup
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
specifier|final
name|String
name|name
range|:
name|directory
operator|.
name|listAll
argument_list|()
control|)
block|{
specifier|final
name|String
name|path
init|=
name|getDirName
argument_list|()
operator|+
literal|"/"
operator|+
name|name
decl_stmt|;
comment|// do not use try-with-resources here, closing the OutputStream will close the entire backup
comment|//            try(final OutputStream os = backup.newEntry(path)) {
try|try
block|{
specifier|final
name|OutputStream
name|os
init|=
name|backup
operator|.
name|newEntry
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|getDataDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|path
argument_list|)
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|backup
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

