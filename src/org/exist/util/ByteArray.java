begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ByteArray.java - Jun 3, 2003  *   * @author wolf  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_interface
specifier|public
interface|interface
name|ByteArray
block|{
name|void
name|setLength
parameter_list|(
name|int
name|len
parameter_list|)
function_decl|;
name|void
name|copyTo
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
name|void
name|copyTo
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|)
function_decl|;
name|void
name|copyTo
parameter_list|(
name|int
name|start
parameter_list|,
name|byte
index|[]
name|newBuf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
name|void
name|append
parameter_list|(
name|byte
name|b
parameter_list|)
function_decl|;
name|void
name|append
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
function_decl|;
name|void
name|append
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
name|int
name|size
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

