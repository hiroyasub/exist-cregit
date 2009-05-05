begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|underheavyload
package|;
end_package

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
name|List
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ClientsManager
implements|implements
name|Runnable
block|{
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|private
name|int
name|number
init|=
literal|1
decl_stmt|;
specifier|protected
name|String
name|url
decl_stmt|;
name|List
argument_list|<
name|Client
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Client
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|ClientsManager
parameter_list|(
name|int
name|number
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|running
operator|=
literal|true
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|running
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Client
name|client
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|running
condition|)
block|{
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>=
name|number
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|running
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|client
operator|=
operator|new
name|Client
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|getURL
parameter_list|()
block|{
return|return
name|url
return|;
block|}
block|}
end_class

end_unit

