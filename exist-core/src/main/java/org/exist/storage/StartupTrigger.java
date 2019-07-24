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
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
operator|.
name|Txn
import|;
end_import

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
comment|/**  * Database Startup Trigger  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|StartupTrigger
block|{
comment|/**      * Synchronously execute a task at database Startup before the database is made available to connections      * Remember, your code within the execute function will block the database startup until it completes!      *      * Any RuntimeExceptions thrown will be ignored and database startup will continue      * Database Startup cannot be aborted by this Trigger!      *       * Note: If you want an Asynchronous Trigger, you could use a Future in your implementation      * to start a new thread, however you cannot access the sysBroker from that thread      * as it may have been returned to the broker pool. Instead if you need a broker, you may be able to      * do something clever by checking the database status and then acquiring a new broker      * from the broker pool. If you wish to work with the broker pool you must obtain this before      * starting your asynchronous execution by calling sysBroker.getBrokerPool().      *       * @param sysBroker The single system broker available during database startup      * @param transaction Transaction      * @param params Key, Values      */
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|DBBroker
name|sysBroker
parameter_list|,
specifier|final
name|Txn
name|transaction
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

