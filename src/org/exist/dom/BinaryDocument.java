begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|security
operator|.
name|GroupImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
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

begin_comment
comment|/**  * Represents a binary resource. Binary resources are just stored  * as binary data in a single overflow page. However, class BinaryDocument  * extends {@link org.exist.dom.DocumentImpl} and thus provides the   * same interface.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|BinaryDocument
extends|extends
name|DocumentImpl
block|{
specifier|private
name|long
name|pageNr
init|=
name|Page
operator|.
name|NO_PAGE
decl_stmt|;
specifier|private
name|int
name|realSize
init|=
literal|0
decl_stmt|;
specifier|public
name|BinaryDocument
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BinaryDocument
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Collection
name|collection
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BinaryDocument
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|XmldbURI
name|fileURI
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|null
argument_list|,
name|fileURI
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BinaryDocument
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Collection
name|collection
parameter_list|,
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
comment|/* (non-Javadoc) 	 * @see org.exist.dom.DocumentImpl#getResourceType() 	 */
specifier|public
name|byte
name|getResourceType
parameter_list|()
block|{
return|return
name|BINARY_FILE
return|;
block|}
specifier|public
name|void
name|setPage
parameter_list|(
name|long
name|page
parameter_list|)
block|{
name|this
operator|.
name|pageNr
operator|=
name|page
expr_stmt|;
block|}
specifier|public
name|long
name|getPage
parameter_list|()
block|{
return|return
name|pageNr
return|;
block|}
specifier|public
name|int
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
name|int
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
name|writeLong
argument_list|(
name|pageNr
argument_list|)
expr_stmt|;
name|SecurityManager
name|secman
init|=
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|secman
operator|==
literal|null
condition|)
block|{
comment|//TODO : explain those 2 values -pb
name|ostream
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|User
name|user
init|=
name|secman
operator|.
name|getUser
argument_list|(
name|permissions
operator|.
name|getOwner
argument_list|()
argument_list|)
decl_stmt|;
name|GroupImpl
name|group
init|=
name|secman
operator|.
name|getGroup
argument_list|(
name|permissions
operator|.
name|getOwnerGroup
argument_list|()
argument_list|)
decl_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|user
operator|.
name|getUID
argument_list|()
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ostream
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|permissions
operator|.
name|getPermissions
argument_list|()
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
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
argument_list|,
name|ostream
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
name|pageNr
operator|=
name|istream
operator|.
name|readLong
argument_list|()
expr_stmt|;
specifier|final
name|SecurityManager
name|secman
init|=
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
specifier|final
name|int
name|uid
init|=
name|istream
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|groupId
init|=
name|istream
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|perm
init|=
operator|(
name|istream
operator|.
name|readByte
argument_list|()
operator|&
literal|0777
operator|)
decl_stmt|;
if|if
condition|(
name|secman
operator|==
literal|null
condition|)
block|{
name|permissions
operator|.
name|setOwner
argument_list|(
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
expr_stmt|;
name|permissions
operator|.
name|setGroup
argument_list|(
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|permissions
operator|.
name|setOwner
argument_list|(
name|secman
operator|.
name|getUser
argument_list|(
name|uid
argument_list|)
argument_list|)
expr_stmt|;
name|GroupImpl
name|group
init|=
name|secman
operator|.
name|getGroup
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
name|permissions
operator|.
name|setGroup
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|permissions
operator|.
name|setPermissions
argument_list|(
name|perm
argument_list|)
expr_stmt|;
name|realSize
operator|=
name|istream
operator|.
name|readInt
argument_list|()
expr_stmt|;
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

