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

begin_comment
comment|/**  * Local implementation of the DatabaseInstanceManager.  */
end_comment

begin_class
specifier|public
class|class
name|LocalDatabaseInstanceManager
implements|implements
name|DatabaseInstanceManager
block|{
specifier|protected
name|BrokerPool
name|pool
decl_stmt|;
specifier|protected
name|User
name|user
decl_stmt|;
specifier|public
name|LocalDatabaseInstanceManager
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
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
block|}
comment|/** 	 *  Shutdown the Database instance 	 * 	 *@exception  XMLDBException 	 */
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
name|user
operator|.
name|hasGroup
argument_list|(
literal|"dba"
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"only users in group dba may "
operator|+
literal|"shut down the database"
argument_list|)
throw|;
name|pool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
literal|"DatabaseInstanceManager"
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

