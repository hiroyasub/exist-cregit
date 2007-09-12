begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * DTMHandleTest.java  *  * 2004 by O2 IT Engineering  * Zurich,  Switzerland (CH)  */
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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|CompiledExpression
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|XMLResource
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

begin_comment
comment|/**  * Tests the TreeLevelOrder function.  *   * @author Tobias Wunden  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|DTMHandleTest
extends|extends
name|TestCase
block|{
comment|/** eXist database url */
specifier|static
specifier|final
name|String
name|eXistUrl
init|=
literal|"xmldb:exist://"
decl_stmt|;
comment|/** eXist configuration file */
specifier|static
specifier|final
name|String
name|eXistConf
init|=
literal|"C:\\Documents and Settings\\Tobias Wunden\\My Documents\\Projects\\Varia\\Test\\conf.xml"
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
name|DTMHandleTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Test for the TreeLevelOrder function. This test 	 *<ul> 	 *<li>Registers a database instance</li> 	 *<li>Writes a document to the database using the XQueryService</li> 	 *<li>Reads the document from the database using XmlDB</li> 	 *<li>Accesses the document using DOM</li> 	 *</ul> 	 */
specifier|public
specifier|final
name|void
name|testTreeLevelOrder
parameter_list|()
block|{
name|Database
name|eXist
init|=
literal|null
decl_stmt|;
name|String
name|document
init|=
literal|"survey.xml"
decl_stmt|;
name|StringBuffer
name|xmlDocument
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<survey>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<date>2004/11/24 17:42:31 GMT</date>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<from>tobias.wunden@o2it.ch</from>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<to>tobias.wunden@o2it.ch</to>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<subject>Test</subject>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<field>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<name>homepage</name>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<value>-</value>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"</field>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"</survey>"
argument_list|)
expr_stmt|;
try|try
block|{
name|eXist
operator|=
name|registerDatabase
argument_list|()
expr_stmt|;
comment|// Obtain XQuery service
name|XQueryService
name|service
init|=
name|getXQueryService
argument_list|(
name|eXist
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Failed to obtain xquery service instance!"
argument_list|,
name|service
argument_list|)
expr_stmt|;
comment|// write document to the database
name|store
argument_list|(
name|xmlDocument
operator|.
name|toString
argument_list|()
argument_list|,
name|service
argument_list|,
name|document
argument_list|)
expr_stmt|;
comment|// read document back from database
name|Node
name|root
init|=
name|load
argument_list|(
name|service
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Document "
operator|+
name|document
operator|+
literal|" was not found in the database!"
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|boolean
name|foundFieldText
init|=
literal|false
decl_stmt|;
name|NodeList
name|rootChildren
init|=
name|root
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|r
init|=
literal|0
init|;
name|r
operator|<
name|rootChildren
operator|.
name|getLength
argument_list|()
condition|;
name|r
operator|++
control|)
block|{
if|if
condition|(
name|rootChildren
operator|.
name|item
argument_list|(
name|r
argument_list|)
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"field"
argument_list|)
condition|)
block|{
name|foundFieldText
operator|=
literal|false
expr_stmt|;
name|Node
name|field
init|=
name|rootChildren
operator|.
name|item
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found field node["
operator|+
literal|1
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|NodeList
name|fieldChildren
init|=
name|field
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|f
init|=
literal|0
init|;
name|f
operator|<
name|fieldChildren
operator|.
name|getLength
argument_list|()
condition|;
name|f
operator|++
control|)
block|{
if|if
condition|(
name|fieldChildren
operator|.
name|item
argument_list|(
name|f
argument_list|)
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"name"
argument_list|)
condition|)
block|{
name|foundFieldText
operator|=
literal|true
expr_stmt|;
name|Node
name|name
init|=
name|fieldChildren
operator|.
name|item
argument_list|(
name|f
argument_list|)
decl_stmt|;
comment|//String nameText = name.getTextContent();
name|String
name|nameText
init|=
name|TreeLevelOrderTest
operator|.
name|textContent
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Failed to read existing field["
operator|+
literal|1
operator|+
literal|"]/name/text()"
argument_list|,
name|nameText
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Failed to read existing field["
operator|+
literal|1
operator|+
literal|"]/name/text()"
argument_list|,
name|foundFieldText
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test succeeded"
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
comment|/** 	 * Stores the given xml fragment into the database. 	 *  	 * @param xml the xml document 	 * @param service the xquery service 	 * @param document the document name	  	 */
specifier|private
specifier|final
name|void
name|store
parameter_list|(
name|String
name|xml
parameter_list|,
name|XQueryService
name|service
parameter_list|,
name|String
name|document
parameter_list|)
block|{
name|StringBuffer
name|query
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"xquery version \"1.0\";"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"declare namespace xdb=\"http://exist-db.org/xquery/xmldb\";"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"let $root := xdb:collection(\""
operator|+
name|eXistUrl
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"\", \"admin\", \"admin\"),"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"$doc := xdb:store($root, $document, $survey)"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"return<result/>"
argument_list|)
expr_stmt|;
try|try
block|{
name|service
operator|.
name|declareVariable
argument_list|(
literal|"survey"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"document"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|CompiledExpression
name|cQuery
init|=
name|service
operator|.
name|compile
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|service
operator|.
name|execute
argument_list|(
name|cQuery
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
comment|/** 	 * Loads the xml document identified by<code>document</code> from the database. 	 *  	 * @param service the xquery service 	 * @param document the document to load	 	 */
specifier|private
specifier|final
name|Node
name|load
parameter_list|(
name|XQueryService
name|service
parameter_list|,
name|String
name|document
parameter_list|)
block|{
name|StringBuffer
name|query
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"xquery version \"1.0\";"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"let $survey := xmldb:document(concat('"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"', '/', $document))"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"return ($survey)"
argument_list|)
expr_stmt|;
try|try
block|{
name|service
operator|.
name|declareVariable
argument_list|(
literal|"document"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|CompiledExpression
name|cQuery
init|=
name|service
operator|.
name|compile
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ResourceSet
name|set
init|=
name|service
operator|.
name|execute
argument_list|(
name|cQuery
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|set
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|getSize
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|XMLResource
operator|)
name|set
operator|.
name|getIterator
argument_list|()
operator|.
name|nextResource
argument_list|()
operator|)
operator|.
name|getContentAsDOM
argument_list|()
return|;
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
return|return
literal|null
return|;
block|}
comment|/** 	 * Registers a new database instance and returns it. 	 */
specifier|private
specifier|final
name|Database
name|registerDatabase
parameter_list|()
block|{
name|Class
name|driver
init|=
literal|null
decl_stmt|;
name|String
name|driverName
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
try|try
block|{
name|driver
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|driverName
argument_list|)
expr_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|driver
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//database.setProperty("configuration", eXistConf);
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
return|return
name|database
return|;
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
return|return
literal|null
return|;
block|}
comment|/** 	 * Retrieves the base collection and thereof returns a reference to the collection's 	 * xquery service. 	 *  	 * @param db the database 	 * @return the xquery service 	 */
specifier|private
specifier|final
name|XQueryService
name|getXQueryService
parameter_list|(
name|Database
name|db
parameter_list|)
block|{
try|try
block|{
name|Collection
name|collection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|eXistUrl
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|"admin"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|service
return|;
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
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

