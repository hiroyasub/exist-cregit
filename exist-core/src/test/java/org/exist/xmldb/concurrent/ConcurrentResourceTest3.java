begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|concurrent
operator|.
name|action
operator|.
name|MultiResourcesAction
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
name|concurrent
operator|.
name|action
operator|.
name|XQueryAction
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentResourceTest3
extends|extends
name|ConcurrentTestBase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|FILES_DIR
init|=
literal|"/home/wolf/xml/movies"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUERY0
init|=
literal|"collection('"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"')/movie"
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
specifier|final
name|Collection
name|c1
init|=
name|DBUtils
operator|.
name|addCollection
argument_list|(
name|getTestCollection
argument_list|()
argument_list|,
literal|"C1-C2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTestCollectionName
parameter_list|()
block|{
return|return
literal|"C1"
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Runner
argument_list|>
name|getRunners
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Runner
argument_list|(
operator|new
name|MultiResourcesAction
argument_list|(
name|FILES_DIR
argument_list|,
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/C1/C1-C2"
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|Runner
argument_list|(
operator|new
name|XQueryAction
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/C1/C1-C2"
argument_list|,
literal|"R1.xml"
argument_list|,
name|QUERY0
argument_list|)
argument_list|,
literal|1500
argument_list|,
literal|200
argument_list|,
literal|100
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

