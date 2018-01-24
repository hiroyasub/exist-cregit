begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2013 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|fluent
operator|.
name|Executor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|fluent
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistWebServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|RestApiSecurityTest
extends|extends
name|AbstractApiSecurityTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
name|ExistWebServer
name|existWebServer
init|=
operator|new
name|ExistWebServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|baseUri
init|=
literal|"/db"
decl_stmt|;
specifier|private
specifier|static
name|String
name|getServerUri
parameter_list|()
block|{
return|return
literal|"http://localhost:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
operator|+
literal|"/rest"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createCol
parameter_list|(
specifier|final
name|String
name|collectionName
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"xmldb:create-collection('/db', '"
operator|+
name|collectionName
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|removeCol
parameter_list|(
specifier|final
name|String
name|collectionName
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|String
name|collectionUri
init|=
name|getServerUri
argument_list|()
operator|+
name|baseUri
operator|+
literal|"/"
operator|+
name|collectionName
decl_stmt|;
specifier|final
name|Executor
name|exec
init|=
name|getExecutor
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|HttpResponse
name|resp
init|=
name|exec
operator|.
name|execute
argument_list|(
name|Request
operator|.
name|Delete
argument_list|(
name|collectionUri
argument_list|)
argument_list|)
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
literal|"Could not remove collection: "
operator|+
name|collectionUri
operator|+
literal|". "
operator|+
name|getResponseBody
argument_list|(
name|resp
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|chownCol
parameter_list|(
specifier|final
name|String
name|collectionUri
parameter_list|,
specifier|final
name|String
name|owner_uid
parameter_list|,
specifier|final
name|String
name|group_gid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"sm:chown(xs:anyURI('"
operator|+
name|collectionUri
operator|+
literal|"'), '"
operator|+
name|owner_uid
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
name|executeQuery
argument_list|(
literal|"sm:chgrp(xs:anyURI('"
operator|+
name|collectionUri
operator|+
literal|"'), '"
operator|+
name|group_gid
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|chmodCol
parameter_list|(
specifier|final
name|String
name|collectionUri
parameter_list|,
specifier|final
name|String
name|mode
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"sm:chmod(xs:anyURI('"
operator|+
name|collectionUri
operator|+
literal|"'), '"
operator|+
name|mode
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|chmodRes
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|String
name|mode
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"sm:chmod(xs:anyURI('"
operator|+
name|resourceUri
operator|+
literal|"'), '"
operator|+
name|mode
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|chownRes
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|String
name|owner_uid
parameter_list|,
specifier|final
name|String
name|group_gid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"sm:chown(xs:anyURI('"
operator|+
name|resourceUri
operator|+
literal|"'), '"
operator|+
name|owner_uid
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
name|executeQuery
argument_list|(
literal|"sm:chgrp(xs:anyURI('"
operator|+
name|resourceUri
operator|+
literal|"'), '"
operator|+
name|group_gid
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getXmlResourceContent
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|Executor
name|exec
init|=
name|getExecutor
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|HttpResponse
name|resp
init|=
name|exec
operator|.
name|execute
argument_list|(
name|Request
operator|.
name|Get
argument_list|(
name|getServerUri
argument_list|()
operator|+
name|resourceUri
argument_list|)
argument_list|)
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
literal|"Could not get XML resource from uri: "
operator|+
name|resourceUri
operator|+
literal|". "
operator|+
name|getResponseBody
argument_list|(
name|resp
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|getResponseBody
argument_list|(
name|resp
operator|.
name|getEntity
argument_list|()
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|removeAccount
parameter_list|(
specifier|final
name|String
name|account_uid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"xmldb:delete-user('"
operator|+
name|account_uid
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|removeGroup
parameter_list|(
specifier|final
name|String
name|group_uid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"if(sm:group-exists('"
operator|+
name|group_uid
operator|+
literal|"'))then sm:delete-group('"
operator|+
name|group_uid
operator|+
literal|"') else()"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createAccount
parameter_list|(
specifier|final
name|String
name|account_uid
parameter_list|,
specifier|final
name|String
name|account_pwd
parameter_list|,
specifier|final
name|String
name|group_gid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"xmldb:create-user('"
operator|+
name|account_uid
operator|+
literal|"', '"
operator|+
name|account_pwd
operator|+
literal|"', ('"
operator|+
name|group_gid
operator|+
literal|"'))"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createGroup
parameter_list|(
specifier|final
name|String
name|group_gid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
literal|"xmldb:create-group('"
operator|+
name|group_gid
operator|+
literal|"', '"
operator|+
name|uid
operator|+
literal|"')"
argument_list|,
name|uid
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createXmlResource
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|String
name|content
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|Executor
name|exec
init|=
name|getExecutor
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|HttpResponse
name|resp
init|=
name|exec
operator|.
name|execute
argument_list|(
name|Request
operator|.
name|Put
argument_list|(
name|getServerUri
argument_list|()
operator|+
name|resourceUri
argument_list|)
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/xml"
argument_list|)
operator|.
name|bodyByteArray
argument_list|(
name|content
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|HttpStatus
operator|.
name|SC_CREATED
condition|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
literal|"Could not store XML resource to uri: "
operator|+
name|resourceUri
operator|+
literal|". "
operator|+
name|getResponseBody
argument_list|(
name|resp
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createBinResource
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|byte
index|[]
name|content
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|Executor
name|exec
init|=
name|getExecutor
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|HttpResponse
name|resp
init|=
name|exec
operator|.
name|execute
argument_list|(
name|Request
operator|.
name|Put
argument_list|(
name|getServerUri
argument_list|()
operator|+
name|resourceUri
argument_list|)
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/octet-stream"
argument_list|)
operator|.
name|bodyByteArray
argument_list|(
name|content
argument_list|)
argument_list|)
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|HttpStatus
operator|.
name|SC_CREATED
condition|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
literal|"Could not store Binary resource to uri: "
operator|+
name|resourceUri
operator|+
literal|". "
operator|+
name|getResponseBody
argument_list|(
name|resp
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|executeQuery
parameter_list|(
specifier|final
name|String
name|xquery
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|Executor
name|exec
init|=
name|getExecutor
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|queryUri
init|=
name|createQueryUri
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
specifier|final
name|HttpResponse
name|resp
init|=
name|exec
operator|.
name|execute
argument_list|(
name|Request
operator|.
name|Get
argument_list|(
name|queryUri
argument_list|)
argument_list|)
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
literal|"Could not execute query uri: "
operator|+
name|queryUri
operator|+
literal|". "
operator|+
name|getResponseBody
argument_list|(
name|resp
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Executor
name|getExecutor
parameter_list|(
specifier|final
name|String
name|uid
parameter_list|,
name|String
name|pwd
parameter_list|)
block|{
return|return
name|Executor
operator|.
name|newInstance
argument_list|()
operator|.
name|authPreemptive
argument_list|(
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
name|existWebServer
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
operator|.
name|auth
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|)
return|;
block|}
specifier|private
name|String
name|createQueryUri
parameter_list|(
specifier|final
name|String
name|xquery
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
return|return
name|getServerUri
argument_list|()
operator|+
name|baseUri
operator|+
literal|"/?_query="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|xquery
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
specifier|private
name|String
name|getResponseBody
parameter_list|(
specifier|final
name|HttpEntity
name|entity
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|entity
operator|.
name|writeTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

