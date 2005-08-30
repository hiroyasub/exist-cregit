begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|CollectionConfigurationException
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
name|util
operator|.
name|LockException
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
name|Document
import|;
end_import

begin_comment
comment|/**  * This collection trigger will save all old versions of documents before  * they are overwritten or removed. The old versions are kept in the  * 'history root' which is by default '<code>/db/history</code>', but can be   * changed with the parameter '<code>root</code>'.  * You need to configure this trigger for every collection whose history you  * want to preserve, by modifying '<code>collection.xconf</code>' such that it  * resembles this:  *  *<pre>  *&lt;?xml version='1.0'?>  *&lt;collection xmlns='http://exist-db.org/collection-config/1.0'>  *&lt;triggers>  *&lt;trigger   *         event='update'  *         class='org.exist.collections.triggers.HistoryTrigger'  *       />  *&lt;trigger  *         event='remove'  *         class='org.exist.collections.triggers.HistoryTrigger'  *       />  *&lt;/triggers>  *&lt;/collection>  *</pre>  *  * @author Mark Spanbroek  * @see org.exist.collections.triggers.Trigger  */
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
specifier|protected
name|String
name|root
init|=
literal|"/db/history"
decl_stmt|;
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
name|parameters
parameter_list|)
throws|throws
name|CollectionConfigurationException
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
if|if
condition|(
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"root"
argument_list|)
condition|)
block|{
name|root
operator|=
name|parameters
operator|.
name|get
argument_list|(
literal|"root"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
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
name|String
name|documentName
parameter_list|,
name|Document
name|existingDocument
parameter_list|)
throws|throws
name|TriggerException
block|{
if|if
condition|(
name|existingDocument
operator|==
literal|null
condition|)
return|return;
comment|// retrieve the document in question
name|DocumentImpl
name|doc
init|=
operator|(
name|DocumentImpl
operator|)
name|existingDocument
decl_stmt|;
comment|// construct the destination path
name|String
name|path
init|=
name|root
operator|+
name|doc
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// construct the destination document name
name|DateFormat
name|formatter
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd-HH:mm:ss:SSS"
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|formatter
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|doc
operator|.
name|getLastModified
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// create the destination document
try|try
block|{
name|Collection
name|destination
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
literal|null
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
literal|null
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|broker
operator|.
name|copyResource
argument_list|(
literal|null
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
name|PermissionDeniedException
name|exception
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockException
name|exception
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.collections.triggers.DocumentTrigger#finish(int, org.exist.storage.DBBroker, java.lang.String, org.w3c.dom.Document)      */
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
name|String
name|documentName
parameter_list|,
name|Document
name|document
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

