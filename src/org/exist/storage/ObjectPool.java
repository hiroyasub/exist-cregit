begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist xml document repository and xpath implementation  * Copyright (C) 2000,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|*
import|;
end_import

begin_comment
comment|/**  * a simple pool for caching nodes  *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ObjectPool
block|{
specifier|protected
name|TreeMap
name|map
decl_stmt|;
specifier|protected
name|int
name|MAX
init|=
literal|15000
decl_stmt|;
specifier|public
name|ObjectPool
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|MAX
operator|=
name|max
expr_stmt|;
name|map
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ObjectPool
parameter_list|()
block|{
name|map
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|node
operator|.
name|getGID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|==
name|MAX
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"cleaning up ObjectPool"
argument_list|)
expr_stmt|;
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|p
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeImpl
name|get
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|)
block|{
return|return
operator|(
name|NodeImpl
operator|)
name|map
operator|.
name|get
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
operator|new
name|NodeProxy
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|node
operator|.
name|getGID
argument_list|()
argument_list|)
argument_list|)
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
name|gid
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getMax
parameter_list|()
block|{
return|return
name|MAX
return|;
block|}
block|}
end_class

end_unit

