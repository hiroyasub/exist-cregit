begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|DOMException
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
name|Document
import|;
end_import

begin_comment
comment|//import org.exist.collections.Collection;
end_comment

begin_comment
comment|//import org.exist.interpreter.ContextAtExist;
end_comment

begin_comment
comment|//import org.exist.security.User;
end_comment

begin_comment
comment|//import org.exist.storage.lock.Lock;
end_comment

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|DocumentAtExist
extends|extends
name|NodeAtExist
extends|,
name|Document
block|{
comment|//	public void setContext(ContextAtExist context);
comment|//	public ContextAtExist getContext();
specifier|public
name|int
name|getFirstChildFor
parameter_list|(
name|int
name|nodeNumber
parameter_list|)
function_decl|;
specifier|public
name|NodeAtExist
name|getNode
parameter_list|(
name|int
name|nodeNr
parameter_list|)
throws|throws
name|DOMException
function_decl|;
comment|//memory
specifier|public
name|int
name|getNextNodeNumber
parameter_list|(
name|int
name|nodeNr
parameter_list|)
throws|throws
name|DOMException
function_decl|;
comment|//memory
specifier|public
name|boolean
name|hasReferenceNodes
parameter_list|()
function_decl|;
comment|//    public boolean isLockedForWrite(); //synchronized
comment|//    public Lock getUpdateLock(); //final synchronized
comment|//
comment|//	public void setUserLock(User user);
comment|//	public User getUserLock();
comment|//	public Collection getCollection();
specifier|public
name|int
name|getDocId
parameter_list|()
function_decl|;
specifier|public
name|XmldbURI
name|getURI
parameter_list|()
function_decl|;
specifier|public
name|Database
name|getDatabase
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

