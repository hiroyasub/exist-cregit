begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  \$Id\$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|performance
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|performance
operator|.
name|actions
operator|.
name|Action
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_class
specifier|public
class|class
name|ActionThread
extends|extends
name|ActionSequence
implements|implements
name|Runnable
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|public
name|void
name|configure
parameter_list|(
name|Runner
name|runner
parameter_list|,
name|Action
name|parent
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
name|name
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|String
name|con
init|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"connection"
argument_list|)
decl_stmt|;
if|if
condition|(
name|con
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"thread needs a connection"
argument_list|)
throw|;
name|connection
operator|=
name|runner
operator|.
name|getConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"unknown connection "
operator|+
name|con
operator|+
literal|" referenced by thread "
operator|+
name|name
argument_list|)
throw|;
name|super
operator|.
name|configure
argument_list|(
name|runner
argument_list|,
literal|null
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|runner
operator|.
name|getResults
argument_list|()
operator|.
name|threadStarted
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|execute
argument_list|(
name|this
operator|.
name|connection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|long
name|elapsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

