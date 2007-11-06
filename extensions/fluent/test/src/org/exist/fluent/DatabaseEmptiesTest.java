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
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|DatabaseEmptiesTest
extends|extends
name|DatabaseTestCase
block|{
annotation|@
name|Test
specifier|public
name|void
name|hasNext
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|Database
operator|.
name|EMPTY_ITERATOR
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NoSuchElementException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|next
parameter_list|()
block|{
name|Database
operator|.
name|EMPTY_ITERATOR
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|Database
operator|.
name|EMPTY_ITERATOR
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|iterator
parameter_list|()
block|{
name|assertSame
argument_list|(
name|Database
operator|.
name|EMPTY_ITERATOR
argument_list|,
name|Database
operator|.
name|EMPTY_ITERABLE
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

