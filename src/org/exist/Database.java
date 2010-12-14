begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|indexing
operator|.
name|IndexManager
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
name|security
operator|.
name|Subject
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
name|CacheManager
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
name|txn
operator|.
name|TransactionManager
import|;
end_import

begin_comment
comment|/**  * Database controller, all operation synchronized by this instance. (singleton)  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Database
block|{
comment|//TODO: javadocs
specifier|public
name|String
name|getId
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @return 	 */
specifier|public
name|SecurityManager
name|getSecurityManager
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @return 	 */
specifier|public
name|IndexManager
name|getIndexManager
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @return 	 */
specifier|public
name|TransactionManager
name|getTransactionManager
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @return 	 */
specifier|public
name|CacheManager
name|getCacheManager
parameter_list|()
function_decl|;
comment|/** 	 *  	 */
specifier|public
name|void
name|shutdown
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @return 	 */
specifier|public
name|Subject
name|getSubject
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @param subject 	 */
specifier|public
name|boolean
name|setSubject
parameter_list|(
name|Subject
name|subject
parameter_list|)
function_decl|;
specifier|public
name|DBBroker
name|getBroker
parameter_list|()
throws|throws
name|EXistException
function_decl|;
comment|//TODO: remove 'throws EXistException'?
comment|/* 	 * @Deprecated ? 	 * try { 	 * 	broker = database.authenticate(account, credentials); 	 *  	 * 	broker1 = database.get(); 	 * 	broker2 = database.get(); 	 * 	... 	 * 	brokerN = database.get(); 	 *  	 * } finally { 	 * 	database.release(broker); 	 * } 	 */
specifier|public
name|DBBroker
name|get
parameter_list|(
name|Subject
name|subject
parameter_list|)
throws|throws
name|EXistException
function_decl|;
specifier|public
name|void
name|release
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

