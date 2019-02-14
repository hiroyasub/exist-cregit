begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2007 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id: AllIndexTests.java 11737 2010-05-02 21:25:21Z ixitar $  *    *  @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|spatial
package|;
end_package

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
name|GMLIndexTest
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|AllSpatialTests
block|{ }
end_class

end_unit

