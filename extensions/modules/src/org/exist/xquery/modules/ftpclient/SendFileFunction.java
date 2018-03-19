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
name|QName
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
name|BooleanValue
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
name|IntegerValue
import|;
end_import

begin_comment
comment|/**  *  * @author WStarcev  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|SendFileFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|CONNECTION_HANDLE_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"connection-handle"
argument_list|,
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The connection handle"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|REMOTE_DIRECTORY_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"remote-directory"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The remote directory"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|FILE_NAME_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"file-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"File name"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|DATA_PARAM
init|=
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
literal|"The binary file data to send"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SendFileFunction
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|"send-binary-file"
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
literal|"Send binary files via FTP."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|CONNECTION_HANDLE_PARAM
block|,
name|REMOTE_DIRECTORY_PARAM
block|,
name|FILE_NAME_PARAM
block|,
name|DATA_PARAM
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
literal|"true or false indicating the success of the file send"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|SendFileFunction
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
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|long
name|connectionUID
init|=
operator|(
operator|(
name|IntegerValue
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
name|getLong
argument_list|()
decl_stmt|;
name|FTPClient
name|ftp
init|=
name|FTPClientModule
operator|.
name|retrieveConnection
argument_list|(
name|context
argument_list|,
name|connectionUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ftp
operator|!=
literal|null
condition|)
block|{
name|String
name|remoteDirectory
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|BinaryValue
name|data
init|=
operator|(
name|BinaryValue
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
name|result
operator|=
name|sendBinaryFile
argument_list|(
name|ftp
argument_list|,
name|remoteDirectory
argument_list|,
name|fileName
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Sequence
name|sendBinaryFile
parameter_list|(
name|FTPClient
name|ftp
parameter_list|,
name|String
name|remoteDirectory
parameter_list|,
name|String
name|fileName
parameter_list|,
name|BinaryValue
name|data
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
try|try
init|(
name|InputStream
name|inputStream
init|=
name|data
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|ftp
operator|.
name|changeWorkingDirectory
argument_list|(
name|remoteDirectory
argument_list|)
expr_stmt|;
name|ftp
operator|.
name|setFileType
argument_list|(
name|FTP
operator|.
name|BINARY_FILE_TYPE
argument_list|)
expr_stmt|;
comment|//BinaryValueFromInputStream bvis = BinaryValueFromInputStream.getInstance(context, new Base64BinaryValueType(), data.getInputStream());
comment|//OutputStream out = ftp.storeFileStream(fileName);
name|result
operator|=
name|ftp
operator|.
name|storeFile
argument_list|(
name|fileName
argument_list|,
name|inputStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
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
name|result
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
end_class

end_unit

