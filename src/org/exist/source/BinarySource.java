begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|BinarySource
extends|extends
name|AbstractSource
block|{
comment|//TODO replace this with a streaming approach
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
name|boolean
name|checkEncoding
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|public
name|BinarySource
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|boolean
name|checkXQEncoding
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|checkEncoding
operator|=
name|checkXQEncoding
expr_stmt|;
block|}
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|data
return|;
block|}
specifier|public
name|int
name|isValid
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|Source
operator|.
name|VALID
return|;
block|}
specifier|public
name|int
name|isValid
parameter_list|(
name|Source
name|other
parameter_list|)
block|{
return|return
name|Source
operator|.
name|VALID
return|;
block|}
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|checkEncoding
argument_list|()
expr_stmt|;
return|return
operator|new
name|InputStreamReader
argument_list|(
name|getInputStream
argument_list|()
argument_list|,
name|encoding
argument_list|)
return|;
block|}
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
return|;
block|}
specifier|public
name|String
name|getContent
parameter_list|()
throws|throws
name|IOException
block|{
name|checkEncoding
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|encoding
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkEncoding
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|checkEncoding
condition|)
block|{
specifier|final
name|InputStream
name|is
init|=
name|getInputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|checkedEnc
init|=
name|guessXQueryEncoding
argument_list|(
name|is
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkedEnc
operator|!=
literal|null
condition|)
block|{
name|encoding
operator|=
name|checkedEnc
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|validate
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|int
name|perm
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|// TODO protected?
block|}
block|}
end_class

end_unit

