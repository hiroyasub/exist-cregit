begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Apr 10, 2004  */
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Service
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
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * @author seb  */
end_comment

begin_interface
specifier|public
interface|interface
name|SchemaService
extends|extends
name|Service
extends|,
name|SchemaAccess
block|{
comment|/** find the whole schema as an XMLResource */
name|XMLResource
name|getSchema
parameter_list|(
name|String
name|targetNamespace
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** Stores a new schema given its contents */
name|void
name|putSchema
parameter_list|(
name|String
name|schemaContents
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** Validates a resource in the current collection */
name|boolean
name|validateResource
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** Validates a resource given its contents */
name|boolean
name|validateContents
parameter_list|(
name|String
name|contents
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** Add a schema on-the-fly. This schema will not be made persistent. This is usefull to validate 	 * documents where one knows that a schema is not in the schema store. 	 * @param schema 	 * @throws XMLDBException 	 */
name|void
name|registerTransientSchema
parameter_list|(
name|String
name|schema
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Delete and recreate the index file based on the schema resources stored in the  	 * /db/system/schema collection 	 * @throws XMLDBException 	 */
name|void
name|rebuildIndex
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

