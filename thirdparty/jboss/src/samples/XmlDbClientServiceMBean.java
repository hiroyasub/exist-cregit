begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|samples
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|system
operator|.
name|ServiceMBean
import|;
end_import

begin_comment
comment|/**  * This are the managed operations for the test service  *  * @author Per Nyfelt  */
end_comment

begin_interface
specifier|public
interface|interface
name|XmlDbClientServiceMBean
extends|extends
name|ServiceMBean
block|{
name|String
name|useXmlDbService
parameter_list|()
function_decl|;
name|String
name|addXMLforResourceName
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|resourceName
parameter_list|)
function_decl|;
name|String
name|fetchXMLforResurceName
parameter_list|(
name|String
name|resourceName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

