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

begin_comment
comment|/**  * Database Startup Trigger  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_interface
specifier|public
interface|interface
name|StartupTrigger
block|{
comment|/**      * Synchronously execute a task at database Startup before the database is made available to connections      *       * Any RuntimeExceptions thrown will be ignored and database startup will continue      * Database Startup cannot be aborted by this Trigger!      *       * If you want an Asynchronous Trigger, simply use a Future in your implementation      */
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

