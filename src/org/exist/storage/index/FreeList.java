begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|util
operator|.
name|ByteConversion
import|;
end_import

begin_comment
comment|/**  * Manages a list of pages containing unused sections.  *   * Class {@link org.exist.storage.index.BFile} stores all data in variable  * length records. As records may grow or shrink, the database has to keep  * track of the amount of free space currently available in pages. Class   * {@link org.exist.storage.index.BFile} will always check if FreeList has a page  * that can be filled before creating a new page.  *   * FreeList implements a linked list of {@link FreeSpace} objects. Each object  * in the list describes a page and the unused space in this page.  *   * @see FreeList  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FreeList
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
name|FreeList
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MAX_FREE_LIST_LEN
init|=
literal|128
decl_stmt|;
specifier|protected
name|FreeSpace
name|header
init|=
literal|null
decl_stmt|;
specifier|protected
name|FreeSpace
name|last
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|size
init|=
literal|0
decl_stmt|;
specifier|public
name|FreeList
parameter_list|()
block|{
block|}
comment|/** 	 * Append a new {@link FreeSpace} object to the list, 	 * describing the amount of free space available on a page. 	 *   	 * @param free 	 */
specifier|public
name|void
name|add
parameter_list|(
name|FreeSpace
name|free
parameter_list|)
block|{
if|if
condition|(
name|header
operator|==
literal|null
condition|)
block|{
name|header
operator|=
name|free
expr_stmt|;
name|last
operator|=
name|free
expr_stmt|;
block|}
else|else
block|{
name|last
operator|.
name|next
operator|=
name|free
expr_stmt|;
name|free
operator|.
name|previous
operator|=
name|last
expr_stmt|;
name|last
operator|=
name|free
expr_stmt|;
block|}
operator|++
name|size
expr_stmt|;
block|}
comment|/** 	 * Remove a record from the list. 	 *  	 * @param node 	 */
specifier|public
name|void
name|remove
parameter_list|(
name|FreeSpace
name|node
parameter_list|)
block|{
operator|--
name|size
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|previous
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|next
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|next
operator|.
name|previous
operator|=
literal|null
expr_stmt|;
name|header
operator|=
name|node
operator|.
name|next
expr_stmt|;
block|}
else|else
name|header
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|previous
operator|.
name|next
operator|=
name|node
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|next
operator|!=
literal|null
condition|)
name|node
operator|.
name|next
operator|.
name|previous
operator|=
name|node
operator|.
name|previous
expr_stmt|;
else|else
name|last
operator|=
name|node
operator|.
name|previous
expr_stmt|;
block|}
block|}
comment|/** 	 * Retrieve the record stored for the given page number. 	 *  	 * @param pageNum 	 */
specifier|public
name|FreeSpace
name|retrieve
parameter_list|(
name|long
name|pageNum
parameter_list|)
block|{
name|FreeSpace
name|next
init|=
name|header
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|page
operator|==
name|pageNum
condition|)
return|return
name|next
return|;
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/** 	 * Try to find a page that has at least requiredSize bytes 	 * available. This method selects the page with the smallest 	 * possible space. This guarantees that all pages will be filled before 	 * creating a new page.  	 *  	 * @param requiredSize 	 */
specifier|public
name|FreeSpace
name|find
parameter_list|(
name|int
name|requiredSize
parameter_list|)
block|{
name|FreeSpace
name|next
init|=
name|header
decl_stmt|;
name|FreeSpace
name|found
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|free
operator|>=
name|requiredSize
condition|)
block|{
if|if
condition|(
name|found
operator|==
literal|null
operator|||
name|next
operator|.
name|free
operator|<
name|found
operator|.
name|free
condition|)
name|found
operator|=
name|next
expr_stmt|;
block|}
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
block|}
return|return
name|found
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|FreeSpace
name|next
init|=
name|header
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|next
operator|.
name|page
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|free
argument_list|)
operator|.
name|append
argument_list|(
literal|"] "
argument_list|)
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Read the list from a {@link RandomAccessFile}.      *       *       * @param buf       * @param offset       * @throws IOException       */
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|fsize
init|=
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|long
name|page
decl_stmt|;
name|int
name|space
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fsize
condition|;
name|i
operator|++
control|)
block|{
name|page
operator|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|8
expr_stmt|;
name|space
operator|=
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|add
argument_list|(
operator|new
name|FreeSpace
argument_list|(
name|page
argument_list|,
name|space
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|offset
return|;
block|}
comment|/**      * Write the list to a {@link RandomAccessFile}.      *       * As the list is written to the file header, its maximum length      * has to be restricted. The method will thus only store      * {@link #MAX_FREE_LIST_LEN} entries and throw away the       * rest. Usually, this should not happen very often, so it is ok to      * waste some space.      *       *       * @param buf       * @param offset       * @throws IOException       */
specifier|public
name|int
name|write
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
comment|//       does the free-space list fit into the file header?
name|int
name|skip
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|MAX_FREE_LIST_LEN
condition|)
block|{
comment|//            LOG.warn("removing " + (size - MAX_FREE_LIST_LEN)
comment|//                    + " free pages.");
comment|// no: remove some smaller entries to make it fit
name|skip
operator|=
name|size
operator|-
name|MAX_FREE_LIST_LEN
expr_stmt|;
block|}
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|size
operator|-
name|skip
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|FreeSpace
name|next
init|=
name|header
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|skip
operator|==
literal|0
condition|)
block|{
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|next
operator|.
name|page
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|8
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|next
operator|.
name|free
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
block|}
else|else
operator|--
name|skip
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
block|}
return|return
name|offset
return|;
block|}
block|}
end_class

end_unit

