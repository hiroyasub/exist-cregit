begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Calendar
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
name|Deflater
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
name|LogManager
operator|.
name|getLogger
argument_list|(
name|DataBackup
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FORMAT_PICTURE
init|=
literal|"yyyyMMddHHmmssS"
decl_stmt|;
specifier|private
specifier|final
name|SimpleDateFormat
name|creationDateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FORMAT_PICTURE
argument_list|)
decl_stmt|;
specifier|private
name|Path
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
specifier|final
name|Path
name|destination
parameter_list|)
block|{
name|dest
operator|=
name|destination
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|afterCheckpoint
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Data Backup Task"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|,
specifier|final
name|Properties
name|properties
parameter_list|)
throws|throws
name|EXistException
block|{
name|dest
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"output-dir"
argument_list|,
literal|"backup"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dest
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|dest
operator|=
operator|(
operator|(
name|Path
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
operator|)
operator|.
name|resolve
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|dest
argument_list|)
operator|&&
operator|!
operator|(
name|Files
operator|.
name|isWritable
argument_list|(
name|dest
argument_list|)
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|dest
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot write backup files to "
operator|+
name|dest
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|". It should be a writable directory."
argument_list|)
throw|;
block|}
else|else
block|{
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Unable to create directory: "
operator|+
name|dest
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
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
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
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
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"DataBackup system task can only be used with the native storage backend"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Backing up data files ..."
argument_list|)
expr_stmt|;
specifier|final
name|String
name|creationDate
init|=
name|creationDateFormat
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|outFilename
init|=
name|dest
operator|.
name|resolve
argument_list|(
name|creationDate
operator|+
literal|".zip"
argument_list|)
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
init|(
specifier|final
name|ZipOutputStream
name|out
init|=
operator|new
name|ZipOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|outFilename
argument_list|)
argument_list|)
init|)
block|{
name|out
operator|.
name|setLevel
argument_list|(
name|Deflater
operator|.
name|NO_COMPRESSION
argument_list|)
expr_stmt|;
specifier|final
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
specifier|static
class|class
name|Callback
implements|implements
name|RawDataBackup
block|{
specifier|final
specifier|private
name|ZipOutputStream
name|zout
decl_stmt|;
specifier|private
name|Callback
parameter_list|(
specifier|final
name|ZipOutputStream
name|out
parameter_list|)
block|{
name|zout
operator|=
name|out
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|OutputStream
name|newEntry
parameter_list|(
specifier|final
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
annotation|@
name|Override
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

