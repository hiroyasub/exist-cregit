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
name|deadlocks
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
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
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
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|GetReleaseBrokerDeadlocks
block|{
specifier|private
specifier|static
name|Random
name|rd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|exterServiceMode
parameter_list|()
block|{
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
name|FunctionFactory
operator|.
name|PROPERTY_DISABLE_DEPRECATED_FUNCTIONS
argument_list|,
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|Database
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|EnterServiceMode
argument_list|()
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|thread
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|EnterServiceMode
implements|implements
name|Runnable
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|BrokerPool
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Subject
name|subject
init|=
name|db
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
decl_stmt|;
try|try
block|{
name|db
operator|.
name|enterServiceMode
argument_list|(
name|subject
argument_list|)
expr_stmt|;
comment|//do something
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|exitServiceMode
argument_list|(
name|subject
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|testingGetReleaseCycle
parameter_list|()
block|{
name|boolean
name|debug
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
name|FunctionFactory
operator|.
name|PROPERTY_DISABLE_DEPRECATED_FUNCTIONS
argument_list|,
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|Database
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Thread
name|thread
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|thread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|GetRelease
argument_list|()
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|rd
operator|.
name|nextInt
argument_list|(
literal|250
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|debug
operator|&&
name|db
operator|.
name|countActiveBrokers
argument_list|()
operator|==
literal|20
condition|)
block|{
name|Map
argument_list|<
name|Thread
argument_list|,
name|StackTraceElement
index|[]
argument_list|>
name|stackTraces
init|=
name|Thread
operator|.
name|getAllStackTraces
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"************************************************\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"************************************************\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Thread
argument_list|,
name|StackTraceElement
index|[]
argument_list|>
name|entry
range|:
name|stackTraces
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|StackTraceElement
index|[]
name|stacks
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"THREAD: "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|stacks
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|stacks
index|[
name|n
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|stackTraces
operator|.
name|isEmpty
argument_list|()
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"No threads."
argument_list|)
expr_stmt|;
comment|//		            System.out.println(sb.toString());
block|}
block|}
while|while
condition|(
name|db
operator|.
name|countActiveBrokers
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Throwable
name|ex
init|=
literal|null
decl_stmt|;
class|class
name|GetRelease
implements|implements
name|Runnable
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|BrokerPool
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Subject
name|subject
init|=
name|db
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
name|subject
argument_list|)
expr_stmt|;
comment|//do something
name|Thread
operator|.
name|sleep
argument_list|(
name|rd
operator|.
name|nextInt
argument_list|(
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|broker
argument_list|,
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|//do something
name|Thread
operator|.
name|sleep
argument_list|(
name|rd
operator|.
name|nextInt
argument_list|(
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

