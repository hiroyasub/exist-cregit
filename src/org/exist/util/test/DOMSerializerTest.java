begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Oct 26, 2003  *  * To change the template for this generated file go to  * Window - Preferences - Java - Code Generation - Code and Comments  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
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
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
comment|/**  * @author wolf  *  * To change the template for this generated type comment go to  * Window - Preferences - Java - Code Generation - Code and Comments  */
end_comment

begin_class
specifier|public
class|class
name|DOMSerializerTest
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
name|DOMSerializerTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DOMSerializerTest
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
name|testSerialize
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|file
argument_list|)
argument_list|)
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
name|doc
operator|.
name|getDocumentElement
argument_list|()
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
block|}
end_class

end_unit

