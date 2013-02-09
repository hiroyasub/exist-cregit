begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id: Configuration.java 5400 2007-02-25 13:20:15Z wolfgang_m $  */
end_comment

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
name|io
operator|.
name|IOException
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

begin_class
specifier|public
class|class
name|SingleInstanceConfiguration
extends|extends
name|Configuration
block|{
comment|/* FIXME:  It's not clear whether this class is meant to be a singleton (due to the static          * file and existHome fields and static methods), or if we should allow many instances to          * run around in the system.  Right now, any attempts to create multiple instances will          * likely get the system confused.  Let's decide which one it should be and fix it properly.          *          * This class cannot be a singleton as it is possible to run multiple instances of the database          * on the same system.          */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
name|SingleInstanceConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//Logger
specifier|protected
specifier|static
name|String
name|_configFile
init|=
literal|null
decl_stmt|;
comment|//config file (conf.xml by default)
specifier|protected
specifier|static
name|File
name|_existHome
init|=
literal|null
decl_stmt|;
specifier|public
name|SingleInstanceConfiguration
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
name|this
argument_list|(
literal|"conf.xml"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SingleInstanceConfiguration
parameter_list|(
name|String
name|configFilename
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|this
argument_list|(
name|configFilename
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SingleInstanceConfiguration
parameter_list|(
name|String
name|configFilename
parameter_list|,
name|String
name|existHomeDirname
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|super
argument_list|(
name|configFilename
argument_list|,
name|existHomeDirname
argument_list|)
expr_stmt|;
name|_configFile
operator|=
name|configFilePath
expr_stmt|;
name|_existHome
operator|=
name|existHome
expr_stmt|;
block|}
comment|/**      * Returns the absolute path to the configuration file.      *      * @return the path to the configuration file      */
specifier|public
specifier|static
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|_configFile
operator|==
literal|null
condition|)
block|{
specifier|final
name|File
name|f
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"conf.xml"
argument_list|)
decl_stmt|;
return|return
name|f
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
return|return
name|_configFile
return|;
block|}
comment|/**      *  Check wether exist runs in Servlet container (as war file).      * @return TRUE if exist runs in servlet container.      */
specifier|public
specifier|static
name|boolean
name|isInWarFile
parameter_list|()
block|{
name|boolean
name|retVal
init|=
literal|true
decl_stmt|;
comment|// if existHome is not set,try to do so.
if|if
condition|(
name|_existHome
operator|==
literal|null
condition|)
block|{
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|new
name|File
argument_list|(
name|_existHome
argument_list|,
literal|"lib/core"
argument_list|)
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|retVal
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|retVal
return|;
block|}
comment|/**      *  Get folder in which the exist webapplications are found.      * For default install ("jar install") and in webcontainer ("war install")      * the location is different. (EXIST_HOME/webapps vs. TOMCAT/webapps/exist)      *      * @return folder.      */
specifier|public
specifier|static
name|File
name|getWebappHome
parameter_list|()
block|{
name|File
name|webappFolder
init|=
literal|null
decl_stmt|;
comment|// if existHome is not set,try to do so.
if|if
condition|(
name|_existHome
operator|==
literal|null
condition|)
block|{
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|isInWarFile
argument_list|()
condition|)
block|{
name|webappFolder
operator|=
operator|new
name|File
argument_list|(
name|_existHome
argument_list|,
literal|".."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|webappFolder
operator|=
operator|new
name|File
argument_list|(
name|_existHome
argument_list|,
literal|"webapp"
argument_list|)
expr_stmt|;
block|}
comment|// convert to real path
try|try
block|{
name|File
name|tmpFolder
init|=
name|webappFolder
operator|.
name|getCanonicalFile
argument_list|()
decl_stmt|;
name|webappFolder
operator|=
name|tmpFolder
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
comment|// oops ; use previous path
block|}
return|return
name|webappFolder
return|;
block|}
comment|/**      * Returns<code>true</code> if the directory<code>dir</code> contains a file      * named<tt>conf.xml</tt>.      *      * @param dir the directory      * @return<code>true</code> if the directory contains a configuration file      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|static
name|boolean
name|containsConfig
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|config
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|!=
literal|null
operator|&&
name|dir
operator|.
name|exists
argument_list|()
operator|&&
name|dir
operator|.
name|isDirectory
argument_list|()
operator|&&
name|dir
operator|.
name|canRead
argument_list|()
condition|)
block|{
specifier|final
name|File
name|c
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
return|return
name|c
operator|.
name|exists
argument_list|()
operator|&&
name|c
operator|.
name|isFile
argument_list|()
operator|&&
name|c
operator|.
name|canRead
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

