begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|cocoon
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|component
operator|.
name|Component
import|;
end_import

begin_comment
comment|/**  *  Description of the Interface  *  *@author     Christofer Dutz  *@created    14. Februar 2002  *@version    1.0  */
end_comment

begin_interface
specifier|public
interface|interface
name|Exist
extends|extends
name|Component
block|{
name|String
name|ROLE
init|=
literal|"org.exist.cocoon.Exist"
decl_stmt|;
block|}
end_interface

end_unit

