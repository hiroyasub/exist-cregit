begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id:  */
end_comment

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
name|EOFException
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Map
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
name|Category
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
name|DBException
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
name|dom
operator|.
name|QName
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
name|XMLUtil
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
name|store
operator|.
name|*
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
name|FastQSort
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
name|ProgressIndicator
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
name|ReadOnlyException
import|;
end_import

begin_comment
comment|//import org.exist.util.StorageAddress;
end_comment

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|VariableByteInputStream
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
name|VariableByteOutputStream
import|;
end_import

begin_class
specifier|public
class|class
name|NativeElementIndex
extends|extends
name|ElementIndex
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|NativeElementIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PARTITION_SIZE
init|=
literal|102400
decl_stmt|;
specifier|protected
name|BFile
name|dbElement
decl_stmt|;
specifier|private
name|VariableByteOutputStream
name|os
init|=
operator|new
name|VariableByteOutputStream
argument_list|()
decl_stmt|;
specifier|public
name|NativeElementIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|BFile
name|dbElement
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|dbElement
operator|=
name|dbElement
expr_stmt|;
block|}
specifier|public
name|void
name|addRow
parameter_list|(
name|QName
name|qname
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|ArrayList
name|buf
decl_stmt|;
if|if
condition|(
name|elementIds
operator|.
name|containsKey
argument_list|(
name|qname
argument_list|)
condition|)
name|buf
operator|=
operator|(
name|ArrayList
operator|)
name|elementIds
operator|.
name|get
argument_list|(
name|qname
argument_list|)
expr_stmt|;
else|else
block|{
name|buf
operator|=
operator|new
name|ArrayList
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|elementIds
operator|.
name|put
argument_list|(
name|qname
argument_list|,
name|buf
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|reindex
parameter_list|(
name|DocumentImpl
name|oldDoc
parameter_list|,
name|NodeImpl
name|node
parameter_list|)
block|{
if|if
condition|(
name|elementIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|Lock
name|lock
init|=
name|dbElement
operator|.
name|getLock
argument_list|()
decl_stmt|;
name|Map
operator|.
name|Entry
name|entry
decl_stmt|;
name|QName
name|qname
decl_stmt|;
name|List
name|oldList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|,
name|idList
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|VariableByteInputStream
name|is
init|=
operator|new
name|VariableByteInputStream
argument_list|()
decl_stmt|;
name|InputStream
name|dis
init|=
literal|null
decl_stmt|;
name|int
name|len
decl_stmt|,
name|docId
decl_stmt|;
name|byte
index|[]
name|data
decl_stmt|;
name|Value
name|ref
decl_stmt|;
name|Value
name|val
decl_stmt|;
name|short
name|sym
decl_stmt|,
name|nsSym
decl_stmt|;
name|short
name|collectionId
init|=
name|oldDoc
operator|.
name|getCollection
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|long
name|delta
decl_stmt|,
name|last
decl_stmt|,
name|gid
decl_stmt|,
name|address
decl_stmt|;
try|try
block|{
comment|// iterate through elements
for|for
control|(
name|Iterator
name|i
init|=
name|elementIds
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|idList
operator|=
operator|(
name|ArrayList
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|qname
operator|=
operator|(
name|QName
operator|)
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|qname
operator|.
name|getNameType
argument_list|()
operator|!=
name|ElementValue
operator|.
name|ATTRIBUTE_ID
condition|)
block|{
name|sym
operator|=
name|DBBroker
operator|.
name|getSymbols
argument_list|()
operator|.
name|getSymbol
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|nsSym
operator|=
name|DBBroker
operator|.
name|getSymbols
argument_list|()
operator|.
name|getNSSymbol
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|ref
operator|=
operator|new
name|ElementValue
argument_list|(
name|qname
operator|.
name|getNameType
argument_list|()
argument_list|,
name|collectionId
argument_list|,
name|sym
argument_list|,
name|nsSym
argument_list|)
expr_stmt|;
block|}
else|else
name|ref
operator|=
operator|new
name|ElementValue
argument_list|(
name|qname
operator|.
name|getNameType
argument_list|()
argument_list|,
name|collectionId
argument_list|,
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
comment|// try to retrieve old index entry for the element
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
comment|//val = dbElement.get(ref);
name|dis
operator|=
name|dbElement
operator|.
name|getAsStream
argument_list|(
name|ref
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
name|error
argument_list|(
literal|"could not acquire lock for index on "
operator|+
name|qname
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"io error while reindexing "
operator|+
name|qname
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|dis
operator|=
literal|null
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
name|os
operator|.
name|clear
argument_list|()
expr_stmt|;
name|oldList
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|dis
operator|!=
literal|null
condition|)
block|{
comment|// add old entries to the new list
comment|//data = val.getData();
name|is
operator|.
name|setInputStream
argument_list|(
name|dis
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|is
operator|.
name|available
argument_list|()
operator|>
literal|0
condition|)
block|{
name|docId
operator|=
name|is
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|len
operator|=
name|is
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|docId
operator|!=
name|oldDoc
operator|.
name|getDocId
argument_list|()
condition|)
block|{
comment|// section belongs to another document:
comment|// copy data to new buffer
name|os
operator|.
name|writeInt
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|is
operator|.
name|copyTo
argument_list|(
name|os
argument_list|,
name|len
operator|*
literal|4
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// copy nodes to new list
name|last
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|delta
operator|=
name|is
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|gid
operator|=
name|last
operator|+
name|delta
expr_stmt|;
name|last
operator|=
name|gid
expr_stmt|;
comment|//address = is.readFixedLong();
name|address
operator|=
name|StorageAddress
operator|.
name|read
argument_list|(
name|is
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
operator|&&
name|oldDoc
operator|.
name|getTreeLevel
argument_list|(
name|gid
argument_list|)
operator|<
name|oldDoc
operator|.
name|reindexRequired
argument_list|()
condition|)
block|{
name|idList
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|oldDoc
argument_list|,
name|gid
argument_list|,
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|node
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|XMLUtil
operator|.
name|isDescendantOrSelf
argument_list|(
name|oldDoc
argument_list|,
name|node
operator|.
name|getGID
argument_list|()
argument_list|,
name|gid
argument_list|)
operator|)
condition|)
block|{
name|oldList
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|oldDoc
argument_list|,
name|gid
argument_list|,
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"io-error while updating index for element "
operator|+
name|qname
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
name|idList
operator|.
name|addAll
argument_list|(
name|oldList
argument_list|)
expr_stmt|;
comment|// write out the updated list
name|FastQSort
operator|.
name|sort
argument_list|(
name|idList
argument_list|,
literal|0
argument_list|,
name|idList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|len
operator|=
name|idList
operator|.
name|size
argument_list|()
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|last
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|idList
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|delta
operator|=
name|p
operator|.
name|gid
operator|-
name|last
expr_stmt|;
name|last
operator|=
name|p
operator|.
name|gid
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|StorageAddress
operator|.
name|write
argument_list|(
name|p
operator|.
name|getInternalAddress
argument_list|()
argument_list|,
name|os
argument_list|)
expr_stmt|;
comment|//os.writeFixedLong(p.getInternalAddress());
block|}
comment|//data = os.toByteArray();
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
name|dis
operator|==
literal|null
condition|)
name|dbElement
operator|.
name|put
argument_list|(
name|ref
argument_list|,
name|os
operator|.
name|data
argument_list|()
argument_list|)
expr_stmt|;
else|else
block|{
name|address
operator|=
operator|(
operator|(
name|BFile
operator|.
name|PageInputStream
operator|)
name|dis
operator|)
operator|.
name|getAddress
argument_list|()
expr_stmt|;
name|dbElement
operator|.
name|update
argument_list|(
name|address
argument_list|,
name|ref
argument_list|,
name|os
operator|.
name|data
argument_list|()
argument_list|)
expr_stmt|;
comment|//dbElement.update(val.getAddress(), ref, data);
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
name|error
argument_list|(
literal|"could not acquire lock on elements"
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
block|}
block|}
catch|catch
parameter_list|(
name|ReadOnlyException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"database is read only"
argument_list|)
expr_stmt|;
block|}
name|elementIds
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|elementIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|Lock
name|lock
init|=
name|dbElement
operator|.
name|getLock
argument_list|()
decl_stmt|;
name|Map
operator|.
name|Entry
name|entry
decl_stmt|;
name|QName
name|qname
decl_stmt|;
name|List
name|newList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|,
name|idList
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|VariableByteInputStream
name|is
decl_stmt|;
name|int
name|len
decl_stmt|,
name|docId
decl_stmt|;
name|byte
index|[]
name|data
decl_stmt|;
name|Value
name|ref
decl_stmt|;
name|Value
name|val
decl_stmt|;
name|short
name|sym
decl_stmt|,
name|nsSym
decl_stmt|;
name|short
name|collectionId
init|=
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|long
name|delta
decl_stmt|,
name|last
decl_stmt|,
name|gid
decl_stmt|,
name|address
decl_stmt|;
try|try
block|{
comment|// iterate through elements
for|for
control|(
name|Iterator
name|i
init|=
name|elementIds
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|idList
operator|=
operator|(
name|ArrayList
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|qname
operator|=
operator|(
name|QName
operator|)
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|qname
operator|.
name|getNameType
argument_list|()
operator|!=
name|ElementValue
operator|.
name|ATTRIBUTE_ID
condition|)
block|{
name|sym
operator|=
name|DBBroker
operator|.
name|getSymbols
argument_list|()
operator|.
name|getSymbol
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|nsSym
operator|=
name|DBBroker
operator|.
name|getSymbols
argument_list|()
operator|.
name|getNSSymbol
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|ref
operator|=
operator|new
name|ElementValue
argument_list|(
name|qname
operator|.
name|getNameType
argument_list|()
argument_list|,
name|collectionId
argument_list|,
name|sym
argument_list|,
name|nsSym
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ref
operator|=
operator|new
name|ElementValue
argument_list|(
name|qname
operator|.
name|getNameType
argument_list|()
argument_list|,
name|collectionId
argument_list|,
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// try to retrieve old index entry for the element
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
name|val
operator|=
name|dbElement
operator|.
name|get
argument_list|(
name|ref
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
name|error
argument_list|(
literal|"could not acquire lock for index on "
operator|+
name|qname
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
name|os
operator|.
name|clear
argument_list|()
expr_stmt|;
name|newList
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
comment|// add old entries to the new list
name|data
operator|=
name|val
operator|.
name|getData
argument_list|()
expr_stmt|;
name|is
operator|=
operator|new
name|VariableByteInputStream
argument_list|(
name|data
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|is
operator|.
name|available
argument_list|()
operator|>
literal|0
condition|)
block|{
name|docId
operator|=
name|is
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|len
operator|=
name|is
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|docId
operator|!=
name|doc
operator|.
name|getDocId
argument_list|()
condition|)
block|{
comment|// section belongs to another document:
comment|// copy data to new buffer
name|os
operator|.
name|writeInt
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
operator|*
literal|4
condition|;
name|j
operator|++
control|)
block|{
name|is
operator|.
name|copyTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// copy nodes to new list
name|last
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|delta
operator|=
name|is
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|gid
operator|=
name|last
operator|+
name|delta
expr_stmt|;
name|last
operator|=
name|gid
expr_stmt|;
name|address
operator|=
name|StorageAddress
operator|.
name|read
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|//address = is.readFixedLong();
if|if
condition|(
operator|!
name|containsNode
argument_list|(
name|idList
argument_list|,
name|gid
argument_list|)
condition|)
block|{
name|newList
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"end-of-file while updating index for element "
operator|+
name|qname
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
name|error
argument_list|(
literal|"io-error while updating index for element "
operator|+
name|qname
argument_list|)
expr_stmt|;
block|}
block|}
comment|// write out the updated list
name|FastQSort
operator|.
name|sort
argument_list|(
name|newList
argument_list|,
literal|0
argument_list|,
name|newList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|len
operator|=
name|newList
operator|.
name|size
argument_list|()
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|last
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|newList
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|delta
operator|=
name|p
operator|.
name|gid
operator|-
name|last
expr_stmt|;
name|last
operator|=
name|p
operator|.
name|gid
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|StorageAddress
operator|.
name|write
argument_list|(
name|p
operator|.
name|getInternalAddress
argument_list|()
argument_list|,
name|os
argument_list|)
expr_stmt|;
comment|//os.writeFixedLong(p.getInternalAddress());
block|}
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
name|val
operator|==
literal|null
condition|)
name|dbElement
operator|.
name|put
argument_list|(
name|ref
argument_list|,
name|os
operator|.
name|data
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|dbElement
operator|.
name|update
argument_list|(
name|val
operator|.
name|getAddress
argument_list|()
argument_list|,
name|ref
argument_list|,
name|os
operator|.
name|data
argument_list|()
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
name|error
argument_list|(
literal|"could not acquire lock on elements"
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
block|}
block|}
catch|catch
parameter_list|(
name|ReadOnlyException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"database is read only"
argument_list|)
expr_stmt|;
block|}
name|elementIds
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|boolean
name|containsNode
parameter_list|(
name|List
name|list
parameter_list|,
name|long
name|gid
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|(
operator|(
name|NodeProxy
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|gid
operator|==
name|gid
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
if|if
condition|(
name|elementIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
specifier|final
name|ProgressIndicator
name|progress
init|=
operator|new
name|ProgressIndicator
argument_list|(
name|elementIds
operator|.
name|size
argument_list|()
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|NodeProxy
name|proxy
decl_stmt|;
name|QName
name|qname
decl_stmt|;
name|ArrayList
name|idList
decl_stmt|;
name|int
name|count
init|=
literal|1
decl_stmt|,
name|len
decl_stmt|;
name|byte
index|[]
name|data
decl_stmt|;
name|String
name|name
decl_stmt|;
name|ElementValue
name|ref
decl_stmt|;
name|Map
operator|.
name|Entry
name|entry
decl_stmt|;
comment|// get collection id for this collection
specifier|final
name|String
name|docName
init|=
name|doc
operator|.
name|getFileName
argument_list|()
decl_stmt|;
name|long
name|prevId
decl_stmt|;
name|long
name|cid
decl_stmt|;
name|long
name|addr
decl_stmt|;
name|short
name|collectionId
init|=
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Lock
name|lock
init|=
name|dbElement
operator|.
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|elementIds
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|qname
operator|=
operator|(
name|QName
operator|)
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|idList
operator|=
operator|(
name|ArrayList
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|os
operator|.
name|clear
argument_list|()
expr_stmt|;
name|FastQSort
operator|.
name|sort
argument_list|(
name|idList
argument_list|,
literal|0
argument_list|,
name|idList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|len
operator|=
name|idList
operator|.
name|size
argument_list|()
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|prevId
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|proxy
operator|=
operator|(
name|NodeProxy
operator|)
name|idList
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|cid
operator|=
name|proxy
operator|.
name|gid
operator|-
name|prevId
expr_stmt|;
name|prevId
operator|=
name|proxy
operator|.
name|gid
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|cid
argument_list|)
expr_stmt|;
comment|//os.writeFixedLong(proxy.getInternalAddress());
name|StorageAddress
operator|.
name|write
argument_list|(
name|proxy
operator|.
name|getInternalAddress
argument_list|()
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|qname
operator|.
name|getNameType
argument_list|()
operator|!=
name|ElementValue
operator|.
name|ATTRIBUTE_ID
condition|)
block|{
name|short
name|sym
init|=
name|NativeBroker
operator|.
name|getSymbols
argument_list|()
operator|.
name|getSymbol
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
decl_stmt|;
name|short
name|nsSym
init|=
name|NativeBroker
operator|.
name|getSymbols
argument_list|()
operator|.
name|getNSSymbol
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|ref
operator|=
operator|new
name|ElementValue
argument_list|(
name|qname
operator|.
name|getNameType
argument_list|()
argument_list|,
name|collectionId
argument_list|,
name|sym
argument_list|,
name|nsSym
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ref
operator|=
operator|new
name|ElementValue
argument_list|(
name|qname
operator|.
name|getNameType
argument_list|()
argument_list|,
name|collectionId
argument_list|,
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|dbElement
operator|.
name|append
argument_list|(
name|ref
argument_list|,
name|os
operator|.
name|data
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"could not save index for element "
operator|+
name|qname
argument_list|)
expr_stmt|;
continue|continue;
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
name|error
argument_list|(
literal|"could not acquire lock on elements"
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
name|error
argument_list|(
literal|"io error while writing element "
operator|+
name|qname
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
name|progress
operator|.
name|setValue
argument_list|(
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|progress
operator|.
name|changed
argument_list|()
condition|)
block|{
name|setChanged
argument_list|()
expr_stmt|;
name|notifyObservers
argument_list|(
name|progress
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ReadOnlyException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"database is read-only"
argument_list|)
expr_stmt|;
return|return;
block|}
name|progress
operator|.
name|finish
argument_list|()
expr_stmt|;
name|setChanged
argument_list|()
expr_stmt|;
name|notifyObservers
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|elementIds
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|sync
parameter_list|()
block|{
name|Lock
name|lock
init|=
name|dbElement
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
try|try
block|{
name|dbElement
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|dbe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|dbe
argument_list|)
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
literal|"could not acquire lock for elements"
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
block|}
block|}
end_class

end_unit

