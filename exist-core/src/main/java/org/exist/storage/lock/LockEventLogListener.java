begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
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
name|Level
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

begin_comment
comment|/**  * A lock event listener which sends events to Log4j  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|LockEventLogListener
implements|implements
name|LockTable
operator|.
name|LockEventListener
block|{
specifier|private
specifier|final
name|Logger
name|log
decl_stmt|;
specifier|private
specifier|final
name|Level
name|level
decl_stmt|;
comment|/**      * @param log The Log4j log      * @param level The level at which to to log the lock events to Log4j      */
specifier|public
name|LockEventLogListener
parameter_list|(
specifier|final
name|Logger
name|log
parameter_list|,
specifier|final
name|Level
name|level
parameter_list|)
block|{
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|LockTable
operator|.
name|LockEventType
name|lockEventType
parameter_list|,
specifier|final
name|long
name|timestamp
parameter_list|,
specifier|final
name|long
name|groupId
parameter_list|,
specifier|final
name|LockTable
operator|.
name|Entry
name|entry
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isEnabled
argument_list|(
name|level
argument_list|)
condition|)
block|{
comment|// read count first to ensure memory visibility from volatile!
specifier|final
name|int
name|localCount
init|=
name|entry
operator|.
name|count
decl_stmt|;
name|log
operator|.
name|log
argument_list|(
name|level
argument_list|,
name|LockTable
operator|.
name|formatString
argument_list|(
name|lockEventType
argument_list|,
name|groupId
argument_list|,
name|entry
operator|.
name|id
argument_list|,
name|entry
operator|.
name|lockType
argument_list|,
name|entry
operator|.
name|lockMode
argument_list|,
name|entry
operator|.
name|owner
argument_list|,
name|localCount
argument_list|,
name|timestamp
argument_list|,
name|entry
operator|.
name|stackTraces
operator|==
literal|null
condition|?
literal|null
else|:
name|entry
operator|.
name|stackTraces
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

