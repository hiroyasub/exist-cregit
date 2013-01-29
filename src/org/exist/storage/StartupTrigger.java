begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Database Startup Trigger  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_interface
specifier|public
interface|interface
name|StartupTrigger
block|{
comment|/**      * Synchronously execute a task at database Startup before the database is made available to connections      *       * Any RuntimeExceptions thrown will be ignored and database startup will continue      * Database Startup cannot be aborted by this Trigger!      *       * Note, If you want an Asynchronous Trigger, simply use a Future in your implementation      *       * @param broker      * @param params Key, Values      */
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|DBBroker
name|sysBroker
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|params
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

