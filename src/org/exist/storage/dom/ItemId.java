begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|dom
package|;
end_package

begin_comment
comment|/**  * Provides static methods to set or test the status bits of a record identifier  * in the dom.dbx persistent DOM store.  *   * @see org.exist.storage.dom.DOMFile  * @author wolf  */
end_comment

begin_class
class|class
name|ItemId
block|{
specifier|public
specifier|static
specifier|final
name|short
name|RELOCATED_MASK
init|=
operator|(
name|short
operator|)
literal|0x8000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|short
name|LINK_MASK
init|=
operator|(
name|short
operator|)
literal|0x4000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|short
name|ID_MASK
init|=
operator|(
name|short
operator|)
literal|0x3FFF
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|short
name|LINK_OR_RELOCATED_MASK
init|=
operator|(
name|short
operator|)
literal|0xC000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|LINK_FLAG
init|=
operator|(
name|byte
operator|)
literal|0x1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|RELOCATED_FLAG
init|=
operator|(
name|byte
operator|)
literal|0x2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|short
name|MAX_ID
init|=
operator|(
name|short
operator|)
literal|0x3FFE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|short
name|DEFRAG_LIMIT
init|=
operator|(
name|short
operator|)
literal|0x2FFE
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|getFlags
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
operator|(
operator|(
name|id
operator|&
name|LINK_OR_RELOCATED_MASK
operator|)
operator|>>>
literal|14
operator|)
return|;
block|}
specifier|public
specifier|final
specifier|static
name|short
name|getId
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
operator|(
name|id
operator|&
name|ID_MASK
operator|)
return|;
block|}
specifier|public
specifier|final
specifier|static
name|boolean
name|matches
parameter_list|(
name|short
name|id
parameter_list|,
name|short
name|targetId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|short
operator|)
operator|(
name|id
operator|&
name|ID_MASK
operator|)
operator|)
operator|==
name|targetId
return|;
block|}
specifier|public
specifier|final
specifier|static
name|short
name|setIsRelocated
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
operator|(
name|id
operator||
name|RELOCATED_MASK
operator|)
return|;
block|}
specifier|public
specifier|final
specifier|static
name|boolean
name|isLink
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
operator|(
name|id
operator|&
name|LINK_MASK
operator|)
operator|==
name|LINK_MASK
return|;
block|}
specifier|public
specifier|final
specifier|static
name|short
name|setIsLink
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
operator|(
name|id
operator||
name|LINK_MASK
operator|)
return|;
block|}
specifier|public
specifier|final
specifier|static
name|boolean
name|isRelocated
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
operator|(
name|id
operator|&
name|RELOCATED_MASK
operator|)
operator|==
name|RELOCATED_MASK
return|;
block|}
specifier|public
specifier|final
specifier|static
name|boolean
name|isLinkOrRelocated
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
operator|(
name|id
operator|&
name|LINK_OR_RELOCATED_MASK
operator|)
operator|!=
literal|0
return|;
block|}
specifier|public
specifier|final
specifier|static
name|boolean
name|isOrdinaryRecord
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
operator|(
name|id
operator|&
name|LINK_OR_RELOCATED_MASK
operator|)
operator|==
literal|0
return|;
block|}
block|}
end_class

end_unit

