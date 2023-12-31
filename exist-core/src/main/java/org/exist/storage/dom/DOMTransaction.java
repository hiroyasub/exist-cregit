begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist team  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|dom
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
name|SupplierE
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
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|lock
operator|.
name|Lock
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
name|lock
operator|.
name|Lock
operator|.
name|LockMode
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
name|lock
operator|.
name|ManagedLock
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
name|util
operator|.
name|LockException
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
name|ReadOnlyException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * DOMTransaction controls access to the DOM file  *   * This implements a wrapper around the code passed in  * method start(). The class acquires a lock on the  * file, enters the locked code block and calls start.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DOMTransaction
block|{
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
name|DOMTransaction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Object
name|ownerObject
decl_stmt|;
specifier|private
specifier|final
name|DOMFile
name|file
decl_stmt|;
specifier|private
specifier|final
name|SupplierE
argument_list|<
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
argument_list|,
name|LockException
argument_list|>
name|acquireFn
decl_stmt|;
specifier|private
specifier|final
name|DocumentImpl
name|document
decl_stmt|;
comment|/**      * Creates a new<code>DOMTransaction</code> instance.      *      * @param owner an<code>Object</code> value      * @param file a<code>DOMFile</code> value      * @param acquireFn a<code>Supplier</code> value      */
specifier|public
name|DOMTransaction
parameter_list|(
specifier|final
name|Object
name|owner
parameter_list|,
specifier|final
name|DOMFile
name|file
parameter_list|,
specifier|final
name|SupplierE
argument_list|<
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
argument_list|,
name|LockException
argument_list|>
name|acquireFn
parameter_list|)
block|{
name|this
argument_list|(
name|owner
argument_list|,
name|file
argument_list|,
name|acquireFn
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new<code>DOMTransaction</code> instance.      *      * @param owner an<code>Object</code> value      * @param file a<code>DOMFile</code> value      * @param acquireFn a<code>Supplier</code> value      * @param doc a<code>DocumentImpl</code> value      */
specifier|public
name|DOMTransaction
parameter_list|(
specifier|final
name|Object
name|owner
parameter_list|,
specifier|final
name|DOMFile
name|file
parameter_list|,
specifier|final
name|SupplierE
argument_list|<
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
argument_list|,
name|LockException
argument_list|>
name|acquireFn
parameter_list|,
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|this
operator|.
name|ownerObject
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|acquireFn
operator|=
name|acquireFn
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|doc
expr_stmt|;
block|}
comment|/**      * The method<code>start</code>      *      * @return an<code>Object</code> value      * @throws ReadOnlyException if an error occurs      */
specifier|public
specifier|abstract
name|Object
name|start
parameter_list|()
throws|throws
name|ReadOnlyException
function_decl|;
comment|/**      * The method<code>run</code>      *      * @return an<code>Object</code> value      */
specifier|public
name|Object
name|run
parameter_list|()
block|{
comment|// try to acquire a lock on the file
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
name|domFileLock
init|=
name|acquireFn
operator|.
name|get
argument_list|()
init|)
block|{
name|file
operator|.
name|setOwnerObject
argument_list|(
name|ownerObject
argument_list|)
expr_stmt|;
name|file
operator|.
name|setCurrentDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
return|return
name|start
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to acquire read lock on "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ReadOnlyException
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
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

