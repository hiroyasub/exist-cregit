begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on May 25, 2004  *  */
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
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exolab
operator|.
name|castor
operator|.
name|xml
operator|.
name|schema
operator|.
name|AttributeDecl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exolab
operator|.
name|castor
operator|.
name|xml
operator|.
name|schema
operator|.
name|ElementDecl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exolab
operator|.
name|castor
operator|.
name|xml
operator|.
name|schema
operator|.
name|XMLType
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
comment|/**  * @author seb  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|SchemaAccess
block|{
name|XMLType
name|getType
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
name|ElementDecl
name|getElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|//void getElement(String xpath) throws XMLDBException;
name|AttributeDecl
name|getAttribute
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Is a schema defining this namespace known  	 * @param namespaceURI 	 * @return 	 * @throws XMLDBException 	 */
name|boolean
name|isKnownNamespace
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

