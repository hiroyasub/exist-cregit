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
name|Group
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
name|DBBroker
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
name|VariableByteArrayInput
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
specifier|public
specifier|final
specifier|static
name|byte
name|DATA
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|pageNr
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|byte
name|signature
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|BinaryDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BinaryDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|docName
parameter_list|,
name|Collection
name|collection
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|docName
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSignature
parameter_list|(
name|byte
name|signature
parameter_list|)
block|{
name|this
operator|.
name|signature
operator|=
name|signature
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
name|writeByte
argument_list|(
name|getResourceType
argument_list|()
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|fileName
operator|.
name|substring
argument_list|(
name|collection
operator|.
name|getName
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeLong
argument_list|(
name|address
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
name|broker
operator|.
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
name|Group
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
block|}
specifier|public
name|void
name|read
parameter_list|(
name|VariableByteArrayInput
name|istream
parameter_list|)
throws|throws
name|IOException
throws|,
name|EOFException
block|{
name|docId
operator|=
name|istream
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|fileName
operator|=
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|address
operator|=
name|istream
operator|.
name|readLong
argument_list|()
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
name|broker
operator|.
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
name|gid
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
name|permissions
operator|.
name|setGroup
argument_list|(
name|secman
operator|.
name|getGroup
argument_list|(
name|gid
argument_list|)
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
block|}
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
specifier|final
name|VariableByteOutputStream
name|ostream
init|=
operator|new
name|VariableByteOutputStream
argument_list|(
literal|17
argument_list|)
decl_stmt|;
try|try
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
name|writeByte
argument_list|(
name|signature
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
name|ostream
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|ostream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|data
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"io error while writing document data"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|void
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|VariableByteArrayInput
name|istream
init|=
operator|new
name|VariableByteArrayInput
argument_list|(
name|data
argument_list|)
decl_stmt|;
try|try
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
name|signature
operator|=
name|istream
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"IO error while reading document metadata"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

