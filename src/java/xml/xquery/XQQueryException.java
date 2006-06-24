begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|javax
operator|.
name|xml
operator|.
name|xquery
package|;
end_package

begin_comment
comment|/**  * XQJ interfaces reconstructed from version 0.5 documentation  */
end_comment

begin_class
specifier|public
class|class
name|XQQueryException
extends|extends
name|XQException
block|{
specifier|private
name|String
name|errorCode
decl_stmt|;
specifier|private
name|String
name|expr
decl_stmt|;
specifier|private
name|XQItem
name|errorItem
decl_stmt|;
specifier|private
name|int
name|lineNumber
decl_stmt|;
specifier|private
name|int
name|position
decl_stmt|;
specifier|private
name|XQStackTraceElement
index|[]
name|trace
decl_stmt|;
name|XQQueryException
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|message
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|Throwable
name|cause
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|vendorCode
parameter_list|,
name|XQException
name|nextException
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|errorCode
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|expr
parameter_list|,
name|XQItem
name|errorItem
parameter_list|,
name|int
name|lineNumber
parameter_list|,
name|int
name|position
parameter_list|,
name|XQStackTraceElement
index|[]
name|trace
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|,
name|vendorCode
argument_list|,
name|nextException
argument_list|)
expr_stmt|;
name|this
operator|.
name|errorCode
operator|=
name|errorCode
expr_stmt|;
name|this
operator|.
name|expr
operator|=
name|expr
expr_stmt|;
name|this
operator|.
name|errorItem
operator|=
name|errorItem
expr_stmt|;
name|this
operator|.
name|lineNumber
operator|=
name|lineNumber
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
name|java
operator|.
name|lang
operator|.
name|String
name|getErrorCode
parameter_list|()
block|{
return|return
name|errorCode
return|;
block|}
name|XQItem
name|getErrorItem
parameter_list|()
block|{
return|return
name|errorItem
return|;
block|}
name|java
operator|.
name|lang
operator|.
name|String
name|getExpression
parameter_list|()
block|{
return|return
name|expr
return|;
block|}
name|int
name|getLineNumber
parameter_list|()
block|{
return|return
name|lineNumber
return|;
block|}
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
name|XQStackTraceElement
index|[]
name|getXQStackTrace
parameter_list|()
block|{
return|return
name|trace
return|;
block|}
block|}
end_class

end_unit

