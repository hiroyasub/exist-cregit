begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
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
name|start
operator|.
name|Main
import|;
end_import

begin_class
specifier|public
class|class
name|XQueryTest
extends|extends
name|TestCase
block|{
specifier|static
name|Main
name|mn
init|=
literal|null
decl_stmt|;
specifier|static
name|String
name|jetty_port
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.port"
argument_list|)
decl_stmt|;
specifier|static
name|String
name|localhost
init|=
literal|"http://localhost:"
operator|+
name|jetty_port
decl_stmt|;
specifier|static
name|String
name|query_url
init|=
name|localhost
operator|+
literal|"/exist/services/Query"
decl_stmt|;
specifier|static
name|String
name|admin_url
init|=
name|localhost
operator|+
literal|"/exist/services/Admin"
decl_stmt|;
name|String
name|testColl
init|=
literal|"/db/test"
decl_stmt|;
name|Query
name|query
decl_stmt|;
name|Admin
name|admin
decl_stmt|;
name|String
name|sessionId
decl_stmt|;
specifier|public
name|XQueryTest
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
name|void
name|testXQuery
parameter_list|()
throws|throws
name|RemoteException
block|{
name|admin
operator|.
name|removeCollection
argument_list|(
name|sessionId
argument_list|,
name|testColl
argument_list|)
expr_stmt|;
name|admin
operator|.
name|createCollection
argument_list|(
name|sessionId
argument_list|,
name|testColl
argument_list|)
expr_stmt|;
name|String
name|data
init|=
literal|"<test>"
operator|+
literal|"<fruit name='apple'/>"
operator|+
literal|"<fruit name='orange'/>"
operator|+
literal|"<fruit name='pear'/>"
operator|+
literal|"<fruit name='grape'/>"
operator|+
literal|"<fruit name='banana'/>"
operator|+
literal|"<fruit name='mango'/>"
operator|+
literal|"</test>"
decl_stmt|;
name|String
name|data1
init|=
literal|"<test>"
operator|+
literal|"<fruit name='guava'/>"
operator|+
literal|"<fruit name='quince'/>"
operator|+
literal|"<fruit name='pineapple'/>"
operator|+
literal|"<fruit name='mandarine'/>"
operator|+
literal|"<fruit name='persimmon'/>"
operator|+
literal|"<fruit name='pomegranate'/>"
operator|+
literal|"</test>"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====> Creating test documents"
argument_list|)
expr_stmt|;
name|admin
operator|.
name|store
argument_list|(
name|sessionId
argument_list|,
name|data
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|,
name|testColl
operator|+
literal|"/docA"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|admin
operator|.
name|store
argument_list|(
name|sessionId
argument_list|,
name|data1
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|,
name|testColl
operator|+
literal|"/docB"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====> getResource"
argument_list|)
expr_stmt|;
name|String
name|rd
init|=
name|query
operator|.
name|getResource
argument_list|(
name|sessionId
argument_list|,
name|testColl
operator|+
literal|"/docA"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rd
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====> listCollection"
argument_list|)
expr_stmt|;
name|Collection
name|coll
init|=
name|query
operator|.
name|listCollection
argument_list|(
name|sessionId
argument_list|,
name|testColl
argument_list|)
decl_stmt|;
name|String
index|[]
name|colls
init|=
name|coll
operator|.
name|getCollections
argument_list|()
operator|.
name|getElements
argument_list|()
decl_stmt|;
if|if
condition|(
name|colls
operator|!=
literal|null
condition|)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|colls
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  collection "
operator|+
name|colls
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|ress
init|=
name|coll
operator|.
name|getResources
argument_list|()
operator|.
name|getElements
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ress
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|ress
operator|!=
literal|null
condition|)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ress
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  resources "
operator|+
name|ress
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====> getResourceData"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|rd1
init|=
name|query
operator|.
name|getResourceData
argument_list|(
name|sessionId
argument_list|,
name|testColl
operator|+
literal|"/docB"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|String
argument_list|(
name|rd1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====> performing xquery with retrieve"
argument_list|)
expr_stmt|;
name|String
name|qry
init|=
literal|"for $a in collection('"
operator|+
name|testColl
operator|+
literal|"')/test/fruit return $a"
decl_stmt|;
name|assertEquals
argument_list|(
name|doXQuery
argument_list|(
name|qry
argument_list|)
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====> performing xquery with retrieveData"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doXQueryB
argument_list|(
name|qry
argument_list|)
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====> performing xquery with retrieveByDocument"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doXQueryC
argument_list|(
name|qry
argument_list|)
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====> performing xquery, expecting 0 hits"
argument_list|)
expr_stmt|;
name|String
name|qry1
init|=
literal|"for $a in collection('"
operator|+
name|testColl
operator|+
literal|"')/test/nuts return $a"
decl_stmt|;
name|assertEquals
argument_list|(
name|doXQuery
argument_list|(
name|qry1
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|String
name|qry2
init|=
literal|"for $a in collection('"
operator|+
name|testColl
operator|+
literal|"')/test/fruit[@name = 'apple'] return $a"
decl_stmt|;
name|assertEquals
argument_list|(
name|doXQuery
argument_list|(
name|qry2
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|doXQuery
parameter_list|(
name|String
name|qry
parameter_list|)
throws|throws
name|RemoteException
block|{
name|QueryResponse
name|rsp
init|=
name|query
operator|.
name|xquery
argument_list|(
name|sessionId
argument_list|,
name|qry
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|noHits
init|=
name|rsp
operator|.
name|getHits
argument_list|()
decl_stmt|;
if|if
condition|(
name|noHits
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|rsps
init|=
name|query
operator|.
name|retrieve
argument_list|(
name|sessionId
argument_list|,
literal|1
argument_list|,
name|noHits
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|"none"
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
name|rsps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rsps
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|noHits
argument_list|,
name|rsps
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No hits"
argument_list|)
expr_stmt|;
block|}
return|return
name|noHits
return|;
block|}
specifier|private
name|int
name|doXQueryB
parameter_list|(
name|String
name|qry
parameter_list|)
throws|throws
name|RemoteException
block|{
name|QueryResponse
name|rsp
init|=
name|query
operator|.
name|xquery
argument_list|(
name|sessionId
argument_list|,
name|qry
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|noHits
init|=
name|rsp
operator|.
name|getHits
argument_list|()
decl_stmt|;
if|if
condition|(
name|noHits
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
index|[]
name|rsps
init|=
name|query
operator|.
name|retrieveData
argument_list|(
name|sessionId
argument_list|,
literal|1
argument_list|,
name|noHits
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|"none"
argument_list|)
operator|.
name|getElements
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
name|rsps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|String
argument_list|(
name|rsps
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|noHits
argument_list|,
name|rsps
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No hits"
argument_list|)
expr_stmt|;
block|}
return|return
name|noHits
return|;
block|}
specifier|private
name|int
name|doXQueryC
parameter_list|(
name|String
name|qry
parameter_list|)
throws|throws
name|RemoteException
block|{
name|QueryResponse
name|rsp
init|=
name|query
operator|.
name|xquery
argument_list|(
name|sessionId
argument_list|,
name|qry
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|noHits
init|=
name|rsp
operator|.
name|getHits
argument_list|()
decl_stmt|;
if|if
condition|(
name|noHits
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|rsps
init|=
name|query
operator|.
name|retrieveByDocument
argument_list|(
name|sessionId
argument_list|,
literal|1
argument_list|,
name|noHits
argument_list|,
name|testColl
operator|+
literal|"/docA"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|"none"
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
name|rsps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rsps
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|noHits
operator|=
name|rsps
operator|.
name|length
expr_stmt|;
comment|//			assertEquals(noHits,rsps.length);
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No hits"
argument_list|)
expr_stmt|;
block|}
return|return
name|noHits
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mn
operator|==
literal|null
condition|)
block|{
name|mn
operator|=
operator|new
name|Main
argument_list|(
literal|"jetty"
argument_list|)
expr_stmt|;
name|mn
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"jetty"
block|}
argument_list|)
expr_stmt|;
block|}
name|QueryService
name|service
init|=
operator|new
name|QueryServiceLocator
argument_list|()
decl_stmt|;
name|query
operator|=
name|service
operator|.
name|getQuery
argument_list|(
operator|new
name|URL
argument_list|(
name|query_url
argument_list|)
argument_list|)
expr_stmt|;
name|sessionId
operator|=
name|query
operator|.
name|connect
argument_list|(
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|AdminService
name|aservice
init|=
operator|new
name|AdminServiceLocator
argument_list|()
decl_stmt|;
name|admin
operator|=
name|aservice
operator|.
name|getAdmin
argument_list|(
operator|new
name|URL
argument_list|(
name|admin_url
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|query
operator|.
name|disconnect
argument_list|(
name|sessionId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|rex
parameter_list|)
block|{
name|rex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//mn.shutdown();
block|}
specifier|public
name|void
name|testRemoveThisEmptyTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|//        assertEquals(1,1);
block|}
block|}
end_class

end_unit

