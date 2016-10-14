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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|SupplierE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

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
name|File
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
name|InputStreamReader
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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * This class extends {@link org.xml.sax.InputSource}, so  * it also manages {@link java.io.File} and  * {@link org.exist.util.VirtualTempFile} as input sources.  *   * @author jmfernandez  *  */
end_comment

begin_class
specifier|public
class|class
name|VirtualTempFileInputSource
extends|extends
name|EXistInputSource
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
name|VirtualTempFileInputSource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Optional
argument_list|<
name|Either
argument_list|<
name|Path
argument_list|,
name|VirtualTempFile
argument_list|>
argument_list|>
name|file
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
specifier|public
name|VirtualTempFileInputSource
parameter_list|(
specifier|final
name|VirtualTempFile
name|vtempFile
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|vtempFile
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|VirtualTempFileInputSource
parameter_list|(
specifier|final
name|VirtualTempFile
name|vtempFile
parameter_list|,
specifier|final
name|String
name|encoding
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Temp file must be immutable from this point
name|vtempFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|Either
operator|.
name|Right
argument_list|(
name|vtempFile
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|setEncoding
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vtempFile
operator|.
name|tempFile
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|setSystemId
argument_list|(
name|vtempFile
operator|.
name|tempFile
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|VirtualTempFileInputSource
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|)
block|{
name|this
argument_list|(
name|file
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|VirtualTempFileInputSource
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|,
specifier|final
name|String
name|encoding
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|file
argument_list|)
operator|.
name|map
argument_list|(
name|Either
operator|::
name|Left
argument_list|)
expr_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|setEncoding
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|setSystemId
argument_list|(
name|file
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * @see InputSource#getByteStream() 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
annotation|@
name|Override
specifier|public
name|InputStream
name|getByteStream
parameter_list|()
block|{
name|assertOpen
argument_list|()
expr_stmt|;
return|return
name|file
operator|.
name|flatMap
argument_list|(
name|f
lambda|->
name|f
operator|.
name|fold
argument_list|(
name|this
operator|::
name|newInputStream
argument_list|,
name|this
operator|::
name|vtfByteStream
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|private
name|Optional
argument_list|<
name|Reader
argument_list|>
name|inputStreamReader
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|,
specifier|final
name|String
name|encoding
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|encoding
argument_list|)
operator|.
name|flatMap
argument_list|(
name|e
lambda|->
name|Optional
operator|.
name|ofNullable
argument_list|(
name|is
argument_list|)
operator|.
name|flatMap
argument_list|(
name|i
lambda|->
block|{
block_content|try
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
block|)
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_comment
unit|}
comment|/** 	 * @see InputSource#getCharacterStream() 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
end_comment

begin_function
unit|@
name|Override
specifier|public
name|Reader
name|getCharacterStream
parameter_list|()
block|{
name|assertOpen
argument_list|()
expr_stmt|;
return|return
name|inputStreamReader
argument_list|(
name|getByteStream
argument_list|()
argument_list|,
name|getEncoding
argument_list|()
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
end_comment

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setByteStream
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|)
block|{
name|assertOpen
argument_list|()
expr_stmt|;
comment|// Nothing, so collateral effects are avoided!
block|}
end_function

begin_comment
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
end_comment

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setCharacterStream
parameter_list|(
specifier|final
name|Reader
name|r
parameter_list|)
block|{
name|assertOpen
argument_list|()
expr_stmt|;
comment|// Nothing, so collateral effects are avoided!
block|}
end_function

begin_comment
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
end_comment

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setSystemId
parameter_list|(
specifier|final
name|String
name|systemId
parameter_list|)
block|{
name|assertOpen
argument_list|()
expr_stmt|;
comment|// Nothing, so collateral effects are avoided!
block|}
end_function

begin_comment
comment|/** 	 * @see EXistInputSource#getSymbolicPath() 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
end_comment

begin_function
annotation|@
name|Override
specifier|public
name|String
name|getSymbolicPath
parameter_list|()
block|{
name|assertOpen
argument_list|()
expr_stmt|;
return|return
name|file
operator|.
name|flatMap
argument_list|(
name|f
lambda|->
name|f
operator|.
name|fold
argument_list|(
name|l
lambda|->
name|Optional
operator|.
name|of
argument_list|(
name|l
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|r
lambda|->
name|Optional
operator|.
name|ofNullable
argument_list|(
name|r
operator|.
name|tempFile
argument_list|)
operator|.
name|map
argument_list|(
name|File
operator|::
name|getAbsolutePath
argument_list|)
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/** 	 * @see EXistInputSource#getByteStreamLength() 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
end_comment

begin_function
annotation|@
name|Override
specifier|public
name|long
name|getByteStreamLength
parameter_list|()
block|{
name|assertOpen
argument_list|()
expr_stmt|;
return|return
name|file
operator|.
name|flatMap
argument_list|(
name|f
lambda|->
name|f
operator|.
name|fold
argument_list|(
name|this
operator|::
name|fileSize
argument_list|,
name|this
operator|::
name|vtfSize
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
operator|-
literal|1l
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isClosed
argument_list|()
condition|)
block|{
try|try
block|{
name|file
operator|.
name|ifPresent
argument_list|(
name|f
lambda|->
name|f
operator|.
name|fold
argument_list|(
name|l
lambda|->
literal|true
argument_list|,
name|VirtualTempFile
operator|::
name|delete
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|=
name|Optional
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_function

begin_function
specifier|private
name|Optional
argument_list|<
name|InputStream
argument_list|>
name|newInputStream
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
return|return
name|safeIO
argument_list|(
parameter_list|()
lambda|->
operator|new
name|BufferedInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
end_function

begin_function
specifier|private
name|Optional
argument_list|<
name|InputStream
argument_list|>
name|vtfByteStream
parameter_list|(
specifier|final
name|VirtualTempFile
name|vtf
parameter_list|)
block|{
return|return
name|safeIO
argument_list|(
parameter_list|()
lambda|->
name|vtf
operator|.
name|getByteStream
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
specifier|private
name|Optional
argument_list|<
name|Long
argument_list|>
name|fileSize
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
return|return
name|safeIO
argument_list|(
parameter_list|()
lambda|->
name|Files
operator|.
name|size
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
end_function

begin_function
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Optional
argument_list|<
name|T
argument_list|>
name|safeIO
parameter_list|(
specifier|final
name|SupplierE
argument_list|<
name|T
argument_list|,
name|IOException
argument_list|>
name|isSource
parameter_list|)
block|{
try|try
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|isSource
operator|.
name|get
argument_list|()
argument_list|)
return|;
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
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
end_function

begin_function
specifier|private
name|Optional
argument_list|<
name|Long
argument_list|>
name|vtfSize
parameter_list|(
specifier|final
name|VirtualTempFile
name|vtf
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|vtf
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

