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
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
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
comment|/**  * @author wolf  *  * To change this generated comment edit the template variable "typecomment":  * Window>Preferences>Java>Templates.  * To enable and disable the creation of type comments go to  * Window>Preferences>Java>Code Generation.  */
end_comment

begin_class
specifier|public
class|class
name|DatabaseInstanceManagerImpl
implements|implements
name|DatabaseInstanceManager
block|{
specifier|protected
name|XmlRpcClient
name|client
decl_stmt|;
comment|/** 	 * Constructor for DatabaseInstanceManagerImpl. 	 */
specifier|public
name|DatabaseInstanceManagerImpl
parameter_list|(
name|XmlRpcClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
comment|/** 	 * @see org.exist.xmldb.DatabaseInstanceManager#shutdown() 	 */
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"shutdown"
argument_list|,
operator|new
name|Vector
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
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
literal|"shutdown failed"
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
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"shutdown failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
specifier|public
name|boolean
name|isLocalInstance
parameter_list|()
block|{
return|return
literal|false
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
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.DatabaseInstanceManager#getConfiguration() 	 */
specifier|public
name|DatabaseStatus
name|getStatus
parameter_list|()
throws|throws
name|XMLDBException
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NOT_IMPLEMENTED
argument_list|,
literal|"this method is not available for remote connections"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

