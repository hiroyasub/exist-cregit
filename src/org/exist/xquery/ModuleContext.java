begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|memtree
operator|.
name|MemTreeBuilder
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
name|UpdateListener
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
name|AnyURIValue
import|;
end_import

begin_comment
comment|/**  * Subclass of {@link org.exist.xquery.XQueryContext} for  * imported modules.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ModuleContext
extends|extends
name|XQueryContext
block|{
specifier|private
specifier|final
name|XQueryContext
name|parentContext
decl_stmt|;
comment|/** 	 * @param parentContext 	 */
specifier|public
name|ModuleContext
parameter_list|(
name|XQueryContext
name|parentContext
parameter_list|)
block|{
name|super
argument_list|(
name|parentContext
operator|.
name|getAccessContext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentContext
operator|=
name|parentContext
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|parentContext
operator|.
name|broker
expr_stmt|;
name|baseURI
operator|=
name|parentContext
operator|.
name|baseURI
expr_stmt|;
name|moduleLoadPath
operator|=
name|parentContext
operator|.
name|moduleLoadPath
expr_stmt|;
name|loadDefaults
argument_list|(
name|broker
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getStaticallyKnownDocuments() 	 */
specifier|public
name|DocumentSet
name|getStaticallyKnownDocuments
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|parentContext
operator|.
name|getStaticallyKnownDocuments
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getModule(java.lang.String) 	 */
specifier|public
name|Module
name|getModule
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
block|{
name|Module
name|module
init|=
name|super
operator|.
name|getModule
argument_list|(
name|namespaceURI
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
name|module
operator|=
name|parentContext
operator|.
name|getModule
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
return|return
name|module
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getModules() 	 */
specifier|public
name|Iterator
name|getModules
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getModules
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getWatchDog() 	 */
specifier|public
name|XQueryWatchDog
name|getWatchDog
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getWatchDog
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getBaseURI() 	 */
specifier|public
name|AnyURIValue
name|getBaseURI
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getBaseURI
argument_list|()
return|;
block|}
specifier|public
name|void
name|setBaseURI
parameter_list|(
name|AnyURIValue
name|uri
parameter_list|)
block|{
name|parentContext
operator|.
name|setBaseURI
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/**      * Delegate to parent context      *       * @see org.exist.xquery.XQueryContext#setXQueryContextVar(String, Object)      */
specifier|public
name|void
name|setXQueryContextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|XQvar
parameter_list|)
block|{
name|parentContext
operator|.
name|setXQueryContextVar
argument_list|(
name|name
argument_list|,
name|XQvar
argument_list|)
expr_stmt|;
block|}
comment|/**      * Delegate to parent context      *       * @see org.exist.xquery.XQueryContext#getXQueryContextVar(String)      */
specifier|public
name|Object
name|getXQueryContextVar
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|parentContext
operator|.
name|getXQueryContextVar
argument_list|(
name|name
argument_list|)
operator|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.XQueryContext#getBroker()      */
specifier|public
name|DBBroker
name|getBroker
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getBroker
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getDocumentBuilder() 	 */
specifier|public
name|MemTreeBuilder
name|getDocumentBuilder
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getDocumentBuilder
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#pushDocumentContext() 	 */
specifier|public
name|void
name|pushDocumentContext
parameter_list|()
block|{
name|parentContext
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#popDocumentContext() 	 */
specifier|public
name|void
name|popDocumentContext
parameter_list|()
block|{
name|parentContext
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|registerUpdateListener
parameter_list|(
name|UpdateListener
name|listener
parameter_list|)
block|{
name|parentContext
operator|.
name|registerUpdateListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|clearUpdateListeners
parameter_list|()
block|{
comment|// will be cleared by the parent context
block|}
block|}
end_class

end_unit

