begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
package|;
end_package

begin_class
specifier|public
class|class
name|EmptySequence
extends|extends
name|AbstractSequence
block|{
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#getItemType() 	 */
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|EMPTY
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
return|return
name|EmptySequenceIterator
operator|.
name|EMPTY_ITERATOR
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

