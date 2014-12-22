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
name|commons
operator|.
name|net
operator|.
name|ftp
operator|.
name|FTPFile
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
name|IntegerValue
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

begin_comment
comment|/**  *  * @author ws  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|GetDirListFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|GetDirListFunction
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|"list"
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
literal|"Get file list from remote FTP server."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|CONNECTION_HANDLE_PARAM
block|,
name|REMOTE_DIRECTORY_PARAM
block|,         }
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
name|ZERO_OR_ONE
argument_list|,
literal|"Response from server in XML"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetDirListFunction
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
name|result
operator|=
name|list
argument_list|(
name|ftp
argument_list|,
name|remoteDirectory
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Sequence
name|list
parameter_list|(
name|FTPClient
name|ftp
parameter_list|,
name|String
name|remoteDirectory
parameter_list|)
block|{
name|Sequence
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
try|try
block|{
name|ftp
operator|.
name|changeWorkingDirectory
argument_list|(
name|remoteDirectory
argument_list|)
expr_stmt|;
name|FTPFile
index|[]
name|ftpFiles
init|=
name|ftp
operator|.
name|listFiles
argument_list|()
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
literal|"list"
argument_list|,
name|FTPClientModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|FTPFile
name|ftpFile
range|:
name|ftpFiles
control|)
block|{
if|if
condition|(
name|ftpFile
operator|.
name|getType
argument_list|()
operator|==
name|FTPFile
operator|.
name|FILE_TYPE
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"file"
argument_list|,
name|FTPClientModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|ftpFile
operator|.
name|getType
argument_list|()
operator|==
name|FTPFile
operator|.
name|DIRECTORY_TYPE
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"directory"
argument_list|,
name|FTPClientModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|characters
argument_list|(
name|ftpFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|result
operator|=
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
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
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

