begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
operator|.
name|AbstractLoggable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
operator|.
name|LogEntryTypes
import|;
end_import

begin_class
specifier|public
class|class
name|TxnAbort
extends|extends
name|AbstractLoggable
block|{
specifier|public
name|TxnAbort
parameter_list|(
name|long
name|transactionId
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TxnAbort
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|long
name|transactionId
parameter_list|)
block|{
name|super
argument_list|(
name|LogEntryTypes
operator|.
name|TXN_ABORT
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|ByteBuffer
name|out
parameter_list|)
block|{
block|}
specifier|public
name|void
name|read
parameter_list|(
name|ByteBuffer
name|in
parameter_list|)
block|{
block|}
specifier|public
name|int
name|getLogSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|String
name|dump
parameter_list|()
block|{
return|return
name|super
operator|.
name|dump
argument_list|()
operator|+
literal|" - transaction "
operator|+
name|transactId
operator|+
literal|" aborted."
return|;
block|}
block|}
end_class

end_unit

