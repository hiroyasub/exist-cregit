begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
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
name|InvalidPathException
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
name|Path
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
name|EXistException
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
name|BrokerPool
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
name|FileUtils
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Factory to create a {@link org.exist.source.Source} object for a given  * URL.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|SourceFactory
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SourceFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Create a {@link Source} object for the given resource URL.      *      * As a special case, if the URL starts with "resource:", the resource      * will be read from the current context class loader.      *      * @param contextPath the context path of the resource.      * @param location the location of the resource (relative to the {@code contextPath}).      * @param checkXQEncoding where we need to check the encoding of the XQuery.      *      * @return The Source of the resource, or null if the resource cannot be found.      *      * @throws PermissionDeniedException if the resource resides in the database,      *     but the calling user does not have permission to access it.      * @throws IOException if a general I/O error occurs whilst accessing the resource.      */
specifier|public
specifier|static
annotation|@
name|Nullable
name|Source
name|getSource
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|String
name|contextPath
parameter_list|,
specifier|final
name|String
name|location
parameter_list|,
specifier|final
name|boolean
name|checkXQEncoding
parameter_list|)
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
block|{
name|Source
name|source
init|=
literal|null
decl_stmt|;
comment|/* resource: */
if|if
condition|(
name|location
operator|.
name|startsWith
argument_list|(
name|ClassLoaderSource
operator|.
name|PROTOCOL
argument_list|)
operator|||
operator|(
name|contextPath
operator|!=
literal|null
operator|&&
name|contextPath
operator|.
name|startsWith
argument_list|(
name|ClassLoaderSource
operator|.
name|PROTOCOL
argument_list|)
operator|)
condition|)
block|{
name|source
operator|=
name|getSource_fromClasspath
argument_list|(
name|contextPath
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
comment|/* xmldb */
if|if
condition|(
name|source
operator|==
literal|null
operator|&&
operator|(
name|location
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
operator|||
operator|(
name|contextPath
operator|!=
literal|null
operator|&&
name|contextPath
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
operator|)
operator|)
condition|)
block|{
name|XmldbURI
name|pathUri
decl_stmt|;
try|try
block|{
if|if
condition|(
name|contextPath
operator|==
literal|null
condition|)
block|{
name|pathUri
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pathUri
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|contextPath
argument_list|)
operator|.
name|append
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// this is allowed if the location is already an absolute URI, below we will try using other schemes
name|pathUri
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|pathUri
operator|!=
literal|null
condition|)
block|{
name|source
operator|=
name|getSource_fromDb
argument_list|(
name|broker
argument_list|,
name|pathUri
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* /db */
if|if
condition|(
name|source
operator|==
literal|null
operator|&&
operator|(
operator|(
name|location
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
operator|&&
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|firstPathSegment
argument_list|(
name|location
argument_list|)
argument_list|)
argument_list|)
operator|)
operator|||
operator|(
name|contextPath
operator|!=
literal|null
operator|&&
name|contextPath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
operator|&&
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|firstPathSegment
argument_list|(
name|contextPath
argument_list|)
argument_list|)
argument_list|)
operator|)
operator|)
condition|)
block|{
specifier|final
name|XmldbURI
name|pathUri
decl_stmt|;
if|if
condition|(
name|contextPath
operator|==
literal|null
condition|)
block|{
name|pathUri
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pathUri
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|contextPath
argument_list|)
operator|.
name|append
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
name|source
operator|=
name|getSource_fromDb
argument_list|(
name|broker
argument_list|,
name|pathUri
argument_list|)
expr_stmt|;
block|}
comment|/* file:// or location without scheme (:/) is assumed to be a file */
if|if
condition|(
name|source
operator|==
literal|null
operator|&&
operator|(
name|location
operator|.
name|startsWith
argument_list|(
literal|"file:/"
argument_list|)
operator|||
operator|!
name|location
operator|.
name|contains
argument_list|(
literal|":/"
argument_list|)
operator|)
condition|)
block|{
name|source
operator|=
name|getSource_fromFile
argument_list|(
name|contextPath
argument_list|,
name|location
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
comment|/* final attempt - any other URL */
if|if
condition|(
name|source
operator|==
literal|null
operator|&&
operator|!
operator|(
name|location
operator|.
name|startsWith
argument_list|(
name|ClassLoaderSource
operator|.
name|PROTOCOL
argument_list|)
operator|||
name|location
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
operator|||
name|location
operator|.
name|startsWith
argument_list|(
literal|"file:/"
argument_list|)
operator|)
condition|)
block|{
try|try
block|{
specifier|final
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|source
operator|=
operator|new
name|URLSource
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedURLException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|source
return|;
block|}
specifier|private
specifier|static
name|String
name|firstPathSegment
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
return|return
name|XmldbURI
operator|.
name|create
argument_list|(
name|path
argument_list|)
operator|.
name|getPathSegments
argument_list|()
index|[
literal|0
index|]
operator|.
name|getRawCollectionPath
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|Source
name|getSource_fromClasspath
parameter_list|(
specifier|final
name|String
name|contextPath
parameter_list|,
specifier|final
name|String
name|location
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|location
operator|.
name|startsWith
argument_list|(
name|ClassLoaderSource
operator|.
name|PROTOCOL
argument_list|)
condition|)
block|{
return|return
operator|new
name|ClassLoaderSource
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|final
name|Path
name|rootPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|contextPath
operator|.
name|substring
argument_list|(
name|ClassLoaderSource
operator|.
name|PROTOCOL
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// 1) try resolving location as child
specifier|final
name|Path
name|childLocation
init|=
name|rootPath
operator|.
name|resolve
argument_list|(
name|location
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|ClassLoaderSource
argument_list|(
name|ClassLoaderSource
operator|.
name|PROTOCOL
operator|+
name|childLocation
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
comment|// no-op, we will try again below
block|}
comment|// 2) try resolving location as sibling
specifier|final
name|Path
name|siblingLocation
init|=
name|rootPath
operator|.
name|resolveSibling
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
operator|new
name|ClassLoaderSource
argument_list|(
name|ClassLoaderSource
operator|.
name|PROTOCOL
operator|+
name|siblingLocation
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Get the resource source from the database.      *      * @param broker The database broker.      * @param path The path to the resource in the database.      *      * @return the source, or null if there is no such resource in the db indicated by {@code path}.      */
specifier|private
specifier|static
annotation|@
name|Nullable
name|Source
name|getSource_fromDb
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|XmldbURI
name|path
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
block|{
name|Source
name|source
init|=
literal|null
decl_stmt|;
try|try
init|(
specifier|final
name|LockedDocument
name|lockedResource
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|path
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|lockedResource
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DocumentImpl
name|resource
init|=
name|lockedResource
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|resource
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
name|source
operator|=
operator|new
name|DBSource
argument_list|(
name|broker
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|resource
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
comment|// XML document: serialize to string source so it can be read as a stream
comment|// by fn:unparsed-text and friends
name|source
operator|=
operator|new
name|StringSource
argument_list|(
name|broker
operator|.
name|getSerializer
argument_list|()
operator|.
name|serialize
argument_list|(
name|resource
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|source
return|;
block|}
comment|/**      * Get the resource source from the filesystem.      *      * @param contextPath the context path of the resource.      * @param location the location of the resource (relative to the {@code contextPath}).      * @param checkXQEncoding where we need to check the encoding of the XQuery.      *      * @return the source, or null if there is no such resource in the db indicated by {@code path}.      */
specifier|private
specifier|static
annotation|@
name|Nullable
name|Source
name|getSource_fromFile
parameter_list|(
specifier|final
name|String
name|contextPath
parameter_list|,
specifier|final
name|String
name|location
parameter_list|,
specifier|final
name|boolean
name|checkXQEncoding
parameter_list|)
block|{
name|String
name|locationPath
init|=
name|location
operator|.
name|replaceAll
argument_list|(
literal|"^(file:)?/*(.*)$"
argument_list|,
literal|"$2"
argument_list|)
decl_stmt|;
name|Source
name|source
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|Path
name|p
init|=
name|Paths
operator|.
name|get
argument_list|(
name|contextPath
argument_list|,
name|locationPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|locationPath
operator|=
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|p
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
comment|// continue trying
block|}
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|Path
name|p2
init|=
name|Paths
operator|.
name|get
argument_list|(
name|locationPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|p2
argument_list|)
condition|)
block|{
name|locationPath
operator|=
name|p2
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|p2
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
comment|// continue trying
block|}
block|}
if|if
condition|(
name|source
operator|==
literal|null
operator|&&
name|contextPath
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|Path
name|p3
init|=
name|Paths
operator|.
name|get
argument_list|(
name|contextPath
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|resolve
argument_list|(
name|locationPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|p3
argument_list|)
condition|)
block|{
name|locationPath
operator|=
name|p3
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|p3
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
comment|// continue trying
block|}
block|}
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
comment|/*              * Try to load as an absolute path              */
try|try
block|{
specifier|final
name|Path
name|p4
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"/"
operator|+
name|locationPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|p4
argument_list|)
condition|)
block|{
name|locationPath
operator|=
name|p4
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|p4
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
comment|// continue trying
block|}
block|}
if|if
condition|(
name|source
operator|==
literal|null
operator|&&
name|contextPath
operator|!=
literal|null
condition|)
block|{
comment|/*              * Try to load from the folder of the contextPath              */
try|try
block|{
specifier|final
name|Path
name|p5
init|=
name|Paths
operator|.
name|get
argument_list|(
name|contextPath
argument_list|)
operator|.
name|resolveSibling
argument_list|(
name|locationPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|p5
argument_list|)
condition|)
block|{
name|locationPath
operator|=
name|p5
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|p5
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
comment|// continue trying
block|}
block|}
if|if
condition|(
name|source
operator|==
literal|null
operator|&&
name|contextPath
operator|!=
literal|null
condition|)
block|{
comment|/*              * Try to load from the parent folder of the contextPath URL              */
try|try
block|{
name|Path
name|p6
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextPath
operator|.
name|startsWith
argument_list|(
literal|"file:/"
argument_list|)
condition|)
block|{
try|try
block|{
name|p6
operator|=
name|Paths
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|contextPath
argument_list|)
argument_list|)
operator|.
name|resolveSibling
argument_list|(
name|locationPath
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
comment|// continue trying
block|}
block|}
if|if
condition|(
name|p6
operator|==
literal|null
condition|)
block|{
name|p6
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|contextPath
operator|.
name|replaceFirst
argument_list|(
literal|"^file:/*(/.*)$"
argument_list|,
literal|"$1"
argument_list|)
argument_list|)
operator|.
name|resolveSibling
argument_list|(
name|locationPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|p6
argument_list|)
condition|)
block|{
name|locationPath
operator|=
name|p6
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|p6
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
comment|// continue trying
block|}
block|}
if|if
condition|(
name|source
operator|==
literal|null
operator|&&
name|contextPath
operator|!=
literal|null
condition|)
block|{
comment|/*              * Try to load from the contextPath URL folder              */
try|try
block|{
name|Path
name|p7
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextPath
operator|.
name|startsWith
argument_list|(
literal|"file:/"
argument_list|)
condition|)
block|{
try|try
block|{
name|p7
operator|=
name|Paths
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
name|contextPath
argument_list|)
argument_list|)
operator|.
name|resolve
argument_list|(
name|locationPath
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
comment|// continue trying
block|}
block|}
if|if
condition|(
name|p7
operator|==
literal|null
condition|)
block|{
name|p7
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|contextPath
operator|.
name|replaceFirst
argument_list|(
literal|"^file:/*(/.*)$"
argument_list|,
literal|"$1"
argument_list|)
argument_list|)
operator|.
name|resolve
argument_list|(
name|locationPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|p7
argument_list|)
condition|)
block|{
name|locationPath
operator|=
name|p7
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|p7
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
comment|// continue trying
block|}
block|}
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
comment|/*              * Lastly we try to load it using EXIST_HOME as the reference point              */
name|Path
name|p8
init|=
literal|null
decl_stmt|;
try|try
block|{
name|p8
operator|=
name|FileUtils
operator|.
name|resolve
argument_list|(
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
argument_list|,
name|locationPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|p8
argument_list|)
condition|)
block|{
name|locationPath
operator|=
name|p8
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|p8
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InvalidPathException
name|e
parameter_list|)
block|{
comment|// continue and abort below
block|}
block|}
return|return
name|source
return|;
block|}
block|}
end_class

end_unit

