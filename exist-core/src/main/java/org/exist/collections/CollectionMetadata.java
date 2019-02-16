begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|ResourceMetadata
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  * @author Adam Retter  */
end_comment

begin_class
specifier|public
class|class
name|CollectionMetadata
implements|implements
name|ResourceMetadata
block|{
specifier|private
specifier|final
name|Collection
name|collection
decl_stmt|;
specifier|public
name|CollectionMetadata
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCreated
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getCreationTime
argument_list|()
return|;
block|}
block|}
end_class

end_unit

