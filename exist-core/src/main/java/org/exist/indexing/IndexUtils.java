begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
package|;
end_package

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
name|AttrImpl
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
name|ElementImpl
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
name|IStoredNode
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
name|TextImpl
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
name|NodePath
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
name|dom
operator|.
name|INodeIterator
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
name|Txn
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Various utility methods to be used by Index implementations.  */
end_comment

begin_class
specifier|public
class|class
name|IndexUtils
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|IndexUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|void
name|scanNode
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|IStoredNode
name|node
parameter_list|,
name|StreamListener
name|listener
parameter_list|)
block|{
try|try
init|(
specifier|final
name|INodeIterator
name|iterator
init|=
name|broker
operator|.
name|getNodeIterator
argument_list|(
name|node
argument_list|)
init|)
block|{
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
specifier|final
name|NodePath
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|scanNode
argument_list|(
name|transaction
argument_list|,
name|iterator
argument_list|,
name|node
argument_list|,
name|listener
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to close iterator"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|scanNode
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|INodeIterator
name|iterator
parameter_list|,
name|IStoredNode
name|node
parameter_list|,
name|StreamListener
name|listener
parameter_list|,
name|NodePath
name|currentPath
parameter_list|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|startElement
argument_list|(
name|transaction
argument_list|,
operator|(
name|ElementImpl
operator|)
name|node
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|hasChildNodes
argument_list|()
operator|||
name|node
operator|.
name|hasAttributes
argument_list|()
condition|)
block|{
specifier|final
name|int
name|childCount
init|=
name|node
operator|.
name|getChildCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|childCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|IStoredNode
name|child
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|currentPath
operator|.
name|addComponent
argument_list|(
name|child
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|scanNode
argument_list|(
name|transaction
argument_list|,
name|iterator
argument_list|,
name|child
argument_list|,
name|listener
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|currentPath
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|endElement
argument_list|(
name|transaction
argument_list|,
operator|(
name|ElementImpl
operator|)
name|node
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|characters
argument_list|(
name|transaction
argument_list|,
operator|(
name|TextImpl
operator|)
name|node
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|attribute
argument_list|(
name|transaction
argument_list|,
operator|(
name|AttrImpl
operator|)
name|node
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
end_class

end_unit

