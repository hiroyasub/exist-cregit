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
name|util
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|LockException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ReadOnlyException
import|;
end_import

begin_comment
comment|/**  * DOMTransaction controls access to the DOM file  *   * This implements a wrapper around the code passed in  * method start(). The class acquires a lock on the  * file, enters the locked code block and calls start.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DOMTransaction
block|{
specifier|private
name|Object
name|ownerObject
decl_stmt|;
specifier|private
name|DOMFile
name|file
decl_stmt|;
specifier|private
name|int
name|mode
init|=
name|Lock
operator|.
name|READ_LOCK
decl_stmt|;
specifier|public
name|DOMTransaction
parameter_list|(
name|Object
name|owner
parameter_list|,
name|DOMFile
name|f
parameter_list|)
block|{
name|ownerObject
operator|=
name|owner
expr_stmt|;
name|file
operator|=
name|f
expr_stmt|;
block|}
specifier|public
name|DOMTransaction
parameter_list|(
name|Object
name|owner
parameter_list|,
name|DOMFile
name|f
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|this
argument_list|(
name|owner
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|Object
name|start
parameter_list|()
throws|throws
name|ReadOnlyException
function_decl|;
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|Lock
name|lock
init|=
name|file
operator|.
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
comment|// try to acquire a lock on the file
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|ownerObject
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|lock
operator|.
name|enter
argument_list|(
name|ownerObject
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
comment|// timed out
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|file
operator|.
name|setOwnerObject
argument_list|(
name|ownerObject
argument_list|)
expr_stmt|;
return|return
name|start
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ReadOnlyException
name|e
parameter_list|)
block|{
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|ownerObject
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

