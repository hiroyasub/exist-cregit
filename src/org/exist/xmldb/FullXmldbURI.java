begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
comment|/** 	 * Contructs an XmldbURI from given URI. 	 * The provided URI must have the XMLDB_SCHEME ("xmldb") 	 * @param xmldbURI A string  	 * @throws URISyntaxException If the given string is not a valid xmldb URI. 	 */
specifier|protected
name|FullXmldbURI
parameter_list|(
name|URI
name|xmldbURI
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|super
argument_list|(
name|xmldbURI
argument_list|)
expr_stmt|;
block|}
comment|/** Feeds private members      * @throws URISyntaxException      */
specifier|protected
name|void
name|parseURI
parameter_list|(
name|URI
name|xmldbURI
parameter_list|,
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
comment|//Put the "right" URI in the message ;-)
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
comment|//Eventually rewrite wrappedURI *without* user info
if|if
condition|(
name|userInfo
operator|!=
literal|null
condition|)
block|{
name|StringBuffer
name|recomputed
init|=
operator|new
name|StringBuffer
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
specifier|protected
name|void
name|splitPath
parameter_list|(
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
comment|//Put the "right" URI in the message ;-)
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
comment|/** To be called each time a private member that interacts with the wrapped URI is modified. 	 * @throws URISyntaxException 	 */
specifier|protected
name|void
name|recomputeURI
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|URI
name|oldWrappedURI
init|=
name|wrappedURI
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|getInstanceName
argument_list|()
operator|!=
literal|null
condition|)
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
comment|//No userInfo
if|if
condition|(
name|getHost
argument_list|()
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getPort
argument_list|()
operator|!=
name|NO_PORT
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|":"
operator|+
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|//TODO : eventually use a prepend.root.collection system property
if|if
condition|(
name|getRawCollectionPath
argument_list|()
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
name|getRawCollectionPath
argument_list|()
argument_list|)
expr_stmt|;
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
specifier|public
name|URI
name|getURI
parameter_list|()
block|{
return|return
name|wrappedURI
return|;
block|}
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
specifier|public
name|String
name|getApiName
parameter_list|()
block|{
return|return
name|apiName
return|;
block|}
specifier|public
name|String
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
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
specifier|public
name|boolean
name|isContextAbsolute
parameter_list|()
block|{
name|String
name|context
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
return|return
literal|true
return|;
return|return
name|context
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
return|;
block|}
specifier|public
name|XmldbURI
name|normalizeContext
parameter_list|()
block|{
name|String
name|context
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
return|return
name|this
return|;
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
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
specifier|public
name|URI
name|relativizeContext
parameter_list|(
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
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The provided URI is null"
argument_list|)
throw|;
name|String
name|context
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The current context is null"
argument_list|)
throw|;
name|URI
name|contextURI
decl_stmt|;
comment|//Adds a final slash if necessary
if|if
condition|(
operator|!
name|context
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
name|context
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
name|context
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
else|else
name|contextURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|contextURI
operator|.
name|relativize
argument_list|(
name|uri
argument_list|)
return|;
block|}
specifier|public
name|URI
name|resolveContext
parameter_list|(
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
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The provided URI is null"
argument_list|)
throw|;
name|String
name|context
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The current context is null"
argument_list|)
throw|;
name|URI
name|contextURI
decl_stmt|;
comment|//Adds a final slash if necessary
if|if
condition|(
operator|!
name|context
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
name|context
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
name|context
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
else|else
name|contextURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|contextURI
operator|.
name|resolve
argument_list|(
name|str
argument_list|)
return|;
block|}
specifier|public
name|URI
name|resolveContext
parameter_list|(
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
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The provided URI is null"
argument_list|)
throw|;
name|String
name|context
init|=
name|this
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"The current context is null"
argument_list|)
throw|;
name|URI
name|contextURI
decl_stmt|;
comment|//Adds a final slash if necessary
if|if
condition|(
operator|!
name|context
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
name|context
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
name|context
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
else|else
name|contextURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|contextURI
operator|.
name|resolve
argument_list|(
name|uri
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.net.URI#getAuthority() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getFragment() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getPort() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getQuery() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getRawAuthority() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getHost() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getUserInfo() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getRawFragment() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getRawQuery() 	 */
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
comment|/* (non-Javadoc) 	 * @see java.net.URI#getRawUserInfo() 	 */
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

