begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|ngram
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
name|storage
operator|.
name|index
operator|.
name|BFile
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
name|backup
operator|.
name|RawDataBackup
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|NGramIndex
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
name|NGramIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|NGramIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BFile
name|db
decl_stmt|;
specifier|private
name|int
name|gramSize
init|=
literal|3
decl_stmt|;
specifier|private
name|File
name|dataFile
init|=
literal|null
decl_stmt|;
specifier|public
name|NGramIndex
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
name|String
name|fileName
init|=
literal|"ngram.dbx"
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
name|fileName
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"n"
argument_list|)
condition|)
try|try
block|{
name|gramSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|config
operator|.
name|getAttribute
argument_list|(
literal|"n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Configuration parameter 'n' should be an integer."
argument_list|)
throw|;
block|}
name|dataFile
operator|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
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
try|try
block|{
name|db
operator|=
operator|new
name|BFile
argument_list|(
name|pool
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|,
literal|false
argument_list|,
name|dataFile
argument_list|,
name|pool
operator|.
name|getCacheManager
argument_list|()
argument_list|,
literal|1.4
argument_list|,
literal|0.01
argument_list|,
literal|0.07
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Failed to create index file: "
operator|+
name|dataFile
operator|.
name|getAbsolutePath
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
literal|"Created NGram index: "
operator|+
name|dataFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DBException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SYNC NGRAM"
argument_list|)
expr_stmt|;
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SYNC NGRAM"
argument_list|)
expr_stmt|;
name|db
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
block|{
name|db
operator|.
name|closeAndRemove
argument_list|()
expr_stmt|;
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
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
comment|//TODO : ensure singleton ? a pool ?
return|return
operator|new
name|NGramIndexWorker
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|int
name|getN
parameter_list|()
block|{
return|return
name|gramSize
return|;
block|}
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
name|OutputStream
name|os
init|=
name|backup
operator|.
name|newEntry
argument_list|(
name|db
operator|.
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|db
operator|.
name|backupToStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|backup
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

