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
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelRunner
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
annotation|@
name|RunWith
argument_list|(
name|ParallelRunner
operator|.
name|class
argument_list|)
specifier|public
specifier|abstract
class|class
name|AbstractHashtableTest
parameter_list|<
name|T
parameter_list|,
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|AbstractHashSetTest
argument_list|<
name|T
argument_list|,
name|K
argument_list|>
block|{
specifier|protected
specifier|abstract
name|void
name|simplePut
parameter_list|(
name|K
name|k
parameter_list|,
name|V
name|v
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|V
name|simpleGet
parameter_list|(
name|K
name|k
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|V
name|valEquiv
parameter_list|(
name|int
name|v
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|int
name|valEquiv
parameter_list|(
name|V
name|v
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|Iterator
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|simpleValueIterator
parameter_list|()
function_decl|;
specifier|protected
name|void
name|simpleAdd
parameter_list|(
name|K
name|k
parameter_list|)
block|{
name|simplePut
argument_list|(
name|k
argument_list|,
name|valEquiv
argument_list|(
name|keyEquiv
argument_list|(
name|k
argument_list|)
operator|^
literal|0xDEADBEEF
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|simpleContainsKey
parameter_list|(
name|K
name|k
parameter_list|)
block|{
return|return
name|simpleGet
argument_list|(
name|k
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|protected
name|void
name|simpleCheckKey
parameter_list|(
name|K
name|k
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"contains "
operator|+
name|k
argument_list|,
name|simpleContainsKey
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"check "
operator|+
name|k
argument_list|,
name|keyEquiv
argument_list|(
name|k
argument_list|)
operator|^
literal|0xDEADBEEF
argument_list|,
name|valEquiv
argument_list|(
name|simpleGet
argument_list|(
name|k
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|zeroValues
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
literal|"empty collection should have no values"
argument_list|,
name|simpleValueIterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNothing
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
literal|"empty collection should have no values"
argument_list|,
name|simpleGet
argument_list|(
name|keyEquiv
argument_list|(
literal|12345
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|valuePut
parameter_list|()
throws|throws
name|Exception
block|{
name|simplePut
argument_list|(
name|keyEquiv
argument_list|(
literal|12345
argument_list|)
argument_list|,
name|valEquiv
argument_list|(
literal|54321
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|simpleGet
argument_list|(
name|keyEquiv
argument_list|(
literal|12345
argument_list|)
argument_list|)
argument_list|,
name|valEquiv
argument_list|(
literal|54321
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|valueMultiplePut
parameter_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|simplePut
argument_list|(
name|keyEquiv
argument_list|(
name|i
operator|+
name|j
argument_list|)
argument_list|,
name|valEquiv
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
index|[]
name|ct
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|vi
init|=
name|simpleValueIterator
argument_list|()
init|;
name|vi
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|V
name|v
init|=
name|vi
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|vv
init|=
name|valEquiv
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|ct
index|[
name|vv
index|]
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|ct
index|[
literal|0
index|]
argument_list|,
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
name|assertEquals
argument_list|(
name|ct
index|[
name|i
index|]
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

