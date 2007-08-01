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
name|File
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
name|storage
operator|.
name|BrokerPool
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|ConfigurationHelper
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//Logger
comment|/**      * Returns a file handle for eXist's home directory.      * Order of tests is designed with the idea, the more precise it is,      * the more the developper know what he is doing      *<ol>      *<li>Brokerpool      : if eXist was already configured.      *<li>exist.home      : if exists      *<li>user.home       : if exists, with a conf.xml file      *<li>user.dir        : if exists, with a conf.xml file      *<li>classpath entry : if exists, with a conf.xml file      *</ol>      *      * @return the file handle or<code>null</code>      */
specifier|public
specifier|static
name|File
name|getExistHome
parameter_list|()
block|{
name|File
name|existHome
init|=
literal|null
decl_stmt|;
comment|// If eXist was allready configured, then return
comment|// the existHome of this instance.
try|try
block|{
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
name|existHome
operator|=
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
expr_stmt|;
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
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Catch all potential problems
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not retieve instance of brokerpool: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|config
init|=
literal|"conf.xml"
decl_stmt|;
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
name|existHome
operator|=
operator|new
name|File
argument_list|(
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
argument_list|)
expr_stmt|;
if|if
condition|(
name|existHome
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got eXist home from system property 'exist.home': "
operator|+
name|existHome
argument_list|)
expr_stmt|;
return|return
name|existHome
return|;
block|}
block|}
comment|// try user.home
name|existHome
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|existHome
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|new
name|File
argument_list|(
name|existHome
argument_list|,
name|config
argument_list|)
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got eXist home from system property 'user.home': "
operator|+
name|existHome
argument_list|)
expr_stmt|;
return|return
name|existHome
return|;
block|}
comment|// try user.dir
name|existHome
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|existHome
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|new
name|File
argument_list|(
name|existHome
argument_list|,
name|config
argument_list|)
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got eXist home from system property 'user.dir': "
operator|+
name|existHome
argument_list|)
expr_stmt|;
return|return
name|existHome
return|;
block|}
comment|// try classpath
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
name|existHome
operator|=
operator|new
name|File
argument_list|(
name|configUrl
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got eXist home from classpath: "
operator|+
name|existHome
argument_list|)
expr_stmt|;
return|return
name|existHome
return|;
block|}
name|existHome
operator|=
literal|null
expr_stmt|;
return|return
name|existHome
return|;
block|}
comment|/**      * Returns a file handle for the given path, while<code>path</code> specifies      * the path to an eXist configuration file or directory.      *<br>      * Note that relative paths are being interpreted relative to<code>exist.home</code>      * or the current working directory, in case<code>exist.home</code> was not set.      *      * @param path the file path      * @return the file handle      */
specifier|public
specifier|static
name|File
name|lookup
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|lookup
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Returns a file handle for the given path, while<code>path</code> specifies      * the path to an eXist configuration file or directory.      *<br>      * If<code>parent</code> is null, then relative paths are being interpreted      * relative to<code>exist.home</code> or the current working directory, in      * case<code>exist.home</code> was not set.      *      * @param path path to the file or directory      * @param parent parent directory used to lookup<code>path</code>      * @return the file handle      */
specifier|public
specifier|static
name|File
name|lookup
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|parent
parameter_list|)
block|{
comment|// resolvePath is used for things like ~user/folder
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|decodeUserHome
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isAbsolute
argument_list|()
condition|)
return|return
name|f
return|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|File
name|home
init|=
name|getExistHome
argument_list|()
decl_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|home
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|=
name|home
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|File
argument_list|(
name|parent
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/**      * Resolves the given path by means of eventually replacing<tt>~</tt> with the users      * home directory, taken from the system property<code>user.home</code>.      *      * @param path the path to resolve      * @return the resolved path      */
specifier|public
specifier|static
name|String
name|decodeUserHome
parameter_list|(
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
name|path
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
operator|+
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
block|}
end_class

end_unit

