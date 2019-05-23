begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|ResourceMetadata
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
name|util
operator|.
name|MimeType
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

begin_class
specifier|public
class|class
name|DocumentMetadata
implements|implements
name|ResourceMetadata
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|NO_DOCTYPE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|HAS_DOCTYPE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|NO_LOCKTOKEN
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|HAS_LOCKTOKEN
init|=
literal|2
decl_stmt|;
comment|/**      * the mimeType of the document      */
specifier|private
name|String
name|mimeType
init|=
name|MimeType
operator|.
name|XML_TYPE
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**      * the creation time of this document      */
specifier|private
name|long
name|created
init|=
literal|0
decl_stmt|;
comment|/**      * time of the last modification      */
specifier|private
name|long
name|lastModified
init|=
literal|0
decl_stmt|;
comment|/**      * the number of data pages occupied by this document      */
specifier|private
name|int
name|pageCount
init|=
literal|0
decl_stmt|;
comment|/**      * contains the user id if a user lock is held on this resource      */
specifier|private
name|int
name|userLock
init|=
literal|0
decl_stmt|;
comment|/**      * the document's doctype declaration - if specified.      */
specifier|private
name|DocumentType
name|docType
init|=
literal|null
decl_stmt|;
comment|/**      * TODO associated lock token - if available      */
specifier|private
name|LockToken
name|lockToken
init|=
literal|null
decl_stmt|;
specifier|protected
specifier|transient
name|int
name|splitCount
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|isReferenced
init|=
literal|false
decl_stmt|;
specifier|public
name|DocumentMetadata
parameter_list|()
block|{
comment|//Nothing to do
block|}
specifier|public
name|DocumentMetadata
parameter_list|(
specifier|final
name|DocumentMetadata
name|other
parameter_list|)
block|{
name|this
operator|.
name|mimeType
operator|=
name|other
operator|.
name|mimeType
expr_stmt|;
name|this
operator|.
name|created
operator|=
name|other
operator|.
name|created
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|other
operator|.
name|lastModified
expr_stmt|;
block|}
comment|/**      * Copy all relevant fields from other      */
specifier|public
name|void
name|copyOf
parameter_list|(
specifier|final
name|DocumentMetadata
name|other
parameter_list|)
block|{
name|setCreated
argument_list|(
name|other
operator|.
name|getCreated
argument_list|()
argument_list|)
expr_stmt|;
name|setLastModified
argument_list|(
name|other
operator|.
name|getLastModified
argument_list|()
argument_list|)
expr_stmt|;
name|setMimeType
argument_list|(
name|other
operator|.
name|getMimeType
argument_list|()
argument_list|)
expr_stmt|;
name|setDocType
argument_list|(
name|other
operator|.
name|getDocType
argument_list|()
argument_list|)
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
name|created
return|;
block|}
specifier|public
name|void
name|setCreated
parameter_list|(
specifier|final
name|long
name|created
parameter_list|)
block|{
name|this
operator|.
name|created
operator|=
name|created
expr_stmt|;
if|if
condition|(
name|lastModified
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|lastModified
operator|=
name|created
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModified
return|;
block|}
specifier|public
name|void
name|setLastModified
parameter_list|(
specifier|final
name|long
name|lastModified
parameter_list|)
block|{
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
block|}
specifier|public
name|String
name|getMimeType
parameter_list|()
block|{
return|return
name|mimeType
return|;
block|}
specifier|public
name|void
name|setMimeType
parameter_list|(
specifier|final
name|String
name|mimeType
parameter_list|)
block|{
name|this
operator|.
name|mimeType
operator|=
name|mimeType
expr_stmt|;
block|}
comment|/**      * Returns the number of pages currently occupied by this document.      */
specifier|public
name|int
name|getPageCount
parameter_list|()
block|{
return|return
name|pageCount
return|;
block|}
comment|/**      * Set the number of pages currently occupied by this document.      *      * @param pageCount      */
specifier|public
name|void
name|setPageCount
parameter_list|(
specifier|final
name|int
name|pageCount
parameter_list|)
block|{
name|this
operator|.
name|pageCount
operator|=
name|pageCount
expr_stmt|;
block|}
specifier|public
name|void
name|incPageCount
parameter_list|()
block|{
operator|++
name|pageCount
expr_stmt|;
block|}
specifier|public
name|void
name|decPageCount
parameter_list|()
block|{
operator|--
name|pageCount
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|SymbolTable
name|symbolTable
parameter_list|,
specifier|final
name|VariableByteOutputStream
name|ostream
parameter_list|)
throws|throws
name|IOException
block|{
name|ostream
operator|.
name|writeLong
argument_list|(
name|created
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeLong
argument_list|(
name|lastModified
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|symbolTable
operator|.
name|getMimeTypeId
argument_list|(
name|mimeType
argument_list|)
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|pageCount
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|userLock
argument_list|)
expr_stmt|;
if|if
condition|(
name|docType
operator|!=
literal|null
condition|)
block|{
name|ostream
operator|.
name|writeByte
argument_list|(
name|HAS_DOCTYPE
argument_list|)
expr_stmt|;
operator|(
operator|(
name|DocumentTypeImpl
operator|)
name|docType
operator|)
operator|.
name|write
argument_list|(
name|ostream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ostream
operator|.
name|writeByte
argument_list|(
name|NO_DOCTYPE
argument_list|)
expr_stmt|;
block|}
comment|// TODO added by dwes
if|if
condition|(
name|lockToken
operator|!=
literal|null
condition|)
block|{
name|ostream
operator|.
name|writeByte
argument_list|(
name|HAS_LOCKTOKEN
argument_list|)
expr_stmt|;
name|lockToken
operator|.
name|write
argument_list|(
name|ostream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ostream
operator|.
name|writeByte
argument_list|(
name|NO_LOCKTOKEN
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|read
parameter_list|(
specifier|final
name|SymbolTable
name|symbolTable
parameter_list|,
specifier|final
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|created
operator|=
name|istream
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|lastModified
operator|=
name|istream
operator|.
name|readLong
argument_list|()
expr_stmt|;
specifier|final
name|int
name|mimeTypeSymbolsIndex
init|=
name|istream
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|mimeType
operator|=
name|symbolTable
operator|.
name|getMimeType
argument_list|(
name|mimeTypeSymbolsIndex
argument_list|)
expr_stmt|;
name|pageCount
operator|=
name|istream
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|userLock
operator|=
name|istream
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|istream
operator|.
name|readByte
argument_list|()
operator|==
name|HAS_DOCTYPE
condition|)
block|{
name|docType
operator|=
name|DocumentTypeImpl
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docType
operator|=
literal|null
expr_stmt|;
block|}
comment|// TODO added by dwes
if|if
condition|(
name|istream
operator|.
name|readByte
argument_list|()
operator|==
name|HAS_LOCKTOKEN
condition|)
block|{
name|lockToken
operator|=
operator|new
name|LockToken
argument_list|()
expr_stmt|;
name|lockToken
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lockToken
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getUserLock
parameter_list|()
block|{
return|return
name|userLock
return|;
block|}
specifier|public
name|void
name|setUserLock
parameter_list|(
specifier|final
name|int
name|userLock
parameter_list|)
block|{
name|this
operator|.
name|userLock
operator|=
name|userLock
expr_stmt|;
block|}
specifier|public
name|LockToken
name|getLockToken
parameter_list|()
block|{
return|return
name|lockToken
return|;
block|}
specifier|public
name|void
name|setLockToken
parameter_list|(
specifier|final
name|LockToken
name|token
parameter_list|)
block|{
name|lockToken
operator|=
name|token
expr_stmt|;
block|}
specifier|public
name|DocumentType
name|getDocType
parameter_list|()
block|{
return|return
name|docType
return|;
block|}
specifier|public
name|void
name|setDocType
parameter_list|(
specifier|final
name|DocumentType
name|docType
parameter_list|)
block|{
name|this
operator|.
name|docType
operator|=
name|docType
expr_stmt|;
block|}
comment|/**      * Increase the page split count of this document. The number      * of pages that have been split during inserts serves as an      * indicator for the fragmentation      */
specifier|public
name|void
name|incSplitCount
parameter_list|()
block|{
name|splitCount
operator|++
expr_stmt|;
block|}
specifier|public
name|int
name|getSplitCount
parameter_list|()
block|{
return|return
name|splitCount
return|;
block|}
specifier|public
name|void
name|setSplitCount
parameter_list|(
specifier|final
name|int
name|count
parameter_list|)
block|{
name|splitCount
operator|=
name|count
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReferenced
parameter_list|()
block|{
return|return
name|isReferenced
return|;
block|}
specifier|public
name|void
name|setReferenced
parameter_list|(
specifier|final
name|boolean
name|referenced
parameter_list|)
block|{
name|isReferenced
operator|=
name|referenced
expr_stmt|;
block|}
block|}
end_class

end_unit

