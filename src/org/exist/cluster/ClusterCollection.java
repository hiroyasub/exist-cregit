begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|//$Id$
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|cluster
package|;
end_package

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
name|Indexer
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
name|IndexInfo
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
name|dom
operator|.
name|BinaryDocument
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
name|DocumentSet
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
name|MutableDocumentSet
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
name|SyntaxException
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|java
operator|.
name|util
operator|.
name|Observer
import|;
end_import

begin_comment
comment|/**  * Created by Francesco Mondora.  *  * TODO ... verify TRANSACTION IN CLUSTER  * @author Francesco Mondora aka Makkina  * @author Michele Danieli aka cinde  * @author Nicola Breda aka maiale  *  *         Date: Aug 31, 2004  *         Time: 8:45:47 AM  *         Revision $Revision$  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ClusterCollection
extends|extends
name|Collection
block|{
name|Collection
name|collection
decl_stmt|;
specifier|private
name|Collection
name|getWrappedCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|instanceof
name|ClusterCollection
condition|)
block|{
return|return
name|getWrappedCollection
argument_list|(
operator|(
operator|(
name|ClusterCollection
operator|)
name|collection
operator|)
operator|.
name|collection
argument_list|)
return|;
block|}
return|return
name|collection
return|;
block|}
specifier|public
name|ClusterCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|getWrappedCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|store
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|IndexInfo
name|info
parameter_list|,
name|String
name|data
parameter_list|,
name|boolean
name|privileged
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|SAXException
throws|,
name|LockException
block|{
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|is
argument_list|,
name|privileged
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeXMLResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|docURI
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|LockException
block|{
name|collection
operator|.
name|removeXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|docURI
argument_list|)
expr_stmt|;
try|try
block|{
name|ClusterComunication
name|cluster
init|=
name|ClusterComunication
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|removeDocument
argument_list|(
name|this
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|docURI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This method is used by the XML RPC client.      *      * @param broker      * @param info      * @param source      * @param privileged      * @throws EXistException      * @throws PermissionDeniedException      * @throws TriggerException      * @throws SAXException      * @throws LockException      */
specifier|public
name|void
name|store
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|IndexInfo
name|info
parameter_list|,
name|InputSource
name|source
parameter_list|,
name|boolean
name|privileged
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|SAXException
throws|,
name|LockException
block|{
name|Indexer
name|indexer
init|=
name|info
operator|.
name|getIndexer
argument_list|()
decl_stmt|;
name|DocumentImpl
name|document
init|=
name|indexer
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|source
argument_list|,
name|privileged
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|source
operator|.
name|getByteStream
argument_list|()
decl_stmt|;
name|Reader
name|cs
init|=
name|source
operator|.
name|getCharacterStream
argument_list|()
decl_stmt|;
name|String
name|uri
init|=
literal|null
decl_stmt|;
name|String
name|content
init|=
literal|""
decl_stmt|;
try|try
block|{
name|byte
name|b
index|[]
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|is
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|is
operator|.
name|read
argument_list|(
name|b
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|c
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|cs
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|uri
operator|=
name|source
operator|.
name|getSystemId
argument_list|()
expr_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|url
operator|.
name|openConnection
argument_list|()
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|content
operator|=
name|buffer
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|bos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|bos
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
name|content
operator|=
name|bos
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|ClusterComunication
name|cluster
init|=
name|ClusterComunication
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|storeDocument
argument_list|(
name|this
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|document
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|BinaryDocument
name|addBinaryResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|name
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|String
name|mimeType
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|TriggerException
block|{
return|return
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|name
argument_list|,
name|data
argument_list|,
name|mimeType
argument_list|)
return|;
block|}
specifier|public
name|Lock
name|getLock
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getLock
argument_list|()
return|;
block|}
specifier|public
name|void
name|addCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|child
parameter_list|,
name|boolean
name|isNew
parameter_list|)
block|{
try|try
block|{
name|collection
operator|.
name|addCollection
argument_list|(
name|broker
argument_list|,
name|child
argument_list|,
name|isNew
argument_list|)
expr_stmt|;
specifier|final
name|String
name|childName
init|=
name|child
operator|.
name|getURI
argument_list|()
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"________ ADDDING COLLECTION "
operator|+
name|child
operator|.
name|getURI
argument_list|()
operator|+
literal|" TO "
operator|+
name|this
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterComunication
name|cluster
init|=
name|ClusterComunication
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|addCollection
argument_list|(
name|this
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|childName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeCollection
parameter_list|(
name|XmldbURI
name|name
parameter_list|)
throws|throws
name|LockException
block|{
try|try
block|{
name|collection
operator|.
name|removeCollection
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"REMOVED COLLECTION "
operator|+
name|name
argument_list|)
expr_stmt|;
name|ClusterComunication
name|cluster
init|=
name|ClusterComunication
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|//TODO: use xmldbUri
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|removeCollection
argument_list|(
name|this
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|name
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|hasChildCollection
parameter_list|(
name|XmldbURI
name|name
parameter_list|)
block|{
return|return
name|collection
operator|.
name|hasChildCollection
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|void
name|release
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|update
parameter_list|(
name|Collection
name|child
parameter_list|)
block|{
name|collection
operator|.
name|update
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addDocument
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|collection
operator|.
name|addDocument
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Iterator
name|collectionIterator
parameter_list|()
block|{
return|return
name|collection
operator|.
name|collectionIterator
argument_list|()
return|;
block|}
specifier|public
name|List
name|getDescendants
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|User
name|user
parameter_list|)
block|{
return|return
name|collection
operator|.
name|getDescendants
argument_list|(
name|broker
argument_list|,
name|user
argument_list|)
return|;
block|}
specifier|public
name|MutableDocumentSet
name|allDocs
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|MutableDocumentSet
name|docs
parameter_list|,
name|boolean
name|recursive
parameter_list|,
name|boolean
name|checkPermissions
parameter_list|)
block|{
return|return
name|collection
operator|.
name|allDocs
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|recursive
argument_list|,
name|checkPermissions
argument_list|)
return|;
block|}
specifier|public
name|DocumentSet
name|getDocuments
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|MutableDocumentSet
name|docs
parameter_list|,
name|boolean
name|checkPermissions
parameter_list|)
block|{
return|return
name|collection
operator|.
name|getDocuments
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|checkPermissions
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|allowUnload
parameter_list|()
block|{
return|return
name|collection
operator|.
name|allowUnload
argument_list|()
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|collection
operator|.
name|compareTo
argument_list|(
name|obj
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|collection
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
specifier|public
name|int
name|getChildCollectionCount
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getChildCollectionCount
argument_list|()
return|;
block|}
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|name
parameter_list|)
block|{
return|return
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|DocumentImpl
name|getDocumentWithLock
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|name
parameter_list|)
throws|throws
name|LockException
block|{
return|return
name|collection
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|DocumentImpl
name|getDocumentWithLock
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|name
parameter_list|,
name|int
name|lockMode
parameter_list|)
throws|throws
name|LockException
block|{
return|return
name|collection
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|name
argument_list|,
name|lockMode
argument_list|)
return|;
block|}
comment|/*       * @deprecated Use other method      * @see org.exist.collections.Collection#releaseDocument(org.exist.dom.DocumentImpl)      */
specifier|public
name|void
name|releaseDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|collection
operator|.
name|releaseDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|releaseDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|collection
operator|.
name|releaseDocument
argument_list|(
name|doc
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getDocumentCount
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getDocumentCount
argument_list|()
return|;
block|}
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getId
argument_list|()
return|;
block|}
specifier|public
name|XmldbURI
name|getURI
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getURI
argument_list|()
return|;
block|}
specifier|public
name|XmldbURI
name|getParentURI
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getParentURI
argument_list|()
return|;
block|}
specifier|public
name|Permission
name|getPermissions
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getPermissions
argument_list|()
return|;
block|}
specifier|public
name|Permission
name|getPermissionsNoLock
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getPermissionsNoLock
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|hasDocument
parameter_list|(
name|XmldbURI
name|name
parameter_list|)
block|{
return|return
name|collection
operator|.
name|hasDocument
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasSubcollection
parameter_list|(
name|XmldbURI
name|name
parameter_list|)
block|{
return|return
name|collection
operator|.
name|hasSubcollection
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasSubcollectionNoLock
parameter_list|(
name|XmldbURI
name|name
parameter_list|)
block|{
return|return
name|collection
operator|.
name|hasSubcollectionNoLock
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|collection
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
return|;
block|}
specifier|public
name|void
name|read
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|collection
operator|.
name|read
argument_list|(
name|broker
argument_list|,
name|istream
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeBinaryResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|docname
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|TriggerException
block|{
name|collection
operator|.
name|removeBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|docname
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeBinaryResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|TriggerException
block|{
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
block|}
specifier|public
name|IndexInfo
name|validateXMLResource
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|name
parameter_list|,
name|InputSource
name|source
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
return|return
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|name
argument_list|,
name|source
argument_list|)
return|;
block|}
specifier|public
name|IndexInfo
name|validateXMLResource
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|name
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
return|return
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|name
argument_list|,
name|data
argument_list|)
return|;
block|}
specifier|public
name|IndexInfo
name|validateXMLResource
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|name
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
return|return
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|name
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|public
name|void
name|store
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|IndexInfo
name|info
parameter_list|,
name|Node
name|node
parameter_list|,
name|boolean
name|privileged
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|SAXException
throws|,
name|LockException
block|{
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|node
argument_list|,
name|privileged
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|collection
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPermissions
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|LockException
block|{
name|collection
operator|.
name|setPermissions
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPermissions
parameter_list|(
name|String
name|mode
parameter_list|)
throws|throws
name|SyntaxException
throws|,
name|LockException
block|{
name|collection
operator|.
name|setPermissions
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPermissions
parameter_list|(
name|Permission
name|permissions
parameter_list|)
throws|throws
name|LockException
block|{
name|collection
operator|.
name|setPermissions
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|VariableByteOutputStream
name|ostream
parameter_list|)
throws|throws
name|IOException
block|{
name|collection
operator|.
name|write
argument_list|(
name|broker
argument_list|,
name|ostream
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setAddress
parameter_list|(
name|long
name|addr
parameter_list|)
block|{
name|collection
operator|.
name|setAddress
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getAddress
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getAddress
argument_list|()
return|;
block|}
specifier|public
name|void
name|setCreationTime
parameter_list|(
name|long
name|ms
parameter_list|)
block|{
name|collection
operator|.
name|setCreationTime
argument_list|(
name|ms
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getCreationTime
argument_list|()
return|;
block|}
specifier|public
name|void
name|setTriggersEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|collection
operator|.
name|setTriggersEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setReader
parameter_list|(
name|XMLReader
name|reader
parameter_list|)
block|{
name|collection
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
comment|/*public InputSource resolveEntity(String publicId, String systemId)             throws SAXException, IOException {         return collection.resolveEntity(publicId, systemId);     }*/
comment|/* (non-Javadoc) 	 * @see java.util.Observable#addObserver(java.util.Observer) 	 */
specifier|public
name|void
name|addObserver
parameter_list|(
name|Observer
name|o
parameter_list|)
block|{
name|collection
operator|.
name|addObserver
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.util.Observable#deleteObservers() 	 */
specifier|public
name|void
name|deleteObservers
parameter_list|()
block|{
name|collection
operator|.
name|deleteObservers
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cacheable#getKey() 	 */
specifier|public
name|long
name|getKey
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getKey
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cacheable#getReferenceCount() 	 */
specifier|public
name|int
name|getReferenceCount
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getReferenceCount
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cacheable#incReferenceCount() 	 */
specifier|public
name|int
name|incReferenceCount
parameter_list|()
block|{
return|return
name|collection
operator|.
name|incReferenceCount
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cacheable#decReferenceCount() 	 */
specifier|public
name|int
name|decReferenceCount
parameter_list|()
block|{
return|return
name|collection
operator|.
name|decReferenceCount
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cacheable#setReferenceCount(int) 	 */
specifier|public
name|void
name|setReferenceCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|collection
operator|.
name|setReferenceCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cacheable#setTimestamp(int) 	 */
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|int
name|timestamp
parameter_list|)
block|{
name|collection
operator|.
name|setTimestamp
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cacheable#getTimestamp() 	 */
specifier|public
name|int
name|getTimestamp
parameter_list|()
block|{
return|return
name|collection
operator|.
name|getTimestamp
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cacheable#release() 	 */
specifier|public
name|boolean
name|sync
parameter_list|(
name|boolean
name|syncJournal
parameter_list|)
block|{
return|return
name|collection
operator|.
name|sync
argument_list|(
name|syncJournal
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.cache.Cacheable#isDirty()      */
specifier|public
name|boolean
name|isDirty
parameter_list|()
block|{
return|return
name|collection
operator|.
name|isDirty
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|collection
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

