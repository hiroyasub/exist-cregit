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
comment|/**  * Defines bit flags to indicate, upon which parts of the execution context an expression  * depends ({@see org.exist.xquery.Expression#getDependencies()}).  *    * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Dependency
block|{
specifier|public
specifier|final
specifier|static
name|int
name|UNKNOWN_DEPENDENCY
init|=
operator|-
literal|1
decl_stmt|;
comment|/** 	 * Expression has no dependencies, for example, if it is a literal value. 	 */
specifier|public
specifier|final
specifier|static
name|int
name|NO_DEPENDENCY
init|=
literal|0
decl_stmt|;
comment|/** 	 * Expression depends on the context sequence. This is the default 	 * for most expressions. 	 */
specifier|public
specifier|final
specifier|static
name|int
name|CONTEXT_SET
init|=
literal|1
decl_stmt|;
comment|/** 	 * Expression depends on the current context item (in addition to the  	 * context sequence). 	 */
specifier|public
specifier|final
specifier|static
name|int
name|CONTEXT_ITEM
init|=
literal|2
decl_stmt|;
comment|/** 	 * Expression depends on a variable declared within the 	 * same for or let expression.  	 */
specifier|public
specifier|final
specifier|static
name|int
name|LOCAL_VARS
init|=
literal|4
decl_stmt|;
comment|/** 	 * Expression depends on a variable declared in the context, i.e. 	 * an outer let or for. 	 */
specifier|public
specifier|final
specifier|static
name|int
name|CONTEXT_VARS
init|=
literal|8
decl_stmt|;
comment|/** 	 * Bit mask to test if the expression depends on a variable reference. 	 */
specifier|public
specifier|final
specifier|static
name|int
name|VARS
init|=
name|LOCAL_VARS
operator|+
name|CONTEXT_VARS
decl_stmt|;
comment|/** 	 * Expression evaluates the context position and thus requires 	 * that the corresponding field in the context is set. 	 */
specifier|public
specifier|final
specifier|static
name|int
name|CONTEXT_POSITION
init|=
literal|16
decl_stmt|;
comment|/** 	 * The default dependencies: just CONTEXT_SET is set. 	 */
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_DEPENDENCIES
init|=
name|CONTEXT_SET
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|getDependenciesName
parameter_list|(
name|int
name|dependencies
parameter_list|)
block|{
if|if
condition|(
name|dependencies
operator|==
name|UNKNOWN_DEPENDENCY
condition|)
return|return
literal|"UNKNOWN"
return|;
if|if
condition|(
name|dependencies
operator|==
name|NO_DEPENDENCY
condition|)
return|return
literal|"NO_DEPENDENCY"
return|;
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|dependencies
operator|&
name|CONTEXT_SET
operator|)
operator|!=
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"CONTEXT_SET | "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|dependencies
operator|&
name|CONTEXT_ITEM
operator|)
operator|!=
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"CONTEXT_ITEM | "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|dependencies
operator|&
name|LOCAL_VARS
operator|)
operator|!=
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"LOCAL_VARS | "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|dependencies
operator|&
name|CONTEXT_VARS
operator|)
operator|!=
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"CONTEXT_VARS | "
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|dependencies
operator|&
name|CONTEXT_POSITION
operator|)
operator|!=
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"CONTEXT_POSITION | "
argument_list|)
expr_stmt|;
name|result
operator|.
name|delete
argument_list|(
name|result
operator|.
name|length
argument_list|()
operator|-
literal|3
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

