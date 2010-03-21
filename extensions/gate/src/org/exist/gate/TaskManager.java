begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist's  Gate extension - REST client for automate document management  *  form any browser in any desktop application on any client platform  *  Copyright (C) 2010,  Evgeny V. Gazdovsky (gazdovsky@gmail.com)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|gate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|InvalidPropertiesFormatException
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
import|;
end_import

begin_class
specifier|public
class|class
name|TaskManager
extends|extends
name|TimerTask
block|{
specifier|private
name|List
argument_list|<
name|Task
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<
name|Task
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|haveNewTask
init|=
literal|false
decl_stmt|;
specifier|private
name|GateApplet
name|gate
decl_stmt|;
specifier|public
name|TaskManager
parameter_list|(
name|GateApplet
name|gate
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|gate
operator|=
name|gate
expr_stmt|;
block|}
specifier|public
name|void
name|addTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
name|tasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|haveNewTask
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|haveNewTask
condition|)
block|{
name|haveNewTask
operator|=
literal|false
expr_stmt|;
name|tasks
operator|.
name|get
argument_list|(
name|tasks
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|load
parameter_list|()
block|{
for|for
control|(
name|File
name|meta
range|:
name|gate
operator|.
name|getMeta
argument_list|()
operator|.
name|listFiles
argument_list|()
control|)
block|{
try|try
block|{
name|Properties
name|prop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|prop
operator|.
name|loadFromXML
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|File
name|tmp
init|=
operator|new
name|File
argument_list|(
name|gate
operator|.
name|getCache
argument_list|()
argument_list|,
name|prop
operator|.
name|getProperty
argument_list|(
literal|"file"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmp
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
name|downloadFrom
init|=
name|prop
operator|.
name|getProperty
argument_list|(
literal|"download-from"
argument_list|)
decl_stmt|;
name|String
name|uploadTo
init|=
name|prop
operator|.
name|getProperty
argument_list|(
literal|"upload-to"
argument_list|)
decl_stmt|;
name|long
name|modified
init|=
operator|new
name|Long
argument_list|(
name|prop
operator|.
name|getProperty
argument_list|(
literal|"modified"
argument_list|)
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|Task
name|task
init|=
operator|new
name|Task
argument_list|(
name|downloadFrom
argument_list|,
name|uploadTo
argument_list|,
name|tmp
argument_list|,
name|gate
argument_list|)
decl_stmt|;
name|tasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|(
name|task
argument_list|,
name|gate
argument_list|,
name|modified
argument_list|)
decl_stmt|;
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|timer
operator|.
name|schedule
argument_list|(
name|listener
argument_list|,
name|GateApplet
operator|.
name|PERIOD
argument_list|,
name|GateApplet
operator|.
name|PERIOD
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InvalidPropertiesFormatException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
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
block|}
end_class

end_unit

