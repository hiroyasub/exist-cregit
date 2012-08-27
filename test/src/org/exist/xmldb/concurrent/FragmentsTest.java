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
name|CreateCollectionAction
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

begin_class
specifier|public
class|class
name|FragmentsTest
extends|extends
name|ConcurrentTestBase
block|{
comment|/**      * @param args      */
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
name|FragmentsTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// jetty.port.jetty
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://localhost:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.port"
argument_list|)
operator|+
literal|"/exist/xmlrpc"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|QUERY
init|=
literal|"let $node := "
operator|+
literal|"<root>"
operator|+
literal|"<nodeA><nodeB>BBB</nodeB></nodeA>"
operator|+
literal|"<nodeC>CCC</nodeC>"
operator|+
literal|"</root>"
operator|+
literal|"return"
operator|+
literal|"   $node/nodeA/nodeB"
decl_stmt|;
specifier|public
name|FragmentsTest
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
name|addAction
argument_list|(
operator|new
name|XQueryAction
argument_list|(
name|URI
operator|+
literal|"/C1"
argument_list|,
literal|"test.xml"
argument_list|,
name|QUERY
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|XQueryAction
argument_list|(
name|URI
operator|+
literal|"/C2"
argument_list|,
literal|"test.xml"
argument_list|,
name|QUERY
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|CreateCollectionAction
argument_list|(
name|URI
operator|+
literal|"/C1"
argument_list|,
literal|"testappend.xml"
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
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
comment|/* (non-Javadoc)      * @see org.exist.xmldb.test.concurrent.ConcurrentTestBase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
comment|//        try {
comment|//            DBUtils.shutdownDB(URI);
comment|//        } catch (XMLDBException e) {
comment|//            e.printStackTrace();
comment|//        }
block|}
block|}
end_class

end_unit

