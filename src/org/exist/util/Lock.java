begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * @author wolf  *  * To change this generated comment edit the template variable "typecomment":  * Window>Preferences>Java>Templates.  * To enable and disable the creation of type comments go to  * Window>Preferences>Java>Code Generation.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Lock
block|{
specifier|public
specifier|final
specifier|static
name|int
name|READ_LOCK
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|WRITE_LOCK
init|=
literal|1
decl_stmt|;
specifier|public
name|boolean
name|acquire
parameter_list|( )
throws|throws
name|LockException
function_decl|;
specifier|public
name|boolean
name|acquire
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|LockException
function_decl|;
specifier|public
name|void
name|release
parameter_list|( )
function_decl|;
block|}
end_interface

end_unit

