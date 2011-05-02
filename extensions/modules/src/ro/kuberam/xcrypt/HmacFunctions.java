begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Java Cryptographic Extension  *  Copyright (C) 2010 Claudius Teodorescu at http://kuberam.ro  *  *  Released under LGPL License - http://gnu.org/licenses/lgpl.html.  *  */
end_comment

begin_package
package|package
name|ro
operator|.
name|kuberam
operator|.
name|xcrypt
package|;
end_package

begin_comment
comment|/*  * @author claudius  */
end_comment

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
name|StringValue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|Mac
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

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
name|java
operator|.
name|security
operator|.
name|InvalidKeyException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
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
name|Base64Encoder
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
name|Base64Decoder
import|;
end_import

begin_class
specifier|public
class|class
name|HmacFunctions
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|supportedHashAlgorithms
init|=
block|{
literal|"HmacMD5"
block|,
literal|"HmacSHA1"
block|,
literal|"HmacSHA256"
block|,
literal|"HmacSHA384"
block|,
literal|"HmacSHA512"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|supportedResultEncodingMethods
init|=
block|{
literal|"hex"
block|,
literal|"base64"
block|}
decl_stmt|;
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
name|HmacFunctions
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
literal|"hmac"
argument_list|,
name|XcryptModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XcryptModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Encrypts the input string."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"message"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The message to be authenticated."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"secret-key"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The secret key used for calculating the authentication."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"algorithm"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The cryptographic hasing algorithm. Legal values are 'MD5', 'SHA-1', 'SHA-256', 'SHA-384', and 'SHA-512'."
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
literal|"The method used for encoding of the result. Legal values are 'hex', and 'base64'."
argument_list|)
block|,                             }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"hash-based message authentication code."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"hmac"
argument_list|,
name|XcryptModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XcryptModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Encrypts the input string. Default value for encoding is 'base64'."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"message"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The message to be authenticated."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"secret-key"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The secret key used for calculating the authentication."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"algorithm"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The cryptographic hasing algorithm. Legal values are 'MD5', 'SHA-1', 'SHA-256', 'SHA-384', and 'SHA-512'."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"hash-based message authentication code."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|HmacFunctions
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
name|result
init|=
literal|null
decl_stmt|;
name|String
name|hashAlgorithm
init|=
literal|"Hmac"
operator|+
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"-"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|asList
argument_list|(
name|supportedHashAlgorithms
argument_list|)
operator|.
name|contains
argument_list|(
name|hashAlgorithm
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The XForms hmac() function does not support '"
operator|+
name|hashAlgorithm
operator|+
literal|"' algoritm for hashing"
argument_list|)
throw|;
block|}
name|String
name|key
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
name|data
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
name|encoding
init|=
operator|(
name|args
operator|.
name|length
operator|==
literal|3
operator|)
condition|?
literal|"base64"
else|:
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|asList
argument_list|(
name|supportedResultEncodingMethods
argument_list|)
operator|.
name|contains
argument_list|(
name|encoding
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"xforms-compute-exception the XForms hmac() function does not support '"
operator|+
name|encoding
operator|+
literal|"' method for encoding the result of hashing"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|encodedKey
init|=
literal|null
decl_stmt|;
name|byte
index|[]
name|encodedData
init|=
literal|null
decl_stmt|;
name|StringBuffer
name|sb
init|=
literal|null
decl_stmt|;
name|Mac
name|mac
init|=
literal|null
decl_stmt|;
comment|//encoding the key
try|try
block|{
name|encodedKey
operator|=
name|key
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|ex
parameter_list|)
block|{
block|}
comment|//generating the signing key
name|SecretKeySpec
name|signingKey
init|=
operator|new
name|SecretKeySpec
argument_list|(
name|encodedKey
argument_list|,
name|hashAlgorithm
argument_list|)
decl_stmt|;
comment|//get and initialize the Mac instance
try|try
block|{
name|mac
operator|=
name|Mac
operator|.
name|getInstance
argument_list|(
name|hashAlgorithm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
block|}
try|try
block|{
name|mac
operator|.
name|init
argument_list|(
name|signingKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidKeyException
name|ex
parameter_list|)
block|{
block|}
comment|//encode the data
try|try
block|{
name|encodedData
operator|=
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|ex
parameter_list|)
block|{
block|}
comment|//compute the hmac
name|byte
name|byteData
index|[]
init|=
name|mac
operator|.
name|doFinal
argument_list|(
name|encodedData
argument_list|)
decl_stmt|;
comment|//get the encoded result
if|if
condition|(
name|encoding
operator|.
name|equals
argument_list|(
literal|"hex"
argument_list|)
condition|)
block|{
name|sb
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|byteData
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
operator|(
name|byteData
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|+
literal|0x100
argument_list|,
literal|16
argument_list|)
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Base64Encoder
name|enc
init|=
operator|new
name|Base64Encoder
argument_list|()
decl_stmt|;
name|enc
operator|.
name|translate
argument_list|(
name|byteData
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|String
argument_list|(
name|enc
operator|.
name|getCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StringValue
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|//hmac('key', 'abc', 'MD5', 'hex')
end_comment

end_unit

