begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|IOException
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
name|model
operator|.
name|Breakpoint
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
name|model
operator|.
name|Location
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
name|model
operator|.
name|Variable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DebuggerTest
implements|implements
name|ResponseListener
block|{
annotation|@
name|Test
specifier|public
name|void
name|testConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
literal|"Database wasn't initilised."
argument_list|,
name|database
argument_list|)
expr_stmt|;
name|Debugger
name|debugger
init|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
decl_stmt|;
name|Exception
name|exception
init|=
literal|null
decl_stmt|;
comment|//if resource don't exist throw exception
try|try
block|{
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/xquery/fibo.xql"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"The resource don't exist, but debugger din't throw exception."
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|exception
operator|.
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"class java.io.IOException"
argument_list|)
expr_stmt|;
try|try
block|{
name|DebuggingSource
name|source
init|=
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/exist/xquery/fibo.xql"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Debugging source can't be NULL."
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|source
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDebugger
parameter_list|()
block|{
name|assertNotNull
argument_list|(
literal|"Database wasn't initilised."
argument_list|,
name|database
argument_list|)
expr_stmt|;
name|Debugger
name|debugger
decl_stmt|;
try|try
block|{
name|debugger
operator|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending init request"
argument_list|)
expr_stmt|;
name|DebuggingSource
name|source
init|=
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/exist/xquery/fibo.xql"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Debugging source can't be NULL."
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"get stack frames"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Location
argument_list|>
name|stack
init|=
name|source
operator|.
name|getStackFrames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"sending step-into"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|source
operator|.
name|stepInto
argument_list|(
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|//TODO: query current stage or wait for BREAK status ???
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"get stack frames"
argument_list|)
expr_stmt|;
name|stack
operator|=
name|source
operator|.
name|getStackFrames
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|9
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|source
operator|.
name|stepInto
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|//TODO: query current stage or wait for BREAK status ???
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"get stack frames"
argument_list|)
expr_stmt|;
name|stack
operator|=
name|source
operator|.
name|getStackFrames
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|78
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getColumnBegin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getColumnBegin
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending get-variables first time"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Variable
argument_list|>
name|vars
init|=
name|source
operator|.
name|getVariables
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|vars
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Variable
name|var
range|:
name|vars
control|)
block|{
if|if
condition|(
name|var
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"n"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|else if
condition|(
name|var
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"dbgp:session"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending get-local-variables"
argument_list|)
expr_stmt|;
name|vars
operator|=
name|source
operator|.
name|getLocalVariables
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|vars
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Variable
name|var
range|:
name|vars
control|)
block|{
name|assertEquals
argument_list|(
literal|"n"
argument_list|,
name|var
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending get-glocal-variables"
argument_list|)
expr_stmt|;
name|vars
operator|=
name|source
operator|.
name|getGlobalVariables
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|vars
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Variable
name|var
range|:
name|vars
control|)
block|{
name|assertEquals
argument_list|(
literal|"dbgp:session"
argument_list|,
name|var
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"sending step-into& waiting stop status"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|7
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|source
operator|.
name|stepInto
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending get-variables second time"
argument_list|)
expr_stmt|;
name|vars
operator|=
name|source
operator|.
name|getVariables
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|vars
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Variable
name|var
range|:
name|vars
control|)
block|{
if|if
condition|(
name|var
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"n"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|else if
condition|(
name|var
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"dbgp:session"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending step-over"
argument_list|)
expr_stmt|;
name|source
operator|.
name|stepOver
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending step-out"
argument_list|)
expr_stmt|;
name|source
operator|.
name|stepOut
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending run"
argument_list|)
expr_stmt|;
name|source
operator|.
name|run
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"IO exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Timeout exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBreakpoints
parameter_list|()
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
literal|"Database wasn't initilised."
argument_list|,
name|database
argument_list|)
expr_stmt|;
name|Debugger
name|debugger
init|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending init request"
argument_list|)
expr_stmt|;
name|DebuggingSource
name|source
init|=
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/exist/xquery/fibo.xql"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Debugging source can't be NULL."
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|Breakpoint
name|breakpoint
init|=
name|source
operator|.
name|newBreakpoint
argument_list|()
decl_stmt|;
name|breakpoint
operator|.
name|setLineno
argument_list|(
literal|24
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|sync
argument_list|()
expr_stmt|;
name|source
operator|.
name|run
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Location
argument_list|>
name|stack
init|=
name|source
operator|.
name|getStackFrames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|remove
argument_list|()
expr_stmt|;
name|source
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"IO exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Timeout exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLineBreakpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
literal|"Database wasn't initilised."
argument_list|,
name|database
argument_list|)
expr_stmt|;
name|Debugger
name|debugger
init|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending init request"
argument_list|)
expr_stmt|;
name|DebuggingSource
name|source
init|=
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/exist/xquery/fibo.xql"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Debugging source can't be NULL."
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|Breakpoint
name|breakpoint
init|=
name|source
operator|.
name|newBreakpoint
argument_list|()
decl_stmt|;
name|breakpoint
operator|.
name|setLineno
argument_list|(
literal|24
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|sync
argument_list|()
expr_stmt|;
name|source
operator|.
name|run
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Location
argument_list|>
name|stack
init|=
name|source
operator|.
name|getStackFrames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|source
operator|.
name|stepInto
argument_list|()
expr_stmt|;
name|stack
operator|=
name|source
operator|.
name|getStackFrames
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|source
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"IO exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Timeout exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEvaluation
parameter_list|()
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
literal|"Database wasn't initilised."
argument_list|,
name|database
argument_list|)
expr_stmt|;
name|Debugger
name|debugger
init|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending init request"
argument_list|)
expr_stmt|;
name|DebuggingSource
name|source
init|=
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/exist/xquery/fibo.xql"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Debugging source can't be NULL."
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|Breakpoint
name|breakpoint
init|=
name|source
operator|.
name|newBreakpoint
argument_list|()
decl_stmt|;
name|breakpoint
operator|.
name|setLineno
argument_list|(
literal|24
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|sync
argument_list|()
expr_stmt|;
name|String
name|res
init|=
name|source
operator|.
name|evaluate
argument_list|(
literal|"$dbgp:session"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|res
operator|=
name|source
operator|.
name|evaluate
argument_list|(
literal|"let $seq := (98.5, 98.3, 98.9) return count($seq)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|res
argument_list|)
expr_stmt|;
comment|//xquery engine have problem here, because context not copied correctly
comment|//			res = source.evaluate("f:fibo(2)");
comment|//			System.out.println(res);
name|source
operator|.
name|run
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Location
argument_list|>
name|stack
init|=
name|source
operator|.
name|getStackFrames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|24
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|remove
argument_list|()
expr_stmt|;
name|source
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"IO exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Timeout exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEvaluation2
parameter_list|()
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
literal|"Database wasn't initilised."
argument_list|,
name|database
argument_list|)
expr_stmt|;
name|Debugger
name|debugger
init|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending init request"
argument_list|)
expr_stmt|;
name|DebuggingSource
name|source
init|=
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/exist/xquery/debug-test.xql"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Debugging source can't be NULL."
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|Breakpoint
name|breakpoint
init|=
name|source
operator|.
name|newBreakpoint
argument_list|()
decl_stmt|;
name|breakpoint
operator|.
name|setLineno
argument_list|(
literal|19
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|sync
argument_list|()
expr_stmt|;
name|source
operator|.
name|run
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Location
argument_list|>
name|stack
init|=
name|source
operator|.
name|getStackFrames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|19
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|res
init|=
name|source
operator|.
name|evaluate
argument_list|(
literal|"$t:XML"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"$t:XML: "
operator|+
name|res
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<root><a id=\"a1\"/><b id=\"b1\" type=\"t\"/><c id=\"c1\">text</c><d id=\"d1\"><e>text</e></d></root>"
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|remove
argument_list|()
expr_stmt|;
name|source
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"IO exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Timeout exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResourceNotExistOrNotRunnable
parameter_list|()
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
literal|"Database wasn't initilised."
argument_list|,
name|database
argument_list|)
expr_stmt|;
name|Debugger
name|debugger
init|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
decl_stmt|;
name|Exception
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending init request"
argument_list|)
expr_stmt|;
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/exist/logo.jpg"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"This point should not be reached"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|exception
operator|.
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"class java.io.IOException"
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sending init request"
argument_list|)
expr_stmt|;
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/notExist/fibo.xql"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"This point should not be reached"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|exception
operator|.
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"class java.io.IOException"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStepInto
parameter_list|()
throws|throws
name|Exception
block|{
name|Debugger
name|debugger
init|=
name|DebuggerImpl
operator|.
name|getDebugger
argument_list|()
decl_stmt|;
name|String
name|url
init|=
literal|"http://127.0.0.1:8080/exist/xquery/json-test.xql"
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"init "
operator|+
name|i
argument_list|)
expr_stmt|;
name|DebuggingSource
name|debuggerSource
init|=
name|debugger
operator|.
name|init
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|debuggerSource
operator|.
name|stepInto
argument_list|()
expr_stmt|;
comment|//Thread.sleep(1000);
name|List
argument_list|<
name|Location
argument_list|>
name|stack
init|=
name|debuggerSource
operator|.
name|getStackFrames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLineBegin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getColumnBegin
argument_list|()
argument_list|)
expr_stmt|;
name|debuggerSource
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|//Thread.sleep(1000);
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"stoped"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
name|database
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|initDB
parameter_list|()
block|{
name|database
operator|=
operator|new
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
argument_list|(
literal|"jetty"
argument_list|)
expr_stmt|;
name|database
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"jetty"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|closeDB
parameter_list|()
block|{
name|DebuggerImpl
operator|.
name|shutdownDebugger
argument_list|()
expr_stmt|;
name|database
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
literal|"getResponse command = "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

