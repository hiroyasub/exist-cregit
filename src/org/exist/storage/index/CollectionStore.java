begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * CollectionStore.java - Jun 19, 2003  *   * @author wolf  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|index
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
name|dom
operator|.
name|persistent
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
name|DBException
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
name|Value
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
name|util
operator|.
name|ByteConversion
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
name|Configuration
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
name|UTF8
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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_comment
comment|/**  * Handles access to the central collection storage file (collections.dbx).   *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CollectionStore
extends|extends
name|BFile
block|{
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"collections.dbx"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FILE_KEY_IN_CONFIG
init|=
literal|"db-connection.collections"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|FREE_DOC_ID_KEY
init|=
literal|"__free_doc_id"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NEXT_DOC_ID_KEY
init|=
literal|"__next_doc_id"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|FREE_COLLECTION_ID_KEY
init|=
literal|"__free_collection_id"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NEXT_COLLECTION_ID_KEY
init|=
literal|"__next_collection_id"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|KEY_TYPE_COLLECTION
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|KEY_TYPE_DOCUMENT
init|=
literal|1
decl_stmt|;
specifier|private
name|Stack
argument_list|<
name|Integer
argument_list|>
name|freeResourceIds
init|=
operator|new
name|Stack
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Stack
argument_list|<
name|Integer
argument_list|>
name|freeCollectionIds
init|=
operator|new
name|Stack
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * @param pool      * @param id      * @param dataDir      * @param config      * @throws DBException      */
specifier|public
name|CollectionStore
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|byte
name|id
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|DBException
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|id
argument_list|,
literal|true
argument_list|,
operator|new
name|File
argument_list|(
name|dataDir
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|getFileName
argument_list|()
argument_list|)
argument_list|,
name|pool
operator|.
name|getCacheManager
argument_list|()
argument_list|,
literal|1.25
argument_list|,
literal|0.03
argument_list|)
expr_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
name|getConfigKeyForFile
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|FILE_NAME
return|;
block|}
specifier|public
specifier|static
name|String
name|getConfigKeyForFile
parameter_list|()
block|{
return|return
name|FILE_KEY_IN_CONFIG
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.store.BFile#getDataSyncPeriod()      */
annotation|@
name|Override
specifier|protected
name|long
name|getDataSyncPeriod
parameter_list|()
block|{
return|return
literal|1000
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|flush
parameter_list|()
throws|throws
name|DBException
block|{
name|boolean
name|flushed
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
condition|)
block|{
name|flushed
operator|=
name|flushed
operator||
name|dataCache
operator|.
name|flush
argument_list|()
expr_stmt|;
name|flushed
operator|=
name|flushed
operator||
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
return|return
name|flushed
return|;
block|}
specifier|public
name|void
name|freeResourceId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
specifier|final
name|Lock
name|lock
init|=
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|freeResourceIds
operator|.
name|push
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to acquire lock on "
operator|+
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getFreeResourceId
parameter_list|()
block|{
name|int
name|freeDocId
init|=
name|DocumentImpl
operator|.
name|UNKNOWN_DOCUMENT_ID
decl_stmt|;
specifier|final
name|Lock
name|lock
init|=
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|freeResourceIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|freeDocId
operator|=
name|freeResourceIds
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to acquire lock on "
operator|+
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|DocumentImpl
operator|.
name|UNKNOWN_DOCUMENT_ID
return|;
comment|//TODO : rethrow ? -pb
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
return|return
name|freeDocId
return|;
block|}
specifier|public
name|void
name|freeCollectionId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
specifier|final
name|Lock
name|lock
init|=
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|freeCollectionIds
operator|.
name|push
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to acquire lock on "
operator|+
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getFreeCollectionId
parameter_list|()
block|{
name|int
name|freeCollectionId
init|=
name|Collection
operator|.
name|UNKNOWN_COLLECTION_ID
decl_stmt|;
specifier|final
name|Lock
name|lock
init|=
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|freeCollectionIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|freeCollectionId
operator|=
name|freeCollectionIds
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to acquire lock on "
operator|+
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collection
operator|.
name|UNKNOWN_COLLECTION_ID
return|;
comment|//TODO : rethrow ? -pb
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
return|return
name|freeCollectionId
return|;
block|}
specifier|protected
name|void
name|dumpValue
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|IOException
block|{
comment|//TODO : what does this 5 stand for ?
if|if
condition|(
name|value
operator|.
name|getLength
argument_list|()
operator|==
literal|5
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
condition|)
block|{
specifier|final
name|short
name|collectionId
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|value
operator|.
name|data
argument_list|()
argument_list|,
name|value
operator|.
name|start
argument_list|()
argument_list|)
decl_stmt|;
comment|//TODO : what does this 1 stand for ?
specifier|final
name|int
name|docId
init|=
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|value
operator|.
name|data
argument_list|()
argument_list|,
name|value
operator|.
name|start
argument_list|()
operator|+
literal|1
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"Document: collection = "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|collectionId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|", docId = "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"Collection: "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
operator|new
name|String
argument_list|(
name|value
operator|.
name|data
argument_list|()
argument_list|,
name|value
operator|.
name|start
argument_list|()
argument_list|,
name|value
operator|.
name|getLength
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|DocumentKey
extends|extends
name|Value
block|{
specifier|public
specifier|static
specifier|final
name|int
name|OFFSET_TYPE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH_TYPE
init|=
literal|1
decl_stmt|;
comment|//sizeof byte
specifier|public
specifier|static
specifier|final
name|int
name|OFFSET_COLLECTION_ID
init|=
name|OFFSET_TYPE
operator|+
name|LENGTH_TYPE
decl_stmt|;
comment|//1
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH_TYPE_DOCUMENT
init|=
literal|2
decl_stmt|;
comment|//sizeof short
specifier|public
specifier|static
specifier|final
name|int
name|OFFSET_DOCUMENT_TYPE
init|=
name|OFFSET_COLLECTION_ID
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
decl_stmt|;
comment|//3
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH_DOCUMENT_TYPE
init|=
literal|1
decl_stmt|;
comment|//sizeof byte
specifier|public
specifier|static
specifier|final
name|int
name|OFFSET_DOCUMENT_ID
init|=
name|OFFSET_DOCUMENT_TYPE
operator|+
name|LENGTH_DOCUMENT_TYPE
decl_stmt|;
comment|//4
specifier|public
name|DocumentKey
parameter_list|()
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|LENGTH_TYPE
index|]
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
name|KEY_TYPE_DOCUMENT
expr_stmt|;
name|len
operator|=
name|LENGTH_TYPE
expr_stmt|;
block|}
specifier|public
name|DocumentKey
parameter_list|(
name|int
name|collectionId
parameter_list|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|LENGTH_TYPE
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
index|]
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
name|KEY_TYPE_DOCUMENT
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
name|len
operator|=
name|LENGTH_TYPE
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
expr_stmt|;
name|pos
operator|=
name|OFFSET_TYPE
expr_stmt|;
block|}
specifier|public
name|DocumentKey
parameter_list|(
name|int
name|collectionId
parameter_list|,
name|byte
name|type
parameter_list|,
name|int
name|docId
parameter_list|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|LENGTH_TYPE
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|LENGTH_DOCUMENT_TYPE
operator|+
name|DocumentImpl
operator|.
name|LENGTH_DOCUMENT_ID
index|]
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
name|KEY_TYPE_DOCUMENT
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
name|data
index|[
name|OFFSET_DOCUMENT_TYPE
index|]
operator|=
name|type
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|docId
argument_list|,
name|data
argument_list|,
name|OFFSET_DOCUMENT_ID
argument_list|)
expr_stmt|;
name|len
operator|=
name|LENGTH_TYPE
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|LENGTH_DOCUMENT_TYPE
operator|+
name|DocumentImpl
operator|.
name|LENGTH_DOCUMENT_ID
expr_stmt|;
name|pos
operator|=
name|OFFSET_TYPE
expr_stmt|;
block|}
specifier|public
specifier|static
name|int
name|getCollectionId
parameter_list|(
name|Value
name|key
parameter_list|)
block|{
return|return
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|key
operator|.
name|data
argument_list|()
argument_list|,
name|key
operator|.
name|start
argument_list|()
operator|+
name|OFFSET_COLLECTION_ID
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|getDocumentId
parameter_list|(
name|Value
name|key
parameter_list|)
block|{
return|return
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|key
operator|.
name|data
argument_list|()
argument_list|,
name|key
operator|.
name|start
argument_list|()
operator|+
name|OFFSET_DOCUMENT_ID
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|CollectionKey
extends|extends
name|Value
block|{
specifier|public
specifier|static
specifier|final
name|int
name|OFFSET_TYPE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH_TYPE
init|=
literal|1
decl_stmt|;
comment|//sizeof byte
specifier|public
specifier|static
specifier|final
name|int
name|OFFSET_VALUE
init|=
name|OFFSET_TYPE
operator|+
name|LENGTH_TYPE
decl_stmt|;
comment|//1
specifier|public
name|CollectionKey
parameter_list|()
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|LENGTH_TYPE
index|]
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
name|KEY_TYPE_COLLECTION
expr_stmt|;
name|len
operator|=
name|LENGTH_TYPE
expr_stmt|;
block|}
specifier|public
name|CollectionKey
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|len
operator|=
name|LENGTH_TYPE
operator|+
name|UTF8
operator|.
name|encoded
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
name|KEY_TYPE_COLLECTION
expr_stmt|;
name|UTF8
operator|.
name|encode
argument_list|(
name|name
argument_list|,
name|data
argument_list|,
name|OFFSET_VALUE
argument_list|)
expr_stmt|;
name|pos
operator|=
name|OFFSET_TYPE
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

