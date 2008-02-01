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

begin_comment
comment|/**  * A global variable declaration (with: declare variable). Variable bindings within  * for and let expressions are handled by {@link org.exist.xquery.ForExpr} and  * {@link org.exist.xquery.LetExpr}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|VariableDeclaration
extends|extends
name|AbstractExpression
block|{
name|String
name|qname
decl_stmt|;
name|SequenceType
name|sequenceType
init|=
literal|null
decl_stmt|;
name|Expression
name|expression
decl_stmt|;
name|boolean
name|analyzeDone
init|=
literal|false
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|VariableDeclaration
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|qname
parameter_list|,
name|Expression
name|expr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expr
expr_stmt|;
block|}
comment|/** 	 * Set the sequence type of the variable. 	 *  	 * @param type 	 */
specifier|public
name|void
name|setSequenceType
parameter_list|(
name|SequenceType
name|type
parameter_list|)
block|{
name|this
operator|.
name|sequenceType
operator|=
name|type
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.AnalyzeContextInfo)      */
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
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|QName
name|qn
init|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|qname
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Variable
name|var
init|=
operator|new
name|Variable
argument_list|(
name|qn
argument_list|)
decl_stmt|;
name|var
operator|.
name|setIsInitialized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|analyzeDone
condition|)
block|{
name|Module
name|myModule
init|=
name|context
operator|.
name|getModule
argument_list|(
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|myModule
operator|!=
literal|null
condition|)
block|{
comment|// WM: duplicate var declaration is now caught in the XQuery tree parser
if|if
condition|(
name|myModule
operator|.
name|isVarDeclared
argument_list|(
name|qn
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"err:XQST0049: It is a static error if more than one "
operator|+
literal|"variable declared or imported by a module has the same expanded QName. Variable: "
operator|+
name|qn
argument_list|)
throw|;
name|myModule
operator|.
name|declareVariable
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// WM: duplicate var declaration is now caught in the XQuery tree parser
if|if
condition|(
name|context
operator|.
name|isVarDeclared
argument_list|(
name|qn
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"err:XQST0049: It is a static error if more than one "
operator|+
literal|"variable declared or imported by a module has the same expanded QName. Variable: "
operator|+
name|qn
argument_list|)
throw|;
block|}
name|context
operator|.
name|declareGlobalVariable
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
name|analyzeDone
operator|=
literal|true
expr_stmt|;
block|}
name|expression
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|var
operator|.
name|setIsInitialized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
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
name|QName
name|qn
init|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|qname
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Module
name|myModule
init|=
name|context
operator|.
name|getModule
argument_list|(
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
comment|// declare the variable
name|Sequence
name|seq
init|=
name|expression
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Variable
name|var
decl_stmt|;
if|if
condition|(
name|myModule
operator|!=
literal|null
condition|)
block|{
name|var
operator|=
name|myModule
operator|.
name|declareVariable
argument_list|(
name|qn
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|var
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|var
operator|=
operator|new
name|Variable
argument_list|(
name|qn
argument_list|)
expr_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|var
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareGlobalVariable
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
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
comment|//Note : that we use seq but we return Sequence.EMPTY_SEQUENCE
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
name|seq
argument_list|)
expr_stmt|;
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
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
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"declare variable $"
argument_list|)
operator|.
name|display
argument_list|(
name|qname
argument_list|,
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" as "
argument_list|)
operator|.
name|display
argument_list|(
name|sequenceType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|expression
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
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"}"
argument_list|)
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
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
literal|"declare variable $"
argument_list|)
operator|.
name|append
argument_list|(
name|qname
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
operator|.
name|append
argument_list|(
name|sequenceType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|expression
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
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|expression
operator|.
name|returnsType
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getCardinality() 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getCardinality
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#resetState() 	 */
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
name|expression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|postOptimization
condition|)
name|analyzeDone
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

