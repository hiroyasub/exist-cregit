begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
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

begin_comment
comment|/**  * OrderedLongLinkedList.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|OrderedLongLinkedList
extends|extends
name|LongLinkedList
block|{
comment|/** 	 * Constructor for OrderedLongLinkedList. 	 */
specifier|public
name|OrderedLongLinkedList
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @see org.exist.util.LongLinkedList#add(long) 	 */
specifier|public
name|void
name|add
parameter_list|(
name|long
name|l
parameter_list|)
block|{
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|createListItem
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|last
operator|=
name|first
expr_stmt|;
name|count
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|ListItem
name|newItem
init|=
name|createListItem
argument_list|(
name|l
argument_list|)
decl_stmt|;
name|ListItem
name|prev
init|=
name|last
decl_stmt|;
while|while
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|newItem
operator|.
name|compareTo
argument_list|(
name|prev
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|newItem
operator|.
name|prev
operator|=
name|prev
expr_stmt|;
name|newItem
operator|.
name|next
operator|=
name|prev
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|prev
operator|==
name|last
condition|)
name|last
operator|=
name|newItem
expr_stmt|;
else|else
name|newItem
operator|.
name|next
operator|.
name|prev
operator|=
name|newItem
expr_stmt|;
name|prev
operator|.
name|next
operator|=
name|newItem
expr_stmt|;
operator|++
name|count
expr_stmt|;
return|return;
block|}
name|prev
operator|=
name|prev
operator|.
name|prev
expr_stmt|;
block|}
comment|// insert as first item
name|first
operator|.
name|prev
operator|=
name|newItem
expr_stmt|;
name|newItem
operator|.
name|next
operator|=
name|first
expr_stmt|;
name|first
operator|=
name|newItem
expr_stmt|;
operator|++
name|count
expr_stmt|;
block|}
block|}
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
name|OrderedLongLinkedList
name|list
init|=
operator|new
name|OrderedLongLinkedList
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|44
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|-
literal|43
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|-
literal|122
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"size: "
operator|+
name|list
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|list
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
specifier|final
name|OrderedLongLinkedList
operator|.
name|ListItem
name|item
init|=
operator|(
name|OrderedLongLinkedList
operator|.
name|ListItem
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|item
operator|.
name|l
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

