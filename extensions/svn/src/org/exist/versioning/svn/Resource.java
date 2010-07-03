begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringBufferInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|dom
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
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
name|lock
operator|.
name|Lock
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
name|TransactionManager
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
name|util
operator|.
name|MimeTable
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
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|wc
operator|.
name|SVNFileUtil
import|;
end_import

begin_comment
comment|/**  * eXist's resource. It extend java.io.File  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Resource
extends|extends
name|File
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3450182389919974961L
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|char
name|separatorChar
init|=
literal|'/'
decl_stmt|;
specifier|protected
name|XmldbURI
name|uri
decl_stmt|;
specifier|protected
name|XmldbURI
name|collectionPath
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
specifier|private
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
specifier|private
name|DocumentImpl
name|resource
init|=
literal|null
decl_stmt|;
specifier|public
name|Resource
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
block|{
name|super
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
specifier|public
name|Resource
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Resource
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|child
parameter_list|)
block|{
name|this
argument_list|(
operator|(
name|Resource
operator|)
name|file
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Resource
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|String
name|child
parameter_list|)
block|{
name|this
argument_list|(
name|resource
operator|.
name|uri
operator|.
name|append
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Resource
parameter_list|(
name|String
name|parent
parameter_list|,
name|String
name|child
parameter_list|)
block|{
name|this
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|parent
argument_list|)
operator|.
name|append
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Resource
name|getParentFile
parameter_list|()
block|{
name|XmldbURI
name|parentPath
init|=
name|uri
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentPath
operator|==
name|XmldbURI
operator|.
name|EMPTY_URI
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|Resource
argument_list|(
name|parentPath
argument_list|)
return|;
block|}
specifier|public
name|Resource
name|getAbsoluteFile
parameter_list|()
block|{
return|return
name|this
return|;
comment|//UNDERSTAND: is it correct?
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|uri
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|mkdir
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|tm
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
operator|.
name|toCollectionPathURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
return|return
literal|true
return|;
name|Collection
name|parent_collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
operator|.
name|toCollectionPathURI
argument_list|()
operator|.
name|removeLastSegment
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent_collection
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|tm
operator|=
name|db
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|Txn
name|transaction
init|=
name|tm
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|Collection
name|child
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|uri
operator|.
name|toCollectionPathURI
argument_list|()
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|tm
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|mkdirs
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|tm
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
operator|.
name|toCollectionPathURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
return|return
literal|true
return|;
name|tm
operator|=
name|db
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|Txn
name|transaction
init|=
name|tm
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|Collection
name|child
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|uri
operator|.
name|toCollectionPathURI
argument_list|()
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|tm
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isDirectory
parameter_list|()
block|{
try|try
block|{
name|init
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
name|resource
operator|==
literal|null
operator|)
return|;
block|}
specifier|public
name|boolean
name|isFile
parameter_list|()
block|{
try|try
block|{
name|init
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
name|resource
operator|!=
literal|null
operator|)
return|;
block|}
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
try|try
block|{
name|init
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
operator|(
name|collection
operator|!=
literal|null
operator|)
operator|||
operator|(
name|resource
operator|!=
literal|null
operator|)
operator|)
return|;
block|}
specifier|public
name|boolean
name|canRead
parameter_list|()
block|{
try|try
block|{
return|return
name|getPermission
argument_list|()
operator|.
name|validate
argument_list|(
name|getBrokerUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|boolean
name|renameTo
parameter_list|(
name|File
name|dest
parameter_list|)
block|{
name|XmldbURI
name|destinationPath
init|=
operator|(
operator|(
name|Resource
operator|)
name|dest
operator|)
operator|.
name|uri
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|tm
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
name|tm
operator|=
name|db
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|Txn
name|transaction
init|=
name|tm
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|destination
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|source
init|=
literal|null
decl_stmt|;
name|XmldbURI
name|newName
decl_stmt|;
try|try
block|{
name|source
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|uri
operator|.
name|removeLastSegment
argument_list|()
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|DocumentImpl
name|doc
init|=
name|source
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|uri
operator|.
name|lastSegment
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|destination
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|destinationPath
operator|.
name|removeLastSegment
argument_list|()
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|newName
operator|=
name|destinationPath
operator|.
name|lastSegment
argument_list|()
expr_stmt|;
name|broker
operator|.
name|copyResource
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|,
name|destination
argument_list|,
name|newName
argument_list|)
expr_stmt|;
name|tm
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
name|source
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
name|destination
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|setReadOnly
parameter_list|()
block|{
comment|//XXX: code !!!
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|delete
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|tm
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
name|tm
operator|=
name|db
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|Txn
name|transaction
init|=
name|tm
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|uri
operator|.
name|removeLastSegment
argument_list|()
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// keep the write lock in the transaction
name|transaction
operator|.
name|registerLock
argument_list|(
name|collection
operator|.
name|getLock
argument_list|()
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|uri
operator|.
name|lastSegment
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
name|collection
operator|.
name|removeBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc
argument_list|)
expr_stmt|;
else|else
name|collection
operator|.
name|removeXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|uri
operator|.
name|lastSegment
argument_list|()
argument_list|)
expr_stmt|;
name|tm
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|createNewFile
parameter_list|()
throws|throws
name|IOException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|tm
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
if|if
condition|(
name|uri
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"It collection, but should be resource: "
operator|+
name|uri
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|XmldbURI
name|collectionURI
init|=
name|uri
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|collectionURI
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Collection not found: "
operator|+
name|collectionURI
argument_list|)
throw|;
name|XmldbURI
name|fileName
init|=
name|uri
operator|.
name|lastSegment
argument_list|()
decl_stmt|;
try|try
block|{
name|resource
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|uri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e1
parameter_list|)
block|{
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|collection
operator|=
name|resource
operator|.
name|getCollection
argument_list|()
expr_stmt|;
name|initialized
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
name|MimeType
name|mimeType
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mimeType
operator|==
literal|null
condition|)
block|{
name|mimeType
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
name|tm
operator|=
name|db
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|Txn
name|transaction
init|=
name|tm
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//			if (mimeType.isXMLType()) {
comment|//				// store as xml resource
comment|//				is = new FileInputStream(temp);
comment|//				IndexInfo info = collection.validateXMLResource(
comment|//						transaction, broker, fileName, new InputSource(new InputStreamReader(is)));
comment|//				is.close();
comment|//				info.getDocument().getMetadata().setMimeType(mimeType.getName());
comment|//				is = new FileInputStream(temp);
comment|//				collection.store(transaction, broker, info, new InputSource(new InputStreamReader(is)), false);
comment|//				is.close();
comment|//
comment|//			} else {
comment|// store as binary resource
name|is
operator|=
operator|new
name|StringBufferInputStream
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|fileName
argument_list|,
name|is
argument_list|,
name|mimeType
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|int
operator|)
literal|0
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
comment|//			}
name|tm
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|tm
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|SVNFileUtil
operator|.
name|closeFile
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|initialized
condition|)
return|return;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
comment|//collection
if|if
condition|(
name|uri
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource not found: "
operator|+
name|uri
argument_list|)
throw|;
comment|//resource
block|}
else|else
block|{
name|resource
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|uri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
comment|//may be, it's collection ... cheking ...
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|)
expr_stmt|;
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
literal|"Resource not found: "
operator|+
name|uri
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|collection
operator|=
name|resource
operator|.
name|getCollection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
name|Permission
name|getPermission
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|isFile
argument_list|()
condition|)
return|return
name|collection
operator|.
name|getPermissions
argument_list|()
return|;
if|if
condition|(
name|isDirectory
argument_list|()
condition|)
return|return
name|resource
operator|.
name|getPermissions
argument_list|()
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"this never should happen"
argument_list|)
throw|;
block|}
specifier|private
name|User
name|getBrokerUser
parameter_list|()
throws|throws
name|IOException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|broker
operator|.
name|getUser
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|getConnection
argument_list|()
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|BufferedInputStream
name|bis
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
return|return
operator|new
name|InputStreamReader
argument_list|(
name|bis
argument_list|)
return|;
block|}
specifier|private
name|URLConnection
name|connection
init|=
literal|null
decl_stmt|;
specifier|private
name|URLConnection
name|getConnection
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"xmldb:exist://"
operator|+
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|connection
return|;
block|}
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getInputStream
argument_list|()
return|;
block|}
specifier|public
name|Writer
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|getOutputStream
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getOutputStream
argument_list|(
literal|false
argument_list|)
return|;
block|}
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|(
name|boolean
name|append
parameter_list|)
throws|throws
name|IOException
block|{
comment|//XXX: code append
return|return
name|getConnection
argument_list|()
operator|.
name|getOutputStream
argument_list|()
return|;
block|}
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|()
expr_stmt|;
return|return
name|resource
return|;
block|}
specifier|public
name|Collection
name|getCollection
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
if|if
condition|(
name|uri
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
operator|.
name|removeLastSegment
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Collection not found: "
operator|+
name|uri
argument_list|)
throw|;
return|return
name|collection
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
return|return
name|collection
return|;
else|else
return|return
name|resource
operator|.
name|getCollection
argument_list|()
return|;
block|}
specifier|public
name|File
index|[]
name|listFiles
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isDirectory
argument_list|()
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|collection
operator|.
name|getLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|File
index|[]
name|children
init|=
operator|new
name|File
index|[
name|collection
operator|.
name|getChildCollectionCount
argument_list|()
operator|+
name|collection
operator|.
name|getDocumentCount
argument_list|()
index|]
decl_stmt|;
comment|//collections
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|XmldbURI
argument_list|>
name|i
init|=
name|collection
operator|.
name|collectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
name|children
index|[
name|j
index|]
operator|=
operator|new
name|Resource
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|//collections
name|List
argument_list|<
name|XmldbURI
argument_list|>
name|allresources
init|=
operator|new
name|ArrayList
argument_list|<
name|XmldbURI
argument_list|>
argument_list|()
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|collection
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|doc
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Include only when (1) locktoken is present or (2)
comment|// locktoken indicates that it is not a null resource
name|LockToken
name|lock
init|=
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLockToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|lock
operator|==
literal|null
operator|||
operator|(
operator|!
name|lock
operator|.
name|isNullResource
argument_list|()
operator|)
condition|)
block|{
name|allresources
operator|.
name|add
argument_list|(
name|doc
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Copy content of list into String array.
for|for
control|(
name|Iterator
argument_list|<
name|XmldbURI
argument_list|>
name|i
init|=
name|allresources
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|children
index|[
name|j
index|]
operator|=
operator|new
name|Resource
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|children
return|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
comment|//throw new IOException("Failed to acquire lock on collection '" + uri + "'");
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

