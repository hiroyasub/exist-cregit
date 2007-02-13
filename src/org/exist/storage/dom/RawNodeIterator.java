begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2007 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

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
name|Paged
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
name|dom
operator|.
name|NodeProxy
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

begin_comment
comment|/**  * An iterator that walks through the raw node data items in a document. The class  * keeps reading data items from the document's sequence of data pages until it encounters  * the end of the document. Each returned value contains the data of one node in the  * document.  */
end_comment

begin_class
specifier|public
class|class
name|RawNodeIterator
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
name|RawNodeIterator
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
name|int
name|offset
decl_stmt|;
specifier|private
name|short
name|lastTID
init|=
name|ItemId
operator|.
name|UNKNOWN_ID
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
name|Object
name|lockKey
decl_stmt|;
comment|/**      * Construct the iterator. The iterator will be positioned before the specified      * start node.      *      * @param lockKey the owner object used to acquire a lock on the underlying data file (usually a DBBroker)      * @param db the underlying data file      * @param node the start node where the iterator will be positioned.      * @throws IOException      */
specifier|public
name|RawNodeIterator
parameter_list|(
name|Object
name|lockKey
parameter_list|,
name|DOMFile
name|db
parameter_list|,
name|StoredNode
name|node
parameter_list|)
throws|throws
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
name|lockKey
operator|=
operator|(
name|lockKey
operator|==
literal|null
condition|?
name|this
else|:
name|lockKey
operator|)
expr_stmt|;
name|seek
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct the iterator. The iterator will be positioned before the specified      * start node.      *      * @param lockKey the owner object used to acquire a lock on the underlying data file (usually a DBBroker)      * @param db the underlying data file      * @param proxy the start node where the iterator will be positioned.      * @throws IOException      */
specifier|public
name|RawNodeIterator
parameter_list|(
name|Object
name|lockKey
parameter_list|,
name|DOMFile
name|db
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
throws|throws
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
name|lockKey
operator|=
operator|(
name|lockKey
operator|==
literal|null
condition|?
name|this
else|:
name|lockKey
operator|)
expr_stmt|;
name|seek
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reposition the iterator to the start of the specified node.      *      * @param node the start node where the iterator will be positioned.      * @throws IOException      */
specifier|public
name|void
name|seek
parameter_list|(
name|StoredNode
name|node
parameter_list|)
throws|throws
name|IOException
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
name|READ_LOCK
argument_list|)
expr_stmt|;
name|RecordPos
name|rec
init|=
literal|null
decl_stmt|;
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
if|if
condition|(
name|rec
operator|==
literal|null
condition|)
block|{
try|try
block|{
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Node not found."
argument_list|)
throw|;
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
catch|catch
parameter_list|(
name|BTreeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Node not found: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|page
operator|=
name|rec
operator|.
name|getPage
argument_list|()
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
name|getPage
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception while scanning document: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
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
comment|/**      * Reposition the iterator to the start of the specified node.      *      * @param proxy the start node where the iterator will be positioned.      * @throws IOException      */
specifier|public
name|void
name|seek
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
throws|throws
name|IOException
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
name|READ_LOCK
argument_list|)
expr_stmt|;
name|RecordPos
name|rec
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|proxy
operator|.
name|getInternalAddress
argument_list|()
operator|!=
name|StoredNode
operator|.
name|UNKNOWN_NODE_IMPL_ADDRESS
condition|)
name|rec
operator|=
name|db
operator|.
name|findRecord
argument_list|(
name|proxy
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|rec
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|long
name|addr
init|=
name|db
operator|.
name|findValue
argument_list|(
name|lockKey
argument_list|,
name|proxy
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Node not found."
argument_list|)
throw|;
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
catch|catch
parameter_list|(
name|BTreeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Node not found: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|page
operator|=
name|rec
operator|.
name|getPage
argument_list|()
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
name|getPage
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception while scanning document: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
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
comment|/** 	 *  Returns the raw data of the next node in document order.      * @return the raw data of the node      */
specifier|public
name|Value
name|next
parameter_list|()
block|{
name|Value
name|nextValue
init|=
literal|null
decl_stmt|;
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
name|db
operator|.
name|setOwnerObject
argument_list|(
name|lockKey
argument_list|)
expr_stmt|;
name|long
name|backLink
init|=
literal|0
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
operator|==
name|Paged
operator|.
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
comment|//LOG.debug(" -> " + nextPage + "; len = " + p.len + "; " + p.page.getPageInfo());
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
name|DOMFile
operator|.
name|LENGTH_TID
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
name|DOMFile
operator|.
name|LENGTH_FORWARD_LOCATION
expr_stmt|;
comment|//System.out.println("skipping link on p " + page + " -> " +
comment|//StorageAddress.pageFromPointer(link));
comment|//continue the iteration
continue|continue;
block|}
comment|// read data length
name|short
name|vlen
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
name|warn
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
name|p
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
name|DOMFile
operator|.
name|LENGTH_ORIGINAL_LOCATION
expr_stmt|;
block|}
comment|//	overflow page? load the overflow value
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
name|p
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
name|odata
init|=
name|db
operator|.
name|getOverflowValue
argument_list|(
name|overflow
argument_list|)
decl_stmt|;
name|nextValue
operator|=
operator|new
name|Value
argument_list|(
name|odata
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
try|try
block|{
name|nextValue
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
name|vlen
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|vlen
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
name|warn
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
name|p
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
name|nextValue
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
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
name|vlen
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
comment|//LOG.debug(db.debugPageContents(p));
comment|//LOG.debug(p.dumpPage());
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
name|nextValue
operator|.
name|setAddress
argument_list|(
name|backLink
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nextValue
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
comment|//YES ! needed because of the continue statement above
block|}
do|while
condition|(
name|nextValue
operator|==
literal|null
condition|)
do|;
return|return
name|nextValue
return|;
block|}
finally|finally
block|{
name|lock
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
specifier|public
name|void
name|closeDocument
parameter_list|()
block|{
name|db
operator|.
name|closeDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the internal virtual storage address of the node at the cursor's current      * position.      *      * @return internal virtual storage address of the node      */
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
block|}
end_class

end_unit

