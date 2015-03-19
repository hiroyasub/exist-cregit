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
name|FTPReply
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
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|GetConnectionFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|RETURN_TYPE
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"an xs:long representing the connection handle"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|FTP_PASSWORD_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"password"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The FTP server password"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|FTP_USERNAME_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The FTP server username"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|FTP_HOST_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"host"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The host to connect to"
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
name|GetConnectionFunction
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|"get-connection"
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
literal|"Opens a connection to a SQL Database"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|FTP_HOST_PARAM
block|,
name|FTP_USERNAME_PARAM
block|,
name|FTP_PASSWORD_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
comment|/**      * GetConnectionFunction Constructor.      *      * @param  context    The Context of the calling XQuery      * @param  signature  DOCUMENT ME!      */
specifier|public
name|GetConnectionFunction
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
comment|/**      * evaluate the call to the xquery get-connection() function, it is really the main entry point of this class.      *      * @param   args             arguments from the get-connection() function call      * @param   contextSequence  the Context Sequence to operate on (not used here internally!)      *      * @return  A xs:long representing a handle to the connection      *      * @throws  XPathException  DOCUMENT ME!      *      * @see     org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)      */
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
comment|// get the ftp connection details
name|String
name|host
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|username
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
name|password
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|FTPClient
name|ftp
init|=
operator|new
name|FTPClient
argument_list|()
decl_stmt|;
try|try
block|{
name|ftp
operator|.
name|connect
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Connected to: "
operator|+
name|host
operator|+
literal|". "
operator|+
name|ftp
operator|.
name|getReplyString
argument_list|()
argument_list|)
expr_stmt|;
comment|// After connection attempt, you should check the reply code to verify
comment|// success.
name|int
name|reply
init|=
name|ftp
operator|.
name|getReplyCode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|FTPReply
operator|.
name|isPositiveCompletion
argument_list|(
name|reply
argument_list|)
condition|)
block|{
name|ftp
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"FTP server refused connection."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// store the Connection and return the uid handle of the Connection
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|FTPClientModule
operator|.
name|storeConnection
argument_list|(
name|context
argument_list|,
name|ftp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|se
parameter_list|)
block|{
if|if
condition|(
name|ftp
operator|.
name|isConnected
argument_list|()
condition|)
block|{
try|try
block|{
name|ftp
operator|.
name|disconnect
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
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

