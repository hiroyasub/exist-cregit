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
name|XQueryAction
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentQueryTest
extends|extends
name|ConcurrentTestBase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
name|XmldbURI
operator|.
name|LOCAL_DB
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
name|ConcurrentQueryTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
name|File
name|tempFile
decl_stmt|;
specifier|private
name|XQueryAction
name|action0
decl_stmt|,
name|action1
decl_stmt|,
name|action2
decl_stmt|;
comment|/**      *       *       * @param name       */
specifier|public
name|ConcurrentQueryTest
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
block|{
try|try
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|String
index|[]
name|wordList
init|=
name|DBUtils
operator|.
name|wordList
argument_list|(
name|rootCol
argument_list|)
decl_stmt|;
name|tempFile
operator|=
name|DBUtils
operator|.
name|generateXMLFile
argument_list|(
literal|500
argument_list|,
literal|7
argument_list|,
name|wordList
argument_list|)
expr_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|getTestCollection
argument_list|()
argument_list|,
literal|"R1.xml"
argument_list|,
name|tempFile
argument_list|)
expr_stmt|;
name|String
name|query0
init|=
literal|"/ROOT-ELEMENT/ELEMENT/ELEMENT-1/ELEMENT-2[@attribute-3]"
decl_stmt|;
name|String
name|query1
init|=
literal|"distinct-values(//ELEMENT/@attribute-2)"
decl_stmt|;
name|String
name|query2
init|=
literal|"/ROOT-ELEMENT//ELEMENT-1[@attribute-3]"
decl_stmt|;
name|action0
operator|=
operator|new
name|XQueryAction
argument_list|(
name|URI
operator|+
literal|"/C1"
argument_list|,
literal|"R1.xml"
argument_list|,
name|query0
argument_list|)
expr_stmt|;
name|action1
operator|=
operator|new
name|XQueryAction
argument_list|(
name|URI
operator|+
literal|"/C1"
argument_list|,
literal|"R1.xml"
argument_list|,
name|query1
argument_list|)
expr_stmt|;
name|action2
operator|=
operator|new
name|XQueryAction
argument_list|(
name|URI
operator|+
literal|"/C1"
argument_list|,
literal|"R1.xml"
argument_list|,
name|query2
argument_list|)
expr_stmt|;
comment|//		action3 = new XQueryAction(URI + "/C1", "R1.xml", query0);
comment|//		action4 = new XQueryAction(URI + "/C1", "R1.xml", query0);
comment|//		action5 = new XQueryAction(URI + "/C1", "R1.xml", query0);
name|addAction
argument_list|(
name|action0
argument_list|,
literal|50
argument_list|,
literal|500
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
name|action1
argument_list|,
literal|50
argument_list|,
literal|250
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
name|action2
argument_list|,
literal|50
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//		addAction(action3, 50, 0, 0);
comment|//		addAction(action4, 50, 0, 0);
comment|//		addAction(action5, 50, 0, 0);
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

