begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|javax
operator|.
name|xml
operator|.
name|xquery
package|;
end_package

begin_comment
comment|/**  * XQJ interfaces reconstructed from version 0.5 documentation  */
end_comment

begin_interface
specifier|public
interface|interface
name|XQMetaData
block|{
name|int
name|getMaxExpressionLength
parameter_list|()
function_decl|;
name|int
name|getMaxUserNameLength
parameter_list|()
function_decl|;
name|int
name|getProductMajorVersion
parameter_list|()
function_decl|;
name|int
name|getProductMinorVersion
parameter_list|()
function_decl|;
name|java
operator|.
name|lang
operator|.
name|String
name|getProductName
parameter_list|()
function_decl|;
name|java
operator|.
name|lang
operator|.
name|String
name|getProductVersion
parameter_list|()
function_decl|;
name|java
operator|.
name|lang
operator|.
name|String
name|getUserName
parameter_list|()
function_decl|;
name|int
name|getXQJMajorVersion
parameter_list|()
function_decl|;
name|int
name|getXQJMinorVersion
parameter_list|()
function_decl|;
name|java
operator|.
name|lang
operator|.
name|String
name|getXQJVersion
parameter_list|()
function_decl|;
name|boolean
name|isCollectionNestingSupported
parameter_list|()
function_decl|;
name|boolean
name|isFullAxisFeatureSupported
parameter_list|()
function_decl|;
name|boolean
name|isModuleFeatureSupported
parameter_list|()
function_decl|;
name|boolean
name|isReadOnly
parameter_list|()
function_decl|;
name|boolean
name|isSchemaImportFeatureSupported
parameter_list|()
function_decl|;
name|boolean
name|isSchemaValidationFeatureSupported
parameter_list|()
function_decl|;
name|boolean
name|isSerializationFeatureSupported
parameter_list|()
function_decl|;
name|boolean
name|isStaticTypingExtensionsSupported
parameter_list|()
function_decl|;
name|boolean
name|isStaticTypingFeatureSupported
parameter_list|()
function_decl|;
name|boolean
name|isTransactionSupported
parameter_list|()
function_decl|;
name|boolean
name|isXQueryXSupported
parameter_list|()
function_decl|;
name|boolean
name|wasCreatedFromJDBCConnection
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

