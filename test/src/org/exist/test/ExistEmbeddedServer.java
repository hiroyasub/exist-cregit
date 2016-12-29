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
name|EXistException
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
name|ConfigurationHelper
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
name|DatabaseConfigurationException
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Exist embedded Server Rule for JUnit  */
end_comment

begin_class
specifier|public
class|class
name|ExistEmbeddedServer
extends|extends
name|ExternalResource
block|{
specifier|private
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|instanceName
decl_stmt|;
specifier|private
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|configFile
decl_stmt|;
specifier|private
specifier|final
name|Optional
argument_list|<
name|Properties
argument_list|>
name|configProperties
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|public
name|ExistEmbeddedServer
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExistEmbeddedServer
parameter_list|(
specifier|final
name|Properties
name|configProperties
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|configProperties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExistEmbeddedServer
parameter_list|(
specifier|final
name|String
name|instanceName
parameter_list|,
specifier|final
name|Path
name|configFile
parameter_list|)
block|{
name|this
argument_list|(
name|instanceName
argument_list|,
name|configFile
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExistEmbeddedServer
parameter_list|(
specifier|final
name|String
name|instanceName
parameter_list|,
specifier|final
name|Path
name|configFile
parameter_list|,
specifier|final
name|Properties
name|configProperties
parameter_list|)
block|{
name|this
operator|.
name|instanceName
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|instanceName
argument_list|)
expr_stmt|;
name|this
operator|.
name|configFile
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|configProperties
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|configProperties
argument_list|)
expr_stmt|;
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
name|startDb
argument_list|()
expr_stmt|;
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|startDb
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|EXistException
block|{
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|instanceName
operator|.
name|orElse
argument_list|(
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|home
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|Paths
operator|::
name|get
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|confFile
init|=
name|configFile
operator|.
name|orElseGet
argument_list|(
parameter_list|()
lambda|->
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"conf.xml"
argument_list|,
name|home
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|config
decl_stmt|;
if|if
condition|(
name|confFile
operator|.
name|isAbsolute
argument_list|()
operator|&&
name|Files
operator|.
name|exists
argument_list|(
name|confFile
argument_list|)
condition|)
block|{
comment|//TODO(AR) is this correct?
name|config
operator|=
operator|new
name|Configuration
argument_list|(
name|confFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|config
operator|=
operator|new
name|Configuration
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|confFile
argument_list|)
argument_list|,
name|home
argument_list|)
expr_stmt|;
block|}
comment|// override any specified config properties
if|if
condition|(
name|configProperties
operator|.
name|isPresent
argument_list|()
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|configProperty
range|:
name|configProperties
operator|.
name|get
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|config
operator|.
name|setProperty
argument_list|(
name|configProperty
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|configProperty
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|BrokerPool
operator|.
name|configure
argument_list|(
name|name
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ExistEmbeddedServer already running"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
specifier|public
name|void
name|restart
parameter_list|()
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
block|{
name|stopDb
argument_list|()
expr_stmt|;
name|startDb
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ExistEmbeddedServer already stopped"
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
name|stopDb
argument_list|()
expr_stmt|;
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|stopDb
parameter_list|()
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// clear instance variables
name|pool
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
literal|"ExistEmbeddedServer already stopped"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

