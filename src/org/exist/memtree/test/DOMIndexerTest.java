begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|memtree
operator|.
name|DocumentImpl
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
name|SAXAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|AccessContext
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
name|serializers
operator|.
name|Serializer
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
name|exist
operator|.
name|xquery
operator|.
name|XQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceIterator
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
comment|/**  * Tests the serializing of constructed in-memory fragments.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DOMIndexerTest
extends|extends
name|TestCase
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
name|DOMIndexerTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<!-- A comment -->"
operator|+
literal|"<root xmlns=\"urn:foo\" id=\"1\">"
operator|+
literal|"<?php print('Hello'); ?>"
operator|+
literal|"<item xmlns:x=\"urn:x\" itemno=\"1\" partno=\"54\">"
operator|+
literal|"<name>City bike</name>"
operator|+
literal|"<x:price>555.60</x:price>"
operator|+
literal|"</item>"
operator|+
literal|"<item itemno=\"2\" partno=\"67\">"
operator|+
literal|"<name>Racing bike</name>"
operator|+
literal|"<x:price xmlns:x=\"urn:x\">600.99</x:price>"
operator|+
literal|"</item>"
operator|+
literal|"</root>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY
init|=
literal|"declare namespace f='urn:foo'; "
operator|+
literal|"let $a := "
operator|+
literal|"   ("
operator|+
literal|"<!-- My test data -->,"
operator|+
literal|"<section>"
operator|+
literal|"<title>Section 1</title>"
operator|+
literal|"<para>First paragraph</para>"
operator|+
literal|"</section>,"
operator|+
literal|"<section>"
operator|+
literal|"<title>Section 2</title>"
operator|+
literal|"<para>First paragraph in second section.</para>"
operator|+
literal|"           {collection('"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test')//f:item[@itemno='2']/f:name}"
operator|+
literal|"</section>"
operator|+
literal|"   ) "
operator|+
literal|"return"
operator|+
literal|"<result>{$a/title, $a/f:name, $a}</result>"
decl_stmt|;
specifier|public
name|void
name|testIndexer
parameter_list|()
block|{
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|DocumentImpl
name|doc
init|=
name|parse
argument_list|(
name|XML
argument_list|)
decl_stmt|;
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|User
name|user
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
decl_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
name|targetDoc
init|=
name|broker
operator|.
name|storeTempResource
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testIndexer(): "
operator|+
name|targetDoc
operator|.
name|printTreeLevelOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|serializer
operator|.
name|serialize
argument_list|(
name|targetDoc
argument_list|)
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
name|testStore
parameter_list|()
block|{
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|User
name|user
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
decl_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
literal|null
argument_list|,
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
literal|null
argument_list|,
name|broker
argument_list|,
literal|"test.xml"
argument_list|,
name|XML
argument_list|)
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
literal|null
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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
name|doc
init|=
name|info
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
literal|null
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStore(): "
operator|+
name|doc
operator|.
name|printTreeLevelOrder
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
specifier|public
name|void
name|testXQuery
parameter_list|()
block|{
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|Sequence
name|result
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|XQUERY
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found: "
operator|+
name|result
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|StringWriter
name|out
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
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
name|out
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|result
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|next
operator|.
name|toSAX
argument_list|(
name|broker
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
block|}
name|serializer
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|out
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
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
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
block|}
comment|/* (non-Javadoc)      * @see junit.framework.TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DocumentImpl
name|parse
parameter_list|(
name|String
name|input
parameter_list|)
block|{
try|try
block|{
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
comment|//        factory.setFeature("http://apache.org/xml/features/validation/schema", true);
comment|//        factory.setFeature("http://apache.org/xml/features/validation/dynamic", true);
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setProperty
argument_list|(
literal|"http://xml.org/sax/properties/lexical-handler"
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
return|return
operator|(
name|DocumentImpl
operator|)
name|adapter
operator|.
name|getDocument
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
block|}
end_class

end_unit

