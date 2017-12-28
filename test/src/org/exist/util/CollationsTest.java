begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Collations
operator|.
name|HTML_ASCII_CASE_INSENSITIVE_COLLATION_URI
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

begin_class
specifier|public
class|class
name|CollationsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|htmlAscii_contains
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|Collator
name|collator
init|=
name|Collations
operator|.
name|getCollationFromURI
argument_list|(
name|HTML_ASCII_CASE_INSENSITIVE_COLLATION_URI
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Collations
operator|.
name|contains
argument_list|(
name|collator
argument_list|,
literal|"iNPut"
argument_list|,
literal|"pu"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Collations
operator|.
name|contains
argument_list|(
name|collator
argument_list|,
literal|"iNPut"
argument_list|,
literal|"PU"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Collations
operator|.
name|contains
argument_list|(
name|collator
argument_list|,
literal|"h&#244;tel"
argument_list|,
literal|"h&#244;t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Collations
operator|.
name|contains
argument_list|(
name|collator
argument_list|,
literal|"h&#244;tel"
argument_list|,
literal|"H&#212;T"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

