begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|data
operator|.
name|Value
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
name|NodeProxy
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
name|DOMFile
operator|.
name|DOMFilePageHeader
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
name|DOMFile
operator|.
name|DOMPage
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

begin_comment
comment|/**  * Class DOMFileIterator is used to iterate over nodes in the DOM storage.  * This implementation returns the raw value of the node. You have to call  * Node.deserialize() to read the node from the value data.  *   * The DOM file is locked to locate the data and released afterwards. Before  * working with the returned data, you should get a copy by calling value.getData().   *   * @author wolf  */
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
name|NodeProxy
name|node
init|=
literal|null
decl_stmt|;
name|int
name|offset
decl_stmt|;
name|short
name|lastTID
init|=
operator|-
literal|1
decl_stmt|;
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
operator|-
literal|1
decl_stmt|;
name|Object
name|lockKey
decl_stmt|;
comment|/** 		 *  Constructor for the DOMFileIterator object 		 * 		 *@param  doc                 Description of the Parameter 		 *@param  db                  Description of the Parameter 		 *@param  node                Description of the Parameter 		 *@exception  BTreeException  Description of the Exception 		 *@exception  IOException     Description of the Exception 		 */
specifier|public
name|DOMFileIterator
parameter_list|(
name|Object
name|lock
parameter_list|,
name|DOMFile
name|db
parameter_list|,
name|NodeProxy
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
operator|-
literal|1
operator|<
name|node
operator|.
name|internalAddress
condition|)
name|startAddress
operator|=
name|node
operator|.
name|internalAddress
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
comment|/** 		 *  Constructor for the DOMFileIterator object 		 * 		 *@param  doc                 Description of the Parameter 		 *@param  db                  Description of the Parameter 		 *@param  address             Description of the Parameter 		 *@exception  BTreeException  Description of the Exception 		 *@exception  IOException     Description of the Exception 		 */
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
name|DOMFile
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
return|return
literal|false
return|;
block|}
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
specifier|final
name|DOMFile
operator|.
name|RecordPos
name|rec
init|=
name|db
operator|.
name|findValuePosition
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
name|findValuePosition
argument_list|(
name|startAddress
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
block|}
if|else if
condition|(
name|page
operator|>
operator|-
literal|1
condition|)
name|p
operator|=
name|db
operator|.
name|getCurrentPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
else|else
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
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
name|DOMFilePageHeader
name|ph
init|=
name|p
operator|.
name|getPageHeader
argument_list|()
decl_stmt|;
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
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
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
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
return|return
literal|null
return|;
block|}
comment|// position the iterator at the start of the first value
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
literal|null
return|;
name|DOMFile
operator|.
name|RecordPos
name|rec
init|=
name|db
operator|.
name|findValuePosition
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
name|findValuePosition
argument_list|(
name|startAddress
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
name|p
operator|=
name|db
operator|.
name|getCurrentPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
else|else
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|final
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
operator|<
literal|0
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
name|lock
operator|.
name|release
argument_list|()
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
block|}
comment|// extract the value
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
specifier|final
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
operator|+
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|Value
name|nextVal
init|=
operator|new
name|Value
argument_list|(
name|p
operator|.
name|data
argument_list|,
name|offset
operator|+
literal|4
argument_list|,
name|l
argument_list|)
decl_stmt|;
name|nextVal
operator|.
name|setAddress
argument_list|(
name|DOMFile
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
argument_list|)
expr_stmt|;
name|offset
operator|=
name|offset
operator|+
literal|4
operator|+
name|l
expr_stmt|;
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
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
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/** 		 * Remove the current node. This implementation just 		 * decrements the node count. It does not actually remove 		 * the node's value, but removes a page if 		 * node count == 0. Use this method only if you want to 		 * delete an entire document, not to remove a single node. 		 */
specifier|public
name|void
name|remove
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
name|lock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|DOMPage
name|p
init|=
literal|null
decl_stmt|;
name|p
operator|=
name|db
operator|.
name|getCurrentPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|DOMFilePageHeader
name|ph
init|=
name|p
operator|.
name|getPageHeader
argument_list|()
decl_stmt|;
name|ph
operator|.
name|decRecordCount
argument_list|()
expr_stmt|;
name|p
operator|.
name|setDirty
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|ph
operator|.
name|getRecordCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|long
name|np
init|=
name|ph
operator|.
name|getNextDataPage
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|np
operator|>
operator|-
literal|1
condition|)
block|{
name|DOMPage
name|next
init|=
name|db
operator|.
name|getCurrentPage
argument_list|(
name|np
argument_list|)
decl_stmt|;
name|next
operator|.
name|getPageHeader
argument_list|()
operator|.
name|prevDataPage
operator|=
operator|-
literal|1
expr_stmt|;
name|db
operator|.
name|getPageBuffer
argument_list|()
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|ph
operator|.
name|setNextDataPage
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|ph
operator|.
name|setPrevDataPage
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|ph
operator|.
name|setDataLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//ph.setNextTID((short)0);
name|ph
operator|.
name|setRecordCount
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
name|p
operator|.
name|setDirty
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|db
operator|.
name|getPageBuffer
argument_list|()
operator|.
name|remove
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|db
operator|.
name|unlinkPages
argument_list|(
name|p
operator|.
name|page
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
block|}
name|page
operator|=
name|np
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
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
block|}
comment|/** 		 *  Reposition the iterator at the address of the proxy node. 		 * 		 *@param  node  The new to value 		 */
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
name|internalAddress
condition|)
block|{
name|startAddress
operator|=
name|node
operator|.
name|internalAddress
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

