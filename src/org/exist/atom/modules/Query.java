begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * AtomProtocol.java  *  * Created on June 16, 2006, 11:39 AM  *  * (C) R. Alexander Milowski alex@milowski.com  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|modules
package|;
end_package

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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|Atom
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|IncomingMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|OutgoingMessage
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
name|http
operator|.
name|BadRequestException
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
name|NotFoundException
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
name|servlets
operator|.
name|HttpRequestWrapper
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
name|servlets
operator|.
name|HttpResponseWrapper
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
name|servlets
operator|.
name|RequestWrapper
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
name|servlets
operator|.
name|ResponseWrapper
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
name|security
operator|.
name|xacml
operator|.
name|AccessContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|URLSource
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
name|serializers
operator|.
name|Serializer
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
name|MimeTable
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
name|MimeType
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
name|serializer
operator|.
name|SAXSerializer
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
name|serializer
operator|.
name|SerializerPool
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
name|CompiledXQuery
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
name|XQuery
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
name|functions
operator|.
name|request
operator|.
name|RequestModule
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
name|functions
operator|.
name|response
operator|.
name|ResponseModule
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
name|functions
operator|.
name|session
operator|.
name|SessionModule
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  *  * @author R. Alexander Milowski  */
end_comment

begin_class
specifier|public
class|class
name|Query
extends|extends
name|AtomModuleBase
implements|implements
name|Atom
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Query
operator|.
name|class
argument_list|)
decl_stmt|;
name|MimeType
name|xqueryMimeType
decl_stmt|;
specifier|public
class|class
name|MethodConfiguration
block|{
name|String
name|contentType
decl_stmt|;
name|URLSource
name|querySource
decl_stmt|;
name|MethodConfiguration
parameter_list|()
block|{
name|querySource
operator|=
literal|null
expr_stmt|;
name|contentType
operator|=
name|Atom
operator|.
name|MIME_TYPE
expr_stmt|;
block|}
specifier|public
name|void
name|setContentType
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|contentType
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|contentType
return|;
block|}
specifier|public
name|URL
name|getQuerySource
parameter_list|()
block|{
return|return
name|querySource
operator|.
name|getURL
argument_list|()
return|;
block|}
specifier|public
name|void
name|setQuerySource
parameter_list|(
name|URL
name|source
parameter_list|)
block|{
name|this
operator|.
name|querySource
operator|=
name|source
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|URLSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|allowQueryPost
decl_stmt|;
name|Map
name|methods
decl_stmt|;
name|MethodConfiguration
name|get
decl_stmt|;
name|MethodConfiguration
name|post
decl_stmt|;
name|MethodConfiguration
name|put
decl_stmt|;
name|MethodConfiguration
name|delete
decl_stmt|;
name|MethodConfiguration
name|head
decl_stmt|;
comment|/** Creates a new instance of AtomProtocol */
specifier|public
name|Query
parameter_list|()
block|{
name|xqueryMimeType
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentType
argument_list|(
literal|"application/xquery"
argument_list|)
expr_stmt|;
name|methods
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|methods
operator|.
name|put
argument_list|(
literal|"GET"
argument_list|,
operator|new
name|MethodConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|methods
operator|.
name|put
argument_list|(
literal|"POST"
argument_list|,
operator|new
name|MethodConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|methods
operator|.
name|put
argument_list|(
literal|"PUT"
argument_list|,
operator|new
name|MethodConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|methods
operator|.
name|put
argument_list|(
literal|"DELETE"
argument_list|,
operator|new
name|MethodConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|methods
operator|.
name|put
argument_list|(
literal|"HEAD"
argument_list|,
operator|new
name|MethodConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|allowQueryPost
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|MethodConfiguration
name|getMethodConfiguration
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|MethodConfiguration
operator|)
name|methods
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|EXistException
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|get
operator|=
operator|(
name|MethodConfiguration
operator|)
name|methods
operator|.
name|get
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|post
operator|=
operator|(
name|MethodConfiguration
operator|)
name|methods
operator|.
name|get
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|put
operator|=
operator|(
name|MethodConfiguration
operator|)
name|methods
operator|.
name|get
argument_list|(
literal|"PUT"
argument_list|)
expr_stmt|;
name|delete
operator|=
operator|(
name|MethodConfiguration
operator|)
name|methods
operator|.
name|get
argument_list|(
literal|"DELETE"
argument_list|)
expr_stmt|;
name|head
operator|=
operator|(
name|MethodConfiguration
operator|)
name|methods
operator|.
name|get
argument_list|(
literal|"HEAD"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doGet
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
if|if
condition|(
name|get
operator|.
name|querySource
operator|!=
literal|null
condition|)
block|{
name|doQuery
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|get
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|doGet
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doPut
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
if|if
condition|(
name|put
operator|.
name|querySource
operator|!=
literal|null
condition|)
block|{
comment|// TODO: handle put body
name|doQuery
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|put
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|doGet
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doDelete
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
if|if
condition|(
name|delete
operator|.
name|querySource
operator|!=
literal|null
condition|)
block|{
name|doQuery
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|delete
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|doGet
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doHead
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
if|if
condition|(
name|head
operator|.
name|querySource
operator|!=
literal|null
condition|)
block|{
name|doQuery
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|head
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|doGet
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doPost
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
if|if
condition|(
name|post
operator|.
name|querySource
operator|!=
literal|null
condition|)
block|{
comment|// TODO: handle post body
name|doQuery
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|post
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|allowQueryPost
condition|)
block|{
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|request
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Collection "
operator|+
name|request
operator|.
name|getPath
argument_list|()
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
init|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|REST
argument_list|)
decl_stmt|;
name|context
operator|.
name|setModuleLoadPath
argument_list|(
name|getContext
argument_list|()
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|contentType
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Content-Type"
argument_list|)
decl_stmt|;
name|String
name|charset
init|=
name|getContext
argument_list|()
operator|.
name|getDefaultCharset
argument_list|()
decl_stmt|;
name|MimeType
name|mime
init|=
name|MimeType
operator|.
name|XML_TYPE
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|int
name|semicolon
init|=
name|contentType
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|semicolon
operator|>
literal|0
condition|)
block|{
name|contentType
operator|=
name|contentType
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|semicolon
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
name|mime
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|int
name|equals
init|=
name|contentType
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|,
name|semicolon
argument_list|)
decl_stmt|;
if|if
condition|(
name|equals
operator|>
literal|0
condition|)
block|{
name|String
name|param
init|=
name|contentType
operator|.
name|substring
argument_list|(
name|semicolon
operator|+
literal|1
argument_list|,
name|equals
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|param
operator|.
name|compareToIgnoreCase
argument_list|(
literal|"charset="
argument_list|)
operator|==
literal|0
condition|)
block|{
name|charset
operator|=
name|param
operator|.
name|substring
argument_list|(
name|equals
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|mime
operator|.
name|isXMLType
argument_list|()
operator|&&
operator|!
name|mime
operator|.
name|equals
argument_list|(
name|xqueryMimeType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"The xquery mime type is not an XML mime type nor application/xquery"
argument_list|)
throw|;
block|}
name|CompiledXQuery
name|compiledQuery
init|=
literal|null
decl_stmt|;
try|try
block|{
name|StringBuffer
name|builder
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Reader
name|r
init|=
operator|new
name|InputStreamReader
argument_list|(
name|request
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|contentLength
init|=
name|request
operator|.
name|getContentLength
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|r
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
operator|&&
name|count
operator|<
name|contentLength
condition|)
block|{
name|count
operator|+=
name|len
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|compiledQuery
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
operator|new
name|StringSource
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot compile xquery."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O exception while compiling xquery."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
operator|new
name|XmldbURI
index|[]
block|{
name|XmldbURI
operator|.
name|create
argument_list|(
name|request
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|AtomProtocol
operator|.
name|FEED_DOCUMENT_NAME
argument_list|)
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|Sequence
name|resultSequence
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiledQuery
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"No topic was found."
argument_list|)
throw|;
block|}
name|response
operator|.
name|setStatusCode
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|Atom
operator|.
name|MIME_TYPE
operator|+
literal|"; charset="
operator|+
name|charset
argument_list|)
expr_stmt|;
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|Writer
name|w
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|SAXSerializer
name|sax
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|w
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|resultSequence
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
name|w
operator|.
name|flush
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Cannot read resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O error on read of resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|saxe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Error while serializing XML: "
operator|+
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|resultSequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot execute xquery."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|doPost
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|declareVariables
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|XPathException
block|{
name|RequestWrapper
name|reqw
init|=
operator|new
name|HttpRequestWrapper
argument_list|(
name|request
argument_list|,
name|request
operator|.
name|getCharacterEncoding
argument_list|()
argument_list|,
name|request
operator|.
name|getCharacterEncoding
argument_list|()
argument_list|)
decl_stmt|;
name|ResponseWrapper
name|respw
init|=
operator|new
name|HttpResponseWrapper
argument_list|(
name|response
argument_list|)
decl_stmt|;
comment|//context.declareNamespace(RequestModule.PREFIX, RequestModule.NAMESPACE_URI);
name|context
operator|.
name|declareVariable
argument_list|(
name|RequestModule
operator|.
name|PREFIX
operator|+
literal|":request"
argument_list|,
name|reqw
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|ResponseModule
operator|.
name|PREFIX
operator|+
literal|":response"
argument_list|,
name|respw
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|SessionModule
operator|.
name|PREFIX
operator|+
literal|":session"
argument_list|,
name|reqw
operator|.
name|getSession
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doQuery
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|,
name|MethodConfiguration
name|config
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|request
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Collection "
operator|+
name|request
operator|.
name|getPath
argument_list|()
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|CompiledXQuery
name|feedQuery
init|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|config
operator|.
name|querySource
argument_list|)
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
if|if
condition|(
name|feedQuery
operator|==
literal|null
condition|)
block|{
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|REST
argument_list|)
expr_stmt|;
name|context
operator|.
name|setModuleLoadPath
argument_list|(
name|getContext
argument_list|()
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|feedQuery
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|config
operator|.
name|querySource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot compile xquery "
operator|+
name|config
operator|.
name|querySource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O exception while compiling xquery "
operator|+
name|config
operator|.
name|querySource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|context
operator|=
name|feedQuery
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|setModuleLoadPath
argument_list|(
name|getContext
argument_list|()
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
operator|new
name|XmldbURI
index|[]
block|{
name|XmldbURI
operator|.
name|create
argument_list|(
name|request
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|AtomProtocol
operator|.
name|FEED_DOCUMENT_NAME
argument_list|)
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|declareVariables
argument_list|(
name|context
argument_list|,
name|request
operator|.
name|getRequest
argument_list|()
argument_list|,
name|response
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|Sequence
name|resultSequence
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|feedQuery
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"No topic was found."
argument_list|)
throw|;
block|}
name|String
name|charset
init|=
name|getContext
argument_list|()
operator|.
name|getDefaultCharset
argument_list|()
decl_stmt|;
name|response
operator|.
name|setStatusCode
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|config
operator|.
name|contentType
operator|+
literal|"; charset="
operator|+
name|charset
argument_list|)
expr_stmt|;
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|Writer
name|w
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|SAXSerializer
name|sax
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|w
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|resultSequence
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
name|w
operator|.
name|flush
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Cannot read resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O error on read of resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|saxe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Error while serializing XML: "
operator|+
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|resultSequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot execute xquery "
operator|+
name|config
operator|.
name|querySource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|xquery
operator|.
name|getXQueryPool
argument_list|()
operator|.
name|returnCompiledXQuery
argument_list|(
name|config
operator|.
name|querySource
argument_list|,
name|feedQuery
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isQueryByPostAllowed
parameter_list|()
block|{
return|return
name|allowQueryPost
return|;
block|}
specifier|public
name|void
name|setQueryByPost
parameter_list|(
name|boolean
name|allowed
parameter_list|)
block|{
name|this
operator|.
name|allowQueryPost
operator|=
name|allowed
expr_stmt|;
block|}
block|}
end_class

end_unit

