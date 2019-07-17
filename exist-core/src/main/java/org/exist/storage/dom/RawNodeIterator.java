begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|NodeHandle
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
name|LockManager
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
name|ManagedLock
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * An iterator that walks through the raw node data items in a document. The class  * keeps reading data items from the document's sequence of data pages until it encounters  * the end of the document. Each returned value contains the data of one node in the  * document.  */
end_comment

begin_class
specifier|public
class|class
name|RawNodeIterator
implements|implements
name|IRawNodeIterator
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
name|RawNodeIterator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|LockManager
name|lockManager
decl_stmt|;
specifier|private
specifier|final
name|DOMFile
name|db
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
comment|/**      * Construct the iterator. The iterator will be positioned before the specified      * start node.      *      * @param broker the owner object used to acquire a lock on the underlying data file (usually a DBBroker)      * @param db the underlying data file      * @param node the start node where the iterator will be positioned.      * @throws IOException if an I/O error occurs      */
specifier|public
name|RawNodeIterator
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DOMFile
name|db
parameter_list|,
specifier|final
name|NodeHandle
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|lockManager
operator|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getLockManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|seek
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|seek
parameter_list|(
specifier|final
name|NodeHandle
name|node
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
name|domFileLock
init|=
name|lockManager
operator|.
name|acquireBtreeReadLock
argument_list|(
name|db
operator|.
name|getLockName
argument_list|()
argument_list|)
init|)
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
try|try
block|{
specifier|final
name|long
name|address
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
name|address
operator|==
name|BTree
operator|.
name|KEY_NOT_FOUND
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Node not found."
argument_list|)
throw|;
block|}
name|rec
operator|=
name|db
operator|.
name|findRecord
argument_list|(
name|address
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
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
block|}
catch|catch
parameter_list|(
specifier|final
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
block|}
annotation|@
name|Override
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
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
name|domFileLock
init|=
name|lockManager
operator|.
name|acquireBtreeReadLock
argument_list|(
name|db
operator|.
name|getLockName
argument_list|()
argument_list|)
init|)
block|{
name|db
operator|.
name|setOwnerObject
argument_list|(
name|broker
argument_list|)
expr_stmt|;
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
name|nextPage
init|=
name|pageHeader
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
literal|"Bad link to next page "
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
comment|//TODO : throw exception here ? -pb
return|return
literal|null
return|;
block|}
name|pageNum
operator|=
name|nextPage
expr_stmt|;
name|page
operator|=
name|db
operator|.
name|getDOMPage
argument_list|(
name|nextPage
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
comment|//Extract the tuple id
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
continue|continue;
block|}
comment|//Read data length
name|short
name|valueLength
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
name|valueLength
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
name|valueLength
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
comment|// found a relocated node. Read the original address
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
comment|//Overflow page? load the overflow value
if|if
condition|(
name|valueLength
operator|==
name|DOMFile
operator|.
name|OVERFLOW
condition|)
block|{
name|valueLength
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
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
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
name|page
operator|.
name|data
argument_list|,
name|offset
argument_list|,
name|valueLength
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|valueLength
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
name|valueLength
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
name|error
argument_list|(
literal|"illegal node on page "
operator|+
name|page
operator|.
name|getPageNum
argument_list|()
operator|+
literal|"; tupleID = "
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
name|valueLength
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
comment|//TODO : throw exception here ? -pb
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
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
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
block|}
end_class

end_unit

