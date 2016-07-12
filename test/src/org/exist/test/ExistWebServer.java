begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|test
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
name|jetty
operator|.
name|JettyStart
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExternalResource
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Exist Jetty Web Server Rule to JUnit  */
end_comment

begin_class
specifier|public
class|class
name|ExistWebServer
extends|extends
name|ExternalResource
block|{
specifier|private
specifier|final
specifier|static
name|int
name|MIN_RANDOM_PORT
init|=
literal|49152
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MAX_RANDOM_PORT
init|=
literal|65535
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MAX_RANDOM_PORT_ATTEMPTS
init|=
literal|10
decl_stmt|;
specifier|private
name|JettyStart
name|server
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|useRandomPort
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|cleanupDbOnShutdown
decl_stmt|;
specifier|public
name|ExistWebServer
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExistWebServer
parameter_list|(
specifier|final
name|boolean
name|useRandomPort
parameter_list|)
block|{
name|this
argument_list|(
name|useRandomPort
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExistWebServer
parameter_list|(
specifier|final
name|boolean
name|useRandomPort
parameter_list|,
specifier|final
name|boolean
name|cleanupDbOnShutdown
parameter_list|)
block|{
name|this
operator|.
name|useRandomPort
operator|=
name|useRandomPort
expr_stmt|;
name|this
operator|.
name|cleanupDbOnShutdown
operator|=
name|cleanupDbOnShutdown
expr_stmt|;
block|}
specifier|public
specifier|final
name|int
name|getPort
parameter_list|()
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
return|return
name|server
operator|.
name|getPrimaryPort
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ExistWebServer is not running"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|useRandomPort
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.port"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nextFreePort
argument_list|(
name|MIN_RANDOM_PORT
argument_list|,
name|MAX_RANDOM_PORT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.secure.port"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nextFreePort
argument_list|(
name|MIN_RANDOM_PORT
argument_list|,
name|MAX_RANDOM_PORT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.ssl.port"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nextFreePort
argument_list|(
name|MIN_RANDOM_PORT
argument_list|,
name|MAX_RANDOM_PORT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|server
operator|=
operator|new
name|JettyStart
argument_list|()
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ExistWebServer already running"
argument_list|)
throw|;
block|}
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|restart
parameter_list|()
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ExistWebServer already stopped"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|after
parameter_list|()
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|cleanupDbOnShutdown
condition|)
block|{
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ExistWebServer already stopped"
argument_list|)
throw|;
block|}
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|nextFreePort
parameter_list|(
specifier|final
name|int
name|from
parameter_list|,
specifier|final
name|int
name|to
parameter_list|)
block|{
for|for
control|(
name|int
name|attempts
init|=
literal|0
init|;
name|attempts
operator|<
name|MAX_RANDOM_PORT_ATTEMPTS
condition|;
name|attempts
operator|++
control|)
block|{
specifier|final
name|int
name|port
init|=
name|random
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
if|if
condition|(
name|isLocalPortFree
argument_list|(
name|port
argument_list|)
condition|)
block|{
return|return
name|port
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Exceeded MAX_RANDOM_PORT_ATTEMPTS"
argument_list|)
throw|;
block|}
specifier|private
name|int
name|random
parameter_list|(
specifier|final
name|int
name|min
parameter_list|,
specifier|final
name|int
name|max
parameter_list|)
block|{
return|return
name|random
operator|.
name|nextInt
argument_list|(
operator|(
name|max
operator|-
name|min
operator|)
operator|+
literal|1
argument_list|)
operator|+
name|min
return|;
block|}
specifier|private
name|boolean
name|isLocalPortFree
parameter_list|(
specifier|final
name|int
name|port
parameter_list|)
block|{
try|try
block|{
operator|new
name|ServerSocket
argument_list|(
name|port
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

