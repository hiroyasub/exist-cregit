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

begin_interface
specifier|public
interface|interface
name|TxnListener
block|{
specifier|public
name|void
name|commit
parameter_list|()
function_decl|;
specifier|public
name|void
name|abort
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

