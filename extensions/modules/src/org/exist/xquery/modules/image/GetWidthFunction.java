begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Image Module Extension GetWidthFunction  *  Copyright (C) 2006 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|IOException
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
name|BinaryDocument
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|AnyURIValue
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
comment|/**  * eXist Image Module Extension GetWidthFunction   *   * Get's the width of an Image stored in the eXist db  *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-03-10  * @version 1.0  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|GetWidthFunction
extends|extends
name|BasicFunction
block|{
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
literal|"get-width"
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
literal|"Get's the width of the image in the db indicated by $a, returning an integer of the images width in pixels or an empty sequence if $a is invalid."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
comment|/** 	 * GetWidthFunction Constructor 	 *  	 * @param context	The Context of the calling XQuery 	 */
specifier|public
name|GetWidthFunction
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
comment|/** 	 * evaluate the call to the xquery get-width() function, 	 * it is really the main entry point of this class 	 *  	 * @param args		arguments from the get-width() function call 	 * @param contextSequence	the Context Sequence to operate on (not used here internally!) 	 * @return		A sequence representing the result of the get-width() function call 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
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
comment|//was a image speficifed
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
comment|//get the path of the image
name|AnyURIValue
name|imgPath
init|=
operator|(
name|AnyURIValue
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
decl_stmt|;
comment|//Get the image document from the db
name|DBBroker
name|dbbroker
init|=
name|context
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|DocumentImpl
name|docImage
init|=
literal|null
decl_stmt|;
try|try
block|{
name|docImage
operator|=
name|dbbroker
operator|.
name|getXMLResource
argument_list|(
name|imgPath
operator|.
name|toXmldbURI
argument_list|()
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|imgPath
operator|+
literal|": permission denied to read resource"
argument_list|)
throw|;
block|}
comment|//Valid document?
if|if
condition|(
name|docImage
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|imgPath
operator|+
literal|" does not exist!"
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|//Binary Document?
if|if
condition|(
name|docImage
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|imgPath
operator|+
literal|" exists but is not a binary resource!"
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|//Binary document is an image?
if|if
condition|(
operator|!
name|docImage
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"image/"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|imgPath
operator|+
literal|" exists but is not an image!"
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|//Get the image document as a binary document
name|BinaryDocument
name|binImage
init|=
operator|(
name|BinaryDocument
operator|)
name|docImage
decl_stmt|;
comment|//get a byte array representing the image
name|byte
index|[]
name|imgData
init|=
name|dbbroker
operator|.
name|getBinaryResource
argument_list|(
name|binImage
argument_list|)
decl_stmt|;
comment|//close the image from the db
name|dbbroker
operator|.
name|closeDocument
argument_list|()
expr_stmt|;
name|Image
name|image
init|=
literal|null
decl_stmt|;
name|int
name|iWidth
init|=
operator|-
literal|1
decl_stmt|;
comment|//Create an Image object from the byte array
try|try
block|{
name|image
operator|=
name|ImageIO
operator|.
name|read
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|imgData
argument_list|)
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
name|error
argument_list|(
name|imgPath
operator|+
literal|" could not read image data!"
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|//Get the width of the image
name|iWidth
operator|=
name|image
operator|.
name|getWidth
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//did we get the width of the image?
if|if
condition|(
name|iWidth
operator|==
operator|-
literal|1
condition|)
block|{
comment|//no, log the error
name|LOG
operator|.
name|error
argument_list|(
name|imgPath
operator|+
literal|" could not read image data!"
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
block|{
comment|//return the width of the image
return|return
operator|new
name|IntegerValue
argument_list|(
name|iWidth
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

