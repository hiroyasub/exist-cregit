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
name|storage
operator|.
name|statistics
package|;
end_package

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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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
name|SAXException
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
name|channels
operator|.
name|SeekableByteChannel
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
import|;
end_import

begin_comment
comment|/**  * Collects statistics on the distribution of elements in the database.  * This is not really an index, though it sits in the indexing pipeline to  * gather its statistics.  *  * The class maintains a graph structure which describes the frequency  * and depth of elements in the database (see @link DataGuide). This forms  * the basis for advanced query optimizations.  */
end_comment

begin_class
specifier|public
class|class
name|IndexStatistics
extends|extends
name|AbstractIndex
implements|implements
name|RawBackupSupport
block|{
specifier|public
specifier|final
specifier|static
name|String
name|ID
init|=
name|IndexStatistics
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|IndexStatistics
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Path
name|dataFile
decl_stmt|;
specifier|private
name|DataGuide
name|dataGuide
init|=
operator|new
name|DataGuide
argument_list|()
decl_stmt|;
specifier|public
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
specifier|public
name|int
name|getMaxParentDepth
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
return|return
name|dataGuide
operator|.
name|getMaxParentDepth
argument_list|(
name|qname
argument_list|)
return|;
block|}
specifier|protected
name|void
name|mergeStats
parameter_list|(
name|DataGuide
name|other
parameter_list|)
block|{
name|dataGuide
operator|=
name|other
operator|.
name|mergeInto
argument_list|(
name|dataGuide
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|updateStats
parameter_list|(
name|DataGuide
name|newGuide
parameter_list|)
block|{
name|dataGuide
operator|=
name|newGuide
expr_stmt|;
block|}
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
name|String
name|fileName
init|=
literal|"stats.dbx"
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|fileName
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
block|}
name|dataFile
operator|=
name|dataDir
operator|.
name|resolve
argument_list|(
name|fileName
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
name|dataGuide
operator|=
operator|new
name|DataGuide
argument_list|()
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|dataFile
argument_list|)
condition|)
block|{
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|SeekableByteChannel
name|chan
init|=
name|Files
operator|.
name|newByteChannel
argument_list|(
name|dataFile
argument_list|)
init|)
block|{
name|dataGuide
operator|.
name|read
argument_list|(
name|chan
argument_list|,
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Reading "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|dataFile
argument_list|)
operator|+
literal|" took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"ms. Size of "
operator|+
literal|"the graph: "
operator|+
name|dataGuide
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Error while loading "
operator|+
name|dataFile
operator|.
name|toAbsolutePath
argument_list|()
operator|+
literal|": "
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
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DBException
block|{
block|}
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
try|try
init|(
specifier|final
name|SeekableByteChannel
name|chan
init|=
name|Files
operator|.
name|newByteChannel
argument_list|(
name|dataFile
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
init|)
block|{
name|dataGuide
operator|.
name|write
argument_list|(
name|chan
argument_list|,
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
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
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DBException
argument_list|(
literal|"Error while writing "
operator|+
name|dataFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|": "
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
name|remove
parameter_list|()
throws|throws
name|DBException
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
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
name|IndexStatisticsWorker
argument_list|(
name|this
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
literal|true
return|;
block|}
specifier|public
name|void
name|toSAX
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
block|{
name|dataGuide
operator|.
name|toSAX
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|dataGuide
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|backupToArchive
parameter_list|(
name|RawDataBackup
name|backup
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|OutputStream
name|os
init|=
name|backup
operator|.
name|newEntry
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|dataFile
argument_list|)
argument_list|)
init|)
block|{
name|Files
operator|.
name|copy
argument_list|(
name|dataFile
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
end_class

end_unit

