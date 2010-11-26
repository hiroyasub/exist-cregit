begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|request
package|;
end_package

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|fail
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
name|List
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
name|httpclient
operator|.
name|HttpException
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
name|httpclient
operator|.
name|HttpMethod
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
name|httpclient
operator|.
name|HttpStatus
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
name|httpclient
operator|.
name|NameValuePair
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
name|httpclient
operator|.
name|methods
operator|.
name|GetMethod
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
name|httpclient
operator|.
name|methods
operator|.
name|PostMethod
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
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|FilePart
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
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|MultipartRequestEntity
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
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|Part
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
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|PartSource
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
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|StringPart
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
name|RESTTest
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
name|EXistResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|Test
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
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|BinaryResource
import|;
end_import

begin_comment
comment|/**  * Tests expected behaviour of request:get-parameter() XQuery function  *   * @author Adam Retter<adam@exist-db.org>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|GetParameterTest
extends|extends
name|RESTTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY
init|=
literal|"for $param-name in request:get-parameter-names() return for $param-value in request:get-parameter($param-name, ()) return fn:concat($param-name, '=', $param-value)"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY_FILENAME
init|=
literal|"test-get-parameter.xql"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_FILE_CONTENT
init|=
literal|"hello world"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_FILE_NAME
init|=
literal|"helloworld.txt"
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|root
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://localhost:8088/xmlrpc/db"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|BinaryResource
name|res
init|=
operator|(
name|BinaryResource
operator|)
name|root
operator|.
name|createResource
argument_list|(
name|XQUERY_FILENAME
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|res
operator|)
operator|.
name|setMimeType
argument_list|(
literal|"application/xquery"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|XQUERY
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|BinaryResource
name|res
init|=
operator|(
name|BinaryResource
operator|)
name|root
operator|.
name|getResource
argument_list|(
name|XQUERY_FILENAME
argument_list|)
decl_stmt|;
name|root
operator|.
name|removeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNoParameter
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|testGet
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostNoParameter
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|testPost
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEmptyParameter
parameter_list|()
block|{
name|testGet
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostEmptyParameter
parameter_list|()
block|{
name|testPost
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetSingleValueParameter
parameter_list|()
block|{
name|testGet
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostSingleValueParameter
parameter_list|()
block|{
name|testPost
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetMultiValueParameter
parameter_list|()
block|{
name|testGet
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|,
literal|"value2"
block|,
literal|"value3"
block|,
literal|"value4"
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostMultiValueParameter
parameter_list|()
block|{
name|testPost
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|,
literal|"value2"
block|,
literal|"value3"
block|,
literal|"value4"
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipartPostMultiValueParameter
parameter_list|()
block|{
name|testMultipartPost
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|,
literal|"value2"
block|,
literal|"value3"
block|,
literal|"value4"
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostMultiValueParameterWithQueryStringMultiValueParameter
parameter_list|()
block|{
name|testPost
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|,
literal|"value2"
block|,
literal|"value3"
block|,
literal|"value4"
block|}
argument_list|)
block|,             }
argument_list|,
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param2"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"valueA"
block|,
literal|"valueB"
block|,
literal|"valueC"
block|,
literal|"valueD"
block|}
argument_list|)
block|,             }
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPostMultiValueParameterWithQueryStringMultiValueParameterMerge
parameter_list|()
block|{
name|testPost
argument_list|(
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|,
literal|"value2"
block|,
literal|"value3"
block|,
literal|"value4"
block|}
argument_list|)
block|,             }
argument_list|,
operator|new
name|NameValues
index|[]
block|{
operator|new
name|NameValues
argument_list|(
literal|"param1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"valueA"
block|,
literal|"valueB"
block|,
literal|"valueC"
block|,
literal|"valueD"
block|}
argument_list|)
block|,             }
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testGet
parameter_list|(
name|NameValues
name|queryStringParams
index|[]
parameter_list|)
block|{
name|StringBuilder
name|expectedResponse
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|NameValuePair
name|qsParams
index|[]
init|=
name|convertNameValuesToNameValuePairs
argument_list|(
name|queryStringParams
argument_list|,
name|expectedResponse
argument_list|)
decl_stmt|;
name|GetMethod
name|get
init|=
operator|new
name|GetMethod
argument_list|(
name|COLLECTION_ROOT_URL
operator|+
literal|"/"
operator|+
name|XQUERY_FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|qsParams
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|get
operator|.
name|setQueryString
argument_list|(
name|qsParams
argument_list|)
expr_stmt|;
block|}
name|testRequest
argument_list|(
name|get
argument_list|,
name|expectedResponse
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testPost
parameter_list|(
name|NameValues
name|formParams
index|[]
parameter_list|)
block|{
name|StringBuilder
name|expectedResponse
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|NameValuePair
name|fParams
index|[]
init|=
name|convertNameValuesToNameValuePairs
argument_list|(
name|formParams
argument_list|,
name|expectedResponse
argument_list|)
decl_stmt|;
name|PostMethod
name|post
init|=
operator|new
name|PostMethod
argument_list|(
name|COLLECTION_ROOT_URL
operator|+
literal|"/"
operator|+
name|XQUERY_FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|fParams
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|post
operator|.
name|setRequestBody
argument_list|(
name|fParams
argument_list|)
expr_stmt|;
block|}
name|testRequest
argument_list|(
name|post
argument_list|,
name|expectedResponse
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testPost
parameter_list|(
name|NameValues
name|queryStringParams
index|[]
parameter_list|,
name|NameValues
name|formParams
index|[]
parameter_list|)
block|{
name|StringBuilder
name|expectedResponse
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|NameValuePair
name|qsParams
index|[]
init|=
name|convertNameValuesToNameValuePairs
argument_list|(
name|queryStringParams
argument_list|,
name|expectedResponse
argument_list|)
decl_stmt|;
name|NameValuePair
name|fParams
index|[]
init|=
name|convertNameValuesToNameValuePairs
argument_list|(
name|formParams
argument_list|,
name|expectedResponse
argument_list|)
decl_stmt|;
name|PostMethod
name|post
init|=
operator|new
name|PostMethod
argument_list|(
name|COLLECTION_ROOT_URL
operator|+
literal|"/"
operator|+
name|XQUERY_FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|qsParams
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|post
operator|.
name|setQueryString
argument_list|(
name|qsParams
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fParams
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|post
operator|.
name|setRequestBody
argument_list|(
name|fParams
argument_list|)
expr_stmt|;
block|}
name|testRequest
argument_list|(
name|post
argument_list|,
name|expectedResponse
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testMultipartPost
parameter_list|(
name|NameValues
name|formParams
index|[]
parameter_list|)
block|{
name|List
argument_list|<
name|Part
argument_list|>
name|parts
init|=
operator|new
name|ArrayList
argument_list|<
name|Part
argument_list|>
argument_list|()
decl_stmt|;
name|StringBuilder
name|expectedResponse
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|NameValuePair
name|nameValuePair
range|:
name|convertNameValuesToNameValuePairs
argument_list|(
name|formParams
argument_list|,
name|expectedResponse
argument_list|)
control|)
block|{
name|parts
operator|.
name|add
argument_list|(
operator|new
name|StringPart
argument_list|(
name|nameValuePair
operator|.
name|getName
argument_list|()
argument_list|,
name|nameValuePair
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//we will just send the XQuery as the file upload
name|parts
operator|.
name|add
argument_list|(
operator|new
name|FilePart
argument_list|(
literal|"fileupload"
argument_list|,
operator|new
name|PartSource
argument_list|()
block|{
specifier|private
name|byte
name|data
index|[]
init|=
name|TEST_FILE_CONTENT
operator|.
name|getBytes
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|data
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|TEST_FILE_NAME
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|createInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|PostMethod
name|post
init|=
operator|new
name|PostMethod
argument_list|(
name|COLLECTION_ROOT_URL
operator|+
literal|"/"
operator|+
name|XQUERY_FILENAME
argument_list|)
decl_stmt|;
name|post
operator|.
name|setRequestEntity
argument_list|(
operator|new
name|MultipartRequestEntity
argument_list|(
name|parts
operator|.
name|toArray
argument_list|(
operator|new
name|Part
index|[
name|parts
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|post
operator|.
name|getParams
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|testRequest
argument_list|(
name|post
argument_list|,
name|expectedResponse
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testRequest
parameter_list|(
name|HttpMethod
name|method
parameter_list|,
name|StringBuilder
name|expectedResponse
parameter_list|)
block|{
try|try
block|{
name|int
name|httpResult
init|=
name|client
operator|.
name|executeMethod
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|read
init|=
operator|-
literal|1
decl_stmt|;
name|StringBuilder
name|responseBody
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
name|method
operator|.
name|getResponseBodyAsStream
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|responseBody
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|httpResult
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResponse
operator|.
name|toString
argument_list|()
argument_list|,
name|responseBody
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpException
name|he
parameter_list|)
block|{
name|fail
argument_list|(
name|he
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|fail
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|method
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|NameValuePair
index|[]
name|convertNameValuesToNameValuePairs
parameter_list|(
name|NameValues
name|nameValues
index|[]
parameter_list|,
name|StringBuilder
name|expectedResponse
parameter_list|)
block|{
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|nameValuePairs
init|=
operator|new
name|ArrayList
argument_list|<
name|NameValuePair
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|nameValues
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|NameValues
name|param
range|:
name|nameValues
control|)
block|{
for|for
control|(
name|String
name|paramValue
range|:
name|param
operator|.
name|getValues
argument_list|()
control|)
block|{
name|nameValuePairs
operator|.
name|add
argument_list|(
operator|new
name|NameValuePair
argument_list|(
name|param
operator|.
name|getName
argument_list|()
argument_list|,
name|paramValue
argument_list|)
argument_list|)
expr_stmt|;
name|expectedResponse
operator|.
name|append
argument_list|(
name|param
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|expectedResponse
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|expectedResponse
operator|.
name|append
argument_list|(
name|paramValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|nameValuePairs
operator|.
name|toArray
argument_list|(
operator|new
name|NameValuePair
index|[
name|nameValuePairs
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
class|class
name|NameValues
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|String
name|values
index|[]
decl_stmt|;
specifier|public
name|NameValues
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|values
index|[]
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|String
index|[]
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
block|}
block|}
end_class

end_unit

