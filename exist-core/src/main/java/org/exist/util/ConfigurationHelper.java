begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
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
name|io
operator|.
name|InputStream
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
name|net
operator|.
name|URL
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
name|xmldb
operator|.
name|DatabaseImpl
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_class
specifier|public
class|class
name|ConfigurationHelper
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
name|ConfigurationHelper
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//Logger
specifier|public
specifier|static
specifier|final
name|String
name|PROP_EXIST_CONFIGURATION_FILE
init|=
literal|"exist.configurationFile"
decl_stmt|;
comment|/**      * Returns a file handle for eXist's home directory.      * Order of tests is designed with the idea, the more precise it is,      * the more the developer know what he is doing      *<ol>      *<li>Brokerpool      : if eXist was already configured.      *<li>exist.home      : if exists      *<li>user.home       : if exists, with a conf.xml file      *<li>user.dir        : if exists, with a conf.xml file      *<li>classpath entry : if exists, with a conf.xml file      *</ol>      *      * @return the path to exist home if known      */
specifier|public
specifier|static
name|Optional
argument_list|<
name|Path
argument_list|>
name|getExistHome
parameter_list|()
block|{
return|return
name|getExistHome
argument_list|(
name|DatabaseImpl
operator|.
name|CONF_XML
argument_list|)
return|;
block|}
comment|/**      * Returns a file handle for eXist's home directory.      * Order of tests is designed with the idea, the more precise it is,      * the more the developper know what he is doing      *<ol>      *<li>Brokerpool      : if eXist was already configured.      *<li>exist.home      : if exists      *<li>user.home       : if exists, with a conf.xml file      *<li>user.dir        : if exists, with a conf.xml file      *<li>classpath entry : if exists, with a conf.xml file      *</ol>      *      * @param config the path to the config file.      *      * @return the path to exist home if known      */
specifier|public
specifier|static
name|Optional
argument_list|<
name|Path
argument_list|>
name|getExistHome
parameter_list|(
specifier|final
name|String
name|config
parameter_list|)
block|{
comment|// If eXist was already configured, then return
comment|// the existHome of this instance.
try|try
block|{
specifier|final
name|BrokerPool
name|broker
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|existHome
init|=
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
operator|.
name|map
argument_list|(
name|Path
operator|::
name|normalize
argument_list|)
decl_stmt|;
if|if
condition|(
name|existHome
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got eXist home from broker: "
operator|+
name|existHome
argument_list|)
expr_stmt|;
return|return
name|existHome
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
comment|// Catch all potential problems
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not retrieve instance of BrokerPool: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// try exist.home
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Path
name|existHome
init|=
name|ConfigurationHelper
operator|.
name|decodeUserHome
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
argument_list|)
operator|.
name|normalize
argument_list|()
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|existHome
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got eXist home from system property 'exist.home': {}"
argument_list|,
name|existHome
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|existHome
argument_list|)
return|;
block|}
block|}
comment|// try user.home
specifier|final
name|Path
name|userHome
init|=
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|userHomeRelativeConfig
init|=
name|userHome
operator|.
name|resolve
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|userHome
argument_list|)
operator|&&
name|Files
operator|.
name|isRegularFile
argument_list|(
name|userHomeRelativeConfig
argument_list|)
condition|)
block|{
specifier|final
name|Path
name|existHome
init|=
name|userHomeRelativeConfig
operator|.
name|getParent
argument_list|()
operator|.
name|normalize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got eXist home: {} from system property 'user.home': {}"
argument_list|,
name|existHome
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|userHome
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|existHome
argument_list|)
return|;
block|}
comment|// try user.dir
specifier|final
name|Path
name|userDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|userDirRelativeConfig
init|=
name|userDir
operator|.
name|resolve
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|userDir
argument_list|)
operator|&&
name|Files
operator|.
name|isRegularFile
argument_list|(
name|userDirRelativeConfig
argument_list|)
condition|)
block|{
specifier|final
name|Path
name|existHome
init|=
name|userDirRelativeConfig
operator|.
name|getParent
argument_list|()
operator|.
name|normalize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got eXist home: {} from system property 'user.dir': {}"
argument_list|,
name|existHome
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|userDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|existHome
argument_list|)
return|;
block|}
comment|// try classpath
specifier|final
name|URL
name|configUrl
init|=
name|ConfigurationHelper
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|configUrl
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Path
name|existHome
decl_stmt|;
if|if
condition|(
literal|"jar"
operator|.
name|equals
argument_list|(
name|configUrl
operator|.
name|getProtocol
argument_list|()
argument_list|)
condition|)
block|{
name|existHome
operator|=
name|Paths
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|configUrl
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|normalize
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|config
operator|+
literal|" file was found on the classpath, but inside a Jar file! Derived EXIST_HOME from Jar's parent folder: {}"
argument_list|,
name|existHome
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|existHome
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|configUrl
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
expr_stmt|;
if|if
condition|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|existHome
argument_list|)
operator|.
name|equals
argument_list|(
literal|"etc"
argument_list|)
condition|)
block|{
name|existHome
operator|=
name|existHome
operator|.
name|getParent
argument_list|()
operator|.
name|normalize
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got EXIST_HOME from classpath: {}"
argument_list|,
name|existHome
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Optional
operator|.
name|of
argument_list|(
name|existHome
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|// Catch all potential problems
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not derive EXIST_HOME from classpath: {}"
argument_list|,
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
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|Optional
argument_list|<
name|Path
argument_list|>
name|getFromSystemProperty
parameter_list|()
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_EXIST_CONFIGURATION_FILE
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|Paths
operator|::
name|get
argument_list|)
return|;
block|}
comment|/**      * Returns a file handle for the given path, while<code>path</code> specifies      * the path to an eXist configuration file or directory.      *<br>      * Note that relative paths are being interpreted relative to<code>exist.home</code>      * or the current working directory, in case<code>exist.home</code> was not set.      *      * @param path the file path      * @return the file handle      */
specifier|public
specifier|static
name|Path
name|lookup
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
return|return
name|lookup
argument_list|(
name|path
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns a file handle for the given path, while<code>path</code> specifies      * the path to an eXist configuration file or directory.      *<br>      * If<code>parent</code> is null, then relative paths are being interpreted      * relative to<code>exist.home</code> or the current working directory, in      * case<code>exist.home</code> was not set.      *      * @param path path to the file or directory      * @param parent parent directory used to lookup<code>path</code>      * @return the file handle      */
specifier|public
specifier|static
name|Path
name|lookup
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|parent
parameter_list|)
block|{
comment|// resolvePath is used for things like ~user/folder
specifier|final
name|Path
name|p
init|=
name|decodeUserHome
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
return|return
name|p
return|;
block|}
return|return
name|parent
operator|.
name|orElse
argument_list|(
name|getExistHome
argument_list|()
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
literal|"user.dir"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|resolve
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**      * Resolves the given path by means of eventually replacing<code>~</code> with the users      * home directory, taken from the system property<code>user.home</code>.      *      * @param path the path to resolve      * @return the resolved path      */
specifier|public
specifier|static
name|Path
name|decodeUserHome
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
name|path
operator|.
name|startsWith
argument_list|(
literal|"~"
argument_list|)
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
argument_list|)
operator|.
name|resolve
argument_list|(
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
annotation|@
name|Nullable
name|Properties
name|loadProperties
parameter_list|(
specifier|final
name|String
name|propertiesFileName
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|classPathRef
parameter_list|)
throws|throws
name|IOException
block|{
comment|// 1) try and load from config path
name|Path
name|propFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
name|propertiesFileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|propFile
argument_list|)
condition|)
block|{
try|try
init|(
specifier|final
name|InputStream
name|pin
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|propFile
argument_list|)
init|)
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|pin
argument_list|)
expr_stmt|;
return|return
name|properties
return|;
block|}
block|}
comment|// 2) try and load from config path set by system property
name|propFile
operator|=
name|ConfigurationHelper
operator|.
name|getFromSystemProperty
argument_list|()
operator|.
name|map
argument_list|(
name|p
lambda|->
name|p
operator|.
name|resolveSibling
argument_list|(
name|propertiesFileName
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|propFile
operator|!=
literal|null
operator|&&
name|Files
operator|.
name|isReadable
argument_list|(
name|propFile
argument_list|)
condition|)
block|{
try|try
init|(
specifier|final
name|InputStream
name|pin
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|propFile
argument_list|)
init|)
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|pin
argument_list|)
expr_stmt|;
return|return
name|properties
return|;
block|}
block|}
if|if
condition|(
name|classPathRef
operator|!=
literal|null
condition|)
block|{
comment|// 3) try and load from classpath classpathRef.getClassName()/client.properties
try|try
init|(
specifier|final
name|InputStream
name|pin
init|=
name|classPathRef
operator|.
name|getResourceAsStream
argument_list|(
name|propertiesFileName
argument_list|)
init|)
block|{
if|if
condition|(
name|pin
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|pin
argument_list|)
expr_stmt|;
return|return
name|properties
return|;
block|}
block|}
comment|// 4) try and load from classpath client.properties
try|try
init|(
specifier|final
name|InputStream
name|pin
init|=
name|classPathRef
operator|.
name|getResourceAsStream
argument_list|(
literal|"/"
operator|+
name|propertiesFileName
argument_list|)
init|)
block|{
if|if
condition|(
name|pin
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|pin
argument_list|)
expr_stmt|;
return|return
name|properties
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

