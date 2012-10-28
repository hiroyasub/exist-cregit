begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|security
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|AbstractSpinnerModel
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
name|Permission
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|UmaskSpinnerModel
extends|extends
name|AbstractSpinnerModel
block|{
comment|//private int umask = Permission.DEFAULT_UMASK;
specifier|private
name|int
name|umask
init|=
literal|0765
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|OCTAL_RADIX
init|=
literal|8
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|intToOctalUmask
argument_list|(
name|umask
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
operator|(
name|value
operator|==
literal|null
operator|)
operator|||
operator|!
operator|(
name|value
operator|instanceof
name|String
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal value"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|otherUmask
init|=
name|octalUmaskToInt
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherUmask
operator|!=
name|umask
condition|)
block|{
name|umask
operator|=
name|otherUmask
expr_stmt|;
name|fireStateChanged
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getNextValue
parameter_list|()
block|{
specifier|final
name|String
name|result
decl_stmt|;
if|if
condition|(
name|umask
operator|<
literal|0777
condition|)
block|{
name|result
operator|=
name|intToOctalUmask
argument_list|(
name|nextUmask
argument_list|(
name|umask
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
literal|"0777"
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getPreviousValue
parameter_list|()
block|{
specifier|final
name|String
name|result
decl_stmt|;
if|if
condition|(
name|umask
operator|>
literal|0
condition|)
block|{
name|result
operator|=
name|intToOctalUmask
argument_list|(
name|prevUmask
argument_list|(
name|umask
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
literal|"0000"
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|int
name|prevUmask
parameter_list|(
name|int
name|umask
parameter_list|)
block|{
if|if
condition|(
name|umask
operator|==
literal|0070
condition|)
block|{
return|return
literal|0007
return|;
block|}
if|else if
condition|(
name|umask
operator|==
literal|0700
condition|)
block|{
return|return
literal|0077
return|;
block|}
else|else
block|{
return|return
name|umask
operator|-
literal|01
return|;
block|}
block|}
specifier|private
name|int
name|nextUmask
parameter_list|(
name|int
name|umask
parameter_list|)
block|{
if|if
condition|(
name|umask
operator|==
literal|0007
condition|)
block|{
return|return
literal|0010
return|;
block|}
if|else if
condition|(
name|umask
operator|==
literal|0070
condition|)
block|{
return|return
literal|0100
return|;
block|}
else|else
block|{
return|return
name|umask
operator|+
literal|01
return|;
block|}
block|}
specifier|private
name|int
name|octalUmaskToInt
parameter_list|(
specifier|final
name|String
name|octalUmask
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|octalUmask
argument_list|,
name|OCTAL_RADIX
argument_list|)
return|;
block|}
specifier|private
name|String
name|intToOctalUmask
parameter_list|(
specifier|final
name|int
name|umask
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%4s"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|umask
argument_list|,
name|OCTAL_RADIX
argument_list|)
argument_list|)
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'0'
argument_list|)
return|;
block|}
block|}
end_class

end_unit

