begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

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
name|IndexInfo
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
name|DefaultDocumentSet
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
name|dom
operator|.
name|MutableDocumentSet
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
name|xacml
operator|.
name|AccessContext
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
name|xupdate
operator|.
name|Modification
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xupdate
operator|.
name|XUpdateProcessor
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
name|InputSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Test trigger to check if trigger configuration is working properly.  */
end_comment

begin_class
specifier|public
class|class
name|TestTrigger
extends|extends
name|FilteringTrigger
implements|implements
name|DocumentTrigger
block|{
specifier|protected
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEMPLATE
init|=
literal|"<?xml version=\"1.0\"?><events></events>"
decl_stmt|;
specifier|private
name|DocumentImpl
name|doc
decl_stmt|;
specifier|public
name|void
name|configure
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
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
argument_list|>
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|TriggerException
block|{
name|super
operator|.
name|configure
argument_list|(
name|broker
argument_list|,
name|parent
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
name|XmldbURI
name|docPath
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"messages.xml"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TestTrigger prepares"
argument_list|)
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|parent
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|docPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|doc
operator|==
literal|null
condition|)
block|{
name|TransactionManager
name|transactMgr
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transactMgr
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"creating new file for collection contents"
argument_list|)
expr_stmt|;
comment|// IMPORTANT: temporarily disable triggers on the collection.
comment|// We would end up in infinite recursion if we don't do that
name|parent
operator|.
name|setTriggersEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|parent
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|docPath
argument_list|,
name|TEMPLATE
argument_list|)
decl_stmt|;
comment|//TODO : unlock the collection here ?
name|parent
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|TEMPLATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|info
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|transactMgr
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|transactMgr
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TriggerException
argument_list|(
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
name|parent
operator|.
name|setTriggersEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Deprecated
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
name|transaction
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
block|}
annotation|@
name|Deprecated
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
name|transaction
parameter_list|,
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
block|}
specifier|private
name|void
name|addRecord
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|xupdate
parameter_list|)
throws|throws
name|TriggerException
block|{
name|MutableDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
try|try
block|{
comment|// IMPORTANT: temporarily disable triggers on the collection.
comment|// We would end up in infinite recursion if we don't do that
name|getCollection
argument_list|()
operator|.
name|setTriggersEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// create the XUpdate processor
name|XUpdateProcessor
name|processor
init|=
operator|new
name|XUpdateProcessor
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|AccessContext
operator|.
name|TRIGGER
argument_list|)
decl_stmt|;
comment|// process the XUpdate
name|Modification
name|modifications
index|[]
init|=
name|processor
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|modifications
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|modifications
index|[
name|i
index|]
operator|.
name|process
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TriggerException
argument_list|(
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
comment|// IMPORTANT: reenable trigger processing for the collection.
name|getCollection
argument_list|()
operator|.
name|setTriggersEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
name|transaction
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
name|String
name|xupdate
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\""
operator|+
name|XUpdateProcessor
operator|.
name|XUPDATE_NS
operator|+
literal|"\">"
operator|+
literal|"<xu:append select='/events'>"
operator|+
literal|"<xu:element name='event'>"
operator|+
literal|"<xu:attribute name='id'>STORE-DOCUMENT</xu:attribute>"
operator|+
literal|"<xu:attribute name='collection'>"
operator|+
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
operator|+
literal|"</xu:attribute>"
operator|+
literal|"</xu:element>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
name|addRecord
argument_list|(
name|broker
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
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
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
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
name|transaction
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
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
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
name|transaction
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
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
block|{
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
name|transaction
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
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
block|{
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
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
name|String
name|xupdate
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\""
operator|+
name|XUpdateProcessor
operator|.
name|XUPDATE_NS
operator|+
literal|"\">"
operator|+
literal|"<xu:append select='/events'>"
operator|+
literal|"<xu:element name='event'>"
operator|+
literal|"<xu:attribute name='id'>REMOVE-DOCUMENT</xu:attribute>"
operator|+
literal|"<xu:attribute name='collection'>"
operator|+
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
operator|+
literal|"</xu:attribute>"
operator|+
literal|"</xu:element>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
name|addRecord
argument_list|(
name|broker
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
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
name|transaction
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
block|}
end_class

end_unit

