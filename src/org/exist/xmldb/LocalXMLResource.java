begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
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
name|EXistException
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
name|NodeProxy
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
name|XMLUtil
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
name|Permission
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
name|User
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|serializer
operator|.
name|DOMSerializer
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
name|DOMStreamer
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
name|IncludeXMLFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
operator|.
name|value
operator|.
name|AtomicValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|ContentHandler
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
name|xml
operator|.
name|sax
operator|.
name|SAXNotRecognizedException
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
name|SAXNotSupportedException
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
name|ErrorCodes
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

begin_comment
comment|/**  * Local implementation of XMLResource.  */
end_comment

begin_class
specifier|public
class|class
name|LocalXMLResource
implements|implements
name|XMLResourceImpl
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LocalXMLResource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|protected
name|String
name|docId
init|=
literal|null
decl_stmt|;
specifier|protected
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
specifier|protected
name|LocalCollection
name|parent
decl_stmt|;
specifier|protected
name|NodeProxy
name|proxy
init|=
literal|null
decl_stmt|;
specifier|protected
name|long
name|id
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|User
name|user
decl_stmt|;
comment|// those are the different types of content this resource
comment|// may have to deal with
specifier|protected
name|String
name|content
init|=
literal|null
decl_stmt|;
specifier|protected
name|File
name|file
init|=
literal|null
decl_stmt|;
specifier|protected
name|Node
name|root
init|=
literal|null
decl_stmt|;
specifier|protected
name|AtomicValue
name|value
init|=
literal|null
decl_stmt|;
specifier|public
name|LocalXMLResource
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|parent
parameter_list|,
name|String
name|did
parameter_list|,
name|long
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
if|if
condition|(
name|did
operator|!=
literal|null
operator|&&
name|did
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>
operator|-
literal|1
condition|)
name|did
operator|=
name|did
operator|.
name|substring
argument_list|(
name|did
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|docId
operator|=
name|did
expr_stmt|;
block|}
specifier|public
name|LocalXMLResource
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|parent
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|docId
operator|=
name|doc
operator|.
name|getFileName
argument_list|()
expr_stmt|;
if|if
condition|(
name|docId
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>
operator|-
literal|1
condition|)
name|docId
operator|=
name|docId
operator|.
name|substring
argument_list|(
name|docId
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LocalXMLResource
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|parent
parameter_list|,
name|NodeProxy
name|p
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
argument_list|(
name|user
argument_list|,
name|pool
argument_list|,
name|parent
argument_list|,
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|)
expr_stmt|;
name|this
operator|.
name|proxy
operator|=
name|p
expr_stmt|;
block|}
specifier|public
name|Object
name|getContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
return|return
name|content
return|;
if|else if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|DOMSerializer
name|serializer
init|=
operator|new
name|DOMSerializer
argument_list|(
name|writer
argument_list|,
name|parent
operator|.
name|properties
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|content
operator|=
name|writer
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
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
return|return
name|content
return|;
block|}
if|else if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|value
operator|.
name|getStringValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
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
block|}
if|else if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|content
operator|=
name|XMLUtil
operator|.
name|readFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
return|return
name|content
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while reading resource contents"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
name|getDocument
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|document
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"permission denied to read resource"
argument_list|)
throw|;
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
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|parent
operator|.
name|properties
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|<
literal|0
condition|)
name|content
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|document
argument_list|)
expr_stmt|;
else|else
block|{
if|if
condition|(
name|proxy
operator|==
literal|null
condition|)
name|proxy
operator|=
operator|new
name|NodeProxy
argument_list|(
name|document
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|content
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
return|return
name|content
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
name|saxe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|saxe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Node
name|getContentAsDOM
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
return|return
name|root
return|;
if|else if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"cannot return an atomic value as DOM node"
argument_list|)
throw|;
block|}
else|else
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
name|getDocument
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|document
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"permission denied to read resource"
argument_list|)
throw|;
if|if
condition|(
name|id
operator|<
literal|0
condition|)
return|return
name|document
operator|.
name|getDocumentElement
argument_list|()
return|;
if|else if
condition|(
name|proxy
operator|!=
literal|null
condition|)
return|return
name|document
operator|.
name|getNode
argument_list|(
name|proxy
argument_list|)
return|;
else|else
return|return
name|document
operator|.
name|getNode
argument_list|(
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|getContentAsSAX
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|String
name|option
init|=
name|parent
operator|.
name|properties
operator|.
name|getProperty
argument_list|(
name|Serializer
operator|.
name|GENERATE_DOC_EVENTS
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
if|if
condition|(
name|option
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"false"
argument_list|)
condition|)
name|handler
operator|=
operator|new
name|IncludeXMLFilter
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|DOMStreamer
name|streamer
init|=
operator|new
name|DOMStreamer
argument_list|(
name|handler
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|streamer
operator|.
name|stream
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
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
block|}
if|else if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|value
operator|.
name|toSAX
argument_list|(
name|broker
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
name|getDocument
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|document
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"permission denied to read resource"
argument_list|)
throw|;
name|String
name|option
init|=
name|parent
operator|.
name|properties
operator|.
name|getProperty
argument_list|(
name|Serializer
operator|.
name|GENERATE_DOC_EVENTS
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
name|parent
operator|.
name|properties
operator|.
name|setProperty
argument_list|(
name|Serializer
operator|.
name|GENERATE_DOC_EVENTS
argument_list|,
name|option
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
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|String
name|xml
decl_stmt|;
try|try
block|{
name|serializer
operator|.
name|setProperties
argument_list|(
name|parent
operator|.
name|properties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|<
literal|0
condition|)
name|serializer
operator|.
name|toSAX
argument_list|(
name|document
argument_list|)
expr_stmt|;
else|else
block|{
if|if
condition|(
name|proxy
operator|==
literal|null
condition|)
name|proxy
operator|=
operator|new
name|NodeProxy
argument_list|(
name|document
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
name|saxe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|saxe
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
if|if
condition|(
name|document
operator|==
literal|null
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"document object is null"
argument_list|)
expr_stmt|;
return|return
name|document
return|;
block|}
specifier|protected
name|void
name|getDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|document
operator|!=
literal|null
condition|)
return|return;
try|try
block|{
name|String
name|path
init|=
operator|(
name|parent
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|?
literal|'/'
operator|+
name|docId
else|:
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|docId
operator|)
decl_stmt|;
name|document
operator|=
operator|(
name|DocumentImpl
operator|)
name|broker
operator|.
name|getDocument
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|NodeProxy
name|getNode
parameter_list|()
block|{
name|getDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|id
operator|<
literal|0
condition|)
comment|// this XMLResource represents a document
return|return
operator|new
name|NodeProxy
argument_list|(
name|document
argument_list|,
literal|1
argument_list|)
return|;
return|return
name|proxy
operator|==
literal|null
condition|?
operator|new
name|NodeProxy
argument_list|(
name|document
argument_list|,
name|id
argument_list|)
else|:
name|proxy
return|;
block|}
specifier|public
name|String
name|getDocumentId
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|docId
return|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|id
operator|<
literal|0
condition|?
name|docId
else|:
name|Long
operator|.
name|toString
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|Collection
name|getParentCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"collection parent is null"
argument_list|)
throw|;
return|return
name|parent
return|;
block|}
specifier|public
name|String
name|getResourceType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"XMLResource"
return|;
block|}
specifier|public
name|Date
name|getCreationTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
name|getDocument
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|document
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"permission denied to read resource"
argument_list|)
throw|;
return|return
operator|new
name|Date
argument_list|(
name|document
operator|.
name|getCreated
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
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
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Date
name|getLastModificationTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
name|getDocument
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|document
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"permission denied to read resource"
argument_list|)
throw|;
return|return
operator|new
name|Date
argument_list|(
name|document
operator|.
name|getLastModified
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
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
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Sets the content for this resource. If value is of type 		* File, it is directly passed to the parser when  		* Collection.storeResource is called. Otherwise the method 		* tries to convert the value to String. 		* 		* Passing a File object should be preferred if the document 		* is large. The file's content will not be loaded into memory 		* but directly passed to a SAX parser. 	 * 	 * @param value the content value to set for the resource. 	 * @exception XMLDBException with expected error codes.<br /> 	 *<code>ErrorCodes.VENDOR_ERROR</code> for any vendor 	 *  specific errors that occur.<br />  	 */
specifier|public
name|void
name|setContent
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|content
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|File
condition|)
name|file
operator|=
operator|(
name|File
operator|)
name|obj
expr_stmt|;
if|else if
condition|(
name|obj
operator|instanceof
name|AtomicValue
condition|)
name|value
operator|=
operator|(
name|AtomicValue
operator|)
name|obj
expr_stmt|;
else|else
block|{
name|content
operator|=
name|obj
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setContentAsDOM
parameter_list|(
name|Node
name|root
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
specifier|public
name|ContentHandler
name|setContentAsSAX
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
operator|new
name|InternalXMLSerializer
argument_list|()
return|;
block|}
specifier|private
class|class
name|InternalXMLSerializer
extends|extends
name|SAXSerializer
block|{
specifier|public
name|InternalXMLSerializer
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|StringWriter
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** 		 * @see org.xml.sax.DocumentHandler#endDocument() 		 */
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|content
operator|=
name|getWriter
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|getSAXFeature
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|SAXNotRecognizedException
throws|,
name|SAXNotSupportedException
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|setSAXFeature
parameter_list|(
name|String
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|)
throws|throws
name|SAXNotRecognizedException
throws|,
name|SAXNotSupportedException
block|{
block|}
block|}
end_class

end_unit

