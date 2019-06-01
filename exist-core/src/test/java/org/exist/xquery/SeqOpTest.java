begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|Arrays
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
name|test
operator|.
name|ExistXmldbEmbeddedServer
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
name|junit
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
name|XPathQueryService
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
name|assertEquals
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
name|assertNotNull
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|SeqOpTest
block|{
specifier|private
specifier|static
name|XPathQueryService
name|query
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|c
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testReverseEmpty
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
literal|"reverse(())"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReverseAtomic1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|,
literal|"reverse(('a'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReverseAtomic2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b"
block|,
literal|"a"
block|}
argument_list|,
literal|"reverse(('a', 'b'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReverseNodes1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|}
argument_list|,
literal|"reverse(//a)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReverseNodes2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<b/>"
block|,
literal|"<a/>"
block|}
argument_list|,
literal|"reverse(/top/*)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReverseMixed
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"c"
block|,
literal|"<b/>"
block|,
literal|"<a/>"
block|}
argument_list|,
literal|"reverse((/top/*, 'c'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveEmpty1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
literal|"remove((), 1)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveEmpty2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
literal|"remove((), 0)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveEmpty3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
literal|"remove((), 42)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveOutOfBounds1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|"remove(('a', 'b'), 0)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveOutOfBounds2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|"remove(('a', 'b'), 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveOutOfBounds3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|"remove(('a', 'b'), -1)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveAtomic1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b"
block|,
literal|"c"
block|}
argument_list|,
literal|"remove(('a', 'b', 'c'), 1)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveAtomic2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"c"
block|}
argument_list|,
literal|"remove(('a', 'b', 'c'), 2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveAtomic3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|"remove(('a', 'b', 'c'), 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMixed1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<b/>"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|,
literal|"remove((/top/*, 'a', 'b', 'c'), 1)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMixed2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|,
literal|"remove((/top/*, 'a', 'b', 'c'), 2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMixed3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"<b/>"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|,
literal|"remove((/top/*, 'a', 'b', 'c'), 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveNodes1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/><c/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<b/>"
block|,
literal|"<c/>"
block|}
argument_list|,
literal|"remove(/top/*, 1)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveNodes2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/><c/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"<c/>"
block|}
argument_list|,
literal|"remove(/top/*, 2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveNodes3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><a/><b/><c/></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"<b/>"
block|}
argument_list|,
literal|"remove(/top/*, 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertEmpty1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
literal|"insert-before((), 1, ())"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertEmpty2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|,
literal|"insert-before((), 1, ('a'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertEmpty3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|,
literal|"insert-before(('a'), 1, ())"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertOutOfBounds1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"c"
block|,
literal|"d"
block|,
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|"insert-before(('a', 'b'), 0, ('c', 'd'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertOutOfBounds2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|}
argument_list|,
literal|"insert-before(('a', 'b'), 3, ('c', 'd'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertOutOfBounds3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|}
argument_list|,
literal|"insert-before(('a', 'b'), 4, ('c', 'd'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertAtomic1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"b"
block|}
argument_list|,
literal|"insert-before(('a', 'b'), 2, ('c', 'd'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertAtomic2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"c"
block|,
literal|"d"
block|,
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|"insert-before(('a', 'b'), 1, ('c', 'd'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertAtomic3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"b"
block|}
argument_list|,
literal|"insert-before(('a', 'b'), 2, ('a', 'b'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertNodes1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><x><a/><b/></x><y><c/><d/></y></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"<c/>"
block|,
literal|"<d/>"
block|,
literal|"<b/>"
block|}
argument_list|,
literal|"insert-before(/top/x/*, 2, /top/y/*)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertNodes2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><x><a/><b/></x><y><c/><d/></y></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<c/>"
block|,
literal|"<d/>"
block|,
literal|"<a/>"
block|,
literal|"<b/>"
block|}
argument_list|,
literal|"insert-before(/top/x/*, 1, /top/y/*)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertNodes3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><x><a/><b/></x><y><c/><d/></y></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"<b/>"
block|,
literal|"<c/>"
block|,
literal|"<d/>"
block|}
argument_list|,
literal|"insert-before(/top/x/*, 3, /top/y/*)"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: currently fails because duplicate nodes are removed
annotation|@
name|Test
specifier|public
name|void
name|testInsertNodes4
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><x><a/><b/></x><y><c/><d/></y></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"<a/>"
block|,
literal|"<b/>"
block|,
literal|"<b/>"
block|}
argument_list|,
literal|"insert-before(/top/x/*, 2, /top/x/*)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertMixed1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><x><a/><b/></x><y><c/><d/></y></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"c"
block|,
literal|"<b/>"
block|}
argument_list|,
literal|"insert-before(/top/x/*, 2, ('c'))"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: currently fails because duplicate nodes are removed
annotation|@
name|Test
specifier|public
name|void
name|testInsertMixed2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createDocument
argument_list|(
literal|"foo"
argument_list|,
literal|"<top><x><a/><b/></x><y><c/><d/></y></top>"
argument_list|)
expr_stmt|;
name|assertSeq
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<a/>"
block|,
literal|"<a/>"
block|,
literal|"<b/>"
block|,
literal|"<b/>"
block|,
literal|"c"
block|}
argument_list|,
literal|"insert-before((/top/x/*, 'c'), 2, /top/x/*)"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertSeq
parameter_list|(
name|String
index|[]
name|expected
parameter_list|,
name|String
name|q
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|rs
init|=
name|query
operator|.
name|query
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|rs
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|a
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|expected
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
operator|(
name|int
operator|)
name|rs
operator|.
name|getSize
argument_list|()
argument_list|)
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
name|rs
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|.
name|add
argument_list|(
name|rs
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|a
operator|.
name|equals
argument_list|(
name|r
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"expected "
operator|+
name|a
operator|+
literal|", got "
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|XMLResource
name|createDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|c
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
name|res
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|ClassRule
specifier|public
specifier|static
name|ExistXmldbEmbeddedServer
name|existXmldbEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setupTestCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
decl_stmt|;
specifier|final
name|CollectionManagementService
name|rootcms
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
name|c
operator|=
name|root
operator|.
name|getChildCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|rootcms
operator|.
name|removeCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|c
operator|=
name|rootcms
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|query
operator|=
operator|(
name|XPathQueryService
operator|)
name|c
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
decl_stmt|;
specifier|final
name|CollectionManagementService
name|rootcms
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
name|rootcms
operator|.
name|removeCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|query
operator|=
literal|null
expr_stmt|;
name|c
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
