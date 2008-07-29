begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|RemoveCollectionTest
operator|.
name|class
block|,
name|ReindexTest
operator|.
name|class
block|,
name|ShutdownTest
operator|.
name|class
block|,
name|CollectionTest
operator|.
name|class
block|,
name|CopyResourceTest
operator|.
name|class
block|,
name|MoveResourceTest
operator|.
name|class
block|,
name|CopyCollectionTest
operator|.
name|class
block|,
name|RecoverBinaryTest
operator|.
name|class
block|,
name|RecoverBinaryTest2
operator|.
name|class
block|,
name|RecoveryTest
operator|.
name|class
block|,
name|AppendTest
operator|.
name|class
block|,
name|RemoveTest
operator|.
name|class
block|,
name|RenameTest
operator|.
name|class
block|,
name|ReplaceTest
operator|.
name|class
block|,
name|UpdateTest
operator|.
name|class
block|,
name|UpdateAttributeTest
operator|.
name|class
block|,
name|UpdateRecoverTest
operator|.
name|class
block|,
name|ResourceTest
operator|.
name|class
block|,
name|RangeIndexUpdateTest
operator|.
name|class
block|,
name|LargeValuesTest
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|AllStorageTests
block|{ }
end_class

end_unit

