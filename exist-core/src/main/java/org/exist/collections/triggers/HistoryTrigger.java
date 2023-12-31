begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2004-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|URISyntaxException
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
name|value
operator|.
name|DateTimeValue
import|;
end_import

begin_comment
comment|/**  * This collection trigger will save all old versions of documents before  * they are overwritten or removed. The old versions are kept in the  * 'history root' which is by default '<code>/db/history</code>', but can be   * changed with the parameter '<code>root</code>'.  * You need to configure this trigger for every collection whose history you  * want to preserve, by modifying '<code>collection.xconf</code>' such that it  * resembles this:  *  *<pre>  *&lt;?xml version='1.0'?&gt;  *&lt;collection xmlns='http://exist-db.org/collection-config/1.0'&gt;  *&lt;triggers&gt;  *&lt;trigger   *         event='update'  *         class='org.exist.collections.triggers.HistoryTrigger'  *       /&gt;  *&lt;trigger  *         event='remove'  *         class='org.exist.collections.triggers.HistoryTrigger'  *       /&gt;  *&lt;/triggers&gt;  *&lt;/collection&gt;  *</pre>  *  * @author Mark Spanbroek  * @see org.exist.collections.triggers.Trigger  */
end_comment

begin_class
specifier|public
class|class
name|HistoryTrigger
extends|extends
name|FilteringTrigger
implements|implements
name|DocumentTrigger
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_ROOT_NAME
init|=
literal|"root"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|DEFAULT_ROOT_PATH
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"history"
argument_list|)
decl_stmt|;
specifier|private
name|XmldbURI
name|rootPath
init|=
name|DEFAULT_ROOT_PATH
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|Collection
name|parent
parameter_list|,
specifier|final
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
name|transaction
argument_list|,
name|parent
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
if|if
condition|(
name|parameters
operator|.
name|containsKey
argument_list|(
name|PARAM_ROOT_NAME
argument_list|)
condition|)
block|{
try|try
block|{
name|rootPath
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|parameters
operator|.
name|get
argument_list|(
name|PARAM_ROOT_NAME
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
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
throw|throw
operator|new
name|TriggerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|makeCopy
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|TriggerException
block|{
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// construct the destination path
specifier|final
name|XmldbURI
name|path
init|=
name|rootPath
operator|.
name|append
argument_list|(
name|doc
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
comment|//construct the destination document name
name|String
name|dtValue
init|=
operator|new
name|DateTimeValue
argument_list|(
operator|new
name|Date
argument_list|(
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|dtValue
operator|=
name|dtValue
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"-"
argument_list|)
expr_stmt|;
comment|// multiple ':' are not allowed in URI so use '-'
name|dtValue
operator|=
name|dtValue
operator|.
name|replaceAll
argument_list|(
literal|"\\."
argument_list|,
literal|"-"
argument_list|)
expr_stmt|;
comment|// as we are using '-' instead of ':' do the same for '.'
specifier|final
name|XmldbURI
name|name
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|dtValue
argument_list|)
decl_stmt|;
comment|// create the destination document
comment|//TODO : how is the transaction handled ? It holds the locks !
specifier|final
name|Collection
name|destination
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|broker
operator|.
name|copyResource
argument_list|(
name|txn
argument_list|,
name|doc
argument_list|,
name|destination
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
decl||
name|IOException
decl||
name|PermissionDeniedException
decl||
name|LockException
decl||
name|EXistException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|xpe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCreateDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCreateDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
name|makeCopy
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCopyDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCopyDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeDeleteDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
name|makeCopy
argument_list|(
name|broker
argument_list|,
name|txn
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
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
name|makeCopy
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterMoveDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterDeleteDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocumentMetadata
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocumentMetadata
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//Nothing to do
block|}
block|}
end_class

end_unit

