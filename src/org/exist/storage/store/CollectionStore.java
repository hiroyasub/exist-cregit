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
name|store
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
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|CollectionCache
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

begin_class
specifier|public
class|class
name|CollectionStore
extends|extends
name|BFile
block|{
comment|//	size of the internal buffer for collection objects
specifier|public
specifier|static
specifier|final
name|int
name|COLLECTION_BUFFER_SIZE
init|=
literal|128
decl_stmt|;
specifier|private
name|CollectionCache
name|collectionsCache
init|=
operator|new
name|CollectionCache
argument_list|(
name|COLLECTION_BUFFER_SIZE
argument_list|)
decl_stmt|;
comment|/** 	 * @param file 	 * @param btreeBuffers 	 * @param dataBuffers 	 */
specifier|public
name|CollectionStore
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|File
name|file
parameter_list|,
name|int
name|btreeBuffers
parameter_list|,
name|int
name|dataBuffers
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|file
argument_list|,
name|btreeBuffers
argument_list|,
name|dataBuffers
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CollectionCache
name|getCollectionCache
parameter_list|()
block|{
return|return
name|collectionsCache
return|;
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
block|}
end_class

end_unit

