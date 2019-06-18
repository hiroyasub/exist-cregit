begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/****************************************************************************/
end_comment

begin_comment
comment|/*  File:       ExistRepository.java                                        */
end_comment

begin_comment
comment|/*  Author:     F. Georges - H2O Consulting                                 */
end_comment

begin_comment
comment|/*  Date:       2010-09-22                                                  */
end_comment

begin_comment
comment|/*  Tags:                                                                   */
end_comment

begin_comment
comment|/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
end_comment

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

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
name|IOException
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
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|nio
operator|.
name|file
operator|.
name|StandardCopyOption
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
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
name|BrokerPoolService
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
name|BrokerPoolServiceException
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
name|NativeBroker
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
name|FileUtils
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
name|Module
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
name|XPathException
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
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|FileSystemStorage
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
name|FileSystemStorage
operator|.
name|FileSystemResolver
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
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Packages
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
name|PackageException
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
name|Repository
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
name|URISpace
import|;
end_import

begin_comment
comment|/**  * A repository as viewed by eXist.  *  * @author Florent Georges  * @author Wolfgang Meier  * @author Adam Retter  * @since  2010-09-22  */
end_comment

begin_class
specifier|public
class|class
name|ExistRepository
extends|extends
name|Observable
implements|implements
name|BrokerPoolService
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
name|ExistRepository
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXPATH_REPO_DIR
init|=
literal|"expathrepo"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXPATH_REPO_DEFAULT
init|=
literal|"webapp/WEB-INF/"
operator|+
name|EXPATH_REPO_DIR
decl_stmt|;
comment|/** The wrapped EXPath repository. */
specifier|private
name|Path
name|expathDir
decl_stmt|;
specifier|private
name|Repository
name|myParent
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
specifier|final
name|Path
name|dataDir
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
operator|(
name|Path
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|NativeBroker
operator|.
name|DEFAULT_DATA_DIR
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|expathDir
operator|=
name|dataDir
operator|.
name|resolve
argument_list|(
name|EXPATH_REPO_DIR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|expathDir
argument_list|)
condition|)
block|{
name|moveOldRepo
argument_list|(
name|brokerPool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
argument_list|,
name|expathDir
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|expathDir
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
throw|throw
operator|new
name|BrokerPoolServiceException
argument_list|(
literal|"Unable to access EXPath repository"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Using directory "
operator|+
name|expathDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" for expath package repository"
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|FileSystemStorage
name|storage
init|=
operator|new
name|FileSystemStorage
argument_list|(
name|expathDir
argument_list|)
decl_stmt|;
name|storage
operator|.
name|setErrorIfNoContentDir
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|myParent
operator|=
operator|new
name|Repository
argument_list|(
name|storage
argument_list|)
expr_stmt|;
name|myParent
operator|.
name|registerExtension
argument_list|(
operator|new
name|ExistPkgExtension
argument_list|()
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
throw|throw
operator|new
name|BrokerPoolServiceException
argument_list|(
literal|"Unable to prepare EXPath Package Repository: "
operator|+
name|expathDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Repository
name|getParentRepo
parameter_list|()
block|{
return|return
name|myParent
return|;
block|}
specifier|public
name|Module
name|resolveJavaModule
parameter_list|(
specifier|final
name|String
name|namespace
parameter_list|,
specifier|final
name|XQueryContext
name|ctxt
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid URI: "
operator|+
name|namespace
argument_list|,
name|ex
argument_list|)
throw|;
block|}
for|for
control|(
specifier|final
name|Packages
name|pp
range|:
name|myParent
operator|.
name|listPackages
argument_list|()
control|)
block|{
specifier|final
name|Package
name|pkg
init|=
name|pp
operator|.
name|latest
argument_list|()
decl_stmt|;
specifier|final
name|ExistPkgInfo
name|info
init|=
operator|(
name|ExistPkgInfo
operator|)
name|pkg
operator|.
name|getInfo
argument_list|(
literal|"exist"
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|clazz
init|=
name|info
operator|.
name|getJava
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
return|return
name|getModule
argument_list|(
name|clazz
argument_list|,
name|namespace
argument_list|,
name|ctxt
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Load a module instance from its class name.  Check the namespace is consistent.      */
specifier|private
name|Module
name|getModule
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|namespace
parameter_list|,
specifier|final
name|XQueryContext
name|ctxt
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|ClassLoader
name|existClassLoader
init|=
name|ctxt
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|Module
argument_list|>
name|clazz
init|=
operator|(
name|Class
argument_list|<
name|Module
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|existClassLoader
argument_list|)
decl_stmt|;
specifier|final
name|Module
name|module
init|=
name|instantiateModule
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ns
init|=
name|module
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ns
operator|.
name|equals
argument_list|(
name|namespace
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The namespace in the Java module "
operator|+
literal|"does not match the namespace in the package descriptor: "
operator|+
name|namespace
operator|+
literal|" - "
operator|+
name|ns
argument_list|)
throw|;
block|}
return|return
name|ctxt
operator|.
name|loadBuiltInModule
argument_list|(
name|namespace
argument_list|,
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot find module class from EXPath repository: "
operator|+
name|name
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassCastException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The class configured in EXPath repository is not a Module: "
operator|+
name|name
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Illegal argument passed to the module ctor"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * Try to instantiate the class using the constructor with a Map parameter,       * or the default constructor.      */
specifier|private
name|Module
name|instantiateModule
parameter_list|(
specifier|final
name|Class
argument_list|<
name|Module
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
try|try
block|{
comment|// attempt for a constructor that takes 1 argument
specifier|final
name|Constructor
argument_list|<
name|Module
argument_list|>
name|cstr1
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|cstr1
operator|.
name|newInstance
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
comment|// attempt for a constructor that takes 0 arguments
return|return
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
comment|// NOTE: must set interrupted flag
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unable to instantiate module from EXPath"
operator|+
literal|"repository: "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Path
name|resolveXQueryModule
parameter_list|(
specifier|final
name|String
name|namespace
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid URI: "
operator|+
name|namespace
argument_list|,
name|ex
argument_list|)
throw|;
block|}
for|for
control|(
specifier|final
name|Packages
name|pp
range|:
name|myParent
operator|.
name|listPackages
argument_list|()
control|)
block|{
specifier|final
name|Package
name|pkg
init|=
name|pp
operator|.
name|latest
argument_list|()
decl_stmt|;
comment|// FIXME: Rely on having a file system storage, that's probably a bad design!
specifier|final
name|FileSystemResolver
name|resolver
init|=
operator|(
name|FileSystemResolver
operator|)
name|pkg
operator|.
name|getResolver
argument_list|()
decl_stmt|;
specifier|final
name|ExistPkgInfo
name|info
init|=
operator|(
name|ExistPkgInfo
operator|)
name|pkg
operator|.
name|getInfo
argument_list|(
literal|"exist"
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|f
init|=
name|info
operator|.
name|getXQuery
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
return|return
name|resolver
operator|.
name|resolveComponentAsFile
argument_list|(
name|f
argument_list|)
return|;
block|}
block|}
name|String
name|sysid
init|=
literal|null
decl_stmt|;
comment|// declared here to be used in catch
name|Source
name|src
init|=
literal|null
decl_stmt|;
try|try
block|{
name|src
operator|=
name|pkg
operator|.
name|resolve
argument_list|(
name|namespace
argument_list|,
name|URISpace
operator|.
name|XQUERY
argument_list|)
expr_stmt|;
if|if
condition|(
name|src
operator|!=
literal|null
condition|)
block|{
name|sysid
operator|=
name|src
operator|.
name|getSystemId
argument_list|()
expr_stmt|;
return|return
name|Paths
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|sysid
argument_list|)
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Error parsing the URI of the query library: "
operator|+
name|sysid
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PackageException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Error resolving the query library: "
operator|+
name|namespace
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|src
operator|!=
literal|null
operator|&&
name|src
operator|instanceof
name|StreamSource
condition|)
block|{
specifier|final
name|StreamSource
name|streamSource
init|=
operator|(
operator|(
name|StreamSource
operator|)
name|src
operator|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|streamSource
operator|.
name|getInputStream
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|streamSource
operator|.
name|getInputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|streamSource
operator|.
name|getReader
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|streamSource
operator|.
name|getReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
literal|"Unable to close pkg source: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|URI
argument_list|>
name|getJavaModules
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|URI
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Packages
name|pp
range|:
name|myParent
operator|.
name|listPackages
argument_list|()
control|)
block|{
specifier|final
name|Package
name|pkg
init|=
name|pp
operator|.
name|latest
argument_list|()
decl_stmt|;
specifier|final
name|ExistPkgInfo
name|info
init|=
operator|(
name|ExistPkgInfo
operator|)
name|pkg
operator|.
name|getInfo
argument_list|(
literal|"exist"
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|modules
operator|.
name|addAll
argument_list|(
name|info
operator|.
name|getJavaModules
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|modules
return|;
block|}
specifier|public
specifier|static
name|Path
name|getRepositoryDir
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|dataDir
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
operator|(
name|Path
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|NativeBroker
operator|.
name|DEFAULT_DATA_DIR
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|expathDir
init|=
name|dataDir
operator|.
name|resolve
argument_list|(
name|EXPATH_REPO_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|expathDir
argument_list|)
condition|)
block|{
name|moveOldRepo
argument_list|(
name|config
operator|.
name|getExistHome
argument_list|()
argument_list|,
name|expathDir
argument_list|)
expr_stmt|;
block|}
name|Files
operator|.
name|createDirectories
argument_list|(
name|expathDir
argument_list|)
expr_stmt|;
return|return
name|expathDir
return|;
block|}
specifier|private
specifier|static
name|void
name|moveOldRepo
parameter_list|(
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|home
parameter_list|,
specifier|final
name|Path
name|newRepo
parameter_list|)
block|{
specifier|final
name|Path
name|repo_dir
init|=
name|home
operator|.
name|map
argument_list|(
name|h
lambda|->
block|{
if|if
condition|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|h
argument_list|)
operator|.
name|equals
argument_list|(
literal|"WEB-INF"
argument_list|)
condition|)
block|{
return|return
name|h
operator|.
name|resolve
argument_list|(
name|EXPATH_REPO_DIR
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|h
operator|.
name|resolve
argument_list|(
name|EXPATH_REPO_DEFAULT
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
operator|.
name|resolve
argument_list|(
name|EXPATH_REPO_DIR
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|repo_dir
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Found old expathrepo directory. Moving to new default location: "
operator|+
name|newRepo
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|repo_dir
argument_list|,
name|newRepo
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
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
name|error
argument_list|(
literal|"Failed to move old expathrepo directory to new default location. Keeping it."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|reportAction
parameter_list|(
specifier|final
name|Action
name|action
parameter_list|,
specifier|final
name|String
name|packageURI
parameter_list|)
block|{
name|notifyObservers
argument_list|(
operator|new
name|Notification
argument_list|(
name|action
argument_list|,
name|packageURI
argument_list|)
argument_list|)
expr_stmt|;
name|setChanged
argument_list|()
expr_stmt|;
block|}
specifier|public
enum|enum
name|Action
block|{
name|INSTALL
block|,
name|UNINSTALL
block|}
specifier|public
specifier|final
specifier|static
class|class
name|Notification
block|{
specifier|private
specifier|final
name|Action
name|action
decl_stmt|;
specifier|private
specifier|final
name|String
name|packageURI
decl_stmt|;
specifier|public
name|Notification
parameter_list|(
specifier|final
name|Action
name|action
parameter_list|,
specifier|final
name|String
name|packageURI
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|packageURI
operator|=
name|packageURI
expr_stmt|;
block|}
specifier|public
name|Action
name|getAction
parameter_list|()
block|{
return|return
name|action
return|;
block|}
specifier|public
name|String
name|getPackageURI
parameter_list|()
block|{
return|return
name|packageURI
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

begin_comment
comment|/*  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.               */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The contents of this file are subject to the Mozilla Public License     */
end_comment

begin_comment
comment|/*  Version 1.0 (the "License"); you may not use this file except in        */
end_comment

begin_comment
comment|/*  compliance with the License. You may obtain a copy of the License at    */
end_comment

begin_comment
comment|/*  http://www.mozilla.org/MPL/.                                            */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  Software distributed under the License is distributed on an "AS IS"     */
end_comment

begin_comment
comment|/*  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See    */
end_comment

begin_comment
comment|/*  the License for the specific language governing rights and limitations  */
end_comment

begin_comment
comment|/*  under the License.                                                      */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The Original Code is: all this file.                                    */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The Initial Developer of the Original Code is Florent Georges.          */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  Contributor(s): Wolfgang Meier, Adam Retter                             */
end_comment

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

end_unit

