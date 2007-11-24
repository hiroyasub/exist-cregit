begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLArgsParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLOptionDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLUtil
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
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
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|JMXClient
block|{
specifier|private
name|MBeanServerConnection
name|connection
decl_stmt|;
specifier|private
name|String
name|instance
decl_stmt|;
specifier|public
name|JMXClient
parameter_list|(
name|String
name|instanceName
parameter_list|)
block|{
name|this
operator|.
name|instance
operator|=
name|instanceName
expr_stmt|;
block|}
specifier|public
name|void
name|connect
parameter_list|(
name|String
name|address
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|JMXServiceURL
name|url
init|=
operator|new
name|JMXServiceURL
argument_list|(
literal|"service:jmx:rmi:///jndi/rmi://"
operator|+
name|address
operator|+
literal|":"
operator|+
name|port
operator|+
literal|"/jmxrmi"
argument_list|)
decl_stmt|;
name|Map
name|env
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|String
index|[]
name|creds
init|=
block|{
literal|"guest"
block|,
literal|"guest"
block|}
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|JMXConnector
operator|.
name|CREDENTIALS
argument_list|,
name|creds
argument_list|)
expr_stmt|;
name|JMXConnector
name|jmxc
init|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
name|url
argument_list|,
name|env
argument_list|)
decl_stmt|;
name|connection
operator|=
name|jmxc
operator|.
name|getMBeanServerConnection
argument_list|()
expr_stmt|;
name|echo
argument_list|(
literal|"Connected to MBean server."
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|memoryStats
parameter_list|()
block|{
try|try
block|{
name|ObjectName
name|name
init|=
operator|new
name|ObjectName
argument_list|(
literal|"java.lang:type=Memory"
argument_list|)
decl_stmt|;
name|CompositeData
name|composite
init|=
operator|(
name|CompositeData
operator|)
name|connection
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|,
literal|"HeapMemoryUsage"
argument_list|)
decl_stmt|;
if|if
condition|(
name|composite
operator|!=
literal|null
condition|)
block|{
name|echo
argument_list|(
literal|"\nMEMORY:"
argument_list|)
expr_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Current heap: %,12d k        Committed memory:  %,12d k"
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|composite
operator|.
name|get
argument_list|(
literal|"used"
argument_list|)
operator|)
operator|/
literal|1024
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|composite
operator|.
name|get
argument_list|(
literal|"committed"
argument_list|)
operator|)
operator|/
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Max memory:   %,12d k"
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|composite
operator|.
name|get
argument_list|(
literal|"max"
argument_list|)
operator|)
operator|/
literal|1024
argument_list|)
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
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|instanceStats
parameter_list|()
block|{
try|try
block|{
name|echo
argument_list|(
literal|"\nINSTANCE:"
argument_list|)
expr_stmt|;
name|ObjectName
name|name
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management."
operator|+
name|instance
operator|+
literal|":type=Database"
argument_list|)
decl_stmt|;
name|Long
name|memReserved
init|=
operator|(
name|Long
operator|)
name|connection
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|,
literal|"ReservedMem"
argument_list|)
decl_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%25s: %10d k"
argument_list|,
literal|"Reserved memory"
argument_list|,
name|memReserved
operator|.
name|longValue
argument_list|()
operator|/
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|Long
name|memCache
init|=
operator|(
name|Long
operator|)
name|connection
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|,
literal|"CacheMem"
argument_list|)
decl_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%25s: %10d k"
argument_list|,
literal|"Cache memory"
argument_list|,
name|memCache
operator|.
name|longValue
argument_list|()
operator|/
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|Long
name|memCollCache
init|=
operator|(
name|Long
operator|)
name|connection
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|,
literal|"CollectionCacheMem"
argument_list|)
decl_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%25s: %10d k"
argument_list|,
literal|"Collection cache memory"
argument_list|,
name|memCollCache
operator|.
name|longValue
argument_list|()
operator|/
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|cols
index|[]
init|=
block|{
literal|"MaxBrokers"
block|,
literal|"AvailableBrokers"
block|,
literal|"ActiveBrokers"
block|}
decl_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\n%17s %17s %17s"
argument_list|,
name|cols
index|[
literal|0
index|]
argument_list|,
name|cols
index|[
literal|1
index|]
argument_list|,
name|cols
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|AttributeList
name|attrs
init|=
name|connection
operator|.
name|getAttributes
argument_list|(
name|name
argument_list|,
name|cols
argument_list|)
decl_stmt|;
name|Object
name|values
index|[]
init|=
name|getValues
argument_list|(
name|attrs
argument_list|)
decl_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%17d %17d %17d"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|,
name|values
index|[
literal|1
index|]
argument_list|,
name|values
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|TabularData
name|table
init|=
operator|(
name|TabularData
operator|)
name|connection
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|,
literal|"ActiveBrokersMap"
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|echo
argument_list|(
literal|"\nCurrently active threads:"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|table
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|CompositeData
name|data
init|=
operator|(
name|CompositeData
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\t%20s: %3d"
argument_list|,
name|data
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|,
name|data
operator|.
name|get
argument_list|(
literal|"referenceCount"
argument_list|)
argument_list|)
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
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|cacheStats
parameter_list|()
block|{
try|try
block|{
name|ObjectName
name|name
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management."
operator|+
name|instance
operator|+
literal|":type=CacheManager"
argument_list|)
decl_stmt|;
name|String
name|cols
index|[]
init|=
block|{
literal|"MaxTotal"
block|,
literal|"CurrentSize"
block|}
decl_stmt|;
name|AttributeList
name|attrs
init|=
name|connection
operator|.
name|getAttributes
argument_list|(
name|name
argument_list|,
name|cols
argument_list|)
decl_stmt|;
name|Object
name|values
index|[]
init|=
name|getValues
argument_list|(
name|attrs
argument_list|)
decl_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\nCACHE [%8d pages max. / %8d pages allocated]"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|,
name|values
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Set
name|beans
init|=
name|connection
operator|.
name|queryNames
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management."
operator|+
name|instance
operator|+
literal|":type=CacheManager.Cache,*"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|cols
operator|=
operator|new
name|String
index|[]
block|{
literal|"Type"
block|,
literal|"FileName"
block|,
literal|"Size"
block|,
literal|"Used"
block|,
literal|"Hits"
block|,
literal|"Fails"
block|}
expr_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%10s %20s %10s %10s %10s %10s"
argument_list|,
name|cols
index|[
literal|0
index|]
argument_list|,
name|cols
index|[
literal|1
index|]
argument_list|,
name|cols
index|[
literal|2
index|]
argument_list|,
name|cols
index|[
literal|3
index|]
argument_list|,
name|cols
index|[
literal|4
index|]
argument_list|,
name|cols
index|[
literal|5
index|]
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|beans
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|name
operator|=
operator|(
name|ObjectName
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|attrs
operator|=
name|connection
operator|.
name|getAttributes
argument_list|(
name|name
argument_list|,
name|cols
argument_list|)
expr_stmt|;
name|values
operator|=
name|getValues
argument_list|(
name|attrs
argument_list|)
expr_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%10s %20s %,10d %,10d %,10d %,10d"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|,
name|values
index|[
literal|1
index|]
argument_list|,
name|values
index|[
literal|2
index|]
argument_list|,
name|values
index|[
literal|3
index|]
argument_list|,
name|values
index|[
literal|4
index|]
argument_list|,
name|values
index|[
literal|5
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectionException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|lockTable
parameter_list|()
block|{
name|echo
argument_list|(
literal|"\nList of threads currently waiting for a lock:"
argument_list|)
expr_stmt|;
name|echo
argument_list|(
literal|"-----------------------------------------------"
argument_list|)
expr_stmt|;
try|try
block|{
name|TabularData
name|table
init|=
operator|(
name|TabularData
operator|)
name|connection
operator|.
name|getAttribute
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management:type=LockManager"
argument_list|)
argument_list|,
literal|"WaitingThreads"
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|table
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|CompositeData
name|data
init|=
operator|(
name|CompositeData
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|echo
argument_list|(
literal|"Thread "
operator|+
name|data
operator|.
name|get
argument_list|(
literal|"waitingThread"
argument_list|)
argument_list|)
expr_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%20s: %s"
argument_list|,
literal|"Lock type"
argument_list|,
name|data
operator|.
name|get
argument_list|(
literal|"lockType"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%20s: %s"
argument_list|,
literal|"Lock mode"
argument_list|,
name|data
operator|.
name|get
argument_list|(
literal|"lockMode"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%20s: %s"
argument_list|,
literal|"Lock id"
argument_list|,
name|data
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%20s: %s"
argument_list|,
literal|"Held by"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
operator|(
name|String
index|[]
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|readers
init|=
operator|(
name|String
index|[]
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"waitingForRead"
argument_list|)
decl_stmt|;
if|if
condition|(
name|readers
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%20s: %s"
argument_list|,
literal|"Wait for read"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|readers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|writers
init|=
operator|(
name|String
index|[]
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"waitingForWrite"
argument_list|)
decl_stmt|;
if|if
condition|(
name|writers
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|echo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%20s: %s"
argument_list|,
literal|"Wait for write"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|writers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|MBeanException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AttributeNotFoundException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectionException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Object
index|[]
name|getValues
parameter_list|(
name|AttributeList
name|attribs
parameter_list|)
block|{
name|Object
index|[]
name|v
init|=
operator|new
name|Object
index|[
name|attribs
operator|.
name|size
argument_list|()
index|]
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
name|attribs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|v
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|Attribute
operator|)
name|attribs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
specifier|private
name|void
name|echo
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|error
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|int
name|HELP_OPT
init|=
literal|'h'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|CACHE_OPT
init|=
literal|'c'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|DB_OPT
init|=
literal|'d'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|WAIT_OPT
init|=
literal|'w'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|LOCK_OPT
init|=
literal|'l'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MEMORY_OPT
init|=
literal|'m'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|PORT_OPT
init|=
literal|'p'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|INSTANCE_OPT
init|=
literal|'i'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|ADDRESS_OPT
init|=
literal|'a'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|CLOptionDescriptor
name|OPTIONS
index|[]
init|=
operator|new
name|CLOptionDescriptor
index|[]
block|{
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"help"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|HELP_OPT
argument_list|,
literal|"print help on command line options and exit."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"cache"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|CACHE_OPT
argument_list|,
literal|"displays server statistics on cache and memory usage."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"db"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|DB_OPT
argument_list|,
literal|"display general info about the db instance."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"wait"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|WAIT_OPT
argument_list|,
literal|"while displaying server statistics: keep retrieving statistics, but wait the "
operator|+
literal|"specified number of seconds between calls."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"locks"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|LOCK_OPT
argument_list|,
literal|"lock manager: display locking information on all threads currently waiting for a lock on a resource "
operator|+
literal|"or collection. Useful to debug deadlocks. During normal operation, the list will usually be empty (means: no "
operator|+
literal|"blocked threads)."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"memory"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|MEMORY_OPT
argument_list|,
literal|"display info on free and total memory. Can be combined with other parameters."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"port"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|PORT_OPT
argument_list|,
literal|"RMI port of the server"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"address"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|ADDRESS_OPT
argument_list|,
literal|"RMI address of the server"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"instance"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|INSTANCE_OPT
argument_list|,
literal|"the ID of the database instance to connect to"
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MODE_STATS
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MODE_LOCKS
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|CLArgsParser
name|optParser
init|=
operator|new
name|CLArgsParser
argument_list|(
name|args
argument_list|,
name|OPTIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|optParser
operator|.
name|getErrorString
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|optParser
operator|.
name|getErrorString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|dbInstance
init|=
literal|"exist"
decl_stmt|;
name|long
name|waitTime
init|=
literal|0
decl_stmt|;
name|List
name|opt
init|=
name|optParser
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|opt
operator|.
name|size
argument_list|()
decl_stmt|;
name|CLOption
name|option
decl_stmt|;
name|int
name|mode
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|port
init|=
literal|1099
decl_stmt|;
name|String
name|address
init|=
literal|"localhost"
decl_stmt|;
name|boolean
name|displayMem
init|=
literal|false
decl_stmt|;
name|boolean
name|displayInstance
init|=
literal|false
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|option
operator|=
operator|(
name|CLOption
operator|)
name|opt
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|option
operator|.
name|getId
argument_list|()
condition|)
block|{
case|case
name|HELP_OPT
case|:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|CLUtil
operator|.
name|describeOptions
argument_list|(
name|OPTIONS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
case|case
name|WAIT_OPT
case|:
try|try
block|{
name|waitTime
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|option
operator|.
name|getArgument
argument_list|()
argument_list|)
operator|*
literal|1000
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"option -w|--wait requires a numeric argument"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
name|CACHE_OPT
case|:
name|mode
operator|=
name|MODE_STATS
expr_stmt|;
break|break;
case|case
name|LOCK_OPT
case|:
name|mode
operator|=
name|MODE_LOCKS
expr_stmt|;
break|break;
case|case
name|PORT_OPT
case|:
try|try
block|{
name|port
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|option
operator|.
name|getArgument
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"option -p|--port requires a numeric argument"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
name|ADDRESS_OPT
case|:
try|try
block|{
name|address
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"option -a|--address requires a numeric argument"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
name|MEMORY_OPT
case|:
name|displayMem
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|DB_OPT
case|:
name|displayInstance
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|INSTANCE_OPT
case|:
name|dbInstance
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
try|try
block|{
name|JMXClient
name|stats
init|=
operator|new
name|JMXClient
argument_list|(
name|dbInstance
argument_list|)
decl_stmt|;
name|stats
operator|.
name|connect
argument_list|(
name|address
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|stats
operator|.
name|memoryStats
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|MODE_STATS
case|:
name|stats
operator|.
name|cacheStats
argument_list|()
expr_stmt|;
break|break;
case|case
name|MODE_LOCKS
case|:
name|stats
operator|.
name|lockTable
argument_list|()
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|displayInstance
condition|)
name|stats
operator|.
name|instanceStats
argument_list|()
expr_stmt|;
if|if
condition|(
name|displayMem
condition|)
name|stats
operator|.
name|memoryStats
argument_list|()
expr_stmt|;
if|if
condition|(
name|waitTime
operator|>
literal|0
condition|)
block|{
synchronized|synchronized
init|(
name|stats
init|)
block|{
try|try
block|{
name|stats
operator|.
name|wait
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"INTERRUPTED: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

