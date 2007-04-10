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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|ExtFulltext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Basic implementation of the {@link ExpressionVisitor} interface.  * This implementation will traverse a PathExpr object if it wraps  * around a single other expression. All other methods are empty.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|BasicExpressionVisitor
implements|implements
name|ExpressionVisitor
block|{
specifier|public
name|void
name|visit
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|processWrappers
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitCastExpr
parameter_list|(
name|CastExpression
name|expression
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitFtExpression
parameter_list|(
name|ExtFulltext
name|fulltext
parameter_list|)
block|{
block|}
comment|/** 	 * Default implementation will traverse a PathExpr 	 * if it is just a wrapper around another single 	 * expression object. 	 */
specifier|public
name|void
name|visitPathExpr
parameter_list|(
name|PathExpr
name|expression
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Expression
name|next
init|=
name|expression
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|next
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|visitGeneralComparison
parameter_list|(
name|GeneralComparison
name|comparison
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitUnionExpr
parameter_list|(
name|Union
name|union
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitAndExpr
parameter_list|(
name|OpAnd
name|and
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitOrExpr
parameter_list|(
name|OpOr
name|or
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitPredicate
parameter_list|(
name|Predicate
name|predicate
parameter_list|)
block|{
block|}
specifier|protected
name|void
name|processWrappers
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|Atomize
operator|||
name|expr
operator|instanceof
name|DynamicCardinalityCheck
operator|||
name|expr
operator|instanceof
name|DynamicNameCheck
operator|||
name|expr
operator|instanceof
name|DynamicTypeCheck
operator|||
name|expr
operator|instanceof
name|UntypedValueCheck
condition|)
block|{
name|expr
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|LocationStep
name|findFirstStep
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|LocationStep
condition|)
return|return
operator|(
name|LocationStep
operator|)
name|expr
return|;
name|FirstStepVisitor
name|visitor
init|=
operator|new
name|FirstStepVisitor
argument_list|()
decl_stmt|;
name|expr
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|firstStep
return|;
block|}
specifier|public
specifier|static
name|List
name|findLocationSteps
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
specifier|final
name|List
name|steps
init|=
operator|new
name|ArrayList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|instanceof
name|LocationStep
condition|)
block|{
name|steps
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
return|return
name|steps
return|;
block|}
name|expr
operator|.
name|accept
argument_list|(
operator|new
name|BasicExpressionVisitor
argument_list|()
block|{
specifier|public
name|void
name|visitPathExpr
parameter_list|(
name|PathExpr
name|expression
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expression
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|next
init|=
name|expression
operator|.
name|getExpression
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|next
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
block|{
name|steps
operator|.
name|add
argument_list|(
name|locationStep
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|steps
return|;
block|}
specifier|public
name|void
name|visitForExpression
parameter_list|(
name|ForExpr
name|forExpr
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitLetExpression
parameter_list|(
name|LetExpr
name|letExpr
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitBuiltinFunction
parameter_list|(
name|Function
name|function
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitUserFunction
parameter_list|(
name|UserDefinedFunction
name|function
parameter_list|)
block|{
block|}
specifier|public
name|void
name|visitConditional
parameter_list|(
name|ConditionalExpression
name|conditional
parameter_list|)
block|{
block|}
specifier|public
specifier|static
class|class
name|FirstStepVisitor
extends|extends
name|BasicExpressionVisitor
block|{
specifier|private
name|LocationStep
name|firstStep
init|=
literal|null
decl_stmt|;
specifier|public
name|LocationStep
name|getFirstStep
parameter_list|()
block|{
return|return
name|firstStep
return|;
block|}
specifier|public
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
block|{
name|firstStep
operator|=
name|locationStep
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

