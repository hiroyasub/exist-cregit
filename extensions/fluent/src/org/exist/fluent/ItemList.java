begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|NodeProxy
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
name|XPathException
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
name|*
import|;
end_import

begin_comment
comment|/**  * The result of a query on the database, holding a collection of items.  * The items can be accessed either as structured resources or as string  * values.  It is also possible to further refine the query by executing queries  * within the context of the results.    *   * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  * @version $Revision: 1.17 $ ($Date: 2006/08/14 23:18:22 $)  */
end_comment

begin_class
specifier|public
class|class
name|ItemList
extends|extends
name|Resource
implements|implements
name|Iterable
argument_list|<
name|Item
argument_list|>
block|{
comment|/** 	 * A facet that treats each item in the list as its effective string value.  Atomic values 	 * are converted to strings, while nodes are converted to the concatenation of all their 	 * text descendants (note: not serialized!). 	 */
specifier|public
class|class
name|ValuesFacet
implements|implements
name|Iterable
argument_list|<
name|String
argument_list|>
block|{
specifier|private
name|ValuesFacet
parameter_list|()
block|{
block|}
comment|/** 		 * Return an iterator over the effective string values of the item list. 		 *  		 * @return a string value iterator 		 */
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|SequenceIterator
name|delegate
init|=
name|seq
operator|.
name|iterate
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|String
name|next
parameter_list|()
block|{
try|try
block|{
return|return
name|delegate
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"failed to construct iterator over sequence"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|ItemList
name|itemList
parameter_list|()
block|{
return|return
name|ItemList
operator|.
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|(
name|o
operator|instanceof
name|ValuesFacet
operator|&&
name|ItemList
operator|.
name|this
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ValuesFacet
operator|)
name|o
operator|)
operator|.
name|itemList
argument_list|()
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|ItemList
operator|.
name|this
operator|.
name|hashCode
argument_list|()
operator|+
literal|2
return|;
block|}
comment|/** 		 * Return an unmodifiable list view over the effective string values of the item list. 		 * 		 * @return a list view 		 */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|asList
parameter_list|()
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
try|try
block|{
return|return
name|seq
operator|.
name|itemAt
argument_list|(
name|index
argument_list|)
operator|.
name|getStringValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|seq
operator|.
name|getItemCount
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** 		 * Convert the list of effective string values to an array. 		 *  		 * @return an array of effective string values 		 */
specifier|public
name|String
index|[]
name|toArray
parameter_list|()
block|{
return|return
name|toArray
argument_list|(
operator|new
name|String
index|[
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** 		 * Convert the list of effective string values to an array.  If the supplied array is sufficient 		 * for holding the strings, use it; if it's larger than necessary, put a<code>null</code> after 		 * the end of the list.  If the array is too small, allocate a new one. 		 * 		 * @param a an array to fill with effective string values 		 * @return an array of effective string values 		 */
specifier|public
name|String
index|[]
name|toArray
parameter_list|(
name|String
index|[]
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
operator|&&
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|a
operator|==
literal|null
operator|||
name|a
operator|.
name|length
operator|<
name|size
argument_list|()
condition|)
name|a
operator|=
operator|new
name|String
index|[
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|a
index|[
name|i
index|]
operator|=
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|a
operator|.
name|length
operator|>
name|size
argument_list|()
condition|)
name|a
index|[
name|size
argument_list|()
index|]
operator|=
literal|null
expr_stmt|;
return|return
name|a
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|ItemList
operator|.
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** 	 * A facet that treats each item in the list as a node.  If an operation accesses an item that 	 * is not a node, it will throw a<code>DatabaseException</code>. 	 */
specifier|public
class|class
name|NodesFacet
implements|implements
name|Iterable
argument_list|<
name|Node
argument_list|>
block|{
specifier|private
name|NodesFacet
parameter_list|()
block|{
block|}
comment|/** 		 * Return an iterator over the list of nodes. 		 *  		 * @return an iterator over the list of nodes 		 */
specifier|public
name|Iterator
argument_list|<
name|Node
argument_list|>
name|iterator
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Node
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|SequenceIterator
name|delegate
init|=
name|seq
operator|.
name|iterate
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|Node
name|next
parameter_list|()
block|{
return|return
operator|new
name|Node
argument_list|(
name|delegate
operator|.
name|nextItem
argument_list|()
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|db
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"failed to construct iterator over sequence"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 		 * Return the set of documents to which the nodes in this list belong. 		 * 		 * @return the set of documents convering the nodes in the list 		 */
specifier|public
name|Set
argument_list|<
name|XMLDocument
argument_list|>
name|documents
parameter_list|()
block|{
name|Set
argument_list|<
name|XMLDocument
argument_list|>
name|docs
init|=
operator|new
name|HashSet
argument_list|<
name|XMLDocument
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|this
control|)
block|{
try|try
block|{
name|docs
operator|.
name|add
argument_list|(
name|node
operator|.
name|document
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// ignore, must be a non-persistent node
block|}
block|}
return|return
name|docs
return|;
block|}
specifier|private
name|ItemList
name|itemList
parameter_list|()
block|{
return|return
name|ItemList
operator|.
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|(
name|o
operator|instanceof
name|NodesFacet
operator|&&
name|ItemList
operator|.
name|this
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NodesFacet
operator|)
name|o
operator|)
operator|.
name|itemList
argument_list|()
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|ItemList
operator|.
name|this
operator|.
name|hashCode
argument_list|()
operator|+
literal|1
return|;
block|}
comment|/** 		 * Return an unmodifiable list view over the list of nodes. 		 * 		 * @return a list view 		 */
specifier|public
name|List
argument_list|<
name|Node
argument_list|>
name|asList
parameter_list|()
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Node
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Node
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|new
name|Node
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
name|index
argument_list|)
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|db
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|seq
operator|.
name|getItemCount
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** 		 * Convert the list of nodes to an array. 		 * 		 * @return an array of nodes 		 */
specifier|public
name|Node
index|[]
name|toArray
parameter_list|()
block|{
return|return
name|toArray
argument_list|(
operator|new
name|Node
index|[
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** 		 * Convert the list of nodes to an array.  If the given array is large enough, fill it; if it's 		 * larger than necessary, put a<code>null</code> marker after the end of the list.  If the 		 * array is not large enough, allocate a new one. 		 * 		 * @param a the array to fill with the list of nodes 		 * @return an array of nodes 		 */
specifier|public
name|Node
index|[]
name|toArray
parameter_list|(
name|Node
index|[]
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|length
operator|<
name|size
argument_list|()
condition|)
name|a
operator|=
operator|new
name|Node
index|[
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
operator|new
name|Node
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|length
operator|>
name|size
argument_list|()
condition|)
name|a
index|[
name|size
argument_list|()
index|]
operator|=
literal|null
expr_stmt|;
return|return
name|a
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|this
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|node
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|final
name|Sequence
name|seq
decl_stmt|;
specifier|private
name|ValuesFacet
name|values
decl_stmt|;
specifier|private
name|NodesFacet
name|nodes
decl_stmt|;
specifier|private
name|ItemList
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|seq
operator|=
literal|null
expr_stmt|;
block|}
name|ItemList
parameter_list|(
name|Sequence
name|seq
parameter_list|,
name|NamespaceMap
name|namespaceBindings
parameter_list|,
name|Database
name|db
parameter_list|)
block|{
name|super
argument_list|(
name|namespaceBindings
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|this
operator|.
name|seq
operator|=
name|seq
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|it
init|=
name|seq
operator|.
name|unorderedIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
name|item
init|=
name|it
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|NodeProxy
condition|)
name|Database
operator|.
name|trackNode
argument_list|(
operator|(
name|NodeProxy
operator|)
name|item
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
name|Sequence
name|convertToSequence
parameter_list|()
block|{
return|return
name|seq
return|;
block|}
comment|/** 	 * Return the number of elements in this item list. 	 *  	 * @return the number of elements in this item list 	 */
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|seq
operator|.
name|getItemCount
argument_list|()
return|;
block|}
comment|/** 	 * Return whether this item list is empty. 	 * 	 * @return<code>true</code> if this item list has no elements 	 */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|seq
operator|.
name|isEmpty
argument_list|()
return|;
block|}
name|Item
name|wrap
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|instanceof
name|NodeValue
condition|)
return|return
operator|new
name|Node
argument_list|(
name|x
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|db
argument_list|)
return|;
return|return
operator|new
name|Item
argument_list|(
name|x
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|db
argument_list|)
return|;
block|}
comment|/** 	 * Return the item at the given index in this result.  Indexing starts at 0. 	 * 	 * @param index the index of the desired item 	 * @return the item at the given index 	 * @throws IndexOutOfBoundsException if the index is out of bounds 	 */
specifier|public
name|Item
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|size
argument_list|()
condition|)
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"index "
operator|+
name|index
operator|+
literal|" out of bounds (upper bound at "
operator|+
name|size
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
return|return
name|wrap
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	 * Delete all nodes contained in this item list; skip over any items (values) that 	 * it doesn't make sense to try to delete. 	 */
specifier|public
name|void
name|deleteAllNodes
parameter_list|()
block|{
name|Transaction
name|tx
init|=
name|Database
operator|.
name|requireTransaction
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Item
name|item
range|:
name|this
control|)
if|if
condition|(
name|item
operator|instanceof
name|Node
condition|)
operator|(
operator|(
name|Node
operator|)
name|item
operator|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|tx
operator|.
name|abortIfIncomplete
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|ItemList
operator|)
condition|)
return|return
literal|false
return|;
name|ItemList
name|that
init|=
operator|(
name|ItemList
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|size
argument_list|()
operator|!=
name|that
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|!
name|this
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|that
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/** 	 * The hash code computation can be expensive, and the hash codes may not be very well distributed. 	 * You probably shouldn't use item lists in situations where they might get hashed. 	 */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hashCode
init|=
literal|1
decl_stmt|;
for|for
control|(
name|Item
name|item
range|:
name|this
control|)
name|hashCode
operator|=
name|hashCode
operator|*
literal|31
operator|+
name|item
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hashCode
return|;
block|}
comment|/** 	 * Return an iterator over all the items in this list. 	 *  	 * @return an iterator over this item list 	 */
specifier|public
name|Iterator
argument_list|<
name|Item
argument_list|>
name|iterator
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Item
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|SequenceIterator
name|delegate
init|=
name|seq
operator|.
name|iterate
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|Item
name|next
parameter_list|()
block|{
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|nextItem
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"failed to construct iterator over sequence"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Return an unmodifiable list view over the list of items. 	 * 	 * @return a list view 	 */
specifier|public
name|List
argument_list|<
name|Item
argument_list|>
name|asList
parameter_list|()
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Item
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Item
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|seq
operator|.
name|getItemCount
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** 	 * Convert this list of items to an array. 	 * 	 * @return an array of items 	 */
specifier|public
name|Item
index|[]
name|toArray
parameter_list|()
block|{
return|return
name|toArray
argument_list|(
operator|new
name|Item
index|[
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** 	 * Convert this list of items to an array.  If the given array is large enough, fill it; if it's 	 * larger than necessary, put a<code>null</code> marker after the end of the list.  If the 	 * array is not large enough, allocate a new one. 	 * 	 * @param a the array to fill with items 	 * @return an array of items 	 */
specifier|public
name|Item
index|[]
name|toArray
parameter_list|(
name|Item
index|[]
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|length
operator|<
name|size
argument_list|()
condition|)
name|a
operator|=
operator|new
name|Item
index|[
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|wrap
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|length
operator|>
name|size
argument_list|()
condition|)
name|a
index|[
name|size
argument_list|()
index|]
operator|=
literal|null
expr_stmt|;
return|return
name|a
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Item
name|item
range|:
name|this
control|)
block|{
if|if
condition|(
name|first
condition|)
name|first
operator|=
literal|false
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Return a view of this item list as a list of effective string values.  Note that no extra 	 * memory is used to present this view; effective string values are computed on demand. 	 *  	 * @return a virtual collection of string values contained in this item list 	 */
specifier|public
name|ValuesFacet
name|values
parameter_list|()
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
name|values
operator|=
operator|new
name|ValuesFacet
argument_list|()
expr_stmt|;
return|return
name|values
return|;
block|}
comment|/** 	 * Return a view of this item list as a list of nodes.  If this list contains any items that are 	 * not nodes, operations on the facet may fail.  Note that no extra memory is used to 	 * present this view. 	 * 	 * @return a virtual collection of nodes contained in this item list 	 */
specifier|public
name|NodesFacet
name|nodes
parameter_list|()
block|{
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
name|nodes
operator|=
operator|new
name|NodesFacet
argument_list|()
expr_stmt|;
return|return
name|nodes
return|;
block|}
specifier|static
specifier|final
name|ItemList
name|NULL
init|=
operator|new
name|ItemList
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|QueryService
name|query
parameter_list|()
block|{
return|return
name|QueryService
operator|.
name|NULL
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Item
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Database
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValuesFacet
name|values
parameter_list|()
block|{
return|return
operator|new
name|ValuesFacet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Database
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
comment|// toArray/0 and toArray/1 take care of themselves thanks to size()
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodesFacet
name|nodes
parameter_list|()
block|{
return|return
operator|new
name|NodesFacet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Node
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Database
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
comment|// toArray/0 and toArray/1 take care of themselves thanks to size()
block|}
return|;
block|}
annotation|@
name|Override
name|Sequence
name|convertToSequence
parameter_list|()
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

