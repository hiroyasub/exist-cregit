begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|util
operator|.
name|FastQSort
import|;
end_import

begin_comment
comment|/**  * Simple list of node ids and their offsets within the text sequence. Mainly used by  * {@link org.exist.storage.NativeTextEngine} during indexing. */
end_comment

begin_class
specifier|public
class|class
name|OccurrenceList
block|{
specifier|private
name|NodeId
name|nodes
index|[]
init|=
operator|new
name|NodeId
index|[
literal|4
index|]
decl_stmt|;
specifier|private
name|int
name|offsets
index|[]
init|=
operator|new
name|int
index|[
literal|4
index|]
decl_stmt|;
specifier|private
name|int
name|position
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|add
parameter_list|(
name|NodeId
name|id
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|ensureCapacity
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|nodes
index|[
name|position
index|]
operator|=
name|id
expr_stmt|;
name|offsets
index|[
name|position
operator|++
index|]
operator|=
name|offset
expr_stmt|;
block|}
specifier|public
name|NodeId
name|getNode
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|nodes
index|[
name|pos
index|]
return|;
block|}
specifier|public
name|int
name|getOffset
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|offsets
index|[
name|pos
index|]
return|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|position
return|;
block|}
specifier|public
name|int
name|getTermCount
parameter_list|()
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|position
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|nodes
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|nodes
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
condition|)
name|count
operator|++
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|public
name|int
name|getOccurrences
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
operator|+
literal|1
init|;
name|i
operator|<
name|position
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|nodes
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|nodes
index|[
name|start
index|]
argument_list|)
condition|)
name|count
operator|++
expr_stmt|;
else|else
break|break;
block|}
return|return
name|count
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|NodeId
name|id
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|position
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|nodes
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|ensureCapacity
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|==
name|nodes
operator|.
name|length
condition|)
block|{
name|NodeId
index|[]
name|nn
init|=
operator|new
name|NodeId
index|[
name|count
operator|*
literal|2
index|]
decl_stmt|;
name|int
index|[]
name|no
init|=
operator|new
name|int
index|[
name|nn
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|nodes
argument_list|,
literal|0
argument_list|,
name|nn
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|offsets
argument_list|,
literal|0
argument_list|,
name|no
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|nn
expr_stmt|;
name|offsets
operator|=
name|no
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|sort
parameter_list|()
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|nodes
argument_list|,
literal|0
argument_list|,
name|position
operator|-
literal|1
argument_list|,
name|offsets
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

