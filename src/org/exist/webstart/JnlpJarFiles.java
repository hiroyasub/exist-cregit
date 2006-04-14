begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-06 Wolfgang M.  * Meier meier@ifs.tu-darmstadt.de http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *  * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  * $Id$  */
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|start
operator|.
name|LatestFileResolver
import|;
end_import

begin_comment
comment|/**  *  Class for managing webstart jar files.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|JnlpJarFiles
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|JnlpJarFiles
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Holders for jar files
specifier|private
name|File
index|[]
name|_coreJars
decl_stmt|;
specifier|private
name|File
name|_mainJar
decl_stmt|;
comment|// Names of core jar files sans ".jar" extension.
comment|// Use %latest% token in place of a version string.
specifier|private
name|String
name|jars
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"antlr"
block|,
literal|"commons-pool-%latest%"
block|,
literal|"excalibur-cli-%latest%"
block|,
literal|"jEdit-syntax"
block|,
literal|"jgroups-all"
block|,
literal|"jline-%latest%"
block|,
literal|"log4j-%latest%"
block|,
literal|"resolver"
block|,
literal|"xmldb"
block|,
literal|"xmlrpc-%latest%-patched"
block|}
decl_stmt|;
comment|// Resolves jar file patterns from jars[].
specifier|private
name|LatestFileResolver
name|jarFileResolver
init|=
operator|new
name|LatestFileResolver
argument_list|()
decl_stmt|;
comment|/**      * Get jar file specified by file pattern.      * @param folder  Directory containing the jars.      * @param jarFileBaseName  Name of jar file, including %latest% token if      * necessary sans .jar file extension.      * @return File object of jar file, null if not found.      */
specifier|public
name|File
name|getJar
parameter_list|(
name|File
name|folder
parameter_list|,
name|String
name|jarFileBaseName
parameter_list|)
block|{
name|String
name|fileToFind
init|=
name|folder
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|jarFileBaseName
operator|+
literal|".jar"
decl_stmt|;
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
name|File
name|jar
init|=
operator|new
name|File
argument_list|(
name|resolvedFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|jar
operator|.
name|exists
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Found match: "
operator|+
name|resolvedFile
operator|+
literal|" for file pattern: "
operator|+
name|fileToFind
argument_list|)
expr_stmt|;
return|return
name|jar
return|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Could not resolve file pattern: "
operator|+
name|fileToFind
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Creates a new instance of JnlpJarFiles      *       * @param jnlpHelper      */
specifier|public
name|JnlpJarFiles
parameter_list|(
name|JnlpHelper
name|jnlpHelper
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing jar files Webstart"
argument_list|)
expr_stmt|;
comment|// Setup array CORE jars
name|int
name|nrCoreJars
init|=
name|jars
operator|.
name|length
decl_stmt|;
name|_coreJars
operator|=
operator|new
name|File
index|[
name|nrCoreJars
index|]
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Number of webstart jars="
operator|+
name|nrCoreJars
argument_list|)
expr_stmt|;
comment|// Setup CORE jars
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nrCoreJars
condition|;
name|i
operator|++
control|)
block|{
name|_coreJars
index|[
name|i
index|]
operator|=
name|getJar
argument_list|(
name|jnlpHelper
operator|.
name|getCoreJarsFolder
argument_list|()
argument_list|,
name|jars
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Setup exist.jar
name|_mainJar
operator|=
operator|new
name|File
argument_list|(
name|jnlpHelper
operator|.
name|getExistJarFolder
argument_list|()
argument_list|,
literal|"exist.jar"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get references to all "core" jar files.      * @return Array of Files.      */
specifier|public
name|File
index|[]
name|getCoreJars
parameter_list|()
block|{
return|return
name|_coreJars
return|;
block|}
comment|/**      * Get references to all "exist" jar files.      * @return Reference to exist.jar.      */
specifier|public
name|File
name|getMainJar
parameter_list|()
block|{
return|return
name|_mainJar
return|;
block|}
comment|/**      * Setter for property mainJar.      * @param mainJar New value of property mainJar.      */
specifier|public
name|void
name|setMainJar
parameter_list|(
name|File
name|mainJar
parameter_list|)
block|{
name|_mainJar
operator|=
name|mainJar
expr_stmt|;
block|}
comment|/**      *  Get File reference of associated jar-file.      * @param name       * @return File reference to resource.      */
specifier|public
name|File
name|getFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|File
name|retVal
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"exist.jar"
argument_list|)
condition|)
block|{
name|retVal
operator|=
name|_mainJar
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|found
operator|&&
name|index
operator|<
name|_coreJars
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|_coreJars
index|[
name|index
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|retVal
operator|=
name|_coreJars
index|[
name|index
index|]
expr_stmt|;
block|}
else|else
block|{
name|index
operator|++
expr_stmt|;
block|}
block|}
block|}
return|return
name|retVal
return|;
block|}
block|}
end_class

end_unit

