begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2008 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
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
name|UnsupportedEncodingException
import|;
end_import

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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|MessageDigester
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
name|SecurityManager
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
name|UserImpl
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
name|BrokerPool
import|;
end_import

begin_comment
comment|/**  * An Authenticator that uses MD5 Digest Authentication.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DigestAuthenticator
implements|implements
name|Authenticator
block|{
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|DigestAuthenticator
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
specifier|public
name|UserImpl
name|authenticate
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|credentials
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Authorization"
argument_list|)
decl_stmt|;
if|if
condition|(
name|credentials
operator|==
literal|null
condition|)
block|{
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Digest
name|digest
init|=
operator|new
name|Digest
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
decl_stmt|;
name|parseCredentials
argument_list|(
name|digest
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
name|SecurityManager
name|secman
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
name|UserImpl
name|user
init|=
operator|(
name|UserImpl
operator|)
name|secman
operator|.
name|getUser
argument_list|(
name|digest
operator|.
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// If user does not exist then send a challenge request again
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|digest
operator|.
name|check
argument_list|(
name|user
operator|.
name|getDigestPassword
argument_list|()
argument_list|)
condition|)
block|{
comment|// If password is incorrect then send a challenge request again
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|user
return|;
block|}
specifier|public
name|void
name|sendChallenge
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setHeader
argument_list|(
literal|"WWW-Authenticate"
argument_list|,
literal|"Digest realm=\"exist\", "
operator|+
literal|"nonce=\""
operator|+
name|createNonce
argument_list|(
name|request
argument_list|)
operator|+
literal|"\", "
operator|+
literal|"domain=\""
operator|+
name|request
operator|.
name|getContextPath
argument_list|()
operator|+
literal|"\", "
operator|+
literal|"opaque=\""
operator|+
name|MessageDigester
operator|.
name|md5
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|hashCode
argument_list|()
argument_list|,
literal|27
argument_list|)
argument_list|,
literal|false
argument_list|)
operator|+
literal|'"'
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|createNonce
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
return|return
name|MessageDigester
operator|.
name|md5
argument_list|(
name|request
operator|.
name|getRemoteAddr
argument_list|()
operator|+
literal|':'
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
operator|+
literal|':'
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|hashCode
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|parseCredentials
parameter_list|(
name|Digest
name|digest
parameter_list|,
name|String
name|credentials
parameter_list|)
block|{
name|credentials
operator|=
name|credentials
operator|.
name|substring
argument_list|(
literal|"Digest "
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuffer
name|current
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|,
name|value
decl_stmt|;
name|boolean
name|inQuotedString
init|=
literal|false
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
name|credentials
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|credentials
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|' '
case|:
break|break;
case|case
literal|'"'
case|:
case|case
literal|'\''
case|:
if|if
condition|(
name|inQuotedString
condition|)
block|{
name|value
operator|=
name|current
operator|.
name|toString
argument_list|()
expr_stmt|;
name|current
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|inQuotedString
operator|=
literal|false
expr_stmt|;
if|if
condition|(
literal|"username"
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
condition|)
name|digest
operator|.
name|username
operator|=
name|value
expr_stmt|;
if|else if
condition|(
literal|"realm"
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
condition|)
name|digest
operator|.
name|realm
operator|=
name|value
expr_stmt|;
if|else if
condition|(
literal|"nonce"
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
condition|)
name|digest
operator|.
name|nonce
operator|=
name|value
expr_stmt|;
if|else if
condition|(
literal|"uri"
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
condition|)
name|digest
operator|.
name|uri
operator|=
name|value
expr_stmt|;
if|else if
condition|(
literal|"response"
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
condition|)
name|digest
operator|.
name|response
operator|=
name|value
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
literal|null
expr_stmt|;
name|inQuotedString
operator|=
literal|true
expr_stmt|;
block|}
break|break;
case|case
literal|','
case|:
name|name
operator|=
literal|null
expr_stmt|;
break|break;
case|case
literal|'='
case|:
name|name
operator|=
name|current
operator|.
name|toString
argument_list|()
expr_stmt|;
name|current
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
default|default:
name|current
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|Digest
block|{
name|String
name|method
init|=
literal|null
decl_stmt|;
name|String
name|username
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|String
name|realm
init|=
literal|null
decl_stmt|;
name|String
name|nonce
init|=
literal|null
decl_stmt|;
name|String
name|uri
init|=
literal|null
decl_stmt|;
name|String
name|response
init|=
literal|null
decl_stmt|;
specifier|public
name|Digest
parameter_list|(
name|String
name|method
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
block|}
specifier|public
name|boolean
name|check
parameter_list|(
name|String
name|credentials
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|credentials
operator|==
literal|null
condition|)
comment|// no password set for the user: return true
return|return
literal|true
return|;
try|try
block|{
name|MessageDigest
name|md
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
comment|// calc A2 digest
name|md
operator|.
name|reset
argument_list|()
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
name|method
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|)
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
operator|(
name|byte
operator|)
literal|':'
argument_list|)
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
name|uri
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|ha2
init|=
name|md
operator|.
name|digest
argument_list|()
decl_stmt|;
comment|// calc digest
name|md
operator|.
name|update
argument_list|(
name|credentials
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|)
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
operator|(
name|byte
operator|)
literal|':'
argument_list|)
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
name|nonce
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|)
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
operator|(
name|byte
operator|)
literal|':'
argument_list|)
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
name|MessageDigester
operator|.
name|byteArrayToHex
argument_list|(
name|ha2
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|digest
init|=
name|md
operator|.
name|digest
argument_list|()
decl_stmt|;
comment|// check digest
return|return
operator|(
name|MessageDigester
operator|.
name|byteArrayToHex
argument_list|(
name|digest
argument_list|)
operator|.
name|equalsIgnoreCase
argument_list|(
name|response
argument_list|)
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"MD5 not supported"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Encoding not supported"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

