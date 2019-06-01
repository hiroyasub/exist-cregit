begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistXmldbEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
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
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * This is the simplest test that demonstrates the<tt>Predicate</tt>/<tt>OpOr</tt>  * bug. Right now, there is only one test - at the very bottom of the   * source code.   * @author Jason Smith  */
end_comment

begin_class
specifier|public
class|class
name|TestXPathOpOrSpecialCase
extends|extends
name|Assert
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|TestXPathOpOrSpecialCase
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/** Database test collection (<tt>/db/blah</tt>). */
specifier|private
name|Collection
name|testCollection
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
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|testCollection
operator|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"blah"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|removeCollection
argument_list|(
literal|"blah"
argument_list|)
expr_stmt|;
name|testCollection
operator|=
literal|null
expr_stmt|;
block|}
comment|/** 	 * Given an essentially empty XML document at path<tt>/db/blah/blah.xml</tt>, 	 * query the document with a bogus predicate containing an<tt>or<tt> operation; 	 * expect<tt>org.exist.xquery.XPathException: exerr:ERROR cannot convert xs:boolean('false') to a node set</tt>. 	 */
annotation|@
name|Test
specifier|public
name|void
name|verifyOpOrInPredicate
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|storeXML
argument_list|(
name|testCollection
argument_list|,
literal|"blah.xml"
argument_list|,
literal|"<blah>No element content.</blah>"
argument_list|)
expr_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
literal|"/blah[a='A' or b='B']"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**  	 * Store the XML string into the specified collection and document. 	 * @param collection The target collection.      * @param documentName The target document name.      * @param content The XML content to be stored.      * @throws XMLDBException See {@link XMLDBException}.      */
specifier|private
name|void
name|storeXML
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|documentName
parameter_list|,
specifier|final
name|String
name|content
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|createResource
argument_list|(
name|documentName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
