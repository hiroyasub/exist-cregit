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
name|xindice
operator|.
name|client
operator|.
name|xmldb
operator|.
name|services
operator|.
name|CollectionManager
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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|Category
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|LocalCollectionManagementService
extends|extends
name|CollectionManager
block|{
specifier|protected
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|protected
name|LocalCollection
name|parent
init|=
literal|null
decl_stmt|;
specifier|protected
name|User
name|user
decl_stmt|;
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|LocalCollection
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      *  Constructor for the LocalCollectionManagementService object      *      *@param  pool    Description of the Parameter      *@param  parent  Description of the Parameter      *@param  user    Description of the Parameter      */
specifier|public
name|LocalCollectionManagementService
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
if|if
condition|(
name|user
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  collName            Description of the Parameter      *@return                     Description of the Return Value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|String
name|collName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|collName
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|collName
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|()
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|coll
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|user
argument_list|,
name|collName
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|//broker.sync();
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
literal|"failed to create collection "
operator|+
name|collName
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
literal|"not allowed to create collection"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LocalCollection
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|parent
argument_list|,
name|collName
argument_list|)
return|;
block|}
comment|/**      *  Creates a new collection in the database identified by name and using      *  the provided configuration.      *      *@param  path                the path of the new collection      *@param  configuration       the XML collection configuration to use for      *      creating this collection.      *@return                     The newly created collection      *@exception  XMLDBException      */
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|String
name|path
parameter_list|,
name|Document
name|configuration
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|createCollection
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**      *  Gets the name attribute of the LocalCollectionManagementService object      *      *@return                     The name value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"CollectionManagementService"
return|;
block|}
comment|/**      *  Gets the property attribute of the LocalCollectionManagementService      *  object      *      *@param  property  Description of the Parameter      *@return           The property value      */
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**      *  Gets the version attribute of the LocalCollectionManagementService      *  object      *      *@return                     The version value      *@exception  XMLDBException  Description of the Exception      */
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
comment|/**      *  Description of the Method      *      *@param  collName            Description of the Parameter      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|removeCollection
parameter_list|(
name|String
name|collName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|path
init|=
operator|(
name|collName
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|?
name|collName
else|:
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|collName
operator|)
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
name|brokerPool
operator|.
name|get
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing collection "
operator|+
name|path
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|user
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
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
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"failed to remove collection "
operator|+
name|collName
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
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Sets the collection attribute of the LocalCollectionManagementService      *  object      *      *@param  parent              The new collection value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|parent
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|parent
operator|=
operator|(
name|LocalCollection
operator|)
name|parent
expr_stmt|;
block|}
comment|/**      *  Sets the property attribute of the LocalCollectionManagementService      *  object      *      *@param  property  The new property value      *@param  value     The new property value      */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

