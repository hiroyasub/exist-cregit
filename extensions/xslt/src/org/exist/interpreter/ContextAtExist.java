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
name|interpreter
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
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
name|util
operator|.
name|hashtable
operator|.
name|NamePool
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
name|XPathException
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
name|value
operator|.
name|AnyURIValue
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|ContextAtExist
block|{
specifier|public
name|DBBroker
name|getBroker
parameter_list|()
function_decl|;
specifier|public
name|NamePool
name|getSharedNamePool
parameter_list|()
function_decl|;
specifier|public
name|String
name|getModuleLoadPath
parameter_list|()
function_decl|;
specifier|public
name|String
name|getPrefixForURI
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
function_decl|;
specifier|public
name|void
name|declareInScopeNamespace
parameter_list|(
name|String
name|string
parameter_list|,
name|String
name|namespaceURI
parameter_list|)
function_decl|;
comment|//TODO: change to DocumentAtExist
specifier|public
name|DocumentImpl
name|storeTemporaryDoc
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|boolean
name|isBaseURIDeclared
parameter_list|()
function_decl|;
specifier|public
name|AnyURIValue
name|getBaseURI
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|//	public TraceListener getTraceListener();
comment|//	public void getTraceListener(TraceListener listener);
block|}
end_interface

end_unit

