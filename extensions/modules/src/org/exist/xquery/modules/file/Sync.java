begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|file
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
name|FileOutputStream
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Date
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
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|*
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
name|dom
operator|.
name|DOMSource
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
name|sax
operator|.
name|SAXResult
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
name|sax
operator|.
name|SAXTransformerFactory
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
name|sax
operator|.
name|TemplatesHandler
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
name|sax
operator|.
name|TransformerHandler
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
name|stream
operator|.
name|StreamSource
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
name|Receiver
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
name|ReceiverToSAX
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
name|DateTimeValue
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
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|TransformerFactoryAllocator
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

begin_class
specifier|public
class|class
name|Sync
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
literal|"sync"
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|FileModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Synchronize a collection with a directory hierarchy. Compares last modified time stamps. "
operator|+
literal|"If $dateTime is given, only resources modified after this time stamp are taken into account. "
operator|+
literal|"This method is only available to the DBA role."
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The collection to sync."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"targetPath"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full path or URI to the directory"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"dateTime"
argument_list|,
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Optional: only resources modified after the given date/time will be synchronized."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true if successful, false otherwise"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Properties
name|DEFAULT_PROPERTIES
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|DEFAULT_PROPERTIES
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|DEFAULT_PROPERTIES
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|DEFAULT_PROPERTIES
operator|.
name|put
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sync
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
if|if
condition|(
operator|!
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Function file:sync is only available to the DBA role"
argument_list|)
throw|;
name|String
name|collectionPath
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Date
name|startDate
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|2
index|]
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|DateTimeValue
name|dtv
init|=
operator|(
name|DateTimeValue
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
name|startDate
operator|=
name|dtv
operator|.
name|getDate
argument_list|()
expr_stmt|;
block|}
name|String
name|target
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|File
name|targetDir
init|=
name|FileModuleHelper
operator|.
name|getFile
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|output
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|targetDir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|File
name|home
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|targetDir
operator|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|output
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sync"
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"collection"
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
name|collectionPath
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"dir"
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
name|targetDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|saveCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|collectionPath
argument_list|)
argument_list|,
name|targetDir
argument_list|,
name|startDate
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|output
operator|.
name|endDocument
argument_list|()
expr_stmt|;
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
argument_list|)
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
return|return
name|output
operator|.
name|getDocument
argument_list|()
return|;
block|}
specifier|private
name|void
name|saveCollection
parameter_list|(
name|XmldbURI
name|collectionPath
parameter_list|,
name|File
name|targetDir
parameter_list|,
name|Date
name|startDate
parameter_list|,
name|MemTreeBuilder
name|output
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
operator|!
name|targetDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|targetDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"Failed to create output directory: "
operator|+
name|targetDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" for collection "
operator|+
name|collectionPath
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|targetDir
operator|.
name|canWrite
argument_list|()
condition|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"Failed to write to output directory: "
operator|+
name|targetDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|XmldbURI
argument_list|>
name|subcollections
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|openCollection
argument_list|(
name|collectionPath
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
name|reportError
argument_list|(
name|output
argument_list|,
literal|"Collection not found: "
operator|+
name|collectionPath
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|collection
operator|.
name|iterator
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|startDate
operator|==
literal|null
operator|||
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
operator|>
name|startDate
operator|.
name|getTime
argument_list|()
condition|)
block|{
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
name|saveBinary
argument_list|(
name|targetDir
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|doc
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|saveXML
argument_list|(
name|targetDir
argument_list|,
name|doc
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|subcollections
operator|=
operator|new
name|ArrayList
argument_list|<
name|XmldbURI
argument_list|>
argument_list|(
name|collection
operator|.
name|getChildCollectionCount
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|XmldbURI
argument_list|>
name|i
init|=
name|collection
operator|.
name|collectionIterator
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|subcollections
operator|.
name|add
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
name|collection
operator|.
name|getLock
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
for|for
control|(
name|XmldbURI
name|childURI
range|:
name|subcollections
control|)
block|{
name|File
name|childDir
init|=
operator|new
name|File
argument_list|(
name|targetDir
argument_list|,
name|childURI
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|saveCollection
argument_list|(
name|collectionPath
operator|.
name|append
argument_list|(
name|childURI
argument_list|)
argument_list|,
name|childDir
argument_list|,
name|startDate
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|reportError
parameter_list|(
name|MemTreeBuilder
name|output
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|output
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"error"
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|output
operator|.
name|characters
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|output
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|saveXML
parameter_list|(
name|File
name|targetDir
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|MemTreeBuilder
name|output
parameter_list|)
block|{
name|File
name|targetFile
init|=
operator|new
name|File
argument_list|(
name|targetDir
argument_list|,
name|doc
operator|.
name|getFileURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetFile
operator|.
name|exists
argument_list|()
operator|&&
name|targetFile
operator|.
name|lastModified
argument_list|()
operator|>=
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
condition|)
block|{
return|return;
block|}
name|boolean
name|isRepoXML
init|=
name|targetFile
operator|.
name|exists
argument_list|()
operator|&&
name|targetFile
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"repo.xml"
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
try|try
block|{
name|output
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"update"
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"file"
argument_list|)
argument_list|,
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|doc
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"collection"
argument_list|)
argument_list|,
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|)
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"modified"
argument_list|)
argument_list|,
operator|new
name|DateTimeValue
argument_list|(
operator|new
name|Date
argument_list|(
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isRepoXML
condition|)
block|{
name|processRepoDesc
argument_list|(
name|targetFile
argument_list|,
name|doc
argument_list|,
name|sax
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|DEFAULT_PROPERTIES
argument_list|)
expr_stmt|;
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|DEFAULT_PROPERTIES
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
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"IO error while saving file: "
operator|+
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"SAX exception while saving file "
operator|+
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|endElement
argument_list|()
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
block|}
block|}
comment|/**      * Merge repo.xml modified by user with original file. This is necessary because we have to      * remove sensitive information during upload (default password) and need to restore it      * when the package is synchronized back to disk.      */
specifier|private
name|void
name|processRepoDesc
parameter_list|(
name|File
name|targetFile
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|SAXSerializer
name|sax
parameter_list|,
name|MemTreeBuilder
name|output
parameter_list|)
block|{
try|try
block|{
name|DocumentBuilder
name|builder
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|original
init|=
name|builder
operator|.
name|parse
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|DEFAULT_PROPERTIES
argument_list|)
expr_stmt|;
name|StreamSource
name|stylesource
init|=
operator|new
name|StreamSource
argument_list|(
name|Sync
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"repo.xsl"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|SAXTransformerFactory
name|factory
init|=
name|TransformerFactoryAllocator
operator|.
name|getTransformerFactory
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|(
name|stylesource
argument_list|)
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setParameter
argument_list|(
literal|"original"
argument_list|,
name|original
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
operator|new
name|SAXResult
argument_list|(
name|sax
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|DEFAULT_PROPERTIES
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|handler
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"Parser exception while saving file "
operator|+
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"SAX exception while saving file "
operator|+
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"IO exception while saving file "
operator|+
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"Transformation exception while saving file "
operator|+
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|saveBinary
parameter_list|(
name|File
name|targetDir
parameter_list|,
name|BinaryDocument
name|binary
parameter_list|,
name|MemTreeBuilder
name|output
parameter_list|)
block|{
name|File
name|targetFile
init|=
operator|new
name|File
argument_list|(
name|targetDir
argument_list|,
name|binary
operator|.
name|getFileURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetFile
operator|.
name|exists
argument_list|()
operator|&&
name|targetFile
operator|.
name|lastModified
argument_list|()
operator|>=
name|binary
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
block|{
name|output
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"update"
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"file"
argument_list|)
argument_list|,
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|binary
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"collection"
argument_list|)
argument_list|,
name|binary
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|)
argument_list|,
literal|"binary"
argument_list|)
expr_stmt|;
name|output
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"modified"
argument_list|)
argument_list|,
operator|new
name|DateTimeValue
argument_list|(
operator|new
name|Date
argument_list|(
name|binary
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBinaryResource
argument_list|(
name|binary
argument_list|)
decl_stmt|;
name|int
name|c
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|c
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
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
literal|"IO error while saving file: "
operator|+
name|targetFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|output
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

