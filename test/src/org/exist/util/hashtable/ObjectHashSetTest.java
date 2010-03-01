begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|hashtable
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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_class
specifier|public
class|class
name|ObjectHashSetTest
extends|extends
name|AbstractHashSetTest
argument_list|<
name|ObjectHashSet
argument_list|,
name|Object
argument_list|>
block|{
specifier|protected
name|ObjectHashSet
name|newT
parameter_list|()
block|{
return|return
operator|new
name|ObjectHashSet
argument_list|()
return|;
block|}
specifier|protected
name|Object
name|keyEquiv
parameter_list|(
name|int
name|k
parameter_list|)
block|{
return|return
name|k
return|;
block|}
specifier|protected
name|boolean
name|simpleContainsKey
parameter_list|(
name|int
name|k
parameter_list|)
block|{
return|return
name|map
operator|.
name|contains
argument_list|(
name|keyEquiv
argument_list|(
name|k
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|int
name|keyEquiv
parameter_list|(
name|Object
name|k
parameter_list|)
block|{
return|return
operator|(
name|Integer
operator|)
name|k
return|;
block|}
specifier|protected
name|Integer
name|keyEquiv_newObject
parameter_list|(
name|int
name|k
parameter_list|)
block|{
return|return
operator|new
name|Integer
argument_list|(
name|k
argument_list|)
return|;
block|}
specifier|protected
name|void
name|simpleAdd
parameter_list|(
name|Object
name|k
parameter_list|)
block|{
name|map
operator|.
name|add
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|simpleRemove
parameter_list|(
name|Object
name|k
parameter_list|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|Iterator
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|simpleKeyIterator
parameter_list|()
block|{
return|return
name|map
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

