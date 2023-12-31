begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_class
specifier|public
class|class
name|FullXmldbURI
extends|extends
name|XmldbURI
block|{
comment|//this will never have xmldb:
specifier|private
name|URI
name|wrappedURI
decl_stmt|;
specifier|private
name|String
name|context
decl_stmt|;
specifier|private
name|String
name|apiName
decl_stmt|;
comment|/**      * Constructs an XmldbURI from given URI. The provided URI must have the      * XMLDB_SCHEME ("xmldb")      *      * @param xmldbURI A string      * @throws URISyntaxException If the given string is not a valid xmldb URI.      */
specifier|protected
name|FullXmldbURI
parameter_list|(
specifier|final
name|URI
name|xmldbURI
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|this
argument_list|(
name|xmldbURI
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|FullXmldbURI
parameter_list|(
specifier|final
name|URI
name|xmldbURI
parameter_list|,
specifier|final
name|boolean
name|mustHaveXMLDB
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|super
argument_list|(
name|xmldbURI
argument_list|,
name|mustHaveXMLDB
argument_list|)
expr_stmt|;
block|}
comment|/**      * Feeds private members      *      * @param xmldbURI the xmldb URI.      * @param hadXmldbPrefix if the xmldb URI has an xmldb prefix.      *      * @throws URISyntaxException if the URI is invalid.      */
annotation|@
name|Override
specifier|protected
name|void
name|parseURI
parameter_list|(
specifier|final
name|URI
name|xmldbURI
parameter_list|,
specifier|final
name|boolean
name|hadXmldbPrefix
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|wrappedURI
operator|=
name|xmldbURI
expr_stmt|;
if|if
condition|(
name|hadXmldbPrefix
condition|)
block|{
if|if
condition|(
name|wrappedURI
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|XMLDB_URI_PREFIX
operator|+
name|wrappedURI
operator|.
name|toString
argument_list|()
argument_list|,
literal|"xmldb URI scheme has no instance name"
argument_list|)
throw|;
block|}
name|String
name|userInfo
init|=
name|wrappedURI
operator|.
name|getUserInfo
argument_list|()
decl_stmt|;
comment|//Very tricky :
if|if
condition|(
name|wrappedURI
operator|.
name|getHost
argument_list|()
operator|==
literal|null
operator|&&
name|wrappedURI
operator|.
name|getAuthority
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|userInfo
operator|=
name|wrappedURI
operator|.
name|getAuthority
argument_list|()
expr_stmt|;
if|if
condition|(
name|userInfo
operator|.
name|endsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|userInfo
operator|=
name|userInfo
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|userInfo
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Eventually rewrite wrappedURI *without* user info
if|if
condition|(
name|userInfo
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringBuilder
name|recomputed
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|//XMLDB_URI_PREFIX);
name|recomputed
operator|.
name|append
argument_list|(
name|wrappedURI
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
name|recomputed
operator|.
name|append
argument_list|(
literal|"://"
argument_list|)
expr_stmt|;
name|recomputed
operator|.
name|append
argument_list|(
name|wrappedURI
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|wrappedURI
operator|.
name|getPort
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|recomputed
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|wrappedURI
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|recomputed
operator|.
name|append
argument_list|(
name|wrappedURI
operator|.
name|getRawPath
argument_list|()
argument_list|)
expr_stmt|;
name|wrappedURI
operator|=
operator|new
name|URI
argument_list|(
name|recomputed
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|parseURI
argument_list|(
name|xmldbURI
argument_list|,
name|hadXmldbPrefix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|splitPath
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastIndex
init|=
operator|-
literal|1
decl_stmt|;
comment|//Reinitialise members
name|this
operator|.
name|context
operator|=
literal|null
expr_stmt|;
name|String
name|pathForSuper
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|host
init|=
name|getHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
operator|||
name|EMBEDDED_SERVER_AUTHORITY
operator|.
name|equals
argument_list|(
name|host
argument_list|)
condition|)
block|{
if|if
condition|(
name|getPort
argument_list|()
operator|!=
name|NO_PORT
condition|)
block|{
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|XMLDB_URI_PREFIX
operator|+
name|wrappedURI
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Local xmldb URI should not provide a port"
argument_list|)
throw|;
block|}
name|apiName
operator|=
name|API_LOCAL
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
name|pathForSuper
operator|=
name|path
expr_stmt|;
block|}
else|else
block|{
comment|//Try to extract the protocol from the provided URI.
comment|//TODO : get rid of this and use a more robust approach (dedicated constructor ?) -pb
comment|//TODO : use named constants
name|index
operator|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"/xmlrpc"
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|>
name|lastIndex
condition|)
block|{
name|apiName
operator|=
name|API_XMLRPC
expr_stmt|;
name|pathForSuper
operator|=
name|path
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|"/xmlrpc"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
operator|+
literal|"/xmlrpc"
expr_stmt|;
name|lastIndex
operator|=
name|index
expr_stmt|;
block|}
comment|//TODO : use named constants
name|index
operator|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"/webdav"
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|>
name|lastIndex
condition|)
block|{
name|apiName
operator|=
name|API_WEBDAV
expr_stmt|;
name|pathForSuper
operator|=
name|path
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|"/webdav"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
operator|+
literal|"/webdav"
expr_stmt|;
name|lastIndex
operator|=
name|index
expr_stmt|;
block|}
comment|//Default : REST-style...
if|if
condition|(
name|apiName
operator|==
literal|null
condition|)
block|{
name|apiName
operator|=
name|API_REST
expr_stmt|;
name|pathForSuper
operator|=
name|path
expr_stmt|;
comment|//TODO : determine the context out of a clean root collection policy.
name|context
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
name|super
operator|.
name|splitPath
argument_list|(
name|pathForSuper
argument_list|)
expr_stmt|;
block|}
comment|/**      * To be called each time a private member that interacts with the wrapped      * URI is modified.      *      * @throws URISyntaxException if the URI is invalid.      */
annotation|@
name|Override
specifier|protected
name|void
name|recomputeURI
parameter_list|()
throws|throws
name|URISyntaxException
block|{
specifier|final
name|URI
name|oldWrappedURI
init|=
name|wrappedURI
decl_stmt|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|getInstanceName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|getInstanceName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"://"
argument_list|)
expr_stmt|;
block|}
comment|//No userInfo
if|if
condition|(
name|getHost
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getPort
argument_list|()
operator|!=
name|NO_PORT
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|//TODO : eventually use a prepend.root.collection system property
if|if
condition|(
name|getRawCollectionPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|getRawCollectionPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|wrappedURI
operator|=
operator|new
name|URI
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
name|wrappedURI
operator|=
name|oldWrappedURI
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|private
name|void
name|setContext
parameter_list|(
name|String
name|context
parameter_list|)
block|{
specifier|final
name|String
name|oldContext
init|=
name|this
operator|.
name|context
decl_stmt|;
try|try
block|{
comment|//trims any trailing slash
if|if
condition|(
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|//include root slash if we have a host
if|if
condition|(
name|this
operator|.
name|getHost
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|context
operator|=
name|context
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|context
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|context
operator|=
literal|""
operator|.
name|equals
argument_list|(
name|context
argument_list|)
condition|?
literal|null
else|:
name|context
expr_stmt|;
name|recomputeURI
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|oldContext
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid URI: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|URI
name|getURI
parameter_list|()
block|{
return|return
name|wrappedURI
return|;
block|}
annotation|@
name|Override
specifier|public
name|URI
name|getXmldbURI
parameter_list|()
block|{
return|return
name|URI
operator|.
name|create
argument_list|(
name|XMLDB_URI_PREFIX
operator|+
name|wrappedURI
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInstanceName
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getScheme
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getApiName
parameter_list|()
block|{
return|return
name|apiName
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAbsolute
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|isAbsolute
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isContextAbsolute
parameter_list|()
block|{
specifier|final
name|String
name|currentContext
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentContext
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|currentContext
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|XmldbURI
name|normalizeContext
parameter_list|()
block|{
specifier|final
name|String
name|currentContext
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentContext
operator|==
literal|null
condition|)
block|{
return|return
name|this
return|;
block|}
specifier|final
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|currentContext
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|FullXmldbURI
name|xmldbURI
init|=
operator|new
name|FullXmldbURI
argument_list|(
name|getXmldbURI
argument_list|()
argument_list|)
decl_stmt|;
name|xmldbURI
operator|.
name|setContext
argument_list|(
name|uri
operator|.
name|normalize
argument_list|()
operator|.
name|getRawPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|xmldbURI
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid URI: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|URI
name|relativizeContext
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|)
block|{
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The provided URI is null"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|currentContext
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The current context is null"
argument_list|)
throw|;
block|}
specifier|final
name|URI
name|contextURI
decl_stmt|;
comment|//Adds a final slash if necessary
if|if
condition|(
operator|!
name|currentContext
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Added a final '/' to '"
operator|+
name|currentContext
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|contextURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|currentContext
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contextURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|currentContext
argument_list|)
expr_stmt|;
block|}
return|return
name|contextURI
operator|.
name|relativize
argument_list|(
name|uri
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|URI
name|resolveContext
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
throws|throws
name|NullPointerException
throws|,
name|IllegalArgumentException
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The provided URI is null"
argument_list|)
throw|;
block|}
name|String
name|currentContext
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The current context is null"
argument_list|)
throw|;
block|}
comment|// Add a final slash if necessary
if|if
condition|(
operator|!
name|currentContext
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Added a final '/' to '"
operator|+
name|currentContext
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|currentContext
operator|+=
literal|"/"
expr_stmt|;
block|}
specifier|final
name|URI
name|contextURI
init|=
name|URI
operator|.
name|create
argument_list|(
name|currentContext
argument_list|)
decl_stmt|;
return|return
name|contextURI
operator|.
name|resolve
argument_list|(
name|str
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|URI
name|resolveContext
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|)
throws|throws
name|NullPointerException
block|{
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The provided URI is null"
argument_list|)
throw|;
block|}
name|String
name|currentContext
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The current context is null"
argument_list|)
throw|;
block|}
comment|// Add a final slash if necessary
if|if
condition|(
operator|!
name|currentContext
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Added a final '/' to '"
operator|+
name|currentContext
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|currentContext
operator|+=
literal|"/"
expr_stmt|;
block|}
specifier|final
name|URI
name|contextURI
init|=
name|URI
operator|.
name|create
argument_list|(
name|currentContext
argument_list|)
decl_stmt|;
return|return
name|contextURI
operator|.
name|resolve
argument_list|(
name|uri
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|hadXmldbPrefix
condition|)
block|{
return|return
name|XMLDB_URI_PREFIX
operator|+
name|wrappedURI
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|wrappedURI
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAuthority
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getAuthority
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFragment
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getFragment
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getPort
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getQuery
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRawAuthority
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getRawAuthority
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getHost
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserInfo
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getUserInfo
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRawFragment
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getRawFragment
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRawQuery
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getRawQuery
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRawUserInfo
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|getRawUserInfo
argument_list|()
return|;
block|}
block|}
end_class

end_unit

