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
name|storage
operator|.
name|md
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|collections
operator|.
name|triggers
operator|.
name|FilteringTrigger
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
name|triggers
operator|.
name|TriggerException
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DocumentEvents
extends|extends
name|FilteringTrigger
block|{
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|existingDocument
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//old, do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
comment|//old, do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCreateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCreateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//		System.out.println("afterCreateDocument "+document.getURI());
try|try
block|{
name|MDStorageManager
operator|.
name|inst
operator|.
name|md
operator|.
name|addMetas
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|MDStorageManager
operator|.
name|LOG
operator|.
name|fatal
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//		System.out.println("afterUpdateDocument "+document.getURI());
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCopyDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCopyDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|oldUri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//		System.out.println("afterCopyDocument "+document.getURI());
name|MDStorageManager
operator|.
name|inst
operator|.
name|md
operator|.
name|copyMetas
argument_list|(
name|oldUri
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeMoveDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterMoveDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|oldUri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//		System.out.println("afterMoveDocument "+oldUri+" to "+document.getURI());
try|try
block|{
name|MDStorageManager
operator|.
name|inst
operator|.
name|md
operator|.
name|moveMetas
argument_list|(
name|oldUri
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|MDStorageManager
operator|.
name|LOG
operator|.
name|fatal
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeDeleteDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterDeleteDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//		System.out.println("afterDeleteDocument "+uri);
try|try
block|{
name|MDStorageManager
operator|.
name|inst
operator|.
name|md
operator|.
name|delMetas
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|MDStorageManager
operator|.
name|LOG
operator|.
name|fatal
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocumentMetadata
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocumentMetadata
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|parent
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
block|}
end_class

end_unit

