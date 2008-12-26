begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

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
name|lang
operator|.
name|annotation
operator|.
name|*
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

begin_comment
comment|/**  * A superclass for database unit tests.  It takes care of starting up and clearing the database in  * its<code>setUp</code> method, and supports mocking with jMock.  By default, the database  * will be configured from the file "conf.xml" in the current directory, but you can annotate your  * test class with {@link DatabaseTestCase.ConfigFile} to specify a different one.  *   * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
annotation|@
name|DatabaseTestCase
operator|.
name|ConfigFile
argument_list|(
literal|"conf.xml"
argument_list|)
specifier|public
specifier|abstract
class|class
name|DatabaseTestCase
block|{
comment|/** 	 * An annotation that specifies the path of the config file to use when setting up the database 	 * for a test. 	 *  	 * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a> 	 */
annotation|@
name|Inherited
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|TYPE
argument_list|)
specifier|public
annotation_defn|@interface
name|ConfigFile
block|{
name|String
name|value
parameter_list|()
function_decl|;
block|}
specifier|protected
specifier|static
name|Database
name|db
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|startupDatabase
parameter_list|()
throws|throws
name|Exception
block|{
name|ConfigFile
name|configFileAnnotation
init|=
name|getClass
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|ConfigFile
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|configFileAnnotation
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"Missing ConfigFile annotation on DatabaseTestCase subclass"
argument_list|)
throw|;
name|File
name|configFile
init|=
operator|new
name|File
argument_list|(
name|configFileAnnotation
operator|.
name|value
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Database
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|Database
operator|.
name|startup
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
name|db
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|db
operator|==
literal|null
condition|)
name|db
operator|=
operator|new
name|Database
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|wipeDatabase
argument_list|()
expr_stmt|;
name|Database
operator|.
name|configureRootCollection
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
comment|// config file gets erased by wipeDatabase()
block|}
annotation|@
name|After
specifier|public
name|void
name|shutdownDatabase
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|Database
operator|.
name|isStarted
argument_list|()
condition|)
block|{
comment|// TODO: a bug in eXist's removeCollection(root) means that we need to shut down immediately
comment|// after wiping every time, otherwise the database gets corrupted.  When this is fixed, this
comment|// method can become a static @AfterClass method for increased performance.
name|wipeDatabase
argument_list|()
expr_stmt|;
name|Database
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|db
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|wipeDatabase
parameter_list|()
throws|throws
name|Exception
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Transaction
name|tx
init|=
name|Database
operator|.
name|requireTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|db
operator|.
name|acquireBroker
argument_list|()
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|)
decl_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|tx
operator|.
name|tx
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|tx
operator|.
name|abortIfIncomplete
argument_list|()
expr_stmt|;
name|db
operator|.
name|releaseBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

