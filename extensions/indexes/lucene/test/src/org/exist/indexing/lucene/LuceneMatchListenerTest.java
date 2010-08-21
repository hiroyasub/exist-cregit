begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
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
name|NamespaceContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|SimpleNamespaceContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLAssert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|TestUtils
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
name|CollectionConfigurationManager
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
name|EXistOutputKeys
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
name|test
operator|.
name|TestConstants
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
name|ConfigurationHelper
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
name|exist
operator|.
name|xquery
operator|.
name|XPathException
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
name|NodeValue
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|util
operator|.
name|HashMap
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

begin_class
specifier|public
class|class
name|LuceneMatchListenerTest
block|{
specifier|private
specifier|static
name|String
name|XML
init|=
literal|"<root>"
operator|+
literal|"<para>some paragraph with<hi>mixed</hi> content.</para>"
operator|+
literal|"<para>another paragraph with<note><hi>nested</hi> inner</note> elements.</para>"
operator|+
literal|"<para>a third paragraph with<term>term</term>.</para>"
operator|+
literal|"<para>double match double match</para>"
operator|+
literal|"</root>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML1
init|=
literal|"<article>"
operator|+
literal|"<head>The<b>title</b>of it</head>"
operator|+
literal|"<p>A simple<note>sic</note> paragraph with<hi>highlighted</hi> text<note>and a note</note> to be ignored.</p>"
operator|+
literal|"<p>Paragraphs with<s>mix</s><s>ed</s> content are<s>danger</s>ous.</p>"
operator|+
literal|"</article>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|CONF1
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<fulltext default=\"none\">"
operator|+
literal|"</fulltext>"
operator|+
literal|"<text qname=\"para\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|CONF2
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<fulltext default=\"none\">"
operator|+
literal|"</fulltext>"
operator|+
literal|"<text qname=\"para\"/>"
operator|+
literal|"<text qname=\"term\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|CONF3
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<fulltext default=\"none\">"
operator|+
literal|"</fulltext>"
operator|+
literal|"<text qname=\"hi\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|CONF4
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index xmlns:tei=\"http://www.tei-c.org/ns/1.0\">"
operator|+
literal|"<fulltext default=\"none\" attributes=\"no\">"
operator|+
literal|"</fulltext>"
operator|+
literal|"<lucene>"
operator|+
literal|"<text qname=\"p\">"
operator|+
literal|"<ignore qname=\"note\"/>"
operator|+
literal|"</text>"
operator|+
literal|"<text qname=\"head\"/>"
operator|+
literal|"<inline qname=\"s\"/>"
operator|+
literal|"</lucene>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|MATCH_START
init|=
literal|"<exist:match xmlns:exist=\"http://exist.sourceforge.net/NS/exist\">"
decl_stmt|;
specifier|private
specifier|static
name|String
name|MATCH_END
init|=
literal|"</exist:match>"
decl_stmt|;
specifier|private
specifier|static
name|BrokerPool
name|pool
decl_stmt|;
comment|/**      * Test match highlighting for index configured by QName, e.g.      *&lt;create qname="a"/&gt;.      */
annotation|@
name|Test
specifier|public
name|void
name|indexByQName
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|configureAndStore
argument_list|(
name|CONF2
argument_list|,
name|XML
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
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//para[ft:query(., 'mixed')]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|result
init|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<para>some paragraph with<hi>"
operator|+
name|MATCH_START
operator|+
literal|"mixed"
operator|+
name|MATCH_END
operator|+
literal|"</hi> content.</para>"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//para[ft:query(., '+nested +inner +elements')]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<para>another paragraph with<note><hi>"
operator|+
name|MATCH_START
operator|+
literal|"nested"
operator|+
name|MATCH_END
operator|+
literal|"</hi> "
operator|+
name|MATCH_START
operator|+
literal|"inner"
operator|+
name|MATCH_END
operator|+
literal|"</note> "
operator|+
name|MATCH_START
operator|+
literal|"elements"
operator|+
name|MATCH_END
operator|+
literal|".</para>"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//para[ft:query(term, 'term')]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<para>a third paragraph with<term>"
operator|+
name|MATCH_START
operator|+
literal|"term"
operator|+
name|MATCH_END
operator|+
literal|"</term>.</para>"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//para[ft:query(., '+double +match')]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<para>"
operator|+
name|MATCH_START
operator|+
literal|"double"
operator|+
name|MATCH_END
operator|+
literal|" "
operator|+
name|MATCH_START
operator|+
literal|"match"
operator|+
name|MATCH_END
operator|+
literal|" "
operator|+
name|MATCH_START
operator|+
literal|"double"
operator|+
name|MATCH_END
operator|+
literal|" "
operator|+
name|MATCH_START
operator|+
literal|"match"
operator|+
name|MATCH_END
operator|+
literal|"</para>"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"for $para in //para[ft:query(., '+double +match')] return\n"
operator|+
literal|"<hit>{$para}</hit>"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<hit><para>"
operator|+
name|MATCH_START
operator|+
literal|"double"
operator|+
name|MATCH_END
operator|+
literal|" "
operator|+
name|MATCH_START
operator|+
literal|"match"
operator|+
name|MATCH_END
operator|+
literal|" "
operator|+
name|MATCH_START
operator|+
literal|"double"
operator|+
name|MATCH_END
operator|+
literal|" "
operator|+
name|MATCH_START
operator|+
literal|"match"
operator|+
name|MATCH_END
operator|+
literal|"</para></hit>"
argument_list|,
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
name|Test
specifier|public
name|void
name|matchInAncestor
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|configureAndStore
argument_list|(
name|CONF1
argument_list|,
name|XML
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
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//para[ft:query(., 'mixed')]/hi"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|result
init|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertXpathEvaluatesTo
argument_list|(
literal|"1"
argument_list|,
literal|"count(//exist:match)"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//para[ft:query(., 'nested')]/note"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertXpathEvaluatesTo
argument_list|(
literal|"1"
argument_list|,
literal|"count(//hi/exist:match)"
argument_list|,
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
name|Test
specifier|public
name|void
name|matchInDescendant
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|configureAndStore
argument_list|(
name|CONF3
argument_list|,
name|XML
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
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//hi[ft:query(., 'mixed')]/ancestor::para"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|result
init|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertXpathEvaluatesTo
argument_list|(
literal|"1"
argument_list|,
literal|"count(//exist:match)"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//hi[ft:query(., 'nested')]/parent::note"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertXpathEvaluatesTo
argument_list|(
literal|"1"
argument_list|,
literal|"count(//hi/exist:match)"
argument_list|,
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
name|Test
specifier|public
name|void
name|inlineNodes
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|configureAndStore
argument_list|(
name|CONF4
argument_list|,
name|XML1
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
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//p[ft:query(., 'mixed')]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|result
init|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<p>Paragraphs with<s>"
operator|+
name|MATCH_START
operator|+
literal|"mix"
operator|+
name|MATCH_END
operator|+
literal|"</s><s>ed</s> content are<s>danger</s>ous.</p>"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//p[ft:query(., 'ignored')]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<p>A simple<note>sic</note> paragraph with<hi>highlighted</hi> text<note>and a note</note> to be "
operator|+
name|MATCH_START
operator|+
literal|"ignored"
operator|+
name|MATCH_END
operator|+
literal|".</p>"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//p[ft:query(., 'highlighted')]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<p>A simple<note>sic</note> paragraph with<hi>"
operator|+
name|MATCH_START
operator|+
literal|"highlighted"
operator|+
name|MATCH_END
operator|+
literal|"</hi> text<note>and a note</note> to be "
operator|+
literal|"ignored.</p>"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//p[ft:query(., 'highlighted')]/hi"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<hi>"
operator|+
name|MATCH_START
operator|+
literal|"highlighted"
operator|+
name|MATCH_END
operator|+
literal|"</hi>"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//head[ft:query(., 'title')]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RESULT: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertEquals
argument_list|(
literal|"<head>The<b>"
operator|+
name|MATCH_START
operator|+
literal|"title"
operator|+
name|MATCH_END
operator|+
literal|"</b>of it</head>"
argument_list|,
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
name|BeforeClass
specifier|public
specifier|static
name|void
name|startDB
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
name|File
name|confFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"conf.xml"
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|confFile
operator|.
name|getAbsolutePath
argument_list|()
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
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
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
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|transact
operator|!=
literal|null
condition|)
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
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"exist"
argument_list|,
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|)
expr_stmt|;
name|NamespaceContext
name|ctx
init|=
operator|new
name|SimpleNamespaceContext
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|XMLUnit
operator|.
name|setXpathNamespaceContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|closeDB
parameter_list|()
block|{
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pool
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|configureAndStore
parameter_list|(
name|String
name|config
parameter_list|,
name|String
name|data
parameter_list|)
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
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|CollectionConfigurationManager
name|mgr
init|=
name|pool
operator|.
name|getConfigurationManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addConfiguration
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|root
argument_list|,
name|config
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
literal|"test_matches.xml"
argument_list|)
argument_list|,
name|XML
argument_list|)
decl_stmt|;
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
name|data
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
specifier|private
name|String
name|queryResult2String
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Sequence
name|seq
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
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
literal|"no"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|HIGHLIGHT_MATCHES
argument_list|,
literal|"elements"
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
name|serializer
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
return|return
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

