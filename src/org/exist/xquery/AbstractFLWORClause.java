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
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_comment
comment|/**  * Abstract base class for clauses in a FLWOR expressions, for/let/group by ...  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractFLWORClause
extends|extends
name|AbstractExpression
implements|implements
name|FLWORClause
block|{
specifier|protected
name|LocalVariable
name|firstVar
init|=
literal|null
decl_stmt|;
specifier|private
name|FLWORClause
name|previousClause
init|=
literal|null
decl_stmt|;
specifier|protected
name|Expression
name|returnExpr
decl_stmt|;
specifier|public
name|AbstractFLWORClause
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LocalVariable
name|createVariable
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|LocalVariable
name|var
init|=
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|name
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstVar
operator|==
literal|null
condition|)
block|{
name|firstVar
operator|=
name|var
expr_stmt|;
block|}
return|return
name|var
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|postEval
parameter_list|(
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// reset variable after evaluation has completed
name|firstVar
operator|=
literal|null
expr_stmt|;
return|return
name|seq
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setReturnExpression
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|returnExpr
operator|=
name|expr
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getReturnExpression
parameter_list|()
block|{
return|return
name|returnExpr
return|;
block|}
annotation|@
name|Override
specifier|public
name|LocalVariable
name|getStartVariable
parameter_list|()
block|{
return|return
name|firstVar
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPreviousClause
parameter_list|(
name|FLWORClause
name|clause
parameter_list|)
block|{
name|previousClause
operator|=
name|clause
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|FLWORClause
name|getPreviousClause
parameter_list|()
block|{
return|return
name|previousClause
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|firstVar
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

