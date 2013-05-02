begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_comment
comment|/**  * Base class to be implemented by an index module if it wants to rewrite  * certain query expressions. Subclasses should overwrite the rewriteXXX methods  * they are interested in.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|QueryRewriter
block|{
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|QueryRewriter
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|boolean
name|rewriteLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
block|}
end_class

end_unit

