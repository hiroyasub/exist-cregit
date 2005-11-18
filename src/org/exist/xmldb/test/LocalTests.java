begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
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
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_class
specifier|public
class|class
name|LocalTests
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
name|TestSuite
name|suite
init|=
operator|new
name|TestSuite
argument_list|(
literal|"Test suite for org.exist.xmldb.test"
argument_list|)
decl_stmt|;
comment|//$JUnit-BEGIN$
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|CreateCollectionsTest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|ResourceTest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|//		suite.addTest(new TestSuite(ResourceSetTest.class));
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|TestEXistXMLSerialize
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|CopyMoveTest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|ContentAsDOMTest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|//        suite.addTest(new TestSuite(MultiDBTest.class));
name|suite
operator|.
name|addTestSuite
argument_list|(
name|XmldbURITest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//$JUnit-END$
return|return
name|suite
return|;
block|}
block|}
end_class

end_unit

