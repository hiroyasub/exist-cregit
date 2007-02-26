begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|numbering
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|io
operator|.
name|VariableByteInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|io
operator|.
name|VariableByteOutputStream
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
comment|/**  * Implementation of {@link NodeIdFactory} for DLN-based  * node ids.  */
end_comment

begin_class
specifier|public
class|class
name|DLNFactory
implements|implements
name|NodeIdFactory
block|{
specifier|public
name|NodeId
name|createInstance
parameter_list|()
block|{
return|return
operator|new
name|DLN
argument_list|()
return|;
block|}
specifier|public
name|NodeId
name|createInstance
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
operator|new
name|DLN
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|NodeId
name|createFromStream
parameter_list|(
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|short
name|bitCnt
init|=
name|is
operator|.
name|readShort
argument_list|()
decl_stmt|;
return|return
name|bitCnt
operator|==
literal|0
condition|?
name|DLN
operator|.
name|END_OF_DOCUMENT
else|:
operator|new
name|DLN
argument_list|(
name|bitCnt
argument_list|,
name|is
argument_list|)
return|;
block|}
specifier|public
name|NodeId
name|createFromStream
parameter_list|(
name|NodeId
name|previous
parameter_list|,
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
return|return
name|createFromStream
argument_list|(
name|is
argument_list|)
return|;
name|byte
name|prefix
init|=
name|is
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|short
name|bitCnt
init|=
name|is
operator|.
name|readShort
argument_list|()
decl_stmt|;
return|return
name|bitCnt
operator|==
literal|0
condition|?
name|DLN
operator|.
name|END_OF_DOCUMENT
else|:
operator|new
name|DLN
argument_list|(
name|prefix
argument_list|,
operator|(
name|DLN
operator|)
name|previous
argument_list|,
name|bitCnt
argument_list|,
name|is
argument_list|)
return|;
block|}
specifier|public
name|NodeId
name|createFromData
parameter_list|(
name|int
name|sizeHint
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|startOffset
parameter_list|)
block|{
return|return
operator|new
name|DLN
argument_list|(
name|sizeHint
argument_list|,
name|data
argument_list|,
name|startOffset
argument_list|)
return|;
block|}
specifier|public
name|NodeId
name|createFromString
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|DLN
argument_list|(
name|string
argument_list|)
return|;
block|}
specifier|public
name|NodeId
name|documentNodeId
parameter_list|()
block|{
return|return
name|DLN
operator|.
name|DOCUMENT_NODE
return|;
block|}
specifier|public
name|int
name|lengthInBytes
parameter_list|(
name|int
name|units
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|startOffset
parameter_list|)
block|{
return|return
name|DLN
operator|.
name|getLengthInBytes
argument_list|(
name|units
argument_list|,
name|data
argument_list|,
name|startOffset
argument_list|)
return|;
block|}
specifier|public
name|void
name|writeEndOfDocument
parameter_list|(
name|VariableByteOutputStream
name|os
parameter_list|)
block|{
name|os
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeShort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

