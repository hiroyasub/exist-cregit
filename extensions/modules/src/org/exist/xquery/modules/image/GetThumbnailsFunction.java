begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Image Module Extension GetThumbnailsFunction  *  Copyright (C) 2006-09 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|net
operator|.
name|URISyntaxException
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Optional
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
name|collections
operator|.
name|Collection
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
name|persistent
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
name|BrokerPool
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
name|journal
operator|.
name|JournalManager
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
name|txn
operator|.
name|TransactionManager
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
name|txn
operator|.
name|Txn
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
name|xmldb
operator|.
name|XmldbURI
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
name|StringValue
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
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  *   *   * @author Rafael Troilo (rtroilo@gmail.com)  *   */
end_comment

begin_class
specifier|public
class|class
name|GetThumbnailsFunction
extends|extends
name|BasicFunction
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
name|GetThumbnailsFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MAXTHUMBHEIGHT
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|MAXTHUMBWIDTH
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|THUMBPREFIX
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|THUMBPATH
init|=
literal|"thumbs"
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
literal|"thumbnail"
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
literal|"Generate thumbnails from the given database collection"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collection"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URI to the collection"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"thumbnail-location"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The location in the database where the thumbnails should be created, this can be a local path, with the prefix 'xmldb:' a absolute path within the database or with 'rel:' path relative to the given $collection.  You can leave this empty then the default is 'rel:/thumbs'. "
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
literal|"The dimension of the thumbnails, if empty then the default values are 'maxheight = 100' and 'maxwidth = 100', the first value is 'maxheight' and the second 'maxwidth'. "
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"prefix"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The prefix to append to the thumbnail filenames"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the result"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetThumbnailsFunction
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], 	 *      org.exist.xquery.value.Sequence) 	 */
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
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
comment|// boolean isDatabasePath = false;
name|boolean
name|isSaveToDataBase
init|=
literal|false
decl_stmt|;
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
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|AnyURIValue
name|picturePath
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
if|if
condition|(
name|picturePath
operator|.
name|getStringValue
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://"
argument_list|)
condition|)
block|{
name|picturePath
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|picturePath
operator|.
name|getStringValue
argument_list|()
operator|.
name|substring
argument_list|(
literal|14
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AnyURIValue
name|thumbPath
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|thumbPath
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|picturePath
operator|.
name|toXmldbURI
argument_list|()
operator|.
name|append
argument_list|(
name|THUMBPATH
argument_list|)
argument_list|)
expr_stmt|;
name|isSaveToDataBase
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|thumbPath
operator|=
operator|(
name|AnyURIValue
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
expr_stmt|;
if|if
condition|(
name|thumbPath
operator|.
name|getStringValue
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"file:"
argument_list|)
condition|)
block|{
name|isSaveToDataBase
operator|=
literal|false
expr_stmt|;
name|thumbPath
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|thumbPath
operator|.
name|getStringValue
argument_list|()
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|isSaveToDataBase
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|XmldbURI
name|thumbsURI
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|thumbPath
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|thumbsURI
operator|.
name|isAbsolute
argument_list|()
condition|)
name|thumbsURI
operator|=
name|picturePath
operator|.
name|toXmldbURI
argument_list|()
operator|.
name|append
argument_list|(
name|thumbPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|thumbPath
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|thumbsURI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
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
comment|// result.add(new StringValue(picturePath.getStringValue()));
comment|// result.add(new StringValue(thumbPath.getStringValue() + " isDB?= "
comment|// + isSaveToDataBase));
name|int
name|maxThumbHeight
init|=
name|MAXTHUMBHEIGHT
decl_stmt|;
name|int
name|maxThumbWidth
init|=
name|MAXTHUMBWIDTH
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|maxThumbHeight
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|2
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
literal|2
index|]
operator|.
name|hasMany
argument_list|()
condition|)
name|maxThumbWidth
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|2
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
name|String
name|prefix
init|=
name|THUMBPREFIX
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|3
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|prefix
operator|=
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
comment|// result.add(new StringValue("maxThumbHeight = " + maxThumbHeight
comment|// + ", maxThumbWidth = " + maxThumbWidth));
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
try|try
init|(
specifier|final
name|DBBroker
name|dbbroker
init|=
name|context
operator|.
name|getBroker
argument_list|()
init|)
block|{
comment|// Start transaction
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|Collection
name|thumbCollection
init|=
literal|null
decl_stmt|;
name|Path
name|thumbDir
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isSaveToDataBase
condition|)
block|{
try|try
block|{
name|thumbCollection
operator|=
name|dbbroker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|thumbPath
operator|.
name|toXmldbURI
argument_list|()
argument_list|)
expr_stmt|;
name|dbbroker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|thumbCollection
argument_list|)
expr_stmt|;
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
else|else
block|{
name|thumbDir
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|thumbPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|thumbDir
argument_list|)
condition|)
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|thumbDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|Collection
name|allPictures
init|=
literal|null
decl_stmt|;
name|Collection
name|existingThumbsCol
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|existingThumbsArray
init|=
literal|null
decl_stmt|;
try|try
block|{
name|allPictures
operator|=
name|dbbroker
operator|.
name|getCollection
argument_list|(
name|picturePath
operator|.
name|toXmldbURI
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|allPictures
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|if
condition|(
name|isSaveToDataBase
condition|)
block|{
name|existingThumbsCol
operator|=
name|dbbroker
operator|.
name|getCollection
argument_list|(
name|thumbPath
operator|.
name|toXmldbURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|existingThumbsArray
operator|=
name|FileUtils
operator|.
name|list
argument_list|(
name|thumbDir
argument_list|,
name|path
lambda|->
block|{
specifier|final
name|String
name|fileName
init|=
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".jpeg"
argument_list|)
operator|||
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".jpg"
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
decl||
name|IOException
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
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|DocumentImpl
name|docImage
init|=
literal|null
decl_stmt|;
name|BinaryDocument
name|binImage
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|BinaryDocument
name|doc
init|=
literal|null
decl_stmt|;
name|BufferedImage
name|bImage
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|byte
index|[]
name|imgData
init|=
literal|null
decl_stmt|;
name|Image
name|image
init|=
literal|null
decl_stmt|;
name|ByteArrayOutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|allPictures
operator|.
name|iterator
argument_list|(
name|dbbroker
argument_list|)
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|docImage
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// is not already existing??
if|if
condition|(
operator|!
operator|(
operator|(
name|fileExist
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|existingThumbsCol
argument_list|,
name|docImage
argument_list|,
name|prefix
argument_list|)
operator|)
operator|||
operator|(
name|fileExist
argument_list|(
name|existingThumbsArray
argument_list|,
name|docImage
argument_list|,
name|prefix
argument_list|)
operator|)
operator|)
condition|)
block|{
if|if
condition|(
name|docImage
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
comment|// TODO maybe extends for gifs too.
if|if
condition|(
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
literal|"image/jpeg"
argument_list|)
condition|)
block|{
name|binImage
operator|=
operator|(
name|BinaryDocument
operator|)
name|docImage
expr_stmt|;
comment|// get a byte array representing the image
try|try
block|{
name|InputStream
name|is
init|=
name|dbbroker
operator|.
name|getBinaryResource
argument_list|(
name|binImage
argument_list|)
decl_stmt|;
name|image
operator|=
name|ImageIO
operator|.
name|read
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
try|try
block|{
name|bImage
operator|=
name|ImageModule
operator|.
name|createThumb
argument_list|(
name|image
argument_list|,
name|maxThumbHeight
argument_list|,
name|maxThumbWidth
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|isSaveToDataBase
condition|)
block|{
name|os
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
try|try
block|{
name|ImageIO
operator|.
name|write
argument_list|(
name|bImage
argument_list|,
literal|"jpg"
argument_list|,
name|os
argument_list|)
expr_stmt|;
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
try|try
block|{
name|doc
operator|=
name|thumbCollection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|dbbroker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|prefix
operator|+
name|docImage
operator|.
name|getFileURI
argument_list|()
argument_list|)
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"image/jpeg"
argument_list|)
expr_stmt|;
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
comment|// result.add(new
comment|// StringValue(""+docImage.getFileURI()+"|"+thumbCollection.getURI()+THUMBPREFIX
comment|// + docImage.getFileURI()));
block|}
else|else
block|{
try|try
block|{
name|ImageIO
operator|.
name|write
argument_list|(
name|bImage
argument_list|,
literal|"jpg"
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|thumbPath
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|prefix
operator|+
name|docImage
operator|.
name|getFileURI
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
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
comment|// result.add(new StringValue(
comment|// thumbPath.toString() + "/"
comment|// + THUMBPREFIX
comment|// + docImage.getFileURI()));
block|}
block|}
block|}
else|else
block|{
comment|// result.add(new StringValue(""+docImage.getURI()+"|"
comment|// + ((existingThumbsCol != null) ? ""
comment|// + existingThumbsCol.getURI() : thumbDir
comment|// .toString()) + "/" + prefix
comment|// + docImage.getFileURI()));
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|docImage
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
decl||
name|LockException
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
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
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
specifier|final
name|Optional
argument_list|<
name|JournalManager
argument_list|>
name|journalManager
init|=
name|pool
operator|.
name|getJournalManager
argument_list|()
decl_stmt|;
name|journalManager
operator|.
name|ifPresent
argument_list|(
name|j
lambda|->
name|j
operator|.
name|flush
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|dbbroker
operator|.
name|closeDocument
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|fileExist
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|col
parameter_list|,
name|DocumentImpl
name|file
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|col
operator|!=
literal|null
condition|)
return|return
name|col
operator|.
name|hasDocument
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|prefix
operator|+
name|file
operator|.
name|getFileURI
argument_list|()
argument_list|)
argument_list|)
return|;
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|fileExist
parameter_list|(
name|List
argument_list|<
name|Path
argument_list|>
name|cols
parameter_list|,
name|DocumentImpl
name|file
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|cols
operator|!=
literal|null
condition|)
for|for
control|(
name|Path
name|col
range|:
name|cols
control|)
block|{
if|if
condition|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|col
argument_list|)
operator|.
name|endsWith
argument_list|(
name|prefix
operator|+
name|file
operator|.
name|getFileURI
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

