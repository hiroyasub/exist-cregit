begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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

begin_class
specifier|public
class|class
name|EmptySequenceIterator
implements|implements
name|SequenceIterator
block|{
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.SequenceIterator#hasNext() 	 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.SequenceIterator#nextItem() 	 */
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

