begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXResult
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
name|Document
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
name|Element
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
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Locator
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
name|ResourceIterator
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
name|XMLResource
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

begin_comment
comment|/**  * @author jmv  */
end_comment

begin_class
specifier|public
class|class
name|DOMTest
block|{
specifier|private
specifier|static
name|String
name|driver
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
specifier|static
name|String
name|baseURI
init|=
name|XmldbURI
operator|.
name|LOCAL_DB
decl_stmt|;
specifier|private
specifier|static
name|String
name|username
init|=
literal|"admin"
decl_stmt|;
specifier|private
specifier|static
name|String
name|password
init|=
literal|""
decl_stmt|;
specifier|private
specifier|static
name|String
name|name
init|=
literal|"test.xml"
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"exist.initdb"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DOMTest
name|tester
init|=
operator|new
name|DOMTest
argument_list|()
decl_stmt|;
name|tester
operator|.
name|runTest1
argument_list|()
expr_stmt|;
name|tester
operator|.
name|runTest2
argument_list|()
expr_stmt|;
name|tester
operator|.
name|runTest3
argument_list|()
expr_stmt|;
name|tester
operator|.
name|runTest4
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|runTest4
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**  	 * - Storing XML resource from XML string 	 * - simple XQuery 	 * - removing resource 	 * - shutdown with the DatabaseInstanceManager 	 */
specifier|public
name|void
name|runTest1
parameter_list|()
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running test1 ..."
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|dbc
init|=
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|dbc
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Collection
name|rootColl
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|rootColl
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|cms
operator|.
name|createCollection
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
comment|// jmv
name|cms
operator|.
name|removeCollection
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|cms
operator|.
name|createCollection
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|Collection
name|coll
init|=
name|rootColl
operator|.
name|getChildCollection
argument_list|(
literal|"A"
argument_list|)
decl_stmt|;
name|XMLResource
name|r
init|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|r
operator|.
name|setContent
argument_list|(
literal|"<properties><property key=\"type\">Table</property></properties>"
argument_list|)
expr_stmt|;
name|coll
operator|.
name|storeResource
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|XPathQueryService
name|xpqs
init|=
operator|(
name|XPathQueryService
operator|)
name|coll
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|rs
init|=
name|xpqs
operator|.
name|query
argument_list|(
literal|"//properties[property[@key='type' and text()='Table']]"
argument_list|)
decl_stmt|;
for|for
control|(
name|ResourceIterator
name|i
init|=
name|rs
operator|.
name|getIterator
argument_list|()
init|;
name|i
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|r
operator|=
operator|(
name|XMLResource
operator|)
name|i
operator|.
name|nextResource
argument_list|()
expr_stmt|;
name|String
name|s
init|=
operator|(
name|String
operator|)
name|r
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|Node
name|content
init|=
name|r
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Resource: "
operator|+
name|r
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"getContent: "
operator|+
name|s
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"getContentAsDOM: "
operator|+
name|content
argument_list|)
expr_stmt|;
name|coll
operator|.
name|removeResource
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|cms
operator|.
name|removeCollection
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|rootColl
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test 1: PASSED"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"test 1: FAILED"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**  	 * - create and fill a simple document via DOM and JAXP 	 * - store it with setContentAsDOM() 	 * - simple access via getContentAsDOM() 	 * */
specifier|public
name|void
name|runTest2
parameter_list|()
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running test 2 ..."
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|dbc
init|=
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|dbc
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Collection
name|coll
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating resource!"
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
expr_stmt|;
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|db
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|db
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|rootElem
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"element"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|appendChild
argument_list|(
name|rootElem
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContentAsDOM
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|coll
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|coll
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found resource!"
argument_list|)
expr_stmt|;
block|}
name|String
name|s
init|=
operator|(
name|String
operator|)
name|resource
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|Node
name|content
init|=
name|resource
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Resource: "
operator|+
name|resource
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"getContent: "
operator|+
name|s
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"getContentAsDOM: "
operator|+
name|content
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|coll
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|dbc
init|=
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|dbc
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Collection
name|coll
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|coll
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|coll
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test 2: PASSED"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test 2: FAILED"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** like test 2 but add attribute and text as well */
specifier|public
name|void
name|runTest3
parameter_list|()
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running test 3 ..."
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|dbc
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|dbc
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Collection
name|coll
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|rootElem
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"element"
argument_list|)
decl_stmt|;
name|Element
name|propertyElem
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"property"
argument_list|)
decl_stmt|;
name|propertyElem
operator|.
name|setAttribute
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|propertyElem
operator|.
name|appendChild
argument_list|(
name|doc
operator|.
name|createTextNode
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|rootElem
operator|.
name|appendChild
argument_list|(
name|propertyElem
argument_list|)
expr_stmt|;
name|doc
operator|.
name|appendChild
argument_list|(
name|rootElem
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContentAsDOM
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|coll
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|coll
operator|.
name|close
argument_list|()
expr_stmt|;
name|coll
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|String
name|s
init|=
operator|(
name|String
operator|)
name|resource
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
name|resource
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"getContent: "
operator|+
name|s
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"getContentAsDOM: "
operator|+
name|n
argument_list|)
expr_stmt|;
name|coll
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|coll
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test 3 : PASSED"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test 3 : FAILED"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** like test 3 but uses the DOM as input to an (identity) XSLT transform */
specifier|public
name|void
name|runTest4
parameter_list|(
name|boolean
name|getContentAsDOM
parameter_list|)
block|{
name|Database
name|database
init|=
literal|null
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running test 4 ..."
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|dbc
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|database
operator|=
operator|(
name|Database
operator|)
name|dbc
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Collection
name|coll
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|rootElem
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"element"
argument_list|)
decl_stmt|;
name|Element
name|propertyElem
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"property"
argument_list|)
decl_stmt|;
name|propertyElem
operator|.
name|setAttribute
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|propertyElem
operator|.
name|appendChild
argument_list|(
name|doc
operator|.
name|createTextNode
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|rootElem
operator|.
name|appendChild
argument_list|(
name|propertyElem
argument_list|)
expr_stmt|;
name|doc
operator|.
name|appendChild
argument_list|(
name|rootElem
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContentAsDOM
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|coll
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|coll
operator|.
name|close
argument_list|()
expr_stmt|;
name|coll
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|coll
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Node
name|n
decl_stmt|;
if|if
condition|(
name|getContentAsDOM
condition|)
block|{
name|n
operator|=
name|resource
operator|.
name|getContentAsDOM
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|s
init|=
operator|(
name|String
operator|)
name|resource
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
decl_stmt|;
name|bytes
operator|=
name|s
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|DocumentBuilder
name|db
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|n
operator|=
name|db
operator|.
name|parse
argument_list|(
name|bais
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"getContentAsDOM: "
operator|+
name|n
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|Transformer
name|t
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|SAXResult
name|result
init|=
operator|new
name|SAXResult
argument_list|(
operator|new
name|DOMTest
operator|.
name|SAXHandler
argument_list|()
argument_list|)
decl_stmt|;
name|t
operator|.
name|transform
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|coll
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test 4 : PASSED"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test 4 : FAILED"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|database
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Collection
name|coll
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|coll
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
block|}
block|}
block|}
specifier|public
specifier|static
class|class
name|SAXHandler
implements|implements
name|ContentHandler
block|{
name|SAXHandler
parameter_list|()
block|{
block|}
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.characters("
operator|+
operator|new
name|String
argument_list|(
name|ch
argument_list|)
operator|+
literal|", "
operator|+
name|start
operator|+
literal|", "
operator|+
name|length
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.endDocument()"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.endElement("
operator|+
name|namespaceURI
operator|+
literal|", "
operator|+
name|localName
operator|+
literal|", "
operator|+
name|qName
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.endPrefixMapping("
operator|+
name|prefix
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.ignorableWhitespace("
operator|+
operator|new
name|String
argument_list|(
name|ch
argument_list|)
operator|+
literal|", "
operator|+
name|start
operator|+
literal|", "
operator|+
name|length
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.processingInstruction("
operator|+
name|target
operator|+
literal|", "
operator|+
name|data
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.setDocumentLocator("
operator|+
name|locator
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|skippedEntity
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.skippedEntity("
operator|+
name|name
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.startDocument()"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.startElement("
operator|+
name|namespaceURI
operator|+
literal|", "
operator|+
name|localName
operator|+
literal|", "
operator|+
name|qName
operator|+
literal|","
operator|+
name|atts
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|xuri
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SAXHandler.startPrefixMapping("
operator|+
name|prefix
operator|+
literal|", "
operator|+
name|xuri
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

