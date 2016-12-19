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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|dom
operator|.
name|persistent
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
name|persistent
operator|.
name|IStoredNode
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
name|storage
operator|.
name|lock
operator|.
name|Lock
operator|.
name|LockMode
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
name|FileUtils
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
name|sanity
operator|.
name|SanityCheck
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
name|dom
operator|.
name|persistent
operator|.
name|NodeHandle
import|;
end_import

begin_comment
comment|/**  * Class NodeIterator is used to iterate over nodes in the DOM storage.  * This implementation locks the DOM file to read the node and unlocks  * it afterwards. It is thus safer than DOMFileIterator, since the node's  * value will not change.   *   * @author wolf  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NodeIterator
implements|implements
name|INodeIterator
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|NodeIterator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DOMFile
name|db
init|=
literal|null
decl_stmt|;
specifier|private
name|NodeHandle
name|node
decl_stmt|;
comment|//= null;
specifier|private
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|offset
decl_stmt|;
specifier|private
name|short
name|lastTupleID
init|=
name|ItemId
operator|.
name|UNKNOWN_ID
decl_stmt|;
specifier|private
name|DOMFile
operator|.
name|DOMPage
name|page
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|pageNum
decl_stmt|;
specifier|private
name|long
name|startAddress
init|=
name|StoredNode
operator|.
name|UNKNOWN_NODE_IMPL_ADDRESS
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|boolean
name|useNodePool
init|=
literal|false
decl_stmt|;
specifier|public
name|NodeIterator
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DOMFile
name|db
parameter_list|,
name|NodeHandle
name|node
parameter_list|,
name|boolean
name|poolable
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
name|doc
operator|=
name|node
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|this
operator|.
name|useNodePool
operator|=
name|poolable
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
comment|/**      *  Returns the internal virtual address of the node at the iterator's      * current position.      *      *@return    The currentAddress value      */
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
name|pageNum
argument_list|,
name|ItemId
operator|.
name|getId
argument_list|(
name|lastTupleID
argument_list|)
argument_list|)
return|;
block|}
comment|/**      *  Are there more nodes to be read?      *      *@return<code>true</code> if there is at least one more node to read      */
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
specifier|final
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
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
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
literal|"Failed to acquire read lock on "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|db
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO : throw exception here ? -pb
return|return
literal|false
return|;
block|}
name|db
operator|.
name|setOwnerObject
argument_list|(
name|broker
argument_list|)
expr_stmt|;
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
name|page
argument_list|)
expr_stmt|;
specifier|final
name|DOMFile
operator|.
name|DOMFilePageHeader
name|pageHeader
init|=
name|page
operator|.
name|getPageHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|offset
operator|<
name|pageHeader
operator|.
name|getDataLength
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|pageHeader
operator|.
name|getNextDataPage
argument_list|()
operator|==
name|Page
operator|.
name|NO_PAGE
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
comment|//Mmmmh... strange -pb
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
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
comment|//TODO : throw exception here ? -pb
block|}
catch|catch
parameter_list|(
specifier|final
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
comment|//TODO : throw exception here ? -pb
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      *  Returns the next node in document order.       */
annotation|@
name|Override
specifier|public
name|IStoredNode
name|next
parameter_list|()
block|{
specifier|final
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
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
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
literal|"Failed to acquire read lock on "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|db
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO : throw exception here ? -pb
return|return
literal|null
return|;
block|}
name|db
operator|.
name|setOwnerObject
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|IStoredNode
name|nextNode
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|gotoNextPosition
argument_list|()
condition|)
block|{
name|long
name|backLink
init|=
literal|0
decl_stmt|;
do|do
block|{
specifier|final
name|DOMFile
operator|.
name|DOMFilePageHeader
name|pageHeader
init|=
name|page
operator|.
name|getPageHeader
argument_list|()
decl_stmt|;
comment|//Next value larger than length of the current page?
if|if
condition|(
name|offset
operator|>=
name|pageHeader
operator|.
name|getDataLength
argument_list|()
condition|)
block|{
comment|//Load next page in chain
name|long
name|nextPageNum
init|=
name|pageHeader
operator|.
name|getNextDataPage
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextPageNum
operator|==
name|Page
operator|.
name|NO_PAGE
condition|)
block|{
name|SanityCheck
operator|.
name|TRACE
argument_list|(
literal|"bad link to next "
operator|+
name|page
operator|.
name|page
operator|.
name|getPageInfo
argument_list|()
operator|+
literal|"; previous: "
operator|+
name|pageHeader
operator|.
name|getPreviousDataPage
argument_list|()
operator|+
literal|"; offset = "
operator|+
name|offset
operator|+
literal|"; lastTupleID = "
operator|+
name|lastTupleID
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|db
operator|.
name|debugPageContents
argument_list|(
name|page
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO : throw exception here ? -pb
return|return
literal|null
return|;
block|}
name|pageNum
operator|=
name|nextPageNum
expr_stmt|;
name|page
operator|=
name|db
operator|.
name|getDOMPage
argument_list|(
name|nextPageNum
argument_list|)
expr_stmt|;
name|db
operator|.
name|addToBuffer
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
comment|//Extract the tuple ID
name|lastTupleID
operator|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|page
operator|.
name|data
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|DOMFile
operator|.
name|LENGTH_TID
expr_stmt|;
comment|//Check if this is just a link to a relocated node
if|if
condition|(
name|ItemId
operator|.
name|isLink
argument_list|(
name|lastTupleID
argument_list|)
condition|)
block|{
comment|//Skip this
name|offset
operator|+=
name|DOMFile
operator|.
name|LENGTH_FORWARD_LOCATION
expr_stmt|;
comment|//Continue the iteration
continue|continue;
block|}
comment|//Read data length
name|short
name|vlen
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|page
operator|.
name|data
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|+=
name|DOMFile
operator|.
name|LENGTH_DATA_LENGTH
expr_stmt|;
if|if
condition|(
name|vlen
operator|<
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Got negative length"
operator|+
name|vlen
operator|+
literal|" at offset "
operator|+
name|offset
operator|+
literal|"!!!"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|db
operator|.
name|debugPageContents
argument_list|(
name|page
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception right now ?
block|}
if|if
condition|(
name|ItemId
operator|.
name|isRelocated
argument_list|(
name|lastTupleID
argument_list|)
condition|)
block|{
comment|//Found a relocated node. Read the original address
name|backLink
operator|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|page
operator|.
name|data
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|DOMFile
operator|.
name|LENGTH_ORIGINAL_LOCATION
expr_stmt|;
block|}
comment|//Overflow page? Load the overflow value
if|if
condition|(
name|vlen
operator|==
name|DOMFile
operator|.
name|OVERFLOW
condition|)
block|{
name|vlen
operator|=
name|DOMFile
operator|.
name|LENGTH_OVERFLOW_LOCATION
expr_stmt|;
specifier|final
name|long
name|overflow
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|page
operator|.
name|data
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|+=
name|DOMFile
operator|.
name|LENGTH_OVERFLOW_LOCATION
expr_stmt|;
try|try
block|{
specifier|final
name|byte
index|[]
name|overflowValue
init|=
name|db
operator|.
name|getOverflowValue
argument_list|(
name|overflow
argument_list|)
decl_stmt|;
name|nextNode
operator|=
name|StoredNode
operator|.
name|deserialize
argument_list|(
name|overflowValue
argument_list|,
literal|0
argument_list|,
name|overflowValue
operator|.
name|length
argument_list|,
name|doc
argument_list|,
name|useNodePool
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while loading overflow value: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"; originating page: "
operator|+
name|page
operator|.
name|page
operator|.
name|getPageInfo
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : rethrow exception ? -pb
block|}
comment|//Normal node
block|}
else|else
block|{
try|try
block|{
name|nextNode
operator|=
name|StoredNode
operator|.
name|deserialize
argument_list|(
name|page
operator|.
name|data
argument_list|,
name|offset
argument_list|,
name|vlen
argument_list|,
name|doc
argument_list|,
name|useNodePool
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|vlen
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while deserializing node: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Reading from offset: "
operator|+
name|offset
operator|+
literal|"; len = "
operator|+
name|vlen
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|db
operator|.
name|debugPageContents
argument_list|(
name|page
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|db
operator|.
name|debugPageContents
argument_list|(
name|page
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|nextNode
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"illegal node on page "
operator|+
name|page
operator|.
name|getPageNum
argument_list|()
operator|+
literal|"; tid = "
operator|+
name|ItemId
operator|.
name|getId
argument_list|(
name|lastTupleID
argument_list|)
operator|+
literal|"; next = "
operator|+
name|page
operator|.
name|getPageHeader
argument_list|()
operator|.
name|getNextDataPage
argument_list|()
operator|+
literal|"; prev = "
operator|+
name|page
operator|.
name|getPageHeader
argument_list|()
operator|.
name|getPreviousDataPage
argument_list|()
operator|+
literal|"; offset = "
operator|+
operator|(
name|offset
operator|-
name|vlen
operator|)
operator|+
literal|"; len = "
operator|+
name|page
operator|.
name|getPageHeader
argument_list|()
operator|.
name|getDataLength
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|db
operator|.
name|debugPageContents
argument_list|(
name|page
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception here ? -pb
return|return
literal|null
return|;
block|}
if|if
condition|(
name|ItemId
operator|.
name|isRelocated
argument_list|(
name|lastTupleID
argument_list|)
condition|)
block|{
name|nextNode
operator|.
name|setInternalAddress
argument_list|(
name|backLink
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nextNode
operator|.
name|setInternalAddress
argument_list|(
name|StorageAddress
operator|.
name|createPointer
argument_list|(
operator|(
name|int
operator|)
name|pageNum
argument_list|,
name|ItemId
operator|.
name|getId
argument_list|(
name|lastTupleID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|nextNode
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|nextNode
operator|==
literal|null
condition|)
do|;
block|}
return|return
name|nextNode
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|BTreeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : re-throw exception ? -pb
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : re-throw exception ? -pb
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
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
comment|//Position the iterator at the start of the first value
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|RecordPos
name|rec
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|StorageAddress
operator|.
name|hasAddress
argument_list|(
name|node
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
condition|)
block|{
name|rec
operator|=
name|db
operator|.
name|findRecord
argument_list|(
name|node
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rec
operator|==
literal|null
condition|)
block|{
specifier|final
name|long
name|addr
init|=
name|db
operator|.
name|findValue
argument_list|(
name|broker
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
block|{
return|return
literal|false
return|;
block|}
name|rec
operator|=
name|db
operator|.
name|findRecord
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
name|pageNum
operator|=
name|rec
operator|.
name|getPage
argument_list|()
operator|.
name|getPageNum
argument_list|()
expr_stmt|;
name|page
operator|=
name|rec
operator|.
name|getPage
argument_list|()
expr_stmt|;
comment|//Position the stream at the very beginning of the record
name|offset
operator|=
name|rec
operator|.
name|offset
operator|-
name|DOMFile
operator|.
name|LENGTH_TID
expr_stmt|;
name|node
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|StorageAddress
operator|.
name|hasAddress
argument_list|(
name|startAddress
argument_list|)
condition|)
block|{
specifier|final
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
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Node not found at specified address."
argument_list|)
throw|;
block|}
name|pageNum
operator|=
name|rec
operator|.
name|getPage
argument_list|()
operator|.
name|getPageNum
argument_list|()
expr_stmt|;
comment|//Position the stream at the very beginning of the record
name|offset
operator|=
name|rec
operator|.
name|offset
operator|-
name|DOMFile
operator|.
name|LENGTH_TID
expr_stmt|;
name|page
operator|=
name|rec
operator|.
name|getPage
argument_list|()
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
if|else if
condition|(
name|pageNum
operator|!=
name|Page
operator|.
name|NO_PAGE
condition|)
block|{
name|page
operator|=
name|db
operator|.
name|getDOMPage
argument_list|(
name|pageNum
argument_list|)
expr_stmt|;
name|db
operator|.
name|addToBuffer
argument_list|(
name|page
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Remove the current node. This implementation just      * decrements the node count. It does not actually remove      * the node's value, but removes a page if      * node count == 0. Use this method only if you want to      * delete an entire document, not to remove a single node.      */
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"remove() method not implemented"
argument_list|)
throw|;
block|}
comment|/**      *  Reposition the iterate at a given address.      *      *@param  address  The new to value      */
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
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|//nothing needs to be done
block|}
block|}
end_class

end_unit

