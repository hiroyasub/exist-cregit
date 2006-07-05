begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|javax
operator|.
name|xml
operator|.
name|namespace
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

begin_comment
comment|/**  * A local copy of the JAXP 1.3 NamespaceContext interface, included here to avoid problems  * when running under JDK 1.4.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NamespaceContext
block|{
name|String
name|getNamespaceURI
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
name|String
name|getPrefix
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
function_decl|;
name|Iterator
name|getPrefixes
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

