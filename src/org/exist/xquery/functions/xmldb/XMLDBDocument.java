begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|collections
operator|.
name|ManagedLocks
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
name|*
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
name|Permission
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
name|UpdateListener
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
name|lock
operator|.
name|ManagedDocumentLock
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
name|LockException
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
name|*
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
name|FunctionReturnSequenceType
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Implements eXist's xmldb:document() function.  *  * @author wolf  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBDocument
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|XMLDBDocument
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"document"
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
literal|"Returns the documents indicated by $document-uris in the input sequence. "
operator|+
name|XMLDBModule
operator|.
name|COLLECTION_URI
operator|+
literal|"If the input sequence is empty, "
operator|+
literal|"the function will load all documents in the database (WARNING this is a very expensive operation!)."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"document-uris"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"The document URIs"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the documents"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|"See the standard fn:doc() function"
argument_list|)
decl_stmt|;
specifier|private
name|UpdateListener
name|listener
init|=
literal|null
decl_stmt|;
specifier|public
name|XMLDBDocument
parameter_list|(
specifier|final
name|XQueryContext
name|context
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
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|MutableDocumentSet
name|mdocs
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|mdocs
operator|=
name|allDocs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|mdocs
operator|=
name|docs
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|lockOnLoad
init|=
name|context
operator|.
name|lockDocumentsOnLoad
argument_list|()
decl_stmt|;
name|ManagedLocks
argument_list|<
name|ManagedDocumentLock
argument_list|>
name|docLocks
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// wait for pending updates
name|docLocks
operator|=
name|mdocs
operator|.
name|lock
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|lockOnLoad
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|results
init|=
operator|new
name|ExtArrayNodeSet
argument_list|(
name|mdocs
operator|.
name|getDocumentCount
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|mdocs
operator|.
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|//, -1, Node.DOCUMENT_NODE));
if|if
condition|(
name|lockOnLoad
condition|)
block|{
name|context
operator|.
name|addLockedDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|results
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Could not acquire lock on document set"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Could not acquire lock on document set."
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|lockOnLoad
condition|)
block|{
comment|// release all locks
if|if
condition|(
name|docLocks
operator|!=
literal|null
condition|)
block|{
name|docLocks
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|MutableDocumentSet
name|allDocs
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|MutableDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
try|try
block|{
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getAllXMLResources
argument_list|(
name|docs
argument_list|)
expr_stmt|;
return|return
name|docs
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
decl||
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
block|}
specifier|private
name|MutableDocumentSet
name|docs
parameter_list|(
specifier|final
name|Sequence
name|args
index|[]
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|MutableDocumentSet
name|mdocs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Sequence
name|arg
range|:
name|args
control|)
block|{
specifier|final
name|XmldbURI
name|docUri
init|=
name|toURI
argument_list|(
name|arg
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|LockedDocument
name|lockedDocument
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|docUri
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|lockedDocument
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|isRaiseErrorOnFailedRetrieval
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FODC0002
argument_list|,
literal|"can not access '"
operator|+
name|docUri
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|lockedDocument
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|doc
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getCurrentSubject
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied: unable to load document "
operator|+
name|docUri
argument_list|)
throw|;
block|}
name|mdocs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Permission denied"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied: unable to load document "
operator|+
name|docUri
argument_list|)
throw|;
block|}
block|}
return|return
name|mdocs
return|;
block|}
specifier|private
name|XmldbURI
name|toURI
parameter_list|(
specifier|final
name|String
name|strUri
parameter_list|)
throws|throws
name|XPathException
block|{
name|XmldbURI
name|uri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|strUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getCollectionPath
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Invalid argument to "
operator|+
name|XMLDBModule
operator|.
name|PREFIX
operator|+
literal|":document() function: empty string is not allowed here."
argument_list|)
throw|;
block|}
if|if
condition|(
name|uri
operator|.
name|numSegments
argument_list|()
operator|==
literal|1
condition|)
block|{
name|uri
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
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
name|uri
return|;
block|}
block|}
end_class

end_unit

