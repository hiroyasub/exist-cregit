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
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|*
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DBWrapper
block|{
specifier|protected
name|DBConnectionPool
name|pool
decl_stmt|;
specifier|protected
name|Configuration
name|config
decl_stmt|;
specifier|public
name|DBWrapper
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|DBConnectionPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|loadFromFile
parameter_list|(
name|String
name|fname
parameter_list|,
name|String
name|table
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|protected
name|boolean
name|checkFile
parameter_list|(
name|String
name|fname
parameter_list|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
specifier|protected
name|void
name|removeFile
parameter_list|(
name|String
name|fname
parameter_list|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

