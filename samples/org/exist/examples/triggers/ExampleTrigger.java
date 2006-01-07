begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Sep 12, 2003  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|examples
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
name|StringReader
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

begin_comment
comment|/**  * This trigger maintains a file "contents.xml", containing a list of all documents added to a collection.  * It uses XUpdate to update "contents.xml" whenever a document is added or removed.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ExampleTrigger
extends|extends
name|FilteringTrigger
block|{
specifier|private
name|DocumentImpl
name|doc
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.exist.collections.Trigger#prepare(java.lang.String, org.w3c.dom.Document) 	 */
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
name|String
name|documentName
parameter_list|,
name|DocumentImpl
name|existingDocument
parameter_list|)
throws|throws
name|TriggerException
block|{
name|String
name|xupdate
decl_stmt|;
comment|// we react to the store and remove events
if|if
condition|(
name|event
operator|==
name|STORE_DOCUMENT_EVENT
condition|)
comment|// create XUpdate command for inserts
name|xupdate
operator|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:append select='/contents'><xu:element name='file'>"
operator|+
name|documentName
operator|+
literal|"</xu:element></xu:append></xu:modifications>"
expr_stmt|;
if|else if
condition|(
name|event
operator|==
name|REMOVE_DOCUMENT_EVENT
condition|)
comment|// create XUpdate command for removals
name|xupdate
operator|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:remove select=\"//file[text()='"
operator|+
name|documentName
operator|+
literal|"']\"></xu:remove>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
else|else
return|return;
name|getLogger
argument_list|()
operator|.
name|debug
argument_list|(
name|xupdate
argument_list|)
expr_stmt|;
comment|// create a document set containing "contents.xml"
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
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
comment|/* (non-Javadoc) 	 * @see org.exist.collections.Trigger#configure(org.exist.storage.DBBroker, org.exist.collections.Collection, java.util.Map) 	 */
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
comment|// the name of the contents file can be set through parameters
name|String
name|contentsFile
init|=
operator|(
name|String
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentsFile
operator|==
literal|null
condition|)
name|contentsFile
operator|=
literal|"contents.xml"
expr_stmt|;
comment|// try to retrieve the contents file
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
name|contentsFile
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
comment|// doesn't exist yet: create it
try|try
block|{
name|getLogger
argument_list|()
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
literal|null
argument_list|,
name|broker
argument_list|,
name|contentsFile
argument_list|,
literal|"<?xml version=\"1.0\"?><contents></contents>"
argument_list|)
decl_stmt|;
name|parent
operator|.
name|store
argument_list|(
literal|null
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
literal|"<?xml version=\"1.0\"?><contents></contents>"
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
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
end_class

end_unit

