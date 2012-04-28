begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Oct 19, 2003  *  * To change the template for this generated file go to  * Window - Preferences - Java - Code Generation - Code and Comments  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|memtree
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
name|exist
operator|.
name|dom
operator|.
name|QName
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
name|junit
operator|.
name|Test
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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  * To change the template for this generated type comment go to  * Window - Preferences - Java - Code Generation - Code and Comments  */
end_comment

begin_class
specifier|public
class|class
name|DOMTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<test count=\"1\" value=\"5543\" xmlns:x=\"http://foo.org\" xmlns=\"http://bla.org\"><x:title id=\"s1\">My title</x:title><paragraph>First paragraph</paragraph>"
operator|+
literal|"<section><title>subsection</title></section></test>"
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
name|DOMTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DOMTest
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
specifier|public
name|void
name|testDocumentBuilder
parameter_list|()
block|{
try|try
block|{
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|()
decl_stmt|;
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|XMLReader
name|reader
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|receiver
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
name|XML
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|receiver
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|Node
name|node
init|=
name|doc
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|DOMSerializer
name|serializer
init|=
operator|new
name|DOMSerializer
argument_list|(
name|writer
argument_list|,
literal|null
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
name|writer
operator|.
name|toString
argument_list|()
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
name|testGetChildNodes1
parameter_list|()
block|{
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"top"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
literal|"text"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|DocumentImpl
name|doc
init|=
name|builder
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|Node
name|top
init|=
name|doc
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Node
operator|.
name|ELEMENT_NODE
argument_list|,
name|top
operator|.
name|getNodeType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"top"
argument_list|,
name|top
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|top
operator|.
name|getChildNodes
argument_list|()
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetChildNodes2
parameter_list|()
block|{
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"top"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"child1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"child2"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|DocumentImpl
name|doc
init|=
name|builder
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|Node
name|top
init|=
name|doc
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Node
operator|.
name|ELEMENT_NODE
argument_list|,
name|top
operator|.
name|getNodeType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"top"
argument_list|,
name|top
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|top
operator|.
name|getChildNodes
argument_list|()
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetElementsByTagName
parameter_list|()
block|{
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"xquery"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"builtin-modules"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"class"
argument_list|,
literal|"class"
argument_list|,
literal|"string"
argument_list|,
literal|"org.exist.xquery.functions.util.UtilModule"
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"uri"
argument_list|,
literal|"uri"
argument_list|,
literal|"string"
argument_list|,
literal|"http://exist-db.org/xquery/util"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"module"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|attrs
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"class"
argument_list|,
literal|"class"
argument_list|,
literal|"string"
argument_list|,
literal|"org.exist.xquery.functions.request.RequestModule"
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"uri"
argument_list|,
literal|"uri"
argument_list|,
literal|"string"
argument_list|,
literal|"http://exist-db.org/xquery/request"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"module"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|attrs
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"string"
argument_list|,
literal|"stream"
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"string"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parameter"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|attrs
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"class"
argument_list|,
literal|"class"
argument_list|,
literal|"string"
argument_list|,
literal|"org.exist.xquery.functions.util.ResponseModule"
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"uri"
argument_list|,
literal|"uri"
argument_list|,
literal|"string"
argument_list|,
literal|"http://exist-db.org/xquery/response"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"module"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|attrs
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"class"
argument_list|,
literal|"class"
argument_list|,
literal|"string"
argument_list|,
literal|"org.exist.xquery.functions.util.SessionModule"
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
literal|"uri"
argument_list|,
literal|"uri"
argument_list|,
literal|"string"
argument_list|,
literal|"http://exist-db.org/xquery/session"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"module"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|DocumentImpl
name|doc
init|=
name|builder
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|Node
name|nXQuery
init|=
name|doc
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nXQuery
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nXQuery
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"xquery"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|nBuiltinModules
init|=
name|nXQuery
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nBuiltinModules
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nBuiltinModules
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"builtin-modules"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeList
name|nlModules
init|=
name|nBuiltinModules
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
name|nlModules
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|nModule
init|=
name|nlModules
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nModule
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nModule
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"module"
argument_list|)
argument_list|)
expr_stmt|;
name|Element
name|eModule
init|=
operator|(
name|Element
operator|)
name|nModule
decl_stmt|;
name|NodeList
name|nlParameter
init|=
name|eModule
operator|.
name|getElementsByTagName
argument_list|(
literal|"parameter"
argument_list|)
decl_stmt|;
if|if
condition|(
name|eModule
operator|.
name|getAttribute
argument_list|(
literal|"class"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"org.exist.xquery.functions.request.RequestModule"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nlParameter
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nlParameter
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|print
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|'<'
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
operator|+
literal|'>'
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|node
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default :
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"unknown node type"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|hasChildNodes
argument_list|()
condition|)
name|print
argument_list|(
name|node
operator|.
name|getFirstChild
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
end_class

end_unit

