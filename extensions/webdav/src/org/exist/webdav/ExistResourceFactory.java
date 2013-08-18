begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webdav
package|;
end_package

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
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|ResourceFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|collections
operator|.
name|Collection
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
name|DocumentImpl
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
name|serializers
operator|.
name|EXistOutputKeys
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

begin_comment
comment|/**  * Class for constructing Milton WebDAV framework resource objects  .  *  * @author Dannes Wessels (dizzzz_at_exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ExistResourceFactory
implements|implements
name|ResourceFactory
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ExistResourceFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|brokerPool
init|=
literal|null
decl_stmt|;
comment|//	default output properties for the XML serialization
specifier|public
specifier|final
specifier|static
name|Properties
name|DEFAULT_WEBDAV_OPTIONS
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|/** XML serialization options */
specifier|private
name|Properties
name|webDavOptions
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|/**      * Default serialization options      */
static|static
block|{
name|DEFAULT_WEBDAV_OPTIONS
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|DEFAULT_WEBDAV_OPTIONS
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|DEFAULT_WEBDAV_OPTIONS
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|DEFAULT_WEBDAV_OPTIONS
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|DEFAULT_WEBDAV_OPTIONS
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|PROCESS_XSL_PI
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
specifier|private
enum|enum
name|ResourceType
block|{
name|DOCUMENT
block|,
name|COLLECTION
block|,
name|IGNORABLE
block|,
name|NOT_EXISTING
block|}
empty_stmt|;
comment|/**      * Default constructor. Get access to instance of exist-db broker pool.      */
specifier|public
name|ExistResourceFactory
parameter_list|()
block|{
try|try
block|{
name|brokerPool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to initialize WebDAV interface."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Set default values
name|webDavOptions
operator|.
name|putAll
argument_list|(
name|DEFAULT_WEBDAV_OPTIONS
argument_list|)
expr_stmt|;
comment|// load specific options
try|try
block|{
comment|// Find right file
name|File
name|eXistHome
init|=
name|brokerPool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|File
name|config
init|=
operator|new
name|File
argument_list|(
name|eXistHome
argument_list|,
literal|"webdav.properties"
argument_list|)
decl_stmt|;
comment|// Read from file if existent
if|if
condition|(
name|config
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Read WebDAV configuration from %s"
argument_list|,
name|config
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|webDavOptions
operator|.
name|load
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Using WebDAV default serialization options."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Construct Resource for path. A Document or Collection resource is returned, NULL if type      * could not be detected.      */
annotation|@
name|Override
specifier|public
name|Resource
name|getResource
parameter_list|(
name|String
name|host
parameter_list|,
name|String
name|path
parameter_list|)
block|{
comment|// DWES: work around if no /db is available return nothing.
if|if
condition|(
operator|!
name|path
operator|.
name|contains
argument_list|(
literal|"/db"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"path should at least contain /db"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Construct path as eXist-db XmldbURI
name|XmldbURI
name|xmldbUri
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Strip preceding path, all up to /db
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
name|path
operator|.
name|indexOf
argument_list|(
literal|"/db"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Strip last slash if available
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"host='%s' path='%s'"
argument_list|,
name|host
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Create uri inside database
name|xmldbUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to convert path '%s'into a XmldbURI representation."
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Return appropriate resource
switch|switch
condition|(
name|getResourceType
argument_list|(
name|brokerPool
argument_list|,
name|xmldbUri
argument_list|)
condition|)
block|{
case|case
name|DOCUMENT
case|:
name|MiltonDocument
name|doc
init|=
operator|new
name|MiltonDocument
argument_list|(
name|host
argument_list|,
name|xmldbUri
argument_list|,
name|brokerPool
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setConfiguration
argument_list|(
name|webDavOptions
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
case|case
name|COLLECTION
case|:
return|return
operator|new
name|MiltonCollection
argument_list|(
name|host
argument_list|,
name|xmldbUri
argument_list|,
name|brokerPool
argument_list|)
return|;
case|case
name|IGNORABLE
case|:
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"ignoring file"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
case|case
name|NOT_EXISTING
case|:
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Resource does not exist: '%s'"
argument_list|,
name|xmldbUri
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
default|default:
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unkown resource type for %s"
argument_list|,
name|xmldbUri
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/*      * Returns the resource type indicated by the path: either COLLECTION, DOCUMENT or NOT_EXISTING.      */
specifier|private
name|ResourceType
name|getResourceType
parameter_list|(
name|BrokerPool
name|brokerPool
parameter_list|,
name|XmldbURI
name|xmldbUri
parameter_list|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
name|ResourceType
name|type
init|=
name|ResourceType
operator|.
name|NOT_EXISTING
decl_stmt|;
comment|// MacOsX finder specific files
name|String
name|documentSeqment
init|=
name|xmldbUri
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|documentSeqment
operator|.
name|startsWith
argument_list|(
literal|"._"
argument_list|)
operator|||
name|documentSeqment
operator|.
name|equals
argument_list|(
literal|".DS_Store"
argument_list|)
condition|)
block|{
comment|//LOG.debug(String.format("Ignoring MacOSX file '%s'", xmldbUri.lastSegment().toString()));
comment|//return ResourceType.IGNORABLE;
block|}
comment|// Documents that start with a dot
if|if
condition|(
name|documentSeqment
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
comment|//LOG.debug(String.format("Ignoring '.' file '%s'", xmldbUri.lastSegment().toString()));
comment|//return ResourceType.IGNORABLE;
block|}
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Path: %s"
argument_list|,
name|xmldbUri
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Try to read as system user. Note that the actual user is not know
comment|// yet. In MiltonResource the actual authentication and authorization
comment|// is performed.
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|brokerPool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
comment|// First check if resource is a collection
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|xmldbUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|ResourceType
operator|.
name|COLLECTION
expr_stmt|;
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|collection
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// If it is not a collection, check if it is a document
name|document
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|xmldbUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|!=
literal|null
condition|)
block|{
comment|// Document is found
name|type
operator|=
name|ResourceType
operator|.
name|DOCUMENT
expr_stmt|;
name|document
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|document
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// No document and no collection.
name|type
operator|=
name|ResourceType
operator|.
name|NOT_EXISTING
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Error determining nature of resource %s"
argument_list|,
name|xmldbUri
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|type
operator|=
name|ResourceType
operator|.
name|NOT_EXISTING
expr_stmt|;
block|}
finally|finally
block|{
comment|// Clean-up, just in case
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
comment|// Clean-up, just in case
if|if
condition|(
name|document
operator|!=
literal|null
condition|)
block|{
name|document
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
comment|// Return broker to pool
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Resource type=%s"
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
block|}
end_class

end_unit

