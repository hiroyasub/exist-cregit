begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Sep 25, 2004  *  * TODO To change the template for this generated file go to  * Window - Preferences - Java - Code Style - Code Templates  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|util
operator|.
name|FileUtils
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
name|xmldb
operator|.
name|concurrent
operator|.
name|action
operator|.
name|AttributeUpdateAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentAttrUpdateTest
extends|extends
name|ConcurrentTestBase
block|{
comment|//	private static final String QUERY =
comment|//		"//ELEMENT[@attribute-1]";
specifier|private
name|String
index|[]
name|wordList
decl_stmt|;
specifier|private
name|Path
name|tempFile
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|wordList
operator|=
name|DBUtils
operator|.
name|wordList
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|wordList
argument_list|)
expr_stmt|;
name|this
operator|.
name|tempFile
operator|=
name|DBUtils
operator|.
name|generateXMLFile
argument_list|(
literal|250
argument_list|,
literal|10
argument_list|,
name|wordList
argument_list|)
expr_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|getTestCollection
argument_list|()
argument_list|,
literal|"R1.xml"
argument_list|,
name|tempFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|tempFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTestCollectionName
parameter_list|()
block|{
return|return
literal|"C1"
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Runner
argument_list|>
name|getRunners
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Runner
argument_list|(
operator|new
name|AttributeUpdateAction
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/C1"
argument_list|,
literal|"R1.xml"
argument_list|,
name|wordList
argument_list|)
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
comment|//new Runner(new XQueryAction(getUri + "/C1", "R1.xml", QUERY), 100, 100, 30);
argument_list|)
return|;
block|}
block|}
end_class

end_unit

