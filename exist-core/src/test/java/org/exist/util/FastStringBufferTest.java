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
name|FastStringBufferTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|insertCharAt_begin
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|insertCharAt
argument_list|(
literal|0
argument_list|,
literal|'#'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"#12345"
argument_list|,
name|fsb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertCharAt_middle
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|insertCharAt
argument_list|(
literal|3
argument_list|,
literal|'#'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123#45"
argument_list|,
name|fsb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertCharAt_end
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|insertCharAt
argument_list|(
literal|5
argument_list|,
literal|'#'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"12345#"
argument_list|,
name|fsb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|insertCharAt_IOOB_front
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|insertCharAt
argument_list|(
operator|-
literal|1
argument_list|,
literal|'#'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|insertCharAt_IOOB_end
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|insertCharAt
argument_list|(
literal|6
argument_list|,
literal|'#'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeCharAt_front
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|removeCharAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2345"
argument_list|,
name|fsb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeCharAt_middle
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|removeCharAt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1245"
argument_list|,
name|fsb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeCharAt_end
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|removeCharAt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1234"
argument_list|,
name|fsb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|removeCharAt_IOOB_front
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|removeCharAt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|removeCharAt_IOOB_end
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fsb
operator|.
name|append
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|fsb
operator|.
name|removeCharAt
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertCharAt_capacity
parameter_list|()
block|{
name|FastStringBuffer
name|fsb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fsb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

