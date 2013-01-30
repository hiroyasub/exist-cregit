begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2013 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractApiSecurityTest
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_COLLECTION1_NAME
init|=
literal|"securityTest1"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_COLLECTION1
init|=
literal|"/db/"
operator|+
name|TEST_COLLECTION1_NAME
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_XML_DOC1_NAME
init|=
literal|"test.xml"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_XML_DOC1
init|=
name|TEST_COLLECTION1
operator|+
literal|"/"
operator|+
name|TEST_XML_DOC1_NAME
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_XML_DOC1_CONTENT
init|=
literal|"<test/>"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_BIN_DOC1_NAME
init|=
literal|"test.bin"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_BIN_DOC1
init|=
name|TEST_COLLECTION1
operator|+
literal|"/"
operator|+
name|TEST_BIN_DOC1_NAME
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|byte
index|[]
name|TEST_BIN_DOC1_CONTENT
init|=
literal|"binary-test"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|ADMIN_UID
init|=
literal|"admin"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|ADMIN_PWD
init|=
literal|""
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_USER1_UID
init|=
literal|"test1"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_USER1_PWD
init|=
name|TEST_USER1_UID
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_USER2_UID
init|=
literal|"test2"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_USER2_PWD
init|=
name|TEST_USER2_UID
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_GROUP_GID
init|=
literal|"group1"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_GROUP_PWD
init|=
name|TEST_GROUP_GID
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|canReadXmlResourceWithOnlyExecutePermissionOnParentCollection
parameter_list|()
throws|throws
name|ApiException
block|{
name|chmodCol
argument_list|(
name|TEST_COLLECTION1
argument_list|,
literal|"--x------"
argument_list|,
name|TEST_USER1_UID
argument_list|,
name|TEST_USER1_PWD
argument_list|)
expr_stmt|;
specifier|final
name|String
name|content
init|=
name|getXmlResourceContent
argument_list|(
name|TEST_XML_DOC1
argument_list|,
name|TEST_USER1_UID
argument_list|,
name|TEST_USER1_PWD
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_XML_DOC1_CONTENT
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cannotReadXmlResourceWithoutExecutePermissionOnParentCollection
parameter_list|()
throws|throws
name|ApiException
block|{
name|chmodCol
argument_list|(
name|TEST_COLLECTION1
argument_list|,
literal|"rw-------"
argument_list|,
name|TEST_USER1_UID
argument_list|,
name|TEST_USER1_PWD
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|String
name|content
init|=
name|getXmlResourceContent
argument_list|(
name|TEST_XML_DOC1
argument_list|,
name|TEST_USER1_UID
argument_list|,
name|TEST_USER1_PWD
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Excpected READ collection denied!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ApiException
name|ae
parameter_list|)
block|{
comment|//do nothing<-- expected exception
block|}
block|}
specifier|protected
specifier|abstract
name|void
name|createCol
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|removeCol
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|chownCol
parameter_list|(
name|String
name|collectionUri
parameter_list|,
name|String
name|owner_uid
parameter_list|,
name|String
name|group_gid
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|chmodCol
parameter_list|(
name|String
name|collectionUri
parameter_list|,
name|String
name|mode
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|chmodRes
parameter_list|(
name|String
name|resourceUri
parameter_list|,
name|String
name|mode
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|chownRes
parameter_list|(
name|String
name|resourceUri
parameter_list|,
name|String
name|owner_uid
parameter_list|,
name|String
name|group_gid
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|String
name|getXmlResourceContent
parameter_list|(
name|String
name|resourceUri
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|removeAccount
parameter_list|(
name|String
name|account_uid
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|removeGroup
parameter_list|(
name|String
name|group_gid
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|createAccount
parameter_list|(
name|String
name|account_uid
parameter_list|,
name|String
name|account_pwd
parameter_list|,
name|String
name|group_uid
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|createGroup
parameter_list|(
name|String
name|group_gid
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|createXmlResource
parameter_list|(
name|String
name|resourceUri
parameter_list|,
name|String
name|content
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|createBinResource
parameter_list|(
name|String
name|resourceUri
parameter_list|,
name|byte
index|[]
name|content
parameter_list|,
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
function_decl|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|ApiException
block|{
name|chmodCol
argument_list|(
literal|"/db"
argument_list|,
literal|"rwxr-xr-x"
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
comment|//ensure /db is always 755
name|removeAccount
argument_list|(
name|TEST_USER1_UID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|removeAccount
argument_list|(
name|TEST_USER2_UID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|removeGroup
argument_list|(
name|TEST_GROUP_GID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|createGroup
argument_list|(
name|TEST_GROUP_GID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|createAccount
argument_list|(
name|TEST_USER1_UID
argument_list|,
name|TEST_USER1_PWD
argument_list|,
name|TEST_GROUP_GID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|createAccount
argument_list|(
name|TEST_USER2_UID
argument_list|,
name|TEST_USER2_PWD
argument_list|,
name|TEST_GROUP_GID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
comment|// create a collection /db/securityTest as user "test1"
name|createCol
argument_list|(
name|TEST_COLLECTION1_NAME
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
comment|// pass ownership to test1
name|chownCol
argument_list|(
name|TEST_COLLECTION1
argument_list|,
name|TEST_USER1_UID
argument_list|,
name|TEST_GROUP_GID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|chmodCol
argument_list|(
name|TEST_COLLECTION1
argument_list|,
literal|"rwxrwx---"
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|createXmlResource
argument_list|(
name|TEST_XML_DOC1
argument_list|,
name|TEST_XML_DOC1_CONTENT
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|chmodRes
argument_list|(
name|TEST_XML_DOC1
argument_list|,
literal|"rwxrwx---"
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|chownRes
argument_list|(
name|TEST_XML_DOC1
argument_list|,
name|TEST_USER1_UID
argument_list|,
name|TEST_GROUP_GID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|createBinResource
argument_list|(
name|TEST_BIN_DOC1
argument_list|,
name|TEST_BIN_DOC1_CONTENT
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|chmodRes
argument_list|(
name|TEST_BIN_DOC1
argument_list|,
literal|"rwxrwx---"
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|chownRes
argument_list|(
name|TEST_BIN_DOC1
argument_list|,
name|TEST_USER1_UID
argument_list|,
name|TEST_GROUP_GID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|ApiException
block|{
name|removeCol
argument_list|(
name|TEST_COLLECTION1_NAME
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|removeAccount
argument_list|(
name|TEST_USER1_UID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|removeAccount
argument_list|(
name|TEST_USER2_UID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
name|removeGroup
argument_list|(
name|TEST_GROUP_GID
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getCollectionUri
parameter_list|(
name|String
name|resourceUri
parameter_list|)
block|{
return|return
name|resourceUri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|resourceUri
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|String
name|getResourceName
parameter_list|(
name|String
name|resourceUri
parameter_list|)
block|{
return|return
name|resourceUri
operator|.
name|substring
argument_list|(
name|resourceUri
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

