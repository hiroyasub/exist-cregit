begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|start
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
name|reflect
operator|.
name|Method
import|;
end_import

begin_comment
comment|/**  * An apache commons daemon class to start eXist.  * @author R. Alexander Milowski  */
end_comment

begin_class
specifier|public
class|class
name|ServiceDaemon
block|{
name|String
index|[]
name|args
decl_stmt|;
name|Main
name|existMain
decl_stmt|;
specifier|public
name|ServiceDaemon
parameter_list|()
block|{
block|}
specifier|protected
name|void
name|finalize
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ServiceDaemon: instance "
operator|+
name|this
operator|.
name|hashCode
argument_list|()
operator|+
literal|" garbage collected"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|String
index|[]
name|arguments
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ServiceDaemon: instance "
operator|+
name|this
operator|.
name|hashCode
argument_list|()
operator|+
literal|" init"
argument_list|)
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|arguments
expr_stmt|;
name|this
operator|.
name|existMain
operator|=
name|Main
operator|.
name|getMain
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ServiceDaemon: init done "
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|/* Dump a message */
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ServiceDaemon: starting"
argument_list|)
expr_stmt|;
name|existMain
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|/* Dump a message */
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ServiceDaemon: stopping"
argument_list|)
expr_stmt|;
try|try
block|{
name|File
name|homeDir
init|=
name|existMain
operator|.
name|detectHome
argument_list|()
decl_stmt|;
name|String
index|[]
name|noArgs
init|=
block|{}
decl_stmt|;
name|Classpath
name|classpath
init|=
name|existMain
operator|.
name|constructClasspath
argument_list|(
name|homeDir
argument_list|,
name|noArgs
argument_list|)
decl_stmt|;
name|ClassLoader
name|cl
init|=
name|classpath
operator|.
name|getClassLoader
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|cl
argument_list|)
expr_stmt|;
name|Class
name|brokerPoolClass
init|=
name|cl
operator|.
name|loadClass
argument_list|(
literal|"org.exist.storage.BrokerPool"
argument_list|)
decl_stmt|;
comment|// This only works in Java 1.5
comment|//Method stopAll = brokerPoolClass.getDeclaredMethod("stopAll",java.lang.Boolean.TYPE);
comment|//stopAll.invoke(null,Boolean.TRUE);
comment|// This is the ugly Java 1.4 version
name|Class
index|[]
name|paramTypes
init|=
operator|new
name|Class
index|[
literal|1
index|]
decl_stmt|;
name|paramTypes
index|[
literal|0
index|]
operator|=
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|TYPE
expr_stmt|;
name|Method
name|stopAll
init|=
name|brokerPoolClass
operator|.
name|getDeclaredMethod
argument_list|(
literal|"stopAll"
argument_list|,
name|paramTypes
argument_list|)
decl_stmt|;
name|Object
index|[]
name|arguments
init|=
operator|new
name|Object
index|[
literal|1
index|]
decl_stmt|;
name|arguments
index|[
literal|0
index|]
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
name|stopAll
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ServiceDaemon: stopped"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ServiceDaemon: instance "
operator|+
name|this
operator|.
name|hashCode
argument_list|()
operator|+
literal|" destroy"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

