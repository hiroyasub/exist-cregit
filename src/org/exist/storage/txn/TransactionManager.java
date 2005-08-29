begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
package|;
end_package

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
name|security
operator|.
name|PermissionDeniedException
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
name|SecurityManager
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
name|journal
operator|.
name|Journal
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
name|journal
operator|.
name|LogException
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
name|recovery
operator|.
name|RecoveryManager
import|;
end_import

begin_comment
comment|/**  * This is the central entry point to the transaction management service.  *   * There's only one TransactionManager per database instance that can be  * retrieved via {@link BrokerPool#getTransactionManager()}. TransactionManager  * provides methods to create, commit and rollback a transaction.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|TransactionManager
block|{
comment|/**      * Logger for this class      */
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
name|TransactionManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|nextTxnId
init|=
literal|0
decl_stmt|;
specifier|private
name|Journal
name|journal
decl_stmt|;
specifier|private
name|boolean
name|enabled
decl_stmt|;
specifier|private
name|boolean
name|groupCommit
init|=
literal|false
decl_stmt|;
comment|/**      * Initialize the transaction manager using the specified data directory.      *       * @param dataDir      * @throws EXistException      */
specifier|public
name|TransactionManager
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|File
name|dataDir
parameter_list|,
name|boolean
name|transactionsEnabled
parameter_list|)
throws|throws
name|EXistException
block|{
name|enabled
operator|=
name|transactionsEnabled
expr_stmt|;
if|if
condition|(
name|enabled
condition|)
name|journal
operator|=
operator|new
name|Journal
argument_list|(
name|pool
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
name|Boolean
name|groupOpt
init|=
operator|(
name|Boolean
operator|)
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"db-connection.recovery.group-commit"
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupOpt
operator|!=
literal|null
condition|)
block|{
name|groupCommit
operator|=
name|groupOpt
operator|.
name|booleanValue
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
literal|"GroupCommits = "
operator|+
name|groupCommit
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Run a database recovery if required. This method is called once during      * startup from {@link org.exist.storage.BrokerPool}.      *       * @param broker      * @throws EXistException      */
specifier|public
name|boolean
name|runRecovery
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|RecoveryManager
name|recovery
init|=
operator|new
name|RecoveryManager
argument_list|(
name|broker
argument_list|,
name|journal
argument_list|)
decl_stmt|;
return|return
name|recovery
operator|.
name|recover
argument_list|()
return|;
block|}
comment|/**      * Create a new transaction. Creates a new transaction id that will      * be logged to disk immediately.       *       * @return      * @throws TransactionException      */
specifier|public
name|Txn
name|beginTransaction
parameter_list|()
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return
literal|null
return|;
name|long
name|txnId
init|=
name|nextTxnId
operator|++
decl_stmt|;
try|try
block|{
name|journal
operator|.
name|writeToLog
argument_list|(
operator|new
name|TxnStart
argument_list|(
name|txnId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to create transaction. Error writing to log file."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Txn
argument_list|(
name|txnId
argument_list|)
return|;
block|}
comment|/**      * Commit a transaction.      *       * @param txn      * @throws TransactionException      */
specifier|public
name|void
name|commit
parameter_list|(
name|Txn
name|txn
parameter_list|)
throws|throws
name|TransactionException
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
if|if
condition|(
name|enabled
condition|)
block|{
name|journal
operator|.
name|writeToLog
argument_list|(
operator|new
name|TxnCommit
argument_list|(
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|groupCommit
condition|)
name|journal
operator|.
name|flushToLog
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|txn
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|abort
parameter_list|(
name|Txn
name|txn
parameter_list|)
block|{
name|txn
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
block|}
comment|/**      * Create a new checkpoint. A checkpoint fixes the current database state. All dirty pages      * are written to disk and the journal file is cleaned.      *       * This method is called from       * {@link org.exist.storage.BrokerPool} within pre-defined periods. It      * should not be called from somewhere else. The database needs to      * be in a stable state (all transactions completed, no operations running).      *       * @throws TransactionException      */
specifier|public
name|void
name|checkpoint
parameter_list|(
name|boolean
name|switchFiles
parameter_list|)
throws|throws
name|TransactionException
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
name|long
name|txnId
init|=
name|nextTxnId
operator|++
decl_stmt|;
name|journal
operator|.
name|checkpoint
argument_list|(
name|txnId
argument_list|,
name|switchFiles
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Journal
name|getJournal
parameter_list|()
block|{
return|return
name|journal
return|;
block|}
specifier|public
name|void
name|reindex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|reindex
argument_list|(
literal|"/db"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception during reindex: "
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
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|enabled
condition|)
name|journal
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

