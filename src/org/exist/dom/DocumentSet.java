begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*   * eXist Open Source Native XML Database  *   * Copyright (C) 2000,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
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

begin_comment
comment|//import it.unimi.dsi.fastutil.Int2ObjectAVLTreeMap;
end_comment

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|Int2ObjectOpenHashMap
import|;
end_import

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|ObjectAVLTreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|*
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

begin_comment
comment|/**  * Manages a set of documents.  *   * This class implements the NodeList interface for a collection of documents.  * It also contains methods to retrieve the collections these documents  * belong to.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DocumentSet
extends|extends
name|Int2ObjectOpenHashMap
implements|implements
name|NodeList
block|{
specifier|private
specifier|final
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|DocumentSet
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|ArrayList
name|list
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|allDocuments
init|=
literal|false
decl_stmt|;
specifier|private
name|ObjectAVLTreeSet
name|collections
init|=
operator|new
name|ObjectAVLTreeSet
argument_list|()
decl_stmt|;
specifier|public
name|DocumentSet
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setAllDocuments
parameter_list|(
name|boolean
name|all
parameter_list|)
block|{
name|allDocuments
operator|=
name|all
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasAllDocuments
parameter_list|()
block|{
return|return
name|allDocuments
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|docId
init|=
name|doc
operator|.
name|getDocId
argument_list|()
decl_stmt|;
if|if
condition|(
name|containsKey
argument_list|(
name|docId
argument_list|)
condition|)
return|return;
name|put
argument_list|(
name|docId
argument_list|,
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
name|list
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|getCollection
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|collections
operator|.
name|contains
argument_list|(
name|doc
operator|.
name|getCollection
argument_list|()
argument_list|)
operator|)
condition|)
name|collections
operator|.
name|add
argument_list|(
name|doc
operator|.
name|getCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|DocumentImpl
operator|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"wrong implementation"
argument_list|)
throw|;
name|add
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeList
name|other
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
name|other
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
name|add
argument_list|(
name|other
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Fast method to add a bunch of documents from a 	 * Java collection. 	 *  	 * The method assumes that no duplicate entries are 	 * in the input collection. 	 *  	 * @param docs 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|java
operator|.
name|util
operator|.
name|Collection
name|docs
parameter_list|)
block|{
name|DocumentImpl
name|doc
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|put
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
name|collections
operator|.
name|add
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|Iterator
name|getCollectionIterator
parameter_list|()
block|{
return|return
name|collections
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|size
argument_list|()
return|;
block|}
specifier|public
name|int
name|getCollectionCount
parameter_list|()
block|{
return|return
name|collections
operator|.
name|size
argument_list|()
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
if|if
condition|(
name|list
operator|==
literal|null
condition|)
name|list
operator|=
operator|new
name|ArrayList
argument_list|(
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|Node
operator|)
name|list
operator|.
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
comment|/* 	public boolean contains(int docId) { 	    return containsKey(docId); 	    //return containsKey(new Integer(docId)); 	} 	*/
specifier|public
name|DocumentImpl
name|getDoc
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|DocumentImpl
operator|)
name|get
argument_list|(
name|docId
argument_list|)
return|;
block|}
specifier|public
name|String
index|[]
name|getNames
parameter_list|()
block|{
name|String
name|result
index|[]
init|=
operator|new
name|String
index|[
name|size
argument_list|()
index|]
decl_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|result
index|[
name|j
index|]
operator|=
name|d
operator|.
name|getFileName
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|DocumentSet
name|intersection
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
block|{
name|DocumentSet
name|r
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|other
operator|.
name|containsKey
argument_list|(
name|d
operator|.
name|docId
argument_list|)
condition|)
name|r
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|i
init|=
name|other
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|containsKey
argument_list|(
name|d
operator|.
name|docId
argument_list|)
operator|&&
operator|(
operator|!
name|r
operator|.
name|containsKey
argument_list|(
name|d
operator|.
name|docId
argument_list|)
operator|)
condition|)
name|r
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
name|DocumentSet
name|union
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
block|{
name|DocumentSet
name|result
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|containsKey
argument_list|(
name|d
operator|.
name|docId
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|size
argument_list|()
operator|>
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
name|DocumentImpl
name|d
decl_stmt|;
name|boolean
name|equal
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|other
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|containsKey
argument_list|(
operator|new
name|Integer
argument_list|(
name|d
operator|.
name|docId
argument_list|)
argument_list|)
condition|)
name|equal
operator|=
literal|true
expr_stmt|;
else|else
name|equal
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|equal
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
name|containsKey
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|int
name|getMinDocId
parameter_list|()
block|{
name|int
name|min
init|=
operator|-
literal|1
decl_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|min
operator|<
literal|0
condition|)
name|min
operator|=
name|d
operator|.
name|getDocId
argument_list|()
expr_stmt|;
if|else if
condition|(
name|d
operator|.
name|getDocId
argument_list|()
operator|<
name|min
condition|)
name|min
operator|=
name|d
operator|.
name|getDocId
argument_list|()
expr_stmt|;
block|}
return|return
name|min
return|;
block|}
specifier|public
name|int
name|getMaxDocId
parameter_list|()
block|{
name|int
name|max
init|=
operator|-
literal|1
decl_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|d
operator|.
name|getDocId
argument_list|()
operator|>
name|max
condition|)
name|max
operator|=
name|d
operator|.
name|getDocId
argument_list|()
expr_stmt|;
block|}
return|return
name|max
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
name|DocumentSet
name|o
init|=
operator|(
name|DocumentSet
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|size
argument_list|()
operator|!=
name|o
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
name|DocumentImpl
name|d
decl_stmt|;
name|boolean
name|equal
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|o
operator|.
name|containsKey
argument_list|(
name|d
operator|.
name|docId
argument_list|)
condition|)
name|equal
operator|=
literal|true
expr_stmt|;
else|else
name|equal
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|equal
return|;
block|}
block|}
end_class

end_unit

