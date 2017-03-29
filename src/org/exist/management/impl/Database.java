begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: Database.java 6177 2007-07-08 14:42:37Z wolfgang_m $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|impl
package|;
end_package

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
name|Optional
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
name|*
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

begin_class
specifier|public
class|class
name|Database
implements|implements
name|DatabaseMXBean
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|itemNames
init|=
block|{
literal|"owner"
block|,
literal|"referenceCount"
block|,
literal|"stack"
block|,
literal|"stackAcquired"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|itemDescriptions
init|=
block|{
literal|"Name of the thread owning the broker"
block|,
literal|"Number of references held by the thread"
block|,
literal|"Stack trace"
block|,
literal|"Broker acquired"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|indexNames
init|=
block|{
literal|"owner"
block|}
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|Database
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInstanceId
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxBrokers
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getMax
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getAvailableBrokers
parameter_list|()
block|{
return|return
name|pool
operator|.
name|available
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getActiveBrokers
parameter_list|()
block|{
return|return
name|pool
operator|.
name|countActiveBrokers
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getTotalBrokers
parameter_list|()
block|{
return|return
name|pool
operator|.
name|total
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getActiveBrokersMap
parameter_list|()
block|{
specifier|final
name|OpenType
argument_list|<
name|?
argument_list|>
index|[]
name|itemTypes
init|=
block|{
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|INTEGER
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|}
decl_stmt|;
try|try
block|{
specifier|final
name|CompositeType
name|infoType
init|=
operator|new
name|CompositeType
argument_list|(
literal|"brokerInfo"
argument_list|,
literal|"Provides information on a broker instance."
argument_list|,
name|itemNames
argument_list|,
name|itemDescriptions
argument_list|,
name|itemTypes
argument_list|)
decl_stmt|;
specifier|final
name|TabularType
name|tabularType
init|=
operator|new
name|TabularType
argument_list|(
literal|"activeBrokers"
argument_list|,
literal|"Lists all threads currently using a broker instance"
argument_list|,
name|infoType
argument_list|,
name|indexNames
argument_list|)
decl_stmt|;
specifier|final
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Thread
argument_list|,
name|DBBroker
argument_list|>
name|entry
range|:
name|pool
operator|.
name|getActiveBrokers
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Thread
name|thread
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|DBBroker
name|broker
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|trace
init|=
name|printStackTrace
argument_list|(
name|thread
argument_list|)
decl_stmt|;
specifier|final
name|String
name|watchdogTrace
init|=
name|pool
operator|.
name|getWatchdog
argument_list|()
operator|.
name|map
argument_list|(
name|wd
lambda|->
name|wd
operator|.
name|get
argument_list|(
name|broker
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|Object
index|[]
name|itemValues
init|=
block|{
name|thread
operator|.
name|getName
argument_list|()
block|,
name|broker
operator|.
name|getReferenceCount
argument_list|()
block|,
name|trace
block|,
name|watchdogTrace
block|}
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|infoType
argument_list|,
name|itemNames
argument_list|,
name|itemValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|OpenDataException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getReservedMem
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getReservedMem
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCacheMem
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getCacheManager
argument_list|()
operator|.
name|getTotalMem
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCollectionCacheMem
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getCollectionCacheMgr
argument_list|()
operator|.
name|getMaxTotal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getUptime
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|pool
operator|.
name|getStartupTime
argument_list|()
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getExistHome
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
operator|.
name|map
argument_list|(
name|p
lambda|->
name|p
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|String
name|printStackTrace
parameter_list|(
name|Thread
name|thread
parameter_list|)
block|{
specifier|final
name|StackTraceElement
index|[]
name|stackElements
init|=
name|thread
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|int
name|showItems
init|=
name|stackElements
operator|.
name|length
operator|>
literal|20
condition|?
literal|20
else|:
name|stackElements
operator|.
name|length
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
name|showItems
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|stackElements
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

