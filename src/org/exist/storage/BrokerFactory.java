begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2018 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|function
operator|.
name|BiFunction
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
name|EXistException
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
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodType
operator|.
name|methodType
import|;
end_import

begin_class
specifier|public
class|class
name|BrokerFactory
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|BrokerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_DATABASE
init|=
literal|"database"
decl_stmt|;
specifier|private
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|constructorArgs
index|[]
init|=
block|{
name|BrokerPool
operator|.
name|class
block|,
name|Configuration
operator|.
name|class
block|}
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
argument_list|>
name|objClasses
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MethodHandles
operator|.
name|Lookup
name|LOOKUP
init|=
name|MethodHandles
operator|.
name|lookup
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|void
name|plug
parameter_list|(
name|String
name|id
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
name|clazz
parameter_list|)
block|{
name|objClasses
operator|.
name|put
argument_list|(
name|id
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
static|static
block|{
name|plug
argument_list|(
literal|"NATIVE"
argument_list|,
name|NativeBroker
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|DBBroker
name|getInstance
parameter_list|(
specifier|final
name|BrokerPool
name|database
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|EXistException
block|{
name|String
name|brokerID
init|=
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_DATABASE
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerID
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No database defined"
argument_list|)
throw|;
block|}
comment|// Repair name ; https://sourceforge.net/p/exist/bugs/810/
name|brokerID
operator|=
name|brokerID
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|objClasses
operator|.
name|containsKey
argument_list|(
name|brokerID
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No database backend found for "
operator|+
name|brokerID
argument_list|)
throw|;
block|}
try|try
block|{
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
name|clazz
init|=
name|objClasses
operator|.
name|get
argument_list|(
name|brokerID
argument_list|)
decl_stmt|;
specifier|final
name|MethodHandle
name|methodHandle
init|=
name|LOOKUP
operator|.
name|findConstructor
argument_list|(
name|clazz
argument_list|,
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|constructorArgs
argument_list|)
argument_list|)
decl_stmt|;
comment|// see https://stackoverflow.com/questions/50211216/how-to-invoke-constructor-using-lambdametafactory#50211536
specifier|final
name|BiFunction
argument_list|<
name|BrokerPool
argument_list|,
name|Configuration
argument_list|,
name|DBBroker
argument_list|>
name|constructor
init|=
operator|(
name|BiFunction
argument_list|<
name|BrokerPool
argument_list|,
name|Configuration
argument_list|,
name|DBBroker
argument_list|>
operator|)
name|LambdaMetafactory
operator|.
name|metafactory
argument_list|(
name|LOOKUP
argument_list|,
literal|"apply"
argument_list|,
name|methodType
argument_list|(
name|BiFunction
operator|.
name|class
argument_list|)
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
operator|.
name|erase
argument_list|()
argument_list|,
name|methodHandle
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|getTarget
argument_list|()
operator|.
name|invokeExact
argument_list|()
decl_stmt|;
comment|// TODO(AR) ideally we want to cache the constructor for re-use on subsequent calls to further reduce overhead
specifier|final
name|DBBroker
name|broker
init|=
name|constructor
operator|.
name|apply
argument_list|(
name|database
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
specifier|final
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Constructed DBBroker in : "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
return|return
name|broker
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't get database backend "
operator|+
name|brokerID
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

