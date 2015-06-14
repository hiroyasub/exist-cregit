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
name|exist
operator|.
name|xmldb
operator|.
name|XQueryService
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
name|CompiledExpression
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
comment|/**  * RemoveAndReloadTest.java  *  * O2 IT Engineering  * Zurich,  Switzerland (CH)  */
end_comment

begin_comment
comment|/**  * This test provokes a parameter type error (how?).  *   * @author Tobias Wunden  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|NodeTypeTest
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
comment|/** eXist home directory */
specifier|static
specifier|final
name|String
name|existHome
init|=
literal|"C:\\Documents and Settings\\Tobias Wunden\\My Documents\\Projects\\Varia\\Test"
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
name|NodeTypeTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * This test passes nodes containing xml entities to eXist and tries 	 * to read it back in: 	 *<ul> 	 *<li>Register a database instance</li> 	 *<li>Write a "live" document to the database using the XQueryService</li> 	 *<li>Create a "work" version of it</li> 	 *</ul> 	 */
specifier|public
specifier|final
name|void
name|testRemoveAndReload
parameter_list|()
block|{
name|XQueryService
name|service
init|=
name|setupDatabase
argument_list|()
decl_stmt|;
comment|// write "live" document to the database
try|try
block|{
name|store
argument_list|(
name|createDocument
argument_list|()
argument_list|,
name|service
argument_list|,
literal|"live.xml"
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
literal|"Failed to write document to database: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// copy content from work.xml to live.xml using XUpdate
try|try
block|{
name|prepareWorkVersion
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to update document in database: "
operator|+
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
literal|"let $isLoggedIn := xdb:login('"
operator|+
name|eXistUrl
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"', \"admin\", \"\"),"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"$doc := xdb:store(\""
operator|+
name|eXistUrl
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"\", $document, $data)"
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
literal|"document"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"data"
argument_list|,
name|xml
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
name|XMLDBException
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
comment|/** 	 * Updates the given xml fragment in the database using XUpdate. 	 *  	 * @param service the xquery service	 	 */
specifier|private
specifier|final
name|void
name|prepareWorkVersion
parameter_list|(
name|XQueryService
name|service
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
literal|"xquery version \"1.0\";\n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"declare namespace xdb=\"http://exist-db.org/xquery/xmldb\";\n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"declare namespace f=\"urn:weblounge\";\n"
argument_list|)
expr_stmt|;
comment|// Returns a new with a given body and a new header
name|query
operator|.
name|append
argument_list|(
literal|"declare function f:create($live as node(), $target as xs:string) as node() { \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"<page partition=\"{$live/@partition}\" path=\"{$live/@path}\" version=\"{$target}\"> \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"        {$live/*} \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"</page> \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"}; \n"
argument_list|)
expr_stmt|;
comment|// Function "prepare". Checks if the work version already exists. If this is not the
comment|// case, it calls the "create" function to have a new page created with the live body
comment|// but with a "work" or "$target" header.
name|query
operator|.
name|append
argument_list|(
literal|"declare function f:prepare($data as node(), $target as xs:string) as xs:string? { \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"    if (empty(xmldb:xcollection($collection)/page[@version=$target])) then \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"        let $isLoggedIn := xdb:login(concat(\"xmldb:exist://\", $collection), 'admin', '') \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"        return xdb:store(concat(\"xmldb:exist://\", $collection), concat($target, \".xml\"), f:create($data, $target)) \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"    else \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"    () \n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"}; \n"
argument_list|)
expr_stmt|;
comment|// Main clause, tries to create a work from an existing live version
name|query
operator|.
name|append
argument_list|(
literal|"let $live := xmldb:xcollection($collection)/page[@version=\"live\"],\n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"     $log := util:log('DEBUG', $live),\n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"     $w := f:prepare($live, \"work\")\n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"    return\n"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"		              ()\n"
argument_list|)
expr_stmt|;
try|try
block|{
name|service
operator|.
name|declareVariable
argument_list|(
literal|"collection"
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION
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
name|XMLDBException
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
comment|/** 	 * Updates the given xml fragment in the database using XUpdate. 	 *  	 * @param service the xquery service	  	 */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
name|void
name|xupdateRemove
parameter_list|(
name|String
name|doc
parameter_list|,
name|XQueryService
name|service
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
literal|"let $isLoggedIn := xdb:login('"
operator|+
name|eXistUrl
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"', \"admin\", \"\"),"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"$mods := xdb:remove(\""
operator|+
name|eXistUrl
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"\", \""
operator|+
name|doc
operator|+
literal|"\")"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"return<modifications>{$mods}</modifications>"
argument_list|)
expr_stmt|;
try|try
block|{
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
name|XMLDBException
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
comment|/** 	 * Loads the xml document identified by<code>document</code> from the database. 	 *  	 * @param service the xquery service 	 * @param document the document to load	  	 */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
literal|"let $result := xmldb:document(concat('"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"', $document))"
argument_list|)
expr_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"return ($result)"
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
if|if
condition|(
name|set
operator|!=
literal|null
operator|&&
name|set
operator|.
name|getSize
argument_list|()
operator|>
literal|0
condition|)
block|{
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
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
argument_list|<
name|?
argument_list|>
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
comment|/** 	 * Retrieves the base collection and thereof returns a reference to the collection's 	 * xquery service. 	 *  	 * @param db the database 	 * @return the xquery service	 	 */
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
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
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
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
specifier|private
specifier|final
name|String
name|createDocument
parameter_list|()
block|{
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
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<page partition=\"home\" path=\"/\" version=\"live\">"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<header>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<renderer>home_dreispaltig</renderer>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<layout>default</layout>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<type>default</type>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<publish>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<from>2005/06/06 10:53:40 GMT</from>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<to>292278994/08/17 07:12:55 GMT</to>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"</publish>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<security>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<owner>www</owner>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<permission id=\"system:manage\" type=\"role\">system:editor</permission>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<permission id=\"system:read\" type=\"role\">system:guest</permission>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<permission id=\"system:translate\" type=\"role\">system:translator</permission>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<permission id=\"system:publish\" type=\"role\">system:publisher</permission>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<permission id=\"system:write\" type=\"role\">system:editor</permission>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"</security>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<keywords/>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<title language=\"de\">Home</title>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<title language=\"fr\">Home</title>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<title language=\"it\">Home</title>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<modified>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<date>2005/06/06 10:53:40 GMT</date>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<user>markus.jauss</user>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"</modified>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"</header>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"<body/>"
argument_list|)
expr_stmt|;
name|xmlDocument
operator|.
name|append
argument_list|(
literal|"</page>"
argument_list|)
expr_stmt|;
return|return
name|xmlDocument
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Creates the database connection. 	 *  	 * @return the xquery service 	 */
specifier|private
name|XQueryService
name|setupDatabase
parameter_list|()
block|{
try|try
block|{
name|Database
name|eXist
init|=
name|registerDatabase
argument_list|()
decl_stmt|;
comment|// Obtain XQuery service
name|XQueryService
name|service
init|=
literal|null
decl_stmt|;
name|service
operator|=
name|getXQueryService
argument_list|(
name|eXist
argument_list|)
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
literal|"Unable to register database: "
operator|+
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

