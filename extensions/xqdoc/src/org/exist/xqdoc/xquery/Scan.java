begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xqdoc
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|external
operator|.
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
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|*
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
name|xqdoc
operator|.
name|XQDocHelper
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
name|*
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
name|modules
operator|.
name|ModuleUtils
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
name|*
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
name|Document
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
name|xqdoc
operator|.
name|conversion
operator|.
name|XQDocException
import|;
end_import

begin_class
specifier|public
class|class
name|Scan
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"scan"
argument_list|,
name|XQDocModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XQDocModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Scan and extract function documentation from an external XQuery function module according to the"
operator|+
literal|"XQDoc specification. The single argument URI may either point to an XQuery module stored in the "
operator|+
literal|"db (URI starts with xmldb:exist:...) or a module in the file system. A file system module is "
operator|+
literal|"searched in the same way as if it were loaded through an \"import module\" statement. Static "
operator|+
literal|"mappings defined in conf.xml are searched first."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"uri"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URI from which to load the function module"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the function docs."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"scan"
argument_list|,
name|XQDocModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XQDocModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Scan and extract function documentation from an external XQuery function module according to the "
operator|+
literal|"XQDoc specification. The two parameter version of the function expects to get the source code of "
operator|+
literal|"the module in the first argument and a name for the module in the second."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"data"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The base64 encoded source data of the module"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the module"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the function docs."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Pattern
name|NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([^/\\.]+)\\.?[^\\.]*$"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NORMALIZE_XQUERY
init|=
literal|"resource:org/exist/xqdoc/xquery/normalize.xql"
decl_stmt|;
specifier|private
name|CompiledXQuery
name|normalizeXQuery
init|=
literal|null
decl_stmt|;
specifier|public
name|Scan
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
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
comment|//TODO ideally should be replaced by changing BinarySource to a streaming approach
specifier|private
name|byte
index|[]
name|binaryValueToByteArray
parameter_list|(
name|BinaryValue
name|binaryValue
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|binaryValue
operator|.
name|streamBinaryTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|Source
name|source
init|=
literal|null
decl_stmt|;
name|String
name|name
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
name|byte
name|data
index|[]
decl_stmt|;
try|try
block|{
name|data
operator|=
name|binaryValueToByteArray
argument_list|(
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
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|name
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|BinarySource
argument_list|(
name|data
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|uri
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
condition|)
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|XmldbURI
name|resourceURI
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|collection
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|openCollection
argument_list|(
name|resourceURI
operator|.
name|removeLastSegment
argument_list|()
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"collection not found: "
operator|+
name|resourceURI
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|doc
operator|=
name|collection
operator|.
name|getDocumentWithLock
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|resourceURI
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
operator|||
operator|!
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"application/xquery"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XQuery resource: "
operator|+
name|uri
operator|+
literal|" is not an XQuery or "
operator|+
literal|"declares a wrong mime-type"
argument_list|)
throw|;
block|}
name|source
operator|=
operator|new
name|DBSource
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|name
operator|=
name|doc
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
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
literal|"invalid module uri: "
operator|+
name|uri
operator|+
literal|": "
operator|+
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
literal|"internal lock error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// first check if the URI points to a registered module
name|String
name|location
init|=
name|context
operator|.
name|getModuleLocation
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|!=
literal|null
condition|)
name|uri
operator|=
name|location
expr_stmt|;
try|try
block|{
name|source
operator|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|context
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|,
name|uri
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|name
operator|=
name|extractName
argument_list|(
name|uri
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
literal|"failed to read module "
operator|+
name|uri
argument_list|,
name|e
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
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"permission denied to read module "
operator|+
name|uri
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
try|try
block|{
name|XQDocHelper
name|helper
init|=
operator|new
name|XQDocHelper
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
name|helper
operator|.
name|scan
argument_list|(
name|source
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|NodeValue
name|root
init|=
name|ModuleUtils
operator|.
name|stringToXML
argument_list|(
name|context
argument_list|,
name|xml
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
return|return
name|normalize
argument_list|(
operator|(
name|NodeValue
operator|)
operator|(
operator|(
name|Document
operator|)
name|root
operator|)
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XQDocException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"error while scanning module: "
operator|+
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
literal|"IO error while scanning module: "
operator|+
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
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"error while scanning module: "
operator|+
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
specifier|private
name|String
name|extractName
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
return|;
block|}
return|return
name|uri
return|;
block|}
specifier|private
name|Sequence
name|normalize
parameter_list|(
name|NodeValue
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
block|{
name|XQuery
name|xquery
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
if|if
condition|(
name|normalizeXQuery
operator|==
literal|null
condition|)
block|{
name|Source
name|source
init|=
operator|new
name|ClassLoaderSource
argument_list|(
name|NORMALIZE_XQUERY
argument_list|)
decl_stmt|;
name|XQueryContext
name|xc
init|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|INITIALIZE
argument_list|)
decl_stmt|;
try|try
block|{
name|normalizeXQuery
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|xc
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
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
argument_list|)
throw|;
block|}
block|}
try|try
block|{
name|normalizeXQuery
operator|.
name|getContext
argument_list|()
operator|.
name|declareVariable
argument_list|(
literal|"xqdoc:doc"
argument_list|,
name|input
argument_list|)
expr_stmt|;
return|return
name|xquery
operator|.
name|execute
argument_list|(
name|normalizeXQuery
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
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
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

