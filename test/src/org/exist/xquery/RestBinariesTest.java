begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|codec
operator|.
name|binary
operator|.
name|Hex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|fluent
operator|.
name|Executor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|fluent
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|jaxb
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|jaxb
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistWebServer
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
name|io
operator|.
name|FastByteArrayOutputStream
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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|Marshaller
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|Unmarshaller
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
import|import static
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
operator|.
name|SC_CREATED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
operator|.
name|SC_OK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|TestUtils
operator|.
name|ADMIN_DB_PWD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|TestUtils
operator|.
name|ADMIN_DB_USER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|RestBinariesTest
extends|extends
name|AbstractBinariesTest
argument_list|<
name|Result
argument_list|,
name|Result
operator|.
name|Value
argument_list|,
name|Exception
argument_list|>
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistWebServer
name|existWebServer
init|=
operator|new
name|ExistWebServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Executor
name|executor
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setupExecutor
parameter_list|()
block|{
name|executor
operator|=
name|Executor
operator|.
name|newInstance
argument_list|()
operator|.
name|auth
argument_list|(
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
name|existWebServer
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|,
name|ADMIN_DB_USER
argument_list|,
name|ADMIN_DB_PWD
argument_list|)
operator|.
name|authPreemptive
argument_list|(
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
name|existWebServer
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@see https://github.com/eXist-db/exist/issues/790#error-case-2}      *      * response:stream is used to return Base64 encoded binary.      */
annotation|@
name|Test
specifier|public
name|void
name|streamBinarySax
parameter_list|()
throws|throws
name|JAXBException
throws|,
name|IOException
block|{
specifier|final
name|String
name|query
init|=
literal|"import module namespace util = \"http://exist-db.org/xquery/util\";\n"
operator|+
literal|"import module namespace response = \"http://exist-db.org/xquery/response\";\n"
operator|+
literal|"let $bin := util:binary-doc('"
operator|+
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
name|BIN1_FILENAME
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|"')\n"
operator|+
literal|"return response:stream($bin, 'media-type=application/octet-stream')"
decl_stmt|;
specifier|final
name|HttpResponse
name|response
init|=
name|postXquery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|HttpEntity
name|entity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|entity
operator|.
name|writeTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|BIN1_CONTENT
argument_list|,
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * {@see https://github.com/eXist-db/exist/issues/790#error-case-2}      *      * response:stream-binary is used to return raw binary.      */
annotation|@
name|Test
specifier|public
name|void
name|streamBinaryRaw
parameter_list|()
throws|throws
name|JAXBException
throws|,
name|IOException
block|{
specifier|final
name|String
name|query
init|=
literal|"import module namespace util = \"http://exist-db.org/xquery/util\";\n"
operator|+
literal|"import module namespace response = \"http://exist-db.org/xquery/response\";\n"
operator|+
literal|"let $bin := util:binary-doc('"
operator|+
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
name|BIN1_FILENAME
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|"')\n"
operator|+
literal|"return response:stream-binary($bin, 'media-type=application/octet-stream', ())"
decl_stmt|;
specifier|final
name|HttpResponse
name|response
init|=
name|postXquery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|HttpEntity
name|entity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|entity
operator|.
name|writeTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|BIN1_CONTENT
argument_list|,
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * {@see https://github.com/eXist-db/exist/issues/790#error-case-5}      *      * response:stream is used to return Base64 encoded binary.      */
annotation|@
name|Test
specifier|public
name|void
name|readAndStreamBinarySax
parameter_list|()
throws|throws
name|IOException
throws|,
name|JAXBException
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|randomData
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
comment|// 1MB
specifier|final
name|Path
name|tmpInFile
init|=
name|createTemporaryFile
argument_list|(
name|data
argument_list|)
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"import module namespace file = \"http://exist-db.org/xquery/file\";\n"
operator|+
literal|"import module namespace response = \"http://exist-db.org/xquery/response\";\n"
operator|+
literal|"let $bin := file:read-binary('"
operator|+
name|tmpInFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"')\n"
operator|+
literal|"return response:stream($bin, 'media-type=application/octet-stream')"
decl_stmt|;
specifier|final
name|HttpResponse
name|response
init|=
name|postXquery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|HttpEntity
name|entity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|entity
operator|.
name|writeTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|tmpInFile
argument_list|)
argument_list|,
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * {@see https://github.com/eXist-db/exist/issues/790#error-case-5}      *      * response:stream-binary is used to return raw binary.      */
annotation|@
name|Test
specifier|public
name|void
name|readAndStreamBinaryRaw
parameter_list|()
throws|throws
name|IOException
throws|,
name|JAXBException
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|randomData
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
comment|// 1MB
specifier|final
name|Path
name|tmpInFile
init|=
name|createTemporaryFile
argument_list|(
name|data
argument_list|)
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"import module namespace file = \"http://exist-db.org/xquery/file\";\n"
operator|+
literal|"import module namespace response = \"http://exist-db.org/xquery/response\";\n"
operator|+
literal|"let $bin := file:read-binary('"
operator|+
name|tmpInFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"')\n"
operator|+
literal|"return response:stream-binary($bin, 'media-type=application/octet-stream', ())"
decl_stmt|;
specifier|final
name|HttpResponse
name|response
init|=
name|postXquery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|HttpEntity
name|entity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|entity
operator|.
name|writeTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|tmpInFile
argument_list|)
argument_list|,
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|storeBinaryFile
parameter_list|(
specifier|final
name|XmldbURI
name|filePath
parameter_list|,
specifier|final
name|byte
index|[]
name|content
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|HttpResponse
name|response
init|=
name|executor
operator|.
name|execute
argument_list|(
name|Request
operator|.
name|Put
argument_list|(
name|getRestUrl
argument_list|()
operator|+
name|filePath
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/octet-stream"
argument_list|)
operator|.
name|bodyByteArray
argument_list|(
name|content
argument_list|)
argument_list|)
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|SC_CREATED
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Unable to store binary file: "
operator|+
name|filePath
argument_list|)
throw|;
block|}
block|}
specifier|private
name|String
name|getRestUrl
parameter_list|()
block|{
return|return
literal|"http://localhost:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
operator|+
literal|"/rest"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|removeCollection
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|HttpResponse
name|response
init|=
name|executor
operator|.
name|execute
argument_list|(
name|Request
operator|.
name|Delete
argument_list|(
name|getRestUrl
argument_list|()
operator|+
name|collectionUri
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|SC_OK
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Unable to delete collection: "
operator|+
name|collectionUri
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|QueryResultAccessor
argument_list|<
name|Result
argument_list|,
name|Exception
argument_list|>
name|executeXQuery
parameter_list|(
specifier|final
name|String
name|xquery
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|HttpResponse
name|response
init|=
name|postXquery
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
specifier|final
name|HttpEntity
name|entity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|entity
operator|.
name|getContent
argument_list|()
init|)
block|{
specifier|final
name|JAXBContext
name|jaxbContext
init|=
name|JAXBContext
operator|.
name|newInstance
argument_list|(
literal|"org.exist.http.jaxb"
argument_list|)
decl_stmt|;
specifier|final
name|Unmarshaller
name|unmarshaller
init|=
name|jaxbContext
operator|.
name|createUnmarshaller
argument_list|()
decl_stmt|;
specifier|final
name|Result
name|result
init|=
operator|(
name|Result
operator|)
name|unmarshaller
operator|.
name|unmarshal
argument_list|(
name|is
argument_list|)
decl_stmt|;
return|return
name|consumer
lambda|->
name|consumer
operator|.
name|accept
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
specifier|private
name|HttpResponse
name|postXquery
parameter_list|(
specifier|final
name|String
name|xquery
parameter_list|)
throws|throws
name|JAXBException
throws|,
name|IOException
block|{
specifier|final
name|Query
name|query
init|=
operator|new
name|Query
argument_list|()
decl_stmt|;
name|query
operator|.
name|setText
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
specifier|final
name|JAXBContext
name|jaxbContext
init|=
name|JAXBContext
operator|.
name|newInstance
argument_list|(
literal|"org.exist.http.jaxb"
argument_list|)
decl_stmt|;
specifier|final
name|Marshaller
name|marshaller
init|=
name|jaxbContext
operator|.
name|createMarshaller
argument_list|()
decl_stmt|;
specifier|final
name|HttpResponse
name|response
decl_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|marshaller
operator|.
name|marshal
argument_list|(
name|query
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|response
operator|=
name|executor
operator|.
name|execute
argument_list|(
name|Request
operator|.
name|Post
argument_list|(
name|getRestUrl
argument_list|()
operator|+
literal|"/db/"
argument_list|)
operator|.
name|bodyByteArray
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_XML
argument_list|)
argument_list|)
operator|.
name|returnResponse
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|SC_OK
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to query, HTTP response code: "
operator|+
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|response
return|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|size
parameter_list|(
specifier|final
name|Result
name|result
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|result
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Result
operator|.
name|Value
name|item
parameter_list|(
specifier|final
name|Result
name|results
parameter_list|,
specifier|final
name|int
name|index
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|results
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isBinaryType
parameter_list|(
specifier|final
name|Result
operator|.
name|Value
name|item
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|type
init|=
name|item
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
literal|"xs:base64Binary"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"xs:hexBinary"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isBooleanType
parameter_list|(
name|Result
operator|.
name|Value
name|item
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|"xs:boolean"
operator|.
name|equals
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|getBytes
parameter_list|(
specifier|final
name|Result
operator|.
name|Value
name|item
parameter_list|)
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|item
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
literal|"xs:base64Binary"
case|:
return|return
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|item
operator|.
name|getContent
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
case|case
literal|"xs:hexBinary"
case|:
return|return
name|Hex
operator|.
name|decodeHex
argument_list|(
name|item
operator|.
name|getContent
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|getBoolean
parameter_list|(
specifier|final
name|Result
operator|.
name|Value
name|item
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|item
operator|.
name|getContent
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

