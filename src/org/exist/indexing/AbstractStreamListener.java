begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
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
name|CharacterDataImpl
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
name|txn
operator|.
name|Txn
import|;
end_import

begin_comment
comment|/**  * Default implementation of a StreamListener. By default forwards all events to  * the next listener in the chain (if there is any). Overwrite methods to handle events  * (but don't forget to call the super method as well).  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractStreamListener
implements|implements
name|StreamListener
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AbstractStreamListener
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|StreamListener
name|next
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setNextInChain
parameter_list|(
name|StreamListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|listener
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|StreamListener
name|getNextInChain
parameter_list|()
block|{
return|return
name|next
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|element
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|startElement
argument_list|(
name|transaction
argument_list|,
name|element
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|AttrImpl
name|attrib
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|attribute
argument_list|(
name|transaction
argument_list|,
name|attrib
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|element
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|endElement
argument_list|(
name|transaction
argument_list|,
name|element
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|CharacterDataImpl
name|text
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|characters
argument_list|(
name|transaction
argument_list|,
name|text
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|abstract
name|IndexWorker
name|getWorker
parameter_list|()
function_decl|;
block|}
end_class

end_unit

