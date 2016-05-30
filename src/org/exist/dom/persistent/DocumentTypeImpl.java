begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist xml document repository and xpath implementation  * Copyright (C) 2001-2014,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|DocumentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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

begin_class
specifier|public
class|class
name|DocumentTypeImpl
extends|extends
name|StoredNode
implements|implements
name|DocumentType
block|{
specifier|protected
name|String
name|publicId
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|systemId
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|public
name|DocumentTypeImpl
parameter_list|()
block|{
name|super
argument_list|(
name|Node
operator|.
name|DOCUMENT_TYPE_NODE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DocumentTypeImpl
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|publicId
parameter_list|,
specifier|final
name|String
name|systemId
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|DOCUMENT_TYPE_NODE
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|publicId
operator|=
name|publicId
expr_stmt|;
name|this
operator|.
name|systemId
operator|=
name|systemId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|publicId
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|systemId
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPublicId
parameter_list|()
block|{
return|return
name|publicId
return|;
block|}
specifier|public
name|void
name|setPublicId
parameter_list|(
specifier|final
name|String
name|publicId
parameter_list|)
block|{
name|this
operator|.
name|publicId
operator|=
name|publicId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSystemId
parameter_list|()
block|{
return|return
name|systemId
return|;
block|}
specifier|public
name|void
name|setSystemId
parameter_list|(
specifier|final
name|String
name|systemId
parameter_list|)
block|{
name|this
operator|.
name|systemId
operator|=
name|systemId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NamedNodeMap
name|getEntities
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|NamedNodeMap
name|getNotations
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInternalSubset
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|write
parameter_list|(
specifier|final
name|VariableByteOutputStream
name|ostream
parameter_list|)
throws|throws
name|IOException
block|{
name|ostream
operator|.
name|writeUTF
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|systemId
operator|!=
literal|null
condition|?
name|systemId
else|:
literal|""
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|publicId
operator|!=
literal|null
condition|?
name|publicId
else|:
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|read
parameter_list|(
specifier|final
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|name
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|systemId
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
if|if
condition|(
name|systemId
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|systemId
operator|=
literal|null
expr_stmt|;
block|}
name|publicId
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
if|if
condition|(
name|publicId
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|publicId
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"<!DOCTYPE "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|publicId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" PUBLIC \""
argument_list|)
operator|.
name|append
argument_list|(
name|publicId
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|systemId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|publicId
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" SYSTEM"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|" \""
argument_list|)
operator|.
name|append
argument_list|(
name|systemId
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

