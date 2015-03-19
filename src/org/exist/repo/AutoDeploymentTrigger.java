begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|repo
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
name|io
operator|.
name|FileFilter
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
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|StartupTrigger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|tui
operator|.
name|BatchUserInteraction
import|;
end_import

begin_comment
comment|/**  * Startup trigger for automatic deployment of application packages. Scans the "autodeploy" directory  * for .xar files. Installs any application which does not yet exist in the database.  */
end_comment

begin_class
specifier|public
class|class
name|AutoDeploymentTrigger
implements|implements
name|StartupTrigger
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|AutoDeploymentTrigger
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|AUTODEPLOY_DIRECTORY
init|=
literal|"autodeploy"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|AUTODEPLOY_PROPERTY
init|=
literal|"exist.autodeploy"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|params
parameter_list|)
block|{
comment|// do not process if the system property exist.autodeploy=off
specifier|final
name|String
name|property
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|AUTODEPLOY_PROPERTY
argument_list|,
literal|"on"
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"off"
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|File
name|homeDir
init|=
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
specifier|final
name|File
name|autodeployDir
init|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
name|AUTODEPLOY_DIRECTORY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|autodeployDir
operator|.
name|canRead
argument_list|()
operator|&&
name|autodeployDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|ExistRepository
name|repo
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getExpathRepo
argument_list|()
decl_stmt|;
specifier|final
name|UserInteractionStrategy
name|interact
init|=
operator|new
name|BatchUserInteraction
argument_list|()
decl_stmt|;
specifier|final
name|File
index|[]
name|xars
init|=
name|autodeployDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".xar"
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|xars
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|autodeployDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Scanning autodeploy directory. Found "
operator|+
name|xars
operator|.
name|length
operator|+
literal|" app packages."
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|xars
argument_list|,
operator|new
name|Comparator
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|File
name|o1
parameter_list|,
name|File
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Deployment
name|deployment
init|=
operator|new
name|Deployment
argument_list|(
name|broker
argument_list|)
decl_stmt|;
comment|// build a map with uri -> file so we can resolve dependencies
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|packages
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|File
name|xar
range|:
name|xars
control|)
block|{
try|try
block|{
specifier|final
name|String
name|name
init|=
name|deployment
operator|.
name|getNameFromDescriptor
argument_list|(
name|xar
argument_list|)
decl_stmt|;
name|packages
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|xar
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught exception while reading app package "
operator|+
name|xar
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PackageException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught exception while reading app package "
operator|+
name|xar
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|PackageLoader
name|loader
init|=
operator|new
name|PackageLoader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|File
name|load
parameter_list|(
name|String
name|name
parameter_list|,
name|PackageLoader
operator|.
name|Version
name|version
parameter_list|)
block|{
comment|// TODO: enforce version check
return|return
name|packages
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
specifier|final
name|File
name|xar
range|:
name|xars
control|)
block|{
try|try
block|{
name|deployment
operator|.
name|installAndDeploy
argument_list|(
name|xar
argument_list|,
name|loader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PackageException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception during deployment of app "
operator|+
name|xar
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|reportStatus
argument_list|(
literal|"An error occurred during app deployment: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception during deployment of app "
operator|+
name|xar
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|reportStatus
argument_list|(
literal|"An error occurred during app deployment: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

