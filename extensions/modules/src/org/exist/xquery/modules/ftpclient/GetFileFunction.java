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
name|ftpclient
package|;
end_package

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
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|net
operator|.
name|ftp
operator|.
name|FTP
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
name|net
operator|.
name|ftp
operator|.
name|FTPClient
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
name|xquery
operator|.
name|value
operator|.
name|BinaryValueFromInputStream
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
name|Base64BinaryValueType
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
name|StringValue
import|;
end_import

begin_comment
comment|/**  *  * @author WStarcev  */
end_comment

begin_class
specifier|public
class|class
name|GetFileFunction
extends|extends
name|BaseFTPClientFunction
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
literal|"getFile"
argument_list|,
name|FTPClientModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|FTPClientModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Get data from FTP to Base64Binary variable."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|URI_PARAM
block|,
name|USER_NAME
block|,
name|USER_PASS
block|,
name|HOME_DIRECTORY
block|,
name|FILE_NAME
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
name|ZERO_OR_MORE
argument_list|,
literal|"Response from server in XML"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetFileFunction
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
name|Sequence
name|res
init|=
literal|null
decl_stmt|;
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
literal|"response"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|FTPClient
name|client
init|=
operator|new
name|FTPClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|connect
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|login
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
name|client
operator|.
name|changeWorkingDirectory
argument_list|(
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|setFileType
argument_list|(
name|FTP
operator|.
name|BINARY_FILE_TYPE
argument_list|)
expr_stmt|;
name|BinaryValueFromInputStream
name|bvis
init|=
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
name|client
operator|.
name|retrieveFileStream
argument_list|(
name|args
index|[
literal|4
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|client
operator|.
name|isConnected
argument_list|()
condition|)
block|{
name|client
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
return|return
name|bvis
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|Logger
operator|.
name|getLogger
argument_list|(
name|SendFileFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|log
argument_list|(
name|Level
operator|.
name|SEVERE
argument_list|,
literal|null
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|ex
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
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
return|;
block|}
block|}
end_class

end_unit

