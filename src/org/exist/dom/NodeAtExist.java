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
name|dom
operator|.
name|QNameable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_comment
comment|//import org.exist.xquery.NodeTest;
end_comment

begin_comment
comment|//import org.exist.xquery.XPathException;
end_comment

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeAtExist
extends|extends
name|Node
extends|,
name|QNameable
extends|,
name|Comparable
argument_list|<
name|Object
argument_list|>
block|{
specifier|public
name|DocumentAtExist
name|getDocumentAtExist
parameter_list|()
function_decl|;
comment|//the document nodes' array index
specifier|public
name|int
name|getNodeNumber
parameter_list|()
function_decl|;
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
comment|//	public Boolean matchChildren(NodeTest test) throws XPathException;
block|}
end_interface

end_unit

