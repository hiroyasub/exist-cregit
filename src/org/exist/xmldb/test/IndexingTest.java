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
name|org
operator|.
name|xmldb
operator|.
name|api
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
name|modules
operator|.
name|*
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
name|DatabaseInstanceManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|*
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
name|*
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
name|*
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
name|*
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
name|xml
operator|.
name|sax
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/** Reproduce the EXistException "the document is too complex/irregularily structured  * to be mapped into eXist's numbering scheme"  * raised in {@link org/exist/dom/DocumentImpl} .  * It creates with DOM a simple document having a branch of 16 elements depth  * connected to the root, with width (arity) of 16 at each level.  *  */
end_comment

begin_class
specifier|public
class|class
name|IndexingTest
extends|extends
name|TestCase
block|{
specifier|private
name|int
name|siblingCount
decl_stmt|;
specifier|private
name|int
name|depth
decl_stmt|;
specifier|private
name|Node
name|deepBranch
decl_stmt|;
specifier|private
name|Random
name|random
decl_stmt|;
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
literal|"xmldb:exist:///db"
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
comment|//<<<
specifier|private
specifier|static
name|String
name|name
init|=
literal|"test.xml"
decl_stmt|;
specifier|private
name|String
name|EXIST_HOME
init|=
literal|""
decl_stmt|;
comment|//<<<
specifier|private
name|int
name|effectiveSiblingCount
decl_stmt|;
specifier|private
name|int
name|effectiveDepth
decl_stmt|;
specifier|private
name|long
name|startTime
decl_stmt|;
specifier|private
name|int
name|arity
decl_stmt|;
specifier|private
name|boolean
name|randomSizes
decl_stmt|;
comment|/** 	 * @see junit.framework.TestCase#setUp() 	 */
specifier|protected
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
name|siblingCount
operator|=
literal|2
expr_stmt|;
name|depth
operator|=
literal|16
expr_stmt|;
name|arity
operator|=
literal|16
expr_stmt|;
name|randomSizes
operator|=
literal|false
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * 	 * @see junit.framework.TestCase#tearDown() 	 */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 */
specifier|public
name|IndexingTest
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
name|IndexingTest
name|tester
init|=
operator|new
name|IndexingTest
argument_list|(
literal|""
argument_list|)
decl_stmt|;
comment|// tester.runTestrregularilyStructured(false);
name|tester
operator|.
name|testIrregularilyStructured
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIrregularilyStructured
parameter_list|( )
block|{
name|testIrregularilyStructured
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIrregularilyStructured
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
specifier|final
name|String
name|testName
init|=
literal|"IrregularilyStructured"
decl_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running test "
operator|+
name|testName
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
comment|// Tell eXist where conf.xml is :
if|if
condition|(
name|EXIST_HOME
operator|!=
literal|""
condition|)
name|System
operator|.
name|setProperty
argument_list|(
literal|"exist.home"
argument_list|,
name|EXIST_HOME
argument_list|)
expr_stmt|;
name|Class
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
name|effectiveSiblingCount
operator|=
name|populate
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|printTime
argument_list|()
expr_stmt|;
name|resource
operator|.
name|setContentAsDOM
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|printTime
argument_list|()
expr_stmt|;
name|coll
operator|.
name|storeResource
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
literal|"TEST> stored Resource "
operator|+
name|name
argument_list|)
expr_stmt|;
name|printTime
argument_list|()
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST> retrieved Resource "
operator|+
name|name
argument_list|)
expr_stmt|;
name|printTime
argument_list|()
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
try|try
block|{
name|bytes
operator|=
name|s
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|bytes
operator|=
name|s
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
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
name|Element
name|documentElement
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
name|documentElement
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
name|documentElement
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
name|assertions
argument_list|(
name|documentElement
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
literal|"TEST> "
operator|+
name|testName
operator|+
literal|" : PASSED"
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
literal|"TEST> "
operator|+
name|testName
operator|+
literal|" : FAILED"
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
name|printTime
argument_list|()
expr_stmt|;
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
name|printTime
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
specifier|private
name|void
name|printTime
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Current ellapsed time : "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|/
literal|1000.f
argument_list|)
expr_stmt|;
block|}
comment|/** Assertions and output: */
specifier|private
name|void
name|assertions
parameter_list|(
name|Element
name|documentElement
parameter_list|)
block|{
name|int
name|computedSiblingCount
init|=
name|documentElement
operator|.
name|getChildNodes
argument_list|()
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|int
name|computedDepth
init|=
operator|(
operator|(
name|Element
operator|)
name|deepBranch
operator|)
operator|.
name|getElementsByTagName
argument_list|(
literal|"element"
argument_list|)
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|int
name|computedElementCount
init|=
name|documentElement
operator|.
name|getElementsByTagName
argument_list|(
literal|"element"
argument_list|)
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" documentElement.getChildNodes().getLength(): "
operator|+
name|computedSiblingCount
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" documentElement.getElementsByTagName(\"element\").getLength(): "
operator|+
name|computedElementCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"siblingCount"
argument_list|,
name|effectiveSiblingCount
argument_list|,
name|computedSiblingCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"depth"
argument_list|,
name|depth
operator|*
name|arity
operator|+
name|depth
argument_list|,
name|computedDepth
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST> assertions PASSED"
argument_list|)
expr_stmt|;
name|printTime
argument_list|()
expr_stmt|;
comment|// dumpCatabaseContent(n);
block|}
comment|/** This one provokes the Exception */
specifier|private
name|int
name|populate
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|int
name|childrenCount
init|=
name|addChildren
argument_list|(
name|doc
argument_list|,
name|siblingCount
argument_list|)
decl_stmt|;
comment|// Add a long fat branch at root's first child :
name|deepBranch
operator|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
name|effectiveDepth
operator|=
name|addFatBranch
argument_list|(
name|doc
argument_list|,
name|deepBranch
argument_list|,
name|depth
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST> populate() done."
argument_list|)
expr_stmt|;
return|return
name|childrenCount
return|;
block|}
comment|/** This one doesn't provoke the Exception */
specifier|private
name|int
name|populateOK
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|int
name|childrenCount
init|=
name|addChildren
argument_list|(
name|doc
argument_list|,
name|siblingCount
argument_list|)
decl_stmt|;
comment|// Add large branches at root's first and last children :
name|addBranch
argument_list|(
name|doc
argument_list|,
name|doc
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getFirstChild
argument_list|()
argument_list|,
name|depth
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|deepBranch
operator|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getLastChild
argument_list|()
expr_stmt|;
name|effectiveDepth
operator|=
name|addBranch
argument_list|(
name|doc
argument_list|,
name|deepBranch
argument_list|,
name|depth
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Element
name|documentElement
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
comment|// Add (small) branches everywhere at level 1 :
name|int
name|firstLevelWidth
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getChildNodes
argument_list|()
operator|.
name|getLength
argument_list|()
decl_stmt|;
block|{
name|Node
name|current
init|=
name|documentElement
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|firstLevelWidth
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|addBranch
argument_list|(
name|doc
argument_list|,
name|current
argument_list|,
literal|10
argument_list|,
literal|"branch"
argument_list|)
expr_stmt|;
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Add level 2 siblings everywhere at level 1 :
block|{
name|Node
name|current
init|=
name|documentElement
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|firstLevelWidth
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|addChildren
argument_list|(
name|current
argument_list|,
name|arity
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST> "
operator|+
name|firstLevelWidth
operator|+
literal|" first Level elements populated."
argument_list|)
expr_stmt|;
return|return
name|childrenCount
return|;
block|}
specifier|private
name|int
name|addBranch
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Node
name|branchNode
parameter_list|,
name|int
name|depth
parameter_list|,
name|String
name|elementName
parameter_list|)
block|{
name|int
name|rdepth
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|branchNode
operator|!=
literal|null
condition|)
block|{
name|Node
name|current
init|=
name|branchNode
decl_stmt|;
if|if
condition|(
name|elementName
operator|==
literal|null
operator|||
name|elementName
operator|==
literal|""
condition|)
name|elementName
operator|=
literal|"element"
expr_stmt|;
if|if
condition|(
name|randomSizes
condition|)
name|rdepth
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|depth
argument_list|)
expr_stmt|;
else|else
name|rdepth
operator|=
name|depth
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|rdepth
condition|;
name|j
operator|++
control|)
block|{
name|Element
name|el
init|=
name|doc
operator|.
name|createElement
argument_list|(
name|elementName
argument_list|)
decl_stmt|;
name|current
operator|.
name|appendChild
argument_list|(
name|el
argument_list|)
expr_stmt|;
name|current
operator|=
name|el
expr_stmt|;
block|}
block|}
return|return
name|rdepth
return|;
block|}
specifier|private
name|int
name|addFatBranch
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Node
name|branchNode
parameter_list|,
name|int
name|depth
parameter_list|,
name|String
name|elementName
parameter_list|)
block|{
name|int
name|rdepth
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|branchNode
operator|!=
literal|null
condition|)
block|{
name|Node
name|current
init|=
name|branchNode
decl_stmt|;
if|if
condition|(
name|elementName
operator|==
literal|null
operator|||
name|elementName
operator|==
literal|""
condition|)
name|elementName
operator|=
literal|"element"
expr_stmt|;
if|if
condition|(
name|randomSizes
condition|)
name|rdepth
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|depth
argument_list|)
expr_stmt|;
else|else
name|rdepth
operator|=
name|depth
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|rdepth
condition|;
name|j
operator|++
control|)
block|{
name|Element
name|el
init|=
name|doc
operator|.
name|createElement
argument_list|(
name|elementName
argument_list|)
decl_stmt|;
name|addChildren
argument_list|(
name|el
argument_list|,
name|arity
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|current
operator|.
name|appendChild
argument_list|(
name|el
argument_list|)
expr_stmt|;
name|current
operator|=
name|el
expr_stmt|;
block|}
block|}
return|return
name|rdepth
return|;
block|}
comment|/** 	 * @param doc 	 * @param i 	 */
specifier|private
name|int
name|addChildren
parameter_list|(
name|Document
name|doc
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|Element
name|rootElem
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|appendChild
argument_list|(
name|rootElem
argument_list|)
expr_stmt|;
return|return
name|addChildren
argument_list|(
name|rootElem
argument_list|,
name|length
argument_list|,
name|doc
argument_list|)
return|;
block|}
specifier|private
name|int
name|addChildren
parameter_list|(
name|Node
name|rootElem
parameter_list|,
name|int
name|length
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|int
name|rlength
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|rootElem
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|randomSizes
condition|)
name|rlength
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
else|else
name|rlength
operator|=
name|length
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|rlength
condition|;
name|j
operator|++
control|)
block|{
name|Element
name|el
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"element"
argument_list|)
decl_stmt|;
name|rootElem
operator|.
name|appendChild
argument_list|(
name|el
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rlength
return|;
block|}
specifier|private
name|void
name|dumpCatabaseContent
parameter_list|(
name|Node
name|n
parameter_list|)
throws|throws
name|TransformerConfigurationException
throws|,
name|TransformerFactoryConfigurationError
throws|,
name|TransformerException
block|{
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
name|IndexingTest
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
block|}
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

