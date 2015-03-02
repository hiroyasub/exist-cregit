begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
operator|.
name|TransactionException
import|;
end_import

begin_comment
comment|/**  * Represents an atomic Transaction on the database  *  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Transaction
extends|extends
name|AutoCloseable
block|{
comment|/**      * @Deprecated use org.exist.Transaction#commit() instead      */
annotation|@
name|Deprecated
specifier|public
name|void
name|success
parameter_list|()
throws|throws
name|TransactionException
function_decl|;
comment|/**      * Performs an atomic commit of the transaction      *      * @throws org.exist.storage.txn.TransactionException if an error occurred      *   during writing any part of the transaction      */
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|TransactionException
function_decl|;
comment|/**      * @Deprecated use org.exist.Transaction#abort() instead      */
annotation|@
name|Deprecated
specifier|public
name|void
name|failure
parameter_list|()
function_decl|;
comment|/**      * Performs an atomic abort of the transaction      */
specifier|public
name|void
name|abort
parameter_list|()
function_decl|;
comment|/**      * Closes the transaction      *      * If the transaction has not been committed then      * it will be auto-aborted.      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

