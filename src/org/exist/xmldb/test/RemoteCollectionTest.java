begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 20 juil. 2004 $Id$  */
end_comment

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
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Server
import|;
end_import

begin_comment
comment|/** WORK IN PROGRESS !!!  * @author jmv  */
end_comment

begin_class
specifier|public
class|class
name|RemoteCollectionTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"http://localhost:8080/exist/xmlrpc"
decl_stmt|;
comment|/** ? @see junit.framework.TestCase#setUp() 	 */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"standalone"
block|}
decl_stmt|;
name|Server
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// Thread ??
block|}
comment|/** ? @see junit.framework.TestCase#tearDown() 	 */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO
block|}
specifier|public
name|void
name|testIndexQueryService
parameter_list|()
block|{
comment|// TODO .............
block|}
comment|/** 	 * @param name 	 */
specifier|public
name|RemoteCollectionTest
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
block|}
end_class

end_unit

