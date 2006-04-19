begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|test
package|;
end_package

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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|xquery
operator|.
name|CompiledXQuery
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
name|XQueryContext
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

begin_class
specifier|public
class|class
name|XQueryUpdateTest
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
name|TestRunner
operator|.
name|run
argument_list|(
name|XQueryUpdateTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|String
name|TEST_COLLECTION
init|=
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
decl_stmt|;
specifier|protected
specifier|static
name|String
name|TEST_XML
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<products/>"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|ITEMS_TO_APPEND
init|=
literal|70
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
comment|//	public void testAppend() {
comment|//        DBBroker broker = null;
comment|//        try {
comment|//        	System.out.println("testAppend() ...\n");
comment|//            broker = pool.get(SecurityManager.SYSTEM_USER);
comment|//
comment|//            XQuery xquery = broker.getXQueryService();
comment|//            for (int i = 0; i< ITEMS_TO_APPEND; i++) {
comment|//                String query =
comment|//                	"	update insert\n" +
comment|//                	"<product id='id{$i}'>\n" +
comment|//                	"<description>Description {$i}</description>\n" +
comment|//                	"<price>{$i + 1.0}</price>\n" +
comment|//                	"<stock>{$i * 10}</stock>\n" +
comment|//                	"</product>\n" +
comment|//                	"	into /products";
comment|//                XQueryContext context = xquery.newContext(AccessContext.TEST);
comment|//                context.declareVariable("i", new Integer(i));
comment|//                CompiledXQuery compiled = xquery.compile(context, query);
comment|//                xquery.execute(compiled, null);
comment|//
comment|//                Sequence seq = xquery.execute("/products", null, AccessContext.TEST);
comment|//                assertEquals(seq.getLength(), 1);
comment|//
comment|//                Serializer serializer = broker.getSerializer();
comment|//                System.out.println(serializer.serialize((NodeValue) seq.itemAt(0)));
comment|//            }
comment|//
comment|//            Sequence seq = xquery.execute("//product", null, AccessContext.TEST);
comment|//            assertEquals(ITEMS_TO_APPEND, seq.getLength());
comment|//
comment|//            seq = xquery.execute("//product[price> 0.0]", null, AccessContext.TEST);
comment|//            assertEquals(ITEMS_TO_APPEND, seq.getLength());
comment|//            System.out.println("testAppend: PASS");
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        } finally {
comment|//            pool.release(broker);
comment|//        }
comment|//	}
comment|//    public void testInsertBefore() {
comment|//        DBBroker broker = null;
comment|//        try {
comment|//            System.out.println("testInsertBefore() ...\n");
comment|//            broker = pool.get(SecurityManager.SYSTEM_USER);
comment|//
comment|//            String query =
comment|//                "   update insert\n" +
comment|//                "<product id='original'>\n" +
comment|//                "<description>Description</description>\n" +
comment|//                "<price>0</price>\n" +
comment|//                "<stock>10</stock>\n" +
comment|//                "</product>\n" +
comment|//                "   into /products";
comment|//
comment|//            XQuery xquery = broker.getXQueryService();
comment|//            xquery.execute(query, null, AccessContext.TEST);
comment|//
comment|//            query =
comment|//                "   declare variable $i external;\n" +
comment|//                "   update insert\n" +
comment|//                "<product id='id{$i}'>\n" +
comment|//                "<description>Description {$i}</description>\n" +
comment|//                "<price>{$i + 1.0}</price>\n" +
comment|//                "<stock>{$i * 10}</stock>\n" +
comment|//                "</product>\n" +
comment|//                "   preceding /products/product[1]";
comment|//            XQueryContext context = xquery.newContext(AccessContext.TEST);
comment|//            CompiledXQuery compiled = xquery.compile(context, query);
comment|//            for (int i = 0; i< ITEMS_TO_APPEND; i++) {
comment|//                context.declareVariable("i", new Integer(i));
comment|//                xquery.execute(compiled, null);
comment|//
comment|//                Sequence seq = xquery.execute("/products", null, AccessContext.TEST);
comment|//                assertEquals(seq.getLength(), 1);
comment|//
comment|//                Serializer serializer = broker.getSerializer();
comment|//                System.out.println(serializer.serialize((NodeValue) seq.itemAt(0)));
comment|//            }
comment|//
comment|//            Sequence seq = xquery.execute("//product", null, AccessContext.TEST);
comment|//            assertEquals(ITEMS_TO_APPEND + 1, seq.getLength());
comment|//
comment|//            seq = xquery.execute("//product[price> 0.0]", null, AccessContext.TEST);
comment|//            assertEquals(ITEMS_TO_APPEND, seq.getLength());
comment|//            System.out.println("testInsertBefore: PASS");
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        } finally {
comment|//            pool.release(broker);
comment|//        }
comment|//    }
specifier|public
name|void
name|testInsertAfter
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testInsertAfter() ...\n"
argument_list|)
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
name|String
name|query
init|=
literal|"   update insert\n"
operator|+
literal|"<product id='original'>\n"
operator|+
literal|"<description>Description</description>\n"
operator|+
literal|"<price>0</price>\n"
operator|+
literal|"<stock>10</stock>\n"
operator|+
literal|"</product>\n"
operator|+
literal|"   into /products"
decl_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|xquery
operator|.
name|execute
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"   declare variable $i external;\n"
operator|+
literal|"   update insert\n"
operator|+
literal|"<product id='id{$i}'>\n"
operator|+
literal|"<description>Description {$i}</description>\n"
operator|+
literal|"<price>{$i + 1.0}</price>\n"
operator|+
literal|"<stock>{$i * 10}</stock>\n"
operator|+
literal|"</product>\n"
operator|+
literal|"   following /products/product[1]"
expr_stmt|;
name|XQueryContext
name|context
init|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|query
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
name|ITEMS_TO_APPEND
condition|;
name|i
operator|++
control|)
block|{
name|context
operator|.
name|declareVariable
argument_list|(
literal|"i"
argument_list|,
operator|new
name|Integer
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"/products"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|seq
operator|.
name|getLength
argument_list|()
argument_list|,
literal|1
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
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//product"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ITEMS_TO_APPEND
operator|+
literal|1
argument_list|,
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//product[price> 0.0]"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ITEMS_TO_APPEND
argument_list|,
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testInsertAfter: PASS"
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
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|pool
operator|=
name|startDB
argument_list|()
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|TransactionManager
name|mgr
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
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
name|Txn
name|transaction
init|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
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
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
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
literal|"test.xml"
argument_list|,
name|TEST_XML
argument_list|)
decl_stmt|;
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
name|TEST_XML
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|mgr
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
literal|"Transaction commited ..."
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
try|try
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
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
block|}
end_class

end_unit

