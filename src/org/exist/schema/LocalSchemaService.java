begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Apr 10, 2004  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|schema
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
name|exist
operator|.
name|xmldb
operator|.
name|LocalCollection
import|;
end_import

begin_comment
comment|/**  * @author seb  */
end_comment

begin_class
specifier|public
class|class
name|LocalSchemaService
extends|extends
name|GenericSchemaService
block|{
specifier|public
name|LocalSchemaService
parameter_list|(
name|User
name|user
parameter_list|,
name|BrokerPool
name|pool
parameter_list|,
name|LocalCollection
name|collection
parameter_list|)
block|{
name|super
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

