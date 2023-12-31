begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterInputStream
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

begin_comment
comment|/**  *  * @author zwobit  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractFilterInputStreamCache
extends|extends
name|FilterInputStream
implements|implements
name|FilterInputStreamCache
block|{
specifier|private
name|int
name|sharedReferenceCount
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|srcOffset
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|InputStream
name|src
decl_stmt|;
specifier|private
name|boolean
name|srcClosed
init|=
literal|false
decl_stmt|;
specifier|public
name|AbstractFilterInputStreamCache
parameter_list|(
name|InputStream
name|src
parameter_list|)
block|{
name|super
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|this
operator|.
name|src
operator|=
name|src
expr_stmt|;
name|incrementSharedReferences
argument_list|()
expr_stmt|;
comment|//if src is CachingFilterInputStream also register there so it can keep track of stream which rely on cache
if|if
condition|(
name|src
operator|instanceof
name|CachingFilterInputStream
condition|)
block|{
specifier|final
name|FilterInputStreamCache
name|otherCache
init|=
operator|(
operator|(
name|CachingFilterInputStream
operator|)
name|src
operator|)
operator|.
name|getCache
argument_list|()
decl_stmt|;
name|otherCache
operator|.
name|incrementSharedReferences
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getSrcOffset
parameter_list|()
block|{
return|return
name|this
operator|.
name|srcOffset
return|;
block|}
specifier|public
name|boolean
name|isSrcClosed
parameter_list|()
block|{
return|return
name|srcClosed
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|srcClosed
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|src
operator|.
name|available
argument_list|()
operator|+
name|getLength
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|/**      * Closes the src InputStream and empties the cache      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|decrementSharedReferences
argument_list|()
expr_stmt|;
if|if
condition|(
name|sharedReferenceCount
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|srcClosed
condition|)
block|{
try|try
block|{
comment|//                    if(src instanceof CachingFilterInputStream) {
comment|//                        ((CachingFilterInputStream) src).decrementSharedReferences();
comment|//                    } else {
comment|//                        src.close();
comment|//                    }
name|src
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|srcClosed
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|this
operator|.
name|invalidate
argument_list|()
expr_stmt|;
comment|//empty the cache
name|FilterInputStreamCacheMonitor
operator|.
name|getInstance
argument_list|()
operator|.
name|deregister
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// deregister with the monitor
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|srcClosed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|FilterInputStreamCache
operator|.
name|INPUTSTREAM_CLOSED
argument_list|)
throw|;
block|}
specifier|final
name|int
name|data
init|=
name|src
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|data
operator|==
name|FilterInputStreamCache
operator|.
name|END_OF_STREAM
condition|)
block|{
return|return
name|FilterInputStreamCache
operator|.
name|END_OF_STREAM
return|;
block|}
name|this
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|this
operator|.
name|srcOffset
operator|++
expr_stmt|;
return|return
name|data
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|srcClosed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|FilterInputStreamCache
operator|.
name|INPUTSTREAM_CLOSED
argument_list|)
throw|;
block|}
name|int
name|srcLen
init|=
name|src
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcLen
operator|==
name|FilterInputStreamCache
operator|.
name|END_OF_STREAM
condition|)
block|{
return|return
name|FilterInputStreamCache
operator|.
name|END_OF_STREAM
return|;
block|}
name|this
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|srcLen
argument_list|)
expr_stmt|;
name|this
operator|.
name|srcOffset
operator|+=
name|srcLen
expr_stmt|;
return|return
name|srcLen
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|srcClosed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|FilterInputStreamCache
operator|.
name|INPUTSTREAM_CLOSED
argument_list|)
throw|;
block|}
if|else if
condition|(
name|n
operator|<
literal|1
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|srcOffset
operator|<
name|n
condition|)
block|{
specifier|final
name|byte
name|skipped
index|[]
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
operator|(
name|n
operator|-
name|srcOffset
operator|)
index|]
decl_stmt|;
name|int
name|srcLen
init|=
name|src
operator|.
name|read
argument_list|(
name|skipped
argument_list|)
decl_stmt|;
comment|//have we reached the end of the stream?
if|if
condition|(
name|srcLen
operator|==
name|FilterInputStreamCache
operator|.
name|END_OF_STREAM
condition|)
block|{
return|return
name|srcOffset
return|;
block|}
comment|//increase srcOffset due to the read operation above
name|srcOffset
operator|+=
name|srcLen
expr_stmt|;
comment|//store data in cache
name|this
operator|.
name|write
argument_list|(
name|skipped
argument_list|,
literal|0
argument_list|,
name|srcLen
argument_list|)
expr_stmt|;
return|return
name|srcOffset
return|;
block|}
else|else
block|{
specifier|final
name|byte
name|skipped
index|[]
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|n
index|]
decl_stmt|;
comment|//TODO could overflow
name|int
name|actualLen
init|=
name|src
operator|.
name|read
argument_list|(
name|skipped
argument_list|)
decl_stmt|;
comment|//increase srcOffset due to read operation above
name|srcOffset
operator|+=
name|actualLen
expr_stmt|;
comment|//store data in the cache
name|this
operator|.
name|write
argument_list|(
name|skipped
argument_list|,
literal|0
argument_list|,
name|actualLen
argument_list|)
expr_stmt|;
return|return
name|actualLen
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"reset() not supported."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|srcIsFilterInputStreamCache
parameter_list|()
block|{
return|return
name|src
operator|instanceof
name|CachingFilterInputStream
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|incrementSharedReferences
parameter_list|()
block|{
name|sharedReferenceCount
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|decrementSharedReferences
parameter_list|()
block|{
name|sharedReferenceCount
operator|--
expr_stmt|;
block|}
block|}
end_class

end_unit

