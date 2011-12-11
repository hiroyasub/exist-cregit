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
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|Auth
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
name|CollectionResource
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
name|CopyableResource
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
name|DeletableResource
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
name|GetableResource
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
name|HttpManager
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
name|LockInfo
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
name|LockResult
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
name|LockTimeout
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
name|LockToken
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
name|LockableResource
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
name|MoveableResource
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
name|PropFindableResource
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
name|Range
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
name|exceptions
operator|.
name|BadRequestException
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
name|exceptions
operator|.
name|ConflictException
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
name|exceptions
operator|.
name|LockedException
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
name|exceptions
operator|.
name|NotAuthorizedException
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
name|exceptions
operator|.
name|PreConditionFailedException
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
name|webdav
operator|.
name|DefaultUserAgentHelper
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
name|webdav
operator|.
name|UserAgentHelper
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|TransformerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|security
operator|.
name|User
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
name|VirtualTempFile
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
name|serializer
operator|.
name|XMLWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|webdav
operator|.
name|ExistResource
operator|.
name|Mode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|webdav
operator|.
name|exceptions
operator|.
name|DocumentAlreadyLockedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|webdav
operator|.
name|exceptions
operator|.
name|DocumentNotLockedException
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
comment|/**  * Class for representing an eXist-db document as a Milton WebDAV document.  * See<a href="http://milton.ettrema.com">Milton</a>.  *  * @author Dannes Wessels (dizzzz_at_exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|MiltonDocument
extends|extends
name|MiltonResource
implements|implements
name|GetableResource
implements|,
name|PropFindableResource
implements|,
name|DeletableResource
implements|,
name|LockableResource
implements|,
name|MoveableResource
implements|,
name|CopyableResource
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PROPFIND_METHOD_XML_SIZE
init|=
literal|"org.exist.webdav.PROPFIND_METHOD_XML_SIZE"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GET_METHOD_XML_SIZE
init|=
literal|"org.exist.webdav.GET_METHOD_XML_SIZE"
decl_stmt|;
specifier|private
name|ExistDocument
name|existDocument
decl_stmt|;
specifier|private
name|VirtualTempFile
name|vtf
init|=
literal|null
decl_stmt|;
empty_stmt|;
comment|// Only for PROPFIND the estimate size for an XML document must be shown
specifier|private
name|boolean
name|isPropFind
init|=
literal|false
decl_stmt|;
specifier|private
enum|enum
name|SIZE_METHOD
block|{
name|NULL
block|,
name|EXACT
block|,
name|APPROXIMATE
block|}
empty_stmt|;
specifier|private
specifier|static
name|SIZE_METHOD
name|propfindSizeMethod
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|SIZE_METHOD
name|getSizeMethod
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|UserAgentHelper
name|userAgentHelper
init|=
literal|null
decl_stmt|;
comment|/**      * Set to TRUE if getContentLength is used for PROPFIND.      */
specifier|public
name|void
name|setIsPropFind
parameter_list|(
name|boolean
name|isPropFind
parameter_list|)
block|{
name|this
operator|.
name|isPropFind
operator|=
name|isPropFind
expr_stmt|;
block|}
comment|/**      *  Constructor of representation of a Document in the Milton framework, without user information.      * To be called by the resource factory.      *      * @param host  FQ host name including port number.      * @param uri   Path on server indicating path of resource      * @param brokerPool Handle to Exist database.      */
specifier|public
name|MiltonDocument
parameter_list|(
name|String
name|host
parameter_list|,
name|XmldbURI
name|uri
parameter_list|,
name|BrokerPool
name|brokerPool
parameter_list|)
block|{
name|this
argument_list|(
name|host
argument_list|,
name|uri
argument_list|,
name|brokerPool
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor of representation of a Document in the Milton framework, with user information.      * To be called by the resource factory.      *      * @param host  FQ host name including port number.      * @param uri   Path on server indicating path of resource.      * @param user  An Exist operation is performed with  User. Can be NULL.      * @param pool Handle to Exist database.      */
specifier|public
name|MiltonDocument
parameter_list|(
name|String
name|host
parameter_list|,
name|XmldbURI
name|uri
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|User
name|user
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
if|if
condition|(
name|userAgentHelper
operator|==
literal|null
condition|)
block|{
name|userAgentHelper
operator|=
operator|new
name|DefaultUserAgentHelper
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"DOCUMENT:"
operator|+
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|resourceXmldbUri
operator|=
name|uri
expr_stmt|;
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|existDocument
operator|=
operator|new
name|ExistDocument
argument_list|(
name|uri
argument_list|,
name|brokerPool
argument_list|)
expr_stmt|;
comment|// store simpler type
name|existResource
operator|=
name|existDocument
expr_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|existDocument
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|existDocument
operator|.
name|initMetadata
argument_list|()
expr_stmt|;
block|}
comment|// PROPFIND method
if|if
condition|(
name|propfindSizeMethod
operator|==
literal|null
condition|)
block|{
comment|// get user supplied preferred size determination approach
name|String
name|systemProp
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|PROPFIND_METHOD_XML_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|systemProp
operator|==
literal|null
condition|)
block|{
comment|// Default method is approximate
name|propfindSizeMethod
operator|=
name|SIZE_METHOD
operator|.
name|APPROXIMATE
expr_stmt|;
block|}
else|else
block|{
comment|// Try to parse from environment property
try|try
block|{
name|propfindSizeMethod
operator|=
name|SIZE_METHOD
operator|.
name|valueOf
argument_list|(
name|systemProp
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set preffered default
name|propfindSizeMethod
operator|=
name|SIZE_METHOD
operator|.
name|APPROXIMATE
expr_stmt|;
block|}
block|}
block|}
comment|// GET method
if|if
condition|(
name|getSizeMethod
operator|==
literal|null
condition|)
block|{
comment|// get user supplied preferred size determination approach
name|String
name|systemProp
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|GET_METHOD_XML_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|systemProp
operator|==
literal|null
condition|)
block|{
comment|// Default method is NULL
name|getSizeMethod
operator|=
name|SIZE_METHOD
operator|.
name|NULL
expr_stmt|;
block|}
else|else
block|{
comment|// Try to parse from environment property
try|try
block|{
name|getSizeMethod
operator|=
name|SIZE_METHOD
operator|.
name|valueOf
argument_list|(
name|systemProp
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set preffered default
name|getSizeMethod
operator|=
name|SIZE_METHOD
operator|.
name|APPROXIMATE
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/* ================      * GettableResource      * ================ */
comment|//@Override
specifier|public
name|void
name|sendContent
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|Range
name|range
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|String
name|contentType
parameter_list|)
throws|throws
name|IOException
throws|,
name|NotAuthorizedException
throws|,
name|BadRequestException
block|{
try|try
block|{
if|if
condition|(
name|vtf
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Serializing from database"
argument_list|)
expr_stmt|;
name|existDocument
operator|.
name|stream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Experimental. Does not work right, the virtual file
comment|// Often does not contain the right amount of bytes.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Serializing from buffer"
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|vtf
operator|.
name|getByteStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|vtf
operator|.
name|delete
argument_list|()
expr_stmt|;
name|vtf
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NotAuthorizedException
argument_list|(
name|this
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|//@Override
specifier|public
name|Long
name|getMaxAgeSeconds
parameter_list|(
name|Auth
name|auth
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|//@Override
specifier|public
name|String
name|getContentType
parameter_list|(
name|String
name|accepts
parameter_list|)
block|{
return|return
name|existDocument
operator|.
name|getMimeType
argument_list|()
return|;
block|}
comment|//@Override
specifier|public
name|Long
name|getContentLength
parameter_list|()
block|{
comment|// Note
comment|// Whilst for non-XML documents the exact size of the documents can
comment|// be determined by checking the administration, this is not possible
comment|// for XML documents.
comment|//
comment|// For XML documents by default the 'approximate' size is available
comment|// which can be sufficient (pagesize * nr of pages). Exact size
comment|// is dependant on many factors, the serialization parameters.
comment|//
comment|// The approximate size is a good indication of the size of document
comment|// but some WebDAV client, mainly the MacOsX Finder version, can
comment|// not deal with this guesstimate, resulting in incomplete or overcomplete
comment|// documents.
comment|//
comment|// Special for this, two system variables can be set to change the
comment|// way the size is calculated. Supported values are
comment|// NULL, EXACT, APPROXIMATE
comment|//
comment|// PROPFIND: Unfortunately both NULL and APPROXIMATE do not work for
comment|// MacOsX Finder. The default behaviour for the Finder 'user-agent' is
comment|// exact, for the others it is approximate.
comment|// This behaviour is swiched by the system properties.
comment|//
comment|// GET: the NULL value seems to be working well for macosx too.
name|Long
name|size
init|=
literal|null
decl_stmt|;
comment|// MacOsX has a bad reputation
name|boolean
name|isMacFinder
init|=
name|userAgentHelper
operator|.
name|isMacFinder
argument_list|(
name|HttpManager
operator|.
name|request
argument_list|()
operator|.
name|getUserAgentHeader
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existDocument
operator|.
name|isXmlDocument
argument_list|()
condition|)
block|{
comment|// XML document, exact size is not (directly) known)
if|if
condition|(
name|isPropFind
condition|)
block|{
comment|// PROPFIND
comment|// In this scensario the XML document is not actually
comment|// downloaded, only the size needs to be known.
comment|// This is the most expensive scenario
if|if
condition|(
name|isMacFinder
operator|||
name|SIZE_METHOD
operator|.
name|EXACT
operator|==
name|propfindSizeMethod
condition|)
block|{
comment|// Returns the exact size, default behaviour for Finder,
comment|// or when set by a system property
name|LOG
operator|.
name|debug
argument_list|(
literal|"Serializing XML to /dev/null to determine size"
operator|+
literal|" ("
operator|+
name|resourceXmldbUri
operator|+
literal|") MacFinder="
operator|+
name|isMacFinder
argument_list|)
expr_stmt|;
comment|// Stream document to '/dev/null' and count bytes
name|ByteCountOutputStream
name|counter
init|=
operator|new
name|ByteCountOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|existDocument
operator|.
name|stream
argument_list|(
name|counter
argument_list|)
expr_stmt|;
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
name|ex
argument_list|)
expr_stmt|;
block|}
name|size
operator|=
name|counter
operator|.
name|getByteCount
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|SIZE_METHOD
operator|.
name|NULL
operator|==
name|propfindSizeMethod
condition|)
block|{
comment|// Returns size unknown. This is not supported
comment|// by MacOsX finder
name|size
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// Returns the estimated document size. This is the
comment|// default value, but not suitable for MacOsX Finder.
name|size
operator|=
literal|0L
operator|+
name|existDocument
operator|.
name|getContentLength
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// GET
comment|// In this scenario, the document will actually be downloaded
comment|// in the next step.
if|if
condition|(
name|SIZE_METHOD
operator|.
name|EXACT
operator|==
name|getSizeMethod
condition|)
block|{
comment|// Return the exact size by pre-serializing the document
comment|// to a buffer first. isMacFinder is not needed
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Serializing XML to virtual file"
operator|+
literal|" ("
operator|+
name|resourceXmldbUri
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|vtf
operator|=
operator|new
name|VirtualTempFile
argument_list|()
expr_stmt|;
name|existDocument
operator|.
name|stream
argument_list|(
name|vtf
argument_list|)
expr_stmt|;
name|vtf
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|ex
argument_list|)
expr_stmt|;
block|}
name|size
operator|=
name|vtf
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|SIZE_METHOD
operator|.
name|APPROXIMATE
operator|==
name|getSizeMethod
condition|)
block|{
comment|// Return approximate size, be warned to use this
name|size
operator|=
literal|0L
operator|+
name|existDocument
operator|.
name|getContentLength
argument_list|()
expr_stmt|;
name|vtf
operator|=
literal|null
expr_stmt|;
comment|// force live serialization
block|}
else|else
block|{
comment|// Return no size, the whole file will be downloaded
comment|// Works well for macosx finder
name|size
operator|=
literal|null
expr_stmt|;
name|vtf
operator|=
literal|null
expr_stmt|;
comment|// force live serialization
block|}
block|}
block|}
else|else
block|{
comment|// Non XML document, actual size is known
name|size
operator|=
literal|0L
operator|+
name|existDocument
operator|.
name|getContentLength
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Size="
operator|+
name|size
operator|+
literal|" ("
operator|+
name|resourceXmldbUri
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|size
return|;
block|}
comment|/* ====================      * PropFindableResource      * ==================== */
comment|//@Override
specifier|public
name|Date
name|getCreateDate
parameter_list|()
block|{
name|Date
name|createDate
init|=
literal|null
decl_stmt|;
name|Long
name|time
init|=
name|existDocument
operator|.
name|getCreationTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|time
operator|!=
literal|null
condition|)
block|{
name|createDate
operator|=
operator|new
name|Date
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
return|return
name|createDate
return|;
block|}
comment|/* =================      * DeletableResource      * ================= */
comment|//@Override
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|NotAuthorizedException
throws|,
name|ConflictException
throws|,
name|BadRequestException
block|{
name|existDocument
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|/* ================      * LockableResource      * ================ */
comment|//@Override
specifier|public
name|LockResult
name|lock
parameter_list|(
name|LockTimeout
name|timeout
parameter_list|,
name|LockInfo
name|lockInfo
parameter_list|)
throws|throws
name|NotAuthorizedException
throws|,
name|PreConditionFailedException
throws|,
name|LockedException
block|{
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
name|inputToken
init|=
name|convertToken
argument_list|(
name|timeout
argument_list|,
name|lockInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lock: "
operator|+
name|resourceXmldbUri
argument_list|)
expr_stmt|;
name|LockResult
name|lr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
name|existLT
init|=
name|existDocument
operator|.
name|lock
argument_list|(
name|inputToken
argument_list|)
decl_stmt|;
comment|// Process result
name|LockToken
name|mltonLT
init|=
name|convertToken
argument_list|(
name|existLT
argument_list|)
decl_stmt|;
name|lr
operator|=
name|LockResult
operator|.
name|success
argument_list|(
name|mltonLT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NotAuthorizedException
argument_list|(
name|this
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|DocumentAlreadyLockedException
name|ex
parameter_list|)
block|{
comment|// set result iso throw new LockedException(this);
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|lr
operator|=
name|LockResult
operator|.
name|failed
argument_list|(
name|LockResult
operator|.
name|FailureReason
operator|.
name|ALREADY_LOCKED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|lr
operator|=
name|LockResult
operator|.
name|failed
argument_list|(
name|LockResult
operator|.
name|FailureReason
operator|.
name|PRECONDITION_FAILED
argument_list|)
expr_stmt|;
block|}
return|return
name|lr
return|;
block|}
comment|//@Override
specifier|public
name|LockResult
name|refreshLock
parameter_list|(
name|String
name|token
parameter_list|)
throws|throws
name|NotAuthorizedException
throws|,
name|PreConditionFailedException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Refresh: "
operator|+
name|resourceXmldbUri
operator|+
literal|" token="
operator|+
name|token
argument_list|)
expr_stmt|;
name|LockResult
name|lr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
name|existLT
init|=
name|existDocument
operator|.
name|refreshLock
argument_list|(
name|token
argument_list|)
decl_stmt|;
comment|// Process result
name|LockToken
name|mltonLT
init|=
name|convertToken
argument_list|(
name|existLT
argument_list|)
decl_stmt|;
name|lr
operator|=
name|LockResult
operator|.
name|success
argument_list|(
name|mltonLT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NotAuthorizedException
argument_list|(
name|this
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|DocumentNotLockedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|lr
operator|=
name|LockResult
operator|.
name|failed
argument_list|(
name|LockResult
operator|.
name|FailureReason
operator|.
name|PRECONDITION_FAILED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentAlreadyLockedException
name|ex
parameter_list|)
block|{
comment|//throw new LockedException(this);
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|lr
operator|=
name|LockResult
operator|.
name|failed
argument_list|(
name|LockResult
operator|.
name|FailureReason
operator|.
name|ALREADY_LOCKED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|lr
operator|=
name|LockResult
operator|.
name|failed
argument_list|(
name|LockResult
operator|.
name|FailureReason
operator|.
name|PRECONDITION_FAILED
argument_list|)
expr_stmt|;
block|}
return|return
name|lr
return|;
block|}
comment|//@Override
specifier|public
name|void
name|unlock
parameter_list|(
name|String
name|tokenId
parameter_list|)
throws|throws
name|NotAuthorizedException
throws|,
name|PreConditionFailedException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unlock: "
operator|+
name|resourceXmldbUri
argument_list|)
expr_stmt|;
try|try
block|{
name|existDocument
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NotAuthorizedException
argument_list|(
name|this
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|DocumentNotLockedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PreConditionFailedException
argument_list|(
name|this
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PreConditionFailedException
argument_list|(
name|this
argument_list|)
throw|;
block|}
block|}
comment|//@Override
specifier|public
name|LockToken
name|getCurrentLock
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"getLock: "
operator|+
name|resourceXmldbUri
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
name|existLT
init|=
name|existDocument
operator|.
name|getCurrentLock
argument_list|()
decl_stmt|;
if|if
condition|(
name|existLT
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No database lock token."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Construct Lock Info
name|LockToken
name|miltonLT
init|=
name|convertToken
argument_list|(
name|existLT
argument_list|)
decl_stmt|;
comment|// Return values in Milton object
return|return
name|miltonLT
return|;
block|}
comment|/* ================      * MoveableResource      * ================ */
comment|//@Override
specifier|public
name|void
name|moveTo
parameter_list|(
name|CollectionResource
name|rDest
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|ConflictException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"moveTo: "
operator|+
name|resourceXmldbUri
operator|+
literal|" newName="
operator|+
name|newName
argument_list|)
expr_stmt|;
name|XmldbURI
name|destCollection
init|=
operator|(
operator|(
name|MiltonCollection
operator|)
name|rDest
operator|)
operator|.
name|getXmldbUri
argument_list|()
decl_stmt|;
try|try
block|{
name|existDocument
operator|.
name|resourceCopyMove
argument_list|(
name|destCollection
argument_list|,
name|newName
argument_list|,
name|Mode
operator|.
name|MOVE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ConflictException
argument_list|(
name|this
argument_list|)
throw|;
block|}
block|}
comment|/* ================      * CopyableResource      * ================ */
comment|//@Override
specifier|public
name|void
name|copyTo
parameter_list|(
name|CollectionResource
name|rDest
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"copyTo: "
operator|+
name|resourceXmldbUri
operator|+
literal|" newName="
operator|+
name|newName
argument_list|)
expr_stmt|;
name|XmldbURI
name|destCollection
init|=
operator|(
operator|(
name|MiltonCollection
operator|)
name|rDest
operator|)
operator|.
name|getXmldbUri
argument_list|()
decl_stmt|;
try|try
block|{
name|existDocument
operator|.
name|resourceCopyMove
argument_list|(
name|destCollection
argument_list|,
name|newName
argument_list|,
name|Mode
operator|.
name|COPY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
comment|// unable to throw new ConflictException(this);
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
comment|/* ================      * StAX serializer      * ================ */
specifier|public
name|void
name|writeXML
parameter_list|(
name|XMLWriter
name|xw
parameter_list|)
throws|throws
name|TransformerException
block|{
name|xw
operator|.
name|startElement
argument_list|(
literal|"document"
argument_list|)
expr_stmt|;
name|xw
operator|.
name|attribute
argument_list|(
literal|"name"
argument_list|,
name|resourceXmldbUri
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xw
operator|.
name|attribute
argument_list|(
literal|"created"
argument_list|,
name|getXmlDateTime
argument_list|(
name|existDocument
operator|.
name|getCreationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|xw
operator|.
name|attribute
argument_list|(
literal|"last-modified"
argument_list|,
name|getXmlDateTime
argument_list|(
name|existDocument
operator|.
name|getLastModified
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|xw
operator|.
name|attribute
argument_list|(
literal|"owner"
argument_list|,
name|existDocument
operator|.
name|getOwnerUser
argument_list|()
argument_list|)
expr_stmt|;
name|xw
operator|.
name|attribute
argument_list|(
literal|"group"
argument_list|,
name|existDocument
operator|.
name|getOwnerGroup
argument_list|()
argument_list|)
expr_stmt|;
name|xw
operator|.
name|attribute
argument_list|(
literal|"permissions"
argument_list|,
literal|""
operator|+
name|existDocument
operator|.
name|getPermissions
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xw
operator|.
name|attribute
argument_list|(
literal|"size"
argument_list|,
literal|""
operator|+
name|existDocument
operator|.
name|getContentLength
argument_list|()
argument_list|)
expr_stmt|;
name|xw
operator|.
name|endElement
argument_list|(
literal|"document"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

