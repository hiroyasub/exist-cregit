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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|Base64Binary
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
name|StringValue
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

begin_class
specifier|public
class|class
name|BinaryToString
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|BinaryToString
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"binary-to-string"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the contents of a binary resource as an xs:string value. The binary data "
operator|+
literal|"is transformed into a Java string using the encoding specified in the optional "
operator|+
literal|"second argument or the default of UTF-8."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binary-resource"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the binary resource"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"encoded-string"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The string containing the encoded binary resource"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"binary-to-string"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the contents of a binary resource as an xs:string value. The binary data "
operator|+
literal|"is transformed into a Java string using the encoding specified in the optional "
operator|+
literal|"second argument or the default of UTF-8."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binary-resource"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the binary resource"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"encoding"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the encoding type.  i.e. 'UTF-8'"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"encoded-string"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The string containing the encoded binary resource"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"string-to-binary"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the contents of a binary resource as an xs:string value. The binary data "
operator|+
literal|"is transformed into a Java string using the encoding specified in the optional "
operator|+
literal|"second argument or the default of UTF-8."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"encoded-string"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The string containing the encoded binary resource"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binary-resource"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the binary resource"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"string-to-binary"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the contents of a binary resource as an xs:string value. The binary data "
operator|+
literal|"is transformed into a Java string using the encoding specified in the optional "
operator|+
literal|"second argument or the default of UTF-8."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"encoded-string"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The string containing the encoded binary resource"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"encoding"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the encoding type.  i.e. 'UTF-8'"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binary-resource"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the binary resource"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|BinaryToString
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
name|logger
operator|.
name|info
argument_list|(
literal|"Entering "
operator|+
name|UtilModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
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
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|UtilModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
name|encoding
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"binary-to-string"
argument_list|)
condition|)
block|{
name|Base64Binary
name|binary
init|=
operator|(
name|Base64Binary
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
name|byte
index|[]
name|data
init|=
name|binary
operator|.
name|getBinaryData
argument_list|()
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|UtilModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|encoding
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unsupported encoding: "
operator|+
name|encoding
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|String
name|str
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
name|str
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|UtilModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|Base64Binary
argument_list|(
name|data
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unsupported encoding: "
operator|+
name|encoding
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

