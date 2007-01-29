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

begin_comment
comment|/**  * Defines a visitor to be used for traversing and analyzing the  * expression tree.  *   * @author wolf  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExpressionVisitor
block|{
comment|/** 	 * Default fallback method if no other method matches 	 * the object's type. 	 *  	 * @param expression 	 */
specifier|public
name|void
name|visit
parameter_list|(
name|Expression
name|expression
parameter_list|)
function_decl|;
comment|/** Found a PathExpr */
specifier|public
name|void
name|visitPathExpr
parameter_list|(
name|PathExpr
name|expression
parameter_list|)
function_decl|;
comment|/** Found a LocationStep */
specifier|public
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
function_decl|;
specifier|public
name|void
name|visitPredicate
parameter_list|(
name|Predicate
name|predicate
parameter_list|)
function_decl|;
specifier|public
name|void
name|visitGeneralComparison
parameter_list|(
name|GeneralComparison
name|comparison
parameter_list|)
function_decl|;
comment|/** Found a CastExpression */
specifier|public
name|void
name|visitCastExpr
parameter_list|(
name|CastExpression
name|expression
parameter_list|)
function_decl|;
specifier|public
name|void
name|visitUnionExpr
parameter_list|(
name|Union
name|union
parameter_list|)
function_decl|;
specifier|public
name|void
name|visitFtExpression
parameter_list|(
name|ExtFulltext
name|fulltext
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

