begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Apr 23, 2004  *  */
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
name|xmldb
operator|.
name|RemoteCollection
import|;
end_import

begin_class
specifier|public
class|class
name|RemoteSchemaService
extends|extends
name|GenericSchemaService
block|{
specifier|public
name|RemoteSchemaService
parameter_list|(
name|RemoteCollection
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

