begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|numbering
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|DLNBaseTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|DLNByteArrayConstructor_roundTrip
parameter_list|()
block|{
specifier|final
name|DLNBase
name|dlnBase
init|=
operator|new
name|DLNBase
argument_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|dlnBase
operator|.
name|incrementLevelId
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
for|for
control|(
specifier|final
name|int
name|levelId
range|:
name|dlnBase
operator|.
name|getLevelIds
argument_list|()
control|)
block|{
name|dlnBase
operator|.
name|addLevelId
argument_list|(
name|levelId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|dlnBase
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|dlnBase
operator|.
name|serialize
argument_list|(
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|DLN
name|reconstructedDln
init|=
operator|new
name|DLN
argument_list|(
name|dlnBase
operator|.
name|units
argument_list|()
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dlnBase
operator|.
name|equals
argument_list|(
name|reconstructedDln
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

