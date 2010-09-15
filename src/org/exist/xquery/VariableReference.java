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
name|DocumentSet
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
name|Type
import|;
end_import

begin_comment
comment|/**  * Represents a reference to an in-scope variable.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|VariableReference
extends|extends
name|AbstractExpression
block|{
specifier|private
specifier|final
name|String
name|qname
decl_stmt|;
specifier|public
name|VariableReference
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|qname
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
name|Variable
name|var
init|=
literal|null
decl_stmt|;
try|try
block|{
name|var
operator|=
name|getVariable
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|// ignore: variable might not be known yet
return|return;
block|}
if|if
condition|(
name|var
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPDY0002 : variable '$"
operator|+
name|qname
operator|+
literal|"' is not set."
argument_list|)
throw|;
if|if
condition|(
operator|!
name|var
operator|.
name|isInitialized
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XQST0054: variable declaration of '$"
operator|+
name|qname
operator|+
literal|"' cannot "
operator|+
literal|"be executed because of a circularity."
argument_list|)
throw|;
name|contextInfo
operator|.
name|setStaticReturnType
argument_list|(
name|var
operator|.
name|getStaticType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|Variable
name|var
init|=
name|getVariable
argument_list|()
decl_stmt|;
if|if
condition|(
name|var
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPDY0002 : variable '$"
operator|+
name|qname
operator|+
literal|"' is not set."
argument_list|)
throw|;
name|Sequence
name|seq
init|=
name|var
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|seq
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPDY0002 : undefined value for variable '$"
operator|+
name|qname
operator|+
literal|"'"
argument_list|)
throw|;
name|Sequence
name|result
init|=
name|seq
decl_stmt|;
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
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
name|Variable
name|getVariable
parameter_list|()
throws|throws
name|XPathException
block|{
try|try
block|{
return|return
name|context
operator|.
name|resolveVariable
argument_list|(
name|qname
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|setLocation
argument_list|(
name|line
argument_list|,
name|column
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#preselect(org.exist.dom.DocumentSet, org.exist.xquery.StaticContext) 	 */
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|in_docs
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
name|display
argument_list|(
literal|'$'
argument_list|)
operator|.
name|display
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"$"
operator|+
name|qname
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
try|try
block|{
name|Variable
name|var
init|=
name|context
operator|.
name|resolveVariable
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|int
name|type
init|=
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
decl_stmt|;
return|return
name|type
return|;
block|}
else|else
block|{
return|return
name|var
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
block|}
return|return
name|Type
operator|.
name|ITEM
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
try|try
block|{
name|Variable
name|var
init|=
name|context
operator|.
name|resolveVariable
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|!=
literal|null
condition|)
block|{
name|int
name|deps
init|=
name|var
operator|.
name|getDependencies
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|deps
return|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
block|}
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getCardinality() 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
try|try
block|{
name|Variable
name|var
init|=
name|context
operator|.
name|resolveVariable
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|!=
literal|null
operator|&&
name|var
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|int
name|card
init|=
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getCardinality
argument_list|()
decl_stmt|;
return|return
name|card
return|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
block|}
return|return
name|Cardinality
operator|.
name|ZERO_OR_MORE
return|;
comment|// unknown cardinality
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
name|visitVariableReference
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowMixNodesInReturn
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

