begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|ConsistencyCheck
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
name|ErrorReport
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
name|SystemExport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|AgentFactory
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
name|BufferedOutputStream
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
name|FileNotFoundException
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|ConsistencyCheckTask
implements|implements
name|SystemTask
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
name|ConsistencyCheckTask
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|exportDir
decl_stmt|;
specifier|private
name|boolean
name|createBackup
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|paused
init|=
literal|false
decl_stmt|;
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
name|exportDir
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"output"
argument_list|,
literal|"export"
argument_list|)
expr_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|exportDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isAbsolute
argument_list|()
condition|)
name|dir
operator|=
operator|new
name|File
argument_list|(
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
argument_list|,
name|exportDir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|exportDir
operator|=
name|dir
operator|.
name|getAbsolutePath
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
literal|"Using output directory "
operator|+
name|exportDir
argument_list|)
expr_stmt|;
name|String
name|backup
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"backup"
argument_list|,
literal|"no"
argument_list|)
decl_stmt|;
name|createBackup
operator|=
name|backup
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"YES"
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
name|paused
condition|)
block|{
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
literal|"Consistency check is paused."
argument_list|)
expr_stmt|;
return|return;
block|}
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|PrintWriter
name|report
init|=
name|openLog
argument_list|()
decl_stmt|;
name|CheckCallback
name|cb
init|=
operator|new
name|CheckCallback
argument_list|(
name|report
argument_list|)
decl_stmt|;
try|try
block|{
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
literal|"Starting consistency check..."
argument_list|)
expr_stmt|;
name|boolean
name|doBackup
init|=
name|createBackup
decl_stmt|;
name|ConsistencyCheck
name|check
init|=
operator|new
name|ConsistencyCheck
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|List
name|errors
init|=
name|check
operator|.
name|checkAll
argument_list|(
name|cb
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
literal|"Errors found: "
operator|+
name|errors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doBackup
operator|=
literal|true
expr_stmt|;
name|paused
operator|=
literal|true
expr_stmt|;
block|}
name|AgentFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|updateErrors
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|errors
argument_list|,
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|doBackup
condition|)
block|{
name|File
name|exportFile
init|=
name|SystemExport
operator|.
name|getUniqueFile
argument_list|(
literal|"data"
argument_list|,
literal|".zip"
argument_list|,
name|exportDir
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
literal|"Creating emergency backup to file: "
operator|+
name|exportFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|SystemExport
name|sysexport
init|=
operator|new
name|SystemExport
argument_list|(
name|broker
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|sysexport
operator|.
name|export
argument_list|(
name|exportFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|errors
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|report
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|PrintWriter
name|openLog
parameter_list|()
throws|throws
name|EXistException
block|{
try|try
block|{
name|File
name|file
init|=
name|SystemExport
operator|.
name|getUniqueFile
argument_list|(
literal|"report"
argument_list|,
literal|".log"
argument_list|,
name|exportDir
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"ERROR: failed to create report file in "
operator|+
name|exportDir
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"ERROR: failed to create report file in "
operator|+
name|exportDir
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|CheckCallback
implements|implements
name|ConsistencyCheck
operator|.
name|ProgressCallback
implements|,
name|SystemExport
operator|.
name|StatusCallback
block|{
specifier|private
name|PrintWriter
name|log
decl_stmt|;
specifier|private
name|boolean
name|errorFound
init|=
literal|false
decl_stmt|;
specifier|private
name|CheckCallback
parameter_list|(
name|PrintWriter
name|log
parameter_list|)
block|{
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
block|}
specifier|public
name|void
name|startDocument
parameter_list|(
name|String
name|path
parameter_list|)
block|{
block|}
specifier|public
name|void
name|startDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|current
parameter_list|,
name|int
name|count
parameter_list|)
block|{
block|}
specifier|public
name|void
name|startCollection
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|errorFound
condition|)
name|log
operator|.
name|write
argument_list|(
literal|"----------------------------------------------\n"
argument_list|)
expr_stmt|;
name|errorFound
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
literal|"COLLECTION: "
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|error
parameter_list|(
name|ErrorReport
name|error
parameter_list|)
block|{
name|log
operator|.
name|write
argument_list|(
literal|"----------------------------------------------\n"
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
name|error
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|exception
parameter_list|)
block|{
name|log
operator|.
name|write
argument_list|(
literal|"----------------------------------------------\n"
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
literal|"EXPORT ERROR: "
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|exception
operator|.
name|printStackTrace
argument_list|(
name|log
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

