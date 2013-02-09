begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|sanity
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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

begin_comment
comment|/**  * Utility class for sanity checks. Provides static methods ASSERT, THROW_ASSERT  * which can be used in the code to react to unexpected conditions. {@link #ASSERT(boolean)}  * logs a stack trace to the log4j log output. {@link #THROW_ASSERT(boolean)}  * throws an additional runtime exception.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|SanityCheck
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
name|SanityCheck
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|void
name|ASSERT
parameter_list|(
name|boolean
name|mustBeTrue
parameter_list|)
block|{
if|if
condition|(
operator|!
name|mustBeTrue
condition|)
block|{
specifier|final
name|AssertFailure
name|failure
init|=
operator|new
name|AssertFailure
argument_list|(
literal|"ASSERT FAILED"
argument_list|)
decl_stmt|;
name|showTrace
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|void
name|ASSERT
parameter_list|(
name|boolean
name|mustBeTrue
parameter_list|,
name|String
name|failureMsg
parameter_list|)
block|{
if|if
condition|(
operator|!
name|mustBeTrue
condition|)
block|{
specifier|final
name|AssertFailure
name|failure
init|=
operator|new
name|AssertFailure
argument_list|(
literal|"ASSERT FAILED: "
operator|+
name|failureMsg
argument_list|)
decl_stmt|;
name|showTrace
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|void
name|THROW_ASSERT
parameter_list|(
name|boolean
name|mustBeTrue
parameter_list|)
block|{
if|if
condition|(
operator|!
name|mustBeTrue
condition|)
block|{
specifier|final
name|AssertFailure
name|failure
init|=
operator|new
name|AssertFailure
argument_list|(
literal|"ASSERT FAILED"
argument_list|)
decl_stmt|;
name|showTrace
argument_list|(
name|failure
argument_list|)
expr_stmt|;
throw|throw
name|failure
throw|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|void
name|THROW_ASSERT
parameter_list|(
name|boolean
name|mustBeTrue
parameter_list|,
name|String
name|failureMsg
parameter_list|)
block|{
if|if
condition|(
operator|!
name|mustBeTrue
condition|)
block|{
specifier|final
name|AssertFailure
name|failure
init|=
operator|new
name|AssertFailure
argument_list|(
literal|"ASSERT FAILED: "
operator|+
name|failureMsg
argument_list|)
decl_stmt|;
name|showTrace
argument_list|(
name|failure
argument_list|)
expr_stmt|;
throw|throw
name|failure
throw|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|void
name|TRACE
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
specifier|final
name|AssertFailure
name|failure
init|=
operator|new
name|AssertFailure
argument_list|(
literal|"TRACE: "
operator|+
name|msg
argument_list|)
decl_stmt|;
name|showTrace
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|static
name|void
name|PRINT_STACK
parameter_list|(
name|int
name|level
parameter_list|)
block|{
specifier|final
name|StackTraceElement
name|elements
index|[]
init|=
operator|new
name|Exception
argument_list|(
literal|"Trace"
argument_list|)
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
if|if
condition|(
name|level
operator|>
name|elements
operator|.
name|length
condition|)
block|{
name|level
operator|=
name|elements
operator|.
name|length
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|level
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|elements
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|showTrace
parameter_list|(
name|AssertFailure
name|failure
parameter_list|)
block|{
specifier|final
name|StringWriter
name|sout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|sout
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Stacktrace:"
argument_list|)
expr_stmt|;
name|failure
operator|.
name|printStackTrace
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|sout
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

