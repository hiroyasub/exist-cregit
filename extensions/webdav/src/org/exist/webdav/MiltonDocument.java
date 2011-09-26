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
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamWriter
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
name|Subject
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
specifier|static
specifier|final
name|String
name|METHOD_EXACT
init|=
literal|"exact"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_GUESS
init|=
literal|"approximate"
decl_stmt|;
specifier|private
specifier|static
name|String
name|propfindMethod
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
comment|/**      *  Constructor of representation of a Document in the Milton framework, without subject information.      * To be called by the resource factory.      *      * @param host  FQ host name including port number.      * @param uri   Path on server indicating path of resource      * @param brokerPool Handle to Exist database.      */
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
comment|/**      *  Constructor of representation of a Document in the Milton framework, with subject information.      * To be called by the resource factory.      *      * @param host  FQ host name including port number.      * @param uri   Path on server indicating path of resource.      * @param subject  An Exist operation is performed with  User. Can be NULL.      * @param pool Handle to Exist database.      */
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
name|Subject
name|subject
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
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
name|subject
operator|!=
literal|null
condition|)
block|{
name|existDocument
operator|.
name|setUser
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|existDocument
operator|.
name|initMetadata
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|propfindMethod
operator|==
literal|null
condition|)
block|{
name|propfindMethod
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.exist.webdav.GUESTIMATE_XML_SIZE"
argument_list|,
name|METHOD_EXACT
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* ================      * GettableResource      * ================ */
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|Long
name|getContentLength
parameter_list|()
block|{
name|Long
name|size
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|existDocument
operator|.
name|isXmlDocument
argument_list|()
condition|)
block|{
if|if
condition|(
name|isPropFind
condition|)
block|{
comment|// PROPFIND
if|if
condition|(
name|METHOD_EXACT
operator|.
name|equals
argument_list|(
name|propfindMethod
argument_list|)
condition|)
block|{
comment|// For PROPFIND the actual size must be calculated
comment|// by serializing the document.
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
literal|")"
argument_list|)
expr_stmt|;
comment|// Stream document to /dev/null and count bytes
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
name|IOException
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
catch|catch
parameter_list|(
name|PermissionDeniedException
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
else|else
block|{
comment|// Use estimated document size
name|size
operator|=
name|existDocument
operator|.
name|getContentLength
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Serialize to virtual file for re-use by sendContent()
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
name|IOException
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
catch|catch
parameter_list|(
name|PermissionDeniedException
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
block|}
else|else
block|{
comment|// Non XML document
comment|// Actual size is known
name|size
operator|=
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
name|XMLStreamWriter
name|writer
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|writer
operator|.
name|writeStartElement
argument_list|(
literal|"exist"
argument_list|,
literal|"document"
argument_list|,
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
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
name|writer
operator|.
name|writeAttribute
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
name|writer
operator|.
name|writeAttribute
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
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"owner"
argument_list|,
name|existDocument
operator|.
name|getOwnerUser
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
argument_list|(
literal|"group"
argument_list|,
name|existDocument
operator|.
name|getOwnerGroup
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeAttribute
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
name|writer
operator|.
name|writeAttribute
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
name|writer
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

