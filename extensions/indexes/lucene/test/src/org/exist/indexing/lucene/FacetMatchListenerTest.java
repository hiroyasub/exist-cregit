begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|params
operator|.
name|FacetSearchParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|CountFacetRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|FacetResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|FacetResultNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|exist
operator|.
name|dom
operator|.
name|DocumentSet
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
name|NewArrayNodeSet
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
name|NodeProxy
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
name|NodeSet
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
name|value
operator|.
name|NodeValue
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
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|FacetMatchListenerTest
extends|extends
name|FacetAbstractTest
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metas1
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
static|static
block|{
name|metas1
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"draft"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metas2
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
static|static
block|{
name|metas2
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"final"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkFacet2
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facets
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|facets
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResult
name|facet
init|=
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|facet
operator|.
name|getNumValidDescendants
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResultNode
name|node
init|=
name|facet
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|node
operator|.
name|value
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"status"
argument_list|,
name|node
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResultNode
argument_list|>
name|subResults
init|=
name|node
operator|.
name|subResults
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|subResults
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|node
operator|.
name|value
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"status/final"
argument_list|,
name|node
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkFacet
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facets
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|facets
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResult
name|facet
init|=
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|facet
operator|.
name|getNumValidDescendants
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResultNode
name|node
init|=
name|facet
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|node
operator|.
name|value
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"status"
argument_list|,
name|node
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResultNode
argument_list|>
name|subResults
init|=
name|node
operator|.
name|subResults
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|subResults
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|node
operator|.
name|value
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"status/final"
argument_list|,
name|node
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|subResults
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|node
operator|.
name|value
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"status/draft"
argument_list|,
name|node
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|DocumentSet
name|docs
init|=
name|configureAndStore
argument_list|(
name|CONF2
argument_list|,
operator|new
name|Resource
index|[]
block|{
operator|new
name|Resource
argument_list|(
literal|"test1.xml"
argument_list|,
name|XML
argument_list|,
name|metas1
argument_list|)
block|,
operator|new
name|Resource
argument_list|(
literal|"test2.xml"
argument_list|,
name|XML
argument_list|,
name|metas2
argument_list|)
block|,
operator|new
name|Resource
argument_list|(
literal|"test3.xml"
argument_list|,
name|XML1
argument_list|,
name|metas2
argument_list|)
block|,                 }
argument_list|)
decl_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
name|db
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|LuceneIndexWorker
name|worker
init|=
operator|(
name|LuceneIndexWorker
operator|)
name|broker
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexId
argument_list|(
name|LuceneIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
decl_stmt|;
name|String
name|result
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"status"
argument_list|)
argument_list|,
literal|10
argument_list|)
comment|//                    new CountFacetRequest(new CategoryPath("Author"), 10)
argument_list|)
decl_stmt|;
name|CountAndCollect
name|cb
init|=
operator|new
name|CountAndCollect
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QName
argument_list|>
name|qnames
init|=
operator|new
name|ArrayList
argument_list|<
name|QName
argument_list|>
argument_list|()
decl_stmt|;
name|qnames
operator|.
name|add
argument_list|(
operator|new
name|QName
argument_list|(
literal|"para"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|//query without facet filter
name|results
operator|=
name|QueryNodes
operator|.
name|query
argument_list|(
name|worker
argument_list|,
name|docs
argument_list|,
name|qnames
argument_list|,
literal|1
argument_list|,
literal|"mixed"
argument_list|,
name|fsp
argument_list|,
literal|null
argument_list|,
name|cb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cb
operator|.
name|count
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|cb
operator|.
name|set
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
block|}
name|checkFacet
argument_list|(
name|results
argument_list|)
expr_stmt|;
name|cb
operator|.
name|count
operator|=
literal|0
expr_stmt|;
comment|//query with facet filter
name|results
operator|=
name|QueryNodes
operator|.
name|query
argument_list|(
name|worker
argument_list|,
name|docs
argument_list|,
name|qnames
argument_list|,
literal|1
argument_list|,
literal|"mixed AND status:final"
argument_list|,
name|fsp
argument_list|,
literal|null
argument_list|,
name|cb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cb
operator|.
name|count
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResult2String
argument_list|(
name|broker
argument_list|,
name|cb
operator|.
name|set
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|checkFacet2
argument_list|(
name|results
argument_list|)
expr_stmt|;
name|cb
operator|.
name|count
operator|=
literal|0
expr_stmt|;
comment|//            seq = xquery.execute("//para[ft:query(., '+nested +inner +elements')]", null, AccessContext.TEST);
comment|//            assertNotNull(seq);
comment|//            assertEquals(1, seq.getItemCount());
comment|//            result = queryResult2String(broker, seq);
comment|//            System.out.println("RESULT: " + result);
comment|//            XMLAssert.assertEquals("<para>another paragraph with<note><hi>" + MATCH_START + "nested" +
comment|//                    MATCH_END + "</hi> " + MATCH_START +
comment|//                    "inner" + MATCH_END + "</note> " + MATCH_START + "elements" + MATCH_END + ".</para>", result);
comment|//
comment|//            seq = xquery.execute("//para[ft:query(term, 'term')]", null, AccessContext.TEST);
comment|//            assertNotNull(seq);
comment|//            assertEquals(1, seq.getItemCount());
comment|//            result = queryResult2String(broker, seq);
comment|//            System.out.println("RESULT: " + result);
comment|//            XMLAssert.assertEquals("<para>a third paragraph with<term>" + MATCH_START + "term" + MATCH_END +
comment|//                    "</term>.</para>", result);
comment|//
comment|//            seq = xquery.execute("//para[ft:query(., '+double +match')]", null, AccessContext.TEST);
comment|//            assertNotNull(seq);
comment|//            assertEquals(1, seq.getItemCount());
comment|//            result = queryResult2String(broker, seq);
comment|//            System.out.println("RESULT: " + result);
comment|//            XMLAssert.assertEquals("<para>" + MATCH_START + "double" + MATCH_END + " " +
comment|//                    MATCH_START + "match" + MATCH_END + " " + MATCH_START + "double" + MATCH_END + " " +
comment|//                    MATCH_START + "match" + MATCH_END + "</para>", result);
comment|//
comment|//            seq = xquery.execute(
comment|//                    "for $para in //para[ft:query(., '+double +match')] return\n" +
comment|//                            "<hit>{$para}</hit>", null, AccessContext.TEST);
comment|//            assertNotNull(seq);
comment|//            assertEquals(1, seq.getItemCount());
comment|//            result = queryResult2String(broker, seq);
comment|//            System.out.println("RESULT: " + result);
comment|//            XMLAssert.assertEquals("<hit><para>" + MATCH_START + "double" + MATCH_END + " " +
comment|//                    MATCH_START + "match" + MATCH_END + " " + MATCH_START + "double" + MATCH_END + " " +
comment|//                    MATCH_START + "match" + MATCH_END + "</para></hit>", result);
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
name|db
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
name|NodeValue
name|node
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
name|node
argument_list|)
return|;
block|}
specifier|protected
class|class
name|CountAndCollect
implements|implements
name|SearchCallback
argument_list|<
name|NodeProxy
argument_list|>
block|{
name|NodeSet
name|set
init|=
operator|new
name|NewArrayNodeSet
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|found
parameter_list|(
name|NodeProxy
name|node
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

