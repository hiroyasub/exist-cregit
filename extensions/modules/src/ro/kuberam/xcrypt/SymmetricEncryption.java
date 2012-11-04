begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * To change this template, choose Tools | Templates  * and open the template in the editor.  */
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|external
operator|.
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
name|security
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
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|*
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
name|XPathException
import|;
end_import

begin_comment
comment|/**  *  * @author claudius  */
end_comment

begin_class
specifier|public
class|class
name|SymmetricEncryption
block|{
specifier|private
specifier|static
name|String
name|asHex
parameter_list|(
name|byte
name|buf
index|[]
parameter_list|)
block|{
name|StringBuffer
name|strbuf
init|=
operator|new
name|StringBuffer
argument_list|(
name|buf
operator|.
name|length
operator|*
literal|2
argument_list|)
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|buf
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
operator|(
name|int
operator|)
name|buf
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|<
literal|0x10
condition|)
name|strbuf
operator|.
name|append
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|strbuf
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
operator|(
name|int
operator|)
name|buf
index|[
name|i
index|]
operator|&
literal|0xff
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|strbuf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|symmetricEncryption
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|plainKey
parameter_list|,
name|String
name|cryptographicAlgorithm
parameter_list|)
throws|throws
name|XPathException
block|{
name|SecretKeySpec
name|skeySpec
init|=
operator|new
name|SecretKeySpec
argument_list|(
name|plainKey
operator|.
name|getBytes
argument_list|()
argument_list|,
name|cryptographicAlgorithm
argument_list|)
decl_stmt|;
comment|// Instantiate the cipher
name|Cipher
name|cipher
decl_stmt|;
try|try
block|{
name|cipher
operator|=
name|Cipher
operator|.
name|getInstance
argument_list|(
name|cryptographicAlgorithm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchPaddingException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
try|try
block|{
name|cipher
operator|.
name|init
argument_list|(
name|Cipher
operator|.
name|ENCRYPT_MODE
argument_list|,
name|skeySpec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidKeyException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|byte
index|[]
name|encrypted
init|=
literal|null
decl_stmt|;
try|try
block|{
name|encrypted
operator|=
name|cipher
operator|.
name|doFinal
argument_list|(
name|input
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalBlockSizeException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|BadPaddingException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|getString
argument_list|(
name|encrypted
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|symmetricDecryption
parameter_list|(
name|String
name|encryptedInput
parameter_list|,
name|String
name|plainKey
parameter_list|,
name|String
name|cryptographicAlgorithm
parameter_list|)
throws|throws
name|XPathException
block|{
name|SecretKeySpec
name|skeySpec
init|=
operator|new
name|SecretKeySpec
argument_list|(
name|plainKey
operator|.
name|getBytes
argument_list|()
argument_list|,
name|cryptographicAlgorithm
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\tKey length: "
operator|+
name|plainKey
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Instantiate the cipher
name|Cipher
name|cipher
decl_stmt|;
try|try
block|{
name|cipher
operator|=
name|Cipher
operator|.
name|getInstance
argument_list|(
name|cryptographicAlgorithm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchPaddingException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
try|try
block|{
name|cipher
operator|.
name|init
argument_list|(
name|Cipher
operator|.
name|DECRYPT_MODE
argument_list|,
name|skeySpec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidKeyException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|byte
index|[]
name|decrypted
init|=
literal|null
decl_stmt|;
try|try
block|{
name|decrypted
operator|=
name|cipher
operator|.
name|doFinal
argument_list|(
name|getBytes
argument_list|(
name|encryptedInput
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalBlockSizeException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|BadPaddingException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|String
argument_list|(
name|decrypted
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getString
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|bytes
index|[
name|i
index|]
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|(
name|int
operator|)
operator|(
literal|0x00FF
operator|&
name|b
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|bytes
operator|.
name|length
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|byte
index|[]
name|getBytes
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|str
argument_list|,
literal|"-"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
name|bos
operator|.
name|write
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/*public static void main(String[] args) throws Exception {      // Symmetric encryption with SymmetricEncryption algorithm and 128 key     System.out.println("Symmetric encryption with AES algorithm and 128 key:");     System.out.println("\tOriginal string: This is a test!");     String encryptedString1 = symmetricEncryption("<root xmlns=\"http://kuberam.ro\"><a>This is a really new test!</a></root>", "1234567890123456", "AES");     System.out.println("\tEncrypted string: " + encryptedString1);     System.out.println("\tDecrypted string: " + symmetricDecryption(encryptedString1, "1234567890123456", "AES"));      // Symmetric encryption with Blowfish algorithm and 128 key     System.out.println("Symmetric encryption with Blowfish algorithm and 128 key:");     System.out.println("\tOriginal string: This is a test!");     String encryptedString2 = symmetricEncryption("This is a test!", "1234567890123456", "Blowfish");     System.out.println("\tEncrypted string: " + encryptedString2);     System.out.println("\tDecrypted string: " + symmetricDecryption(encryptedString2, "1234567890123456", "Blowfish"));        }*/
block|}
end_class

end_unit

