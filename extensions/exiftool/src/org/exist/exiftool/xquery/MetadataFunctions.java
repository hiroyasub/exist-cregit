begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|exiftool
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
name|ByteArrayInputStream
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
name|net
operator|.
name|URI
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
name|persistent
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
name|NativeBroker
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
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
name|MetadataFunctions
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
name|MetadataFunctions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|getMetadata
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-metadata"
argument_list|,
name|ExiftoolModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExiftoolModule
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
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The binary file from which to extract from"
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
comment|/*     public final static FunctionSignature writeMetadata = new FunctionSignature(         new QName("write-metadata", ExiftoolModule.NAMESPACE_URI, ExiftoolModule.PREFIX),         "write the metadata into a binary document",         new SequenceType[]{             new FunctionParameterSequenceType("doc",Type.DOCUMENT, Cardinality.ONE, " XML file containing file"),             new FunctionParameterSequenceType("binary", Type.BASE64_BINARY, Cardinality.ONE, "The binary data into where metadata is written")         },         new FunctionReturnSequenceType(Type.DOCUMENT, Cardinality.ONE, "Extracted metadata")     );     */
specifier|public
name|MetadataFunctions
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
name|String
name|uri
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|uri
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"http"
argument_list|)
condition|)
block|{
comment|//document from the web
return|return
name|extractMetadataFromWebResource
argument_list|(
name|uri
argument_list|)
return|;
block|}
else|else
block|{
comment|//document from the db
name|XmldbURI
name|docUri
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
argument_list|)
decl_stmt|;
return|return
name|extractMetadataFromLocalResource
argument_list|(
name|docUri
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|use
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not parse document URI: "
operator|+
name|use
operator|.
name|getMessage
argument_list|()
argument_list|,
name|use
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Sequence
name|extractMetadataFromLocalResource
parameter_list|(
name|XmldbURI
name|docUri
parameter_list|)
throws|throws
name|XPathException
block|{
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|docUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|instanceof
name|BinaryDocument
condition|)
block|{
comment|//resolve real filesystem path of binary file
name|File
name|binaryFile
init|=
operator|(
operator|(
name|NativeBroker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|)
operator|.
name|getCollectionBinaryFileFsPath
argument_list|(
name|docUri
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|binaryFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Binary Document at "
operator|+
name|docUri
operator|.
name|toString
argument_list|()
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
return|return
name|exifToolExtract
argument_list|(
name|binaryFile
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The binay document at "
operator|+
name|docUri
operator|.
name|toString
argument_list|()
operator|+
literal|" cannot be found."
argument_list|)
throw|;
block|}
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
literal|"Could not access binary document: "
operator|+
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
block|{
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
block|}
block|}
block|}
specifier|private
name|Sequence
name|extractMetadataFromWebResource
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//parse the string uri into a URI object to make sure its valid
name|URI
name|u
decl_stmt|;
try|try
block|{
name|u
operator|=
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
expr_stmt|;
return|return
name|exifToolWebExtract
argument_list|(
name|u
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"URI syntax error"
operator|+
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
specifier|private
name|Sequence
name|exifToolExtract
parameter_list|(
name|File
name|binaryFile
parameter_list|)
throws|throws
name|XPathException
block|{
name|ExiftoolModule
name|module
init|=
operator|(
name|ExiftoolModule
operator|)
name|getParentModule
argument_list|()
decl_stmt|;
name|InputStream
name|stdIn
init|=
literal|null
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Process
name|p
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|module
operator|.
name|getPerlPath
argument_list|()
operator|+
literal|" "
operator|+
name|module
operator|.
name|getExiftoolPath
argument_list|()
operator|+
literal|" -X -struct "
operator|+
name|binaryFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|stdIn
operator|=
name|p
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|baos
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
comment|//buffer stdin
name|int
name|read
init|=
operator|-
literal|1
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
name|read
operator|=
name|stdIn
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
name|baos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
comment|//make sure process is complete
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
return|return
name|ModuleUtils
operator|.
name|inputSourceToXML
argument_list|(
name|context
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not execute the Exiftool "
operator|+
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
name|saxe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"exiftool returned="
operator|+
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|saxe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not parse output from the Exiftool "
operator|+
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
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not execute the Exiftool "
operator|+
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ie
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|baos
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
block|}
block|}
if|if
condition|(
name|stdIn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stdIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
block|}
block|}
block|}
block|}
specifier|private
name|Sequence
name|exifToolWebExtract
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|XPathException
block|{
name|ExiftoolModule
name|module
init|=
operator|(
name|ExiftoolModule
operator|)
name|getParentModule
argument_list|()
decl_stmt|;
name|InputStream
name|stdIn
init|=
literal|null
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Process
name|p
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|module
operator|.
name|getExiftoolPath
argument_list|()
operator|+
literal|" -fast -X -"
argument_list|)
decl_stmt|;
name|stdIn
operator|=
name|p
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|OutputStream
name|stdOut
init|=
name|p
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|Source
name|src
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|null
argument_list|,
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|InputStream
name|isSrc
init|=
name|src
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
comment|//write the remote data to stdOut
name|int
name|read
init|=
operator|-
literal|1
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
name|read
operator|=
name|isSrc
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
name|stdOut
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
name|stdOut
operator|.
name|flush
argument_list|()
expr_stmt|;
name|stdOut
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//read stdin to buffer
name|baos
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|read
operator|=
operator|-
literal|1
expr_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|stdIn
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
name|baos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
comment|//make sure process is complete
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
return|return
name|ModuleUtils
operator|.
name|inputSourceToXML
argument_list|(
name|context
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not execute the Exiftool "
operator|+
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
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not execute the Exiftool "
operator|+
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
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
name|error
argument_list|(
literal|"exiftool returned="
operator|+
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|saxe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not parse output from the Exiftool "
operator|+
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
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not execute the Exiftool "
operator|+
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ie
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|baos
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
block|}
block|}
if|if
condition|(
name|stdIn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stdIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

