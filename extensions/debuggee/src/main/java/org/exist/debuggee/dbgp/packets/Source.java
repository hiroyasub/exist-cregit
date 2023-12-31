begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dbgp
operator|.
name|packets
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|session
operator|.
name|IoSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dbgp
operator|.
name|Errors
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
name|LockedDocument
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
name|storage
operator|.
name|DBBroker
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
operator|.
name|LockMode
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
name|io
operator|.
name|FastByteArrayOutputStream
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
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Source
extends|extends
name|Command
block|{
comment|/** 	 * file URI  	 */
specifier|private
name|String
name|fileURI
decl_stmt|;
comment|/** 	 * begin line 	 */
specifier|private
name|Integer
name|lineBegin
init|=
literal|null
decl_stmt|;
comment|/** 	 * end line 	 */
specifier|private
name|Integer
name|lineEnd
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|private
name|Exception
name|exception
init|=
literal|null
decl_stmt|;
specifier|private
name|byte
index|[]
name|source
decl_stmt|;
specifier|private
name|byte
index|[]
name|response
init|=
literal|null
decl_stmt|;
specifier|public
name|Source
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setArgument
parameter_list|(
name|String
name|arg
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"f"
argument_list|)
condition|)
name|fileURI
operator|=
name|val
expr_stmt|;
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"b"
argument_list|)
condition|)
name|lineBegin
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
expr_stmt|;
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"e"
argument_list|)
condition|)
name|lineEnd
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
expr_stmt|;
else|else
name|super
operator|.
name|setArgument
argument_list|(
name|arg
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|exec
parameter_list|()
block|{
if|if
condition|(
name|fileURI
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|fileURI
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"dbgp://"
argument_list|)
condition|)
block|{
name|String
name|uri
init|=
name|fileURI
operator|.
name|substring
argument_list|(
literal|7
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"file:/"
argument_list|)
condition|)
block|{
name|uri
operator|=
name|fileURI
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|is
operator|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmldbURI
name|pathUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|URLDecoder
operator|.
name|decode
argument_list|(
name|fileURI
operator|.
name|substring
argument_list|(
literal|15
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|Database
name|db
init|=
name|getJoint
argument_list|()
operator|.
name|getContext
argument_list|()
operator|.
name|getDatabase
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|db
operator|.
name|getBroker
argument_list|()
init|;
specifier|final
name|LockedDocument
name|resource
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|pathUri
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|resource
operator|.
name|getDocument
argument_list|()
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
name|is
operator|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|resource
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//TODO: xml source???
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|fileURI
argument_list|)
decl_stmt|;
name|URLConnection
name|conn
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|is
operator|=
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
block|}
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
name|int
name|c
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|is
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
comment|//TODO: begin& end line should affect
name|baos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|source
operator|=
name|baos
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|public
name|byte
index|[]
name|responseBytes
parameter_list|()
block|{
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
name|String
name|url
init|=
literal|"NULL"
decl_stmt|;
if|if
condition|(
name|fileURI
operator|!=
literal|null
condition|)
name|url
operator|=
name|fileURI
expr_stmt|;
name|response
operator|=
name|errorBytes
argument_list|(
literal|"source"
argument_list|,
name|Errors
operator|.
name|ERR_100
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
operator|+
literal|" (URL:"
operator|+
name|url
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|String
name|head
init|=
name|xml_declaration
operator|+
literal|"<response "
operator|+
name|namespaces
operator|+
literal|"command=\"source\" "
operator|+
literal|"success=\""
operator|+
name|getSuccessString
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"encoding=\"base64\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\"><![CDATA["
decl_stmt|;
name|String
name|tail
init|=
literal|"]]></response>"
decl_stmt|;
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
name|source
argument_list|)
expr_stmt|;
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|(
name|head
operator|.
name|length
argument_list|()
operator|+
operator|(
operator|(
name|source
operator|.
name|length
operator|/
literal|100
operator|)
operator|*
literal|33
operator|)
operator|+
name|tail
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|baos
operator|.
name|write
argument_list|(
name|head
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|baos
operator|.
name|write
argument_list|(
operator|new
name|String
argument_list|(
name|enc
operator|.
name|getCharArray
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|baos
operator|.
name|write
argument_list|(
name|tail
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|=
name|baos
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|response
operator|=
name|errorBytes
argument_list|(
literal|"source"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|response
operator|=
name|errorBytes
argument_list|(
literal|"source"
argument_list|,
name|Errors
operator|.
name|ERR_100
argument_list|,
name|Errors
operator|.
name|ERR_100_STR
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|response
return|;
block|}
specifier|private
name|String
name|getSuccessString
parameter_list|()
block|{
if|if
condition|(
name|success
condition|)
return|return
literal|"1"
return|;
return|return
literal|"0"
return|;
block|}
specifier|public
name|byte
index|[]
name|commandBytes
parameter_list|()
block|{
name|String
name|command
init|=
literal|"source"
operator|+
literal|" -i "
operator|+
name|transactionID
operator|+
literal|" -f "
operator|+
name|fileURI
decl_stmt|;
if|if
condition|(
name|lineBegin
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -b "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|lineBegin
argument_list|)
expr_stmt|;
if|if
condition|(
name|lineEnd
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -e "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|lineEnd
argument_list|)
expr_stmt|;
return|return
name|command
operator|.
name|getBytes
argument_list|()
return|;
block|}
specifier|public
name|void
name|setFileURI
parameter_list|(
name|String
name|fileURI
parameter_list|)
block|{
name|this
operator|.
name|fileURI
operator|=
name|fileURI
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|response
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|response
operator|.
name|append
argument_list|(
literal|"source "
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileURI
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|append
argument_list|(
literal|"fileURI = '"
argument_list|)
expr_stmt|;
name|response
operator|.
name|append
argument_list|(
name|fileURI
argument_list|)
expr_stmt|;
name|response
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|append
argument_list|(
literal|"["
operator|+
name|transactionID
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

