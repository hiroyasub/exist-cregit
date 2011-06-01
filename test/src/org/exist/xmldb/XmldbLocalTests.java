begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
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
name|CreateCollectionsTest
operator|.
name|class
block|,
name|ResourceTest
operator|.
name|class
block|,
name|BinaryResourceUpdateTest
operator|.
name|class
block|,
comment|/* ResourceSetTest.class */
name|TestEXistXMLSerialize
operator|.
name|class
block|,
name|CopyMoveTest
operator|.
name|class
block|,
name|ContentAsDOMTest
operator|.
name|class
block|,
name|XmldbURITest
operator|.
name|class
block|,
name|CollectionConfigurationTest
operator|.
name|class
block|,
name|CollectionTest
operator|.
name|class
comment|/* MultiDBTest.class */
block|}
argument_list|)
specifier|public
class|class
name|XmldbLocalTests
block|{
specifier|public
specifier|final
specifier|static
name|String
name|ROOT_URI
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ADMIN_UID
init|=
literal|"admin"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ADMIN_PWD
init|=
literal|""
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|GUEST_UID
init|=
literal|"guest"
decl_stmt|;
specifier|public
specifier|static
name|File
name|getExistDir
parameter_list|()
block|{
specifier|final
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
return|return
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|File
name|getShakespeareSamplesDirectory
parameter_list|()
block|{
specifier|final
name|String
name|directory
init|=
literal|"samples/shakespeare"
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|getExistDir
argument_list|()
argument_list|,
name|directory
argument_list|)
return|;
block|}
block|}
end_class

end_unit

