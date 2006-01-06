begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  *     * Copyright (C) 2001-04 Wolfgang M. Meier wolfgang@exist-db.org  *   * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *   * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
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
name|CollectionConfigurationException
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
name|CollectionConfigurationManager
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
name|DocumentSet
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
name|NativeBroker
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
name|sync
operator|.
name|Sync
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
name|Occurrences
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
name|XQuery
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
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

begin_class
specifier|public
class|class
name|LocalIndexQueryService
implements|implements
name|IndexQueryService
block|{
specifier|private
name|LocalCollection
name|parent
init|=
literal|null
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|private
name|User
name|user
decl_stmt|;
specifier|public
name|LocalIndexQueryService
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|parent
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
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.IndexQueryService#reindexCollection()      */
specifier|public
name|void
name|reindexCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|broker
operator|.
name|reindexCollection
argument_list|(
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|(
name|Sync
operator|.
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.IndexQueryService#reindexCollection(java.lang.String)      */
specifier|public
name|void
name|reindexCollection
parameter_list|(
name|String
name|collectionPath
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|path
init|=
name|XmldbURI
operator|.
name|checkPath
argument_list|(
name|collectionPath
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|broker
operator|.
name|reindexCollection
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|(
name|Sync
operator|.
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.IndexQueryService#configure(java.lang.String) 	 */
specifier|public
name|void
name|configureCollection
parameter_list|(
name|String
name|configData
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|CollectionConfigurationManager
name|mgr
init|=
name|pool
operator|.
name|getConfigurationManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addConfiguration
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|parent
operator|.
name|getCollection
argument_list|()
argument_list|,
name|configData
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Configured '"
operator|+
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CollectionConfigurationException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.IndexQueryService#getIndexedElements(boolean) 	 */
specifier|public
name|Occurrences
index|[]
name|getIndexedElements
parameter_list|(
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
return|return
name|broker
operator|.
name|getElementIndex
argument_list|()
operator|.
name|scanIndexedElements
argument_list|(
name|parent
operator|.
name|getCollection
argument_list|()
argument_list|,
name|inclusive
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"database access error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
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
literal|"permission denied"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Service#getName() 	 */
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"IndexQueryService"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Service#getVersion() 	 */
specifier|public
name|String
name|getVersion
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"1.0"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Service#setCollection(org.xmldb.api.base.Collection) 	 */
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|col
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
operator|(
name|col
operator|instanceof
name|LocalCollection
operator|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_COLLECTION
argument_list|,
literal|"incompatible collection type: "
operator|+
name|col
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
name|parent
operator|=
operator|(
name|LocalCollection
operator|)
name|col
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Configurable#getProperty(java.lang.String) 	 */
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Configurable#setProperty(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.IndexQueryService#scanIndexTerms(java.lang.String, java.lang.String, boolean) 	 */
specifier|public
name|Occurrences
index|[]
name|scanIndexTerms
parameter_list|(
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|parent
operator|.
name|getCollection
argument_list|()
operator|.
name|allDocs
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|inclusive
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|broker
operator|.
name|getTextEngine
argument_list|()
operator|.
name|scanIndexTerms
argument_list|(
name|docs
argument_list|,
name|docs
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
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
literal|"permission denied"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"database access error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Occurrences
index|[]
name|scanIndexTerms
parameter_list|(
name|String
name|xpath
parameter_list|,
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|Sequence
name|nodes
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|xpath
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|broker
operator|.
name|getTextEngine
argument_list|()
operator|.
name|scanIndexTerms
argument_list|(
name|nodes
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|nodes
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"database access error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

