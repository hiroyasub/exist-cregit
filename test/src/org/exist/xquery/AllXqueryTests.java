begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|AnyURITest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Suite
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Suite
operator|.
name|class
argument_list|)
annotation|@
name|Suite
operator|.
name|SuiteClasses
argument_list|(
block|{
name|XQueryFunctionsTest
operator|.
name|class
block|,
name|JavaFunctionsTest
operator|.
name|class
block|,
name|XPathQueryTest
operator|.
name|class
block|,
name|XQueryTest
operator|.
name|class
block|,
name|EntitiesTest
operator|.
name|class
block|,
name|SpecialNamesTest
operator|.
name|class
block|,
name|ValueIndexTest
operator|.
name|class
block|,
name|LexerTest
operator|.
name|class
block|,
name|DeepEqualTest
operator|.
name|class
block|,
name|SeqOpTest
operator|.
name|class
block|,
name|XMLNodeAsXQueryParameterTest
operator|.
name|class
block|,
name|OpNumericTest
operator|.
name|class
block|,
name|DocumentUpdateTest
operator|.
name|class
block|,
name|AnyURITest
operator|.
name|class
block|,
name|XQueryGroupByTest
operator|.
name|class
block|,
name|ConstructedNodesTest
operator|.
name|class
block|,
name|ConstructedNodesRecoveryTest
operator|.
name|class
block|,
name|DuplicateAttributesTest
operator|.
name|class
block|,
name|StoredModuleTest
operator|.
name|class
block|,
name|TransformTest
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|AllXqueryTests
block|{ }
end_class

end_unit

