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
name|security
operator|.
name|Permission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|assertFalse
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
comment|/**  *  * @author<a href="mailto:adam@existsolutions.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|UnixStylePermissionAiderTest
block|{
specifier|public
class|class
name|SecurityTestPair
block|{
specifier|public
name|SecurityTestPair
parameter_list|(
specifier|final
name|String
name|permissionString
parameter_list|,
specifier|final
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
name|setUid_roundtrip
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|Permission
name|permission
init|=
operator|new
name|UnixStylePermissionAider
argument_list|(
literal|0555
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|permission
operator|.
name|isSetUid
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|.
name|setSetUid
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permission
operator|.
name|isSetUid
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|04555
argument_list|,
name|permission
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|=
operator|new
name|UnixStylePermissionAider
argument_list|(
literal|04555
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permission
operator|.
name|isSetUid
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|.
name|setSetUid
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permission
operator|.
name|isSetUid
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0555
argument_list|,
name|permission
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setGid_roundtrip
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|Permission
name|permission
init|=
operator|new
name|UnixStylePermissionAider
argument_list|(
literal|0555
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|permission
operator|.
name|isSetGid
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|.
name|setSetGid
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permission
operator|.
name|isSetGid
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|02555
argument_list|,
name|permission
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|=
operator|new
name|UnixStylePermissionAider
argument_list|(
literal|02555
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permission
operator|.
name|isSetGid
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|.
name|setSetGid
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permission
operator|.
name|isSetGid
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0555
argument_list|,
name|permission
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setSticky_roundtrip
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|Permission
name|permission
init|=
operator|new
name|UnixStylePermissionAider
argument_list|(
literal|0555
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|permission
operator|.
name|isSticky
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|.
name|setSticky
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permission
operator|.
name|isSticky
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|01555
argument_list|,
name|permission
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|=
operator|new
name|UnixStylePermissionAider
argument_list|(
literal|01555
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|permission
operator|.
name|isSticky
argument_list|()
argument_list|)
expr_stmt|;
name|permission
operator|.
name|setSticky
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|permission
operator|.
name|isSticky
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0555
argument_list|,
name|permission
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fromString_toString
parameter_list|()
throws|throws
name|SyntaxException
block|{
specifier|final
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
literal|"rwxrwxrwx"
argument_list|,
literal|0777
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
literal|"rwxrwx---"
argument_list|,
literal|0770
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
literal|"rwx------"
argument_list|,
literal|0700
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
literal|"------rwx"
argument_list|,
literal|07
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
literal|"---rwxrwx"
argument_list|,
literal|077
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
literal|0444
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
literal|"rwxr--r--"
argument_list|,
literal|0744
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
literal|"rwxrw-rw-"
argument_list|,
literal|0766
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
literal|"rwxr-xr-x"
argument_list|,
literal|0755
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
literal|"--s------"
argument_list|,
literal|04100
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
literal|"--S------"
argument_list|,
literal|04000
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
literal|"-----s---"
argument_list|,
literal|02010
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
literal|"-----S---"
argument_list|,
literal|02000
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
literal|"--------t"
argument_list|,
literal|01001
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
literal|"--------T"
argument_list|,
literal|01000
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|SecurityTestPair
name|sec
range|:
name|securityTestPairs
control|)
block|{
specifier|final
name|UnixStylePermissionAider
name|perm
init|=
name|UnixStylePermissionAider
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
name|getMode
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
name|UnixStylePermissionAider
operator|.
name|fromString
argument_list|(
literal|"rwx"
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
name|UnixStylePermissionAider
operator|.
name|fromString
argument_list|(
literal|"rwurwurwu"
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
end_class

end_unit

