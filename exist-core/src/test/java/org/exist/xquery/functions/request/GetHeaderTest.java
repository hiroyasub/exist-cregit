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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLAssert
operator|.
name|assertXMLEqual
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
name|net
operator|.
name|URLEncoder
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
name|HttpStatus
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

begin_comment
comment|/**  * Tests expected behaviour of request:get-header() XQuery function  *   * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|GetHeaderTest
extends|extends
name|RESTTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|HTTP_HEADER_NAME
init|=
literal|"header1"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|xquery
init|=
literal|"<request-header name=\""
operator|+
name|HTTP_HEADER_NAME
operator|+
literal|"\">{request:get-header(\""
operator|+
name|HTTP_HEADER_NAME
operator|+
literal|"\")}</request-header>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetNoHeader
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|testGetHeader
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyHeader
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|testGetHeader
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHeaderValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|testGetHeader
argument_list|(
literal|"value1"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testGetHeader
parameter_list|(
name|String
name|headerValue
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|Request
name|request
init|=
name|Request
operator|.
name|Get
argument_list|(
name|getCollectionRootUri
argument_list|()
operator|+
literal|"?_query="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|xquery
argument_list|,
literal|"UTF-8"
argument_list|)
operator|+
literal|"&_indent=no&_wrap=no"
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|xmlExpectedResponse
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<request-header name=\""
operator|+
name|HTTP_HEADER_NAME
operator|+
literal|"\">"
argument_list|)
decl_stmt|;
if|if
condition|(
name|headerValue
operator|!=
literal|null
condition|)
block|{
name|request
operator|=
name|request
operator|.
name|addHeader
argument_list|(
name|HTTP_HEADER_NAME
argument_list|,
name|headerValue
argument_list|)
expr_stmt|;
name|xmlExpectedResponse
operator|.
name|append
argument_list|(
name|headerValue
argument_list|)
expr_stmt|;
block|}
name|xmlExpectedResponse
operator|.
name|append
argument_list|(
literal|"</request-header>"
argument_list|)
expr_stmt|;
specifier|final
name|HttpResponse
name|response
init|=
name|request
operator|.
name|execute
argument_list|()
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|os
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|response
operator|.
name|getEntity
argument_list|()
operator|.
name|writeTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|assertXMLEqual
argument_list|(
name|xmlExpectedResponse
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|String
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

