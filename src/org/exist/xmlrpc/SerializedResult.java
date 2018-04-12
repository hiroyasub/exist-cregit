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
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|TemporaryFileManager
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
name|XPathException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * Simple container for the results of a query. Used to cache  * query results that may be retrieved later by the client.  *  * @author jmfernandez  */
end_comment

begin_class
specifier|public
class|class
name|SerializedResult
extends|extends
name|AbstractCachedResult
block|{
specifier|protected
name|Path
name|result
decl_stmt|;
comment|// set upon failure
specifier|protected
name|XPathException
name|exception
init|=
literal|null
decl_stmt|;
specifier|public
name|SerializedResult
parameter_list|(
specifier|final
name|Path
name|result
parameter_list|)
block|{
name|this
argument_list|(
name|result
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SerializedResult
parameter_list|(
specifier|final
name|Path
name|result
parameter_list|,
specifier|final
name|long
name|queryTime
parameter_list|)
block|{
name|super
argument_list|(
name|queryTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
specifier|public
name|SerializedResult
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
comment|/**      * @return Returns the result.      */
annotation|@
name|Override
specifier|public
name|Path
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|TemporaryFileManager
operator|.
name|getInstance
argument_list|()
operator|.
name|returnTemporaryFile
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

