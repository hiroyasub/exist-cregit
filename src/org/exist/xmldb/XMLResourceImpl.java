begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * XMLResourceImpl.java - Aug 4, 2003  *   * @author wolf  */
end_comment

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
name|util
operator|.
name|Date
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
comment|/**  * Extends org.xmldb.api.modules.XMLResource with eXist specific extensions.  */
end_comment

begin_interface
specifier|public
interface|interface
name|XMLResourceImpl
extends|extends
name|XMLResource
block|{
name|Date
name|getCreationTime
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
name|Date
name|getLastModificationTime
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

