begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * \$Id\$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|client
package|;
end_package

begin_import
import|import
name|jline
operator|.
name|ConsoleReader
import|;
end_import

begin_import
import|import
name|jline
operator|.
name|Terminal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|CommandContinuation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debugger
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
name|EOFException
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

begin_class
specifier|public
class|class
name|DebugClient
implements|implements
name|ResponseListener
block|{
specifier|private
specifier|final
specifier|static
name|Pattern
name|REGEX_COMMAND
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^([^\\s]+)\\s*(.*)$"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COMMAND_RUN
init|=
literal|"run"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COMMAND_QUIT
init|=
literal|"quit"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COMMAND_STEP
init|=
literal|"step"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COMMAND_CONT
init|=
literal|"cont"
decl_stmt|;
specifier|private
name|ConsoleReader
name|console
decl_stmt|;
specifier|private
name|Debugger
name|debugger
decl_stmt|;
specifier|private
name|DebuggingSource
name|source
init|=
literal|null
decl_stmt|;
specifier|public
name|DebugClient
parameter_list|()
throws|throws
name|IOException
block|{
name|Terminal
operator|.
name|setupTerminal
argument_list|()
expr_stmt|;
name|console
operator|=
operator|new
name|ConsoleReader
argument_list|()
expr_stmt|;
name|debugger
operator|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|readlineInputLoop
parameter_list|()
block|{
name|String
name|prompt
init|=
literal|"offline"
decl_stmt|;
name|boolean
name|cont
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|cont
condition|)
block|{
try|try
block|{
name|String
name|line
init|=
name|console
operator|.
name|readLine
argument_list|(
name|prompt
operator|+
literal|"> "
argument_list|)
decl_stmt|;
name|cont
operator|=
name|parseInput
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
break|break;
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
literal|"Exception caught: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
name|source
operator|.
name|stop
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|parseInput
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|Matcher
name|commandMatcher
init|=
name|REGEX_COMMAND
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|commandMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|command
init|=
name|commandMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|arguments
init|=
name|commandMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|COMMAND_RUN
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
name|run
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
if|else if
condition|(
name|COMMAND_STEP
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
name|source
operator|.
name|stepInto
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|else if
condition|(
name|COMMAND_CONT
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
name|source
operator|.
name|run
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|else if
condition|(
name|COMMAND_QUIT
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unknown command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unknown command: "
operator|+
name|line
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|run
parameter_list|(
name|String
name|arguments
parameter_list|)
block|{
name|String
name|target
init|=
literal|"http://127.0.0.1:8080/exist/"
operator|+
name|arguments
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Connecting to "
operator|+
name|target
argument_list|)
expr_stmt|;
try|try
block|{
name|source
operator|=
name|debugger
operator|.
name|init
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to initialize session. Connection timed out."
argument_list|)
expr_stmt|;
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
literal|"Error while initializing session: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Timeout while initializing session: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|responseEvent
parameter_list|(
name|CommandContinuation
name|command
parameter_list|,
name|Response
name|response
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|command
operator|.
name|getStatus
argument_list|()
operator|+
literal|": "
operator|+
name|response
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|DebugClient
name|client
init|=
operator|new
name|DebugClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|readlineInputLoop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

