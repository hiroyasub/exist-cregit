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
name|memtree
operator|.
name|NodeImpl
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
name|Lock
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
name|serializer
operator|.
name|DOMStreamerPool
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
name|value
operator|.
name|AtomicValue
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
name|NodeValue
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
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|LexicalHandler
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
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * Local implementation of XMLResource.  */
end_comment

begin_class
specifier|public
class|class
name|LocalXMLResource
extends|extends
name|AbstractEXistResource
implements|implements
name|XMLResource
block|{
comment|//protected DocumentImpl document = null;
specifier|protected
name|NodeProxy
name|proxy
init|=
literal|null
decl_stmt|;
specifier|protected
name|Properties
name|outputProperties
init|=
literal|null
decl_stmt|;
specifier|protected
name|LexicalHandler
name|lexicalHandler
init|=
literal|null
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
specifier|protected
name|Date
name|datecreated
init|=
literal|null
decl_stmt|;
specifier|protected
name|Date
name|datemodified
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
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|super
argument_list|(
name|user
argument_list|,
name|pool
argument_list|,
name|parent
argument_list|,
name|did
argument_list|,
literal|"text/xml"
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
name|getDocument
argument_list|()
operator|.
name|getFileName
argument_list|()
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
comment|// Case 1: content is an external DOM node
if|else if
condition|(
name|root
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|root
operator|instanceof
name|NodeValue
operator|)
condition|)
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
name|getProperties
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
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
comment|// Case 2: content is an atomic value
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
comment|// Case 3: content is a file
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
comment|// Case 4: content is a document or internal node
block|}
else|else
block|{
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
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
name|serializer
operator|.
name|setProperties
argument_list|(
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
name|content
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|root
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
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
else|else
block|{
name|document
operator|=
name|openDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|READ_LOCK
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
name|content
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|document
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
name|closeDocument
argument_list|(
name|document
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|pool
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
block|{
if|if
condition|(
name|root
operator|instanceof
name|NodeImpl
condition|)
block|{
operator|(
operator|(
name|NodeImpl
operator|)
name|root
operator|)
operator|.
name|expand
argument_list|()
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
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
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|document
operator|=
name|getDocument
argument_list|(
name|broker
argument_list|,
literal|true
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
name|getDocumentElement
argument_list|()
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
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|releaseDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|pool
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
comment|// case 1: content is an external DOM node
if|if
condition|(
name|root
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|root
operator|instanceof
name|NodeValue
operator|)
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
name|DOMStreamer
name|streamer
init|=
name|DOMStreamerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowDOMStreamer
argument_list|()
decl_stmt|;
name|streamer
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|setLexicalHandler
argument_list|(
name|lexicalHandler
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|serialize
argument_list|(
name|root
argument_list|,
name|option
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|DOMStreamerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnDOMStreamer
argument_list|(
name|streamer
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
comment|// case 2: content is an atomic value
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
name|pool
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
comment|// case 3: content is an internal node or a document
block|}
else|else
block|{
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
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
name|serializer
operator|.
name|setProperties
argument_list|(
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|handler
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
name|serializer
operator|.
name|toSAX
argument_list|(
operator|(
name|NodeValue
operator|)
name|root
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|serializer
operator|.
name|toSAX
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
try|try
block|{
name|document
operator|=
name|openDocument
argument_list|(
name|broker
argument_list|,
name|Lock
operator|.
name|READ_LOCK
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
name|serializer
operator|.
name|toSAX
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeDocument
argument_list|(
name|document
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
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
name|pool
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
name|docId
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
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|DocumentImpl
name|document
init|=
name|getDocument
argument_list|(
name|broker
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|pool
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
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|DocumentImpl
name|document
init|=
name|getDocument
argument_list|(
name|broker
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getContentLength() 	 */
specifier|public
name|int
name|getContentLength
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
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|DocumentImpl
name|document
init|=
name|getDocument
argument_list|(
name|broker
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|document
operator|.
name|getContentLength
argument_list|()
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Sets the content for this resource. If value is of type File, it is 	 * directly passed to the parser when Collection.storeResource is called. 	 * Otherwise the method tries to convert the value to String. 	 *  	 * Passing a File object should be preferred if the document is large. The 	 * file's content will not be loaded into memory but directly passed to a 	 * SAX parser. 	 *  	 * @param value 	 *                   the content value to set for the resource. 	 * @exception XMLDBException 	 *                         with expected error codes.<br /><code>ErrorCodes.VENDOR_ERROR</code> 	 *                         for any vendor specific errors that occur.<br /> 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xmldb.EXistResource#getPermissions() 	 */
specifier|public
name|Permission
name|getPermissions
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
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|DocumentImpl
name|document
init|=
name|getDocument
argument_list|(
name|broker
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|document
operator|!=
literal|null
condition|?
name|document
operator|.
name|getPermissions
argument_list|()
else|:
literal|null
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
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#setLexicalHandler(org.xml.sax.ext.LexicalHandler) 	 */
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
name|LexicalHandler
name|handler
parameter_list|)
block|{
name|lexicalHandler
operator|=
name|handler
expr_stmt|;
block|}
specifier|protected
name|void
name|setProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|this
operator|.
name|outputProperties
operator|=
name|properties
expr_stmt|;
block|}
specifier|private
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
name|outputProperties
operator|==
literal|null
condition|?
name|parent
operator|.
name|properties
else|:
name|outputProperties
return|;
block|}
specifier|protected
name|DocumentImpl
name|getDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|boolean
name|lock
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|lock
condition|)
block|{
try|try
block|{
name|document
operator|=
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
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
literal|"Failed to acquire lock on document "
operator|+
name|docId
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|document
operator|=
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|document
operator|==
literal|null
condition|)
block|{
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
return|return
name|document
return|;
block|}
specifier|protected
name|NodeProxy
name|getNode
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
return|return
name|proxy
return|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|DocumentImpl
name|document
init|=
name|getDocument
argument_list|(
name|broker
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

