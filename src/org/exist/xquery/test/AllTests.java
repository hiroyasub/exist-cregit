begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|test
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|AllTests
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|swingui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|AllTests
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
name|TestSuite
name|suite
init|=
operator|new
name|TestSuite
argument_list|(
literal|"Test for org.exist.xquery.test"
argument_list|)
decl_stmt|;
comment|//$JUnit-BEGIN$
name|suite
operator|.
name|addTestSuite
argument_list|(
name|XPathQueryTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|LexerTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// jmv: Note: LexerTest needs /db/test created by XPathQueryTest
name|suite
operator|.
name|addTestSuite
argument_list|(
name|DeepEqualTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//		suite.addTestSuite(XQueryUseCasesTest.class);
comment|//$JUnit-END$
return|return
name|suite
return|;
block|}
block|}
end_class

end_unit

