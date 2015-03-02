begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
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
name|URISyntaxException
import|;
end_import

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
name|QName
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
name|txn
operator|.
name|TransactionManager
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
name|txn
operator|.
name|Txn
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
name|MimeTable
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
name|MimeType
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
name|exist
operator|.
name|xquery
operator|.
name|BasicFunction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Cardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionSignature
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|AnyURIValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|FunctionParameterSequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * @author Dannes Wessels (dannes@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBSetMimeType
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XMLDBSetMimeType
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"set-mime-type"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Set the MIME type of the resource $resource-uri."
operator|+
name|XMLDBModule
operator|.
name|ANY_URI
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"resource-uri"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The resource URI"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"mime-type"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The new mime-type, use empty sequence to set default value."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|XMLDBSetMimeType
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|args
index|[]
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// Get handle to Mime-type info
specifier|final
name|MimeTable
name|mimeTable
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|// Get first parameter
specifier|final
name|String
name|pathParameter
init|=
operator|new
name|AnyURIValue
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathParameter
operator|.
name|matches
argument_list|(
literal|"^[a-z]+://.*"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Can not set mime-type for resources outside the database."
argument_list|)
throw|;
block|}
name|XmldbURI
name|pathUri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pathUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|pathParameter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|logger
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
name|XPathException
argument_list|(
literal|"Invalid path '"
operator|+
name|pathParameter
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|// Verify mime-type input
name|MimeType
name|newMimeType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// No input, use default mimetype
name|newMimeType
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
name|pathParameter
argument_list|)
expr_stmt|;
if|if
condition|(
name|newMimeType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unable to determine mimetype for '"
operator|+
name|pathParameter
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// Mimetype is provided, check if valid
name|newMimeType
operator|=
name|mimeTable
operator|.
name|getContentType
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|newMimeType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"mime-type '"
operator|+
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"' is not supported."
argument_list|)
throw|;
block|}
block|}
comment|// Get mime-type of resource
name|MimeType
name|currentMimeType
init|=
name|getMimeTypeStoredResource
argument_list|(
name|pathUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentMimeType
operator|==
literal|null
condition|)
block|{
comment|// stored resource has no mime-type (unexpected situation)
comment|// fall back to document name
name|logger
operator|.
name|debug
argument_list|(
literal|"Resource '"
operator|+
name|pathUri
operator|+
literal|"' has no mime-type, retrieve from document name."
argument_list|)
expr_stmt|;
name|currentMimeType
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
name|pathUri
argument_list|)
expr_stmt|;
comment|// if extension based lookup still fails
if|if
condition|(
name|currentMimeType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unable to determine mime-type from path '"
operator|+
name|pathUri
operator|+
literal|"'."
argument_list|)
throw|;
block|}
block|}
comment|// Check if mimeType are equivalent
comment|// in some cases value null is set, then allow to set to new value (repair action)
if|if
condition|(
name|newMimeType
operator|.
name|isXMLType
argument_list|()
operator|!=
name|currentMimeType
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"New mime-type must be a "
operator|+
name|currentMimeType
operator|.
name|getXMLDBType
argument_list|()
operator|+
literal|" mime-type"
argument_list|)
throw|;
block|}
comment|// At this moment it is possible to update the mimetype
specifier|final
name|DBBroker
name|broker
init|=
name|context
operator|.
name|getBroker
argument_list|()
decl_stmt|;
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
comment|// relative collection Path: add the current base URI
name|pathUri
operator|=
name|context
operator|.
name|getBaseURI
argument_list|()
operator|.
name|toXmldbURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|pathUri
argument_list|)
expr_stmt|;
comment|// try to open the document and acquire a lock
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|broker
operator|.
name|getXMLResource
argument_list|(
name|pathUri
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
comment|// no document selected, abort
name|txn
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// set new mime-type
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|newMimeType
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// store meta data into database
name|broker
operator|.
name|storeMetadata
argument_list|(
name|txn
argument_list|,
name|doc
argument_list|)
expr_stmt|;
comment|// commit changes
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|//release all locks
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|/**      * Determine mimetype of currently stored resource. Copied from      * get-mime-type.      */
specifier|private
name|MimeType
name|getMimeTypeStoredResource
parameter_list|(
name|XmldbURI
name|pathUri
parameter_list|)
throws|throws
name|XPathException
block|{
name|MimeType
name|returnValue
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// relative collection Path: add the current base URI
name|pathUri
operator|=
name|context
operator|.
name|getBaseURI
argument_list|()
operator|.
name|toXmldbURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|pathUri
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Unable to convert path "
operator|+
name|pathUri
argument_list|)
expr_stmt|;
return|return
name|returnValue
return|;
block|}
try|try
block|{
comment|// try to open the document and acquire a lock
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|pathUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Resource '"
operator|+
name|pathUri
operator|+
literal|"' does not exist."
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|String
name|mimetype
init|=
operator|(
operator|(
name|DocumentImpl
operator|)
name|doc
operator|)
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
decl_stmt|;
name|returnValue
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentType
argument_list|(
name|mimetype
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
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
block|}
return|return
name|returnValue
return|;
block|}
block|}
end_class

end_unit

