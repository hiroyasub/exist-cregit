begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dgbp
operator|.
name|packets
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dgbp
operator|.
name|DGBPPacket
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Init
extends|extends
name|DGBPPacket
block|{
name|String
name|init_message
init|=
literal|"<init "
operator|+
literal|"appid=\"7035\" "
operator|+
literal|"idekey=\"1\" "
operator|+
literal|"session=\"1\" "
operator|+
literal|"thread=\"1\" "
operator|+
literal|"parent=\"1\" "
operator|+
literal|"language=\"XQuery\" "
operator|+
literal|"protocol_version=\"1.0\" "
operator|+
literal|"fileuri=\"file:///home/dmitriy/projects/eXist-svn/trunk/eXist/webapp/admin/admin.xql\"></init>"
decl_stmt|;
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|init_message
operator|.
name|length
argument_list|()
return|;
block|}
specifier|public
name|byte
index|[]
name|toBytes
parameter_list|()
block|{
return|return
name|init_message
operator|.
name|getBytes
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|init_message
return|;
block|}
block|}
end_class

end_unit

