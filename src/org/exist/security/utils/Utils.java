begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|utils
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
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|Permission
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
name|PermissionDeniedException
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
name|txn
operator|.
name|Txn
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
name|LockException
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Utils
block|{
specifier|public
specifier|static
name|Collection
name|createCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|TriggerException
block|{
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Collection "
operator|+
name|uri
operator|+
literal|" cannot be created."
argument_list|)
throw|;
block|}
name|collection
operator|.
name|setPermissions
argument_list|(
name|Permission
operator|.
name|DEFAULT_SYSTEM_SECURITY_COLLECTION_PERM
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
return|return
name|collection
return|;
block|}
specifier|public
specifier|static
name|Collection
name|getOrCreateCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|TriggerException
block|{
name|Collection
name|col
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|collectionUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|col
operator|==
literal|null
condition|)
block|{
name|col
operator|=
name|createCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|collectionUri
argument_list|)
expr_stmt|;
block|}
return|return
name|col
return|;
block|}
block|}
end_class

end_unit

