begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|contentextraction
operator|.
name|xquery
package|;
end_package

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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|contentextraction
operator|.
name|ContentExtraction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|contentextraction
operator|.
name|ContentExtractionException
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
name|dom
operator|.
name|StoredNode
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
name|DocumentBuilderReceiver
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
name|storage
operator|.
name|NodePath
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
name|AttrList
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
name|Receiver
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
name|FunctionCall
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
name|BinaryValue
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
name|FunctionReference
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
name|NodeValue
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
name|SequenceIterator
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

begin_comment
comment|/**  * @author Dulip Withanage<dulip.withanage@gmail.com>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|ContentFunctions
extends|extends
name|BasicFunction
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ContentFunctions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|getMeatadata
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-metadata"
argument_list|,
name|ContentExtractionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ContentExtractionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"extracts the metadata"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binary"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The binary data to extract from"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOCUMENT
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"Extracted metadata"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|getMetadataAndContent
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-metadata-and-content"
argument_list|,
name|ContentExtractionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ContentExtractionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"extracts the metadata and contents"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binary"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The binary data to extract from"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOCUMENT
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"Extracted content and metadata"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|streamContent
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"stream-content"
argument_list|,
name|ContentExtractionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ContentExtractionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"extracts the metadata"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binary"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The binary data to extract from"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"paths"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"A sequence of (simple) node paths which should be passed to the callback function"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"callback"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The callback function. Expected signature: "
operator|+
literal|"callback($node as node(), $userData as item()*, $retValue as item()*),"
operator|+
literal|"where $node is the currently processed node, $userData contains the data supplied in the "
operator|+
literal|"$userData parameter of stream-content, and $retValue is the return value of the previous "
operator|+
literal|"call to the callback function. The last two parameters are used for passing information "
operator|+
literal|"between the calling function and subsequent invocations of the callback function."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"namespaces"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Prefix/namespace mappings to be used for matching the paths. Pass an XML fragment with the following "
operator|+
literal|"structure:<namespaces><namespace prefix=\"prefix\" uri=\"uri\"/></namespaces>."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"userData"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Additional data which will be passed to the callback function."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|,
literal|"Returns empty sequence"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|ContentFunctions
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
comment|// is argument the empty sequence?
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
name|DocumentBuilderReceiver
name|builder
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|()
decl_stmt|;
name|ContentExtraction
name|ce
init|=
operator|new
name|ContentExtraction
argument_list|()
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"stream-content"
argument_list|)
condition|)
block|{
name|BinaryValue
name|binary
init|=
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
decl_stmt|;
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
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
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|3
index|]
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|NodeValue
name|namespaces
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|parseMappings
argument_list|(
name|namespaces
argument_list|,
name|mappings
argument_list|)
expr_stmt|;
block|}
return|return
name|streamContent
argument_list|(
name|ce
argument_list|,
name|binary
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|,
name|ref
argument_list|,
name|mappings
argument_list|,
name|args
index|[
literal|4
index|]
argument_list|)
return|;
block|}
else|else
block|{
try|try
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"get-metadata"
argument_list|)
condition|)
block|{
name|ce
operator|.
name|extractMetadata
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
argument_list|,
operator|(
name|ContentHandler
operator|)
name|builder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ce
operator|.
name|extractContentAndMetadata
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
argument_list|,
operator|(
name|ContentHandler
operator|)
name|builder
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ContentExtractionException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|parseMappings
parameter_list|(
name|NodeValue
name|namespaces
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|XMLStreamReader
name|reader
init|=
name|context
operator|.
name|getXMLStreamReader
argument_list|(
name|namespaces
argument_list|)
decl_stmt|;
name|reader
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
operator|&&
name|reader
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"namespace"
argument_list|)
condition|)
block|{
name|String
name|prefix
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"prefix"
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"uri"
argument_list|)
decl_stmt|;
name|mappings
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Error while parsing namespace mappings: "
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
literal|"Error while parsing namespace mappings: "
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
name|Sequence
name|streamContent
parameter_list|(
name|ContentExtraction
name|ce
parameter_list|,
name|BinaryValue
name|binary
parameter_list|,
name|Sequence
name|pathSeq
parameter_list|,
name|FunctionReference
name|ref
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|,
name|Sequence
name|data
parameter_list|)
throws|throws
name|XPathException
block|{
name|NodePath
index|[]
name|paths
init|=
operator|new
name|NodePath
index|[
name|pathSeq
operator|.
name|getItemCount
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|iter
init|=
name|pathSeq
operator|.
name|iterate
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
name|iter
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|paths
index|[
name|i
index|]
operator|=
operator|new
name|NodePath
argument_list|(
name|mappings
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|ContentReceiver
name|receiver
init|=
operator|new
name|ContentReceiver
argument_list|(
name|paths
argument_list|,
name|ref
argument_list|,
name|data
argument_list|)
decl_stmt|;
try|try
block|{
name|ce
operator|.
name|extractContentAndMetadata
argument_list|(
name|binary
argument_list|,
name|receiver
argument_list|)
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
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ContentExtractionException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
return|return
name|receiver
operator|.
name|getResult
argument_list|()
return|;
block|}
specifier|private
class|class
name|ContentReceiver
implements|implements
name|Receiver
block|{
specifier|private
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|private
name|FunctionReference
name|ref
decl_stmt|;
specifier|private
name|NodePath
name|currentPath
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
specifier|private
name|NodePath
index|[]
name|paths
decl_stmt|;
specifier|private
name|DocumentBuilderReceiver
name|receiver
init|=
literal|null
decl_stmt|;
specifier|private
name|NodePath
name|lastPath
init|=
literal|null
decl_stmt|;
specifier|private
name|Sequence
name|userData
init|=
literal|null
decl_stmt|;
specifier|private
name|Sequence
name|prevReturnData
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|ContentReceiver
parameter_list|(
name|NodePath
index|[]
name|paths
parameter_list|,
name|FunctionReference
name|ref
parameter_list|,
name|Sequence
name|userData
parameter_list|)
block|{
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|this
operator|.
name|userData
operator|=
name|userData
expr_stmt|;
block|}
specifier|protected
name|Sequence
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|matches
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|paths
index|[
name|i
index|]
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespaceURI
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|,
name|AttrList
name|attribs
parameter_list|)
throws|throws
name|SAXException
block|{
name|currentPath
operator|.
name|addComponent
argument_list|(
name|qname
argument_list|)
expr_stmt|;
if|if
condition|(
name|matches
argument_list|(
name|currentPath
argument_list|)
operator|&&
name|receiver
operator|==
literal|null
condition|)
block|{
name|lastPath
operator|=
name|currentPath
expr_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|receiver
operator|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|receiver
operator|!=
literal|null
condition|)
block|{
name|receiver
operator|.
name|startElement
argument_list|(
name|qname
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|receiver
operator|!=
literal|null
condition|)
block|{
name|receiver
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentPath
operator|.
name|match
argument_list|(
name|lastPath
argument_list|)
condition|)
block|{
name|Document
name|doc
init|=
name|receiver
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|NodeImpl
name|root
init|=
operator|(
name|NodeImpl
operator|)
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|receiver
operator|=
literal|null
expr_stmt|;
name|lastPath
operator|=
literal|null
expr_stmt|;
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
name|Sequence
index|[]
name|params
init|=
operator|new
name|Sequence
index|[
literal|3
index|]
decl_stmt|;
name|params
index|[
literal|0
index|]
operator|=
name|root
expr_stmt|;
name|params
index|[
literal|1
index|]
operator|=
name|userData
expr_stmt|;
name|params
index|[
literal|2
index|]
operator|=
name|prevReturnData
expr_stmt|;
try|try
block|{
name|Sequence
name|ret
init|=
name|ref
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|prevReturnData
operator|=
name|ret
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|currentPath
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|receiver
operator|!=
literal|null
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|receiver
operator|!=
literal|null
condition|)
block|{
name|receiver
operator|.
name|attribute
argument_list|(
name|qname
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|comment
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|cdataSection
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|documentType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|highlightText
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentNode
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

