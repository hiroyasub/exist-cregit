begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|IndexInfo
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
name|BrokerPool
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
name|storage
operator|.
name|lock
operator|.
name|Lock
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
name|txn
operator|.
name|TransactionManager
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
name|txn
operator|.
name|Txn
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
name|Configuration
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
name|Attr
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
name|NamedNodeMap
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

begin_comment
comment|/**  * Tests basic DOM methods like getChildNodes(), getAttribute() ...  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|NodeTest
extends|extends
name|XMLTestCase
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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|NodeTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|String
name|XML
init|=
literal|"<!-- doc starts here -->"
operator|+
literal|"<test xmlns:ns=\"http://foo.org\">"
operator|+
literal|"<a ns:a=\"1\" ns:b=\"m\">abc</a>"
operator|+
literal|"<b ns:a=\"2\">def</b>"
operator|+
literal|"<c>ghi</c>"
operator|+
literal|"<d>jkl</d>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|private
name|Collection
name|root
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|testDocument
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|doc
operator|=
name|root
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|NodeList
name|children
init|=
name|doc
operator|.
name|getChildNodes
argument_list|()
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
name|IStoredNode
name|node
init|=
operator|(
name|IStoredNode
operator|)
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|node
operator|.
name|getNodeId
argument_list|()
operator|+
literal|": "
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
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
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testChildAxis
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|doc
operator|=
name|root
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|Element
name|rootNode
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing getChildNodes() ..."
argument_list|)
expr_stmt|;
name|NodeList
name|cl
init|=
name|rootNode
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|IStoredNode
operator|)
name|rootNode
operator|)
operator|.
name|getChildCount
argument_list|()
argument_list|,
name|cl
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|cl
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cl
operator|.
name|item
argument_list|(
literal|1
argument_list|)
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing getFirstChild() ..."
argument_list|)
expr_stmt|;
name|StoredNode
name|node
init|=
operator|(
name|StoredNode
operator|)
name|cl
operator|.
name|item
argument_list|(
literal|1
argument_list|)
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|node
operator|.
name|getNodeValue
argument_list|()
argument_list|,
literal|"def"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing getChildNodes() ..."
argument_list|)
expr_stmt|;
name|node
operator|=
operator|(
name|StoredNode
operator|)
name|cl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|node
operator|.
name|getChildCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|cl
operator|=
name|node
operator|.
name|getChildNodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cl
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cl
operator|.
name|item
argument_list|(
literal|2
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing getParentNode() ..."
argument_list|)
expr_stmt|;
name|Node
name|parent
init|=
name|cl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getParentNode
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parent
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|parent
operator|=
name|parent
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parent
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"test"
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSiblingAxis
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testSiblingAxis() ..."
argument_list|)
expr_stmt|;
name|doc
operator|=
name|root
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|Element
name|rootNode
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|Element
name|child
init|=
operator|(
name|Element
operator|)
name|rootNode
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|Node
name|sibling
init|=
name|child
operator|.
name|getNextSibling
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|sibling
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sibling
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
while|while
condition|(
name|sibling
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
name|sibling
argument_list|)
expr_stmt|;
name|sibling
operator|=
name|sibling
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
name|NodeList
name|cl
init|=
name|rootNode
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|sibling
operator|=
name|cl
operator|.
name|item
argument_list|(
literal|2
argument_list|)
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sibling = "
operator|+
name|sibling
argument_list|)
expr_stmt|;
name|sibling
operator|=
name|sibling
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
comment|// should be null - there's no following sibling
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sibling = "
operator|+
name|sibling
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|sibling
operator|=
name|cl
operator|.
name|item
argument_list|(
literal|3
argument_list|)
expr_stmt|;
while|while
condition|(
name|sibling
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
name|sibling
argument_list|)
expr_stmt|;
name|sibling
operator|=
name|sibling
operator|.
name|getPreviousSibling
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testSiblingAxis(): PASS"
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testAttributeAxis
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|doc
operator|=
name|root
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|Element
name|rootNode
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|Element
name|first
init|=
operator|(
name|Element
operator|)
name|rootNode
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|getAttribute
argument_list|(
literal|"ns:a"
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|getAttributeNS
argument_list|(
literal|"http://foo.org"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|Attr
name|attr
init|=
name|first
operator|.
name|getAttributeNode
argument_list|(
literal|"ns:a"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|attr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getLocalName
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
literal|"http://foo.org"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|Node
name|parent
init|=
name|attr
operator|.
name|getOwnerElement
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parent
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|parent
operator|=
name|attr
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parent
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|attr
operator|=
name|first
operator|.
name|getAttributeNodeNS
argument_list|(
literal|"http://foo.org"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|attr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getLocalName
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
literal|"http://foo.org"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|NamedNodeMap
name|map
init|=
name|first
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|getLength
argument_list|()
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
name|map
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|map
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|=
operator|(
name|Attr
operator|)
name|map
operator|.
name|getNamedItemNS
argument_list|(
literal|"http://foo.org"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|attr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getLocalName
argument_list|()
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
literal|"http://foo.org"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"m"
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Deprecated
specifier|public
name|void
name|testVisitor
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testVisitor() ..."
argument_list|)
expr_stmt|;
name|doc
operator|=
name|root
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|StoredNode
name|rootNode
init|=
operator|(
name|StoredNode
operator|)
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|NodeVisitor
name|visitor
init|=
operator|new
name|NodeVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|IStoredNode
name|node
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|node
operator|.
name|getNodeId
argument_list|()
operator|+
literal|"\t"
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
empty_stmt|;
block|}
decl_stmt|;
name|rootNode
operator|.
name|accept
argument_list|(
name|visitor
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
literal|null
decl_stmt|;
name|Txn
name|transaction
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|startDB
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transact
argument_list|)
expr_stmt|;
name|transaction
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NodeTest#setUp ..."
argument_list|)
expr_stmt|;
name|root
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|root
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|,
name|XML
argument_list|)
decl_stmt|;
comment|//TODO : unlock the collection here ?
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|root
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NodeTest#setUp finished."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
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
finally|finally
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerPool
name|startDB
parameter_list|()
block|{
name|String
name|home
decl_stmt|,
name|file
init|=
literal|"conf.xml"
decl_stmt|;
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
expr_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|file
argument_list|,
name|home
argument_list|)
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
return|return
name|BrokerPool
operator|.
name|getInstance
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
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
literal|null
decl_stmt|;
name|Txn
name|transaction
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transact
argument_list|)
expr_stmt|;
name|transaction
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"BasicNodeSetTest#tearDown>>>"
argument_list|)
expr_stmt|;
name|root
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
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
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
name|pool
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

