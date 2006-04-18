begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|NodeProxy
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
name|StoredNode
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
name|StorageAddress
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
name|BTree
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
name|BTreeException
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
name|LockException
import|;
end_import

begin_comment
comment|/**  * Iterate through all nodes of a document in the DOM storage. Returns the  * raw data of the node in a {@link org.exist.storage.btree.Value}. Use class   * {@link org.exist.storage.dom.NodeIterator} to get node objects instead of  * raw data.  *   * The DOM file is locked to locate the data and released afterwards. Before  * working with the returned data, you should get a copy by calling value.getData().   *   * @author wolf  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|DOMFileIterator
implements|implements
name|Iterator
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DOMFileIterator
operator|.
name|class
argument_list|)
decl_stmt|;
name|DOMFile
name|db
init|=
literal|null
decl_stmt|;
name|StoredNode
name|node
init|=
literal|null
decl_stmt|;
name|int
name|offset
decl_stmt|;
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
name|short
name|lastTID
init|=
operator|-
literal|1
decl_stmt|;
name|DOMFile
operator|.
name|DOMPage
name|p
init|=
literal|null
decl_stmt|;
name|long
name|page
decl_stmt|;
name|long
name|startAddress
init|=
name|StoredNode
operator|.
name|UNKNOWN_NODE_IMPL_ADDRESS
decl_stmt|;
name|Object
name|lockKey
decl_stmt|;
specifier|public
name|DOMFileIterator
parameter_list|(
name|Object
name|lock
parameter_list|,
name|DOMFile
name|db
parameter_list|,
name|StoredNode
name|node
parameter_list|)
throws|throws
name|BTreeException
throws|,
name|IOException
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getInternalAddress
argument_list|()
operator|!=
name|StoredNode
operator|.
name|UNKNOWN_NODE_IMPL_ADDRESS
condition|)
name|startAddress
operator|=
name|node
operator|.
name|getInternalAddress
argument_list|()
expr_stmt|;
else|else
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|lockKey
operator|=
operator|(
name|lock
operator|==
literal|null
condition|?
name|this
else|:
name|lock
operator|)
expr_stmt|;
block|}
specifier|public
name|DOMFileIterator
parameter_list|(
name|Object
name|lock
parameter_list|,
name|DOMFile
name|db
parameter_list|,
name|long
name|address
parameter_list|)
throws|throws
name|BTreeException
throws|,
name|IOException
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|startAddress
operator|=
name|address
expr_stmt|;
name|lockKey
operator|=
operator|(
name|lock
operator|==
literal|null
condition|?
name|this
else|:
name|lock
operator|)
expr_stmt|;
block|}
comment|/** 		 *  Returns the internal virtual address of the node at the iterator's 		 * current position. 		 * 		 *@return    The currentAddress value 		 */
specifier|public
name|long
name|currentAddress
parameter_list|()
block|{
return|return
name|StorageAddress
operator|.
name|createPointer
argument_list|(
operator|(
name|int
operator|)
name|page
argument_list|,
name|lastTID
argument_list|)
return|;
block|}
comment|/** 		 *  Are there more nodes to be read? 		 * 		 *@return    Description of the Return Value 		 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
name|Lock
name|lock
init|=
name|db
operator|.
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|()
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
literal|"Failed to acquire read lock on "
operator|+
name|db
operator|.
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|gotoNextPosition
argument_list|()
condition|)
block|{
name|db
operator|.
name|getPageBuffer
argument_list|()
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
specifier|final
name|DOMFile
operator|.
name|DOMFilePageHeader
name|ph
init|=
name|p
operator|.
name|getPageHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|offset
operator|<
name|ph
operator|.
name|getDataLength
argument_list|()
condition|)
return|return
literal|true
return|;
if|else if
condition|(
name|ph
operator|.
name|getNextDataPage
argument_list|()
operator|==
name|Page
operator|.
name|NO_PAGE
condition|)
return|return
literal|false
return|;
else|else
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|BTreeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
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
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/** 		 *  Returns the raw data of the next node in the sequence. 		 * 		 *@return    Description of the Return Value 		 */
specifier|public
name|Object
name|next
parameter_list|()
block|{
name|Lock
name|lock
init|=
name|db
operator|.
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|()
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
literal|"Failed to acquire read lock on "
operator|+
name|db
operator|.
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Value
name|nextVal
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|gotoNextPosition
argument_list|()
condition|)
block|{
do|do
block|{
name|DOMFile
operator|.
name|DOMFilePageHeader
name|ph
init|=
name|p
operator|.
name|getPageHeader
argument_list|()
decl_stmt|;
comment|// next value larger than length of the current page?
if|if
condition|(
name|offset
operator|>=
name|ph
operator|.
name|getDataLength
argument_list|()
condition|)
block|{
comment|// load next page in chain
specifier|final
name|long
name|nextPage
init|=
name|ph
operator|.
name|getNextDataPage
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextPage
operator|==
name|Page
operator|.
name|NO_PAGE
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"bad link to next "
operator|+
name|p
operator|.
name|page
operator|.
name|getPageInfo
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|page
operator|=
name|nextPage
expr_stmt|;
name|p
operator|=
name|db
operator|.
name|getCurrentPage
argument_list|(
name|nextPage
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|db
operator|.
name|addToBuffer
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|lastOffset
operator|=
name|offset
expr_stmt|;
comment|// extract tid
name|lastTID
operator|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|p
operator|.
name|data
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|2
expr_stmt|;
comment|//	check if this is just a link to a relocated node
if|if
condition|(
name|ItemId
operator|.
name|isLink
argument_list|(
name|lastTID
argument_list|)
condition|)
block|{
comment|// skip this
name|offset
operator|+=
literal|8
expr_stmt|;
continue|continue;
block|}
comment|// read data length
name|short
name|l
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|p
operator|.
name|data
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|+=
literal|2
expr_stmt|;
if|if
condition|(
name|ItemId
operator|.
name|isRelocated
argument_list|(
name|lastTID
argument_list|)
condition|)
block|{
comment|// found a relocated node. Skip the next 8 bytes
name|offset
operator|+=
literal|8
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|==
name|DOMFile
operator|.
name|OVERFLOW
condition|)
block|{
specifier|final
name|long
name|op
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|p
operator|.
name|data
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|+=
literal|8
expr_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
name|db
operator|.
name|getOverflowValue
argument_list|(
name|op
argument_list|)
decl_stmt|;
name|nextVal
operator|=
operator|new
name|Value
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nextVal
operator|=
operator|new
name|Value
argument_list|(
name|p
operator|.
name|data
argument_list|,
name|offset
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|l
expr_stmt|;
block|}
name|nextVal
operator|.
name|setAddress
argument_list|(
name|StorageAddress
operator|.
name|createPointer
argument_list|(
operator|(
name|int
operator|)
name|page
argument_list|,
name|ItemId
operator|.
name|getId
argument_list|(
name|lastTID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|nextVal
operator|==
literal|null
condition|)
do|;
block|}
return|return
name|nextVal
return|;
block|}
catch|catch
parameter_list|(
name|BTreeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
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
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Remove is not allowed"
argument_list|)
throw|;
block|}
specifier|private
name|boolean
name|gotoNextPosition
parameter_list|()
throws|throws
name|BTreeException
throws|,
name|IOException
block|{
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|setOwnerObject
argument_list|(
name|lockKey
argument_list|)
expr_stmt|;
name|long
name|addr
init|=
name|db
operator|.
name|findValue
argument_list|(
name|lockKey
argument_list|,
operator|new
name|NodeProxy
argument_list|(
name|node
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|==
name|BTree
operator|.
name|KEY_NOT_FOUND
condition|)
return|return
literal|false
return|;
name|DOMFile
operator|.
name|RecordPos
name|rec
init|=
name|db
operator|.
name|findRecord
argument_list|(
name|addr
argument_list|)
decl_stmt|;
if|if
condition|(
name|rec
operator|!=
literal|null
condition|)
block|{
name|page
operator|=
name|rec
operator|.
name|page
operator|.
name|getPageNum
argument_list|()
expr_stmt|;
name|p
operator|=
name|rec
operator|.
name|page
expr_stmt|;
name|offset
operator|=
name|rec
operator|.
name|offset
operator|-
literal|2
expr_stmt|;
name|node
operator|=
literal|null
expr_stmt|;
block|}
else|else
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|startAddress
operator|!=
name|StoredNode
operator|.
name|UNKNOWN_NODE_IMPL_ADDRESS
condition|)
block|{
name|DOMFile
operator|.
name|RecordPos
name|rec
init|=
name|db
operator|.
name|findRecord
argument_list|(
name|startAddress
argument_list|)
decl_stmt|;
if|if
condition|(
name|rec
operator|!=
literal|null
condition|)
block|{
name|page
operator|=
name|rec
operator|.
name|page
operator|.
name|getPageNum
argument_list|()
expr_stmt|;
name|offset
operator|=
name|rec
operator|.
name|offset
operator|-
literal|2
expr_stmt|;
name|p
operator|=
name|rec
operator|.
name|page
expr_stmt|;
name|startAddress
operator|=
name|StoredNode
operator|.
name|UNKNOWN_NODE_IMPL_ADDRESS
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|page
operator|==
name|Page
operator|.
name|NO_PAGE
condition|)
return|return
literal|false
return|;
name|p
operator|=
name|db
operator|.
name|getCurrentPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|db
operator|.
name|addToBuffer
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** 		 *  Reposition the iterator at the address of the proxy node. 		 * 		 *@param  node  The new to value 		 */
specifier|public
name|void
name|setTo
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getInternalAddress
argument_list|()
operator|!=
name|StoredNode
operator|.
name|UNKNOWN_NODE_IMPL_ADDRESS
condition|)
block|{
name|startAddress
operator|=
name|node
operator|.
name|getInternalAddress
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
block|}
comment|/** 		 *  Reposition the iterate at a given address. 		 * 		 *@param  address  The new to value 		 */
specifier|public
name|void
name|setTo
parameter_list|(
name|long
name|address
parameter_list|)
block|{
name|this
operator|.
name|startAddress
operator|=
name|address
expr_stmt|;
block|}
block|}
end_class

end_unit

