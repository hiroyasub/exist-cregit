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
name|xupdate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|DBUtils
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
name|ResourceSet
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
name|XPathQueryService
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
name|XUpdateQueryService
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|RemoveAppendTest
extends|extends
name|TestCase
block|{
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
name|TestRunner
operator|.
name|run
argument_list|(
name|RemoveAppendTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
specifier|static
name|String
name|XU_INSERT_START
init|=
literal|"<xu:modifications xmlns:xu=\""
operator|+
name|XUpdateProcessor
operator|.
name|XUPDATE_NS
operator|+
literal|"\" version=\"1.0\">"
operator|+
literal|"<xu:insert-before select=\"/test/item[@id='5']\">"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
specifier|static
name|String
name|XU_INSERT_END
init|=
literal|"</xu:insert-before>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XU_REMOVE
init|=
literal|"<xu:modifications xmlns:xu=\""
operator|+
name|XUpdateProcessor
operator|.
name|XUPDATE_NS
operator|+
literal|"\" version=\"1.0\">"
operator|+
literal|"<xu:remove select=\"/test/item[@id='5'][2]\"/>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|static
specifier|final
name|int
name|ITEM_COUNT
init|=
literal|0
decl_stmt|;
specifier|private
name|Collection
name|rootCol
decl_stmt|;
specifier|private
name|Collection
name|testCol
decl_stmt|;
specifier|private
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|//    public void testRemoveAppend() throws Exception {
comment|//        XUpdateQueryService service = (XUpdateQueryService)
comment|//            testCol.getService("XUpdateQueryService", "1.0");
comment|//        XPathQueryService query = (XPathQueryService)
comment|//            testCol.getService("XPathQueryService", "1.0");
comment|//        for (int i = 1; i< 1000; i++) {
comment|//            int which = rand.nextInt(ITEM_COUNT) + 1;
comment|//            insert(service, which);
comment|//            remove(service, which);
comment|//
comment|//            ResourceSet result = query.query("/test/item[@id='" + which + "']");
comment|//            assertEquals(result.getSize(), 1);
comment|//            System.out.println(result.getResource(0).getContent());
comment|//        }
comment|//    }
specifier|public
name|void
name|testAppendRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|testCol
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|XPathQueryService
name|query
init|=
operator|(
name|XPathQueryService
operator|)
name|testCol
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|append
argument_list|(
name|service
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|query
operator|.
name|query
argument_list|(
literal|"/test/item[@id='"
operator|+
name|i
operator|+
literal|"']"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|100
init|;
name|i
operator|>
literal|10
condition|;
name|i
operator|--
control|)
block|{
name|String
name|xu
init|=
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
operator|+
literal|"<xu:remove select=\"/test/item[@id='"
operator|+
name|i
operator|+
literal|"']\"/>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
name|long
name|mods
init|=
name|service
operator|.
name|update
argument_list|(
name|xu
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mods
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|query
operator|.
name|query
argument_list|(
literal|"/test/item/e0"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|append
parameter_list|(
name|XUpdateQueryService
name|service
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|StringWriter
name|out
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<xu:append select=\"/test\">"
argument_list|)
expr_stmt|;
name|createItem
argument_list|(
name|id
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</xu:append>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</xu:modifications>"
argument_list|)
expr_stmt|;
name|long
name|mods
init|=
name|service
operator|.
name|update
argument_list|(
name|out
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mods
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|insert
parameter_list|(
name|XUpdateQueryService
name|service
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|StringWriter
name|out
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<xu:insert-before select=\"/test/item[@id='"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"']\">"
argument_list|)
expr_stmt|;
name|createItem
argument_list|(
literal|5
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</xu:insert-before>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</xu:modifications>"
argument_list|)
expr_stmt|;
name|long
name|mods
init|=
name|service
operator|.
name|update
argument_list|(
name|out
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mods
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|remove
parameter_list|(
name|XUpdateQueryService
name|service
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|Exception
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|String
name|xu
init|=
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
operator|+
literal|"<xu:remove select=\"/test/item[@id='"
operator|+
name|id
operator|+
literal|"'][2]\"/>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
name|long
name|mods
init|=
name|service
operator|.
name|update
argument_list|(
name|XU_REMOVE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mods
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|rootCol
operator|=
name|DBUtils
operator|.
name|setupDB
argument_list|(
name|URI
argument_list|)
expr_stmt|;
name|testCol
operator|=
name|rootCol
operator|.
name|getChildCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|)
expr_stmt|;
if|if
condition|(
name|testCol
operator|!=
literal|null
condition|)
block|{
name|CollectionManagementService
name|mgr
init|=
name|DBUtils
operator|.
name|getCollectionManagementService
argument_list|(
name|rootCol
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|removeCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|)
expr_stmt|;
block|}
name|testCol
operator|=
name|DBUtils
operator|.
name|addCollection
argument_list|(
name|rootCol
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCol
argument_list|)
expr_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|testCol
argument_list|,
literal|"test.xml"
argument_list|,
literal|"<test/>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|DBUtils
operator|.
name|shutdownDB
argument_list|(
name|URI
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|createItem
parameter_list|(
name|int
name|id
parameter_list|,
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|"<item "
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"id=\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|addAttributes
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"<e"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|addAttributes
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</e"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"</item>"
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param out      * @param rand      * @throws IOException      */
specifier|private
name|void
name|addAttributes
parameter_list|(
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|" attr"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

