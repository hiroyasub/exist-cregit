begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|expath
operator|.
name|exist
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
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|dom
operator|.
name|persistent
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
name|persistent
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
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
name|Element
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

begin_comment
comment|/**  * Created by Alister Pillow on 10/07/2014.  */
end_comment

begin_class
specifier|public
class|class
name|ZipFileFunctions
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ZipFileFunctions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|HREF_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"href"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URI for locating the Zip file"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|ENTRY_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A zip:entry element describing the contents of the file"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|FILE_ENTRIES
init|=
literal|"entries"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ZIP_FILE
init|=
literal|"zip-file"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|UPDATE_ENTRIES
init|=
literal|"update"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
comment|//zip:entries($href as xs:anyURI) as as element(zip:file)
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|FILE_ENTRIES
argument_list|,
name|ZipModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ZipModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a zip:file element that describes the hierarchical structure of the ZIP file identified by $href in terms of ZIP entries"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|HREF_PARAM
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
name|EXACTLY_ONE
argument_list|,
literal|"The document node containing a zip:entry"
argument_list|)
argument_list|)
block|,
comment|//zip:zip-file($zip as element(zip:file)) as empty-sequence()
comment|/*            new FunctionSignature(                     new QName(ZIP_FILE, ZipModule.NAMESPACE_URI, ZipModule.PREFIX),                     "Creates a new zip file at zip:file/@href using the children specified within the element",                     new SequenceType[]{                             ENTRY_PARAM                     },                     new FunctionReturnSequenceType(Type.EMPTY, Cardinality.EMPTY, "The empty sequence.")             ),*/
comment|//zip:update-entries($zip as element(zip:file), $output as xs:anyURI) as empty-sequence()
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|UPDATE_ENTRIES
argument_list|,
name|ZipModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ZipModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a copy of the zip file at $href, after replacing or adding each binary using the matching path/filename in $paths."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"href"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URI for locating the Zip file"
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
name|ONE_OR_MORE
argument_list|,
literal|"a sequence of file paths"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binaries"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"a sequence of binaries matching the paths"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The new zipped data or the empty sequence if the numbers of $paths and $binaries are different"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/**      * SendRequestFunction Constructor      *      * @param context   The Context of the calling XQuery      * @param signature The actual signature of the function      */
specifier|public
name|ZipFileFunctions
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
name|Sequence
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
name|FILE_ENTRIES
argument_list|)
condition|)
block|{
name|XmldbURI
name|uri
init|=
operator|(
operator|(
name|AnyURIValue
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
operator|)
operator|.
name|toXmldbURI
argument_list|()
decl_stmt|;
name|result
operator|=
name|extractEntries
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|ZIP_FILE
argument_list|)
condition|)
block|{
name|Element
name|zipEntry
init|=
operator|(
name|Element
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
name|result
operator|=
name|createZip
argument_list|(
name|zipEntry
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|UPDATE_ENTRIES
argument_list|)
condition|)
block|{
name|XmldbURI
name|uri
init|=
operator|(
operator|(
name|AnyURIValue
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
operator|)
operator|.
name|toXmldbURI
argument_list|()
decl_stmt|;
name|String
index|[]
name|paths
init|=
name|getPaths
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|BinaryValue
index|[]
name|newData
init|=
name|getBinaryData
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|result
operator|=
name|updateZip
argument_list|(
name|uri
argument_list|,
name|paths
argument_list|,
name|newData
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Sequence
name|updateZip
parameter_list|(
name|XmldbURI
name|uri
parameter_list|,
name|String
index|[]
name|paths
parameter_list|,
name|BinaryValue
index|[]
name|binaries
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|paths
operator|.
name|length
operator|!=
name|binaries
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Different number of paths ("
operator|+
name|paths
operator|.
name|length
operator|+
literal|") and binaries ("
operator|+
name|binaries
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|ZipFileSource
name|zipFileSource
init|=
operator|new
name|ZipFileFromDb
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|ZipInputStream
name|zis
init|=
literal|null
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|BinaryValue
argument_list|>
name|binariesTable
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|BinaryValue
argument_list|>
argument_list|(
name|paths
operator|.
name|length
argument_list|)
decl_stmt|;
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
name|binariesTable
operator|.
name|put
argument_list|(
name|paths
index|[
name|i
index|]
argument_list|,
name|binaries
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|zis
operator|=
name|zipFileSource
operator|.
name|getStream
argument_list|()
expr_stmt|;
name|ZipOutputStream
name|zos
init|=
operator|new
name|ZipOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
comment|// zos is the output - the result
name|ZipEntry
name|ze
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|16384
index|]
decl_stmt|;
name|int
name|bytes_read
decl_stmt|;
while|while
condition|(
operator|(
name|ze
operator|=
name|zis
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|zen
init|=
name|ze
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|binariesTable
operator|.
name|containsKey
argument_list|(
name|zen
argument_list|)
condition|)
block|{
comment|// Replace this entry
name|ZipEntry
name|nze
init|=
operator|new
name|ZipEntry
argument_list|(
name|zen
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
name|nze
argument_list|)
expr_stmt|;
name|binariesTable
operator|.
name|get
argument_list|(
name|zen
argument_list|)
operator|.
name|streamBinaryTo
argument_list|(
name|zos
argument_list|)
expr_stmt|;
name|binariesTable
operator|.
name|remove
argument_list|(
name|zen
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// copy this entry to output
if|if
condition|(
name|ze
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// can't add empty directory to Zip
name|ZipEntry
name|dirEntry
init|=
operator|new
name|ZipEntry
argument_list|(
name|ze
operator|.
name|getName
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"."
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
name|dirEntry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// copy file across
name|ZipEntry
name|nze
init|=
operator|new
name|ZipEntry
argument_list|(
name|zen
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
name|nze
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|bytes_read
operator|=
name|zis
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
name|zos
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytes_read
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// add any remaining items as NEW entries
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|BinaryValue
argument_list|>
name|entry
range|:
name|binariesTable
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ZipEntry
name|nze
init|=
operator|new
name|ZipEntry
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
name|nze
argument_list|)
expr_stmt|;
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|streamBinaryTo
argument_list|(
name|zos
argument_list|)
expr_stmt|;
block|}
name|zos
operator|.
name|close
argument_list|()
expr_stmt|;
name|zis
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|BinaryValueFromInputStream
operator|.
name|getInstance
argument_list|(
name|context
argument_list|,
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"IO Exception in zip:update"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Permission denied to read the source zip"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Sequence
name|extractEntries
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|XPathException
block|{
name|ZipFileSource
name|zipFileSource
init|=
operator|new
name|ZipFileFromDb
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|ZipInputStream
name|zis
init|=
literal|null
decl_stmt|;
name|Sequence
name|xmlResponse
init|=
literal|null
decl_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"file"
argument_list|,
name|ZipModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ZipModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"href"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|zis
operator|=
name|zipFileSource
operator|.
name|getStream
argument_list|()
expr_stmt|;
name|ZipEntry
name|zipEntry
decl_stmt|;
while|while
condition|(
operator|(
name|zipEntry
operator|=
name|zis
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|zipEntry
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"dir"
argument_list|,
name|ZipModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ZipModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|zipEntry
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"file: "
operator|+
name|zipEntry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"entry"
argument_list|,
name|ZipModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ZipModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|zipEntry
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Permission denied to read the source zip"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"IO exception while reading the source zip"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|xmlResponse
operator|=
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
return|return
operator|(
name|xmlResponse
operator|)
return|;
block|}
specifier|private
name|Sequence
name|createZip
parameter_list|(
name|Element
name|zipFile
parameter_list|)
block|{
name|Node
name|child
init|=
name|zipFile
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"processing zipFile: "
operator|+
name|zipFile
operator|.
name|getAttribute
argument_list|(
literal|"href"
argument_list|)
argument_list|)
expr_stmt|;
comment|// if this IS the zip:entry, then the src attribute will tell us where to write the output file.
comment|// if it has no src
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
comment|//Parse each of the child nodes
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
comment|//&& child.hasChildNodes()) {
name|Element
name|e
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
comment|// I need to be able to handle a dir element because that's in the SPEC
name|String
name|s
init|=
name|e
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"entry"
argument_list|)
condition|)
block|{
comment|// process the entry by finding the content, serializing according to the attributes, and streaming into the new zip file
name|logger
operator|.
name|debug
argument_list|(
literal|"zip:entry name: "
operator|+
name|e
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|" src: "
operator|+
name|e
operator|.
name|getAttribute
argument_list|(
literal|"src"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"dir"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"zip:entry contains dir: "
operator|+
name|e
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|" src: "
operator|+
name|e
operator|.
name|getAttribute
argument_list|(
literal|"src"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
comment|// copied from
specifier|public
interface|interface
name|ZipFileSource
block|{
specifier|public
name|ZipInputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
function_decl|;
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
block|}
specifier|private
class|class
name|ZipFileFromDb
implements|implements
name|ZipFileSource
block|{
specifier|private
name|BinaryDocument
name|binaryDoc
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|XmldbURI
name|uri
decl_stmt|;
specifier|public
name|ZipFileFromDb
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ZipInputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
block|{
if|if
condition|(
name|binaryDoc
operator|==
literal|null
condition|)
block|{
name|binaryDoc
operator|=
name|getDoc
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ZipInputStream
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBinaryResource
argument_list|(
name|binaryDoc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|binaryDoc
operator|!=
literal|null
condition|)
block|{
name|binaryDoc
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
specifier|private
name|BinaryDocument
name|getDoc
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|DocumentImpl
name|doc
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|uri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
operator|||
name|doc
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|BinaryDocument
operator|)
name|doc
return|;
block|}
block|}
comment|// copied from AccountManagementFunction
specifier|private
name|String
index|[]
name|getPaths
parameter_list|(
specifier|final
name|Sequence
name|seq
parameter_list|)
block|{
specifier|final
name|String
name|paths
index|[]
init|=
operator|new
name|String
index|[
name|seq
operator|.
name|getItemCount
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|seq
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|paths
return|;
block|}
specifier|private
name|BinaryValue
index|[]
name|getBinaryData
parameter_list|(
specifier|final
name|Sequence
name|seq
parameter_list|)
block|{
specifier|final
name|BinaryValue
name|binaries
index|[]
init|=
operator|new
name|BinaryValue
index|[
name|seq
operator|.
name|getItemCount
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|seq
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|binaries
index|[
name|i
index|]
operator|=
operator|(
name|BinaryValue
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|binaries
return|;
block|}
block|}
end_class

end_unit

