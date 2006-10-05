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
name|CacheManager
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
name|NativeBroker
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
name|util
operator|.
name|ByteConversion
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
comment|/**      *       *       * @param pool       * @param cacheManager       * @param file       * @throws DBException       */
specifier|public
name|CollectionStore
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|File
name|file
parameter_list|,
name|CacheManager
name|cacheManager
parameter_list|)
throws|throws
name|DBException
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|NativeBroker
operator|.
name|COLLECTIONS_DBX_ID
argument_list|,
literal|true
argument_list|,
name|file
argument_list|,
name|cacheManager
argument_list|,
literal|1.25
argument_list|,
literal|0.01
argument_list|,
literal|0.03
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.dbxml.core.filer.BTree#getBTreeSyncPeriod()      */
specifier|protected
name|long
name|getBTreeSyncPeriod
parameter_list|()
block|{
return|return
literal|1000
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.store.BFile#getDataSyncPeriod()      */
specifier|protected
name|long
name|getDataSyncPeriod
parameter_list|()
block|{
return|return
literal|1000
return|;
block|}
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
if|if
condition|(
name|value
operator|.
name|getLength
argument_list|()
operator|==
literal|7
condition|)
block|{
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
literal|3
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
block|}
end_class

end_unit

