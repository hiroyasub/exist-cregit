begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
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
name|TestCase
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|Validator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|XmlLibraryChecker
import|;
end_import

begin_comment
comment|/**  *  Class for testing xerces and xalan configuration.  *  * @author dizzzz  */
end_comment

begin_class
specifier|public
class|class
name|ApacheXmlComponentsTest
extends|extends
name|TestCase
block|{
specifier|public
name|ApacheXmlComponentsTest
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|super
argument_list|(
name|testName
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
name|ApacheXmlComponentsTest
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|suite
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tearDown"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"setUp"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testXercesVersion
parameter_list|()
block|{
name|String
name|version
init|=
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|impl
operator|.
name|Version
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Xerces"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Required version '"
operator|+
name|XmlLibraryChecker
operator|.
name|XERCESVERSION
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found version '"
operator|+
name|version
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Incorrect Xerces version! "
operator|+
literal|"Please put correct jar in endorsed folder"
argument_list|,
name|XmlLibraryChecker
operator|.
name|isXercesVersionOK
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testXalanVersion
parameter_list|()
block|{
name|String
name|version
init|=
name|org
operator|.
name|apache
operator|.
name|xalan
operator|.
name|Version
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Xalan"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Required version '"
operator|+
name|XmlLibraryChecker
operator|.
name|XALANVERSION
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found version '"
operator|+
name|version
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Incorrect Xalan version! "
operator|+
literal|"Please put correct jar in endorsed folder"
argument_list|,
name|XmlLibraryChecker
operator|.
name|isXalanVersionOK
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

