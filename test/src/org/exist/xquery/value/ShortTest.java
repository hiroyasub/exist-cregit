begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
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
name|XPathException
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

begin_comment
comment|/**  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|ShortTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XPathException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testOver
parameter_list|()
throws|throws
name|XPathException
block|{
operator|new
name|IntegerValue
argument_list|(
literal|"32768"
argument_list|,
name|Type
operator|.
name|SHORT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPositiveLimit
parameter_list|()
throws|throws
name|XPathException
block|{
operator|new
name|IntegerValue
argument_list|(
literal|"32767"
argument_list|,
name|Type
operator|.
name|SHORT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNegativeLimit
parameter_list|()
throws|throws
name|XPathException
block|{
operator|new
name|IntegerValue
argument_list|(
literal|"-32768"
argument_list|,
name|Type
operator|.
name|SHORT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XPathException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testUnder
parameter_list|()
throws|throws
name|XPathException
block|{
operator|new
name|IntegerValue
argument_list|(
literal|"-32769"
argument_list|,
name|Type
operator|.
name|SHORT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

