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
name|util
operator|.
name|hashtable
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|hashtable
operator|.
name|Int2ObjectHashMap
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|HashtableTest
extends|extends
name|TestCase
block|{
specifier|private
name|int
name|tabSize
init|=
literal|10000
decl_stmt|;
specifier|private
name|Int2ObjectHashMap
name|table
init|=
operator|new
name|Int2ObjectHashMap
argument_list|(
name|tabSize
argument_list|)
decl_stmt|;
specifier|private
name|int
name|keys
index|[]
init|=
operator|new
name|int
index|[
name|tabSize
index|]
decl_stmt|;
specifier|private
name|Object
name|values
index|[]
init|=
operator|new
name|Object
index|[
name|tabSize
index|]
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|HashtableTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPut
parameter_list|()
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
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
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
name|keys
index|[
name|i
index|]
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
operator|new
name|String
argument_list|(
literal|"a"
operator|+
name|keys
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|table
operator|.
name|put
argument_list|(
name|keys
index|[
name|i
index|]
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|v
init|=
name|table
operator|.
name|get
argument_list|(
name|keys
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|int
name|r
decl_stmt|;
name|long
name|p
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
name|tabSize
operator|/
literal|10
condition|;
name|i
operator|++
control|)
block|{
do|do
block|{
name|r
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|tabSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|values
index|[
name|r
index|]
operator|==
literal|null
condition|)
do|;
name|table
operator|.
name|remove
argument_list|(
name|keys
index|[
name|r
index|]
argument_list|)
expr_stmt|;
comment|//assertTrue(p> -1);
name|values
index|[
name|r
index|]
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|String
name|v
init|=
operator|(
name|String
operator|)
name|table
operator|.
name|get
argument_list|(
name|keys
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"key "
operator|+
name|keys
index|[
name|i
index|]
operator|+
literal|" already removed?"
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|table
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|c
operator|++
control|)
block|{
name|Integer
name|v
init|=
operator|(
name|Integer
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|table
operator|.
name|size
argument_list|()
operator|+
literal|" = "
operator|+
name|c
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"maxRehash: "
operator|+
name|table
operator|.
name|getMaxRehash
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|table
operator|.
name|size
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

