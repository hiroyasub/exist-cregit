begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2010 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id: TryCatchExpression.java 13700 2011-01-30 13:34:44Z dizzzz $  */
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

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
name|ErrorCodes
operator|.
name|EXistErrorCode
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
name|ErrorCodes
operator|.
name|ErrorCode
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
name|ErrorCodes
operator|.
name|JavaErrorCode
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
name|util
operator|.
name|ExpressionDumper
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
name|Item
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
name|QNameValue
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
name|SequenceType
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
name|StringValue
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
name|Type
import|;
end_import

begin_comment
comment|/**  * XQuery 1.1+ try {...} catch{...} expression.  *   * @author Adam Retter<adam@exist-db.org>  * @author Leif-JÃ¶ran Olsson<ljo@exist-db.org>  * @author Dannes Wessels<dannes@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|TryCatchExpression
extends|extends
name|AbstractExpression
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TryCatchExpression
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Expression
name|tryTargetExpr
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|CatchClause
argument_list|>
name|catchClauses
init|=
operator|new
name|ArrayList
argument_list|<
name|CatchClause
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      *  Constructor.      *       * @param context   Xquery context      * @param tryTargetExpr Expression to be evaluated      */
specifier|public
name|TryCatchExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|tryTargetExpr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|tryTargetExpr
operator|=
name|tryTargetExpr
expr_stmt|;
block|}
comment|/**      * Receive catch-clause data from parser.      *      * TODO: List<String> must be changed to List<QName>      */
specifier|public
name|void
name|addCatchClause
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|catchErrorList
parameter_list|,
name|List
argument_list|<
name|QName
argument_list|>
name|catchVars
parameter_list|,
name|Expression
name|catchExpr
parameter_list|)
block|{
name|catchClauses
operator|.
name|add
argument_list|(
operator|new
name|CatchClause
argument_list|(
name|catchErrorList
argument_list|,
name|catchVars
argument_list|,
name|catchExpr
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#getDependencies()      */
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator||
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
specifier|public
name|Expression
name|getTryTargetExpr
parameter_list|()
block|{
return|return
name|tryTargetExpr
return|;
block|}
specifier|public
name|List
argument_list|<
name|CatchClause
argument_list|>
name|getCatchClauses
parameter_list|()
block|{
return|return
name|catchClauses
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#getCardinality()      */
annotation|@
name|Override
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|ZERO_OR_MORE
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|contextInfo
operator|.
name|setFlags
argument_list|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
operator|(
operator|~
name|IN_PREDICATE
operator|)
argument_list|)
expr_stmt|;
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tryTargetExpr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|context
operator|.
name|expressionStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|getContext
argument_list|()
operator|.
name|getXQueryVersion
argument_list|()
operator|<
literal|30
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0003
argument_list|,
literal|"The try-catch expression is supported for xquery version \"3.0\" and later."
argument_list|)
throw|;
block|}
try|try
block|{
comment|// Evaluate 'try' expression
name|Sequence
name|tryTargetSeq
init|=
name|tryTargetExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
return|return
name|tryTargetSeq
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|ErrorCode
name|errorCode
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|throwable
operator|instanceof
name|XPathException
condition|)
block|{
comment|// Get errorcode from nicely thrown xpathexception
name|XPathException
name|xpe
init|=
operator|(
name|XPathException
operator|)
name|throwable
decl_stmt|;
name|errorCode
operator|=
name|xpe
operator|.
name|getErrorCode
argument_list|()
expr_stmt|;
comment|// if no errorcode is found, reconstruct by parsing the error text.
if|if
condition|(
name|errorCode
operator|==
literal|null
condition|)
block|{
name|errorCode
operator|=
name|extractErrorCode
argument_list|(
operator|(
name|XPathException
operator|)
name|xpe
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Get errorcode from all other errors and exceptions
name|errorCode
operator|=
operator|new
name|JavaErrorCode
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
comment|// We need the qname in the end
name|QName
name|errorCodeQname
init|=
name|errorCode
operator|.
name|getErrorQName
argument_list|()
decl_stmt|;
comment|// Exception in thrown, catch expression will be evaluated.
comment|// catchvars (CatchErrorCode (, CatchErrorDesc (, CatchErrorVal)?)? )
comment|// need to be retrieved as variables
name|Sequence
name|catchResultSeq
init|=
literal|null
decl_stmt|;
name|LocalVariable
name|mark0
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
comment|// flag used to escape loop when errorcode has matched
name|boolean
name|errorMatched
init|=
literal|false
decl_stmt|;
comment|// Iterate on all catch clauses
for|for
control|(
name|CatchClause
name|catchClause
range|:
name|catchClauses
control|)
block|{
if|if
condition|(
name|isErrorInList
argument_list|(
name|errorCodeQname
argument_list|,
name|catchClause
operator|.
name|getCatchErrorList
argument_list|()
argument_list|)
operator|&&
operator|!
name|errorMatched
condition|)
block|{
name|errorMatched
operator|=
literal|true
expr_stmt|;
comment|// Get catch variables
name|List
argument_list|<
name|QName
argument_list|>
name|catchVars
init|=
operator|(
name|List
argument_list|<
name|QName
argument_list|>
operator|)
name|catchClause
operator|.
name|getCatchVars
argument_list|()
decl_stmt|;
name|LocalVariable
name|mark1
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|int
name|varPos
init|=
literal|1
decl_stmt|;
try|try
block|{
comment|// catch variables
comment|// "(" CatchErrorCode ("," CatchErrorDesc ("," CatchErrorVal)?)? ")"
for|for
control|(
name|QName
name|catchVar
range|:
name|catchVars
control|)
block|{
comment|// reset qname and prefix
name|catchVar
operator|.
name|setPrefix
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|catchVar
operator|.
name|setNamespaceURI
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|LocalVariable
name|localVar
init|=
operator|new
name|LocalVariable
argument_list|(
name|catchVar
argument_list|)
decl_stmt|;
comment|// This should be in order of existance
comment|// xs:QName, xs:string?, and item()* respectively.
switch|switch
condition|(
name|varPos
condition|)
block|{
case|case
literal|1
case|:
comment|// Error code: qname
name|localVar
operator|.
name|setSequenceType
argument_list|(
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
expr_stmt|;
name|QNameValue
name|qnv
init|=
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|catchVar
argument_list|)
decl_stmt|;
name|localVar
operator|.
name|setValue
argument_list|(
operator|new
name|StringValue
argument_list|(
name|errorCode
operator|.
name|getErrorQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// Error description : optional string
name|localVar
operator|.
name|setSequenceType
argument_list|(
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
expr_stmt|;
name|StringValue
name|sv
init|=
operator|new
name|StringValue
argument_list|(
name|errorCode
operator|.
name|getDescription
argument_list|()
argument_list|)
decl_stmt|;
name|localVar
operator|.
name|setValue
argument_list|(
name|sv
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
comment|// Error value : optional item
name|localVar
operator|.
name|setSequenceType
argument_list|(
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|throwable
operator|instanceof
name|XPathException
condition|)
block|{
comment|// Get errorcode from exception
name|XPathException
name|xpe
init|=
operator|(
name|XPathException
operator|)
name|throwable
decl_stmt|;
name|Sequence
name|sequence
init|=
name|xpe
operator|.
name|getErrorVal
argument_list|()
decl_stmt|;
if|if
condition|(
name|sequence
operator|==
literal|null
condition|)
block|{
comment|// TODO setting an empty sequence does not work, it does
comment|// not make the variable visible
name|sequence
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
name|localVar
operator|.
name|setValue
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// fill data from throwable object
name|StringValue
name|value
init|=
operator|new
name|StringValue
argument_list|(
name|getStackTrace
argument_list|(
name|throwable
argument_list|)
argument_list|)
decl_stmt|;
name|localVar
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|localVar
argument_list|)
expr_stmt|;
name|varPos
operator|++
expr_stmt|;
block|}
comment|// Var catch variables
comment|// Evaluate catch expression
name|catchResultSeq
operator|=
operator|(
operator|(
name|Expression
operator|)
name|catchClause
operator|.
name|getCatchExpr
argument_list|()
operator|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// if in the end nothing is set, rethrow
block|}
block|}
comment|// for catch clauses
comment|// If an error hasn't been catched, throw new one
if|if
condition|(
operator|!
name|errorMatched
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|throwable
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark0
argument_list|)
expr_stmt|;
block|}
return|return
name|catchResultSeq
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|expressionEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Extract and construct errorcode from error text.      */
specifier|private
name|ErrorCode
name|extractErrorCode
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
comment|// Get message from string
name|String
name|message
init|=
name|xpe
operator|.
name|getMessage
argument_list|()
decl_stmt|;
comment|// if the 9th position has a ":" it is probably a custom error text
if|if
condition|(
literal|':'
operator|==
name|message
operator|.
name|charAt
argument_list|(
literal|8
argument_list|)
condition|)
block|{
name|String
index|[]
name|data
init|=
name|extractLocalName
argument_list|(
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|ErrorCode
name|errorCode
init|=
operator|new
name|ErrorCode
argument_list|(
operator|new
name|QName
argument_list|(
name|data
index|[
literal|0
index|]
argument_list|,
literal|"err"
argument_list|)
argument_list|,
name|data
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|errorCode
operator|.
name|getErrorQName
argument_list|()
operator|.
name|setPrefix
argument_list|(
literal|"err"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsed string '"
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
operator|+
literal|"' for Errorcode. "
operator|+
literal|"Qname='"
operator|+
name|data
index|[
literal|0
index|]
operator|+
literal|"' message='"
operator|+
name|data
index|[
literal|1
index|]
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return
name|errorCode
return|;
block|}
comment|// Convert xpe to Throwable
name|Throwable
name|retVal
init|=
name|xpe
decl_stmt|;
comment|// Swap with cause if present
name|Throwable
name|cause
init|=
name|xpe
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|cause
operator|instanceof
name|XPathException
operator|)
condition|)
block|{
name|retVal
operator|=
name|cause
expr_stmt|;
block|}
comment|// Fallback, create java error
return|return
operator|new
name|ErrorCodes
operator|.
name|JavaErrorCode
argument_list|(
name|retVal
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"try {"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|tryTargetExpr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
expr_stmt|;
for|for
control|(
name|CatchClause
name|catchClause
range|:
name|catchClauses
control|)
block|{
name|Expression
name|catchExpr
init|=
operator|(
name|Expression
operator|)
name|catchClause
operator|.
name|getCatchExpr
argument_list|()
decl_stmt|;
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"} catch (expr) {"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|catchExpr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"try { "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|tryTargetExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|CatchClause
name|catchClause
range|:
name|catchClauses
control|)
block|{
name|Expression
name|catchExpr
init|=
operator|(
name|Expression
operator|)
name|catchClause
operator|.
name|getCatchExpr
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" } catch (expr) { "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|catchExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#returnsType()      */
annotation|@
name|Override
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
comment|// fixme! /ljo
return|return
operator|(
operator|(
name|Expression
operator|)
name|catchClauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCatchExpr
argument_list|()
operator|)
operator|.
name|returnsType
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#resetState()      */
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
name|tryTargetExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
for|for
control|(
name|CatchClause
name|catchClause
range|:
name|catchClauses
control|)
block|{
name|Expression
name|catchExpr
init|=
operator|(
name|Expression
operator|)
name|catchClause
operator|.
name|getCatchExpr
argument_list|()
decl_stmt|;
name|catchExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visitTryCatch
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isErrorInList
parameter_list|(
name|QName
name|error
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errors
parameter_list|)
block|{
name|String
name|qError
init|=
name|error
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|lError
range|:
name|errors
control|)
block|{
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|lError
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|qError
operator|.
name|equals
argument_list|(
name|lError
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|String
index|[]
name|extractLocalName
parameter_list|(
name|String
name|errorText
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|int
name|p
init|=
name|errorText
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|null
block|,
name|errorText
block|}
return|;
block|}
return|return
operator|new
name|String
index|[]
block|{
name|errorText
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
operator|.
name|trim
argument_list|()
block|,
name|errorText
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
block|}
return|;
block|}
specifier|private
name|String
name|getStackTrace
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Data container      */
specifier|public
class|class
name|CatchClause
block|{
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|catchErrorList
init|=
literal|null
decl_stmt|;
specifier|private
name|List
argument_list|<
name|QName
argument_list|>
name|catchVars
init|=
literal|null
decl_stmt|;
specifier|private
name|Expression
name|catchExpr
init|=
literal|null
decl_stmt|;
specifier|public
name|CatchClause
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|catchErrorList
parameter_list|,
name|List
argument_list|<
name|QName
argument_list|>
name|catchVars
parameter_list|,
name|Expression
name|catchExpr
parameter_list|)
block|{
name|this
operator|.
name|catchErrorList
operator|=
name|catchErrorList
expr_stmt|;
name|this
operator|.
name|catchVars
operator|=
name|catchVars
expr_stmt|;
name|this
operator|.
name|catchExpr
operator|=
name|catchExpr
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getCatchErrorList
parameter_list|()
block|{
return|return
name|catchErrorList
return|;
block|}
specifier|public
name|void
name|setCatchErrorList
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|catchErrorList
parameter_list|)
block|{
name|this
operator|.
name|catchErrorList
operator|=
name|catchErrorList
expr_stmt|;
block|}
specifier|public
name|Expression
name|getCatchExpr
parameter_list|()
block|{
return|return
name|catchExpr
return|;
block|}
specifier|public
name|void
name|setCatchExpr
parameter_list|(
name|Expression
name|catchExpr
parameter_list|)
block|{
name|this
operator|.
name|catchExpr
operator|=
name|catchExpr
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|QName
argument_list|>
name|getCatchVars
parameter_list|()
block|{
return|return
name|catchVars
return|;
block|}
specifier|public
name|void
name|setCatchVars
parameter_list|(
name|List
argument_list|<
name|QName
argument_list|>
name|catchVars
parameter_list|)
block|{
name|this
operator|.
name|catchVars
operator|=
name|catchVars
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

