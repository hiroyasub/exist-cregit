begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|IndexQueryService
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
name|RemoveAppendAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|XMLDBException
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
comment|/**  * Test concurrent XUpdates on the same document.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentXUpdateTest
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
specifier|private
specifier|final
specifier|static
name|String
name|CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<create path=\"//ELEMENT-1/@attribute-3\" type=\"xs:string\"/>"
operator|+
literal|"<create path=\"//ELEMENT-1/@attribute-1\" type=\"xs:string\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
name|File
name|tempFile
decl_stmt|;
specifier|public
name|ConcurrentXUpdateTest
parameter_list|()
block|{
name|super
argument_list|(
name|URI
argument_list|,
literal|"C1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|Override
specifier|public
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
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|getTestCollection
argument_list|()
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|idxConf
argument_list|)
expr_stmt|;
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|CONFIG
argument_list|)
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
name|assertNotNull
argument_list|(
name|wordList
argument_list|)
expr_stmt|;
name|tempFile
operator|=
name|DBUtils
operator|.
name|generateXMLFile
argument_list|(
literal|500
argument_list|,
literal|10
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
comment|//String query0 = "xmldb:document('" + DBBroker.ROOT_COLLECTION + "/C1/R1.xml')/ROOT-ELEMENT//ELEMENT-1[@attribute-3]";
comment|//String query1 = "xmldb:document()/ROOT-ELEMENT//ELEMENT-2[@attribute-2]";
name|addAction
argument_list|(
operator|new
name|RemoveAppendAction
argument_list|(
name|URI
operator|+
literal|"/C1"
argument_list|,
literal|"R1.xml"
argument_list|,
name|wordList
argument_list|)
argument_list|,
literal|50
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|)
expr_stmt|;
comment|//addAction(new RemoveAppendAction(URI + "/C1", "R1.xml", wordList), 50, 100, 200);
comment|//addAction(new MultiResourcesAction("samples/mods", URI + "/C1"), 1, 0, 300);
comment|//addAction(new RetrieveResourceAction(URI + "/C1", "R1.xml"), 10, 1000, 2000);
comment|//addAction(new XQueryAction(URI + "/C1", "R1.xml", query0), 100, 100, 100);
comment|//addAction(new XQueryAction(URI + "/C1", "R1.xml", query1), 100, 200, 100);
block|}
annotation|@
name|After
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|//super.tearDown();
name|DBUtils
operator|.
name|shutdownDB
argument_list|(
name|URI
argument_list|)
expr_stmt|;
name|tempFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

