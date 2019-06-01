begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

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
comment|/**  * Defines a visitor to be used for traversing and analyzing the  * expression tree.  *   * @author wolf  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExpressionVisitor
block|{
comment|/**      * Default fallback method if no other method matches      * the object's type.      *       * @param expression      */
name|void
name|visit
parameter_list|(
name|Expression
name|expression
parameter_list|)
function_decl|;
comment|/** Found a PathExpr */
name|void
name|visitPathExpr
parameter_list|(
name|PathExpr
name|expression
parameter_list|)
function_decl|;
comment|/** Found a LocationStep */
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
function_decl|;
name|void
name|visitFilteredExpr
parameter_list|(
name|FilteredExpression
name|filtered
parameter_list|)
function_decl|;
name|void
name|visitPredicate
parameter_list|(
name|Predicate
name|predicate
parameter_list|)
function_decl|;
name|void
name|visitFunctionCall
parameter_list|(
name|FunctionCall
name|call
parameter_list|)
function_decl|;
name|void
name|visitGeneralComparison
parameter_list|(
name|GeneralComparison
name|comparison
parameter_list|)
function_decl|;
name|void
name|visitCastExpr
parameter_list|(
name|CastExpression
name|expression
parameter_list|)
function_decl|;
name|void
name|visitUnionExpr
parameter_list|(
name|Union
name|union
parameter_list|)
function_decl|;
name|void
name|visitIntersectionExpr
parameter_list|(
name|Intersect
name|intersect
parameter_list|)
function_decl|;
name|void
name|visitAndExpr
parameter_list|(
name|OpAnd
name|and
parameter_list|)
function_decl|;
name|void
name|visitOrExpr
parameter_list|(
name|OpOr
name|or
parameter_list|)
function_decl|;
name|void
name|visitForExpression
parameter_list|(
name|ForExpr
name|forExpr
parameter_list|)
function_decl|;
name|void
name|visitLetExpression
parameter_list|(
name|LetExpr
name|letExpr
parameter_list|)
function_decl|;
name|void
name|visitOrderByClause
parameter_list|(
name|OrderByClause
name|orderBy
parameter_list|)
function_decl|;
name|void
name|visitGroupByClause
parameter_list|(
name|GroupByClause
name|groupBy
parameter_list|)
function_decl|;
name|void
name|visitWhereClause
parameter_list|(
name|WhereClause
name|where
parameter_list|)
function_decl|;
name|void
name|visitBuiltinFunction
parameter_list|(
name|Function
name|function
parameter_list|)
function_decl|;
name|void
name|visitUserFunction
parameter_list|(
name|UserDefinedFunction
name|function
parameter_list|)
function_decl|;
name|void
name|visitConditional
parameter_list|(
name|ConditionalExpression
name|conditional
parameter_list|)
function_decl|;
name|void
name|visitTryCatch
parameter_list|(
name|TryCatchExpression
name|tryCatch
parameter_list|)
function_decl|;
name|void
name|visitDocumentConstructor
parameter_list|(
name|DocumentConstructor
name|constructor
parameter_list|)
function_decl|;
name|void
name|visitElementConstructor
parameter_list|(
name|ElementConstructor
name|constructor
parameter_list|)
function_decl|;
name|void
name|visitTextConstructor
parameter_list|(
name|DynamicTextConstructor
name|constructor
parameter_list|)
function_decl|;
name|void
name|visitAttribConstructor
parameter_list|(
name|AttributeConstructor
name|constructor
parameter_list|)
function_decl|;
name|void
name|visitAttribConstructor
parameter_list|(
name|DynamicAttributeConstructor
name|constructor
parameter_list|)
function_decl|;
name|void
name|visitVariableReference
parameter_list|(
name|VariableReference
name|ref
parameter_list|)
function_decl|;
name|void
name|visitVariableDeclaration
parameter_list|(
name|VariableDeclaration
name|decl
parameter_list|)
function_decl|;
name|void
name|visitSimpleMapOperator
parameter_list|(
name|OpSimpleMap
name|simpleMap
parameter_list|)
function_decl|;
block|}
end_interface

end_unit
