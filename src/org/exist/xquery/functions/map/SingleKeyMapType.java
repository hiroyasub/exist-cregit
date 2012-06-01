begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|map
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Constants
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
name|XQueryContext
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
name|AtomicValue
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
name|Sequence
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|SingleKeyMapType
extends|extends
name|AbstractMapType
block|{
specifier|private
name|AtomicValue
name|key
decl_stmt|;
specifier|private
name|Sequence
name|value
decl_stmt|;
specifier|private
name|Comparator
argument_list|<
name|AtomicValue
argument_list|>
name|comparator
decl_stmt|;
specifier|public
name|SingleKeyMapType
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|collation
parameter_list|,
name|AtomicValue
name|key
parameter_list|,
name|Sequence
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|getComparator
argument_list|(
name|collation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|key
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|get
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
block|{
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|this
operator|.
name|key
argument_list|,
name|key
argument_list|)
operator|==
name|Constants
operator|.
name|EQUAL
condition|)
return|return
name|this
operator|.
name|value
return|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
block|{
return|return
operator|(
name|comparator
operator|.
name|compare
argument_list|(
name|this
operator|.
name|key
argument_list|,
name|key
argument_list|)
operator|==
name|Constants
operator|.
name|EQUAL
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|keys
parameter_list|()
block|{
return|return
name|key
operator|==
literal|null
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
name|key
return|;
block|}
annotation|@
name|Override
specifier|public
name|AbstractMapType
name|remove
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|MapType
argument_list|(
name|context
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getItemCount
parameter_list|()
block|{
return|return
name|key
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|AtomicValue
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
