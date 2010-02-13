begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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
name|XMLFilenameFilter
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
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|IndexQueryService
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
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|ResourceSet
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
name|XMLDBException
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
name|XQueryService
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
name|IOException
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|OptimizerTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|OPTIMIZE
init|=
literal|"declare option exist:optimize 'enable=yes';"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NO_OPTIMIZE
init|=
literal|"declare option exist:optimize 'enable=no';"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NAMESPACES
init|=
literal|"declare namespace mods='http://www.loc.gov/mods/v3';"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MSG_OPT_ERROR
init|=
literal|"Optimized query should return same number of results."
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<root>"
operator|+
literal|"<a><b>one</b></a>"
operator|+
literal|"<a><c><b>one</b></c></a>"
operator|+
literal|"<c><a><c><b>two</b></c></a></c>"
operator|+
literal|"</root>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index xmlns:mods=\"http://www.loc.gov/mods/v3\">"
operator|+
literal|"<fulltext default=\"none\">"
operator|+
literal|"<create qname=\"LINE\"/>"
operator|+
literal|"<create qname=\"SPEAKER\"/>"
operator|+
literal|"<create qname=\"mods:title\"/>"
operator|+
literal|"<create qname=\"mods:topic\"/>"
operator|+
literal|"</fulltext>"
operator|+
literal|"<create qname=\"b\" type=\"xs:string\"/>"
operator|+
literal|"<create qname=\"SPEAKER\" type=\"xs:string\"/>"
operator|+
literal|"<create qname=\"mods:internetMediaType\" type=\"xs:string\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|testCollection
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|nestedQuery
parameter_list|()
block|{
name|execute
argument_list|(
literal|"/root/a[descendant::b = 'one']"
argument_list|,
literal|true
argument_list|,
literal|"Inner b node should be returned."
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"/root/a[b = 'one']"
argument_list|,
literal|true
argument_list|,
literal|"Inner b node should not be returned."
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"/root/a[b = 'one']"
argument_list|,
literal|false
argument_list|,
literal|"Inner b node should not be returned."
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simplePredicates
parameter_list|()
block|{
name|int
name|r
init|=
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king']"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[SPEAKER = 'HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[SPEAKER = 'HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[descendant::SPEAKER = 'HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[descendant::SPEAKER = 'HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SCENE[descendant::LINE&= 'king']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SCENE[descendant::LINE&= 'king']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//LINE[.&= 'king']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//LINE[.&= 'king']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEAKER[. = 'HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEAKER[. = 'HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
comment|//        r = execute("//LINE[descendant-or-self::LINE&= 'king']", false);
comment|//        execute("//LINE[descendant-or-self::LINE&= 'king']", true, MSG_OPT_ERROR, r);
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEAKER[descendant-or-self::SPEAKER = 'HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEAKER[descendant-or-self::SPEAKER = 'HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH/LINE[.&= 'king']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH/LINE[.&= 'king']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//*[LINE&= 'king']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//*[LINE&= 'king']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//*[SPEAKER = 'HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//*[SPEAKER = 'HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|namespaces
parameter_list|()
block|{
name|int
name|r
init|=
name|execute
argument_list|(
literal|"//mods:mods/mods:titleInfo[mods:title&= 'ethnic']"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|execute
argument_list|(
literal|"//mods:mods/mods:titleInfo[mods:title&= 'ethnic']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods/mods:physicalDescription[mods:internetMediaType&= 'application/pdf']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods/mods:physicalDescription[mods:internetMediaType&= 'application/pdf']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods/mods:*[mods:title&= 'ethnic']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods/mods:*[mods:title&= 'ethnic']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simplePredicatesRegex
parameter_list|()
block|{
name|int
name|r
init|=
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'nor*']"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'nor*']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'skirts nor*']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'skirts nor*']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[near(LINE, 'skirts nor*', 2)]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[near(LINE, 'skirts nor*', 2)]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
comment|//Test old and new functions
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[fn:match-all(LINE, 'skirts', 'nor.*')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[fn:match-all(LINE, 'skirts', 'nor.*')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[text:match-all(LINE, ('skirts', 'nor.*'))]"
argument_list|,
literal|false
argument_list|,
literal|"Query should return same number of results."
argument_list|,
name|r
argument_list|)
expr_stmt|;
comment|//Test old and new functions
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[fn:match-any(LINE, 'skirts', 'nor.*')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[fn:match-any(LINE, 'skirts', 'nor.*')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[text:match-any(LINE, ('skirts', 'nor.*'), 'w')]"
argument_list|,
literal|false
argument_list|,
literal|"Query should return same number of results."
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[text:match-any(LINE, ('skirts', 'nor.*'), 'w')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[text:match-any(LINE, ('skirts', '^nor.*$'))]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[matches(SPEAKER, '^HAM.*')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[matches(SPEAKER, '^HAM.*')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[starts-with(SPEAKER, 'HAML')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[starts-with(SPEAKER, 'HAML')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[ends-with(SPEAKER, 'EO')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[ends-with(SPEAKER, 'EO')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[matches(descendant::SPEAKER, 'HAML.*')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[matches(descendant::SPEAKER, 'HAML.*')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|twoPredicates
parameter_list|()
block|{
name|int
name|r
init|=
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king'][SPEAKER='HAMLET']"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king'][SPEAKER='HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[SPEAKER='HAMLET'][LINE&= 'king']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[SPEAKER='HAMLET'][LINE&= 'king']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noOptimization
parameter_list|()
block|{
name|int
name|r
init|=
name|execute
argument_list|(
literal|"//mods:title[ancestor-or-self::mods:title&= 'ethnic']"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|execute
argument_list|(
literal|"//mods:title[ancestor-or-self::mods:title&= 'ethnic']"
argument_list|,
literal|true
argument_list|,
literal|"Ancestor axis should not be optimized."
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//node()[parent::mods:title&= 'ethnic']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//node()[parent::mods:title&= 'ethnic']"
argument_list|,
literal|true
argument_list|,
literal|"Parent axis should not be optimized."
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"/root//b[parent::c/b = 'two']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"/root//b[parent::c/b = 'two']"
argument_list|,
literal|true
argument_list|,
literal|"Parent axis should not be optimized."
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"/root//b[ancestor::a/c/b = 'two']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"/root//b[ancestor::a/c/b = 'two']"
argument_list|,
literal|true
argument_list|,
literal|"Ancestor axis should not be optimized."
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"/root//b[ancestor::a/b = 'two']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"/root//b[ancestor::a/b = 'two']"
argument_list|,
literal|true
argument_list|,
literal|"Ancestor axis should not be optimized."
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"/root//b[text()/parent::b = 'two']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"/root//b[text()/parent::b = 'two']"
argument_list|,
literal|true
argument_list|,
literal|"Parent axis should not be optimized."
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"/root//b[matches(text()/parent::b, 'two')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"/root//b[matches(text()/parent::b, 'two')]"
argument_list|,
literal|true
argument_list|,
literal|"Parent axis should not be optimized."
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|complexPaths
parameter_list|()
block|{
name|int
name|r
init|=
name|execute
argument_list|(
literal|"//mods:mods[mods:titleInfo/mods:title&= 'ethnic']"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[mods:titleInfo/mods:title&= 'ethnic']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[text:match-all(mods:titleInfo/mods:title, 'and')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[text:match-all(mods:titleInfo/mods:title, 'and')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[./mods:titleInfo/mods:title&= 'ethnic']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[./mods:titleInfo/mods:title&= 'ethnic']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[*/mods:title&= 'ethnic']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[*/mods:title&= 'ethnic']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[.//mods:title&= 'ethnic']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[.//mods:title&= 'ethnic']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[mods:physicalDescription/mods:internetMediaType = 'text/html']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[mods:physicalDescription/mods:internetMediaType = 'text/html']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[./mods:physicalDescription/mods:internetMediaType = 'text/html']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[./mods:physicalDescription/mods:internetMediaType = 'text/html']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[*/mods:internetMediaType = 'text/html']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[*/mods:internetMediaType = 'text/html']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[matches(mods:physicalDescription/mods:internetMediaType, 'text/html')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[matches(mods:physicalDescription/mods:internetMediaType, 'text/html')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:mods[matches(*/mods:internetMediaType, 'text/html')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:mods[matches(*/mods:internetMediaType, 'text/html')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reversePaths
parameter_list|()
block|{
name|int
name|r
init|=
name|execute
argument_list|(
literal|"/root//b/parent::c[b = 'two']"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"/root//b/parent::c[b = 'two']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//mods:url/ancestor::mods:mods[mods:titleInfo/mods:title&= 'and']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|17
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//mods:url/ancestor::mods:mods[mods:titleInfo/mods:title&= 'and']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|booleanOperator
parameter_list|()
block|{
name|int
name|r
init|=
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king'][SPEAKER='HAMLET']"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king' and SPEAKER='HAMLET']"
argument_list|,
literal|false
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king' and SPEAKER='HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king' or SPEAKER='HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king' or SPEAKER='HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'love' and LINE&= \"woman's\" and SPEAKER='HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'love' and LINE&= \"woman's\" and SPEAKER='HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[(LINE&= 'king' or LINE&= 'love') and SPEAKER='HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[(LINE&= 'king' or LINE&= 'love') and SPEAKER='HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[(LINE&= 'juliet' and LINE&= 'romeo') or SPEAKER='HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|368
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[(LINE&= 'juliet' and LINE&= 'romeo') or SPEAKER='HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[(LINE&= 'juliet' and LINE&= 'romeo') and SPEAKER='HAMLET']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[(LINE&= 'juliet' and LINE&= 'romeo') and SPEAKER='HAMLET']"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|=
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'juliet' or (LINE&= 'king' and SPEAKER='HAMLET')]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|65
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'juliet' or (LINE&= 'king' and SPEAKER='HAMLET')]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[true() and false()]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|execute
argument_list|(
literal|"//SPEECH[true() and true()]"
argument_list|,
literal|true
argument_list|,
name|MSG_OPT_ERROR
argument_list|,
literal|2628
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|execute
parameter_list|(
name|String
name|query
parameter_list|,
name|boolean
name|optimize
parameter_list|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--- Query: "
operator|+
name|query
operator|+
literal|"; Optimize: "
operator|+
name|Boolean
operator|.
name|toString
argument_list|(
name|optimize
argument_list|)
argument_list|)
expr_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
if|if
condition|(
name|optimize
condition|)
name|query
operator|=
name|OPTIMIZE
operator|+
name|query
expr_stmt|;
else|else
name|query
operator|=
name|NO_OPTIMIZE
operator|+
name|query
expr_stmt|;
name|query
operator|=
name|NAMESPACES
operator|+
name|query
expr_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-- Found: "
operator|+
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|int
operator|)
name|result
operator|.
name|getSize
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
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
literal|0
return|;
block|}
specifier|private
name|void
name|execute
parameter_list|(
name|String
name|query
parameter_list|,
name|boolean
name|optimize
parameter_list|,
name|String
name|message
parameter_list|,
name|int
name|expected
parameter_list|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--- Query: "
operator|+
name|query
operator|+
literal|"; Optimize: "
operator|+
name|Boolean
operator|.
name|toString
argument_list|(
name|optimize
argument_list|)
argument_list|)
expr_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
if|if
condition|(
name|optimize
condition|)
name|query
operator|=
name|NAMESPACES
operator|+
name|OPTIMIZE
operator|+
name|query
expr_stmt|;
else|else
name|query
operator|=
name|NAMESPACES
operator|+
name|NO_OPTIMIZE
operator|+
name|query
expr_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-- Found: "
operator|+
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|message
argument_list|,
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
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
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|initDatabase
parameter_list|()
block|{
try|try
block|{
comment|//Since we use the deprecated fn:match-all() function, we have to be sure is is enabled
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
name|FunctionFactory
operator|.
name|PROPERTY_DISABLE_DEPRECATED_FUNCTIONS
argument_list|,
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
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
comment|// initialize driver
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
literal|"org.exist.xmldb.DatabaseImpl"
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
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
name|testCollection
operator|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
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
name|XML
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
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
name|File
name|existDir
init|=
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
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/shakespeare"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|canRead
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to read samples directory"
argument_list|)
throw|;
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|XMLFilenameFilter
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Create resource from "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
name|dir
operator|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/mods"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|canRead
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to read samples directory"
argument_list|)
throw|;
name|files
operator|=
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|XMLFilenameFilter
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Create resource from "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
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
name|Assert
operator|.
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
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|shutdownDB
parameter_list|()
block|{
try|try
block|{
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|testCollection
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
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|testCollection
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tearDown PASSED"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

