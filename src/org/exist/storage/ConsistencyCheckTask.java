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
name|Agent
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
name|management
operator|.
name|TaskStatus
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|TerminatedException
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
specifier|private
name|boolean
name|incremental
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|incrementalCheck
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|maxInc
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|File
name|lastExportedBackup
init|=
literal|null
decl_stmt|;
specifier|private
name|ProcessMonitor
operator|.
name|Monitor
name|monitor
init|=
operator|new
name|ProcessMonitor
operator|.
name|Monitor
argument_list|()
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OUTPUT_PROP_NAME
init|=
literal|"output"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|BACKUP_PROP_NAME
init|=
literal|"backup"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCREMENTAL_PROP_NAME
init|=
literal|"incremental"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCREMENTAL_CHECK_PROP_NAME
init|=
literal|"incremental-check"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|MAX_PROP_NAME
init|=
literal|"max"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|LoggingCallback
name|logCallback
init|=
operator|new
name|LoggingCallback
argument_list|()
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
name|OUTPUT_PROP_NAME
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
name|BACKUP_PROP_NAME
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
name|String
name|inc
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|INCREMENTAL_PROP_NAME
argument_list|,
literal|"no"
argument_list|)
decl_stmt|;
name|incremental
operator|=
name|inc
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"YES"
argument_list|)
expr_stmt|;
name|String
name|incCheck
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|INCREMENTAL_CHECK_PROP_NAME
argument_list|,
literal|"yes"
argument_list|)
decl_stmt|;
name|incrementalCheck
operator|=
name|incCheck
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"YES"
argument_list|)
expr_stmt|;
name|String
name|max
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|MAX_PROP_NAME
argument_list|,
literal|"5"
argument_list|)
decl_stmt|;
try|try
block|{
name|maxInc
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|max
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
name|EXistException
argument_list|(
literal|"Parameter 'max' has to be an integer"
argument_list|)
throw|;
block|}
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
specifier|final
name|Agent
name|agentInstance
init|=
name|AgentFactory
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|TaskStatus
name|endStatus
init|=
operator|new
name|TaskStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|STOPPED_OK
argument_list|)
decl_stmt|;
name|agentInstance
operator|.
name|changeStatus
argument_list|(
name|brokerPool
argument_list|,
operator|new
name|TaskStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|INIT
argument_list|)
argument_list|)
expr_stmt|;
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
name|agentInstance
operator|.
name|changeStatus
argument_list|(
name|brokerPool
argument_list|,
operator|new
name|TaskStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|PAUSED
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|brokerPool
operator|.
name|getProcessMonitor
argument_list|()
operator|.
name|startJob
argument_list|(
name|ProcessMonitor
operator|.
name|ACTION_BACKUP
argument_list|,
literal|null
argument_list|,
name|monitor
argument_list|)
expr_stmt|;
comment|//        long start = System.currentTimeMillis();
name|PrintWriter
name|report
init|=
literal|null
decl_stmt|;
try|try
block|{
name|boolean
name|doBackup
init|=
name|createBackup
decl_stmt|;
comment|// TODO: don't use the direct access feature for now. needs more testing
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|incremental
operator|||
name|incrementalCheck
condition|)
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
literal|"Starting consistency check..."
argument_list|)
expr_stmt|;
block|}
name|report
operator|=
name|openLog
argument_list|()
expr_stmt|;
name|CheckCallback
name|cb
init|=
operator|new
name|CheckCallback
argument_list|(
name|report
argument_list|)
decl_stmt|;
name|ConsistencyCheck
name|check
init|=
operator|new
name|ConsistencyCheck
argument_list|(
name|broker
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|agentInstance
operator|.
name|changeStatus
argument_list|(
name|brokerPool
argument_list|,
operator|new
name|TaskStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|RUNNING_CHECK
argument_list|)
argument_list|)
expr_stmt|;
name|errors
operator|=
name|check
operator|.
name|checkAll
argument_list|(
name|cb
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|endStatus
operator|.
name|setStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|STOPPED_ERROR
argument_list|)
expr_stmt|;
name|endStatus
operator|.
name|setReason
argument_list|(
name|errors
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
literal|"Errors found: "
operator|+
name|errors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|doBackup
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|fatalErrorsFound
argument_list|(
name|errors
argument_list|)
condition|)
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
literal|"Fatal errors were found: pausing the consistency check task."
argument_list|)
expr_stmt|;
block|}
name|paused
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|doBackup
condition|)
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
literal|"Starting backup..."
argument_list|)
expr_stmt|;
block|}
name|SystemExport
name|sysexport
init|=
operator|new
name|SystemExport
argument_list|(
name|broker
argument_list|,
name|logCallback
argument_list|,
name|monitor
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|lastExportedBackup
operator|=
name|sysexport
operator|.
name|export
argument_list|(
name|exportDir
argument_list|,
name|incremental
argument_list|,
name|maxInc
argument_list|,
literal|true
argument_list|,
name|errors
argument_list|)
expr_stmt|;
name|agentInstance
operator|.
name|changeStatus
argument_list|(
name|brokerPool
argument_list|,
operator|new
name|TaskStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|RUNNING_BACKUP
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
name|lastExportedBackup
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Created backup to file: "
operator|+
name|lastExportedBackup
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|TerminatedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
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
if|if
condition|(
name|report
operator|!=
literal|null
condition|)
name|report
operator|.
name|close
argument_list|()
expr_stmt|;
name|agentInstance
operator|.
name|changeStatus
argument_list|(
name|brokerPool
argument_list|,
name|endStatus
argument_list|)
expr_stmt|;
name|brokerPool
operator|.
name|getProcessMonitor
argument_list|()
operator|.
name|endJob
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Gets the last exported backup      */
specifier|public
name|File
name|getLastExportedBackup
parameter_list|()
block|{
return|return
name|lastExportedBackup
return|;
block|}
specifier|private
name|boolean
name|fatalErrorsFound
parameter_list|(
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
parameter_list|)
block|{
for|for
control|(
name|ErrorReport
name|error
range|:
name|errors
control|)
block|{
switch|switch
condition|(
name|error
operator|.
name|getErrcode
argument_list|()
condition|)
block|{
comment|// the following errors are considered fatal: export the db and
comment|// stop the task
case|case
name|ErrorReport
operator|.
name|CHILD_COLLECTION
case|:
case|case
name|ErrorReport
operator|.
name|RESOURCE_ACCESS_FAILED
case|:
return|return
literal|true
return|;
block|}
block|}
comment|// no fatal errors
return|return
literal|false
return|;
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
specifier|static
class|class
name|LoggingCallback
implements|implements
name|SystemExport
operator|.
name|StatusCallback
block|{
specifier|public
name|void
name|startCollection
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|TerminatedException
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
throws|throws
name|TerminatedException
block|{
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
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|exception
argument_list|)
expr_stmt|;
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
comment|//        public void startDocument(String path) {
comment|//        }
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
throws|throws
name|TerminatedException
block|{
if|if
condition|(
operator|!
name|monitor
operator|.
name|proceed
argument_list|()
condition|)
throw|throw
operator|new
name|TerminatedException
argument_list|(
literal|"consistency check terminated"
argument_list|)
throw|;
if|if
condition|(
operator|(
name|current
operator|%
literal|1000
operator|==
literal|0
operator|)
operator|||
operator|(
name|current
operator|==
name|count
operator|)
condition|)
block|{
name|log
operator|.
name|write
argument_list|(
literal|"  DOCUMENT: "
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|current
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
literal|" of "
argument_list|)
expr_stmt|;
name|log
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|count
argument_list|)
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
name|log
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|startCollection
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|TerminatedException
block|{
if|if
condition|(
operator|!
name|monitor
operator|.
name|proceed
argument_list|()
condition|)
throw|throw
operator|new
name|TerminatedException
argument_list|(
literal|"consistency check terminated"
argument_list|)
throw|;
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
name|log
operator|.
name|flush
argument_list|()
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
name|log
operator|.
name|flush
argument_list|()
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
name|log
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

