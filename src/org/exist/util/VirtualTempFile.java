begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|external
operator|.
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_comment
comment|/**  *   * This class is a cross-over of many others, but mainly File and OutputStream  *   * @author jmfernandez  *  */
end_comment

begin_class
specifier|public
class|class
name|VirtualTempFile
extends|extends
name|OutputStream
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
name|VirtualTempFile
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_CHUNK_SIZE
init|=
literal|0x40000
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DEFAULT_TEMP_PREFIX
init|=
literal|"eXistRPCV"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DEFAULT_TEMP_POSTFIX
init|=
literal|".res"
decl_stmt|;
specifier|protected
name|File
name|tempFile
decl_stmt|;
specifier|protected
name|boolean
name|deleteTempFile
decl_stmt|;
specifier|protected
name|ByteArrayOutputStream
name|baBuffer
decl_stmt|;
specifier|protected
name|FileOutputStream
name|strBuffer
decl_stmt|;
specifier|protected
name|OutputStream
name|os
decl_stmt|;
specifier|protected
name|byte
index|[]
name|tempBuffer
decl_stmt|;
specifier|protected
name|int
name|maxMemorySize
decl_stmt|;
specifier|protected
name|int
name|maxChunkSize
decl_stmt|;
specifier|protected
name|long
name|vLength
decl_stmt|;
specifier|protected
name|String
name|temp_prefix
decl_stmt|;
specifier|protected
name|String
name|temp_postfix
decl_stmt|;
comment|/** 	 * Constructor for a fresh VirtualTempFile 	 */
specifier|public
name|VirtualTempFile
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_MAX_CHUNK_SIZE
argument_list|,
name|DEFAULT_MAX_CHUNK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructor for a fresh VirtualTempFile, with some params 	 * @param maxMemorySize 	 * @param maxChunkSize 	 */
specifier|public
name|VirtualTempFile
parameter_list|(
name|int
name|maxMemorySize
parameter_list|,
name|int
name|maxChunkSize
parameter_list|)
block|{
name|this
operator|.
name|maxMemorySize
operator|=
name|maxMemorySize
expr_stmt|;
name|this
operator|.
name|maxChunkSize
operator|=
name|maxChunkSize
expr_stmt|;
name|vLength
operator|=
operator|-
literal|1L
expr_stmt|;
name|baBuffer
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|maxMemorySize
argument_list|)
expr_stmt|;
name|strBuffer
operator|=
literal|null
expr_stmt|;
name|tempFile
operator|=
literal|null
expr_stmt|;
name|tempBuffer
operator|=
literal|null
expr_stmt|;
name|deleteTempFile
operator|=
literal|true
expr_stmt|;
name|os
operator|=
name|baBuffer
expr_stmt|;
name|temp_prefix
operator|=
name|DEFAULT_TEMP_PREFIX
expr_stmt|;
name|temp_postfix
operator|=
name|DEFAULT_TEMP_POSTFIX
expr_stmt|;
block|}
comment|/** 	 * Constructor for an already known file 	 * @param theFile 	 */
specifier|public
name|VirtualTempFile
parameter_list|(
name|File
name|theFile
parameter_list|)
block|{
name|this
argument_list|(
name|theFile
argument_list|,
name|DEFAULT_MAX_CHUNK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructor for an already known file, with params 	 * @param theFile 	 * @param maxChunkSize 	 */
specifier|public
name|VirtualTempFile
parameter_list|(
name|File
name|theFile
parameter_list|,
name|int
name|maxChunkSize
parameter_list|)
block|{
comment|// This one is not going to be used, but it is set to avoid uninitialized variables
name|this
operator|.
name|maxMemorySize
operator|=
name|maxChunkSize
expr_stmt|;
name|this
operator|.
name|maxChunkSize
operator|=
name|maxChunkSize
expr_stmt|;
name|baBuffer
operator|=
literal|null
expr_stmt|;
name|strBuffer
operator|=
literal|null
expr_stmt|;
name|os
operator|=
literal|null
expr_stmt|;
name|tempFile
operator|=
name|theFile
expr_stmt|;
name|deleteTempFile
operator|=
literal|false
expr_stmt|;
name|vLength
operator|=
name|theFile
operator|.
name|length
argument_list|()
expr_stmt|;
name|tempBuffer
operator|=
literal|null
expr_stmt|;
name|temp_prefix
operator|=
name|DEFAULT_TEMP_PREFIX
expr_stmt|;
name|temp_postfix
operator|=
name|DEFAULT_TEMP_POSTFIX
expr_stmt|;
block|}
comment|/** 	 * Constructor for an already known memory block 	 * @param theBlock 	 */
specifier|public
name|VirtualTempFile
parameter_list|(
name|byte
index|[]
name|theBlock
parameter_list|)
block|{
name|this
argument_list|(
name|theBlock
argument_list|,
name|theBlock
operator|.
name|length
argument_list|,
name|DEFAULT_MAX_CHUNK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructor for an already known memory block, with params 	 * @param theBlock 	 * @param maxMemorySize 	 * @param maxChunkSize 	 */
specifier|public
name|VirtualTempFile
parameter_list|(
name|byte
index|[]
name|theBlock
parameter_list|,
name|int
name|maxMemorySize
parameter_list|,
name|int
name|maxChunkSize
parameter_list|)
block|{
comment|// This one is not going to be used, but it is set to avoid uninitialized variables
name|this
operator|.
name|maxMemorySize
operator|=
name|maxMemorySize
expr_stmt|;
name|this
operator|.
name|maxChunkSize
operator|=
name|maxChunkSize
expr_stmt|;
name|baBuffer
operator|=
literal|null
expr_stmt|;
name|strBuffer
operator|=
literal|null
expr_stmt|;
name|os
operator|=
literal|null
expr_stmt|;
name|temp_prefix
operator|=
name|DEFAULT_TEMP_PREFIX
expr_stmt|;
name|temp_postfix
operator|=
name|DEFAULT_TEMP_POSTFIX
expr_stmt|;
name|tempFile
operator|=
literal|null
expr_stmt|;
name|deleteTempFile
operator|=
literal|true
expr_stmt|;
name|vLength
operator|=
name|theBlock
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|vLength
operator|<=
name|maxMemorySize
condition|)
block|{
name|tempBuffer
operator|=
name|theBlock
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|tempFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
name|temp_prefix
argument_list|,
name|temp_postfix
argument_list|)
expr_stmt|;
name|tempFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing to temporary file: "
operator|+
name|tempFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|OutputStream
name|tmpBuffer
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tempFile
argument_list|)
decl_stmt|;
try|try
block|{
name|tmpBuffer
operator|.
name|write
argument_list|(
name|theBlock
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tmpBuffer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// Do Nothing(R)
block|}
block|}
block|}
comment|/** 	 * The prefix string used when the temp file is going to be created 	 * @return prefix string 	 */
specifier|public
name|String
name|getTempPrefix
parameter_list|()
block|{
return|return
name|temp_prefix
return|;
block|}
comment|/** 	 * The postfix string used when the temp file is going to be created 	 * @return  postfix string 	 */
specifier|public
name|String
name|getTempPostfix
parameter_list|()
block|{
return|return
name|temp_postfix
return|;
block|}
comment|/** 	 * It sets the used prefix string on temp filename creation 	 * @param newPrefix 	 */
specifier|public
name|void
name|setTempPrefix
parameter_list|(
name|String
name|newPrefix
parameter_list|)
block|{
if|if
condition|(
name|newPrefix
operator|==
literal|null
condition|)
name|newPrefix
operator|=
name|DEFAULT_TEMP_PREFIX
expr_stmt|;
name|temp_prefix
operator|=
name|newPrefix
expr_stmt|;
block|}
comment|/** 	 * It sets the used prefix string on temp filename creation 	 * @param newPostfix 	 */
specifier|public
name|void
name|setTempPostfix
parameter_list|(
name|String
name|newPostfix
parameter_list|)
block|{
if|if
condition|(
name|newPostfix
operator|==
literal|null
condition|)
name|newPostfix
operator|=
name|DEFAULT_TEMP_POSTFIX
expr_stmt|;
name|temp_postfix
operator|=
name|newPostfix
expr_stmt|;
block|}
comment|/** 	 * Method from OutputStream 	 */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|baBuffer
operator|!=
literal|null
condition|)
block|{
name|tempBuffer
operator|=
name|baBuffer
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|baBuffer
operator|=
literal|null
expr_stmt|;
name|vLength
operator|=
name|tempBuffer
operator|.
name|length
expr_stmt|;
block|}
if|if
condition|(
name|strBuffer
operator|!=
literal|null
condition|)
block|{
name|strBuffer
operator|.
name|close
argument_list|()
expr_stmt|;
name|strBuffer
operator|=
literal|null
expr_stmt|;
name|vLength
operator|=
name|tempFile
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
name|os
operator|=
literal|null
expr_stmt|;
block|}
comment|/** 	 * Method from OutputStream 	 */
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|os
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No stream to flush"
argument_list|)
throw|;
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * The method<code>getChunk</code>      *      * @param offset a<code>long</code> value      * @return a<code>byte[]</code> value      * @exception IOException if an error occurs      */
specifier|public
name|byte
index|[]
name|getChunk
parameter_list|(
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|tempFile
operator|!=
literal|null
condition|)
block|{
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|tempFile
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
name|raf
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|long
name|remaining
init|=
name|raf
operator|.
name|length
argument_list|()
operator|-
name|offset
decl_stmt|;
if|if
condition|(
name|remaining
operator|>
name|maxChunkSize
condition|)
name|remaining
operator|=
name|maxChunkSize
expr_stmt|;
if|else if
condition|(
name|remaining
operator|<
literal|0
condition|)
name|remaining
operator|=
literal|0
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|remaining
index|]
expr_stmt|;
name|raf
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|tempBuffer
operator|!=
literal|null
condition|)
block|{
name|long
name|remaining
init|=
name|tempBuffer
operator|.
name|length
operator|-
name|offset
decl_stmt|;
if|if
condition|(
name|remaining
operator|>
name|maxChunkSize
condition|)
name|remaining
operator|=
name|maxChunkSize
expr_stmt|;
if|else if
condition|(
name|remaining
operator|<
literal|0
condition|)
name|remaining
operator|=
literal|0
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|remaining
index|]
expr_stmt|;
if|if
condition|(
name|remaining
operator|>
literal|0
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|tempBuffer
argument_list|,
operator|(
name|int
operator|)
name|offset
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|remaining
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|tempFile
operator|!=
literal|null
operator|||
name|tempBuffer
operator|!=
literal|null
operator|||
name|baBuffer
operator|!=
literal|null
return|;
block|}
specifier|public
name|long
name|length
parameter_list|()
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// IgnoreIT(R)
block|}
block|}
return|return
name|vLength
return|;
block|}
comment|/** 	 * Method from File 	 * @return Always returns true 	 */
specifier|public
name|boolean
name|delete
parameter_list|()
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// IgnoreIT(R)
block|}
block|}
if|if
condition|(
name|tempFile
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|strBuffer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|strBuffer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// IgnoreIT(R)
block|}
name|strBuffer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|deleteTempFile
condition|)
name|tempFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tempFile
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|baBuffer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|baBuffer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// IgnoreIT(R)
block|}
name|baBuffer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|tempBuffer
operator|!=
literal|null
condition|)
block|{
name|tempBuffer
operator|=
literal|null
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|writeSwitch
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tempFile
operator|==
literal|null
condition|)
block|{
name|tempFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
name|temp_prefix
argument_list|,
name|temp_postfix
argument_list|)
expr_stmt|;
name|tempFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing to temporary file: "
operator|+
name|tempFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|strBuffer
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|tempFile
argument_list|)
expr_stmt|;
name|strBuffer
operator|.
name|write
argument_list|(
name|baBuffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|=
name|strBuffer
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|os
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No stream to write to"
argument_list|)
throw|;
block|}
name|os
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|baBuffer
operator|!=
literal|null
operator|&&
name|baBuffer
operator|.
name|size
argument_list|()
operator|>
name|maxMemorySize
condition|)
block|{
name|writeSwitch
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|write
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
name|os
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No stream to write to"
argument_list|)
throw|;
block|}
name|os
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|baBuffer
operator|!=
literal|null
operator|&&
name|baBuffer
operator|.
name|size
argument_list|()
operator|>
name|maxMemorySize
condition|)
block|{
name|writeSwitch
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * A commodity method to write the whole content of an InputStream 	 */
specifier|public
name|void
name|write
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|is
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * A commodity method to write the whole content of an InputStream, 	 * giving an optional max length (honored when it is bigger than 0) 	 */
specifier|public
name|void
name|write
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|long
name|lengthHint
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|os
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No stream to write to"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|maxChunkSize
index|]
decl_stmt|;
name|long
name|off
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
do|do
block|{
name|count
operator|=
name|is
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|off
operator|+=
name|count
expr_stmt|;
block|}
if|if
condition|(
name|baBuffer
operator|!=
literal|null
operator|&&
name|baBuffer
operator|.
name|size
argument_list|()
operator|>
name|maxMemorySize
condition|)
block|{
name|writeSwitch
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|count
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|lengthHint
operator|<=
literal|0
operator|||
name|off
operator|<
name|lengthHint
operator|)
condition|)
do|;
block|}
comment|/** 	 * An easy way to obtain an InputStream 	 * @return byte stream 	 * @throws IOException 	 */
specifier|public
name|InputStream
name|getByteStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tempFile
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tempFile
argument_list|)
argument_list|,
literal|655360
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|tempBuffer
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|tempBuffer
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * It returns either a byte array or a File 	 * with the content. The initial threshold rules 	 * which kind of object you are getting 	 * @return Either a File or a byte[] object 	 */
specifier|public
name|Object
name|getContent
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// IgnoreIT(R)
block|}
return|return
operator|(
name|tempFile
operator|!=
literal|null
operator|)
condition|?
name|tempFile
else|:
name|tempBuffer
return|;
block|}
comment|/** 	 * Method to force materialization as a (temp)file the VirtualTempFile instance 	 * @return A (temporal) file with the content 	 * @throws IOException 	 */
specifier|public
name|File
name|toFile
parameter_list|()
throws|throws
name|IOException
block|{
comment|// First, forcing the write to temp file
name|writeSwitch
argument_list|()
expr_stmt|;
comment|// Second, close
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
name|close
argument_list|()
expr_stmt|;
name|File
name|retFile
init|=
name|tempFile
decl_stmt|;
comment|// From this point the tempFile is not managed any more by this VirtualTempFile
name|tempFile
operator|=
literal|null
expr_stmt|;
return|return
name|retFile
return|;
block|}
comment|/** 	 * Method to materialize the accumulated content in an OutputStream 	 * @param out The output stream where the content is going to be written 	 */
specifier|public
name|void
name|writeToStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tempFile
operator|!=
literal|null
condition|)
block|{
comment|//			byte[] writeBuffer=new byte[65536];
name|InputStream
name|input
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tempFile
argument_list|)
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|input
argument_list|)
expr_stmt|;
comment|//			try {
comment|//				int readBytes;
comment|//				while((readBytes = input.read(writeBuffer,0,writeBuffer.length))!=-1) {
comment|//					out.write(writeBuffer,0,readBytes);
comment|//				}
comment|//			} finally {
comment|//				input.close();
comment|//			}
block|}
if|else if
condition|(
name|tempBuffer
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|tempBuffer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

