begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
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

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_interface
specifier|public
interface|interface
name|ByteBufferAccessor
block|{
name|ByteBuffer
name|getBuffer
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

