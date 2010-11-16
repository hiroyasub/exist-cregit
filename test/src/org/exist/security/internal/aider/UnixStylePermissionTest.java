begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
operator|.
name|aider
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|SyntaxException
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|UnixStylePermissionTest
block|{
specifier|public
class|class
name|SecurityTestPair
block|{
specifier|public
name|SecurityTestPair
parameter_list|(
name|String
name|permissionString
parameter_list|,
name|int
name|permission
parameter_list|)
block|{
name|this
operator|.
name|permissionString
operator|=
name|permissionString
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
block|}
specifier|public
name|String
name|permissionString
decl_stmt|;
specifier|public
name|int
name|permission
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fromString
parameter_list|()
throws|throws
name|SyntaxException
block|{
name|List
argument_list|<
name|SecurityTestPair
argument_list|>
name|securityTestPairs
init|=
operator|new
name|ArrayList
argument_list|<
name|SecurityTestPair
argument_list|>
argument_list|()
decl_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"rwurwurwu"
argument_list|,
literal|511
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"rwurwu---"
argument_list|,
literal|504
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"rwu------"
argument_list|,
literal|448
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"------rwu"
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"---rwurwu"
argument_list|,
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"r--r--r--"
argument_list|,
literal|292
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"rwur--r--"
argument_list|,
literal|484
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"rwur--r--"
argument_list|,
literal|484
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"a-----------"
argument_list|,
literal|2048
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"-g----------"
argument_list|,
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|securityTestPairs
operator|.
name|add
argument_list|(
operator|new
name|SecurityTestPair
argument_list|(
literal|"--s---------"
argument_list|,
literal|512
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SecurityTestPair
name|sec
range|:
name|securityTestPairs
control|)
block|{
name|UnixStylePermission
name|perm
init|=
name|UnixStylePermission
operator|.
name|fromString
argument_list|(
name|sec
operator|.
name|permissionString
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sec
operator|.
name|permission
argument_list|,
name|perm
operator|.
name|getPermissions
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sec
operator|.
name|permissionString
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SyntaxException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|fromStringInvalidSyntax_tooShort
parameter_list|()
throws|throws
name|SyntaxException
block|{
name|UnixStylePermission
operator|.
name|fromString
argument_list|(
literal|"rwu"
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SyntaxException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|fromStringInvalidSyntax_invalidChars
parameter_list|()
throws|throws
name|SyntaxException
block|{
name|UnixStylePermission
operator|.
name|fromString
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
end_class

end_unit

