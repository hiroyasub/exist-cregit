begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
package|;
end_package

begin_comment
comment|/**  * Defines bit flags to indicate, upon which parts of the execution context an expression  * depends ({@see org.exist.xpath.Expression#getDependencies()}).  *    * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Dependency
block|{
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
comment|/** 	 * Expression depends on one or more in-scope variables.  	 */
specifier|public
specifier|final
specifier|static
name|int
name|LOCAL_VARS
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|GLOBAL_VARS
init|=
literal|8
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
block|}
end_class

end_unit

