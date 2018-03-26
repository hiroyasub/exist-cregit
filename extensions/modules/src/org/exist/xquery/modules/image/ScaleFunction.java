begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Image Module Extension ScaleFunction  *  Copyright (C) 2006-09 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|image
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

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Image
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|image
operator|.
name|BufferedImage
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
name|InputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|imageio
operator|.
name|ImageIO
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
name|xquery
operator|.
name|BasicFunction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Cardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionSignature
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Base64BinaryValueType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|BinaryValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|BinaryValueFromInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|FunctionParameterSequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|FunctionReturnSequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|IntegerValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * eXist Image Module Extension ScaleFunction   *   * Scale's an Image  *   * @author Adam Retter<adam@exist-db.org>  * @author Loren Cahlander  * @serial 2007-01-16  * @version 1.0  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|ScaleFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ScaleFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MAXHEIGHT
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MAXWIDTH
init|=
literal|100
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"scale"
argument_list|,
name|ImageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ImageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Scale the image image to a specified dimension.  If no dimensions are specified, then the default values are 'maxheight = 100' and 'maxwidth = 100'."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"image"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The image data"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"dimension"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The maximum dimension of the scaled image. expressed in pixels (maxheight, maxwidth).  If empty, then the default values are 'maxheight = 100' and 'maxwidth = 100'."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"mimeType"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The mime-type of the image"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the scaled image or an empty sequence if $image is invalid"
argument_list|)
argument_list|)
decl_stmt|;
comment|/** 	 * ScaleFunction Constructor 	 *  	 * @param context	The Context of the calling XQuery 	 */
specifier|public
name|ScaleFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * evaluate the call to the xquery scale() function, 	 * it is really the main entry point of this class 	 *  	 * @param args		arguments from the scale() function call 	 * @param contextSequence	the Context Sequence to operate on (not used here internally!) 	 * @return		A sequence representing the result of the scale() function call 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//was an image and a mime-type speficifed
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|||
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|//get the maximum dimensions to scale to
name|int
name|maxHeight
init|=
name|MAXHEIGHT
decl_stmt|;
name|int
name|maxWidth
init|=
name|MAXWIDTH
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|maxHeight
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|hasMany
argument_list|()
condition|)
name|maxWidth
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
comment|//get the mime-type
name|String
name|mimeType
init|=
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|formatName
init|=
name|mimeType
operator|.
name|substring
argument_list|(
name|mimeType
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|//TODO currently ONLY tested for JPEG!!!
name|Image
name|image
init|=
literal|null
decl_stmt|;
name|BufferedImage
name|bImage
init|=
literal|null
decl_stmt|;
try|try
init|(
comment|//get the image data
name|InputStream
name|inputStream
init|=
operator|(
operator|(
name|BinaryValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|image
operator|=
name|ImageIO
operator|.
name|read
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|image
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Unable to read image data!"
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|//scale the image
name|bImage
operator|=
name|ImageModule
operator|.
name|createThumb
argument_list|(
name|image
argument_list|,
name|maxHeight
argument_list|,
name|maxWidth
argument_list|)
expr_stmt|;
comment|//get the new scaled image
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ImageIO
operator|.
name|write
argument_list|(
name|bImage
argument_list|,
name|formatName
argument_list|,
name|os
argument_list|)
expr_stmt|;
comment|//return the new scaled image data
return|return
name|BinaryValueFromInputStream
operator|.
name|getInstance
argument_list|(
name|context
argument_list|,
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
name|os
operator|.
name|toInputStream
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

