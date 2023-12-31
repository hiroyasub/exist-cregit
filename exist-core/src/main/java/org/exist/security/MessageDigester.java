begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
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
name|util
operator|.
name|Base64Encoder
import|;
end_import

begin_class
specifier|public
class|class
name|MessageDigester
block|{
specifier|private
specifier|static
name|String
index|[]
name|hex
init|=
block|{
literal|"0"
block|,
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|,
literal|"5"
block|,
literal|"6"
block|,
literal|"7"
block|,
literal|"8"
block|,
literal|"9"
block|,
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e"
block|,
literal|"f"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|MessageDigester
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|String
name|md5
parameter_list|(
name|String
name|message
parameter_list|,
name|boolean
name|base64
parameter_list|)
block|{
name|MessageDigest
name|md5
init|=
literal|null
decl_stmt|;
name|String
name|digest
init|=
name|message
decl_stmt|;
try|try
block|{
name|md5
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
expr_stmt|;
name|md5
operator|.
name|update
argument_list|(
name|message
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|digestData
init|=
name|md5
operator|.
name|digest
argument_list|()
decl_stmt|;
if|if
condition|(
name|base64
condition|)
block|{
specifier|final
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
name|digestData
argument_list|)
expr_stmt|;
name|digest
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
else|else
block|{
name|digest
operator|=
name|byteArrayToHex
argument_list|(
name|digestData
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"MD5 not supported. Using plain string as password!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Digest creation failed. Using plain string as password!"
argument_list|)
expr_stmt|;
block|}
return|return
name|digest
return|;
block|}
specifier|public
specifier|static
name|String
name|calculate
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|algorithm
parameter_list|,
name|boolean
name|base64
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
comment|// Can throw a  NoSuchAlgorithmException
name|MessageDigest
name|md
init|=
literal|null
decl_stmt|;
try|try
block|{
name|md
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|error
init|=
literal|"'"
operator|+
name|algorithm
operator|+
literal|"' is not a supported MessageDigest algorithm."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|error
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|error
argument_list|)
throw|;
block|}
comment|// Calculate hash
name|md
operator|.
name|update
argument_list|(
name|message
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|digestData
init|=
name|md
operator|.
name|digest
argument_list|()
decl_stmt|;
comment|// Write digest as string
name|String
name|digest
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|base64
condition|)
block|{
specifier|final
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
name|digestData
argument_list|)
expr_stmt|;
name|digest
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
else|else
block|{
name|digest
operator|=
name|byteArrayToHex
argument_list|(
name|digestData
argument_list|)
expr_stmt|;
block|}
return|return
name|digest
return|;
block|}
specifier|private
specifier|static
name|void
name|byteToHex
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|byte
name|b
parameter_list|)
block|{
name|int
name|n
init|=
name|b
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
name|n
operator|=
literal|256
operator|+
name|n
expr_stmt|;
block|}
specifier|final
name|int
name|d1
init|=
name|n
operator|/
literal|16
decl_stmt|;
specifier|final
name|int
name|d2
init|=
name|n
operator|%
literal|16
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|hex
index|[
name|d1
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|hex
index|[
name|d2
index|]
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|byteArrayToHex
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|b
operator|.
name|length
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|byte
name|value
range|:
name|b
control|)
block|{
name|byteToHex
argument_list|(
name|buf
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *  The main program for the MD5 class      *      *@param  args  The command line arguments      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"input: "
operator|+
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MD5:   "
operator|+
name|MessageDigester
operator|.
name|md5
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MD5 (base64):   "
operator|+
name|MessageDigester
operator|.
name|md5
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

