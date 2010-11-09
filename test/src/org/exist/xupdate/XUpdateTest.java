begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|BufferedReader
import|;
end_import

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
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|traversal
operator|.
name|DocumentTraversal
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
name|traversal
operator|.
name|NodeFilter
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
name|traversal
operator|.
name|NodeIterator
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
name|XUpdateQueryService
import|;
end_import

begin_comment
comment|/**  * @author berlinge-to  *  * To change this generated comment edit the template variable "typecomment":  * Window>Preferences>Java>Templates.  * To enable and disable the creation of type comments go to  * Window>Preferences>Java>Code Generation.  */
end_comment

begin_class
specifier|public
class|class
name|XUpdateTest
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
specifier|private
specifier|final
specifier|static
name|String
name|XUPDATE_COLLECTION
init|=
literal|"xupdate_tests"
decl_stmt|;
specifier|static
name|File
name|existDir
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
specifier|private
specifier|final
specifier|static
name|String
name|MODIFICATION_DIR
init|=
operator|(
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"test/src/org/exist/xupdate/modifications"
argument_list|)
operator|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|RESTULT_DIR
init|=
operator|(
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"test/src/org/exist/xupdate/results"
argument_list|)
operator|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|SOURCE_DIR
init|=
operator|(
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"test/src/org/exist/xupdate/input"
argument_list|)
operator|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XUPDATE_FILE
init|=
literal|"xu.xml"
decl_stmt|;
comment|// xlm document name in eXist
specifier|private
name|Collection
name|col
init|=
literal|null
decl_stmt|;
comment|/** 	 * Constructor for xupdate. 	 */
specifier|public
name|XUpdateTest
parameter_list|()
block|{
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
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
name|col
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/"
operator|+
name|XUPDATE_COLLECTION
argument_list|)
expr_stmt|;
if|if
condition|(
name|col
operator|==
literal|null
condition|)
block|{
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
name|mgtService
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
name|col
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|XUPDATE_COLLECTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"collection created."
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doTest
parameter_list|(
name|String
name|testName
parameter_list|,
name|String
name|sourceFile
parameter_list|)
throws|throws
name|Exception
block|{
name|addDocument
argument_list|(
name|sourceFile
argument_list|)
expr_stmt|;
comment|//update input xml file
name|Document
name|xupdateResult
init|=
name|updateDocument
argument_list|(
name|MODIFICATION_DIR
operator|+
literal|"/"
operator|+
name|testName
operator|+
literal|".xml"
argument_list|)
decl_stmt|;
name|removeWhiteSpace
argument_list|(
name|xupdateResult
argument_list|)
expr_stmt|;
comment|//Read reference xml file
name|DocumentBuilderFactory
name|parserFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|parserFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|parserFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|referenceXML
init|=
name|builder
operator|.
name|parse
argument_list|(
name|RESTULT_DIR
operator|+
literal|"/"
operator|+
name|testName
operator|+
literal|".xml"
argument_list|)
decl_stmt|;
name|removeWhiteSpace
argument_list|(
name|referenceXML
argument_list|)
expr_stmt|;
comment|//compare
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
operator|new
name|CompareDocuments
argument_list|()
operator|.
name|compare
argument_list|(
name|referenceXML
argument_list|,
name|xupdateResult
argument_list|)
expr_stmt|;
name|removeDocument
argument_list|()
expr_stmt|;
block|}
comment|/* 	 * helperfunctions 	 *  	 */
specifier|public
name|void
name|addDocument
parameter_list|(
name|String
name|sourceFile
parameter_list|)
throws|throws
name|Exception
block|{
name|XMLResource
name|document
init|=
operator|(
name|XMLResource
operator|)
name|col
operator|.
name|createResource
argument_list|(
name|XUPDATE_FILE
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|SOURCE_DIR
operator|+
literal|"/"
operator|+
name|sourceFile
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"can't read file "
operator|+
name|sourceFile
argument_list|)
expr_stmt|;
name|document
operator|.
name|setContent
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|col
operator|.
name|storeResource
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"document stored."
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Resource
name|document
init|=
name|col
operator|.
name|getResource
argument_list|(
name|XUPDATE_FILE
argument_list|)
decl_stmt|;
name|col
operator|.
name|removeResource
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"document removed."
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Document
name|updateDocument
parameter_list|(
name|String
name|updateFile
parameter_list|)
throws|throws
name|Exception
block|{
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// Read XUpdate-Modifcations
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"update file: "
operator|+
name|updateFile
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|updateFile
argument_list|)
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|char
index|[]
name|characters
init|=
operator|new
name|char
index|[
operator|new
name|Long
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|intValue
argument_list|()
index|]
decl_stmt|;
name|br
operator|.
name|read
argument_list|(
name|characters
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|xUpdateModifications
init|=
operator|new
name|String
argument_list|(
name|characters
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"modifications: "
operator|+
name|xUpdateModifications
argument_list|)
expr_stmt|;
comment|//
name|service
operator|.
name|update
argument_list|(
name|xUpdateModifications
argument_list|)
expr_stmt|;
comment|//col.setProperty("pretty", "true");
comment|//col.setProperty("encoding", "UTF-8");
name|XMLResource
name|ret
init|=
operator|(
name|XMLResource
operator|)
name|col
operator|.
name|getResource
argument_list|(
name|XUPDATE_FILE
argument_list|)
decl_stmt|;
name|String
name|xmlString
init|=
operator|(
operator|(
name|String
operator|)
name|ret
operator|.
name|getContent
argument_list|()
operator|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Result:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|xmlString
argument_list|)
expr_stmt|;
comment|// convert xml string to dom
comment|// todo: make it nicer
name|DocumentBuilderFactory
name|parserFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|parserFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|in
init|=
operator|new
name|InputSource
argument_list|(
operator|(
name|InputStream
operator|)
operator|new
name|ByteArrayInputStream
argument_list|(
name|xmlString
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
name|parserFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
return|return
name|builder
operator|.
name|parse
argument_list|(
name|in
argument_list|)
return|;
block|}
specifier|private
name|void
name|removeWhiteSpace
parameter_list|(
name|Document
name|document
parameter_list|)
throws|throws
name|Exception
block|{
name|DocumentTraversal
name|dt
init|=
operator|(
name|DocumentTraversal
operator|)
name|document
decl_stmt|;
name|NodeIterator
name|nodeIterator
init|=
name|dt
operator|.
name|createNodeIterator
argument_list|(
name|document
argument_list|,
name|NodeFilter
operator|.
name|SHOW_TEXT
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
name|nodeIterator
operator|.
name|nextNode
argument_list|()
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|getNodeValue
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|compareTo
argument_list|(
literal|""
argument_list|)
operator|==
literal|0
condition|)
block|{
name|node
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|node
operator|=
name|nodeIterator
operator|.
name|nextNode
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

