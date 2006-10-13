begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|File
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
name|parsers
operator|.
name|SAXParser
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
name|SAXParserFactory
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
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|OutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|XMLSerializer
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
name|exist
operator|.
name|util
operator|.
name|XMLFilenameFilter
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
name|InputSource
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
name|XMLReader
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
name|Resource
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
name|CollectionManagementService
import|;
end_import

begin_class
specifier|public
class|class
name|ResourceTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
comment|/** 	 * Constructor for XMLDBTest. 	 * @param arg0 	 */
specifier|public
name|ResourceTest
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReadNonExistingResource
parameter_list|()
block|{
try|try
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|Resource
name|nonExistent
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"12345.xml"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|nonExistent
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
literal|"testReadNonExistingResource(): Exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testReadResource
parameter_list|()
block|{
try|try
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|String
index|[]
name|resources
init|=
name|testCollection
operator|.
name|listResources
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|resources
operator|.
name|length
argument_list|,
name|testCollection
operator|.
name|getResourceCount
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"reading "
operator|+
name|resources
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
name|resources
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testing XMLResource.getContentAsSAX()"
argument_list|)
expr_stmt|;
name|StringWriter
name|sout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|OutputFormat
name|format
init|=
operator|new
name|OutputFormat
argument_list|(
literal|"xml"
argument_list|,
literal|"ISO-8859-1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|format
operator|.
name|setLineWidth
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|XMLSerializer
name|xmlout
init|=
operator|new
name|XMLSerializer
argument_list|(
name|sout
argument_list|,
name|format
argument_list|)
decl_stmt|;
name|doc
operator|.
name|getContentAsSAX
argument_list|(
name|xmlout
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"----------------------------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sout
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"----------------------------------------"
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
literal|"testReadResource(): Exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testReadDOM
parameter_list|()
block|{
try|try
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"r_and_j.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|doc
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|Element
name|elem
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|n
operator|instanceof
name|Element
condition|)
block|{
name|elem
operator|=
operator|(
name|Element
operator|)
name|n
expr_stmt|;
block|}
if|else if
condition|(
name|n
operator|instanceof
name|Document
condition|)
block|{
name|elem
operator|=
operator|(
operator|(
name|Document
operator|)
name|n
operator|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|elem
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"PLAY"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Root element: "
operator|+
name|elem
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|NodeList
name|children
init|=
name|elem
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|node
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Child: "
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"child: "
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
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
block|}
specifier|public
name|void
name|testSetContentAsSAX
parameter_list|()
block|{
try|try
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"test.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
literal|"<test><title>Title</title>"
operator|+
literal|"<para>Paragraph1</para>"
operator|+
literal|"<para>Paragraph2</para>"
operator|+
literal|"</test>"
decl_stmt|;
name|ContentHandler
name|handler
init|=
name|doc
operator|.
name|setContentAsSAX
argument_list|()
decl_stmt|;
name|SAXParserFactory
name|saxFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|SAXParser
name|sax
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
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
specifier|public
name|void
name|testSetContentAsDOM
parameter_list|()
block|{
try|try
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"dom.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
literal|"<test><title>Title</title>"
operator|+
literal|"<para>Paragraph1</para>"
operator|+
literal|"<para>Paragraph2</para>"
operator|+
literal|"</test>"
decl_stmt|;
name|DocumentBuilderFactory
name|docFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
name|docFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|dom
init|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContentAsDOM
argument_list|(
name|dom
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
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
specifier|public
name|void
name|testQueryRemoveResource
parameter_list|()
block|{
name|Resource
name|resource
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|String
name|resourceName
init|=
literal|"QueryTestPerson.xml"
decl_stmt|;
name|String
name|id
init|=
literal|"test."
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|content
init|=
literal|"<?xml version='1.0'?><person id=\""
operator|+
name|id
operator|+
literal|"\"><name>Jason</name></person>"
decl_stmt|;
name|resource
operator|=
name|testCollection
operator|.
name|createResource
argument_list|(
name|resourceName
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|testCollection
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
name|service
operator|.
name|query
argument_list|(
literal|"/person[@id='"
operator|+
name|id
operator|+
literal|"']"
argument_list|)
decl_stmt|;
for|for
control|(
name|ResourceIterator
name|iterator
init|=
name|rs
operator|.
name|getIterator
argument_list|()
init|;
name|iterator
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|Resource
name|r
init|=
name|iterator
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Resource id="
operator|+
name|r
operator|.
name|getId
argument_list|()
operator|+
literal|" xml="
operator|+
name|r
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|removeResource
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|resource
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unexpected Exception occured: "
operator|+
name|xe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|xe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testAddRemove
parameter_list|()
block|{
try|try
block|{
specifier|final
name|String
name|resourceID
init|=
literal|"addremove.xml"
decl_stmt|;
name|XMLResource
name|created
init|=
name|addResource
argument_list|(
name|resourceID
argument_list|,
name|xmlForTest
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|created
argument_list|)
expr_stmt|;
comment|// need to test documents xml structure
name|XMLResource
name|located
init|=
name|resourceForId
argument_list|(
name|resourceID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|located
argument_list|)
expr_stmt|;
comment|//assertEquals((String) created.getContent(), (String) located.getContent());
name|removeDocument
argument_list|(
name|resourceID
argument_list|)
expr_stmt|;
name|XMLResource
name|locatedAfterRemove
init|=
name|resourceForId
argument_list|(
name|resourceID
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|locatedAfterRemove
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
specifier|private
name|void
name|removeDocument
parameter_list|(
name|String
name|id
parameter_list|)
block|{
try|try
block|{
name|XMLResource
name|resource
init|=
name|resourceForId
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|resource
condition|)
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
expr_stmt|;
name|collection
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
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
specifier|private
name|XMLResource
name|addResource
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|content
parameter_list|)
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|XMLResource
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|createResource
argument_list|(
name|id
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
expr_stmt|;
name|result
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
name|result
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
finally|finally
block|{
name|closeCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|XMLResource
name|resourceForId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|XMLResource
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|getResource
argument_list|(
name|id
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
finally|finally
block|{
name|closeCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|void
name|closeCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
literal|null
operator|!=
name|collection
condition|)
block|{
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|String
name|xmlForTest
parameter_list|()
block|{
return|return
literal|"<test><title>Title</title>"
operator|+
literal|"<para>Paragraph1</para>"
operator|+
literal|"<para>Paragraph2</para>"
operator|+
literal|"</test>"
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
comment|// initialize driver
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|String
name|directory
init|=
literal|"samples/shakespeare"
decl_stmt|;
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|File
name|existDir
init|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|File
name|files
index|[]
init|=
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|XMLFilenameFilter
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|files
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
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
name|ResourceTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

