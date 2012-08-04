begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|Node
import|;
end_import

begin_comment
comment|/**  * Represents a node value. May either be an in-memory node  * or a persistent node.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeValue
extends|extends
name|Item
extends|,
name|Sequence
block|{
comment|/** Node is a constructed in-memory node */
specifier|public
specifier|final
specifier|static
name|int
name|IN_MEMORY_NODE
init|=
literal|0
decl_stmt|;
comment|/** Node is a persistent, i.e. stored in the database */
specifier|public
specifier|final
specifier|static
name|int
name|PERSISTENT_NODE
init|=
literal|1
decl_stmt|;
comment|/** 	 * Returns true if this node has the same identity as another 	 * node. Used to implement "is" and "isnot" comparisons. 	 *  	 * @param other 	 * @throws XPathException 	 */
specifier|public
name|boolean
name|equals
parameter_list|(
name|NodeValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns true if this node comes before another node in 	 * document order. 	 *  	 * @param other 	 * @throws XPathException 	 */
specifier|public
name|boolean
name|before
parameter_list|(
name|NodeValue
name|other
parameter_list|,
name|boolean
name|isPreceding
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns true if this node comes after another node in 	 * document order. 	 *  	 * @param other 	 * @throws XPathException 	 */
specifier|public
name|boolean
name|after
parameter_list|(
name|NodeValue
name|other
parameter_list|,
name|boolean
name|isFollowing
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns the implementation-type of this node, i.e. either 	 * {@link #IN_MEMORY_NODE} or {@link #PERSISTENT_NODE}. 	 *  	 */
specifier|public
name|int
name|getImplementationType
parameter_list|()
function_decl|;
specifier|public
name|void
name|addContextNode
parameter_list|(
name|int
name|contextId
parameter_list|,
name|NodeValue
name|node
parameter_list|)
function_decl|;
comment|/** Retrieve the actual node. This operation is<strong>expensive</strong>. 	 * @return The actual node. 	 */
specifier|public
name|Node
name|getNode
parameter_list|()
function_decl|;
specifier|public
name|Document
name|getOwnerDocument
parameter_list|()
function_decl|;
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

