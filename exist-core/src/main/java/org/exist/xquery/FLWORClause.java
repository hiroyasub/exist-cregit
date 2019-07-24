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
name|value
operator|.
name|Sequence
import|;
end_import

begin_comment
comment|/**  * Interface for FLWOR clauses like for/let/group by ...  *  * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|FLWORClause
extends|extends
name|Expression
block|{
enum|enum
name|ClauseType
block|{
name|FOR
block|,
name|LET
block|,
name|GROUPBY
block|,
name|ORDERBY
block|,
name|WHERE
block|,
name|SOME
block|,
name|EVERY
block|}
comment|/**      * Returns the type of clause implemented by a subclass.      *      * @return the type of the clause      */
name|ClauseType
name|getType
parameter_list|()
function_decl|;
comment|/**      * Set the return expression of the clause. Might either be      * an expression given in a "return" or another clause.      *      * @param expr the return expression      */
name|void
name|setReturnExpression
parameter_list|(
name|Expression
name|expr
parameter_list|)
function_decl|;
comment|/**      * Get the return expression of the clause.      *      * @return the return expression      */
name|Expression
name|getReturnExpression
parameter_list|()
function_decl|;
comment|/**      * Set the previous FLWOR clause if this is not the      * top clause.      *      * @param clause the previous clause      */
name|void
name|setPreviousClause
parameter_list|(
name|FLWORClause
name|clause
parameter_list|)
function_decl|;
comment|/**      * Get the previous FLWOR clause if this is not the      * top clause.      *      * @return previous clause or null if this is the top clause      */
name|FLWORClause
name|getPreviousClause
parameter_list|()
function_decl|;
comment|/**      * Called by a for clause before it starts iteration, passing in      * the sequence of items to be iterated. Used by {@link WhereClause}      * to filter the input sequence in advance if possible.      *      * @param seq the sequence of items to be iterated by the current for      * @return post-processed result sequence      * @throws XPathException if an error occurs during pre-evaluation      */
name|Sequence
name|preEval
parameter_list|(
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Called by the top FLWOR expression when it finished iteration.      * Implemented by {@link GroupByClause}, which first collects      * tuples into groups, then processes them in this method.      *      * @param seq the return sequence of the top FLWOR expression      * @return post-processed result sequence      * @throws XPathException if an error occurs during post-evaluation      */
name|Sequence
name|postEval
parameter_list|(
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Create a new local variable for the FLWOR clause.      * Tracks the variables for this expression.      *      * @param name the name of the variable      * @return a new local variable, registered in the context      * @throws XPathException if an error occurs whilst creating the variable      */
name|LocalVariable
name|createVariable
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Returns the first variable created by this FLWOR clause for reference      * from subsequent clauses.      *      * @return first variable created      */
name|LocalVariable
name|getStartVariable
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

