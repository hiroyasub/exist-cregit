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
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|DefaultLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|taskdefs
operator|.
name|Java
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|Commandline
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
name|ConfigurationHelper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
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
name|*
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
name|Properties
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
name|boolean
name|spawn
init|=
name|SystemTray
operator|.
name|isSupported
argument_list|()
decl_stmt|;
name|LauncherWrapper
name|wrapper
init|=
operator|new
name|LauncherWrapper
argument_list|(
name|LAUNCHER
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|launch
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|String
name|command
decl_stmt|;
specifier|protected
name|File
name|output
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
name|launch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|launch
parameter_list|(
name|boolean
name|spawn
parameter_list|)
block|{
name|String
name|home
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
name|Project
name|project
init|=
operator|new
name|Project
argument_list|()
decl_stmt|;
name|project
operator|.
name|setBasedir
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|DefaultLogger
name|logger
init|=
operator|new
name|DefaultLogger
argument_list|()
decl_stmt|;
name|logger
operator|.
name|setOutputPrintStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|logger
operator|.
name|setErrorPrintStream
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|logger
operator|.
name|setMessageOutputLevel
argument_list|(
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|project
operator|.
name|addBuildListener
argument_list|(
name|logger
argument_list|)
expr_stmt|;
name|Java
name|java
init|=
operator|new
name|Java
argument_list|()
decl_stmt|;
name|java
operator|.
name|setFork
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|java
operator|.
name|setSpawn
argument_list|(
name|spawn
argument_list|)
expr_stmt|;
comment|//java.setClassname(org.exist.start.Main.class.getName());
name|java
operator|.
name|setProject
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|java
operator|.
name|setJar
argument_list|(
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"start.jar"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Path path = java.createClasspath();
comment|//path.setPath("start.jar");
name|Commandline
operator|.
name|Argument
name|jvmArgs
init|=
name|java
operator|.
name|createJvmarg
argument_list|()
decl_stmt|;
name|String
name|javaOpts
init|=
name|getJavaOpts
argument_list|(
name|home
argument_list|)
decl_stmt|;
name|jvmArgs
operator|.
name|setLine
argument_list|(
name|javaOpts
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Java opts: "
operator|+
name|javaOpts
argument_list|)
expr_stmt|;
name|Commandline
operator|.
name|Argument
name|args
init|=
name|java
operator|.
name|createArg
argument_list|()
decl_stmt|;
name|args
operator|.
name|setLine
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|java
operator|.
name|init
argument_list|()
expr_stmt|;
name|java
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|String
name|getJavaOpts
parameter_list|(
name|String
name|home
parameter_list|)
block|{
name|StringBuilder
name|opts
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|opts
operator|.
name|append
argument_list|(
name|getVMOpts
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|LAUNCHER
argument_list|)
operator|&&
name|OS
operator|.
name|equals
argument_list|(
literal|"mac os x"
argument_list|)
condition|)
block|{
name|opts
operator|.
name|append
argument_list|(
literal|" -Dapple.awt.UIElement=true"
argument_list|)
expr_stmt|;
block|}
name|opts
operator|.
name|append
argument_list|(
literal|" -Dexist.home="
argument_list|)
expr_stmt|;
name|opts
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|home
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|opts
operator|.
name|append
argument_list|(
literal|" -Djava.endorsed.dirs="
argument_list|)
expr_stmt|;
name|opts
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|home
operator|+
literal|"/lib/endorsed"
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
return|return
name|opts
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getVMOpts
parameter_list|()
block|{
name|StringBuilder
name|opts
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|Properties
name|vmProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|File
name|propFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"vm.properties"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|propFile
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|propFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|is
operator|=
name|LauncherWrapper
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"vm.properties"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|vmProperties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"vm.properties not found"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
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
name|vmProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
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
name|equals
argument_list|(
literal|"vmoptions"
argument_list|)
condition|)
block|{
name|opts
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"vmoptions."
argument_list|)
condition|)
block|{
name|String
name|os
init|=
name|key
operator|.
name|substring
argument_list|(
literal|"vmoptions."
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
name|opts
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|opts
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

