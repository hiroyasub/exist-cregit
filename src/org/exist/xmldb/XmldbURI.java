begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; er version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:  */
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
name|MalformedURLException
import|;
end_import

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
name|URLDecoder
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

begin_comment
comment|/** A utility class for xmldb URis.  * Since, java.net.URI is<strong>final</strong> this class acts as a wrapper.  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|XmldbURI
block|{
comment|//Should be provided by org.xmldb.api package !!!
specifier|public
specifier|static
specifier|final
name|String
name|XMLDB_URI_PREFIX
init|=
literal|"xmldb:"
decl_stmt|;
specifier|private
name|URI
name|wrappedURI
decl_stmt|;
specifier|private
name|String
name|instanceName
decl_stmt|;
specifier|private
name|String
name|host
decl_stmt|;
specifier|private
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|String
name|context
decl_stmt|;
specifier|private
name|String
name|escapedCollectionName
decl_stmt|;
specifier|private
name|String
name|apiName
decl_stmt|;
comment|/** Contructs an XmldbURI from given string. 	 * Note that we construct URIs starting with XmldbURI.XMLDB_URI_PREFIX. 	 * Do not forget that org.xmldb.api.DatabaseManager<strong>trims</strong> this prefix.  	 * @param xmldbURI A string  	 * @throws URISyntaxException If the given string is not a valid xmldb URI. 	 */
specifier|public
name|XmldbURI
parameter_list|(
name|String
name|xmldbURI
parameter_list|)
throws|throws
name|URISyntaxException
block|{
try|try
block|{
name|wrappedURI
operator|=
operator|new
name|URI
argument_list|(
name|xmldbURI
argument_list|)
expr_stmt|;
name|parseURI
argument_list|()
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
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/** Contructs an XmldbURI from the given string, handling the necessary escapings. 	 * @param accessURI 	 * @param collectionName An unescaped collection name. 	 * @throws URISyntaxException 	 */
specifier|public
name|XmldbURI
parameter_list|(
name|String
name|accessURI
parameter_list|,
name|String
name|collectionName
parameter_list|)
throws|throws
name|URISyntaxException
block|{
try|try
block|{
name|String
name|escaped
init|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|collectionName
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|//This is the trick : unescape slashed in order to keep java.net.URI capabilities
name|escaped
operator|=
name|escaped
operator|.
name|replaceAll
argument_list|(
literal|"%2F"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|wrappedURI
operator|=
operator|new
name|URI
argument_list|(
name|accessURI
operator|+
name|escaped
argument_list|)
expr_stmt|;
name|parseURI
argument_list|()
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
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|wrappedURI
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|accessURI
operator|+
name|collectionName
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** Feeds private members 	 * @throws URISyntaxException 	 */
specifier|private
name|void
name|parseURI
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|String
name|path
init|=
literal|null
decl_stmt|;
name|URI
name|truncatedURI
decl_stmt|;
comment|//Reinitialise members
name|this
operator|.
name|instanceName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|host
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|port
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|apiName
operator|=
literal|null
expr_stmt|;
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
name|path
operator|=
name|wrappedURI
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|wrappedURI
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|XMLDB_URI_PREFIX
argument_list|)
condition|)
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|wrappedURI
operator|.
name|toString
argument_list|()
argument_list|,
literal|"xmldb URI scheme does not start with "
operator|+
name|XMLDB_URI_PREFIX
argument_list|)
throw|;
try|try
block|{
name|truncatedURI
operator|=
operator|new
name|URI
argument_list|(
name|wrappedURI
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
name|XMLDB_URI_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|//Put the "right" URI in the message ;-)
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|wrappedURI
operator|.
name|toString
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|truncatedURI
operator|.
name|getQuery
argument_list|()
operator|!=
literal|null
condition|)
comment|//Put the "right" URI in the message ;-)
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|wrappedURI
operator|.
name|toString
argument_list|()
argument_list|,
literal|"xmldb URI should not provide a query part"
argument_list|)
throw|;
if|if
condition|(
name|truncatedURI
operator|.
name|getFragment
argument_list|()
operator|!=
literal|null
condition|)
comment|//Put the "right" URI in the message ;-)
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|wrappedURI
operator|.
name|toString
argument_list|()
argument_list|,
literal|"xmldb URI should not provide a fragment part"
argument_list|)
throw|;
comment|//Is an encoded scheme ever possible ?
name|instanceName
operator|=
name|truncatedURI
operator|.
name|getScheme
argument_list|()
expr_stmt|;
if|if
condition|(
name|instanceName
operator|==
literal|null
condition|)
comment|//Put the "right" URI in the message ;-)
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|wrappedURI
operator|.
name|toString
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"xmldb URI scheme has no instance name"
argument_list|)
throw|;
name|host
operator|=
name|truncatedURI
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|port
operator|=
name|truncatedURI
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|path
operator|=
name|truncatedURI
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
name|splitPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/** Given a java.net.URI.getPath(),<strong>tries</strong> to dispatch the host's context 	 * from the collection name as smartly as possible.  	 * One would probably prefer a split policy based on the presence of a well-known root collection. 	 * @param path The java.net.URI.getPath() provided. 	 * @throws URISyntaxException 	 */
specifier|private
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
name|this
operator|.
name|escapedCollectionName
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|host
operator|!=
literal|null
condition|)
block|{
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
literal|"xmlrpc"
expr_stmt|;
name|escapedCollectionName
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
literal|"webdav"
expr_stmt|;
name|escapedCollectionName
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
comment|//Default : a local URI...
if|if
condition|(
name|apiName
operator|==
literal|null
condition|)
block|{
name|apiName
operator|=
literal|"rest-style"
expr_stmt|;
name|escapedCollectionName
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
else|else
block|{
if|if
condition|(
name|port
operator|>
operator|-
literal|1
condition|)
comment|//Put the "right" URI in the message ;-)
throw|throw
operator|new
name|URISyntaxException
argument_list|(
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
literal|"direct access"
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
name|escapedCollectionName
operator|=
name|path
expr_stmt|;
block|}
comment|//Trim trailing slash if necessary
if|if
condition|(
name|escapedCollectionName
operator|!=
literal|null
operator|&&
name|escapedCollectionName
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|escapedCollectionName
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|escapedCollectionName
operator|=
name|escapedCollectionName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|escapedCollectionName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|//TODO : check that collectionName starts with DBBroker.ROOT_COLLECTION ?
block|}
block|}
comment|/** To be called each time a private member that interacts with the wrapped URI is modified. 	 * @throws URISyntaxException 	 */
specifier|private
name|void
name|recomputeURI
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|instanceName
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
name|XMLDB_URI_PREFIX
argument_list|)
operator|.
name|append
argument_list|(
name|instanceName
argument_list|)
operator|.
name|append
argument_list|(
literal|"://"
argument_list|)
expr_stmt|;
if|if
condition|(
name|host
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
name|host
argument_list|)
expr_stmt|;
if|if
condition|(
name|port
operator|>
operator|-
literal|1
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|":"
operator|+
name|port
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
name|escapedCollectionName
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
name|escapedCollectionName
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
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/** To be called before a context operation with another XmldbURI. 	 * @param uri 	 * @throws IllegalArgumentException 	 */
specifier|private
name|void
name|checkCompatibilityForContextOperation
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|this
operator|.
name|getInstanceName
argument_list|()
operator|!=
literal|null
operator|&&
name|uri
operator|.
name|getInstanceName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|getInstanceName
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
operator|.
name|getInstanceName
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|.
name|getInstanceName
argument_list|()
operator|+
literal|" instance differs from "
operator|+
name|uri
operator|.
name|getInstanceName
argument_list|()
argument_list|)
throw|;
comment|//case insensitive comparison
if|if
condition|(
name|this
operator|.
name|getHost
argument_list|()
operator|!=
literal|null
operator|&&
name|uri
operator|.
name|getHost
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|getHost
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|.
name|getHost
argument_list|()
operator|+
literal|" host differs from "
operator|+
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|getPort
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|uri
operator|.
name|getPort
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|this
operator|.
name|getPort
argument_list|()
operator|!=
name|uri
operator|.
name|getPort
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|.
name|getPort
argument_list|()
operator|+
literal|" port differs from "
operator|+
name|uri
operator|.
name|getPort
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|getCollectionName
argument_list|()
operator|!=
literal|null
operator|&&
name|uri
operator|.
name|getCollectionName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|getCollectionName
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
operator|.
name|getCollectionName
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|.
name|getCollectionName
argument_list|()
operator|+
literal|" collection differs from "
operator|+
name|uri
operator|.
name|getCollectionName
argument_list|()
argument_list|)
throw|;
block|}
comment|/** To be called before a collection name operation with another XmldbURI. 	 * @param uri 	 * @throws IllegalArgumentException 	 */
specifier|private
name|void
name|checkCompatibilityForCollectionOperation
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|this
operator|.
name|getInstanceName
argument_list|()
operator|!=
literal|null
operator|&&
name|uri
operator|.
name|getInstanceName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|getInstanceName
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
operator|.
name|getInstanceName
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|.
name|getInstanceName
argument_list|()
operator|+
literal|" instance differs from "
operator|+
name|uri
operator|.
name|getInstanceName
argument_list|()
argument_list|)
throw|;
comment|//case insensitive comparison
if|if
condition|(
name|this
operator|.
name|getHost
argument_list|()
operator|!=
literal|null
operator|&&
name|uri
operator|.
name|getHost
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|getHost
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|.
name|getHost
argument_list|()
operator|+
literal|" host differs from "
operator|+
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|getPort
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|uri
operator|.
name|getPort
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|this
operator|.
name|getPort
argument_list|()
operator|!=
name|uri
operator|.
name|getPort
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|.
name|getPort
argument_list|()
operator|+
literal|" port differs from "
operator|+
name|uri
operator|.
name|getPort
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|getContext
argument_list|()
operator|!=
literal|null
operator|&&
name|uri
operator|.
name|getContext
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|getContext
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
operator|.
name|getContext
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|.
name|getContext
argument_list|()
operator|+
literal|" context differs from "
operator|+
name|uri
operator|.
name|getContext
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|void
name|setInstanceName
parameter_list|(
name|String
name|instanceName
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|String
name|oldInstanceName
init|=
name|this
operator|.
name|instanceName
decl_stmt|;
try|try
block|{
name|this
operator|.
name|instanceName
operator|=
name|instanceName
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
name|instanceName
operator|=
name|oldInstanceName
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|public
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|String
name|oldHost
init|=
name|this
operator|.
name|host
decl_stmt|;
try|try
block|{
name|this
operator|.
name|host
operator|=
name|host
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
name|host
operator|=
name|oldHost
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|public
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|URISyntaxException
block|{
comment|//TODO : check range ?
name|int
name|oldPort
init|=
name|this
operator|.
name|port
decl_stmt|;
try|try
block|{
name|this
operator|.
name|port
operator|=
name|port
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
name|port
operator|=
name|oldPort
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|public
name|void
name|setContext
parameter_list|(
name|String
name|context
parameter_list|)
throws|throws
name|URISyntaxException
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
name|e
throw|;
block|}
block|}
specifier|public
name|void
name|setContext
parameter_list|(
name|URI
name|context
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|String
name|str
init|=
name|context
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setContext
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCollectionName
parameter_list|(
name|String
name|collectionName
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|String
name|oldCollectionName
init|=
name|collectionName
decl_stmt|;
try|try
block|{
if|if
condition|(
name|collectionName
operator|==
literal|null
condition|)
name|this
operator|.
name|escapedCollectionName
operator|=
literal|null
expr_stmt|;
else|else
block|{
name|String
name|escaped
init|=
name|URLEncoder
operator|.
name|encode
argument_list|(
name|collectionName
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|//This is the trick : unescape slashed in order to keep java.net.URI capabilities
name|escaped
operator|=
name|escaped
operator|.
name|replaceAll
argument_list|(
literal|"%2F"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|this
operator|.
name|escapedCollectionName
operator|=
name|escaped
expr_stmt|;
block|}
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
name|escapedCollectionName
operator|=
name|oldCollectionName
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|wrappedURI
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|this
operator|.
name|toString
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setCollectionName
parameter_list|(
name|URI
name|collectionName
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|String
name|str
init|=
name|context
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setCollectionName
argument_list|(
name|str
argument_list|)
expr_stmt|;
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
name|String
name|getInstanceName
parameter_list|()
block|{
return|return
name|instanceName
return|;
block|}
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|host
return|;
block|}
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
specifier|public
name|String
name|getCollectionName
parameter_list|()
block|{
if|if
condition|(
name|escapedCollectionName
operator|==
literal|null
condition|)
return|return
literal|null
return|;
try|try
block|{
return|return
name|URLDecoder
operator|.
name|decode
argument_list|(
name|escapedCollectionName
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|//Should never happen
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|escapedCollectionName
operator|+
literal|" can not be properly escaped"
argument_list|)
throw|;
block|}
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
name|int
name|compareTo
parameter_list|(
name|Object
name|ob
parameter_list|)
throws|throws
name|ClassCastException
block|{
if|if
condition|(
operator|!
operator|(
name|ob
operator|instanceof
name|XmldbURI
operator|)
condition|)
throw|throw
operator|new
name|ClassCastException
argument_list|(
literal|"The provided Object is not an XmldbURI"
argument_list|)
throw|;
return|return
name|wrappedURI
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|XmldbURI
operator|)
name|ob
operator|)
operator|.
name|getURI
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|XmldbURI
name|create
parameter_list|(
name|String
name|str
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|XmldbURI
argument_list|(
name|str
argument_list|)
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|ob
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|ob
operator|instanceof
name|XmldbURI
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|wrappedURI
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|XmldbURI
operator|)
name|ob
operator|)
operator|.
name|getURI
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isOpaque
parameter_list|()
block|{
return|return
name|wrappedURI
operator|.
name|isOpaque
argument_list|()
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
name|XmldbURI
name|xmldbURI
init|=
operator|new
name|XmldbURI
argument_list|(
name|this
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|xmldbURI
operator|.
name|setContext
argument_list|(
operator|(
name|uri
operator|.
name|normalize
argument_list|()
operator|)
operator|.
name|toString
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
init|=
name|URI
operator|.
name|create
argument_list|(
name|context
argument_list|)
decl_stmt|;
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
init|=
name|URI
operator|.
name|create
argument_list|(
name|context
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
init|=
name|URI
operator|.
name|create
argument_list|(
name|context
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
specifier|public
name|String
name|toASCIIString
parameter_list|()
block|{
comment|//TODO : trim trailing slash if necessary
return|return
name|wrappedURI
operator|.
name|toASCIIString
argument_list|()
return|;
block|}
specifier|public
name|URL
name|toURL
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|MalformedURLException
block|{
return|return
name|wrappedURI
operator|.
name|toURL
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|//TODO : trim trailing slash if necessary
return|return
name|wrappedURI
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//	TODO : prefefined URIs as static classes...
block|}
end_class

end_unit

