begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
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
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|RemoteCollection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_comment
comment|/**  * @author Sebastian Bossung, Technische Universitaet Hamburg-Harburg  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RemoteDBTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://localhost:8088/xmlrpc"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COLLECTION_NAME
init|=
literal|"unit-testing-collection-CittÃ "
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DB_DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
name|RemoteCollection
name|collection
init|=
literal|null
decl_stmt|;
comment|/**      * @param name      */
specifier|public
name|RemoteDBTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws Exception      * @throws ClassNotFoundException      * @throws InstantiationException      * @throws IllegalAccessException      * @throws XMLDBException      */
specifier|protected
name|void
name|setUpRemoteDatabase
parameter_list|()
throws|throws
name|Exception
throws|,
name|ClassNotFoundException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
throws|,
name|XMLDBException
block|{
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DB_DRIVER
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Collection
name|rootCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/db"
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Collection
name|childCollection
init|=
name|rootCollection
operator|.
name|getChildCollection
argument_list|(
name|COLLECTION_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|childCollection
operator|==
literal|null
condition|)
block|{
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|setCollection
argument_list|(
operator|(
name|RemoteCollection
operator|)
name|cms
operator|.
name|createCollection
argument_list|(
name|COLLECTION_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Cannot run test because the collection /db/"
operator|+
name|COLLECTION_NAME
operator|+
literal|" already "
operator|+
literal|"exists. If it is a left-over of a previous test run, please remove it manually."
argument_list|)
throw|;
block|}
block|}
comment|/**      * @throws XMLDBException      * @throws Exception      */
specifier|protected
name|void
name|removeCollection
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|Exception
block|{
name|Collection
name|rootCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/db"
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|cms
operator|.
name|removeCollection
argument_list|(
name|COLLECTION_NAME
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the collection.      */
specifier|public
name|RemoteCollection
name|getCollection
parameter_list|()
block|{
return|return
name|collection
return|;
block|}
comment|/**      * @param collection      *                   The collection to set.      */
specifier|public
name|void
name|setCollection
parameter_list|(
name|RemoteCollection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
specifier|protected
name|String
name|getTestCollectionName
parameter_list|()
block|{
return|return
name|COLLECTION_NAME
return|;
block|}
block|}
end_class

end_unit

