begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-04 Wolfgang M. Meier (wolfgang@exist-db.org)  *  and others (see http://exist-db.org) * *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. * *  You should have received a copy of the GNU Lesser General Public License *  along with this program; if not, write to the Free Software *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. *  *  $Id$ */
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
operator|.
name|concurrent
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Test concurrent acess to resources.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentResourceTest
extends|extends
name|ConcurrentTestBase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist:///db"
decl_stmt|;
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
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|ConcurrentResourceTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
name|File
name|tempFile
decl_stmt|;
comment|/** 	 * @param name 	 * @param uri 	 * @param testCollection 	 */
specifier|public
name|ConcurrentResourceTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|URI
argument_list|,
literal|"C1"
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.test.concurrent.ConcurrentTestBase#setUp() 	 */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
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
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|c1
argument_list|,
literal|"R1.xml"
argument_list|,
name|ReplaceResourceAction
operator|.
name|XML
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|ReplaceResourceAction
argument_list|(
name|URI
operator|+
literal|"/C1/C1-C2"
argument_list|,
literal|"R1.xml"
argument_list|)
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|RetrieveResourceAction
argument_list|(
name|URI
operator|+
literal|"/C1/C1-C2"
argument_list|,
literal|"R1.xml"
argument_list|)
argument_list|,
literal|500
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.test.concurrent.ConcurrentTestBase#tearDown() 	 */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

