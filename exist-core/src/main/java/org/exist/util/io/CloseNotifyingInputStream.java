begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|RunnableE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterInputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * An Input Stream filter which executes a callback  * after the stream has been closed.  *  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|CloseNotifyingInputStream
extends|extends
name|FilterInputStream
block|{
specifier|private
specifier|final
name|RunnableE
argument_list|<
name|IOException
argument_list|>
name|closedCallback
decl_stmt|;
comment|/**      * @param is The input stream.      * @param closedCallback the callback to execute when this stream is closed.      */
specifier|public
name|CloseNotifyingInputStream
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|,
specifier|final
name|RunnableE
argument_list|<
name|IOException
argument_list|>
name|closedCallback
parameter_list|)
block|{
name|super
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|this
operator|.
name|closedCallback
operator|=
name|closedCallback
expr_stmt|;
block|}
annotation|@
name|Override
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
name|closedCallback
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

