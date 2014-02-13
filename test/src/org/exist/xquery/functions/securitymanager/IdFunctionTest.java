begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2014 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

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
name|securitymanager
package|;
end_package

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|SimpleNamespaceContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XpathEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|exceptions
operator|.
name|XpathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|EasyMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|createMockBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|verify
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
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
name|Subject
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
name|Sequence
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
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|IdFunctionTest
block|{
comment|/**      * Test of eval method, of class IdFunction.      * when real and effective users are different      */
annotation|@
name|Test
specifier|public
name|void
name|differingRealAndEffectiveUsers
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XpathException
block|{
specifier|final
name|XQueryContext
name|mckContext
init|=
name|createMockBuilder
argument_list|(
name|XQueryContext
operator|.
name|class
argument_list|)
operator|.
name|addMockedMethod
argument_list|(
literal|"getRealUser"
argument_list|)
operator|.
name|addMockedMethod
argument_list|(
literal|"getEffectiveUser"
argument_list|)
operator|.
name|createMock
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|mckRealUser
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|realUsername
init|=
literal|"real"
decl_stmt|;
name|expect
argument_list|(
name|mckContext
operator|.
name|getRealUser
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mckRealUser
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckRealUser
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|realUsername
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckRealUser
operator|.
name|getGroups
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"realGroup1"
block|,
literal|"realGroup2"
block|}
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckRealUser
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Subject
name|mckEffectiveUser
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|effectiveUsername
init|=
literal|"effective"
decl_stmt|;
name|expect
argument_list|(
name|mckContext
operator|.
name|getEffectiveUser
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mckEffectiveUser
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckEffectiveUser
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckEffectiveUser
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|effectiveUsername
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckEffectiveUser
operator|.
name|getGroups
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"effectiveGroup1"
block|,
literal|"effectiveGroup2"
block|}
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mckEffectiveUser
argument_list|,
name|mckRealUser
argument_list|,
name|mckContext
argument_list|)
expr_stmt|;
specifier|final
name|IdFunction
name|idFunctions
init|=
operator|new
name|IdFunction
argument_list|(
name|mckContext
argument_list|,
name|IdFunction
operator|.
name|FNS_ID
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|idFunctions
operator|.
name|eval
argument_list|(
operator|new
name|Sequence
index|[]
block|{
name|Sequence
operator|.
name|EMPTY_SEQUENCE
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|XpathEngine
name|xpathEngine
init|=
name|XMLUnit
operator|.
name|newXpathEngine
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|namespaces
operator|.
name|put
argument_list|(
literal|"sm"
argument_list|,
literal|"http://exist-db.org/xquery/securitymanager"
argument_list|)
expr_stmt|;
name|xpathEngine
operator|.
name|setNamespaceContext
argument_list|(
operator|new
name|SimpleNamespaceContext
argument_list|(
name|namespaces
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DocumentImpl
name|resultDoc
init|=
operator|(
name|DocumentImpl
operator|)
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|NodeList
name|nlRealUsername
init|=
name|xpathEngine
operator|.
name|getMatchingNodes
argument_list|(
literal|"/sm:id/sm:real/sm:username"
argument_list|,
name|resultDoc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nlRealUsername
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|realUsername
argument_list|,
name|nlRealUsername
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|nlEffectiveUsername
init|=
name|xpathEngine
operator|.
name|getMatchingNodes
argument_list|(
literal|"/sm:id/sm:effective/sm:username"
argument_list|,
name|resultDoc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nlEffectiveUsername
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|effectiveUsername
argument_list|,
name|nlEffectiveUsername
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mckEffectiveUser
argument_list|,
name|mckRealUser
argument_list|,
name|mckContext
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test of eval method, of class IdFunction.      * when real and effective users are the same      */
annotation|@
name|Test
specifier|public
name|void
name|sameRealAndEffectiveUsers
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XpathException
block|{
specifier|final
name|XQueryContext
name|mckContext
init|=
name|createMockBuilder
argument_list|(
name|XQueryContext
operator|.
name|class
argument_list|)
operator|.
name|addMockedMethod
argument_list|(
literal|"getRealUser"
argument_list|)
operator|.
name|addMockedMethod
argument_list|(
literal|"getEffectiveUser"
argument_list|)
operator|.
name|createMock
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|mckUser
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|username
init|=
literal|"user1"
decl_stmt|;
name|expect
argument_list|(
name|mckContext
operator|.
name|getRealUser
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mckUser
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckUser
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckUser
operator|.
name|getGroups
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"group1"
block|,
literal|"group2"
block|}
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckUser
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckContext
operator|.
name|getEffectiveUser
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mckUser
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mckUser
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mckUser
argument_list|,
name|mckContext
argument_list|)
expr_stmt|;
specifier|final
name|IdFunction
name|idFunctions
init|=
operator|new
name|IdFunction
argument_list|(
name|mckContext
argument_list|,
name|IdFunction
operator|.
name|FNS_ID
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|idFunctions
operator|.
name|eval
argument_list|(
operator|new
name|Sequence
index|[]
block|{
name|Sequence
operator|.
name|EMPTY_SEQUENCE
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|XpathEngine
name|xpathEngine
init|=
name|XMLUnit
operator|.
name|newXpathEngine
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|namespaces
operator|.
name|put
argument_list|(
literal|"sm"
argument_list|,
literal|"http://exist-db.org/xquery/securitymanager"
argument_list|)
expr_stmt|;
name|xpathEngine
operator|.
name|setNamespaceContext
argument_list|(
operator|new
name|SimpleNamespaceContext
argument_list|(
name|namespaces
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DocumentImpl
name|resultDoc
init|=
operator|(
name|DocumentImpl
operator|)
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|NodeList
name|nlRealUsername
init|=
name|xpathEngine
operator|.
name|getMatchingNodes
argument_list|(
literal|"/sm:id/sm:real/sm:username"
argument_list|,
name|resultDoc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nlRealUsername
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|username
argument_list|,
name|nlRealUsername
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|nlEffectiveUsername
init|=
name|xpathEngine
operator|.
name|getMatchingNodes
argument_list|(
literal|"/sm:id/sm:effective/sm:username"
argument_list|,
name|resultDoc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nlEffectiveUsername
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mckUser
argument_list|,
name|mckContext
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

