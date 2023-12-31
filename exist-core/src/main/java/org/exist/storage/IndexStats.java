begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * IndexStats.java - Apr 4, 2003  *   * @author wolf  */
end_comment

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
name|dom
operator|.
name|DOMFile
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
name|index
operator|.
name|BFile
import|;
end_import

begin_class
specifier|public
class|class
name|IndexStats
block|{
specifier|private
name|BufferStats
name|indexBufferStats
init|=
literal|null
decl_stmt|;
specifier|private
name|BufferStats
name|dataBufferStats
init|=
literal|null
decl_stmt|;
specifier|public
name|IndexStats
parameter_list|(
name|BFile
name|db
parameter_list|)
block|{
name|indexBufferStats
operator|=
name|db
operator|.
name|getIndexBufferStats
argument_list|()
expr_stmt|;
name|dataBufferStats
operator|=
name|db
operator|.
name|getDataBufferStats
argument_list|()
expr_stmt|;
block|}
specifier|public
name|IndexStats
parameter_list|(
name|DOMFile
name|db
parameter_list|)
block|{
name|indexBufferStats
operator|=
name|db
operator|.
name|getIndexBufferStats
argument_list|()
expr_stmt|;
name|dataBufferStats
operator|=
name|db
operator|.
name|getDataBufferStats
argument_list|()
expr_stmt|;
block|}
specifier|public
name|BufferStats
name|getIndexBufferStats
parameter_list|()
block|{
return|return
name|indexBufferStats
return|;
block|}
specifier|public
name|BufferStats
name|getDataBufferStats
parameter_list|()
block|{
return|return
name|dataBufferStats
return|;
block|}
block|}
end_class

end_unit

