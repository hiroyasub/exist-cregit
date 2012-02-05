begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|EXistException
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XUpdateQueryService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|StringReader
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

begin_comment
comment|/**  * LocalXUpdateQueryService.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|LocalXUpdateQueryService
implements|implements
name|XUpdateQueryService
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LocalXUpdateQueryService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|Subject
name|user
decl_stmt|;
specifier|private
name|LocalCollection
name|parent
decl_stmt|;
specifier|private
name|XUpdateProcessor
name|processor
init|=
literal|null
decl_stmt|;
comment|/** 	 * Constructor for LocalXUpdateQueryService. 	 */
specifier|public
name|LocalXUpdateQueryService
parameter_list|(
name|Subject
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
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/** 	 * @see org.xmldb.api.modules.XUpdateQueryService#updateResource(java.lang.String, java.lang.String) 	 */
specifier|public
name|long
name|updateResource
parameter_list|(
name|String
name|resource
parameter_list|,
name|String
name|xupdate
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|MutableDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
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
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
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
name|c
init|=
name|parent
operator|.
name|getCollection
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
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|docs
operator|=
name|c
operator|.
name|allDocs
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XmldbURI
name|resourceURI
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
init|=
name|c
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|resourceURI
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"Resource not found: "
operator|+
name|resource
argument_list|)
throw|;
block|}
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|processor
operator|==
literal|null
condition|)
name|processor
operator|=
operator|new
name|XUpdateProcessor
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|parent
operator|.
name|getAccessContext
argument_list|()
argument_list|)
expr_stmt|;
else|else
block|{
name|processor
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|processor
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
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
name|long
name|mods
init|=
literal|0
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
block|{
name|mods
operator|+=
name|modifications
index|[
name|i
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
comment|//Cluster event send (removed)
name|LOG
operator|.
name|debug
argument_list|(
literal|"xupdate took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"ms."
argument_list|)
expr_stmt|;
return|return
name|mods
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
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
name|IOException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
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
name|SAXException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
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
name|URISyntaxException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
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
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
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
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
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
name|Exception
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
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
finally|finally
block|{
if|if
condition|(
name|processor
operator|!=
literal|null
condition|)
name|processor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * * @see org.xmldb.api.modules.XUpdateQueryService#update(java.lang.String) 	 */
specifier|public
name|long
name|update
parameter_list|(
name|String
name|arg1
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|updateResource
argument_list|(
literal|null
argument_list|,
name|arg1
argument_list|)
return|;
block|}
comment|/** 	 * @see org.xmldb.api.base.Service#getName() 	 */
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"XUpdateQueryService"
return|;
block|}
comment|/** 	 * @see org.xmldb.api.base.Service#getVersion() 	 */
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
comment|/** 	 * @see org.xmldb.api.base.Service#setCollection(org.xmldb.api.base.Collection) 	 */
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|arg0
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
comment|/** 	 * @see org.xmldb.api.base.Configurable#getProperty(java.lang.String) 	 */
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 * @see org.xmldb.api.base.Configurable#setProperty(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
block|}
end_class

end_unit

