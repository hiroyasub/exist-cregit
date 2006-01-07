begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|util
operator|.
name|Date
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
name|User
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
name|xquery
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|DocumentType
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
name|ext
operator|.
name|LexicalHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ErrorCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * Abstract base implementation of interface EXistResource.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractEXistResource
implements|implements
name|EXistResource
block|{
specifier|protected
name|User
name|user
decl_stmt|;
specifier|protected
name|BrokerPool
name|pool
decl_stmt|;
specifier|protected
name|LocalCollection
name|parent
decl_stmt|;
specifier|protected
name|String
name|docId
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|mimeType
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|isNewResource
init|=
literal|false
decl_stmt|;
specifier|public
name|AbstractEXistResource
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|parent
parameter_list|,
name|String
name|docId
parameter_list|,
name|String
name|mimeType
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
comment|//TODO : use dedicated function in XmldbURI
if|if
condition|(
name|docId
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
name|docId
operator|=
name|docId
operator|.
name|substring
argument_list|(
name|docId
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|this
operator|.
name|mimeType
operator|=
name|mimeType
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getCreationTime() 	 */
specifier|public
specifier|abstract
name|Date
name|getCreationTime
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getLastModificationTime() 	 */
specifier|public
specifier|abstract
name|Date
name|getLastModificationTime
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#getPermissions() 	 */
specifier|public
specifier|abstract
name|Permission
name|getPermissions
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.EXistResource#setLexicalHandler(org.xml.sax.ext.LexicalHandler) 	 */
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
name|LexicalHandler
name|handler
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setMimeType
parameter_list|(
name|String
name|mime
parameter_list|)
block|{
name|this
operator|.
name|mimeType
operator|=
name|mime
expr_stmt|;
block|}
specifier|public
name|String
name|getMimeType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|mimeType
return|;
block|}
specifier|protected
name|DocumentImpl
name|openDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|int
name|lockMode
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|parentCollection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parentCollection
operator|=
name|parent
operator|.
name|getCollectionWithLock
argument_list|(
name|lockMode
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentCollection
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_COLLECTION
argument_list|,
literal|"Collection "
operator|+
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|" not found"
argument_list|)
throw|;
try|try
block|{
name|document
operator|=
name|parentCollection
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|docId
argument_list|,
name|lockMode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Failed to acquire lock on document "
operator|+
name|docId
argument_list|)
throw|;
block|}
if|if
condition|(
name|document
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|)
throw|;
block|}
comment|//	    System.out.println("Opened document " + document.getName() + " mode = " + lockMode);
return|return
name|document
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|parentCollection
operator|!=
literal|null
condition|)
name|parentCollection
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|closeDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|int
name|lockMode
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
return|return;
comment|//		System.out.println("Closed " + doc.getName() + " mode = " + lockMode);
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|lockMode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DocumentType
name|getDocType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setDocType
parameter_list|(
name|DocumentType
name|doctype
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
block|}
end_class

end_unit

