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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|lazy
operator|.
name|LazyValE
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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
operator|.
name|Left
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
operator|.
name|Right
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

begin_comment
comment|/**  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|BrokerFactory
block|{
specifier|private
specifier|static
specifier|final
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
specifier|private
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|LazyValE
argument_list|<
name|BiFunction
argument_list|<
name|BrokerPool
argument_list|,
name|Configuration
argument_list|,
name|DBBroker
argument_list|>
argument_list|,
name|RuntimeException
argument_list|>
argument_list|>
name|CONSTRUCTORS
init|=
operator|new
name|ConcurrentHashMap
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
specifier|final
name|String
name|PROPERTY_DATABASE
init|=
literal|"database"
decl_stmt|;
specifier|public
specifier|static
name|void
name|plug
parameter_list|(
specifier|final
name|String
name|brokerId
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
name|clazz
parameter_list|)
block|{
name|CONSTRUCTORS
operator|.
name|computeIfAbsent
argument_list|(
name|formatBrokerId
argument_list|(
name|brokerId
argument_list|)
argument_list|,
name|key
lambda|->
operator|new
name|LazyValE
argument_list|<>
argument_list|(
parameter_list|()
lambda|->
name|getConstructor
argument_list|(
name|key
argument_list|,
name|clazz
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
static|static
block|{
name|plug
argument_list|(
name|formatBrokerId
argument_list|(
literal|"NATIVE"
argument_list|)
argument_list|,
name|NativeBroker
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a DBBroker instance.      *      * @param brokerPool the database's broker pool.      * @param configuration the database's configuration.      *      * @return DBBroker an instance of a sub-class of {@link DBBroker}.      *      * @throws IllegalArgumentException if the configuration does not define a broker ID.      * @throws IllegalStateException if there is no database backend defined for the broker ID.      * @throws RuntimeException if the database backend cannot be constructed.      */
specifier|public
specifier|static
name|DBBroker
name|getInstance
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|,
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|RuntimeException
throws|,
name|EXistException
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
name|String
name|brokerId
init|=
name|getBrokerId
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
specifier|final
name|LazyValE
argument_list|<
name|BiFunction
argument_list|<
name|BrokerPool
argument_list|,
name|Configuration
argument_list|,
name|DBBroker
argument_list|>
argument_list|,
name|RuntimeException
argument_list|>
name|constructor
init|=
name|CONSTRUCTORS
operator|.
name|get
argument_list|(
name|brokerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|constructor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No database backend found for: "
operator|+
name|brokerId
argument_list|)
throw|;
block|}
specifier|final
name|DBBroker
name|broker
init|=
name|constructor
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|brokerPool
argument_list|,
name|configuration
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
literal|"Constructed DBBroker in: "
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
comment|/**      * Creates a constructor function for a sub-class of DBBroker.      *      * @param brokerId the id of the DBBroker.      * @param clazz the sub-class of DBBroker.      *      * @return Either a constructor function, or a RuntimeException.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|static
name|Either
argument_list|<
name|RuntimeException
argument_list|,
name|BiFunction
argument_list|<
name|BrokerPool
argument_list|,
name|Configuration
argument_list|,
name|DBBroker
argument_list|>
argument_list|>
name|getConstructor
parameter_list|(
specifier|final
name|String
name|brokerId
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
name|clazz
parameter_list|)
block|{
try|try
block|{
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
name|BrokerPool
operator|.
name|class
argument_list|,
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
comment|// see https://stackoverflow.com/questions/50211216/how-to-invoke-constructor-using-lambdametafactory#50211536
return|return
name|Right
argument_list|(
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
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
comment|// NOTE: must set interrupted flag
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
return|return
name|Left
argument_list|(
operator|new
name|RuntimeException
argument_list|(
literal|"Can't get database backend: "
operator|+
name|brokerId
argument_list|,
name|e
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Gets the Broker ID from a Configuration.      *      * @param configuration the configuration.      * @return the broker ID.      *      * @throws IllegalArgumentException if the configuration does not define a broker ID.      */
specifier|private
specifier|static
name|String
name|getBrokerId
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
specifier|final
name|String
name|brokerId
init|=
operator|(
name|String
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|PROPERTY_DATABASE
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No database defined in: "
operator|+
name|configuration
operator|.
name|getConfigFilePath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|formatBrokerId
argument_list|(
name|brokerId
argument_list|)
return|;
block|}
comment|/**      * Ensures consistent formatting fo the Broker ID.      *      * Repair name {@see https://sourceforge.net/p/exist/bugs/810/}.      *      * @param brokerId the broker id to be formatted.      *      * @return consistently formatted broker id.      */
specifier|private
specifier|static
name|String
name|formatBrokerId
parameter_list|(
specifier|final
name|String
name|brokerId
parameter_list|)
block|{
return|return
name|brokerId
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
return|;
block|}
block|}
end_class

end_unit

