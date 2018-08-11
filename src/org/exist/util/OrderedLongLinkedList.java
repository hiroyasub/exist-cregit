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
annotation|@
name|Override
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
block|{
return|return;
block|}
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
block|{
name|last
operator|=
name|newItem
expr_stmt|;
block|}
else|else
block|{
name|newItem
operator|.
name|next
operator|.
name|prev
operator|=
name|newItem
expr_stmt|;
block|}
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
block|}
end_class

end_unit

