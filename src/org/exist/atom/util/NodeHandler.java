begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * NodeFilter.java  *  * Created on June 20, 2006, 12:31 PM  *  * (C) R. Alexander Milowski alex@milowski.com  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  *  * @author R. Alexander Milowski  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeHandler
block|{
name|void
name|process
parameter_list|(
name|Node
name|parent
parameter_list|,
name|Node
name|input
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

