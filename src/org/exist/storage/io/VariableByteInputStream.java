begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
comment|/**  * Implements VariableByteInput on top of an InputStream.  *    * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|VariableByteInputStream
extends|extends
name|AbstractVariableByteInput
block|{
specifier|private
name|InputStream
name|is
decl_stmt|;
comment|/**      *       */
specifier|public
name|VariableByteInputStream
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|is
operator|=
name|is
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see java.io.InputStream#read()      */
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|is
operator|.
name|read
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see java.io.InputStream#available()      */
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|is
operator|.
name|available
argument_list|()
return|;
block|}
block|}
end_class

end_unit

