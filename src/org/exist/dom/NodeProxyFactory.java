begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_comment
comment|/**  * NodeProxyFactory.java enclosing_type  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|NodeProxyFactory
block|{
specifier|private
specifier|static
name|Stack
name|stack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|void
name|release
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|stack
operator|.
name|push
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|NodeProxy
name|createInstance
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|short
name|nodeType
parameter_list|,
name|long
name|address
parameter_list|)
block|{
if|if
condition|(
name|stack
operator|.
name|empty
argument_list|()
condition|)
return|return
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
name|address
argument_list|)
return|;
else|else
block|{
name|NodeProxy
name|proxy
init|=
operator|(
name|NodeProxy
operator|)
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|proxy
operator|.
name|setDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setGID
argument_list|(
name|gid
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setNodeType
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setInternalAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
return|return
name|proxy
return|;
block|}
block|}
specifier|public
specifier|static
name|NodeProxy
name|createInstance
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|)
block|{
return|return
name|createInstance
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeProxy
name|createInstance
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|short
name|nodeType
parameter_list|)
block|{
return|return
name|createInstance
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
name|nodeType
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeProxy
name|createInstance
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
return|return
name|createInstance
argument_list|(
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|,
name|p
operator|.
name|getNodeType
argument_list|()
argument_list|,
name|p
operator|.
name|getNodeType
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

