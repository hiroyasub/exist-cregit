begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Jul 15, 2004  *  * TODO To change the template for this generated file go to  * Window - Preferences - Java - Code Style - Code Templates  */
end_comment

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

begin_comment
comment|/**  * @author wolf  *  * TODO To change the template for this generated type comment go to  * Window - Preferences - Java - Code Style - Code Templates  */
end_comment

begin_class
specifier|public
class|class
name|ModuleContext
extends|extends
name|XQueryContext
block|{
specifier|private
name|XQueryContext
name|parentContext
decl_stmt|;
comment|/** 	 * @param broker 	 */
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
name|getBroker
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentContext
operator|=
name|parentContext
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getStaticallyKnownDocuments() 	 */
specifier|public
name|DocumentSet
name|getStaticallyKnownDocuments
parameter_list|()
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
return|return
name|parentContext
operator|.
name|getModule
argument_list|(
name|namespaceURI
argument_list|)
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
name|String
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
block|}
end_class

end_unit

