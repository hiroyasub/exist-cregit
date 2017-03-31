begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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

begin_comment
comment|//TODO replace with extends Iterator<Item>
end_comment

begin_interface
specifier|public
interface|interface
name|SequenceIterator
block|{
name|SequenceIterator
name|EMPTY_ITERATOR
init|=
operator|new
name|EmptySequenceIterator
argument_list|()
decl_stmt|;
comment|/** 	 * Determines if there is a next item in the sequence 	 * 	 * @return true if there is another item available, false otherwise. 	 */
name|boolean
name|hasNext
parameter_list|()
function_decl|;
comment|/** 	 * Retrieves the next item from the Sequence 	 * 	 * @return The item, or null if there are no more items 	 */
name|Item
name|nextItem
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

