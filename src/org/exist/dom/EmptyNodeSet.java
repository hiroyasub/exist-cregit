begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2001-06,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public  * License along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *   * $Id$  */
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
name|xquery
operator|.
name|value
operator|.
name|Item
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
name|SequenceIterator
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|EmptyNodeSet
extends|extends
name|AbstractNodeSet
block|{
specifier|private
specifier|final
specifier|static
name|EmptyNodeSetIterator
name|EMPTY_ITERATOR
init|=
operator|new
name|EmptyNodeSetIterator
argument_list|()
decl_stmt|;
specifier|public
name|NodeSetIterator
name|iterator
parameter_list|()
block|{
return|return
name|EMPTY_ITERATOR
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
return|return
name|SequenceIterator
operator|.
name|EMPTY_ITERATOR
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.AbstractNodeSet#unorderedIterator() 	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
block|{
return|return
name|SequenceIterator
operator|.
name|EMPTY_ITERATOR
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
block|}
specifier|public
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeList
name|other
parameter_list|)
block|{
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|Node
name|item
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|EmptyNodeSetIterator
implements|implements
name|NodeSetIterator
block|{
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#remove() 		 */
specifier|public
name|void
name|remove
parameter_list|()
block|{
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#hasNext() 		 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#next() 		 */
specifier|public
name|Object
name|next
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setPosition
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"Empty#"
argument_list|)
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

