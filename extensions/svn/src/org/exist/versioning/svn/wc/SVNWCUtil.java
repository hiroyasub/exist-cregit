begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ====================================================================  * Copyright (c) 2004-2010 TMate Software Ltd.  All rights reserved.  *  * This software is licensed as described in the file COPYING, which  * you should have received as part of this distribution.  The terms  * are also available at http://svnkit.com/license.html  * If newer versions of this license are posted there, you may use a  * newer version instead, at your option.  * ====================================================================  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|DefaultSVNAuthenticationManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|DefaultSVNOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|SVNExternal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|admin
operator|.
name|SVNAdminArea
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|admin
operator|.
name|SVNVersionedProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|admin
operator|.
name|SVNWCAccess
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNCancelException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|auth
operator|.
name|ISVNAuthenticationManager
import|;
end_import

begin_comment
comment|/**  * The<b>SVNWCUtil</b> is a utility class providing some common methods used  * by Working Copy API classes for such purposes as creating default run-time  * configuration and authentication drivers and some others.  *   *   * @version 1.3  * @author TMate Software Ltd., Peter Skoog  * @since  1.2  * @see ISVNOptions  * @see<a target="_top" href="http://svnkit.com/kb/examples/">Examples</a>  */
end_comment

begin_class
specifier|public
class|class
name|SVNWCUtil
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ECLIPSE_AUTH_MANAGER_CLASSNAME
init|=
literal|"org.tmatesoft.svn.core.internal.wc.EclipseSVNAuthenticationManager"
decl_stmt|;
specifier|private
specifier|static
name|Boolean
name|ourIsEclipse
decl_stmt|;
comment|/**      * Gets the location of the default SVN's run-time configuration area on the      * current machine. The result path depends on the platform on which SVNKit      * is running:      *<ul>      *<li>on<i>Windows</i> this path usually looks like<i>'Documents and      * Settings\UserName\Subversion'</i> or simply<i>'%APPDATA%\Subversion'</i>.      *<li>on a<i>Unix</i>-like platform -<i>'~/.subversion'</i>.      *</ul>      *       * @return a {@link java.io.File} representation of the default SVN's      *         run-time configuration area location      */
specifier|public
specifier|static
name|File
name|getDefaultConfigurationDirectory
parameter_list|()
block|{
return|return
operator|new
name|Resource
argument_list|(
literal|"/config/subversion"
argument_list|)
return|;
block|}
comment|/**      * Creates a default authentication manager that uses the default SVN's      *<i>servers</i> configuration and authentication storage. Whether the      * default auth storage is used or not depends on the 'store-auth-creds'</i>      * option that can be found in the SVN's<i>config</i> file under the      *<i>[auth]</i> section.      *       * @return a default implementation of the credentials and servers      *         configuration driver interface      * @see #getDefaultConfigurationDirectory()      */
specifier|public
specifier|static
name|ISVNAuthenticationManager
name|createDefaultAuthenticationManager
parameter_list|()
block|{
return|return
name|createDefaultAuthenticationManager
argument_list|(
name|getDefaultConfigurationDirectory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Creates a default authentication manager that uses the<i>servers</i>      * configuration and authentication storage located in the provided      * directory. The authentication storage is enabled.      *       * @param configDir      *            a new location of the run-time configuration area      * @return a default implementation of the credentials and servers      *         configuration driver interface      */
specifier|public
specifier|static
name|ISVNAuthenticationManager
name|createDefaultAuthenticationManager
parameter_list|(
name|File
name|configDir
parameter_list|)
block|{
return|return
name|createDefaultAuthenticationManager
argument_list|(
name|configDir
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Creates a default authentication manager that uses the default SVN's      *<i>servers</i> configuration and provided user's credentials. Whether      * the default auth storage is used or not depends on the 'store-auth-creds'</i>      * option that can be found in the SVN's<i>config</i> file under the      *<i>[auth]</i> section.      *       * @param userName      *            a user's name      * @param password      *            a user's password      * @return a default implementation of the credentials and servers      *         configuration driver interface      */
specifier|public
specifier|static
name|ISVNAuthenticationManager
name|createDefaultAuthenticationManager
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
block|{
return|return
name|createDefaultAuthenticationManager
argument_list|(
literal|null
argument_list|,
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
comment|/**      * Creates a default authentication manager that uses the provided      * configuration directory and user's credentials. Whether the default auth      * storage is used or not depends on the 'store-auth-creds'</i> option that      * is looked up in the<i>config</i> file under the<i>[auth]</i> section.      * Files<i>config</i> and<i>servers</i> will be created (if they still      * don't exist) in the specified directory (they are the same as those ones      * you can find in the default SVN's run-time configuration area).      *       * @param configDir      *            a new location of the run-time configuration area      * @param userName      *            a user's name      * @param password      *            a user's password      * @return a default implementation of the credentials and servers      *         configuration driver interface      */
specifier|public
specifier|static
name|ISVNAuthenticationManager
name|createDefaultAuthenticationManager
parameter_list|(
name|File
name|configDir
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|DefaultSVNOptions
name|options
init|=
name|createDefaultOptions
argument_list|(
name|configDir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|store
init|=
name|options
operator|.
name|isAuthStorageEnabled
argument_list|()
decl_stmt|;
return|return
name|createDefaultAuthenticationManager
argument_list|(
name|configDir
argument_list|,
name|userName
argument_list|,
name|password
argument_list|,
name|store
argument_list|)
return|;
block|}
comment|/**      * Creates a default authentication manager that uses the provided      * configuration directory and user's credentials. The      *<code>storeAuth</code> parameter affects on using the auth storage.      *       *       * @param configDir      *            a new location of the run-time configuration area      * @param userName      *            a user's name      * @param password      *            a user's password      * @param storeAuth      *            if<span class="javakeyword">true</span> then the auth      *            storage is enabled, otherwise disabled      * @return a default implementation of the credentials and servers      *         configuration driver interface      */
specifier|public
specifier|static
name|ISVNAuthenticationManager
name|createDefaultAuthenticationManager
parameter_list|(
name|File
name|configDir
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|,
name|boolean
name|storeAuth
parameter_list|)
block|{
return|return
name|createDefaultAuthenticationManager
argument_list|(
name|configDir
argument_list|,
name|userName
argument_list|,
name|password
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|storeAuth
argument_list|)
return|;
block|}
comment|/**      * Creates a default authentication manager that uses the provided      * configuration directory and user's credentials. The      *<code>storeAuth</code> parameter affects on using the auth storage.      *       *       * @param configDir      *            a new location of the run-time configuration area      * @param userName      *            a user's name      * @param password      *            a user's password      * @param privateKey      *            a private key file for SSH session      * @param passphrase      *            a passphrase that goes with the key file      * @param storeAuth      *            if<span class="javakeyword">true</span> then the auth      *            storage is enabled, otherwise disabled      * @return a default implementation of the credentials and servers      *         configuration driver interface      */
specifier|public
specifier|static
name|ISVNAuthenticationManager
name|createDefaultAuthenticationManager
parameter_list|(
name|File
name|configDir
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|,
name|File
name|privateKey
parameter_list|,
name|String
name|passphrase
parameter_list|,
name|boolean
name|storeAuth
parameter_list|)
block|{
return|return
operator|new
name|DefaultSVNAuthenticationManager
argument_list|(
name|configDir
argument_list|,
name|storeAuth
argument_list|,
name|userName
argument_list|,
name|password
argument_list|,
name|privateKey
argument_list|,
name|passphrase
argument_list|)
return|;
block|}
comment|/**      * Creates a default run-time configuration options driver that uses the      * provided configuration directory.      *       *<p>      * If<code>dir</code> is not<span class="javakeyword">null</span> then      * all necessary config files (in particular<i>config</i> and<i>servers</i>)      * will be created in this directory if they still don't exist. Those files      * are the same as those ones you can find in the default SVN's run-time      * configuration area.      *       * @param dir      *            a new location of the run-time configuration area      * @param readonly      *            if<span class="javakeyword">true</span> then run-time      *            configuration options are available only for reading, if<span      *            class="javakeyword">false</span> then those options are      *            available for both reading and writing      * @return a default implementation of the run-time configuration options      *         driver interface      */
specifier|public
specifier|static
name|DefaultSVNOptions
name|createDefaultOptions
parameter_list|(
name|File
name|dir
parameter_list|,
name|boolean
name|readonly
parameter_list|)
block|{
return|return
operator|new
name|DefaultSVNOptions
argument_list|(
name|dir
argument_list|,
name|readonly
argument_list|)
return|;
block|}
comment|/**      * Creates a default run-time configuration options driver that uses the      * default SVN's run-time configuration area.      *       * @param readonly      *            if<span class="javakeyword">true</span> then run-time      *            configuration options are available only for reading, if<span      *            class="javakeyword">false</span> then those options are      *            available for both reading and writing      * @return a default implementation of the run-time configuration options      *         driver interface      * @see #getDefaultConfigurationDirectory()      */
specifier|public
specifier|static
name|DefaultSVNOptions
name|createDefaultOptions
parameter_list|(
name|boolean
name|readonly
parameter_list|)
block|{
return|return
operator|new
name|DefaultSVNOptions
argument_list|(
literal|null
argument_list|,
name|readonly
argument_list|)
return|;
block|}
comment|/**      * Determines if a directory is under version control.      *       * @param dir      *            a directory to check      * @return<span class="javakeyword">true</span> if versioned, otherwise      *<span class="javakeyword">false</span>      */
specifier|public
specifier|static
name|boolean
name|isVersionedDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|SVNWCAccess
name|wcAccess
init|=
name|SVNWCAccess
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|wcAccess
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|Level
operator|.
name|FINEST
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
finally|finally
block|{
try|try
block|{
name|wcAccess
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Determines if a directory is the root of the Working Copy.      *       * @param versionedDir      *            a versioned directory to check      * @return<span class="javakeyword">true</span> if      *<code>versionedDir</code> is versioned and the WC root (or the      *         root of externals if<code>considerExternalAsRoot</code> is      *<span class="javakeyword">true</span>), otherwise<span      *         class="javakeyword">false</span>      * @throws SVNException      * @since 1.1      */
specifier|public
specifier|static
name|boolean
name|isWorkingCopyRoot
parameter_list|(
specifier|final
name|File
name|versionedDir
parameter_list|)
throws|throws
name|SVNException
block|{
name|SVNWCAccess
name|wcAccess
init|=
name|SVNWCAccess
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|wcAccess
operator|.
name|open
argument_list|(
name|versionedDir
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|Level
operator|.
name|FINEST
argument_list|)
expr_stmt|;
return|return
name|wcAccess
operator|.
name|isWCRoot
argument_list|(
name|versionedDir
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|wcAccess
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @param versionedDir      *            a versioned directory to check      * @param externalIsRoot      * @return<span class="javakeyword">true</span> if      *<code>versionedDir</code> is versioned and the WC root (or the      *         root of externals if<code>considerExternalAsRoot</code> is      *<span class="javakeyword">true</span>), otherwise<span      *         class="javakeyword">false</span>      * @throws SVNException      * @deprecated use {@link #isWorkingCopyRoot(File)}} instead      */
specifier|public
specifier|static
name|boolean
name|isWorkingCopyRoot
parameter_list|(
specifier|final
name|File
name|versionedDir
parameter_list|,
name|boolean
name|externalIsRoot
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|isWorkingCopyRoot
argument_list|(
name|versionedDir
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|externalIsRoot
condition|)
block|{
return|return
literal|true
return|;
block|}
name|File
name|root
init|=
name|getWorkingCopyRoot
argument_list|(
name|versionedDir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|root
operator|.
name|equals
argument_list|(
name|versionedDir
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns the Working Copy root directory given a versioned directory that      * belongs to the Working Copy.      *       *<p>      * If both<span>versionedDir</span> and its parent directory are not      * versioned this method returns<span class="javakeyword">null</span>.      *       * @param versionedDir      *            a directory belonging to the WC which root is to be searched      *            for      * @param stopOnExtenrals      *            if<span class="javakeyword">true</span> then this method      *            will stop at the directory on which any externals definitions      *            are set      * @return the WC root directory (if it is found) or<span      *         class="javakeyword">null</span>.      * @throws SVNException      */
specifier|public
specifier|static
name|File
name|getWorkingCopyRoot
parameter_list|(
name|File
name|versionedDir
parameter_list|,
name|boolean
name|stopOnExtenrals
parameter_list|)
throws|throws
name|SVNException
block|{
name|versionedDir
operator|=
name|versionedDir
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
if|if
condition|(
name|versionedDir
operator|==
literal|null
operator|||
operator|(
operator|!
name|isVersionedDirectory
argument_list|(
name|versionedDir
argument_list|)
operator|&&
operator|(
name|versionedDir
operator|.
name|getParentFile
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|isVersionedDirectory
argument_list|(
name|versionedDir
operator|.
name|getParentFile
argument_list|()
argument_list|)
operator|)
operator|)
condition|)
block|{
comment|// both this dir and its parent are not versioned,
comment|// or dir is root and not versioned
return|return
literal|null
return|;
block|}
name|File
name|parent
init|=
name|versionedDir
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|versionedDir
return|;
block|}
if|if
condition|(
name|isWorkingCopyRoot
argument_list|(
name|versionedDir
argument_list|)
condition|)
block|{
comment|// this is root.
if|if
condition|(
name|stopOnExtenrals
condition|)
block|{
return|return
name|versionedDir
return|;
block|}
name|File
name|parentRoot
init|=
name|getWorkingCopyRoot
argument_list|(
name|parent
argument_list|,
name|stopOnExtenrals
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentRoot
operator|==
literal|null
condition|)
block|{
comment|// if parent is not versioned return this dir.
return|return
name|versionedDir
return|;
block|}
comment|// parent is versioned. we have to check if it contains externals
comment|// definition for this dir.
while|while
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|SVNWCAccess
name|parentAccess
init|=
name|SVNWCAccess
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|SVNAdminArea
name|dir
init|=
name|parentAccess
operator|.
name|open
argument_list|(
name|parent
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|SVNVersionedProperties
name|props
init|=
name|dir
operator|.
name|getProperties
argument_list|(
name|dir
operator|.
name|getThisDirName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|externalsProperty
init|=
name|props
operator|.
name|getStringPropertyValue
argument_list|(
name|SVNProperty
operator|.
name|EXTERNALS
argument_list|)
decl_stmt|;
name|SVNExternal
index|[]
name|externals
init|=
name|externalsProperty
operator|!=
literal|null
condition|?
name|SVNExternal
operator|.
name|parseExternals
argument_list|(
name|dir
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|externalsProperty
argument_list|)
else|:
operator|new
name|SVNExternal
index|[
literal|0
index|]
decl_stmt|;
comment|// now externals could point to our dir.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|externals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SVNExternal
name|external
init|=
name|externals
index|[
name|i
index|]
decl_stmt|;
name|File
name|externalFile
init|=
operator|new
name|File
argument_list|(
name|parent
argument_list|,
name|external
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|externalFile
operator|.
name|equals
argument_list|(
name|versionedDir
argument_list|)
condition|)
block|{
return|return
name|parentRoot
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|SVNCancelException
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
finally|finally
block|{
name|parentAccess
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|parent
operator|.
name|equals
argument_list|(
name|parentRoot
argument_list|)
condition|)
block|{
break|break;
block|}
name|parent
operator|=
name|parent
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
return|return
name|versionedDir
return|;
block|}
return|return
name|getWorkingCopyRoot
argument_list|(
name|parent
argument_list|,
name|stopOnExtenrals
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isEclipse
parameter_list|()
block|{
if|if
condition|(
name|ourIsEclipse
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|ClassLoader
name|loader
init|=
name|SVNWCUtil
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
name|loader
operator|=
name|ClassLoader
operator|.
name|getSystemClassLoader
argument_list|()
expr_stmt|;
block|}
name|Class
name|platform
init|=
name|loader
operator|.
name|loadClass
argument_list|(
literal|"org.eclipse.core.runtime.Platform"
argument_list|)
decl_stmt|;
name|Method
name|isRunning
init|=
name|platform
operator|.
name|getMethod
argument_list|(
literal|"isRunning"
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Object
name|result
init|=
name|isRunning
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
operator|&&
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
name|ourIsEclipse
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
block|}
name|ourIsEclipse
operator|=
name|Boolean
operator|.
name|FALSE
expr_stmt|;
block|}
return|return
name|ourIsEclipse
operator|.
name|booleanValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

