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
name|memtree
operator|.
name|Receiver
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
name|file
init|=
literal|"samples/biblio.rdf"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|xml
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
throws|throws
name|Exception
block|{
name|Receiver
name|receiver
init|=
operator|new
name|Receiver
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
name|xml
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

