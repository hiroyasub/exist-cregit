begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|collections
operator|.
name|Collection
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
name|BrokerPool
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
name|blob
operator|.
name|BlobId
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
name|btree
operator|.
name|Paged
operator|.
name|Page
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
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
comment|/**  * Represents a binary resource. Binary resources are just stored  * as binary data in a single overflow page. However, class BinaryDocument  * extends {@link org.exist.dom.persistent.DocumentImpl} and thus provides the  * same interface.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|BinaryDocument
extends|extends
name|DocumentImpl
block|{
specifier|private
name|BlobId
name|blobId
decl_stmt|;
specifier|private
name|long
name|realSize
init|=
literal|0L
decl_stmt|;
specifier|public
name|BinaryDocument
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new persistent binary Document instance.      *      * @param pool The broker pool      * @param collection The Collection which holds this document      * @param fileURI The name of the document      */
specifier|public
name|BinaryDocument
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|XmldbURI
name|fileURI
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|collection
argument_list|,
name|fileURI
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new persistent binary Document instance to replace an existing document instance.      *      * @param prevDoc The previous binary Document object that we are overwriting      */
specifier|public
name|BinaryDocument
parameter_list|(
specifier|final
name|DocumentImpl
name|prevDoc
parameter_list|)
block|{
name|super
argument_list|(
name|prevDoc
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new persistent binary Document instance to replace an existing document instance.      *      * @param collection The Collection which holds this document      * @param prevDoc The previous Document object that we are overwriting      * @param prevDoc The previous binary Document object that we are overwriting      */
specifier|public
name|BinaryDocument
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|Collection
operator|.
name|CollectionEntry
name|prevDoc
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|collection
argument_list|,
name|prevDoc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|getResourceType
parameter_list|()
block|{
return|return
name|BINARY_FILE
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getContentLength
parameter_list|()
block|{
return|return
name|realSize
return|;
block|}
specifier|public
name|void
name|setContentLength
parameter_list|(
specifier|final
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|realSize
operator|=
name|length
expr_stmt|;
block|}
comment|/**      * Get the Blob Store id for the      * content of this document.      *      * @return      */
annotation|@
name|Nullable
specifier|public
name|BlobId
name|getBlobId
parameter_list|()
block|{
return|return
name|blobId
return|;
block|}
specifier|public
name|void
name|setBlobId
parameter_list|(
specifier|final
name|BlobId
name|blobId
parameter_list|)
block|{
name|this
operator|.
name|blobId
operator|=
name|blobId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|writeInt
argument_list|(
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|blobId
operator|.
name|getId
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|write
argument_list|(
name|blobId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|getPermissions
argument_list|()
operator|.
name|write
argument_list|(
name|ostream
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeLong
argument_list|(
name|realSize
argument_list|)
expr_stmt|;
name|getMetadata
argument_list|()
operator|.
name|write
argument_list|(
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
argument_list|,
name|ostream
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|setDocId
argument_list|(
name|istream
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|setFileURI
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|istream
operator|.
name|readUTF
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|blobIdRaw
init|=
operator|new
name|byte
index|[
name|istream
operator|.
name|readInt
argument_list|()
index|]
decl_stmt|;
name|istream
operator|.
name|read
argument_list|(
name|blobIdRaw
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobId
operator|=
operator|new
name|BlobId
argument_list|(
name|blobIdRaw
argument_list|)
expr_stmt|;
name|getPermissions
argument_list|()
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
name|this
operator|.
name|realSize
operator|=
name|istream
operator|.
name|readLong
argument_list|()
expr_stmt|;
specifier|final
name|DocumentMetadata
name|metadata
init|=
operator|new
name|DocumentMetadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|read
argument_list|(
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
argument_list|,
name|istream
argument_list|)
expr_stmt|;
name|setMetadata
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

