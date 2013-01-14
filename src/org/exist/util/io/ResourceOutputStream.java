begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ResourceOutputStream
extends|extends
name|FileOutputStream
block|{
specifier|public
name|ResourceOutputStream
parameter_list|(
name|Resource
name|file
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|super
argument_list|(
name|file
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ResourceOutputStream
parameter_list|(
name|Resource
name|file
parameter_list|,
name|boolean
name|append
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|super
argument_list|(
name|file
operator|.
name|getFile
argument_list|()
argument_list|,
name|append
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//XXX: xml upload back to db
comment|//XXX: locking?
block|}
block|}
end_class

end_unit

