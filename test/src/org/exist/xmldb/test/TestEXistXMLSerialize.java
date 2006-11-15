begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * TestEXistXMLSerialize.java  *  * Created on January 22, 2004, 11:01 AM  */
end_comment

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
name|ByteArrayOutputStream
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
name|util
operator|.
name|Properties
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
name|stream
operator|.
name|StreamResult
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
name|serializer
operator|.
name|DOMSerializer
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
name|serializer
operator|.
name|SAXSerializer
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
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_comment
comment|/**  *  * @author  bmadigan  */
end_comment

begin_class
specifier|public
class|class
name|TestEXistXMLSerialize
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA
init|=
literal|"<test>"
operator|+
literal|"<para>Ã¤Ã¤Ã¶Ã¶Ã¼Ã¼ÃÃÃÃÃÃÃÃ</para>"
operator|+
literal|"<para>\uC5F4\uB2E8\uACC4</para>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XSL_DATA
init|=
literal|"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" "
operator|+
literal|"version=\"1.0\">"
operator|+
literal|"<xsl:param name=\"testparam\"/>"
operator|+
literal|"<xsl:template match=\"test\"><test><xsl:apply-templates/></test></xsl:template>"
operator|+
literal|"<xsl:template match=\"para\">"
operator|+
literal|"<p><xsl:value-of select=\"$testparam\"/>:<xsl:apply-templates/></p></xsl:template>"
operator|+
literal|"</xsl:stylesheet>"
decl_stmt|;
specifier|static
name|File
name|existDir
init|=
literal|null
decl_stmt|;
static|static
block|{
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
name|existDir
operator|=
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
expr_stmt|;
block|}
comment|/** Creates a new instance of TestEXistXMLSerialize */
specifier|public
name|TestEXistXMLSerialize
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|Collection
name|c
init|=
literal|null
decl_stmt|;
name|Database
name|database
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"test/src/org/exist/xmldb/test/PerformanceTest.xml"
argument_list|)
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|Class
name|cl
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
name|cl
operator|.
name|newInstance
argument_list|()
expr_stmt|;
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
name|c
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
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
name|tearDown
parameter_list|()
block|{
try|try
block|{
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|c
operator|=
literal|null
expr_stmt|;
name|database
operator|=
literal|null
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
name|testSerialize1
parameter_list|( )
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Xerces version: "
operator|+
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|impl
operator|.
name|Version
operator|.
name|getVersion
argument_list|( )
argument_list|)
expr_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
literal|null
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|( )
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContentAsDOM
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
literal|"Storing resource: "
operator|+
name|resource
operator|.
name|getId
argument_list|( )
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|getResource
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|resource
operator|.
name|getContentAsDOM
argument_list|( )
decl_stmt|;
name|node
operator|=
name|node
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Attempting serialization 1"
argument_list|)
expr_stmt|;
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|( )
decl_stmt|;
name|StreamResult
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|Transformer
name|xformer
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|xformer
operator|.
name|transform
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using javax.xml.transform.Transformer"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|String
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--------------------- "
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
name|testSerialize2
parameter_list|( )
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Xerces version: "
operator|+
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|impl
operator|.
name|Version
operator|.
name|getVersion
argument_list|( )
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|( )
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
literal|null
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContentAsDOM
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
literal|"Storing resource: "
operator|+
name|resource
operator|.
name|getId
argument_list|( )
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|getResource
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Node
name|node
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
literal|"Attempting serialization using XMLSerializer"
argument_list|)
expr_stmt|;
name|OutputFormat
name|format
init|=
operator|new
name|OutputFormat
argument_list|( )
decl_stmt|;
name|format
operator|.
name|setLineWidth
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|format
operator|.
name|setIndent
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|format
operator|.
name|setPreserveSpace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|( )
decl_stmt|;
name|XMLSerializer
name|serializer
init|=
operator|new
name|XMLSerializer
argument_list|(
name|out
argument_list|,
name|format
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|Document
condition|)
block|{
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|Document
operator|)
name|node
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|node
operator|instanceof
name|Element
condition|)
block|{
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Can't serialize node type: "
operator|+
name|node
argument_list|)
throw|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using org.apache.xml.serialize.XMLSerializer"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|String
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--------------------- "
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
name|testSerialize3
parameter_list|( )
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Xerces version: "
operator|+
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|impl
operator|.
name|Version
operator|.
name|getVersion
argument_list|( )
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|( )
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
literal|null
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContentAsDOM
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
literal|"Storing resource: "
operator|+
name|resource
operator|.
name|getId
argument_list|( )
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|getResource
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Node
name|node
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
literal|"Attempting serialization using eXist's serializer"
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
literal|"indent"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|DOMSerializer
name|serializer
init|=
operator|new
name|DOMSerializer
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using org.exist.util.serializer.DOMSerializer"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|writer
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
literal|"---------------------"
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
name|testSerialize4
parameter_list|( )
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Xerces version: "
operator|+
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|impl
operator|.
name|Version
operator|.
name|getVersion
argument_list|( )
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|( )
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
literal|null
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContentAsDOM
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
literal|"Storing resource: "
operator|+
name|resource
operator|.
name|getId
argument_list|( )
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|getResource
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Node
name|node
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
literal|"Attempting serialization using eXist's SAX serializer"
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
literal|"indent"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using org.exist.util.serializer.SAXSerializer"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|writer
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
literal|"---------------------"
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
name|testSerialize5
parameter_list|()
block|{
try|try
block|{
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
literal|"test.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|XML_DATA
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Storing resource: "
operator|+
name|resource
operator|.
name|getId
argument_list|( )
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XMLResource
name|style
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
literal|"test.xsl"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|style
operator|.
name|setContent
argument_list|(
name|XSL_DATA
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Storing resource: "
operator|+
name|style
operator|.
name|getId
argument_list|( )
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|style
argument_list|)
expr_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
literal|"indent"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setProperty
argument_list|(
literal|"stylesheet"
argument_list|,
literal|"test.xsl"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setProperty
argument_list|(
literal|"stylesheet-param.testparam"
argument_list|,
literal|"TEST"
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using org.exist.util.serializer.SAXSerializer"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|writer
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
literal|"---------------------"
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
comment|/**      * @param args the command line arguments      */
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
name|TestEXistXMLSerialize
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

