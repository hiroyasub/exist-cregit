begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DocumentOrderComparator
implements|implements
name|Comparator
block|{
specifier|public
name|DocumentOrderComparator
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) 	 */
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
specifier|final
name|NodeProxy
name|p1
init|=
operator|(
name|NodeProxy
operator|)
name|o1
decl_stmt|;
specifier|final
name|NodeProxy
name|p2
init|=
operator|(
name|NodeProxy
operator|)
name|o2
decl_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|p1
operator|.
name|doc
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|docId
operator|>
name|p2
operator|.
name|doc
operator|.
name|docId
condition|)
return|return
literal|1
return|;
if|else if
condition|(
name|doc
operator|.
name|docId
operator|<
name|p2
operator|.
name|doc
operator|.
name|docId
condition|)
return|return
operator|-
literal|1
return|;
else|else
block|{
if|if
condition|(
name|p1
operator|.
name|gid
operator|==
name|p2
operator|.
name|gid
condition|)
return|return
literal|0
return|;
name|int
name|la
init|=
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|p1
operator|.
name|gid
argument_list|)
decl_stmt|;
name|int
name|lb
init|=
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|p2
operator|.
name|gid
argument_list|)
decl_stmt|;
if|if
condition|(
name|la
operator|==
name|lb
condition|)
return|return
name|p1
operator|.
name|gid
operator|<
name|p2
operator|.
name|gid
condition|?
operator|-
literal|1
else|:
literal|1
return|;
name|long
name|pa
init|=
name|p1
operator|.
name|gid
decl_stmt|,
name|pb
init|=
name|p2
operator|.
name|gid
decl_stmt|;
if|if
condition|(
name|la
operator|>
name|lb
condition|)
block|{
while|while
condition|(
name|la
operator|>
name|lb
condition|)
block|{
name|pa
operator|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|doc
argument_list|,
name|pa
argument_list|,
name|la
argument_list|)
expr_stmt|;
operator|--
name|la
expr_stmt|;
block|}
if|if
condition|(
name|pa
operator|==
name|pb
condition|)
return|return
literal|1
return|;
else|else
return|return
name|pa
operator|<
name|pb
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
else|else
block|{
while|while
condition|(
name|lb
operator|>
name|la
condition|)
block|{
name|pb
operator|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|doc
argument_list|,
name|pb
argument_list|,
name|lb
argument_list|)
expr_stmt|;
operator|--
name|lb
expr_stmt|;
block|}
if|if
condition|(
name|pb
operator|==
name|pa
condition|)
return|return
operator|-
literal|1
return|;
else|else
return|return
name|pa
operator|<
name|pb
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

