begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
block|{
specifier|public
specifier|final
specifier|static
name|byte
name|HAS_DOCTYPE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|REINDEX_ALL
init|=
operator|-
literal|1
decl_stmt|;
comment|/** the mimeType of the document */
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
comment|/** the creation time of this document */
specifier|private
name|long
name|created
init|=
literal|0
decl_stmt|;
comment|/** time of the last modification */
specifier|private
name|long
name|lastModified
init|=
literal|0
decl_stmt|;
comment|/** the number of data pages occupied by this document */
specifier|private
name|int
name|pageCount
init|=
literal|0
decl_stmt|;
comment|/** contains the user id if a user lock is held on this resource */
specifier|private
name|int
name|userLock
init|=
literal|0
decl_stmt|;
comment|/** the document's doctype declaration - if specified. */
specifier|private
name|DocumentType
name|docType
init|=
literal|null
decl_stmt|;
specifier|private
specifier|transient
name|NodeIndexListener
name|listener
init|=
name|NullNodeIndexListener
operator|.
name|INSTANCE
decl_stmt|;
specifier|protected
specifier|transient
name|int
name|splitCount
init|=
literal|0
decl_stmt|;
comment|/**  	 * if set to> -1, the document needs to be partially reindexed 	 *  - beginning at the tree-level defined by reindex 	 */
specifier|protected
specifier|transient
name|int
name|reindex
init|=
name|REINDEX_ALL
decl_stmt|;
specifier|public
name|DocumentMetadata
parameter_list|()
block|{
block|}
specifier|public
name|DocumentMetadata
parameter_list|(
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
name|lastModified
operator|=
name|created
expr_stmt|;
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
comment|/** 	 * Returns the number of pages currently occupied by this document. 	 *  	 * @return 	 */
specifier|public
name|int
name|getPageCount
parameter_list|()
block|{
return|return
name|pageCount
return|;
block|}
comment|/** 	 * Set the number of pages currently occupied by this document. 	 * @param count 	 */
specifier|public
name|void
name|setPageCount
parameter_list|(
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
name|writeUTF
argument_list|(
name|mimeType
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
name|ostream
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|read
parameter_list|(
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
throws|,
name|EOFException
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
name|mimeType
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
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
operator|new
name|DocumentTypeImpl
argument_list|()
expr_stmt|;
operator|(
operator|(
name|DocumentTypeImpl
operator|)
name|docType
operator|)
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
block|}
else|else
name|docType
operator|=
literal|null
expr_stmt|;
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
specifier|public
name|NodeIndexListener
name|getIndexListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
specifier|public
name|void
name|clearIndexListener
parameter_list|()
block|{
name|listener
operator|=
name|NullNodeIndexListener
operator|.
name|INSTANCE
expr_stmt|;
block|}
specifier|public
name|void
name|setIndexListener
parameter_list|(
name|NodeIndexListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
specifier|public
name|int
name|reindexRequired
parameter_list|()
block|{
return|return
name|reindex
return|;
block|}
specifier|public
name|void
name|setReindexRequired
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|reindex
operator|=
name|level
expr_stmt|;
block|}
comment|/** 	 * Increase the page split count of this document. The number 	 * of pages that have been split during inserts serves as an 	 * indicator for the  	 * 	 */
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
name|int
name|count
parameter_list|)
block|{
name|splitCount
operator|=
name|count
expr_stmt|;
block|}
block|}
end_class

end_unit

