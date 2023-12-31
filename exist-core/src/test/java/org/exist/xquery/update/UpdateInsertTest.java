begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|update
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|XQueryService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|UpdateInsertTest
extends|extends
name|AbstractTestUpdate
block|{
specifier|private
specifier|static
specifier|final
name|String
name|EOL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|insertNamespacedAttribute
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|docName
init|=
literal|"pathNs2.xml"
decl_stmt|;
specifier|final
name|XQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
name|docName
argument_list|,
literal|"<test/>"
argument_list|)
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//t[@xml:id]"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|String
name|update
init|=
literal|"update insert<t xml:id=\"id1\"/> into /test"
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
name|update
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//t[@xml:id eq 'id1']"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"/test/id('id1')"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|update
operator|=
literal|"update value //t/@xml:id with 'id2'"
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
name|update
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//t[@xml:id eq 'id2']"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"id('id2', /test)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertPrecedingAttribute
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|tempId
init|=
literal|"tmp-1512257166656"
decl_stmt|;
specifier|final
name|String
name|doc
init|=
literal|"<annotation-list>"
operator|+
name|EOL
operator|+
literal|"<annotation-item generator=\"earlyPrint\" status=\"pending\" visibility=\"public\" temp-id=\""
operator|+
name|tempId
operator|+
literal|"\" reason=\"\" creator=\"craig\" created=\"2017-12-02T23:26:06.656Z\" modified=\"2017-12-02T23:26:06.656Z\" generated=\"2017-12-02T23:26:06.656Z\" ticket=\"s-1512257166639\">"
operator|+
name|EOL
operator|+
literal|"<annotation-body subtype=\"update\" format=\"text/xml\" type=\"TEI\" original-value=\"Worthies\">"
operator|+
name|EOL
operator|+
literal|"<w>test123</w>"
operator|+
name|EOL
operator|+
literal|"</annotation-body>"
operator|+
name|EOL
operator|+
literal|"<annotation-target source=\"A00969\" version=\"\">"
operator|+
name|EOL
operator|+
literal|"<target-selector type=\"IdSelector\" value=\"A00969-001-b-0240\"/>"
operator|+
name|EOL
operator|+
literal|"</annotation-target>"
operator|+
name|EOL
operator|+
literal|"</annotation-item>"
operator|+
name|EOL
operator|+
literal|"</annotation-list>"
decl_stmt|;
specifier|final
name|String
name|docName
init|=
literal|"A00969_annotations.xml"
decl_stmt|;
specifier|final
name|XQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
name|docName
argument_list|,
name|doc
argument_list|)
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//annotation-item[@temp-id = '"
operator|+
name|tempId
operator|+
literal|"']/@status"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//annotation-item[@temp-id = '"
operator|+
name|tempId
operator|+
literal|"']/@id"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|uuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|update
init|=
literal|"update insert attribute id {'"
operator|+
name|uuid
operator|+
literal|"'} preceding //annotation-item[@temp-id = '"
operator|+
name|tempId
operator|+
literal|"']/@status"
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
name|update
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//annotation-item[@temp-id = '"
operator|+
name|tempId
operator|+
literal|"']/@id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertInMemoryDocument
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|doc
init|=
literal|"<empty/>"
decl_stmt|;
specifier|final
name|String
name|docName
init|=
literal|"empty.xml"
decl_stmt|;
specifier|final
name|XQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
name|docName
argument_list|,
name|doc
argument_list|)
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//empty/child::node()"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|uuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|update
init|=
literal|"update insert document {<uuid>"
operator|+
name|uuid
operator|+
literal|"</uuid> } into /empty"
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
name|update
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//empty/uuid"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

