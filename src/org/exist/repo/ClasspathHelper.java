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
name|org
operator|.
name|apache
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
name|start
operator|.
name|Classpath
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
name|EXistClassLoader
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
name|Package
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Helper class to construct classpath for expath modules containing  * jar files. Part of start.jar  */
end_comment

begin_class
specifier|public
class|class
name|ClasspathHelper
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ClasspathHelper
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|void
name|updateClasspath
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|ClassLoader
name|loader
init|=
name|pool
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|loader
operator|instanceof
name|EXistClassLoader
operator|)
condition|)
return|return;
name|Classpath
name|cp
init|=
operator|new
name|Classpath
argument_list|()
decl_stmt|;
name|scanPackages
argument_list|(
name|pool
argument_list|,
name|cp
argument_list|)
expr_stmt|;
operator|(
operator|(
name|EXistClassLoader
operator|)
name|loader
operator|)
operator|.
name|addURLs
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|updateClasspath
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Package
name|pkg
parameter_list|)
block|{
name|ClassLoader
name|loader
init|=
name|pool
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|loader
operator|instanceof
name|EXistClassLoader
operator|)
condition|)
return|return;
name|FileSystemStorage
operator|.
name|FileSystemResolver
name|resolver
init|=
operator|(
name|FileSystemStorage
operator|.
name|FileSystemResolver
operator|)
name|pkg
operator|.
name|getResolver
argument_list|()
decl_stmt|;
name|File
name|packageDir
init|=
name|resolver
operator|.
name|resolveResourceAsFile
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|Classpath
name|cp
init|=
operator|new
name|Classpath
argument_list|()
decl_stmt|;
try|try
block|{
name|scanPackageDir
argument_list|(
name|cp
argument_list|,
name|packageDir
argument_list|)
expr_stmt|;
operator|(
operator|(
name|EXistClassLoader
operator|)
name|loader
operator|)
operator|.
name|addURLs
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"An error occurred while updating classpath for package "
operator|+
name|pkg
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|scanPackages
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Classpath
name|classpath
parameter_list|)
block|{
try|try
block|{
name|ExistRepository
name|repo
init|=
name|pool
operator|.
name|getExpathRepo
argument_list|()
decl_stmt|;
for|for
control|(
name|Packages
name|pkgs
range|:
name|repo
operator|.
name|getParentRepo
argument_list|()
operator|.
name|listPackages
argument_list|()
control|)
block|{
name|Package
name|pkg
init|=
name|pkgs
operator|.
name|latest
argument_list|()
decl_stmt|;
try|try
block|{
name|FileSystemStorage
operator|.
name|FileSystemResolver
name|resolver
init|=
operator|(
name|FileSystemStorage
operator|.
name|FileSystemResolver
operator|)
name|pkg
operator|.
name|getResolver
argument_list|()
decl_stmt|;
name|File
name|packageDir
init|=
name|resolver
operator|.
name|resolveResourceAsFile
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|scanPackageDir
argument_list|(
name|classpath
argument_list|,
name|packageDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"An error occurred while updating classpath for package "
operator|+
name|pkg
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"An error occurred while updating classpath for packages"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|scanPackageDir
parameter_list|(
name|Classpath
name|classpath
parameter_list|,
name|File
name|module
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|exist
init|=
operator|new
name|File
argument_list|(
name|module
argument_list|,
literal|".exist"
argument_list|)
decl_stmt|;
if|if
condition|(
name|exist
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|exist
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The .exist config dir is not a dir: "
operator|+
name|exist
argument_list|)
throw|;
block|}
name|File
name|cp
init|=
operator|new
name|File
argument_list|(
name|exist
argument_list|,
literal|"classpath.txt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
operator|.
name|exists
argument_list|()
condition|)
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|classpath
operator|.
name|addComponent
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

