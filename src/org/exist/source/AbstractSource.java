begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
package|;
end_package

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSource
implements|implements
name|Source
block|{
specifier|private
name|long
name|cacheTime
init|=
literal|0
decl_stmt|;
comment|/* (non-Javadoc)      * @see java.lang.Object#equals(java.lang.Object)      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Source
operator|)
name|obj
operator|)
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see java.lang.Object#hashCode()      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getKey
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#getCacheTimestamp()      */
specifier|public
name|long
name|getCacheTimestamp
parameter_list|()
block|{
return|return
name|cacheTime
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.source.Source#setCacheTimestamp(long)      */
specifier|public
name|void
name|setCacheTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|cacheTime
operator|=
name|timestamp
expr_stmt|;
block|}
block|}
end_class

end_unit

