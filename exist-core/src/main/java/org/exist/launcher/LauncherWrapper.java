begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|launcher
package|;
end_package

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
name|javax
operator|.
name|swing
operator|.
name|*
import|;
end_import

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
name|io
operator|.
name|InputStreamReader
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
name|*
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|launcher
operator|.
name|ConfigurationUtility
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A wrapper to start a Java process using start.jar with correct VM settings.  * Spawns a new Java VM using Ant. Mainly used when launching  * eXist by double clicking on start.jar.  *  * @author Tobi Krebs  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|LauncherWrapper
block|{
specifier|private
specifier|final
specifier|static
name|String
name|LAUNCHER
init|=
name|org
operator|.
name|exist
operator|.
name|launcher
operator|.
name|Launcher
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|OS
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|LauncherWrapper
name|wrapper
init|=
operator|new
name|LauncherWrapper
argument_list|(
name|LAUNCHER
argument_list|)
decl_stmt|;
if|if
condition|(
name|ConfigurationUtility
operator|.
name|isFirstStart
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"First launch: opening configuration dialog"
argument_list|)
expr_stmt|;
name|ConfigurationDialog
name|configDialog
init|=
operator|new
name|ConfigurationDialog
argument_list|(
name|restart
lambda|->
block|{
name|wrapper
operator|.
name|launch
argument_list|()
expr_stmt|;
comment|// make sure the process dies when the dialog is closed
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|configDialog
operator|.
name|open
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|configDialog
operator|.
name|requestFocus
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|wrapper
operator|.
name|launch
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|command
decl_stmt|;
specifier|public
name|LauncherWrapper
parameter_list|(
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
specifier|public
name|void
name|launch
parameter_list|()
block|{
specifier|final
name|String
name|debugLauncher
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.debug.launcher"
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|launcherProperties
init|=
name|ConfigurationUtility
operator|.
name|loadProperties
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|getJavaCmd
argument_list|()
argument_list|)
expr_stmt|;
name|getJavaOpts
argument_list|(
name|args
argument_list|,
name|launcherProperties
argument_list|)
expr_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|debugLauncher
argument_list|)
operator|&&
operator|!
literal|"client"
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-Xdebug"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5006"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Debug mode for Launcher on JDWP port 5006. Will await connection..."
argument_list|)
expr_stmt|;
block|}
comment|// recreate the classpath
name|args
operator|.
name|add
argument_list|(
literal|"-cp"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|getClassPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// call exist main with our new command
name|args
operator|.
name|add
argument_list|(
literal|"org.exist.start.Main"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
try|try
block|{
name|run
argument_list|(
name|args
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
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
literal|null
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Error Running Process"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getClassPath
parameter_list|()
block|{
comment|// if we are booted using appassembler-booter, then we should use `app.class.path`
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"app.class.path"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|run
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Executing: ["
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|args
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|args
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|home
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|pb
operator|.
name|directory
argument_list|(
name|home
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|pb
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pb
operator|.
name|inheritIO
argument_list|()
expr_stmt|;
name|pb
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|String
name|getJavaCmd
parameter_list|()
block|{
specifier|final
name|File
name|javaHome
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|OS
operator|.
name|startsWith
argument_list|(
literal|"windows"
argument_list|)
condition|)
block|{
name|Path
name|javaBin
init|=
name|Paths
operator|.
name|get
argument_list|(
name|javaHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"bin"
argument_list|,
literal|"javaw.exe"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isExecutable
argument_list|(
name|javaBin
argument_list|)
condition|)
block|{
return|return
literal|'"'
operator|+
name|javaBin
operator|.
name|toString
argument_list|()
operator|+
literal|'"'
return|;
block|}
name|javaBin
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|javaHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"bin"
argument_list|,
literal|"java.exe"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|isExecutable
argument_list|(
name|javaBin
argument_list|)
condition|)
block|{
return|return
literal|'"'
operator|+
name|javaBin
operator|.
name|toString
argument_list|()
operator|+
literal|'"'
return|;
block|}
block|}
else|else
block|{
name|Path
name|javaBin
init|=
name|Paths
operator|.
name|get
argument_list|(
name|javaHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"bin"
argument_list|,
literal|"java"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isExecutable
argument_list|(
name|javaBin
argument_list|)
condition|)
block|{
return|return
name|javaBin
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|return
literal|"java"
return|;
block|}
specifier|protected
name|void
name|getJavaOpts
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|,
specifier|final
name|Properties
name|launcherProperties
parameter_list|)
block|{
name|getLauncherOpts
argument_list|(
name|args
argument_list|,
name|launcherProperties
argument_list|)
expr_stmt|;
name|boolean
name|foundExistHomeSysProp
init|=
literal|false
decl_stmt|;
specifier|final
name|Properties
name|sysProps
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
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
name|entry
range|:
name|sysProps
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"exist."
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
literal|"log4j."
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
literal|"jetty."
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
literal|"app."
argument_list|)
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-D"
operator|+
name|key
operator|+
literal|"="
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"exist.home"
argument_list|)
condition|)
block|{
name|foundExistHomeSysProp
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|foundExistHomeSysProp
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-Dexist.home=\".\""
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|LAUNCHER
argument_list|)
operator|&&
literal|"mac os x"
operator|.
name|equals
argument_list|(
name|OS
argument_list|)
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-Dapple.awt.UIElement=true"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|getLauncherOpts
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|,
specifier|final
name|Properties
name|launcherProperties
parameter_list|)
block|{
for|for
control|(
specifier|final
name|String
name|key
range|:
name|launcherProperties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"memory."
argument_list|)
condition|)
block|{
if|if
condition|(
name|LAUNCHER_PROPERTY_MAX_MEM
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-Xmx"
operator|+
name|launcherProperties
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
operator|+
literal|'m'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|LAUNCHER_PROPERTY_MIN_MEM
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-Xms"
operator|+
name|launcherProperties
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
operator|+
literal|'m'
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|LAUNCHER_PROPERTY_VMOPTIONS
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
name|launcherProperties
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|LAUNCHER_PROPERTY_VMOPTIONS
operator|+
literal|'.'
argument_list|)
condition|)
block|{
specifier|final
name|String
name|os
init|=
name|key
operator|.
name|substring
argument_list|(
operator|(
name|LAUNCHER_PROPERTY_VMOPTIONS
operator|+
literal|'.'
operator|)
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|OS
operator|.
name|contains
argument_list|(
name|os
argument_list|)
condition|)
block|{
specifier|final
name|String
name|value
init|=
name|launcherProperties
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|stream
argument_list|(
name|value
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
argument_list|)
operator|.
name|forEach
argument_list|(
name|args
operator|::
name|add
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

