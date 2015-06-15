begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|XQueryUseCasesTest
block|{
specifier|protected
name|XQueryUseCase
name|useCase
init|=
operator|new
name|XQueryUseCase
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|useCase
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|// jmv: to activate when we'll have function deep-equal()
comment|//	public void testXMP() throws Exception {
comment|//		useCase.doTest("xmp");
comment|//	}
annotation|@
name|Test
specifier|public
name|void
name|sgml
parameter_list|()
throws|throws
name|Exception
block|{
name|useCase
operator|.
name|doTest
argument_list|(
literal|"sgml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|tree
parameter_list|()
throws|throws
name|Exception
block|{
name|useCase
operator|.
name|doTest
argument_list|(
literal|"tree"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|parts
parameter_list|()
throws|throws
name|Exception
block|{
name|useCase
operator|.
name|doTest
argument_list|(
literal|"parts"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|string
parameter_list|()
throws|throws
name|Exception
block|{
name|useCase
operator|.
name|doTest
argument_list|(
literal|"string"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ns
parameter_list|()
throws|throws
name|Exception
block|{
name|useCase
operator|.
name|doTest
argument_list|(
literal|"ns"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|seq
parameter_list|()
throws|throws
name|Exception
block|{
name|useCase
operator|.
name|doTest
argument_list|(
literal|"seq"
argument_list|)
expr_stmt|;
block|}
comment|// jmv: to activate when implemented
comment|// org.xmldb.api.base.XMLDBException: Cannot query constructed nodes.
comment|//	@Test
comment|//	public void r() throws Exception {
comment|//		useCase.doTest("r");
comment|//	}
block|}
end_class

end_unit

