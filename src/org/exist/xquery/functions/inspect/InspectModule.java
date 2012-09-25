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
name|inspect
package|;
end_package

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
name|memtree
operator|.
name|MemTreeBuilder
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
name|DBSource
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
name|Source
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
name|SourceFactory
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
name|value
operator|.
name|*
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
name|helpers
operator|.
name|AttributesImpl
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
name|MalformedURLException
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

begin_class
specifier|public
class|class
name|InspectModule
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
literal|"inspect-module"
argument_list|,
name|InspectionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|InspectionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Compiles a module from source (without importing it) and returns a sequence of function items "
operator|+
literal|"defined in the module."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"location"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The location URI of the module to inspect"
argument_list|)
block|,             }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"An XML fragment describing the module and all functions contained in it."
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|MODULE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"module"
argument_list|)
decl_stmt|;
specifier|public
name|InspectModule
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
name|String
name|moduleLoadPath
init|=
name|context
operator|.
name|getModuleLoadPath
argument_list|()
decl_stmt|;
name|String
name|location
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|XQueryContext
name|tempContext
init|=
operator|new
name|XQueryContext
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|AccessContext
operator|.
name|XMLDB
argument_list|)
decl_stmt|;
name|ExternalModule
name|module
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|location
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
operator|||
operator|(
operator|(
name|location
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
operator|-
literal|1
operator|)
operator|&&
name|moduleLoadPath
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
operator|)
condition|)
block|{
comment|// Is the module source stored in the database?
try|try
block|{
name|XmldbURI
name|locationUri
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|moduleLoadPath
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
condition|)
block|{
name|XmldbURI
name|moduleLoadPathUri
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|moduleLoadPath
argument_list|)
decl_stmt|;
name|locationUri
operator|=
name|moduleLoadPathUri
operator|.
name|resolveCollectionPath
argument_list|(
name|locationUri
argument_list|)
expr_stmt|;
block|}
name|DocumentImpl
name|sourceDoc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sourceDoc
operator|=
name|tempContext
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|locationUri
operator|.
name|toCollectionPathURI
argument_list|()
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|sourceDoc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
literal|"Module location hint URI '"
operator|+
name|location
operator|+
literal|" does not refer to anything."
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|location
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|sourceDoc
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
operator|)
operator|||
operator|!
name|sourceDoc
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
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
literal|"Module location hint URI '"
operator|+
name|location
operator|+
literal|" does not refer to an XQuery."
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|location
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
name|DBSource
name|moduleSource
init|=
operator|new
name|DBSource
argument_list|(
name|tempContext
operator|.
name|getBroker
argument_list|()
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|sourceDoc
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|tempContext
operator|.
name|setModuleLoadPath
argument_list|(
literal|"xmldb:exist:///db"
argument_list|)
expr_stmt|;
name|module
operator|=
name|compile
argument_list|(
name|tempContext
argument_list|,
name|location
argument_list|,
name|moduleSource
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
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
literal|"Permission denied to read module source from location hint URI '"
operator|+
name|location
operator|+
literal|"."
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|location
argument_list|)
argument_list|)
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Error while loading XQuery module: "
operator|+
name|locationUri
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|sourceDoc
operator|!=
literal|null
condition|)
block|{
name|sourceDoc
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
block|}
block|}
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
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
literal|"Invalid module location hint URI '"
operator|+
name|location
operator|+
literal|"."
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|location
argument_list|)
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// No. Load from file or URL
try|try
block|{
name|Source
name|moduleSource
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|tempContext
operator|.
name|getBroker
argument_list|()
argument_list|,
name|moduleLoadPath
argument_list|,
name|location
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|module
operator|=
name|compile
argument_list|(
name|tempContext
argument_list|,
name|location
argument_list|,
name|moduleSource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
literal|"Invalid module location hint URI '"
operator|+
name|location
operator|+
literal|"."
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|location
argument_list|)
argument_list|)
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
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
literal|"Source for module not found module location hint URI '"
operator|+
name|location
operator|+
literal|"."
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|location
argument_list|)
argument_list|)
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
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
literal|"Permission denied to read module source from location hint URI '"
operator|+
name|location
operator|+
literal|"."
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|location
argument_list|)
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|module
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"uri"
argument_list|,
literal|"uri"
argument_list|,
literal|"CDATA"
argument_list|,
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"prefix"
argument_list|,
literal|"prefix"
argument_list|,
literal|"CDATA"
argument_list|,
name|module
operator|.
name|getDefaultPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|MODULE_QNAME
argument_list|,
name|attribs
argument_list|)
decl_stmt|;
for|for
control|(
name|FunctionSignature
name|sig
range|:
name|module
operator|.
name|listFunctions
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|ExternalModuleImpl
operator|)
name|module
operator|)
operator|.
name|isPrivate
argument_list|(
name|sig
argument_list|)
condition|)
block|{
name|InspectFunction
operator|.
name|generateDocs
argument_list|(
name|sig
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
specifier|private
name|ExternalModule
name|compile
parameter_list|(
name|XQueryContext
name|tempContext
parameter_list|,
name|String
name|location
parameter_list|,
name|Source
name|source
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
name|QName
name|qname
init|=
name|source
operator|.
name|isModule
argument_list|()
decl_stmt|;
if|if
condition|(
name|qname
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|tempContext
operator|.
name|compileModule
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|location
argument_list|,
name|source
argument_list|)
return|;
block|}
block|}
end_class

end_unit

