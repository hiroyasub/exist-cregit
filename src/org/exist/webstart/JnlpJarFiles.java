begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webstart
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|stream
operator|.
name|Collectors
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
name|start
operator|.
name|LatestFileResolver
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

begin_comment
comment|/**  * Class for managing webstart jar files.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|JnlpJarFiles
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|JnlpJarFiles
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Path
argument_list|>
name|allFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Path
name|mainJar
decl_stmt|;
comment|// Names of core jar files sans ".jar" extension.
comment|// Use %latest% token in place of a version string.
specifier|private
specifier|final
name|String
name|allJarNames
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"antlr-%latest%"
block|,
literal|"cglib-nodep-%latest%"
block|,
literal|"clj-ds-%latest%"
block|,
literal|"commons-codec-%latest%"
block|,
literal|"commons-collections-%latest%"
block|,
literal|"commons-io-%latest%"
block|,
literal|"commons-logging-%latest%"
block|,
literal|"commons-pool-%latest%"
block|,
literal|"jargo-%latest%"
block|,
literal|"guava-%latest%"
block|,
literal|"gnu-crypto-%latest%"
block|,
literal|"j8fu-%latest%"
block|,
literal|"jackson-core-%latest%"
block|,
literal|"jcip-annotations-%latest%"
block|,
literal|"jline-%latest%"
block|,
literal|"jta-%latest%"
block|,
literal|"log4j-api-%latest%"
block|,
literal|"log4j-core-%latest%"
block|,
literal|"log4j-jul-%latest%"
block|,
literal|"log4j-slf4j-impl-%latest%"
block|,
literal|"pkg-repo"
block|,
literal|"quartz-%latest%"
block|,
literal|"rsyntaxtextarea-%latest%"
block|,
literal|"slf4j-api-%latest%"
block|,
literal|"ws-commons-util-%latest%"
block|,
literal|"xmldb"
block|,
literal|"xmlrpc-client-%latest%"
block|,
literal|"xmlrpc-common-%latest%"
block|}
decl_stmt|;
comment|// Resolves jar file patterns from jars[].
specifier|private
specifier|final
name|LatestFileResolver
name|jarFileResolver
init|=
operator|new
name|LatestFileResolver
argument_list|()
decl_stmt|;
comment|/**      * Get jar file specified by file pattern.      *      * @param folder          Directory containing the jars.      * @param jarFileBaseName Name of jar file, including %latest% token if      *                        necessary sans .jar file extension.      * @return File object of jar file, null if not found.      */
specifier|private
name|Path
name|getJarFromLocation
parameter_list|(
specifier|final
name|Path
name|folder
parameter_list|,
specifier|final
name|String
name|jarFileBaseName
parameter_list|)
block|{
specifier|final
name|String
name|fileToFind
init|=
name|folder
operator|.
name|normalize
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
name|java
operator|.
name|io
operator|.
name|File
operator|.
name|separatorChar
operator|+
name|jarFileBaseName
operator|+
literal|".jar"
decl_stmt|;
specifier|final
name|String
name|resolvedFile
init|=
name|jarFileResolver
operator|.
name|getResolvedFileName
argument_list|(
name|fileToFind
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|jar
init|=
name|Paths
operator|.
name|get
argument_list|(
name|resolvedFile
argument_list|)
operator|.
name|normalize
argument_list|()
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|jar
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Found match: %s for file pattern: %s"
argument_list|,
name|resolvedFile
argument_list|,
name|fileToFind
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|jar
return|;
block|}
else|else
block|{
name|LOGGER
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Could not resolve file pattern: %s"
argument_list|,
name|fileToFind
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|// Copy jars from map to list
specifier|private
name|void
name|addToJars
parameter_list|(
specifier|final
name|Path
name|jar
parameter_list|)
block|{
if|if
condition|(
name|jar
operator|!=
literal|null
operator|&&
name|FileUtils
operator|.
name|fileName
argument_list|(
name|jar
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
name|allFiles
operator|.
name|put
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|jar
argument_list|)
argument_list|,
name|jar
argument_list|)
expr_stmt|;
comment|// Add jar.pack.gz if existent
specifier|final
name|Path
name|pkgz
init|=
name|getJarPackGz
argument_list|(
name|jar
argument_list|)
decl_stmt|;
if|if
condition|(
name|pkgz
operator|!=
literal|null
condition|)
block|{
name|allFiles
operator|.
name|put
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|pkgz
argument_list|)
argument_list|,
name|pkgz
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Creates a new instance of JnlpJarFiles      *      * @param jnlpHelper      */
specifier|public
name|JnlpJarFiles
parameter_list|(
specifier|final
name|JnlpHelper
name|jnlpHelper
parameter_list|)
block|{
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Initializing jar files Webstart"
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Number of webstart jars=%s"
argument_list|,
name|allJarNames
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|// Setup CORE jars
for|for
control|(
specifier|final
name|String
name|jarname
range|:
name|allJarNames
control|)
block|{
name|Path
name|location
init|=
name|getJarFromLocation
argument_list|(
name|jnlpHelper
operator|.
name|getCoreJarsFolder
argument_list|()
argument_list|,
name|jarname
argument_list|)
decl_stmt|;
name|addToJars
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
comment|// Setup exist.jar
name|mainJar
operator|=
name|jnlpHelper
operator|.
name|getExistJarFolder
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"exist.jar"
argument_list|)
expr_stmt|;
name|addToJars
argument_list|(
name|mainJar
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get All jar file as list.      *      * @return list of jar files.      */
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getAllWebstartJars
parameter_list|()
block|{
return|return
name|allFiles
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
parameter_list|(
name|file
parameter_list|)
lambda|->
operator|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
operator|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Get file reference for JAR file.      *      * @param key      * @return Reference to the jar file, NULL if not existent.      */
specifier|public
name|Path
name|getJarFile
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
block|{
return|return
name|allFiles
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|private
name|Path
name|getJarPackGz
parameter_list|(
specifier|final
name|Path
name|jarName
parameter_list|)
block|{
specifier|final
name|String
name|path
init|=
name|jarName
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|".pack.gz"
decl_stmt|;
specifier|final
name|Path
name|pkgz
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|pkgz
argument_list|)
condition|)
block|{
return|return
name|pkgz
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Get last modified of main JAR file      */
specifier|public
name|long
name|getLastModified
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|mainJar
operator|==
literal|null
operator|)
condition|?
operator|-
literal|1
else|:
name|Files
operator|.
name|getLastModifiedTime
argument_list|(
name|mainJar
argument_list|)
operator|.
name|toMillis
argument_list|()
return|;
block|}
block|}
end_class

end_unit

