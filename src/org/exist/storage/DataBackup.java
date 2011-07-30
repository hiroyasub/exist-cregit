begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|EXistException
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
name|util
operator|.
name|Configuration
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
name|FileOutputStream
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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_class
specifier|public
class|class
name|DataBackup
implements|implements
name|SystemTask
block|{
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
name|DataBackup
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|SimpleDateFormat
name|creationDateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyMMdd-HHmmss"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
specifier|private
name|String
name|dest
decl_stmt|;
specifier|public
name|DataBackup
parameter_list|()
block|{
block|}
specifier|public
name|DataBackup
parameter_list|(
name|String
name|destination
parameter_list|)
block|{
name|dest
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|boolean
name|afterCheckpoint
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.SystemTask#configure(java.util.Properties)      */
specifier|public
name|void
name|configure
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|Properties
name|properties
parameter_list|)
throws|throws
name|EXistException
block|{
name|dest
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"output-dir"
argument_list|,
literal|"backup"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|dest
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|dest
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|dest
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
operator|&&
operator|!
operator|(
name|f
operator|.
name|canWrite
argument_list|()
operator|&&
name|f
operator|.
name|isDirectory
argument_list|()
operator|)
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot write backup files to "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|". It should be a writable directory."
argument_list|)
throw|;
else|else
name|f
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dest
operator|=
name|f
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting backup data directory: "
operator|+
name|dest
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
if|if
condition|(
operator|!
operator|(
name|broker
operator|instanceof
name|NativeBroker
operator|)
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"DataBackup system task can only be used "
operator|+
literal|"with the native storage backend"
argument_list|)
throw|;
name|NativeBroker
name|nbroker
init|=
operator|(
name|NativeBroker
operator|)
name|broker
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Backing up data files ..."
argument_list|)
expr_stmt|;
name|String
name|creationDate
init|=
name|creationDateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|outFilename
init|=
name|dest
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|creationDate
operator|+
literal|".zip"
decl_stmt|;
comment|// Create the ZIP file
name|LOG
operator|.
name|debug
argument_list|(
literal|"Archiving data files into: "
operator|+
name|outFilename
argument_list|)
expr_stmt|;
try|try
block|{
name|ZipOutputStream
name|out
init|=
operator|new
name|ZipOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|outFilename
argument_list|)
argument_list|)
decl_stmt|;
name|Callback
name|cb
init|=
operator|new
name|Callback
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|broker
operator|.
name|backupToArchive
argument_list|(
name|cb
argument_list|)
expr_stmt|;
comment|// close the zip file
name|out
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
literal|"An IO error occurred while backing up data files: "
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
name|Callback
implements|implements
name|RawDataBackup
block|{
specifier|private
name|ZipOutputStream
name|zout
decl_stmt|;
specifier|private
name|Callback
parameter_list|(
name|ZipOutputStream
name|out
parameter_list|)
block|{
name|zout
operator|=
name|out
expr_stmt|;
block|}
specifier|public
name|OutputStream
name|newEntry
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|zout
operator|.
name|putNextEntry
argument_list|(
operator|new
name|ZipEntry
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|zout
return|;
block|}
specifier|public
name|void
name|closeEntry
parameter_list|()
throws|throws
name|IOException
block|{
name|zout
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

