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
return|return
name|broker
operator|.
name|getTextEngine
argument_list|()
operator|.
name|scanIndexTerms
argument_list|(
name|user
argument_list|,
name|parent
operator|.
name|getCollection
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|inclusive
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
block|}
end_class

end_unit

