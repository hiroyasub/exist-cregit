begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|dbxml
operator|.
name|core
operator|.
name|filer
operator|.
name|BTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|dbxml
operator|.
name|core
operator|.
name|filer
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
name|NodeImpl
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

begin_comment
comment|/**  * Class NodeIterator is used to iterate over nodes in the DOM storage.  * This implementation locks the DOM file to read the node and unlocks  * it afterwards. It is thus safer than DOMFileIterator, since the node's  * value will not change.   *   * @author wolf  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NodeIterator
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
name|NodeProxy
name|node
init|=
literal|null
decl_stmt|;
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
name|lastTID
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|DOMFile
operator|.
name|DOMPage
name|p
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|page
decl_stmt|;
specifier|private
name|long
name|startAddress
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|Object
name|lockKey
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
name|Object
name|lock
parameter_list|,
name|DOMFile
name|db
parameter_list|,
name|NodeProxy
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
name|getDocument
argument_list|()
expr_stmt|;
name|this
operator|.
name|useNodePool
operator|=
name|poolable
expr_stmt|;
if|if
condition|(
operator|-
literal|1
operator|<
name|node
operator|.
name|getInternalAddress
argument_list|()
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
name|NodeIterator
parameter_list|(
name|Object
name|lock
parameter_list|,
name|DOMFile
name|db
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|address
parameter_list|)
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
name|doc
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
comment|/** 	 *  Returns the internal virtual address of the node at the iterator's 	 * current position. 	 * 	 *@return    The currentAddress value 	 */
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
name|ItemId
operator|.
name|getId
argument_list|(
name|lastTID
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	 *  Are there more nodes to be read? 	 * 	 *@return    Description of the Return Value 	 */
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
return|return
literal|false
return|;
block|}
name|db
operator|.
name|setOwnerObject
argument_list|(
name|lockKey
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
operator|<
literal|0
condition|)
return|return
literal|false
return|;
else|else
return|return
literal|true
return|;
block|}
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
comment|/** 	 *  Returns the next node in document order.  	 */
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
name|NodeImpl
name|nextNode
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
name|db
operator|.
name|setOwnerObject
argument_list|(
name|lockKey
argument_list|)
expr_stmt|;
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
name|boolean
name|skipped
init|=
literal|false
decl_stmt|;
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
operator|<
literal|0
condition|)
block|{
name|SanityCheck
operator|.
name|TRACE
argument_list|(
literal|"bad link to next "
operator|+
name|p
operator|.
name|page
operator|.
name|getPageInfo
argument_list|()
operator|+
literal|"; previous: "
operator|+
name|ph
operator|.
name|getPrevDataPage
argument_list|()
operator|+
literal|"; offset = "
operator|+
name|offset
operator|+
literal|"; lastTID = "
operator|+
name|lastTID
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
comment|//						LOG.debug(" -> " + nextPage + "; len = " + p.len + "; " + p.page.getPageInfo());
name|db
operator|.
name|addToBuffer
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
comment|// extract the tid
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
name|long
name|link
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
comment|//						System.out.println("skipping link on p " + page + " -> " +
comment|//								StorageAddress.pageFromPointer(link));
name|skipped
operator|=
literal|true
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
comment|// found a relocated node. Read the original address
name|backLink
operator|=
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
expr_stmt|;
name|offset
operator|+=
literal|8
expr_stmt|;
block|}
comment|//	overflow page? load the overflow value
if|if
condition|(
name|l
operator|==
name|DOMFile
operator|.
name|OVERFLOW
condition|)
block|{
comment|//					    LOG.warn("unexpected overflow page at " + p.getPageNum() + "; tid = " +
comment|//					            ItemId.getId(lastTID) + "; offset = " + offset + "; skipped = " + skipped);
name|l
operator|=
literal|8
expr_stmt|;
specifier|final
name|long
name|overflow
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
try|try
block|{
specifier|final
name|byte
index|[]
name|odata
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
name|NodeImpl
operator|.
name|deserialize
argument_list|(
name|odata
argument_list|,
literal|0
argument_list|,
name|odata
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
name|p
operator|.
name|page
operator|.
name|getPageInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// normal node
block|}
else|else
block|{
name|nextNode
operator|=
name|NodeImpl
operator|.
name|deserialize
argument_list|(
name|p
operator|.
name|data
argument_list|,
name|offset
argument_list|,
name|l
argument_list|,
name|doc
argument_list|,
name|useNodePool
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|l
expr_stmt|;
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
name|debug
argument_list|(
literal|"illegal node on page "
operator|+
name|p
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
name|lastTID
argument_list|)
operator|+
literal|"; next = "
operator|+
name|p
operator|.
name|getPageHeader
argument_list|()
operator|.
name|getNextDataPage
argument_list|()
operator|+
literal|"; prev = "
operator|+
name|p
operator|.
name|getPageHeader
argument_list|()
operator|.
name|getPrevDataPage
argument_list|()
operator|+
literal|"; offset = "
operator|+
operator|(
name|offset
operator|-
name|l
operator|)
operator|+
literal|"; len = "
operator|+
name|p
operator|.
name|getPageHeader
argument_list|()
operator|.
name|getDataLength
argument_list|()
argument_list|)
expr_stmt|;
comment|//					    LOG.debug(db.debugPageContents(p));
comment|//					    LOG.debug(p.dumpPage());
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
name|lastTID
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
name|nextNode
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|//					System.out.println("Next: " + nextNode.getNodeName() + " [" + page + "]");
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
name|BTreeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
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
operator|.
name|getMessage
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
argument_list|()
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
comment|//	position the iterator at the start of the first value
if|if
condition|(
name|node
operator|!=
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
name|lockKey
argument_list|,
name|node
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
if|else if
condition|(
operator|-
literal|1
operator|<
name|startAddress
condition|)
block|{
specifier|final
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
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Node not found at specified address."
argument_list|)
throw|;
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
operator|-
literal|1
expr_stmt|;
block|}
if|else if
condition|(
name|page
operator|>
operator|-
literal|1
condition|)
block|{
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
comment|//			LOG.debug("reading " + p.page.getPageNum() + "; " + p.page.hashCode());
block|}
else|else
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/** 	 * Remove the current node. This implementation just 	 * decrements the node count. It does not actually remove 	 * the node's value, but removes a page if 	 * node count == 0. Use this method only if you want to 	 * delete an entire document, not to remove a single node. 	 */
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
comment|/** 	 *  Reposition the iterator at the address of the proxy node. 	 * 	 *@param  node  The new to value 	 */
specifier|public
name|void
name|setTo
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
if|if
condition|(
operator|-
literal|1
operator|<
name|node
operator|.
name|getInternalAddress
argument_list|()
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
comment|/** 	 *  Reposition the iterate at a given address. 	 * 	 *@param  address  The new to value 	 */
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

