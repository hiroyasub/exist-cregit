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

begin_class
specifier|public
class|class
name|LockException
extends|extends
name|Exception
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6273549212242606084L
decl_stmt|;
comment|/** 	 * Constructor for LockException. 	 */
specifier|public
name|LockException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Constructor for LockException. 	 * @param s 	 */
specifier|public
name|LockException
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

