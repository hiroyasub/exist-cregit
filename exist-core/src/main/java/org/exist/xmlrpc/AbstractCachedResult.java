begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/**  * Simple abstract container for serialized resources or results of a query.  * Used to cache them that may be retrieved by chunks later by the client.  *  * @author wolf  * @author jmfernandez  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCachedResult
implements|implements
name|Closeable
block|{
specifier|protected
name|long
name|queryTime
init|=
literal|0
decl_stmt|;
specifier|protected
name|long
name|creationTimestamp
init|=
literal|0
decl_stmt|;
specifier|protected
name|long
name|timestamp
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|public
name|AbstractCachedResult
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractCachedResult
parameter_list|(
specifier|final
name|long
name|queryTime
parameter_list|)
block|{
name|this
operator|.
name|queryTime
operator|=
name|queryTime
expr_stmt|;
name|touch
argument_list|()
expr_stmt|;
name|this
operator|.
name|creationTimestamp
operator|=
name|this
operator|.
name|timestamp
expr_stmt|;
block|}
comment|/**      * @return Returns the queryTime.      */
specifier|public
name|long
name|getQueryTime
parameter_list|()
block|{
return|return
name|queryTime
return|;
block|}
comment|/**      * @return Returns the timestamp.      */
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
comment|/**      * This method can be used to explicitly update the      * last time the cached result has been used      */
specifier|public
name|void
name|touch
parameter_list|()
block|{
name|timestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return Returns the timestamp.      */
specifier|public
name|long
name|getCreationTimestamp
parameter_list|()
block|{
return|return
name|creationTimestamp
return|;
block|}
comment|/**      * This abstract method returns the cached result      * or null      *      * @return The object which is being cached      */
specifier|public
specifier|abstract
name|Object
name|getResult
parameter_list|()
function_decl|;
comment|/**      * Returns true if the Cached Result      * has been closed.      *      * @return true if the cached result has been closed.      */
specifier|public
specifier|final
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
comment|/**      * Implement this in your sub-class if you need      * to do cleanup.      *      * The method will only be called once, no matter      * how many times the user calls {@link #close()}.      */
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isClosed
argument_list|()
condition|)
block|{
try|try
block|{
name|doClose
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
comment|/**      * This abstract method must be used      * to free internal variables.      *      * @deprecated Call {@link #close()} instead.      */
annotation|@
name|Deprecated
specifier|public
specifier|final
name|void
name|free
parameter_list|()
block|{
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// Calling free to reclaim pinned resources
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

