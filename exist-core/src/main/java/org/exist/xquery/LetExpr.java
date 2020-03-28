begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  * Implements an XQuery let-expression.  *   * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_class
specifier|public
class|class
name|LetExpr
extends|extends
name|BindingExpression
block|{
specifier|public
name|LetExpr
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
annotation|@
name|Override
specifier|public
name|ClauseType
name|getType
parameter_list|()
block|{
return|return
name|ClauseType
operator|.
name|LET
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
specifier|final
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
comment|//Save the local variable stack
specifier|final
name|LocalVariable
name|mark
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
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
specifier|final
name|AnalyzeContextInfo
name|varContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|inputSequence
operator|.
name|analyze
argument_list|(
name|varContextInfo
argument_list|)
expr_stmt|;
comment|//Declare the iteration variable
specifier|final
name|LocalVariable
name|inVar
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
name|varName
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|inVar
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|inVar
operator|.
name|setStaticType
argument_list|(
name|varContextInfo
operator|.
name|getStaticReturnType
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|inVar
argument_list|)
expr_stmt|;
comment|//Reset the context position
name|context
operator|.
name|setContextSequencePosition
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|returnExpr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"No namespace defined for prefix "
operator|+
name|varName
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// restore the local variable stack
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
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
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|expressionStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
comment|//Save the local variable stack
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Sequence
name|in
decl_stmt|;
name|LocalVariable
name|var
decl_stmt|;
name|Sequence
name|resultSequence
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// evaluate input sequence
name|in
operator|=
name|inputSequence
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|clearContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
comment|// Declare the iteration variable
name|var
operator|=
name|createVariable
argument_list|(
name|varName
argument_list|)
expr_stmt|;
name|var
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|==
literal|null
condition|)
block|{
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
block|}
comment|//Just because it makes conversions !
name|var
operator|.
name|setContextDocs
argument_list|(
name|inputSequence
operator|.
name|getContextDocSet
argument_list|()
argument_list|)
expr_stmt|;
name|registerUpdateListener
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|resultSequence
operator|=
name|returnExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
block|{
name|Cardinality
name|actualCardinality
decl_stmt|;
if|if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
if|else if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|hasMany
argument_list|()
condition|)
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|_MANY
expr_stmt|;
block|}
else|else
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|EXACTLY_ONE
expr_stmt|;
block|}
comment|//Type.EMPTY is *not* a subtype of other types ; checking cardinality first
if|if
condition|(
operator|!
name|sequenceType
operator|.
name|getCardinality
argument_list|()
operator|.
name|isSuperCardinalityOrEqualOf
argument_list|(
name|actualCardinality
argument_list|)
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
name|XPTY0004
argument_list|,
literal|"Invalid cardinality for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|sequenceType
operator|.
name|getCardinality
argument_list|()
operator|.
name|getHumanDescription
argument_list|()
operator|+
literal|", got "
operator|+
name|actualCardinality
operator|.
name|getHumanDescription
argument_list|()
argument_list|,
name|in
argument_list|)
throw|;
block|}
comment|//TODO : ignore nodes right now ; they are returned as xs:untypedAtomicType
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|,
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
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
name|XPTY0004
argument_list|,
literal|"Invalid type for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|,
name|in
argument_list|)
throw|;
block|}
comment|//Here is an attempt to process the nodes correctly
block|}
else|else
block|{
comment|//Same as above : we probably may factorize
if|if
condition|(
operator|!
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|sequenceType
operator|.
name|checkType
argument_list|(
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Sequence
name|value
init|=
name|var
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|SequenceType
name|valueType
init|=
operator|new
name|SequenceType
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|,
name|value
operator|.
name|getCardinality
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|value
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
operator|==
name|Type
operator|.
name|DOCUMENT
operator|&&
name|value
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|DOCUMENT
condition|)
block|{
comment|// it's a document... we need to get the document element's name
specifier|final
name|NodeValue
name|nvItem
init|=
operator|(
name|NodeValue
operator|)
name|value
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|doc
decl_stmt|;
if|if
condition|(
name|nvItem
operator|instanceof
name|Document
condition|)
block|{
name|doc
operator|=
operator|(
name|Document
operator|)
name|nvItem
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|=
name|nvItem
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Element
name|elem
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|elem
operator|!=
literal|null
condition|)
block|{
name|valueType
operator|.
name|setNodeName
argument_list|(
operator|new
name|QName
argument_list|(
name|elem
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|elem
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Invalid type for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|sequenceType
operator|.
name|toString
argument_list|()
operator|+
literal|", got "
operator|+
name|valueType
operator|.
name|toString
argument_list|()
argument_list|,
name|in
argument_list|)
throw|;
block|}
block|}
block|}
block|}
finally|finally
block|{
comment|// Restore the local variable stack
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|,
name|resultSequence
argument_list|)
expr_stmt|;
block|}
name|clearContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|resultSequence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resultSequence
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|resultSequence
operator|instanceof
name|DeferredFunctionCall
operator|)
condition|)
block|{
name|setActualReturnType
argument_list|(
name|resultSequence
operator|.
name|getItemType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getPreviousClause
argument_list|()
operator|==
literal|null
condition|)
block|{
name|resultSequence
operator|=
name|postEval
argument_list|(
name|resultSequence
argument_list|)
expr_stmt|;
block|}
return|return
name|resultSequence
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|expressionEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
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
literal|"let "
argument_list|,
name|line
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"$"
argument_list|)
operator|.
name|display
argument_list|(
name|varName
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" := "
argument_list|)
expr_stmt|;
name|inputSequence
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
comment|//TODO : QuantifiedExpr
if|if
condition|(
name|returnExpr
operator|instanceof
name|LetExpr
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"return "
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|returnExpr
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
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
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
literal|"let "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
operator|.
name|append
argument_list|(
name|varName
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" := "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|inputSequence
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
comment|//TODO : QuantifiedExpr
if|if
condition|(
name|returnExpr
operator|instanceof
name|LetExpr
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
literal|"return "
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
name|returnExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|visitLetExpression
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowMixedNodesInReturn
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

