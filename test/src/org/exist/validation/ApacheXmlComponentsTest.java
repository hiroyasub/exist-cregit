begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
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

begin_comment
comment|/**  *  Class for testing XML Parser and XML Transformer configuration.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ApacheXmlComponentsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|parserVersion
parameter_list|()
block|{
name|StringBuilder
name|xmlLibMessage
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|validParser
init|=
name|XmlLibraryChecker
operator|.
name|hasValidParser
argument_list|(
name|xmlLibMessage
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|xmlLibMessage
operator|.
name|toString
argument_list|()
argument_list|,
name|validParser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|transformerVersion
parameter_list|()
block|{
name|StringBuilder
name|xmlLibMessage
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|validTransformer
init|=
name|XmlLibraryChecker
operator|.
name|hasValidTransformer
argument_list|(
name|xmlLibMessage
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|xmlLibMessage
operator|.
name|toString
argument_list|()
argument_list|,
name|validTransformer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

